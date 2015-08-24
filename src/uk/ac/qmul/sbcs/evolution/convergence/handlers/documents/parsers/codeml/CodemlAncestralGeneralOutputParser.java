package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.BranchPairComparison;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;

/**
 * Class to parse the codeml-anc.out output from the modified codeml-ancestral package by	Jason De Koning at evolutionarygenomics.com / U Colorado, and used in Castoe <i>et al.</i> (2009)'s paper on convergence in agamid lizards.
 * <p>Constructor should be called with a single {@link java.io.File} argument, this being the location of the codeml-anc.out file. The constructor will then parse the taxon list and phylogeny information from the file.</p>
 * <p>Note that the branch-pair convergent/divergent site probabilities comparison info is pulled from convergentSites.out by {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser}</p>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/ancestral.html">evolutionarygenomics.com</a>
 * @see <a href="http://www.evolutionarygenomics.com/codeml_ancestral/codeMLancestral/README.txt">codeml-ancestral README</a>
 * @see {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml}
 * @see {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser}
 */
public class CodemlAncestralGeneralOutputParser {
	public final File inputFile;		// the convergentSites.out file from codeml-ancestral
	public ArrayList<String> data; 		// a array of the input file data
	public TreeNode phylogeny; 			// a phylogeny which this data has been drawn from
	public TreeSet<String> inferredTaxonSet;		// the set of all taxa present in the tree, inferred from the tree string
	public HashMap<String,Integer> inferredTaxonNamesIDMap;	// the mapping from taxon names to ID numbers, assuming alphabetical ordering

	/**
	 * Default no-arg constructor. Deprecated. Use CodemlAncestralSiteOutputParser(File input) instead.
	 */
	@Deprecated
	public CodemlAncestralGeneralOutputParser(){
		this.inputFile = null;
		this.data = null;
		this.phylogeny = null;
	}

	public CodemlAncestralGeneralOutputParser(File input){
		this.inputFile = input;
		this.phylogeny = null;
		// load input file data as ArrayList<String>
		data = new BasicFileReader().loadSequences(inputFile, false, true);
		// iterate through loaded data
		Pattern treePattern = Pattern.compile("[\\(\\)\\,\\:A-Za-z0-9\\ \\s]+;");
		String lastTree = null;
		for(String line:data){
			Matcher treeMatch = treePattern.matcher(line);
			if(treeMatch.find()){
				// there is a tree here
				lastTree = line.replaceAll("\\s", "");
			}
		}
		if(lastTree != null){
			phylogeny = new TreeNode(lastTree,1);
			inferredTaxonSet = inferTaxonSet(lastTree);
			inferredTaxonNamesIDMap = inferTaxonNumberMapping(inferredTaxonSet);
		}
	}
	
	/**
	 * Infers a TreeSet of taxa from the tree (phylogeny) string. No guarantee that this is complete or accurate..
	 * @return TreeSet<String> of all alphanumeric strings in the phylogeny.
	 */
	public static TreeSet<String> inferTaxonSet(String phylogenyString) {
		Pattern alphabet = Pattern.compile("[A-Za-z]+");
		Matcher nameMatch = alphabet.matcher(phylogenyString);
		TreeSet<String> possibleTaxonSet = new TreeSet<String>();
		while(nameMatch.find()){
			possibleTaxonSet.add(nameMatch.group());
		}
		return possibleTaxonSet;
	}

	/**
	 * Infers a mapping from taxon strings to numbering IDs, assuming alphabetical ordering
	 * @return HashMap<String,Integer> mapping taxon strings to unique ID numbers;
	 */
	public static HashMap<String,Integer> inferTaxonNumberMapping(TreeSet<String> taxonSet){
		HashMap<String,Integer> returnTaxonNumberingMap = new HashMap<String,Integer>();
		Iterator<String> itr = taxonSet.iterator();
		int uid = 0;
		while(itr.hasNext()){
			uid++;
			returnTaxonNumberingMap.put(itr.next(), uid);
		}
		return returnTaxonNumberingMap;
	}
	
	/**
	 * @return the phylogeny
	 */
	public TreeNode getPhylogeny() {
		return phylogeny;
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @return the data as read from input file
	 */
	public ArrayList<String> getData() {
		return data;
	}

	/**
	 * @return the inferred TaxonSet - guessed as the set of all alphabetic groups in the tree string
	 */
	public TreeSet<String> getInferredTaxonSet() {
		return inferredTaxonSet;
	}

	/**
	 * @return the inferred taxonNamesIDMap - guessed as the alphabetic order of the inferred taxon set 
	 */
	public HashMap<String, Integer> getTaxonNamesIDMap() {
		return inferredTaxonNamesIDMap;
	}
}
