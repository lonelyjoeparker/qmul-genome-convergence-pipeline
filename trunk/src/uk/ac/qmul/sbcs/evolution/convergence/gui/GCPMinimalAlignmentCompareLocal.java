package uk.ac.qmul.sbcs.evolution.convergence.gui;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 



/*
 * TableRenderDemo.java requires no other files.
 */

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.tests.AlignedSequenceRepresentationPreloader;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 
 * TableRenderDemo is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */
public class GCPMinimalAlignmentCompareLocal extends JPanel implements ActionListener{
    private boolean DEBUG = true;
    private JTextArea text;
 //   private ChartPanel cpanel;
 //   private ChartPanel hpanel;
 //   private JFreeChart chart;
 //  private JFreeChart hist;
 //   private ChartTestbed chartTest;
 //   private ChartTestbed histTest;
    private JScrollPane sequencePane = new JScrollPane();
    private JScrollPane sequencePaneAA = new JScrollPane();
    private JTable table;
    private MyTableModel dataModel;
//    private CirclePanel circle;
    private final JFileChooser fc = new JFileChooser();
    private final JFileChooser dc = new JFileChooser();
    
    public GCPMinimalAlignmentCompareLocal() {
        super(new GridLayout(3,1));

 		// text
		JLabel label = new JLabel("Data pane; click 'MENU' to add more alignments;\rDrag-click window to resize.");
		text = new JTextArea();
		text.setText("debug output goes here\n");
		JPanel panel= new JPanel();
//		JButton button = new JButton("Add single alignment");
//		button.addActionListener(new AddButtonListener());
//		panel.add(button);
//		JButton dirButton = new JButton("Add directory of alignments");
//		dirButton.addActionListener(new AddDirectoryButtonListener());
//		panel.add(dirButton);
		panel.add(label);
		panel.add(text);

		dataModel = new MyTableModel();
        table = new JTable(dataModel);
        table.setPreferredScrollableViewportSize(new Dimension(700, 250));
        table.setFillsViewportHeight(true);
        // Add action listeners - from TableSelectionDemo
        table.getSelectionModel().addListSelectionListener(new RowListener());
        table.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        //ESSENTIAL - disable row sorting so table data match up with panels...
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
        // /essential
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
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up column sizes.
        initColumnSizes(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(2));

        //Add the scroll pane to this panel.
//        add(scrollPane);
        // .... or don't:
        panel.add(scrollPane);
        add(panel);
        
     /*
      * Commented out while I work out the .jnlp packaging...   
      
		// chart (entropies)
		chartTest = new ChartTestbed();
		chartTest.go();
		chart = chartTest.createChart();
		cpanel = new ChartPanel(chart);
		add(cpanel);

		// chart (histogram)
		// TODO
		histTest = new ChartTestbed();
		histTest.go();
		hist = histTest.createHistogram();
		hpanel = new ChartPanel(hist);
		add(hpanel);
	 */
        
 
  

		// sequenceView - NT
//		sequencePane.setSize(700, 200);
		add(sequencePane);

		// sequenceView - AA
//		sequencePaneAA.setSize(700,200);
		add(sequencePaneAA);

		/*
		 * Circle for debug/testing
		 * 
			circle = new CirclePanel();
			add(circle);
		 */
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        MyTableModel model = (MyTableModel)table.getModel();
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

    public void setUpSportColumn(JTable table, TableColumn sportColumn) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("NT");
        comboBox.addItem("AA");
        comboBox.addItem("None of the above");
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        sportColumn.setCellRenderer(renderer);
    }

    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Alignment",
                                        "Locus (guess)",
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
    /*
                text.setText("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
     */
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
      /*
       * commented out while .. jnlp
            if(col == 3){
            	chartTest.addSeries((Integer)value);
            }
       
       */
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

    /*
     * Selection listening stuff: from TableSelectionDemo
     */
    public void actionPerformed(ActionEvent event) {
    	// from TableSelectionDemo
        String command = event.getActionCommand();
        //Cell selection is disabled in Multiple Interval Selection
        //mode. The enabled state of cellCheck is a convenient flag
        //for this status.
        if ("Row Selection" == command) {
//-            table.setRowSelectionAllowed(rowCheck.isSelected());
            //In MIS mode, column selection allowed must be the
            //opposite of row selection allowed.
//-            if (!cellCheck.isEnabled()) {
//-                table.setColumnSelectionAllowed(!rowCheck.isSelected());
        	//-            }
        } else if ("Column Selection" == command) {
        	//-            table.setColumnSelectionAllowed(columnCheck.isSelected());
            //In MIS mode, row selection allowed must be the
            //opposite of column selection allowed.
        	//-            if (!cellCheck.isEnabled()) {
        	//-                table.setRowSelectionAllowed(!columnCheck.isSelected());
        	//-            }
        } else if ("Cell Selection" == command) {
        	//-           table.setCellSelectionEnabled(cellCheck.isSelected());
        } else if ("Multiple Interval Selection" == command) { 
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //If cell selection is on, turn it off.
          //-           if (cellCheck.isSelected()) {
          //-                cellCheck.setSelected(false);
          //-                table.setCellSelectionEnabled(false);
          //-           }
            //And don't let it be turned back on.
          //-            cellCheck.setEnabled(false);
        } else if ("Single Interval Selection" == command) {
            table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            //Cell selection is ok in this mode.
          //-          cellCheck.setEnabled(true);
        } else if ("Single Selection" == command) {
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Cell selection is ok in this mode.
          //-          cellCheck.setEnabled(true);
        }

        //Update checkboxes to reflect selection mode side effects.
      //-      rowCheck.setSelected(table.getRowSelectionAllowed());
      //-      columnCheck.setSelected(table.getColumnSelectionAllowed());
      //-      if (cellCheck.isEnabled()) {
      //-          cellCheck.setSelected(table.getCellSelectionEnabled());
      //-      }
    }

    private void outputSelection() {
    /*
     * fuck this right off..
     * 
    	text.append(String.format("Lead: %d, %d. ",
                    table.getSelectionModel().getLeadSelectionIndex(),
                    table.getColumnModel().getSelectionModel().getLeadSelectionIndex()));
        text.append("Rows:");
        for (int c : table.getSelectedRows()) {
            text.append(String.format(" %d", c));
        }
        text.append(". Columns:");
        for (int c : table.getSelectedColumns()) {
            text.append(String.format(" %d", c));
        }
        text.append(".\n");
	 */
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
 	 	 					dataModel.addRow(da);
 	 	 					table.repaint();
 	 		 			} catch (TaxaLimitException e) {
 	 		 				// TODO Auto-generated catch block
 	 		 				e.printStackTrace();
 	 		 			}
 	 				}
 	 			}			
 				
 			}
		}
    }
    
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
			dataModel.addRow(da);
			table.repaint();
		}
    }
    
    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
        	/*
        	 * My code - try and get the row data...
        	 */
        	int row = table.getSelectedRow();
        	Object[] a_row = dataModel.data[row];
        	int element = (Integer)a_row[3];
        	System.out.println("selected diameter: "+element);

        	DisplayAlignment selectedAlignment = (DisplayAlignment)a_row[0];
        	/*
        	 * Replace the chart
        	 */
        	// COMMENTED OUT
        	//chartTest.setNewDataset(selectedAlignment.entropyNTData);
        	//chartTest.addXYseries(selectedAlignment.entropyAAData.getSeries(0));
        	/*
        	 * Try the histogram
        	 */
        	double[] compEntropies = new double[dataModel.data.length];
        	double minVal = 0; //maximum to set hist limit
        	double maxVal = 0; //maximum to set hist limit
        	for(int i=0;i<compEntropies.length;i++){
        		compEntropies[i] = ((Integer) dataModel.data[i][4]).doubleValue();
        		minVal = Math.min(minVal, compEntropies[i]);
        		maxVal = Math.max(maxVal, compEntropies[i]);
        	}
        	// COMMENTED OUT
        	//histTest.replaceHistSeries(compEntropies,minVal,maxVal);
        	/*
        	 * Replace the alignment NT
        	 */
        	text.setText("RENDERING ALIGNMENT...");
        	JScrollPane scroller = selectedAlignment.getAlignmentScroller();
			remove(sequencePane);
			sequencePane = scroller;
			add(sequencePane);
        	text.setText("...done.");

        	/*
        	 * Replace the alignment AA
        	 */
        	text.setText("RENDERING ALIGNMENT...");
        	JScrollPane scrollerAA = selectedAlignment.getAlignmentScrollerAA();
        	remove(sequencePaneAA);
        	sequencePaneAA = scrollerAA;
        	add(sequencePaneAA);
        	text.setText("...done.");

        	//text.append("ROW SELECTION EVENT. ");
        	text.setText("ROW SELECTION EVENT.");
            outputSelection();

            /*
			if(scroller.getWidth() > -1){ 
				remove(sequencePane);
				sequencePane = scroller;
				// 		sequencePane.setSize(700, 200);
				//sequencePane.setBackground(new Color((float)Math.random(), 1.0f, (float)Math.random()));
				//sequencePane.setVisible(true);
				add(sequencePane);
			}else{
				//oops, alignment is not playing nicely with scrollpane setup..
				remove(sequencePane);
				System.err.println("what's up with the alignment? ");
				JTextPane textPaneBasic = new JTextPane();
				textPaneBasic.setText(selectedAlignment.getFirstSequence());
				textPaneBasic.setBackground(new Color((float)Math.random(), 1.0f, (float)Math.random()));
				sequencePane = new JScrollPane();
				sequencePane.add(textPaneBasic);
				//sequencePane.setBackground(new Color((float)Math.random(), 1.0f, (float)Math.random()));
				//sequencePane.setVisible(true);
				add(sequencePane);
			}
			//sequencePane.repaint();
			 
			

        	JScrollPane scrollerAA = null;
			try {
				scrollerAA = selectedAlignment.getAlignmentScrollerAA();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(scrollerAA.getWidth() > -1){
    			remove(sequencePaneAA);
            	sequencePaneAA = scrollerAA;
    //    		sequencePaneAA.setSize(700, 200);
            	sequencePaneAA.setBackground(new Color((float)Math.random(), (float)Math.random(), 1.0f));
              	sequencePaneAA.setVisible(true);
            	add(sequencePaneAA);
			}
			
			text.append("ROW SELECTION EVENT. ");
            outputSelection();
            */
            /*
            	circle.setDiameter(element);
            	circle.setColour(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
             */
            //repaint();
        }
    }

    private class ColumnListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
   //         text.append("COLUMN SELECTION EVENT. ");
            text.setText("COLUMN SELECTION EVENT. ");
            outputSelection();
        }
    }

    public void addFile(File newFile){
    	//File alignmentFile = newFile("/Users/gsjones/Downloads/NM005550/rhp.phy");
    	AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
    	try {
    		asr.loadSequences(newFile, false);
    		asr.calculateAlignmentStats(false);
    	} catch (TaxaLimitException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	DisplayAlignment da = new DisplayAlignment(newFile.getName(),asr);
    	dataModel.addRow(da);
    	table.repaint();    	
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
    	da.setName((dataModel.getRowCount()+1)+"_file");
    	dataModel.addRow(da);
    	table.repaint();    	
    }
    
    
    /**
     * Create the JMenu
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;
 
        //Create the menu bar.
        menuBar = new JMenuBar();
 
        //Build the first menu.
        menu = new JMenu("Menu");
        menuBar.add(menu);
 
        //a group of JMenuItems
        // single file button
        menuItem = new JMenuItem("Add single alignment file...");
        menuItem.getAccessibleContext().setAccessibleDescription("Adds a single alignment");
        menuItem.addActionListener(new AddButtonListener());
        menu.add(menuItem);
 
        // directory button
        menuItem = new JMenuItem("Add directory of alignments as batch...");
        menuItem.getAccessibleContext().setAccessibleDescription("Adds a directory of alignments");
        menuItem.addActionListener(new AddDirectoryButtonListener());
        menu.add(menuItem);
 
/*
 *
        //a group of radio button menu items
        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
 
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);
 
        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);
 
        //a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);
 
        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);
 
        //a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);
 
        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);
 
        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);

 *  
 *  
 */
 
 
        return menuBar;
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
        GCPMinimalAlignmentCompareLocal newContentPane = new GCPMinimalAlignmentCompareLocal();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        //Create / set the menu
        frame.setJMenuBar(newContentPane.createMenuBar());

        //Display the window.
        frame.pack();
        frame.setSize(900,760);
        frame.setVisible(true);
        
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
