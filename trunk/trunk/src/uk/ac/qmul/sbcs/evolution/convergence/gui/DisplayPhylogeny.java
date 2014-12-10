package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;

/**
 * A class to hold data and display information about phylogenies, which can be specified as either TreeNode or NewickTreeRepresentation
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class DisplayPhylogeny {
	private final NewickTreeRepresentation newickTree;
	private final TreeNode treeNode;
	private final String[] taxonList;
	private final TreeSet<String> taxonSet;
	private String textTreeRepresentation;
	private final File treeFile;
	
	/**
	 * Default no-arg constructor
	 */
	public DisplayPhylogeny(){
		newickTree = null;
		treeNode = null;
		taxonList = null;
		taxonSet = null;
		textTreeRepresentation = null;
		treeFile = null;
	}
	
	/**
	 * Constructor using NewickTreeRepresentation
	 * @param newNewickTree
	 */
	public DisplayPhylogeny(NewickTreeRepresentation newNewickTree){
		newickTree = newNewickTree;
		treeNode = null;
		taxonList = null;
		taxonSet = null;
		textTreeRepresentation = newickTree.getTreeString();
		treeFile = null;
	}

	/**
	 * Constructor using TreeNode
	 * @param newTreeNode
	 */
	public DisplayPhylogeny(TreeNode newTreeNode){
		newickTree = null;
		treeNode = newTreeNode;
		taxonList = treeNode.getTipsBelow();
		taxonSet = null;
		textTreeRepresentation = treeNode.printRecursively();
		treeFile = null;
	}
	
	/**
	 * Constructor using String
	 * @param newTreeAsString
	 */
	public DisplayPhylogeny(String newTreeAsString){
		newickTree = null;
		treeNode = null;
		taxonList = null;
		taxonSet = null;
		textTreeRepresentation = newTreeAsString;
		treeFile = null;
	}

	/**
	 * Constructor using File. <b>Preferred constructor</b>.
	 * @param newTreeAsFile
	 */
	public DisplayPhylogeny(File newTreeAsFile){
		newickTree = null;
		treeNode = null;
		taxonList = null;
		taxonSet = null;
		textTreeRepresentation = null;
		treeFile = newTreeAsFile;
	}

	@Override
	public String toString(){
		return this.treeFile.getAbsolutePath();
	}
	
	public NewickTreeRepresentation getNewickTree() {
		return newickTree;
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	public String[] getTaxonList() {
		return taxonList;
	}

	public TreeSet<String> getTaxonSet() {
		return taxonSet;
	}

	public String getTextTreeRepresentation() {
		return textTreeRepresentation;
	}
}
