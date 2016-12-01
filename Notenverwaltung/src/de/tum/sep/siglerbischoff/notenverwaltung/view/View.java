package de.tum.sep.siglerbischoff.notenverwaltung.view;

public interface View {
	
	void zeigen();
	
	void schliessen();
	
	void showError(String titel, String nachricht);

	default void showError(Throwable e) {
		showError("Fehler", e.getMessage());
	}
}
