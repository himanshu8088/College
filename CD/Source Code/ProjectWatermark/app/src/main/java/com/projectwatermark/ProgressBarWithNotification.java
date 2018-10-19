package com.projectwatermark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Timer;

public class ProgressBarWithNotification extends AppCompatActivity {

    public ProgressDialog progressBar;
    String fname;
    String workFolder = null;
    String demoVideoFolder = null;
    String demoVideoPath = null;
    String vkLogPath = null;
    LoadJNI vk;
    private final int STOP_TRANSCODING_MSG = -1;
    private final int FINISHED_TRANSCODING_MSG = 0;
    private boolean commandValidationFailedFlag = false;
    String inputVideo="";
    String image="";
    String outputVideo="";
    String overlayPositionCommand="overlay=(W-w)/2:(H-h)/2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(Prefs.TAG, "onCreate ffmpeg4android ProgressBarExample");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar_with_notification);
        createCutomActionBarTitle();
        demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video watermark/temp/";
        demoVideoPath = demoVideoFolder + "in.mp4";
        Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()) );
        Button yes =  (Button)findViewById(R.id.btnYes);
        Button no =  (Button)findViewById(R.id.btnNo);
        no.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce1));
        yes.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce2));

        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Log.i(Prefs.TAG, "run clicked.");

                    File imgFile = new File(Media.getImage());
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                 // int w = (int) (Size.getWidth() * imageBitmap.getWidth() / VideoFrame.getWidth());
                    int w=(int)Size.getWidth();
                    Bitmap newBitmap = Bitmap.createScaledBitmap(imageBitmap, w, w, false);
                    image = saveImage(newBitmap);
                    inputVideo=Media.video;
                Random generator = new Random();
                int n = 1000;
                n = generator.nextInt(n);
                outputVideo = "/sdcard/Video watermark/Output/output_" + n + inputVideo.substring(inputVideo.indexOf('.'),inputVideo.length());
                Log.wtf("mmmmmmmmmmmm",outputVideo);
                if(PositionWatermarkCorner.getCorner()==1) {
                    overlayPositionCommand = "overlay=5:5";
                }else if(PositionWatermarkCorner.getCorner()==2){
                    overlayPositionCommand="overlay=W-w-5:5";
                }else if(PositionWatermarkCorner.getCorner()==3){
                    overlayPositionCommand="overlay=5:H-h-5";
                }else if(PositionWatermarkCorner.getCorner()==4){
                    overlayPositionCommand="overlay=W-w-5:H-h-5";
                }else{
                    overlayPositionCommand="overlay=(W-w)/2:(H-h)/2";
                }
                runTranscoding();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
           finish();
            }
        });

        workFolder = getApplicationContext().getFilesDir() + "/";
        //Log.i(Prefs.TAG, "workFolder: " + workFolder);
        vkLogPath = workFolder + "vk.log";
        GeneralUtils.copyLicenseFromAssetsToSDIfNeeded(this, workFolder);
        GeneralUtils.copyDemoVideoFromAssetsToSDIfNeeded(this, demoVideoFolder);
        int rc = GeneralUtils.isLicenseValid(getApplicationContext(), workFolder);
        Log.i(Prefs.TAG, "License check RC: " + rc);
    }

    public String saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Video Watermark/temp/");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  root + "/Video Watermark/temp/"+fname;
    }

    private void createCutomActionBarTitle(){
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        Typeface tf = Typeface.createFromAsset(getAssets(),"New Walt Disney.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(tf);
        ((TextView)v.findViewById(R.id.titleFragment2)).setTypeface(tf);
        this.getSupportActionBar().setCustomView(v);
        ((TextView)findViewById(R.id.txtMsg)).setTypeface(tf);
    }

    private void runTranscodingUsingLoader() {
        Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

        PowerManager powerManager = (PowerManager)ProgressBarWithNotification.this.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        Log.d(Prefs.TAG, "Acquire wake lock");
        wakeLock.acquire();

          String complexCommand[];
		  complexCommand = new String[]{"ffmpeg", "-y", "-i", inputVideo, "-strict", "experimental", "-vf", "movie=" + image + " [watermark]; [in][watermark] " + overlayPositionCommand + " [out]", "-r", "30", "-b", "15496k", "-vcodec", "mpeg4", "-ab", "48000", "-ac", "2", "-ar", "22050", outputVideo};

        vk = new LoadJNI();
        try {
            vk.run(complexCommand, workFolder, getApplicationContext());
            Log.i(Prefs.TAG, "vk.run finished.");
            GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);

        } catch (CommandValidationException e) {
            Log.e(Prefs.TAG, "vk run exeption.", e);
            commandValidationFailedFlag = true;
            Toast.makeText(this,"Invalid media content. Please try again.",Toast.LENGTH_LONG).show();

        } catch (Throwable e) {
            Log.e(Prefs.TAG, "vk run exeption.", e);
            Toast.makeText(this,"Invalid media content. Please try again.",Toast.LENGTH_LONG).show();
        }
        finally {
            if (wakeLock.isHeld()) {
                wakeLock.release();
                Log.i(Prefs.TAG, "Wake lock released");
            }else{
                Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
            }
        }

        // finished Toast
        String rc = null;
        if (commandValidationFailedFlag) {
            rc = "Command Vaidation Failed";
        }
        else {
            rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
        }
        final String status = rc;
        ProgressBarWithNotification.this.runOnUiThread(new Runnable() {
            public void run() {
                if(status.equalsIgnoreCase("Transcoding Status: Finished OK")){
                    Toast.makeText(ProgressBarWithNotification.this, "Operation Successfully Performed", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(ProgressBarWithNotification.this,Home.class);
                    startActivity(i);
                }
                else
                Toast.makeText(ProgressBarWithNotification.this, status, Toast.LENGTH_LONG).show();
                if (status.equals("Transcoding Status: Failed")) {
                    Toast.makeText(ProgressBarWithNotification.this, "Oops Error Occured. Check: " + vkLogPath + " for more information.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(Prefs.TAG, "Handler got message");
            if (progressBar != null) {
                progressBar.dismiss();
                // stopping the transcoding native
                if (msg.what == STOP_TRANSCODING_MSG) {
                    Log.i(Prefs.TAG, "Got cancel message, calling fexit");
                    vk.fExit(getApplicationContext());


                }
            }
        }
    };

    public void runTranscoding() {
        progressBar = new ProgressDialog(ProgressBarWithNotification.this,R.style.Theme_MyDialog);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
       // progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setTitle("Applying Watermark Please Wait!");
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setIndeterminate(false);
        progressBar.setCancelable(false);
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessage(STOP_TRANSCODING_MSG);
            }
        });

        progressBar.show();

        new Thread() {
            public void run() {
                Log.d(Prefs.TAG,"Worker started");
                try {
                    //sleep(5000);
                    runTranscodingUsingLoader();
                    handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);

                } catch(Exception e) {
                    Log.e("threadmessage",e.getMessage());
                }
            }
        }.start();

        // Progress update thread
        new Thread() {
            ProgressCalculator pc = new ProgressCalculator(vkLogPath);
            public void run() {
                Log.d(Prefs.TAG,"Progress update started");
                int progress = -1;
                try {
                    while (true) {
                        sleep(300);
                        progress = pc.calcProgress();
                        if (progress != 0 && progress < 100) {
                            progressBar.setProgress(progress);
                        }
                        else if (progress == 100) {
                            Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
                            pc.initCalcParamsForNextInter();

                            break;

                        }
                    }

                } catch(Exception e) {
                    Log.e("threadmessage",e.getMessage());
                }
            }
        }.start();
    }
}
