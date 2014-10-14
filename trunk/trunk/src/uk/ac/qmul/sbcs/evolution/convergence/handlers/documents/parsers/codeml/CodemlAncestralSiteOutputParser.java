package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.io.File;
import java.util.HashMap;

/**
 * Class to parse the convergentSites.out output from the modified codeml-ancestral package by	Jason De Koning at evolutionarygenomics.com / U Colorado, and used in Castoe <i>et al.</i> (2009)'s paper on convergence in agamid lizards.
 * <p>Constructor should be called with a single {@link java.io.File} argument, this being the location of the convergentSites.out file. The constructor will then build a hash of node comparison IDs and site convergent/divergent probability data. This can then be polled by the getData() method.</p>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/ancestral.html">evolutionarygenomics.com</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/codeMLancestral/README.txt">codeml-ancestral README</a>
 */
public class CodemlAncestralSiteOutputParser {
	// A class to parse the sites info from codeml-ancestral
	// By 'sites info' we mean the '' file from the codeml-ancestral analysis
	
	public final File inputFile;		// the convergentSites.out file from codeml-ancestral
	public HashMap<String,String> data; // a hashmap of the parsed data, key as pairs of nodes (branch comparisons) in the form 'A_B' where A and B are branch IDs in the 'from..to' notation.


	/**
	 * Default no-arg constructor. Deprecated. Use CodemlAncestralSiteOutputParser(File input) instead.
	 */
	@Deprecated
	public CodemlAncestralSiteOutputParser(){
		this.inputFile = null;
	}
	
	public CodemlAncestralSiteOutputParser(File input){
		this.inputFile = input;
	}
}
