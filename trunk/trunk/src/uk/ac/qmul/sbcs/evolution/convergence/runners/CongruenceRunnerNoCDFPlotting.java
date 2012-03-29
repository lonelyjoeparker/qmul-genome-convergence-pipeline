package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.analyses.*;

public class CongruenceRunnerNoCDFPlotting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		assert(args.length==9);
		File dataSet = new File(args[0]);
		File treeFileOne = new File(args[1]);
		File treeFileTwo = new File(args[2]);
		File workDir = new File(args[3]);
		String runID = args[4];
		File binaries = new File(args[5]);
		Integer replicatesForNull = Integer.parseInt(args[6]);
		int thisFilter = Integer.parseInt(args[7]);
		boolean doFactor = false;
		switch(Integer.parseInt(args[8])){
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
		CongruenceAnalysisNoCDFPlotting analysis = new CongruenceAnalysisNoCDFPlotting(dataSet, treeFileOne, treeFileTwo, workDir, binaries, runID, taxaList, replicatesForNull, thisFilter, doFactor);
			analysis.go();
	}

}
