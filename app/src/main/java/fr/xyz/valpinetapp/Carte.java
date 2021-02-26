package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Locale;

public class Carte extends AppCompatActivity {

    private MapView carte;
    IMapController mapController;
    GeoPoint refuge;
    GeoPoint position;
    boolean positionActive=true;

    ArrayList<OverlayItem> items = new ArrayList<>();

    LocationManager locationManager = null;

    private String fournisseur;
    private double latitude;
    private double longitude;
    private String lat;
    private String longi;
    private String autres;


    Geocoder geocoder = new Geocoder(this, Locale.getDefault());


    LocationListener ecouteurGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location localisation) {

            autres = String.format("Vitesse : %f - Altitude : %f - Cap : %f\n", localisation.getSpeed(), localisation.getAltitude(), localisation.getBearing());
            latitude = localisation.getLatitude();
            longitude = localisation.getLongitude();
            position = new GeoPoint(latitude, longitude);


            OverlayItem positionU = new OverlayItem("Votre position", autres, position);
            Drawable marqueurP = positionU.getMarker(0);
            items.add(positionU);

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(),
                    items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    return true;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            });

            mOverlay.setFocusItemsOnTap(true);
            carte.getOverlays().add(mOverlay);
        }

        @Override
        public void onProviderDisabled(String fournisseur) {
            Toast.makeText(getApplicationContext(), fournisseur + " désactivé !", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String fournisseur) {
            Toast.makeText(getApplicationContext(), fournisseur + " activé !", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String fournisseur, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Toast.makeText(getApplicationContext(), fournisseur + " état disponible", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(getApplicationContext(), fournisseur + " état indisponible", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(getApplicationContext(), fournisseur + " état temporairement indisponible", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), fournisseur + " état : " + status, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_carte);
        carte = findViewById(R.id.osm_carte);
        carte.setTileSource(TileSourceFactory.MAPNIK); //Type de carte
        carte.setBuiltInZoomControls(true); //Zoom
        createGpsDisabledAlert();
        refuge = new GeoPoint(42.66615, 0.10372);
        mapController = carte.getController();
        mapController.setZoom(10.0);
        mapController.setCenter(refuge);

        OverlayItem refugePineta = new OverlayItem("Refuge Pineta", "Point de départ", refuge);
        Drawable marqueur = refugePineta.getMarker(0);
        items.add(refugePineta);

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(),
                items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });

        mOverlay.setFocusItemsOnTap(true);
        carte.getOverlays().add(mOverlay);
        initialiserLocalisation();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        arreterLocalisation();
    }

        @Override
        public void onPause () {
            super.onPause();
            carte.onPause();
        }

        @Override
        public void onResume () {
            super.onResume();
            carte.onResume();
        }


        public void onClik (View v){
            initialiserLocalisation();
        }


        private void showGpsOptions () {
            startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), -1);
        }


        private void createGpsDisabledAlert () {
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
                                        positionActive=true;
                                        Carte.this.showGpsOptions();
                                    }
                                });
                localBuilder.setNegativeButton("Ne pas l'activer ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                positionActive=false;
                                paramDialogInterface.cancel();
                            }
                        });
                localBuilder.create().show();
            }
        }

    private void initialiserLocalisation()
    {
        if(positionActive==true) {
            if (locationManager == null) {
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteres = new Criteria();

                // la précision  : (ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision)
                criteres.setAccuracy(Criteria.ACCURACY_FINE);

                // l'altitude
                criteres.setAltitudeRequired(true);

                // la direction
                criteres.setBearingRequired(true);

                // la vitesse
                criteres.setSpeedRequired(true);

                // la consommation d'énergie demandée
                criteres.setCostAllowed(true);
                //criteres.setPowerRequirement(Criteria.POWER_HIGH);
                criteres.setPowerRequirement(Criteria.POWER_MEDIUM);

                fournisseur = locationManager.getBestProvider(criteres, true);
                Log.d("GPS", "fournisseur : " + fournisseur);
            }

            if (fournisseur != null) {
                // dernière position connue
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    createGpsDisabledAlert();
                }

                Location localisation = locationManager.getLastKnownLocation(fournisseur);
                if (localisation != null) {
                    // on notifie la localisation
                    ecouteurGPS.onLocationChanged(localisation);

                }

                // on configure la mise à jour automatique : au moins 10 mètres et 15 secondes
                locationManager.requestLocationUpdates(fournisseur, 15000, 10, ecouteurGPS);
            }
        }
        else{
            createGpsDisabledAlert();
        }
    }

    private void arreterLocalisation()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(ecouteurGPS);
            ecouteurGPS = null;
        }
    }

    private void gestionCache(){
        CacheManager cacheM = new CacheManager();
        CacheManager.CacheManagerTask cacheManagerTask = new CacheManager.CacheManagerTask();

        
    }

    }


