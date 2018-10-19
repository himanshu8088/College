package com.projectwatermark;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;



public class Video extends AppCompatActivity implements View.OnTouchListener,SeekBar.OnSeekBarChangeListener {

    FrameLayout frmLayout;
    StickerImageView iv_sticker;
    VideoView videoView;
    ImageButton bTopLeft,bTopRight,bCenter,bBottomLeft,bBottomRight, bPlay, bPause;
    int padX,padY;
    Bitmap bmp;
    View vv;
    SeekBar seekBar;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        createCutomActionBarTitle();
        videoView=(VideoView)findViewById(R.id.videoV);
        frmLayout=(FrameLayout)findViewById(R.id.frameVideo);
        bTopLeft=(ImageButton)findViewById(R.id.btnTL);
        bTopRight=(ImageButton)findViewById(R.id.btnTR);
        bCenter=(ImageButton)findViewById(R.id.btnC);
        bBottomLeft=(ImageButton)findViewById(R.id.btnBL);
        bBottomRight=(ImageButton)findViewById(R.id.btnBR);
        bPlay=(ImageButton)findViewById(R.id.btnPlay);
        bPause=(ImageButton)findViewById(R.id.btnPause);

        seekBar=(SeekBar)findViewById(R.id.seekBar);
        Typeface tf = Typeface.createFromAsset(getAssets(),"New Walt Disney.ttf");
        ((TextView)findViewById(R.id.txtLabel)).setTypeface(tf);


        bTopLeft.setOnTouchListener(this);
        bTopRight.setOnTouchListener(this);
        bCenter.setOnTouchListener(this);
        bBottomLeft.setOnTouchListener(this);
        bBottomRight.setOnTouchListener(this);
        bPlay.setOnTouchListener(this);
        bPause.setOnTouchListener(this);
        Bundle bundle = getIntent().getExtras();
        Uri uriV=Uri.parse(bundle.getString("videoPath"));
        Media m=null;
        showVideo(uriV);
             String imagePath = bundle.getString("imagePath");
             m= new Media(imagePath, bundle.getString("videoPath"));
             File imgFile = new  File(imagePath);
             bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
             iv_sticker = new StickerImageView(this);
             iv_sticker.setImageBitmap(bmp);
             frmLayout.addView(iv_sticker);
        new PositionWatermarkCorner(5);
        new VideoFrame(getWindowManager().getDefaultDisplay().getWidth());
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            seekBar.getThumb().setColorFilter(Color.parseColor("#C51162"), PorterDuff.Mode.SRC_IN);
        }
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
    public void onBackPressed() {
        frmLayout.removeView(iv_sticker);
        Intent i=new Intent(this,WatermarkActivity.class);
        startActivity(i);
        finish();
        return;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(v==bPlay){
                videoView.start();
                updateProgressBar();
                bPlay.setImageResource(R.drawable.pl);
            }else if(v==bPause){
                videoView.pause();
                updateProgressBar();
                bPause.setImageResource(R.drawable.pa);
            }
        }

        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            if(v==bTopLeft){
                frmLayout.removeView(iv_sticker);
                iv_sticker.setX(0);
                iv_sticker.setY(0);
                new PositionWatermarkCorner(1);
                frmLayout.addView(iv_sticker);
            }else if(v==bTopRight){
                frmLayout.removeView(iv_sticker);
                iv_sticker.setX(videoView.getWidth()-iv_sticker.getWidth());
                iv_sticker.setY(0);
                new PositionWatermarkCorner(2);
                frmLayout.addView(iv_sticker);
            }else if(v==bCenter){
                frmLayout.removeView(iv_sticker);
                iv_sticker = new StickerImageView(this);
                iv_sticker.setImageBitmap(bmp);
                new PositionWatermarkCorner(5);
                frmLayout.addView(iv_sticker);
            }else if(v==bBottomLeft){
                frmLayout.removeView(iv_sticker);
                iv_sticker.setX(0);
                iv_sticker.setY(videoView.getHeight()-iv_sticker.getHeight());
                new PositionWatermarkCorner(3);
                frmLayout.addView(iv_sticker);
            }else if(v==bBottomRight){
                frmLayout.removeView(iv_sticker);
                iv_sticker.setX(videoView.getWidth()-iv_sticker.getWidth());
                iv_sticker.setY(videoView.getHeight()-iv_sticker.getHeight());
                new PositionWatermarkCorner(4);
                frmLayout.addView(iv_sticker);
            }else if(v==bPlay){
                resetButton();
            }else if(v==bPause){
                resetButton();
            }
        }
        return true;
    }

    public void showVideo(Uri uri){
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.pause();
    }

    public void resetButton(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                bPlay.setImageResource(R.drawable.p);
                bPause.setImageResource(R.drawable.pause);
            }
        }, 200);

    }

    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            seekBar.setProgress(videoView.getCurrentPosition());
            seekBar.setMax(videoView.getDuration());
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekbar, int progress,boolean fromTouch) {

    }
    @Override
    public void onStartTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekbar) {
        mHandler.removeCallbacks(updateTimeTask);
        videoView.seekTo(seekbar.getProgress());
        updateProgressBar();
    }
}

