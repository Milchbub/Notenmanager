package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface LoginView {
	
	void addLoginListener(ActionListener l); 
	
	void login();
	
	String getUser();
	
	String getPassword();
	
	void success();
	
	void failure();
	
}
