package de.tum.sep.siglerbischoff.notenverwaltung.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

public abstract class DAO {
	
	public abstract Benutzer passwortPruefen(String name, String pass) throws DatenbankFehler;

	public abstract Jahre gebeJahre() throws DatenbankFehler;
	
	public abstract List<Benutzer> gebeBenutzer() throws DatenbankFehler;
	
	public abstract List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler;

	public abstract List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler;

	public abstract List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler;
	
	public abstract void benutzerAnlegen(String name, String loginName, String passwort, boolean istAdmin) throws DatenbankFehler;
	
	public abstract void schülerHinzufügen(String name, String gebDat, String adresse) throws DatenbankFehler;
	
	public abstract void klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler;
	
	public abstract void kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler;
	
	public abstract void benutzerLoeschen(String loginName) throws DatenbankFehler;
	
	public static DAO erstelleDAO() throws DatenbankFehler {
		singleton = new MysqlDAO();
		return singleton;
	}
	
	private static DAO singleton;
	
	public static DAO dao() {
		return singleton;
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
