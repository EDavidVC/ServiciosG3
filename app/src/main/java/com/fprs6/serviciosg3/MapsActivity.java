package com.fprs6.serviciosg3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.fprs6.serviciosg3.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocClient;

    private final int REQUEST_LOCATION = 1;
    private String locationPermissions[];

    private FloatingActionButton fapReplyLocationButton;
    private FloatingActionButton fabAcceptSendLocationButton;

    private String chatMyUid;
    private String chatContactUid;
    private boolean onlyView;
    private double latitude = 0;
    private double longitude = 0;
    private String userName = "Ubicacion";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        fabAcceptSendLocationButton = findViewById(R.id.fab_sendLocationButton);
        fapReplyLocationButton = findViewById(R.id.fab_replyLocationButton);

        chatContactUid = getIntent().getStringExtra("chatContactUid");
        chatMyUid = getIntent().getStringExtra("chatMyUid");
        onlyView = getIntent().getBooleanExtra("onlyView", false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fapReplyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabAcceptSendLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(latitude == 0 && longitude == 0)){
                    sendMessage();
                }else {
                    Toast.makeText(MapsActivity.this, "Activar Ubicacion Por favor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!onlyView){
            getCurrentLocation();
        }else{
            fabAcceptSendLocationButton.setVisibility(View.GONE);
            latitude = getIntent().getDoubleExtra("latitude",0);
            longitude = getIntent().getDoubleExtra("longitude", 0);
            userName = getIntent().getStringExtra("userName");

            LatLng userLocation = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions().position(userLocation).title(userName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
    }

    private void requestLocPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions, REQUEST_LOCATION);
    }
    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void getCurrentLocation(){
        if(!checkLocationPermission()){
            requestLocPermission();
        }else {
            fusedLocClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            fusedLocClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NotNull Task<Location> task) {
                    if(task.isSuccessful()){
                        if(task.getResult() != null){
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            LatLng myLocation = new LatLng(latitude,longitude);
                            mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi Ubicacion"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NotNull Exception e) {
                    System.out.println("ERROR GOOGLE MAPS : " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }else{
            Toast.makeText(this, "Servicios de Ubicacion Desabilitados", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void sendMessage(){
        String timeSending = String.valueOf(System.currentTimeMillis());

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> messageStructure = new HashMap<>();
        messageStructure.put("sender", chatMyUid);
        messageStructure.put("receiver", chatContactUid);
        messageStructure.put("latitude", latitude);
        messageStructure.put("longitude", longitude);
        messageStructure.put("messageStatus", false);
        messageStructure.put("timeSending", timeSending);
        messageStructure.put("type", "location");
        dbReference.child("chats").push().setValue(messageStructure);
        finish();
    }
}