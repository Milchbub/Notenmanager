package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;

public interface View {
	
	void zeigen();
	
	void schliessen();
	
	void showError(String titel, String nachricht);

	default void showError(Throwable e) {
		showError("Fehler", "<html><body width=\"1000\">" + e.getMessage().replaceAll("\n", "<br />") + "</body></html>");
	}
	
	void addActionListener(ActionListener l);
	
	void removeActionListener(ActionListener l);
}
