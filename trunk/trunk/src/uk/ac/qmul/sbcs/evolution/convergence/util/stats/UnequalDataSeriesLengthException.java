/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class UnequalDataSeriesLengthException extends Exception {

	/**
	 * 
	 */
	public UnequalDataSeriesLengthException() {
		System.err.println("Cannot compare these data series; they are of inequal length.");
	}

	/**
	 * @param arg0
	 */
	public UnequalDataSeriesLengthException(String arg0) {
		super(arg0);
		System.err.println("Cannot compare these data series; they are of inequal length.");
	}

	/**
	 * @param arg0
	 */
	public UnequalDataSeriesLengthException(Throwable arg0) {
		super(arg0);
		System.err.println("Cannot compare these data series; they are of inequal length.");
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnequalDataSeriesLengthException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		System.err.println("Cannot compare these data series; they are of inequal length.");
	}

}
