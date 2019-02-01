package com.excall.minato.posture_analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Pictured_RuledLine extends View {

    private final Paint paint = new Paint();
    private int y = 0;
    private float StrokeWidth = 2.0f;

    {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10.f);
    }

    public Pictured_RuledLine(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.argb(0, 222, 0, 0));
        paint.setColor(Color.argb(150, 200, 200, 0));
        paint.setStrokeWidth(StrokeWidth);
        paint.setStyle(Paint.Style.STROKE);


        for (int y = 0; y < 50; y++) {
            canvas.drawLine(0, 70 * y, canvas.getWidth(), 70 * y, paint);
            canvas.drawLine(70 * y, 0, 70 * y, canvas.getHeight(), paint);
        }

    }

}
