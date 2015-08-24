package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.ResultsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe.WireframeResultsController.ColumnListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe.WireframeResultsController.RowListener;

public class WireframeResultsPanel extends JComponent {
	private JPanel panel;
	private JFileChooser fc = new JFileChooser();
	private JButton addResults;
	private JTable resultsTable;
	private JScrollPane resultsScrollPane;
	
	public WireframeResultsPanel() {
//		panel = new JPanel(new GridLayout(3, 1));
		panel = new JPanel(new FlowLayout());
		JLabel filler = new JLabel("results a sub panel");
		filler.setHorizontalAlignment(JLabel.LEFT);
		filler.setVerticalAlignment(JLabel.TOP);
		panel.add(filler);
		addResults = new JButton("Add results...");
		panel.add(addResults);
		/*
			setVisible(true);
			setOpaque(true);
		 *	not needed as I think Visible / Opaque should propagate through all the components from main JFrame..
		 */
	}

	public JTable getTable(){
		return resultsTable;
	}
	
	public JPanel getPanel(){
		return panel;
	}
	
	public void addResultsButtonListener(ActionListener al){
		addResults.addActionListener(al);
	}

	public void addTable(ResultsTableModel resultsModel) {
        // Init resultsTable
        resultsTable = new JTable(resultsModel);
        resultsTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowSelectionAllowed(true);
        resultsTable.setColumnSelectionAllowed(true);
        resultsTable.setCellSelectionEnabled(true);
        resultsTable.setAutoCreateRowSorter(true);
		resultsScrollPane = new JScrollPane(resultsTable);
		resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(resultsScrollPane);
 	}

	public void addListRowSelectionListener(RowListener rl){
        resultsTable.getSelectionModel().addListSelectionListener(rl);
	}

	public void addListColumnSelectionListener(ColumnListener cl){
        resultsTable.getColumnModel().getSelectionModel().addListSelectionListener(cl);
	}

	public JFileChooser getFileChooser() {
		return fc;
	}
}
