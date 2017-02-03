package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class Benutzer {

	private final String loginName;
	private String name;
	private boolean istAdmin;
	
	Benutzer(String loginName, String name, boolean istAdmin) {
		this.loginName = loginName;
		this.name = name;
		this.istAdmin = istAdmin;
	}
	
	public String gebeLoginName() {
		return loginName;
	}

	public String gebeName() {
		return name;
	}
	
	public boolean istAdmin() {
		return istAdmin;
	}

	public ListModel<Klasse> gebeGeleiteteKlassen(int jahr, Model model) throws DatenbankFehler {
		List<Klasse> klassen = model.gebeDao().gebeGeleiteteKlassen(this, jahr);
		DefaultListModel<Klasse> lm = new DefaultListModel<>();
		for(Klasse k : klassen) {
			lm.addElement(k);
		}
		return lm;
	}
	
	public ListModel<Kurs> gebeGeleiteteKurse(int jahr, Model model) throws DatenbankFehler {
		List<Kurs> kurse = model.gebeDao().gebeKurse(this, jahr);
		DefaultListModel<Kurs> lm = new DefaultListModel<>();
		for(Kurs k : kurse) {
			lm.addElement(k);
		}
		return lm;
	}

	public void setzeName(String neuName, Model model) throws DatenbankFehler {
		model.gebeDao().benutzerAendern(this, neuName, istAdmin);
		this.name = neuName;
	}

	public void setzeIstAdmin(boolean neuIstAdmin, Model model) throws DatenbankFehler {
		model.gebeDao().benutzerAendern(this, name, neuIstAdmin);
		this.istAdmin = neuIstAdmin;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Benutzer && ((Benutzer) o).loginName == loginName;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static BenutzerTableModel gebeBenutzer(Model model, Benutzer loggedIn) throws DatenbankFehler {
		return new BenutzerTableModel(model.gebeDao().gebeAlleBenutzer(), model, loggedIn);
	}
	
	public static Benutzer erstelleBenutzer(String loginName, String name, char[] passwort, boolean istAdmin, Model model) throws DatenbankFehler {
		return model.gebeDao().benutzerAnlegen(loginName, name, passwort, istAdmin);
	}
}
