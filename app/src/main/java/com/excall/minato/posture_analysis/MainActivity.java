package com.excall.minato.posture_analysis;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    TextView title;
    Button Camera, Tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Display Size
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = getWindowManager().getDefaultDisplay();
        Point realSize = new Point();
        display.getRealSize(realSize);
        int width = realSize.x;
        int height = realSize.y;

        //  TextViews
        title = findViewById(R.id.title);
        title.setText("姿勢測定アプリケーション");
        title.setTextColor(Color.BLACK);

        //  Buttons
        Camera = (Button) findViewById(R.id.camera);
        Camera.setWidth(3 * width / 5);
        Camera.setHeight(height / 5);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), CameraActivity.class);
                startActivity(intent);
            }
        });

        Tips = (Button) findViewById(R.id.tips);
        Tips.setWidth(3 * width / 5);
        Tips.setHeight(height / 5);
        Tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "onStop()");
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy()");
        System.gc();
    }
}

