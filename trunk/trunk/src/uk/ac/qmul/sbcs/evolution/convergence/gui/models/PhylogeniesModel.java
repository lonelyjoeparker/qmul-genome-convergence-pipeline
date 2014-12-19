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
	public PhylogeniesModel(){
		// instantiate the table, although this is a bit risky as null vals in first row...
		data = new Object[1][3];
		data[0][0] = new DisplayPhylogeny("(A,(B,C));");
		data[0][1] = 0;
		data[0][2] = "(A,(B,C));";
	}
	
	/* Object methods for specific PhylogenyModel-y things... */
	
	/**
	 * Add a new phylogeny to the TableModel as a File.
	 */
	public void addPhylogenyRowAsStringTree(File newTreeAsFile){
		if((data.length == 1)&&(data[0][0] == null)){
			System.out.println("the first row of the table is null");
			// rather than update it we should just replace
			Object[][] newData = new Object[1][3];
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
			data = newData;
			this.fireTableRowsInserted(data.length-1, data.length-1);
		}else{
			// data already exists
			Object[][] newData = new Object[data.length+1][data[0].length];
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
			data = newData;
			this.fireTableRowsInserted(data.length-1, data.length-1);
		}
	}
}
