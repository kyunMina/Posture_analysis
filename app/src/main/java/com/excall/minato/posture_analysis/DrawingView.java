package com.excall.minato.posture_analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private final HistoryStack<ArrayList<PointF>> history = new HistoryStack<ArrayList<PointF>>();
    private final Paint paint = new Paint();
    public MainActivity mainActivity = new MainActivity();
    public int points = 0;
    public float point_x[] = new float[100];
    public float point_y[] = new float[100];
    private ArrayList<PointF> currentStroke;


    {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10.f);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    /**
     * アンドゥ
     */
    public void undo() {
        history.undo();
        invalidate();
        points--;
    }

    /**
     * リドゥ
     */
    public void redo() {
        history.redo();
        invalidate();
        points++;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 新しい描画
            currentStroke = new ArrayList<PointF>();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            //invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            currentStroke.add(new PointF(event.getX(), event.getY()));
            currentStroke.add(new PointF(event.getX() - 1, event.getY() - 1));
            history.add(currentStroke);
            currentStroke = null;

            point_x[points] = event.getX();
            point_y[points] = event.getY();

            if (points < 5) {
                invalidate();
                points++;
            }

            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * PointFの配列を元に一連の線を描画する
     *
     * @param canvas
     * @param paint
     * @param stroke
     */
    private void drawStroke(Canvas canvas, Paint paint, ArrayList<PointF> stroke) {

        PointF startPoint = null;
        for (PointF pf : stroke) {
            if (startPoint != null) {
                canvas.drawCircle(pf.x, pf.y, 20, paint);
            }

            startPoint = pf;

            if (points == 5) {
                for (int i = 0; i < 4; i++) {
                    canvas.drawLine(point_x[i], point_y[i], point_x[i + 1], point_y[i + 1], paint);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 履歴に入っている線を描画する
        for (final ArrayList<PointF> stroke : history.iterateUndo()) {
            drawStroke(canvas, paint, stroke);
        }

        // 現在描画中の線を描画する
        if (currentStroke != null) {
            drawStroke(canvas, paint, currentStroke);
        }
    }
}
