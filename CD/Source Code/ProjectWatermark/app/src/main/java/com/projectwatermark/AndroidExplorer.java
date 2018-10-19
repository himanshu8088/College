package com.projectwatermark;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;

public class AndroidExplorer extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer);
        CustomListAdapter adapter = new CustomListAdapter(this, R.drawable.media);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        TextView path=(TextView)findViewById(R.id.path);
        Typeface fontSpaceman=Typeface.createFromAsset(getAssets(),"SF Fedora Titles.ttf");
        path.setTypeface(fontSpaceman);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,Home.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}