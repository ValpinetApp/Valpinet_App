package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;


import java.util.ArrayList;

public class Carte extends AppCompatActivity {

    private MapView carte;
    IMapController mapController;
    GeoPoint refuge;

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
        }
    }
}


