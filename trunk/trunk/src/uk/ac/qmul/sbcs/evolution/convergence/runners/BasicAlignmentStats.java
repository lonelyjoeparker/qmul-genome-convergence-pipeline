package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
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
	
	public BasicAlignmentStats(String string) {
		// TODO Auto-generated constructor stub
		this.inputFile = new File(string);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BasicAlignmentStats quick = new BasicAlignmentStats(args[0]);
		if(args.length>1){
			quick.doSitewiseEntropy = Boolean.parseBoolean(args[1]);
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

}
