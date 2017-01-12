package de.tum.sep.siglerbischoff.notenverwaltung.view;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerKursModel;

public interface KursverwaltungView extends View {

	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NEU = "neu";
	static final String COMMAND_BEARBEITEN = "bearbeiten";
	static final String COMMAND_LOESCHEN = "löschen";
	static final String COMMAND_NEU_FERTIG = "neuFertig";
	static final String COMMAND_BEARBEITEN_FERTIG = "bearbeitenFertig";

	void bearbeiten(Kurs kurs, ListModel<Benutzer> lehrer, SchuelerKursModel schueler);
	
	Kurs gebeAusgewaehlt();
	
	String gebeNeuName();
	
	String gebeNeuFach();
	
	Benutzer gebeNeulehrer();
}
