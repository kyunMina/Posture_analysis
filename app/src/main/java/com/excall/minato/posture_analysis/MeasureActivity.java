package com.excall.minato.posture_analysis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MeasureActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("MeasureActivity");

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
