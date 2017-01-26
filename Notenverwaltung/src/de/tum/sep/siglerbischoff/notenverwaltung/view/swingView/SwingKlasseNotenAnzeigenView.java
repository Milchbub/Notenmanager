package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout.Alignment;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.View;

class SwingKlasseNotenAnzeigenView extends JDialog implements View {

	private static final long serialVersionUID = 1L;

	public SwingKlasseNotenAnzeigenView(JFrame parent, KlasseNotenModel noten,
			Klasse klasse) {
		super(parent, "Notendurchschnitte der Klasse " + klasse.gebeName(), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		JTable table = new JTable(noten);
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(ae -> {
			dispose();
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, 600, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk))
			.addContainerGap()
		);
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane, 300, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnOk)
			.addContainerGap()
		);
		getContentPane().setLayout(groupLayout);
		
		pack();
		setMinimumSize(getSize());
	}

	@Override
	public void zeigen() {
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	@Override
	public void schliessen() {
		dispose();
	}

	@Override
	public void showError(String titel, String nachricht) {
		JOptionPane.showMessageDialog(this, nachricht, titel, JOptionPane.ERROR_MESSAGE);	
	}

	@Override
	public void addActionListener(ActionListener l) {}

	@Override
	public void removeActionListener(ActionListener l) {}

}
