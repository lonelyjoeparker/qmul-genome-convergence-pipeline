package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;

public	class AlignmentsTableModel extends AbstractTableModel {

	public final boolean DEBUG;
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

	public AlignmentsTableModel(){
		DEBUG = false;
	}
	
	public AlignmentsTableModel(boolean doDebugOutput){
		DEBUG = doDebugOutput;
	}

	public void addRow(DisplayAlignment rowData){
		Object[][] newData = new Object[getData().length+1][getData()[0].length];
		for(int i=0;i<getData().length;i++){
			newData[i] = getData()[i];
		}
		Object[] newRow = new Object[getData()[0].length];
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
		newData[getData().length] = newRow;
		setData(newData);
		this.fireTableRowsInserted(getData().length-1, getData().length-1);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return getData().length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return getData()[row][col];
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

		getData()[row][col] = value;
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
				System.out.print("  " + getData()[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object[][] data) {
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public Object[][] getData() {
		return data;
	}
}
