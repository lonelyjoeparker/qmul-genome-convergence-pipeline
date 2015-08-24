package uk.ac.qmul.sbcs.evolution.convergence;

public class SequenceTypeNotSupportedException extends Exception {
	public SequenceTypeNotSupportedException(){
		System.out.println("This sequence type is not supported for this operation.");
	}
	public SequenceTypeNotSupportedException(SequenceCodingType required, SequenceCodingType actual){
		System.out.println("This sequence type ("+actual+") is not supported for this operation (expected "+required+").");
	}
}
