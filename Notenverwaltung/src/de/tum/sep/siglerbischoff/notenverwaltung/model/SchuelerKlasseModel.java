package de.tum.sep.siglerbischoff.notenverwaltung.model;

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
		
		for(Schueler s : model.gebeDao().gebeSchueler()) {
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
}
