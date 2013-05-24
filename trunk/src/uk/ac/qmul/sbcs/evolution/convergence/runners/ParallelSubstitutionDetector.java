package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.ParsimonyReconstruction;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.serFilter;

public class ParallelSubstitutionDetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParallelSubstitutionDetector p = new ParallelSubstitutionDetector();
		p.go(args);
	}

	private void go(String[] args){
		System.out.println("input\tInputFileName\tmodel\tParallel H1\tParallel H1c\tParallel H1o\tParallel H2\tParallel H2c\tParallel H2o\t(Ambiguous at root)");
		File dir = new File(args[0]);
		FilenameFilter serFileFilter = new serFilter();
		StringBuffer bufMain = new StringBuffer();				// no treefile buffer for this one.
//		bufMain.append("locus\tensembl_URL\tshortcode\tdescription\thomog\tmodel\tmissingData\tlnl (species tree)\tlength (species tree)\tmodel alpha\tnumberOfTaxa\tnumberOfSites\tpreferredTopology");
//		bufMain.append("\n");

		Pattern specificPattern = Pattern.compile(args[1]);

		if(dir.isDirectory()){
			String[] serFilesList = dir.list(serFileFilter);
			for(String someFile:serFilesList){
				Matcher simMatch = specificPattern.matcher(someFile);
				if(simMatch.find()){
					InputStream serfile;
					ObjectInputStream inOne;
					SitewiseSpecificLikelihoodSupportAaml candidate;
					try {
						File input = new File(dir.getAbsolutePath()+"/"+someFile);
						serfile = new FileInputStream(input);
						inOne = new ObjectInputStream(serfile);
						candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
						TreeNode species = new TreeNode(candidate.getFittedTrees()[0].replaceAll("\\s", ""),1);
						HashMap<String, HashSet<String>[]> states = candidate.getDataset().getAminoAcidsAsFitchStates();
						HashSet<String>[] baseStates = species.getFitchStates(states).clone();
						int ambiguousAtRoot = 0;
						for(HashSet<String> statesSet:baseStates){
							if(statesSet.size()>1){
								ambiguousAtRoot++;
							}
						}
						species.resolveFitchStatesTopnode();
						species.resolveFitchStates(species.states);
						ParsimonyReconstruction pr = new ParsimonyReconstruction(states, species);
						//pr.printAncestralComparison();
						String[] controls = {"EIDOLON","PTEROPUS"};
						String[] echolocators = {"MEGADERMA","RHINOLOPHUS","MYOTIS","PTERONOTUS"};
						String[] echolocatorsH2 = {"TURSIOPS","MEGADERMA","RHINOLOPHUS","MYOTIS","PTERONOTUS"};
						int pll_H1  = pr.findParallelSubtitutionsFromAncestral(echolocators, false);
						int pll_H1c = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguities(echolocators,baseStates, false);
						int pll_H1o = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguitiesControllingOutgroups(echolocators,baseStates, false, controls);
						int pll_H2  = pr.findParallelSubtitutionsFromAncestral(echolocatorsH2, false);
						int pll_H2c = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguities(echolocatorsH2,baseStates,false);
						int pll_H2o = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguitiesControllingOutgroups(echolocatorsH2,baseStates, false, controls);
						System.out.println(input.getName()+"\t"+candidate.getInputFileName()+"\t"+candidate.getModel()+"\t"+pll_H1+"\t"+pll_H1c+"\t"+pll_H1o+"\t"+pll_H2+"\t"+pll_H2c+"\t"+pll_H2o+"\t"+ambiguousAtRoot);
						species.getEndPos();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
