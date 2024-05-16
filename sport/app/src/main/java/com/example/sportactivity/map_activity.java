package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import classes.CounterService;
import classes.Data;
import classes.Guest;
import classes.Point;
import classes.Session;
import classes.User;
import helpers.FirebaseHelper;

public class map_activity extends FragmentActivity implements OnMapReadyCallback {

    //google map stuff
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final double EARTH_RADIUS_KM = 6378.1370;

    private final int MIN_TIME = 1000;//sec
    private final int MIN_DISTANCE = 3; //meter

    private LatLng latLng;

    private ArrayList<Point> locations;

    //xml stuff
    public static Button[] btn = new Button[2];
    public static TextView[] txt = new TextView[4];
    Boolean flag = false;
    String user_name;
    String mode;

    double distance = 0;
    int steps = 0;

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        Intent intent = getIntent();
        user_name = intent.getStringExtra("userName");
        mode = intent.getStringExtra("mode");

        //request location permission.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        btn[0] = findViewById(R.id.btn);
        btn[1] = findViewById(R.id.btn2);

        txt[0] = findViewById(R.id.time);
        txt[1] = findViewById(R.id.distance);
        txt[2] = findViewById(R.id.calories);
        txt[3] = findViewById(R.id.steps);

        for (int i = 0; i < 2; i++) {
            btn[i].setOnClickListener(this::onClick);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locations = new ArrayList<>();

        getCurrentLocation();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    void onClick(View view) {
        if (view == btn[0]) {
            if (!flag) {
                btn[0].setText("resume");
                btn[1].setVisibility(View.VISIBLE);
                flag = true;

                Intent intent = new Intent(map_activity.this, CounterService.class);
                stopService(intent); // Stop the service

                locationManager.removeUpdates(locationListener);//stop location update
            } else {
                btn[0].setText("stop");
                btn[1].setVisibility(View.GONE);
                flag = false;

                locations.clear();
                getCurrentLocation();

                try{locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);}
                catch (SecurityException e){Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();}//resume

                Intent intent = new Intent(map_activity.this, CounterService.class);
                intent.setAction(CounterService.ACTION_RESUME);
                String time = txt[0].getText().toString();
                String hr = "" + time.charAt(0) +  time.charAt(1);
                String min = "" + time.charAt(3) +  time.charAt(4);
                String sec = "" + time.charAt(6) +  time.charAt(7);
                int seconds = (Integer.parseInt(hr)*3600) + (Integer.parseInt(min)*60) + (Integer.parseInt(sec));
                intent.putExtra("TIME", seconds);
                startService(intent); // Resume the service
            }
        } else if (view == btn[1]) {
            Intent intent = new Intent(map_activity.this, CounterService.class);
            stopService(intent); // stop the service

            String time = txt[0].getText().toString();
            String hr = "" + time.charAt(0) +  time.charAt(1);
            String min = "" + time.charAt(3) +  time.charAt(4);
            String sec = "" + time.charAt(6) +  time.charAt(7);
            int seconds = (Integer.parseInt(hr)*3600) + (Integer.parseInt(min)*60) + (Integer.parseInt(sec));

            // create a SimpleDateFormat object with the desired format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // create a Date object representing the current date
            Date currentDate = new Date();
            // format the date using the SimpleDateFormat object
            String formattedDate = dateFormat.format(currentDate);

            Session session = new Session(locations.get(locations.size()-1).getLatitude(),
                                        locations.get(locations.size()-1).getLongitude(), seconds, distance, formattedDate);

            firebaseHelper.getUser(user_name, new FirebaseHelper.ReturnCall<Data>() {
                @Override
                public void onSuccess(Data result) {
                    if (result instanceof User) {
                        ((User) result).getSes().add(session);
                        ((User) result).setXp(((User) result).getXp() + steps);
                        firebaseHelper.addUser((User) result);
                    }
                    else if(result instanceof Guest){
                        ((Guest) result).getSes().add(session);
                        ((Guest) result).setXp(((Guest) result).getXp() + steps);
                        firebaseHelper.addGuest((Guest) result);
                    }
                }

                @Override
                public void onError() {

                }
            });

            Intent intent1 = new Intent(map_activity.this, Main.class);
            intent1.putExtra("userName", user_name);
            finish();
            startActivity(intent1);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                locations.add(new Point(location.getLatitude(),location.getLongitude()));

                //PolylineOptions polylineOptions = new PolylineOptions();
                //for (int i = 0; i < locations.size(); i++) {
                //    polylineOptions.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
                //}

                //draw line
                //mMap.addPolyline(polylineOptions);

                //add arrows
                for (int i = 0; i < locations.size() - 1; i++) {
                    LatLng start = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                    LatLng end = new LatLng(locations.get(i+1).getLatitude(), locations.get(i+1).getLongitude());

                    Location location1 = new Location("");
                    location1.setLatitude(start.latitude);
                    location1.setLongitude(start.longitude);

                    Location location2 = new Location("");
                    location2.setLatitude(end.latitude);
                    location2.setLongitude(end.longitude);

                    float bearing = location1.bearingTo(location2); // Calculate bearing between two points
                    float speed = location.getSpeed();//in meter for second

                    if (mode.equals("running")) {
                        if (i+1 == locations.size() - 1) {
                            if (speed<=8 && speed >=2.2) {
                                calculation(location1,location2);
                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add(start, end)
                                        .width(10)
                                        .color(Color.GREEN)
                                        .geodesic(true));
                                MarkerOptions arrowMarker = new MarkerOptions()
                                        .position(end)
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                rotateBitmap(map_activity.this,R.drawable.green_arrow, bearing))) // Rotate arrow icon by bearing angle
                                        .anchor(0.5f, 0.5f); // Set anchor to center of marker
                                mMap.addMarker(arrowMarker); // Add arrow marker to map
                            }
                            else{
                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add(start, end)
                                        .width(10)
                                        .color(Color.RED)
                                        .geodesic(true));
                                MarkerOptions arrowMarker = new MarkerOptions()
                                        .position(end)
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                rotateBitmap(map_activity.this,R.drawable.red_arrow, bearing))) // Rotate arrow icon by bearing angle
                                        .anchor(0.5f, 0.5f); // Set anchor to center of marker
                                mMap.addMarker(arrowMarker); // Add arrow marker to map
                            }

                        }
                    }
                    else if (mode.equals("riding")) {
                        if (i+1 == locations.size() - 1) {
                            if (speed<=15 && speed >=3.7) {
                                calculation(location1,location2);
                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add(start, end)
                                        .width(10)
                                        .color(Color.GREEN)
                                        .geodesic(true));
                                MarkerOptions arrowMarker = new MarkerOptions()
                                        .position(end)
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                rotateBitmap(map_activity.this,R.drawable.green_arrow, bearing))) // Rotate arrow icon by bearing angle
                                        .anchor(0.5f, 0.5f); // Set anchor to center of marker
                                mMap.addMarker(arrowMarker); // Add arrow marker to map
                            }
                            else{
                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add(start, end)
                                        .width(10)
                                        .color(Color.RED)
                                        .geodesic(true));
                                MarkerOptions arrowMarker = new MarkerOptions()
                                        .position(end)
                                        .icon(BitmapDescriptorFactory.fromBitmap(
                                                rotateBitmap(map_activity.this,R.drawable.red_arrow, bearing))) // Rotate arrow icon by bearing angle
                                        .anchor(0.5f, 0.5f); // Set anchor to center of marker
                                mMap.addMarker(arrowMarker); // Add arrow marker to map
                            }
                        }
                    }
                }
                //mMap.addMarker(new MarkerOptions().position(latLng).title("my location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onStatusChanged(String s,int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        try{locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);}
        catch (SecurityException e){Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();}
    }

    public static Bitmap rotateBitmap(Context context, int resourceId, float angle) {
        Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        Bitmap smallArrowBitmap = Bitmap.createScaledBitmap(arrowBitmap, 40, 40, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(smallArrowBitmap, 0, 0, smallArrowBitmap.getWidth(), smallArrowBitmap.getHeight(), matrix, true);
    }

    public void getCurrentLocation(){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());
                        locations.add(new Point(location.getLatitude(),location.getLongitude()));
                        mMap.setMinZoomPreference(17);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("start pos"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    }
                }
            });
        }
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }


    public static double haversineFormulaForDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = haversin(dLat) + Math.cos(lat1) * Math.cos(lat2) * haversin(dLon);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public void calculation(Location location1,Location location2){
        distance += haversineFormulaForDistance(location1.getLatitude(),location1.getLongitude(),
                location2.getLatitude(),location2.getLongitude()); // in kilometers
        DecimalFormat df = new DecimalFormat("#.###");
        String formattedNumber = df.format(distance);
        txt[1].setText(formattedNumber);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUser(user_name, new FirebaseHelper.ReturnCall<Data>() {
            @Override
            public void onSuccess(Data result) {
                if (result instanceof User) {
                    double stepLen = result.getStep_size();
                    steps = (int) ((distance*1000)/(stepLen/100));
                    txt[3].setText(""+steps);

                    //Calories Burned = (0.75 x body weight in kilograms x distance in kilometers)
                    double calories = 0.75 * result.getWeight() * distance;
                    DecimalFormat cf = new DecimalFormat("#.#");
                    String formattedCalories = cf.format(calories);
                    txt[2].setText(formattedCalories);
                }
                else{
                    double stepLen = result.getStep_size();
                    steps = (int) ((distance*1000)/(stepLen/100));
                    txt[3].setText(""+steps);

                    //Calories Burned = (0.75 x body weight in kilograms x distance in kilometers)
                    double calories = 0.75 * result.getWeight() * distance;
                    DecimalFormat cf = new DecimalFormat("#.#");
                    String formattedCalories = cf.format(calories);
                    txt[2].setText(formattedCalories);
                }
            }
            @Override
            public void onError() {
                Toast.makeText(map_activity.this, "error", Toast.LENGTH_LONG).show();
            }
        });
    }
}