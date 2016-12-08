package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface BenutzerdatenView extends View {
	
	public static final String COMMAND_SCHLIESSEN = "schliessen";
	public static final String COMMAND_NEU = "neu";
	public static final String COMMAND_NEU_FERTIG = "neuFertig";
	
	void addActionListener(ActionListener l);
	
	void removeActionListener(ActionListener l);

	void neu();
	
	String gebeNeuLoginName();
	
	String gebeNeuName();
	
	String gebeNeuPasswort();
	
	boolean gebeNeuIstAdmin();
	
	void update();

}
