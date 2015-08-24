/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AnalysesController.AddAnalysesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AnalysesController.DeleteAnalysesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.ResultsController.ResultsColumnListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.ResultsController.ResultsRowListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class AnalysesView extends JComponent {

	private JPanel panel, buttonPanel;
	private JButton addAnalyses, deleteAnalyses;
	private JTable analysesTable;
	private JScrollPane analysesScrollPane;
	private JFileChooser chooser = new JFileChooser("Choose analysis XMLs");
	
	
	public AnalysesView() {
		// main panel
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("Panel for analyses records/XMLs"));
		// sub-panel for buttons
		addAnalyses = new JButton("Add analyses...");
		deleteAnalyses = new JButton("Delete analyses...");
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(addAnalyses);
		buttonPanel.add(deleteAnalyses);
		panel.add(buttonPanel);
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
        analysesScrollPane = new JScrollPane(analysesTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
	
	/**
	 * Add an ActionListener to add analyses
	 * @param listener
	 */
	public void addAddAnalysesListener(AddAnalysesListener listener){
		addAnalyses.addActionListener(listener);
	}

	/**
	 * Add an ActionListener to delete analyses
	 * @param listener
	 */
	public void addDeleteAnalysesListener(DeleteAnalysesListener listener){
		deleteAnalyses.addActionListener(listener);
	}
}
