package de.tum.sep.siglerbischoff.notenverwaltung.view;

import javax.swing.ComboBoxModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingMainView;

public interface MainView extends View {

	void loginBenutzer(Benutzer benutzer, ComboBoxModel<Integer> jahre) throws DatenbankFehler;

	LoginView getLoginView();

	SchuelerdatenView getSchuelerdatenView(TableModel schueler);

	BenutzerverwaltungView getBenutzerverwaltungView();

	KlassenverwaltungView getKlassenverwaltungView();

	KursverwaltungView getKursverwaltungView();
	
	public static MainView erstelleMainView() {
		return new SwingMainView();
	}
}
