package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SchuelerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	List<Schueler> schueler;
	Model model;

	private static final String[] columnNames = new String[]{"ID", "Name", "Geburtsdatum"};
	private static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, Date.class};
	
	public SchuelerTableModel(List<Schueler> schueler, Model model) {
		this.schueler = schueler;
		this.model = model;
	}
	
	@Override
	public int getRowCount() {
		return schueler.size();
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
				return schueler.get(rowIndex).gebeId();
			case 1: 
				return schueler.get(rowIndex).gebeName();
			case 2:
				return schueler.get(rowIndex).gebeGebDat();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Schueler s = schueler.get(rowIndex);
		try {
			switch(columnIndex) {
				case 1:
					s.setzeName((String) aValue, model);
					break;
				case 2:
					s.setzeGebDat((Date) aValue, model);
					break;
			} 
		}catch(DatenbankFehler e) {
			//TODO
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	public void hinzufuegen(String name, Date gebDat, Model model) throws DatenbankFehler {
		schueler.add(model.gebeDao().schuelerHinzufuegen(name, gebDat));
		fireTableRowsInserted(getColumnCount() - 1,	getColumnCount() - 1);
	}
}