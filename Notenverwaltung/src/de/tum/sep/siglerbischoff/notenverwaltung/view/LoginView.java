package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Login;

public interface LoginView extends View {

	Login gebeLogin();
	
	void failure();
	
	void addActionListener(ActionListener l);
}
