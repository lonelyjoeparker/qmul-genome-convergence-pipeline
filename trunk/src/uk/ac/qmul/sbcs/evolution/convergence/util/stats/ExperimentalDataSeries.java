package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * 
 * @author Joe Parker
 * @since 02 March, 2012
 * @see PercentileOutOfRangeError
 * 
 * This class is intended to provide basic utility methods for a vector of float data.
 * This class is an experimental adaptation of the DataSeries class that should (!) give better data binning etc
 */
public class ExperimentalDataSeries {
	private final ArrayList<Float> listData;
	private final float[] data;
	private float[] deviations;
	private final String name;
	private final int count;
	private static float min;
	private static float max;
	private float[] binBounds;
	private float[] counts;
	private float[] freqs;
	private float[] cumCountsProp;
	private float[] pointVals;
	
	public ExperimentalDataSeries(){
		this.data = null;
		this.name = null;
		this.count = 0;
		this.listData = null;
		this.max = 0;
		this.min = 0;
	}
	
	public ExperimentalDataSeries(float[] data, String name){
		this.data = data;
		this.name = name;
		this.count = data.length;
		this.max = 0;
		this.min = 0;
		listData = new ArrayList<Float>();
		for(float val:data){
			listData.add(val);
			if(val < min){
				min = val;
			}
			if(val > max){
				max = val;
			}
		}
	}
	
	public ExperimentalDataSeries(ArrayList<Float> listData, String name){
		this.name = name;
		this.count = listData.size();
		this.data = new float[count];
		this.max = 0;
		this.min = 0;
		for(int i = 0; i<count;i++){
			this.data[i] = listData.get(i); 
			if(this.data[i] < min){
				min = this.data[i];
			}
			if(this.data[i] > max){
				max = this.data[i];
			}
		}
		this.listData = listData;
	}
	
	public ExperimentalDataSeries(DataSeries aDataSeries) {
		// TODO Auto-generated constructor stub
		this.name = aDataSeries.getName();
		this.data = aDataSeries.getData();
		this.count = aDataSeries.getCount();
		this.max = 0;
		this.min = 0;
		listData = new ArrayList<Float>();
		for(float val:data){
			listData.add(val);
			if(val < min){
				min = val;
			}
			if(val > max){
				max = val;
			}
		}
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public static void setMin(float min) {
		ExperimentalDataSeries.min = min;
	}

	public static void setMax(float max) {
		ExperimentalDataSeries.max = max;
	}

	public float[] getData(){
		return data;
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
		float median;
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
		return median;
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
	public float returnUpperPercentileOverlap(ExperimentalDataSeries query){
		// TODO ------ It might be a good idea to specify bin positions; and this could even be a Bins object (novel)
		// TODO ------ Maybe if these implemented Collections and Comparator, wouldn't need to sort etc
		return 0.0f;
	}

	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the lower percentile where this DataSeries crosses the other one
	 */
	public float returnLowerPercentileOverlap(ExperimentalDataSeries query){
		return 0.0f;
	}

	/**
	 * 
	 * @param query - the other DataSeries
	 * @return the KS value
	 */
	public float doKStest(ExperimentalDataSeries query){
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
	public ExperimentalDataSeries compareData(ExperimentalDataSeries otherData) throws UnequalDataSeriesLengthException{
		if(otherData.count != this.count){
			throw new UnequalDataSeriesLengthException();
		}else{
			float[] differences = new float[count];
			float[] otherDataArray = otherData.getData();
			for(int i=0;i<count;i++){
				differences[i] = data[i] - otherDataArray[i];
			}
			return new ExperimentalDataSeries(differences, "Differences between "+this.name+" and "+otherData.getMode());
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
	
	@Deprecated
	public float[][] getCountsDataDeprecateMe(){
		float[] binBounds = new float[100];
		Collections.sort(this.listData);
		
		/*
		 * Determine range etc
		 */
		float min = this.listData.get(0);
		float max = this.listData.get(count-1);
		float range = max-min;
		float incr = range/100;
		
		/*
		 * Form bins
		 */
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			binBounds[boundIndex] = min+(incr*boundIndex);
		}
		
		/*
		 * Populate bins with counts
		 */
		float[] counts = new float[binBounds.length];
		int dataIndex = 0;
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			while(listData.get(dataIndex) <= binBounds[boundIndex]){
				counts[boundIndex]++;
				dataIndex++;
			}
		}
		
		/*
		 * Finally, fill the whole retmatrix up
		 */
		float[][] ret = new float[100][2];
		
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			ret[boundIndex][0] = binBounds[boundIndex];
			ret[boundIndex][1] = counts[boundIndex];
		}

		return ret;
	}

	@Deprecated
	public float[][] getCountsPDFCDFDataDeprecateMe(){
		binBounds = new float[100];
		Collections.sort(this.listData);
		
		/*
		 * Determine range etc
		 */
		float min = this.listData.get(0);
		float max = this.listData.get(count-1);
		float range = max-min;
		float incr = range/100;
		
		/*
		 * Form bins
		 * 
		 * (by the way, I think all of this can be done in a single pass through the loop....)
		 */
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			binBounds[boundIndex] = min+(incr*boundIndex);
		}
		
		counts = new float[100]; // Length MUST == binBounds length, but hardcoded here for speed
		freqs = new float[100]; // see above
		cumCountsProp = new float[100]; // see above
		pointVals = new float[100]; // see above

		
		/*
		 * Populate bins with counts
		 * 
		 * (by the way, I think all of this can be done in a single pass through the loop....)
		 */
		int dataIndex = 0;
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			while(listData.get(dataIndex) <= binBounds[boundIndex]){
				counts[boundIndex]++;
				dataIndex++;
			}
		}
	
		
		/*
		 * Populate bins with freqs
		 * 
		 * (by the way, I think all of this can be done in a single pass through the loop....)
		 */
		for(int boundIndex = 0; boundIndex<100; boundIndex++){
			freqs[boundIndex] = counts[boundIndex]/count;
		}


		/*
		 * Populate bins with cumulative proportions
		 * 
		 * (by the way, I think all of this can be done in a single pass through the loop....)
		 */
		float runningProportion = 0;
		for(int boundIndex = 0; boundIndex<100; boundIndex++){
			cumCountsProp[boundIndex] = freqs[boundIndex]+runningProportion;
			runningProportion = cumCountsProp[boundIndex];
		}

		
		/*
		 * Populate bins with point values
		 * 
		 * (by the way, I think all of this can be done in a single pass through the loop....)
		 */
		for(int boundIndex = 0; boundIndex<100; boundIndex++){
			try {
				pointVals[boundIndex] = this.getValueAtPercentile(boundIndex);
			} catch (PercentileOutOfRangeError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		/*
		 * Finally, fill the whole retmatrix up
		 * 
		 * ret[i][0] - bin bounds
		 * ret[i][1] - count data
		 * ret[i][2] - freq data
		 * ret[i][3] - cumulative counts
		 * ret[i][4] - value at percentile
		 */
		float[][] ret = new float[100][5];
		
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			ret[boundIndex][0] = binBounds[boundIndex];
			ret[boundIndex][1] = counts[boundIndex];
			ret[boundIndex][2] = freqs[boundIndex];
			ret[boundIndex][3] = cumCountsProp[boundIndex];
			ret[boundIndex][4] = pointVals[boundIndex];
		}
	
		return ret;
	}

	@Deprecated
	public float[][] getCountsPDFCDFDataDeprecateMeEfficient(float explicitMin, float explicitMax){
		binBounds = new float[100];
		Collections.sort(this.listData);
		
		/*
		 * Determine range etc
		 */
		float min = explicitMin;
		float max = explicitMax;
		float range = max-min;
		float incr = range/100;
		counts = new float[100]; // Length MUST == binBounds length, but hardcoded here for speed
		freqs = new float[100]; // see above
		cumCountsProp = new float[100]; // see above
		pointVals = new float[100]; // see above
	
		
		int dataIndex = 0;
		float runningProportion = 0;
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			/*
			 * Form bins
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			binBounds[boundIndex] = min+(incr*boundIndex);
			/*
			 * Populate bins with counts
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			try {
				while(listData.get(dataIndex) <= binBounds[boundIndex]){
					counts[boundIndex]++;
					dataIndex++; // may need a try / catch here..
				}
			} catch (IndexOutOfBoundsException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/*
			 * Populate bins with freqs
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			freqs[boundIndex] = counts[boundIndex]/count;
			/*
			 * Populate bins with cumulative proportions
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			cumCountsProp[boundIndex] = freqs[boundIndex]+runningProportion;
			runningProportion = cumCountsProp[boundIndex];
			/*
			 * Populate bins with point values
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
				try {
					pointVals[boundIndex] = this.getValueAtPercentile(boundIndex);
				} catch (PercentileOutOfRangeError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	
		
		/*
		 * Finally, fill the whole retmatrix up
		 * 
		 * ret[i][0] - bin bounds
		 * ret[i][1] - count data
		 * ret[i][2] - freq data
		 * ret[i][3] - cumulative counts
		 * ret[i][4] - value at percentile
		 */
		float[][] ret = new float[100][5];
		
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			ret[boundIndex][0] = binBounds[boundIndex];
			ret[boundIndex][1] = counts[boundIndex];
			ret[boundIndex][2] = freqs[boundIndex];
			ret[boundIndex][3] = cumCountsProp[boundIndex];
			ret[boundIndex][4] = pointVals[boundIndex];
		}
	
		return ret;
	}

	@Deprecated
	public float[][] getCountsPDFCDFDataDeprecateMeEfficient(){
		binBounds = new float[100];
		Collections.sort(this.listData);
		
		/*
		 * Determine range etc
		 */
		float min = this.listData.get(0);
		float max = this.listData.get(count-1);
		float range = max-min;
		float incr = range/100;
		counts = new float[100]; // Length MUST == binBounds length, but hardcoded here for speed
		freqs = new float[100]; // see above
		cumCountsProp = new float[100]; // see above
		pointVals = new float[100]; // see above
	
		
		int dataIndex = 0;
		float runningProportion = 0;
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			/*
			 * Form bins
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			binBounds[boundIndex] = min+(incr*boundIndex);
			/*
			 * Populate bins with counts
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			while(listData.get(dataIndex) <= binBounds[boundIndex]){
				counts[boundIndex]++;
				dataIndex++;
			}
			/*
			 * Populate bins with freqs
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			freqs[boundIndex] = counts[boundIndex]/count;
			/*
			 * Populate bins with cumulative proportions
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
			cumCountsProp[boundIndex] = freqs[boundIndex]+runningProportion;
			runningProportion = cumCountsProp[boundIndex];
			/*
			 * Populate bins with point values
			 * 
			 * (by the way, I think all of this can be done in a single pass through the loop....)
			 */
				try {
					pointVals[boundIndex] = this.getValueAtPercentile(boundIndex);
				} catch (PercentileOutOfRangeError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	
		
		/*
		 * Finally, fill the whole retmatrix up
		 * 
		 * ret[i][0] - bin bounds
		 * ret[i][1] - count data
		 * ret[i][2] - freq data
		 * ret[i][3] - cumulative counts
		 * ret[i][4] - value at percentile
		 */
		float[][] ret = new float[100][5];
		
		for(int boundIndex = 0; boundIndex<100;boundIndex++){
			ret[boundIndex][0] = binBounds[boundIndex];
			ret[boundIndex][1] = counts[boundIndex];
			ret[boundIndex][2] = freqs[boundIndex];
			ret[boundIndex][3] = cumCountsProp[boundIndex];
			ret[boundIndex][4] = pointVals[boundIndex];
		}
	
		return ret;
	}
	
	private boolean binsInitialised(){
		return !((binBounds == null)&&(counts==null)&&(freqs==null)&&(cumCountsProp==null)&&(pointVals==null));
	}
	
	public float getThresholdValueAtCumulativeDensity(float density) throws PercentileOutOfRangeError{
		if(density<0 || density>1){
			throw new PercentileOutOfRangeError();
		}
		if(!this.binsInitialised()){
			this.getCountsPDFCDFDataDeprecateMeEfficient();
		}
		float retval = pointVals[0];
		
		int binpos = 0;
		while((binpos<100)&&(cumCountsProp[binpos] < density)){
			retval = pointVals[binpos];
			binpos ++;
		}
		return retval;
	}
	
	public int getNumberLessThan(float lowerBound){
		int count = 0;
		Collections.sort(this.listData);
		Iterator itr = listData.iterator();
		while(itr.hasNext()){
			if((Float)itr.next() < lowerBound){
				count ++;
			}
		}
		return count;
	}

	public int getNumberGreaterThan(float upperBound){
		int count = 0;
		Collections.sort(this.listData);
		Iterator itr = listData.iterator();
		while(itr.hasNext()){
			if((Float)itr.next() > upperBound){
				count ++;
			}
		}
		return count;
	}
}
