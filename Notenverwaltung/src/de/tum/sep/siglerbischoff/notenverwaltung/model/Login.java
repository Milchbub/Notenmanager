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
	
	String gebePasswort() {
		String p = new String(passwort);
		for(int i = 0; i < passwort.length; i++) {
			passwort[i] = 0;
		}
		return p;
	}
}
