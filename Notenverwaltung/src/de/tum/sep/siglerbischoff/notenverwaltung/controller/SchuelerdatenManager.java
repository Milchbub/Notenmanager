package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements ActionListener {
	
	private SchuelerdatenView view;
	private SchuelerTableModel schueler;
	
	SchuelerdatenManager (SchuelerdatenView view, SchuelerTableModel schueler) {
		this.schueler = schueler;
		this.view = view;
		view.addActionListener(this);
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
						view.showError("Fehler", "Sch�ler d�rfen nicht j�nger als drei Jahre alt sein. ");
					} else {
						try {
						schueler.hinzufuegen(name, gebDat);
						//TODO Hier soll nicht die gro�e view, sondern nur der "neu"-Dialog geschlossen werden. 
						//view.schliessen();
						} catch (DatenbankFehler e) {
							view.showError(e);
						}
					}
				}
				break;
			case SchuelerdatenView.COMMAND_LOESCHEN: 
				try {
					schueler.loeschen(view.gebeMarkierteZeile());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			case SchuelerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
