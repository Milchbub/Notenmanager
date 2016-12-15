package de.tum.sep.siglerbischoff.notenverwaltung.model;

public class Kurs {

	private final int id; 
	private String name; 
	private String fach; 
	private int jahr; 
	private Benutzer kursleiter;
	
	Kurs (int id, String name, String fach, int jahr, Benutzer kursleiter) {
		this.id = id;
		this.name = name; 
		this.fach = fach;
		this.jahr = jahr; 
		this.kursleiter = kursleiter;
	}
	
	public int gebeId() {
		return id;
	}
	public String gebeName() {
		return name;
	}
	
	public String gebeFach() {
		return fach;
	}
	
	public int gebeJahr() {
		return jahr;
	}
	
	public Benutzer gebeKursleiter() {
		return kursleiter;
	}
	
	void setzeName(String name, Model model) throws DatenbankFehler {
		model.gebeDao().kursAendern(id, name, fach, kursleiter);
		this.name = name;
	}
	
	void setzeFach(String fach, Model model) throws DatenbankFehler {
		model.gebeDao().kursAendern(id, name, fach, kursleiter);
		this.fach = fach;
	}
	
	void setzeKursleiter(Benutzer kursleiter, Model model) throws DatenbankFehler {
		model.gebeDao().kursAendern(id, name, fach, kursleiter);
		this.kursleiter = kursleiter;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static KurseModel gebeKurse(int jahr, Model model) throws DatenbankFehler {
		return new KurseModel(model.gebeDao().gebeKurse(jahr), jahr, model);
	}
}
