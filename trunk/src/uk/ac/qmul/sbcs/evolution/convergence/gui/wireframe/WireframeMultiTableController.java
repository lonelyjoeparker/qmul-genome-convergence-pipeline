/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.MyAnalysesTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.ResultsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.tests.AlignedSequenceRepresentationPreloader;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.sandbox.FileTree;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class WireframeMultiTableController extends JTabbedPane implements ActionListener {

	private AlignmentsTableModel alignmentsModel;
	private MyAnalysesTableModel analysesModel;
	private JTabbedPane tabs = new JTabbedPane();
	private final JFileChooser dc = new JFileChooser();
	private boolean DEBUG = true;
	private final JFileChooser fc = new JFileChooser();
	private JPanel mainPanel;
	private JTable alignmentsTable;
	private JTable analysesTable;
	private JTable resultsTable;
	private JTextArea text;
	private JScrollPane sequencePane = new JScrollPane();
	private JScrollPane sequencePaneAA = new JScrollPane();
	private JCheckBox checkboxGlobalFooBarValue; // this is now a global variable for wireframing
	// MVC architecture:
	private ResultsTableModel resultsModel;
	private WireframeResultsPanel resultsView;
	private WireframeResultsController resultsController;
	private SecondWireframeResultsController secondController;
	private JMenuItem resultsMenuItem; // this as a global so that the SecondWireframeResultsController can see it.

	private class AddButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = fc.showOpenDialog(text);
			File alignmentFile = fc.getSelectedFile();
			//File alignmentFile = new File("/Users/gsjones/Downloads/NM005550/rhp.phy");
			AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
			try {
				asr.loadSequences(alignmentFile, false);
				asr.calculateAlignmentStats(false);
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
			alignmentsModel.addRow(da);
			alignmentsTable.repaint();
		}
	}

	private class AddAnalysesFromLoadedAlignmentsButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			// we were using a filechooser, this could be used to set the output dir but it's probably been set already (checking dependencies etc?) so fuck it off for now...
			/*
			int returnVal = fc.showOpenDialog(text);
			File resultFile = fc.getSelectedFile();
			 */
			System.out.println("create analyses from the list of alignments, of trees, and parameter settings");
			int rowCount = alignmentsModel.getRowCount();
			// example of some global analysis settings
			String whichTree = "tree_1";	// this would be a TreeNode or NewickTreeRepresentation etc
			String analysisName = "myAnalysis"+Math.rint(Math.random()*100);
			boolean enableFooValue = checkboxGlobalFooBarValue.isSelected();
			for(int r=0;r<rowCount;r++){
				// add a new analysis with the values from the alignmentsModel, and the global trees and parameter values
				// this will need a new class (SitewiseSpecificLikelihoodSupportAnalysis, anyone??)
				DisplayAlignment da = (DisplayAlignment) alignmentsModel.getValueAt(r, 0);
				String nameOf = da.getNameGuess();
				double entropy = da.getMeanSitewiseEntropyAA();
				analysesModel.addRow(analysisName, whichTree, nameOf, entropy, da, enableFooValue);

			}
			analysesTable.repaint(); // I *think* we only need to call this once, after we've updated the whole model
		}
	}

	/**
	 * Attempt to add a directory of files in batch mode. 
	 * <br/>Currently *very* inefficient as multiple calls to addRow()
	 * @TODO improve efficiency: avoiding multiple calls to addRow()
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	private class AddDirectoryButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = dc.showOpenDialog(text);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File alignmentDirectory = dc.getSelectedFile();
				if(alignmentDirectory.isDirectory()){
					File[] files  = alignmentDirectory.listFiles();
					for(File alignmentFile:files){
						try {
							AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
							asr.loadSequences(alignmentFile, false);
							asr.calculateAlignmentStats(false);
							DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
							alignmentsModel.addRow(da);
							alignmentsTable.repaint();
						} catch (TaxaLimitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}			

			}
		}
	}

	private class ColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			text.setText("COLUMN SELECTION EVENT. ");
			outputSelection();
		}
	}


	class MyResultsTableModelInternal extends AbstractTableModel {
		private String[] columnNames = new String[] {
				"Alignment",
				"Locus (guess)",
				"Input type",
				"# taxa",
				"# sites (NT)",
				"# invar. sites (NT)",
				"# sites (AA)",
				"# invar. sites (AA)",
				"mean entropy NT",
		"Selection data?"};

		private Object[][] data = new Object[][]{
				{new DisplayAlignment("-"), "-","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), new Boolean(false)}
		};

		private Object[] longNames = new Object[] {"file", "locus","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), Boolean.FALSE};

		public void addRow(File resultFile){
			Object[][] newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[data[0].length];
			newRow[0] = resultFile;
			newRow[1] = resultFile.getName();
			newRow[2] = "None of the above";
			newRow[3] = Math.rint(Math.random()*100);
			newRow[4] = Math.rint(Math.random()*100);
			newRow[5] = Math.rint(Math.random()*100);
			newRow[6] = Math.rint(Math.random()*100);
			newRow[7] = Math.rint(Math.random()*100);
			newRow[8] = Math.random();
			newRow[9] = false;
			newData[data.length] = newRow;
			data = newData;
			this.fireTableRowsInserted(data.length-1, data.length-1);
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col < 2) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value
						+ " (an instance of "
						+ value.getClass() + ")");
			}

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i=0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j=0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}

	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/*
			 * My code - try and get the row data...
			 * 
			 * NB that at the moment there is only one RowListener (and ColumnListnener) shared by all TableDataModels
			 * NB that at the moment TableDataModel.data is not visible hence access through getData() and setData() calls.
			 * This is possibly less efficient but safer than allowing any class to modify Data (given it is an Object[][] of a variety of subclasses)
			 */
			int viewModelRow = alignmentsTable.getSelectedRow();
			Object[] a_row = alignmentsModel.getData()[viewModelRow];
			int val = (Integer)a_row[4];
			float entropy = (Float)a_row[8];
			System.out.println("VIEW ROW ("+viewModelRow+") selected n sites nt: "+val+", entropy "+entropy);
			int tableModelRow = alignmentsTable.convertRowIndexToModel(viewModelRow);
			a_row = alignmentsModel.getData()[tableModelRow];
			val = (Integer)a_row[4];
			entropy = (Float)a_row[8];
			System.out.println("MODEL ROW ("+tableModelRow+") selected n sites nt: "+val+", entropy "+entropy);

			/*
			 * Try the histogram
			 */
			double[] compEntropies = new double[alignmentsModel.getData().length];
			double minVal = 0; //maximum to set hist limit
			double maxVal = 0; //maximum to set hist limit
			for(int i=0;i<compEntropies.length;i++){
				compEntropies[i] = ((Integer) alignmentsModel.getData()[i][4]).doubleValue();
				minVal = Math.min(minVal, compEntropies[i]);
				maxVal = Math.max(maxVal, compEntropies[i]);
			}
			text.setText("RENDERING ALIGNMENT...");


			text.setText("ROW SELECTION EVENT.");
			outputSelection();
		}
	}

	private class SetupButtonActionListener implements ActionListener{
		final JCheckBox chkApple = new JCheckBox("calculate by site");
		final JCheckBox chkMango = new JCheckBox("use thresholds");
		final JCheckBox chkPeer = new JCheckBox("export full rst?");
		JLabel statusLabel = new JLabel("Parameters");

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFrame frame = new JFrame("Setup");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//Create and set up the content pane.
			JTabbedPane tabbedPane = new JTabbedPane();

			JComponent panel1 = makeTextPanel("Panel #1");
			tabbedPane.addTab("delta-SSLS", panel1);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

			JComponent panel2 = makeTextPanel("Panel #2");
			tabbedPane.addTab("input trees", panel2);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

			JComponent panel3 = makeTextPanel("Panel #3");
			tabbedPane.addTab("simulations", panel3);
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

			JComponent panel4 = makeTextPanel( "Panel #4 (has a preferred size of 410 x 50).");
			panel4.setPreferredSize(new Dimension(410, 50));
			tabbedPane.addTab("random control trees for Uc", panel4);
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

			chkApple.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {         
					statusLabel.setText("Do by site: "+(e.getStateChange()==1?"true":"false"));
				}           
			});
			panel1.add(chkApple);
			panel1.add(chkMango);
			panel1.add(chkPeer);
			panel1.add(statusLabel);


			final JCheckBox g = new JCheckBox("use clades?");
			final JCheckBox a = new JCheckBox("use rapid?");
			final JCheckBox n = new JCheckBox("export full tree?");
			panel2.add(g);
			panel2.add(a);
			panel2.add(n);

			//Add the tabbed pane to this panel.
			add(tabbedPane);

			//The following line enables to use scrolling tabs.
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


			tabbedPane.setOpaque(true); //content panes must be opaque
			frame.setContentPane(tabbedPane);


			//Display the window.
			frame.pack();
			frame.setSize(500,580);
			frame.setVisible(true);
		}
	}

	class MyAlignmentsTableModelInternal extends AbstractTableModel {
		private String[] columnNames = {"Results",
				"Alignment",
				"Input type",
				"# taxa",
				"# sites (NT)",
				"# invar. sites (NT)",
				"# sites (AA)",
				"# invar. sites (AA)",
				"mean entropy NT",
		"Selection data?"};

		private Object[][] data = {
				{new DisplayAlignment("-"), "-","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), new Boolean(false)}
		};

		public final Object[] longValues = {"file", "locus","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), Boolean.FALSE};

		public void addRow(DisplayAlignment rowData){
			Object[][] newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[data[0].length];
			newRow[0] = rowData;
			newRow[1] = rowData.getNameGuess();
			newRow[2] = "None of the above";
			newRow[3] = rowData.getNumberOfTaxa();
			newRow[4] = rowData.getNumberOfSitesNT();
			newRow[5] = rowData.getNumberOfInvariantSitesNT();
			newRow[6] = rowData.getNumberOfSitesAA();
			newRow[7] = rowData.getNumberOfInvariantSitesAA();
			newRow[8] = rowData.getMeanSitewiseEntropyNT();
			newRow[9] = false;
			newData[data.length] = newRow;
			data = newData;
			this.fireTableRowsInserted(data.length-1, data.length-1);
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col < 2) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value
						+ " (an instance of "
						+ value.getClass() + ")");
			}

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i=0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j=0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}

	/**
	 * 
	 */
	public WireframeMultiTableController() {
		mainPanel = new JPanel(new GridLayout(3,1));

		// text
		JLabel label = new JLabel("Data pane; click 'MENU' to add more alignments;\rDrag-click window to resize.");
		text = new JTextArea();
		text.setText("debug output goes here\n");
		JPanel panel= new JPanel();

		panel.add(label);
		panel.add(text);

		// init jtable models
		alignmentsModel = new AlignmentsTableModel();
		analysesModel = new MyAnalysesTableModel();
		resultsModel = new ResultsTableModel(true);

		/*
		 *  Init tables; first, the alignmentsTable
		 */
		alignmentsTable = new JTable(alignmentsModel);
		alignmentsTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
		alignmentsTable.setFillsViewportHeight(true);
		// Add action listeners - from TableSelectionDemo
		alignmentsTable.getSelectionModel().addListSelectionListener(new RowListener());
		alignmentsTable.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
		alignmentsTable.setRowSelectionAllowed(true);
		alignmentsTable.setColumnSelectionAllowed(true);
		alignmentsTable.setCellSelectionEnabled(true);
		//ESSENTIAL - disable row sorting so table data match up with panels...
		alignmentsTable.setAutoCreateRowSorter(true);
		/*
		 * DISABLE ROW SORTING
		 * alignmentsTable.setRowSorter(null);
		 * alignmentsTable.setAutoCreateRowSorter(false);
		 */

		// Init analysesTable
		analysesTable = new JTable(analysesModel);
		analysesTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
		analysesTable.setFillsViewportHeight(true);
		// Add action listeners - from TableSelectionDemo
		analysesTable.getSelectionModel().addListSelectionListener(new RowListener());
		analysesTable.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
		analysesTable.setRowSelectionAllowed(true);
		analysesTable.setColumnSelectionAllowed(true);
		analysesTable.setCellSelectionEnabled(true);
		//ESSENTIAL - disable row sorting so table data match up with panels...
		analysesTable.setAutoCreateRowSorter(true);
		/*
		 * DISABLE ROW SORTING
		 * analysesTable.setRowSorter(null);
		 * analysesTable.setAutoCreateRowSorter(false);
		 */

		/*
		 * resultsTable setting in MVC now
        // Init resultsTable
        resultsTable = new JTable(resultsModel);
        resultsTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
        resultsTable.setFillsViewportHeight(true);
        // Add action listeners - from TableSelectionDemo
        resultsTable.getSelectionModel().addListSelectionListener(new RowListener());
        resultsTable.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
        resultsTable.setRowSelectionAllowed(true);
        resultsTable.setColumnSelectionAllowed(true);
        resultsTable.setCellSelectionEnabled(true);
        //ESSENTIAL - disable row sorting so table data match up with panels...
        resultsTable.setAutoCreateRowSorter(true);
		 *
		 */

		/*
		 * DISABLE ROW SORTING
		 * resultsTable.setRowSorter(null);
		 * resultsTable.setAutoCreateRowSorter(false);
		 */

		//essential
		dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		/*
		 * 		// try to add init data
		 *        
    	try {
        	File alignmentFile = new File("/Users/gsjones/Downloads/NM005550/rhp.phy");
        	AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
			asr.loadSequences(alignmentFile, false);
			DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
	        dataModel.data[4][0] = da;
	        dataModel.data[4][1] = da.getName();
	        dataModel.data[4][2] = "None of the above";
	        dataModel.data[4][3] = asr.getNumberOfTaxa();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 *
		 */

		/*
		 * alternative init
		 */
	
		ArrayList<ArrayList<String>> listOfAlignmentRawData = new AlignedSequenceRepresentationPreloader().getPreloadingAlignments();
		Iterator<ArrayList<String>> itr = listOfAlignmentRawData.iterator();
		while(itr.hasNext()){
			ArrayList<String> rawAlignment = itr.next();
			this.addAlignment(rawAlignment);
		}

		//Create the scroll panes and add the table to them.
		JScrollPane alignmentsScrollPane = new JScrollPane(alignmentsTable);
		JScrollPane analysesScrollPane = new JScrollPane(analysesTable);
		//JScrollPane resultsScrollPane = new JScrollPane(resultsTable);


		//Fiddle with the Input Type column's cell editors/renderers.
		setUpComboBoxColumn(alignmentsTable, alignmentsTable.getColumnModel().getColumn(2));

		//Add the scroll pane to this panel.
		panel.add(alignmentsScrollPane);
		mainPanel.add(panel);



		// sequenceView - NT
		mainPanel.add(sequencePane);

		// sequenceView - AA
		mainPanel.add(sequencePaneAA);



		/* converting to a tabbed pane model */
		checkboxGlobalFooBarValue = new JCheckBox("calculate by site");
		final JCheckBox chkMango = new JCheckBox("use thresholds");
		final JCheckBox chkPeer = new JCheckBox("export full rst?");
		JLabel statusLabel = new JLabel("Parameters");

		addTab("Alignments", mainPanel);

		JComponent panel2 = makeTextPanel("Input trees");
		addTab("Trees", panel2);
		setMnemonicAt(1, KeyEvent.VK_2);

		FileTree fileTreePanel = new FileTree(new File("/Users/joeparker/Documents/all_work/QMUL-convEvol/FSD/results/f_99_ENSG0000PRESTIN_ng.fas"));
		addTab("Files", fileTreePanel);
		setMnemonicAt(0, KeyEvent.VK_1);

		JComponent analysesPanel = makeTextPanel("Analysis steps in pipeline");
		addTab("Analyses", analysesPanel);
		setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel( "Execution platforms");
		panel4.setPreferredSize(new Dimension(410, 50));
		addTab("Environments", panel4);
		setMnemonicAt(3, KeyEvent.VK_4);

		/*
		 * RESULTS PANEL AS Model-View-Controller 
		 */
		// Create view for results panel
		resultsView = new WireframeResultsPanel();
		// Create controller for results view / model
		resultsController = new WireframeResultsController(resultsModel, resultsView);
		// Add controller view to global view
		addTab("Results via getView()", resultsController.getView());

		//Set up column sizes.
		/*
        initAlignmentColumnSizes(alignmentsTable.getModel(), alignmentsTable); // initColumnSizes only implemented for alignmentsTable
        initAnalysesColumnSizes(analysesTable.getModel(), analysesTable);
        initResultsColumnSizes(resultsTable.getModel() resultsTable);
		 */
		initColumnSizesAnyModel(alignmentsTable.getModel(), alignmentsTable); 
		initColumnSizesAnyModel(analysesTable.getModel(), analysesTable);
		//initColumnSizesAnyModel(resultsTable.getModel(), resultsTable); this now handled by WireframeResultsController
		

		analysesPanel.add(checkboxGlobalFooBarValue);
		analysesPanel.add(chkMango);
		analysesPanel.add(chkPeer);
		analysesPanel.add(statusLabel);
		analysesPanel.add(analysesScrollPane);

		//resultsPanelView.add(resultsScrollPane);

		final JCheckBox g = new JCheckBox("use clades?");
		final JCheckBox a = new JCheckBox("use rapid?");
		final JCheckBox n = new JCheckBox("export full tree?");
		panel2.add(g);
		panel2.add(a);
		panel2.add(n);

		//Add the tabbed pane to this panel.
		//		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


		setOpaque(true); //content panes must be opaque
	}

	/**
	 * @param arg0
	 */
	public WireframeMultiTableController(int arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public WireframeMultiTableController(int arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * This method picks good column sizes.
	 * If all column heads are wider than the column's cells'
	 * contents, then you can just use column.sizeWidthToFit().
	 * 
	 * WARNING
	 * This is not very OO or inheritance-friendly:
	 * At the moment each tableModel has its own call to initXColumnsizes()
	 * Not robust at all...
	 */
	private void initAlignmentColumnSizes(AlignmentsTableModel model, JTable table) {
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		Object[] longValues = model.longValues;
		TableCellRenderer headerRenderer =
			table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < 5; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(
					null, column.getHeaderValue(),
					false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i)).
			getTableCellRendererComponent(
					table, longValues[i],
					false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			if (DEBUG) {
				System.out.println("Initializing width of column "
						+ i + ". "
						+ "headerWidth = " + headerWidth
						+ "; cellWidth = " + cellWidth);
			}

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}


	/*
	 * Attempt to write a generic init method for column sizes
	 */
	public static void initColumnSizesAnyModel(TableModel model, JTable table) {
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		int rowCount = model.getRowCount();
		int colCount = model.getColumnCount();
		//Object[] longValues = model.longValues; // longValues seems to be just the null vals for each column. we can get this from scratch...
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

		// try and establish the preferred widths. the first row of each dataModel contains null vals anyway (we know this cos it's in their constructors, we put it there).
		if(rowCount>0){
			for (int col = 0; col < colCount; col++) {
				column = table.getColumnModel().getColumn(col);

				comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
				headerWidth = comp.getPreferredSize().width;

				comp = table.getDefaultRenderer(model.getColumnClass(col)).getTableCellRendererComponent(table, model.getValueAt(0,col),false, false, 0, col);
				cellWidth = comp.getPreferredSize().width;
				column.setPreferredWidth(Math.max(headerWidth, cellWidth));
			}
		}
	}



	private void outputSelection() {
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.LEFT);
		filler.setVerticalAlignment(JLabel.TOP);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	/**
	 * Create the JMenu
	 */
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu alignmentsMenu, actionsMenu, resultsMenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		alignmentsMenu = new JMenu("Alignments...");
		menuBar.add(alignmentsMenu);

		resultsMenu = new JMenu("Results...");
		menuBar.add(resultsMenu);
		resultsMenuItem = new JMenuItem("Add results files (DUMMY)...");
		//menuItem.addActionListener(new AddResultsButtonListener()); menuItem will be added as a global then this will be passed to the SecondWireframeResultsController
		secondController = new SecondWireframeResultsController(resultsModel, resultsView, resultsMenuItem);
		resultsMenu.add(resultsMenuItem);

		//a group of JMenuItems
		// single file button
		menuItem = new JMenuItem("Add single alignment file...");
		menuItem.getAccessibleContext().setAccessibleDescription("Adds a single alignment");
		menuItem.addActionListener(new AddButtonListener());
		alignmentsMenu.add(menuItem);

		// directory button
		menuItem = new JMenuItem("Add directory of alignments as batch...");
		menuItem.getAccessibleContext().setAccessibleDescription("Adds a directory of alignments");
		menuItem.addActionListener(new AddDirectoryButtonListener());
		alignmentsMenu.add(menuItem);

		//Build the second menu.
		actionsMenu = new JMenu("Actions...");
		menuBar.add(actionsMenu);

		//a group of JMenuItems
		// single file button
		menuItem = new JMenuItem("Setup parameters...");
		menuItem.getAccessibleContext().setAccessibleDescription("Adds a single alignment");
		menuItem.addActionListener(new SetupButtonActionListener());
		actionsMenu.add(menuItem);

		// directory button
		menuItem = new JMenuItem("Infer phylogenies by RAxML...");
		menuItem.getAccessibleContext().setAccessibleDescription("Adds a directory of alignments");
		menuItem.addActionListener(new AddDirectoryButtonListener());
		actionsMenu.add(menuItem);

		// actually try and create analyses from alignments list
		menuItem = new JMenuItem("Create analyses files from aligments (DUMMY)");
		menuItem.addActionListener(new AddAnalysesFromLoadedAlignmentsButtonListener());
		actionsMenu.add(menuItem);

		// submenu
		JMenu submenu = new JMenu("XML actions");
		submenu.add(new JMenuItem("Write XML..."));
		submenu.add(new JMenuItem("Read XML..."));
		submenu.add(new JMenuItem("Verify XML..."));

		// variety
		actionsMenu.add(new JMenuItem("Estimate convergent sites..."));
		actionsMenu.add(new JMenuItem("Add taxon list..."));
		actionsMenu.add(new JMenuItem("Verify taxon list..."));
		actionsMenu.add(new JMenuItem("Correlate dN/dS and ÆSSLS"));
		actionsMenu.add(submenu);

		return menuBar;
	}

	public void addAlignment(ArrayList<String> alignment) {
		AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
		try {
			asr.loadSequences(alignment, false);
			asr.calculateAlignmentStats(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DisplayAlignment da = new DisplayAlignment(null,asr);
		da.setName((alignmentsModel.getRowCount()+1)+"_file");
		alignmentsModel.addRow(da);
		alignmentsTable.repaint();    	
	}

	/**
	 * An editor for cells with combo-box (radio button-like) behaviour
	 * @param table
	 * @param someColumn
	 */
	public void setUpComboBoxColumn(JTable table, TableColumn someColumn) {
		//Set up the editor for the radio selection cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("NT");
		comboBox.addItem("AA");
		comboBox.addItem("None of the above");
		someColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the radio selection cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		someColumn.setCellRenderer(renderer);
	}

	/*
	 * This method picks good column sizes.
	 * If all column heads are wider than the column's cells'
	 * contents, then you can just use column.sizeWidthToFit().
	 */
	private void initResultsColumnSizes(JTable table) {
		ResultsTableModel model = (ResultsTableModel)table.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		Object[] longValues = model.getLongNames();
		TableCellRenderer headerRenderer =
			table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < 5; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(
					null, column.getHeaderValue(),
					false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i)).
			getTableCellRendererComponent(
					table, longValues[i],
					false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			if (DEBUG) {
				System.out.println("Initializing width of column "
						+ i + ". "
						+ "headerWidth = " + headerWidth
						+ "; cellWidth = " + cellWidth);
			}

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	/*
	 * This method picks good column sizes.
	 * If all column heads are wider than the column's cells'
	 * contents, then you can just use column.sizeWidthToFit().
	 */
	private void initAnalysesColumnSizes(JTable table) {
		MyAnalysesTableModel model = (MyAnalysesTableModel)table.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		Object[] longValues = model.longValues;
		TableCellRenderer headerRenderer =
			table.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < 5; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(
					null, column.getHeaderValue(),
					false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i)).
			getTableCellRendererComponent(
					table, longValues[i],
					false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			if (DEBUG) {
				System.out.println("Initializing width of column "
						+ i + ". "
						+ "headerWidth = " + headerWidth
						+ "; cellWidth = " + cellWidth);
			}

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("FAVE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		WireframeMultiTableController newContentPane = new WireframeMultiTableController();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Create / set the menu
		frame.setJMenuBar(newContentPane.createMenuBar());

		//Display the window.
		frame.pack();
		frame.setSize(900,760);
		frame.setVisible(true);

	}
}
