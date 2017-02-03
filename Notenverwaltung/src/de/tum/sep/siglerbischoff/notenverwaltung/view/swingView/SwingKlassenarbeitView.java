package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;
import de.tum.sep.siglerbischoff.notenverwaltung.view.KlassenarbeitView;

class SwingKlassenarbeitView extends JDialog implements KlassenarbeitView {
	
	private static final long serialVersionUID = 1L;
	
	private Kurs kurs;
	
	private EventListenerList listeners;
	
	private List<Integer> werte;
	private List<Schueler> schueler;
	private JSpinner sprDatum; 
	private JSpinner sprGewichtung;
	private JComboBox<String> txtArt; 
	private JTextField txtKommentar;

	SwingKlassenarbeitView(JFrame parent, ListModel<Schueler> schueler, Kurs kurs) {
		super(parent, "Klassenarbeit eintragen", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.kurs = kurs;
		
		listeners = new EventListenerList();
		
		werte = new Vector<>();
		this.schueler = new Vector<>();
		for(int i = 0; i < schueler.getSize(); i++) {
			werte.add(-1);
			this.schueler.add(schueler.getElementAt(i));
		}
		
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
		
		TableModel tableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getRowCount() { return schueler.getSize(); }

			@Override
			public int getColumnCount() { return 2; }

			@Override
			public String getColumnName(int columnIndex) { return columnIndex == 0 ? "Schüler" : "Wert"; }

			@Override
			public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Schueler.class : Integer.class; }

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) { return columnIndex == 1; }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if(columnIndex == 0) {
					return schueler.getElementAt(rowIndex);
				} else {
					return werte.get(rowIndex);
				}
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if(aValue instanceof Integer) {
					werte.set(rowIndex, (Integer) aValue);
				}
			}
		};
		JTable table = new JTable(tableModel);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setCellEditor(new NotenCellEditor());
		table.setFillsViewportHeight(true);
		table.setRowHeight(20);
		JScrollPane scrollpane = new JScrollPane(table);
		
		JButton btnSpeichern = new JButton("Speichern");
		btnSpeichern.setActionCommand(COMMAND_NOTEN_EINTRAGEN);
		btnSpeichern.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
			//TODO Der Dialog sollte vom Controller geschlossen werden, nur falls die Angaben korrekt sind.
			dispose();
		});
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setActionCommand(COMMAND_ABBRECHEN);
		btnAbbrechen.addActionListener(ae -> {
			for(ActionListener l : listeners.getListeners(ActionListener.class)) {
				l.actionPerformed(ae);
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
				.addComponent(lblDatum)
				.addComponent(sprDatum)
				.addComponent(lblGewichtung)
				.addComponent(sprGewichtung)
				.addComponent(lblArt)
				.addComponent(txtArt)
				.addComponent(lblKommentar)
				.addComponent(txtKommentar)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(btnSpeichern)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbrechen)
				)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(scrollpane, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
			.addContainerGap()
		);
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblDatum)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sprDatum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblGewichtung)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sprGewichtung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblArt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtArt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblKommentar)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtKommentar)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSpeichern)
						.addComponent(btnAbbrechen))
				)
				.addComponent(scrollpane, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
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
	public List<Integer> gebeNeuWerte() {
		return werte;
	}

	@Override
	public Date gebeNeuDatum() {
		return (Date) sprDatum.getValue();
	}

	@Override
	public double gebeNeuGewichtung() {
		return (double) sprGewichtung.getValue();
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
	public List<Schueler> gebeNeuSchueler() {
		return schueler;
	}
	
	private static class NotenCellEditor extends AbstractCellEditor implements TableCellEditor {
		
		private static final long serialVersionUID = 1L;
		private JComboBox<Integer> cmbBox;
		
		public NotenCellEditor() {
			cmbBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,-1});
			((JLabel) cmbBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		public Object getCellEditorValue() {
			return cmbBox.getSelectedItem();
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			cmbBox.setSelectedItem(value);
			return cmbBox;
		}
	}

}
