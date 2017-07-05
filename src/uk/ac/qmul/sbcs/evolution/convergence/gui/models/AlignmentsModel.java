package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.math.NumberUtils;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

public	class AlignmentsModel extends AbstractTableModel {

	public final boolean DEBUG;
	// Column names for the header and text output
	private final static String[] columnNames = {			//column index
			"ResultsFilename",					//0		String
			"AlignmentFilename",				//1		String
			"InputType",						//2		String (enum)
			"NumberOfTaxa",						//3		int
			"NumberOfSitesNT",					//4		int
			"NumberOfInvariantSitesNT",			//5		int
			"NumberOfSitesAA",					//6		int
			"NumberOfInvariantSitesAA",			//7		int
			"MeanEntropyNT",					//8		float
			"MeanEntropyAA",					//9		float
			"MeanTaxonwiseLongestUngappedSequenceNT",//10	float
			"MeanTaxonwiseLongestUngappedSequenceAA",//11	float
			"LongestNonZeroEntropyRunNT",		//12	float
			"WhichNonZeroEntropyRunNT",			//13	float
			"LongestNonZeroEntropyRunAA",		//14	float
			"WhichNonZeroEntropyRunAA",			//15	float
			"HasSelectionData",					//16	boolean
			"SourceAlignmentHash"};				//17	AlignedSequenceRepresentation.toString()
	private final static String[] columnDefinitions = {	//column index
			"Results = Results file (alignment if native)",		//0		String
			"Alignment = Alignment file name",					//1		String
			"Input type = Nuclotide/amino acid/codon/none",		//2		String (enum)
			"# taxa = Number of taxa",							//3		int
			"# sites (NT) = Number of nucleotides (NT)",		//4		int
			"# invar. sites (NT) = Number of NT positions that are invariant across all taxa",						//5		int
			"# sites (AA) = Number of amino acids in translated sequence (AA; first forward reading frame assumed)",//6		int
			"# invar. sites (AA) = Number of NT positions that are invariant across all taxa",						//7		int
			"Mean entropy (NT) = Mean sitewise Shannon entropy (heterogeneity) in NT sequence",						//8		float
			"Mean entropy (AA) = Mean sitewise Shannon entropy (heterogeneity) in NT sequence",						//9		float
			"MeanTaxonwiseLongestUngappedSequenceNT = Longest contiguous ungapped NT sequence (averaged over all taxa)",//10	float
			"MeanTaxonwiseLongestUngappedSequenceAA = Longest contiguous ungapped AA sequence (averaged over all taxa)",//11	float
			"LongestNonZeroEntropyRunNT = Longest contiguous run of non-zero (ie, variant) NT sites",					//12	float
			"WhichNonZeroEntropyRunNT = Value of Shannon entropy (sitewise heterogeneity) in longest non-zero NT run",	//13	float
			"LongestNonZeroEntropyRunAA = Longest contiguous run of non-zero (ie, variant) NT sites",					//14	float
			"WhichNonZeroEntropyRunAA = Value of Shannon entropy (sitewise heterogeneity) in longest non-zero AA run",	//15	float
			"Selection data? = (not implemented - debug only)",															//16	boolean
			"Source alignment = (Java object code - debug only)"};														//17	AlignedSequenceRepresentation.toString()
	// The main data table
	private Object[][] data;
	// Holder for summary statistics
	private HashMap<String,DataSeries> summaryStatistics = new HashMap<String,DataSeries>();
	// Column indices of Integers
	private final static Integer[] integerIndices = new Integer[]{3,4,5,6,7};
	// Column indices of Floats
	private final static Integer[] floatIndices = new Integer[]{8,9,10,11,12,13,14,15};
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
	
	public AlignmentsModel(){
		DEBUG = false;
	}
	
	public AlignmentsModel(boolean doDebugOutput){
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

	/*
	 * Ensures the column names are available.
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
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

	/**
	 * Print the summary statistics for numeric columns to a string, tab-delimited.
	 * @return - String suitable for printing to STDOUT
	 */
	private String printPrettySummaryData(){
		StringBuilder output = new StringBuilder();
		output.append("Alignment stat\tN\tmin\tmedian\tmean\tmax\n");
		Iterator stats = summaryStatistics.keySet().iterator();
		while(stats.hasNext()){
			String stat = (String)stats.next();
			DataSeries data = summaryStatistics.get(stat);
			output.append(stat+"\t");
			output.append(data.getCount()+"\t");
			output.append(NumberUtils.min(data.getData())+"\t");
			output.append(data.getMedian()+"\t");
			output.append(data.getMean()+"\t");
			output.append(NumberUtils.max(data.getData())+"\n");
		}
		
		return output.toString();
	}
	
	/**
	 * Print the table completely (DEBUG)
	 */
	@Deprecated
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
	 * Casts Double[] to float[] via Double.floatValue()
	 * Typically this would be data model numeric column to DataSeries input.
	 * @param someArray
	 * @return
	 */
	private float[] doubleArrayToFloat(Double[] someArray){
		float[] returnArr = new float[someArray.length];
		for(int i=0;i<someArray.length;i++){
			returnArr[i] = someArray[i].floatValue();
		}
		return returnArr;
	}
	
	/**
	 * Globally sets all the data in the data model, overwriting completely.
	 * @param data the data to set
	 */
	public void setData(Object[][] data) {
		// Replace the current data - with NO CHECKING or UNDOs
		this.data = data;
		
		// Recalculate the DataSeries (summary stats) associated with each numeric column
		for(int column=3;column<16; column++){
			summaryStatistics.put(columnNames[column], new DataSeries(this.doubleArrayToFloat((this.getColumnDataAsDouble(column))), columnNames[column]));
		}
		//debug only
		//System.out.println(this.printPrettySummaryData());
	}

	/**
	 * @return the data
	 */
	public Object[][] getData() {
		return data;
	}

	public HashMap<String, DataSeries> getSummaryStatistics() {
		return summaryStatistics;
	}

	public Double[] getColumnDataAsDouble(int whichCol) throws ArrayIndexOutOfBoundsException{
		// work out whether this index is an int, a float, or something horrible-er
		if(whichCol < 3 || whichCol > 15){
			// probably not a numeric data column
			throw new ArrayIndexOutOfBoundsException("Not a numeric data column!");
		}else{
			// init return array
			Double[] returnArr = new Double[this.data.length];

			// now work out which data type we have and try to populate
			ArrayList<Integer> listFloats = new ArrayList<Integer>();
			listFloats.addAll(Arrays.asList(this.floatIndices));
			ArrayList<Integer> listInts = new ArrayList<Integer>();
			listInts.addAll(Arrays.asList(this.integerIndices));
			if(listFloats.contains(whichCol)){
				// probably a Float column
				for(int i=0;i<returnArr.length;i++){
					returnArr[i] = new Double((Float)this.data[i][whichCol]);
				}
			}else{
				if(listInts.contains(whichCol)){
					// probably an Integer column
					for(int i=0;i<returnArr.length;i++){
						returnArr[i] = new Double((Integer)this.data[i][whichCol]);
					}
				}
			}
			return returnArr;
		}
	}
	
	/**
	 * Get the definitions corresponding to each column in the data
	 * @return String[] of table definitions
	 */
	public String[] getTableColumnDefinitions() {
		// TODO Auto-generated method stub
		return this.columnDefinitions;
	}

	/**
	 * Get the definitions corresponding to each column in the data
	 * @return String[] of table definitions as HTML, including &lt;html&gt; tag
	 */
	public String[] getTableColumnDefinitionsHTML() {
		String [] HTMLdefinitions = new String[columnDefinitions.length+2];
		HTMLdefinitions[0] = "<html>";
		HTMLdefinitions[HTMLdefinitions.length-1] = "</html>";
		int counter = 1;
		for(String definition:columnDefinitions){
			String[] separateDefinitions = definition.split("=");
			if(separateDefinitions.length == 2){
				HTMLdefinitions[counter] = "<p><b>"+separateDefinitions[0]+"</b><br><i>"+separateDefinitions[1]+"</i></p>";
			}
			counter++;
		}
		return HTMLdefinitions;
	}
}
