package fr.xyz.valpinetapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InfoExcursion extends AppCompatActivity {

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
    private String nomFichier;
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
        nomFichier=getString(R.string.nomFichier);
        ajoutExcursionTableau(id);

    }

    public void onClikCarte(View v){
            Intent intent= new Intent(this, Carte.class);
            intent.putExtra("nom",identifiant);
            startActivity(intent);
        }

    public Boolean ajoutExcursionTableau(int id) {
        try {
            InputStream fichierALire = openFileInput(nomFichier);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fichierALire));
            String ligneCourante="";
            String texte="";

            while ((ligneCourante = reader.readLine()) != null) {
                texte += ligneCourante;
            }

            JSONObject jsonRootObject = new JSONObject(texte);
            JSONObject excursionCourant=jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(id)).getJSONObject("nameValuePairs");
            String titre;
            titre=excursionCourant.getJSONObject("title").getJSONObject("nameValuePairs").getString("rendered");
            titre= titre.replace("&#8211;","-");
            tv_distance.setText(excursionCourant.getString("distance_excursion"));
            tv_title.setText(titre);
            tv_duree.setText(excursionCourant.getString("duree"));
            tv_denM.setText(excursionCourant.getString("denivele_de_montee"));
            tv_denD.setText(excursionCourant.getString("denivele_de_descente"));
            tv_Hostilite.setText(excursionCourant.getString("hostilite_du_milieu"));
            tv_TypeP.setText(excursionCourant.getJSONObject("type_parcours").getJSONObject("nameValuePairs").getJSONObject("0").getJSONObject("nameValuePairs").getString("name"));
            tv_DifT.setText(excursionCourant.getString("difficulte_technique"));
            tv_DifO.setText(excursionCourant.getString("difficulte_orientation"));
            tv_Effort.setText(excursionCourant.getString("effort_necessaire"));
            identifiant = String.valueOf(excursionCourant.getInt("id"));
            fichierALire.close();
            return true;
        } catch (Exception e) {
           return false;
        }
    }
    //pour les tests
    public void setNomFichier (String nom){
        nomFichier = nom ;
    }

}