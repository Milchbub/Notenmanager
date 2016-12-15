package de.tum.sep.siglerbischoff.notenverwaltung.model;

import javax.swing.DefaultListModel;

public class SchuelerKlasseModel {
	
	private Klasse klasse;
	private DefaultListModel<Schueler> schuelerIn;
	private DefaultListModel<Schueler> schuelerOut;
	
	private Model model;

	SchuelerKlasseModel(Klasse klasse, Model model) throws DatenbankFehler {
		this.klasse = klasse;
		this.model = model;
		
		for(Schueler s : model.gebeDao().gebeSchueler(klasse)) {
			schuelerIn.addElement(s);
		}
		
		for(Schueler s : model.gebeDao().gebeSchueler()) {
			if(!schuelerIn.contains(s)) {
				schuelerOut.addElement(s);
			}
		}
	}	
}
