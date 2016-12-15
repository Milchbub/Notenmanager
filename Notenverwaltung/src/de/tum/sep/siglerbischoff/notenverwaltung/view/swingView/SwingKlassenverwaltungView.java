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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;

public class SwingKlassenverwaltungView extends JDialog implements KlassenverwaltungView {

	private static final long serialVersionUID = 1L;

	private Component parent;
	
	private EventListenerList listeners;
	
	private KlassenModel klassen;
	private JList<Klasse> jList;
	
	public SwingKlassenverwaltungView(JFrame parent, KlassenModel klassen) {
		super(parent, "Klassenverwaltung: " + klassen.gebeJahr(), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.parent = parent;
		
		listeners = new EventListenerList();
		
		this.klassen = klassen;
		
		JLabel lblAlleKlassen = new JLabel("Alle Klassen: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 200));

		jList = new JList<>(klassen);
		
		if (klassen.getSize() > 0) {
			scrollPane.setViewportView(jList);
		} else {
			scrollPane.setViewportView(new JLabel(" Es sind noch keine Klassen eingetragen..."));
		}
		
		JButton btnBearbeiten = new JButton("Bearbeiten...");
		btnBearbeiten.setActionCommand(COMMAND_BEARBEITEN);
		btnBearbeiten.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		JButton btnNeu = new JButton("Neue Klasse...");
		btnNeu.setActionCommand(COMMAND_NEU);
		btnNeu.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		JButton btnLoeschen = new JButton("Klasse löschen");
		btnLoeschen.addActionListener(ae -> {
			//TODO
		});
		
		JButton btnOk = new JButton("Ok");
		btnOk.setActionCommand(COMMAND_SCHLIESSEN);
		btnOk.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		GroupLayout gl_klassenVerwaltung = new GroupLayout(getContentPane());
		gl_klassenVerwaltung.setHorizontalGroup(gl_klassenVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_klassenVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleKlassen)
				.addComponent(scrollPane)
				.addComponent(btnBearbeiten)
				.addGroup(gl_klassenVerwaltung.createSequentialGroup()
					.addComponent(btnNeu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLoeschen)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnOk)
				)
			)
			.addContainerGap()
		);
		gl_klassenVerwaltung.setVerticalGroup(gl_klassenVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblAlleKlassen)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(btnBearbeiten)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_klassenVerwaltung.createParallelGroup(Alignment.BASELINE)
				.addComponent(btnNeu)
				.addComponent(btnLoeschen)
				.addComponent(btnOk)
			)
			.addContainerGap()
		);
		getContentPane().setLayout(gl_klassenVerwaltung);
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
		listeners.add(ActionListener.class, l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listeners.remove(ActionListener.class, l);
	}
	
	@Override
	public Klasse gebeAusgewaehlt() {
		return jList.getSelectedValue();
	}
	
	private String neuName;
	private Benutzer neuKlassenlehrer;
	
	@Override
	public void bearbeiten(Klasse klasse, ListModel<Benutzer> lehrer) {
		JDialog dialog = new JDialog(this);
		
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField();
		JLabel lblJahr = new JLabel("Jahr: "+ klassen.gebeJahr());
		
		JButton btnOk = new JButton("Ok");
		
		JLabel lblLehrer = new JLabel("Klassenlehrer: ");
		JList<Benutzer> list = new JList<>(lehrer);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		
		if(klasse == null) {
			dialog.setTitle("Neue Klasse");
			btnOk.setActionCommand(COMMAND_NEU_FERTIG);
		} else {
			dialog.setTitle("Klasse \"" + klasse.gebeName() + "\" bearbeiten");
			btnOk.setActionCommand(COMMAND_BEARBEITEN_FERTIG);
			txtName.setText(klasse.gebeName());
			list.setSelectedValue(klasse.gebeKlassenlehrer(), true);
		}
		
		btnOk.addActionListener(ae -> {
			neuName = txtName.getText();
			neuKlassenlehrer = list.getSelectedValue();
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
				.addComponent(lblName)
				.addComponent(txtName)
				.addComponent(lblJahr)
				.addComponent(lblLehrer)
				.addComponent(scrollPane)
				.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbr))
			)
			.addContainerGap()
		);
		
		gl_neuer_Schueler.setVerticalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblJahr)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblLehrer)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
				.addComponent(btnOk)
				.addComponent(btnAbbr))
			.addContainerGap()
		);
		
		dialog.setLayout(gl_neuer_Schueler);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	@Override
	public String gebeNeuName() {
		return neuName;
	}

	@Override
	public Benutzer gebeNeuKlassenlehrer() {
		return neuKlassenlehrer;
	}
}
