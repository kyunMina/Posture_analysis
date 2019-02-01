package com.excall.minato.posture_analysis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MeasureActivity extends AppCompatActivity {

    //    public TextView Counter;
    public int points = 0;
    public float point_x[] = new float[5];
    public float point_y[] = new float[5];

    ImageView mImageView;

    Button Test, Undo, Redo;

    TextView result;

    /**
     * 有効数字以下の数値を四捨五入。未使用につき参考まで
     *
     * @param value          　数値
     * @param effectiveDigit 　有効数字桁数
     * @return　四捨五入された数値
     */
    public static double getEfficientRound(double value, int effectiveDigit) {

        int valueDigit = (int) Math.rint(Math.log10(Math.abs(value)));
        int roundDigit = valueDigit - effectiveDigit + 1;
        double v = Math.floor(value / Math.pow(10, roundDigit) + 0.5);
        return v * Math.pow(10, roundDigit);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        //  Intent
        //  to get message from PicturedActivity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String path = bundle.getString("path");
        int degree = bundle.getInt("degree");
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  User Clicked OK Button
            }
        });


        //  DrawingView
        final DrawingView view = (DrawingView) findViewById(R.id.drawingView);
        findViewById(R.id.drawingView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                int event = motionEvent.getActionMasked();

                switch (event) {
                    case MotionEvent.ACTION_UP:

                        if (points < 5) {
                            //  配列に各点を保存
                            point_x[points] = x;
                            point_y[points] = y;

                            Log.d("debug", "x = " + point_x[points]);
                            Log.d("debug", "y = " + point_y[points]);


                        }
                        points++;
                        OutputDialog(points);

                }

                return false;
            }
        });


        result = findViewById(R.id.result);

        //  Buttons
        Test = (Button) findViewById(R.id.test);
        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Undo = findViewById(R.id.btnUndo);
        Undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points > 0) {
                    points--;
                    view.undo();
                }

            }
        });

        Redo = findViewById(R.id.btnRedo);
        Redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points++;
                view.redo();
            }
        });

        //  Bitmap
        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setRotation(degree);
        mImageView.setImageBitmap(bitmap);

        //  OutputDialog_Start_MeasureActivity
        OutputDialog(0);

    }

    //  角度計算
    public void math() {
        //  首・体の傾きの算出
        double A_x, A_y, B_x, B_y;
        double score;
        double cos, cos2;
        double neck_deg, body_deg;
        double C_x, C_y, D_x, D_y;


        //  首の角度
        A_x = point_x[0] - point_x[1];
        A_y = point_y[0] - point_y[1];
        B_x = point_x[2] - point_x[1];
        B_y = point_y[2] - point_y[1];
        cos = (A_x * B_x + A_y * B_y) / (Math.sqrt(A_x * A_x + A_y * A_y) * Math.sqrt(B_x * B_x + B_y * B_y));
        neck_deg = 180 - Math.round(Math.toDegrees(Math.acos(cos)));

        //  体の傾き
        C_x = point_x[4] - point_x[0];
        C_y = point_y[4] - point_y[0];
        D_x = point_x[0] - point_x[0];
        D_y = point_y[4] - point_y[0];
        cos2 = (C_x * D_x + C_y * D_y) / (Math.sqrt(C_x * C_x + C_y * C_y) * Math.sqrt(D_x * D_x + D_y * D_y));
        body_deg = Math.round(Math.toDegrees(Math.acos(cos2)));

        score = Math.round(100 * (((180 - neck_deg) / 180) * ((180 - body_deg)) / 180));


        Undo.setVisibility(View.INVISIBLE);
        Redo.setVisibility(View.INVISIBLE);

        result.setText("首の傾き = " + neck_deg + "度\n" +
                "体の傾き = " + body_deg + "度\n" +
                "スコア = " + score + "点");
        result.setTextSize(30);
        result.setTextColor(Color.BLACK);
        result.setVisibility(View.VISIBLE);
    }

    //  ダイアログの出力
    public void OutputDialog(int num) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //  User Clicked OK Button
            }
        });

        switch (num) {
            case 0:
                alertDialog.setTitle("Tips");
                alertDialog.setMessage("耳朶を打点してください");
                break;

            case 1:
                alertDialog.setTitle("Tips");
                alertDialog.setMessage("肩峰突起を打点してください");
                break;

            case 2:
                alertDialog.setTitle("Tips");
                alertDialog.setMessage("大腿骨大転子を打点してください");
                break;

            case 3:
                alertDialog.setTitle("Tips");
                alertDialog.setMessage("膝を打点してください");
                break;

            case 4:
                alertDialog.setTitle("Tips");
                alertDialog.setMessage("外果を打点してください");
                break;

            case 5:
                math();
                break;

        }

        if (points < 5) {
            alertDialog.show();
        }

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
        mImageView.setImageDrawable(null);
        System.gc();
    }
}
