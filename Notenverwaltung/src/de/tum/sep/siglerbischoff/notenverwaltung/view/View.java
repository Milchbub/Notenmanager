package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface View {
	
	public static final String COMMAND_SCHUELERDATEN = "schuelerdaten";
	public static final String COMMAND_BENUTZERVERWALTUNG = "benutzerverwaltung";
	public static final String COMMAND_KLASSEN_ANLEGEN = "klassenAnlegen";
	public static final String COMMAND_KURSE_ANLEGEN = "kurseAnlegen";
	
	void zeigen();
	
	void schliessen();
	
	void showError(String titel, String nachricht);

	default void showError(Throwable e) {
		showError("Fehler", e.getMessage());
	}

	void addActionListener(ActionListener l);
}
