package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TabExcursions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_excursions);
    }

    public void Back(View v){
        finish(); //Retour à l'accueil.
    }

    public void onClik(View v){
        Intent intent= new Intent(this, infoExcursion.class);
        startActivity(intent);
    }
    public void reload(View v){
        AlertDialog.Builder demandeReload = new AlertDialog.Builder(this);
        demandeReload.setTitle("Rechargement Terminé");
        demandeReload.setMessage("La page rechargera bientôt les données");
        demandeReload.show();
    }
}