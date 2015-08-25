package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.FilterOutOfAllowableRangeException;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTest extends TestCase {

	private AlignedSequenceRepresentation sourceDataASR;
	private File dataset = new File("trunk/examples/input.phy");
	private SequenceCodingType inputSequenceCodingType;
	
	public AlignedSequenceRepresentationTest(String name) {
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

	public void testRemoveStopCodons(){
		sourceDataASR.removeStopCodons();
		sourceDataASR.printCompleteSequences();
		try {
			sourceDataASR.filterForMissingData(0, false);
		} catch (FilterOutOfAllowableRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.printCompleteSequences();
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		assert(inputSequenceCodingType != null);
	}
}
