package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;

import uk.ac.qmul.sbcs.evolution.sandbox.TreeNode;
import junit.framework.TestCase;

public class TreeNodeTest extends TestCase {

	public TreeNodeTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTreeNodeStringInt() {
		TreeNode n1 = new TreeNode("((a,b),c)",1);
		TreeNode n2 = new TreeNode("((a:0.01,b:0.02):0.01,c:0.03)",1);
		TreeNode n3 = new TreeNode("((a:0.01,b:0.02):-0.01,c:0.03)",1);
		TreeNode n4 = new TreeNode("((a,b),(c,(d,e)))",1);
		assert(true);
	}

	public void testAssignStates(){
		HashMap<String, HashSet<String>[]> states = new HashMap<String,HashSet<String>[]>();
		HashSet<String>[] hseta = (HashSet<String>[]) Array.newInstance(HashSet.class, 2);
		HashSet<String>[] hsetb = (HashSet<String>[]) Array.newInstance(HashSet.class, 2);
		HashSet<String>[] hsetc = (HashSet<String>[]) Array.newInstance(HashSet.class, 2);
		hseta[0] = new HashSet<String>();
		hsetb[0] = new HashSet<String>();
		hsetc[0] = new HashSet<String>();
		hseta[0].add("black");
		hsetb[0].add("white");
		hsetc[0].add("black");
		hseta[1] = new HashSet<String>();
		hsetb[1] = new HashSet<String>();
		hsetc[1] = new HashSet<String>();
		hseta[1].add("black");
		hsetb[1].add("white");
		hsetc[1].add("fushcia");
		
		states.put("a", hseta);
		states.put("b", hsetb);
		states.put("c", hsetc);
		for (int i = 0; i < 10; i++) {
			System.out.println("rep "+i);
			TreeNode n1 = new TreeNode("((a,b),c)", 1);
			HashSet<String>[] baseStates = n1.getFitchStates(states);
			System.out.println("rep "+i+" set initial states");
			n1.printStates();
			n1.resolveFitchStatesTopnode();
			n1.resolveFitchStates(n1.states);
			n1.getEndPos();
			System.out.println("rep "+i+" final states");
			n1.printStates();
		}
	}
}
