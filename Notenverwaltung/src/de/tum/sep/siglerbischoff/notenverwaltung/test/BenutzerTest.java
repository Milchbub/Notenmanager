package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.*;

import java.util.Random;

import javax.swing.ListModel;

import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;

public class BenutzerTest {

	// Es wird verglichen, ob der Output von gebeBenutzer() in DAO mit dem Output von gebeBenutzer() aus
	// der Benutzer Klasse uebereinstimmt. Verglichen werden die Werte Name und Id. Die Daten werden
	// dazu zweimal voneinander getrennt über die jeweilige Methode aus der DB geholt.
	@Test
	public void gebeBenutzerTest() {
		try {
			// DAO erstellen
			DAO dao = DAO.erstelleDAO();
			// Benutzer aus DB direkt ueber DAO geholt
			ListModel<Benutzer> benutzerUeberDAO = dao.gebeBenutzer();
			// Vergleichsarray 1 erstellen
			String[][] vergleichsarray1 = new String[benutzerUeberDAO.getSize()][2];
			for (int i = 0; i < benutzerUeberDAO.getSize(); i++) {
				vergleichsarray1[i][0] = String.valueOf(benutzerUeberDAO.getElementAt(i).getId());
				vergleichsarray1[i][1] = benutzerUeberDAO.getElementAt(i).getName();
			}
			
			// Benutzer ueber Benutzermethode aus DB geholt
			// Benutzermethode gibt ListModel zurueck
			// Daher werden die List und ListModel Inhalte ueber die Namen verglichen
			ListModel<Benutzer> benutzerUeberBenutzer = dao.gebeBenutzer();
			// Vergleichsarray 2 erstellen
			String[][] vergleichsarray2 = new String[benutzerUeberBenutzer.getSize()][2];
			for (int i = 0; i < benutzerUeberBenutzer.getSize(); i++) {
				vergleichsarray2[i][0] = String.valueOf(benutzerUeberBenutzer.getElementAt(i).getId());
				vergleichsarray2[i][1] = benutzerUeberBenutzer.getElementAt(i).getName();
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
			// Benutzer aus DB direkt ueber DAO geholt
			ListModel<Benutzer> benutzerListeVorEinfuegen = dao.gebeBenutzer();
			
			// Benutzernamen aus benutzerListeVorEinfuegen in Array stecken.
			// Testbenutzer-Namen am Ende dazu setzen
			String[] vergleichsarray1 = new String[benutzerListeVorEinfuegen.getSize() + 1];
			int i;
			for (i = 0; i < benutzerListeVorEinfuegen.getSize(); i++) {
				vergleichsarray1[i] = benutzerListeVorEinfuegen.getElementAt(i).getName();
			}
			// Testbenutzer-Namen zu Array dazusetzen
			vergleichsarray1[i] = "Test";
			
			// Test-Benutzer in DB ueber MysqlDAO erstellen und einfuegen
			dao.benutzerAnlegen("Test", "Test", "Test", false);
			// Erneut die veraenderte BenutzerListe aus DB holen
			ListModel<Benutzer> benutzerListeNachEinfuegen = dao.gebeBenutzer();
			// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
			String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
			//String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
			for (i = 0; i < benutzerListeNachEinfuegen.getSize(); i++) {
				vergleichsarray2[i] = benutzerListeNachEinfuegen.getElementAt(i).getName();
			}
			// Testbenutzer wieder aus DB loeschen
			dao.benutzerLoeschen("Test");
				
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
	public void HundertbenutzerAnlegenTest() {
		String[] vergleichsarray1= null;
		String[] vergleichsarray2= null;
		for (int j=0; j < 1; j++){
			String allowedChars ="0123456789abcdefghijklmnopqrstuvwxyz";
			Random random = new Random();
			String randomString = generateRandomString(allowedChars, random);
		
			try {
				// DAO erstellen
				DAO dao = DAO.erstelleDAO();
				// Benutzer aus DB direkt ueber DAO geholt
				ListModel<Benutzer> benutzerListeVorEinfuegen = dao.gebeBenutzer();
			
				// Benutzernamen aus benutzerListeVorEinfuegen in Array stecken.
				// Testbenutzer-Namen am Ende dazu setzen
				vergleichsarray1 = new String[benutzerListeVorEinfuegen.getSize() + 1];
				int i;
				for (i = 0; i < benutzerListeVorEinfuegen.getSize(); i++) {
					vergleichsarray1[i] = benutzerListeVorEinfuegen.getElementAt(i).getName();
				}
				// Testbenutzer-Namen zu Array dazusetzen
				vergleichsarray1[i] = randomString;
			
				// Test-Benutzer in DB ueber MysqlDAO erstellen und einfuegen
				dao.benutzerAnlegen(randomString, randomString +"login", randomString +"pass", false);
				// Erneut die veraenderte BenutzerListe aus DB holen
				ListModel<Benutzer> benutzerListeNachEinfuegen = dao.gebeBenutzer();
				// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
				vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
				//String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
				for (i = 0; i < benutzerListeNachEinfuegen.getSize(); i++) {
					vergleichsarray2[i] = benutzerListeNachEinfuegen.getElementAt(i).getName();
				}
				// Testbenutzer wieder aus DB loeschen
				dao.benutzerLoeschen(randomString +"login");
		
				// Der eigentliche Test (Beide Arrays sollten identisch sein)
				//assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
			} catch (DatenbankFehler e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
		
		
	}
	
	
	
	private static String generateRandomString(String allowedChars,	Random random){
			int max = allowedChars.length();
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i<16; i++) {
			int value = random.nextInt(max);
			buffer.append(allowedChars.charAt(value));
			}
			return buffer.toString();
			} 
		
	
	//Es wird getestet, ob ein Benutzer mit leerem String als Name/Passwort angelegt werden kann.
	//Dies sollte nicht möglich sein und zu einer Exeption führen (Test schlägt allerdings noch fehl)
	
	@Test(expected=DatenbankFehler.class)
	 public void leererStringTest() {
		try {
			DAO dao = DAO.erstelleDAO();
			dao.benutzerAnlegen("", "Test", "", false);
			dao.benutzerLoeschen("Test");
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	@Test(expected=DatenbankFehler.class)
	 public void NullStringTest() {
		try {
			DAO dao = DAO.erstelleDAO();
			dao.benutzerAnlegen(null, "Test", "", false);
			dao.benutzerLoeschen("Test");
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }
	
	
	
	//Testet, ob ein Benutzer mit gleichem LoginNamen zweimal hintereinander in die DB eingefügt werden kann.
	@Test(expected=DatenbankFehler.class)
	 public void ZweimalGleicherLoginNameTest() throws DatenbankFehler {
			DAO dao = DAO.erstelleDAO();
			dao.benutzerAnlegen("Test1", "Testlogin", "", false);
			dao.benutzerAnlegen("Test2", "Testlogin", "", false);
			
	 }
	
	//Löscht den zuvor eingefügten Benutzer "Test1" wieder
	@Test
	public void ZweimalGleicherLoginNameLöschen() throws DatenbankFehler {
		DAO dao = DAO.erstelleDAO();
		dao.benutzerLoeschen("Testlogin");
		
 }
	
}