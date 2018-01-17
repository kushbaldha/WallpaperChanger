package com.example.kush.wallpaperchanger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int PICK_SUNRISE = 1;
    public static final int PICK_SUNSET =  2;


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


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button refreshButt = findViewById(R.id.refreshButt);
        refreshButt.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                findLocation();
            }
        });

        Button chooseSunrise = findViewById(R.id.ChooseSRID);
        chooseSunrise.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_SUNRISE);
            }
        });
        Button chooseSunset = findViewById(R.id.ChooseSSID);
        chooseSunset.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_SUNSET);
            }
        });




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
                                TextView LatitudeText = (TextView) findViewById(R.id.LatitudeID);
                                TextView LongitudeText = (TextView) findViewById(R.id.LongitudeID);
                                DecimalFormat df = new DecimalFormat("#.####");
                                df.setRoundingMode(RoundingMode.DOWN);
                                LatitudeText.setText("Latitude : " + df.format(location.getLatitude()));
                                LongitudeText.setText("Longitude : " + df.format(location.getLongitude()));
                                com.luckycatlabs.sunrisesunset.dto.Location locationObj = new com.luckycatlabs.sunrisesunset.dto.Location("" + location.getLatitude(), "" + location.getLongitude());
                                Calendar temp = Calendar.getInstance();
// Pass the time zone display here in the second parameter.
                                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(locationObj, TimeZone.getDefault());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_SUNRISE) {
            //TODO: action
            Uri uri = data.getData();
            String temp = uri.getPath();
            int temp2 = 0;
            System.out.println(temp);
        }
        if (requestCode == PICK_SUNSET) {
            Uri uri = data.getData();
            int temp = 0;
        }
    }
}

