package com.d3iftelu.gooddayteam.speechtrash;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Sholeh Hermawan on 30/04/2018.
 */

public class ProcessingHelper {

    public String changeUnixTimeStampToStringDate(long unixTimeStamp){
        Date date = new Date(unixTimeStamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy (HH:mm)");
        String formattedDate = sdf.format(date);
        Log.i(TAG, "changeUnixTimeStampToStringDate: " + formattedDate);
        return formattedDate;
    }

    public String changeToTime(long unixTimeStamp){
        Date date = new Date(unixTimeStamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedDate = sdf.format(date);
        Log.i(TAG, "changeToTime: " + formattedDate);
        return formattedDate;
    }

    public String changeToDate(long unixTimeStamp){
        Date date = new Date(unixTimeStamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = sdf.format(date);
        Log.i(TAG, "changeToDate: " + formattedDate);
        return formattedDate;
    }

    public String changeToChild(long unixTimeStamp){
        Date date = new Date(unixTimeStamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
        String formattedDate = sdf.format(date);
        Log.i(TAG, "changeToChild: " + formattedDate);
        return formattedDate;
    }

    public long getDateNow(){
        return System.currentTimeMillis()/1000L;
    }
}
