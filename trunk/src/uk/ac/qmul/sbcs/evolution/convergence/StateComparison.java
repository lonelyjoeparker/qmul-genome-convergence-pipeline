package uk.ac.qmul.sbcs.evolution.convergence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A utility class to perform comparisons betwen ancestral state reconstructions and terminal tips.
 * <p>Assumes the reconstructions themselves have already been performed on a TreeNode with a ParsimonyReconstruction.
 * <p>This class likely called by a ParallelSubstitutionDetector
 * <p>States (of nucleotides, amino acids or other discrete data) are represented as an array of HashSet<String>s - 
 * it is presumed that in the case of sequence data this array will therefore comprise one HashSet<String> per aligned sequence position.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see TreeNode
 * @see ParsimonyReconstruction
 * @see uk.ac.qmul.sbcs.evolution.convergence.runners.ParallelSubstitutionDetector
 * @see uk.ac.qmul.sbcs.evolution.convergence.runners.ParallelSubstitutionDetectorMRCA
 */
public class StateComparison {
	HashMap<String,HashSet<String>[]> toCompare;
	HashSet<String>[] ancestor;
	int parallelChanges=0;
	
	@Deprecated
	public StateComparison(){}
	
	public StateComparison(HashSet<String>[] anAncestor, String explicitRequire, HashMap<String,HashSet<String>[]> taxaToCompare){
		this.ancestor = anAncestor;
		this.toCompare = taxaToCompare;

		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}

		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[toCompare.size()][ancestorArray.length];
		
		// Create a boolean[] for subs at explicitCompare
		boolean[] explicitSubstitution = new boolean[ancestorArray.length];
		
		// List of parallel taxa keys (very inefficient, but..)
		String[] parallelTaxa = new String[toCompare.size()];
		Iterator<String> taxonIter = toCompare.keySet().iterator();
		int t=0;
		boolean isTheExplicit = false;
		while(taxonIter.hasNext()){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = taxonIter.next();
			if(taxon.equals(explicitRequire)){
				isTheExplicit = true;
			}else{
				isTheExplicit = false;
			}
			HashSet<String>[] taxonSeq = toCompare.get(taxon);
			String[] taxonArray = new String[taxonSeq.length];
	//		System.out.println(taxon+"\t\t");
			for(int i=0;i<taxonSeq.length;i++){
				taxonArray[i] = (String)taxonSeq[i].toArray()[0];
				if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
	//				System.out.print('.');
				}else{
	//				System.out.print(taxonArray[i]);
					substitutionsMatrix[t][i] = taxonArray[i];
					if(isTheExplicit){
						explicitSubstitution[i] = true;
					}
				}
			}
	//		System.out.println();
			t++;	// IMPORTANT don't delete..
		}

		// Iterate through the substitutionsMatrix looking for parallel changes
		HashSet<String> substitutionsSet;
		for(int j=0;j<substitutionsMatrix[0].length;j++){
			boolean parallelHere = false;
			substitutionsSet = new HashSet<String>();
			for(int i=0;i<parallelTaxa.length;i++){
				String substitution = substitutionsMatrix[i][j];
				if(substitution != null){
					if(substitutionsSet.contains(substitution)){
						parallelHere = true;
					}else{
						substitutionsSet.add(substitution);
					}
				}
			}
			if(parallelHere && explicitSubstitution[j]){
				parallelChanges++;
	//			System.out.print(j+"\t");
	//			for(int i=0;i<parallelTaxa.length;i++){
	//				String sub = ".";
	//				if(substitutionsMatrix[i][j] != null){
	//					sub = substitutionsMatrix[i][j];
	//				}
	//				System.out.print(sub);
	//			}
	//			System.out.println();
			}
		}
	//	System.out.println("parallel:"+parallelChanges);
	}
	
	public StateComparison(HashSet<String>[] anAncestor, HashMap<String,HashSet<String>[]> taxaToCompare){
		this.ancestor = anAncestor;
		this.toCompare = taxaToCompare;
	
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}
	
		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[toCompare.size()][ancestorArray.length];
		
		// List of parallel taxa keys (very inefficient, but..)
		String[] parallelTaxa = new String[toCompare.size()];
		Iterator<String> taxonIter = toCompare.keySet().iterator();
		int t=0;
		while(taxonIter.hasNext()){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = taxonIter.next();
			HashSet<String>[] taxonSeq = toCompare.get(taxon);
			String[] taxonArray = new String[taxonSeq.length];
	//		System.out.println(taxon+"\t\t");
			for(int i=0;i<taxonSeq.length;i++){
				taxonArray[i] = (String)taxonSeq[i].toArray()[0];
				if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
	//				System.out.print('.');
				}else{
	//				System.out.print(taxonArray[i]);
					substitutionsMatrix[t][i] = taxonArray[i];
				}
			}
	//		System.out.println();
			t++;	// IMPORTANT don't delete..
		}
	
		// Iterate through the substitutionsMatrix looking for parallel changes
		HashSet<String> substitutionsSet;
		for(int j=0;j<substitutionsMatrix[0].length;j++){
			boolean parallelHere = false;
			substitutionsSet = new HashSet<String>();
			for(int i=0;i<parallelTaxa.length;i++){
				String substitution = substitutionsMatrix[i][j];
				if(substitution != null){
					if(substitutionsSet.contains(substitution)){
						parallelHere = true;
					}else{
						substitutionsSet.add(substitution);
					}
				}
			}
			if(parallelHere){
				parallelChanges++;
	//			System.out.print(j+"\t");
	//			for(int i=0;i<parallelTaxa.length;i++){
	//				String sub = ".";
	//				if(substitutionsMatrix[i][j] != null){
	//					sub = substitutionsMatrix[i][j];
	//				}
	//				System.out.print(sub);
	//			}
	//			System.out.println();
			}
		}
	//	System.out.println("parallel:"+parallelChanges);
	}

	public StateComparison(HashSet<String>[] anAncestor, boolean[] ancestorAmbiguities, HashMap<String,HashSet<String>[]> taxaToCompare){
		this.ancestor = anAncestor;
		this.toCompare = taxaToCompare;
	
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}
	
		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[toCompare.size()][ancestorArray.length];
		
		// List of parallel taxa keys (very inefficient, but..)
		String[] parallelTaxa = new String[toCompare.size()];
		Iterator<String> taxonIter = toCompare.keySet().iterator();
		int t=0;
		while(taxonIter.hasNext()){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = taxonIter.next();
			HashSet<String>[] taxonSeq = toCompare.get(taxon);
			String[] taxonArray = new String[taxonSeq.length];
	//		System.out.println(taxon+"\t\t");
			for(int i=0;i<taxonSeq.length;i++){
				taxonArray[i] = (String)taxonSeq[i].toArray()[0];
				if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
	//				System.out.print('.');
				}else{
	//				System.out.print(taxonArray[i]);
					substitutionsMatrix[t][i] = taxonArray[i];
				}
			}
	//		System.out.println();
			t++;	// IMPORTANT don't delete..
		}
	
		// Iterate through the substitutionsMatrix looking for parallel changes
		HashSet<String> substitutionsSet;
		ArrayList<String> substitutionsList;
		for(int j=0;j<substitutionsMatrix[0].length;j++){
			if(!ancestorAmbiguities[j]){
		//		boolean parallelHere = false;
				substitutionsSet = new HashSet<String>();
				substitutionsList = new ArrayList<String>();
				for(int i=0;i<parallelTaxa.length;i++){
					String substitution = substitutionsMatrix[i][j];
					if(substitution != null){
						substitutionsList.add(substitution);
						if(!substitutionsSet.contains(substitution)){
							substitutionsSet.add(substitution);
						}
					}
				}
				if((substitutionsSet.size() == 1) && (substitutionsList.size() == parallelTaxa.length)){
					parallelChanges++;
		//			System.out.print(j+"\t");
		//			for(int i=0;i<parallelTaxa.length;i++){
		//				String sub = ".";
		//				if(substitutionsMatrix[i][j] != null){
		//					sub = substitutionsMatrix[i][j];
		//				}
		//				System.out.print(sub);
		//			}
		//			System.out.println();
				}
			}
		}
	//	System.out.println("parallel:"+parallelChanges);
	}

	public StateComparison(HashSet<String>[] anAncestor, String explicitRequire, boolean[] ancestorAmbiguities, HashMap<String,HashSet<String>[]> taxaToCompare){
		this.ancestor = anAncestor;
		this.toCompare = taxaToCompare;
	
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}
	
		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[toCompare.size()][ancestorArray.length];
		
		// Create a boolean[] for subs at explicitCompare
		boolean[] explicitSubstitution = new boolean[ancestorArray.length];
		
		// List of parallel taxa keys (very inefficient, but..)
		String[] parallelTaxa = new String[toCompare.size()];
		Iterator<String> taxonIter = toCompare.keySet().iterator();
		int t=0;
		boolean isTheExplicit = false;
		while(taxonIter.hasNext()){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = taxonIter.next();
			if(taxon.equals(explicitRequire)){
				isTheExplicit = true;
			}else{
				isTheExplicit = false;
			}
			HashSet<String>[] taxonSeq = toCompare.get(taxon);
			String[] taxonArray = new String[taxonSeq.length];
	//		System.out.println(taxon+"\t\t");
			for(int i=0;i<taxonSeq.length;i++){
				if (!ancestorAmbiguities[i]) {
					taxonArray[i] = (String) taxonSeq[i].toArray()[0];
					if ((ancestorArray[i].equals(taxonArray[i]))
							|| (taxonArray[i].equals("X"))
							|| (taxonArray[i].equals("-"))) {
						//				System.out.print('.');
					} else {
						//				System.out.print(taxonArray[i]);
						substitutionsMatrix[t][i] = taxonArray[i];
						if (isTheExplicit) {
							explicitSubstitution[i] = true;
						}
					}
				}
			}
	//		System.out.println();
			t++;	// IMPORTANT don't delete..
		}
	
		// Iterate through the substitutionsMatrix looking for parallel changes
		HashSet<String> substitutionsSet;
		for(int j=0;j<substitutionsMatrix[0].length;j++){
			boolean parallelHere = false;
			substitutionsSet = new HashSet<String>();
			for(int i=0;i<parallelTaxa.length;i++){
				String substitution = substitutionsMatrix[i][j];
				if(substitution != null){
					if(substitutionsSet.contains(substitution)){
						parallelHere = true;
					}else{
						substitutionsSet.add(substitution);
					}
				}
			}
			if(parallelHere && explicitSubstitution[j]){
				parallelChanges++;
	//			System.out.print(j+"\t");
	//			for(int i=0;i<parallelTaxa.length;i++){
	//				String sub = ".";
	//				if(substitutionsMatrix[i][j] != null){
	//					sub = substitutionsMatrix[i][j];
	//				}
	//				System.out.print(sub);
	//			}
	//			System.out.println();
			}
		}
	//	System.out.println("parallel:"+parallelChanges);
	}

	public int countParallelChanges(){
		return this.parallelChanges;
	}
	
	/**
	 * This is a static utility method to print a comparison between two sets of states (implied to be from nodeOne and nodeTwo). 
	 * <p><b>NOTE:</b> it is assumed 
	 * @param nodeStatesOne - States of first node (e.g. [{A},{C},{C},{T}... n])
	 * @param nodeStatesTwo - States of second node
	 * @param nodeLabelOne  - Label of first node (e.g. name)
	 * @param nodeLabelTwo  - Label of second node 
	 * @return divergentStatePositions - array of positions <b>(indexed to 1, not 0)</b> of divergent 
	 * states (positions/NTs/AAs) between nodeOne and nodeTwo. Calling divergentStatePositions.length 
	 * will therefore give the count of substitutions (not including 'X' or '-' characters).
	 * <br/><br/><i>*Indexed from (1..length) not (0..(length-1)) as this the convention when indexing 
	 * sequence alignment positions</i>
	 */
	public static Integer[] printStateComparisonBetweenTwoNodes(HashSet<String>[] nodeStatesOne, HashSet<String>[] nodeStatesTwo, String nodeLabelOne, String nodeLabelTwo){
		ArrayList<Integer> divergentStatePositionsList = new ArrayList<Integer>();	// We'll keep track of the number of mismatches and report back
		
		// Check the two arrays are equal size
		if(nodeStatesOne.length != nodeStatesTwo.length){
			throw new ArrayIndexOutOfBoundsException("Sequence state arrays are of unequal length ["+nodeStatesOne.length+"]["+nodeStatesTwo.length+"]");
		}
		
		// Cast the two state HashSet[] arrays down into simple String[] arrays
		String[] stateArrayOne = new String[nodeStatesOne.length];
		String[] stateArrayTwo = new String[nodeStatesTwo.length];
		
		// Initialise output buffers
		StringBuffer nodeOnePrintBuffer = new StringBuffer(nodeLabelOne+':');
		StringBuffer comparisonPrintBuffer = new StringBuffer("<comparison>:");
		StringBuffer nodeTwoPrintBuffer = new StringBuffer(nodeLabelTwo+':');
		
		// Pad the names to be equally sized, then finally append a tab to each
		int maxNodeNameLength = Math.max(Math.max(nodeOnePrintBuffer.length(), nodeTwoPrintBuffer.length()), comparisonPrintBuffer.length());
		while(nodeOnePrintBuffer.length() < maxNodeNameLength){nodeOnePrintBuffer.append(' ');}
		while(comparisonPrintBuffer.length() < maxNodeNameLength){comparisonPrintBuffer.append(' ');}
		while(nodeTwoPrintBuffer.length() < maxNodeNameLength){nodeTwoPrintBuffer.append(' ');}
		nodeOnePrintBuffer.append("\t");
		comparisonPrintBuffer.append("\t");
		nodeTwoPrintBuffer.append("\t");

		/*
		 *  Iterate through (we should have checked they're of equal length by now). 
		 *  Also buffer output of taxon states and comparison on this pass
		 */
		for(int i=0;i<nodeStatesOne.length;i++){
			stateArrayOne[i] = (String)nodeStatesOne[i].toArray()[0];
			stateArrayTwo[i] = (String)nodeStatesTwo[i].toArray()[0];
			nodeOnePrintBuffer.append(stateArrayOne[i]);
			nodeTwoPrintBuffer.append(stateArrayTwo[i]);
			// Test whether the states are equal (or undetermined 'X', or a gap '-')
			if((stateArrayOne[i].equals(stateArrayTwo[i]))||(stateArrayTwo[i].equals("X"))||(stateArrayTwo[i].equals("-"))){
				// Identity: append consensus char to buffer
				comparisonPrintBuffer.append('.');
			}else{
				// Divergent: append divergent char to buffer, increment divergent counter
				comparisonPrintBuffer.append(stateArrayTwo[i]);
				divergentStatePositionsList.add(i+1);
			}
		}
		// Append line endings to buffers
		nodeOnePrintBuffer.append("\n");
		comparisonPrintBuffer.append("\n");
		nodeTwoPrintBuffer.append("\n");
		// Cast the divergent positions list to an array
		Integer[] divergentStatePositions = divergentStatePositionsList.toArray(new Integer[divergentStatePositionsList.size()]);
		// Print buffers
		System.out.println(nodeOnePrintBuffer.toString()+comparisonPrintBuffer.toString()+nodeTwoPrintBuffer.toString());
		System.out.print("substitutions ("+divergentStatePositions.length+")");
		for(int position:divergentStatePositions){
			System.out.print(" "+position);
		}
		System.out.println();
		return divergentStatePositions;
	}
}
