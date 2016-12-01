package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.*;

import javax.swing.ListModel;

import org.junit.Test;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;



public class SchuelerTest {

	
	
	@Test
	public void schuelerHinzufuegenTest() {
	
		try {
			// DAO erstellen
			DAO dao = DAO.erstelleDAO();
			// Schueler aus DB direkt ueber DAO geholt
			ListModel<Schueler> schuelerListeVorEinfuegen = dao.gebeSchueler();
			
			// Schuelernamen aus schuelerListeVorEinfuegen in Array stecken.
			// Testschueler-Namen am Ende dazu setzen
			String[] vergleichsarray1 = new String[schuelerListeVorEinfuegen.getSize() + 1];
			int i;
			for (i = 0; i < schuelerListeVorEinfuegen.getSize(); i++) {
				vergleichsarray1[i] = schuelerListeVorEinfuegen.getElementAt(i).getName();
			}
			// Testschueler-Namen zu Array dazusetzen
			vergleichsarray1[i] = "Schüler";
			
			// Testschueler in DB ueber MysqlDAO erstellen und einfuegen
			dao.schülerHinzufügen("Schüler", "2001-11-01", "Marsstraße");
			// Erneut die veraenderte SchuelerListe aus DB holen
			ListModel<Schueler> schuelerListeNachEinfuegen = dao.gebeSchueler();
			// Benutzernamen aus benutzerListeNachEinfuegen in Array stecken.
			String[] vergleichsarray2 = new String[schuelerListeNachEinfuegen.getSize()];
			//String[] vergleichsarray2 = new String[benutzerListeNachEinfuegen.getSize()];
			for (i = 0; i < schuelerListeNachEinfuegen.getSize(); i++) {
				vergleichsarray2[i] = schuelerListeNachEinfuegen.getElementAt(i).getName();
			}
			// Testschueler wieder aus DB loeschen
			
			dao.schuelerLoeschen("Schüler");
				
			// Der eigentliche Test (Beide Arrays sollten identisch sein)
			assertArrayEquals("test adding a user", vergleichsarray1 , vergleichsarray2);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
