package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.AbstractListModel;

public class KurseModel extends AbstractListModel<Kurs> {

	private static final long serialVersionUID = 1L;
	
	private List<Kurs> kurse;
	private int jahr;
	
	private Model model;
	
	KurseModel(List<Kurs> kurse, int jahr, Model model) {
		this.kurse = kurse;
		this.jahr = jahr;
		this.model = model;
	}
	
	public void hinzufuegen(String name, String fach, Benutzer kursleiter) throws DatenbankFehler {
		kurse.add(model.gebeDao().kursEinrichten(name, jahr, fach, kursleiter));
		fireIntervalAdded(this, getSize() - 1, getSize() - 1);
	}
	
	public void bearbeiten(Kurs kurs, String neuFach, Benutzer neuKursleiter) throws DatenbankFehler {
		kurs.setzeFach(neuFach, model);
		kurs.setzeKursleiter(neuKursleiter, model);
		int i = kurse.indexOf(kurs);
		fireContentsChanged(this, i, i);
	}

	public void loeschen(Kurs kurs) throws DatenbankFehler {
		model.gebeDao().kursLoeschen(kurs);
		int i = kurse.indexOf(kurs);
		kurse.remove(kurs);
		fireIntervalRemoved(this, i, i);
	}
	
	public int gebeJahr() {
		return jahr;
	}

	@Override
	public int getSize() {
		return kurse.size();
	}

	@Override
	public Kurs getElementAt(int index) {
		return kurse.get(index);
	}
}
