package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;

public class MyAnalysesTableModel extends AbstractTableModel {

	public final boolean DEBUG;
	private String[] columnNames = {
			"Analysis",
			"Tree",
			"Input alignment",
			"Input name",
			"Input type",
			"# sites (NT)",
			"# invar. sites (AA)",
			"mean entropy NT",
			"Selection data?"};
	private Object[][] data = {
			{"-", "-", new DisplayAlignment("-"), "-","None of the above", new Integer(0), new Integer(0), new Float(0), new Boolean(false)}
	};
	public final Object[] longValues = {"analysis_name","tree","alignment","alignmentName","None of the above", new Integer(0), new Integer(0), new Float(0), Boolean.FALSE};

	public MyAnalysesTableModel(){
		DEBUG = false;
	}
	
	public MyAnalysesTableModel(boolean doDebugOutput){
		DEBUG = doDebugOutput;
	}

	public void addRow(String analysisName, String whichTree, String nameOf, double entropy, DisplayAlignment rowData, boolean enableFooValue){
		Object[][] newData = new Object[data.length+1][data[0].length];
		for(int i=0;i<data.length;i++){
			newData[i] = data[i];
		}
		Object[] newRow = new Object[data[0].length];
		newRow[0] = analysisName;
		newRow[1] = whichTree;
		newRow[2] = rowData;
		newRow[3] = nameOf;
		newRow[4] = "None of the above";
		newRow[5] = rowData.getNumberOfInvariantSitesNT();
		newRow[6] = rowData.getNumberOfInvariantSitesAA();
		newRow[7] = rowData.getMeanSitewiseEntropyNT();
		newRow[8] = enableFooValue;
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
