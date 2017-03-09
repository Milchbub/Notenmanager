package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursNotenAnzeigenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

class KursNotenMangaer implements ActionListener {

	private KursNotenAnzeigenView view;
	
	private KursNotenModel noten;
	
	public KursNotenMangaer(MainView mainView, Model model) {
		try {
			noten = new KursNotenModel(mainView.gebeAusgewaehltenKurs(), model);
			view = mainView.kursNotenAnzeigen(noten, mainView.gebeAusgewaehltenKurs().gebeSchueler(model), mainView.gebeAusgewaehltenKurs());
			view.addActionListener(this);
		} catch (DatenbankFehler e) {
			if(Main.debug) {
				e.printStackTrace();
			}
			view.showError(e);
		}
		view.zeigen();
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		switch(ae.getActionCommand()) {
			case KursNotenAnzeigenView.COMMAND_NOTE_LOESCHEN:
				if(view.gebeZuLoeschendeNote() != null) {
					try {
						noten.noteLoeschen(view.gebeZuLoeschendeNote());
					} catch(DatenbankFehler e) {
						view.showError(e);
					}
				}
		}
	}

}
