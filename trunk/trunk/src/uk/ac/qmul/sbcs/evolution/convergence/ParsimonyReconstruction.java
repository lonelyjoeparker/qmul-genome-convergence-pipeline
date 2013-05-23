package uk.ac.qmul.sbcs.evolution.convergence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ParsimonyReconstruction {

	public HashMap<String, HashSet<String>[]> extantTaxonStates;
	public TreeNode phylogeny;
	
	public ParsimonyReconstruction(HashMap<String, HashSet<String>[]> states, TreeNode tree) {
		this.extantTaxonStates = states;
		this.phylogeny = tree;
	}

	public void printAncestralComparison() {
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		HashSet<String>[] ancestor = phylogeny.getStates();
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}

		//Iterate through the sequences
		Iterator<String> taxonItr = extantTaxonStates.keySet().iterator();
		while(taxonItr.hasNext()){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = taxonItr.next();
			HashSet<String>[] taxonSeq = extantTaxonStates.get(taxon);
			String[] taxonArray = new String[taxonSeq.length];
			for(int i=0;i<taxonSeq.length;i++){
				taxonArray[i] = (String)taxonSeq[i].toArray()[0];
			}
			System.out.print("ancestral:\t\t\t");
			for(String state:ancestorArray){
				System.out.print(state);
			}
			System.out.println();
			int substitutions = 0;
			System.out.print(taxon+":\t\t\t");
			for(int i=0;i<taxonSeq.length;i++){
				if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
					System.out.print('.');
				}else{
					System.out.print(taxonArray[i]);
					substitutions++;
				}
			}
			System.out.println();
			System.out.print(taxon+":\t\t\t");
			for(String state:taxonArray){
				System.out.print(state);
			}
			System.out.println("\nsubstitutions:\t"+substitutions+"\n");
		}
		
	}

	/**
	 * Compares a set of taxa against the ancestral, looking for changes from the ancestral which are parallel to two or more.
	 * <p>Execution:
	 * <pre>
	 * Build the String[]  arrays of ancestral and taxa; store them
	 * Initialise a String[# parallel taxa][#states] compareMatrix
	 * for(i in parallel taxa){
	 * 	for(j in states of i){
	 * 		if j ­ ancestor[j] || '-' || 'X'; compareMatrix[i][j] = states[j]; else compareMatrix[i][j] = null
	 * 	}
	 * }
	 * for(j in compareMatrix[0][j]){
	 * 	initialise an empty set
	 * 	for(i in # parallelTaxa){
	 * 		if ! set contains compareMatrix[i][j]||compareMarix[i][j] = null; add compareMatrix[i][j] to set
	 * 		else; increment parallel changes
	 * 	}
	 * }
	 * </pre>
	 * @param parallelTaxa - a String[] of taxa to look for parallel changes in.
	 */
	public int findParallelSubtitutionsFromAncestral(String[] parallelTaxa) {
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		HashSet<String>[] ancestor = phylogeny.getStates();
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}

		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[parallelTaxa.length][ancestorArray.length];
		         
		// Iterate through the sequences
		for(int t=0;t<parallelTaxa.length;t++){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = parallelTaxa[t];
			if(extantTaxonStates.containsKey(taxon)){
				HashSet<String>[] taxonSeq = extantTaxonStates.get(taxon);
				String[] taxonArray = new String[taxonSeq.length];
				System.out.println(taxon+"\t\t");
				for(int i=0;i<taxonSeq.length;i++){
					taxonArray[i] = (String)taxonSeq[i].toArray()[0];
					if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
						System.out.print('.');
					}else{
						System.out.print(taxonArray[i]);
						substitutionsMatrix[t][i] = taxonArray[i];
					}
				}
				System.out.println();
			}
		}
		
		// Iterate through the substitutionsMatrix looking for parallel changes
		int parallelChanges = 0;
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
				System.out.print(j+"\t");
				for(int i=0;i<parallelTaxa.length;i++){
					String sub = ".";
					if(substitutionsMatrix[i][j] != null){
						sub = substitutionsMatrix[i][j];
					}
					System.out.print(sub);
				}
				System.out.println();
			}
		}
		System.out.println("parallel:"+parallelChanges);
		return parallelChanges;
	}

	/**
	 * Compares a set of taxa against the ancestral, looking for changes from the ancestral which are parallel to two or more.
	 * <p>Execution:
	 * <pre>
	 * Build the String[]  arrays of ancestral and taxa; store them
	 * Initialise a String[# parallel taxa][#states] compareMatrix
	 * for(i in parallel taxa){
	 * 	for(j in states of i){
	 * 		if j ­ ancestor[j] || '-' || 'X'; compareMatrix[i][j] = states[j]; else compareMatrix[i][j] = null
	 * 	}
	 * }
	 * for(j in compareMatrix[0][j]){
	 * 	initialise an empty set
	 * 	for(i in # parallelTaxa){
	 * 		if ! set contains compareMatrix[i][j]||compareMarix[i][j] = null; add compareMatrix[i][j] to set
	 * 		else; increment parallel changes
	 * 	}
	 * }
	 * </pre>
	 * @param parallelTaxa - a String[] of taxa to look for parallel changes in.
	 */
	public int findParallelSubtitutionsFromAncestralRejectingAmbiguities(String[] parallelTaxa, HashSet<String>[] rootStates) {
		// Convert ancestral states to array (NB, assumes they are resolved, e.g. one state only at each position)
		HashSet<String>[] ancestor = phylogeny.getStates();
		String[] ancestorArray = new String[ancestor.length];
		for(int i=0;i<ancestor.length;i++){
			ancestorArray[i] = (String)ancestor[i].toArray()[0];
		}

		// Create the String[] which will hold the changeMatrix.
		String[][] substitutionsMatrix = new String[parallelTaxa.length][ancestorArray.length];
		         
		// Iterate through the sequences
		for(int t=0;t<parallelTaxa.length;t++){
			// Convert extant states to array (NB, assumes they are resolved, e.g. one state only at each position)
			String taxon = parallelTaxa[t];
			if(extantTaxonStates.containsKey(taxon)){
				HashSet<String>[] taxonSeq = extantTaxonStates.get(taxon);
				String[] taxonArray = new String[taxonSeq.length];
				System.err.println(taxon+"\t\t");
				for(int i=0;i<taxonSeq.length;i++){
					taxonArray[i] = (String)taxonSeq[i].toArray()[0];
					if((ancestorArray[i].equals(taxonArray[i]))||(taxonArray[i].equals("X"))||(taxonArray[i].equals("-"))){
						System.err.print('.');
					}else{
						System.err.print(taxonArray[i]);
						substitutionsMatrix[t][i] = taxonArray[i];
					}
				}
				System.err.println();
			}
		}
		
		// Iterate through the substitutionsMatrix looking for parallel changes
		int parallelChanges = 0;
		HashSet<String> substitutionsSet;
		for(int j=0;j<substitutionsMatrix[0].length;j++){
			if(rootStates[j].size()>1){
				// there is an ambiguous state at the root, discount this.
				// System.out.println(rootStates[j].toArray());
			}else{
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
					System.err.print(j+"\t");
					for(int i=0;i<parallelTaxa.length;i++){
						String sub = ".";
						if(substitutionsMatrix[i][j] != null){
							sub = substitutionsMatrix[i][j];
						}
						System.err.print(sub);
					}
					System.err.println();
				}
			}
		}
		System.err.println("parallel:"+parallelChanges);
		return parallelChanges;
	}
	
}
