package ru.pro2410.littlepaint;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;

public class CurrentState {

    public static Bitmap currentBitmap;
    public static PaintBrush currentPaintBrush;

    public static Paint currentPaint;
    public static MainActivity currenContext;

    public static ArrayList<byte[]> currentList;
    public static boolean isSitting;
    public static boolean isRedo;
    public static boolean isUndo;
    public static int idCurrent;
    public static boolean isClear;
    public static boolean isClearBitmapAfterOnStop;
}
