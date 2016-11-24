package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerverwaltungView;

public class SwingBenutzerverwaltungView extends JDialog implements BenutzerverwaltungView {

	private static final long serialVersionUID = 1L;
	
	private JTextField txtName;
	private JTextField txtLoginName;
	private JPasswordField txtPass;
	private JCheckBox boxIstAdmin;
	private JButton btnFertig;
	
	private Component parent;

	public SwingBenutzerverwaltungView(Component parent) {
		setModal(true);
		this.parent = parent;
		
		JLabel lblName = new JLabel("Name: ");
		txtName = new JTextField();
		
		JLabel lblLoginName = new JLabel("Login-Name: ");
		txtLoginName = new JTextField();
		
		JLabel lblPass = new JLabel("Passwort: ");
		txtPass = new JPasswordField();
		
		JLabel lblIstAdmin = new JLabel("Admin? ");
		boxIstAdmin = new JCheckBox();
		
		btnFertig = new JButton("Benutzer eintragen");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblLoginName)
				.addComponent(txtLoginName, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblPass)
				.addComponent(txtPass, GroupLayout.PREFERRED_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblIstAdmin)
				.addComponent(boxIstAdmin)
				.addComponent(btnFertig))
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblLoginName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtLoginName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblPass)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtPass)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblIstAdmin)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(boxIstAdmin)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnFertig)
			.addContainerGap()
		);
		getContentPane().setLayout(gl_kursVerwaltung);
		pack();
	}

	@Override
	public void showBenutzerverwaltung() {
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	@Override
	public void addActionListener(ActionListener l) {
		btnFertig.addActionListener(l);
	}

	@Override
	public String getName() {
		return txtName.getText();
	}

	@Override
	public String getLoginName() {
		return txtLoginName.getText();
	}

	@Override
	public String getPass() {
		return txtPass.getText();
	}

	@Override
	public boolean getIstAdmin() {
		return boxIstAdmin.isSelected();
	}

	@Override
	public void schliessen() {
		setVisible(false);
	}
}
