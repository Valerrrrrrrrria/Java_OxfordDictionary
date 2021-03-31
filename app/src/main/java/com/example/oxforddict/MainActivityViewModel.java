package com.example.oxforddict;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.jayway.jsonpath.JsonPath.parse;

public class MainActivityViewModel extends ViewModel {

    private String result;
    private MutableLiveData<String> resultLiveData = new MutableLiveData<>();

    private String appId = "5e360595";
    private String appKey = "6ef6b77b7b0bcdd1a395e409655558e9";
    public static SharedPreferences sharedPreferences;


    public MutableLiveData<String> getCurrentValue() {
        resultLiveData.setValue(result);
        return resultLiveData;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppId(String m_appId) {
        appId = m_appId;
    }

    public void setAppKey(String m_appKey) {
        appKey = m_appKey;
    }



}

class CallbackTask extends AsyncTask<String, Integer, String> {

    String appId = "5e360595";
    String appKey = "6ef6b77b7b0bcdd1a395e409655558e9";
    final String language = "en-gb";

    public String dictionaryEntries(final String myWord) {
        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/" + myWord.toLowerCase() + "?strictMatch=false";
    }
    protected String getLemma(String rawWord) throws IOException, JSONException {
        String url = "https://od-api.oxforddictionaries.com/api/v2/lemmas/" + language + "/" + rawWord.toLowerCase();

        //Log.i("getLemma", getConnection(url).getResponseMessage());

        BufferedReader breader = new BufferedReader(new InputStreamReader(getConnection(url).getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = breader.readLine()) != null) {
            stringBuilder.append(line);
        }
        ArrayList<String> parsed = parse(stringBuilder.toString()).read("$..lexicalEntries[0].inflectionOf[0].text");

        if (parsed.size() != 0)
            return parsed.get(0);
        else
            return rawWord;
    }

    protected HttpsURLConnection getConnection(String rawurl) throws IOException {

        URL url = new URL(rawurl);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("app_id", appId);
        urlConnection.setRequestProperty("app_key", appKey);
        urlConnection.setConnectTimeout(1000);

        return urlConnection;
    }

    @Override
    protected String doInBackground(String... params) {

        appId = params[1];
        appKey = params[2];
        final String rawWord = params[3];

        try {

            HttpsURLConnection connect = getConnection(dictionaryEntries(getLemma(rawWord)));
            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.i("ON POS EXECUTE", result);

        if (result == "") {
            MainActivity.defIsTextView.setText("NOT FOUND");
            return;
        }

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(result);

        ArrayList<String> lexical = JsonPath.read(document, "$..lexicalCategory.id");
        ArrayList<String> audios = JsonPath.read(document, "$..audioFile");
        MainActivity.audioURL = audios.get(0);

        ArrayList<String> transcriptions = JsonPath.read(document, "$..phoneticSpelling");
        ArrayList<String> dialects = JsonPath.read(document, "$..dialects");
        ArrayList<LinkedHashMap<String, Object>> senses = JsonPath.read(document, "$.results[0].lexicalEntries[0].entries[0].senses");

        MainActivity.defIsTextView.setText(lexical.get(0).toString());
        MainActivity.soundImageView.setVisibility(View.VISIBLE);
        MainActivity.translTextView.setText("/" + transcriptions.get(0).toString() + "/");

        String allSenses = "";
        for (int i = 0; i < senses.size(); i++) {
            allSenses += senses.get(i).get("definitions").toString().replace("[\"", "").replace("\"]","") +
                    "\n" + "Examples:" + "\n" + senses.get(i).get("examples").toString().replace("[{\"text\":\"","").
                    replace("\"},","").
                    replace("{\"text\":\"", "").
                    replace("\"}]","") + "\n\n";
        }

        MainActivity.resultTextView.setText(allSenses);
    }
}
