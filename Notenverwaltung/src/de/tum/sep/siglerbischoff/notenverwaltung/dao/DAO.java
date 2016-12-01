package de.tum.sep.siglerbischoff.notenverwaltung.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

public interface DAO {
	
	Benutzer passwortPruefen(String name, String pass, Properties config) throws DatenbankFehler;

	Jahre gebeJahre() throws DatenbankFehler;
	
	ListModel<Benutzer> gebeBenutzer() throws DatenbankFehler;
	
	ListModel<Schueler> gebeSchueler() throws DatenbankFehler;
	
	ListModel<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler;

	ListModel<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler;

	ListModel<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler;
	
	Benutzer benutzerAnlegen(String loginName, String name, String passwort, boolean istAdmin) throws DatenbankFehler;
	void benutzerAendern(Benutzer benutzer, String neuerLoginName, String neuerName, String neuIstAdmin) throws DatenbankFehler;
	void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler;
	
	Schueler schuelerHinzufuegen(String name, Date date) throws DatenbankFehler;
	void schuelerAendern(Schueler schueler, String neuerName, Date neuesGebDat) throws DatenbankFehler;
	void schuelerLoeschen(Schueler schueler) throws DatenbankFehler;
	
	Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler;
	void klasseAendern(Klasse klasse, String neuerName, Benutzer neuerKlassenlehrer) throws DatenbankFehler;
	void klasseLoeschen(Klasse klasse);
	
	Kurs kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler;
	void kursAendern(Kurs kurs, String neuerName, String neuesFach, Benutzer neuerKursleiter);
	void kursLoeschen(Kurs kurs);
	
	static DAO erstelleDAO() throws DatenbankFehler {
		return new MysqlDAO();
	}

	static String hashPasswort(String passwort) {
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwort.getBytes());
            byte[] bytes = md.digest();
            return javax.xml.bind.DatatypeConverter.printHexBinary(bytes).toLowerCase();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            throw new RuntimeException();
        }
	}
}
