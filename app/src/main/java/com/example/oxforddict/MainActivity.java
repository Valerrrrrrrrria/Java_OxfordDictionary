package com.example.oxforddict;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.jayway.jsonpath.JsonPath.parse;



public class MainActivity extends AppCompatActivity {

    MainActivityViewModel myViewModel;

    ArrayList<String> result;
    static boolean isNight;
    ConstraintLayout mainLayout;
    EditText searchEditText;
    Button searchButton;
    static String audioURL;
    TextView wordIsTextView;
    static TextView defIsTextView;
    static TextView translTextView;
    static TextView resultTextView;
    static ImageView soundImageView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                Log.i("Menu iten selected", "Settings");
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("AppId", myViewModel.getAppId());
                intent.putExtra("AppKey", myViewModel.getAppKey());
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

        myViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        wordIsTextView = (TextView) findViewById(R.id.wordIsTextView);
        defIsTextView = (TextView) findViewById(R.id.defIsTextView);
        translTextView = (TextView) findViewById(R.id.translTextView);
        soundImageView = (ImageView) findViewById(R.id.soundImageView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = searchEditText.getText().toString();
                wordIsTextView.setText(searchEditText.getText());
                searchEditText.setText("");

                new CallbackTask().execute("", myViewModel.getAppId(), myViewModel.getAppKey(), word);
            }
        });

        soundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(audioURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.start();
            }
        });
    }

}
