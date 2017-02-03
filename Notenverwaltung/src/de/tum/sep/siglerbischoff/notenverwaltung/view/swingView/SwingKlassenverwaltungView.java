package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
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
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerKlasseModel;
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

		klassen.addListDataListener(new ListDataListener() {

			@Override
			public void intervalAdded(ListDataEvent e) {a();}

			@Override
			public void intervalRemoved(ListDataEvent e) {a();}

			@Override
			public void contentsChanged(ListDataEvent e) {a();}
			
			private void a() {
				if (klassen.getSize() > 0) {
					scrollPane.setViewportView(jList);
				} else {
					scrollPane.setViewportView(new JLabel(" Es sind noch keine Klassen eingetragen..."));
				}
			}
		});
		
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
		btnLoeschen.setActionCommand(COMMAND_LOESCHEN);
		btnLoeschen.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
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
	public void bearbeiten(Klasse klasse, ListModel<Benutzer> lehrer, SchuelerKlasseModel schueler) {
		JDialog dialog = new JDialog(this);
		dialog.setModal(true);
		
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField(klasse != null ? klasse.gebeName() : "");
		JLabel lblJahr = new JLabel("Jahr: "+ klassen.gebeJahr());
		
		JLabel lblLehrer = new JLabel("Klassenlehrer: ");
		JList<Benutzer> list = new JList<>(lehrer);
		JScrollPane scrollPane = new JScrollPane(list);
		
		JButton btnOk = new JButton("Ok");
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
		
		GroupLayout gl_neue_Klasse = new GroupLayout(dialog.getContentPane());
		SequentialGroup horizontalGroup = gl_neue_Klasse.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_neue_Klasse.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName)
				.addComponent(lblJahr)
				.addComponent(lblLehrer)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, GroupLayout.DEFAULT_SIZE)
				.addGroup(gl_neue_Klasse.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnAbbr))
			);
		ParallelGroup verticalParallelGroup = gl_neue_Klasse.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_neue_Klasse.createSequentialGroup()
				.addComponent(lblName)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblJahr)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblLehrer)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_neue_Klasse.createParallelGroup(Alignment.LEADING, false)
					.addComponent(btnOk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbr, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
		
		if(klasse == null) {
			dialog.setTitle("Neue Klasse");
			btnOk.setActionCommand(COMMAND_NEU_FERTIG);
		} else {
			JLabel lblZuordnen = new JLabel("Schüler zuordnen: ");
			JList<Schueler> listIn = new JList<>(schueler.gebeIn());
			JScrollPane scrollListIn = new JScrollPane(listIn);
			JList<Schueler> listOut = new JList<>(schueler.gebeOut());
			JScrollPane scrollListOut = new JScrollPane(listOut);
			
			JButton btnIn = new JButton("< hinzufügen");
			btnIn.addActionListener(ae -> {
				try {
					schueler.moveIn(listOut.getSelectedValuesList());
				} catch (DatenbankFehler e) {
					showError(e);
				}
			});
			
			JButton btnOut = new JButton("entfernen >");
			btnOut.addActionListener(ae -> {
				if(listIn.getSelectedValue() != null) {
					try {
						schueler.moveOut(listIn.getSelectedValuesList());
					} catch (DatenbankFehler e) {
						showError(e);
					}
				}
			});
			dialog.setTitle("Klasse \"" + klasse.gebeName() + "\" bearbeiten");
			btnOk.setActionCommand(COMMAND_BEARBEITEN_FERTIG);
			list.setSelectedValue(klasse.gebeKlassenlehrer(), true);
			
			horizontalGroup
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_neue_Klasse.createParallelGroup(Alignment.LEADING)
					.addComponent(lblZuordnen)
					.addGroup(gl_neue_Klasse.createSequentialGroup()
						.addComponent(scrollListIn, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_neue_Klasse.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(scrollListOut, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
					)
				);
			
			verticalParallelGroup
				.addGroup(gl_neue_Klasse.createSequentialGroup()
					.addComponent(lblZuordnen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_neue_Klasse.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollListIn)
						.addGroup(gl_neue_Klasse.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addComponent(btnIn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOut)
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGap(0)
						)
						.addComponent(scrollListOut)
					)
				);
		}
		
		horizontalGroup.addContainerGap();
		gl_neue_Klasse.setHorizontalGroup(horizontalGroup);
		
		gl_neue_Klasse.setVerticalGroup(gl_neue_Klasse.createSequentialGroup()
			.addContainerGap()
			.addGroup(verticalParallelGroup)
			.addContainerGap()
		);
		
		dialog.setLayout(gl_neue_Klasse);
		dialog.getRootPane().setDefaultButton(btnOk);
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
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
