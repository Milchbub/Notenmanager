package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;

public interface KlassenverwaltungView {

	void showKlassenverwaltung(ListModel<Benutzer> listModel);
	
	void addActionListener(ActionListener l);
	
	String getName();
	
	int getSchuljahr();
	
	Benutzer getKlassenlehrer();

	void schliessen();

}
