package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

public class SwingSchuelerdatenView extends JDialog implements SchuelerdatenView {
	
	private static final long serialVersionUID = 1L;
	
	private Component parent;
	
	private EventListenerList listeners;

	public SwingSchuelerdatenView(Component parent, TableModel schueler) {
		setModal(true);
		
		this.parent = parent;
		
		listeners = new EventListenerList();
		
		JLabel lblAlleSchler = new JLabel("Alle Sch\u00FCler: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300,200));
		
		JTable schuelerTable = new JTable(schueler);
		schuelerTable.setFillsViewportHeight(true);
		if (schueler.getRowCount() > 0) {
			scrollPane.setViewportView(schuelerTable);
		} else {
			scrollPane.setViewportView(new JLabel(" Keine Sch\u00FCler eingetragen..."));
		}
		
		JButton btnHinzufuegen = new JButton("Sch\u00FCler hinzuf\u00FCgen...");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleSchler)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(btnHinzufuegen))
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblAlleSchler)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnHinzufuegen)
			.addContainerGap()
		);
		getContentPane().setLayout(gl_kursVerwaltung);
		pack();
		setMinimumSize(getSize());
	}

	@Override
	public void zeigen() {
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	@Override
	public void schliessen() {
		setVisible(false);
	}

	@Override
	public void showError(String titel, String nachricht) {
		JOptionPane.showMessageDialog(this, nachricht, titel, JOptionPane.ERROR_MESSAGE);
	}
}
