package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class SchuelerKlasseModel {
	
	private Klasse klasse;
	private DefaultListModel<Schueler> schuelerIn;
	private DefaultListModel<Schueler> schuelerOut;
	
	private Model model;

	SchuelerKlasseModel(Klasse klasse, Model model) throws DatenbankFehler {
		this.klasse = klasse;
		this.model = model;
		
		schuelerIn = new DefaultListModel<>();
		schuelerOut = new DefaultListModel<>();
		
		for(Schueler s : model.gebeDao().gebeSchueler(klasse)) {
			schuelerIn.addElement(s);
		}
		
		for(Schueler s : model.gebeDao().gebeAlleSchueler()) {
			if(!schuelerIn.contains(s)) {
				schuelerOut.addElement(s);
			}
		}
	}
	
	public ListModel<Schueler> gebeIn() {
		return schuelerIn;
	}
	
	public ListModel<Schueler> gebeOut() {
		return schuelerOut;
	}
	
	public void moveIn(List<Schueler> schueler) throws DatenbankFehler {
		for(Schueler s : schueler) {
			schuelerOut.removeElement(s);
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
				schuelerOut.addElement(s);
			}
			klasse.schuelerEntfernen(s, model);
		}
	}
}
