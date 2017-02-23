package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Login;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;

public class KursTest {
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
			//Bisherige Kurse des Jahres 2017 aus der DB holen
			KurseModel kursModel = Kurs.gebeKurse(2017,model);
			int oldSize = kursModel.getSize();
		
			//Test-Klasse in DB einfügen
			kursModel.hinzufuegen("Test", "Testfach", benutzer);
			int newSize = kursModel.getSize();
			
			//Test-Klasse wieder aus DB löschen
			Kurs kurs = kursModel.getElementAt(newSize - 1);
			kursModel.loeschen(kurs);
			
			// Der eigentliche Test (Beide Size Werte sollten identisch sein)
			assertEquals("Must be old size + 1", oldSize + 1, newSize);
			
			} catch (DatenbankFehler e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
}
