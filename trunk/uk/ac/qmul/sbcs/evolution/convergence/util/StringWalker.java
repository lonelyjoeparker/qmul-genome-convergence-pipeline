package uk.ac.qmul.sbcs.evolution.convergence.util;

public class StringWalker {
	public final String finishedSequence;
	
	/**
	 * 
	 * @param input: the candidate string
	 * @param target: the character to replace
	 * @param replacement: the character to replace target with
	 */
	public StringWalker(String input, char target, char replacement){
		char[] charArray = input.toCharArray();
		for(int i=0;i<charArray.length;i++){
			if(charArray[i] == target){
				charArray[i] = replacement;
			}
		}
		finishedSequence = new String(charArray);
	}
	
	/**
	 * 
	 * @param charArray: the candidate char array
	 * @param target: the character to replace
	 * @param replacement: the character to replace target with
	 */
	public StringWalker(char[] charArray, char target, char replacement){
		for(int i=0;i<charArray.length;i++){
			if(charArray[i] == target){
				charArray[i] = replacement;
			}
		}
		finishedSequence = new String(charArray);
	}

	/**
	 * 
	 * @param input: the candidate string
	 * @param target: the character to replace
	 * @param replacement: the character to replace target with
	 * @param boundingChars: the characters that bound the region of the string over which to do the replacement
	 */
	public StringWalker(String input, char target, char replacement, char boundingChars){
		char[] charArray = input.toCharArray();
		boolean inRegion = false;
		for(int i=0;i<charArray.length;i++){
			if((charArray[i] == target) && inRegion){
				charArray[i] = replacement;
			}
			if(charArray[i] == boundingChars){
				if(inRegion){
					inRegion = false;
				}else{
					inRegion = true;
//					System.out.println("in region");
				}
			}
		}
		finishedSequence = new String(charArray);
	}
	
	public StringWalker(String input, char[] targets, char replacement){
		finishedSequence = input;
		// TODO implement iteration over all targets (use java.util.regex.* etc)
	}
	
}
