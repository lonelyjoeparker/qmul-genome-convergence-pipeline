package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.analyses.*;

public class CongruenceRunner1000BinCDFMultiHn {

	/**
	 * @param args
	 * @since r96: 30/3/2012
	 * A multiple congruence analysis to compare the SSLS values of an AA dataset on FOUR input trees and a de novo RAxML tree.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		assert(args.length==11);
		File dataSet = new File(args[0]);
		File treeFileOne = new File(args[1]);
		File treeFileTwo = new File(args[2]);
		File treeFileThree = new File(args[3]);
		File treeFileFour = new File(args[4]);
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
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		MultiHnCongruenceAnalysis1000BinCDFs analysis = new MultiHnCongruenceAnalysis1000BinCDFs(dataSet, treeFileOne, treeFileTwo, treeFileThree, treeFileFour, workDir, binaries, runID, taxaList, replicatesForNull, thisFilter, doFactor);
		analysis.go();
	}

}
