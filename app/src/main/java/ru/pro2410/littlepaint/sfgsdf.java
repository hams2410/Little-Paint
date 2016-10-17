/*
package ru.pro2410.littlepaint;


import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;

import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PaintThread extends Thread{

    private float lastX;
    private float lastY;

    private boolean isRun;
    private boolean isBitmap;
    private boolean isSitting;

    private Bitmap tBitmap;
    private PaintBrush tpaintBrush;
    private CanvasPaint tCanvasPaint;
    private SurfaceHolder tSurfaceHolder;
    public Canvas tCanvas;
    private Paint tpaint;




    public ArrayList<byte[]> listUndoBitmap;
    public ArrayList<byte[]> listRedoBitmap;



    boolean flag;
    public boolean isEndo;
    public boolean isRedo;


    public PaintThread(SurfaceHolder surfaceHolder, CanvasPaint canvasPaint){
        tSurfaceHolder = surfaceHolder;
        tCanvasPaint = canvasPaint;

        tpaintBrush = CurrentState.currentPaintBrush;

        tpaint = CurrentState.currentPaint;

        CanvasPaint.widthSurface = canvasPaint.getWidth();
        CanvasPaint.heightSurface = canvasPaint.getHeight();
        listUndoBitmap = new ArrayList<>(30);
        listRedoBitmap = new ArrayList<>(30);



    }

    void setRun(boolean run){
        isRun = run;
    }

    @Override
    public void run() {
        while (isRun) {
            waitBitmap();
            Canvas canvas = null;
            try {
                canvas = tSurfaceHolder.lockCanvas();
                if (canvas!=null) {
                    if (!isSitting) {
                        canvas.drawBitmap(tBitmap,0,0,null);
                    }
                        */
/*else if (isSitting){
                            canvas.drawColor(Color.WHITE);
                            switch (tpaintBrush.getTypeBrush()){
                                case PaintBrush.ERASER:
                                    Paint paint = new Paint();
                                    paint.setColor(Color.BLACK);
                                    paint.setStyle(Paint.Style.STROKE);

                                    canvas.drawCircle(CanvasPaint.widthSurface/2,CanvasPaint.heightSurface/2,
                                            tpaintBrush.brushSize *.5f,paint);

                                    break;
                                case PaintBrush.POUR:
                                    canvas.drawColor(tpaintBrush.brushColor);
                                    break;
                                default: canvas.drawLine(CanvasPaint.widthSurface/2-200,CanvasPaint.heightSurface/2,
                                        CanvasPaint.widthSurface/2+200,CanvasPaint.heightSurface/2,tpaint);
                            }
                        }*//*

                }
            } finally {
                if (canvas!=null) {
                    tSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    void waitBitmap (){

        while (tBitmap==null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setBitmap(Bitmap bitmap) {
        tBitmap = bitmap;
        tCanvas = new Canvas(bitmap);
        tCanvas.drawColor(Color.WHITE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                saveBitmapBuffer();
            }
        });




    }

    public void startDraw(float x, float y){


        if (tpaintBrush.getTypeBrush()==PaintBrush.POUR){
            tCanvas.drawColor(tpaintBrush.brushColor);


            return;
        }


        tCanvas.drawCircle(x,y,tpaintBrush.brushSize *0.5f,tpaint);
        lastX = x;
        lastY = y;

    }

    public void moveDraw(float x, float y) {
        isRedo = false;
        listRedoBitmap.clear();

        if(lastX<=0||lastY<=0) return;
        if (tpaintBrush.getTypeBrush()==PaintBrush.POUR) return;
        tCanvas.drawLine(lastX,lastY,x,y,tpaint);

        lastX = x;
        lastY = y;


    }
    void setIsSitting(boolean isSitting){
        this.isSitting = isSitting;
    }
    boolean getIsSitting(){return isSitting;}

    public void resetLastXY() {lastX=-1; lastY=-1;
    }

    void saveBitmapBuffer(){
        if (listUndoBitmap.size()==30)listUndoBitmap.subList(0,4).clear();

        byte []undo = new byte[tBitmap.getRowBytes()*tBitmap.getHeight()];
        ByteBuffer undoBuffer = ByteBuffer.wrap(undo);

        tBitmap.copyPixelsToBuffer(undoBuffer);
        listUndoBitmap.add(undo);
        isEndo = true;

    }

    void returnUndoBitmap(){
        int lastId = listUndoBitmap.size()-1;
        if (lastId !=0) {

            byte[] undo = listUndoBitmap.get(lastId-1 );
            ByteBuffer by = ByteBuffer.wrap(undo);
            tBitmap.copyPixelsFromBuffer(by);

            listRedoBitmap.add(listUndoBitmap.get(lastId));
            isRedo = true;
            listUndoBitmap.remove(lastId);

            if (listUndoBitmap.size()==1) isEndo = false;
        }

    }
    void returnRedoBitmap(){
        int lastId = listRedoBitmap.size()-1;

        if (lastId!=0){
            byte[] undo = listRedoBitmap.get(lastId-1 );
            ByteBuffer by = ByteBuffer.wrap(undo);
            tBitmap.copyPixelsFromBuffer(by);

        }
    }
}
*/
