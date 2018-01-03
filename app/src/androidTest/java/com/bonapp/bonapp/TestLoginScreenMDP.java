package com.bonapp.bonapp;

/**
 * Created by Jeremie on 08/06/2017.
 */

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
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
public class TestLoginScreenMDP {
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
    public void initActivity() {
        // Specify a valid string.
        mStringToBetypedLogin = "Espresso";
        mStringToBetypedPass = "Espresso";
        PACKAGE_NAME ="com.bonapp.bonapp";
    }

    @Test
    public void test_reset() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        //on se met dans le cas debug
        for(int i=0;i<7;i++){
            onView(withId(R.id.activity_main_debug)).perform(click());
        }
        onView(withId(R.id.mainactivity_connect)).perform(click());


        onView(withId(R.id.loginScreen_EnregistrerInfos)).check(matches(isNotChecked()));

        onView(withId(R.id.loginScreen_ResetPass)).perform(click());
//
//        //ici on verifie qu'ne appuyant sur le bouton inscription on lance bien l'activité ActivityWeb avec le parametre url egal à l'url de l'adresse de reset de mdp du site
        intended(allOf(
                hasComponent(hasShortClassName(".ActivityWeb")),
                toPackage(PACKAGE_NAME),
                hasExtra(equalTo("url"),equalTo(appContext.getResources().getString(R.string.URL_ResetPassword)))));

    }

}