package fr.xyz.valpinetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class Carte extends AppCompatActivity implements OnMapReadyCallback  {

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

        // Construction de FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Vérif localisation activer

        
    }


}

