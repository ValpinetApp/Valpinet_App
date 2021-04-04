package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class Carte extends AppCompatActivity {

    private org.osmdroid.views.MapView map = null;
    private boolean GPSestActif;
    public MyLocationNewOverlay overlayPosition;
    public IMapController mapController;
    public CompassOverlay overlayCompas;
    public RotationGestureOverlay overlayRotationToucher;
    public GeoPoint pointDeDepart;
    private String nomFichierGPX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_carte);
        nomFichierGPX = getIntent().getStringExtra("nom");
        Log.d("test",nomFichierGPX);
        demandePermissionGPS();
        Log.d("demandePerm", "Demande perm passée");
        map = findViewById(R.id.mv_map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(14.5);
        pointDeDepart = new GeoPoint(42.66620, 0.10373);
        mapController.setCenter(pointDeDepart);
        Marker refuge = new Marker(map);
        refuge.setPosition(pointDeDepart);
        refuge.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        refuge.setTitle("Refuge");
        map.getOverlays().add(refuge);
        Log.d("Refuge", "Refuge positionné");
        overlayPosition = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            demandeGPS();
        } else {
            GPSestActif = true;
        }

        if (GPSestActif) {
            seGeolocaliser();
        }

        Log.d("Localisation", "Localisation activée");
        lancerCompas();
        Log.d("Create", "Compas positionné");
        activerGlisser();
        Log.d("Create", "Gestion zoom à deux doigts établis");
        lancerEchelle();
        Log.d("Create", "Echelle positionnée");
        tracerGPX();
        Log.d("Create", "GPX en place");

    }

    public void onResume() {
        super.onResume();
        map.onResume();
        Log.d("Resume", "De retour");

    }

    public void onPause() {
        super.onPause();
        map.onPause();
        Log.d("Resume", "En pause");
    }

    private Boolean demandeGPS() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder
                .setMessage(getString(R.string.GPStext))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.GPSOui),
                        (paramDialogInterface, paramInt) -> {
                            GPSestActif = true;
                            demandePermissionGPS();
                            Carte.this.montrerOptionGPS();
                            seGeolocaliser();
                        }
                );
        localBuilder.setNegativeButton(getString(R.string.GPSNon), (paramDialogInterface, paramInt) -> { GPSestActif = false;paramDialogInterface.cancel();
                }
        );
        localBuilder.create().show();
        return GPSestActif;
    }

    private void montrerOptionGPS() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), -1);
    }

    public void demandePermissionGPS() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            seGeolocaliser();
        }
    }

    public void seGeolocaliser() {
        overlayPosition.enableMyLocation();
        map.getOverlays().add(overlayPosition);
    }

    public void lancerCompas() {
        overlayCompas = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        overlayCompas.enableCompass();
        map.getOverlays().add(overlayCompas);
    }

    public void activerGlisser() {
        overlayRotationToucher = new RotationGestureOverlay(this, map);
        overlayRotationToucher.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(overlayRotationToucher);
    }

    public void lancerEchelle() {
        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);
    }

    public Boolean tracerGPX() {
        //afficher trait
        Polyline chemin;
        ArrayList<GeoPoint> pathPoints = new ArrayList<>();
        chemin = new Polyline();
        chemin.setWidth(10);
        map.getOverlays().add(chemin);

        try {
            Log.d("tryGPX", "GPX loadé");
            WayPoint pointGPS;
            Stream<WayPoint> gpx;
            InputStream fichierALire = openFileInput(nomFichierGPX);
            gpx = GPX.read(fichierALire).tracks().flatMap(Track::segments).flatMap(TrackSegment::points);
            Iterator<WayPoint> it = gpx.iterator();
            double latitude;
            double longitude;

            if(it.hasNext()){
                pointGPS = it.next();
                latitude = pointGPS.getLatitude().doubleValue();
                longitude = pointGPS.getLongitude().doubleValue();
                GeoPoint point = new GeoPoint(latitude, longitude);
                pathPoints.add(point);
                mapController.setCenter(point);
            }

            while (it.hasNext()) {
                pointGPS = it.next();
                latitude = pointGPS.getLatitude().doubleValue();
                longitude = pointGPS.getLongitude().doubleValue();
                GeoPoint point = new GeoPoint(latitude, longitude);
                pathPoints.add(point);
            }
        } catch (IOException e) {
            Log.d("Erreur", e.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.dismiss();
            return false;
        }
        chemin.setPoints(pathPoints);
        map.invalidate();
        return true;
    }
}
