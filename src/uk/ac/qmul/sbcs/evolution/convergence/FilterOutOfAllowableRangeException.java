package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * 
 * @author joeparker
 * @since 0.0.1 r77
 * @see AlignedSequenceRepresentation
 * This class is thrown whenever the filterForMissingData() method in AlignedSequenceRepresentation is called with inappropriate filter. 
 * The allowable filter bounds are:
 * 	0<filter²100 	- when using filterFactor = true;
 *  0<numberOfTaxa 	- when using filterFactor = false (e.g., threshold no. of gaps per site.
 */
public class FilterOutOfAllowableRangeException extends Exception {

}
