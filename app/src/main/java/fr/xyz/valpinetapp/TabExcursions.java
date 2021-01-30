package fr.xyz.valpinetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        Intent intent= new Intent(this, Carte.class);
        startActivity(intent);
    }
}