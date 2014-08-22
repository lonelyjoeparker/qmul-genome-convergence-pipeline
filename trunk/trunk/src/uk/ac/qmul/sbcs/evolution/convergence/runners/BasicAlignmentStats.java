package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.HashMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

/** 
 * A utility class to provide a one-line summary of input alignment files
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class BasicAlignmentStats {

	/**
	 * @param args - a single input file
	 */
	private File inputFile;
	private AlignedSequenceRepresentation data;
	private boolean doSitewiseEntropy = false;
	private boolean doSharedSubsIn = false;
	private boolean doSharedPrivateSubsIn = false;
	private boolean doSharedPrivateSubsInSubset = false;
	private String[] focusTaxa = null;
	private String[] subsetTaxa = null;
	
	public BasicAlignmentStats(String string) {
		// TODO Auto-generated constructor stub
		this.inputFile = new File(string);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BasicAlignmentStats quick = new BasicAlignmentStats(args[0]);
		
		if(args.length>1){
			if(args[1].equals("shared")){
				quick.doSharedSubsIn = true;
			}else if(args[1].equals("private")){
				quick.doSharedPrivateSubsIn  = true;
			}else if(args[1].equals("subset")){
				quick.doSharedPrivateSubsInSubset  = true;
				// get the focus and subset taxon lists from args[2] and args[3]
				quick.focusTaxa = args[2].split("\\|");
				quick.subsetTaxa = args[3].split("\\|");
			}else{
				quick.doSitewiseEntropy = Boolean.parseBoolean(args[1]);
			}
		}
		quick.go();
	}
	
	private void go() {
		// TODO Auto-generated method stub
		if(inputFile.isDirectory()){
			File[] children = inputFile.listFiles();
			for(File child:children){
				data = new AlignedSequenceRepresentation();
				float[] siteEntropies = null;
				try {
					data.loadSequences(child, false);
					data.calculateAlignmentStats(false);
					if(this.doSitewiseEntropy){
						siteEntropies = data.getSitewiseEntropies(true);
					}
					if(this.doSharedSubsIn||this.doSharedPrivateSubsIn||this.doSharedPrivateSubsInSubset){
						if (!data.isAA()) {
							try {
								data.translate(true);
							} catch (SequenceTypeNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(this.doSharedSubsIn){
							data.printSharedSubs();
						}else if(this.doSharedPrivateSubsIn){
							data.printSharedPrivateSubs();
						}else{
							this.printSubsetOfSharedSubsWithFocus(this.focusTaxa, this.subsetTaxa,child);
						}
					}
				} catch (TaxaLimitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.print(
						child.getName()+"\t"+
						data.getNumberOfSites()+"\t"+
						data.getNumberOfTaxa()+"\t"+
						data.getNumberOfInvariantSites()+"\t"+
						data.getMeanSitewiseEntropy()+"\t"+
						data.getMeanTaxonwiseLongestUngappedSequence()
						);
				System.err.print(
						child.getName()+"\t"+
						data.getNumberOfSites()+"\t"+
						data.getNumberOfTaxa()+"\t"+
						data.getNumberOfInvariantSites()+"\t"+
						data.getMeanSitewiseEntropy()+"\t"+
						data.getMeanTaxonwiseLongestUngappedSequence()
						);
				if((this.doSitewiseEntropy)&&(siteEntropies != null)){
					float [] entropyStats = this.parseEntropiesToFindLongestNonzeroRun(siteEntropies);
					System.out.print("\t"+entropyStats[0]+"\t"+entropyStats[1]);
					System.err.print("\t"+entropyStats[0]+"\t"+entropyStats[1]);
					System.out.print("\tE:");
					for(float entropy:siteEntropies){
						System.out.print("\t"+entropy);
					}
				}
				System.err.println();
				System.out.println();
				
			}
		}else{
			data = new AlignedSequenceRepresentation();
			float[] siteEntropies = null;
			try {
				data.loadSequences(inputFile, false);
				data.calculateAlignmentStats(false);
				if(this.doSitewiseEntropy){
					siteEntropies = data.getSitewiseEntropies(true);
				}
				if(this.doSharedSubsIn||this.doSharedPrivateSubsIn||this.doSharedPrivateSubsInSubset){
					if (!data.isAA()) {
						try {
							data.translate(true);
						} catch (SequenceTypeNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(this.doSharedSubsIn){
						data.printSharedSubs();
					}else if(this.doSharedPrivateSubsIn){
						data.printSharedPrivateSubs();
					}else{
						this.printSubsetOfSharedSubsWithFocus(this.focusTaxa, this.subsetTaxa, this.inputFile);
					}
				}
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(
					inputFile.getName()+"\t"+
					data.getNumberOfSites()+"\t"+
					data.getNumberOfTaxa()+"\t"+
					data.getNumberOfInvariantSites()+"\t"+
					data.getMeanSitewiseEntropy()+"\t"+
					data.getMeanTaxonwiseLongestUngappedSequence()
					);
			System.err.print(
					inputFile.getName()+"\t"+
					data.getNumberOfSites()+"\t"+
					data.getNumberOfTaxa()+"\t"+
					data.getNumberOfInvariantSites()+"\t"+
					data.getMeanSitewiseEntropy()+"\t"+
					data.getMeanTaxonwiseLongestUngappedSequence()
					);
			if((this.doSitewiseEntropy)&&(siteEntropies != null)){
				float [] entropyStats = this.parseEntropiesToFindLongestNonzeroRun(siteEntropies);
				System.err.print("\t"+entropyStats[0]+"\t"+entropyStats[1]);
				System.out.print("\t"+entropyStats[0]+"\t"+entropyStats[1]);
				System.out.print("\tE:");
				for(float entropy:siteEntropies){
					System.out.print("\t"+entropy);
				}
			}
			System.out.println();
			System.err.println();
		}
	}
	
	/**
	 *
	 * @param entropies - float[] of sitewise entropy (diversity) stats from an alignment
	 * @return retval - a float[] containing: [0] longest run of identical and nonzero entropies; [1] what the value of those entropies was
	 */
	public static float[] parseEntropiesToFindLongestNonzeroRun(float [] entropies){
		// Initialise vars
		float [] retval = {0.0f,0.0f};		// return array (doing this explicitly)
		int longestNonZero = 0;				// globally-longest run of identical nonzero entropies
		int currentNonZeroLength = 0;		// current (locally) longest run of identical nonzero entropies
		float whichNonZero = 0.0f;			// value of current nonzero entropies in current run (should update to global on completion)
		float last = 0.0f;					// previous entropy seen

		// iterate through sites
		for(float entropy:entropies){
			if(entropy == 0.0f){
				// this entropy is zero which will terminate any nonzero runs so far
				if(currentNonZeroLength > longestNonZero){
					longestNonZero = currentNonZeroLength;	// there is a previous run to update LNZ with
					whichNonZero = last;
				}
				currentNonZeroLength = 0;
			}else{
				// this entropy is nonzero.
				// update LNZ so far
				if(currentNonZeroLength > longestNonZero){
					longestNonZero = currentNonZeroLength;	// there is a previous run to update LNZ with
				}
				// find out if the current entropy is part of current run, or a new run
				if(entropy == last){
					// is part of current run
					currentNonZeroLength++;
					whichNonZero = last;
				}else{
					// this entropy is starting a new run, reset CNZ counter
					currentNonZeroLength = 0;
				}
			}
			last = entropy;		// set the last entropy marker
		}
		
		// Assign the return values to the retval array
		retval[0] = (float) longestNonZero;
		retval[1] = whichNonZero;
		return retval;
	}

	/**
	 * Does a private shared amino acids comparison between two sets of taxa; one set (the <i>focus</i> set) 
	 * being compared simultaneously - that is, substitutions must be exclusive to either/each of the focal set;
	 * and a separate subset of the aligned taxa (the <i>subset set</i>) which are compared iteratively, e.g.
	 * for each in turn the others are removed from the alignment.
	 * 
	 * <p>TODO currently assumes focus taxa n=2. Generalise for any n<numberOfTaxa.</p>
	 * 
	 * @param focusTaxa - taxa for simultaneous pairwise private shared sites comparison (e.g. 'bos','taurus')
	 * @param subsetTaxa - taxa to be iteratively compared in isolation (e.g. for each n, all but n removed)
	 */
	private void printSubsetOfSharedSubsWithFocus(String[] focusTaxa, String[] subsetTaxa, File originalInput){
		// storing the masked alignments in a hashset to aid debugging...
		HashMap<String,AlignedSequenceRepresentation> downsampledAlignments = new HashMap<String,AlignedSequenceRepresentation>();
		// create the subsets
		for(String subsetTaxonInTurn:subsetTaxa){
			// clone the alignment
			//AlignedSequenceRepresentation maskedAlignment = data.clone();
			// massive megaballs. cloning isn't working. can we reinstantiate..?
			AlignedSequenceRepresentation maskedAlignment = new AlignedSequenceRepresentation();
			try {
				maskedAlignment.loadSequences(originalInput, false);
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			maskedAlignment.calculateAlignmentStats(false);
			try {
				maskedAlignment.translate(true);
			} catch (SequenceTypeNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * mask (remove, as no mask method implemented) all
			 * but the current member of the subset..
			 * .. I know this is bad iteration practice but in a hurry
			 */
			String[] taxaToMask = new String[subsetTaxa.length-1];
			int index = 0;
			for(String subsetTaxonToMask:subsetTaxa){
				if(subsetTaxonToMask != subsetTaxonInTurn){
					taxaToMask[index] = subsetTaxonToMask;
					index++;
				}
			}
			// mask
			maskedAlignment.removeTaxa(taxaToMask);
			// put the masked alignment in hashmap
			downsampledAlignments.put(subsetTaxonInTurn, maskedAlignment);
			
			/*
			 * Now we have a masked alignment, look for shared private
			 * pairwise subs
			 */
			for(String focalTaxon:focusTaxa){
				maskedAlignment.printSharedPrivateSubs(focalTaxon, subsetTaxonInTurn);
			}
		}
	}
}
