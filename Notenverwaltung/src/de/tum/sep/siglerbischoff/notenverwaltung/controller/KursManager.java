package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class KursManager implements ActionListener {

	private KursverwaltungView view;
	private KurseModel kurse;
	
	private Model model;
	
	KursManager(MainView mainView, Model model) {
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
					view.bearbeiten(null, Benutzer.gebeBenutzer(model));
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KursverwaltungView.COMMAND_BEARBEITEN:
				try {
					view.bearbeiten(view.gebeAusgewaehlt(), Benutzer.gebeBenutzer(model));
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KursverwaltungView.COMMAND_NEU_FERTIG:
				try {
					kurse.hinzufuegen(view.gebeNeuName(), view.gebeNeuFach(), view.gebeNeulehrer());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KursverwaltungView.COMMAND_BEARBEITEN_FERTIG: 
				try {
					kurse.bearbeiten(view.gebeAusgewaehlt(), 
							view.gebeNeuName(), view.gebeNeuFach(), view.gebeNeulehrer());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case KursverwaltungView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}

}
