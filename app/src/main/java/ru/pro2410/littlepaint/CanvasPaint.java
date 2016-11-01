package ru.pro2410.littlepaint;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CanvasPaint extends SurfaceView implements SurfaceHolder.Callback{
    private PaintThread paintThread;
    public Bitmap mBitmap;

    public static int widthSurface;
    public static int heightSurface;
    private boolean flag;

    public CanvasPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        MainActivity.mPaintThread = getTreadPaint();

        paintThread.setRun(true);
        paintThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mBitmap==null) {
            mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            paintThread.setBitmap(mBitmap,true);
        }else paintThread.setBitmap(mBitmap,false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    boolean repeat = true;
    paintThread.setRun(false);
    while (repeat){
        try {
            paintThread.join();
            repeat = false;
        } catch (InterruptedException e) {}
    }
        paintThread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!getTreadPaint().getIsSitting()) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    paintThread.startDraw(event.getX(),event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    paintThread.moveDraw(event.getX(),event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    paintThread.resetLastXY();
                    if (!flag){
                        paintThread.saveBitmapBuffer();
                        paintThread.isEndo = true;
                    }
                    else flag = false;
                    break;
            }
        }else if (event.getY()>=194&&event.getY()<=778&&paintThread.getIsSitting()){
             CurrentState.currenContext.sittingRelative.setVisibility(GONE);
            flag = true;
            paintThread.setIsSitting(false);
        }
        return true;
    }
    PaintThread getTreadPaint(){
        if (paintThread==null) paintThread = new PaintThread(getHolder(),this);
        return paintThread;
    }
}
