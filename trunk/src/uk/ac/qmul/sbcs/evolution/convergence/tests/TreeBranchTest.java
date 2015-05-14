package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.TreeBranch;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import junit.framework.TestCase;
import java.util.ArrayList;

public class TreeBranchTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testTreeBranchTreeNodeTreeNode() {
		String input = "(((LOXODONTA:0.080618,DASYPUS:0.028235):0.002756,(((((CANIS:0.012983,FELIS:0.013897):0.005719,(EQUUS:0.028437,((TURSIOPS:0.038936,BOS:0.016707):0.003048,VICUGNA:0.031996):0.004509):0.006443):0.000004,(MYOTIS:0.056507,((RHINOLOPHUS:0.066174,MEGADERMA:0.021473):0.006671,PTEROPUS:0.015521):0.000004):0.008379):0.002227,(SOREX:0.022136,ERINACEUS:0.013937):0.004338):0.004428,((MUS:0.034943,(ORYCTOLAGUS:0.021193,OCHOTONA:0.063783):0.025907):0.003677,(PAN:0.010448,HOMO:0.001622):0.021809):0.002889):0.000004):0.144025,MONODELPHIS:0.113014)";
		TreeNode tree = new TreeNode(input,1);
		ArrayList<TreeBranch> branches = tree.getBranches();
		for(TreeBranch branch:branches){
			System.out.println(branch);
		}
		if(branches.size() != 38){
			fail("Wrong branch count returned"); // this is a pretty minimal test...
		}
	}

}
