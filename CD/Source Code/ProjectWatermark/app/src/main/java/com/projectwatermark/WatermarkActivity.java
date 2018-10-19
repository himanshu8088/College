package com.projectwatermark;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class WatermarkActivity extends AppCompatActivity implements View.OnTouchListener{

    public String pathImage=null;
    public String pathVideo=null;
    Uri uriImage = null;
    public String m_Text = "Hello";
    private static final int READ_REQUEST_CODE1 = 41;
    private static final int READ_REQUEST_CODE2 = 42;
    VideoView videoView;
    Button buttonImg,  buttonTxt;
    FrameLayout frameLayout;
    StickerImageView iv_sticker;
    Uri videoUri;
    static int a=0,b=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watermark);
        createCutomActionBarTitle();
        buttonImg=(Button)findViewById(R.id.bt_imgWatermark);
        buttonTxt=(Button)findViewById(R.id.bt_txtWatermark);
        buttonImg.setOnTouchListener(this);
        buttonTxt.setOnTouchListener(this);
        a=0;
        b=0;
        Typeface fontSpaceman1=Typeface.createFromAsset(getAssets(),"spaceman.ttf");
        buttonTxt.setTypeface(fontSpaceman1);
        buttonImg.setTypeface(fontSpaceman1);
    }
    @Override
    public void onBackPressed() {
        a=0;
        b=0;
        Intent i=new Intent(this,Home.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void createCutomActionBarTitle(){
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        Typeface tf = Typeface.createFromAsset(getAssets(),"New Walt Disney.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(tf);
        ((TextView)v.findViewById(R.id.titleFragment2)).setTypeface(tf);
        //assign the view to the actionbar
        this.getSupportActionBar().setCustomView(v);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(buttonImg == v) {
                    Drawable bottom = getResources().getDrawable(R.drawable.p_touched);
                    buttonImg.setCompoundDrawablesWithIntrinsicBounds(null, null , null, bottom);
                    buttonImg.setTextSize(12);
                }
                if (buttonTxt==v){
                    Drawable bottom = getResources().getDrawable(R.drawable.v_touched);
                    buttonTxt.setCompoundDrawablesWithIntrinsicBounds(null, null , null, bottom);
                    buttonTxt.setTextSize(12);
                }

                break;

                // Your Action here on Button Touch
            }
            case MotionEvent.ACTION_UP:

                if(buttonImg == v) {
                    resetButton();
                    imageButtonClicked();
                }
                if (buttonTxt==v){
                    resetButton();
                    videoButtonClicked();
                }

                break;
            // Your action here on button click

            case MotionEvent.ACTION_CANCEL: {
                break;
            }
        }
        return true;
    }
    public void resetButton(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Drawable bottom1 = getResources().getDrawable(R.drawable.p_not_touched);
                buttonImg.setCompoundDrawablesWithIntrinsicBounds(null, null , null, bottom1);
                buttonImg.setTextSize(14);

                Drawable bottom2 = getResources().getDrawable(R.drawable.v_not_touched);
                buttonTxt.setCompoundDrawablesWithIntrinsicBounds(null, null , null, bottom2);
                buttonTxt.setTextSize(14);
            }
        }, 200);

    }


    public void videoButtonClicked(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, READ_REQUEST_CODE1);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


    public void imageButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE2);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE2 && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                uriImage = resultData.getData();
                pathImage=ExactFilePath.getPath(this,uriImage);
                a=1;
            }
        }
        if (requestCode == READ_REQUEST_CODE1 && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                videoUri = resultData.getData();
                pathVideo=ExactFilePath.getPath(this,videoUri);
                b=1;
            }
        }

        if(a==1 && b==1){
            Intent i=new Intent(this,Video.class);
            i.putExtra("videoPath",pathVideo);
            i.putExtra("imagePath",pathImage);
            startActivity(i);
        }
    }

}

class ExactFilePath
{
    public static String getPath(final Context context, final Uri uri)
    {
        //check here to KITKAT or new version
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}