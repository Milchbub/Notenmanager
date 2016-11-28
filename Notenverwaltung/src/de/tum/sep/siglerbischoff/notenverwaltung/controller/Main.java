package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.View;

public final class Main implements ActionListener {

	private static final boolean debug = true;
	
	private DAO dao;
	private MainView view;
	
	private Benutzer loggedIn;
	
	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
		view = MainView.erstelleMainView();
		view.addActionListener(this);
		
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
				Benutzer benutzer = dao.passwortPruefen(lv.getUser(), lv.getPassword());
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
				}
			} catch (DatenbankFehler e) {
				lv.showError(e);
			}
		});
		//TODO
		//lv.login();
		try {
			Benutzer benutzer = dao.passwortPruefen("michael.bischoff", "hallo");
			Jahre jahre = dao.gebeJahre();
			int laj = jahre.gebeLetztesAktuellesJahr();
			view.loginBenutzer(benutzer, jahre, 
					dao.gebeGeleiteteKlassen(loggedIn, laj), 
					dao.gebeKurse(loggedIn, laj)
			);
		} catch (DatenbankFehler e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			switch(e.getActionCommand()) {
				case View.COMMAND_SCHUELERDATEN: {
					SchuelerdatenView sdView = view.getSchuelerdatenView(dao.gebeSchuelerdaten());
					SchuelerdatenManager sdm = new SchuelerdatenManager(sdView, dao);
					sdView.addActionListener(sdm);
					sdView.zeigen();
					break; 
				}
				case View.COMMAND_BENUTZERVERWALTUNG: {
					BenutzerverwaltungView bvView = view.getBenutzerverwaltungView();
					bvView.addActionListener(ae -> {
						try {
							dao.benutzerAnlegen(bvView.getName(), bvView.getLoginName(), bvView.getPass(), bvView.getIstAdmin());
							bvView.schliessen();
						} catch (DatenbankFehler f) {
							if(debug) {
								f.printStackTrace();
							}
							view.showError(f);
						}
					});
					bvView.showBenutzerverwaltung();
					break;
				}
				case View.COMMAND_KLASSEN_ANLEGEN: {
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
					klassenView.showKlassenverwaltung(Benutzer.gebeBenutzer());
					break;
				}
				case View.COMMAND_KURSE_ANLEGEN: {
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
					kursView.showKursverwaltung(Benutzer.gebeBenutzer());
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
