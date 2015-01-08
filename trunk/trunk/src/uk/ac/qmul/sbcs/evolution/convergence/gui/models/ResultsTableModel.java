package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;


public class ResultsTableModel extends AbstractTableModel {
	public final boolean DEBUG;
	public final String[] columnNames = new String[] {
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
	private Object[][] data;
	private final Object[] longNames = new Object[] {"file", "locus","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), Boolean.FALSE};

	public ResultsTableModel(){
		DEBUG = false;
	}
	
	public ResultsTableModel(boolean doDebugOutput){
		DEBUG = doDebugOutput;
	}
	
	public void addRow(File resultFile){
		Object[][] newData;
		if(data != null){
			newData = new Object[data.length+1][data[0].length];
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
		}else{
			newData = new Object[1][getColumnCount()];
			Object[] newRow = new Object[getColumnCount()];
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
			newData[0] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if(data != null){
			return data.length;
		}else{
			return 0;
		}
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		if(data != null){
			return data[row][col];
		}else{
			return null;
		}
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
	 * Don't need to implement this method unless column's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		//Note that the data/cell address is constant,
		//no matter where the cell appears onscreen.
		if (col < 0) {
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

	/**
	 * @return the longNames
	 */
	public Object[] getLongNames() {
		return longNames;
	}
	
	public Object[][] getData(){
		return data;
	}
}
