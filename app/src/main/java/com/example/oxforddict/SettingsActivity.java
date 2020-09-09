package com.example.oxforddict;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText appIdEditText = (EditText) findViewById(R.id.appIdTextView);
        EditText appKeyEditText = (EditText) findViewById(R.id.appKeyTextView);
        Switch isDarkSwitch = (Switch) findViewById(R.id.isDarkSwitch);

        Intent intent = getIntent();
        String appId = intent.getStringExtra("AppId");
        String appKey = intent.getStringExtra("AppKey");



        boolean isNight = intent.getBooleanExtra("isNight", false);

        if (appId != null) {
            appIdEditText.setText(appId);
        }

        if (appKey != null) {
            appKeyEditText.setText(appKey);
        }

        isDarkSwitch.setChecked(isNight); // переключатель для темы
    }
}
