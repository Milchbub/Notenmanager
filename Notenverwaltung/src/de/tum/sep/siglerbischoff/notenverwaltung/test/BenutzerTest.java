package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertArrayEquals;

import java.util.Properties;
import java.util.Random;

import javax.swing.ListModel;

import org.junit.Before;
import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;

public class BenutzerTest {	

	/* BLOCK KOMMENTAR UEBER GANZE KLASSE!!!
	 * TODO AUF AKTUELLE STRUKTUR UMAENDERN
	 
	private DAO dao;
	
	@Before
	public void initialisieren() throws DatenbankFehler {
		dao = DAO.erstelleDAO();
		Properties props = new Properties();
		props.setProperty("dbhost", "127.0.0.1");
		props.setProperty("dbname", "Notenmanager");
		dao.passwortPruefen("michi", "test", props);
	}

	// Es wird verglichen, ob der Output von gebeBenutzer() in DAO mit dem Output von gebeBenutzer() aus
	// der Benutzer Klasse uebereinstimmt. Verglichen werden die Werte Name und Id. Die Daten werden
	// dazu zweimal voneinander getrennt über die jeweilige Methode aus der DB geholt.
	@Test
	public void gebeBenutzerTest() {
		try {
			// Benutzer aus DB direkt ueber DAO geholt
			ListModel<Benutzer> benutzerUeberDAO = dao.gebeBenutzer();
			// Vergleichsarray 1 erstellen
			String[][] vergleichsarray1 = new String[benutzerUeberDAO.getSize()][2];
			for (int i = 0; i < benutzerUeberDAO.getSize(); i++) {
				vergleichsarray1[i][0] = String.valueOf(benutzerUeberDAO.getElementAt(i).gebeId());
				vergleichsarray1[i][1] = benutzerUeberDAO.getElementAt(i).gebeName();
			}
			
			// Benutzer ueber Benutzermethode aus DB geholt
			// Benutzermethode gibt ListModel zurueck
			// Daher werden die List und ListModel Inhalte ueber die Namen verglichen
			ListModel<Benutzer> benutzerUeberBenutzer = dao.gebeBenutzer();
			// Vergleichsarray 2 erstellen
			String[][] vergleichsarray2 = new String[benutzerUeberBenutzer.getSize()][2];
			for (int i = 0; i < benutzerUeberBenutzer.getSize(); i++) {
				vergleichsarray2[i][0] = String.valueOf(benutzerUeberBenutzer.getElementAt(i).gebeId());
				vergleichsarray2[i][1] = benutzerUeberBenutzer.getElementAt(i).gebeName();
			}
			
			// Der eigentliche Test (es werden die Inhalte der 2 Arrays verglichen, Id und Name im genau gesagt)
			assertArrayEquals("compare user lists", vergleichsarray1 , vergleichsarray2);
			
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Es wird getestet, ob man fehlerfrei einen Benutzer anlegen kann (DB User und Eintrag in User Tabelle).
	// Danach wird der User auch wieder geloescht, was die Loeschroutine mitueberprueft. Verglichen werden
	// zwei Arrays mit den Benutzernamen darin. Das Array vor dem User anlegen (mit manuell hinzugefuegtem
	// Testuser) muss gleich dem Array nach dem User anlegen (frisch aus DB geholt) sein.
	@Test
	public void benutzerAnlegenUndLoeschenTest() {
	
		try {
			// DAO erstellen
			DAO dao = DAO.erstelleDAO();
			Properties props = new Properties();
			props.setProperty("dbhost", "127.0.0.1");
			props.setProperty("dbname", "Notenmanager");
			dao.passwortPruefen("michi", "test", props);
			// Benutzer aus DB direkt ueber DAO geholt
			ListModel<Benutzer> benutzerListeVorEinfuegen = dao.gebeBenutzer();
			
			// Benutzernamen aus benutzerListeVorEinfuegen in Array stecken.
			// Testbenutzer-Namen am Ende dazu setzen
			String[] vergleichsarray1 = new String[benutzerListeVorEinfuegen.getSize() + 1];
			int i;
			for (i = 0; i < benutzerListeVorEinfuegen.getSize(); i++) {
				vergleichsarray1[i] = benutzerListeVorEinfuegen.getElementAt(i).gebeName();
			}
			// Testbenutzer-Namen zu Array dazusetzen
			vergleichsarray1[i] = "Test";
			
			// Test-Benutzer in DB ueber MysqlDAO erstellen und einfuegen
			Benutzer test = dao.benutzerAnlegen("Test", "Test", "Test", false);
			// Erneut die veraenderte BenutzerListe aus DB holen
			ListModel<Benutzer> benutzerListeNachEinfuegen = dao.gebeBenutzer();
			// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
			String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
			//String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
			for (i = 0; i < benutzerListeNachEinfuegen.getSize(); i++) {
				vergleichsarray2[i] = benutzerListeNachEinfuegen.getElementAt(i).gebeName();
			}
			// Testbenutzer wieder aus DB loeschen
			dao.benutzerLoeschen(test);
				
			// Der eigentliche Test (Beide Arrays sollten identisch sein)
			assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
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
	 
	 BLOCK KOMMENTAR ENDE */
}