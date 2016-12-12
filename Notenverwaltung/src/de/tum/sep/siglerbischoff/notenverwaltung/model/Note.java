package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Note {

	private DAO dao;
	
	public Note (... , DAO dao) {
		
	}
	
	public void wertAendern(int neuerWert) {
		this.wert = neuerWert;
		aendern(neuerWert, gewichtung);
	}
	
	private void aendern(int neuerWert, int neueGewichtung, ...) {
		String sql = 
	}
	
}
