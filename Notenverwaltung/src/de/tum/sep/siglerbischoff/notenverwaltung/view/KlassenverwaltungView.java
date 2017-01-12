package de.tum.sep.siglerbischoff.notenverwaltung.view;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerKlasseModel;

public interface KlassenverwaltungView extends View {
	
	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NEU = "neu";
	static final String COMMAND_BEARBEITEN = "bearbeiten";
	static final String COMMAND_LOESCHEN = "löschen";
	static final String COMMAND_NEU_FERTIG = "neuFertig";
	static final String COMMAND_BEARBEITEN_FERTIG = "bearbeitenFertig";

	void bearbeiten(Klasse klasse, ListModel<Benutzer> lehrer, SchuelerKlasseModel schueler);
	
	Klasse gebeAusgewaehlt();
	
	String gebeNeuName();
	
	Benutzer gebeNeuKlassenlehrer();
}
