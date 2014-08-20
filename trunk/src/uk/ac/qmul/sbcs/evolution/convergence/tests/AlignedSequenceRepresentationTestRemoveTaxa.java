package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;

import junit.framework.TestCase;

public class AlignedSequenceRepresentationTestRemoveTaxa extends TestCase {

	AlignedSequenceRepresentation data;
	
	public AlignedSequenceRepresentationTestRemoveTaxa(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		data = new AlignedSequenceRepresentation();
		data.loadSequences(new File("/pamlTest/ENSG00000070214/short.physhort.phyconv1375280367957_pamlNT.phy"), false);
		data.calculateAlignmentStats(false);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testRemoveTaxaShouldPass() {
		int numTaxa = data.getNumberOfTaxa();
		String[] toRemove = {"MUS"};
		data.removeTaxa(toRemove);
		if(data.getNumberOfTaxa() == numTaxa){
			fail("wrong taxon count"); // TODO
		}
	}

	public final void testRemoveTaxaTwiceShouldFail() {
		int numTaxa = data.getNumberOfTaxa();
		String[] toRemove = {"MUS"};
		data.removeTaxa(toRemove);
		if(data.getNumberOfTaxa() == numTaxa){
			fail("wrong taxon count"); // TODO
		}
		data.removeTaxa(toRemove);
		if(data.getNumberOfTaxa() != (numTaxa-1)){
			fail("wrong taxon count on repeat attempt to remove taxon"); // TODO
		}
	}

	public final void testRemoveTaxaShouldFail() {
		int numTaxa = data.getNumberOfTaxa();
		String[] toRemove = {"MOUSE"};
		data.removeTaxa(toRemove);
		if(data.getNumberOfTaxa() != numTaxa){
			fail("wrong taxon count"); // TODO
		}
	}

	public final void testClone() {
		data.printShortSequences(4);
		AlignedSequenceRepresentation clone_1 = data.cloneDeep();
		String[] toRemove = {"BOS"};
		data.removeTaxa(toRemove);
		AlignedSequenceRepresentation clone_2 = data.cloneDeep();
		String[] toRemoveOther = {"CANIS"};
		clone_1.removeTaxa(toRemoveOther);
		String[] toRemoveAnother = {"FELIS"};
		clone_2.removeTaxa(toRemoveAnother);
		data.removeTaxa(toRemoveOther);
		data.removeTaxa(toRemoveAnother);
		data.printShortSequences(4);
		fail("Not yet implemented"); // TODO
	}

}
