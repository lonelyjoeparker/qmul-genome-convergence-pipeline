package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.util.EnsemblCodeGuesser;
import junit.framework.TestCase;

public class EnsemblCodeGuesserTest extends TestCase {

	public EnsemblCodeGuesserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGuessFile() {
		File f = new File("foo/bar/files/ENSG00000167671_ng.fas");
		String returnString = EnsemblCodeGuesser.guess(f);
		if(returnString != "ENSG00000167671"){
			fail("Wrong ensembl code");
		}
	}

	public final void testGuessString() {
		String testString = "ENSG00000167671_ng.fas";
		String returnString = EnsemblCodeGuesser.guess(testString);
		if(returnString != "ENSG00000167671"){
			fail("Wrong ensembl code");
		}
	}

}
