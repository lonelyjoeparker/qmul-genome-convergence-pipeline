package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation
 * 
 * This is a handler for the Newick Utilities package by Junier & Zobhov (2010)
 * <b>Note</b> this analysis will fail if the binary location isn't specified.
 */
public class NewickUtilitiesHandler {
	private File inputFile = null;
	private File binariesDir = null;
	private TreeSet<String> taxaList = null;
	
	/**
	 * Default no-arg constructor. Using this would be pretty silly since there is no way to ensure binary / input exist.
	 */
	public NewickUtilitiesHandler(){}
	
	/**
	 * The preferred constructor.
	 * @param inputF - location of input file
	 * @param binDir - location of the nw_utils binaries directory
	 */
	public NewickUtilitiesHandler(File binDir, File inputF, TreeSet<String> taxaL){
		this.inputFile = inputF;
		this.binariesDir = binDir;
		this.taxaList = taxaL;
	}
	
	public NewickTreeRepresentation pruneTaxa(TreeSet<String> taxaToRemove){
		String prunedTree = null;
		StringBuffer sb = new StringBuffer();
		Iterator taxaItr = taxaToRemove.iterator();
		while(taxaItr.hasNext()){
			sb.append(" "+taxaItr.next());
		}
		sb.append(" ");
		String exeString = this.binariesDir.getAbsolutePath() + "/nw_prune "+this.inputFile.getAbsolutePath()+" "+sb.toString();
		System.out.println(exeString);
		prunedTree = new VerboseSystemCommand(exeString).getiReader().toString();

		return new NewickTreeRepresentation(prunedTree, taxaList);
	}

	public NewickTreeRepresentation pruneAndDeRootTaxa(TreeSet<String> taxaToRemove){
		String prunedTree = null;
		StringBuffer sb = new StringBuffer();
		Iterator taxaItr = taxaToRemove.iterator();
		while(taxaItr.hasNext()){
			sb.append(" "+taxaItr.next());
		}
		sb.append(" ");
		String exeString = this.binariesDir.getAbsolutePath() + "/nw_prune "+this.inputFile.getAbsolutePath()+" "+sb.toString()+" | "+this.binariesDir.getAbsolutePath() + "/nw_reroot -d -";
		System.out.println(exeString);
		prunedTree = new VerboseSystemCommand(exeString).getiReader().toString();

		return new NewickTreeRepresentation(prunedTree, taxaList);
	}
}
