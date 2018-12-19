package com.excall.minato.posture_analysis;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MeasureActivity extends AppCompatActivity{

    public TextView Counter;
    public int points = 0;
    public float point_x[] = new float[5];
    public float point_y[] = new float[5];
    ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        Intent intent = this.getIntent();

        Bundle bundle = intent.getExtras();
        String path = bundle.getString("path");
        int degree = bundle.getInt("degree");
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        //  DrawingView
        final DrawingView view = (DrawingView)findViewById(R.id.drawingView);
        findViewById(R.id.drawingView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                int event = motionEvent.getActionMasked();
                //Counter.setText("ss");
                //points++;
                //Counter.setText(""+points);

                switch (event){
                    case MotionEvent.ACTION_UP:

                        if (points < 5){
                            //  配列に各点を保存
                            point_x[points] = x;
                            point_y[points] = y;

                            Log.d("debug","x = "+point_x[points]);
                            Log.d("debug","y = "+point_y[points]);

                        }
                        else{
                            math();
                        }
                        points++;
                        Counter.setText(""+points);

                }

                return false;
            }
        });


        //  TextViews
        TextView title = findViewById(R.id.title);
        title.setText("MeasureActivity");

        Counter = findViewById(R.id.counter);

        //  Buttons
        Button Test = (Button)findViewById(R.id.test);
        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button Undo = findViewById(R.id.btnUndo);
        Undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points > 0){
                    points--;
                    Counter.setText(""+points);
                    view.undo();
                }

            }
        });

        Button Redo = findViewById(R.id.btnRedo);
        Redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points++;
                Counter.setText(""+points);
                view.redo();
            }
        });

        //  Bitmap
        mImageView = (ImageView)findViewById(R.id.image_view);
        mImageView.setRotation(degree);
        mImageView.setImageBitmap(bitmap);

    }

    //  角度計算
    public void math(){
        //  首・体の傾きの算出
        double A_x,A_y,B_x,B_y, x_sum=0, y_sum=0,x_sumv=0, y_sumv=0, x_ave, y_ave, x_squ, y_squ, x_v, y_v;
        double x_cov, y_cov, cov = 0, s;
        double cos,cos2;
        double C_x, C_y, D_x, D_y;

//        //  test
//        for (int i=0;i<5;i++){
//            point_x[i] = i*10;
//            point_y[i] = i*10;
//        }

        //  首の角度
        A_x = point_x[0] - point_x[1];
        A_y = point_y[0] - point_y[1];
        B_x = point_x[2] - point_x[1];
        B_y = point_y[2] - point_y[1];
        cos = (A_x * B_x + A_y * B_y) / (Math.sqrt(A_x*A_x + A_y*A_y)*Math.sqrt(B_x*B_x + B_y*B_y));
        //Math.acos(cos[i]);

        //  体の傾き
        C_x = point_x[4] - point_x[0];
        C_y = point_y[4] - point_y[0];
        D_x = point_x[0] - point_x[0];
        D_y = point_y[4] - point_y[0];
        cos2 = (C_x * D_x + C_y * D_y) / (Math.sqrt(C_x*C_x + C_y*C_y)*Math.sqrt(D_x*D_x + D_y*D_y));


        //  体の傾き・標本分散
        for (int i = 0;i < 5;i++){
            x_sum += point_x[i]-point_x[0];
            x_sumv += (point_x[i]-point_x[0])*(point_x[i]-point_x[0]);
            y_sum += point_y[i]-point_y[0];
            y_sumv += (point_y[i]-point_y[0])*(point_y[i]-point_y[0]);
        }
        //  平均
        x_ave = x_sum / 5;
        y_ave = y_sum / 5;
        //  平方和
        x_squ = x_sumv - (x_sum*x_sum / 5);
        y_squ = y_sumv - (y_sum*y_sum / 5);
        //  分散
        x_v = x_squ / 5;
        y_v = y_squ / 5;
        //  標準偏差
        x_cov = Math.sqrt(x_v);
        y_cov = Math.sqrt(y_v);
        //  共分散
        for (int i = 0;i < 5;i++){
            cov += ((point_x[i]-point_x[0]) - x_ave)*((point_y[i]-point_y[0]) - y_ave) / 5;
        }
        //  相関係数
        s = cov / (x_cov * y_cov);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("角度");
        alertDialog.setMessage(
                "首の傾き = "+getEfficientRound(Math.toDegrees(Math.acos(cos)),3)+ "度\n"+
                        "体の傾き = "+getEfficientRound(Math.toDegrees(Math.acos(cos2)),3)+ "度\n"
        );
        alertDialog.setPositiveButton("完了", null);
        alertDialog.show();

        //  test



    }

    //  結果の出力
    public void Result(){


    }

    //  四捨五入
    /**
     * 有効数字以下の数値を四捨五入します。
     * @param value　数値
     * @param effectiveDigit　有効数字桁数
     * @return　四捨五入された数値
     */
    public static double getEfficientRound( double value, int effectiveDigit ) {

        int valueDigit = (int)Math.rint( Math.log10( Math.abs(value) ) );
        int roundDigit = valueDigit - effectiveDigit + 1;
        double v = Math.floor( value / Math.pow( 10, roundDigit ) + 0.5 );
        return v * Math.pow( 10, roundDigit );

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
        mImageView.setImageDrawable(null);
        System.gc();
    }
}
