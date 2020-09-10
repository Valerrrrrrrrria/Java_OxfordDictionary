package com.example.oxforddict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    ConstraintLayout settingsLayout;
    EditText appIdEditText;
    EditText appKeyEditText;
    Switch isDarkSwitch;
    Boolean isNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appIdEditText = (EditText) findViewById(R.id.appIdTextView);
        appKeyEditText = (EditText) findViewById(R.id.appKeyTextView);
        isDarkSwitch = (Switch) findViewById(R.id.isDarkSwitch);
        settingsLayout = (ConstraintLayout) findViewById(R.id.settingsLayout);

        final Intent intent = getIntent();
        String appId = intent.getStringExtra("AppId");
        String appKey = intent.getStringExtra("AppKey");
        isNight = intent.getBooleanExtra("isNight", false);

        if (isNight) nightTheme();
        else dayTheme();


        if (appId != null) {
            appIdEditText.setText(appId);
        }

        if (appKey != null) {
            appKeyEditText.setText(appKey);
        }

        isDarkSwitch.setChecked(isNight); // переключатель для темы

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appIdNew = appIdEditText.getText().toString();
                String appKeyNew = appKeyEditText.getText().toString();

                MainActivity.sharedPreferences.edit().putString("AppId",appIdNew).apply();
                MainActivity.sharedPreferences.edit().putString("AppKey",appKeyNew).apply();
                //MainActivity.sharedPreferences.edit().putBoolean("isNight", isNight).apply();

                Log.i("Новые данные: ", "id: "+ appIdNew + " key" + appKeyNew);
            }
        });


        isDarkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Change theme
                if (isDarkSwitch.isChecked()) {
                    nightTheme();
                    isNight = true;
                    MainActivity.sharedPreferences.edit().putBoolean("isNight", isNight).apply();
                }
                else {
                    dayTheme();
                    isNight = false;
                    MainActivity.sharedPreferences.edit().putBoolean("isNight", isNight).apply();

                }
            }
        });


    }


    public void nightTheme() {
            // Night mode
            isNight = true;

            settingsLayout.setBackgroundColor(getResources().getColor(R.color.colorNight));
            appIdEditText.setTextColor(getResources().getColor(R.color.colorDay));
            appKeyEditText.setTextColor(getResources().getColor(R.color.colorDay));
            isDarkSwitch.setTextColor(getResources().getColor(R.color.colorDay));
        }


        public void dayTheme() {
                // Day mode
                isNight = false;

                settingsLayout.setBackgroundColor(getResources().getColor(R.color.colorDay));
                appIdEditText.setTextColor(getResources().getColor(R.color.colorNight));
                appKeyEditText.setTextColor(getResources().getColor(R.color.colorNight));
                isDarkSwitch.setTextColor(getResources().getColor(R.color.colorNight));
        }
}
