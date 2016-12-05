package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Klasse {

	final int id; 
	private String name; 
	private int jahr; 
	private Benutzer klassenlehrer;
	
	public Klasse (int id, String name, int jahr, Benutzer klassenlehrer) {
		this.id = id; 
		this.name = name; 
		this.jahr = jahr; 
		this.klassenlehrer = klassenlehrer;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getJahr() {
		return jahr;
	}
	
	public Benutzer getKlassenlehrer() {
		return klassenlehrer;
	}
	
	public void setName(String neuerName) {
		name = neuerName;
	}
	
	public void setKlassenlehrer(Benutzer neuerKlassenlehrer) {
		klassenlehrer = neuerKlassenlehrer;
	}
	
	
	
	@Override
	public String toString() {
		return name;
	}
}
