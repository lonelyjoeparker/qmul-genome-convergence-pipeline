/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;

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
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
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
		if(data != null){
			return getValueAt(0, c).getClass();
		}else{
			return null;
		}
	}

	public Object[][] getData(){
		return data;
	}

	/* Constructors */
	
	/**
	 * Default no-arg constructor
	 */
	public AnalysesModel(){
		// instantiate the table, although this is a bit risky as null vals in first row...
		data = new Object[1][3];
		data[0][0] = new File("/");
		data[0][1] = new File("/");
		data[0][2] = false;
	}
	
	/* Object methods for specific AnalysesModel-y things... */

	/**
	 * Add a new analysis to the TableModel as a File.
	 */
	public void addAnalysisFile(File newFile){
		Object [][] newData;
		if((data.length == 1)&&(data[0][0] == null)){
			System.out.println("the first row of the table is null");
			// rather than update it we should just replace
			newData = new Object[1][3];
			Object[] newRow = new Object[3];
			newRow[0] = newFile;
			newRow[1] = newFile;
			newRow[2] = false;
			newData[0] = newRow;
		}else{
			// data already exists
			newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[this.getColumnCount()];
			newRow[0] = newFile;
			newRow[1] = newFile;
			newRow[2] = false;
			newData[data.length] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}
}
