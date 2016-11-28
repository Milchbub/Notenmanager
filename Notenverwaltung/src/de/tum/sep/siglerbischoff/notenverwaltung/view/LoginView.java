package de.tum.sep.siglerbischoff.notenverwaltung.view;

public interface LoginView extends View {
	
	String getUser();
	
	String getPassword();
	
	void failure();
	
}
