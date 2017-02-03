package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.util.Date;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

public interface NotenHinzufuegenView extends View {

	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NOTE_EINTRAGEN = "noteEintragen";
	
	int gebeNeuWert();
	Date gebeNeuErstellungsdatum();
	Double gebeNeuGewichtung();
	Schueler gebeNeuSchueler();
	Kurs gebeNeuKurs();
	String gebeNeuArt();
	String gebeNeuKommentar(); 
	
}
