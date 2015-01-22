package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
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
			"Selection data?",
			"Source alignment"};
	private Object[][] data;
	public final Object[] longValues = {"file", "locus","None of the above", new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Float(0), Boolean.FALSE};

	public AlignmentsTableModel(){
		DEBUG = false;
	}
	
	public AlignmentsTableModel(boolean doDebugOutput){
		DEBUG = doDebugOutput;
	}

	/**
	 * Add a single row to the data.
 	 * <p><b>Note that this operation is NOT SYNCHRONISED</b></p>
	 * @param rowDisplayAlignment - the DisplayAlignment that will be used to draw alignment panes
	 * @param rowSourceAlignment - the AlignedSequenceRepresentation containing the original source data
	 */
	public void addRow(DisplayAlignment rowDisplayAlignment, AlignedSequenceRepresentation rowSourceAlignment){
		Object[][] newData;
		if(data != null){
			newData = new Object[getData().length+1][getData()[0].length];
			for(int i=0;i<getData().length;i++){
				newData[i] = getData()[i];
			}
			Object[] newRow = new Object[getData()[0].length];
			newRow[0] = rowDisplayAlignment;
			newRow[1] = rowDisplayAlignment.getNameGuess();
			newRow[2] = "None of the above";
			newRow[3] = rowDisplayAlignment.getNumberOfTaxa();
			newRow[4] = rowDisplayAlignment.getNumberOfSitesNT();
			newRow[5] = rowDisplayAlignment.getNumberOfInvariantSitesNT();
			newRow[6] = rowDisplayAlignment.getNumberOfSitesAA();
			newRow[7] = rowDisplayAlignment.getNumberOfInvariantSitesAA();
			newRow[8] = rowDisplayAlignment.getMeanSitewiseEntropyNT();
			newRow[9] = false;
			newRow[10] = rowSourceAlignment;
			newData[getData().length] = newRow;
		}else{
			newData = new Object[1][getColumnCount()];
			Object[] newRow = new Object[getColumnCount()];
			newRow[0] = rowDisplayAlignment;
			newRow[1] = rowDisplayAlignment.getNameGuess();
			newRow[2] = "None of the above";
			newRow[3] = rowDisplayAlignment.getNumberOfTaxa();
			newRow[4] = rowDisplayAlignment.getNumberOfSitesNT();
			newRow[5] = rowDisplayAlignment.getNumberOfInvariantSitesNT();
			newRow[6] = rowDisplayAlignment.getNumberOfSitesAA();
			newRow[7] = rowDisplayAlignment.getNumberOfInvariantSitesAA();
			newRow[8] = rowDisplayAlignment.getMeanSitewiseEntropyNT();
			newRow[9] = false;
			newRow[10] = rowSourceAlignment;
			newData[0] = newRow;
		}
		setData(newData);
		this.fireTableRowsInserted(getData().length-1, getData().length-1);
	}

	/**
	 * Removes a single row from the data.
	 * <p><b>Note that this operation is NOT SYNCHRONISED</b></p>
	 * @param removeThisRow
	 */
	public void removeRow(int removeThisRow){
		// check data is not null
		if(data != null){
			// check requested row index isn't out of range
			if(removeThisRow < data.length){
				// should be able to delete this row, attempt to initialise a new data array
				Object[][] shorterTable = new Object[data.length-1][getColumnCount()];
				int newRowIndex = 0;
				// iterate through data adding rows to shorterTable, unless it's the row we want to delete...
				for(int existingRowIndex=0;existingRowIndex<data.length;existingRowIndex++){
					if(existingRowIndex != removeThisRow){
						// this is a row we want, add it to the new shorterTable
						shorterTable[newRowIndex] = data[existingRowIndex];
						newRowIndex++;
					}else{
						// this is the row we want to remove - do nothing...
					}
				}
				// we should now have a new shorterTable, replace the existing data..
				data = shorterTable;
				this.fireTableRowsDeleted(removeThisRow, removeThisRow);
			}
		}
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if(data != null){
			return getData().length;
		}else{
			return 0;
		}
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		if(data != null){
			return getData()[row][col];
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
