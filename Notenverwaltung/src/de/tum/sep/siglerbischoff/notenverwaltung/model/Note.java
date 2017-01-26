package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
	
	private final int id;
	private int wert;
	private Date erstellungsdatum;
	private String art;
	private Double gewichtung;
	private int schuelerID;
	private int kursID;
		
	private DAO dao;

	public Note (int id, int wert, Date erstellungsdatum, String art, Double gewichtung, Schueler schueler, Kurs kurs) {
		this.id = id;
		this.wert = wert;
		this.erstellungsdatum = erstellungsdatum;
		this.art = art;
		this.gewichtung = gewichtung;
		this.schuelerID = schueler.gebeId();
		this.kursID = kurs.gebeId();
	}
	
	public int getId() {
		return id;
	}
	
	public int getWert() {
		return wert;
	}
	
	public Date getErstellungsdatum() {
		return erstellungsdatum;
	}
	
	public String getArt() {
		return art;
	}
	
	public Double getGewichtung() {
		return gewichtung;
	}
	
	public int getSchuelerID(){
		return schuelerID;
	}
	
	public int getKursID(){
		return kursID;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Note && ((Note) obj).id == id;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return format.format(erstellungsdatum) + ", " + wert + " (" + art + ")";
	}
	
	
	public void wertAendern(int neuerWert) {
		this.wert = neuerWert;
		try {
			aendern(this.getId(), neuerWert, erstellungsdatum, art, gewichtung);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void erstellungsdatumAendern(Date neuesErstellungsdatum) {
		this.erstellungsdatum = neuesErstellungsdatum;
		try {
			aendern(this.getId(), wert, neuesErstellungsdatum, art, gewichtung);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void artAendern(String neueArt) {
		this.art = neueArt;
		try {
			aendern(this.getId(), wert, erstellungsdatum, neueArt, gewichtung);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gewichtungAendern(Double neueGewichtung) {
		this.gewichtung = neueGewichtung;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, neueGewichtung);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void aendern(int noteID, int neuerWert, Date neuesErstellungsdatum, String neueArt, Double neueGewichtung) throws DatenbankFehler {
		dao.noteAendern(noteID, neuerWert, neuesErstellungsdatum, neueArt, neueGewichtung);
	}
	
	public static Note noteEintragen(Model model, int wert, Date erstellungsdatum, String art, Double gewichtung, Schueler schueler, Kurs kurs) throws DatenbankFehler {
		return model.gebeDao().noteHinzufuegen(wert, erstellungsdatum, art, gewichtung, schueler, kurs);
	}
}
