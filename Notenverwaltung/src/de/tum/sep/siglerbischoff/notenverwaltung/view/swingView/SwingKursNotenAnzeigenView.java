package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursNotenAnzeigenView;

public class SwingKursNotenAnzeigenView extends JDialog implements KursNotenAnzeigenView {

	private static final long serialVersionUID = 1L;
	private JLabel lblWert;
	private JLabel lblGewichtung;
	private JLabel lblArt;
	private JLabel lblDatum;

	SwingKursNotenAnzeigenView(JFrame parent, KursNotenModel noten, 
			ListModel<Schueler> schueler, Kurs kurs) {
		super(parent, "Noten von \"" + kurs.gebeName() + "\":");
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPaneSchueler = new JScrollPane();
		
		JLabel lblSchler = new JLabel("Sch\u00FCler: ");
		
		JScrollPane scrollPaneNoten = new JScrollPane();
		
		JLabel lblNoten = new JLabel("Note: ");
		
		lblWert = new JLabel("Wert: ");
		
		//JLabel lblDurchschnitt = new JLabel("Durchschnitt:");
		
		lblGewichtung = new JLabel("Gewichtung: ");
		
		lblArt = new JLabel("Art: ");
		
		lblDatum = new JLabel("Datum: ");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSchler)
						.addComponent(scrollPaneSchueler, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNoten)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPaneNoten, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDatum)
								.addComponent(lblArt)
								.addComponent(lblGewichtung)
								.addComponent(lblWert))))
					.addContainerGap(180, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSchler)
						.addComponent(lblNoten))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblWert)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblGewichtung)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblArt)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblDatum)
							.addPreferredGap(ComponentPlacement.RELATED, 130, Short.MAX_VALUE))
						.addComponent(scrollPaneSchueler, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
						.addComponent(scrollPaneNoten, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JList<Note> lstNoten = new JList<>(noten);
		lstNoten.addListSelectionListener(se -> {
			if(se.getValueIsAdjusting() || lstNoten.isSelectionEmpty()) {
				return;
			}
			lblWert.setText("Wert: " + lstNoten.getSelectedValue().getWert());
			lblArt.setText("Art: " + lstNoten.getSelectedValue().getArt());
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			lblDatum.setText("Datum: " + 
					format.format(lstNoten.getSelectedValue().getErstellungsdatum()));
			lblGewichtung.setText("Gewichtung: " + lstNoten.getSelectedValue().getGewichtung());
		});
		scrollPaneNoten.setViewportView(lstNoten);
		
		JList<Schueler> lstSchueler = new JList<>(schueler);
		lstSchueler.addListSelectionListener(se -> {
			try {
				noten.schuelerAuswaehlen(lstSchueler.getSelectedValue());
				lblWert.setText("Wert: ");
				lblArt.setText("Art: ");
				lblDatum.setText("Datum: ");
				lblGewichtung.setText("Gewichtung: ");
				lstNoten.clearSelection();
			} catch (DatenbankFehler e) {
				showError(e);
			}
		});
		scrollPaneSchueler.setViewportView(lstSchueler);
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
	public void addActionListener(ActionListener l) {
		//Nicht notwendig, da keine Veraenderungen an den Daten vorgenommen werden
	}

	@Override
	public void removeActionListener(ActionListener l) {
		//Siehe addActionListener(ActionListener l)
	}
}
