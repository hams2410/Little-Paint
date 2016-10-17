package ru.pro2410.littlepaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {


    private CanvasPaint canvasPaint;

    public RelativeLayout sittingRelative;
    private LinearLayout linearBrush;


    private SeekBar seekBarSize;
    private SeekBar seekBarRadius;

    private TextView textSize;
    private TextView textRadius;
    private TextView textPallete;
    private TextView textTypeBlur;
    private Spinner mSpinner;
    boolean isProgramchangeItem ;

    private PaintBrush mPaintBrush;


    public static PaintThread mPaintThread;
    private boolean isCallSpinner;

    private final static int INDEX_CLEAR = 1;
    private final static int INDEX_EXIT = 2;
    private boolean isCancellKey;


    private class SaveBitmapTask extends AsyncTask<Void,Void,Void>{
        Dialog dialog = new Dialog(MainActivity.this,R.style.dialog_theme);

        @Override
        protected void onPreExecute() {
            mPaintThread.setActive(false);
            dialog.setContentView(R.layout.progress);

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            saveBitmap();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.hide();
            mPaintThread.setActive(true);
            if (isCancellKey) finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasPaint = (CanvasPaint) findViewById(R.id.idCanvas);
        sittingRelative = (RelativeLayout) findViewById(R.id.sittingRelative);
        linearBrush = (LinearLayout) findViewById(R.id.idLinearBrush);

        isProgramchangeItem = false;
        textTypeBlur = (TextView) findViewById(R.id.idTextTypeBlur);

        mPaintBrush = new PaintBrush();
        mPaintBrush.setType(PaintBrush.PENCIL);
        CurrentState.currentPaintBrush = mPaintBrush;

        textPallete = (TextView) findViewById(R.id.idTextPallete);

        seekBarSize = (SeekBar) findViewById(R.id.idSeekbarSize);
        seekBarSize.setMax(70);
        seekBarSize.setProgress(mPaintBrush.brushSize);
        String str = getResources().getText(R.string.size)+" "+String.valueOf(seekBarSize.getProgress());
        textSize = (TextView) findViewById(R.id.idTextSize);
        textSize.setText(str);

        CurrentState.currenContext = this;

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                if (progress==0) progress+=2;

                mPaintBrush.setSize(progress);
                String str = getResources().getText(R.string.size)+" "+String.valueOf(progress);

                textSize.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {resetPreset();}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        isCallSpinner = false;

        seekBarRadius = (SeekBar) findViewById(R.id.idSeekbarRadius);
        seekBarRadius.setMax(70);
        seekBarRadius.setProgress((int) mPaintBrush.blurRadius);
        String str1 = getResources().getText(R.string.radius)+" "+String.valueOf(seekBarRadius.getProgress());
        textRadius = (TextView) findViewById(R.id.idTextRadius);
        textRadius.setText(str1);
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress>0){
                    mPaintBrush.setStyle(progress,mPaintBrush.blurStyle==null?
                            BlurMaskFilter.Blur.NORMAL:mPaintBrush.blurStyle);
                    if (fromUser) {
                        isCallSpinner = true;
                        mSpinner.setSelection(mPaintBrush.getBlurType());
                    }

                }
                else {
                    mPaintBrush.setStyle(0,null);
                    if (fromUser) {
                        isCallSpinner = true;
                        mSpinner.setSelection(0);
                    }

                }
                String str1 =  getResources().getText(R.string.radius)+" "+String.valueOf(progress);
                textRadius.setText(str1);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {resetPreset();}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mSpinner = (Spinner) findViewById(R.id.idSpinner);
        isProgramchangeItem = true;
        mSpinner.setSelection(mPaintBrush.getBlurType());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isProgramchangeItem) {
                    resetPreset();
                    if (position==0){
                        if (!isCallSpinner) {
                            seekBarRadius.setProgress(0);
                        }else isCallSpinner = false;
                    }
                    else {
                            mPaintBrush.setBlurType(position);
                            seekBarRadius.setProgress((int) mPaintBrush.blurRadius);
                    }
                } else isProgramchangeItem = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.idBrush: sittingRelative.setVisibility(View.VISIBLE);
                 mPaintThread.setIsSitting(true); break;
            case R.id.idClear:
                createDialogFragment(INDEX_CLEAR);
                break;
            case R.id.idUnder:
                mPaintThread.idCurrent++; //-
                mPaintThread.returnUndoRedoBitmap();
                mPaintThread.isRedo = true;
                break;
            case R.id.idRedo:
                mPaintThread.idCurrent--; //+
                mPaintThread.returnUndoRedoBitmap();
                mPaintThread.isEndo = true;
                break;
            case R.id.idSave:
                savePicture();
                break;
            case R.id.idFileOpen:
                openFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mPaintThread.isEndo)menu.getItem(3).setEnabled(false);
        else menu.getItem(3).setEnabled(true);

        if (!mPaintThread.isRedo)menu.getItem(4).setEnabled(false);
        else menu.getItem(4).setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU&&mPaintThread.getIsSitting()) {return true;}
        if (keyCode == KeyEvent.KEYCODE_BACK&&mPaintThread.getIsSitting()) {
            sittingRelative.setVisibility(View.GONE);
            mPaintThread.setIsSitting(false);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK&&!mPaintThread.getIsSitting()){
            dialogCreate(INDEX_EXIT);
            isCancellKey = true;

        }
        return super.onKeyDown(keyCode, event);
    }

    private void openFile() {
        if (!isStorageAvailable()) return;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 23);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 23) {


            Bitmap bitmap  = getPicture(data.getData());
            Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888,true);

            int height = bitmap1.getHeight();
            int width = bitmap1.getWidth();

            if (CanvasPaint.heightSurface!=height||CanvasPaint.widthSurface!=width){
                Toast.makeText(this,R.string.isNotValidFile,Toast.LENGTH_SHORT).show();
                return;
            }
            CurrentState.isClearBitmapAfterOnStop = true;
            canvasPaint.mBitmap = bitmap1;


        }
    }


    public Bitmap getPicture(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        cursor.moveToFirst();

        String str[] = cursor.getColumnNames();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    private void createDialogFragment (int index){
        DialogFragment dialog = null;

        switch (index) {
            case INDEX_CLEAR:
                dialogCreate(INDEX_CLEAR);
                break;
            case INDEX_EXIT:
                dialogCreate(INDEX_EXIT);
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        CurrentState.isSitting = mPaintThread.getIsSitting();
        CurrentState.currentList = mPaintThread.listUndoBitmap;
        CurrentState.isRedo = mPaintThread.isRedo;
        CurrentState.isUndo = mPaintThread.isEndo;
        CurrentState.idCurrent = mPaintThread.idCurrent;
        CurrentState.isClear = mPaintThread.isClear;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CurrentState.currentList = null;
        CurrentState.isRedo = false;
        CurrentState.isUndo = false;
        CurrentState.idCurrent = 0;
    }

    private void dialogCreate(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (index){
            case INDEX_CLEAR:
                builder.setMessage(R.string.messageClear)
                        .setPositiveButton(R.string.buttonPositiv, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //mPaintThread .tCanvas.drawColor(Color.WHITE);
                                mPaintThread.clearListRedoUndoBuffer();
                            }
                        })
                        .setNegativeButton(R.string.buttonNegative, null).show();
                break;
            case INDEX_EXIT:
                builder.setMessage(R.string.messageExit)
                        .setPositiveButton(R.string.buttonPositiv, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savePicture();

                            }
                        })
                        .setNegativeButton(R.string.buttonNegative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
        }
    }

    private boolean isStorageAvailable() {
        String state = Environment.getExternalStorageState(); // запрашиваем состояние внешнего хранилища

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Toast.makeText(this, R.string.sd_card_not_writeable,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.sd_card_not_available,
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void savePicture(){
        if (!isStorageAvailable()) return;
        new SaveBitmapTask().execute();
    }

    private String getFilePath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator +getString(R.string.app_name) + File.separator;
        String name = "Paint";
        int count = 0;
        String format = ".png";
        String name1 = "Paint.png";
        String pathname = path + name1; //name + count + format;

        File file = new File(path);

        if(!file.exists()) {
            file.mkdirs();
        }
        while (new File(pathname).exists()){
            count++;
            pathname = path + name + count + format;
        }
        return pathname;
    }

    private void saveBitmap() {

        File f = new File (getFilePath());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            CurrentState.currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            Uri uri = Uri.fromFile(f);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPreset(View view){

        switch (view.getId()){
            case R.id.idButtonPencil:
                mPaintBrush.setType(PaintBrush.PENCIL); break;
            case R.id.idButtonPen:
                mPaintBrush.setType(PaintBrush.PEN);break;
            case R.id.idButtonMarker:
                mPaintBrush.setType(PaintBrush.MARKER);break;
            case R.id.idButtonBrush:
                mPaintBrush.setType(PaintBrush.BRUSH);break;
            case R.id.idButtonEraser:
                mPaintBrush.setType(PaintBrush.ERASER);break;
            case R.id.idButtonPour:
                mPaintBrush.setType(PaintBrush.POUR);break;
        }

        if (mPaintBrush.getTypeBrush()!=PaintBrush.POUR) {

            seekBarSize.setProgress(mPaintBrush.brushSize);
            seekBarRadius.setProgress((int) mPaintBrush.blurRadius);

            textRadius.setTextColor(Color.BLACK);
            textSize.setTextColor(Color.BLACK);

            seekBarRadius.setEnabled(true);
            seekBarSize.setEnabled(true);

            textTypeBlur.setTextColor(Color.BLACK);
            mSpinner.setEnabled(true);

            isProgramchangeItem = true;
            mSpinner.setSelection(mPaintBrush.getBlurType());

        }else {
            seekBarSize.setProgress(0);
            seekBarSize.setEnabled(false);
            seekBarRadius.setProgress(0);
            seekBarRadius.setEnabled(false);

            textRadius.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorNotClickable,null));
            textSize.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorNotClickable,null));

            isProgramchangeItem = true;
            mSpinner.setSelection(0);

            textTypeBlur.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorNotClickable,null));
            mSpinner.setEnabled(false);
        }

        ImageButton button = (ImageButton) findViewById(R.id.idButtonPallete);

        if (mPaintBrush.getTypeBrush()==PaintBrush.ERASER){
            button.setClickable(false);
            button.setAlpha(0.5f);
            textPallete.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorNotClickable,null));
        }
        else {
            button.setClickable(true);
            button.setAlpha(1.0f);
            textPallete.setTextColor(Color.BLACK);
        }

        resetPreset();
        setSelectedBrush(view);

    }

    private void setSelectedBrush(View view) {
        view.setBackgroundResource(R.drawable.shape2);
    }


    private void resetPreset() {
        int count=linearBrush.getChildCount();
        ImageButton button;
        for (int i = 0; i < count; i++) {
            button= (ImageButton) linearBrush.getChildAt(i);
            button.setBackgroundResource(R.drawable.shape);
        }
    }
    public void setColorBrush(View view){new ColorBrushDialog(this).show();}
}
