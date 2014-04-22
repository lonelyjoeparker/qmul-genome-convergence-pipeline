package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;

import junit.framework.TestCase;

public class DisplayAlignmentTest extends TestCase {

	File alignmentFile = new File("/Users/gsjones/Downloads/NM005550/rhp.phy");
	AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
	
	public DisplayAlignmentTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		asr.loadSequences(alignmentFile, true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDisplayAlignmentLoader(){
		DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
		da.getSequences();
	}
}
