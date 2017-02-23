package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;


import org.junit.Before;
import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Login;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;


public class KlassenTest {
	Model model;
	Benutzer benutzer;
	
	@Before
	public void initialisieren() throws DatenbankFehler {
		try {
			model = new Model();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Login login = new Login("sigl", new char[] {'t', 'u', '2' , '0', '1', '7'});
		benutzer = model.passwortPruefen(login);
	}
	
	@Test
	public void klasseHinzufuegenTest() {
	
		try {
			//Bisherige Klassen des Jahres 2017 aus der DB holen
			KlassenModel klassenModel = Klasse.gebeKlassen(2017,model);
			int oldSize = klassenModel.getSize();
		
			//Test-Klasse in DB einfügen
			klassenModel.hinzufuegen("Test", benutzer);
			int newSize = klassenModel.getSize();
			
			//Test-Klasse wieder aus DB löschen
			Klasse klasse = klassenModel.getElementAt(newSize - 1);
			klassenModel.loeschen(klasse);
			
			// Der eigentliche Test (Beide Size Werte sollten identisch sein)
			assertEquals("Must be old size + 1", oldSize + 1, newSize);
			
			} catch (DatenbankFehler e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
}
