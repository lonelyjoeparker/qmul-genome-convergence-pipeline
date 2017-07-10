package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestHasWholeNumberOfCodons extends TestCase {
	AlignedSequenceRepresentation bigWhole;			//a large alignment with n%3 = 0 codon
	AlignedSequenceRepresentation bigOneOff;		//a large alignment with n%3 = 1 codon remainders
	AlignedSequenceRepresentation bigTwoOff;		//a large alignment with n%3 = 2 codon remainders
	AlignedSequenceRepresentation smallWhole;		//a small alignment with n=3 bases
	AlignedSequenceRepresentation one;				//a small alignment with n=2 bases
	AlignedSequenceRepresentation two;				//a small alignment with n=1 base
	AlignedSequenceRepresentation none;				//a small alignment with n=0 bases
	AlignedSequenceRepresentation emptyFile;		//an empty file
	
	public AlignedSequenceRepresentationTestHasWholeNumberOfCodons(String name) {
		super(name);
		bigWhole = new AlignedSequenceRepresentation();
		bigOneOff = new AlignedSequenceRepresentation();
		bigTwoOff = new AlignedSequenceRepresentation();
		smallWhole = new AlignedSequenceRepresentation();
		one = new AlignedSequenceRepresentation();
		two = new AlignedSequenceRepresentation();
		none = new AlignedSequenceRepresentation();
		emptyFile = new AlignedSequenceRepresentation();
		
		try {
			bigWhole.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/largeWhole.phy"),true);
			bigOneOff.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/largeOneOff.phy"),true);
			bigTwoOff.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/largeTwoOff.phy"),true);
			smallWhole.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/smallWhole.phy"),true);
			one.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/one.phy"),true);
			two.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/two.phy"),true);
		//	none.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/none.phy"),true);
		//	emptyFile.loadSequences(new File("junit-test-inputs/testHasWholeNumberOfCodons/emptyFile.phy"),true);
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

	public void testHasWholeNumberOfCodonsBigWhole() {
		try {
			if(!bigWhole.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsBigOne() {
		try {
			if(bigOneOff.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsBigTwo() {
		try {
			if(bigTwoOff.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsSmallWhole() {
		try {
			if(!smallWhole.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsTwo() {
		try {
			if(two.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsOne() {
		try {
			if(one.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsNone() {
		try {
			if(none.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testHasWholeNumberOfCodonsEmpty() {
		try {
			if(emptyFile.hasWholeNumberOfCodons()){
				fail("incorrect codon remainder detected."); // TODO
			}
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
