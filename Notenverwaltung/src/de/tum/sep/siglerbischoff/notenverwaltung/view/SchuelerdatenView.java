package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface SchuelerdatenView {

	void showSchuelerdaten();
	
	void addActionListener(ActionListener l);

	String getName();

	String getGebDat();
	
	String getAdresse();

	void schliessen();

}
