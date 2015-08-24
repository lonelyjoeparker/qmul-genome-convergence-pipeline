package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.PhylogenyConvergenceContext;
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
	private DisplayPhylogenyPanel displayedPhylogeny;
	private PhylogenyConvergenceContext convergenceType;
	
	/**
	 * Default no-arg constructor. Deprecated.
	 * @deprecated
	 */
	@Deprecated
	public DisplayPhylogeny(){
		newickTree = null;
		treeNode = null;
		taxonList = null;
		taxonSet = null;
		textTreeRepresentation = null;
		treeFile = null;
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
	}
	
	/**
	 * Constructor using NewickTreeRepresentation
	 * @param newNewickTree
	 */
	public DisplayPhylogeny(NewickTreeRepresentation newNewickTree){
		newickTree = newNewickTree;
		treeNode = new TreeNode(newickTree.getTreeString(),1);
		taxonList = treeNode.getTipsBelow();
		taxonSet = newickTree.getTaxaNames();
		textTreeRepresentation = newickTree.getTreeString();
		treeFile = null;
		ArrayList<String> names = treeNode.getTipsInOrder();
		ArrayList<Integer[]> coordsFromBranches = treeNode.getBranchesAsCoordinatesFromTips(0, 0);
		displayedPhylogeny = new DisplayPhylogenyPanel(coordsFromBranches, names);
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
	}

	/**
	 * Constructor using TreeNode
	 * @param newTreeNode
	 */
	public DisplayPhylogeny(TreeNode newTreeNode){
		treeNode = newTreeNode;
		newickTree = new NewickTreeRepresentation(treeNode.printRecursively());
		taxonList = treeNode.getTipsBelow();
		taxonSet = newickTree.getTaxaNames();
		textTreeRepresentation = treeNode.printRecursively();
		treeFile = null;
		ArrayList<String> names = treeNode.getTipsInOrder();
		ArrayList<Integer[]> coordsFromBranches = treeNode.getBranchesAsCoordinatesFromTips(0, 0);
		displayedPhylogeny = new DisplayPhylogenyPanel(coordsFromBranches, names);
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
	}
	
	/**
	 * Constructor using String. This is <b>VERY</b> likely to cause problems further on however
	 * @param newTreeAsString
	 */
	public DisplayPhylogeny(String newTreeAsString){
		newickTree = new NewickTreeRepresentation(newTreeAsString);
		treeNode = new TreeNode(newTreeAsString,1);
		taxonList = treeNode.getTipsBelow();
		taxonSet = newickTree.getTaxaNames();
		textTreeRepresentation = newTreeAsString;
		treeFile = null;
		ArrayList<String> names = treeNode.getTipsInOrder();
		ArrayList<Integer[]> coordsFromBranches = treeNode.getBranchesAsCoordinatesFromTips(0, 0);
		displayedPhylogeny = new DisplayPhylogenyPanel(coordsFromBranches, names);
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
	}

	/**
	 * Constructor using String and overloaded File argument (needed by {@link DisplayPhylogenyFactory#fromFile()} 
	 * method to create multiple DisplayPhylogenies with the same source file. This is <b>VERY</b> likely to cause
	 *  problems further on however.
	 * @param newTreeAsString
	 */
	public DisplayPhylogeny(String newTreeAsString, File sourceFile){
		newickTree = new NewickTreeRepresentation(newTreeAsString);
		treeNode = new TreeNode(newTreeAsString,1);
		taxonList = treeNode.getTipsBelow();
		taxonSet = newickTree.getTaxaNames();
		textTreeRepresentation = newTreeAsString;
		treeFile = sourceFile;
		ArrayList<String> names = treeNode.getTipsInOrder();
		ArrayList<Integer[]> coordsFromBranches = treeNode.getBranchesAsCoordinatesFromTips(0, 0);
		displayedPhylogeny = new DisplayPhylogenyPanel(coordsFromBranches, names);
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
	}

	/**
	 * Constructor using File. <b>Preferred constructor</b>.
	 * @param newTreeAsFile
	 */
	public DisplayPhylogeny(File newTreeAsFile) throws Exception{
		newickTree = new NewickTreeRepresentation(newTreeAsFile);
		treeNode = new TreeNode(newickTree.getTreeString(),1);
		taxonList = treeNode.getTipsBelow();
		taxonSet = newickTree.getTaxaNames();
		textTreeRepresentation = treeNode.printRecursively();
		treeFile = newTreeAsFile;
		ArrayList<String> names = treeNode.getTipsInOrder();
		ArrayList<Integer[]> coordsFromBranches = treeNode.getBranchesAsCoordinatesFromTips(0, 0);
		displayedPhylogeny = new DisplayPhylogenyPanel(coordsFromBranches, names);
		convergenceType = PhylogenyConvergenceContext.NULL_CONVERGENCE_CONTEXT_NOT_SET;
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
	
	public JPanel getDisplayedPhylogeny() {
		return displayedPhylogeny;
	}

	public PhylogenyConvergenceContext getConvergenceContext() {
		return convergenceType;
	}

	public void setConvergenceContext(PhylogenyConvergenceContext convergenceType) {
		this.convergenceType = convergenceType;
	}

	/**
	 * Compares a HashSet<String> of taxa against the String[] of taxa present in this DisplayPhylogeny. Any new taxa are added to the set and the set is returned.
	 * @param treeSet
	 * @return originalNameSet with any unseen taxa present in the DisplayPhylogeny added...
	 */
	public TreeSet<String> expandTaxonNameSet(TreeSet<String> treeSet) throws NullPointerException{
		for(String someTaxon:taxonList){
			if(!treeSet.contains(someTaxon)){
				treeSet.add(someTaxon);
			}
		}
		return treeSet;
	}
}
