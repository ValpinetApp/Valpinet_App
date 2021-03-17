package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
<<<<<<< Updated upstream
import android.Manifest;
=======
>>>>>>> Stashed changes
import android.content.Context;
import android.content.Intent;
<<<<<<< Updated upstream
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
=======
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
>>>>>>> Stashed changes

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
<<<<<<< Updated upstream
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import io.jenetics.jpx.GPX;
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
    public GeoPoint startPoint;

=======
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;


import java.util.ArrayList;

public class Carte extends AppCompatActivity {

    private MapView carte;
    IMapController mapController;
    GeoPoint refuge;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_carte);
<<<<<<< Updated upstream
        demandePerm();
        Log.d("demandePerm","Demande perm passée");
        map = findViewById(R.id.mv_map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(14.5);
        startPoint = new GeoPoint(42.66620, 0.10373);
        mapController.setCenter(startPoint);
        Marker refuge = new Marker(map);
        refuge.setPosition(startPoint);
        refuge.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        refuge.setTitle("Refuge");
        map.getOverlays().add(refuge);
        Log.d("Refuge","Refuge positionné");

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

        Log.d("Localisation","Localisation activée");

        compas();
        Log.d("Create","Compas positionné");
        glisser();
        Log.d("Create","Gestion zoom à deux doigts établis");
        echelle();
        Log.d("Create","Echelle positionnée");
        tracerGPX();
        Log.d("Create","GPX en place");

    }

    public void onResume(){
        super.onResume();
        map.onResume();
        Log.d("Resume","De retour");

    }

    public void onPause(){
        super.onPause();
        map.onPause();
        Log.d("Resume","En pause");
    }

    private void createGpsDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder
                .setMessage("Le GPS est inactif, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS ",
                        (paramDialogInterface, paramInt) -> {
                            estActif = true;
                            demandePerm();
                            seGeolocaliser();
                            Carte.this.showGpsOptions();
                        }
                );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                (paramDialogInterface, paramInt) -> {
                    estActif=false;
                    paramDialogInterface.cancel();
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

    public void echelle(){
        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);
    }

    public void tracerGPX(){
        //afficher trait
        Polyline polyline;
        ArrayList<GeoPoint> pathPoints = new ArrayList<>();
        polyline = new Polyline();
        polyline.setWidth(10);
        map.getOverlays().add(polyline);


        try {
            Log.d("tryGPX","GPX loadé");
            WayPoint wp;
            Stream <WayPoint> gpx;
            gpx = GPX.read(getAssets().open("0205111114-22208.gpx")).tracks().flatMap(Track::segments).flatMap(TrackSegment::points);
            Iterator <WayPoint> it = gpx.iterator();
            double lat;
            double longi;

            while (it.hasNext()) {
                wp=it.next();
                lat = wp.getLatitude().doubleValue();
                longi = wp.getLongitude().doubleValue();
                GeoPoint p = new GeoPoint(lat,longi);
                pathPoints.add(p);
            }
        }
        catch (IOException e) {
            Log.d("Erreur",e.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            AlertDialog dialog = builder.create();
            dialog.show();
=======
        carte = findViewById(R.id.osm_carte);
        carte.setTileSource(TileSourceFactory.MAPNIK); //Type de carte
        carte.setBuiltInZoomControls(true); //Zoom
        createGpsDisabledAlert();
        refuge = new GeoPoint(42.66615, 0.10372);
        mapController = carte.getController();
        mapController.setZoom(10.0);
        mapController.setCenter(refuge);

        ArrayList<OverlayItem> items = new ArrayList<>();
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

    }

    @Override
    public void onPause() {
        super.onPause();
        carte.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        carte.onResume();
    }


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
>>>>>>> Stashed changes
        }
        polyline.setPoints(pathPoints);
        map.invalidate();
    }
<<<<<<< Updated upstream
=======
}
>>>>>>> Stashed changes

}
