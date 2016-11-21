package de.tum.sep.siglerbischoff.notenverwaltung.controller;
//hugzusfdztw
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.tum.sep.siglerbischoff.notenverwaltung.dao.DAO;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.View;

public final class Main implements ActionListener {

	private static final boolean debug = true;
	
	private View view;
	private DAO dao;
	
	private Benutzer loggedIn;
	
	public static void main(String[] args) {
		new Main();
	}
	
	private Main() {
		view = View.erstelleView();
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
		lv.addLoginListener(ae -> {
			try {
				Benutzer benutzer = dao.passwortPruefen(lv.getUser(), lv.getPassword());
				if(benutzer == null) {
					lv.failure();
				} else {
					loggedIn = benutzer;
					lv.success();
					view.loginBenutzer(loggedIn);
				}
			} catch (DatenbankFehler e) {
				if(debug) {
					e.printStackTrace();
				}
				view.showError(e);
			}
		});
		lv.login();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
