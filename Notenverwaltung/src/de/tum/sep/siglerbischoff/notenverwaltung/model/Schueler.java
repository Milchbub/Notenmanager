package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;

public class Schueler extends Entity {
	
	final int id;
	private String name;
	private Date gebDat;
	
	public Schueler (int id, String name, Date gebDat) {
		this.id = id;
		this.name = name;
		this.gebDat = gebDat;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getGebDat() {
		return gebDat;
	}

	@Override
	public Object[] getData() {
		return new Object[]{id, name, gebDat};
	}
	
}
