package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;

public class Benutzer {

	int id;
	private String name;
	private boolean istAdmin;
	
	public Benutzer(int id, String name, boolean istAdmin) {
		this.id = id;
		this.name = name;
		this.istAdmin = istAdmin;
	}
	
	/*public static Benutzer erstelleBenutzer(int id, String name, boolean istAdmin) {
		Benutzer b = new Benutzer(id, name, istAdmin);
		DAO.dao().benutzerEintragen(b);
		return b;
	}*/

	public String getName() {
		return name;
	}
	
	public boolean istAdmin() {
		return istAdmin;
	}
	
	
	public List<Kurs> gebeKurse(int jahr) throws DatenbankFehler {
		return DAO.dao().gebeKurse(this, jahr);
	}
	
	public List<Klasse> gebeGeleiteteKlassen(int jahr) throws DatenbankFehler {
		return DAO.dao().gebeGeleiteteKlassen(this, jahr);
	}

	public int getId() {
		return id;
	}
	
}
