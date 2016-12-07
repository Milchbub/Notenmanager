package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel.SchuelerListener;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements SchuelerListener, ActionListener {
	
	private SchuelerdatenView view;
	private DefaultListModel<Schueler> schueler;
	private DAO dao;
	
	SchuelerdatenManager (MainView mainView, DAO dao) {
		try {
			schueler = (DefaultListModel<Schueler>) dao.gebeSchueler();
			TableModel table = new SchuelerTableModel(schueler, this);
			view = mainView.getSchuelerdatenView(table);
			view.addActionListener(this);
		} catch (DatenbankFehler e) {
			if(Main.debug) {
				e.printStackTrace();
			}
			mainView.showError(e);
		}
		this.dao = dao;
		view.zeigen();
	}

	@Override
	public void schuelerAendern(Schueler schueler, String name, Date gebDat) {
		try {
			dao.schuelerAendern(schueler, name, gebDat);
		} catch (DatenbankFehler e) {
			//TODO
			view.showError(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case SchuelerdatenView.COMMAND_NEU:
				view.neu();
				break;
			case SchuelerdatenView.COMMAND_NEU_FERTIG:
				try {
					schueler.addElement(dao.schuelerHinzufuegen(view.gebeNeuName(), view.gebeNeuGebDat()));
					view.update();
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
