package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;
import java.util.List;
import java.util.Properties;

abstract class DAO {
	
	abstract Benutzer passwortPruefen(String name, char[] pass, Properties config) throws DatenbankFehler;

	abstract Jahre gebeJahre() throws DatenbankFehler;
	
	abstract List<Benutzer> gebeAlleBenutzer() throws DatenbankFehler;
	abstract List<Schueler> gebeAlleSchueler() throws DatenbankFehler;

	abstract List<Klasse> gebeKlassen(int jahr) throws DatenbankFehler;
	abstract List<Schueler> gebeSchueler(Klasse klasse) throws DatenbankFehler;
	abstract List<Note> gebeNoten(Klasse klasse) throws DatenbankFehler;
	
	abstract List<Kurs> gebeKurse(int jahr) throws DatenbankFehler;
	abstract List<Schueler> gebeSchueler(Kurs kurs) throws DatenbankFehler;
	abstract List<Note> gebeNoten(Kurs kurs, Schueler schueler) throws DatenbankFehler;
	
	abstract List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler;
	abstract List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler;
	abstract List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler;
	
	abstract Benutzer benutzerAnlegen(String loginName, String name, char[] passwort, boolean istAdmin) throws DatenbankFehler;
	abstract void benutzerAendern(Benutzer benutzer, String neuerName, boolean neuIstAdmin) throws DatenbankFehler;
	abstract void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler;
	
	abstract Schueler schuelerHinzufuegen(String name, Date gebDat) throws DatenbankFehler;
	abstract void schuelerAendern(Schueler schueler, String neuerName, Date neuesGebDat) throws DatenbankFehler;
	abstract void schuelerLoeschen(Schueler schueler) throws DatenbankFehler;
	
	abstract Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler;
	abstract void klasseAendern(Klasse klasse, Benutzer neuerKlassenlehrer) throws DatenbankFehler;
	abstract void klasseLoeschen(Klasse klasse) throws DatenbankFehler;
	abstract void zuKlasseHinzufuegen(Klasse klasse, Schueler schueler) throws DatenbankFehler;
	abstract void ausKlasseLoeschen(Klasse klasse, Schueler schueler) throws DatenbankFehler;
	
	abstract Kurs kursEinrichten(String name, int jahr, String fach, Benutzer kursleiter) throws DatenbankFehler;
	abstract void kursAendern(Kurs kurs, String neuesFach, Benutzer neuerKursleiter) throws DatenbankFehler;
	abstract void kursLoeschen(Kurs kurs) throws DatenbankFehler;
	abstract void zuKursHinzufuegen(Kurs kurs, Schueler schueler) throws DatenbankFehler;
	abstract void ausKursLoeschen(Kurs kurs, Schueler schueler) throws DatenbankFehler;
	
	abstract Note noteHinzufuegen(int wert, Date datum, double gewichtung, String art, String kommentar, Kurs kurs, Schueler schueler, Benutzer benutzer) throws DatenbankFehler; 
	abstract void noteLoeschen(Note note) throws DatenbankFehler;

	KlasseNotenModel gebeKlasseNotenModel(Klasse klasse) throws DatenbankFehler {
		return new KlasseNotenModel(gebeSchueler(klasse), gebeNoten(klasse));
	}

	public static DAO erstelleDAO() {
		return new MysqlDAO();
	}
}
