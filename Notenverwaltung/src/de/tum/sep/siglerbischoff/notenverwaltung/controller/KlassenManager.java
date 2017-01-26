package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class KlassenManager implements ActionListener {

	private KlassenverwaltungView view;
	private KlassenModel klassen;
	
	private Model model;
	
	private Main parent;
	
	KlassenManager(MainView mainView, Model model, Main parent) {
		this.parent = parent;
		try {
			klassen = Klasse.gebeKlassen(mainView.gebeJahr(), model);
			view = mainView.getKlassenverwaltungView(klassen);
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
			case KlassenverwaltungView.COMMAND_NEU:
				try {
					view.bearbeiten(null, Benutzer.gebeBenutzer(model, parent.getLoggedIn()), null);
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KlassenverwaltungView.COMMAND_BEARBEITEN:
				if(view.gebeAusgewaehlt() == null) {
					view.showError("Fehler", "Keine Klasse ausgewählt...");
				} else {
					try {
						view.bearbeiten(view.gebeAusgewaehlt(), Benutzer.gebeBenutzer(model, parent.getLoggedIn()), view.gebeAusgewaehlt().gebeSchuelerKlasseModel(model));
					} catch (DatenbankFehler e) {
						view.showError(e);
					}
				}
				break;
			case KlassenverwaltungView.COMMAND_LOESCHEN: 
				if(view.gebeAusgewaehlt() == null) {
					view.showError("Fehler", "Keine Klasse ausgewählt...");
				} else {
					try {
						klassen.loeschen(view.gebeAusgewaehlt());
					} catch (DatenbankFehler e) {
						 view.showError(e);
					}
				}
				break;
			case KlassenverwaltungView.COMMAND_NEU_FERTIG: { //Hier Klammern, um 'name' und 'lehrer' wiederverwenden zu können
				String name = view.gebeNeuName();
				Benutzer lehrer = view.gebeNeuKlassenlehrer();
				if(name.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie einen Namen ein. ");
				} else if (lehrer == null) {
					view.showError("Fehler", "Kein Klassenleiter ausgewählt..." );
				} else {
					try {
						klassen.hinzufuegen(name, lehrer);
					} catch (DatenbankFehler e) {
						//TODO Fehlermeldung zu doppeltem Klassenname-Schuljahr-Eintrag
						view.showError(e);
					}						
				}
				break;
			}
			case KlassenverwaltungView.COMMAND_BEARBEITEN_FERTIG: 
				String name = view.gebeNeuName();
				Benutzer lehrer = view.gebeNeuKlassenlehrer();
				if(name.equals("")) {
					view.showError("Fehler", "Bitte tragen Sie einen Namen ein. ");
				} else if (lehrer == null) {
					view.showError("Fehler", "Kein Klassenleiter ausgewählt..." );
				} else {
					try {
						klassen.bearbeiten(view.gebeAusgewaehlt(), name, lehrer);
					} catch (DatenbankFehler e) {
						//TODO Fehlermeldung zu doppeltem Klassenname-Schuljahr-Eintrag
						view.showError(e);
					}
				}
				break;
			case KlassenverwaltungView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}

}
