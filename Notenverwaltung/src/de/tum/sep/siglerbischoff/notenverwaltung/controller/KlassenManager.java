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
	
	KlassenManager(MainView mainView, Model model) {
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
					view.bearbeiten(null, Benutzer.gebeBenutzer(model));
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KlassenverwaltungView.COMMAND_BEARBEITEN:
				try {
					view.bearbeiten(view.gebeAusgewaehlt(), Benutzer.gebeBenutzer(model));
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KlassenverwaltungView.COMMAND_NEU_FERTIG:
				try {
					klassen.hinzufuegen(view.gebeNeuName(), 
							view.gebeNeuKlassenlehrer());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KlassenverwaltungView.COMMAND_BEARBEITEN_FERTIG: 
				try {
					klassen.bearbeiten(view.gebeAusgewaehlt(), 
							view.gebeNeuName(), view.gebeNeuKlassenlehrer());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KlassenverwaltungView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}

}
