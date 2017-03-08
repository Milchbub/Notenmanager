package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

public class Klasse {

	private final String name; 
	private final int jahr; 
	private Benutzer klassenlehrer;
	
	Klasse (String name, int jahr, Benutzer klassenlehrer) {
		this.name = name; 
		this.jahr = jahr; 
		this.klassenlehrer = klassenlehrer;
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
	
	public List<Schueler> gebeSchueler(Model model) throws DatenbankFehler {
		return model.gebeDao().gebeSchueler(this);
	}
	
	public SchuelerKlasseModel gebeSchuelerKlasseModel(Model model) throws DatenbankFehler {
		return new SchuelerKlasseModel(this, model);
	}
	
	void setzeKlassenlehrer(Benutzer klassenlehrer, Model model) throws DatenbankFehler {
		model.gebeDao().klasseAendern(this, klassenlehrer);
		this.klassenlehrer = klassenlehrer;
	}
	
	void schuelerHinzufuegen(Schueler schueler, Model model) throws DatenbankFehler {
		model.gebeDao().zuKlasseHinzufuegen(this, schueler);
	}
	
	void schuelerEntfernen(Schueler schueler, Model model) throws DatenbankFehler{
		model.gebeDao().ausKlasseLoeschen(this, schueler);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static KlassenModel gebeKlassen(int jahr, Model model) throws DatenbankFehler {
		return new KlassenModel(model.gebeDao().gebeKlassen(jahr), jahr, model);
	}
}
