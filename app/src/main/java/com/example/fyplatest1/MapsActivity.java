package com.example.fyplatest1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.fyplatest1.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private FirebaseUser user;
    private DatabaseReference reference;

    String userID;

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    FloatingActionButton startStopBtn;
    GridLayout gridLayoutRecord;
    Running running = new Running();
    TextView index_time;

    TimerTask timerTask;
    Double time = 0.0;
    Timer timer;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    //private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int REQUEST_LOCATION= 1;
    private LocationManager locationManager;

    private boolean mLocationPermissionGranted = false;

    boolean isPressed =false;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Running");
        userID = user.getUid();

        setContentView (R.layout.activity_maps);

        timer = new Timer();
        index_time = (TextView) findViewById(R.id.index_time);
        startStopBtn = (FloatingActionButton)findViewById(R.id.startrecord);
        gridLayoutRecord = (GridLayout)findViewById(R.id.infor);
        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById (R.id.map);
            //initialize fused Location
        client = LocationServices.getFusedLocationProviderClient (this);
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        loadingDialog = new LoadingDialog(MapsActivity.this);
        loadingDialog.startLoadingDialog();
        getLocation();

        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create method
                long startTime = System.currentTimeMillis();
                gridLayoutRecord.setVisibility(View.VISIBLE);
                startStopBtn.setImageResource(R.drawable.stop);
                getLocation1(startTime);
                startTimer();
            }
        });
        //Check permission
        /*if (ActivityCompat.checkSelfPermission (MapsActivity.this,
                Manifest.permission. ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //get current location when permission is granted
            getCurrentLocation();
            startStopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    } else {
                        gridLayoutRecord.setVisibility(View.VISIBLE);
                        startStopBtn.setImageResource(R.drawable.stop);
                        getLocation();
                    }
                }
            });

        } else {
            //request permission when permission is denied
            ActivityCompat.requestPermissions (MapsActivity.this,
                    new String []{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }*/

        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.me);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.record:
                        return true;
                    case R.id.workout:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.me:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        intent = new Intent(getApplicationContext(), MeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MapsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            getCurrentLocation();

            //startStopBtn.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    private void getLocation1(long startTime1){
        //Long.toString(startTime/100000)
        if(ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }else{
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS  != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();

                //TextView latitude = (TextView)findViewById(R.id.latitude);
                //TextView longitude = (TextView)findViewById(R.id.longitude);
                //TextView address = (TextView)findViewById(R.id.address);

                running.setLatitude(String.valueOf(lat));
                running.setLongitude(String.valueOf(longi));

                String unique = reference.push().getKey();

                reference.child(userID).child(unique).setValue(running)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MapsActivity.this,
                                    "Running started", Toast.LENGTH_LONG).show();
                            startStopBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    long modulus, timesecond;
                                    String time;
                                    double time1, pace;
                                    long difference = System.currentTimeMillis() - startTime1;
                                    timesecond = difference/1000;
                                    modulus = timesecond%60;
                                    String padded = String.format("%02d" , modulus);
                                    time = timesecond/60 + ":" + padded;

                                    gridLayoutRecord.setVisibility(View.GONE);
                                    startStopBtn.setImageResource(R.drawable.record_icon);
                                    if(ActivityCompat.checkSelfPermission(
                                            MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                            MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                                    }else{
                                        Location locationGPS = locationManager
                                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if (locationGPS  != null) {
                                            double lat1 = locationGPS.getLatitude();
                                            double longi1 = locationGPS.getLongitude();
                                            Double distance, modulus1;
                                            String totalpace;

                                            distance = getDistanceFromLatLonInKm(lat,longi,lat1,longi1);
                                            if (distance > 0.01){
                                                long j = Long.valueOf(distance.longValue());
                                                double timesecondconvert = (double) timesecond;
                                                double modulusconvert = (double) modulus;
                                                //distance tak boleh zero
                                                pace = timesecond / distance;
                                                modulusconvert = pace % 60;
                                                long paded = (long) modulusconvert;
                                                long pacec = (long) pace;
                                                String padded1 = String.format("%02d", paded);
                                                totalpace = pacec / 60 + ":" + padded1;

                                                running.setTime(time);
                                                running.setPace(totalpace);
                                                running.setDistance(distance);
                                                running.setLatitude1(String.valueOf(lat1));
                                                running.setLongitude1(String.valueOf(longi1));

                                                //String distance1 = Double.toString(distance);
                                                String distance2 = String.format("%.2f", distance);

                                                reference.child(userID).child(unique).setValue(running)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(MapsActivity.this,
                                                                    "Running finished", Toast.LENGTH_LONG).show();
                                                            //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                            Intent intent = new Intent(MapsActivity.this, AddRunningAuto.class);
                                                            intent.putExtra("time", time);
                                                            intent.putExtra("pace", totalpace);
                                                            intent.putExtra("distance", distance2);
                                                            startActivity(intent);

                                                        } else {
                                                            Toast.makeText(MapsActivity.this,
                                                                    "Running not inserted", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(MapsActivity.this,
                                                        "Distance cannot be zero!", Toast.LENGTH_LONG).show();
                                            }
                                        }else {
                                            Toast.makeText(MapsActivity.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(MapsActivity.this,
                                    "Running not inserted", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(MapsActivity.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        //Initialize task location
        /*Double currentLatitude = 27.658143;
        Double currentLongitude = 85.3199503;
        Double fixedLatitude = 27.667491;
        Double fixedLongitude = 85.3208583;*/

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //When success
                if (location != null) {
                    //sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            loadingDialog.dismissDialog();
                            //Initialize Lat Lng
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            //Create marker options
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title("I am here");
                            //zoom map scale 15
                            googleMap.clear();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                            googleMap.addMarker(options);
                            //float zoomLevel = 19.0f; //This goes up to 21
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                            /*String uri = "http://maps.google.com/maps?saddr=" + currentLatitude+","+currentLongitude+"&daddr="+fixedLatitude+","+fixedLongitude;
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);*/
                        }
                    });
                }
            }
        });
    }
    /*@Override
    public void onRequestPermissionsResult (
            int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                //Get current location when permission granted
                getLocation();
            }
        }
    }*/
    //Haversine formula
    public static double getDistanceFromLatLonInKm(double lat1, double lon1,
                                                   double lat2, double lon2) {

        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1); // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km

        return d;
    }

    public static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        index_time.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

    @Override
    public void onBackPressed() {

        //startActivity(new Intent(this, HomeActivity.class));
        //finish();
        if (isPressed){
            finishAffinity();
            System.exit(0);
        }else{
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            isPressed = true;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isPressed = false;
            }
        };

        new Handler().postDelayed(runnable, 2000);
    }
}