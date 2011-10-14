package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.BasemlParameters;
import junit.framework.TestCase;

public class BasemlAnalysisTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAamlAnalysis() {
		AlignedSequenceRepresentation[] PSR = null;
		File[] treefiles = null;
		TreeMap<BasemlParameters, String> parameters = new TreeMap<BasemlParameters, String>();
		parameters.put(BasemlParameters.SEQFILE, "seqfile = /pamlTest/brown.nuc");
		parameters.put(BasemlParameters.TREEFILE, "treefile = /pamlTest/brown.trees");
		parameters.put(BasemlParameters.OUTFILE, "outfile = /pamlTest/lnf.out");
		BasemlAnalysis a = new BasemlAnalysis(PSR, treefiles, parameters,"another.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/paml44/bin/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"baseml"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.RunAnalysis();
	}

}
