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
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
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
    private boolean estActif;
    public MyLocationNewOverlay mLocationOverlay;
    public IMapController mapController;
    public CompassOverlay mCompassOverlay;
    public RotationGestureOverlay mRotationGestureOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_carte);
        demandePerm();
        map = (org.osmdroid.views.MapView) findViewById(R.id.mv_map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(14.5);
        GeoPoint startPoint = new GeoPoint(42.66620, 0.10373);
        mapController.setCenter(startPoint);
        Marker refuge = new Marker(map);
        refuge.setPosition(startPoint);
        refuge.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        refuge.setTitle("Refuge");
        map.getOverlays().add(refuge);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            createGpsDisabledAlert();
        }
        else{estActif = true;}

        if(estActif){
           seGeolocaliser();
        }

        compas();
        glisser();
        Log.d("toto","wtf j'ai un looooooooooooooooooooooooooog");


        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);


        //lire un chemin

        //afficher trait
         Polyline polyline;
         ArrayList<GeoPoint> pathPoints = new ArrayList<GeoPoint>();
        polyline = new Polyline();
        polyline.setWidth(2);
        map.getOverlays().add(polyline);
        pathPoints.add(startPoint);

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

    }

    public void onResume(){
        super.onResume();
        map.onResume();

    }

    public void onPause(){
        super.onPause();
        map.onPause();

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
                                seGeolocaliser();
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

    public void seGeolocaliser(){
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
    }

    public void compas(){
        mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);
    }

    public void glisser(){
        mRotationGestureOverlay = new RotationGestureOverlay(this, map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
    }

}
