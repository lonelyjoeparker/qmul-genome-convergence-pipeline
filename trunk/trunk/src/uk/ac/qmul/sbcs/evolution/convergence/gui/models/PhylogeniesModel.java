package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.PhylogenyConvergenceContext;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;

public class PhylogeniesModel extends AbstractTableModel{

	/* Object variables */
	
	public final String[] columnNames = new String[]{"File","Number of phylogenies","Number of tips","First phylogeny","Phylogeny convergence type"};
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

    /**
     * Overridden method to set value of (last column), ie the convergenceContextType
     */
   public boolean isCellEditable(int row, int col) {
        if (col != getColumnCount()-1) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Overridden method to set value of (last column), ie the convergenceContextType
     */
    public void setValueAt(Object value, int row, int col) {
    	if(col == getColumnCount()-1){
    		// get the convergence context
    		PhylogenyConvergenceContext newPhylogenyConvergenceContext = (PhylogenyConvergenceContext)value;
            // set the table value
    		data[row][col] = newPhylogenyConvergenceContext;
    		// don't forget to update the displayphylogeny
            DisplayPhylogeny dp = (DisplayPhylogeny)data[row][0];
            dp.setConvergenceContext(newPhylogenyConvergenceContext);
            // and... update again
            data[row][0] = dp;
            fireTableCellUpdated(row, col);
    	}
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
			newData = new Object[1][getColumnCount()];
			Object[] newRow = new Object[getColumnCount()];
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
				newRow[2] = dp.getTreeNode().howManyTips();
			} catch (Exception e) {
				newRow[2] = "0";
			}
			try {
				newRow[3] = dp.getTextTreeRepresentation();
			} catch (Exception e) {
				newRow[3] = "();";
			}
			try {
				newRow[4] = dp.getConvergenceContext();
			} catch (Exception e) {
				newRow[4] = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
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
				newRow[2] = dp.getTreeNode().howManyTips();
			} catch (Exception e) {
				newRow[2] = "0";
			}
			try {
				newRow[3] = dp.getTextTreeRepresentation();
			} catch (Exception e) {
				newRow[3] = "();";
			}
			try {
				newRow[4] = dp.getConvergenceContext();
			} catch (Exception e) {
				newRow[4] = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
			}
			newData[data.length] = newRow;
		}
		data = newData;
		this.fireTableRowsInserted(data.length-1, data.length-1);
	}
}
