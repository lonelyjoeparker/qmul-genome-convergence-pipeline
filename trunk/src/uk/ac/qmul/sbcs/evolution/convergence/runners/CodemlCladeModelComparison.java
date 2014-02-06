package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.CodemlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CladeModelOutput;

public class CodemlCladeModelComparison {

	CodemlResultReader modelM1a_output;
	CodemlResultReader modelMC_output;
	CladeModelOutput modelC_rstFile;
	boolean printAllSites = false;			// print sitewise omegas?
	boolean printDivSites = false;			// print sitewise omegas (divergent class only)?
	
	/**
	 * Constructor method to set up an analysis testing the LRT of ModelC : Model M1a, and return the site probabilities.
	 * <br/>nb. the analyse() method should be called after this.
	 * @param m1a_output	- the output from the Model M1a (null) model
	 * @param modelC_output	- the output from the Model C (clade selection) model
	 * @param rst_file		- the output from the Model C (clade selection) sitewise probabilities, 'rst' by default in codeml.
	 */
	public CodemlCladeModelComparison(File m1a_output, File modelC_output, File rst_file){
		this.modelM1a_output = new CodemlResultReader(m1a_output);
		this.modelMC_output = new CodemlResultReader(modelC_output);
		this.modelC_rstFile = new CladeModelOutput(rst_file);
	}
	
	/**
	 * Constructor method to set up an analysis testing the LRT of ModelC : Model M1a, and return the site probabilities. Overloaded to specify whether sites should be printed or not.
	 * <br/>nb. the analyse() method should be called after this.
	 * @param m1a_output	- the output from the Model M1a (null) model
	 * @param modelC_output	- the output from the Model C (clade selection) model
	 * @param rst_file		- the output from the Model C (clade selection) sitewise probabilities, 'rst' by default in codeml.
	 * @param printSites	- print site omegas?
	 * @param printDiv		- print site omegas (divergent sites only)?
	 */
	public CodemlCladeModelComparison(File m1a_output, File modelC_output, File rst_file, boolean printSites, boolean printDiv){
		this.modelM1a_output = new CodemlResultReader(m1a_output);
		this.modelMC_output = new CodemlResultReader(modelC_output);
		this.modelC_rstFile = new CladeModelOutput(rst_file);
		this.printAllSites = printSites;
		this.printDivSites = printDiv;
	}

	/**
	 * Class to set up an analysis testing the LRT of ModelC : Model M1a, and return the site probabilities.
	 * <br/>nb. Doesn't actually <i>run</i> codeml itself; just parses. Relies on the M1a.out, MC.out and rst files existing...
	 * @param args - the M1a.out, MC.out and rst files
	 * @throws Exception - if ­ 3 args are passed.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if(args.length == 3){
			File modelM1a_output = new File(args[0]);
			File modelMC_output = new File(args[1]);
			File modelC_rstFile = new File(args[2]);
			new CodemlCladeModelComparison(modelM1a_output,modelMC_output,modelC_rstFile).analyse();
		}else if(args.length == 5){
			File modelM1a_output = new File(args[0]);
			File modelMC_output = new File(args[1]);
			File modelC_rstFile = new File(args[2]);
			new CodemlCladeModelComparison(modelM1a_output,modelMC_output,modelC_rstFile,Boolean.parseBoolean(args[3]),Boolean.parseBoolean(args[4])).analyse();
		}else{
			throw new Exception("Incorrect arguments specified.\n");
		}
	}

	/**
	 * Method to analyse the codeml outputs. Assuming they all exist, and represent the Model M1a results (model fit), Model C results (model fit), and Model C sitewise BEB probabilites, this method will:
	 * <ul>
	 * 	<li>Get the lnL scores of both models
	 * 	<li>Perform the LRT on both with the test statistic 2*Æl, and critical values from Yang (2007) of:
	 * 		<br/> 2.71 and 5.41 at the 5% and 1% significance levels, respectively) (Self and Liang 1987). 
	 * 		<br/>Some authors (e.g., Zhang, Nielsen, and Yang 2005) also suggested the use of chi-sq(2,1) (with critical values to be 3.84 and 5.99)
	 *	<li>Print the estimated mean omega, omega in the divergent (2nd) site class, 
	 *	<li>Print the sites under selection
	 * </ul>
	 */
	private void analyse() {
		// TODO Auto-generated method stub
		modelM1a_output.getOmegaVal();
		modelMC_output.getOmegaVal();
		modelC_rstFile.getAllSiteOmegas();
		
		double[] dNdS = modelC_rstFile.getdNdS();
		double modelC_omega = dNdS[2];
		System.out.println(modelC_omega);

		// print the site omegas if allsites is set
		if(printAllSites){
			HashMap<Integer, Double> allSites = modelC_rstFile.getAllSiteOmegas();
			Iterator<Integer> siteItr = allSites.keySet().iterator();
			while(siteItr.hasNext()){
				int site = siteItr.next();
				System.out.println("\t"+ site + "\t" + allSites.get(site));
			}
		}

		// print the site omegas if divsites is set
		if(printDivSites){
			HashMap<Integer, Double> divSites = modelC_rstFile.getDivergentSiteOmegas();
			Iterator<Integer> siteItr = divSites.keySet().iterator();
			while(siteItr.hasNext()){
				int site = siteItr.next();
				System.out.println("\t"+ site + "\t" + divSites.get(site));
			}
		}
	}

}
