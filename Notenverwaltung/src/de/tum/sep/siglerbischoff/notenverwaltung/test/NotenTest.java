package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import org.junit.Before;
import org.junit.Test;
import de.tum.sep.siglerbischoff.notenverwaltung.model.*;

public class NotenTest {
	Model model;
	Benutzer benutzer;
	KurseModel alleKurseImJahr;
	ListModel<Kurs> alleKurseImJahrProBenutzer;
	int kursJahr = 2017;
	
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
		// Alle Kurse pro kursJahr
		alleKurseImJahr = Kurs.gebeKurse(kursJahr, model);
	}
	
	@Test
	public void notenHinzufuegenTest() {
		try {
			// Testkurs für Benutzer benutzer hinzufügen
			alleKurseImJahr.hinzufuegen("Testkurs", "Testfach", benutzer);
			// Alle Kurse für einen Benutzer pro kursJahr
			alleKurseImJahrProBenutzer = benutzer.gebeGeleiteteKurse(kursJahr, model);
			Kurs testKurs = null;
			for (int i = 0; i < alleKurseImJahrProBenutzer.getSize(); i++)
				if (alleKurseImJahrProBenutzer.getElementAt(i).gebeName().contains("Testkurs"))
					testKurs = alleKurseImJahrProBenutzer.getElementAt(i);
			if (testKurs != null) {
				// Testschüler erstellen und zu Testkurs hinzufügen
				Schueler testSchueler = Schueler.erstelleSchueler("Testschueler", new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01"), model);
				List<Schueler> testSchuelerListe = new ArrayList<Schueler>();
				testSchuelerListe.add(testSchueler);
				testKurs.gebeSchuelerKursModel(model).moveIn(testSchuelerListe);
				// Stand der Noten von vor dem Einfügen holen
				KursNotenModel kursNoteModel = new KursNotenModel(testKurs, model);
				kursNoteModel.schuelerAuswaehlen(testSchueler);
				int oldSize = kursNoteModel.getSize();
				// Testnote erstellen und eintragen
				Note.noteEintragen(
						1, 
						new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01"), 
						1.0,
						"Schriftlich", 
						"Es ist eine Testnote", 
						testKurs, 
						testSchueler, 
						benutzer, 
						model);
				// Notenliste aktualisieren
				KursNotenModel kursNoteModel2 = new KursNotenModel(testKurs, model);
				kursNoteModel2.schuelerAuswaehlen(testSchueler);
				int newSize = kursNoteModel2.getSize();
				// Angelegte Test Daten wieder löschen, Note wird kaskadierend miteglöscht
				Schueler.loescheSchueler(model, testSchueler);
				alleKurseImJahr.loeschen(testKurs);
				// Vergleich der Notenanzahl vor und nach dem Einfügen
				assertEquals("Must be old size + 1", oldSize + 1, newSize);
			} else {
				fail("Testkurs was not created succesfully");
			}	
		} catch (DatenbankFehler | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		/*
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
	*/	
}
