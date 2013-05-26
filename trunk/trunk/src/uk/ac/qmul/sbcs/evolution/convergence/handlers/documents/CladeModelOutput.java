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
	 * No-arg constructor
	 */
	@Deprecated
	public CladeModelOutput(){}
	
	public CladeModelOutput(File rstfile){
		this.sitesOmegas = rstfile;
		rawData = new BasicFileReader().loadSequences(rstfile,false,true);
		this.readRawData();
	}
	
	public HashMap<Integer,Double> getDivergentSiteOmegas(){
		return this.siteDivergent;
	}
	
	public HashMap<Integer,Double> getAllSiteOmegas(){
		return this.siteOmegas;
	}

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
}
