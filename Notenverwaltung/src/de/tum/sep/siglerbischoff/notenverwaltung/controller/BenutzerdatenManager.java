package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.BenutzerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerdatenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class BenutzerdatenManager implements ActionListener {
	
	private BenutzerdatenView view;
	private BenutzerTableModel benutzer;
	
	BenutzerdatenManager (MainView mainView, Model model) {
		try {
			benutzer = Benutzer.gebeBenutzer(model);
			view = mainView.getBenutzerverwaltungView(benutzer);
			view.addActionListener(this);
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
			case BenutzerdatenView.COMMAND_NEU:
				view.neu();
				break;
			case BenutzerdatenView.COMMAND_NEU_FERTIG:
				try {
					benutzer.hinzufuegen(view.gebeNeuLoginName(), 
							view.gebeNeuName(), view.gebeNeuPasswort(), view.gebeNeuIstAdmin());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case BenutzerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
