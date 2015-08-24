package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * 
 * @author joeparker
 * Custom exception for simulateConvergence() method in AlignedSequenceRepresentation, for when the number of variant sites available on which to simulate convergence is fewer than the total number required.
 */
public class VariantSitesUnavailableException extends Exception {

	public VariantSitesUnavailableException() {
		// TODO Auto-generated constructor stub
	}

	public VariantSitesUnavailableException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public VariantSitesUnavailableException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public VariantSitesUnavailableException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
