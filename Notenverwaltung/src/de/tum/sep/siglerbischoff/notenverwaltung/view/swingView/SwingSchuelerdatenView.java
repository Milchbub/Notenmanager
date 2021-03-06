package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

public class SwingSchuelerdatenView extends JDialog implements SchuelerdatenView {
	
	private static final long serialVersionUID = 1L;
	
	private Component parent;
	
	private EventListenerList listeners;
	private List<JButton> buttons;
	
	private JTable schuelerTable;
	
	public SwingSchuelerdatenView(JFrame parent, TableModel schueler) {
		super(parent, "Schülerdaten", true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.parent = parent;
		
		listeners = new EventListenerList();
		buttons = new Vector<>();
		
		JLabel lblAlleSchler = new JLabel("Alle Sch\u00FCler: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300,200));
		
		schuelerTable = new JTable(schueler);
		schuelerTable.setAutoCreateRowSorter(true);
		schuelerTable.setFillsViewportHeight(true);
		schuelerTable.getTableHeader().setReorderingAllowed(false);
		
		schuelerTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		schuelerTable.getColumnModel().getColumn(1).setPreferredWidth(600);
		schuelerTable.getColumnModel().getColumn(2).setPreferredWidth(300);
		
		schuelerTable.getColumnModel().getColumn(2).setCellRenderer(new DateCellRenderer());
		schuelerTable.getColumnModel().getColumn(2).setCellEditor(new DateCellEditor());
		
		if (schueler.getRowCount() > 0) {
			scrollPane.setViewportView(schuelerTable);
		} else {
			scrollPane.setViewportView(new JLabel(" Keine Sch\u00FCler eingetragen..."));
		}
		
		schueler.addTableModelListener(te -> {
			if (schueler.getRowCount() > 0) {
				scrollPane.setViewportView(schuelerTable);
			} else {
				scrollPane.setViewportView(new JLabel(" Keine Sch\u00FCler eingetragen..."));
			}
		});
		
		JButton btnHinzufuegen = new JButton("Sch\u00FCler hinzuf\u00FCgen...");
		btnHinzufuegen.setActionCommand(COMMAND_NEU);
		buttons.add(btnHinzufuegen);

		JButton btnLoeschen = new JButton("Sch\u00FCler loschen");
		btnLoeschen.setActionCommand(COMMAND_LOESCHEN);
		buttons.add(btnLoeschen);
		
		JButton btnOk = new JButton("Ok");
		btnOk.setActionCommand(COMMAND_SCHLIESSEN);
		buttons.add(btnOk);
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleSchler)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(gl_kursVerwaltung.createSequentialGroup()
					.addComponent(btnHinzufuegen)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnLoeschen))
				.addComponent(btnOk)
			)
			.addContainerGap()
		);
		gl_kursVerwaltung.setVerticalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblAlleSchler)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
					.addComponent(btnHinzufuegen)
					.addComponent(btnLoeschen))
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnOk)
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
		for(JButton button : buttons) {
			button.addActionListener(l);
		}
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listeners.remove(ActionListener.class, l);
		for(JButton button : buttons) {
			button.removeActionListener(l);
		}
	}

	private String neuName;
	private Date neuDatum;
	
	@Override
	public void neu() {
		JDialog dialog = new JDialog(this, "Neuer Sch\u00FCler");
		
		JLabel lblName = new JLabel("Name: ");
		JTextField txtName = new JTextField();
		JLabel lblDatum = new JLabel("Geburtsdatum: ");
		
		Calendar cal = Calendar.getInstance();
	    Date heute = cal.getTime();
	    cal.add(Calendar.YEAR, -100);
	    Date fruehestes = cal.getTime();
		JSpinner sprDatum = new JSpinner(new SpinnerDateModel(heute, fruehestes, heute, Calendar.YEAR));
		sprDatum.setEditor(new DateEditor(sprDatum, "dd.MM.yyyy"));
		
		JButton btnOk = new JButton("Ok");
		btnOk.setActionCommand(COMMAND_NEU_FERTIG);
		btnOk.addActionListener(ae -> {
			neuName = txtName.getText();
			neuDatum = (Date) sprDatum.getValue();
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
				.addComponent(lblDatum)
				.addComponent(sprDatum)
				.addGroup(gl_neuer_Schueler.createSequentialGroup()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnAbbr))
			)
			.addContainerGap());
		
		gl_neuer_Schueler.setVerticalGroup(gl_neuer_Schueler.createSequentialGroup()
			.addContainerGap()
			.addComponent(lblName)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(txtName)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(lblDatum)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(sprDatum)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addGroup(gl_neuer_Schueler.createParallelGroup(Alignment.LEADING)
				.addComponent(btnOk)
				.addComponent(btnAbbr))
			.addContainerGap());
		
		dialog.setLayout(gl_neuer_Schueler);
		dialog.getRootPane().setDefaultButton(btnOk);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	@Override
	public String gebeNeuName() {
		return neuName;
	}

	@Override
	public Date gebeNeuGebDat() {
		return neuDatum;
	}
	
	@Override
	public int gebeMarkierteZeile() {
		return schuelerTable.getRowSorter().convertRowIndexToModel(schuelerTable.getSelectedRow());
	}
	
	private static class DateCellRenderer extends DefaultTableCellRenderer {

	    private static final long serialVersionUID = 1L;
		private SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");

	    public Component getTableCellRendererComponent(JTable table,
	            Object value, boolean isSelected, boolean hasFocus,
	            int row, int column) {
	        value = f.format(value);
	        return super.getTableCellRendererComponent(table, value, isSelected,
	                hasFocus, row, column);
	    }
	}
	
	private static class DateCellEditor extends AbstractCellEditor implements TableCellEditor {
		
		private static final long serialVersionUID = 1L;
		private JSpinner spinner;
		
		public DateCellEditor() {
		    Calendar calendar = Calendar.getInstance();
		    Date heute = calendar.getTime();
		    calendar.add(Calendar.YEAR, -100);
		    Date fruehestes = calendar.getTime();
			SpinnerModel dateModel = new SpinnerDateModel(
					heute, fruehestes, heute, Calendar.DAY_OF_MONTH);
			spinner = new JSpinner(dateModel);
			spinner.setEditor(new JSpinner.DateEditor(spinner, "dd.MM.yyyy"));
		}
		
		public Object getCellEditorValue() {
			return ((SpinnerDateModel)spinner.getModel()).getDate();
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			spinner.setValue(value);
			return spinner;
		}
	}
}