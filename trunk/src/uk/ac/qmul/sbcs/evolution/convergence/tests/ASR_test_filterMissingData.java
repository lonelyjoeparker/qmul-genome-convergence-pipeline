package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import javax.swing.JFileChooser;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.FilterOutOfAllowableRangeException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class ASR_test_filterMissingData extends TestCase {

	private File inputFile;
	private AlignedSequenceRepresentation ASR = null;
	private AlignedSequenceRepresentation copy = null;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		if(ASR==null){
			try {
				inputFile = new File("/Users/joeparker/mc2.paml");
//				System.out.println("trying to read "+inputFile.getAbsolutePath()+" file\n");
				ASR = new AlignedSequenceRepresentation();
				try{
					ASR.loadSequences(inputFile,true);
				}catch(TaxaLimitException ex){
					ex.printStackTrace();
				}
//				System.out.println("read "+ASR.getNumberOfSites()+" sites and "+ASR.getNumberOfTaxa()+" taxa.");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public final void testRemoveUnambiguousGaps() {
		if(ASR != null){
			ASR.removeUnambiguousGaps();
			ASR.printNumberOfInvariantSites();
			assert(true);
		}else{
			fail("ASR not implemented");
		}
	}

	public final void testCopyASR(){
		copy = ASR.clone();
		copy.hashCode();
		System.out.println("\nFIRST ALIGNMENT");
		ASR.printCompleteSequences();
		System.out.println("\nCLONE ALIGNMENT");
		copy.printCompleteSequences();
	}

//	public final void testDetermineInvariantSites() {
//		fail("not implemented");
//		copy.getMissingDataFiltered(0, false);
//	}
	
	public final void testFilterForMissingDataThres0(){
		ASR.hashCode();
		try {
			ASR.filterForMissingData(0, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 0 factor:false");
		}	
		ASR.hashCode();
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThresh1(){
		ASR.hashCode();
		try {
			ASR.filterForMissingData(1, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 1 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThresh2(){
		try {
			ASR.filterForMissingData(2, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 2 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThresh4(){
		try {
			ASR.filterForMissingData(4, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 2 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThreshMinus1(){
		try {
			ASR.filterForMissingData(-1, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter -1 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThreshEnoughTaxa(){
		try {
			ASR.filterForMissingData(20, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 18 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataThreshTooManyTaxa(){
		try {
			ASR.filterForMissingData(21, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 19 factor:false");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}
	
	public final void testFilterForMissingDataFactor1(){
		try {
			ASR.filterForMissingData(1, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 1 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor10(){
		try {
			ASR.filterForMissingData(10, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 10 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor20(){
		try {
			ASR.filterForMissingData(20, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 20 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor49(){
		try {
			ASR.filterForMissingData(49, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 49 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor50(){
		try {
			ASR.filterForMissingData(50, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 50 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor51(){
		try {
			ASR.filterForMissingData(51, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 51 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor90(){
		try {
			ASR.filterForMissingData(90, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 91 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactor101(){
		try {
			ASR.filterForMissingData(101, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter 101 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}

	public final void testFilterForMissingDataFactorMinus1(){
		try {
			ASR.filterForMissingData(-1, true);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("I was trying to filter out the missing data with filter -1 factor:true");
		}		
		ASR.printCompleteSequences();
		ASR.hashCode();
	}
}
