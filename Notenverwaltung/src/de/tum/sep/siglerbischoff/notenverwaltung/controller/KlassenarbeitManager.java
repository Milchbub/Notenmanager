package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenarbeitView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class KlassenarbeitManager implements ActionListener {

	private KlassenarbeitView view;
	
	private Model model;
	
	private Benutzer loggedIn;
	
	KlassenarbeitManager(MainView mainView, Model model, Kurs kurs, Benutzer loggedIn) {
		try {
			view = mainView.getKlassenarbeitView(kurs.gebeSchueler(model), kurs);
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
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case KlassenarbeitView.COMMAND_ABBRECHEN:
				view.schliessen();
				break;
			case KlassenarbeitView.COMMAND_NOTEN_EINTRAGEN:
				List<Integer> werte = view.gebeNeuWerte();
				Date datum = view.gebeNeuDatum();
				double gewichtung = view.gebeNeuGewichtung();
				String art = view.gebeNeuArt();
				String kommentar = view.gebeNeuKommentar();
				Kurs kurs = view.gebeNeuKurs();
				List<Schueler> schueler = view.gebeNeuSchueler();
				if(werte == null
						|| schueler == null
						|| werte.size() != schueler.size()) {
					//Nur nochmal zur Sicherheit...
					throw new RuntimeException();
				}

				if (datum.after(Calendar.getInstance().getTime())) {
					view.showError("Fehler", "Das Datum der Note muss in der Vergangenheit liegen. ");
				} else if (art.equals("")) {
					view.showError("Fehler" , "Bitte geben Sie die Art der Noten ein. ");
				} else if (gewichtung < 0) {
					view.showError("Fehler" , "Bitte geben Sie eine Gewichtung größer gleich null an. ");
				} else {
					for(int i = 0; i < werte.size(); i++) {
						if (werte.get(i) != -1) {
							if (werte.get(i) < 1 || werte.get(i) > 6) {
								view.showError("Fehler", "Bitte geben Sie nur Noten zwischen 1 und 6 an. ");
							} else {
								try {
									Note.noteEintragen(werte.get(i), datum, gewichtung, art, kommentar, kurs, schueler.get(i), loggedIn, model);
									view.schliessen();
								} catch (DatenbankFehler f) {
									view.showError(f);
								}
							}
						}
					}
				}
				break;
		}
	}

}
