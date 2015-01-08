package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;

public class PhylogeniesModel extends AbstractTableModel{

	/* Object variables */
	
	public final String[] columnNames = new String[]{"File","Number of phylogenies","First phylogeny"};
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

	/**
	 * Returns an internal Object representing data. <b>Remember:</b>
	 * <ul>
	 * <li>All Objects must be cast - which class depends on which column, see javadoc for this class;</li>
	 * <li>This method may throw NullPointerException or similar if the data array has not been filled (when this class is instantiated data is left null).</li>
	 * </ul>
	 * @return Object containing the table data for this cell.
	 */
	@Override
	public Object getValueAt(int row, int col) {
		if(data != null){
			return data[row][col];
		}else{
			return null;
		}
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

	/*
	 * Don't need to implement this method unless column's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
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
	public PhylogeniesModel(){
		// nothing to do here as we want data (Object[][]) to remain null.
	}
	
	/* Object methods for specific PhylogenyModel-y things... */
	
	/**
	 * Add a new phylogeny to the TableModel as a File.
	 */
	public void addPhylogenyRowAsStringTree(File newTreeAsFile){
		Object[][] newData;
		if(data == null){
			System.out.println("the first row of the table is null");
			// rather than update it we should just replace
			newData = new Object[1][3];
			Object[] newRow = new Object[3];
			DisplayPhylogeny dp;
			try {
				dp = new DisplayPhylogeny(newTreeAsFile);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				dp = new DisplayPhylogeny("();");
			}
			newRow[0] = dp;
			try {
				newRow[1] = dp.getNewickTree().getNumberOfTrees();
			} catch (Exception e) {
				newRow[1] = 0;
			}
			try {
				newRow[2] = dp.getTextTreeRepresentation();
			} catch (Exception e) {
				newRow[2] = "();";
			}
			newData[0] = newRow;
		}else{
			// data already exists
			newData = new Object[data.length+1][data[0].length];
			for(int i=0;i<data.length;i++){
				newData[i] = data[i];
			}
			Object[] newRow = new Object[this.getColumnCount()];
			DisplayPhylogeny dp;
			try {
				dp = new DisplayPhylogeny(newTreeAsFile);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				dp = new DisplayPhylogeny("();");
			}
			newRow[0] = dp;
			try {
				newRow[1] = dp.getNewickTree().getNumberOfTrees();
			} catch (Exception e) {
				newRow[1] = 0;
			}
			try {
				newRow[2] = dp.getTextTreeRepresentation();
			} catch (Exception e) {
				newRow[2] = "();";
			}
			newData[data.length] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}
}
