package com.excall.minato.posture_analysis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PicturedActivity extends AppCompatActivity {

    ImageView mImageView;
    Button TurnLeft, TurnRight;
    Bitmap bitmap;
    int degree = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictured);

        //  Intent
        //  to get message from MainActivity
        Intent intent = this.getIntent();
        final String path = intent.getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(path);

        //  Display Size
        WindowManager wm =(WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = getWindowManager().getDefaultDisplay();
        Point realSize = new Point();
        display.getRealSize(realSize);
        int width = realSize.x;
        int height = realSize.y;

        //  ImageView
        mImageView = (ImageView)findViewById(R.id.image_view);
        mImageView.setImageBitmap(bitmap);

        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("PicturedActivity");

        //  Buttons
        Button Retake = (Button)findViewById(R.id.retake);
        Retake.setWidth(2*width/5);
        Retake.setHeight(height/9);
        Retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button Analysis = (Button)findViewById(R.id.analysis);
        Analysis.setWidth(2*width/5);
        Analysis.setHeight(height/9);
        Analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MeasureActivity.class);
                //intent.putExtra("path", file.getAbsolutePath() +"/" + filename.format(mDate) + ".jpg");

                Bundle bundle = new Bundle();

                bundle.putString("path",path);
                bundle.putInt("degree",degree);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        TurnLeft = (Button)findViewById(R.id.left);
        TurnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degree--;
                mImageView.setRotation(degree);
                mImageView.setImageBitmap(bitmap);
            }
        });

        TurnRight = (Button)findViewById(R.id.right);
        TurnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degree++;
                mImageView.setRotation(degree);
                mImageView.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","onStartPictured()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug","onRestartPictured()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResumePictured()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPausePictured()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug","onStopPictured()");
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug","onDestroyPictured()");
        mImageView.setImageDrawable(null);
        System.gc();
    }
}
