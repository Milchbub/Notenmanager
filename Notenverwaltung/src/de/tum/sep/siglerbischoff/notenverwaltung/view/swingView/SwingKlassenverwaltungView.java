package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;

public class SwingKlassenverwaltungView extends JDialog implements KlassenverwaltungView {

	private static final long serialVersionUID = 1L;
	
	private JTextField txtName;
	private JTextField txtJahr;
	private JList<Benutzer> listLehrer;
	private JButton btnFertig;
	
	private Component parent;

	public SwingKlassenverwaltungView(Component parent) {
		setModal(true);
		this.parent = parent;
		
		JLabel lblName = new JLabel("Klassenname: ");
		txtName = new JTextField();
		
		JLabel lblJahr = new JLabel("Schuljahr: ");
		txtJahr = new JTextField();
		
		JLabel lblLehrer = new JLabel("Klassenlehrer: ");
		listLehrer = new JList<>();
		JScrollPane scrollPane = new JScrollPane(listLehrer);
		
		btnFertig = new JButton("Klasse eintragen");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblJahr)
				.addComponent(txtJahr, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblLehrer)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnFertig))
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblJahr)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtJahr)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(lblLehrer)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.DEFAULT_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnFertig)
			.addContainerGap()
		);
		getContentPane().setLayout(gl_kursVerwaltung);
		pack();
	}

	@Override
	public void showKlassenverwaltung(ListModel<Benutzer> lehrer) {
		listLehrer.setModel(lehrer);
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
	public int getSchuljahr() {
		return Integer.parseInt(txtJahr.getText());
	}

	@Override
	public Benutzer getKlassenlehrer() {
		return listLehrer.getSelectedValue();
	}

	@Override
	public void schliessen() {
		dispose();
	}
}
