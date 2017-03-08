package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class KursManager implements ActionListener {

	private KursverwaltungView view;
	private KurseModel kurse;
	
	private Model model;
	
	private Main parent;
	private MainView mainView;
	
	KursManager(MainView mainView, Model model, Main parent) {
		this.parent = parent;
		this.mainView = mainView;
		try {
			kurse = Kurs.gebeKurse(mainView.gebeJahr(), model);
			view = mainView.getKursverwaltungView(kurse);
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
		switch (ae.getActionCommand()) {
			case KursverwaltungView.COMMAND_NEU:
				try {
					view.bearbeiten(null, Benutzer.gebeBenutzer(model, parent.getLoggedIn()), null, null);
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KursverwaltungView.COMMAND_BEARBEITEN:
				if(view.gebeAusgewaehlt() == null) {
					view.showError("Fehler", "Kein Kurs ausgewählt...");
				} else {
					try {
						view.bearbeiten(view.gebeAusgewaehlt(), 
								Benutzer.gebeBenutzer(model, parent.getLoggedIn()), 
								view.gebeAusgewaehlt().gebeSchuelerKursModel(model), 
								Klasse.gebeKlassen(mainView.gebeJahr(), model));
					} catch (DatenbankFehler e) {
						view.showError(e);
					}
				}
				break;
			case KursverwaltungView.COMMAND_LOESCHEN: 
				if(view.gebeAusgewaehlt() == null) {
					view.showError("Fehler", "Keine Kurs ausgewählt...");
				} else {
					try {
						kurse.loeschen(view.gebeAusgewaehlt());
					} catch (DatenbankFehler e) {
						 view.showError(e);
					}
				}
				break;
			case KursverwaltungView.COMMAND_NEU_FERTIG: { //Hier Klammern, um 'name', 'lehrer' und 'fach' wiederverwenden zu können
				String name = view.gebeNeuName();
				String fach = view.gebeNeuFach();
				Benutzer lehrer = view.gebeNeulehrer();
				if(name.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie einen Namen ein. ");
				} else if (fach.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie ein Fach ein. ");
				} else if (lehrer == null) {
					view.showError("Fehler", "Kein Klassenleiter ausgewählt..." );
				} else {
					try {
						kurse.hinzufuegen(name, fach, lehrer);
					} catch (DatenbankFehler e) {
						//TODO Fehlermeldung zu doppeltem Kursname-Schuljahr-Eintrag
						view.showError(e);
					}
				}
				break;
			}
			case KursverwaltungView.COMMAND_BEARBEITEN_FERTIG: 
				String name = view.gebeNeuName();
				String fach = view.gebeNeuFach();
				Benutzer lehrer = view.gebeNeulehrer();
				if(name.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie einen Namen ein. ");
				} else if (fach.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie ein Fach ein. ");
				} else if (lehrer == null) {
					view.showError("Fehler", "Kein Klassenleiter ausgewählt..." );
				} else {
					try {
						kurse.bearbeiten(view.gebeAusgewaehlt(), fach, lehrer);
					} catch (DatenbankFehler e) {
						//TODO Fehlermeldung zu doppeltem Klassenname-Schuljahr-Eintrag
						view.showError(e);
					}
				}
				break;
			case KursverwaltungView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}

}
