package com.bonapp.bonapp;

/**
 * Created by Jeremie on 08/06/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUI {
    /*
    Test sur l'UI bien plus interessant

    doc ici :
        https://developer.android.com/training/testing/ui-testing/espresso-testing.html

    On peut en plus simuler des intents pour tester les activités separement
     */
    private String mStringToBetypedLogin;
    private String mStringToBetypedPass;
    private String PACKAGE_NAME;
    private boolean debug;


    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule =
            new IntentsTestRule<>(MainActivity.class);


    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBetypedLogin = "Espresso";
        mStringToBetypedPass = "Espresso";
        PACKAGE_NAME ="com.bonapp.bonapp";
        debug =false;
    }

    @Test
    public void test_UI_general() {

        Context appContext = getTargetContext();

        // Type text and then press the button.
        onView(withId(R.id.mainactivity_connect)).perform(click());
        onView(withId(R.id.loginScreen_ButonRetour)).perform(click());
        // Verifies that the DisplayMessageActivity received an intent
        // with the correct package name and message.

        //ici on verifie qu'ne appuyant sur le bouton connect on lance bien l'activité LoginScreen avec le parametre debug =false
//        intended(hasExtra(equalTo("debug"),equalTo(false)));
//        onView(withId(R.id.mainactivity_connect)).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".LoginScreen")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("debug"),equalTo(false))));


        onView(withId(R.id.mainactivity_insciption)).perform(click());
//        // Verifies that the DisplayMessageActivity received an intent
//        // with the correct package name and message.
//
//        //ici on verifie qu'ne appuyant sur le bouton inscription on lance bien l'activité ActivityWeb avec le parametre url egal à l'url de l'adresse d'inscription du site
        intended(allOf(
                hasComponent(hasShortClassName(".ActivityWeb")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("url"),equalTo(appContext.getResources().getString(R.string.URL_Inscription)))));

        onView(withId(R.id.web_Activity_button)).perform(click());


        //on active le mode debug
        if(debug) {
            for (int i = 0; i < 7; i++) {
                onView(withId(R.id.activity_main_debug)).perform(click());
            }
        }
//        //ici on verifie qu'ne appuyant sur le bouton connect on lance bien l'activité LoginScreen avec le parametre debug =true
        onView(withId(R.id.mainactivity_connect)).perform(click());
        if(debug) {
            intended(allOf(
                    hasComponent(hasShortClassName(".LoginScreen")),
                    toPackage(PACKAGE_NAME),
                    hasExtra(equalTo("debug"), equalTo(true))));
        }

        onView(withId(R.id.loginScreenEditTextLogin)).perform(clearText());
        onView(withId(R.id.loginScreenEditTextPassword)).perform(clearText());
        //on tape les textes
        onView(withId(R.id.loginScreenEditTextLogin)).perform(typeText(mStringToBetypedLogin));
        onView(withId(R.id.loginScreenEditTextPassword)).perform(typeText(mStringToBetypedPass));

        //on active puis desactive le bouton pour sauvegarder les info
        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isNotChecked()));
        onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());
        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isChecked()));
        onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());



        ///////
        ////////  ATTENTION à partir d'ici le mockUp serveur ou le vrai serveur doit être lancé !
        //////
        onView(withId(R.id.loginScreenButtonConnect)).perform(click());
    //on test qu'on lance bien l'activité SelectionMenu avec le bon identifiant et avec un token
        intended(allOf(
                hasComponent(hasShortClassName(".SelectionMenu")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("Log"), equalTo(mStringToBetypedLogin)),
                hasExtraWithKey(equalTo("token"))));


        onView(withId(R.id.selectionMenu_buttonMenu)).perform(click());
//on test qu'on lance bien l'activité AfficheMenu
        intended(allOf(
                hasComponent(hasShortClassName(".AfficheMenu")),
                toPackage(PACKAGE_NAME)
                ));

        onView(withId(R.id.afficheMenu_Button)).perform(click());


        //test bouton radio exclusif
        onView(withId(R.id.activity_selection_menu_radioButton_JeMange)).check(matches(isChecked()));
        onView(withId(R.id.activity_selection_menu_radioButton_JeNeMangePas)).check(matches(isNotChecked()));

        onView(withId(R.id.activity_selection_menu_radioButton_JeNeMangePas)).perform(click());
        onView(withId(R.id.activity_selection_menu_radioButton_JeMange)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_selection_menu_radioButton_JeNeMangePas)).check(matches(isChecked()));

        //on test que les jours de la semaine sont touts decoché puis on les coches tous
        for(int i=0;i<7;i++){
            onView(withId(R.id.valeurCheckBox+i)).check(matches(isNotChecked()));
            onView(withId(R.id.valeurCheckBox+i)).perform(click());
            onView(withId(R.id.valeurCheckBox+i)).check(matches(isChecked()));
        }
        //on valide
        onView(withId(R.id.activity_selection_menu_button_OK)).perform(click());


        ///on test la sauvegarde ici

        onView(withId(R.id.loginScreenButtonConnect)).perform(click());
        //on test qu'on lance bien l'activité SelectionMenu avec le bon identifiant et avec un token





        //test bouton radio exclusif + on remet tout en etat
        onView(withId(R.id.activity_selection_menu_radioButton_JeMange)).check(matches(isNotChecked()));
        onView(withId(R.id.activity_selection_menu_radioButton_JeNeMangePas)).check(matches(isChecked()));
        onView(withId(R.id.activity_selection_menu_radioButton_JeMange)).perform(click());
        onView(withId(R.id.activity_selection_menu_radioButton_JeMange)).check(matches(isChecked()));
        onView(withId(R.id.activity_selection_menu_radioButton_JeNeMangePas)).check(matches(isNotChecked()));
        //on test que les jours de la semaine sont touts decoché puis on les coches tous
        for(int i=0;i<7;i++){
            onView(withId(R.id.valeurCheckBox+i)).check(matches(isChecked()));
            onView(withId(R.id.valeurCheckBox+i)).perform(click());
            onView(withId(R.id.valeurCheckBox+i)).check(matches(isNotChecked()));
        }

        onView(withId(R.id.activity_selection_menu_button_OK)).perform(click());
        onView(withId(R.id.loginScreen_ButonRetour)).perform(click());
        //retour ecran accueil

        /*

        EXEMPLE :


        intended(allOf(
    hasAction(equalTo(Intent.ACTION_VIEW)),
    hasCategories(hasItem(equalTo(Intent.CATEGORY_BROWSABLE))),
    hasData(hasHost(equalTo("www.google.com"))),
    hasExtras(allOf(
        hasEntry(equalTo("key1"), equalTo("value1")),
        hasEntry(equalTo("key2"), equalTo("value2")))),
        toPackage("com.android.browser")));
         */

        /*
        onView(withId(R.id.editTextUserInput)).perform(typeText(mStringToBetyped), closeSoftKeyboard());
        onView(withId(R.id.changeTextBt)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.textToBeChanged)).check(matches(withText(mStringToBetyped)));
        */
    }
}