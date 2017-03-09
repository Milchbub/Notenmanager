package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractListModel;

public class KursNotenModel extends AbstractListModel<Note> {

	private static final long serialVersionUID = 1L;
	
	private Kurs kurs;
	private Model model;
	
	private Schueler ausgewaehlt;
	private HashMap<Schueler, List<Note>> notenlisten;
	
	public KursNotenModel(Kurs kurs, Model model) {
		this.kurs = kurs;
		this.model = model;
		
		notenlisten = new HashMap<>();
	}

	@Override
	public int getSize() {
		if(ausgewaehlt == null) {
			return 0;
		}
		return notenlisten.get(ausgewaehlt).size();
	}

	@Override
	public Note getElementAt(int index) {
		return notenlisten.get(ausgewaehlt).get(index);
	}
	
	public void schuelerAuswaehlen(Schueler schueler) throws DatenbankFehler {
		ausgewaehlt = schueler;
		if(!notenlisten.containsKey(schueler)) {
			notenlisten.put(schueler, model.gebeDao().gebeNoten(kurs, schueler));
		}
		fireContentsChanged(this, 0, getSize() - 1); 
	}
	
	public void noteLoeschen(Note note) throws DatenbankFehler {
		model.gebeDao().noteLoeschen(note);
		notenlisten.get(ausgewaehlt).remove(note);
		fireContentsChanged(this, 0, getSize() - 1);
	}
}
