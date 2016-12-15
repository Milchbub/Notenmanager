package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class Benutzer {

	private final int id;
	private String loginName;
	private String name;
	private boolean istAdmin;
	
	Benutzer(int id, String loginName, String name, boolean istAdmin) {
		this.id = id;
		this.loginName = loginName;
		this.name = name;
		this.istAdmin = istAdmin;
	}

	public int gebeId() {
		return id;
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

	public void setzeLoginName(String loginName, Model model) throws DatenbankFehler {
		model.gebeDao().benutzerLoginAendern(this, loginName);
		this.loginName = loginName;
	}

	public void setzeName(String name, Model model) throws DatenbankFehler {
		model.gebeDao().benutzerNameAendern(id, name);
		this.name = name;
	}

	public void setzeIstAdmin(boolean istAdmin, Model model) throws DatenbankFehler {
		if(this.istAdmin != istAdmin) {
			model.gebeDao().benutzerIstAdminAendern(this);
			this.istAdmin = istAdmin;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Benutzer && ((Benutzer) o).id == id;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static BenutzerTableModel gebeBenutzer(Model model) throws DatenbankFehler {
		return new BenutzerTableModel(model.gebeDao().gebeBenutzer(), model);
	}
	
	public static Benutzer erstelleBenutzer(String loginName, String name, char[] passwort, boolean istAdmin, Model model) throws DatenbankFehler {
		return model.gebeDao().benutzerAnlegen(loginName, name, passwort, istAdmin);
	}
}
