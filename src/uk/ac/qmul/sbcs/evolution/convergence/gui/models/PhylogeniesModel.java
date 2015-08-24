package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import uk.ac.qmul.sbcs.evolution.convergence.PhylogenyConvergenceContext;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogenyFactory;

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
	public void addPhylogenyRow(File newTreeAsFile){
		/* First, construct DisplayPhylogeny objects usind DisplayPhylogenyFactory methods */
		DisplayPhylogeny[] phylogenies = DisplayPhylogenyFactory.fromFile(newTreeAsFile);
		if((phylogenies != null)&&(phylogenies.length>0)){
			/* 
			 * The data[][] table will need to either be instantiated (if null) 
			 * or have rows added (if not null), with # new/extra rows == phylogenies.length
			 */
			int numberOfPhylogenies = phylogenies.length;
			Object[][] newData;
			if(data == null){
				System.out.println("the first row of the table is null");
				// Create a new table
				newData = new Object[numberOfPhylogenies][getColumnCount()];
				int rowCount = 0;
				for(DisplayPhylogeny dp:phylogenies){
					// Create a new row as an Object[]
					Object[] newRow = new Object[getColumnCount()];
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
					// Add the new row to the table
					newData[rowCount] = newRow;
					// Don't forget to increment the row counter
					rowCount++;
				}
			}else{
				// data already exists
				// create a new data table, with additional rows equal to number of phylogenies
				newData = new Object[data.length+numberOfPhylogenies][data[0].length];
				for(int i=0;i<data.length;i++){
					newData[i] = data[i];
				}
				// initialise row counter - set to data.length, e.g. first of the new (empty) rows
				int rowCount = data.length;
				for(DisplayPhylogeny dp:phylogenies){
					// Create a new row as an Object[]
					Object[] newRow = new Object[getColumnCount()];
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
					// Add the new row to the table
					newData[rowCount] = newRow;
					// Don't forget to increment the row counter
					rowCount++;
				}
			}
			data = newData;
			this.fireTableRowsInserted(data.length-1, data.length-1);
		}
	}
	
	
	/**
	 * Returns an array containing those DisplayPhylogeny objects corresponding to the required 
	 * convergence context (i.e., species/reference phylogeny, alternative/hypothesis phylogeny, 
	 * RAxML constraint tree, etc)
	 * @param context - {@link PhylogenyConvergenceContext}
	 * @return {@link DisplayPhylogeny}[]
	 * @see DisplayPhylogeny
	 * @see PhylogenyConvergenceContext
	 */
	public DisplayPhylogeny[] getPhylogeniesByConvergenceContext(PhylogenyConvergenceContext context){
		// Initialise an ArrayList to temp hold matching phylogenies 
		ArrayList<DisplayPhylogeny> matchingPhylogenies = new ArrayList<DisplayPhylogeny>();
		
		// Iterate through data[][] array, adding DisplayPhylogenies to the arrayList if their contexts match
		for(Object[] aRow:data){
			if(context == ((PhylogenyConvergenceContext)aRow[aRow.length-1])){
				matchingPhylogenies.add((DisplayPhylogeny)aRow[0]);
			}
		}
		
		// see if any matching phylogenies were found. 
		if(matchingPhylogenies.size()>0){
			// If so: Cast the arrayList to a DisplayPhylogeny[] array
			DisplayPhylogeny[] returnPhylogeniesArray = matchingPhylogenies.toArray(new DisplayPhylogeny[matchingPhylogenies.size()]);
			// return the array
			return returnPhylogeniesArray;
		}else{
			// return null
			return null;
		}
	}
}
