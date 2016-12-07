package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.view.SchuelerdatenView;

public class SwingSchuelerdatenView extends JDialog implements SchuelerdatenView, WindowListener {
	
	private static final long serialVersionUID = 1L;
	
	private Component parent;
	
	private JButton btnSpeichern;

	public SwingSchuelerdatenView(Component parent, TableModel schueler) {
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		this.parent = parent;
		
		JLabel lblAlleSchler = new JLabel("Alle Sch\u00FCler: ");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300,200));
		
		JTable schuelerTable = new JTable(schueler);
		schuelerTable.setFillsViewportHeight(true);
		schuelerTable.getColumnModel().getColumn(2).setCellRenderer(new DateCellRenderer());
		schuelerTable.getColumnModel().getColumn(2).setCellEditor(new DateCellEditor());
		
		if (schueler.getRowCount() > 0) {
			scrollPane.setViewportView(schuelerTable);
		} else {
			scrollPane.setViewportView(new JLabel(" Keine Sch\u00FCler eingetragen..."));
		}
		
		JButton btnHinzufuegen = new JButton("Sch\u00FCler hinzuf\u00FCgen...");
		btnSpeichern = new JButton("Änderungen speichern");
		
		GroupLayout gl_kursVerwaltung = new GroupLayout(getContentPane());
		gl_kursVerwaltung.setHorizontalGroup(gl_kursVerwaltung.createSequentialGroup()
			.addContainerGap()
			.addGroup(gl_kursVerwaltung.createParallelGroup(Alignment.LEADING)
				.addComponent(lblAlleSchler)
				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(gl_kursVerwaltung.createSequentialGroup()
					.addComponent(btnHinzufuegen)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(btnSpeichern))
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
					.addComponent(btnSpeichern))
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
		setVisible(false);
	}

	@Override
	public void showError(String titel, String nachricht) {
		JOptionPane.showMessageDialog(this, nachricht, titel, JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void addActionListener(ActionListener l) {
		btnSpeichern.addActionListener(l);
	}
	
	private static class DateCellRenderer extends DefaultTableCellRenderer {

	    private static final long serialVersionUID = 1L;
		private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

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
		    Date initDate = calendar.getTime();
		    calendar.add(Calendar.YEAR, -100);
		    Date earliestDate = calendar.getTime();
		    calendar.add(Calendar.YEAR, 200);
		    Date latestDate = calendar.getTime();
			SpinnerModel dateModel = new SpinnerDateModel(
					initDate, earliestDate,	latestDate, Calendar.YEAR);
			spinner = new JSpinner(dateModel);
			spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
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

	//TODO WindowListener hier gefällt mir nicht besonders...
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		int i = JOptionPane.showConfirmDialog(this, 
				"Möchten Sie Ihre Änderungen speichern? ", 
				"Änderungen speichern? ", JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE);
		if (i == JOptionPane.YES_OPTION) {
			btnSpeichern.doClick();
		} else if (i == JOptionPane.NO_OPTION) {
			dispose();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
