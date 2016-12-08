package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.ConfigDatei;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;

public final class Main implements ActionListener {

	static final boolean debug = true;
	
	private DAO dao;
	private ConfigDatei config;
	private MainView view;
	
	private Benutzer loggedIn;
	
	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
		view = MainView.erstelleMainView();
		view.addActionListener(this);
		
		try {
			config = new ConfigDatei();
		} catch (IOException e) {
			if(debug) {
				e.printStackTrace();
			}
			view.showError(e);
		}
		
		try {
			dao = DAO.erstelleDAO();
			
			login();
		} catch (DatenbankFehler e) {
			if(debug) {
				e.printStackTrace();
			}
			view.showError("Datenbankfehler", "Fehler beim Verbinden mit der Datenbank, prüfen Sie Ihre Internetverbindung. ");
			System.exit(0);
		}
	}
	
	private void login() {
		LoginView lv = view.getLoginView();
		lv.addActionListener(ae -> {
			try {
				Benutzer benutzer = dao.passwortPruefen(lv.getUser(), lv.getPassword(), config);
				if(benutzer == null) {
					lv.failure();
				} else {
					loggedIn = benutzer;
					lv.schliessen();
					Jahre jahre = dao.gebeJahre();
					int laj = jahre.gebeLetztesAktuellesJahr();
					view.loginBenutzer(loggedIn, jahre, 
							dao.gebeGeleiteteKlassen(loggedIn, laj), 
							dao.gebeKurse(loggedIn, laj)
					);
					view.zeigen();
				}
			} catch (DatenbankFehler e) {
				lv.showError(e);
			}
		});
		//TODO
		lv.zeigen();
		/*try {
			Benutzer benutzer = dao.passwortPruefen("michael.bischoff", "hallo");
			Jahre jahre = dao.gebeJahre();
			int laj = jahre.gebeLetztesAktuellesJahr();
			view.loginBenutzer(benutzer, jahre, 
					dao.gebeGeleiteteKlassen(loggedIn, laj), 
					dao.gebeKurse(loggedIn, laj)
			);
		} catch (DatenbankFehler e) {
			throw new RuntimeException(e);
		}*/
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			switch(e.getActionCommand()) {
				case MainView.COMMAND_SCHUELERDATEN: {
					new SchuelerdatenManager(view, dao);
					break; 
				}
				case MainView.COMMAND_BENUTZERVERWALTUNG: {
					new BenutzerdatenManager(view, dao);
					break;
				}
				case MainView.COMMAND_KLASSEN_ANLEGEN: {
					KlassenverwaltungView klassenView = view.getKlassenverwaltungView();
					klassenView.addActionListener(ae -> {
						try {
							dao.klasseEinrichten(klassenView.getName(), klassenView.getSchuljahr(), klassenView.getKlassenlehrer());
							klassenView.schliessen();
						} catch (DatenbankFehler f) {
							if(debug) {
								f.printStackTrace();
							}
							view.showError(f);
						}
					});
					klassenView.showKlassenverwaltung(dao.gebeBenutzer());
					break;
				}
				case MainView.COMMAND_KURSE_ANLEGEN: {
					KursverwaltungView kursView = view.getKursverwaltungView();
					kursView.addActionListener(ae -> {
						try {
							dao.kursEinrichten(kursView.getName(), kursView.getFach(), kursView.getSchuljahr(), kursView.getLehrer());
							kursView.schliessen();
						} catch (DatenbankFehler f) {
							if(debug) {
								f.printStackTrace();
							}
							view.showError(f);
						}
					});
					kursView.showKursverwaltung(dao.gebeBenutzer());
					break;
				}
			}
		} catch (DatenbankFehler f) {
			if(debug) {
				f.printStackTrace();
			}
			view.showError(f);
		}
	}
}
