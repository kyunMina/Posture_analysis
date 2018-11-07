package com.excall.minato.posture_analysis;

import android.content.Intent;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("ホーム画面");

        //  Buttons
        Button Camera = (Button)findViewById(R.id.camera);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), CameraActivity.class);
                startActivity(intent);
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug","onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug","onStop()");
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug","onDestroy()");
        System.gc();
    }
}

