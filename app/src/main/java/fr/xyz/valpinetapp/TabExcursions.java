package fr.xyz.valpinetapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TabExcursions extends AppCompatActivity {

    private ProgressDialog progressBar;
    public Configuration config;
    public Gson gsonFR;
    public Gson gsonES;
    public File fichierES;
    public File fichierFR;
    public TextView test;
    public boolean estActif;
    private String nomFichier="json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_excursions);
        config = new Configuration(this.getResources().getConfiguration());
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Cargando...");
        progressBar.setIndeterminate(true);

        gsonFR=new Gson();
        gsonES=new Gson();

        test = findViewById(R.id.test);

        File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        fichierES = new File(chemin,nomFichier);



        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo!=null) {
            estActif=true;
        } else {
            createWiFIDisabledAlert();
        }
    }

    public void onClik(View v){
        Intent intent= new Intent(this, infoExcursion.class);
        startActivity(intent);
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
                        jsonES = jsonToPrettyFormat(jsonES);
                        ecrireJSONES(jsonES);
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


    public static String jsonToPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        return gson.toJson(json);
    }

    public void ecrireJSONES(String json){
        FileOutputStream fos;
        try {
            fos = openFileOutput(nomFichier, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData() {
        try {
            InputStream fis = openFileInput(nomFichier);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = r.readLine()) != null) {
               test.setText(line+"\n");
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        localBuilder.create().show();
    }

    private void showWiFiOptions() {
        startActivityForResult(new Intent(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)), -1);
    }

}