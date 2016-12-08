package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.EventListener;

import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;

public class BenutzerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	ListModel<Benutzer> benutzer;

	private static final String[] columnNames = new String[]{"ID", "Loginname", "Name", "Ist Admin?"};
	private static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, String.class, Boolean.class};
	
	public BenutzerTableModel(ListModel<Benutzer> benutzer, BenutzerListener listener) {
		this.benutzer = benutzer;
		listenerList.add(BenutzerListener.class, listener);
	}
	
	@Override
	public int getRowCount() {
		return benutzer.getSize();
	}

	@Override
	public int getColumnCount() {
		return 4;
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
		if(columnIndex <= 1) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0:
				return benutzer.getElementAt(rowIndex).getId();
			case 1: 
				return benutzer.getElementAt(rowIndex).getLoginName();
			case 2:
				return benutzer.getElementAt(rowIndex).getName();
			case 3: 
				return benutzer.getElementAt(rowIndex).istAdmin();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Benutzer b = benutzer.getElementAt(rowIndex);
		switch(columnIndex) {
			case 2:
				for(BenutzerListener l : listenerList.getListeners(BenutzerListener.class)) {
					l.benutzerAendern(b, b.getLoginName(), (String) aValue, b.istAdmin());
				}
				break;
			case 3:
				for(BenutzerListener l : listenerList.getListeners(BenutzerListener.class)) {
					l.benutzerAendern(b, b.getLoginName(), b.getName(), (boolean) aValue);
				}
				break;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public static interface BenutzerListener extends EventListener {
		
		void benutzerAendern(Benutzer benutzer, String loginName, String name, boolean istAdmin);
		
	}
}