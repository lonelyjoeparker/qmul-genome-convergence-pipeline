package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;

public class CladeModelOutput {
	
	File sitesOmegas;
	ArrayList<String> rawData;
	double[] dNdS = new double[3];
	HashMap<Integer,Double> siteOmegas = new HashMap<Integer,Double>();
	HashMap<Integer,Double> siteDivergent = new HashMap<Integer,Double>();
	
	/**
	 * No-arg constructor. Deprecated.
	 */
	@Deprecated
	public CladeModelOutput(){}
	
	/**
	 * Single-arg constructor for CladeModelOutput; this will automatically parse the supplied 'rst' file when a new instance is created.
	 * @param rstfile - a {@link File} containing the results from a PAML Clade Model C analysis (normally 'rst').
	 */
	public CladeModelOutput(File rstfile){
		this.sitesOmegas = rstfile;
		rawData = new BasicFileReader().loadSequences(rstfile,false,true);
		this.readRawData();
	}
	
	/**
	 * Getter method for the estimated site omega (dN/dS) ratios, for sites under <i>divergent</i> selection only.
	 * <br/>These are calculated from PAML as the product of the category BEB probabilities * the category dN/dS estimates.
	 * <br/>Differs from {@link CladeModelOutput#getAllSiteOmegas()} in that only sites in third (divergent) site category BEB ³ 0.50 are returned.
	 * @return {@link HashMap} <{@link Integer},{@link Double}>: a HashMap containing <i>divergent</i> site indices and their calculated product omegas.
	 * @see {@link CladeModelOutput#getAllSiteOmegas()}
	 */
	public HashMap<Integer,Double> getDivergentSiteOmegas(){
		return this.siteDivergent;
	}
	
	/**
	 * Getter method for the estimated site omega (dN/dS) ratios.
	 * <br/>These are calculated from PAML as the product of the category BEB probabilities * the category dN/dS estimates.
	 * @return {@link HashMap} <{@link Integer},{@link Double}>: a HashMap containing site indices and their calculated product omegas.
	 * @see {@link CladeModelOutput#getDivergentSiteOmegas()}
	 */
	public HashMap<Integer,Double> getAllSiteOmegas(){
		return this.siteOmegas;
	}

	/**
	 * Private parsing script called by constructor. Assumes 'rst' file from PAML 4.4b.
	 */
	private void readRawData(){
		boolean inBEB = false;
		Pattern p_BEB = Pattern.compile("BEB"); 
		Pattern p_num = Pattern.compile("^[\\ ]{1,}[0-9]{1,}");
		Pattern rates = Pattern.compile("BRANCH\\ TYPE\\ 1");
		Pattern positive = Pattern.compile("3\\)");
		
		for(String line:rawData){
			Matcher isBEB = p_BEB.matcher(line);
			Matcher isNum = p_num.matcher(line);
			Matcher isRst = rates.matcher(line);
			Matcher isPos = positive.matcher(line);
			
			if(isNum.find() && inBEB && line.length()>2){
				// We are in a data line, get probabilities
				String[] tokens = line.split("\\ {1,}");
				double[] probabilities = new double[3];
				probabilities[0] = Double.parseDouble(tokens[3]);
				probabilities[1] = Double.parseDouble(tokens[4]);
				probabilities[2] = Double.parseDouble(tokens[5]);
				double w = (probabilities[0]*dNdS[0])+(probabilities[1]*dNdS[1])+(probabilities[2]*dNdS[2]);
				Integer siteIndex = Integer.parseInt(tokens[1])-1;
				siteOmegas.put(siteIndex, w);
				if(isPos.find()){
					siteDivergent.put(siteIndex, w);
				}
			}
			if(isBEB.find()){inBEB=true;}
			if(isRst.find()){
				String[] tokens = line.split("\\ {1,}");
				dNdS[0] = Double.parseDouble(tokens[3]);
				dNdS[1] = Double.parseDouble(tokens[4]);
				dNdS[2] = Double.parseDouble(tokens[5]);
			}
		}
	}

	public double[] getdNdS() {
		// TODO Auto-generated method stub
		return this.dNdS;
	}
}
