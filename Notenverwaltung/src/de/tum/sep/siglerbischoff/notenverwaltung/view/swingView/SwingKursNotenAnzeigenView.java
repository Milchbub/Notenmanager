package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

	SwingKursNotenAnzeigenView(JFrame parent, KursNotenModel noten, 
			ListModel<Schueler> schueler, Kurs kurs) {
		super(parent, "Noten von \"" + kurs.gebeName() + "\"", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPaneSchueler = new JScrollPane();
		
		JLabel lblSchueler = new JLabel("Sch\u00FCler: ");		
		JScrollPane scrollPaneNoten = new JScrollPane();
		
		JLabel lblNoten = new JLabel("Note: ");		
		JLabel lblDaten = new JLabel("Daten: ");
		JLabel lblWert = new JLabel("Wert: ");
		JLabel lblDatum = new JLabel("Datum: ");
		JLabel lblGewichtung = new JLabel("Gewichtung: ");
		JLabel lblArt = new JLabel("Art: ");
		JLabel lblKommentar = new JLabel("Kommentar: ");
		
		JLabel lblDurchschnitt = new JLabel("Durchschnitt:");
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblSchueler)
				.addComponent(scrollPaneSchueler, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblNoten)
				.addComponent(scrollPaneNoten, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblDaten, 180, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(lblWert)
				.addComponent(lblDatum)
				.addComponent(lblGewichtung)
				.addComponent(lblArt)
				.addComponent(lblKommentar)
				.addComponent(lblDurchschnitt))
			.addContainerGap()
		);
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblSchueler)
					.addComponent(scrollPaneSchueler, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblNoten)
					.addComponent(scrollPaneNoten, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblDaten)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblWert)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDatum)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblGewichtung)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblArt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblKommentar)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(lblDurchschnitt))
			)
			.addContainerGap()
		);
		
		JList<Note> lstNoten = new JList<>(noten);
		lstNoten.addListSelectionListener(se -> {
			if(se.getValueIsAdjusting() || lstNoten.isSelectionEmpty()) {
				return;
			}
			lblWert.setText("Wert: " + lstNoten.getSelectedValue().gebeWert());
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			lblDatum.setText("Datum: " + 
					format.format(lstNoten.getSelectedValue().gebeDatum()));
			lblGewichtung.setText("Gewichtung: " + lstNoten.getSelectedValue().getGewichtung());
			lblArt.setText("Art: " + lstNoten.getSelectedValue().gebeArt());
			lblKommentar.setText("<html>Kommentar: " + lstNoten.getSelectedValue().gebeKommentar());
		});
		scrollPaneNoten.setViewportView(lstNoten);
		
		JList<Schueler> lstSchueler = new JList<>(schueler);
		lstSchueler.addListSelectionListener(se -> {
			try {
				noten.schuelerAuswaehlen(lstSchueler.getSelectedValue());
				lblWert.setText("Wert: ");
				lblDatum.setText("Datum: ");
				lblGewichtung.setText("Gewichtung: ");
				lblArt.setText("Art: ");
				lblKommentar.setText("Kommentar: ");
				if(noten.getSize() > 0) {
					double gewicht = 0.0;
					double summe = 0.0;
					for(int i = 0; i < noten.getSize(); i++) {
						Note n = noten.getElementAt(i);
						gewicht += n.getGewichtung();
						summe += n.getGewichtung() * n.gebeWert();
					}
					NumberFormat f = new DecimalFormat("0.00");
					lblDurchschnitt.setText("Durchschnitt: " + f.format(summe / gewicht));
				} else {
					lblDurchschnitt.setText("Durchschnitt: -");
				}
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
