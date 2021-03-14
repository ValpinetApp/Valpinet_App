package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class Carte extends AppCompatActivity {
    private org.osmdroid.views.MapView map = null;
    private LocationManager manager;
    private boolean estActif;


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
        map = (org.osmdroid.views.MapView) findViewById(R.id.mv_map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(14.5);
        GeoPoint startPoint = new GeoPoint(42.66620, 0.10373);
        GeoPoint endPoint = new GeoPoint(42.67827,0.07461);
        GeoPoint popo = new GeoPoint(47.67827,0.07461);
        mapController.setCenter(startPoint);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            createGpsDisabledAlert();
        }
        else{estActif = true;}

        if(estActif==true){
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


        //lire un chemin

        //afficher trait
         Polyline polyline;
         ArrayList<GeoPoint> pathPoints = new ArrayList<GeoPoint>();
        polyline = new Polyline();
        polyline.setWidth(2);
        map.getOverlays().add(polyline);
        pathPoints.add(startPoint);
        pathPoints.add(endPoint);
        pathPoints.add(popo);

        try {
            WayPoint wp;
            Stream <WayPoint> gpx;
            gpx = GPX.read(getAssets().open("Balcon2.gpx")).tracks().flatMap(Track::segments).flatMap(TrackSegment::points);
            Iterator <WayPoint> it = gpx.iterator();
            Double lat;
            Double longi;

            while (it.hasNext()) {
                wp=it.next();
                lat = wp.getLatitude().doubleValue();
                longi = wp.getLongitude().doubleValue();
                GeoPoint p = new GeoPoint(lat,longi);
                pathPoints.add(p);
            }
        }
        catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        polyline.setPoints(pathPoints);
        map.invalidate();

        ////
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



}
