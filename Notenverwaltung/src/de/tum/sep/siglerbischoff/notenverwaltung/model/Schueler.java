package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;
import java.util.List;

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
	
	public List<Kurs> gebeKurse(int jahr, Model model) throws DatenbankFehler {
		return model.gebeDao().gebeKurse(this, jahr);
	}
	
	public void setzeName(String neuerName, Model model) throws DatenbankFehler {
		model.gebeDao().schuelerAendern(this, neuerName, gebDat);
		name = neuerName;
	}

	public void setzeGebDat(Date neuesGebDat, Model model) throws DatenbankFehler {
		model.gebeDao().schuelerAendern(this, name, neuesGebDat);
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
		return new SchuelerTableModel(model.gebeDao().gebeAlleSchueler(), model);
	}
	
	public static Schueler erstelleSchueler(String name, Date gebDat, Model model) throws DatenbankFehler {
		return model.gebeDao().schuelerHinzufuegen(name, gebDat);
	}
}
