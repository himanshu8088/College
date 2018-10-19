package com.projectwatermark;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.projectwatermark.util.PlayOrShare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import android.content.res.AssetManager;
public class Home extends AppCompatActivity implements View.OnTouchListener{
ImageButton btShare,btStart,btCreation,btRate;
TextView txStart,txCreation;
String folderPath;
File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);
            requestPermissions(new String[]{android.Manifest.permission.WAKE_LOCK}, 1);
        }

        /*For Advertisement */
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4827826624748292/1726877160");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        TextView label1=(TextView)findViewById(R.id.textWatermark);
        btStart=((ImageButton)findViewById(R.id.btnStart));
        btCreation=((ImageButton)findViewById(R.id.btnMyWork));
        btShare=((ImageButton)findViewById(R.id.btnShare));
        btRate=((ImageButton)findViewById(R.id.btnRate));
        txStart=(TextView)findViewById(R.id.txtStart);
        txCreation=(TextView)findViewById(R.id.txtCreation);

        Typeface fontSpaceman1=Typeface.createFromAsset(getAssets(),"spaceman.ttf");
        Typeface fontSpaceman2=Typeface.createFromAsset(getAssets(),"GodOfWar.ttf");
        Typeface fontSpaceman3=Typeface.createFromAsset(getAssets(),"New Walt Disney.ttf");

        txStart.setTypeface(fontSpaceman1);
        txCreation.setTypeface(fontSpaceman1);
        label1.setTypeface(fontSpaceman3);

        btStart.setOnTouchListener(this);
        btCreation.setOnTouchListener(this);
        btShare.setOnTouchListener(this);
        btRate.setOnTouchListener(this);
        folderPath = createFolder();
    }
    public void myCreationClicked()
    {
        Intent i=new Intent(this,AndroidExplorer.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        new PlayOrShare(0);
    }

    public void startClicked() {
        Intent intent = new Intent(this,WatermarkActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void shareClicked(){
        Intent i=new Intent(this,AndroidExplorer.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        new PlayOrShare(1);
    }

    private String createFolder()
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        folder = new File(extStorageDirectory, "/Video Watermark/Output");
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        return folder.getPath().toString();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(btStart == v) {
                    btStart.setImageResource(R.drawable.start_touched);
                    txStart.setTextSize(18);
                }
                if (btCreation==v){
                    btCreation.setImageResource(R.drawable.mycreation_touched);
                    txCreation.setTextSize(18);
                }
                if (btShare==v){
                    btShare.setImageResource(R.drawable.share_clicked);

                }
                if (btRate==v){
                    btRate.setImageResource(R.drawable.rate_touch);

                }
                break;
            }
            case MotionEvent.ACTION_UP:

                if(btStart == v) {
                    startClicked();
                    resetStartButton();
                }
                if (btCreation==v){
                    myCreationClicked();
                    resetStartButton();
                    finish();
                }
                if (btShare==v){
                    shareClicked();
                    resetStartButton();
                }
                if(btRate==v){
                    rateClicked();
                    resetStartButton();
                }
                break;
                // Your action here on button click

            case MotionEvent.ACTION_CANCEL: {
                break;
            }
        }
        return true;
    }

    public void rateClicked(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing App")
                .setMessage("Are you sure you want to say bye?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        folder = new File(extStorageDirectory, "/Video Watermark/temp");
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void resetStartButton(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                btStart.setImageResource(R.drawable.start_not_touched);
                txStart.setTextSize(14);

                btCreation.setImageResource(R.drawable.mycreation_not_touched);
                txCreation.setTextSize(14);
                btRate.setImageResource(R.drawable.rate_nt_touch);
                btShare.setImageResource(R.drawable.share_not_clicked);
            }
        }, 200);
    }
}
