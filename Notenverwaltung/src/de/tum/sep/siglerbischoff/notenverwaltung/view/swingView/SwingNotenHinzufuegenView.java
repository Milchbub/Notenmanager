package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.NotenHinzufuegenView;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;

class SwingNotenHinzufuegenView extends JDialog implements NotenHinzufuegenView {
	
	private static final long serialVersionUID = 1L;
	
	private Kurs kurs;
	
	private Component parent;
	private JTextField txtArt;
	private JTextField txtGewichtung;
	private JTextField txtDatum;
	private JButton btnSpeichern;
	private JButton btnAbbrechen;
	private JTextField txtWert;
	private JList<Schueler> list;
	
	SwingNotenHinzufuegenView(JFrame parent, ListModel<Schueler> schueler, Kurs kurs) {
		super(parent, "Note hinzufuegen");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.parent = parent;
		
		this.kurs = kurs;

		setMinimumSize(getSize());
		
		JLabel lblSchler = new JLabel("Sch\u00FCler: ");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblNotenwert = new JLabel("Notenwert: ");
		
		txtWert = new JTextField();
		txtWert.setColumns(10);
		
		JLabel lblDatum = new JLabel("Datum: ");
		
		JLabel lblArt = new JLabel("Art: ");
		
		txtArt = new JTextField();
		txtArt.setColumns(10);
		
		JLabel lblGewichtung = new JLabel("Gewichtung:");
		
		txtGewichtung = new JTextField();
		txtGewichtung.setColumns(10);
		
		txtDatum = new JTextField();
		txtDatum.setColumns(10);
		
		btnSpeichern = new JButton("Speichern");
		
		btnAbbrechen = new JButton("Abbrechen");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSchler)
							.addGap(170)
							.addComponent(lblNotenwert))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnAbbrechen, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblGewichtung)
								.addComponent(lblArt)
								.addComponent(lblDatum)
								.addComponent(btnSpeichern, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(txtWert, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtArt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtGewichtung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(197, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSchler)
						.addComponent(lblNotenwert)
						.addComponent(txtWert, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(16)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblDatum)
								.addComponent(txtDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblArt)
								.addComponent(txtArt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblGewichtung)
								.addComponent(txtGewichtung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(57)
							.addComponent(btnSpeichern)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAbbrechen)))
					.addContainerGap(122, Short.MAX_VALUE))
		);
		
		list = new JList<>(schueler);
		scrollPane.setViewportView(list);
		getContentPane().setLayout(groupLayout);
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
		dispose();
	}

	@Override
	public void showError(String titel, String nachricht) {
		JOptionPane.showMessageDialog(this, nachricht, titel, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void addActionListener(ActionListener l) {
		btnSpeichern.addActionListener(l);
		btnAbbrechen.addActionListener(l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		btnSpeichern.removeActionListener(l);
		btnAbbrechen.removeActionListener(l);
	}

	@Override
	public int gebeNeuWert() {
		return Integer.parseInt(txtWert.getText());
	}

	//TODO
	@Override
	public Date gebeNeuErstellungsdatum() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return format.parse(txtDatum.getText());
		} catch (ParseException e) {
			showError(e);
			throw new RuntimeException();
		}
	}

	@Override
	public Float gebeNeuGewichtung() {
		return Float.parseFloat(txtGewichtung.getText());
	}

	@Override
	public Schueler gebeNeuSchueler() {
		return list.getSelectedValue();
	}

	@Override
	public Kurs gebeNeuKurs() {
		return kurs;
	}

	@Override
	public String gebeNeuArt() {
		return txtArt.getText();
	}
}
