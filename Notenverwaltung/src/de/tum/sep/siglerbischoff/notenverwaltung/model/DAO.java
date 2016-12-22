package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

abstract class DAO {
	
	abstract Benutzer passwortPruefen(String name, String pass, Properties config) throws DatenbankFehler;

	abstract Jahre gebeJahre() throws DatenbankFehler;
	
	abstract List<Benutzer> gebeBenutzer() throws DatenbankFehler;
	
	abstract List<Schueler> gebeSchueler() throws DatenbankFehler;

	abstract List<Klasse> gebeKlassen(int jahr) throws DatenbankFehler;
	abstract List<Schueler> gebeSchueler(Klasse klasse) throws DatenbankFehler;
	
	abstract List<Kurs> gebeKurse(int jahr) throws DatenbankFehler;
	abstract List<Schueler> gebeSchueler(Kurs kurs) throws DatenbankFehler;
	
	abstract List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler;

	abstract List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler;

	abstract List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler;
	
	abstract Benutzer benutzerAnlegen(String loginName, String name, char[] passwort, boolean istAdmin) throws DatenbankFehler;
	abstract void benutzerLoginAendern(Benutzer benutzer, String neuerLoginName) throws DatenbankFehler;
	abstract void benutzerNameAendern(int id, String neuerName) throws DatenbankFehler;
	abstract void benutzerIstAdminAendern(Benutzer benutzer) throws DatenbankFehler;
	abstract void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler;
	
	abstract Schueler schuelerHinzufuegen(String name, Date gebDat) throws DatenbankFehler;
	abstract void schuelerAendern(int id, String neuerName, Date neuesGebDat) throws DatenbankFehler;
	abstract void schuelerLoeschen(int id) throws DatenbankFehler;
	
	abstract Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler;
	abstract void klasseAendern(int id, String neuerName, Benutzer neuerKlassenlehrer) throws DatenbankFehler;
	abstract void klasseLoeschen(int id) throws DatenbankFehler;
	abstract void zuKlasseHinzufuegen(int klasseId, int schuelerId) throws DatenbankFehler;
	abstract void ausKlasseLoeschen(int klasseId, int schuelerId) throws DatenbankFehler;
	
	abstract Kurs kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler;
	abstract void kursAendern(int id, String neuerName, String neuesFach, Benutzer neuerKursleiter) throws DatenbankFehler;
	abstract void kursLoeschen(int id) throws DatenbankFehler;
	abstract void zuKursHinzufuegen(int kursId, int schuelerId) throws DatenbankFehler;
	abstract void ausKursLoeschen(int kursId, int schuelerId) throws DatenbankFehler;
	
	abstract void fireSQL(String sql) throws DatenbankFehler;
	abstract ResultSet fireSQLResult(String sql) throws DatenbankFehler;
	
	abstract Note noteHinzufuegen(int wert, Date erstellungsdatum, String art, Float gewichtung, String tendenz, Schueler schueler, Kurs kurs) throws DatenbankFehler; 
	abstract void noteAendern(int noteID, int neuerWert, Date neuesErstellungsdatum, String neueArt, Float neueGewichtung, String neueTendenz, int neueSchuelerID, int neueKursID) throws DatenbankFehler;
	abstract void noteLoeschen(int id) throws DatenbankFehler;
	
	public static DAO erstelleDAO() throws DatenbankFehler {
		return new MysqlDAO();
	}
}
