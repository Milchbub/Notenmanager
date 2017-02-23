package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.ComboBoxEditor;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlassenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KursNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KurseModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.BenutzerdatenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenarbeitView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KursverwaltungView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.LoginView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.MainView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.NotenHinzufuegenView;
import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

public class SwingMainView extends JFrame implements MainView {

	private static final long serialVersionUID = 1L;

	private JComboBox<Integer> cmbboxJahr;
	private JLabel lblHerzlichWillkommen;
	private JPanel pnlContent;
	
	private EventListenerList listeners;

	private JList<Klasse> listKlasse;
	private JList<Kurs> listKurse;

	public SwingMainView() {
		super("Notenmanager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//TODO
		/*addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				for(ActionListener l : listeners.getListeners(ActionListener.class)) {
					l.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, MainView.COMMAND_UPDATE));
				}
			}
		});*/
		
		GroupLayout gl_content = new GroupLayout(getContentPane()); 
		
		listeners = new EventListenerList();

		JPanel pnlHeader = new JPanel();

		lblHerzlichWillkommen = new JLabel("TEST");
		lblHerzlichWillkommen.setFont(new Font("Tahoma", Font.BOLD, 16));
		JLabel lblJahr = new JLabel("Schuljahr:");
		cmbboxJahr = new JComboBox<>();
		
		GroupLayout gl_pnlHeader = new GroupLayout(pnlHeader);
		gl_pnlHeader.setHorizontalGroup(gl_pnlHeader.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblHerzlichWillkommen)
			.addPreferredGap(ComponentPlacement.UNRELATED, 30, Short.MAX_VALUE)
			.addComponent(lblJahr)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(cmbboxJahr, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		gl_pnlHeader.setVerticalGroup(gl_pnlHeader.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_pnlHeader.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblHerzlichWillkommen)
				.addComponent(lblJahr)
				.addComponent(cmbboxJahr))
			.addContainerGap()
		);
		pnlHeader.setLayout(gl_pnlHeader);

		pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(0, 8, 0, 8));
		pnlContent.setLayout(new BorderLayout(0, 0));

		JPanel pnlFooter = new JPanel();

		JButton button = new JButton("");
		button.setIcon(new ImageIcon(
				SwingMainView.class.
				getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png")));
		
		GroupLayout gl_pnlFooter = new GroupLayout(pnlFooter);
		gl_pnlFooter.setHorizontalGroup(gl_pnlFooter.createSequentialGroup()
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		gl_pnlFooter.setVerticalGroup(gl_pnlFooter.createSequentialGroup()
			.addContainerGap()
			.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()
		);
		pnlFooter.setLayout(gl_pnlFooter);
		
		gl_content.setHorizontalGroup(gl_content.createSequentialGroup()
			.addGroup(gl_content.createParallelGroup(Alignment.LEADING)
				.addComponent(pnlHeader)
				.addComponent(pnlContent, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
				.addComponent(pnlFooter))
		);
		gl_content.setVerticalGroup(gl_content.createSequentialGroup()
			.addComponent(pnlHeader)
			.addComponent(pnlContent, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
			.addComponent(pnlFooter)
		);
		getContentPane().setLayout(gl_content);
	}

	@Override
	public void zeigen() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void schliessen() {
		dispose();
	}

	@Override
	public void loginBenutzer(Benutzer benutzer, Jahre jahre) throws DatenbankFehler {

		lblHerzlichWillkommen.setText("Herzlich willkommen, " + benutzer.gebeName() + "!");

		cmbboxJahr.setModel(jahre);
		cmbboxJahr.setEditable(true);
		cmbboxJahr.setSelectedItem(jahre.gebeLetztesAktuellesJahr());
		cmbboxJahr.setEditor(new JahreEditor(jahre.gebeLetztesAktuellesJahr()));
		cmbboxJahr.setActionCommand(COMMAND_JAHR_GEAENDERT);
		cmbboxJahr.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
	}
	
	@Override
	public void updateContent(Benutzer benutzer, ListModel<Klasse> geleiteteKlassen, 
			ListModel<Kurs> geleiteteKurse) {
		pnlContent.removeAll();
		
		boolean istAdmin = benutzer.istAdmin();
		boolean istKlassenleiter = geleiteteKlassen.getSize() > 0;
		boolean istKursleiter = geleiteteKurse.getSize() > 0;
		boolean tabs = (istKlassenleiter && istKursleiter) || (istAdmin && istKursleiter)
			|| (istAdmin && istKlassenleiter);

		if (!(istKursleiter || istKlassenleiter || istAdmin)) {
			JLabel lblLeer = new JLabel("<html><center>Sie sind im ausgewählten Jahr "
					+ "leider weder Admin, <br />"
					+ "noch haben Sie Klassen oder Kurse.</center></html>");
			lblLeer.setHorizontalAlignment(SwingConstants.CENTER);
			pnlContent.add(lblLeer);
			lblLeer.setHorizontalTextPosition(SwingConstants.CENTER);
		} else {

			JTabbedPane tabbedPane = null;
			if (tabs) {
				tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			}
	
			JPanel pnlAdmin = null;
			JPanel pnlKlassen = null;
			JPanel pnlKurse = null;
	
			if (istAdmin) {
				pnlAdmin = new JPanel();
				
				ActionListener listener = ae -> {
					for(ActionListener l : listeners.getListeners(ActionListener.class)) {
						l.actionPerformed(ae);
					}
				};
				
				JButton btnSchlerdaten = new JButton("Sch\u00FClerdaten");
				btnSchlerdaten.setActionCommand(COMMAND_SCHUELERDATEN);
				btnSchlerdaten.addActionListener(listener);
	
				JButton btnBenutzerverwaltung = new JButton("Benutzerverwaltung");
				btnBenutzerverwaltung.setActionCommand(COMMAND_BENUTZERVERWALTUNG);
				btnBenutzerverwaltung.addActionListener(listener);
	
				JButton btnKlassenAnlegen = new JButton("Klassenverwaltung");
				btnKlassenAnlegen.setActionCommand(COMMAND_KLASSEN_ANLEGEN);
				btnKlassenAnlegen.addActionListener(listener);
	
				JButton btnKurseAnlegen = new JButton("Kursverwaltung");
				btnKurseAnlegen.setActionCommand(COMMAND_KURSE_ANLEGEN);
				btnKurseAnlegen.addActionListener(listener);
				
				GroupLayout gl_pnlAdmin = new GroupLayout(pnlAdmin);
				gl_pnlAdmin.setHorizontalGroup(gl_pnlAdmin.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlAdmin.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnKurseAnlegen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(btnKlassenAnlegen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(btnSchlerdaten, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(btnBenutzerverwaltung, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
					.addContainerGap()
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
					.addContainerGap()
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

				JLabel lblIhreGeleitetenKlassen = new JLabel("Ihre geleiteten Klassen: ");
				JScrollPane scrollPane = new JScrollPane();
	
				//JButton btnNewButton = new JButton("Speichern unter...");	
				//JLabel lblZeugnisse = new JLabel("Zeugnisse: ");		
				//JButton btnZeugnisbemerkungen = new JButton("Zeugnisbemerkungen...");
				//btnZeugnisbemerkungen.setEnabled(false);	
				//JButton btnGefhrdungenAnzeigen = new JButton("Gef\u00E4hrdungen anzeigen...");
	
				JButton btnNotenAnzeigen = new JButton("Noten anzeigen...");
				btnNotenAnzeigen.setEnabled(false);
				btnNotenAnzeigen.setActionCommand(COMMAND_KLASSE_NOTEN_ANZEIGEN);
				btnNotenAnzeigen.addActionListener(ae -> {
					for(ActionListener l : listeners.getListeners(ActionListener.class)) {
						l.actionPerformed(ae);
					}
				});
				
				GroupLayout gl_pnlKlassen = new GroupLayout(pnlKlassen);
				gl_pnlKlassen.setHorizontalGroup(gl_pnlKlassen.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIhreGeleitetenKlassen)	
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnNotenAnzeigen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
					.addContainerGap()
				);
				gl_pnlKlassen.setVerticalGroup(gl_pnlKlassen.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblIhreGeleitetenKlassen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlKlassen.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
						.addComponent(btnNotenAnzeigen))
					.addContainerGap()
				);
	
				listKlasse = new JList<>(geleiteteKlassen);
				listKlasse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				listKlasse.addListSelectionListener(se -> {
					btnNotenAnzeigen.setEnabled(true);
				});
				scrollPane.setViewportView(listKlasse);
				pnlKlassen.setLayout(gl_pnlKlassen);
	
				if (tabs) {
					tabbedPane.add("Klassen", pnlKlassen);
				} else {
					pnlContent.add(pnlKlassen, BorderLayout.CENTER);
				}
			}
	
			if (istKursleiter) {
				pnlKurse = new JPanel();

				JLabel lblIhreKurse = new JLabel("Ihre Kurse: ");
				JScrollPane scrollPane = new JScrollPane();
	
				JButton btnNotenAnzeigen = new JButton("Noten anzeigen");
				btnNotenAnzeigen.setEnabled(false);
				btnNotenAnzeigen.setActionCommand(COMMAND_KURS_NOTEN_ANZEIGEN);
				btnNotenAnzeigen.addActionListener(ae -> {
					for(ActionListener l : listeners.getListeners(ActionListener.class)) {
						l.actionPerformed(ae);
					}
				});
	
				JButton btnNoteEintragen = new JButton("Einzelnote eintragen");
				btnNoteEintragen.setEnabled(false);
				btnNoteEintragen.setActionCommand(COMMAND_NOTE_EINTRAGEN);
				btnNoteEintragen.addActionListener(ae -> {
					for(ActionListener l : listeners.getListeners(ActionListener.class)) {
						l.actionPerformed(ae);
					}
				});
	
				JButton btnKlassenarbeitEintagen = new JButton("Klassenarbeit eintragen");
				btnKlassenarbeitEintagen.setEnabled(false);
				btnKlassenarbeitEintagen.setActionCommand(COMMAND_KLASSENARBEIT_EINTRAGEN);
				btnKlassenarbeitEintagen.addActionListener(ae -> {
					for(ActionListener l : listeners.getListeners(ActionListener.class)) {
						l.actionPerformed(ae);
					}
				});
				
				GroupLayout gl_pnlKurse = new GroupLayout(pnlKurse);
				gl_pnlKurse.setHorizontalGroup(gl_pnlKurse.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIhreKurse)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnNotenAnzeigen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(btnNoteEintragen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(btnKlassenarbeitEintagen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
					.addContainerGap()
				);
				gl_pnlKurse.setVerticalGroup(gl_pnlKurse.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblIhreKurse)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlKurse.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
						.addGroup(gl_pnlKurse.createSequentialGroup()
							.addComponent(btnNoteEintragen)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNotenAnzeigen)
							.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addComponent(btnKlassenarbeitEintagen))
						)
					.addContainerGap()
				);
	
				listKurse = new JList<>(geleiteteKurse);
				listKurse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				listKurse.addListSelectionListener(se -> {
					btnNotenAnzeigen.setEnabled(true);
					btnNoteEintragen.setEnabled(true);
					btnKlassenarbeitEintagen.setEnabled(true);
				});
				scrollPane.setViewportView(listKurse);
				pnlKurse.setLayout(gl_pnlKurse);
	
				if (tabs) {
					tabbedPane.add("Kurse", pnlKurse);
				} else {
					pnlContent.add(pnlKurse, BorderLayout.CENTER);
				}
			}
	
			if (tabs) {
				pnlContent.add(tabbedPane);
			}
			
		}
		pnlContent.repaint();
		pnlContent.revalidate();
		pack();
		setMinimumSize(getSize());
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
	public LoginView getLoginView() {
		return new SwingLoginView();
	}

	@Override
	public int gebeJahr() {
		if(cmbboxJahr.getSelectedItem() == null) {
			return Calendar.getInstance().get(Calendar.YEAR);
		}
		return (int) cmbboxJahr.getSelectedItem();
	}
	
	@Override
	public SchuelerdatenView getSchuelerdatenView(TableModel schueler) {
		return new SwingSchuelerdatenView(this, schueler);
	}

	@Override
	public BenutzerdatenView getBenutzerverwaltungView(TableModel benutzer) {
		return new SwingBenutzerdatenView(this, benutzer);
	}

	@Override
	public KlassenverwaltungView getKlassenverwaltungView(KlassenModel klassen) {
		return new SwingKlassenverwaltungView(this, klassen);
	}

	@Override
	public KursverwaltungView getKursverwaltungView(KurseModel kurse) {
		return new SwingKursverwaltungView(this, kurse);
	}
	
	@Override
	public NotenHinzufuegenView getNotenHinzufuegenView(ListModel<Schueler> schueler, Kurs kurs) {
		return new SwingNotenHinzufuegenView(this, schueler, kurs);
	}
	
	@Override
	public KlassenarbeitView getKlassenarbeitView(ListModel<Schueler> schueler, Kurs kurs) {
		return new SwingKlassenarbeitView(this, schueler, kurs);
	}
	
	@Override
	public void klasseNotenAnzeigen(KlasseNotenModel klasseNotenModel, Klasse selectedKlasse) {
		new SwingKlasseNotenAnzeigenView(this, klasseNotenModel, selectedKlasse).zeigen();
	}
	
	@Override
	public Klasse gebeAusgewaehlteKlasse() {
		return listKlasse.getSelectedValue();
	}

	@Override
	public void kursNotenAnzeigen(KursNotenModel kursNotenModel, ListModel<Schueler> schueler, Kurs kurs) {
		new SwingKursNotenAnzeigenView(this, kursNotenModel, schueler, kurs).zeigen();
	}

	@Override
	public Kurs gebeAusgewaehltenKurs() {
		return listKurse.getSelectedValue();
	}
	
	private class JahreEditor implements ComboBoxEditor {

		private JSpinner editor;
		private EventListenerList listeners;
		
		private JahreEditor(int letztesAktuellesJahr) {
			SpinnerModel model = new SpinnerNumberModel(letztesAktuellesJahr, 1990, 2100, 1);
			editor = new JSpinner(model);
			editor.setEditor(new JSpinner.NumberEditor(editor, "#"));
			listeners = new EventListenerList();
			editor.addChangeListener(ce -> {
				for(ActionListener l : listeners.getListeners(ActionListener.class)) {
					l.actionPerformed(new ActionEvent(editor, 0, ""));
				}
			});
		}
		
		@Override
		public Component getEditorComponent() {
			return editor;
		}

		@Override
		public void setItem(Object anObject) {
			if(anObject == null) {
				anObject = 0;
			}
			editor.setValue(anObject);
		}

		@Override
		public Object getItem() {
			return editor.getValue();
		}

		@Override
		public void selectAll() {}

		@Override
		public void addActionListener(ActionListener l) {
			listeners.add(ActionListener.class, l);
		}

		@Override
		public void removeActionListener(ActionListener l) {
			listeners.remove(ActionListener.class, l);
		}
		
	}
}
