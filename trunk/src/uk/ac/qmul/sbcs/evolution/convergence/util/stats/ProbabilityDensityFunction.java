package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @version 0.1
 * @since r145 2013/04/07
 */
public class ProbabilityDensityFunction {

	private double[] values;
	private double[] function;
	private int N;
	private double[] range = {(Double) null,(Double) null};
	private double sum;
	private double increment;
	private int binCount;
	
	/**
	 * No-arg constructor for PDF. This s
	 */
	@Deprecated
	public ProbabilityDensityFunction(){
		// TODO void no-arg constructor
	}
	
	public ProbabilityDensityFunction(double[] input){
		// TODO one-arg constructor
	}
	
	public ProbabilityDensityFunction(double[] input, double[] specifiedRange, double[] increment){
		// TODO constructor with explicity-specified instance vars
	}
	
	public ProbabilityDensityFunction(double[] input, ProbabilityDensityFunction template){
		// TODO constructor instantiated by comparison to existing PDF
	}
}
