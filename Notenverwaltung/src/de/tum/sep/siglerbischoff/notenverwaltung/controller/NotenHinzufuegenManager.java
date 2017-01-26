package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
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
				int wert = view.gebeNeuWert();
				Date datum = view.gebeNeuErstellungsdatum();
				String art = view.gebeNeuArt();
				double gewichtung = view.gebeNeuGewichtung();
				Schueler schueler = view.gebeNeuSchueler();
				Kurs kurs = view.gebeNeuKurs();
				if (wert < 1 || wert > 6) {
					view.showError("Fehler", "Bitte geben Sie eine Note zwischen 1 und 6 an. ");
				} else if (datum.after(Calendar.getInstance().getTime())) {
					view.showError("Fehler", "Das Datum der Note muss in der Vergangenheit liegen. ");
				} else if (art.equals("")) {
					view.showError("Fehler" , "Bitte geben Sie die Art der Note ein. ");
				} else if (gewichtung < 0) {
					view.showError("Fehler" , "Bitte geben Sie eine Gewichtung größer gleich null an. ");
				} else if (schueler == null) { 
					view.showError("Fehler" , "Kein Schüler ausgewählt... ");
				} else {
					try {
						Note.noteEintragen(model, wert, datum, art, gewichtung, schueler, kurs);
						view.schliessen();
					} catch (DatenbankFehler e) {
						view.showError(e);
					}
				}
				break;
		}
	}
	
}
