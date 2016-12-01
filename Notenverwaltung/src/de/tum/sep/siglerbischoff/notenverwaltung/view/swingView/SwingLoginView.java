package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import java.awt.Color;

public class SwingLoginView extends JFrame implements LoginView {

	private static final long serialVersionUID = 1L;
	public static final String COMMAND_LOGIN = "login";
	
	private JTextField textField;
	private JPasswordField passwordField;
	private JButton btnAnmelden;
	private JLabel lblStatus;

	public SwingLoginView() {
		setTitle("Login");
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		
		JLabel lblLoginDaten = new JLabel("Bitte geben Sie Ihre Login-Daten ein: ");
		lblLoginDaten.setFont(new Font("Tahoma", Font.PLAIN, 22));
		
		JLabel lblBenutzername = new JLabel("Benutzername: ");
		lblBenutzername.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField.setColumns(10);
		
		JLabel lblPasswort = new JLabel("Passwort: ");
		lblPasswort.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		btnAnmelden = new JButton("Anmelden");
		btnAnmelden.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnAnmelden.setActionCommand(COMMAND_LOGIN);
		
		lblStatus = new JLabel("");
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
					.addComponent(textField, GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
					.addComponent(lblLoginDaten)
					.addComponent(lblBenutzername)
					.addComponent(lblPasswort)
					.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
					.addComponent(btnAnmelden)
					.addComponent(lblStatus))
				.addContainerGap()
		);
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(lblLoginDaten)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblBenutzername)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblPasswort)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblStatus)
				.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(btnAnmelden)
				.addContainerGap()
		);
		getContentPane().setLayout(groupLayout);
		getRootPane().setDefaultButton(btnAnmelden);
		pack();
		setMinimumSize(getSize());
	}

	@Override
	public void addActionListener(ActionListener l) {
		btnAnmelden.addActionListener(l);
	}

	@Override
	public void zeigen() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void schliessen() {
		setVisible(false);
	}

	@Override
	public String getUser() {
		return textField.getText();
	}

	@Override
	public String getPassword() {
		return new String(passwordField.getPassword());
	}

	@Override
	public void failure() {
		lblStatus.setText("<html>Falsche Daten! <br />Bitte versuchen Sie es erneut...</html>");
		pack();
		setMinimumSize(getSize());
	}

	@Override
	public void showError(String titel, String nachricht) {
		JOptionPane.showMessageDialog(this, nachricht, titel, JOptionPane.ERROR_MESSAGE);
	}
}
