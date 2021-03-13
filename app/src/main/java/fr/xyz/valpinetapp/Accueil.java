package fr.xyz.valpinetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

        francais = findViewById(R.id.b_francais);
        espagnol = findViewById(R.id.b_espagnol);

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

    public static void changerLangue(Resources res, String locale){
        Configuration config;
        config = new Configuration(res.getConfiguration());

        switch(locale){
            case "es":
                config.setLocale(new Locale("es"));
                break;
            case "fr":
                config.setLocale(Locale.FRENCH);
                break;
            default:
                config.setLocale(new Locale("es"));
                break;
        }

        res.updateConfiguration(config, res.getDisplayMetrics());

    }
}