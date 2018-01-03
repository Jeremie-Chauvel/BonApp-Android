package com.bonapp.bonapp;



import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class AfficheMenu extends AppCompatActivity {
    private Document htmlDocument;
    private String htmlPageUrl;
    Elements htmlElements;

    private static final String TAG="TAG_AfficheMenu";



    Elements htmlElements2;

    private TextView[] parsedHtmlNode= new TextView[7];
    private TextView[] parsedHtmlDates= new TextView[7];
    private String[] dates = new String[7];
    private String[] htmlContentInStringFormat= new String[7];
    private Button mbuttonRetour;
    private GridLayout monLayout;
    private boolean mErreur;
    private int nbElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_menu);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //On lance le thread qui va chercher les infos en arriere plan

        htmlPageUrl=getString(R.string.URL_Crous);
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();



        monLayout= (GridLayout) findViewById(R.id.afficheMenu_Grid);
        GridLayout monLayout= (GridLayout) findViewById(R.id.afficheMenu_Grid);

        for(int i=0;i<7;i++ ) {
//            On genere les parametre de layout pour placer les Views dans une grille
            GridLayout.Spec col = GridLayout.spec(i);
            GridLayout.Spec ligneTexte = GridLayout.spec(1);
            GridLayout.LayoutParams paramsTexte = new GridLayout.LayoutParams(ligneTexte,col);
            GridLayout.Spec ligneDate = GridLayout.spec(0);
            GridLayout.LayoutParams paramsDates = new GridLayout.LayoutParams(ligneDate,col);
//            On genere les views en questions
//            le set texte Color est important sinon de base i va ecrire en blanc sur blanc...
            parsedHtmlNode[i] = new TextView(getApplicationContext());
            parsedHtmlNode[i].setLayoutParams(paramsTexte);
            parsedHtmlNode[i].setTextColor(getResources().getColor(R.color.noire));
            parsedHtmlDates[i] = new TextView(getApplicationContext());
            parsedHtmlDates[i].setLayoutParams(paramsDates);
            parsedHtmlDates[i].setPadding(20,20,100,30);
            parsedHtmlDates[i].setTextColor(getResources().getColor(R.color.colorRed));
            monLayout.addView(parsedHtmlDates[i]);
            monLayout.addView(parsedHtmlNode[i]);
            mErreur=false;
        }
        mbuttonRetour = (Button) findViewById(R.id.afficheMenu_Button);
        mbuttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"on recherche sur le net");
            try {
                //On recupere le document html
                htmlDocument = Jsoup.connect(htmlPageUrl).get();

                Log.d(TAG,"on est connecté");

                Log.d("TAG_Menu","on est connecté");



                htmlElements2=htmlDocument.select("ul.slides");
                for(int i=0;i<7;i++ ) {
                    htmlContentInStringFormat[i]="";
                    dates[i]="";
                }

                int nombreMenu=htmlElements2.select("h3").size();
//                Log.d("TAG_Menu",htmlElements2.toString());
                Log.d(TAG,"Nombre jour : "+nombreMenu);
                nbElements=nombreMenu;
                //On parse le document HTML pour recuperer les valeurs qui nous interesse.
                for(int k=0;k<nombreMenu;k++){
                    dates[k]=htmlElements2.select("h3").get(k).text()+":";
                    htmlElements=htmlElements2.select("div.content-repas").get(1+k*3).select("ul.liste-plats").get(1).select("li");
                    for(Element htmlElement:htmlElements){
                        htmlContentInStringFormat[k]+= "\t\t+\t"+htmlElement.text().toString()+"\n\n";

                        Log.d(TAG,"\t\t+"+htmlElement.text().toString()+"\n\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Erreur Internet "+e.toString());
                dates[0]=getResources().getString(R.string.InternetError);
                mErreur=true;
//                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Log.d(TAG,"On a set les view");


            for(int i=0;i<7;i++ ) {
                //on modifie les textes des Views
                parsedHtmlNode[i].setText(htmlContentInStringFormat[i]);
                parsedHtmlDates[i].setText(dates[i]);
            }
            if(mErreur) {
                //si erreur on supprime toute les views sauf une qui sert à afficher le message.
                for (int i = 0; i < 7; i++) {
                    parsedHtmlNode[i].setVisibility(View.GONE);
                }
                for (int i = 1; i < 7; i++) {
                    parsedHtmlDates[i].setVisibility(View.GONE);
                }
            }else{//on degage les view qui serve à rien
            for(int i=nbElements;i<7;i++ ) {
                parsedHtmlNode[i].setVisibility(View. GONE );
                parsedHtmlDates[i].setVisibility(View. GONE );
            }

// Gérez la visibilité du composant, il peut être Visible, Invisible (caché mais l'espace pour le composant reste réservé) ou Gone (caché et l'espace du composant est libéré et utilisé par les autres composants).
            }

            monLayout.requestLayout();


        }
    }

}
