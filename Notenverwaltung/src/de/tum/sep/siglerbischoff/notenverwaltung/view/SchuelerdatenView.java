package de.tum.sep.siglerbischoff.notenverwaltung.view;

public interface SchuelerdatenView extends View {
	
	public static final String COMMAND_NEUER_SCHUELER = "neuerSchueler";
	public static final int NEUER_SCHUELER_NAME = 0;
	public static final int NEUER_SCHUELER_GEBDAT = 1;
	public static final int NEUER_SCHUELER_ADRESSE = 2;

	String[] getNeuerSchueler();

}
