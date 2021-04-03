package fr.xyz.valpinetapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TabExcursions extends AppCompatActivity {

    private ProgressDialog progressBar;
    public Configuration config;
    public Gson gsonES;
    public File fichierES;
    public ListView liste;
    public boolean estActif;
    private String nomFichier="json";
    private ArrayList<String> chaine;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_excursions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        config = new Configuration(this.getResources().getConfiguration());
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Cargando...");
        progressBar.setIndeterminate(true);

        gsonES = new Gson();

        chaine = new ArrayList<String>();

        liste = findViewById(R.id.lv_maListe);

        File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        fichierES = new File(chemin, nomFichier);


        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            estActif = true;
        } else {
            createWiFIDisabledAlert();
        }

        if (!nomFichier.isEmpty()) {
            readData();
        } else {
            Toast.makeText(TabExcursions.this, "Por favor, recargue.", Toast.LENGTH_LONG).show();
        }
    }
    public void reload(View v){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo!=null) {
            estActif=true;
        } else {
            createWiFIDisabledAlert();
        }
        if(estActif){
        RequestQueue queue = Volley.newRequestQueue(this);
        progressBar.show();
        String url="https://valpinetapp.projetud.ovh/wp-json/monapi/v2/excursionsAPI";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.hide();
                        String jsonES = gsonES.toJson(response);
                        ecrire(jsonES,nomFichier);
                        Log.d("test", String.valueOf(response.length()));
                        readData();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                       progressBar.hide();
                       Toast.makeText(TabExcursions.this, error.toString(),Toast.LENGTH_LONG).show();

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);}
    else{
        createWiFIDisabledAlert();
        }
    }

    public void reloadGPX(String url, String nomF){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo!=null) {
            estActif=true;
        } else {
            createWiFIDisabledAlert();
        }
        if(estActif){
            RequestQueue queue = Volley.newRequestQueue(this);
            progressBar.show();

            StringRequest gpx = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    response = response.replace("ï","");
                    response = response.replace("»","");
                    response = response.replace("¿","");
                    ecrire(response,nomF);
                    Log.d("test",response);
                    Log.d("test",nomF);
                    progressBar.hide();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error",error.toString());
                    progressBar.hide();
                }
            });

            // Access the RequestQueue through your singleton class.
            queue.add(gpx);}
        else{
            createWiFIDisabledAlert();
        }
    }

    public void ecrire(String fichier,String nomFichier){
        FileOutputStream fos;
        try {
            fos = openFileOutput(nomFichier, Context.MODE_PRIVATE);
            fos.write(fichier.getBytes());
            fos.close();
            InputStream fis = openFileInput(nomFichier);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis,StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readData() {
        try {
            InputStream fis = openFileInput(nomFichier);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis,StandardCharsets.UTF_8));

            String fichier="";
            String line="";
            String ex="";

            while ((fichier = r.readLine()) != null) {
              line += fichier;
            }
            JSONObject jsonRootObject = new JSONObject(line);
            for(int i=0;i<jsonRootObject.getJSONObject("nameValuePairs").length();i++){
            String titreE = jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs").getJSONObject("title").getJSONObject("nameValuePairs").getString("rendered");
            ex = "\n" + "Actividad : " + titreE + "\n"+""+"\n";
            String vallee = jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs").getString("vallee");
            ex = ex + "Zona/Valle : " + vallee + "\n"+""+"\n";
            String denivelle = jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs").getString("denivele_de_montee");
            ex = ex + "Desnivel (m) : " + denivelle + "\n";
            chaine.add(ex);
            Log.d("test",ex);
            String url = jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs").getJSONObject("track").getJSONObject("nameValuePairs").getString("guid");
            id = String.valueOf(jsonRootObject.getJSONObject("nameValuePairs").getJSONObject(String.valueOf(i)).getJSONObject("nameValuePairs").getInt("id"));
            reloadGPX(url,id);
            }

            liste.setAdapter(new ArrayAdapter<String>(TabExcursions.this, android.R.layout.simple_list_item_1, chaine));
            liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position;
                    Intent intent = new Intent(TabExcursions.this, infoExcursion.class);
                    intent.putExtra("id",pos);
                    startActivity(intent);
                }
            });

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createWiFIDisabledAlert() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setMessage("Le Wifi est inactif, voulez-vous l'activer ?");
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton("Activer Wifi ",
                (paramDialogInterface, paramInt) -> {
                    TabExcursions.this.showWiFiOptions();
                    estActif=true;
                }
        );
        localBuilder.setNegativeButton("Ne pas l'activer ",
                (paramDialogInterface, paramInt) -> {
                    estActif = false;
                    paramDialogInterface.cancel();
                }
        );
        AlertDialog dialog = localBuilder.create();
        dialog.show();
    }

    private void showWiFiOptions() {
        startActivityForResult(new Intent(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)), -1);
    }

}