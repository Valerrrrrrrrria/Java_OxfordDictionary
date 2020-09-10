package com.example.oxforddict;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

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
import java.util.LinkedHashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.jayway.jsonpath.JsonPath.parse;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> result;
    static boolean isNight;
    ConstraintLayout mainLayout;
    EditText searchEditText;
    Button searchButton;
    static SharedPreferences sharedPreferences;
    String appId;
    String appKey;

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
                intent.putExtra("AppId", appId);
                intent.putExtra("AppKey", appKey);
                intent.putExtra("isNight", isNight);
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

        sharedPreferences = this.getSharedPreferences("com.example.oxforddict", Context.MODE_PRIVATE);
        //sharedPreferences.edit().putString("AppId","5e360595").apply();
        //sharedPreferences.edit().putString("AppKey","6ef6b77b7b0bcdd1a395e409655558e9").apply();
        //sharedPreferences.edit().putBoolean("isNight", false).apply();

        appId = sharedPreferences.getString("AppId", "");
        appKey = sharedPreferences.getString("AppKey", "");
        isNight = sharedPreferences.getBoolean("isNight", false);

        Log.i("ISNIGHT", "" + isNight);


        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);

        if (isNight) nightTheme();
        else dayTheme();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = searchEditText.getText().toString();
                searchEditText.setText("");

                new CallbackTask().execute("", appId, appKey, word);

            }
        });
    }

    private String dictionaryEntries(final String myWord, final String fields) {
        final String word = myWord;
        final String word_id = word.toLowerCase();

        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/" + word_id + "?strictMatch=false";
    }

    private class CallbackTask extends AsyncTask<String, Integer, String> {

        String appId;
        String appKey;
        final String language = "en-gb";

        protected String getLemma(String rawWord) throws IOException, JSONException {
            String url = "https://od-api.oxforddictionaries.com:443/api/v2/lemmas/" + language + "/" + rawWord.toLowerCase();

            BufferedReader breader = new BufferedReader(new InputStreamReader(getConnection(url).getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                stringBuilder.append(line);
            }
            ArrayList<String> parsed = parse(stringBuilder.toString()).read("$..lexicalEntries[0].inflectionOf[0].text");
            return parsed.get(0);
        }

        protected HttpsURLConnection getConnection(String rawurl) throws IOException {

            URL url = new URL(rawurl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",appId);
            urlConnection.setRequestProperty("app_key",appKey);
            urlConnection.setConnectTimeout(100);

            return urlConnection;
        }

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            appId  = params[1];
            appKey = params[2];
            final String rawWord = params[3];

            try {

                HttpsURLConnection connect = getConnection(dictionaryEntries(getLemma(rawWord), ""));
                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
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

            Object document = Configuration.defaultConfiguration().jsonProvider().parse(result);
            //ArrayList<String> parsed = parse(stringBuilder.toString()).read("$..lexicalEntries[0].inflectionOf[0].text");

            ArrayList<String> lexical = JsonPath.read(document,"$..lexicalCategory.text");
            ArrayList<URL> audios = JsonPath.read(document,"$..audioFile");
            ArrayList<String> dialects = JsonPath.read(document,"$..dialects");
            ArrayList<LinkedHashMap<String, Object>> senses = JsonPath.read(document,"$.results[0].lexicalEntries[0].entries[0].senses");

            for (int i = 0; i<senses.size(); i++)
                Log.i("SENSES1", "Definition " + senses.get(i).get("definitions").toString() + "EXAMPLES" + senses.get(i).get("examples").toString());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appId = sharedPreferences.getString("AppId", "");
        appKey = sharedPreferences.getString("AppKey", "");
        isNight = sharedPreferences.getBoolean("isNight", false);

        if (isNight) nightTheme();
        else dayTheme();
    }

    public void nightTheme() {
        // Night mode
        isNight = true;

        mainLayout.setBackgroundColor(getResources().getColor(R.color.colorNight));

    }


    public void dayTheme() {
        // Day mode
        isNight = false;

        mainLayout.setBackgroundColor(getResources().getColor(R.color.colorDay));

    }


}
