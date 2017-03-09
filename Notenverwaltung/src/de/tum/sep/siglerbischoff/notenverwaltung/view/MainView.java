package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.io.File;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.swingView.SwingMainView;

public interface MainView extends View {
	
	static final String COMMAND_JAHR_GEAENDERT = "jahrAendern";
	
	static final String COMMAND_SCHUELERDATEN = "schuelerdaten";
	static final String COMMAND_BENUTZERVERWALTUNG = "benutzerverwaltung";
	static final String COMMAND_KLASSEN_ANLEGEN = "klassenAnlegen";
	static final String COMMAND_KURSE_ANLEGEN = "kurseAnlegen";
	
	static final String COMMAND_KLASSE_NOTEN_ANZEIGEN = "klasseNotenAnzeigen";
	static final String COMMAND_KLASSE_NOTEN_PDF = "klasseNotenPFD";
	
	static final String COMMAND_NOTE_EINTRAGEN = "noteEintragen";
	static final String COMMAND_KURS_NOTEN_ANZEIGEN = "kursNotenAnzeigen";
	static final String COMMAND_KLASSENARBEIT_EINTRAGEN = "klassenarbeitEintragen";

	static final String COMMAND_UPDATE = "update";

	void loginBenutzer(Benutzer benutzer, Jahre jahre) throws DatenbankFehler;
	
	void updateContent(Benutzer benutzer, ListModel<Klasse> geleiteteKlassen, 
			ListModel<Kurs> geleiteteKurse);

	LoginView getLoginView();

	int gebeJahr();

	SchuelerdatenView getSchuelerdatenView(TableModel schueler);

	BenutzerdatenView getBenutzerverwaltungView(TableModel benutzer);

	KlassenverwaltungView getKlassenverwaltungView(KlassenModel klassen);

	KursverwaltungView getKursverwaltungView(KurseModel kurse);
	
	NotenHinzufuegenView getNotenHinzufuegenView(ListModel<Schueler> schueler, Kurs kurs);
	
	void klasseNotenAnzeigen(KlasseNotenModel klasseNotenModel, Klasse selectedKlasse);

	Klasse gebeAusgewaehlteKlasse();
	
	File gebeSpeicherort();
	
	KursNotenAnzeigenView kursNotenAnzeigen(KursNotenModel kursNotenModel, ListModel<Schueler> schueler, Kurs selectedKurs);

	Kurs gebeAusgewaehltenKurs();
	
	public static MainView erstelleMainView() {
		return new SwingMainView();
	}

	KlassenarbeitView getKlassenarbeitView(ListModel<Schueler> gebeSchueler, Kurs kurs);
}
