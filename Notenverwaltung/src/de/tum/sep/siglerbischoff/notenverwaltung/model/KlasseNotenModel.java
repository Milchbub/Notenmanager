package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class KlasseNotenModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private Klasse klasse;
	private Model model;
	
	private List<Kurs> kurse;
	private List<Schueler> schueler;
	private Map<Schueler, Map<Kurs, Double>> daten;
	
	public KlasseNotenModel(Klasse klasse, Model model) throws DatenbankFehler {
		this.klasse = klasse;
		this.model = model;
		
		kurse = new Vector<>();
		schueler = klasse.gebeSchueler(model);
		daten = new HashMap<>();
		
		for(Schueler s : schueler) {
			Map<Kurs, Double> noten = new HashMap<>();
			for(Kurs k : s.gebeKurse(klasse.gebeJahr(), model)) {
				if(!kurse.contains(k)) {
					kurse.add(k);
				}
				
				double gewicht = 0.0;
				double summe = 0.0;
				for(Note n : model.gebeDao().gebeNoten(s, k)) {
					gewicht += n.getGewichtung();
					summe += n.getGewichtung() * n.getWert();
				}
				noten.put(k, Math.round((summe / gewicht) * 100.0) / 100.0);
			}
			daten.put(s, noten);
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
			return kurse.get((columnIndex) - 1).gebeName();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 0) {
			return Schueler.class;
		} else {
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return schueler.get(rowIndex);
		} else {
			Map<Kurs, Double> map = daten.get(schueler.get(rowIndex));
			Kurs kurs = kurse.get(columnIndex - 1);
			if(map.containsKey(kurs)) {
				return map.get(kurs).toString();
			} else {
				return "nicht belegt";
			}
		}
	}
}
