package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class SchuelerKursModel {
	
	private Kurs kurs;
	private DefaultListModel<Schueler> schuelerIn;
	private DefaultListModel<Schueler> schuelerOut;
	
	private Model model;

	SchuelerKursModel(Kurs kurs, Model model) throws DatenbankFehler {
		this.kurs = kurs;
		this.model = model;
		
		schuelerIn = new DefaultListModel<>();
		schuelerOut = new DefaultListModel<>();
		
		for(Schueler s : model.gebeDao().gebeSchueler(kurs)) {
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
			kurs.schuelerHinzufuegen(s, model);
		}
	}
	
	public void moveOut(List<Schueler> schueler) throws DatenbankFehler {
		for(Schueler s : schueler) {
			schuelerIn.removeElement(s);
			if(!schuelerOut.contains(s)) {
				schuelerOut.addElement(s);
			}
			kurs.schuelerEntfernen(s, model);
		}
	}

}
