package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.EventListenerList;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.NotenHinzufuegenView;

class SwingNotenHinzufuegenView extends JDialog implements NotenHinzufuegenView {
	
	private static final long serialVersionUID = 1L;
	
	private Kurs kurs;
	
	private EventListenerList listeners;
	
	private JComboBox<Integer> cmbBxWert;
	private JSpinner sprDatum;
	private JSpinner sprGewichtung;
	private JComboBox<String> txtArt;
	private JTextField txtKommentar;
	private JList<Schueler> list;
	
	SwingNotenHinzufuegenView(JFrame parent, ListModel<Schueler> schueler, Kurs kurs) {
		super(parent, "Note hinzufuegen", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.kurs = kurs;
		
		listeners = new EventListenerList();
		
		JLabel lblSchler = new JLabel("Sch\u00FCler: ");
		JScrollPane scrollPane = new JScrollPane();
		list = new JList<>(schueler);
		scrollPane.setViewportView(list);
		
		JLabel lblNotenwert = new JLabel("Notenwert: ");
		cmbBxWert = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
				
		JLabel lblDatum = new JLabel("Datum: ");
		Calendar cal = Calendar.getInstance();
		Date heute = cal.getTime();
		cal.add(Calendar.YEAR, -100);
		Date fruehestes = cal.getTime();
		sprDatum = new JSpinner(new SpinnerDateModel(heute, fruehestes, heute, Calendar.YEAR));
		sprDatum.setEditor(new DateEditor(sprDatum, "dd.MM.yyyy"));
		
		JLabel lblGewichtung = new JLabel("Gewichtung: ");
		sprGewichtung = new JSpinner(new SpinnerNumberModel(1.0, 0.05, 20.0, 0.25));
		sprGewichtung.setEditor(new NumberEditor(sprGewichtung, "0.00"));
		
		JLabel lblArt = new JLabel("Art: ");
		txtArt = new JComboBox<>(new String[]{"Unterrichtsbeitrag", "Stegreifaufgabe", "Ausfrage", 
				"Projekt", "Kurzarbeit"});
		txtArt.setEditable(true);
		
		JLabel lblKommentar = new JLabel("Kommentar: ");
		txtKommentar = new JTextField();
		
		JButton btnSpeichern = new JButton("Speichern");
		btnSpeichern.setActionCommand(COMMAND_NOTE_EINTRAGEN);
		btnSpeichern.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
			//TODO Der Dialog sollte vom Controller geschlossen werden, nur falls die Angaben korrekt sind.
			dispose();
		});
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setActionCommand(COMMAND_SCHLIESSEN);
		btnAbbrechen.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblSchler)
				.addComponent(scrollPane, 150, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNotenwert)
						.addComponent(lblDatum)
						.addComponent(lblGewichtung)
						.addComponent(lblArt)
						.addComponent(lblKommentar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(cmbBxWert)
						.addComponent(sprDatum)
						.addComponent(sprGewichtung)
						.addComponent(txtArt)
						.addComponent(txtKommentar))
				)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(btnSpeichern)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbrechen))
				)
			.addContainerGap()
		);
		
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblSchler)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, 200, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNotenwert)
						.addComponent(cmbBxWert))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDatum)
						.addComponent(sprDatum))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGewichtung)
						.addComponent(sprGewichtung))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblArt)
						.addComponent(txtArt))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblKommentar)
						.addComponent(txtKommentar))
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSpeichern)
						.addComponent(btnAbbrechen))
				)
			)
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
	public void addActionListener(ActionListener l) {
		listeners.add(ActionListener.class, l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listeners.remove(ActionListener.class, l);
	}

	@Override
	public int gebeNeuWert() {
		return (int) cmbBxWert.getSelectedItem();
	}

	@Override
	public Date gebeNeuErstellungsdatum() {
		return (Date) sprDatum.getValue();
	}

	@Override
	public Double gebeNeuGewichtung() {
		return (Double) sprGewichtung.getValue();
	}

	@Override
	public String gebeNeuArt() {
		return (String) txtArt.getSelectedItem();
	}
	
	@Override
	public String gebeNeuKommentar() {
		return txtKommentar.getText();
	}

	@Override
	public Kurs gebeNeuKurs() {
		return kurs;
	}

	@Override
	public Schueler gebeNeuSchueler() {
		return list.getSelectedValue();
	}
}
