package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileNotFoundException;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaAbsentFromAlignmentException;
import uk.ac.qmul.sbcs.evolution.convergence.VariantSitesUnavailableException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

public class ConvergenceForcerAA {

	/**
	 * 'Simulate' convergence by translating, and then forcing sites that are currently invariant across the alignment to have parallel AA substitutions in two or more taxa.
	 * Gaps and stop codons are not selected; other AAs are selected at random.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		if(args.length > 3){
			File input = new File(args[0]);
			int numberOfSitesToConverge = Integer.parseInt(args[1]);
			if(input.canRead()){
				String first = args[2]; 
				String[] otherTaxa = new String[args.length-3];
				for(int i=3;i<args.length;i++){
					otherTaxa[i-3] = args[i];
				}
			AlignedSequenceRepresentation data = new AlignedSequenceRepresentation();
			try {
				data.loadSequences(input, false);
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				data.translate(true);
			} catch (SequenceTypeNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				data.simulateConvergenceInInvariantSites(first, otherTaxa, numberOfSitesToConverge);
			} catch (TaxaAbsentFromAlignmentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VariantSitesUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.writePhylipFile(input.getAbsoluteFile()+"converge_"+numberOfSitesToConverge+".phy", true);
			}else{
				throw new FileNotFoundException();
			}
		}
	}

}
