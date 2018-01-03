package com.bonapp.bonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Random;

public class LoginScreen extends AppCompatActivity {

    private final String TAG = "BONAPP LoginScreen";
    // On instancie les variables permettants de recuperrer les valeurs des champs et le click du bouton
    private EditText mPassword;
    private EditText mLogin;
    private Button mConnection;
    private CheckBox mCheckBoxSaveInfo;
    private String url;
    private RequestQueue queue;
    private int compteurEssaiMDP;
    private boolean debug;
    private String token; //token d'identification
    private boolean gotInternet;
    private Button boutonReset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        url = getString(R.string.URL_connection);
        Intent appelant = getIntent();//On recupere les valeurs qui nous ont été envoyés et on les separes
        debug = appelant.getBooleanExtra("debug", false);


        //        On recupere les pref pour pouvoir initialiser sur les dernier choix
        SharedPreferences mesPref = getSharedPreferences("mesPref", Context.MODE_PRIVATE);
        compteurEssaiMDP = 0;

        //On recupere par Id
        mLogin = (EditText) findViewById(R.id.loginScreenEditTextLogin);
        mPassword = (EditText) findViewById(R.id.loginScreenEditTextPassword);
        mCheckBoxSaveInfo = (CheckBox) findViewById(R.id.loginScreen_EnregistrerInfos);
        mConnection = (Button) findViewById(R.id.loginScreenButtonConnect);
        boutonReset =(Button) findViewById(R.id.loginScreen_ResetPass);
        boutonReset.setVisibility(View.INVISIBLE);
        //on set le listener sur le bouton

        if (mesPref.getBoolean("LoginScreen_SavInfo", false)) {
            mLogin.setText(mesPref.getString("Login", ""));
            mPassword.setText(mesPref.getString("MDP", ""));
            mCheckBoxSaveInfo.setChecked(true);
            Log.d(TAG, "On a chargé les infos de connexion");
        }
        if (!mesPref.getBoolean("LoginScreen_SavInfo", false)) {
            mLogin.setText("");
            mPassword.setText("");
            mCheckBoxSaveInfo.setChecked(false);
            Log.d(TAG, "On n'a pas chargé les infos de connexion");
        }

        mConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifier(mLogin.getText().toString(), mPassword.getText().toString());
            }
        });
        //button retour
        (findViewById(R.id.loginScreen_ButonRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on retourne à l'activité appelante en terminant celle là
                finish();

            }
        });

        if(debug){
            boutonReset.setVisibility(View.VISIBLE);
            boutonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //on change d'activité si on s'inscrit
                    Intent mchangeActivity = new Intent();
                    mchangeActivity.setClass(getApplicationContext(), ActivityWeb.class);
                    mchangeActivity.putExtra("url", getResources().getString(R.string.URL_ResetPassword));
                    startActivity(mchangeActivity);

                }
            });
        }



        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this.getApplication());
        // Request a string response from the provided URL.
        LoginScreen.JsoupAsyncTask jsoupAsyncTask = new LoginScreen.JsoupAsyncTask();
        jsoupAsyncTask.execute();

    }

    private void changeActiviteSiBon(boolean verifi) {
        if (verifi) {
            changeActivite();
        } else {
            Toast.makeText(getApplicationContext(), R.string.LogError, Toast.LENGTH_SHORT).show();
//            mPassword.setText(""); //ne pas vider le champ sinon c'est frustrant

            if (compteurEssaiMDP >= 3 || (debug && compteurEssaiMDP >= 0)) {
                if(!debug) {
                    boutonReset.setVisibility(View.VISIBLE);
                    //                boutonReset
                    Log.d(TAG, "On affiche le bouton reset le mdp car 5 tentative erronées");
                    //                ((RelativeLayout.LayoutParams) boutonReset.getLayoutParams()).addRule(RelativeLayout.BELOW, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //                boutonReset.requestLayout();

                    boutonReset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //on change d'activité si on s'inscrit
                            Intent mchangeActivity = new Intent();
                            mchangeActivity.setClass(getApplicationContext(), ActivityWeb.class);
                            mchangeActivity.putExtra("url", getResources().getString(R.string.URL_ResetPassword));
                            startActivity(mchangeActivity);

                        }
                    });
                }
                compteurEssaiMDP = -1;
            } else if (compteurEssaiMDP >= 0) {
                compteurEssaiMDP++;
            }
        }
    }

    private void changeActivite() {
        //on change d'app
        Log.d(TAG, "On change d'activité, on passe les arguments login et password");
        Intent mchangeActivity = new Intent();
        mchangeActivity.setClass(getApplicationContext(), SelectionMenu.class);
        mchangeActivity.putExtra("Log", mLogin.getText().toString());
        mchangeActivity.putExtra("token", token);
        mchangeActivity.putExtra("debug", debug);
        startActivity(mchangeActivity);
    }

    private void verifier(String login, String password) {

        LoginScreen.JsoupAsyncTask jsoupAsyncTask = new LoginScreen.JsoupAsyncTask();
        jsoupAsyncTask.execute();
        Log.d(TAG, "On verifie les info de connexion");
        if (debug) {
            verifierV1();
        } else {
            if (gotInternet) {
                verifierV2(login, password);
            } else {
                Toast.makeText(getApplicationContext(), R.string.InternetAcessError, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void verifierV1() {
        //version 1: on renvoit une valeur aleatoire
        Log.d(TAG, "Verification aleatoire");
        Random valeurAlea = new Random();
        if (valeurAlea.nextBoolean()) {
            changeActiviteSiBon(true);
        } else {
            changeActiviteSiBon(false);
        }
    }

    private void verifierV2(String login, final String password) {
        Log.d(TAG, "Verification par connexion non securisé");
        //body du message : on met un token pour l'identification ulterieur, le mdp et le login
        HashMap<String, String> params = new HashMap<String, String>();
        SessionIdentifierGenerator rand = new SessionIdentifierGenerator();
        token = rand.nextSessionId();
        params.put("token", token);
        params.put("user", login);
        params.put("password", password);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url + "connexion" , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    // Parsing json object response
                    // response will be a json object
                    VolleyLog.v("Response:%n %s", response.toString(4));

                    if (response.getBoolean("verif")) {
                        Log.d(TAG, "bon mdp fourni et verifié");
                        changeActiviteSiBon(true);

                    } else {
                        Log.d(TAG, "mauvais identifiants fourni");
                        changeActiviteSiBon(false);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error: " + e.getMessage());
                }

//                        Toast.makeText(getApplicationContext(),"Response is: "+ response.toString(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
                Toast.makeText(getApplicationContext(), "That didn't work, Internet connection failure.", Toast.LENGTH_SHORT).show();
                changeActiviteSiBon(false);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjReq);


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

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "on test la connexion");
            gotInternet = isOnline();
            Log.d(TAG, String.valueOf(gotInternet));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

        }

    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences mesPref = getSharedPreferences("mesPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editeur = mesPref.edit();
        editeur.putBoolean("LoginScreen_SavInfo", mCheckBoxSaveInfo.isChecked());
        if (mCheckBoxSaveInfo.isChecked()) {
            Log.d(TAG, "On a sauvegardé les infos de connexion");
            editeur.putString("Login", mLogin.getText().toString());
            editeur.putString("MDP", mPassword.getText().toString());
            editeur.putBoolean("LoginScreen_SavInfo", true);
        }
        editeur.apply();
    }


    /*getters pour les tests

     */
//
//    public String getTAG() {
//        return TAG;
//    }
//
//    public EditText getPassword() {
//        return mPassword;
//    }
//
//    public EditText getLogin() {
//        return mLogin;
//    }
//
//    public Button getConnection() {
//        return mConnection;
//    }
//
//    public CheckBox getCheckBoxSaveInfo() {
//        return mCheckBoxSaveInfo;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public RequestQueue getQueue() {
//        return queue;
//    }
//
//    public int getCompteurEssaiMDP() {
//        return compteurEssaiMDP;
//    }
//
//    public boolean isDebug() {
//        return debug;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public boolean isGotInternet() {
//        return gotInternet;
//    }
//
//    public Button getBoutonReset() {
//        return boutonReset;
//    }
}
