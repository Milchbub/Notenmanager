package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface BenutzerdatenView extends View {
	
	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NEU = "neu";
	static final String COMMAND_NEU_FERTIG = "neuFertig";
	
	void addActionListener(ActionListener l);
	
	void removeActionListener(ActionListener l);

	void neu();
	
	String gebeNeuLoginName();
	
	String gebeNeuName();
	
	char[] gebeNeuPasswort();
	
	boolean gebeNeuIstAdmin();
}
