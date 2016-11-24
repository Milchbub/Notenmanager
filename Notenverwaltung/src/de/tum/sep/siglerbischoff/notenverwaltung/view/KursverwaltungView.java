package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;

public interface KursverwaltungView {

	void showKursverwaltung(ListModel<Benutzer> lehrer);
	
	void addActionListener(ActionListener l);
	
	String getName();
	
	String getFach();
	
	int getSchuljahr();
	
	Benutzer getLehrer();

	void schliessen();

}
