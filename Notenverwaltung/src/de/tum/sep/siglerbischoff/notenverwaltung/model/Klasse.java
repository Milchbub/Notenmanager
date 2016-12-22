package de.tum.sep.siglerbischoff.notenverwaltung.model;

import javax.swing.ListModel;

public class Klasse {

	private final int id; 
	private String name; 
	private int jahr; 
	private Benutzer klassenlehrer;
	
	Klasse (int id, String name, int jahr, Benutzer klassenlehrer) {
		this.id = id; 
		this.name = name; 
		this.jahr = jahr; 
		this.klassenlehrer = klassenlehrer;
	}
	
	public int gebeId() {
		return id;
	}
	
	public String gebeName() {
		return name;
	}
	
	public int gebeJahr() {
		return jahr;
	}
	
	public Benutzer gebeKlassenlehrer() {
		return klassenlehrer;
	}
	
	public ListModel<Schueler> gebeSchueler(Model model) throws DatenbankFehler {
		//TODO
		return null;
	}
	
	public SchuelerKlasseModel gebeSchuelerKlasseModel(Model model) throws DatenbankFehler {
		return new SchuelerKlasseModel(this, model);
	}
	
	void setzeName(String name, Model model) throws DatenbankFehler {
		model.gebeDao().klasseAendern(id, name, klassenlehrer);
		this.name = name;
	}
	
	void setzeKlassenlehrer(Benutzer klassenlehrer, Model model) throws DatenbankFehler {
		model.gebeDao().klasseAendern(id, name, klassenlehrer);
		this.klassenlehrer = klassenlehrer;
	}
	
	void schuelerHinzufuegen(Schueler schueler, Model model) throws DatenbankFehler {
		model.gebeDao().zuKlasseHinzufuegen(id, schueler.gebeId());
	}
	
	void schuelerEntfernen(Schueler schueler, Model model) throws DatenbankFehler{
		model.gebeDao().ausKlasseLoeschen(id, schueler.gebeId());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static KlassenModel gebeKlassen(int jahr, Model model) throws DatenbankFehler {
		return new KlassenModel(model.gebeDao().gebeKlassen(jahr), jahr, model);
	}
}
