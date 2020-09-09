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

        EditText apiEditText = (EditText) findViewById(R.id.apiEditText);
        Switch isDarkSwitch = (Switch) findViewById(R.id.isDarkSwitch);

        Intent intent = getIntent();
        String api = intent.getStringExtra("API");
        boolean isNight = intent.getBooleanExtra("isNight", false);

        if (api != null) {
            apiEditText.setText(api);
        }

        isDarkSwitch.setChecked(isNight); // переключатель для темы
    }
}
