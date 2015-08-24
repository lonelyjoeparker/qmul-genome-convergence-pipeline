package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author Joe Parker
 * @since 19 October, 2011
 * @see PercentileOutOfRangeError
 * 
 * This class is intended to provide basic utility methods for a vector of float data.
 */
public class DataSeries {
	private final ArrayList<Float> listData;
	private final float[] data;
	private float[] deviations;
	private final String name;
	private final int count;
	private float variance = Float.NaN;
	private float SS = Float.NaN;
	private float SE = Float.NaN;
	private float SD = Float.NaN;
	private float mean = Float.NaN;
	private float median = Float.NaN;
	private float sum = Float.NaN;
	
	
	public DataSeries(){
		this.data = null;
		this.name = null;
		this.listData = null;
		this.count = 0;
		this.variance = 0.0f;
		this.SS = 0.0f;
//		this.SE = 0.0f;
//		this.SD = 0.0f;
//		this.mean = 0.0f;
//		this.median = 0.0f;
		this.sum = 0.0f;
	}
	
	public DataSeries(float[] data, String name){
		this.data = data;
		this.name = name;
		this.count = data.length;
		this.listData = new ArrayList<Float>();
		this.SS = 0.0f;
		this.sum = 0.0f;
		for(float val:data){
			listData.add(val);
			sum+=val;
			SS += (val*val);
		}
	}
	
	public DataSeries(ArrayList<Float> listData, String name){
		this.name = name;
		this.count = listData.size();
		this.data = new float[count];
		this.SS = 0.0f;
		this.sum = 0.0f;
		for(int i = 0; i<count;i++){
			float val = listData.get(i);
			this.data[i] = val;
			sum+=val;
			SS += (val*val);
		}
		this.listData = listData;
	}
	
	public float[] getData(){
		return data;
	}
	
	public void formBins(float[] intervals){
		// TODO
	}
	
	public void calcMean(){
		mean = this.sum / (float)count;
	}

	public float getMean(){
		if(new Float(mean).isNaN()){
			this.calcMean();
		}
		return mean;
	}
	
	/**
	 * @TODO implement this
	 * @return the mode
	 */
	public float getMode(){
		return 0.0f;
	}

	public void calcMedian(){
		Collections.sort(this.listData);
		if((Math.floor(count/2))*2<count){
			// odd
			int index = (int)Math.floor(count/2);
			median = listData.get(index);
		}else{
			// even
			int index = count/2;
			median = (listData.get(index)+listData.get(index-1))/2;
		}
	}

	public float getMedian(){
		if(new Float(median).isNaN()){
			this.calcMedian();
		}
		return median;
	}
	
	public void calcSD(){
		SD = (float)Math.sqrt(variance);
	}
	
	public float getSD(){
		if(new Float(SD).isNaN()){
			this.calcSD();
		}
		return SD;
	}
	
	public void calcSE(){
		SE = 0.0f;
		float mean = this.getMean();
		deviations = new float[this.data.length];
		for(int i=0; i<data.length; i++){
			deviations[i] = mean - data[i];
			SE += deviations[i];
		}
		assert(count == deviations.length);
		assert(count == data.length);
		SE = SE / (float)count;
	}
	
	public float getSE(){
		if(new Float(SE).isNaN()){
			this.calcSE();
		}
		return SE;
	}
	
	public void calcVariance(){
		// TODO implement this
		variance = (SS / (float)count) - (this.getMean()*this.getMean());
	}
	
	public float getVariance(){
		if(new Float(variance).isNaN()){
			this.calcVariance();
		}
		return variance;
	}
	
	
	/**
	 * @deprecated - sum should be calculated in constructor.
	 */
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
	 * 
	 * TODO Note that this class will return zeroes or NaN when there are fewer than 100 floats in the bin - RIRO
	 * FIXME This needs correcting.
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
	 * 
	 * TODO Note that this class will return zeroes or NaN when there are fewer than 100 floats in the bin - RIRO
	 * FIXME This needs correcting.
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
	
	/**
	 * 
	 * @param otherData
	 * @return a new DataSeries which is the differences (this - otherData)
	 * @throws UnequalDataSeriesLengthException
	 */
	public DataSeries compareData(DataSeries otherData) throws UnequalDataSeriesLengthException{
		if(otherData.count != this.count){
			throw new UnequalDataSeriesLengthException();
		}else{
			float[] differences = new float[count];
			float[] otherDataArray = otherData.getData();
			for(int i=0;i<count;i++){
				differences[i] = data[i] - otherDataArray[i];
			}
			return new DataSeries(differences, "Differences between "+this.name+" and "+otherData.getMode());
		}
	}
	
	/*
	 * Prints the data series to stdout.
	 */
	public void printBasic(){
		System.out.println(name);
		for(float value:data){
			System.out.println(value);
		}
	}
	
	public int numObservationsGreaterThan(float threshold){
		int score = 0;
		for(float observation:data){
			if(observation>threshold){
				score++;
			}
		}
		return score;
	}
	
	public int numObservationsLessThan(float floor){
		int score = 0;
		for(float observation:data){
			if(observation<floor){
				score++;
			}
		}
		return score;
	}

	public float getSSE() {
		// TODO Auto-generated method stub
		this.calcMean();
		float SSE = 0.0f;
		for(float val:data){
			float dev = mean - val;
			dev = dev*dev;
			dev = (float) Math.pow(dev, 0.5);
			SSE += dev;
		}
		return (SSE/count);
	}
}
