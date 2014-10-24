package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import java.io.File;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.NullBranchPairComparisonPointerException;

public class CodemlAncestralSiteOutputParserTest extends TestCase {

	public CodemlAncestralSiteOutputParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testCodemlAncestralSiteOutputParser() {
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser();
		fail("Not yet implemented"); // TODO
	}

	public final void testCodemlAncestralSiteOutputParserFile() {
		/*
		 * Get the location for the path to the convergentSites.out file, so that
		 * there is no need for multiple test methods for different paths/files.
		 * 
		 * This is set in the debug config window in eclipse via the VM Arguments tab
		 * e.g. 
		 * 	-DdebugConvergentSitesFilePath="/path/to/convergentSites.out"
		 * 	-DdebugConvergentSitesFilePath="/Users/gsjones/Downloads/ENSG00000072364/convergentSites.out""
		 * 	-DdebugConvergentSitesFilePath="/Users/joeparker/Documents/all_work/manuscripts_presentations/nature-rebuttal/zhang_empirical/codeml_anc_all_loci/ENSG00000072364/convergentSites.out"
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
	}

	public final void testGetAllBranchPairProbabilitiesSitewiseSummed() {
		/*
		 * Get the location for the path to the convergentSites.out file, so that
		 * there is no need for multiple test methods for different paths/files.
		 * 
		 * This is set in the debug config window in eclipse via the VM Arguments tab
		 * e.g. 
		 * 	-DdebugConvergentSitesFilePath="/path/to/convergentSites.out"
		 * 	-DdebugConvergentSitesFilePath="/Users/gsjones/Downloads/ENSG00000072364/convergentSites.out""
		 * 	-DdebugConvergentSitesFilePath="/Users/joeparker/Documents/all_work/manuscripts_presentations/nature-rebuttal/zhang_empirical/codeml_anc_all_loci/ENSG00000072364/convergentSites.out"
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		float[][] data = parser.getAllBranchPairProbabilitiesSitewiseSummed();
		data.toString();
	}

	public final void testHasNode(){
		/*
		 * nodes that should be in the data:
		 *	39 11 33 12
		 * nodes that should NOT be in the data:
		 *	391 03
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		System.out.println("constructor passed.");
		boolean test_should_pass_node_A = !parser.containsNode(39); // should eval to 0
		boolean test_should_pass_node_B = !parser.containsNode(12); // should eval to 0
		boolean test_should_pass_node_C = !parser.containsNode(003); // should eval to 0, NB leading zeroes don't affect comparison
		boolean test_should_fail_node_A = parser.containsNode(391); // should eval to 1
		boolean test_should_fail_node_B = parser.containsNode(00); // should eval to 1
		if(test_should_pass_node_A||test_should_pass_node_B||test_should_pass_node_C||test_should_fail_node_A||test_should_fail_node_B){
			fail("hasNode tests failed: "+test_should_pass_node_A+""+test_should_pass_node_B+""+test_should_pass_node_C+""+test_should_fail_node_A+""+test_should_fail_node_B);
		}else{
			System.out.println("hasNode tests passed.");
		}
	}

	public final void testHasBranch(){
		/*
		 * branches that should be in the data:
		 *	39..11
		 *  33..12
		 * branches that should NOT be in the data:
		 *	39..12
		 *  33..11
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		boolean test_should_pass_branch_A = !parser.containsBranch(39,11); // should eval to 0
		boolean test_should_pass_branch_B = !parser.containsBranch(33,12); // should eval to 0
		boolean test_should_fail_branch_A = parser.containsBranch(39,12); // should eval to 1
		boolean test_should_fail_branch_B = parser.containsBranch(33,11); // should eval to 1
		if(test_should_pass_branch_A||test_should_pass_branch_B||test_should_fail_branch_A||test_should_fail_branch_B){
			fail("hasBranch tests failed: "+test_should_pass_branch_A+""+test_should_pass_branch_B+""+test_should_fail_branch_A+""+test_should_fail_branch_B);
		}else{
			System.out.println("hasBranch: tests passed.");
		}
	}

	public final void testGetProbabilitiesFromBranchPairComparison(){
		/*
		 * branches that should be in the data:
		 *	39..11
		 *  33..12
		 * branches that should NOT be in the data:
		 *	39..12
		 *  33..11
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		// should exist
		int[] branch_A = {39,11};
		int[] branch_B = {33,12};
		float[] dataForThisPair = null;
		try {
			dataForThisPair = parser.getProbabilitiesForNodeComparisons(branch_A[0], branch_A[1], branch_B[0], branch_B[1]);
			System.out.println(dataForThisPair[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dataForThisPair == null){
			fail("no branch pair comparison probabilities found for this pair");
		}else{
			System.out.println("Passed: branch pair comparison probabilities found for this pair: {"+ dataForThisPair[0] + ","+ dataForThisPair[1] + ","+ dataForThisPair[2] + ","+ dataForThisPair[3] + "}");
		}
		// now try with branches that should fail
		dataForThisPair = null;
		int[] branch_A_fail = {39,12};
		int[] branch_B_fail = {33,11};
		try {
			dataForThisPair = parser.getProbabilitiesForNodeComparisons(branch_A_fail[0], branch_A_fail[1], branch_B_fail[0], branch_B_fail[1]);
			System.out.println(dataForThisPair[0]);
		} catch (NullBranchPairComparisonPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dataForThisPair != null){
			fail("branch pair comparison probabilities found for this pair, should not exist");
		}else{
			System.out.println("Passed: no branch pair comparison probabilities found for this pair, which should not exist.");
		}
	}
	
	public final void testSortIntPair(){
		// make some calls to sortIntPair..
		// should {0,0}
		int[] return_0_0 = new CodemlAncestralSiteOutputParser().sortIntPair(0, 0);
		// should {0,1}
		int[] return_1_0 = new CodemlAncestralSiteOutputParser().sortIntPair(1, 0);
		// should {0,1}
		int[] return_0_1 = new CodemlAncestralSiteOutputParser().sortIntPair(0, 1);
		// should {1,1}
		int[] return_1_1 = new CodemlAncestralSiteOutputParser().sortIntPair(1, 1);
		// should {-1,-1}
		int[] return_m1_m1 = new CodemlAncestralSiteOutputParser().sortIntPair(-1, -1);
		// should {-1,0}
		int[] return_0_m1 = new CodemlAncestralSiteOutputParser().sortIntPair(0, -1);
		// should {-1,1}
		int[] return_1_m1 = new CodemlAncestralSiteOutputParser().sortIntPair(1, -1);
		// should {max,max}
		int[] return_max_max = new CodemlAncestralSiteOutputParser().sortIntPair(Integer.MAX_VALUE, Integer.MAX_VALUE);
		// should {min,min}
		int[] return_min_min = new CodemlAncestralSiteOutputParser().sortIntPair(Integer.MIN_VALUE, Integer.MIN_VALUE);
		// should {min,max}
		int[] return_max_min = new CodemlAncestralSiteOutputParser().sortIntPair(Integer.MAX_VALUE, Integer.MIN_VALUE);
		// should {min,max}
		int[] return_min_max = new CodemlAncestralSiteOutputParser().sortIntPair(Integer.MIN_VALUE, Integer.MAX_VALUE);
		// should {min,max}
		int[] return_0_min = new CodemlAncestralSiteOutputParser().sortIntPair(0, Integer.MIN_VALUE);
		// should {0,max}
		int[] return_max_0 = new CodemlAncestralSiteOutputParser().sortIntPair(Integer.MAX_VALUE, 0);
		
		/*
		 * tests on outputs
		 */
		// simple positive ints
		if(!((return_0_0[0]==0)&&(return_0_0[1]==0))){fail("0 0");}
		if(!((return_1_0[0]==0)&&(return_1_0[1]==1))){fail("0 1");}
		if(!((return_0_1[0]==0)&&(return_0_1[1]==1))){fail("0 1");}
		if(!((return_1_1[0]==1)&&(return_1_1[1]==1))){fail("1 1");}
		// signed ints
		if(!((return_m1_m1[0]==-1)&&(return_m1_m1[1]==-1))){fail("-1 -1");}
		if(!((return_0_m1[0]==-1)&&(return_0_m1[1]==0))){fail("-1 0");}
		if(!((return_1_m1[0]==-1)&&(return_1_m1[1]==1))){fail("-1 1");}
		// Integer.max and min vals
		if(!((return_max_max[0]==Integer.MAX_VALUE)&&(return_max_max[1]==Integer.MAX_VALUE))){fail("max max");}
		if(!((return_min_min[0]==Integer.MIN_VALUE)&&(return_min_min[1]==Integer.MIN_VALUE))){fail("min min");}
		if(!((return_max_min[0]==Integer.MIN_VALUE)&&(return_max_min[1]==Integer.MAX_VALUE))){fail("min max");}
		if(!((return_min_max[0]==Integer.MIN_VALUE)&&(return_min_max[1]==Integer.MAX_VALUE))){fail("min max");}
		if(!((return_0_min[0]==Integer.MIN_VALUE)&&(return_0_min[1]==0))){fail("min 0");}
		if(!((return_max_0[0]==0)&&(return_max_0[1]==Integer.MAX_VALUE))){fail("0 max");}
		
		// test complete if it reached here
		System.out.println("Test sortIntPair() complete.");
	}

}
