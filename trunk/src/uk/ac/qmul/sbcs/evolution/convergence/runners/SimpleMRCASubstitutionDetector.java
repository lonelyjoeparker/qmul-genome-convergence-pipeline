package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.ParsimonyReconstruction;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.StateComparison;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.SerfileFilter;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

class SimpleMRCASubstitutionDetector{

	/* Variables */

	File alignmentFile;						//  where we expect to find input alignment files
	File phylogenyFile;						//  where we expect to find input phylogeny files
	AlignedSequenceRepresentation alignment;			// alignment in ASR
	NewickTreeRepresentation newickPhylogeny;			// phylogeny in newick file
	TreeNode phylogeny;									// a TreeNode - recursive data structure representing a phylogenetic tree
	ParsimonyReconstruction pr;							// a parsimony reconstruction object, holds???
	HashSet<String> focalTaxaSet;						// hash set of the focal taxa - the taxa we'll look for parallel changes amongst
	String[] outgroupTaxaArray = {"EIDOLON","PTEROPUS"};
	String[] focalTaxaArray = {"MEGADERMA","RHINOLOPHUS","MYOTIS","PTERONOTUS"};
	boolean[] ancestorAmbiguities;	// which of the ancestral states are ambiguous
	int ambiguousAtRoot;			// how many TRUE in ancestorAmbiguities
	HashMap<String,HashSet<String>[]> focalTaxaAndTheirMRCASetStates;	// Tip and MRCA states
	HashSet<String>[] focalTaxaMRCAstates;				// MRCA states
	HashMap<String, HashSet<String>[]> states;			// input alignment as states
	HashSet<String>[] baseStates;						// states of root node before any ambiguity checking / resoluton

	/* Constructors */

	@Deprecated
	/**
	 * @Deprecated
	 * No-arg constructor, deprecated
	 */
	public SimpleMRCASubstitutionDetector(){
		// Deprecated
	}

	/**
	 * Preferred constructor
	 * @param args
	 */
	public SimpleMRCASubstitutionDetector(String[] args){
		/* Basic I/O */
		alignmentFile = new File(args[0]);
		alignment = new AlignedSequenceRepresentation();
		try {
			alignment.loadSequences(alignmentFile, false);
			alignment.translate(true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phylogenyFile = new File(args[1]);
		newickPhylogeny = new NewickTreeRepresentation(phylogenyFile);
		phylogeny = new TreeNode(newickPhylogeny.getTreeString().replaceAll("\\s", ""),1);
		
	}

	/* Methods */

	public static void main(String[] args){
		SimpleMRCASubstitutionDetector s = 	new SimpleMRCASubstitutionDetector(args);
		s.go();
		s.printResults();
	}

	/**
	 * main logic
	 */
	public void go(){
		/* Initialise hash sets used for fitch states. These used as ambiguities reconstructed initially as union of possibilities, e.g (A) u (B) => (A,B) */
		states = alignment.getAminoAcidsAsFitchStates();
		baseStates = phylogeny.getFitchStates(states).clone();
		ambiguousAtRoot = 0;
		ancestorAmbiguities = new boolean[baseStates.length];
		/* Establish how many ancestral states are ambiguous */
		for(int i=0;i<baseStates.length;i++){
			HashSet<String> statesSet=baseStates[i];
			if(statesSet.size()>1){
				ambiguousAtRoot++;
				ancestorAmbiguities[i] = true;
			}
		}
		/* Attempt to resolve the rootnode states */
		phylogeny.resolveFitchStatesTopnode();
		phylogeny.resolveFitchStates(phylogeny.states);
		/* A new PR object used to hold reconstructions */
		pr = new ParsimonyReconstruction(states, phylogeny);
		/* Compare each taxon to tree root MRCA */
		pr.printAncestralComparison();
		/* Define the taxon sets for comparison */
		focalTaxaSet = new HashSet<String>(Arrays.asList(focalTaxaArray));
		focalTaxaSet = phylogeny.areTipsPresent(focalTaxaSet);
		focalTaxaAndTheirMRCASetStates = phylogeny.getTipAndMRCAStatesOf(focalTaxaSet);
		focalTaxaMRCAstates = focalTaxaAndTheirMRCASetStates.remove("MRCA");
	}

	/**
	 * Print the results
	 */
	public void printResults() {
		int MRCAnumParallel_H1 = new StateComparison(focalTaxaMRCAstates,focalTaxaAndTheirMRCASetStates).countParallelChanges();
		int MRCAnumParallel_H1fullRequire = new StateComparison(focalTaxaMRCAstates,ancestorAmbiguities,focalTaxaAndTheirMRCASetStates).countParallelChanges();
		int pll_H1  = pr.findParallelSubtitutionsFromAncestral(focalTaxaArray, false);
		int pll_H1c = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguities(focalTaxaArray,baseStates, false);
		int pll_H1o = pr.findParallelSubtitutionsFromAncestralRejectingAmbiguitiesControllingOutgroups(focalTaxaArray,baseStates, false, outgroupTaxaArray);
		System.out.println(
				pll_H1+"\t"+
				pll_H1c+"\t"+
				pll_H1o+"\t"+
				MRCAnumParallel_H1+"\t"+
				MRCAnumParallel_H1fullRequire+"\t"+
				ambiguousAtRoot);
		phylogeny.getEndPos();
	}


}