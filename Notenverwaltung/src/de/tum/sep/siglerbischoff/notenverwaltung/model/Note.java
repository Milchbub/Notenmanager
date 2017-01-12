package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
	
	private final int id;
	private int wert;
	private Date erstellungsdatum;
	private String art;
	private Float gewichtung;
	private int schuelerID;
	private int kursID;
		
	private DAO dao;

	public Note (int id, int wert, Date erstellungsdatum, String art, Float gewichtung, Schueler schueler, Kurs kurs) {
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
	
	public Float getGewichtung() {
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
		return format.format(erstellungsdatum) + ", " + art;
	}
	
	
	public void wertAendern(int neuerWert) {
		this.wert = neuerWert;
		try {
			aendern(this.getId(), neuerWert, erstellungsdatum, art, gewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void erstellungsdatumAendern(Date neuesErstellungsdatum) {
		this.erstellungsdatum = neuesErstellungsdatum;
		try {
			aendern(this.getId(), wert, neuesErstellungsdatum, art, gewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void artAendern(String neueArt) {
		this.art = neueArt;
		try {
			aendern(this.getId(), wert, erstellungsdatum, neueArt, gewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gewichtungAendern(Float neueGewichtung) {
		this.gewichtung = neueGewichtung;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, neueGewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void schuelerAendern(int neueSchuelerID) {
		this.schuelerID = neueSchuelerID;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, gewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void kursAendern(int neueKursID) {
		this.kursID = neueKursID;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, gewichtung, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void aendern(int noteID, int neuerWert, Date neuesErstellungsdatum, String neueArt, Float neueGewichtung, int neueSchuelerID, int neueKursID) throws DatenbankFehler {
		dao.noteAendern(noteID, neuerWert, neuesErstellungsdatum, neueArt, neueGewichtung, neueSchuelerID, neueKursID);
	}
	
	public static Note noteEintragen(Model model, int wert, Date erstellungsdatum, String art, Float gewichtung, Schueler schueler, Kurs kurs) throws DatenbankFehler {
		return model.gebeDao().noteHinzufuegen(wert, erstellungsdatum, art, gewichtung, schueler, kurs, kurs.gebeKursleiter());
	}
}
