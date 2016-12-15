package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface View {
	
	void zeigen();
	
	void schliessen();
	
	void showError(String titel, String nachricht);

	default void showError(Throwable e) {
		showError("Fehler", e.getMessage());
	}
	
	void addActionListener(ActionListener l);
	
	void removeActionListener(ActionListener l);
}
