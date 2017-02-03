package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
	
	private final int id;
	private int wert;
	private Date datum;
	private double gewichtung;
	private String art;
	private String kommentar;
	private String kursName;
	private int kursJahr;
	private int schuelerID;

	public Note (int id, int wert, Date datum, double gewichtung, 
			String art, String kommentar, Kurs kurs, Schueler schueler) {
		this.id = id;
		this.wert = wert;
		this.datum = datum;
		this.gewichtung = gewichtung;
		this.art = art;
		this.kommentar = kommentar;
		this.kursName = kurs.gebeName();
		this.kursJahr = kurs.gebeJahr();
		this.schuelerID = schueler.gebeId();
	}
	
	public int gebeId() {
		return id;
	}
	
	public int gebeWert() {
		return wert;
	}
	
	public Date gebeDatum() {
		return datum;
	}
	
	public double getGewichtung() {
		return gewichtung;
	}
	
	public String gebeArt() {
		return art;
	}
	
	public String gebeKommentar() {
		return kommentar;
	}
	
	public String gebeKursName() {
		return kursName;
	}
	
	public int gebeKursJahr() {
		return kursJahr;
	}
	
	public int gebeSchuelerID(){
		return schuelerID;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Note && ((Note) obj).id == id;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return format.format(datum) + ", " + wert + " (" + art + ")";
	}
	
	
	public void setzeWert(int neuerWert, Model model) throws DatenbankFehler {
		model.gebeDao().noteAendern(this, neuerWert, datum, gewichtung, art, kommentar);
		this.wert = neuerWert;
	}
	
	public void setzeDatum(Date neuesDatum, Model model) throws DatenbankFehler {
		model.gebeDao().noteAendern(this, wert, neuesDatum, gewichtung, art, kommentar);
		this.datum = neuesDatum;
	}
	
	public void setzeGewichtung(double neueGewichtung, Model model) throws DatenbankFehler {
		model.gebeDao().noteAendern(this, wert, datum, neueGewichtung, art, kommentar);
		this.gewichtung = neueGewichtung;
	}
	
	public void setzeArt(String neueArt, Model model) throws DatenbankFehler {
		model.gebeDao().noteAendern(this, wert, datum, gewichtung, neueArt, kommentar);
		this.art = neueArt;
	}
	
	public void setzeKommentar(String neuerKommentar, Model model) throws DatenbankFehler {
		model.gebeDao().noteAendern(this, wert, datum, gewichtung, art, neuerKommentar);
		this.kommentar = neuerKommentar;
	}
	
	public static Note noteEintragen(int wert, Date datum, double gewichtung, String art, String kommentar, Kurs kurs, Schueler schueler, Benutzer benutzer, Model model) throws DatenbankFehler {
		return model.gebeDao().noteHinzufuegen(wert, datum, gewichtung, art, kommentar, kurs, schueler, benutzer);
	}
}
