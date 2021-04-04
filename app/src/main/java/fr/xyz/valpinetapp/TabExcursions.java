package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TabExcursions extends AppCompatActivity {

    private ProgressDialog barreDeChargement;
    public Configuration config;
    public Gson parseToString;
    public ListView maListe;
    public boolean WIFIestActif;
    private String nomFichier;
    private ArrayList<String> listeExcursions;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_excursions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        config = new Configuration(this.getResources().getConfiguration());
        barreDeChargement = new ProgressDialog(this);
        barreDeChargement.setCancelable(true);
        barreDeChargement.setMessage(getString(R.string.telechargement));
        barreDeChargement.setIndeterminate(true);
        parseToString = new Gson();
        listeExcursions = new ArrayList<String>();
        maListe = findViewById(R.id.lv_maListe);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        nomFichier=getString(R.string.nomFichier);
        if (lire()) {
        } else {
            listeExcursions.add(getString(R.string.pasExcursion));
            maListe.setAdapter(new ArrayAdapter<String>(TabExcursions.this, android.R.layout.simple_list_item_1, listeExcursions));
            Toast.makeText(TabExcursions.this, getString(R.string.notifAucuneExcursion), Toast.LENGTH_LONG).show();
        }
    }

    public void telechargerExcursion(View v){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        //on vérifie qu'il y est internet
        if (activeNetworkInfo!=null) {
            WIFIestActif =true;
        } else {
            demandeWifi();
        }

        if(WIFIestActif){
            RequestQueue queue = Volley.newRequestQueue(this);
            barreDeChargement.show();
            String url=getString(R.string.url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) { //Reception grace a l'url de l'api REST qui retourne le json des excursion
                            barreDeChargement.hide();
                            String jsonExcursions = parseToString.toJson(response);
                            Log.d("tab",jsonExcursions);
                            ecrire(jsonExcursions,nomFichier);
                            Log.d("test", String.valueOf(response.length()));
                            lire();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            barreDeChargement.hide();
                            Toast.makeText(TabExcursions.this, getString(R.string.notifProblemeTelechargement),Toast.LENGTH_LONG ).show();
                        }
                    });
            queue.add(jsonObjectRequest);}
         }

    public Boolean telechargerGPX(String url, String nomFichier){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        final Boolean[] estTelecharger = {false};
        if (activeNetworkInfo!=null) {
            WIFIestActif =true;
        } else {
            demandeWifi();
        }

        if(WIFIestActif){
            // reception des données de façon asynchrone pour éviter de faire freezer l'application
            RequestQueue queue = Volley.newRequestQueue(this);
            barreDeChargement.show();
            StringRequest gpx = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // suppression des carracteres corrompus
                    response = response.replace("ï","");
                    response = response.replace("»","");
                    response = response.replace("¿","");
                    ecrire(response,nomFichier);
                    Log.d("test",response);
                    Log.d("test",nomFichier);
                    barreDeChargement.hide();
                    estTelecharger[0] = true;
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error",error.toString());
                    barreDeChargement.hide();
                }
            });
            queue.add(gpx);}
        return estTelecharger[0];
        }

    public Boolean ecrire(String texte,String nomFichier){
        FileOutputStream fos;
        try {
            fos = openFileOutput(nomFichier, Context.MODE_PRIVATE);
            fos.write(texte.getBytes());
            fos.close();
            InputStream fis = openFileInput(nomFichier);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    public Boolean lire() {
        try {
            InputStream fis = openFileInput(nomFichier);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis,StandardCharsets.UTF_8));
            listeExcursions.clear();
            String ligneCourante="";
            String texte="";
            String excursion="";
            String titreExcursion;
            String vallee;
            String denivelle;
            String url;
            while ((ligneCourante = r.readLine()) != null) {
                texte += ligneCourante;
            }

            JSONObject jsonRootObject = new JSONObject(texte);
            JSONObject excursionCourant;
            // on parcourt chaque excursions qui est dans le fichir json
            for(int i=0;i<jsonRootObject.getJSONObject("nameValuePairs").length();i++){
                excursionCourant=jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs");

                titreExcursion = excursionCourant.getJSONObject("title").getJSONObject("nameValuePairs").getString("rendered");
                titreExcursion= titreExcursion.replace("&#8211;","-");
                vallee = excursionCourant.getString("vallee");
                denivelle =excursionCourant.getString("denivele_de_montee");
                excursion="\n" + getString(R.string.activite) + titreExcursion + "\n"+""+"\n"+getString(R.string.valle) + vallee + "\n"+""+"\n"+ getString(R.string.denivele) + denivelle + "\n";
                listeExcursions.add(excursion);
                Log.d("test",excursion);
                url = excursionCourant.getJSONObject("track").getJSONObject("nameValuePairs").getString("guid");
                id = String.valueOf(excursionCourant.getInt("id"));
                if(WIFIestActif) {
                    telechargerGPX(url, id);
                }
            }

            maListe.setAdapter(new ArrayAdapter<String>(TabExcursions.this, android.R.layout.simple_list_item_1, listeExcursions));
            maListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position;
                    Intent intent = new Intent(TabExcursions.this, InfoExcursion.class);
                    intent.putExtra("id",pos);
                    startActivity(intent);
                }
            });
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Boolean demandeWifi() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setMessage(getString(R.string.wifitext));
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton(getString(R.string.wifiOui),
                (paramDialogInterface, paramInt) -> { TabExcursions.this.montrerOptionWifi();WIFIestActif =true; }
        );
        localBuilder.setNegativeButton(getString(R.string.wifiNon), (paramDialogInterface, paramInt) -> { WIFIestActif = false; paramDialogInterface.cancel(); }
        );
        AlertDialog dialog = localBuilder.create();
        dialog.show();
        return WIFIestActif;
    }

    private void montrerOptionWifi() {
        startActivityForResult(new Intent(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)), -1);
    }
    // utilisé dans les tests
    public void setNomFichier (String nom){
        nomFichier=nom;
    }
}