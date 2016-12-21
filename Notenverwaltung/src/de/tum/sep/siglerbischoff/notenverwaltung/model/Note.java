package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
	
	private final int id;
	private int wert;
	private Date erstellungsdatum;
	private String art;
	private Float gewichtung;
	private String tendenz; 
	private int schuelerID;
	private int kursID;
		
	private DAO dao;

	public Note (int id, int wert, Date erstellungsdatum, String art, Float gewichtung, String tendenz, Schueler schueler, Kurs kurs) {
		this.id = id;
		this.erstellungsdatum = erstellungsdatum;
		this.art = art;
		this.gewichtung = gewichtung;
		this.tendenz = tendenz;
		this.schuelerID = schueler.gebeId();
		this.kursID = kurs.gebeId();
	}
	
	public int getId() {
		return id;
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
	
	public String getTendenz() {
		return tendenz;
	}
	
	public int getSchuelerID(){
		return schuelerID;
	}
	
	public int getKursID(){
		return kursID;
	}
	
	
	public void wertAendern(int neuerWert) {
		this.wert = neuerWert;
		try {
			aendern(this.getId(), neuerWert, erstellungsdatum, art, gewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void erstellungsdatumAendern(Date neuesErstellungsdatum) {
		this.erstellungsdatum = neuesErstellungsdatum;
		try {
			aendern(this.getId(), wert, neuesErstellungsdatum, art, gewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void artAendern(String neueArt) {
		this.art = neueArt;
		try {
			aendern(this.getId(), wert, erstellungsdatum, neueArt, gewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gewichtungAendern(Float neueGewichtung) {
		this.gewichtung = neueGewichtung;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, neueGewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void tendenzAendern(String neueTendenz) {
		this.tendenz = neueTendenz;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, gewichtung, neueTendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void schuelerAendern(int neueSchuelerID) {
		this.schuelerID = neueSchuelerID;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, gewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void kursAendern(int neueKursID) {
		this.kursID = neueKursID;
		try {
			aendern(this.getId(), wert, erstellungsdatum, art, gewichtung, tendenz, schuelerID, kursID);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void aendern(int noteID, int neuerWert, Date neuesErstellungsdatum, String neueArt, Float neueGewichtung, String neueTendenz, int neueSchuelerID, int neueKursID) throws DatenbankFehler {
		dao.noteAendern(noteID, neuerWert, neuesErstellungsdatum, neueArt, neueGewichtung, neueTendenz, neueSchuelerID, neueKursID);
	}
	
	
		
	

}
