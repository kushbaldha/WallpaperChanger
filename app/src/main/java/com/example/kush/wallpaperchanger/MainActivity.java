package com.example.kush.wallpaperchanger;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int PICK_SUNRISE = 1;
    public static final int PICK_SUNSET =  2;
    public boolean ACTIVE = false;
    public String srPath ="";
    public String ssPath = "";
    public Bitmap bitmapSR = null;
    public Bitmap bitmapSS = null;
    public int rotateSR = 0;
    public int rotateSS = 0;
    public Matrix matrix = new Matrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"We need storage permission to choose wallpapers from your gallery.",Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"We need location permission to calculate time of sunset and sunrise",Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    3);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"We need location permission to calculate time of sunset and sunrise",Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MANAGE_DOCUMENTS},
                    3);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button refreshButt = findViewById(R.id.refreshButt);
        refreshButt.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                findLocation();
            }
        });


        //Getting previous activate value
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int activVal = sharedPref.getInt("Activate Value", 0);
        if(activVal == 1)
        {
            ACTIVE = true;
        }
        else if(activVal == 2)
        {
            ACTIVE = false;
        }
        setActiveText();


        //setting Sunrise and Sunset Pictures

            Uri uri;
            ssPath = sharedPref.getString("SSPath", "");
            if (!ssPath.equals("")) {
                uri = Uri.parse(ssPath);
                setImage(uri, false);
            }
            srPath = sharedPref.getString("SRPath", "");
            if (!srPath.equals("")) {
                uri = Uri.parse(srPath);
                  setImage(uri,true);
            }

        //rotating images 
        for(int i = 0; i < rotateSR;i++)
        {
            bitmapSR = rotate(bitmapSR,true);
        }
        for(int i = 0; i < rotateSS;i++)
        {
            bitmapSS = rotate(bitmapSS,false);
        }
        setImage(bitmapSR,true);
        setImage(bitmapSS,false);

        //Listening for Activate
        Button activButt = findViewById(R.id.activButt);
        activButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ACTIVE = !ACTIVE;
                setActiveText();
            }
        });
        Button rotateSRButt = findViewById(R.id.RotateSR);
        rotateSRButt.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view)
            {
                rotateSR = (rotateSR+1)%3;
                if(bitmapSR!=null)
                    setImage(rotate(bitmapSR,true),true);
            }
        });
        Button rotateSSButt = findViewById(R.id.RotateSS);
        rotateSSButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                rotateSS = (rotateSS+1)%3;
                if(bitmapSS!=null)
                    setImage(rotate(bitmapSS,false),false);
            }
        });


        //Choosing Sunrise and Sunset Button
        Button chooseSunrise = findViewById(R.id.ChooseSRID);
        chooseSunrise.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
               passIntent(true);
            }
        });
        Button chooseSunset = findViewById(R.id.ChooseSSID);
        chooseSunset.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                passIntent(false);
            }
        });




    }
    @Override
    public void onStart()
    {
        super.onStart();

    }
    public void passIntent(boolean sunRise)
    {
        int pick = 0;
        if(sunRise)
            pick = 1;
        else
            pick = 2;
        Intent intent;
        if (Build.VERSION.SDK_INT < 19){
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        }
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, pick);
        //intent = new Intent();
            //intent.setAction(Intent.ACTION_GET_CONTENT);
           // intent.setType("*/*");
            //intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
           // intent.addCategory(Intent.CATEGORY_OPENABLE);
           // intent.setType("image/*");
           // startActivityForResult(intent, pick);

    }
    @Override
    protected void onPause()
    {
        super.onPause();

        //Storing Activate Value
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int activVal = 0;
        if(ACTIVE)
            activVal = 1;
        else
            activVal =2;
        editor.putInt("Activate Value", activVal);

        editor.commit();


    }

    public void setActiveText()
    {
        TextView text =  findViewById(R.id.activInfo);
        if(ACTIVE) {
            text.setText("ACTIVATED");
            text.setTextColor(Color.GREEN);
        }
        else{
            text.setText("NOT ACTIVATED");
            text.setTextColor(Color.RED);
        }
    }
    public void findLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                //TextView LatitudeText = (TextView) findViewById(R.id.LatitudeID);
                                //TextView LongitudeText = (TextView) findViewById(R.id.LongitudeID);
                                DecimalFormat df = new DecimalFormat("#.####");
                                df.setRoundingMode(RoundingMode.DOWN);
                               // LatitudeText.setText("Latitude : " + df.format(location.getLatitude()));
                               // LongitudeText.setText("Longitude : " + df.format(location.getLongitude()));
                                com.luckycatlabs.sunrisesunset.dto.Location locationObj = new com.luckycatlabs.sunrisesunset.dto.Location("" + location.getLatitude(), "" + location.getLongitude());
                                Calendar temp = Calendar.getInstance();
// Pass the time zone display here in the second parameter.
                                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(locationObj, TimeZone.getDefault());
                                System.out.println(TimeZone.getDefault().toString());
                                String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
                                String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
                                TextView sun = findViewById(R.id.SunriseID);
                                sun.setText("Sunrise: " + officialSunrise);
                                sun = findViewById(R.id.SunsetID);
                                sun.setText("Sunset:" + officialSunset);
                            }
                        }
                    });

        }
        else
        {
            Toast.makeText(this,"We need location permission to calculate time of sunset and sunrise",Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    3);
        }


            //Calendar calendar = Calendar.getInstance();
            //TimeZone timezoneObj =  calendar.getTimeZone();
           // timezoneObj.getID();



    }

    public Bitmap rotate(Bitmap source, boolean sunRise)
    {

            //rotate bitmapSS
            matrix.postRotate(90);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }
    public void setImage(Uri uri, boolean sunRise)
    {
        if(sunRise) {
            try {
                bitmapSR = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView SRImage =  findViewById(R.id.SRImageID);
                SRImage.setImageBitmap(bitmapSR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try{
                bitmapSS = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ImageView SSImage = findViewById(R.id.SSImageID);
                SSImage.setImageBitmap(bitmapSS);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void setImage(Bitmap source, boolean sunRise)
    {
        if(sunRise) {
            ImageView SRImage =  findViewById(R.id.SRImageID);
            SRImage.setImageBitmap(source);
        }
        else
        {
            ImageView SSImage =  findViewById(R.id.SSImageID);
            SSImage.setImageBitmap(source);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Uri uri = data.getData();
        int temp = requestCode;
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        final int takeFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Check for the freshest data.
        ContentResolver resolver = getContentResolver();
        resolver.takePersistableUriPermission(uri, takeFlags);
        if (requestCode == PICK_SUNRISE) {
            srPath = uri.toString();
            editor.putString("SRPath",srPath);
            editor.commit();
            setImage(uri,true);



        }
        if (requestCode == PICK_SUNSET) {
            ssPath = uri.toString();
            editor.putString("SSPath",ssPath);
            editor.commit();
            setImage(uri,false);
        }
    }
}

