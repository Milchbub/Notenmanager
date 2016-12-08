package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.BenutzerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.BenutzerTableModel.BenutzerListener;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel.SchuelerListener;
import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerdatenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class BenutzerdatenManager implements BenutzerListener, ActionListener {
	
	private BenutzerdatenView view;
	private DefaultListModel<Benutzer> benutzer;
	private DAO dao;
	
	BenutzerdatenManager (MainView mainView, DAO dao) {
		try {
			benutzer = (DefaultListModel<Benutzer>) dao.gebeBenutzer();
			TableModel table = new BenutzerTableModel(benutzer, this);
			view = mainView.getBenutzerverwaltungView(table);
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
	public void benutzerAendern(Benutzer benutzer, String loginName, String name, boolean istAdmin) {
		try {
			dao.benutzerAendern(benutzer, loginName, name, istAdmin);
		} catch (DatenbankFehler e) {
			//TODO
			view.showError(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case BenutzerdatenView.COMMAND_NEU:
				view.neu();
				break;
			case BenutzerdatenView.COMMAND_NEU_FERTIG:
				try {
					benutzer.addElement(dao.benutzerAnlegen(view.gebeNeuLoginName(), view.gebeNeuName(), view.gebeNeuPasswort(), view.gebeNeuIstAdmin()));
					view.update();
				} catch (DatenbankFehler e1) {
					view.showError(e1);
				}
				break;
			case BenutzerdatenView.COMMAND_SCHLIESSEN: 
				view.schliessen();
				break;
		}
	}
}
