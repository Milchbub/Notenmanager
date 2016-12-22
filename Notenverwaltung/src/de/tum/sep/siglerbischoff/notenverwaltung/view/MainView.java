package de.tum.sep.siglerbischoff.notenverwaltung.view;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingMainView;

public interface MainView extends View {
	
	public static final String COMMAND_SCHUELERDATEN = "schuelerdaten";
	public static final String COMMAND_BENUTZERVERWALTUNG = "benutzerverwaltung";
	public static final String COMMAND_KLASSEN_ANLEGEN = "klassenAnlegen";
	public static final String COMMAND_KURSE_ANLEGEN = "kurseAnlegen";

	void loginBenutzer(Benutzer benutzer, ListModel<Klasse> geleiteteKlassen, ListModel<Kurs> geleiteteKurse,
			Jahre jahre) throws DatenbankFehler;

	LoginView getLoginView();

	int gebeJahr();

	SchuelerdatenView getSchuelerdatenView(TableModel schueler);

	BenutzerdatenView getBenutzerverwaltungView(TableModel benutzer);

	KlassenverwaltungView getKlassenverwaltungView(KlassenModel klassen);

	KursverwaltungView getKursverwaltungView(KurseModel kurse);
	
	NotenHinzufuegenView getNotenHinzufuegenView(ListModel<Schueler> schueler, Kurs kurs);
	
	public static MainView erstelleMainView() {
		return new SwingMainView();
	}
}
