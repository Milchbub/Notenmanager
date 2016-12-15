package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;

import javax.swing.ListModel;

public class Schueler {
	
	private final int id;
	private String name;
	private Date gebDat;
	
	Schueler (int id, String name, Date gebDat) {
		this.id = id;
		this.name = name;
		this.gebDat = gebDat;
	}
	
	public int gebeId() {
		return id;
	}
	
	public String gebeName() {
		return name;
	}
	
	public Date gebeGebDat() {
		return gebDat;
	}
	
	public ListModel<Klasse> gebeKlassen(int jahr, Model model) throws DatenbankFehler {
		//TODO
		return null;
	}
	
	public ListModel<Kurs> gebeKurse(int jahr, Model model) throws DatenbankFehler {
		//TODO
		return null;
	}
	
	public void setzeName(String neuerName, Model model) throws DatenbankFehler {
		model.gebeDao().schuelerAendern(id, neuerName, gebDat);
		name = neuerName;
	}

	public void setzeGebDat(Date neuesGebDat, Model model) throws DatenbankFehler {
		model.gebeDao().schuelerAendern(id, name, neuesGebDat);
		gebDat = neuesGebDat;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Schueler && ((Schueler) o).id == id;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static SchuelerTableModel gebeSchueler(Model model) throws DatenbankFehler {
		return new SchuelerTableModel(model.gebeDao().gebeSchueler(), model);
	}
	
	public static Schueler erstelleSchueler(String name, Date gebDat, Model model) throws DatenbankFehler {
		return model.gebeDao().schuelerHinzufuegen(name, gebDat);
	}
}
