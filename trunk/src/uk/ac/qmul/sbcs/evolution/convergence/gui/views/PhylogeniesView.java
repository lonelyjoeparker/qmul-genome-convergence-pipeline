package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;

public class PhylogeniesView extends JComponent {
	private JPanel mainPanel, selectionPanel, displayPanel;
	private JTextArea textTreeDisplay;
	private JTable phylogeniesTable;
	private JScrollPane phylogenyTableScrollPane;
	private JFileChooser chooser = new JFileChooser("Choose a phylogeny");
	
	/**
	 * Default no-arg constructor
	 */
	public PhylogeniesView(){
		mainPanel = new JPanel(new GridLayout(2,1));
		selectionPanel = new JPanel(new GridLayout(2,1));
		selectionPanel.add(new JLabel("Selection/table here"));
		displayPanel = new JPanel(new GridLayout(2,1));
		displayPanel.add(new JLabel("Phylogeny display here"));
		textTreeDisplay = new JTextArea("Trees as text strings");
		textTreeDisplay.setColumns(35);
		displayPanel.add(textTreeDisplay);
		mainPanel.add(selectionPanel);
		mainPanel.add(displayPanel);
	}
	
	/**
	 * Update the display of the current tree (text/string display currently)
	 * @param updateTextTreeString
	 */
	public void setTextTreeDisplay(String updateTextTreeString){
		textTreeDisplay.setText(updateTextTreeString);
	}
	
	/**
	 * Get a JComponent to be added to the GlobalView (main JFrame)
	 * @return
	 */
	public JPanel getPanel(){
		return mainPanel;
	}
	
	/**
	 * Get a file chooser.
	 * @return
	 */
	public JFileChooser getFileChooser(){
		return chooser;
	}
	
	/**
	 * Sets the TableModel used to pick / update phylogenies list
	 * @param newTableModel a PhylogenyModel (superclass AbstractTableModel)
	 */
	public void addTable(PhylogeniesModel newTableModel){
		phylogeniesTable = new JTable(newTableModel);
		phylogenyTableScrollPane = new JScrollPane(phylogeniesTable);
		phylogenyTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		phylogenyTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		selectionPanel.add(phylogenyTableScrollPane);
	}
}
