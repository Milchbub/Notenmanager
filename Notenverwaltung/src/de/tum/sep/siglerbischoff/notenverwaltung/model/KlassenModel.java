package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.AbstractListModel;

public class KlassenModel extends AbstractListModel<Klasse> {

	private static final long serialVersionUID = 1L;
	
	private List<Klasse> klassen;
	private int jahr;
	
	private Model model;
	
	KlassenModel(List<Klasse> klassen, int jahr, Model model) {
		this.klassen = klassen;
		this.jahr = jahr;
		this.model = model;
	}
	
	public void hinzufuegen(String name, Benutzer klassenlehrer) throws DatenbankFehler {
		klassen.add(model.gebeDao().klasseEinrichten(name, jahr, klassenlehrer));
		fireIntervalAdded(this, getSize() - 1, getSize() - 1);
	}

	public void setzeKlassenlehrer(Klasse klasse, Benutzer neuKlassenlehrer) throws DatenbankFehler {
		klasse.setzeKlassenlehrer(neuKlassenlehrer, model);
		int i = klassen.indexOf(klasse);
		fireContentsChanged(this, i, i);
	}

	public void loeschen(Klasse klasse) throws DatenbankFehler {
		model.gebeDao().klasseLoeschen(klasse);
		int i = klassen.indexOf(klasse);
		klassen.remove(klasse);
		fireIntervalRemoved(this, i, i);
	}
	
	public int gebeJahr() {
		return jahr;
	}

	@Override
	public int getSize() {
		return klassen.size();
	}

	@Override
	public Klasse getElementAt(int index) {
		return klassen.get(index);
	}
}
