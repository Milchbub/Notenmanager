package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.io.IOException;

public class Model {

	private DAO dao;
	private ConfigDatei config;
	
	public Model() throws IOException {
		dao = DAO.erstelleDAO();
		config = new ConfigDatei();
	}
	
	public Benutzer passwortPruefen(Login login) throws DatenbankFehler {
		return dao.passwortPruefen(login.gebeBenutzername(), login.gebePasswort(), config);
	}

	public Jahre gebeJahre() throws DatenbankFehler {
		return dao.gebeJahre();
	}
	
	DAO gebeDao() {
		return dao;
	}
	
}
