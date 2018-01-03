package com.bonapp.bonapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class ActivityWeb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent appelant= getIntent();

        ((WebView)findViewById(R.id.web_Activity_WebView)).loadUrl(appelant.getStringExtra("url"));
        (findViewById(R.id.web_Activity_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on retourne à l'activité appelante en terminant celle là
                finish();

            }
        });
    }
}
