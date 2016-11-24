package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

public class SwingSchuelerdatenView extends JDialog implements SchuelerdatenView {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField txtName;
	private JTextField txtGebDat;
	private JTextField txtAdresse;
	private JButton btnFertig;
	
	private Component parent;

	public SwingSchuelerdatenView(Component parent) {
		setModal(true);
		
		this.parent = parent;JLabel lblName = new JLabel("Name: ");
		txtName = new JTextField();
		
		JLabel lblGebDat = new JLabel("Geburtsdatum: ");
		txtGebDat = new JTextField();
		
		JLabel lblAdresse = new JLabel("Adresse: ");
		txtAdresse = new JTextField();
		
		btnFertig = new JButton("Schüler eintragen");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblGebDat)
				.addComponent(txtGebDat, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblAdresse)
				.addComponent(txtAdresse, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(btnFertig))
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblGebDat)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtGebDat)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblAdresse)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtAdresse)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(btnFertig)
			.addContainerGap()
		);
		getContentPane().setLayout(gl_kursVerwaltung);
		pack();
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
	public String getGebDat() {
		return txtGebDat.getText();
	}

	@Override
	public String getAdresse() {
		return txtAdresse.getText();
	}

	@Override
	public void showSchuelerdaten() {
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	@Override
	public void schliessen() {
		setVisible(false);
	}
}
