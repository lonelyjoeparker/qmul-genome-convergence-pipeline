package uk.ac.qmul.sbcs.evolution.convergence.util;

public class StringRemover {
	public final String finishedSequence;

	public StringRemover(String input, char target){
		StringBuffer buffer = new StringBuffer();
		for(char c:input.toCharArray()){
			if(c != target){
				buffer.append(c);
			}
		}
		finishedSequence = buffer.toString();
	}
}
