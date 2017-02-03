package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Login {

	private final String benutzername;
	private final char[] passwort;
	
	public Login(String benutzername, char[] passwort) {
		this.benutzername = benutzername;
		this.passwort = passwort;
	}

	String gebeBenutzername() {
		return benutzername;
	}
	
	char[] gebePasswort() {
		return passwort;
	}
}
