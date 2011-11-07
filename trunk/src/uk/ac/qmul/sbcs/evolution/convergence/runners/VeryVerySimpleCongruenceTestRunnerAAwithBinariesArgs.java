package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.analyses.*;

public class VeryVerySimpleCongruenceTestRunnerAAwithBinariesArgs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		assert(args.length==6);
		File dataSet = new File(args[0]);
		File treeFileOne = new File(args[1]);
		File treeFileTwo = new File(args[2]);
		File workDir = new File(args[3]);
		String runID = args[4];
		File binaries = new File(args[5]);
		TreeSet<String> taxaList = new TreeSet<String>();
		taxaList.add("Tursiops");
		taxaList.add("Canis");
		taxaList.add("Felis");
		taxaList.add("Loxodonta");
		taxaList.add("Erinaceus");
		taxaList.add("Mus");
		taxaList.add("Monodelphi");
		taxaList.add("Pan");
		taxaList.add("Homo");
		taxaList.add("Pteronotus");
		taxaList.add("Rhinolophu");
		taxaList.add("Pteropus");
		taxaList.add("Eidolon");
		taxaList.add("Dasypus");
		taxaList.add("Equus");
		taxaList.add("Megaderma");
		taxaList.add("Myotis");
		taxaList.add("Bos");
		VeryVerySimpleCongruenceAnalysisAA analysis = new VeryVerySimpleCongruenceAnalysisAA(dataSet, treeFileOne, treeFileTwo, workDir, binaries, runID, taxaList);
		analysis.go();
	}

}
