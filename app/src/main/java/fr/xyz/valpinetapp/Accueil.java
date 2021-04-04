package fr.xyz.valpinetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Locale;

public class Accueil extends AppCompatActivity {

    private Button francais;
    private Button espagnol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        francais = findViewById(R.id.b_francais);
        espagnol = findViewById(R.id.b_espagnol);
        demandePermissionGPS();
        demandePermissionStockage();
    }

    public void onClik(View v){
        Intent intent= new Intent(this, TabExcursions.class);
        startActivity(intent);
        switch(v.getId()){
            case R.id.b_espagnol:
                changerLangue(this.getResources(),"es");
                break;

            case R.id.b_francais:
                changerLangue(this.getResources(),"fr");
                break;
        }
    }

    public static String changerLangue(Resources res, String langue){
        Configuration config;
        config = new Configuration(res.getConfiguration());
        String resultat = "es";

        switch(langue){
            case "es":
                config.setLocale(new Locale("es"));
                break;

            case "fr":
                config.setLocale(Locale.FRENCH);
                resultat = "fr";
                break;

            default:
                config.setLocale(new Locale("es"));
                break;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
        return resultat;
    }

    public void demandePermissionGPS(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
    }

    public void demandePermissionStockage(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }


}