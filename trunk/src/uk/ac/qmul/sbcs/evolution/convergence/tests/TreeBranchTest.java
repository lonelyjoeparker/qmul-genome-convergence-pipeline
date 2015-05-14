package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.TreeBranch;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import junit.framework.TestCase;

public class TreeBranchTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testTreeBranchTreeNodeTreeNode() {
		String inputTree = "(((((CANIS,FELIS),(VICUGNA,(TURSIOPS,BOS))),((PTERONOTUS,MYOTIS),((RHINOLOPHUS,MEGADERMA),(PTEROPUS,EIDOLON)))),(SOREX,ERINACEUS)),((MUS,OCHOTONA),HOMO));";
		TreeNode tree = new TreeNode(inputTree,1);
		TreeBranch[] branches = tree.getBranches();
		for(TreeBranch branch:branches){
			System.out.println(branch);
		}
		fail("Not yet implemented"); // TODO
	}

}
