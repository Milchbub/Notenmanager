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
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;

public class SwingKursverwaltungView extends JDialog implements KursverwaltungView {

	private static final long serialVersionUID = 1L;
	
	private JList<Benutzer> listLehrer;
	private JButton btnFertig;
	private JTextField txtName;
	private JTextField txtFach;
	private JTextField txtJahr;
	
	private Component parent;
	
	public SwingKursverwaltungView(Component parent) {
		setModal(true);
		this.parent = parent;
		
		JLabel lblName = new JLabel("Kursname: ");
		txtName = new JTextField();
		
		JLabel lblFach = new JLabel("Fach: ");
		txtFach = new JTextField();
		
		JLabel lblJahr = new JLabel("Schuljahr: ");
		txtJahr = new JTextField();
		
		JLabel lblLehrer = new JLabel("Kursleiter: ");
		listLehrer = new JList<>();
		JScrollPane scrollPane = new JScrollPane(listLehrer);
		
		btnFertig = new JButton("Kurs eintragen");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(lblFach)
				.addComponent(txtFach, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
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
			.addComponent(lblFach)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtFach)
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
	public void showKursverwaltung(ListModel<Benutzer> lehrer) {
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
	public String getFach() {
		return txtFach.getText();
	}

	@Override
	public int getSchuljahr() {
		return Integer.parseInt(txtJahr.getText());
	}

	@Override
	public Benutzer getLehrer() {
		return listLehrer.getSelectedValue();
	}

	@Override
	public void schliessen() {
		dispose();
	}

}
