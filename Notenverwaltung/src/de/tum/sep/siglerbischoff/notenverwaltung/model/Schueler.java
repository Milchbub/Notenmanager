package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Schueler {
	
	final int id;
	private String name; 
	
	public Schueler (int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
}
