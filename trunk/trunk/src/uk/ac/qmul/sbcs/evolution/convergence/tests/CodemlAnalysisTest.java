package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.CodemlParameters;
import java.io.*;
import java.util.TreeMap;

public class CodemlAnalysisTest extends TestCase {

	public CodemlAnalysisTest(String name) {
		super(name);
	}

	public void testCodemlAnalysis() {
		AlignedSequenceRepresentation[] PSR = null;
		File[] treefiles = null;
		TreeMap<CodemlParameters, String> parameters = new TreeMap<CodemlParameters, String>();
		parameters.put(CodemlParameters.SEQFILE, "seqfile = /pamlTest/stewart.aa");
		parameters.put(CodemlParameters.TREEFILE, "treefile = /pamlTest/stewart.trees");
		parameters.put(CodemlParameters.AARATEFILE, "aaRatefile = /Applications/Phylogenetics/PAML/paml44/dat/wag.dat");
		parameters.put(CodemlParameters.OUTFILE, "outfile = /pamlTest/lnf.out");
		CodemlAnalysis a = new CodemlAnalysis(PSR, treefiles, parameters,"another.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/bin/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"codeml"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.RunAnalysis();
	}

}
