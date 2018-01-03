package com.bonapp.bonapp;

/**
 * Created by Jeremie on 08/06/2017.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestMain {
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
    public IntentsTestRule<MainActivity> mIntentsRule =
            new IntentsTestRule<>(MainActivity.class);


    @Before
    public void initValidString() {
        // Specify a valid string.
        PACKAGE_NAME ="com.bonapp.bonapp";

    }

    @Test
    public void test_button_Connect() {

        onView(withId(R.id.mainactivity_connect)).perform(click());
        //ici on verifie qu'ne appuyant sur le bouton connect on lance bien l'activité LoginScreen avec le parametre debug =false
//        intended(hasExtra(equalTo("debug"),equalTo(false)));
//        onView(withId(R.id.mainactivity_connect)).perform(click());
        intended(allOf(
                hasComponent(hasShortClassName(".LoginScreen")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("debug"),equalTo(false))));




    }
    public void test_button_Inscription() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        onView(withId(R.id.mainactivity_insciption)).perform(click());
//        // Verifies that the DisplayMessageActivity received an intent
//        // with the correct package name and message.
//
//        //ici on verifie qu'ne appuyant sur le bouton inscription on lance bien l'activité ActivityWeb avec le parametre url egal à l'url de l'adresse d'inscription du site
        intended(allOf(
                hasComponent(hasShortClassName(".ActivityWeb")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("url"),equalTo(appContext.getResources().getString(R.string.URL_Inscription)))));

    }
    public void test_button_ConnectwithDebug() {

        //on active le mode debug
        for (int i = 0; i < 7; i++) {
            onView(withId(R.id.activity_main_debug)).perform(click());

        }
//        //ici on verifie qu'ne appuyant sur le bouton connect on lance bien l'activité LoginScreen avec le parametre debug =true
        onView(withId(R.id.mainactivity_connect)).perform(click());

            intended(allOf(
                    hasComponent(hasShortClassName(".LoginScreen")),
                    toPackage(PACKAGE_NAME),
                    hasExtra(equalTo("debug"), equalTo(true))));

    }
}