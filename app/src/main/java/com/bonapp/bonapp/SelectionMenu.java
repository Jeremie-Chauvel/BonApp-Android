package com.bonapp.bonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SelectionMenu extends AppCompatActivity {
    private final String TAG="BONAPP SelectionMenu";
    private boolean debugAvecInternet;
//    declarations des variables qui permettent de creer dynamiquement les boutons et le texte
    private TextView[] mtextesJours;
    private CheckBox[] mcheckboxJours;
//                                                      //on gere dans le fragment today maintenant
////    Radio bouton pour choisir si on mange ou pas
    private RadioButton mbuttonJeMange;
    private RadioButton mbuttonJeNeMangePas;
    private String login;
    private String token; //token d'identification

//    Bouton pour valider
    private Button mbuttonOK;         //on gere dans le fragment grille
    private Button mbuttonMenu;

    private RequestQueue queue; //queue Volley
    private String url ;//url serveur

    private boolean mangeAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_menu);

        ////On lock l'orientation de l'ecran
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        url=getString(R.string.URL_connection);

//        //On recupere les pref pour pouvoir initialiser sur les dernier choix
        SharedPreferences mesPref = getSharedPreferences("mesPref", Context.MODE_PRIVATE);//On a sauvegardé dans les preferences (un fichier sur le telephone

//        //On recupere aussi le login pour pouvoir stocker plusieur profil de choix sur le tel
        Intent appelant=getIntent();//On recupere les valeurs qui nous ont été envoyés et on les separes
        login = appelant.getStringExtra("Log");
        token=appelant.getStringExtra("token");
        debugAvecInternet=appelant.getBooleanExtra("debug",false);
        Log.d(TAG,"On a recuperé les valeurs de l'activité precedente");
        Log.d(TAG, "log :"+login+' '+token);//affichage des valeurs recup

//        //On creer une variables pour le texte des jours (ps la creer comme ça et non en la codant en dure permet la traduction automatique
        final List<String> jours = Arrays.asList(getString(R.string.Monday),getString(R.string.Tuesday),getString(R.string.Wednesday),getString(R.string.Thursday),getString(R.string.Friday),getString(R.string.Saturday),getString(R.string.Sunday));
//        On init les variables
        mtextesJours=  new TextView[7];
        mcheckboxJours = new CheckBox[7]; //on gere dans le fragment today maintenant
//        On recupere la view GridLayout declaré dans le XML
        final GridLayout maGrille = (GridLayout) findViewById(R.id.activity_selection_menu_Grid);
//        On creer les 7 boutons
        for(int i=0;i<7;i++ ){
            mtextesJours[i]= new TextView(getApplicationContext());
            mcheckboxJours[i]= new CheckBox(getApplicationContext());

            mtextesJours[i].setText(jours.get(i));
            // pour positionner dans la grille on a besoin d'utiliser un Layout param
            GridLayout.Spec row = GridLayout.spec(i);
            GridLayout.Spec colTexte = GridLayout.spec(0);
            LayoutParams paramsTexte = new LayoutParams(row,colTexte);
            GridLayout.Spec colCheckBox = GridLayout.spec(1);
            LayoutParams paramsCheckBox = new LayoutParams(row,colCheckBox);
            mtextesJours[i].setLayoutParams(paramsTexte);
            mtextesJours[i].setPadding(0,20,0,20);
            mtextesJours[i].setTextColor(getResources().getColor(R.color.noire));
            mcheckboxJours[i].setLayoutParams(paramsCheckBox);
            mcheckboxJours[i].setId(R.id.valeurCheckBox + i);//Utilisé pour le test Expresso
            //on utilise les pref pour avoir les anciennes sauvegardes
            mcheckboxJours[i].setChecked(mesPref.getBoolean(login+"jours"+i,false));
            maGrille.addView(mcheckboxJours[i]);
            maGrille.addView(mtextesJours[i]);
        }

        mbuttonJeMange = (RadioButton) findViewById(R.id.activity_selection_menu_radioButton_JeMange);
        mbuttonJeMange.setChecked(mesPref.getBoolean(login+"TodayMange",true));
        mbuttonJeNeMangePas = (RadioButton) findViewById(R.id.activity_selection_menu_radioButton_JeNeMangePas);
        mbuttonJeNeMangePas.setChecked(mesPref.getBoolean(login+"TodayMangePas",false));
        mbuttonOK = (Button) findViewById(R.id.activity_selection_menu_button_OK);
        mbuttonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //J'assigne au bouton OK la sauvegarde et la destruction de l'activity
                    Toast.makeText(getApplicationContext(),R.string.Validate,Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        mbuttonMenu = (Button) findViewById(R.id.selectionMenu_buttonMenu);
        mbuttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"On change d'activité pour voire les menu pour la semaine");
                Intent mchangeActivity = new Intent();
                mchangeActivity.setClass(getApplicationContext(),AfficheMenu.class);
                startActivity(mchangeActivity);
            }
        });




// Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this.getApplication());
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"Activité en pause : on sauvegarde sans internet");
        sav(false);
        //On sauvegarde ici comme ça dans tout les cas c'est bon, même si le bouton ok n'a pas été pressé
    }

    @Override
    public void onStop(){
        super.onStop();
        sav(true);
        Log.d(TAG,"Activité arreté : on sauvegarde avec internet");
        //On savegarde ici avec internet (c'est lancé lorsque l'activité est arreté)


    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "on test la connexion");
            if(isOnline()){
                Log.d(TAG,"On a sauvé avec internet");
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("token", token);//utiliser un string de 32 de long
                param.put("user", login);
                param.put("mange", String.valueOf(mangeAnswer));
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                        url+"choix"+"", new JSONObject(param), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            // Parsing json object response
                            // response will be a json object

                            if(response.getBoolean("c_bon")){
                                Log.d(TAG,"Modifié sur le serveur !");


                            }else{

                                Log.d(TAG,"Error" );
                                Toast.makeText(getApplicationContext(),R.string.InternetError,Toast.LENGTH_LONG).show();
//
                            }


                        } catch (JSONException error) {
                            error.printStackTrace();
                            Log.d(TAG, "Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),R.string.InternetError,Toast.LENGTH_LONG).show();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),R.string.InternetError,Toast.LENGTH_LONG).show();
//
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsonObjReq);
            }else{
                Toast.makeText(getApplicationContext(),R.string.InternetAcessError,Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

        }

    }
    private void sav(boolean avecInternet){


        if (avecInternet && !debugAvecInternet) {
            mangeAnswer=mbuttonJeMange.isChecked();
            SelectionMenu.JsoupAsyncTask jsoupAsyncTask = new SelectionMenu.JsoupAsyncTask();
            jsoupAsyncTask.execute();

        }else{
            Log.d(TAG,"On a sauvé en local");
            SharedPreferences mesPref = getSharedPreferences("mesPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editeur = mesPref.edit();
            for (int i = 0; i < 7; i++) {
                editeur.putBoolean(login + "jours" + i, mcheckboxJours[i].isChecked());
            }
            editeur.putBoolean(login + "TodayMange", mbuttonJeMange.isChecked());
            editeur.putBoolean(login + "TodayMangePas", mbuttonJeNeMangePas.isChecked());
            editeur.apply();
        }
    }
    /* methode pour verifier la connexion à Internet
    on check avec un ping detail sur :
    https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
            */
    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }


}
