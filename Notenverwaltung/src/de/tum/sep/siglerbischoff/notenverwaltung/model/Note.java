package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
	
	private final int id;
	private final int wert;
	private final Date datum;
	private final double gewichtung;
	private final String art;
	private final String kommentar;
	private final Kurs kurs;
	private final Schueler schueler;

	public Note (int id, int wert, Date datum, double gewichtung, 
			String art, String kommentar, Kurs kurs, Schueler schueler) {
		this.id = id;
		this.wert = wert;
		this.datum = datum;
		this.gewichtung = gewichtung;
		this.art = art;
		this.kommentar = kommentar;
		this.kurs = kurs;
		this.schueler = schueler;
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
	
	public double gebeGewichtung() {
		return gewichtung;
	}
	
	public String gebeArt() {
		return art;
	}
	
	public String gebeKommentar() {
		return kommentar;
	}
	
	public Kurs gebeKurs() {
		return kurs;
	}
	
	public Schueler gebeSchueler(){
		return schueler;
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
	
	public static Note noteEintragen(int wert, Date datum, double gewichtung, String art, String kommentar, Kurs kurs, Schueler schueler, Benutzer benutzer, Model model) throws DatenbankFehler {
		return model.gebeDao().noteHinzufuegen(wert, datum, gewichtung, art, kommentar, kurs, schueler, benutzer);
	}
}
