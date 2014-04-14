/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class SerfileFilter implements FilenameFilter {

	/**
	 * 
	 */
	public SerfileFilter() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File arg0, String arg1) {
		// TODO Auto-generated method stub
		return arg1.endsWith(".ser");
	}

}
