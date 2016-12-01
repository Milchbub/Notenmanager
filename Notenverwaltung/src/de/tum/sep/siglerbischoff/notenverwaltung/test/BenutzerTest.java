package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertArrayEquals;

import javax.swing.ListModel;

import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;

public class BenutzerTest {

	// Es wird verglichen, ob der Output von gebeBenutzer() in DAO mit dem Output von gebeBenutzer() aus
	// der Benutzer Klasse uebereinstimmt. Verglichen werden die Werte Name und Id. Die Daten werden
	// dazu zweimal voneinander getrennt �ber die jeweilige Methode aus der DB geholt.
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
	
	// Es wird getestet, ob man Fehlerfrei einen Benutzer anlegen kann (DB User und Eintrag in User Tabelle).
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
			vergleichsarray1[i] = "Test-Benutzer";
			
			// Test-Benutzer in DB ueber MysqlDAO erstellen und einfuegen
			dao.benutzerAnlegen("Test-Benutzer", "Test", "Test", false);
			// Erneut die veraenderte BenutzerListe aus DB holen
			ListModel<Benutzer> benutzerListeNachEinfuegen = dao.gebeBenutzer();
			// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
			String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
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
		
}