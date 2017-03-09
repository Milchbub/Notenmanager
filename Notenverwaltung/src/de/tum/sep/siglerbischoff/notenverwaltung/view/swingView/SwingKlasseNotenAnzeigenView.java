package de.tum.sep.siglerbischoff.notenverwaltung.view.swingView;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.TableCellRenderer;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel.KursNoten;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Note;
import de.tum.sep.siglerbischoff.notenverwaltung.view.View;

class SwingKlasseNotenAnzeigenView extends JDialog implements View {

	private static final long serialVersionUID = 1L;

	public SwingKlasseNotenAnzeigenView(JFrame parent, KlasseNotenModel noten,
			Klasse klasse) {
		super(parent, "Notendurchschnitte der Klasse " + klasse.gebeName(), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		JTable table = new JTable(noten);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(KursNoten.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus,
					int row, int column) {
				KursNoten kn = (KursNoten) value;
				Component renderer = table.getDefaultRenderer(String.class)
					.getTableCellRendererComponent(table, kn, isSelected, 
							hasFocus, row, column);
				Iterator<Note> it = kn.gebeNoten().iterator();
				if(!it.hasNext()) {
					return renderer;
				} else {
					String text = "<html><p>";
					while (it.hasNext()) {
						text += it.next() + (it.hasNext() ? "<br />" : "");
					}
					((JComponent) renderer).setToolTipText(text + "</p></html>");
					return renderer;
				}
			}
		});
		scrollPane.setViewportView(table);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(ae -> {
			dispose();
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, 600, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(btnOk))
			.addContainerGap()
		);
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(scrollPane, 300, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(btnOk)
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
	public void addActionListener(ActionListener l) {}

	@Override
	public void removeActionListener(ActionListener l) {}

}
