package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestStripGapsPreserceCodons extends TestCase {
	AlignedSequenceRepresentation singleStopDNA;		//a 21-nt DNA alignment with n%3 = 0 codons and one stop					//should nSites=21 at halt
	AlignedSequenceRepresentation singleGapDNA;			//a 21-nt DNA alignment with n%3 = 0 codons and one gap						//should nSites=21 at halt
	AlignedSequenceRepresentation singleCodonGapDNA;	//a 21-nt DNA alignment with n%3 = 0 codons and one codon gap				//should nSites=21 at halt
	AlignedSequenceRepresentation multipleGapDNA;		//a 21-nt DNA alignment with n%3 = 0 codons and an entire columnar codon gap	//should nSites=18 at halt

	public AlignedSequenceRepresentationTestStripGapsPreserceCodons(String name) {
		super(name);
		singleStopDNA = new AlignedSequenceRepresentation();
		singleGapDNA = new AlignedSequenceRepresentation();
		singleCodonGapDNA = new AlignedSequenceRepresentation();
		multipleGapDNA = new AlignedSequenceRepresentation();
		try {
			singleStopDNA.loadSequences(new File("junit-test-inputs/testStripGapsPreserveCodons/singleStopDNA.phy"),true);
			singleGapDNA.loadSequences(new File("junit-test-inputs/testStripGapsPreserveCodons/singleGapDNA.phy"),true);
			singleCodonGapDNA.loadSequences(new File("junit-test-inputs/testStripGapsPreserveCodons/singleCodonGapDNA.phy"),true);
			multipleGapDNA.loadSequences(new File("junit-test-inputs/testStripGapsPreserveCodons/multipleGapDNA.phy"),true);
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

	public final void testStripGapsPreserveCodonsSingleStop() {
		try {
			singleStopDNA.stripGapsPreserveCodons();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(singleStopDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites");
		}
	}

	public final void testStripGapsPreserveCodonsSingleGap() {
		try {
			singleGapDNA.stripGapsPreserveCodons();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(singleGapDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites");
		}
	}
	
	public final void testStripGapsPreserveCodonsCodonGap() {
		try {
			singleCodonGapDNA.stripGapsPreserveCodons();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(singleCodonGapDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites");
		}
	}
	
	public final void testStripGapsPreserveCodonsMultipleGap() {
		try {
			multipleGapDNA.stripGapsPreserveCodons();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(multipleGapDNA.getNumberOfSites() != 18){
			fail("Wrong numberOfSites ("+multipleGapDNA.getNumberOfSites()+")");
		}
	}
}
