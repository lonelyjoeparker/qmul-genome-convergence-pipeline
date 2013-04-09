package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @version 0.1
 * @since r145 2013/04/07
 * 
 * <p>A class to allow for a jsc.goodnessfit.KolmolgorovTest, which requires an EmpriricalDistribution
 *
 * <p>This represents unsorted real-valued input data as a probability density function.
 * 
 * @see EmpiricalDistribution
 */
public class ProbabilityDensityFunction {

	private double[] values;
	private double[] function;
	private BigDecimal[] binLimits;
	private int N;
	private BigDecimal[] observationsRange = new BigDecimal[2];
	private BigDecimal[] functionRange = new BigDecimal[2];
	private double sum;
	private BigDecimal increment;
	private int binCount;
	
	/**
	 * No-arg constructor for PDF. This should never be used.
	 */
	@Deprecated
	public ProbabilityDensityFunction(){
		// TODO void no-arg constructor
	}
	
	/**
	 * 
	 * @param input 
	 * @deprecated - this method doesn't currently implement initialisation for vars including double[] function, pending implementation and testing of determineIncrement()
	 * <p>One-arg constructor for unsupervised PDF. Range will be observations' range, increment will be at most (range/2N); 
	 * TODO implement check that range is integer multiple of increment.
	 * 
	 * 
	 */
	@Deprecated
	public ProbabilityDensityFunction(double[] input){
		// TODO one-arg constructor
		this.values = input;
		Arrays.sort(values);
		this.N = values.length;
		this.observationsRange[0] = new BigDecimal(values[0]);
		this.observationsRange[1] = new BigDecimal(values[N]);
		this.functionRange = observationsRange.clone();
		this.increment = this.determineIncrement();
		
	}
	

	/**
	 * 
	 * @param input
	 * @param specifiedRange - range for the function to go over, <i>not</i> observations range
	 * @param specifiedIncrement - real-valued increments for bins
	 * 
	 * <p>Explicit constructor. Assumes specifiedIncrement has already been determined, should satisfy increment ² range/2N.
	 */
	public ProbabilityDensityFunction(double[] input, double[] specifiedRange, BigDecimal specifiedIncrement){
		// TODO constructor with explicity-specified instance vars
		this.values = input;
		Arrays.sort(values);
		this.N = values.length;
		this.observationsRange[0] = new BigDecimal(values[0]);
		this.observationsRange[1] = new BigDecimal(values[N-1]);
		this.functionRange[0] = new BigDecimal(specifiedRange[0]);
		this.functionRange[1] = new BigDecimal(specifiedRange[1]);
		this.increment = specifiedIncrement;
		this.binCount = this.functionRange[1].subtract(this.functionRange[0]).divide(this.increment).intValue();
		this.binLimits = new BigDecimal[this.binCount];
		this.function  = new double[this.binCount];
		try {
			this.computeDensity();
		} catch (IllegalRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param input
	 * @param template - another PDF to be used as a template to ensure identical ranges. 
	 * 
	 * <p>Range, increment etc will be taken from template PDF
	 */
	public ProbabilityDensityFunction(double[] input, ProbabilityDensityFunction template){
		// TODO constructor instantiated by comparison to existing PDF
		this.values = input;
		Arrays.sort(values);
		this.N = values.length;
		this.observationsRange[0] = new BigDecimal(values[0]);
		this.observationsRange[1] = new BigDecimal(values[N-1]);
	}

	/**
	 * @return the observations of X
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * @return the probability density function f(x)
	 */
	public double[] getFunction() {
		return function;
	}

	/**
	 * @return the number of observations
	 */
	public int getN() {
		return N;
	}

	/**
	 * @return the range of x
	 */
	public BigDecimal[] getRange() {
		return observationsRange;
	}

	/**
	 * @return the sum of observations
	 */
	public double getSum() {
		return sum;
	}

	/**
	 * @return the increment
	 */
	public BigDecimal getIncrement() {
		return increment;
	}

	/**
	 * @return the binCount
	 */
	public int getBinCount() {
		return binCount;
	}
	
	/**
	 * 
	 * @return increment such that range is integer multiple of increment, and at most (range/2N)
	 * TODO implement
	 */
	private BigDecimal determineIncrement() {
		// TODO Auto-generated method stub
		
		// wireframe, very very quick version, CHECK CAREFULLY
		/*
		 * get range
		 * propose increment = range/2N
		 * 
		 * OR IF SETTING #bins directly
		 * increment * #bins has to > range[1]
		 * if not >> increment until it does
		 * and >> range[1] if required (this implies range[1] - penultimateBinInterval is < proposed increment)
		 */
	
		assert(false);
		return null;
	}

	/**
	 * Private method to calculate the density function
	 * @throws IllegalRangeException
	 */
	private void computeDensity() throws IllegalRangeException{
		// Check data range is in target function range
		if((this.values[0] < this.functionRange[0].doubleValue())||(this.values[this.N-1]>this.functionRange[1].doubleValue())){
			throw new IllegalRangeException("Data values cover larger range than target PDF");
		}
		
		// Initialise valuesIndex - the counter for the input data values array
		int valuesIndex = 0;

		// Init first bin
		this.binLimits[0] = this.functionRange[0];
		// Step through bins
		for(int i=0;i<this.binCount;i++){
			// fill up bins, how many data points etc
			this.function[i] = 0.0d;
			BigDecimal nextBin = this.binLimits[i].add(this.increment, MathContext.DECIMAL128);
			if(this.values[valuesIndex] >= binLimits[i].doubleValue()){
				
				// Step go through values until we reach the bin interval..
				// To allow for range values[0] == 0 etc, comparison is left-oriented, e.g binned if (leftBin ² x < rightBin) 
				while((this.values[valuesIndex] >= this.binLimits[i].doubleValue())&&(this.values[valuesIndex] < nextBin.doubleValue())&&(valuesIndex<this.N)){
					this.function[i] = this.function[i] + 1.0d;
					if(valuesIndex < this.N-1){
						valuesIndex++;
					}else{
						nextBin= new BigDecimal(Integer.MIN_VALUE);
					}
				}

				// Calculate the density
				this.function[i] = (this.function[i] / this.N);
			}
			if (i<(binCount-1)) this.binLimits[i+1] = this.binLimits[i].add(this.increment, MathContext.DECIMAL128);
		}
		for(double someDensity:function){
			this.sum += someDensity;
		}
	}
}
