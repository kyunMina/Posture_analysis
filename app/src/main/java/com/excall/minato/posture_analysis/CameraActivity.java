package com.excall.minato.posture_analysis;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;

public class CameraActivity extends AppCompatActivity implements SensorEventListener {


    private static final int REQUEST_MULTI_PERMISSIONS = 101;
    final long countNumber = 10000;     //  10sec = 10 * 1000 = 10000msec
    final long interval = 10;       //  interval 10msec
    final CountDown countDown = new CountDown(countNumber, interval);
    final double threshold = 0.005;     //  センサー閾値
    public boolean isAllowedCamera = false;
    public boolean isAllowedExternalRead = false;
    public boolean isAllowedExternalWrite = false;
    public StringBuffer message = new StringBuffer();

    SoundPool soundPool;
    Button Test, Measure;
    private int startcamera_sound, shutter_sound;
    //  Camera,View
    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private Handler mBackgroundHandler = new Handler();
    private CameraCaptureSession mCaptureSession = null;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    private TextView TimerText;
    private SimpleDateFormat TimerFormat = new SimpleDateFormat("mm:ss.SSS", Locale.US);
    private SensorManager sensorManager;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //  Display Size
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = getWindowManager().getDefaultDisplay();
        Point realSize = new Point();
        display.getRealSize(realSize);
        int width = realSize.x;
        int height = realSize.y;

        //  TextViews
        TimerText = findViewById(R.id.Timer_text);
        TimerText.setText(TimerFormat.format(0));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //  Buttons
        Test = (Button) findViewById(R.id.test);
        Test.setWidth(2 * width / 5);
        Test.setHeight(height / 9);
        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Measure = (Button) findViewById(R.id.measure);
        Measure.setWidth(2 * width / 5);
        Measure.setHeight(height / 9);
        Measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MeasureActivity.class);
                startActivity(intent);
            }
        });

        //  TextureView
        mTextureView = (TextureView) findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        //  Sounds
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build();

        startcamera_sound = soundPool.load(this, R.raw.startcamera, 1);
        shutter_sound = soundPool.load(this, R.raw.shutter, 1);

        //  Sound_Load
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug", "sampleId=" + sampleId);
                Log.d("debug", "status=" + status);
            }
        });

    }

    //  Permission Check
    private void checkMultiPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionReadExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> reqPermissions = new ArrayList<>();

        //  Permissionが許可されているか確認
        //  Camera
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            isAllowedCamera = true;
            Log.d("debug", "permissionCamera:GRANTED");
        } else {
            reqPermissions.add(Manifest.permission.CAMERA);
        }
        //  ReadExternalStorage
        if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
            isAllowedExternalRead = true;
            Log.d("debug", "permissionR_E_S:GRANTED");
        } else {
            reqPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //  WriteExternalStorage
        if (permissionWriteExtStorage == PackageManager.PERMISSION_GRANTED) {
            isAllowedExternalWrite = true;
            Log.d("debug", "permissionW_E_S:GRANTED");
        } else {
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //   IF DENIED
        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    reqPermissions.toArray(new String[reqPermissions.size()]),
                    REQUEST_MULTI_PERMISSIONS);
        }
    }

    //  Permission Checkの結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_MULTI_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].
                            equals(Manifest.permission.CAMERA)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedCamera = true;
                        } else {
                            // それでも拒否された時の対応
                            message.append("カメラの許可がないので計測できません\n");
                        }
                    } else if (permissions[i].
                            equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedExternalRead = true;
                        } else {
                            // それでも拒否された時の対応
                            message.append("外部読取の許可がないので読取りできません\n");
                        }
                    } else if (permissions[i].
                            equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedExternalWrite = true;
                        } else {
                            // それでも拒否された時の対応
                            message.append("外部書込の許可がないので書き込みできません\n");
                        }
                    }
                }
            }
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String selectedCameraId = "";
        try {
            selectedCameraId = manager.getCameraIdList()[1];

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        try {
            manager.openCamera(selectedCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(3492, 4656); // 自分の手元のデバイスで決めうちしてます
        Surface surface = new Surface(texture);

        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mPreviewRequest = mPreviewRequestBuilder.build();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // カメラがcloseされている場合
                    if (null == mCameraDevice) {
                        return;
                    }

                    mCaptureSession = session;

                    try {
                        session.setRepeatingRequest(mPreviewRequest, null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void checkBitmap() {
        try {
            mCaptureSession.stopRepeating(); // プレビューの更新を止める
            Bitmap bitmap = mTextureView.getBitmap();
            Intent intent = new Intent(getApplication(), PicturedActivity.class);
            intent.putExtra("bitmap", bitmap);
            startActivityForResult(intent, 1);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmap() {

        SimpleDateFormat filename = new SimpleDateFormat("yyyyMMddHHmmss");
        filePath = getExternalFilesDir(null).getPath();

        File file = new File(filePath);
        Date mDate = new Date();


        try {
            mCaptureSession.stopRepeating(); // プレビューの更新を止める
            if (mTextureView.isAvailable()) {

                FileOutputStream fos = null;
                fos = new FileOutputStream(new File(file, filename.format(mDate) + ".jpg"));
                Bitmap bitmap = mTextureView.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.close();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  Save Index
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //  Send Intent
        Intent intent = new Intent(getApplication(), PicturedActivity.class);
        intent.putExtra("path", file.getAbsolutePath() + "/" + filename.format(mDate) + ".jpg");
        startActivity(intent);

    }


    protected void stop_sensor() {
        sensorManager.unregisterListener(this);
        soundPool.play(startcamera_sound, 1.0f, 1.0f, 0, 0, 1);
        countDown.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("debug", "onSensorChanged");

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float sensorX = event.values[0];
            float sensorY = event.values[1];
            float sensorZ = event.values[2];

            if (abs(sensorX) < threshold && abs(sensorY) < threshold && abs(sensorZ) < threshold) {
                stop_sensor();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "onStartCameraActivity()");

        //  Android 6.0 以上でPermission Check
        if (Build.VERSION.SDK_INT >= 23) {
            checkMultiPermissions();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        createCameraPreviewSession();
        Log.d("debug", "onRestartCameraActivity()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "onResumeCameraActivity()");

        //  Listenerの登録
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor gyro_uc = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        if (gyro != null) {
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, gyro_uc, SensorManager.SENSOR_DELAY_UI);
        } else {
            String ns = "No Support!";
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "onPauseCameraActivity()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "onStopCameraActivity()");

        countDown.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroyCameraActivity()");

        countDown.cancel();
        mCameraDevice.close();
        System.gc();
    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        //  完了時呼ばれる
        @Override
        public void onFinish() {

            TimerText.setText(TimerFormat.format(0));
            soundPool.play(shutter_sound, 1.0f, 1.0f, 1, 0, 1);
            saveBitmap();

        }

        //  インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {

            TimerText.setText(TimerFormat.format(millisUntilFinished));

        }
    }
}
