package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class DatenbankFehler extends Exception {

	DatenbankFehler(Throwable e) {
		super(e);
	}

	public DatenbankFehler(String nachricht) {
		super(nachricht);
	}

	private static final long serialVersionUID = 2967306509973323802L;
	
}
