package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface BenutzerverwaltungView {

	void showBenutzerverwaltung();
	
	void addActionListener(ActionListener l);

	String getName();
	
	String getLoginName();
	
	String getPass();
	
	boolean getIstAdmin();

	void schliessen();	

}
