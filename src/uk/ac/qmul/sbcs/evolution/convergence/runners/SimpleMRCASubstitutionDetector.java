package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.ParsimonyReconstruction;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.StateComparison;
import uk.ac.qmul.sbcs.evolution.convergence.TreeBranch;
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
	/* Generalised to n taxon sets or clades */
	HashSet<String>[] baseStates;						// states of root node before any ambiguity checking / resoluton
	HashSet<String>[] cladeTips;						// Arrays of HashSet<String>s describing tips. The MRCA node of each will be found and substitutions calculated.
	HashSet<TreeNode>[] cladeTipsAsNodes;				// Arrays of HashSet<TreeNodes>s describing tips.
	TreeNode[] MRCAnodes;								// MRCA nodes of each clade
	/* Totals for MRCA clades */
	int[] MRCAcladesBranchTotals;						// Number of branches in each MRCA clade
	int[] MRCAcladesBranchTotalsTerminal;				// Number of branches in each MRCA clade leading to external edges (extant taxa)
	int[] MRCAcladesBranchTotalsInternal;				// Number of branches in each MRCA clade connecting internal nodes only
	int[] MRCAcladesSubstitutionTotals;					// Number of substitutions in each MRCA clade
	int[] MRCAcladesSubstitutionTotalsTerminal;			// Number of substitutions in each MRCA clade on terminal branches
	int[] MRCAcladesSubstitutionTotalsInternal;			// Number of substitutions in each MRCA clade on internal nodes only
	
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
		phylogeny.setNodeNumbers(0, phylogeny.howManyTips());
		
	}

	/* Methods */

	public static void main(String[] args){
		SimpleMRCASubstitutionDetector s = 	new SimpleMRCASubstitutionDetector(args);
		if(args.length>2){
			s.streamlinedGo(args);
		}else{
			s.go();
			s.printResults();
		}
	}

	private void streamlinedGo(String[] argsList) {
		// Setup and I/O
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
		/* Should now be resolved as far as possible.
		 * Compare each taxon to tree root MRCA */
		//pr.printAncestralComparison();

		/*
		 *  Parse the args into lists
		 *  Then find the tips and MRCAs of each list. at this point print all branches below
		 *  Then walk through the MRCA / lists counting subs
		 */
		int numberOfClades = argsList.length-2;	// we'll assume the first two args are alignment and phylogeny respectively, and all the others (we've checked >2) are clades described by tips
		// Guessed the number of clades, initialise arrays
		cladeTips = new HashSet[numberOfClades];
		cladeTipsAsNodes = new HashSet[numberOfClades];
		MRCAnodes = new TreeNode[numberOfClades];
		MRCAcladesBranchTotals = new int[numberOfClades];
		MRCAcladesBranchTotalsTerminal = new int[numberOfClades];
		MRCAcladesBranchTotalsInternal = new int[numberOfClades];
		MRCAcladesSubstitutionTotals = new int[numberOfClades];
		MRCAcladesSubstitutionTotalsTerminal = new int[numberOfClades];
		MRCAcladesSubstitutionTotalsInternal = new int[numberOfClades];
		System.out.println("Assuming "+numberOfClades+" separate clades. Parsing clade descriptions...");
		// Parse the clade lists
		for(int i=2;i<argsList.length;i++){
			String[] taxaTokens = argsList[i].split(":");
			cladeTips[i-2] = new HashSet<String>();
			cladeTipsAsNodes[i-2] = new HashSet<TreeNode>();
			for(String aTaxon:taxaTokens){
				cladeTips[i-2].add(aTaxon);
			}
		}
		
		// Check we've parsed them correctly
		System.out.println("Read these taxon lists:");
		for(int i=0;i<cladeTips.length;i++){
			System.out.print("Clade "+i);
			for(String taxon:cladeTips[i]){
				System.out.print(" "+taxon);
			}
			System.out.println();
		}
		
		// Find the MRCA node of each clade, and also print all the branches beneath that MRCA node
		System.out.println("Searching the tree for the MRCAs of each clade...");
		for(int i=0;i<numberOfClades;i++){
			// Find the tip nodes corresponding to extant taxa
			Iterator<String> itr = cladeTips[i].iterator();
			while(itr.hasNext()){
				int nodeIDofTip = phylogeny.getTipNumber(itr.next());
				TreeNode tipNode = phylogeny.getNodeByNumberingID(nodeIDofTip);
				cladeTipsAsNodes[i].add(tipNode);
			}
				
			// Find the ID of the MRCA node
			int nodeIDofMRCA = phylogeny.getNodeNumberingIDContainingTaxa(cladeTips[i]);
			TreeNode cladeNodeMRCA = phylogeny.getNodeByNumberingID(nodeIDofMRCA);
			MRCAnodes[i] = cladeNodeMRCA;

			// Print all the branches below MRCA
			System.out.println("Found the MRCA of clade "+i+" ("+nodeIDofMRCA+"):\n"+cladeNodeMRCA.getContent()+"\nPrinting all branches below this node:");
			MRCAcladesBranchTotals[i] = cladeNodeMRCA.howManyTips();
			for(TreeBranch branch:cladeNodeMRCA.getBranches()){
				System.out.println(branch);
				Integer[] substitutions = StateComparison.printStateComparisonBetweenTwoNodes(branch.getParentNode().states, branch.getDaughterNode().states, branch.getParentNode().getContent(), branch.getDaughterNode().getContent());
				MRCAcladesSubstitutionTotals[i] = substitutions.length;
				if(branch.isEndsInTerminalTaxon()){
					MRCAcladesBranchTotalsTerminal[i]++;
					MRCAcladesSubstitutionTotalsTerminal[i] = substitutions.length;
				}else{
					MRCAcladesBranchTotalsInternal[i]++;
					MRCAcladesSubstitutionTotalsInternal[i] = substitutions.length;
				}
				System.out.println();
			}
		}
		
		// For each MRCA node and clade tips combination, compare and print substitutions
		System.out.println("Comparing ancestral clade MRCA node sequences with extant sequences...");
		for(int i=0;i<numberOfClades;i++){
			System.out.println("Comparing ancestral MRCA sequence for CLADE "+i+" against *ALL* clades' terminal taxa...");
			TreeNode thisMRCA = MRCAnodes[i];
			for(int j=0;j<cladeTipsAsNodes.length;j++){
				System.out.println("Clade MRCA: "+i+" -vs- clade tips: "+j);
				int MRCAtoTipsSubstitutions = 0;
				for(TreeNode someTip:cladeTipsAsNodes[j]){
					Integer[] substitutions = StateComparison.printStateComparisonBetweenTwoNodes(thisMRCA.states, someTip.states, "MRCA_clade_"+i, someTip.getContent());
					MRCAtoTipsSubstitutions+= substitutions.length;
				}
				System.out.println("Substitutions from Clade MRCA: "+i+" -vs- clade tips: "+j+": "+MRCAtoTipsSubstitutions);
			}
		}
		
		// All uncorrected pairwise comparisons
		System.out.println("Comparing extant sequences directly...");
		for(int i=0;i<numberOfClades;i++){
			for(TreeNode someTip:cladeTipsAsNodes[i]){
				for(int j=0;j<cladeTipsAsNodes.length;j++){
					System.out.println("Basis clade: "+i+" -vs- clade tips: "+j);
					int MRCAtoTipsSubstitutions = 0;
					for(TreeNode someOtherTip:cladeTipsAsNodes[j]){
						Integer[] substitutions = StateComparison.printStateComparisonBetweenTwoNodes(someTip.states, someOtherTip.states, someTip.getContent(), someOtherTip.getContent());
						MRCAtoTipsSubstitutions+= substitutions.length;
					}
					System.out.println("Substitutions from Clade MRCA: "+i+" -vs- clade tips: "+j+": "+MRCAtoTipsSubstitutions);
				}
			}
		}	
		
		// Print a summary of each clade
		System.out.println("Summary of clade counts...");
		System.out.println("Clade\tbranches\texternal\tinternal\t\tsubstitutions\texternal\tinternal");
		for(int i=0;i<numberOfClades;i++){
			System.out.println(i
					+"\t"+MRCAcladesBranchTotals[i]
					+"\t"+MRCAcladesBranchTotalsTerminal[i]
					+"\t"+MRCAcladesBranchTotalsInternal[i]+"\t"
					+"\t"+MRCAcladesSubstitutionTotals[i]
					+"\t"+MRCAcladesSubstitutionTotalsTerminal[i]
					+"\t"+MRCAcladesSubstitutionTotalsInternal[i]);
		}
		System.out.println("Done.");
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
		// print all branches to see their substitutions
		this.printBranchSubstitutions();
	}

	/**
	 * Print out all the branches of the tree and the state changes ('substitutions') therein.
	 */
	public void printBranchSubstitutions() {
		ArrayList<TreeBranch> allBranches = phylogeny.getBranches();
		Iterator<TreeBranch> i = allBranches.iterator();
		while(i.hasNext()){
			TreeBranch someBranch = i.next();
			System.out.println(someBranch);
			StateComparison.printStateComparisonBetweenTwoNodes(someBranch.getParentNode().states, someBranch.getDaughterNode().states, someBranch.getParentNode().getContent(), someBranch.getDaughterNode().getContent());
			System.out.println();
		}
		
	}


}