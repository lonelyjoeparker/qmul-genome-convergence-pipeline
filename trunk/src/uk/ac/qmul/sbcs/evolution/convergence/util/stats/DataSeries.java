package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author Joe Parker
 * @since 19 October, 2011
 *
 */
public class DataSeries {
	private final ArrayList<Float> listData;
	private final float[] data;
	private float[] deviations;
	private final String name;
	private final int count;
	
	public DataSeries(){
		this.data = null;
		this.name = null;
		this.count = 0;
		this.listData = null;
	}
	
	public DataSeries(float[] data, String name){
		this.data = data;
		this.name = name;
		this.count = data.length;
		listData = new ArrayList<Float>();
		for(float val:data){
			listData.add(val);
		}
	}
	
	public DataSeries(ArrayList<Float> listData, String name){
		this.name = name;
		this.count = listData.size();
		this.data = new float[count];
		for(int i = 0; i<count;i++){
			this.data[i] = listData.get(i); 
		}
		this.listData = listData;
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
	 * @author Joe Parker
	 * @since 02/11/2011
	 * @param percentile - the desired percentile of the data
	 * @return float - value found in the distribution at that percentile
	 * @throws PercentileOutOfRangeError - if argument < 0 or > 100
	 */
	public float getValueAtPercentile(int percentile) throws PercentileOutOfRangeError{
		Collections.sort(this.listData);
		if(percentile<0||percentile>100){
			System.out.println("percentile out of range: "+percentile);
			throw new PercentileOutOfRangeError();
		}else if(percentile == 0){
			return listData.get(0);
		}else if(percentile == 100){
			return listData.get(count-1);
		}else{
			float value = 0.0f;
			float approxBin = (float) count * ((float)percentile/100);
//			System.out.println("%: "+percentile+" N "+count+", approx bin: "+approxBin);
			int index = Math.round(approxBin);
			value = listData.get(index);
			return value;
		}
	}
	
	/**
	 * 
	 * @param bound - the data position we are looking from
	 * @return percentile - the percentile at which this data is found in the data
	 */
	public int getPercentileCorrespondingToValue(float bound){
		int percentile = 0;
		int index = 0;
		boolean boundReached = false;
		Collections.sort(this.listData);
		for(float aFloat:listData){
			if(!boundReached){
				if(aFloat>=bound){
					float approxBin = ((float) index / (float) count)*100.0f;
//					System.out.println(approxBin);
					percentile = Math.round(approxBin);
					boundReached = true;
				}
			}
			index++;
		}
		if(!boundReached){
			percentile = 100;
		}
		return percentile;
	}
	
	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the upper percentile where this DataSeries crosses the other one
	 */
	public float returnUpperPercentileOverlap(DataSeries query){
		// TODO ------ It might be a good idea to specify bin positions; and this could even be a Bins object (novel)
		// TODO ------ Maybe if these implemented Collections and Comparator, wouldn't need to sort etc
		return 0.0f;
	}

	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the lower percentile where this DataSeries crosses the other one
	 */
	public float returnLowerPercentileOverlap(DataSeries query){
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

	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}
}
