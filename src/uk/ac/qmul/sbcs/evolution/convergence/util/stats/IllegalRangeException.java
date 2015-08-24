/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @since r145, 2013/04/09
 * 
 * <p>Range exception thrown by ProbabilityDensityFunction (mainly ProbabilityDensityFunction.computeDensity()) - where the data values span a larger range than the double[] specidiedRange
 * 
 * @see ProbabilityDensityFunction
 */
public class IllegalRangeException extends Exception {

	/**
	 * 
	 */
	public IllegalRangeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public IllegalRangeException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public IllegalRangeException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public IllegalRangeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
