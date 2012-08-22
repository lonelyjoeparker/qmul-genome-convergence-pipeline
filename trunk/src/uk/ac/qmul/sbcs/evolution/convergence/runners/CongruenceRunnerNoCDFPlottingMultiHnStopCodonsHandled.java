package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.analyses.*;

public class CongruenceRunnerNoCDFPlottingMultiHnStopCodonsHandled {

	/**
	 * @param args
	 * @since r120: 22/8/2012
	 * A multiple congruence analysis to compare the SSLS values of an AA dataset on FOUR input trees and a de novo RAxML tree.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		assert(args.length==11);
		File dataSet = new File(args[0]);
		File treeFileH0 = new File(args[1]);
		File treeFileH1 = new File(args[2]);
		File treeFileH2 = new File(args[3]);
		File treeFileH3 = new File(args[4]);
		File workDir = new File(args[5]);
		String runID = args[6];
		File binaries = new File(args[7]);
		Integer replicatesForNull = Integer.parseInt(args[8]);
		int thisFilter = Integer.parseInt(args[9]);
		boolean doFactor = false;
		switch(Integer.parseInt(args[10])){
			case (1): doFactor = true; break;
			case (0): doFactor = false; break;
		}
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
		MultiHnCongruenceAnalysisNoCDFSitewiseLikelihoodOutputTemp analysis = new MultiHnCongruenceAnalysisNoCDFSitewiseLikelihoodOutputTemp(dataSet, treeFileH0, treeFileH1, treeFileH2, treeFileH3, workDir, binaries, runID, taxaList, replicatesForNull, thisFilter, doFactor);
		analysis.go();
	}

}

