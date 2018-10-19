package com.projectwatermark;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.animation.Animator.*;

public class Share extends AppCompatActivity implements View.OnTouchListener {

    ImageButton fb,twitter,whatsapp,instagram;
    Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_pop_up);
        createCutomActionBarTitle();
        whatsapp=(ImageButton)findViewById(R.id.btnWhatsapp);
        whatsapp.setOnTouchListener(this);
        instagram=(ImageButton)findViewById(R.id.btnInstagram);
        instagram.setOnTouchListener(this);
        fb=(ImageButton)findViewById(R.id.btnFb);
        fb.setOnTouchListener(this);
        twitter=(ImageButton)findViewById(R.id.btnTwitter);
        twitter.setOnTouchListener(this);
        instagram.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_left_view));
        twitter.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_right_view));
        whatsapp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_left_view));
        fb.startAnimation(AnimationUtils.loadAnimation(this,  R.anim.slide_from_right_view));
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
        ((TextView)findViewById(R.id.txtSt)).setTypeface(tf);
        this.getSupportActionBar().setCustomView(v);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(whatsapp == v) {
                    whatsapp.setImageResource(R.drawable.whats_t);
                }
                if(instagram==v){
                    instagram.setImageResource(R.drawable.insta_t);
                }
                if(fb==v){
                    fb.setImageResource(R.drawable.fb_t);
                }
                if(twitter==v){
                    twitter.setImageResource(R.drawable.twit_t);
                }

                break;
            }
            case MotionEvent.ACTION_UP:
                String packageInfo="",url="",msg="";
                if(whatsapp == v) {
                    packageInfo="com.whatsapp";
                    url="https://www.whatsapp.com/";
                    msg="WhatsApp not Installed";

                }
                if(instagram == v) {
                    packageInfo="com.instagram.android";
                    url="https://www.instagram.com/";
                    msg="Instagram not Installed";

                }
                if(twitter == v) {
                    packageInfo="com.twitter.android";
                    url="https://twitter.com/";
                    msg="Twitter not Installed";
                }
                if(fb == v) {
                    packageInfo="com.facebook.katana";
                    url="https://www.facebook.com/";
                    msg="Facebook not Installed";
                }
                resetButton();

                Bundle bundle = getIntent().getExtras();
                Uri uriV=Uri.parse(bundle.getString("uri"));
                PackageManager pm=getPackageManager();
                try {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    PackageInfo info=pm.getPackageInfo(packageInfo, PackageManager.GET_META_DATA);
                    intent.setPackage(packageInfo);
                    intent.putExtra(Intent.EXTRA_STREAM, uriV);
                    startActivity(Intent.createChooser(intent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            // Your action here on button click

            case MotionEvent.ACTION_CANCEL: {
                whatsapp.clearAnimation();
                instagram.clearAnimation();
                twitter.clearAnimation();
                fb.clearAnimation();
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
                whatsapp.setImageResource(R.drawable.whats_nt);
                instagram.setImageResource(R.drawable.insta_nt);
                fb.setImageResource(R.drawable.fb_nt);
                twitter.setImageResource(R.drawable.twit_nt);

            }
        }, 200);
    }
}
