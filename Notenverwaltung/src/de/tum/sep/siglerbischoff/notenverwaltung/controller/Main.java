package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Login;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Model;
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
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		login();
	}
	
	private void login() {
		/*LoginView lv = view.getLoginView();
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
					view.loginBenutzer(jahre);
					view.updateContent(loggedIn, loggedIn.gebeGeleiteteKlassen(laj, model), 
							loggedIn.gebeGeleiteteKurse(laj, model));
					view.zeigen();
				}
			} catch (DatenbankFehler e) {
				lv.showError(e);
			}
		});
		lv.zeigen();*/
		
		//TODO
		try {
			loggedIn = model.passwortPruefen(new Login("notenmanager", new String("1234").toCharArray()));
			Jahre jahre = model.gebeJahre();
			int laj = jahre.gebeLetztesAktuellesJahr();
			view.loginBenutzer(loggedIn, jahre);
			view.updateContent(loggedIn, loggedIn.gebeGeleiteteKlassen(laj, model), 
					loggedIn.gebeGeleiteteKurse(laj, model));
			view.zeigen();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
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
				new SchuelerdatenManager(view, model);
				break; 
			}
			case MainView.COMMAND_BENUTZERVERWALTUNG: {
				new BenutzerdatenManager(view, model);
				break;
			}
			case MainView.COMMAND_KLASSEN_ANLEGEN: {
				new KlassenManager(view, model);
				break;
			}
			case MainView.COMMAND_KURSE_ANLEGEN: {
				new KursManager(view, model);
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
			}
		}
	}
}
