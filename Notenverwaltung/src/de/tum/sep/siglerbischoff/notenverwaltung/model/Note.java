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
	private Schueler schueler;
	private Kurs kurs;
		
	private DAO dao;

	public Note (int id, int wert, Date erstellungsdatum, String art, Float gewichtung, String tendenz, Schueler schueler, Kurs kurs , DAO dao) {
		this.id = id;
		this.erstellungsdatum = erstellungsdatum;
		this.art = art;
		this.gewichtung = gewichtung;
		this.tendenz = tendenz;
		this.schueler = schueler;
		this.kurs = kurs;
		this.dao = dao;
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
	
	public Schueler getSchueler(){
		return schueler;
	}
	
	public Kurs getKurs(){
		return kurs;
	}
	
	
	public void wertAendern(int neuerWert) {
		this.wert = neuerWert;
		try {
			aendern(this, neuerWert, erstellungsdatum, art, gewichtung, tendenz, schueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void erstellungsdatumAendern(Date neuesErstellungsdatum) {
		this.erstellungsdatum = neuesErstellungsdatum;
		try {
			aendern(this, wert, neuesErstellungsdatum, art, gewichtung, tendenz, schueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void artAendern(String neueArt) {
		this.art = neueArt;
		try {
			aendern(this, wert, erstellungsdatum, neueArt, gewichtung, tendenz, schueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gewichtungAendern(Float neueGewichtung) {
		this.gewichtung = neueGewichtung;
		try {
			aendern(this, wert, erstellungsdatum, art, neueGewichtung, tendenz, schueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void tendenzAendern(String neueTendenz) {
		this.tendenz = neueTendenz;
		try {
			aendern(this, wert, erstellungsdatum, art, gewichtung, neueTendenz, schueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void schuelerAendern(Schueler neuerSchueler) {
		this.schueler = neuerSchueler;
		try {
			aendern(this, wert, erstellungsdatum, art, gewichtung, tendenz, neuerSchueler, kurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void kursAendern(Kurs neuerKurs) {
		this.kurs = neuerKurs;
		try {
			aendern(this, wert, erstellungsdatum, art, gewichtung, tendenz, schueler, neuerKurs);
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void aendern(Note note, int neuerWert, Date neuesErstellungsdatum, String neueArt, Float neueGewichtung, String neueTendenz, Schueler neuerSchueler, Kurs neuerKurs) throws DatenbankFehler{
		String sql = "UPDATE note SET wert = '" + neuerWert + "', "
				+ "datum = '" + neuesErstellungsdatum + "', "
				+ "art = '" + neueArt + "', "
				+ "gewichtung = '" + neueGewichtung + "', "
				+ "tendenz = '" + neueTendenz + "', "
				+ "schuelerID = '" + neuerSchueler.gebeId() + "', "
				+ "art = '" + neuerKurs.gebeId() + "' "
				+ "WHERE noteID = " + note.getId();
		dao.fireSQL(sql);
	}
	
	public Note noteHinzufuegen(int wert, Date erstellungsdatum, String art, Float gewichtung, String tendenz, Schueler schueler, Kurs kurs , DAO dao) throws DatenbankFehler, SQLException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "INSERT INTO note (wert, datum, art, gewichtung, tendenz, schuelerID, kursID) VALUES "
				+ "('" + wert + "', "
				+ "'" + df.format(erstellungsdatum) +"',"
				+ "('" + art + "', "
				+ "('" + gewichtung + "', "
				+ "('" + tendenz + "', "
				+ "('" + schueler.gebeId() + "', "
				+ "('" + kurs.gebeId()+ "')";
		ResultSet rs = dao.fireSQLResult(sql);
		rs.next();
		int id = rs.getInt(1);
		return new Note(id, wert, erstellungsdatum, art, gewichtung, tendenz, schueler, kurs, dao);
	}
		
	public void noteLoeschen(Note note) throws DatenbankFehler{
		String sql = "DELETE FROM note WHERE noteID = " + note.getId();
		dao.fireSQL(sql);	
	}

}
