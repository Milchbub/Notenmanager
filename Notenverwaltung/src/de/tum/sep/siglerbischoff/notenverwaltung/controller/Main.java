package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

public final class Main implements ActionListener {

	static final boolean debug = true;
	
	private MainView view;
	private Model model;
	
	private Benutzer loggedIn;
	
	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
		view = MainView.erstelleMainView();
		view.addActionListener(this);
		
		try {
			model = new Model();
			
			login();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Fehler beim Lesen oder Erstellen der Konfigurationsdatei.",
					"Fehlerhafte Verbindung", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void login() {
		LoginView lv = view.getLoginView();
		lv.addActionListener(ae -> {
			try {
				Benutzer benutzer = model.passwortPruefen(lv.gebeLogin());
				if(benutzer == null) {
					lv.failure();
				} else {
					loggedIn = benutzer;
					lv.schliessen();
					Jahre jahre = model.gebeJahre();
					int laj = jahre.gebeLetztesAktuellesJahr();
					view.loginBenutzer(loggedIn, jahre);
					view.updateContent(loggedIn, loggedIn.gebeGeleiteteKlassen(laj, model), 
							loggedIn.gebeGeleiteteKurse(laj, model));
					view.zeigen();
				}
			} catch (DatenbankFehler e) {
				lv.showError(e);
			}
		});
		lv.zeigen();
		
		/*//TODO
		try {
			loggedIn = model.passwortPruefen(new Login("bisc", new String("1234").toCharArray()));
			Jahre jahre = model.gebeJahre();
			int laj = jahre.gebeLetztesAktuellesJahr();
			view.loginBenutzer(loggedIn, jahre);
			view.updateContent(loggedIn, loggedIn.gebeGeleiteteKlassen(laj, model), 
					loggedIn.gebeGeleiteteKurse(laj, model));
			view.zeigen();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}*/
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		switch(ae.getActionCommand()) {
			case MainView.COMMAND_JAHR_GEAENDERT: {
				try {
					view.updateContent(loggedIn, loggedIn.gebeGeleiteteKlassen(view.gebeJahr(), model), 
							loggedIn.gebeGeleiteteKurse(view.gebeJahr(), model));
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			}
			case MainView.COMMAND_SCHUELERDATEN: {
				new SchuelerdatenManager(view, model, this);
				break; 
			}
			case MainView.COMMAND_BENUTZERVERWALTUNG: {
				new BenutzerdatenManager(view, model, this);
				break;
			}
			case MainView.COMMAND_KLASSEN_ANLEGEN: {
				new KlassenManager(view, model, this);
				break;
			}
			case MainView.COMMAND_KURSE_ANLEGEN: {
				new KursManager(view, model, this);
				break;
			}
			case MainView.COMMAND_KLASSE_NOTEN_ANZEIGEN: {
				try {
					view.klasseNotenAnzeigen(new KlasseNotenModel(view.getSelectedKlasse(), model), 
							view.getSelectedKlasse());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			}
			case MainView.COMMAND_KURS_NOTEN_ANZEIGEN: {
				try {
					view.kursNotenAnzeigen(new KursNotenModel(view.getSelectedKurs(), model), 
							view.getSelectedKurs().gebeSchueler(model), view.getSelectedKurs());
				} catch (DatenbankFehler e) {
					view.showError(e);
				}
				break;
			}
			case MainView.COMMAND_NOTE_EINTRAGEN: {
				new NotenHinzufuegenManager(view, model, view.getSelectedKurs());
				break;
			}
			case MainView.COMMAND_KLASSENARBEIT_EINTRAGEN: {
				new KlassenarbeitManager(view, model, view.getSelectedKurs());
				break;
			}
		}
	}
	
	Benutzer getLoggedIn() {
		return loggedIn;
	}
}
