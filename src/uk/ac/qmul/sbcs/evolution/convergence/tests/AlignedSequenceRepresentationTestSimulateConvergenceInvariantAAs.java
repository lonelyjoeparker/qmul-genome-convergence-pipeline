package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaAbsentFromAlignmentException;
import uk.ac.qmul.sbcs.evolution.convergence.VariantSitesUnavailableException;
import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestSimulateConvergenceInvariantAAs extends TestCase {

	AlignedSequenceRepresentation data = new AlignedSequenceRepresentation();;
	
	protected void setUp() throws Exception {
		super.setUp();
		data.loadSequences(new File("/pamlTest/pamlDebugs/Joe_test2/prestinInputDec2011.faprestin_1000BinCDFs_pamlAA.phy"), false);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testSimulateConvergenceInInvariantSites() {
		data.printShortSequences(20);
		System.out.println("read "+data.getNumberOfSites()+" sites and "+data.getNumberOfTaxa()+" taxa.");
		
		
		
		String first = data.getTaxon(0);
		String[] last = {data.getTaxon(data.getNumberOfTaxa()-1),data.getTaxon(1)};
		System.out.println("converging "+last[0]+" sequence to "+first);
		try {
			data.simulateConvergenceInInvariantSites(first, last, 100);
		} catch (TaxaAbsentFromAlignmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VariantSitesUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.printShortSequences(20);

	}

}
