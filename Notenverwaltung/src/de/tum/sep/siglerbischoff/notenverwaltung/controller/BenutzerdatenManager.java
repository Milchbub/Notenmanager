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
	
	private Main parent;
	
	BenutzerdatenManager (MainView mainView, Model model, Main parent) {
		this.parent = parent;
		try {
			benutzer = Benutzer.gebeBenutzer(model, parent.getLoggedIn());
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
				String loginName = view.gebeNeuLoginName();
				String name = view.gebeNeuName();
				char[] passwort = view.gebeNeuPasswort();
				boolean istAdmin = view.gebeNeuIstAdmin();
				
				if(benutzer.contains(loginName)) {
					view.showError("Fehler", "Dieser Benutzername existiert bereits. ");
				} else if (loginName.equals("")) {
					view.showError("Fehler", "Bitte geben Sie einen Login-Namen ein. ");
				} else if (name.equals("")) {
					view.showError("Fehler", "Bitte geben Sie einen Namen ein. ");
				} else if (passwort.length == 0) {
					view.showError("Fehler", "Bitte geben Sie ein Passwort ein. ");
				} else {
					try {
					benutzer.hinzufuegen(loginName, name, passwort, istAdmin);
					//TODO Hier soll nicht die groﬂe view, sondern nur der "neu"-Dialog geschlossen werden. 
					//view.schliessen();
					} catch (DatenbankFehler e) {
						view.showError(e);
					}
				}
				break;
			case BenutzerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
