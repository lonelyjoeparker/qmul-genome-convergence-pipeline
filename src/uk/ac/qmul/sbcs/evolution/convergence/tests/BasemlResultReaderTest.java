/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader;
import junit.framework.TestCase;

/**
 * @author Joe Parker
 *
 */
public class BasemlResultReaderTest extends TestCase {
	private BasemlResultReader brr;
	private boolean brrInit = false;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		brr = new BasemlResultReader(new File("/pamlTest/trialDataFromGeorgia/evolver.output.phybaseml.out"));
		if(brr !=  null){brrInit = true;}
		System.out.println("SETUP\nTEST: kappa rates: [["+brr.getKappaRates()+"]]");
		System.out.println("SETUP\nTEST: kappa freqs: [["+brr.getKappaFreqs()+"]]");
//		assert(brr.getKappaRates().equals("  0.00094  0.02832  0.19564  0.81091  3.96419"));
		brr.printParams();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#BasemlResultReader(java.io.File)}.
	 */
	public void testBasemlResultReaderFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getFile()}.
	 */
	public void testGetFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getReader()}.
	 */
	public void testGetReader() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getRawData()}.
	 */
	public void testGetRawData() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getOptimisedTree()}.
	 */
	public void testGetOptimisedTree() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getRates()}.
	 */
	public void testGetRates() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getBaseFreqs()}.
	 */
	public void testGetBaseFreqs() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getRateMatrix()}.
	 */
	public void testGetRateMatrix() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getAlpha()}.
	 */
	public void testGetAlpha() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getKappaRates()}.
	 */
	public void testGetKappaRates() {
		assert(this.brrInit);
		if(this.brrInit){
			assert(brr != null);
			System.out.println("TEST: kappa rates: "+brr.getKappaRates());
	//		assert(brr.getKappaRates().equals("  0.00094  0.02832  0.19564  0.81091  3.96419"));
		}else{
			fail("Not initialised"); // TODO
		}
		System.out.println("TEST: kappa rates: "+brr.getKappaRates());
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader#getKappaFreqs()}.
	 */
	public void testGetKappaFreqs() {
		fail("Not yet implemented"); // TODO
	}

}
