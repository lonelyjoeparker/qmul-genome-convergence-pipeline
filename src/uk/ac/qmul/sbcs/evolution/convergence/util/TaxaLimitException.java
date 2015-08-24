/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util;


/**
 * @author Joe Parker
 * @mailto: joe@kitson-consulting.co.uk
 */
public class TaxaLimitException extends Exception {
	private int taxa;
	
	/**
	 * 
	 */
	public TaxaLimitException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param taxa - The number of taxa that we have attempted to load.
	 */
	public TaxaLimitException(int taxa){
		super();
		this.taxa = taxa;
	}
	
	/**
	 * @param arg0
	 */
	public TaxaLimitException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public TaxaLimitException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TaxaLimitException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
