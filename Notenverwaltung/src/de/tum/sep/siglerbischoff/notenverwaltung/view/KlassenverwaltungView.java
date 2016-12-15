package de.tum.sep.siglerbischoff.notenverwaltung.view;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;

public interface KlassenverwaltungView extends View {
	
	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NEU = "neu";
	static final String COMMAND_BEARBEITEN = "bearbeiten";
	static final String COMMAND_NEU_FERTIG = "neuFertig";
	static final String COMMAND_BEARBEITEN_FERTIG = "bearbeitenFertig";

	void bearbeiten(Klasse klasse, ListModel<Benutzer> lehrer);
	
	Klasse gebeAusgewaehlt();
	
	String gebeNeuName();
	
	Benutzer gebeNeuKlassenlehrer();
}
