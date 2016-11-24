package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;

import de.tum.sep.siglerbischoff.notenverwaltung.controller.BenutzerManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.KlassenManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.KursManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.SchuelerdatenManager;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingView;

public interface View {
	
	public static final String COMMAND_SCHUELERDATEN = "schuelerdaten";
	public static final String COMMAND_BENUTZERVERWALTUNG = "benutzerverwaltung";
	public static final String COMMAND_KLASSEN_ANLEGEN = "klassenAnlegen";
	public static final String COMMAND_KURSE_ANLEGEN = "kurseAnlegen";
	
	public static View erstelleView() {
		return new SwingView();
	}

	LoginView getLoginView();

	void loginBenutzer(Benutzer benutzer, ComboBoxModel<Integer> jahre) throws DatenbankFehler;

	void showError(Throwable e);
	
	void showError(String titel, String nachricht);

	void addActionListener(ActionListener l);

	SchuelerdatenView getSchuelerdatenView(SchuelerdatenManager schuelerdatenManager);

	BenutzerverwaltungView getBenutzerverwaltungView(BenutzerManager benutzerManager);

	KlassenverwaltungView getKlassenverwaltungView(KlassenManager klassenManager);

	KursverwaltungView getKursverwaltungView(KursManager kursManager);
}
