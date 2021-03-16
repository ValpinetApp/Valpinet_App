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
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.ArrayList;

public class Carte extends AppCompatActivity {
    private org.osmdroid.views.MapView map = null;
    private boolean estActif;
    MyLocationNewOverlay mLocationOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string
        //inflate and create the map
        setContentView(R.layout.activity_carte);
        demandePerm();
        map = (org.osmdroid.views.MapView) findViewById(R.id.mv_map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(14.5);
        GeoPoint startPoint = new GeoPoint(42.66620, 0.10373);
        GeoPoint endPoint = new GeoPoint(42.67827,0.07461);
        mapController.setCenter(startPoint);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            createGpsDisabledAlert();
        }
        else{estActif = true;}

        if(estActif){
            //creation de l'overlay personne
            //activation de la recherche
            mLocationOverlay.enableMyLocation();
            //implementation sur la carte
            map.getOverlays().add(mLocationOverlay);
        }


        //creation de l'overlay boussole
        CompassOverlay mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        //activation compass
       mCompassOverlay.enableCompass();
        //implementation sur la carte
        map.getOverlays().add(mCompassOverlay);

        //rotation
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(this, map);

        //ajout double touch
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        //implementation sur la carte
        map.getOverlays().add(mRotationGestureOverlay);


        //implementation echelle
        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        //implementation sur la carte
        map.getOverlays().add(mScaleBarOverlay);

        //[MARCHE PAS] centrer sur le joggeur
        mapController.setCenter(mLocationOverlay.getMyLocation());

    }
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up

    }

    private void createGpsDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder
                .setMessage("Le GPS est inactif, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                estActif = true;
                                demandePerm();
                                //creation de l'overlay personne
                                //activation de la recherche
                                mLocationOverlay.enableMyLocation();
                                //implementation sur la carte
                                map.getOverlays().add(mLocationOverlay);
                                Carte.this.showGpsOptions();
                            }
                        }
                );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        estActif=false;
                        paramDialogInterface.cancel();
                    }
                }
        );
        localBuilder.create().show();
    }

    private void showGpsOptions() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"),-1);
    }

    public void demandePerm(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},2
            );
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
}

}
