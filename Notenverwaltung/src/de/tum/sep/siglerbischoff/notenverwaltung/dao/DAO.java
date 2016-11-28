package de.tum.sep.siglerbischoff.notenverwaltung.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

public interface DAO {
	
	Benutzer passwortPruefen(String name, String pass) throws DatenbankFehler;

	Jahre gebeJahre() throws DatenbankFehler;
	
	List<Benutzer> gebeBenutzer() throws DatenbankFehler;
	
	List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler;

	List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler;

	List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler;
	
	TableModel gebeSchuelerdaten();
	
	void benutzerAnlegen(String name, String loginName, String passwort, boolean istAdmin) throws DatenbankFehler;
	
	void schülerHinzufügen(String name, String gebDat, String adresse) throws DatenbankFehler;
	
	void klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler;
	
	void kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler;
	
	public abstract void benutzerLoeschen(String loginName) throws DatenbankFehler;
	
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
