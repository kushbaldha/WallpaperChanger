package com.example.kush.wallpaperchanger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Kush on 1/26/2018.
 */

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context,"works",Toast.LENGTH_LONG).show();
        System.out.println("works");
         WallpaperManager manager = WallpaperManager.getInstance(context);

       /* try {

            manager.setBitmap(MainActivity.bitmapSR, null, false, WallpaperManager.FLAG_SYSTEM);
            manager.setBitmap(MainActivity.bitmapSR, null, false, WallpaperManager.FLAG_LOCK);
        }catch(IOException e)
        {
            e.printStackTrace();
        }*/


    }
    public void setAlarm(Context context, Calendar calendar)
    {
       /* ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();*/

        Calendar calnow = Calendar.getInstance();
        if(calendar==null)
        {
            return;
        }
        int num = calendar.compareTo(calnow);
        if(num<=0)
        {
            return;
        }

        calnow.add(Calendar.SECOND,3);


        System.out.println(calnow.getTime());
        System.out.println(calendar.getTime());


        AlarmManager alrmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,Alarm.class);
       // i.putExtra("image",byteArray);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,i,0);
        alrmManager.set(AlarmManager.RTC, calnow.getTimeInMillis(),pi);

    }
    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
