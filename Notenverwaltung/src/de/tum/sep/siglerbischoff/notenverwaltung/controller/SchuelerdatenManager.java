package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.ListModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerTableModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements ActionListener {
	
	private SchuelerdatenView view;
	private DAO dao;
	
	SchuelerdatenManager (MainView mainView, DAO dao) {
		try {
			ListModel<Schueler> schueler = dao.gebeSchueler();
			view = mainView.getSchuelerdatenView(new SchuelerTableModel(schueler));
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
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
