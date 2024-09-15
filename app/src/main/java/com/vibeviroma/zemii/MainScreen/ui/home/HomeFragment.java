package com.vibeviroma.zemii.MainScreen.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.MainScreen.ui.BottomSearchSheet;
import com.vibeviroma.zemii.R;

import java.util.Objects;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        BottomSearchSheet.BottomSheetListener
{

    //Google ApiClient
    //private GoogleApiClient googleApiClient;
    private FloatingActionButton fab_search;
    private GoogleMap mMap;
    private double latitude, longitude;
    private MapRipple mapRipple;
    private Context context;
    private Activity activity;
    private FusedLocationProviderClient client;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context= getContext();
        activity=getActivity();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        fab_search=(FloatingActionButton)root.findViewById(R.id.fab_search);
        mapFragment.getMapAsync(this);
        client= LocationServices.getFusedLocationProviderClient(context);
        /*googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();*/

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_clicked();
            }
        });
        return root;
    }

    private void search_clicked() {
        BottomSearchSheet bottomSearchSheet= new BottomSearchSheet(getView(), getActivity());
        bottomSearchSheet.show(requireFragmentManager(), "");
        bottomSearchSheet.setmListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        latitude=7; longitude=7.5;
        getCurrentLocation();

    }

    @Override
    public void onSearchLaunched(String start_name, String start_coordinate, String end_name, String end_coordinate) {

    }

    @Override
    public void onStart() {
        super.onStart();
        //googleApiClient.connect();
    }

    private void getCurrentLocation(){
        if((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 255);
            return;
        }

        if(mMap!=null){
            mMap.clear();
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                        moveMap();
                    }
                }
            });
        }
    }

    private void moveMap(){
        LatLng latLng= new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Votre position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))) ;
        mapRipple= new MapRipple(mMap, latLng, context);
        mapRipple.withNumberOfRipples(2);
        mapRipple.withDistance(500);
        mapRipple.withRippleDuration(3500);
        mapRipple.withTransparency(0.5f);

        mapRipple.startRippleMapAnimation();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();
        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));

        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==255){
            if((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                    && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)){
                    getCurrentLocation();
            }
        }
    }
}
