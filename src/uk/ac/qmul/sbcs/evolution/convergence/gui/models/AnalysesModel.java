/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import com.thoughtworks.xstream.XStream;

import uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.runners.GeneralCongruenceRunnerXML;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class AnalysesModel extends AbstractTableModel {

	/* Object variables */
	
	public final String[] columnNames = new String[]{"Analysis XML File","Input alignment","Run locally?"};
	private Object[][] data;
	
	/* Utility methods for TableModel type behaviour */

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(data != null){
			return data.length;
		}else{
			return 0;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(data != null){
			return data[row][col];
		}else{
			return null;
		}
	}

	/*
	 * Don't need to implement this method unless column's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
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

	/**
	 * Returns the internal Object[][] representing data. <b>Remember:</b>
	 * <ul>
	 * <li>All Objects must be cast - which class depends on which column, see javadoc for this class;</li>
	 * <li>This method may throw NullPointerException or similar if the data array has not been filled (when this class is instantiated data is left null).</li>
	 * </ul>
	 * @return Object[][] containing the table data.
	 */
	public Object[][] getData(){
		return data;
	}

	/* Constructors */
	
	/**
	 * Default no-arg constructor
	 */
	public AnalysesModel(){
		// nothing to do here as we want data (Object[][]) to remain null.
	}
	
	/* Object methods for specific AnalysesModel-y things... */

	/**
	 * Add a new analysis to the TableModel as a File.
	 */
	public void addAnalysisFile(File newFile){
		XStream xstream = new XStream();
		SiteSpecificLikelihoodSupportAnalysis analysis = (SiteSpecificLikelihoodSupportAnalysis) xstream.fromXML(newFile);

		Object [][] newData;
		if(data == null){
			System.out.println("the first row of the table is null");
			// rather than update it we should just replace
			newData = new Object[1][3];
			Object[] newRow = new Object[3];
			newRow[0] = analysis;
			newRow[1] = analysis.getInputAlignment();
			newRow[2] = false;
			newData[0] = newRow;
		}else{
			// data already exists
			newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[this.getColumnCount()];
			newRow[0] = analysis;
			newRow[1] = analysis.getInputAlignment();
			newRow[2] = false;
			newData[data.length] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}

	public void addAnalysisRow(SiteSpecificLikelihoodSupportAnalysis analysis) {
		// TODO Auto-generated method stub
		// TODO not implemented yet
		Object [][] newData;
		if(data == null){
			// data array is empty
			System.out.println("the first row of the table is null");
			// rather than update it we should just replace
			newData = new Object[1][3];
			Object[] newRow = new Object[3];
			newRow[0] = analysis;
			newRow[1] = analysis.getInputAlignment();
			newRow[2] = false;
			newData[0] = newRow;
		}else{
			// data array already exists
			newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[this.getColumnCount()];
			newRow[0] = analysis;
			newRow[1] = analysis.getInputAlignment();
			newRow[2] = false;
			newData[data.length] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}

	public SiteSpecificLikelihoodSupportAnalysis getLastRowSSLSAnalysis() {
		if(data.length > 0){
			return (SiteSpecificLikelihoodSupportAnalysis) data[data.length-1][0];
		}else{
			return null;
		}
	}

	/**
	 *  Removes the selected row from the data model
	 * <p><b>Note that this operation is NOT SYNCHRONISED</b></p>
	 *  @param removeThisRow the row to delete
	 */
	public void removeRow(int removeThisRow) {
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
}
