package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Carte extends AppCompatActivity implements OnMapReadyCallback {

    boolean mLocationPermissionGranted;
    GoogleMap mMap; //Objet GoogleMap pour manipuler la carte (marqueurs etc)
    private Location mLastKnownLocation; //Dernière position connue
    private FusedLocationProviderClient mFusedLocationProviderClient; //Fournisseur de Loc
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085); //Location par défaut
    private static final int DEFAULT_ZOOM = 15; //Zoom carte par défaut
    private static final String TAG = Carte.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_carte);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;}


    public void onClik(View v) {
        createGpsDisabledAlert();
    }


    private void showGpsOptions() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), -1);
    }


    private void createGpsDisabledAlert() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //on recupere l etat du gps pour savoir si il est actif ou non
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            Button fra = findViewById(R.id.button2);
            localBuilder
                    .setMessage("Le GPS est inactif, voulez-vous l'activer ?")
                    .setCancelable(false)
                    .setPositiveButton("Activer GPS ",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    mLocationPermissionGranted = true;
                                    Carte.this.showGpsOptions();
                                }
                            });
            localBuilder.setNegativeButton("Ne pas l'activer ",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            paramDialogInterface.cancel();
                        }
                    });
            localBuilder.create().show();
        }
    }

}