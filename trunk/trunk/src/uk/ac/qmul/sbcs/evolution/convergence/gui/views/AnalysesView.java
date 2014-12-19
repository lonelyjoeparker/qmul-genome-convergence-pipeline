/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.ResultsController.ResultsColumnListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.ResultsController.ResultsRowListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class AnalysesView extends JComponent {

	private JPanel panel;
	private JTable analysesTable;
	private JScrollPane analysesScrollPane;
	private JFileChooser chooser = new JFileChooser("Choose analysis XMLs");
	
	
	public AnalysesView() {
		panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Panel for analyses records/XMLs"));
	}

	public JTable getTable(){
		return analysesTable;
	}
	
	/**
	 * Sets the TableModel used to pick / update analyses list
	 * @param newTableModel a AnalysesModel (superclass AbstractTableModel)
	 */
	public void addTable(AnalysesModel analysesModel) {
        analysesTable = new JTable(analysesModel);
        analysesScrollPane = new JScrollPane(analysesTable);
		analysesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		analysesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(analysesScrollPane);
 	}

	public void addListRowSelectionListener(ResultsRowListener rl){
        analysesTable.getSelectionModel().addListSelectionListener(rl);
	}

	public void addListColumnSelectionListener(ResultsColumnListener cl){
        analysesTable.getColumnModel().getSelectionModel().addListSelectionListener(cl);
	}

	/**
	 * Get a JComponent to be added to the GlobalView (main JFrame)
	 * @return
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Get a file chooser.
	 * @return
	 */
	public JFileChooser getFileChooser(){
		return chooser;
	}	
}
