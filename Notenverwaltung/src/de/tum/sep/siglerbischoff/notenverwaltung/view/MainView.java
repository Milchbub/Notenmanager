package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingMainView;

public interface MainView extends View {
	
	public static final String COMMAND_SCHUELERDATEN = "schuelerdaten";
	public static final String COMMAND_BENUTZERVERWALTUNG = "benutzerverwaltung";
	public static final String COMMAND_KLASSEN_ANLEGEN = "klassenAnlegen";
	public static final String COMMAND_KURSE_ANLEGEN = "kurseAnlegen";

	void loginBenutzer(Benutzer benutzer, ComboBoxModel<Integer> jahre, ListModel<Klasse> list, ListModel<Kurs> list2) throws DatenbankFehler;

	LoginView getLoginView();

	SchuelerdatenView getSchuelerdatenView(TableModel schueler);

	BenutzerverwaltungView getBenutzerverwaltungView();

	KlassenverwaltungView getKlassenverwaltungView();

	KursverwaltungView getKursverwaltungView();
	
	void addActionListener(ActionListener l);
	
	public static MainView erstelleMainView() {
		return new SwingMainView();
	}
}
