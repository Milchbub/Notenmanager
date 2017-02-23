package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import de.tum.sep.siglerbischoff.notenverwaltung.model.*;

public class NotenTest {
	Model model;
	Benutzer benutzer;
	KurseModel alleKurseImJahr;
	ListModel<Kurs> alleKurseImJahrProBenutzer;
	int kursJahr = 2017;
	Kurs testKurs;
	Schueler testSchueler;
	
	@Before
	public void initialisieren() throws DatenbankFehler, ParseException, IOException {
		model = new Model();
		Login login = new Login("sigl", new char[] {'t', 'u', '2' , '0', '1', '7'});
		benutzer = model.passwortPruefen(login);
		// Alle Kurse pro kursJahr
		alleKurseImJahr = Kurs.gebeKurse(kursJahr, model);
		// Testkurs für Benutzer benutzer hinzufügen
		alleKurseImJahr.hinzufuegen("Testkurs", "Testfach", benutzer);
		// Alle Kurse für Benutzer benutzer pro kursJahr
		alleKurseImJahrProBenutzer = benutzer.gebeGeleiteteKurse(kursJahr, model);
		testKurs = null;
		for (int i = 0; i < alleKurseImJahrProBenutzer.getSize(); i++)
			if (alleKurseImJahrProBenutzer.getElementAt(i).gebeName().contains("Testkurs"))
				testKurs = alleKurseImJahrProBenutzer.getElementAt(i);
		if (testKurs != null) {
			// Testschüler erstellen und zu Testkurs hinzufügen
			testSchueler = Schueler.erstelleSchueler("Testschueler", new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01"), model);
			List<Schueler> testSchuelerListe = new ArrayList<Schueler>();
			testSchuelerListe.add(testSchueler);
			testKurs.gebeSchuelerKursModel(model).moveIn(testSchuelerListe);
		} else {
			assumeTrue(false);
			fail("Der Kurs 'Testkurs' konnte nicht erzeugt werden!");
		}
	}
	
	@Test
	public void notenHinzufuegenTest() {
		try {
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
			// Vergleich der Notenanzahl vor und nach dem Einfügen
			assertEquals("Must be old size + 1", oldSize + 1, newSize);
		} catch (DatenbankFehler | ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

	@Test
	public void unerlaubterNotenwert(){
		try {
			// Testnote erstellen und eintragen
			Note.noteEintragen(
					7, 
					new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01"), 
					1.0,
					"Schriftlich", 
					"Es ist eine Testnote", 
					testKurs, 
					testSchueler, 
					benutzer, 
					model);
			fail("Note 7 konnte in Datenbank eingetragen werden!");
		} catch (DatenbankFehler | ParseException e) {
			assertEquals(e.getClass(), DatenbankFehler.class);
		}
	}
	
	@After
	public void aufraeumen() throws Exception {
		try {
			Schueler.loescheSchueler(model, testSchueler);
			alleKurseImJahr.loeschen(testKurs);
		} catch (DatenbankFehler e) {
			throw new Exception("Fehler in @After Methode aufraeumen()!", e);
		}
	}
}