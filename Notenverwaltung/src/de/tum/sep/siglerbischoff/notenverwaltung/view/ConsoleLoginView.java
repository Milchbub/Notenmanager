package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.SwingUtilities;

public class ConsoleLoginView implements LoginView {

	private List<ActionListener> list;
	
	private String user;
	private String passwort;
	
	private Scanner scan;
	
	ConsoleLoginView(Scanner scan) {
		this.scan = scan;
		list = new Vector<>();
	}
	
	@Override
	public void login() {
		SwingUtilities.invokeLater(() -> {
			System.out.println("Geben Sie Ihren Benutzernamen ein: ");
			user = scan.nextLine();
			System.out.println("Geben Sie Ihr Passwort ein: "); 
			passwort = scan.nextLine();
			for(ActionListener l : list) {
				l.actionPerformed(new ActionEvent(this, -1, ""));
			}
		});
	}
	
	@Override
	public void addLoginListener(ActionListener l) {
		list.add(l);
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return passwort;
	}

	@Override
	public void success() {
		System.out.println("Erfolg!");
	}

	@Override
	public void failure() {
		System.out.println("Falscher Benutzername/Passwort! Versuchen Sie es nochmal...");
		login();
	}

}
