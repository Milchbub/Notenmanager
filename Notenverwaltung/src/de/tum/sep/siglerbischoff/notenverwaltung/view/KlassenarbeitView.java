package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.util.Date;
import java.util.List;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

public interface KlassenarbeitView extends View {

	static final String COMMAND_ABBRECHEN = "abbrechen";
	static final String COMMAND_NOTEN_EINTRAGEN = "notenEintragen";
	
	List<Integer> gebeNeuWerte();
	Date gebeNeuErstellungsdatum();
	Double gebeNeuGewichtung();
	List<Schueler> gebeNeuSchueler();
	Kurs gebeNeuKurs();
	String gebeNeuArt(); 
}
