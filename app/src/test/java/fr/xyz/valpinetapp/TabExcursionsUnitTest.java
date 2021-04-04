package fr.xyz.valpinetapp;

import android.view.View;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class TabExcursionsUnitTest {
    @Test
    public void ecrire_dans_un_fichier_qui_n_existe_pas() {
        Boolean estEcrit;
        TabExcursions te = new TabExcursions();
        estEcrit = te.ecrire("faux","faux");
        assertEquals(false,estEcrit);
    }

    @Test
    public void lire_dans_un_fichier_qui_n_existe_pas() {
        Boolean estlu;
        TabExcursions te = new TabExcursions();
        te.setNomFichier("faux");
        estlu = te.lire();
        assertEquals(false,estlu);
    }

}
