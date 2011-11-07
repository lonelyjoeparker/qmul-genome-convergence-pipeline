package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;

/**
 * 
 * @author - Joe Parker
 * @since - 02/11/2011
 * @version - 0.0.1
 * This class provides <b>limited</b> support for a Newick tree file and associated I/O.
 * It assumes one single tree on one line (the first) of the file.
 * The main purpose is to provide functionality to prune phylogenetic trees through the pruneTaxon() method.
 * <p>Note that the pruneTaxon() method simply uses regular expressions to parse the tree string; it does <b>NOT</b> create (or rely on) a true Newick tree repsesentation as a series of nested nodes, with associated traversal operations etc. 
 * In particular there is no guarantee that non-standard treefile formats will be correctly parsed, including BEAST and MrBayes trees.
 */
public class NewickTreeRepresentation {
	private String treeString = null;
	private File treeFile = null;
	private int numberOfTaxa = 0;
	private TreeSet<String> taxaNames = null;
	
	@Deprecated
	/**
	 * WARNING this constructor should not be used at present, as it relies on the internal private obtainTaxaNames() method to get the taxaNames list that is needed for a safe pruneTaxon() call.
	 * At the moment this method is not implemented, and so this constructor is marked as deprecated.
	 * @param tree
	 */
	public NewickTreeRepresentation(){}
	
	public NewickTreeRepresentation(File inputFile, TreeSet<String> names){
		this.treeFile = inputFile;
		this.treeString = new BasicFileReader().loadSequences(treeFile,true).get(0);
		this.taxaNames = names;
		this.numberOfTaxa = taxaNames.size();
	}
	
	@Deprecated
	/**
	 * WARNING this constructor should not be used at present, as it relies on the internal private obtainTaxaNames() method to get the taxaNames list that is needed for a safe pruneTaxon() call.
	 * At the moment this method is not implemented, and so this constructor is marked as deprecated.
	 * @param tree
	 */
	public NewickTreeRepresentation(File inputFile){
		this.treeFile = inputFile;
		this.treeString = new BasicFileReader().loadSequences(treeFile,true).get(0);
		this.taxaNames = this.obtainTaxaNames(treeString);
		this.numberOfTaxa = taxaNames.size();
	}
	
	public NewickTreeRepresentation(String tree, TreeSet<String> names){
		this.treeString = tree;
		this.taxaNames = names;
		this.numberOfTaxa = taxaNames.size();
	}

	@Deprecated
	/**
	 * WARNING this constructor should not be used at present, as it relies on the internal private obtainTaxaNames() method to get the taxaNames list that is needed for a safe pruneTaxon() call.
	 * At the moment this method is not implemented, and so this constructor is marked as deprecated.
	 * @param tree
	 */
	public NewickTreeRepresentation(String tree){
		this.treeString = tree;
		this.taxaNames = this.obtainTaxaNames(treeString);
		this.numberOfTaxa = taxaNames.size();
	}
	
	public String getTreeString() {
		return treeString;
	}

	public File getTreeFile() {
		return treeFile;
	}

	public int getNumberOfTaxa() {
		return numberOfTaxa;
	}

	public TreeSet<String> getTaxaNames() {
		return taxaNames;
	}

	public void write(){
		if(this.treeFile == null){
			this.treeFile = new File(System.getProperty("user.dir")+"/NewickTreeRepresentation"+System.currentTimeMillis()+".tre");
		}
		new BasicFileWriter(this.treeFile, this.treeString);
	}
	
	public void write(File outputTreeFile){
		new BasicFileWriter(outputTreeFile, this.treeString);
	}
	
	public void setTreeFile(File newTreeFile){
		this.treeFile = newTreeFile;
	}
	
	@Deprecated
	private TreeSet<String> obtainTaxaNames(String inputTreeString){
		assert(false);
		// FIXME this method is used by both single-arg constructors - they are marked deprecated until this method is implemented.
		// TODO implement this.
		TreeSet<String> names = new TreeSet<String>();
		return names;
	}
	
	public void printSimply(){
		System.out.println(this.treeString);
	}
	
	public void pruneTaxon(String taxonToPrune) throws TaxonNotFoundError{
		assert(this.taxaNames.contains(taxonToPrune));
		System.out.println("Attempting to prune taxon "+taxonToPrune);
		if(this.taxaNames.contains(taxonToPrune)){
			String regex = taxonToPrune+":[0-9|\\.]{1,}";
			treeString = treeString.replaceFirst(regex, "");
			System.out.println("tst "+treeString);
			boolean replacementNeeded = true;
			while(replacementNeeded){
				int editsThisPass = 0;
				regex = "\\(\\)";
				String tmp = treeString.replaceAll(regex, "");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}
				
				regex = "\\,\\,";
				tmp = treeString.replaceAll(regex, ",");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}
				
				regex = "\\(\\,";
				tmp = treeString.replaceAll(regex, "(");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}

				regex = "\\,\\)";
				tmp = treeString.replaceAll(regex, ")");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}
				
				regex = "\\(:[0-9|\\.]{1,}\\,";
				tmp = treeString.replaceAll(regex, "(");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}

				regex = "\\,:[0-9|\\.]{1,}\\)";
				tmp = treeString.replaceAll(regex, ")");
				if(tmp != treeString){
					this.treeString = tmp;
					System.out.println("tmp"+tmp);
					System.out.println("tst"+treeString);
					editsThisPass++;
				}

				/* This may be the most complicated edit:
				 * Look for whole taxa (incl. branch lengths) surrounded by brackets.
				 * Remove brackets
				 * Replace .... 
				 */
				System.out.println("CHECK: orphan taxa");
				regex = "\\([A-Za-z|\\_|\\-]{1,}:[0-9|\\.]{1,}\\)";
		//		regex = "Bos";
//				String regex2 = "\\([A-Za-z|\\_|\\-]{1,}:[0-9|\\.]{1,}\\)";
				Pattern orphan = Pattern.compile(regex);
				Matcher orphanMatch = orphan.matcher(this.treeString);
				if(orphanMatch.find()){
					String foundOrphan = orphanMatch.group();
					System.out.println("WARNING! detected orphan taxon "+foundOrphan+", paml may have trouble: tmp"+tmp);
					tmp = treeString.replaceAll(regex, foundOrphan.substring(1,foundOrphan.length()-2)); //set with an arbitrary branch length...
					this.treeString = tmp;
					System.out.println("tst"+treeString);
					editsThisPass++;
//					tmp = treeString; //just for now, reset tmp just in case...
				}
				System.out.println("NO: orphan taxa");

				if(editsThisPass < 1){
					replacementNeeded = false;
					System.out.println("Done pruning tree");
				}
			}
		}else{
			throw new TaxonNotFoundError();
		}
	}
}
