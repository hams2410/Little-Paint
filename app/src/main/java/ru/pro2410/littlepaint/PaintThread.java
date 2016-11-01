package ru.pro2410.littlepaint;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    public Bitmap tBitmap;

    private PaintBrush tpaintBrush;
    private CanvasPaint tCanvasPaint;
    private SurfaceHolder tSurfaceHolder;
    public Canvas tCanvas;
    private Paint tpaint;

    public ArrayList<byte[]> listUndoBitmap;

     int idCurrent;
    public boolean isEndo;
    public boolean isRedo;
    public boolean isClear;
    private boolean isActive;
    public boolean isSittingDraw;


    public PaintThread(SurfaceHolder surfaceHolder, CanvasPaint canvasPaint){
        isActive = true;
        tSurfaceHolder = surfaceHolder;
        tCanvasPaint = canvasPaint;

        tpaintBrush = CurrentState.currentPaintBrush;

        tpaint = CurrentState.currentPaint;

        CanvasPaint.widthSurface = canvasPaint.getWidth();
        CanvasPaint.heightSurface = canvasPaint.getHeight();

        if (CurrentState.currentList!=null) listUndoBitmap = CurrentState.currentList;
        else listUndoBitmap = new ArrayList<>(30);

        isSitting = CurrentState.isSitting;
        isEndo = CurrentState.isUndo;
        isRedo = CurrentState.isRedo;
        idCurrent = CurrentState.idCurrent;
        isClear = CurrentState.isClear;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    void setRun(boolean run){
        isRun = run;
    }

    @Override
    public void run() {
        while (isRun) {
            if (isActive){
                waitBitmap();
                Canvas canvas = null;
                try {
                    canvas = tSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        if (!isSitting||isSittingDraw) {
                            canvas.drawBitmap(tBitmap, 0, 0, null);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        tSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
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

    public void setBitmap(Bitmap bitmap, boolean clear) {
        tBitmap = bitmap;
        isSittingDraw = true;

        if (clear) {
            tCanvas = new Canvas(bitmap);
            tCanvas.drawColor(Color.WHITE);

            CurrentState.currentBitmap = tBitmap;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    saveBitmapBuffer();
                }
            });
        } else {
            tCanvas = new Canvas(tBitmap);
            CurrentState.currentBitmap = tBitmap;

            if (CurrentState.isClearBitmapAfterOnStop) {
                listUndoBitmap.clear();
                isClear = false;
                isRedo = false;
                isEndo = false;

                AsyncTask.execute(new Runnable() {

                    @Override
                    public void run() {
                        saveBitmapBuffer();
                    }
                });
                CurrentState.isClearBitmapAfterOnStop = false;
            }
        }
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
    public void clearListRedoUndoBuffer(){
        listUndoBitmap.clear();
        isClear = false;
        isRedo = false;
        isEndo = false;

       setBitmap(Bitmap.createBitmap(CanvasPaint.widthSurface,CanvasPaint.heightSurface,Bitmap.Config.ARGB_8888),true);
    }

    void saveBitmapBuffer(){
        if (isClear) {

            listUndoBitmap.subList(listUndoBitmap.size()-idCurrent ,listUndoBitmap.size()).clear();
            isClear = false;
            isRedo = false;
        }
        idCurrent=0;

        if (listUndoBitmap.size()==30)listUndoBitmap.subList(0,4).clear();

        byte []undo = new byte[tBitmap.getRowBytes()*tBitmap.getHeight()];
        ByteBuffer undoBuffer = ByteBuffer.wrap(undo);

        tBitmap.copyPixelsToBuffer(undoBuffer);
        listUndoBitmap.add(undo);
    }

    void returnUndoRedoBitmap(){
        int lastId = listUndoBitmap.size()-1;

        byte[] undo = listUndoBitmap.get(lastId-idCurrent );
        ByteBuffer by = ByteBuffer.wrap(undo);
        tBitmap.copyPixelsFromBuffer(by);
        if (idCurrent==lastId) isEndo = false;
        if (idCurrent==0) isRedo = false;
        isClear = true;

    }
}
