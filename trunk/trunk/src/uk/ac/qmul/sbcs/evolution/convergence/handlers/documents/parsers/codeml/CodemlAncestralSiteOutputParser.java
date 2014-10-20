package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;


/**
 * Class to parse the convergentSites.out output from the modified codeml-ancestral package by	Jason De Koning at evolutionarygenomics.com / U Colorado, and used in Castoe <i>et al.</i> (2009)'s paper on convergence in agamid lizards.
 * <p>Constructor should be called with a single {@link java.io.File} argument, this being the location of the convergentSites.out file. The constructor will then build a hash of node comparison IDs and site convergent/divergent probability data. This can then be polled by the getData() method.</p>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/ancestral.html">evolutionarygenomics.com</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/codeMLancestral/README.txt">codeml-ancestral README</a>
 */
public class CodemlAncestralSiteOutputParser {
	// A class to parse the sites info from codeml-ancestral
	// By 'sites info' we mean the '' file from the codeml-ancestral analysis

	public final File inputFile;		// the convergentSites.out file from codeml-ancestral
	public HashMap<BranchPairComparison,ArrayList<Float[]>> data; // a hashmap of the parsed data, key as pairs of nodes (branch comparisons) in the form 'A_B' where A and B are branch IDs in the 'from..to' notation.
	public TreeNode phylogeny; 			// a phylogeny which this data has been drawn from

	/**
	 * Default no-arg constructor. Deprecated. Use CodemlAncestralSiteOutputParser(File input) instead.
	 */
	@Deprecated
	public CodemlAncestralSiteOutputParser(){
		this.inputFile = null;
		this.data = null;
		this.phylogeny = null;
	}

	public CodemlAncestralSiteOutputParser(File input){
		this.inputFile = input;
		this.phylogeny = null;
		// load input file data as ArrayList<String>
		ArrayList<String> inputData = new BasicFileReader().loadSequences(inputFile, false, true);
		// add the data on divergent and convergent site probabilities by sitewise branch-pair comparison
		// initialise HashMap
		data = new HashMap<BranchPairComparison,ArrayList<Float[]>>();
		// iterate through loaded data
		for(String line:inputData){
			String[] tokens = line.split("\t");
			if(tokens.length == 8){
				// The internal class BranchPairComparison (containing a Set of Branches) is used to hold the branch ID information.
				// FIXME TODO IMPORTANT note that at present brnach IDs 'branch_foo_branch_bar' and 'branch_bar_branch_foo' are *NOT* equivalent keys and will be treated separately!!!! --> SHOULD BE FIXED now as bidirectional Set.contains() used to hold IDs
				// TODO check codeml_ancestral output files to see whether this ever occurs, or only triagonal comparisons are made (e.g A_B, A_C, A_D; B_C, B_D; C_D but not e.g. also D_A, D_B, D_C; C_A, C_B; B_A and diagonals A_A B_B C_C D_D etc)
				String branch_A = tokens[2]; 	// the first branch, will contain pattern of the form "branch pair 22..10" or similar
				String branch_B = tokens[3];	// the second branch, will contain pattern of the form "39..11:" or similar
				// Now get the integers from these strings. Using http://stackoverflow.com/questions/4030928/extract-digits-from-a-string-in-java for simplicity
				// NB this is *not* efficient, really..
				// First split the branch strings into 'from' and 'to' portions using the '..' notation in PAML output:
				String[] branch_A_tokens = tokens[2].split("\\.\\.");
				String[] branch_B_tokens = tokens[3].split("\\.\\.");
				// Now strip all non-digit chars fro those strings:
				String branch_A_cleaned_from 	= branch_A_tokens[0].replaceAll("\\D+","");
				String branch_A_cleaned_to 		= branch_A_tokens[1].replaceAll("\\D+","");
				String branch_B_cleaned_from 	= branch_B_tokens[0].replaceAll("\\D+","");
				String branch_B_cleaned_to 		= branch_B_tokens[1].replaceAll("\\D+","");
				// Now the cleaned strings can be parsed as integers
				int branch_A_from 	= Integer.parseInt(branch_A_cleaned_from);
				int branch_A_to 	= Integer.parseInt(branch_A_cleaned_to);
				int branch_B_from 	= Integer.parseInt(branch_B_cleaned_from);
				int branch_B_to 	= Integer.parseInt(branch_B_cleaned_to);
				// Finally the Branches / BranchPairComparisons can be initialised for these
				// TODO consider overriding compare() in BranchPairComparison...
				BranchPairComparison theBranches = new BranchPairComparison(branch_A_from, branch_A_to, branch_B_from, branch_B_to);
				// FIXED amend branch info parsing to remove unwanted whitespace etc, e.h. from  'branch pair 22..10' or '39..11:' to '22..10' or '39..11'
				// FIXED ultimately data structure should be e.g. HashMap<Set<Int>[],ArrayList<Float[]>> where key is an Set<Int>[], each elem of [] is a Set<Integer> e.g. {{22,10},{39,11}} 
				String composite_unique_branch_key = branch_A + "_" + branch_B;
				// parse the probabilities, should be in tokens[4..7]
				Float prob_divergent 			= Float.parseFloat(tokens[4]);		// probability of divergent changes between branches A and B
				Float prob_convergent_all 		= Float.parseFloat(tokens[5]);		// probability of all types of convergent changes between branches A and B
				Float prob_convergent_parallel 	= Float.parseFloat(tokens[6]);		// probability of parallel convergent changes between branches A and B
				Float prob_convergent_strict 	= Float.parseFloat(tokens[7]);		// probability of strict convergent changes between branches A and B
				// initialise Float[] to be added to data
				Float[] newProbabilities = new Float[4];
				newProbabilities[0] = prob_divergent;
				newProbabilities[1] = prob_convergent_all;
				newProbabilities[2] = prob_convergent_parallel;
				newProbabilities[3] = prob_convergent_strict;
				if(data.containsKey(theBranches)){
					// this branch pair has already been seen in the data, simply add to its branch info line (the probabilities for divergence, all convergence, parallel, strict convergence respectively)
					ArrayList<Float[]> existingProbabilities = data.remove(composite_unique_branch_key);
					existingProbabilities.add(newProbabilities);
					data.put(theBranches, existingProbabilities);
				}else{
					// this branch pair has not already been seen in the data, create a new ArrayList<String> with this line's info (the probabilities for divergence, all convergence, parallel, strict convergence respectively)
					ArrayList<Float[]> existingProbabilities = new ArrayList<Float[]>();
					existingProbabilities.add(newProbabilities);
					data.put(theBranches, existingProbabilities);
				}
			}
		}
	}
	
	/**
	 * @return the phylogeny
	 */
	public TreeNode getPhylogeny() {
		return phylogeny;
	}

	/**
	 * @param phylogeny the phylogeny to set
	 */
	public void setPhylogeny(TreeNode phylogeny) {
		this.phylogeny = phylogeny;
	}

	public float[][] getAllBranchPairProbabilitiesSitewiseSummed(){
		// return all branch-pair divergent, convergent, parallel, strict-convergent probabilities, as a list (square matrix). sitewise values (different sites' on a branch-pair comparison) will be summed.
		float[][] returnMatrix = new float[data.size()][4];
		int rowIndex = 0;
		Iterator<BranchPairComparison> itr = data.keySet().iterator();
		while(itr.hasNext()){
			// loop through branch pairs
			BranchPairComparison branch_pair = itr.next();
			float[] rowData = new float[4];
			// get branch pair sitewise info
			ArrayList<Float[]> branch_pair_probabilities = data.get(branch_pair);
			for(Float[] sitewise_branch_pair_probabilities:branch_pair_probabilities){
				// sum over all substitutions at this branch pair
				for(int i=0;i<4;i++){
					rowData[i] = rowData[i] + sitewise_branch_pair_probabilities[i];
				}
			}
			// add to retMat
			returnMatrix[rowIndex] = rowData;
			rowIndex++;
		}
		return returnMatrix;
	}
	
	public float[][] getSitewiseBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber){
		// return all the sitewise (not-summed) divergent, convergent, parallel, strict-convergent probabilities for any branch-pair comparisons including a particular node number
		// currently only way to do this is loop through the data examining each String of composite_unique_branch_key looking to see if it contains nodeNumber. buggy as fuck, not even going to implement that
		// TODO not implemented
		// FIXME implement this
		return null;
	}
	
	public float[] getSummedBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber){
		// return all the sitewise (summed) divergent, convergent, parallel, strict-convergent probabilities for any branch-pair comparisons including a particular node number
		// currently only way to do this is loop through the data examining each String of composite_unique_branch_key looking to see if it contains nodeNumber. buggy as fuck, not even going to implement that
		// TODO not implemented
		// FIXME implement this
		return null;
	}
	
	public float[] getProbabilitiesForNodeComparisons(int nodeFrom_A, int nodeTo_A, int nodeFrom_B, int nodeTo_B){
		// return all the sitewise (summed) divergent, convergent, parallel, strict-convergent probabilities for the branch-pair comparison between branches A1->A2 and B1->B2
		// currently no good way to do this as data containing Strings of composite_unique_branch_key are not a logical data structure. buggy as fuck, not even going to implement this
		// TODO not implemented
		// FIXME implement this
		return null;
	}

	public float[] getProbabilitiesForNodeComparisonsDefinedByTaxonSetMRCAs(String[] TaxonSetFrom_A, String[] TaxonSetTo_A, String[] TaxonSetFrom_B, String[] TaxonSetTo_B) throws ReferencePhylogenyNotSetException{
		if(this.phylogeny == null){throw new ReferencePhylogenyNotSetException();}
		// return all the sitewise (summed) divergent, convergent, parallel, strict-convergent probabilities for the branch-pair comparison between branches A1->A2 and B1->B2, defined by taxon sets
		// e.g. A1 = MRCA of TaxonSetFrom_A, A2 = MRCA of TaxonSetFrom_A etc. 
		// assumes TreeNode phylogeny present/set
		// needs to be robust to terminal branches, too
		// currently no good way to do this as data containing Strings of composite_unique_branch_key are not a logical data structure. buggy as fuck, not even going to implement this
		// TODO not implemented
		// FIXME implement this
		return null;
	}
	
	public float[] getProbabilitiesBySiteIndex(int siteIndex){
		// return all the divergent, convergent, parallel, strict-convergent probabilities, summed over all branch-pair comparison, for a given site index
		// currently no good way to do this as data containing site indices or pattern indices discarded at the moment. buggy as fuck, not even going to implement this
		// TODO not implemented
		// FIXME implement this
		return null;
	}
	
	public float[][] getProbabilitiesAllSites(){
		// return all the divergent, convergent, parallel, strict-convergent probabilities, summed over all branch-pair comparison, for every site in the alignment
		// currently no good way to do this as data containing site indices or pattern indices discarded at the moment. buggy as fuck, not even going to implement this
		// TODO not implemented
		// FIXME implement this
		return null;
	}

	public class ReferencePhylogenyNotSetException extends Exception{}
	
	/**
	 * Internal class to represent pairs of branches (themselves pairs of nodes) which are compared in a branch-pair comparison.
	 * <p>Note that the BranchPairComparison and the contains() method <b>is not</b> an implementation of {@link java.util.Set#contains()}, or subclass of e.g. {@link java.util.HashSet#contains()}.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 * @see {@link Branch}
	 */
	public class BranchPairComparison{
		Branch branch_A;
		Branch branch_B;
		
		/**
		 * No-arg constructor. Deprecated.
		 * @deprecated
		 * TODO consider overriding compare() in BranchPairComparison...
		 */
		@Deprecated
		private BranchPairComparison(){}

		public BranchPairComparison(int node_from_branch_A, int node_to_branch_A, int node_from_branch_B, int node_to_branch_B){
			branch_A = new Branch(node_from_branch_A, node_to_branch_A);
			branch_B = new Branch(node_from_branch_B, node_to_branch_B);
		}
		
		/**
		 * Returns true if this either of the branches under comparison joins node I
		 * @param I - node ID 
		 * @return {@link Boolean} - true if node is present
		 * @see {@link java.util.Set#contains()}
		 */
		public boolean contains(Integer I){
			return (branch_A.contains(I) || branch_B.contains(I));
		}

		/**
		 * Overidden toString() method. 
		 * @return String of form "from_A..to_A|from_B..to_B"
		 */
		@Override
		public String toString(){
			return branch_A.toString() + "|" + branch_B.toString();
		}
	}
	
	/**
	 * Internal class to represent branch (pairs of nodes) which are compared in a branch-pair comparison.
	 * <p>Note that the contains() method <b>is not</b> an implementation of {@link java.util.Set#contains()}, or subclass of e.g. {@link java.util.HashSet#contains()}.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 * @see {@link BranchPairComparison}
	 */
	public class Branch{
		int node_begin;
		int node_end;
		HashSet<Integer> branch;
		
		/**
		 * No-arg constructor. Deprecated.
		 * @deprecated
		 */
		@Deprecated
		private Branch(){}
		
		public Branch(int node_from, int node_to){
			node_begin = node_from;
			node_end = node_to;
			branch = new HashSet<Integer>();
			branch.add(node_begin);
			branch.add(node_end);
		}
		
		/**
		 * Returns true if this branch joins node I
		 * @param I - node ID 
		 * @return {@link Boolean} - true if node is present
		 * @see {@link java.util.Set#contains()}
		 */
		public boolean contains(Integer I){
			return branch.contains(I);
		}
		
		/**
		 * Overidden toString() method. 
		 * @return String of form "from..to"
		 */
		@Override
		public String toString(){
			return node_begin + ".." + node_end;
		}
	}
}
