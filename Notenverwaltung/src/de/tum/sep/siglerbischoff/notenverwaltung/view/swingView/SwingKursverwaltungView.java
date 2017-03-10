package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.SchuelerKursModel;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;

public class SwingKursverwaltungView extends JDialog implements KursverwaltungView {

	private static final long serialVersionUID = 1L;

	private Component parent;
	
	private EventListenerList listeners;
	
	private KurseModel kurse;
	private JList<Kurs> jList;
	
	public SwingKursverwaltungView(JFrame parent, KurseModel kurse) {
		super(parent, "Kursverwaltung: " + kurse.gebeJahr(), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.parent = parent;
		
		listeners = new EventListenerList();
		
		this.kurse = kurse;
		
		JLabel lblAlleKurse = new JLabel("Alle Kurse: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 200));

		jList = new JList<>(kurse);
		
		if (kurse.getSize() > 0) {
			scrollPane.setViewportView(jList);
		} else {
			scrollPane.setViewportView(new JLabel(" Es sind noch keine Kurse eingetragen..."));
		}

		kurse.addListDataListener(new ListDataListener() {

			@Override
			public void intervalAdded(ListDataEvent e) {a();}

			@Override
			public void intervalRemoved(ListDataEvent e) {a();}

			@Override
			public void contentsChanged(ListDataEvent e) {a();}
			
			private void a() {
				if (kurse.getSize() > 0) {
					scrollPane.setViewportView(jList);
				} else {
					scrollPane.setViewportView(new JLabel(" Es sind noch keine Kurse eingetragen..."));
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
		
		JButton btnNeu = new JButton("Neuer Kurs...");
		btnNeu.setActionCommand(COMMAND_NEU);
		btnNeu.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		JButton btnLoeschen = new JButton("Kurs löschen");
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
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleKurse)
				.addComponent(scrollPane)
				.addComponent(btnBearbeiten)
				.addGroup(gl_kursVerwaltung.createSequentialGroup()
					.addComponent(btnNeu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLoeschen)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnOk)
				)
			)
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblAlleKurse)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(btnBearbeiten)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.BASELINE)
				.addComponent(btnNeu)
				.addComponent(btnLoeschen)
				.addComponent(btnOk)
			)
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
	public Kurs gebeAusgewaehlt() {
		return jList.getSelectedValue();
	}
	
	private String neuName;
	private String neuFach;
	private Benutzer neuLehrer;
	
	@Override
	public void bearbeiten(Kurs kurs, ListModel<Benutzer> lehrer, SchuelerKursModel schueler, KlassenModel klassen) {
		JDialog dialog = new JDialog(this);
		dialog.setModal(true);
		
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField(kurs != null ? kurs.gebeName() : "");
		JLabel lblFach = new JLabel("Fach: ");
		JTextField txtFach = new JTextField(kurs != null ? kurs.gebeFach() : "");
		JLabel lblJahr = new JLabel("Jahr: "+ kurse.gebeJahr());
		
		JLabel lblLehrer = new JLabel("Kursleiter: ");
		JList<Benutzer> list = new JList<>(lehrer);
		JScrollPane scrollPane = new JScrollPane(list);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(ae -> {
			neuName = txtName.getText();
			neuFach = txtFach.getText();
			neuLehrer = list.getSelectedValue();
			for (ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
			dialog.dispose();
		});
		
		JButton btnAbbr = new JButton("Abbrechen");
		btnAbbr.addActionListener(ae -> {
			dialog.dispose();
		});
		
		GroupLayout gl_neuer_Kurs = new GroupLayout(dialog.getContentPane());
		SequentialGroup horizontalGroup = gl_neuer_Kurs.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
				.addComponent(lblName)
				.addComponent(txtName)
				.addComponent(lblFach)
				.addComponent(txtFach)
				.addComponent(lblJahr)
				.addComponent(lblLehrer)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, GroupLayout.DEFAULT_SIZE)
				.addGroup(gl_neuer_Kurs.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbr))
			);
		
		ParallelGroup verticalParallelGroup = gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_neuer_Kurs.createSequentialGroup()
				.addContainerGap()
				.addComponent(lblName)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblFach)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(txtFach, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblJahr)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblLehrer)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(scrollPane)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
					.addComponent(btnOk)
					.addComponent(btnAbbr))
			);
		
		if(kurs == null) {
			dialog.setTitle("Neuer Kurs");
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
			
			JLabel lblFilter = new JLabel("Filtern:");
			JTextField txtFilter = new JTextField();
			txtFilter.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {a();}
				
				@Override
				public void insertUpdate(DocumentEvent e) {a();}
				
				@Override
				public void changedUpdate(DocumentEvent e) {a();}
				
				private void a() {
					try {
						schueler.filter(txtFilter.getText());
					} catch (DatenbankFehler e) {
						showError(e);
					}
				}
			});
			
			JLabel lblKlasseFilter = new JLabel("Klasse:");
			JComboBox<Klasse> cmbBxKlasseFilter = new JComboBox<>(klassen);
			cmbBxKlasseFilter.addActionListener(ae -> {
				try {
					schueler.filter((Klasse) cmbBxKlasseFilter.getSelectedItem());
				} catch (DatenbankFehler e) {
					showError(e);
				}
			});
			
			dialog.setTitle("Kurs \"" + kurs.gebeName() + "\" bearbeiten");
			btnOk.setActionCommand(COMMAND_BEARBEITEN_FERTIG);
			list.setSelectedValue(kurs.gebeKursleiter(), true);
			
			horizontalGroup
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
					.addComponent(lblZuordnen)
					.addGroup(gl_neuer_Kurs.createSequentialGroup()
						.addComponent(scrollListIn, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.CENTER, false)
							.addComponent(btnIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
							.addComponent(scrollListOut, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
							.addComponent(lblFilter)
							.addComponent(txtFilter)
							.addComponent(lblKlasseFilter)
							.addComponent(cmbBxKlasseFilter)
						)
					)
				);
			
			verticalParallelGroup
				.addGroup(gl_neuer_Kurs.createSequentialGroup()
					.addComponent(lblZuordnen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_neuer_Kurs.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollListIn)
						.addGroup(gl_neuer_Kurs.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addComponent(btnIn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOut)
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGap(0)
						)
						.addGroup(gl_neuer_Kurs.createSequentialGroup()
							.addComponent(scrollListOut)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblFilter)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblKlasseFilter)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cmbBxKlasseFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
					)
				);
		}
		
		horizontalGroup.addContainerGap();
		gl_neuer_Kurs.setHorizontalGroup(horizontalGroup);
		
		gl_neuer_Kurs.setVerticalGroup(gl_neuer_Kurs.createSequentialGroup()
			.addContainerGap()
			.addGroup(verticalParallelGroup)
			.addContainerGap()
		);
		
		dialog.setLayout(gl_neuer_Kurs);
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
	public String gebeNeuFach() {
		return neuFach;
	}
	
	@Override
	public Benutzer gebeNeulehrer() {
		return neuLehrer;
	}
}
