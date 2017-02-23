package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import javax.swing.ListModel;
import org.junit.Before;
import org.junit.Test;
import de.tum.sep.siglerbischoff.notenverwaltung.model.*;

public class BenutzerTest {	
	 
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
	
	// Es wird getestet, ob man fehlerfrei einen Benutzer anlegen kann (DB User und Eintrag in User Tabelle).
	// Danach wird der User auch wieder geloescht. Verglichen werden die BenutzerTableModel Größen vor und
	// nach dem Einfügen.
	@Test
	public void benutzerAnlegenTest() {
		try {
			// Bisherige Benutzer aus DB geholt
			BenutzerTableModel benutzerTableModel = Benutzer.gebeBenutzer(model, benutzer);
			int oldSize = benutzerTableModel.getSize();
			
			// Test-Benutzer in DB einfuegen
// Zeile unten Kommentar entfernen, sobald Privileg-Probleme in DB geloest. Dann laueft der Test!			
//			benutzerTableModel.hinzufuegen("Test", "Test", new char[] {'T','e','s','t'}, false);
			
			// Testbenutzer wieder aus DB loeschen
			Benutzer test = benutzerTableModel.getElementAt(benutzerTableModel.getSize() - 1);
// Zeile unten Kommentar entfernen, sobald Privileg-Probleme in DB geloest. Dann laueft der Test!
//			Benutzer.loescheBenutzer(model, test);
			
			// Der eigentliche Test (Beide Size Werte sollten identisch sein)
			assertEquals("Must be old size + 1", oldSize + 1, benutzerTableModel.getSize());
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error accessing database within testing!");
		}	
	}
	
	// Debug method
	void printBenutzerTableModel(BenutzerTableModel benutzerTableModel) {
		System.out.println("BenutzerTableModel: ");
		System.out.println("getSize(): " + benutzerTableModel.getSize());
		System.out.println("getRowCount(): " + benutzerTableModel.getRowCount());
		for (int i = 0; i < benutzerTableModel.getSize(); i++) {
			System.out.println("Position=" + i + 
					", LoginName=" + 
					benutzerTableModel.getElementAt(i).gebeLoginName());
		}
	}
	
	/**************
	
	// Es wird getestet, ob man fehlerfrei 100 Benutzer (erzeugt durch Zufallsstrings der Länge 16) 
	//anlegen kann (DB User und Eintrag in User Tabelle). Danach werden die 100 User auch wieder geloescht.
	// Verglichen werden zwei Arrays mit den Benutzernamen darin.
	// Das Array vor dem User anlegen (mit manuell hinzugefuegten 100 Randomusern)
	// muss gleich dem Array nach dem User anlegen (frisch aus DB geholt) sein.
	@Test
	public void HundertbenutzerAnlegenTest() throws DatenbankFehler {

		// DAO erstellen
		DAO dao = DAO.erstelleDAO();
		Properties props = new Properties();
		props.setProperty("dbhost", "127.0.0.1");
		props.setProperty("dbname", "Notenmanager");
		dao.passwortPruefen("michi", "test", props);
		
		// Benutzer aus DB direkt ueber DAO geholt
		ListModel<Benutzer> benutzerListeVorEinfuegen = dao.gebeBenutzer();
		
		Benutzer[] vergleichsarray1 = new Benutzer[1000 + benutzerListeVorEinfuegen.getSize()];
		Benutzer[] vergleichsarray2 = new Benutzer[1000 + benutzerListeVorEinfuegen.getSize()];
	
		// Benutzernamen aus benutzerListeVorEinfuegen in Array stecken.
		// Testbenutzer-Namen am Ende dazu setzen
		for (int i = 0; i < benutzerListeVorEinfuegen.getSize(); i++) {
			vergleichsarray1[i] = benutzerListeVorEinfuegen.getElementAt(i);
		}

		Random random = new Random();
		for(int i = benutzerListeVorEinfuegen.getSize(); i < vergleichsarray1.length; i++) {
			String allowedChars ="0123456789abcdefghijklmnopqrstuvwxyzöäü";
			String randomString = generateRandomString(allowedChars, random);
			
			// Testbenutzer-Namen zu Array dazusetzen
			try {
				// Test-Benutzer in DB ueber MysqlDAO erstellen und einfuegen
				vergleichsarray1[i] = dao.benutzerAnlegen(randomString +"login", randomString, randomString +"pass", false);		
			} catch (DatenbankFehler e) {
				e.printStackTrace();
			}
		}
		
		// Erneut die veraenderte BenutzerListe aus DB holen
		ListModel<Benutzer> benutzerListeNachEinfuegen = dao.gebeBenutzer();

		// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
		for(int j=0; j < vergleichsarray2.length; j++) {
			vergleichsarray2[j] = benutzerListeNachEinfuegen.getElementAt(j);
		}
		
		for(int j = benutzerListeVorEinfuegen.getSize(); j < vergleichsarray1.length; j++) {
			dao.benutzerLoeschen(vergleichsarray1[j]);
		}

		// Der eigentliche Test (Beide Arrays sollten identisch sein)
		//assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
		assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
		
		
	}
	
	
	
	private static String generateRandomString(String allowedChars,	Random random){
			int max = allowedChars.length();
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i<12; i++) {
			int value = random.nextInt(max);
			buffer.append(allowedChars.charAt(value));
			}
			return buffer.toString();
			} 
		
	
	//Es wird getestet, ob ein Benutzer mit leerem String als Name/Passwort angelegt werden kann.
	//Dies sollte nicht möglich sein und zu einer Exeption führen (Test schlägt allerdings noch fehl)
	
	@Test(expected=IllegalArgumentException.class)
	 public void leererStringTest() {
		Benutzer benutzer = null;
		try {
			benutzer = dao.benutzerAnlegen("", "Test", "", false);
		} catch (DatenbankFehler e) {
			e.printStackTrace();
		} finally {
			try {
				if(benutzer != null) {
					dao.benutzerLoeschen(benutzer);
				}
			} catch (DatenbankFehler e) {
				e.printStackTrace();
			}
		}
	 }
	
	@Test(expected=IllegalArgumentException.class)
	 public void NullStringTest() {
		Benutzer benutzer = null;
		try {
			benutzer = dao.benutzerAnlegen(null, "Test", "", false);
		} catch (DatenbankFehler e) {
			e.printStackTrace();
		} finally {
			try {
				if(benutzer != null) {
					dao.benutzerLoeschen(benutzer);
				}
			} catch (DatenbankFehler e) {
				e.printStackTrace();
			}
		}
	 }
	
	
	
	//Testet, ob ein Benutzer mit gleichem LoginNamen zweimal hintereinander in die DB eingefügt werden kann.
	@Test(expected=DatenbankFehler.class)
	 public void ZweimalGleicherLoginNameTest() throws DatenbankFehler {
		DAO dao = DAO.erstelleDAO();
		try {
			Properties props = new Properties();
			props.setProperty("dbhost", "127.0.0.1");
			props.setProperty("dbname", "Notenmanager");
			dao.passwortPruefen("michi", "test", props);
			dao.benutzerAnlegen("Test1", "Testlogin", "", false);
			dao.benutzerAnlegen("Test2", "Testlogin", "", false);
		} finally {
			dao.benutzerLoeschen("Testlogin");
		}
	 }
 *********/
}