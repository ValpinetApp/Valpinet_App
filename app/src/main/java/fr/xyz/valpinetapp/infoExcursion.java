package fr.xyz.valpinetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class infoExcursion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_excursion);

    }   public void onClikCarte(View v){
            Intent intent= new Intent(this, Carte.class);
            startActivity(intent);
        }

}