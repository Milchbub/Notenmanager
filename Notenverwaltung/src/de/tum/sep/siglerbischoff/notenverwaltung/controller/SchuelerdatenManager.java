package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements ActionListener {
	
	private SchuelerdatenView view;
	private SchuelerTableModel schueler;
	
	SchuelerdatenManager (MainView mainView, Model model) {
		try {
			schueler = Schueler.gebeSchueler(model);
			view = mainView.getSchuelerdatenView(schueler);
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
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case SchuelerdatenView.COMMAND_NEU:
				view.neu();
				break;
			case SchuelerdatenView.COMMAND_NEU_FERTIG:
				try {
					schueler.hinzufuegen(view.gebeNeuName(), view.gebeNeuGebDat());
				} catch (DatenbankFehler e1) {
					view.showError(e1);
				}
				break;
			case SchuelerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
