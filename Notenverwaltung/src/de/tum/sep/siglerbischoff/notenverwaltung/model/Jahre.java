package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

public class Jahre implements ComboBoxModel<Integer> {
	
	private List<Integer> jahre;
	private EventListenerList listeners;
	
	private Object selected;
	
	public Jahre(List<Integer> jahre) {
		this.jahre = jahre;
		listeners = new EventListenerList();
		
		selected = null;
	}

	@Override
	public int getSize() {
		return jahre.size();
	}

	@Override
	public Integer getElementAt(int index) {
		return jahre.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(ListDataListener.class, l);
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selected = anItem;
	}	
}
