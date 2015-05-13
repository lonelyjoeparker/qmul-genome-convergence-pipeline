package uk.ac.qmul.sbcs.evolution.convergence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A utility class to perform comparisons betwen ancestral state reconstructions and terminal tips.
 * <p>Assumes the reconstructions themselves have already been performed on a TreeNode with a ParsimonyReconstruction.
 * <p>This class likely called by a ParallelSubstitutionDetector
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
}
