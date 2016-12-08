package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerdatenView;

public class SwingBenutzerdatenView extends JDialog implements BenutzerdatenView {
	
	private static final long serialVersionUID = 1L;
	
	private Component parent;
	
	private EventListenerList listeners;
	
	private JTable benutzerTable;
	
	public SwingBenutzerdatenView(JFrame parent, TableModel schueler) {
		super(parent, "Benutzerdaten");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.parent = parent;
		
		listeners = new EventListenerList();
		
		JLabel lblAlleSchler = new JLabel("Alle Sch\u00FCler: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300,200));
		
		benutzerTable = new JTable(schueler);
		benutzerTable.setFillsViewportHeight(true);
		
		if (schueler.getRowCount() > 0) {
			scrollPane.setViewportView(benutzerTable);
		} else {
			scrollPane.setViewportView(new JLabel(" Keine Benutzer eingetragen..."));
		}
		
		JButton btnHinzufuegen = new JButton("Benutzer hinzuf\u00FCgen...");
		btnHinzufuegen.setActionCommand(COMMAND_NEU);
		btnHinzufuegen.addActionListener(ae -> {
			for (ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		JButton btnOk = new JButton("Ok");
		btnOk.setActionCommand(COMMAND_SCHLIESSEN);
		btnOk.addActionListener(ae -> {
			for (ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleSchler)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(gl_kursVerwaltung.createSequentialGroup()
					.addComponent(btnHinzufuegen)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnOk))
				)
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblAlleSchler)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
					.addComponent(btnHinzufuegen)
					.addComponent(btnOk))
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

	@Override
	public void addActionListener(ActionListener l) {
		listeners.add(ActionListener.class, l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listeners.remove(ActionListener.class, l);
	}

	private String neuLoginName;
	private String neuName;
	private String neuPasswort;
	private boolean neuIstAdmin;
	
	@Override
	public void neu() {
		JDialog dialog = new JDialog(this, "Neuer Benutzer");

		JLabel lblLoginName = new JLabel("Login-Name: ");
		JTextField txtLoginName = new JTextField();
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField();
		JLabel lblPasswort = new JLabel("Passwort: ");
		JPasswordField txtPasswort = new JPasswordField();
		
		JButton btnOk = new JButton("Ok");
		btnOk.setActionCommand(COMMAND_NEU_FERTIG);
		btnOk.addActionListener(ae -> {
			neuLoginName = txtLoginName.getText();
			neuName = txtName.getText();
			neuPasswort = txtPasswort.getText();
			for (ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
			dialog.dispose();
		});
		
		JButton btnAbbr = new JButton("Abbrechen");
		btnAbbr.addActionListener(ae -> {
			dialog.dispose();
		});
		
		GroupLayout gl_neuer_Schueler = new GroupLayout(dialog.getContentPane());
		gl_neuer_Schueler.setHorizontalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
				.addComponent(lblLoginName)
				.addComponent(txtLoginName)
				.addComponent(lblName)
				.addComponent(txtName)
				.addComponent(lblPasswort)
				.addComponent(txtPasswort)
				.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbr))
			)
			.addContainerGap());
		
		gl_neuer_Schueler.setVerticalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblLoginName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtLoginName)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblPasswort)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtPasswort)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
				.addComponent(btnOk)
				.addComponent(btnAbbr))
			.addContainerGap());
		
		dialog.setLayout(gl_neuer_Schueler);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	
	@Override
	public String gebeNeuLoginName() {
		return neuLoginName;
	}

	@Override
	public String gebeNeuName() {
		return neuName;
	}
	
	@Override
	public String gebeNeuPasswort() {
		return neuPasswort;
	}
	
	@Override
	public boolean gebeNeuIstAdmin() {
		return neuIstAdmin;
	}
	
	@Override
	public void update() {
		((AbstractTableModel) (benutzerTable.getModel())).fireTableDataChanged();
	}
}
