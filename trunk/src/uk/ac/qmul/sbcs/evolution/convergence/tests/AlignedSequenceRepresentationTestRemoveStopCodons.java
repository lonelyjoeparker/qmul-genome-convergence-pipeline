package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestRemoveStopCodons extends TestCase {
	AlignedSequenceRepresentation singleStopDNA;		//a 21-nt DNA alignment with n%3 = 0 codons and one stop					//should nSites=21 at halt
	AlignedSequenceRepresentation singleGapDNA;			//a 21-nt DNA alignment with n%3 = 0 codons and one gap						//should nSites=21 at halt
	AlignedSequenceRepresentation singleCodonGapDNA;	//a 21-nt DNA alignment with n%3 = 0 codons and one codon gap				//should nSites=21 at halt
	AlignedSequenceRepresentation multipleGapDNA;		//a 21-nt DNA alignment with n%3 = 0 codons and an entire columnar codon gap	//should nSites=18 at halt
	AlignedSequenceRepresentation multipleStopDNA;		//a 21-nt DNA alignment with n%3 = 0 codons and an entire columnar codon stop	//should nSites=18 at halt
	AlignedSequenceRepresentation vicugna;				//a vicugna alignment with multiple 'N' and trailing stop, breaks current iteration					//should nSites=21 at halt
	AlignedSequenceRepresentation taxon166881;			//alignment from 166881 that breaks for unknown reason

	public AlignedSequenceRepresentationTestRemoveStopCodons(String name) {
		super(name);
		singleStopDNA = new AlignedSequenceRepresentation();
		singleGapDNA = new AlignedSequenceRepresentation();
		singleCodonGapDNA = new AlignedSequenceRepresentation();
		multipleGapDNA = new AlignedSequenceRepresentation();
		multipleStopDNA = new AlignedSequenceRepresentation();
		vicugna = new AlignedSequenceRepresentation();
		taxon166881 = new AlignedSequenceRepresentation();
		try {
			singleStopDNA.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/singleStopDNA.phy"),true);
			singleGapDNA.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/singleGapDNA.phy"),true);
			singleCodonGapDNA.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/singleCodonGapDNA.phy"),true);
			multipleGapDNA.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/multipleGapDNA.phy"),true);
			multipleStopDNA.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/multipleStopDNA.phy"),true);
			vicugna.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/trouble.phy"),true);
			taxon166881.loadSequences(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/debug_data/testStripGapsPreserveCodons/166881.phy"),true);
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

	public final void testRemoveStopCodonsUseCaseTaxon166881() {
		taxon166881.printCompleteSequences();
		taxon166881.removeStopCodons();
		taxon166881.printCompleteSequences();
		if(taxon166881.getNumberOfSites() != 36){
			fail("Wrong numberOfSites ("+taxon166881.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsUseCaseVicugna() {
		vicugna.printCompleteSequences();
		vicugna.removeStopCodons();
		vicugna.printCompleteSequences();
		if(vicugna.getNumberOfSites() != 36){
			fail("Wrong numberOfSites ("+vicugna.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsSingleStop() {
		singleStopDNA.removeStopCodons();
		if(singleStopDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites ("+singleStopDNA.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsSingleGap() {
		singleGapDNA.removeStopCodons();
		if(singleGapDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites ("+singleGapDNA.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsCodonStop() {
		singleCodonGapDNA.removeStopCodons();
		if(singleCodonGapDNA.getNumberOfSites() != 21){
			fail("Wrong numberOfSites ("+singleCodonGapDNA.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsMultipleGap() {
		multipleGapDNA.removeStopCodons();
		if(multipleGapDNA.getNumberOfSites() != 18){
			fail("Wrong numberOfSites ("+multipleGapDNA.getNumberOfSites()+")");
		}
	}

	public final void testRemoveStopCodonsMultipleStop() {
		multipleStopDNA.removeStopCodons();
		if(multipleStopDNA.getNumberOfSites() != 18){
			fail("Wrong numberOfSites ("+multipleStopDNA.getNumberOfSites()+")");
		}
	}

}
