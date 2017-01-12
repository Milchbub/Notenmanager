package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public class BenutzerTableModel extends AbstractTableModel implements ListModel<Benutzer> {

	private static final long serialVersionUID = 1L;

	private List<Benutzer> benutzer;
	private Model model;

	private static final String[] columnNames = new String[]{"ID", "Loginname", "Name", "Ist Admin?"};
	private static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, String.class, Boolean.class};
	
	BenutzerTableModel(List<Benutzer> benutzer, Model model) {
		this.benutzer = benutzer;
		this.model = model;
	}
	
	@Override
	public int getRowCount() {
		return benutzer.size();
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
				return benutzer.get(rowIndex).gebeId();
			case 1: 
				return benutzer.get(rowIndex).gebeLoginName();
			case 2:
				return benutzer.get(rowIndex).gebeName();
			case 3: 
				return benutzer.get(rowIndex).istAdmin();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Benutzer b = benutzer.get(rowIndex);
		try {
			switch(columnIndex) {
				case 1:
					b.setzeLoginName((String) aValue, model);
					break;
				case 2:
					b.setzeName((String) aValue, model);
					break;
				case 3:
					b.setzeIstAdmin((Boolean) aValue, model);
					break;
			}
		} catch(DatenbankFehler e) {
			//TODO
			e.printStackTrace();
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public void hinzufuegen(String loginName, String name, char[] passwort,	boolean istAdmin) throws DatenbankFehler {
		benutzer.add(model.gebeDao().benutzerAnlegen(loginName, name, passwort, istAdmin));
		fireTableRowsInserted(getRowCount() - 1, getColumnCount() - 1); 
	}

	//======== ListModel-Funktionalität ========
	
	@Override
	public int getSize() {
		return getRowCount();
	}

	@Override
	public Benutzer getElementAt(int index) {
		return benutzer.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		//TODO
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		//TODO
	}
}