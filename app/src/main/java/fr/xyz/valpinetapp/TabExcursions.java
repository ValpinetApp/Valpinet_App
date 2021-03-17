package fr.xyz.valpinetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class TabExcursions extends AppCompatActivity {

    private ProgressDialog progressBar;
    Configuration config;
    Gson gsonFR;
    Gson gsonES;
    File fichierES;
    File fichierFR;
    TextView test;


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
        fichierES = new File(chemin,"jsonES.txt");
        fichierFR = new File(chemin, "jsonFR.txt");

    }

    public void onClik(View v){
        Intent intent= new Intent(this, infoExcursion.class);
        startActivity(intent);
    }
    public void reload(View v){
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
            fos = openFileOutput("jsonES.txt", Context.MODE_PRIVATE);
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
            InputStream fis = openFileInput("jsonES.txt");
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
}