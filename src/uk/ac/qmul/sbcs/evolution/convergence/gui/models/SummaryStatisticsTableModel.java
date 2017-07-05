package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.util.HashMap;

import javassist.bytecode.Descriptor.Iterator;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.math.NumberUtils;

import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

public class SummaryStatisticsTableModel extends AbstractTableModel {

	public final static String[] columnNames = {
		"Statistic\t",
		"N\t",
		"Min\t",
		"Median",
		"Mean\t",
		"Max\t"
	};
	
	private final static Object[] longValues = {
		new String(),
		new Integer(0), 
		new Float(0), 
		new Float(0), 
		new Float(0), 
		new Float(0) 
	};

	// The main data table
	private Object[][] data;
	
	/**
	 * No-arg constructor is discouraged, unless you want a blank table. Use SummaryStatisticsTableModel(HashMap<String,Dataseries>) instead.
	 */
	public SummaryStatisticsTableModel() {
		this.data = new Object[1][6];
		this.data[0][0] = "<no data available>";
		this.data[0][1] = 0;
		this.data[0][2] = Float.NaN;
		this.data[0][3] = Float.NaN;
		this.data[0][4] = Float.NaN;
		this.data[0][5] = Float.NaN;
	}
	
	/**
	 * Preferred constructor.
	 * @param startingData - key, value map for <Stat name,Stat DataSeries>
	 */
	public SummaryStatisticsTableModel(HashMap<String,DataSeries> startingData){
		this.setData(startingData);
	}

	/*
	 * Ensures the columnn names are available.
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
	    return columnNames[col];
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
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

	//@Override
	//public Class getColumnClass(int c) {
	//	return getValueAt(0, c).getClass();
	//}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/** 
	 * The main way the table data is set
	 * @param startingData
	 */
	public void setData(HashMap<String, DataSeries> startingData) {
		// Create new data
		Object[][] newData = new Object[startingData.size()][6];
		java.util.Iterator<String> data = startingData.keySet().iterator();
		int rowCount = 0;
		while(data.hasNext()){
			String statName = data.next();
			DataSeries statData = startingData.get(statName);
			newData[rowCount][0] = statName;							// name
			newData[rowCount][1] = statData.getCount();					// N
			newData[rowCount][2] = NumberUtils.min(statData.getData());	// min
			newData[rowCount][3] = statData.getMedian();				// median
			newData[rowCount][4] = statData.getMean();					// mean
			newData[rowCount][5] = NumberUtils.max(statData.getData());	// max
			rowCount++;
		}
		this.data = newData;
	}
}
