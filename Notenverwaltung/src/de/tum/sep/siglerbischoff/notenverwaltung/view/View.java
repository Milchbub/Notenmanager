package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingView;

public interface View {

	LoginView getLoginView();

	void loginBenutzer(Benutzer benutzer) throws DatenbankFehler;

	void showError(Throwable e);
	
	void showError(String titel, String nachricht);
	
	public static View erstelleView() {
		return new SwingView();
	}

	void addActionListener(ActionListener l);
}
