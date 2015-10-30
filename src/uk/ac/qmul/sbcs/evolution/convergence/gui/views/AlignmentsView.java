package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsTableModel;


public class AlignmentsView extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3925504665016275161L;
	private JPanel panel, subPanel, buttonPanel;
	private JFileChooser fc = new JFileChooser();	// file chooser for single files
	private JFileChooser dc = new JFileChooser();	// file chooser for directories
	private JButton addAlignmentsButton;		// add alignment
	private JButton removeAlignmentsButton;		// remove selected alignment
	private JButton textDumpButton;				// dump all data to text at 'alignment_descriptive_stats.tdf' in the selected dir
	private JButton tableDefinitionButton;		// show definitions of alignment stats
	private JTable alignmentsTable;
	private JScrollPane alignmentsScrollPane;
	private JScrollPane sequencePaneNT;
	private JScrollPane sequencePaneAA;
	public DefinitionsFrame definitionFrame;

	public AlignmentsView() {
		// set up panels
		panel = new JPanel(new GridLayout(3,1));
		subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		// set up buttons
		addAlignmentsButton = new JButton("Add...");
		addAlignmentsButton.setToolTipText("Adds a single alignment. Use 'File>Add Alignments..>Directory' to add whole directories in batch mode.");
		removeAlignmentsButton = new JButton("Remove...");
		removeAlignmentsButton.setToolTipText("Removes the currently selected alignment.");
		textDumpButton = new JButton("Export statistics..");
		textDumpButton.setToolTipText("Selects a directory, then creates a file 'alignment_descriptive_stats.tdf' and dumps alignment statistics into it.");
		tableDefinitionButton = new JButton("Statistics' definitions...");
		tableDefinitionButton.setToolTipText("Display alignment statistics' definitions.");
		// add buttons to panels
		buttonPanel.add(addAlignmentsButton);
		buttonPanel.add(removeAlignmentsButton);
		buttonPanel.add(textDumpButton);
		buttonPanel.add(tableDefinitionButton);
		subPanel.add(buttonPanel);
		panel.add(subPanel);
		dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// JFrame to display table's statistics
		definitionFrame = new DefinitionsFrame();
	}
	
	/**
	 * Class extending a JFrame to hold the statistics definitions
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since Aug 20, 2015
	 * @version 0.1
	 */
	private class DefinitionsFrame extends JFrame{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2377591672408559870L;
		JLabel definition;
		private DefinitionsFrame(){
			super("Statistics' definitions");
			setSize(600, 650);
			setLocationRelativeTo(null);
			definition = new JLabel("");
			add(definition);
			setVisible(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		}
		
		private void setLabelText(String[] text){
			String label = "";
			for(String s:text){
				label+=s;
			}
			this.definition.setText(label);
		}
	}
	
	public void addTable(AlignmentsTableModel alignmentsModel) {
        // Init resultsTable
        alignmentsTable = new JTable(alignmentsModel);
        alignmentsTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
        alignmentsTable.setFillsViewportHeight(true);
        alignmentsTable.setRowSelectionAllowed(true);
        alignmentsTable.setColumnSelectionAllowed(true);
        alignmentsTable.setCellSelectionEnabled(true);
        alignmentsTable.setAutoCreateRowSorter(true);
        alignmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        /* Create scrollpanes  and add them to the pane */
        alignmentsScrollPane = new JScrollPane(alignmentsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sequencePaneNT = new JScrollPane();
		sequencePaneNT.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sequencePaneNT.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sequencePaneAA = new JScrollPane();
		sequencePaneAA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sequencePaneAA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        subPanel.add(alignmentsScrollPane);
        panel.add(sequencePaneNT);
        panel.add(sequencePaneAA);
	}
	

	public JTable getTable(){
		return alignmentsTable;
	}
	
	public JPanel getPanel(){
		return panel;
	}

	public void addAddAlignmentsButtonListener(ActionListener al){
		addAlignmentsButton.addActionListener(al);
	}
	
	public void addRemoveAlignmentsButtonListener(ActionListener al){
		removeAlignmentsButton.addActionListener(al);
	}

	public void addTextDumpButtonListener(ActionListener al){
		textDumpButton.addActionListener(al);
	}

	public void addTableDefinitionButtonListener(ActionListener al){
		tableDefinitionButton.addActionListener(al);
	}
	
	public void addListRowSelectionListener(AlignmentsRowListener arl){
        alignmentsTable.getSelectionModel().addListSelectionListener(arl);
	}

	public void addListColumnSelectionListener(AlignmentsColumnListener acl){
        alignmentsTable.getColumnModel().getSelectionModel().addListSelectionListener(acl);
	}

	public JFileChooser getFileChooser() {
		return fc;
	}

	public JFileChooser getDirectoryChooser() {
		return dc;
	}

	/**
	 * Updates the sequencePaneNT and sequencePaneAA with currently selected alignments
	 * @param da DisplayAlignment contraining the alignment data to display
	 */
	public void updateAlignmentScrollPanes(DisplayAlignment da) {
		// Get scroller JScrollPanes from DisplayAlignment
		//JScrollPane newPaneNT = da.getAlignmentScroller();	// do not call the old Stylesheet-based method
		// instead call getAlignmentCanvas() which uses java.awt.Graphics2D to render text.
		JScrollPane newPaneNT = da.getAlignmentCanvas(false);	//arg (useAminoAcidColours) is false e.g. nucleotide colouring
		//JScrollPane newPaneAA = da.getAlignmentScrollerAA();	// do not call the old Stylesheet-based method
		// instead call getAlignmentCanvas() which uses java.awt.Graphics2D to render text.
		JScrollPane newPaneAA = da.getAlignmentCanvas(true);	//arg (useAminoAcidColours) is true e.g. amino-acid colouring
		// Swap the sequencePaneNT
		panel.remove(sequencePaneNT);
		sequencePaneNT = newPaneNT;
//		sequencePaneNT.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		sequencePaneNT.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(sequencePaneNT);
		// Swap the sequencePaneAA		
		panel.remove(sequencePaneAA);
		sequencePaneAA = newPaneAA;
//		sequencePaneAA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		sequencePaneAA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(sequencePaneAA);
		
		panel.revalidate();
		panel.repaint();

		// repaint / update components
/*
		paintComponents(getGraphics());
		panel.paintComponents(panel.getGraphics());
		sequencePaneNT.paintComponents(sequencePaneNT.getGraphics());
		repaint();
		subPanel.repaint();
		super.repaint();
*/
	}


	public void setStatisticDefinitions(String[] tableColumnDefinitions) {
		definitionFrame.setLabelText(tableColumnDefinitions);
		
	}


	public void setDefinitionFrameVisibility(boolean b) {
		definitionFrame.setVisible(b);
	}
}