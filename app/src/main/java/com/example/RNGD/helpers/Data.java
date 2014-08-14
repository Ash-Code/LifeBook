package com.example.RNGD.helpers;

import android.graphics.Bitmap;

/**
 * Created by Ashwin Kumar on 8/6/2014.
 */
public class Data {
    public int dbId;
public String title;
    public String body;
    public String images;
    public String date;
    public Bitmap coverImg;
    public String coverImgUri;

    public Data(int id,String t, String b, String i, Bitmap c, String cc, String d){
        dbId=id;
        title=t;
        body=b;
        images=i;
        coverImg=c;
        coverImgUri=cc;
        date=d;
    }

}
