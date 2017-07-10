package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestEngapStopCodonsInDNA extends TestCase {
	AlignedSequenceRepresentation singleStopRNA;			//a large RNA alignment with n%3 = 0 codons and one stop
	AlignedSequenceRepresentation singleStop;			//a large DNA alignment with n%3 = 0 codons and one stop

	public AlignedSequenceRepresentationTestEngapStopCodonsInDNA(String name) {
		super(name);
		singleStop = new AlignedSequenceRepresentation();
		singleStopRNA = new AlignedSequenceRepresentation();
		try {
			singleStop.loadSequences(new File("junit-test-inputs/testEngapStopCodonsInDNA/singleStop.phy"),true);
			singleStopRNA.loadSequences(new File("junit-test-inputs/testEngapStopCodonsInDNA/singleStopRNA.phy"),true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testEngapSingleStopCodonInDNA() {
		try {
			singleStop.engapStopCodonsInDNA();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char[] firstSeq=singleStop.getSequenceChars(0);
		if(firstSeq[1] != '-'){
			assert(true);
			fail("not engapped"); // TODO
		}
	}

	public final void testEngapSingleStopCodonInRNA() {
		try {
			singleStopRNA.engapStopCodonsInDNA();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char[] firstSeq=singleStopRNA.getSequenceChars(0);
		if(firstSeq[1] != '-'){
			assert(true);
			fail("not engapped"); // TODO
		}
	}

}
