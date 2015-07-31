package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;

public	class AlignmentsTableModel extends AbstractTableModel {

	public final boolean DEBUG;
	// Column names for the header and text output
	private String[] columnNames = {		//column index
			"Results",						//0		String
			"Alignment",					//1		String
			"Input type",					//2		String (enum)
			"# taxa",						//3		int
			"# sites (NT)",					//4		int
			"# invar. sites (NT)",			//5		int
			"# sites (AA)",					//6		int
			"# invar. sites (AA)",			//7		int
			"mean entropy (NT)",			//8		float
			"mean entropy (AA)",			//9		float
			"meanTaxonwiseLongestUngappedSequenceNT",//10	float
			"meanTaxonwiseLongestUngappedSequenceAA",//11	float
			"longestNonZeroEntropyRunNT",	//12	float
			"whichNonZeroEntropyRunNT",		//13	float
			"longestNonZeroEntropyRunAA",	//14	float
			"whichNonZeroEntropyRunAA",		//15	float
			"Selection data?",				//16	boolean
			"Source alignment"};			//17	AlignedSequenceRepresentation.toString()
	// The main data table
	private Object[][] data;
	// Default values for (hopefully) sizing the table, etc
	public final Object[] longValues = {
			"file", 
			"locus",
			"None of the above",
			new Integer(0), 
			new Integer(0), 
			new Integer(0), 
			new Integer(0), 
			new Integer(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			new Float(0), 
			Boolean.FALSE,
			new Object()
			};

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(data != null){
			for(String columnName:this.columnNames){
				sb.append(columnName+"\t");
			}
			sb.append("\n");
			for(Object[] aRow:data){
				for(Object o:aRow){
					sb.append(o.toString()+"\t");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
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
			/* Data table has been initialised. Add rows to it*/
			// Copy data to newData with an extra row and populate it.
			newData = new Object[getData().length+1][getData()[0].length];
			for(int i=0;i<getData().length;i++){
				newData[i] = getData()[i];
			}
			// Make a new row for the new data
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
			newRow[9] = rowDisplayAlignment.getMeanSitewiseEntropyAA();
			newRow[10] = rowDisplayAlignment.getMeanTaxonwiseLongestUngappedSequenceNT();
			newRow[11] = rowDisplayAlignment.getMeanTaxonwiseLongestUngappedSequenceAA();
			newRow[12] = rowDisplayAlignment.getLongestNonZeroEntropyRunNT();
			newRow[13] = rowDisplayAlignment.getWhichNonZeroEntropyRunNT();
			newRow[14] = rowDisplayAlignment.getLongestNonZeroEntropyRunAA();
			newRow[15] = rowDisplayAlignment.getWhichNonZeroEntropyRunAA();
			newRow[16] = false;
			newRow[17] = rowSourceAlignment;
			// Write the new row to the data table
			newData[getData().length] = newRow;
		}else{
			/* Data table is blank, instantate it with 1 row and as many cols as there are col names */
			newData = new Object[1][getColumnCount()];
			// Make a new row for the new data
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
			newRow[9] = rowDisplayAlignment.getMeanSitewiseEntropyAA();
			newRow[10] = rowDisplayAlignment.getMeanTaxonwiseLongestUngappedSequenceNT();
			newRow[11] = rowDisplayAlignment.getMeanTaxonwiseLongestUngappedSequenceAA();
			newRow[12] = rowDisplayAlignment.getLongestNonZeroEntropyRunNT();
			newRow[13] = rowDisplayAlignment.getWhichNonZeroEntropyRunNT();
			newRow[14] = rowDisplayAlignment.getLongestNonZeroEntropyRunAA();
			newRow[15] = rowDisplayAlignment.getWhichNonZeroEntropyRunAA();
			newRow[16] = false;
			newRow[17] = rowSourceAlignment;
			// Write the new row to the data table
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
