package fr.xyz.valpinetapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class infoExcursion extends AppCompatActivity {

    private int id;
    private TextView tv_title;
    private TextView tv_distance;
    private TextView tv_denM;
    private TextView tv_denD;
    private TextView tv_TypeP;
    private TextView tv_Hostilite;
    private TextView tv_DifT;
    private TextView tv_DifO;
    private TextView tv_Effort;
    private TextView tv_duree;
    private String nomFichier="json";
    private String nomExcursion;
    private String identifiant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_excursion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        id = getIntent().getIntExtra("id",0);
        tv_title = findViewById(R.id.tv_titreExcursion);
        tv_distance = findViewById(R.id.tv_distanceValue);
        tv_denM = findViewById(R.id.tv_monteeValue);
        tv_denD = findViewById(R.id.tv_descenteValue);
        tv_TypeP = findViewById(R.id.tv_typePValue);
        tv_Hostilite = findViewById(R.id.tv_hostiliteValue);
        tv_DifT = findViewById(R.id.tv_difficulteTValue);
        tv_DifO = findViewById(R.id.tv_difficulteOValue);
        tv_Effort = findViewById(R.id.tv_effortValue);
        tv_duree = findViewById(R.id.tv_dureeValue);

        readData(id);

    }

    public void onClikCarte(View v){
            Intent intent= new Intent(this, Carte.class);
            intent.putExtra("nom",identifiant);
            startActivity(intent);
        }

    public void readData(int id) {
        try {
            InputStream fis = openFileInput(nomFichier);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            String fichier="";
            String line="";

            while ((fichier = r.readLine()) != null) {
                line += fichier;
            }

            JSONObject jsonRootObject = new JSONObject(line);
            tv_distance.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("distance_excursion"));
            nomExcursion = jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getJSONObject("title").getJSONObject("nameValuePairs").getString("rendered");
            tv_title.setText(nomExcursion);
            tv_duree.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("duree"));
            tv_denM.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("denivele_de_montee"));
            tv_denD.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("denivele_de_descente"));
            tv_Hostilite.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("hostilite_du_milieu"));
            tv_TypeP.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getJSONObject("type_parcours").getJSONObject("nameValuePairs").getJSONObject("0").getJSONObject("nameValuePairs").getString("name"));
            tv_DifT.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("difficulte_technique"));
            tv_DifO.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("difficulte_orientation"));
            tv_Effort.setText(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getString("effort_necessaire"));
            identifiant = String.valueOf(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs").getInt("id"));
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}