package ru.pro2410.littlepaint;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorBrushDialog extends Dialog {

    private int width;
    private int height;

    public ColorBrushDialog(Context context) {
        super(context);
        width = 412;
        height = 550;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.titleColorDialog);
        setContentView(new ColorBrushView(getContext()));
        getWindow().setLayout(width, height);
    }

    private class ColorBrushView extends View{
        Paint sweepPaint;
        Paint linearPaint;
        Paint centerPaint;
        PaintBrush currentPaintBrush;

        SweepGradient gradientCircle;
        LinearGradient gradientLine;

        private int center_Y = 160; // 418 height
        private int center_X = 190; // 380 width
        private int radiusSweep = 109;
        private int radiusCircleCenter = 45;

        private int colorSweep[];
        private int colorLinear[];
        private int currentColor;

        private int lineX1;
        private int lineX2;
        private int lineY1;
        private int lineY2;

        private boolean isCenterCircle;
        private boolean isLine;
        private boolean isLineTracking;
        private boolean isCenterCircleTracking;

        private int heightView = 418;
        private int widthView = 380;

        public ColorBrushView(Context context) {

            super(context);



            lineX1 = 20;
            lineX2 = center_X*2-20;
            lineY1 = center_Y+radiusSweep+65 - 45;
            lineY2 = center_Y+radiusSweep+65;

            currentPaintBrush = CurrentState.currentPaintBrush;
            currentColor = currentPaintBrush.brushColor;    //CurrentState.currentPaintBrush.brushColor;

            sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sweepPaint.setStyle(Paint.Style.STROKE);
            sweepPaint.setStrokeWidth(45);
            colorSweep = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
                    0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
            gradientCircle = new SweepGradient(center_X,center_Y,colorSweep,null);
            sweepPaint.setShader(gradientCircle);

            colorLinear = getColor(currentColor);
            gradientLine = new LinearGradient(20,center_Y*2+10,center_X*2-20,
                    center_Y*2+10,colorLinear,null, Shader.TileMode.CLAMP);
            linearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linearPaint.setStrokeWidth(45);
            linearPaint.setShader(gradientLine);

            centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            centerPaint.setStrokeWidth(13);
            centerPaint.setColor(currentColor);


        }
        private int[] getColor(int color){
            if (color == Color.BLACK || color == Color.WHITE){
                return new int[]{Color.BLACK,Color.WHITE};
            }
            return new int[]{Color.BLACK,color,Color.WHITE};
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            setMeasuredDimension(widthView,heightView);
        }

        @Override
        protected void onDraw(Canvas canvas) {



            centerPaint.setColor(currentColor);

            canvas.drawCircle(center_X,center_Y,radiusSweep,sweepPaint);
            canvas.drawLine(20,center_Y*2+10,center_X*2-20,
                    center_Y*2+10,linearPaint);
            canvas.drawCircle(center_X,center_Y,radiusCircleCenter,centerPaint);
            if (isCenterCircleTracking) {
                centerPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(center_X,center_Y,radiusCircleCenter+10,centerPaint);
            }

            centerPaint.setStyle(Paint.Style.FILL);
        }
        int ave(int src,int c, float p){return src+Math.round(p*(c-src)); }

        int defineColor(int [] color,float unit){
            if (unit<=0) return color[0];
            if(unit >=1) return color[color.length-1];
            float p = unit*(color.length-1);
            int i = (int) p;
            p-=i;

            int c0 = color[i];
            int c1 = color[i+1];
            int a = ave(Color.alpha(c0),Color.alpha(c1),p);
            int r = ave(Color.red(c0),Color.red(c1),p);
            int g = ave(Color.green(c0),Color.green(c1),p);
            int b = ave(Color.blue(c0),Color.blue(c1),p);

            return Color.argb(a,r,g,b);
        }
        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            String h = String.valueOf(getHeight());
            String w = String.valueOf(getWidth());
            Log.d("Dialog",h);
            Log.d("Dialog",w);

            float x = event.getX() - center_X;
            float y = event.getY() - center_Y;
            isCenterCircle = Math.sqrt(x*x + y*y) <= radiusCircleCenter;

            isLine = event.getY()>=center_Y+radiusSweep+35 && event.getY()<=center_Y*2+35;

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isLineTracking = isLine;
                    isCenterCircleTracking = isCenterCircle;
                    if (isCenterCircle){

                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (isLineTracking){

                        float unit = (x+center_X)/(center_X*2-40);

                        currentColor = defineColor(colorLinear,unit);
                        invalidate();
                    }
                    else if (!isCenterCircleTracking){
                        float angle = (float) Math.atan2(y,x);
                        float unit = angle/(PI*2);
                        if (unit < 0) unit++;
                        currentColor = defineColor(colorSweep,unit);
                        colorLinear = getColor(currentColor);
                        gradientLine = new LinearGradient(20,center_Y*2+10,center_X*2-20,
                                center_Y*2+10,colorLinear,null, Shader.TileMode.CLAMP);
                        linearPaint.setShader(gradientLine);

                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isCenterCircleTracking){
                        isCenterCircleTracking = false;
                        invalidate();
                    }
                    if (isCenterCircle){

                        if (currentPaintBrush.getTypeBrush()!=currentPaintBrush.ERASER) {
                            currentPaintBrush.setColor(currentColor);
                        }
                        dismiss();
                    }
            }

            return true;
        }
    }
}
