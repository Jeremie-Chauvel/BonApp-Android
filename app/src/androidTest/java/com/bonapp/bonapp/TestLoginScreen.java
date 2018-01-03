package com.bonapp.bonapp;

/**
 * Created by Jeremie on 08/06/2017.
 */

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestLoginScreen {
    /*
    Test sur l'UI bien plus interessant

    doc ici :
        https://developer.android.com/training/testing/ui-testing/espresso-testing.html

    On peut en plus simuler des intents pour tester les activités separement
     */
    private String mStringToBetypedLogin;
    private String mStringToBetypedPass;
    private String PACKAGE_NAME;


    @Rule
    public IntentsTestRule<LoginScreen> mIntentsRule =
            new IntentsTestRule<>(LoginScreen.class);


    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBetypedLogin = "Espresso";
        mStringToBetypedPass = "Espresso";
        PACKAGE_NAME ="com.bonapp.bonapp";
    }



    @Test
    public void test_savInfo() {

        onView(withId(R.id.loginScreenEditTextLogin)).perform(clearText());
        onView(withId(R.id.loginScreenEditTextPassword)).perform(clearText());
        //on tape les textes
        onView(withId(R.id.loginScreenEditTextLogin)).perform(typeText(mStringToBetypedLogin));
        onView(withId(R.id.loginScreenEditTextPassword)).perform(typeText(mStringToBetypedPass));

        //on active le bouton pour sauvegarder les info
        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isNotChecked()));
        onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());
        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isChecked()));
        onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());

        // j'arrive pas à tester ça correctement pour le moment
        //on restart l'activité
//        resetScreen();
//        //test pour attendre
//        onView(withText("a string depending on XXX value")).check(doesNotExist());
//
//        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isChecked()));
//        onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());
//        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isNotChecked()));
//
//        onView(withId(R.id.loginScreenEditTextLogin)).check(matches(withText(mStringToBetypedLogin)));
//        onView(withId(R.id.loginScreenEditTextPassword)).check(matches(withText(mStringToBetypedPass)));
//
//        //on restart l'activité
//        resetScreen();
//        //test pour attendre
//        onView(withText("a string depending on XXX value")).check(doesNotExist());
//
//
//        onView(withId(R.id.loginScreenEditTextLogin)).check(matches(withText("")));
//        onView(withId(R.id.loginScreenEditTextPassword)).check(matches(withText("")));
        // onView(withId(R.id.loginScreen_EnregistrerInfos)).perform(click());
    }
    @Test
    public void test_connexion() {


        onView(withId(R.id.loginScreenEditTextLogin)).perform(clearText());
        onView(withId(R.id.loginScreenEditTextPassword)).perform(clearText());
        //on tape les textes
        onView(withId(R.id.loginScreenEditTextLogin)).perform(typeText(mStringToBetypedLogin));
        onView(withId(R.id.loginScreenEditTextPassword)).perform(typeText(mStringToBetypedPass));
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

    }


    private void resetScreen() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Activity activity =mIntentsRule.getActivity();
                activity.recreate(); // old activity instance is destroyed and shut down.
//                mIntentsRule.getActivity().recreate();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}