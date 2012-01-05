package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * 
 * @author joeparker
 * Custom exception for simulateConvergence() method in AlignedSequenceRepresentation, for when simulateConvergence() is asked to converge sites in taxa that aren't in the alignment.
 */
public class TaxaAbsentFromAlignmentException extends Exception {

	public TaxaAbsentFromAlignmentException() {
		// TODO Auto-generated constructor stub
	}

	public TaxaAbsentFromAlignmentException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TaxaAbsentFromAlignmentException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public TaxaAbsentFromAlignmentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
