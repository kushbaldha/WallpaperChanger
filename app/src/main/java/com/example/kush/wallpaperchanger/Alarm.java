package com.example.kush.wallpaperchanger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Kush on 1/26/2018.
 */

public class Alarm extends BroadcastReceiver {
    private boolean sunRise = false;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("my prefs",Context.MODE_PRIVATE);
        int type = intent.getIntExtra("type",-1);
        if(type == 0)
        {
            Calendar temp = Calendar.getInstance();

            double longitude = sharedPref.getFloat("longitude",0);
            double latitude = sharedPref.getFloat("latitude",0);
            com.luckycatlabs.sunrisesunset.dto.Location locationObj = new com.luckycatlabs.sunrisesunset.dto.Location("" + latitude, "" + longitude);
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(locationObj,TimeZone.getDefault());
            Calendar calrise = calculator.getOfficialSunriseCalendarForDate(temp);
            Calendar calset = calculator.getOfficialSunsetCalendarForDate(temp);
            Alarm alarm = new Alarm();
            alarm.setAlarm(context,calrise,true);
            alarm.setAlarm(context, calset,false);
        }
        else if(type >= 1)
        {
            Uri uri = null;
            Bitmap bitmap = null;

            if(type == 1)
            {
                String srPath = sharedPref.getString("SRPath", "");
                if (!srPath.equals("")) {
                    uri = Uri.parse(srPath);
                }
            }
            if(type == 2)
            {
                String ssPath = sharedPref.getString("SSPath", "");
                if (!ssPath.equals("")) {
                    uri = Uri.parse(ssPath);
                }
            }

            int width = sharedPref.getInt("width",10);
            int height = sharedPref.getInt("height",10);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap,width,height,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WallpaperManager manager = WallpaperManager.getInstance(context);
            try {

                manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM);
                manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK);
            }catch(IOException e)
            {
                e.printStackTrace();
            }

        }


    }
    public boolean setAlarm(Context context, Calendar calendar,boolean sunRiseIn)
    {
        sunRise = sunRiseIn;
        Calendar calnow = Calendar.getInstance();
        if(calendar==null)
        {
            return false;
        }
        int num = calendar.compareTo(calnow);
        //if time has passed. That means it is the current time so set the wallpaper
       /* if(num<=0)
        {
            WallpaperManager manager = WallpaperManager.getInstance(context);
            Bitmap bitmap = null;

            if(sunRise)
                bitmap = MainActivity.bitmapSR;
            else
                bitmap = MainActivity.bitmapSS;
            try {

                manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM);
                manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK);
                Toast.makeText(context,"Set Current Wallpaper", Toast.LENGTH_LONG).show();

            }catch(IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }*/

        AlarmManager alrmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,Alarm.class);
        if(sunRise)
        i.putExtra("type",1);
        else
        i.putExtra("type",2);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,i,0);
        alrmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pi);
        Toast.makeText(context,"Set alarm", Toast.LENGTH_LONG).show();
        return true;

    }


    //refresh alarms every midnight
    public void setRefreshAlarm(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,24);


        //sets repeating alarm for everyday at midnight
        AlarmManager alrmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,Alarm.class);
        i.putExtra("type",0);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,i,0);
        alrmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(), 24*60*60*1000 ,pi);
        Toast.makeText(context,"Set alarm", Toast.LENGTH_LONG).show();
    }
    public void cancelAlarm(Context context, int requestCode)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
