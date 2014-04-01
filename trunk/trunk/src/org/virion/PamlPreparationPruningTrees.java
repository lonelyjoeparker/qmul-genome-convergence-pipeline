package org.virion;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TaxonNotFoundError;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.NewickUtilitiesHandler;
import uk.ac.qmul.sbcs.evolution.convergence.runners.GeneralCongruenceRunnerXML;
import uk.ac.qmul.sbcs.evolution.convergence.util.CapitalisedFileReader;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

/**
 * Class to prepare a range of input file formats by stripping stop codons and writing PAML/codeml/RAxML compatible *.phy files out.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * 
 */
public class PamlPreparationPruningTrees {

	/**
	 * @param args
	 */
	private File inputFile;
	private AlignedSequenceRepresentation data;
	public PamlPreparationPruningTrees(String string) {
		// TODO Auto-generated constructor stub
		this.inputFile = new File(string);
	}
	
	/**
	 * Main / entry point
	 * 
	 * @param args : &lt;alignment&gt; &lt;tree&gt; &lt;taxon list (codeml labels)&gt;  &lt;taxon list (no labels)&gt;
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PamlPreparationPruningTrees quick = new PamlPreparationPruningTrees(args[0]);
		quick.go();
		quick.pruneTree(args[1],args[2], args[3]);
	}
	
	private void pruneTree(String inputTreePath, String labelledTaxonListPath, String unlabelledTaxonListPath) {
		TreeSet<String> excludedList = null;
		// TODO Auto-generated method stub
		try {
			excludedList = this.excludedTaxaList(parseTaxaListFromConfigFile(labelledTaxonListPath),parseTaxaListFromConfigFile(unlabelledTaxonListPath), data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NewickTreeRepresentation unprunedMainTrees = new NewickTreeRepresentation(new File(inputTreePath), data.getTaxaList());
		this.pruneTaxa(unprunedMainTrees, excludedList).write(new File(inputTreePath+".pruned"));
	}

	private NewickTreeRepresentation pruneTaxa(NewickTreeRepresentation unprunedTree, TreeSet<String> taxaToPrune){
		File nw_prune_exe = new File("/Applications/Phylogenetics/NewickUtils/newick-utils-1.6/src/nw_prune");
		if(nw_prune_exe.exists()){
			//TODO implement NW_prune for pruning, not my lamo method
			NewickTreeRepresentation prunedTree = new NewickUtilitiesHandler(new File("/Applications/Phylogenetics/NewickUtils/newick-utils-1.6/src/"), unprunedTree.getTreeFile(), data.getTaxaList()).pruneTaxa(taxaToPrune);
			unprunedTree = prunedTree;
		}else{
			Iterator itrTaxon = taxaToPrune.iterator();
			while(itrTaxon.hasNext()){
				String taxonToPrune = (String)itrTaxon.next().toString();
				try {
					unprunedTree.pruneTaxon(taxonToPrune);
					System.out.println("Pruned taxon "+taxonToPrune+" from tree.");
				} catch (TaxonNotFoundError e) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't prune taxon "+taxonToPrune+" from tree.");
					e.printStackTrace();
				}
			}
		}
		return unprunedTree;
	}
	
	/**
	 * 
	 * @param configFileArg - path to file containing a list of possible taxa
	 * @return
	 * @throws Exception 
	 */
	private TreeSet<String> parseTaxaListFromConfigFile(String configFileArg) throws Exception{
		TreeSet<String> taxaList = new TreeSet<String>();
		File configFile = new File(configFileArg);
		if(configFile.canRead()){
			ArrayList<String> taxa = new CapitalisedFileReader().loadSequences(configFile,false);
			for(String taxon:taxa){
				taxaList.add(taxon);
			}
		}else{
			throw new Exception("Unable to parse the taxon list.\n");
		}
		return taxaList;
	}
	
	/**
	 * 
	 * @param fullUnlabelledList - the full list of taxa that might be seen in this pipeline
	 * @param alignment - the actual alignment under test.
	 * @return excludedList - a TreeSet<String> of the taxa that are NOT in the alignment and need to be pruned from any future tree
	 */
	private TreeSet<String> excludedTaxaList(TreeSet<String> fullLabelledList, TreeSet<String> fullUnlabelledList, AlignedSequenceRepresentation alignment){
		TreeSet<String> excludedList = new TreeSet<String>();
		TreeSet<String> includedList = alignment.getTaxaList();
		Iterator itrComp = fullUnlabelledList.iterator();
		while(itrComp.hasNext()){
			String aTaxon = (String) itrComp.next().toString();
			if(!includedList.contains(aTaxon)){
				excludedList.add(aTaxon);
			}
		}
		Iterator itrCompLabelled = fullLabelledList.iterator();
		while(itrCompLabelled.hasNext()){
			String aTaxon = (String) itrCompLabelled.next().toString();
			if(!includedList.contains(aTaxon)){
				excludedList.add(aTaxon+"#1");
			}
		}
		return excludedList;
	}
	
	public void go() {
		// TODO Auto-generated method stub
		data = new AlignedSequenceRepresentation();
		try {
			data.loadSequences(inputFile, true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.removeStopCodons();
		data.writePhylipFile(inputFile.getAbsoluteFile()+".stops.removed.phy",true);
	}

}
