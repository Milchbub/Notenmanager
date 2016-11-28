package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager implements ActionListener {
	
	private SchuelerdatenView view;
	private DAO dao;
	
	SchuelerdatenManager (SchuelerdatenView view, DAO dao) {
		this.view = view;
		this.dao = dao;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case SchuelerdatenView.COMMAND_NEUER_SCHUELER: 
			try {
				String[] ns = view.getNeuerSchueler();
				dao.neuerSchueler(ns[SchuelerdatenView.NEUER_SCHUELER_NAME], 
						ns[SchuelerdatenView.NEUER_SCHUELER_GEBDAT], 
						ns[SchuelerdatenView.NEUER_SCHUELER_ADRESSE]);
			} catch (DatenbankFehler f) {
				view.showError(f);
			}
			break;
		}
	}

}
