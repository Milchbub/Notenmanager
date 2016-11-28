package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Benutzer {

	int id;
	private String name;
	private boolean istAdmin;
	
	public Benutzer(int id, String name, boolean istAdmin) {
		this.id = id;
		this.name = name;
		this.istAdmin = istAdmin;
	}

	public String getName() {
		return name;
	}
	
	public boolean istAdmin() {
		return istAdmin;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
