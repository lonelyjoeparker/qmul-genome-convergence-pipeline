package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.FilterOutOfAllowableRangeException;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestGetCharSequence extends TestCase {

	private AlignedSequenceRepresentation sourceDataASR;
	private File dataset = new File("/pamlTest/trialDataFromGeorgia/short.fa");
	private SequenceCodingType inputSequenceCodingType;
	
	public AlignedSequenceRepresentationTestGetCharSequence(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.sourceDataASR = new AlignedSequenceRepresentation();
		try {
			sourceDataASR.loadSequences(dataset,false);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.removeUnambiguousGaps();
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		try {
			sourceDataASR.translate(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetTaxaByIndex(){
		String taxon;
		for(int i=0;i<sourceDataASR.getNumberOfTaxa();i++){
			taxon = sourceDataASR.getTaxon(i);
			if(taxon == null){
				fail();
			}
		}
	}
	
	public void testGetSequenceChars(){
		char[] sequence = new char[sourceDataASR.getNumberOfSites()];
		String taxon;
		for(int i=0;i<sourceDataASR.getNumberOfTaxa();i++){
			try {
				sequence = sourceDataASR.getSequenceChars(i);
				if(sequence.length<sourceDataASR.getNumberOfSites()){
					fail();
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			taxon = sourceDataASR.getTaxon(i);
			System.out.print(taxon+"\t");
			for(int j=0;j<sequence.length;j++){
				System.out.print(sequence[j]+"|");
			}
			System.out.println();
		}
	}
}
