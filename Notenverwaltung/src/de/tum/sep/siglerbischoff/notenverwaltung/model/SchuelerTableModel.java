package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;
import java.util.EventListener;

import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;

public class SchuelerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	ListModel<Schueler> schueler;

	private static final String[] columnNames = new String[]{"ID", "Name", "Geburtsdatum"};
	private static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, Date.class};
	
	public SchuelerTableModel(ListModel<Schueler> schueler, SchuelerListener listener) {
		this.schueler = schueler;
		listenerList.add(SchuelerListener.class, listener);
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
		switch(columnIndex) {
			case 1:
				for(SchuelerListener l : listenerList.getListeners(SchuelerListener.class)) {
					l.schuelerAendern(schueler.getElementAt(rowIndex), (String) aValue, schueler.getElementAt(rowIndex).getGebDat());
				}
				break;
			case 2:
				for(SchuelerListener l : listenerList.getListeners(SchuelerListener.class)) {
					l.schuelerAendern(schueler.getElementAt(rowIndex), schueler.getElementAt(rowIndex).getName(), (Date) aValue);
				}
				break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public static interface SchuelerListener extends EventListener {
		
		void schuelerAendern(Schueler schueler, String name, Date gebDat);
		
	}
}