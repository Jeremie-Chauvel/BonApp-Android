package com.bonapp.bonapp;

import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestUnitaires {

    //on test la classe generant des token aleatoire d'identification
    //on fait des test unitaire sans contexte donc on ne peut pas vraiment tester d'autres trucs
    @Test
    public void testToken() throws Exception {
        TreeSet<String> set = new TreeSet<>();
        SessionIdentifierGenerator rand = new SessionIdentifierGenerator();
        //un set ne peux pas contenir 2 fois la meme valeur
        String randString;
        for(int i=0;i<800;i++){
            randString=rand.nextSessionId();
            if(set.contains(randString)){
                throw new Exception();
            }else{
                set.add(randString);
            }

        }


    }
}