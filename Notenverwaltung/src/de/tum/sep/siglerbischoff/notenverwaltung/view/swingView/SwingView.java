package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;

import de.tum.sep.siglerbischoff.notenverwaltung.controller.BenutzerManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.KlassenManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.KursManager;
import de.tum.sep.siglerbischoff.notenverwaltung.controller.SchuelerdatenManager;
import de.tum.sep.siglerbischoff.notenverwaltung.dao.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.View;

public class SwingView extends JFrame implements View {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JComboBox<Integer> cmbboxJahr;
	private JLabel lblHerzlichWillkommen;
	private JPanel pnlContent;
	
	private EventListenerList listeners;

	public SwingView() {
		setTitle("Notenmanager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 240, 240));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setPreferredSize(new Dimension(700, 600));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		listeners = new EventListenerList();

		JPanel pnlHeader = new JPanel();
		contentPane.add(pnlHeader, BorderLayout.NORTH);

		lblHerzlichWillkommen = new JLabel("TEST");
		lblHerzlichWillkommen.setFont(new Font("Tahoma", Font.BOLD, 16));
		JLabel lblJahr = new JLabel("Schuljahr:");
		cmbboxJahr = new JComboBox<>();
		
		GroupLayout gl_pnlHeader = new GroupLayout(pnlHeader);
		gl_pnlHeader.setHorizontalGroup(gl_pnlHeader.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblHerzlichWillkommen)
			.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(lblJahr)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(cmbboxJahr, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		gl_pnlHeader.setVerticalGroup(gl_pnlHeader.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_pnlHeader.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblHerzlichWillkommen)
				.addComponent(lblJahr)
				.addComponent(cmbboxJahr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap()
		);
		pnlHeader.setLayout(gl_pnlHeader);

		pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(0, 8, 0, 8));
		contentPane.add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new BorderLayout(0, 0));

		JPanel pnlFooter = new JPanel();
		contentPane.add(pnlFooter, BorderLayout.SOUTH);

		JButton button = new JButton("");
		button.setIcon(
				new ImageIcon(SwingView.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png")));
		
		GroupLayout gl_pnlFooter = new GroupLayout(pnlFooter);
		gl_pnlFooter.setHorizontalGroup(gl_pnlFooter.createSequentialGroup()
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		gl_pnlFooter.setVerticalGroup(gl_pnlFooter.createSequentialGroup()
			.addGap(8)
			.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
			.addGap(8)
		);
		pnlFooter.setLayout(gl_pnlFooter);
	}

	@Override
	public LoginView getLoginView() {
		return new SwingLoginView();
	}

	@Override
	public void loginBenutzer(Benutzer benutzer, ComboBoxModel<Integer> jahre) throws DatenbankFehler {

		lblHerzlichWillkommen.setText("Herzlich willkommen, " + benutzer.getName() + "!");

		int jahr = Calendar.getInstance().get(Calendar.YEAR);
		cmbboxJahr.setModel(jahre);
		cmbboxJahr.setSelectedItem(new Integer(jahr));

		ListModel<Klasse> klassen = benutzer.gebeGeleiteteKlassen(jahr);
		ListModel<Kurs> kurse = benutzer.gebeKurse(jahr);

		boolean istAdmin = benutzer.istAdmin();
		boolean istKlassenleiter = klassen.getSize() > 0;
		boolean istKursleiter = kurse.getSize() > 0;
		boolean tabs = (istAdmin && istKlassenleiter) || (istAdmin && istKursleiter)
				|| (istKlassenleiter && istKursleiter);

		if (!(istAdmin || istKlassenleiter || istKursleiter)) {
			JLabel lblLeer = new JLabel("<html><center>Sie sind leider weder Admin, <br />"
					+ "noch haben Sie Klassen oder Kurse.</center></html>");
			lblLeer.setHorizontalAlignment(SwingConstants.CENTER);
			pnlContent.add(lblLeer);
			lblLeer.setHorizontalTextPosition(SwingConstants.CENTER);
		}

		JTabbedPane tabbedPane = null;
		if (tabs) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		}

		JPanel pnlAdmin = null;
		JPanel pnlKlassen = null;
		JPanel pnlKurse = null;

		if (istAdmin) {
			pnlAdmin = new JPanel();
			
			JButton btnSchlerdaten = new JButton("Sch\u00FClerdaten...");
			btnSchlerdaten.setActionCommand(COMMAND_SCHUELERDATEN);

			JButton btnBenutzerverwaltung = new JButton("Benutzerverwaltung...");
			btnBenutzerverwaltung.setActionCommand(COMMAND_BENUTZERVERWALTUNG);

			JButton btnKlassenAnlegen = new JButton("Klassen anlegen...");
			btnKlassenAnlegen.setActionCommand(COMMAND_KLASSEN_ANLEGEN);

			JButton btnKurseAnlegen = new JButton("Kurse anlegen...");
			btnKurseAnlegen.setActionCommand(COMMAND_KURSE_ANLEGEN);
			
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				btnSchlerdaten.addActionListener(l);
				btnBenutzerverwaltung.addActionListener(l);
				btnKlassenAnlegen.addActionListener(l);
				btnKurseAnlegen.addActionListener(l);
			}
			
			GroupLayout gl_pnlAdmin = new GroupLayout(pnlAdmin);
			gl_pnlAdmin.setHorizontalGroup(gl_pnlAdmin.createSequentialGroup()
				.addContainerGap()
				.addGroup(gl_pnlAdmin.createParallelGroup(Alignment.TRAILING, false)
					.addComponent(btnKurseAnlegen, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,	GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnKlassenAnlegen, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnSchlerdaten, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnBenutzerverwaltung, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			);
			gl_pnlAdmin.setVerticalGroup(gl_pnlAdmin.createSequentialGroup()
				.addContainerGap()
				.addComponent(btnSchlerdaten)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(btnBenutzerverwaltung)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(btnKlassenAnlegen)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(btnKurseAnlegen)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			);
			pnlAdmin.setLayout(gl_pnlAdmin);

			if (tabs) {
				tabbedPane.add("Admin", pnlAdmin);
			} else {
				pnlContent.add(pnlAdmin, BorderLayout.CENTER);
			}
		}

		if (istKlassenleiter) {
			pnlKlassen = new JPanel();

			JScrollPane scrollPane = new JScrollPane();

			JButton btnNewButton = new JButton("Speichern unter...");

			JLabel lblZeugnisse = new JLabel("Zeugnisse: ");

			JLabel lblIhreGeleitetenKlassen = new JLabel("Ihre geleiteten Klassen: ");

			JButton btnZeugnisbemerkungen = new JButton("Zeugnisbemerkungen...");
			btnZeugnisbemerkungen.setEnabled(false);

			JButton btnGefhrdungenAnzeigen = new JButton("Gef\u00E4hrdungen anzeigen...");

			JButton btnNotenAnzeigen = new JButton("Noten anzeigen...");
			btnNotenAnzeigen.setEnabled(false);
			GroupLayout gl_pnlKlassen = new GroupLayout(pnlKlassen);
			gl_pnlKlassen.setHorizontalGroup(gl_pnlKlassen.createSequentialGroup()
				.addContainerGap()
				.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING, false)
					.addComponent(lblIhreGeleitetenKlassen)
					.addGroup(gl_pnlKlassen.createSequentialGroup()
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnNotenAnzeigen)
							.addComponent(btnGefhrdungenAnzeigen)
							.addComponent(btnNewButton)
							.addComponent(lblZeugnisse)
							.addComponent(btnZeugnisbemerkungen))))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			);
			gl_pnlKlassen.setVerticalGroup(gl_pnlKlassen.createSequentialGroup()
				.addContainerGap()
				.addComponent(lblIhreGeleitetenKlassen)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING)
					.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
					.addGroup(gl_pnlKlassen.createSequentialGroup()
						.addComponent(btnZeugnisbemerkungen)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnNotenAnzeigen)
						.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnGefhrdungenAnzeigen)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblZeugnisse)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnNewButton)))
				.addContainerGap()
			);

			JList<Klasse> list = new JList<>();
			list.setModel(klassen);
			scrollPane.setViewportView(list);
			pnlKlassen.setLayout(gl_pnlKlassen);

			if (tabs) {
				tabbedPane.add("Klassen", pnlKlassen);
			} else {
				pnlContent.add(pnlKlassen, BorderLayout.CENTER);
			}
		}

		if (istKursleiter) {
			pnlKurse = new JPanel();

			JButton btnNotenAnzeigen_1 = new JButton("Noten anzeigen");
			btnNotenAnzeigen_1.setEnabled(false);

			JButton btnKlassenarbeitEintagen = new JButton("Klassenarbeit eintragen");

			JButton button_2 = new JButton("Einzelnote");
			button_2.setEnabled(false);

			JScrollPane scrollPane_1 = new JScrollPane();

			JLabel lblIhreKurse = new JLabel("Ihre Kurse: ");
			GroupLayout gl_pnlKurse = new GroupLayout(pnlKurse);
			gl_pnlKurse.setHorizontalGroup(gl_pnlKurse.createSequentialGroup()
				.addContainerGap()
				.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING)
					.addComponent(lblIhreKurse)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnNotenAnzeigen_1, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
						.addComponent(button_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addComponent(btnKlassenarbeitEintagen, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(232, Short.MAX_VALUE)
			);
			gl_pnlKurse.setVerticalGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING).addGroup(gl_pnlKurse
					.createSequentialGroup().addContainerGap().addComponent(lblIhreKurse)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlKurse.createSequentialGroup().addComponent(button_2)
									.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNotenAnzeigen_1)
									.addPreferredGap(ComponentPlacement.RELATED, 310, Short.MAX_VALUE)
									.addComponent(btnKlassenarbeitEintagen))
							.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 406,
									Short.MAX_VALUE))
					.addContainerGap()));

			JList<Kurs> list_1 = new JList<>();
			list_1.setModel(kurse);
			scrollPane_1.setViewportView(list_1);
			pnlKurse.setLayout(gl_pnlKurse);

			if (tabs) {
				tabbedPane.add("Kurse", pnlKurse);
			} else {
				pnlContent.add(pnlKurse, BorderLayout.CENTER);
			}
		}

		if (tabs) {
			pnlContent.add(tabbedPane, BorderLayout.CENTER);
		}
				
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void showError(Throwable e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), "Fehler!", JOptionPane.ERROR_MESSAGE);
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
	public SchuelerdatenView getSchuelerdatenView(SchuelerdatenManager schuelerdatenManager) {
		return new SwingSchuelerdatenView(this);
	}

	@Override
	public BenutzerverwaltungView getBenutzerverwaltungView(BenutzerManager benutzerManager) {
		return new SwingBenutzerverwaltungView(this);
	}

	@Override
	public KlassenverwaltungView getKlassenverwaltungView(KlassenManager klassenManager) {
		return new SwingKlassenverwaltungView(this);
	}

	@Override
	public KursverwaltungView getKursverwaltungView(KursManager kursManager) {
		return new SwingKursverwaltungView(this);
	}
}
