package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * A minimal class to hold branch information. 
 * Note that this is intended as a lightweight class, 
 * most complicated stuff can be got from the parent or daughter TreeNode objects.
 * @author joeparker
 * @see TreeNode
 */
public class TreeBranch {
	TreeNode parentNode = null;
	TreeNode daughterNode = null;
	int nodeNumberFrom = -1;
	int nodeNumberTo = -1;
	String contentFrom = null;
	String contentTo = null;
	boolean endsInTerminalTaxon = false;
	double branchLength = 0.0d;
	
	@Deprecated
	/**
	 * Default no-arg constructor is deprecated. Use TreeBranch(TreeNode parent, TreeNode daughter) instead.
	 */
	public TreeBranch(){}
	
	/**
	 * Create a branch from a pair of nodes. Pretty straightforward. 
	 * The isTerminal and branchLength are taken from the daughter.
	 * @param parent
	 * @param daughter
	 */
	public TreeBranch(TreeNode parent, TreeNode daughter){
		/* Instance variables set from parent and daughter treeNodes */
		this.parentNode		= parent;
		this.daughterNode	= daughter;
		nodeNumberFrom 		= parent.nodeNumber;
		nodeNumberTo 		= daughter.nodeNumber;
		contentFrom 		= parent.content;
		contentTo 			= daughter.content;
		
		// NB length and isTerminal status set from DAUGHTER not parent!
		endsInTerminalTaxon = daughter.isTerminal;
		branchLength 		= daughter.branchLength;
	}
	
	@Override
	/**
	 * Overridden toString() inherited from java.lang.Object
	 */
	public String toString(){
		return "Branch from: "+contentFrom+" # "+nodeNumberFrom+" =="+branchLength+"==> to: "+contentTo+" # "+nodeNumberTo+"(isTerminal: "+endsInTerminalTaxon+")";
	}
}
