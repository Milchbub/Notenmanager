package de.tum.sep.siglerbischoff.notenverwaltung.view;

public interface BenutzerdatenView extends View {
	
	static final String COMMAND_SCHLIESSEN = "schliessen";
	static final String COMMAND_NEU = "neu";
	static final String COMMAND_NEU_FERTIG = "neuFertig";

	void neu();
	
	String gebeNeuLoginName();
	
	String gebeNeuName();
	
	char[] gebeNeuPasswort();
	
	boolean gebeNeuIstAdmin();
}
