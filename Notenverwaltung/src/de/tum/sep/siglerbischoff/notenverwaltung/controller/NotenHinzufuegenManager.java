package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.NotenHinzufuegenView;

class NotenHinzufuegenManager implements ActionListener {

	private NotenHinzufuegenView view;
	
	private Model model;
	
	NotenHinzufuegenManager(MainView mainView, Model model, Kurs kurs) {
		try {
			view = mainView.getNotenHinzufuegenView(kurs.gebeSchueler(model), kurs);
			view.addActionListener(this);
			this.model = model;
		} catch (DatenbankFehler e) {
			if(Main.debug) {
				e.printStackTrace();
			}
			mainView.showError(e);
		}
		view.zeigen();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		switch(ae.getActionCommand()) {
			case NotenHinzufuegenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
			case NotenHinzufuegenView.COMMAND_NOTE_EINTRAGEN:
				try {
					Note.noteEintragen(model, view.gebeNeuWert(), view.gebeNeuErstellungsdatum(), view.gebeNeuArt(), view.gebeNeuGewichtung(), view.gebeNeuSchueler(), view.gebeNeuKurs());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
		}
	}
	
}
