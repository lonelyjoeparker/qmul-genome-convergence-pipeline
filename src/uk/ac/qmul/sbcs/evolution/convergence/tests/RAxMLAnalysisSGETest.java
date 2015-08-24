package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.RAxMLAnalysisSGE;
import junit.framework.TestCase;

public class RAxMLAnalysisSGETest extends TestCase {

	File pamlDataFileAA = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/input.phy.nc.fasFF100ENSG00000070214_ng.fas_pamlAA.phy");
	File pamlDataFileNT = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/input.phy.nc.fasFF100ENSG00000070214_ng.fas_pamlNT.phy");
	File workDir		= new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/");
	File constraintTreeFile;
	TreeSet<String> taxaList;
	
	public RAxMLAnalysisSGETest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		taxaList = new TreeSet<String>();
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
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRAxMLwithSubtreeConstraint(){
		constraintTreeFile = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/constraint_RAxML.tre");
		String ID = new String(System.currentTimeMillis()+"RAxML");
		RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, this.constraintTreeFile, ID, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
		ra.setTreeConstraint(true);
		ra.setMultifuricatingConstraint(true);
		ra.setNoStartTree(true);
		ra.setBinaryDir(new File("/Applications/Phylogenetics/RAxML/RAxML-7.2.8-ALPHA/raxmlHPC"));
	//	ra.setWorkingDir(this.workDir);
		ra.RunAnalysis();
		NewickTreeRepresentation resolvedTree = new NewickTreeRepresentation(ra.getOutputFile(),taxaList);
	}
}
