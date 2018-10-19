package com.projectwatermark;

/**
 * Created by HDMP on 03-Apr-17.
 */

public class Media {
    public static String image;
    public static String video;

    public Media(String img,String vid){
        image=img;
        video=vid;
    }
    public static String getImage(){
        return image;
    }
    public static String getVideo(){
        return video;
    }
}
