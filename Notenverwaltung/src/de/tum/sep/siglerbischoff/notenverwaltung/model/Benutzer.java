package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

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

	public String getName() {
		return name;
	}
	
	public boolean istAdmin() {
		return istAdmin;
	}
	
	public static ListModel<Benutzer> gebeBenutzer() throws DatenbankFehler {
		List<Benutzer> benutzer = DAO.dao().gebeBenutzer();
		return new ListModel<Benutzer>() {

			@Override
			public int getSize() {
				return benutzer.size();
			}

			@Override
			public Benutzer getElementAt(int index) {
				return benutzer.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				// TODO				
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				// TODO 				
			}
		};
	}
	
	public ListModel<Kurs> gebeKurse(int jahr) throws DatenbankFehler {
		List<Kurs> kurse = DAO.dao().gebeKurse(this, jahr);
		return new ListModel<Kurs>() {
			@Override
			public int getSize() {
				return kurse.size();
			}

			@Override
			public Kurs getElementAt(int index) {
				return kurse.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				//TODO
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				//TODO
			}
		};
	}
	
	public ListModel<Klasse> gebeGeleiteteKlassen(int jahr) throws DatenbankFehler {
		List<Klasse> klassen = DAO.dao().gebeGeleiteteKlassen(this, jahr);
		return new ListModel<Klasse>() {
			@Override
			public int getSize() {
				return klassen.size();
			}
			
			@Override
			public Klasse getElementAt(int index) {
				return klassen.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				//TODO
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				//TODO
			}
		};
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
