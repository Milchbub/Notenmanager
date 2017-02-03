package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class Kurs {

	private final String name; 
	private final int jahr; 
	private String fach; 
	private Benutzer kursleiter;
	
	Kurs (String name, int jahr, String fach, Benutzer kursleiter) {
		this.name = name; 
		this.jahr = jahr; 
		this.fach = fach;
		this.kursleiter = kursleiter;
	}

	public String gebeName() {
		return name;
	}
	
	public String gebeFach() {
		return fach;
	}
	
	public int gebeJahr() {
		return jahr;
	}
	
	public Benutzer gebeKursleiter() {
		return kursleiter;
	}

	public ListModel<Schueler> gebeSchueler(Model model) throws DatenbankFehler {
		List<Schueler> schueler = model.gebeDao().gebeSchueler(this);
		DefaultListModel<Schueler> list = new DefaultListModel<>();
		for(Schueler s : schueler) {
			list.addElement(s);
		}
		return list;
	}
	
	public SchuelerKursModel gebeSchuelerKursModel(Model model) throws DatenbankFehler {
		return new SchuelerKursModel(this, model);
	}
	
	void setzeFach(String fach, Model model) throws DatenbankFehler {
		model.gebeDao().kursAendern(this, fach, kursleiter);
		this.fach = fach;
	}
	
	void setzeKursleiter(Benutzer kursleiter, Model model) throws DatenbankFehler {
		model.gebeDao().kursAendern(this, fach, kursleiter);
		this.kursleiter = kursleiter;
	}
	
	void schuelerHinzufuegen(Schueler schueler, Model model) throws DatenbankFehler {
		model.gebeDao().zuKursHinzufuegen(this, schueler);
	}
	
	void schuelerEntfernen(Schueler schueler, Model model) throws DatenbankFehler{
		model.gebeDao().ausKursLoeschen(this, schueler);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Kurs && 
			((Kurs) o).name.equalsIgnoreCase(name) && ((Kurs) o).jahr == jahr;
	}
	
	@Override
	public int hashCode() {
		return name.toLowerCase().hashCode() + jahr;
	}
	
	public static KurseModel gebeKurse(int jahr, Model model) throws DatenbankFehler {
		return new KurseModel(model.gebeDao().gebeKurse(jahr), jahr, model);
	}
}
