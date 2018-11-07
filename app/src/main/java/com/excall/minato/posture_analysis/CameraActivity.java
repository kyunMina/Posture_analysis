package com.excall.minato.posture_analysis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_MULTI_PERMISSIONS = 101;

    //  Permission
    public boolean isAllowedCamera = false;
    public boolean isAllowedExternalRead = false;
    public boolean isAllowedExternalWrite = false;

    //  その他


    public StringBuffer message = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("CameraActivity");

        //  Buttons
        Button Test = (Button)findViewById(R.id.test);
        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button Measure = (Button)findViewById(R.id.measure);
        Measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MeasureActivity.class);
                startActivity(intent);
            }
        });
    }


    //  Permission Check
    private void checkMultiPermissions(){
        int permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionReadExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> reqPermissions = new ArrayList<>();

        //  Permissionが許可されているか確認
        //  Camera
        if (permissionCamera == PackageManager.PERMISSION_GRANTED){
            isAllowedCamera = true;
            Log.d("debug","permissionCamera:GRANTED");
        }
        else{
            reqPermissions.add(Manifest.permission.CAMERA);
        }
        //  ReadExternalStorage
        if (permissionCamera == PackageManager.PERMISSION_GRANTED){
            isAllowedExternalRead = true;
            Log.d("debug","permissionR_E_S:GRANTED");
        }
        else{
            reqPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //  WriteExternalStorage
        if (permissionWriteExtStorage == PackageManager.PERMISSION_GRANTED){
            isAllowedExternalWrite = true;
            Log.d("debug","permissionW_E_S:GRANTED");
        }
        else{
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //  DENIED
        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    reqPermissions.toArray(new String[reqPermissions.size()]),
                    REQUEST_MULTI_PERMISSIONS);
        }
        else{
            cameraActivity();
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

                    cameraActivity();

                }
            }
        }
    }

    //  カメラ動作
    private void cameraActivity()  {

        //  カメラ呼び出し
        //  画像データの保存・出力
        //  以降別アクティビティ
        //  画像打点
        //  評価

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","onStartCameraActivity()");

        //  Android 6.0 以上でPermission Check
        if (Build.VERSION.SDK_INT >= 23){
            checkMultiPermissions();
        }
        else{
            cameraActivity();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug","onRestartCameraActivity()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResumeCameraActivity()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPauseCameraActivity()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug","onStopCameraActivity()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug","onDestroyCameraActivity()");
        System.gc();
    }
}
