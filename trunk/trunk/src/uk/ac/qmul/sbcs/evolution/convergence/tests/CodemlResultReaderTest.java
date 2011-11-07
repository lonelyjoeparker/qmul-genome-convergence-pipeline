package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.CodemlResultReader;
import junit.framework.TestCase;

public class CodemlResultReaderTest extends TestCase {
	private CodemlResultReader crr;
	private boolean crrInit = false;
	
	public CodemlResultReaderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		crr = new CodemlResultReader(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/mlc"));
		if(crr !=  null){crrInit = true;}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCodemlResultReader() {
		fail("Not yet implemented"); // TODO
	}

	public void testCodemlResultReaderFile() {
		fail("Not yet implemented"); // TODO
	}

	public void testPrintParams() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetObsAvgFreqs() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetBaseRates() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetTiTv() {
		fail("Not yet implemented"); // TODO
	}

	public void testIsInitialised() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetFile() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetReader() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetRawData() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetRates() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetBaseFreqs() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetRateMatrix() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetKappaRates() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetKappaFreqs() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetOptimisedTree() {
		if(this.crrInit){
			System.out.println("optimisedTree "+crr.getOptimisedTree());
		}
	}

	public void testGetAlpha() {
		if(this.crrInit){
			System.out.println("alpha "+crr.getAlpha());
		}
	}
	
	public void testGetTreeLength() {
		if(this.crrInit){
			System.out.println("tree length "+crr.getTreeLength());
		}
	}

	public void testGetOmegaVal() {
		if(this.crrInit){
			System.out.println("omega "+crr.getOmegaVal());
		}
	}

	public void testGetKappaVal() {
		if(this.crrInit){
			System.out.println("kappa "+crr.getKappaVal());
		}
	}

}
