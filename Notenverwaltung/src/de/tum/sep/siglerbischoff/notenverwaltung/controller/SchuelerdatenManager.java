package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.util.Date;

import javax.swing.ListModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

class SchuelerdatenManager {
	
	private SchuelerdatenView view;
	private DAO dao;
	
	SchuelerdatenManager (MainView mainView, DAO dao) {
		try {
			ListModel<Schueler> schueler = dao.gebeSchueler();
			view = mainView.getSchuelerdatenView(new SchuelerTableModel(schueler, dao));
		} catch (DatenbankFehler e) {
			if(Main.debug) {
				e.printStackTrace();
			}
			mainView.showError(e);
		}
		this.dao = dao;
		view.zeigen();
	}
	
	private static class SchuelerTableModel implements TableModel {

		private ListModel<Schueler> schueler;
		private DAO dao;
		private static final String[] columnNames = new String[]{"ID", "Name", "Geburtsdatum"};
		private static final Class<?>[] columnTypes = new Class<?>[]{int.class, String.class, Date.class};
		
		private SchuelerTableModel(ListModel<Schueler> schueler, DAO dao) {
			this.schueler = schueler;
			this.dao = dao;
		}
		
		@Override
		public int getRowCount() {
			return schueler.getSize();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 0) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0:
					return schueler.getElementAt(rowIndex).getId();
				case 1: 
					return schueler.getElementAt(rowIndex).getName();
				case 2:
					return schueler.getElementAt(rowIndex).getGebDat();
			}
			throw new IllegalArgumentException();
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			try {
				switch(columnIndex) {
					case 1:
						dao.schuelerAendern(schueler.getElementAt(rowIndex), (String) aValue, schueler.getElementAt(rowIndex).getGebDat());
						break;
					case 2:
						dao.schuelerAendern(schueler.getElementAt(rowIndex), schueler.getElementAt(rowIndex).getName(), (Date) aValue);
						break;
				}
			} catch (DatenbankFehler e) {
				
			}
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
