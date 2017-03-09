package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class SchuelerKlasseModel {
	
	private Klasse klasse;
	private DefaultListModel<Schueler> schuelerIn;
	private List<Schueler> schuelerOut;
	private DefaultListModel<Schueler> schuelerOutFiltered;
	
	private Model model;

	SchuelerKlasseModel(Klasse klasse, Model model) throws DatenbankFehler {
		this.klasse = klasse;
		this.model = model;
		
		schuelerIn = new DefaultListModel<>();
		schuelerOut = new Vector<>();
		schuelerOutFiltered = new DefaultListModel<>();
		
		for(Schueler s : model.gebeDao().gebeSchueler(klasse)) {
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
			klasse.schuelerHinzufuegen(s, model);
		}
	}
	
	public void moveOut(List<Schueler> schueler) throws DatenbankFehler {
		for(Schueler s : schueler) {
			schuelerIn.removeElement(s);
			if(!schuelerOut.contains(s)) {
				schuelerOut.add(s);
				schuelerOutFiltered.addElement(s);
			}
			klasse.schuelerEntfernen(s, model);
		}
	}

	public void filter(String filter) throws DatenbankFehler {
		schuelerOutFiltered.removeAllElements();
		for(Schueler s : schuelerOut) {
			if(filter != null && s.gebeName().toLowerCase().contains(filter.toLowerCase())) {
				schuelerOutFiltered.addElement(s);
			}
		}
	}
}
