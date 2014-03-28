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
				System.out.println(
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
					System.err.print("\tE:");
					for(float entropy:siteEntropies){
						System.err.print("\t"+entropy);
					}
				}
				System.err.println();
				
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
			System.out.println(
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
				System.err.print("\tE:");
				for(float entropy:siteEntropies){
					System.err.print("\t"+entropy);
				}
			}
			System.err.println();
		}
	}

}
