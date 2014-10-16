package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

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
	public HashMap<String,ArrayList<Float[]>> data; // a hashmap of the parsed data, key as pairs of nodes (branch comparisons) in the form 'A_B' where A and B are branch IDs in the 'from..to' notation.
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
		data = new HashMap<String,ArrayList<Float[]>>();
		// iterate through loaded data
		for(String line:inputData){
			String[] tokens = line.split("\t");
			if(tokens.length == 8){
				// a common ID for branch pairs will be constructed, of the form "branch_A_branch_B"
				// FIXME TODO IMPORTANT note that at present brnach IDs 'branch_foo_branch_bar' and 'branch_bar_branch_foo' are *NOT* equivalent keys and will be treated separately!!!!
				// TODO check codeml_ancestral output files to see whether this ever occurs, or only triagonal comparisons are made (e.g A_B, A_C, A_D; B_C, B_D; C_D but not e.g. also D_A, D_B, D_C; C_A, C_B; B_A and diagonals A_A B_B C_C D_D etc)
				String branch_A = tokens[2]; 	// the first branch, will be of the form "branch pair 22..10" or similar
				String branch_B = tokens[3];	// the second branch, will be of the form "39..11:" or similar
				// TODO amend branch info parsing to remove unwanted whitespace etc, e.h. from  'branch pair 22..10' or '39..11:' to '22..10' or '39..11'
				// TODO ultimately data structure should be e.g. HashMap<Set<Int>[],ArrayList<Float[]>> where key is an Set<Int>[], each elem of [] is a Set<Integer> e.g. {{22,10},{39,11}} 
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
				if(data.containsKey(composite_unique_branch_key)){
					// this branch pair has already been seen in the data, simply add to its branch info line (the probabilities for divergence, all convergence, parallel, strict convergence respectively)
					ArrayList<Float[]> existingProbabilities = data.remove(composite_unique_branch_key);
					existingProbabilities.add(newProbabilities);
					data.put(composite_unique_branch_key, existingProbabilities);
				}else{
					// this branch pair has not already been seen in the data, create a new ArrayList<String> with this line's info (the probabilities for divergence, all convergence, parallel, strict convergence respectively)
					ArrayList<Float[]> existingProbabilities = new ArrayList<Float[]>();
					existingProbabilities.add(newProbabilities);
					data.put(composite_unique_branch_key, existingProbabilities);
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
		Iterator<String> itr = data.keySet().iterator();
		while(itr.hasNext()){
			// loop through branch pairs
			String branch_pair = itr.next();
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
}
