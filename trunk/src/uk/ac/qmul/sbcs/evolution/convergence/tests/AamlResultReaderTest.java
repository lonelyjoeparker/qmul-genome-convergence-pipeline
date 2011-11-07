package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlResultReader;
import junit.framework.TestCase;

public class AamlResultReaderTest extends TestCase {
	private AamlResultReader arr;
	private boolean arrInit = false;
	
	public AamlResultReaderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		arr = new AamlResultReader(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/mlaaml"));
		if(arr !=  null){arrInit = true;}
//		brr.printParams();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAamlResultReaderFile() {
		if(this.arrInit){
			System.out.println("file "+arr.getFile().getAbsolutePath());
		}
	}

	public void testGetOptimisedTree() {
		if(this.arrInit){
			System.out.println("optimisedTree "+arr.getOptimisedTree());
		}
	}

	public void testGetAlpha() {
		if(this.arrInit){
			System.out.println("alpha "+arr.getAlpha());
		}
	}
	
	public void testGetTreeLength() {
		if(this.arrInit){
			System.out.println("tree length "+arr.getTreeLength());
		}
	}

}
