package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
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
	
	public void testPrestinTrees(){
		TreeNode n1 = new TreeNode("(((LOXODONTA:0.080618,DASYPUS:0.028235):0.002756,(((((CANIS:0.012983,FELIS:0.013897):0.005719,(EQUUS:0.028437,((TURSIOPS:0.038936,BOS:0.016707):0.003048,VICUGNA:0.031996):0.004509):0.006443):0.000004,(MYOTIS:0.056507,((RHINOLOPHUS:0.066174,MEGADERMA:0.021473):0.006671,PTEROPUS:0.015521):0.000004):0.008379):0.002227,(SOREX:0.022136,ERINACEUS:0.013937):0.004338):0.004428,((MUS:0.034943,(ORYCTOLAGUS:0.021193,OCHOTONA:0.063783):0.025907):0.003677,(PAN:0.010448,HOMO:0.001622):0.021809):0.002889):0.000004):0.144025,MONODELPHIS:0.113014)",1);
		TreeNode n2 = new TreeNode("(((LOXODONTA: 0.080618, DASYPUS: 0.028235): 0.002756, (((((CANIS: 0.012983, FELIS: 0.013897): 0.005719, (EQUUS: 0.028437, ((TURSIOPS: 0.038936, BOS: 0.016707): 0.003048, VICUGNA: 0.031996): 0.004509): 0.006443): 0.000004, (MYOTIS: 0.056507, ((RHINOLOPHUS: 0.066174, MEGADERMA: 0.021473): 0.006671, PTEROPUS: 0.015521): 0.000004): 0.008379): 0.002227, (SOREX: 0.022136, ERINACEUS: 0.013937): 0.004338): 0.004428, ((MUS: 0.034943, (ORYCTOLAGUS: 0.021193, OCHOTONA: 0.063783): 0.025907): 0.003677, (PAN: 0.010448, HOMO: 0.001622): 0.021809): 0.002889): 0.000004): 0.144025, MONODELPHIS: 0.113014)",1);
		TreeNode n3 = new TreeNode("(((LOXODONTA: 0.080618, DASYPUS: 0.028235): 0.002756, (((((CANIS: 0.012983, FELIS: 0.013897): 0.005719, (EQUUS: 0.028437, ((TURSIOPS: 0.038936, BOS: 0.016707): 0.003048, VICUGNA: 0.031996): 0.004509): 0.006443): 0.000004, (MYOTIS: 0.056507, ((RHINOLOPHUS: 0.066174, MEGADERMA: 0.021473): 0.006671, PTEROPUS: 0.015521): 0.000004): 0.008379): 0.002227, (SOREX: 0.022136, ERINACEUS: 0.013937): 0.004338): 0.004428, ((MUS: 0.034943, (ORYCTOLAGUS: 0.021193, OCHOTONA: 0.063783): 0.025907): 0.003677, (PAN: 0.010448, HOMO: 0.001622): 0.021809): 0.002889): 0.000004): 0.144025, MONODELPHIS: 0.113014);",1);
		n1.getEndPos();
	}
	
	public void testReinflateSerAndAssignStates() throws IOException, ClassNotFoundException{
		InputStream serfile = new FileInputStream("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/g_100_ENSG0000PRESTIN_ng.fas/g_100_ENSG0000PRESTIN_ng.fasinput100.faconv1367283909044wag.ser");
		ObjectInputStream inOne = new ObjectInputStream(serfile);
		SitewiseSpecificLikelihoodSupportAaml candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
		TreeNode species = new TreeNode(candidate.getFittedTrees()[0].replaceAll("\\s", ""),1);
		species.getEndPos();
		HashMap<String, HashSet<String>[]> states = candidate.getDataset().getAminoAcidsAsFitchStates();
	}
}
