package com.bonapp.bonapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean debug;
    private int nbappui;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nbappui=0;
        debug=false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ( findViewById(R.id.mainactivity_connect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on change d'activité si on connecte
                Intent mchangeActivity = new Intent();
                mchangeActivity.setClass(getApplicationContext(),LoginScreen.class);
                mchangeActivity.putExtra("debug",debug);
                startActivity(mchangeActivity);

            }
        });
        ( findViewById(R.id.mainactivity_insciption)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on change d'activité si on s'inscrit
                Intent mchangeActivity = new Intent();
                mchangeActivity.setClass(getApplicationContext(),ActivityWeb.class);
                mchangeActivity.putExtra("url",getResources().getString(R.string.URL_Inscription));
                startActivity(mchangeActivity);

            }
        });
        (findViewById(R.id.activity_main_debug)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nbappui<5){
                    nbappui+=1;
                }else if (nbappui==5){
                    debug=true;
                    nbappui+=1;
                    Toast.makeText(getApplicationContext(),"Debug ON",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
