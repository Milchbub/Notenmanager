package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class KlasseNotenModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private List<Kurs> kurse;
	private List<Schueler> schueler;
	private Map<Schueler, Map<Kurs, List<Note>>> daten;
	
	KlasseNotenModel(List<Schueler> schueler, List<Note> noten) throws DatenbankFehler {
		this.kurse = new Vector<>();
		this.schueler = schueler;
		daten = new HashMap<>();
		
		for(Note n : noten) {
			if(!kurse.contains(n.gebeKurs())) {
				kurse.add(n.gebeKurs());
			}
			if(daten.containsKey(n.gebeSchueler())) {
				if(daten.get(n.gebeSchueler()).containsKey(n.gebeKurs())) {
					daten.get(n.gebeSchueler()).get(n.gebeKurs()).add(n);
				} else {
					List<Note> list = new Vector<>();
					list.add(n);
					daten.get(n.gebeSchueler()).put(n.gebeKurs(), list);
				}
			} else {
				Map<Kurs, List<Note>> map = new HashMap<>();
				List<Note> list = new Vector<>();
				list.add(n);
				map.put(n.gebeKurs(), list);
				daten.put(n.gebeSchueler(), map);
			}
		}
	}

	@Override
	public int getRowCount() {
		return schueler.size();
	}

	@Override
	public int getColumnCount() {
		return kurse.size() + 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if(columnIndex == 0) {
			return "Name";
		} else {
			return kurse.get(columnIndex - 1).gebeName() + 
					" (" + kurse.get(columnIndex - 1).gebeKursleiter().gebeLoginName() + ")";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 0) {
			return Schueler.class;
		} else {
			return KursNoten.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return schueler.get(rowIndex);
		} else {
			if(daten.containsKey(schueler.get(rowIndex))) {
				Map<Kurs, List<Note>> map = daten.get(schueler.get(rowIndex));
				if(map.containsKey(kurse.get(columnIndex - 1))) {
					return new KursNoten(map.get(kurse.get(columnIndex - 1))); 
				} else {
					return new KursNoten();
				}
			} else {
				return new KursNoten();
			}
		}
	}
	
	public static final class KursNoten {
		
		private List<Note> noten;
		private double schnitt;
		
		private KursNoten(List<Note> noten) {
			this.noten = noten;
			double gewicht = 0.0;
			double summe = 0.0;
			for(Note n : noten) {
				gewicht += n.gebeGewichtung();
				summe += n.gebeGewichtung() * n.gebeWert();
			}
			schnitt = Math.round((summe / gewicht) * 100.0) / 100.0; 
		}
		
		private KursNoten() {
			noten = new Vector<>();
		}
		
		public List<Note> gebeNoten() {
			return noten;
		}
		
		public double gebeSchnitt() {
			return schnitt;
		}
		
		@Override
		public String toString() {
			if(noten.isEmpty()) {
				return "keine Note";
			} else {
				return new Double(schnitt).toString();
			}
		}
		
	}
}
