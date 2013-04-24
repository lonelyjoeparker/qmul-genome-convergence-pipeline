package uk.ac.qmul.sbcs.evolution.sandbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSandbox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(String arg:args){
			Pattern somePattern = Pattern.compile("phy+");
			Matcher match = somePattern.matcher(arg);
			while(match.find()){
				System.out.println("Ohh-kayyyyyyyy..... ("+arg+") "+match.group());
			}
		}
	}

}
