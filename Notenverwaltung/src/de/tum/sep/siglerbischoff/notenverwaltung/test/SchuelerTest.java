package de.tum.sep.siglerbischoff.notenverwaltung.test;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.model.*;



public class SchuelerTest {

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
	public void schuelerHinzufuegenTest() {
	
		try {
			// Bisherige Schueler aus DB geholt
			SchuelerTableModel schuelerTableModel = Schueler.gebeSchueler(model);
			int oldSize = schuelerTableModel.getRowCount();
			
			// Test-Schueler in DB einfuegen
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date yourDate;
			try {
				yourDate = sdf.parse("2010-01-01");
				schuelerTableModel.hinzufuegen("Test", yourDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Testschueler wieder aus DB loeschen
			Schueler schueler = schuelerTableModel.getElementAt(schuelerTableModel.getRowCount() - 1);
			Schueler.loescheSchueler(model, schueler);
							
			// Der eigentliche Test (Beide Size Werte sollten identisch sein)
			assertEquals("Must be old size + 1", oldSize + 1, schuelerTableModel.getRowCount());
				
			} catch (DatenbankFehler e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	
}
