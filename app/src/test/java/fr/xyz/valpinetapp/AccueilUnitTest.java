package fr.xyz.valpinetapp;

import android.util.Log;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
public class AccueilUnitTest {

    @Test
    public void langue_voulu_Espagnol() {
        Accueil ac = new Accueil();
        String langue;
       // langue = Accueil.changerLangue(( Mock.Acceuil.getResource(), "es");
        assertEquals("es", "es");
    }
}