package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;

public class ConsoleView implements View {
	
	private Scanner scan;
	
	ConsoleView() {
		scan = new Scanner(System.in);
		System.out.println("Herzlich Willkommen! ");
	}

	@Override
	public void showError(Throwable e) {
		e.printStackTrace(System.err);
	}

	public LoginView getLoginView() {
		return new ConsoleLoginView(scan);
	}

	public void loginBenutzer(Benutzer benutzer) throws DatenbankFehler {
		System.out.println("Herzlich willkommen, " + benutzer.gebeName() + "! ");
		int jahr = Calendar.getInstance().get(Calendar.YEAR);
		System.out.println("Das aktuell eingestellte Jahr ist " + jahr + ". ");
		List<Klasse> klassen = benutzer.gebeGeleiteteKlassen(jahr);
		boolean hatKlassen = !klassen.isEmpty();
		
		List<Kurs> kurse = benutzer.gebeKurse(jahr);
		boolean hatKurse = !kurse.isEmpty(); 
		
		if(benutzer.istAdmin()) {
			System.out.println("Sie sind Admin. ");
		}
		if(hatKlassen) {
			System.out.print("Sie sind Klassenleiter der Klasse");
			if(klassen.size() > 1) {
				System.out.print("n");
			}
			System.out.print(" ");
			for(int i = 0; i < klassen.size(); i++) {
				boolean last = i + 1 == klassen.size();
				boolean and = i + 2 == klassen.size();
				System.out.print(klassen.get(i).gebeName() + (last ? "" : (and ? " und " : ", ")));
			}
			System.out.println(". ");
		}
		if(hatKurse) {
			System.out.print("Sie sind Kursleiter ");
			if(klassen.size() > 1) {
				System.out.print("der Kurse ");
			} else {
				System.out.print("des Kurses ");
			}
			for(int i = 0; i < kurse.size(); i++) {
				boolean last = i + 1 == kurse.size();
				boolean and = i + 2 == kurse.size();
				System.out.print("\"" + kurse.get(i).gebeName() + (last ? "\"" : (and ? "\" und " : "\", ")));
			}
			System.out.println(". ");
		}
		if(!(hatKurse || hatKlassen || benutzer.istAdmin())) {
			System.out.println("Es gibt hier leider nichts anzuzeigen. ");
		}
	}

	@Override
	public void addActionListener(ActionListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String titel, String nachricht) {
		// TODO Auto-generated method stub
		
	}

}
