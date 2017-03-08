package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class SchuelerKursModel {
	
	private Kurs kurs;
	private DefaultListModel<Schueler> schuelerIn;
	private List<Schueler> schuelerOut;
	private DefaultListModel<Schueler> schuelerOutFiltered;
	
	private Model model;

	SchuelerKursModel(Kurs kurs, Model model) throws DatenbankFehler {
		this.kurs = kurs;
		this.model = model;
		
		schuelerIn = new DefaultListModel<>();
		schuelerOut = new Vector<>();
		schuelerOutFiltered = new DefaultListModel<>();
		
		for(Schueler s : model.gebeDao().gebeSchueler(kurs)) {
			schuelerIn.addElement(s);
		}
		
		for(Schueler s : model.gebeDao().gebeAlleSchueler()) {
			if(!schuelerIn.contains(s)) {
				schuelerOut.add(s);
				schuelerOutFiltered.addElement(s);
			}
		}
	}	
	
	public ListModel<Schueler> gebeIn() {
		return schuelerIn;
	}
	
	public ListModel<Schueler> gebeOut() {
		return schuelerOutFiltered;
	}
	
	public void moveIn(List<Schueler> schueler) throws DatenbankFehler {
		for(Schueler s : schueler) {
			schuelerOut.remove(s);
			schuelerOutFiltered.removeElement(s);
			if(!schuelerIn.contains(s)) {
				schuelerIn.addElement(s);
			}
			kurs.schuelerHinzufuegen(s, model);
		}
	}
	
	public void moveOut(List<Schueler> schueler) throws DatenbankFehler {
		for(Schueler s : schueler) {
			schuelerIn.removeElement(s);
			if(!schuelerOut.contains(s)) {
				schuelerOut.add(s);
				schuelerOutFiltered.addElement(s);
			}
			kurs.schuelerEntfernen(s, model);
		}
	}

	private String filter = "";
	private Klasse filterKlasse;
	
	private void filter(String filter, Klasse filterKlasse) throws DatenbankFehler {
		this.filter = filter;
		this.filterKlasse = filterKlasse;
		schuelerOutFiltered.removeAllElements();
		for(Schueler s : schuelerOut) {
			if(filter != null && s.gebeName().toLowerCase().contains(filter.toLowerCase())) {
				if(filterKlasse != null) {
					List<Schueler> schueler = model.gebeDao().gebeSchueler(filterKlasse);
					if(schueler.contains(s)) {
						schuelerOutFiltered.addElement(s);
					}
				}
			}
		}
	}

	public void filter(Klasse klasse) throws DatenbankFehler {
		filter(filter, klasse);
	}
	
	public void filter(String filter) throws DatenbankFehler {
		filter(filter, filterKlasse);
	}

}
