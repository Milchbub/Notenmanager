package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Kurs {

	int id; 
	private String name; 
	private String fach; 
	private int jahr; 
	private Benutzer kursleiter;
	
	public Kurs (int id, String name, String fach, int jahr, Benutzer kursleiter) {
		this.id = id;
		this.name = name; 
		this.fach = fach;
		this.jahr = jahr; 
		this.kursleiter = kursleiter;
	}
	
	public int getId() {
		return id;
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
	
	public Benutzer getKursleiter() {
		return kursleiter;
	}
	
	public void setName(String neuerName) {
		name = neuerName;
	}
	
	public void setFach(String neuesFach) {
		fach = neuesFach;
	}
	
	public void setKursleiter(Benutzer neuerKursleiter) {
		kursleiter = neuerKursleiter;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
