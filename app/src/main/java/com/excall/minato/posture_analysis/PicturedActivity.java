package com.excall.minato.posture_analysis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PicturedActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictured);

        //  Intent
        //  to get message from MainActivity
        Intent intent = this.getIntent();
        String path = intent.getStringExtra("path");
        //Bundle bundle = intent.getExtras();
        //Bitmap bitmap = (Bitmap)bundle.get("bitmap");
        //Bitmap bitmap = (Bitmap)intent.getParcelableExtra("bitmap");
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        //  ImageView
        mImageView = (ImageView)findViewById(R.id.image_view);
        mImageView.setImageBitmap(bitmap);

        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("PicturedActivity");

        //  Buttons
        Button Test = (Button)findViewById(R.id.test);
        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        System.gc();
    }
}
