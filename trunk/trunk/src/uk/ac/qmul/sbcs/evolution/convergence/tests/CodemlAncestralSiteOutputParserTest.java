package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import java.io.File;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser;

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
		boolean test_should_pass_node_A = !parser.containsNode(39); // should eval to 0
		boolean test_should_pass_node_B = !parser.containsNode(12); // should eval to 0
		boolean test_should_pass_node_C = !parser.containsNode(003); // should eval to 0, NB leading zeroes don't affect comparison
		boolean test_should_fail_node_A = parser.containsNode(391); // should eval to 1
		boolean test_should_fail_node_B = parser.containsNode(00); // should eval to 1
		if(test_should_pass_node_A||test_should_pass_node_B||test_should_pass_node_C||test_should_fail_node_A||test_should_fail_node_B){
			fail("hasNode tests failed: "+test_should_pass_node_A+""+test_should_pass_node_B+""+test_should_pass_node_C+""+test_should_fail_node_A+""+test_should_fail_node_B);
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
		int[] branch_A = {39,11};
		int[] branch_B = {33,12};
		float[] dataForThisPair = parser.getProbabilitiesForNodeComparisons(branch_A[0], branch_A[1], branch_B[0], branch_B[1]);
		System.out.println(dataForThisPair[0]);
	}
}
