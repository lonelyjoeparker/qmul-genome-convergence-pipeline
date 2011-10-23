package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.util.ArrayList;

/**
 * 
 * @author Joe Parker
 * @since 19 October, 2011
 *
 */
public class DataSeries {
	private final float[] data;
	private float[] deviations;
	private final String name;
	private final int count;
	
	public DataSeries(){
		this.data = null;
		this.name = null;
		this.count = 0;
	}
	
	public DataSeries(float[] data, String name){
		this.data = data;
		this.name = name;
		this.count = data.length;
	}
	
	public DataSeries(ArrayList<Float> listData, String name){
		this.name = name;
		this.count = listData.size();
		this.data = new float[count];
		for(int i = 0; i<count;i++){
			this.data[i] = listData.get(i); 
		}
	}
	
	public void formBins(float[] intervals){
		// TODO
	}
	
	public float getMean(){
		float mean = this.getSum() / (float)count;
		return mean;
	}
	
	public float getMode(){
		return 0.0f;
	}

	public float getMedian(){
		return 0.0f;
	}
	
	public float getSE(){
		float SE = 0.0f;
		float mean = this.getMean();
		deviations = new float[this.data.length];
		for(int i=0; i<data.length; i++){
			deviations[i] = mean - data[i];
			SE += deviations[i];
		}
		assert(count == deviations.length);
		assert(count == data.length);
		SE = SE / (float)count;
		return SE;
	}
	
	public float getSum(){
		float sum = 0.0f;
		for(float num:data){
			sum += num;
		}
		return sum;
	}
	
	public void calculatePDF(){}
	
	public void calculateCDF(){}
	
	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the upper percentile where this DataSeries crosses the other one
	 */
	public float returnUpperPercentile(DataSeries query){
		// TODO ------ It might be a good idea to specify bin positions; and this could even be a Bins object (novel)
		// TODO ------ Maybe if these implemented Collections and Comparator, wouldn't need to sort etc
		return 0.0f;
	}

	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the lower percentile where this DataSeries crosses the other one
	 */
	public float returnLowerPercentile(DataSeries query){
		return 0.0f;
	}

	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the KS value
	 */
	public float doKStest(DataSeries query){
		return 0.0f;
	}

	public String getName() {
		return name;
	}
}
