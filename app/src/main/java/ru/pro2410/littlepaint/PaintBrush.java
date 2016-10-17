package ru.pro2410.littlepaint;


import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;

public class PaintBrush {

    public int brushSize;
    public int brushColor;
    public float blurRadius;
    public BlurMaskFilter.Blur blurStyle;
    public int typeBrush;

    private Paint bPaint;


    public static final int PENCIL = 1;
    public static final int PEN = 2;
    public static final int MARKER = 3;
    public static final int BRUSH = 4;
    public static final int ERASER = 5;
    public static final int POUR = 6;
    public static final int CUSTOM = 7;

    int getTypeBrush(){return typeBrush;}

    public PaintBrush() {
        bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        CurrentState.currentPaint = bPaint;
        bPaint.setStrokeCap(Paint.Cap.ROUND);
        setColor(Color.BLACK);
    }

    public void setColor(int color) {
        if (typeBrush != ERASER) {
            brushColor = color;
        }
        bPaint.setColor(color);
    }

    public void setType(int typeBrush) {
        this.typeBrush = typeBrush;
        switch (typeBrush){
            case PENCIL:
                setColor(brushColor);
                setSize(2);
                setStyle(10, BlurMaskFilter.Blur.INNER);
                break;
            case PEN:
                setColor(brushColor);
                setSize(2);
                setStyle(0, null);
                break;
            case BRUSH:
                setColor(brushColor);
                setSize(15);
                setStyle(18, BlurMaskFilter.Blur.NORMAL);
                break;
            case MARKER:
                setColor(brushColor);
                setSize(20);
                setStyle(0, null);
                break;
            case CUSTOM:
                setColor(brushColor);
                break;
            case ERASER:
                setColor(Color.WHITE);
                setSize(20);
                setStyle(18, BlurMaskFilter.Blur.NORMAL);
                break;
            case POUR:
                setColor(brushColor);
            break;
        }
    }

    public void setStyle(float radius, BlurMaskFilter.Blur style) {
       if (style == null) {
           bPaint.setMaskFilter(null);
           blurRadius = 0;
           blurStyle = null;
           return;
       }
        blurRadius = radius;
        blurStyle = style;
        bPaint.setMaskFilter(new BlurMaskFilter(radius,style));
    }

    public void setSize(int size) {
        brushSize = size;
        bPaint.setStrokeWidth(size);
    }
    public int getBlurType(){
        if (blurStyle==null) return 0;
        return blurStyle.ordinal()+1;
    }
    public void setBlurType(int position){
if (blurRadius==0)blurRadius++;
        switch (position){
            case 0: setStyle(0,null);break;
            case 1: setStyle(blurRadius, BlurMaskFilter.Blur.NORMAL);break;
            case 2: setStyle(blurRadius, BlurMaskFilter.Blur.SOLID);break;
            case 3: setStyle(blurRadius, BlurMaskFilter.Blur.OUTER);break;
            case 4: setStyle(blurRadius, BlurMaskFilter.Blur.INNER);break;
        }
    }
}
