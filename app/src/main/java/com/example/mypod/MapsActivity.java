package com.example.mypod;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    int numofrecords;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    TextView recordTV;
    Boolean isRecording;
    MediaRecorder recorder;
    File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recordTV = findViewById(R.id.recordTV);
        isRecording = false;
        numofrecords = 1;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        recordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!isRecording){
                        isRecording = true;
                    recordAudio();
                    }
                    else {
                        recordTV.setBackground(null);
                        isRecording = false;
                        recorder.stop();
                        Log.d("loading", "path is = " + path);
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(path.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        numofrecords++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},0);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                           Double latitude = location.getLatitude();
                           Double longitude = location.getLongitude();
                            LatLng newyork = new LatLng(latitude,longitude);
                            mMap.addMarker(new MarkerOptions().position(newyork).title("Marker in New York"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newyork));
                            // Logic to handle location object
                        }
                    }
                });
    }

    private void recordAudio() throws IOException {
        recordTV.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background));
        path = null;
        recorder = new MediaRecorder();
        String status = Environment.getExternalStorageState();
        if(status.equals("mounted")){
            path = new File(Environment.getExternalStorageDirectory(),
                    "audio_test4.3gp");
        }
        Log.d("saving path","path is = " + path);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path.getAbsolutePath());
        recorder.prepare();
        recorder.start();

    }

    private String addTime() {
        Calendar rightNow = Calendar.getInstance();

        long offset = rightNow.get(Calendar.ZONE_OFFSET) +  rightNow.get(Calendar.DST_OFFSET);

        String sinceMidnight = Long.toString((rightNow.getTimeInMillis() + offset) %  (24 * 60 * 60 * 1000));

        return sinceMidnight;
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
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(15);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
