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
		super(parent, "Klassenverwaltung: " + kurse.gebeJahr(), true);
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
	public void bearbeiten(Kurs kurs, ListModel<Benutzer> lehrer, SchuelerKursModel schueler) {
		JDialog dialog = new JDialog(this);
		dialog.setModal(true);
		
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField();
		JLabel lblFach = new JLabel("Fach: ");
		JTextField txtFach = new JTextField();
		JLabel lblJahr = new JLabel("Jahr: "+ kurse.gebeJahr());
		
		JButton btnOk = new JButton("Ok");
		
		JLabel lblLehrer = new JLabel("Kursleiter: ");
		JList<Benutzer> list = new JList<>(lehrer);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		
		if(kurs == null) {
			dialog.setTitle("Neuer Kurs");
			btnOk.setActionCommand(COMMAND_NEU_FERTIG);
		} else {
			dialog.setTitle("Kurs \"" + kurs.gebeName() + "\" bearbeiten");
			btnOk.setActionCommand(COMMAND_BEARBEITEN_FERTIG);
			txtName.setText(kurs.gebeName());
			list.setSelectedValue(kurs.gebeKursleiter(), true);
		}
		
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

		JLabel lblZuordnen = new JLabel("Schüler zuordnen: ");
		JList<Schueler> listIn = new JList<>(schueler.gebeIn());
		JScrollPane scrollListIn = new JScrollPane(listIn);
		JList<Schueler> listOut = new JList<>(schueler.gebeOut());
		JScrollPane scrollListOut = new JScrollPane(listOut);
		
		JButton btnIn = new JButton("< hinzufügen");
		btnIn.addActionListener(ae -> {
			schueler.moveIn(listOut.getSelectedValuesList());
		});
		
		JButton btnOut = new JButton("entfernen >");
		btnOut.addActionListener(ae -> {
			if(listIn.getSelectedValue() != null) {
				schueler.moveOut(listIn.getSelectedValuesList());
			}
		});
		
		GroupLayout gl_neuer_Schueler = new GroupLayout(dialog.getContentPane());
		gl_neuer_Schueler.setHorizontalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblName)
				.addComponent(txtName)
				.addComponent(lblFach)
				.addComponent(txtFach)
				.addComponent(lblJahr)
				.addComponent(lblLehrer)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, GroupLayout.DEFAULT_SIZE)
				.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnAbbr))
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
				.addComponent(lblZuordnen)
				.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(scrollListIn, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnOut, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollListOut, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
				)
			)
			.addContainerGap()
		);
		
		gl_neuer_Schueler.setVerticalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblFach)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtFach)
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
			.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(lblZuordnen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollListIn)
						.addGroup(gl_neuer_Schueler.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addComponent(btnIn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOut)
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGap(0)
						)
						.addComponent(scrollListOut)
					)
				)
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
	public String gebeNeuFach() {
		return neuFach;
	}
	
	@Override
	public Benutzer gebeNeulehrer() {
		return neuLehrer;
	}
}
