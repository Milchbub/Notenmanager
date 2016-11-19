package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Klasse {

	int id; 
	private String name; 
	private int jahr; 
	private int klassenlehrerID;
	
	public Klasse (int id, String name, int jahr, int klassenlehrerID) {
		this.id = id; 
		this.name = name; 
		this.jahr = jahr; 
		this.klassenlehrerID = klassenlehrerID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getJahr() {
		return jahr;
	}
	
	public int getKlassenlehrerID() {
		return klassenlehrerID;
	}
}
