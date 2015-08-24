package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AamlAnalysisTest extends TestCase {

	AamlAnalysis a;
	AlignedSequenceRepresentation[] PSR ={new AlignedSequenceRepresentation()};
	
	public AamlAnalysisTest(){
		try {
			PSR[0].loadSequences(new File("/pamlTest/stewart.aa.alternative.phy"),true);
			PSR[0].writePhylipFile("/pamlTest/input.phy");
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] treefiles = null;
		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
		parameters.put(AamlParameters.SEQFILE, "seqfile = /pamlTest/input.phy");
		parameters.put(AamlParameters.TREEFILE, "treefile = /pamlTest/stewart.single.tre");
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = /Applications/Phylogenetics/PAML/paml44/dat/wag.dat");
		parameters.put(AamlParameters.OUTFILE, "outfile = /pamlTest/lnf.out");
		a = new AamlAnalysis(PSR, treefiles, parameters,"another.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/bin/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"codeml"));
		a.setWorkingDir(new File("/pamlTest/"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.RunAnalysis();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetPatternSSLS() {
//		AlignedSequenceRepresentation[] PSR ={new AlignedSequenceRepresentation()};
//		try {
//			PSR[0].loadSequences(new File("/pamlTest/stewart.aa.alternative.phy"),true);
//			PSR[0].writePhylipFile("/pamlTest/input.phy");
//		} catch (TaxaLimitException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		File[] treefiles = null;
//		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
//		parameters.put(AamlParameters.SEQFILE, "seqfile = /pamlTest/input.phy");
//		parameters.put(AamlParameters.TREEFILE, "treefile = /pamlTest/stewart.alternative.trees");
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = /Applications/Phylogenetics/PAML/paml44/dat/wag.dat");
//		parameters.put(AamlParameters.OUTFILE, "outfile = /pamlTest/lnf.out");
//		a = new AamlAnalysis(PSR, treefiles, parameters,"another.ctl");
//		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/bin/").getAbsoluteFile());
//		a.setExecutionBinary(new File(a.getBinaryDir(),"codeml"));
//		System.out.println(a.getBinaryDir().getAbsolutePath());
//		System.out.println(a.getExecutionBinary().getAbsolutePath());
//		a.RunAnalysis();
		a.getPatternSSLS();
	}

	public void testPrintSitewiseSSLS() {
		a.printSitewiseSSLS(PSR[0]);
	}
	
	public void testGetFloatSitewiseLnL() {}
	
	public void testGetFloatSitewiseMeanLnL() {}
}
