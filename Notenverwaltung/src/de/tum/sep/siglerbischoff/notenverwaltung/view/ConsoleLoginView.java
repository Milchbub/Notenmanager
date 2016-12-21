package de.tum.sep.siglerbischoff.notenverwaltung.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.SwingUtilities;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Login;

public class ConsoleLoginView implements LoginView {

	private List<ActionListener> list;
	
	private String user;
	private String passwort;
	
	private Scanner scan;
	
	ConsoleLoginView(Scanner scan) {
		this.scan = scan;
		list = new Vector<>();
	}
	
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
	
	public void addLoginListener(ActionListener l) {
		list.add(l);
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return passwort;
	}

	public void success() {
		System.out.println("Erfolg!");
	}

	@Override
	public void failure() {
		System.out.println("Falscher Benutzername/Passwort! Versuchen Sie es nochmal...");
		login();
	}

	/* Folgende Methoden wurden nur autogeneriert, um die Java Fehlermeldung auszumerzen.
	 * Evtl nicht benoetigte Methoden aus Superklassen bzw. Interfaces entfernen.
	 * Waere ansich sauberer. 
	 * (non-Javadoc)
	 * @see de.tum.sep.siglerbischoff.notenverwaltung.view.View#zeigen()
	 */
	@Override
	public void zeigen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schliessen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String titel, String nachricht) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeActionListener(ActionListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Login gebeLogin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addActionListener(ActionListener l) {
		// TODO Auto-generated method stub
		
	}
	/* Ende des autogenerierten Blocks */
}
