package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Benutzer {

	private final int id;
	private final String loginName;
	private String name;
	private boolean istAdmin;
	
	public Benutzer(int id, String loginName, String name, boolean istAdmin) {
		this.id = id;
		this.loginName = loginName;
		this.name = name;
		this.istAdmin = istAdmin;
	}

	public int getId() {
		return id;
	}
	
	public String getLoginName() {
		return loginName;
	}

	public String getName() {
		return name;
	}
	
	public boolean istAdmin() {
		return istAdmin;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
