package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import junit.framework.TestCase;

public class AamlAnalysisSGETestMultiTreeSSLS extends TestCase {

	AamlAnalysisSGE a;
	NewickTreeRepresentation t;
	AlignedSequenceRepresentation[] PSR ={new AlignedSequenceRepresentation()};
	
	public AamlAnalysisSGETestMultiTreeSSLS(){
		TreeSet<String> taxaList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		t = new NewickTreeRepresentation(new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/debug.tre.pruned.tre"),taxaList);
		try {
			PSR[0].loadSequences(new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/input.phy.nc.fainput.phy.nc.faconv1362774584247_pamlAA.phy"),true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] treefiles = null;
		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
		parameters.put(AamlParameters.SEQFILE, "seqfile = input.phy.nc.fainput.phy.nc.faconv1362774584247_pamlAA.phy");
		parameters.put(AamlParameters.TREEFILE, "treefile = debug.tre.pruned.tre");
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = binaries/dat/dayhoff.dat");
		parameters.put(AamlParameters.OUTFILE, "outfile = aamlTreeOne.out");
		a = new AamlAnalysisSGE(PSR, treefiles, parameters,"another.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/bin/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"codeml"));
		a.setWorkingDir(new File("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.setHasRun(true);
		a.setNumberOfTreesets(t.getNumberOfTrees());
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
	
	public void testGetSitewiseSSLS(){
		float[] lnL = a.getSitewiseSSLS();
		assert(lnL[0]!=Float.NaN);
	}
	public void testGetFloatSitewiseLnL() {
		float[][] res = a.getFloatSitewiseLnL();
		assert(res[0][0]!=Float.NaN);
	}
	
	public void testGetFloatSitewiseMeanLnL() {
		a.getFloatSitewiseMeanLnL();
	}
	
	public void testGetFloatSitewiseLnLVariance() {
		a.getFloatSitewiseLnLVariance();
	}
	
	public void testMultipleTreeMeanAndVarianceProcessing(){
		float[][] res = a.getFloatSitewiseLnL();
		assert(res[0][0]!=Float.NaN);
		float means[] = a.getFloatSitewiseMeanLnL();
		float SE[] = a.getFloatSitewiseLnLVariance();
		assert(SE[0]+means[0]>Float.NEGATIVE_INFINITY);
	}
}
