package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements ActionListener {
	
	private Model model;
	
	private SchuelerdatenView view;
	private SchuelerTableModel schueler;
	
	private Main parent;
	
	SchuelerdatenManager (MainView mainView, Model model, Main parent) {
		this.parent = parent;
		this.model = model;
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
	public void actionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand()) {
			case SchuelerdatenView.COMMAND_NEU:
				view.neu();
				break;
			case SchuelerdatenView.COMMAND_NEU_FERTIG:
				String name = view.gebeNeuName();
				Date gebDat = view.gebeNeuGebDat();
				
				if(name.equals("")) {
					view.showError("Fehler", "Bitte geben Sie einen Namen ein. ");
				} else {
					Calendar c = Calendar.getInstance();
					c.setTime(gebDat);
					c.add(Calendar.YEAR, 3);
					if (c.after(Calendar.getInstance())) {
						view.showError("Fehler", "Schüler dürfen nicht jünger als drei Jahre alt sein. ");
					} else {
						try {
						schueler.hinzufuegen(name, gebDat, model);
						//TODO Hier soll nicht die große view, sondern nur der "neu"-Dialog geschlossen werden. 
						//view.schliessen();
						} catch (DatenbankFehler e) {
							view.showError(e);
						}
					}
				}
				break;
			case SchuelerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
