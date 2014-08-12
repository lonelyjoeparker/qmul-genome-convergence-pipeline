package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class invoked to use the EnsembleCodeGuesser.guess() method functionality to detect Ensembl (ENSG) codes from a filename or string.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public final class EnsemblCodeGuesser {

	/**
	 * No-arg constructor, should never be used anyway.
	 */
	public EnsemblCodeGuesser(){}
	
	public static String guess(java.io.File input){
		return EnsemblCodeGuesser.guess(input.getName());
	}
	
	/**
	 * Parses an input string to see if it contains a substring which could be an Ensembl code; here defined as a 15-digit alphanumeric starting 'ENSG...'
	 * <br>Attempts to split the string up into tokens based on '_', '.' or ',' chars and then tests the tokens to see if they match.
	 * @param input - the input (query) string
	 * @return ensemblCode - the code, if found, or null, if not.
	 */
	public static String guess(String input){
		String ensemblCode = null;
		Pattern tokenSplit = Pattern.compile("ENSG[0-9A-Za-z]+");
		Matcher tokenMatch = tokenSplit.matcher(input);
		while(tokenMatch.find()){
			ensemblCode = tokenMatch.group();
			ensemblCode.charAt(0);
		}
		
		return ensemblCode;
	}
}
