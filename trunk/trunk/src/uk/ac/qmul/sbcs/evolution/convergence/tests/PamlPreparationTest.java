package uk.ac.qmul.sbcs.evolution.convergence.tests;

import org.virion.PamlPreparation;

import junit.framework.TestCase;

public class PamlPreparationTest extends TestCase {

	public PamlPreparationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testMainHIVFile() {
		PamlPreparation quick = new PamlPreparation("/Users/gsjones/Documents/all_work/contract/IMM/comparison_dat/codeml_rundir/wu-p.N90.lanlalign.phy");
		quick.go();
	}

	public final void testMainRyunFile() {
		PamlPreparation quick = new PamlPreparation("/Users/gsjones/Documents/all_work/collaborations/seb-feb-2014-fastCodeML/combined/ryun/ryun.trimmed");
		quick.go();
	}
}
