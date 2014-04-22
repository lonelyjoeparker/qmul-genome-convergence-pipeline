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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/** 
 * TableRenderDemo is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */
public class TableRenderDemoGCP extends JPanel implements ActionListener{
    private boolean DEBUG = true;
    private JTextArea text;
    private CirclePanel circle;
    private ChartPanel cpanel;
    private JFreeChart chart;
    private ChartTestbed chartTest;
    private JScrollPane sequencePane = new JScrollPane();
    private JTable table;
    private MyTableModel dataModel;
    private final JFileChooser fc = new JFileChooser();
    
    public TableRenderDemoGCP() {
        super(new GridLayout(2,2));

        dataModel = new MyTableModel();
        table = new JTable(dataModel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        // Add action listeners - from TableSelectionDemo
        table.getSelectionModel().addListSelectionListener(new RowListener());
        table.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);

        // try to add data
    	File alignmentFile = new File("/Users/gsjones/Downloads/NM005550/rhp.phy");
    	AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
    	try {
			asr.loadSequences(alignmentFile, false);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
        dataModel.data[4][0] = da;
        dataModel.data[4][1] = da.getName();
        dataModel.data[4][2] = "None of the above";
        dataModel.data[4][3] = asr.getNumberOfTaxa();

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up column sizes.
        initColumnSizes(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(2));

        //Add the scroll pane to this panel.
        add(scrollPane);

		JLabel label = new JLabel("somelabel");
		text = new JTextArea();
		text.setText("lalalalalala\nlalalalala\ns\ns\ne\nd\nr\nkjhkjhkjhkjhkjh\n");


		/*
		// circle
		circle = new CirclePanel();
		add(circle);
		 */
		
		// chart
		chartTest = new ChartTestbed();
		chartTest.go();
		chart = chartTest.createChart();
		cpanel = new ChartPanel(chart);
		add(cpanel);
		
		// text
		JPanel panel= new JPanel();
		JButton button = new JButton("add");
		button.addActionListener(new AddButtonListener());
		panel.add(button);
		panel.add(label);
		panel.add(text);
		add(panel);

		// sequenceView
		add(sequencePane);

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
        comboBox.addItem("Snowboarding");
        comboBox.addItem("Rowing");
        comboBox.addItem("Knitting");
        comboBox.addItem("Speed reading");
        comboBox.addItem("Pool");
        comboBox.addItem("None of the above");
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        sportColumn.setCellRenderer(renderer);
    }

    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"First Name",
                                        "Last Name",
                                        "Sport",
                                        "# of Years",
                                        "Vegetarian"};

        private Object[][] data = {
        		{new DisplayAlignment("Kathy"), "Smith","Snowboarding", new Integer(5), new Boolean(false)},
        		{new DisplayAlignment("John"), "Doe","Rowing", new Integer(3), new Boolean(true)},
        		{new DisplayAlignment("Sue"), "Black","Knitting", new Integer(2), new Boolean(false)},
        		{new DisplayAlignment("Jane"), "White","Speed reading", new Integer(20), new Boolean(true)},
        		{new DisplayAlignment("Joe"), "Brown","Pool", new Integer(10), new Boolean(false)}
        };

        public final Object[] longValues = {"Jane", "Kathy","None of the above",new Integer(20), Boolean.TRUE};

        public void addRow(DisplayAlignment rowData){
        	Object[][] newData = new Object[data.length+1][data[0].length];
        	for(int i=0;i<data.length;i++){
        		newData[i] = data[i];
        	}
        	Object[] newRow = new Object[data[0].length];
        	newRow[0] = rowData;
        	newRow[1] = rowData.getName();
        	newRow[2] = "None of the above";
        	newRow[3] = rowData.getNumberOfTaxa();
        	newRow[4] = false;
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
                text.setText("Setting value at " + row + "," + col
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
            
            if(col == 3){
            	chartTest.addSeries((Integer)value);
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
            table.setSelectionMode(
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //If cell selection is on, turn it off.
          //-           if (cellCheck.isSelected()) {
          //-                cellCheck.setSelected(false);
          //-                table.setCellSelectionEnabled(false);
          //-           }
            //And don't let it be turned back on.
          //-            cellCheck.setEnabled(false);
        } else if ("Single Interval Selection" == command) {
            table.setSelectionMode(
                    ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            //Cell selection is ok in this mode.
          //-          cellCheck.setEnabled(true);
        } else if ("Single Selection" == command) {
            table.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
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
        text.append(String.format("Lead: %d, %d. ",
                    table.getSelectionModel().getLeadSelectionIndex(),
                    table.getColumnModel().getSelectionModel().
                        getLeadSelectionIndex()));
        text.append("Rows:");
        for (int c : table.getSelectedRows()) {
            text.append(String.format(" %d", c));
        }
        text.append(". Columns:");
        for (int c : table.getSelectedColumns()) {
            text.append(String.format(" %d", c));
        }
        text.append(".\n");
    }

    private class AddButtonListener implements ActionListener{
 		@Override
		public void actionPerformed(ActionEvent ev) {
			// TODO Auto-generated method stub
 			int returnVal = fc.showOpenDialog(text);
 			File alignmentFile = fc.getSelectedFile();
 	    	//File alignmentFile = new File("/Users/gsjones/Downloads/NM005550/rhp.phy");
 	    	AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
 	    	try {
 				asr.loadSequences(alignmentFile, false);
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
        	int diameter = (Integer)a_row[3];
        	System.out.println("selected diameter: "+diameter);

        	DisplayAlignment selectedAlignment = (DisplayAlignment)a_row[0];
        	/*
        	 * Replace the chart
        	 */
        	chartTest.setNewDataset(selectedAlignment.entropyNTData);
        	/*
        	 * Replace the alignement
        	 */
        	JScrollPane scroller = selectedAlignment.getAlignmentScroller();
        	//add(scroller);
        	remove(sequencePane);
        	sequencePane = scroller;
        	add(sequencePane);
        	
            text.append("ROW SELECTION EVENT. ");
            outputSelection();
        }
    }

    private class ColumnListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            text.append("COLUMN SELECTION EVENT. ");
            outputSelection();
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableRenderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TableRenderDemoGCP newContentPane = new TableRenderDemoGCP();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
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
