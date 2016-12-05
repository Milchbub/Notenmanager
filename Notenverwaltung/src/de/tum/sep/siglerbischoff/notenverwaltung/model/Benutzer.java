package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Benutzer {

	private final int id;
	private String loginName;
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
	
	public void setLoginName(String neuerLoginName) {
		loginName = neuerLoginName;
	}
	
	public void setName(String neuerName) {
		name = neuerName;
	}
	
	public void setIstAdmin(boolean neuIstAdmin) {
		istAdmin = neuIstAdmin;
	}
	
	
	
	@Override
	public String toString() {
		return name;
	}
}
