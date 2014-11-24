package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.util.HashSet;
import java.util.Iterator;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.Branch;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.BranchLineage;
import junit.framework.TestCase;

public class CodemlAncestralSiteOutputParserBranchLineageTest extends TestCase {

	public HashSet<Branch> dataset;
	
	protected void setUp() throws Exception {
		super.setUp();
		dataset = setUpBranches();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testBranchLineage() {
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		if(lineage.doesLineageAdjoin()){
			fail("lineage appears to adjoin, this is not correct."); 
		}
	}

	public final void testGetCompleteLineage() {
		// should pass
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		Iterator<Branch> itr = dataset.iterator();
		while(itr.hasNext()){
			lineage.addBranch(itr.next());
		}
		if(lineage.doesLineageAdjoin()){
			// this is fine
		}else{
			fail("does not adjoin - should do"); // TODO
		}
		Branch test = new CodemlAncestralSiteOutputParser().new Branch(19,38);
		HashSet<Branch> lineageComplete = lineage.getCompleteLineage();
		if(!lineageComplete.contains(test)){
			fail("The required branch is absent");
		}
	}

	public final void testAddBranch() {
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		Iterator<Branch> itr = dataset.iterator();
		while(itr.hasNext()){
			lineage.addBranch(itr.next());
			if(lineage.doesLineageAdjoin()){
				break;
			}else{
				fail("does not adjoin - should do"); // TODO
			}
		}
	}

	/**
	 * checks the basic machinery is working with no misleading branches, e.g only 2 edges per node, no erroneous links possible
	 */
	public final void testDoesLineageAdjoinPassSimpleChain() {
		// should pass
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		Iterator<Branch> itr = dataset.iterator();
		while(itr.hasNext()){
			lineage.addBranch(itr.next());
		}
		if(lineage.doesLineageAdjoin()){
			// this is fine
		}else{
			fail("does not adjoin - should do"); // TODO
		}
	}
	
	/**
	 * checks the lineage branch finder can deal with bifurcating branches, e.g. three edges per not, lineage linker may erroneously add a branch to chain which goes to nowhere...
	 */
	public final void testDoesLineageAdjoinPassMisleadingBranches() {
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		dataset.add(new CodemlAncestralSiteOutputParser().new Branch(7,9));
		dataset.add(new CodemlAncestralSiteOutputParser().new Branch(17,19));
		Iterator<Branch> itr = dataset.iterator();
		while(itr.hasNext()){
			lineage.addBranch(itr.next());
		}
		if(lineage.doesLineageAdjoin()){
			fail("branch adjoins - should not"); // TODO
		}else{
			// this is fine
		}
	}

	public final void testDoesLineageAdjoinFailDueToMissing() {
		BranchLineage lineage = new CodemlAncestralSiteOutputParser().new BranchLineage(1,2);
		HashSet<Branch> branches = new HashSet<Branch>();
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(1,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(8,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(8,38));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(38,19));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(2,7));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(6,19));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(7,9));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(17,19));
		Iterator<Branch> itr = branches.iterator();
		while(itr.hasNext()){
			lineage.addBranch(itr.next());
		}
		if(lineage.doesLineageAdjoin()){
			fail("branch adjoins - should not"); // TODO
		}else{
			// this is fine
		}
	}

	public final void testBranchOtherNode(){
		Branch b = new CodemlAncestralSiteOutputParser().new Branch(1,2);
		int otherNode = b.otherNode(2);
		if(otherNode != 1){
			fail("Branch.otherNode(2) on branch "+b.toString()+" returned "+otherNode);
		}
		// try and get a node which we know isn't there
		boolean success = false;
		try {
			otherNode = b.otherNode(3);
			success = true; // should not reach this point
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(success){
			fail("were able to get otherNode from non-existent arg");
		}
	}
	
	private HashSet<Branch> setUpBranches(){
		HashSet<Branch> branches = new HashSet<Branch>();
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(1,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(8,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(8,38));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(38,19));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(2,6));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(6,19));
		return branches;
	}
}
