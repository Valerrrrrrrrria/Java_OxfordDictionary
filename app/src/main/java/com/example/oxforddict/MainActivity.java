package com.example.oxforddict;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> result;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.settings:
                Log.i("Menu iten selected", "Settings");
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("AppId", "5e360595");
                intent.putExtra("AppKey", "6ef6b77b7b0bcdd1a395e409655558e9");
                intent.putExtra("isNight", false);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String app_id = "5e360595";
        final String app_key = "6ef6b77b7b0bcdd1a395e409655558e9";

        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = searchEditText.getText().toString();
                searchEditText.setText("");

                String fields = "pronunciations";
                new CallbackTask().execute(dictionaryEntries(word, fields), app_id, app_key);

            }
        });
    }

    private String dictionaryEntries(final String myWord, final String fields) {
        final String language = "en-gb";
        final String word = myWord;

        //final String fields = "pronunciations";
        //final String fields = "definitions";
        //final String fields = "etymologies";


        final String strictMatch = "false";
        final String word_id = word.toLowerCase();
        //return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/" + word_id + "?strictMatch=false";
    }

    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = params[1];
            final String app_key = params[2];
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                //Log.i("ALL ", jsonObject.toString());
                ParsingJSON(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //System.out.println(result);
        }
    }

    // пока void, дальше решу
        public void ParsingJSON (JSONObject jsonObject) {
            Log.i("IN PARSING", jsonObject.toString());

            try {
                String oxtInfo = jsonObject.getString("results");
                Log.i("BY PART RESULTS ", oxtInfo);

//                JSONArray jsonArray = new JSONArray(oxtInfo);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonPart = jsonArray.getJSONObject(i);
//                    //Log.i("BY lexicalEntries ", jsonPart.getString("lexicalEntries"));
//
//                    JSONArray jsonArray1 = new JSONArray(jsonPart.getString("lexicalEntries"));
//                    for (int j = 0; j < jsonArray1.length(); j++) {
//                        JSONObject jsonPart1 = jsonArray1.getJSONObject(j);
//                        Log.i("BY entries ", jsonPart1.getString("entries"));
//
//
//
//                    }
//
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
}