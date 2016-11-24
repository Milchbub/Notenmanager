package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Kurs {

	int id; 
	private String name; 
	private String fach; 
	private int jahr; 
	private int lehrerID;
	
	public Kurs (int id, String name, String fach, int jahr, int lehrerID) {
		this.id = id;
		this.name = name; 
		this.fach = fach;
		this.jahr = jahr; 
		this.lehrerID = lehrerID;
	}

	public String getName() {
		return name;
	}
	
	public String getFach() {
		return fach;
	}
	
	public int getJahr() {
		return jahr;
	}
	
	public int getKlassenleiterID() {
		return lehrerID;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
