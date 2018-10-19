package com.projectwatermark;

/**
 * Created by HDMP on 16-Feb-17.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectwatermark.util.PlayOrShare;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CustomListAdapter extends BaseAdapter {

    private List<String> item = null;
    private List<String> path = null;
    private String root = "/sdcard/Video Watermark/Output";
    private TextView myPath;
    Context context;
    int imageId;
    private static LayoutInflater inflater = null;

    public CustomListAdapter(AndroidExplorer exActivity, int prgmImages) {
        // TODO Auto-generated constructor stub
        myPath = (TextView) exActivity.findViewById(R.id.path);
        getDir(root);
        context = exActivity;
        imageId = prgmImages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.explorer_row, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textR);
        holder.img = (ImageView) rowView.findViewById(R.id.textIcon);
        holder.tv.setText(item.get(position));
        holder.img.setImageResource(imageId);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               File file = new File(path.get(position));
               if(PlayOrShare.getFlag()==0) {
                   Intent i = new Intent(Intent.ACTION_VIEW,Uri.fromFile(file));
                   i.setDataAndType(Uri.fromFile(file),"video/*");
                   context.startActivity(i);

               }else{
                   Intent i=new Intent(context,Share.class);
                   i.putExtra("uri",""+Uri.fromFile(file));
                   context.startActivity(i);

               }

            }
        });

        return rowView;
    }

    private void getDir(String dirPath) {
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (!file.isDirectory())
            {
                String s=file.getName();
                if(s.endsWith(".mp4") || s.endsWith(".mkv") || s.endsWith(".3gp") || s.endsWith(".avi") || s.endsWith(".MP4") || s.endsWith(".MKV") || s.endsWith(".3GP") || s.endsWith(".AVI"))
                item.add(s);
            }
        }
    }
}