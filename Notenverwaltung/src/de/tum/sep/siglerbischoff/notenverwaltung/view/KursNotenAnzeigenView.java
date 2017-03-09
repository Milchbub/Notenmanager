package de.tum.sep.siglerbischoff.notenverwaltung.view;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;

public interface KursNotenAnzeigenView extends View {

	static final String COMMAND_NOTE_LOESCHEN = "noteLoeschen";
	
	Note gebeZuLoeschendeNote();
}
