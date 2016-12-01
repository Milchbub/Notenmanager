package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface LoginView extends View {
	
	String getUser();
	
	String getPassword();
	
	void failure();
	
	void addActionListener(ActionListener l);
}
