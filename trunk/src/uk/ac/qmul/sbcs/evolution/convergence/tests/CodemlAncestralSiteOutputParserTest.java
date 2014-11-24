package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.Branch;
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
	
	/**
	 * Test the {@link CodemlAncestralSiteOutputParser#getSitewiseBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber)} and {@link CodemlAncestralSiteOutputParser#getSummedBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber)} methods.
	 */
	public final void testGetProbabilitiesIncludingNode(){
		/*
		 * Test the getSitewiseBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber) and getSummedBranchPairProbabilitiesIncludingSpecificNodeNumber(int nodeNumber) methods
		 */
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		float[][] returnMatrix = parser.getSitewiseBranchPairProbabilitiesIncludingSpecificNodeNumber(7);
		float[] returnRow = parser.getSummedBranchPairProbabilitiesIncludingSpecificNodeNumber(7);
		/*
		 * The correct matrix for node 7 should be:
		 * 
		 *  0	0.0056	0.0038	0.0018
			0.0039	0	0	0
			0.0038	0	0	0
			0.0059	0	0	0
			0	0.0021	0.0014	0.0007
			0.0182	0	0	0
			0.0117	0	0	0
			0.0224	0	0	0
			0.0082	0	0	0
			0	0.0125	0.0013	0.0112
			0	0.0178	0.0169	0.0009
			0.0004	0.0112	0.0107	0.0005
			0.0109	0.0074	0.0071	0.0003
			0.0001	0.0207	0.0198	0.0009
			0.0013	0.0003	0.0003	0
			0.0075	0.0045	0.0017	0.0028
			0.005	0	0	0
			0.0277	0	0	0
			0.0331	0	0	0
			0.0071	0	0	0
			0.0028	0	0	0
			0	0.0046	0.0034	0.0012
			0.0649	0	0	0
			0.005	0	0	0
			0.0223	0.0013	0.0011	0.0002
			0.0481	0.0001	0.0001	0
			0.0035	0	0	0
			0.0015	0	0	0
			0.0024	0	0	0
			0.0109	0.0001	0	0.0001
			0.0021	0	0	0
			0.007	0	0	0
			0.0725	0	0	0
			0	0.0352	0.0345	0.0007
			0.0437	0	0	0
			0	0.0634	0.0627	0.0007
			0	0.0053	0.0051	0.0001
			0	0.0352	0.0345	0.0007
			0.0049	0	0	0
			0.0052	0	0	0
			0.0003	0.0319	0.0315	0.0004
			0.009	0	0	0
		 * 
		 * And the correct row values (summed of above) should be:
		 * 
		 * 0.4733	0.2592	0.2359	0.0232	
		 */
		
		// test the row totals first, as this is easier
		float[] testVals = {0.4733f,	0.2592f,	0.2359f,	0.0232f	};
		for(int i=0;i<4;i++){
			if(Math.abs(testVals[i] - returnRow[i])>0.00001){	// floating-point errors mean values are not exact...
				fail("Row value mismatch! Expected ["+testVals[i]+"], got ["+returnRow[i]+"] (diff "+Math.abs(testVals[i] - returnRow[i])+").");
			}		
		}
		// now do the same for the matrix
		float[][] testMatrix = {
				{0f,0.0056f,0.0038f,0.0018f},
				{0.0039f,0f,0f,0f},
				{0.0038f,0f,0f,0f},
				{0.0059f,0f,0f,0f},
				{0f,0.0021f,0.0014f,0.0007f},
				{0.0182f,0f,0f,0f},
				{0.0117f,0f,0f,0f},
				{0.0224f,0f,0f,0f},
				{0.0082f,0f,0f,0f},
				{0f,0.0125f,0.0013f,0.0112f},
				{0f,0.0178f,0.0169f,0.0009f},
				{0.0004f,0.0112f,0.0107f,0.0005f},
				{0.0109f,0.0074f,0.0071f,0.0003f},
				{0.0001f,0.0207f,0.0198f,0.0009f},
				{0.0013f,0.0003f,0.0003f,0f},
				{0.0075f,0.0045f,0.0017f,0.0028f},
				{0.005f,0f,0f,0f},
				{0.0277f,0f,0f,0f},
				{0.0331f,0f,0f,0f},
				{0.0071f,0f,0f,0f},
				{0.0028f,0f,0f,0f},
				{0f,0.0046f,0.0034f,0.0012f},
				{0.0649f,0f,0f,0f},
				{0.005f,0f,0f,0f},
				{0.0223f,0.0013f,0.0011f,0.0002f},
				{0.0481f,0.0001f,0.0001f,0f},
				{0.0035f,0f,0f,0f},
				{0.0015f,0f,0f,0f},
				{0.0024f,0f,0f,0f},
				{0.0109f,0.0001f,0f,0.0001f},
				{0.0021f,0f,0f,0f},
				{0.007f,0f,0f,0f},
				{0.0725f,0f,0f,0f},
				{0f,0.0352f,0.0345f,0.0007f},
				{0.0437f,0f,0f,0f},
				{0f,0.0634f,0.0627f,0.0007f},
				{0f,0.0053f,0.0051f,0.0001f},
				{0f,0.0352f,0.0345f,0.0007f},
				{0.0049f,0f,0f,0f},
				{0.0052f,0f,0f,0f},
				{0.0003f,0.0319f,0.0315f,0.0004f},
				{0.009f,0f,0f,0}
			};
		// we'll compare matrix dimensions and sums, as values may be in a different order and rounding errors too
		if(testMatrix.length != returnMatrix.length){
			fail("matrices' lengths unequal!");
		}
		if(testMatrix[0].length != returnMatrix[0].length){
			fail("matrices' widths unequal!");
		}
		float testSum = 0;
		float matrixSum = 0;
		float[] testMatrixSums = new float[4];
		float[] matrixSums = new float [4];
		// sum the columns in both test and returned matrices
		for(int i=0;i<testMatrix.length;i++){
			for(int j=0;j<4;j++){
				// increment test matrix column totals
				testMatrixSums[j] += testMatrix[i][j];
				
				// increment matrix col tots
				matrixSums[j] += returnMatrix[i][j];
				
				// may as well update running totals too now, not efficient but who cares
				testSum += testMatrix[i][j];
				matrixSum += returnMatrix[i][j];
			}
		}
		// compare running totals
		if(Math.abs(matrixSum-testSum) > 0.001){
			fail("Running totals do not match ("+testSum+") and ("+matrixSum+") diff ("+Math.abs(matrixSum-testSum)+")");
		}
		// compare column totals
		for(int i=0;i<4;i++){
			if(Math.abs(testMatrixSums[i] - matrixSums[i])>0.00001){	// floating-point errors mean values are not exact...
				fail("Row value mismatch! Expected ["+testMatrixSums[i]+"], got ["+matrixSums[i]+"].");
			}		
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


	/*
	 * Test that the TreeNode finds the lowest internal node containing target taxa, and returns ID / numbering of branch leading to it
	 * Tests for lowest clade containing all the taxa in the target set, which in this case are actually paraphyletic (e.g. n tips > n set, or a non-listed taxon is present in the clade, to put it another way)
	 */
	public void testGetBranchNumberingIDContainingTaxaCheckMonophyly(){
		// instantiate the tree
		String input="(((LOXODONTA:0.023584,DASYPUS:0.029504):0.000004,((((CANIS:0.076115,(EQUUS:0.014067,((TURSIOPS:0.000004,BOS:0.014131):0.003492,VICUGNA:0.021123):0.010546):0.000004):0.000004,((PTERONOTUS:0.025088,MYOTIS:0.032407):0.003456,((RHINOLOPHUS:0.008430,MEGADERMA:0.031984):0.005840,(PTEROPUS:0.000004,EIDOLON:0.006953):0.021190):0.000004):0.000004):0.000004,(SOREX:0.088536,ERINACEUS:0.044769):0.010306):0.003510,((MUS:0.090365,(ORYCTOLAGUS:0.011232,OCHOTONA:0.044380):0.036082):0.001096,(PAN:0.000004,HOMO:0.000004):0.013368):0.006229):0.001213):0.165422,MONODELPHIS:0.138559);";
// use tip alphanumeric labels
//		String input = ("(((8_LOXODONTA,3_DASYPUS)24,((((2_CANIS,(5_EQUUS,((20_TURSIOPS,1_BOS)31,21_VICUGNA)30)29)28,((16_PTERONOTUS,12_MYOTIS)33,((18_RHINOLOPHUS,9_MEGADERMA)35,(17_PTEROPUS,4_EIDOLON)36)34)32)27,(19_SOREX,6_ERINACEUS)37)26,((11_MUS,(14_ORYCTOLAGUS,13_OCHOTONA)40)39,(15_PAN,7_HOMO)41)38)25)23,10_MONODELPHIS);"); // no spaces
//		String input = "(((8_LOXODONTA, 3_DASYPUS) 24 , ((((2_CANIS, (5_EQUUS, ((20_TURSIOPS, 1_BOS) 31 , 21_VICUGNA) 30 ) 29 ) 28 , ((16_PTERONOTUS, 12_MYOTIS) 33 , ((18_RHINOLOPHUS, 9_MEGADERMA) 35 , (17_PTEROPUS, 4_EIDOLON) 36 ) 34 ) 32 ) 27 , (19_SOREX, 6_ERINACEUS) 37 ) 26 , ((11_MUS, (14_ORYCTOLAGUS, 13_OCHOTONA) 40 ) 39 , (15_PAN, 7_HOMO) 41 ) 38 ) 25 ) 23 , 10_MONODELPHIS) 22 ;"; // with spaces
		TreeNode phylogeny_number_1 = new TreeNode(input,1);

		// create the mapping set
		HashMap<String,Integer> tipNumberMap = new HashMap<String,Integer>();
		tipNumberMap.put("LOXODONTA",8 );
		tipNumberMap.put("DASYPUS",3 );
		tipNumberMap.put("CANIS",2);
		tipNumberMap.put("EQUUS",5);
		tipNumberMap.put("TURSIOPS",20);
		tipNumberMap.put("BOS",1);
		tipNumberMap.put("VICUGNA",21);
		tipNumberMap.put("PTERONOTUS",16);
		tipNumberMap.put("MYOTIS",12);
		tipNumberMap.put("RHINOLOPHUS",18);
		tipNumberMap.put("MEGADERMA",9);
		tipNumberMap.put("PTEROPUS",17);
		tipNumberMap.put("EIDOLON",4 );
		tipNumberMap.put("SOREX",19);
		tipNumberMap.put("ERINACEUS",6);
		tipNumberMap.put("MUS",11);
		tipNumberMap.put("ORYCTOLAGUS",14);
		tipNumberMap.put("OCHOTONA",13);
		tipNumberMap.put("PAN",15);
		tipNumberMap.put("HOMO",7);
		tipNumberMap.put("MONODELPHIS",10);
		
		// create some taxa lists to look for
		String[] bats = {
				"PTERONOTUS",
				"MYOTIS",
				"RHINOLOPHUS",
				"PTEROPUS",
				"EIDOLON"
		};
		String[] cetartiodactyls = {"TURSIOPS","BOS"};
		String[] cows = {"BOS"};
		String[] dols = {"TURSIOPS"};
		// array to hold them all so we can iterate
		Object[] tries = {cetartiodactyls,cows,dols};
		
		// should have the tree set up now
		// set up the parser with output:
		String convergentSitesFilePath = System.getProperty("debugConvergentSitesFilePath");
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File(convergentSitesFilePath));
		parser.setPhylogenyWithSpecifiedTipLabelNumberMapping(phylogeny_number_1,tipNumberMap);		
		for(Object focal:tries){
			String[] focalTaxa = (String[]) focal;
			float[] dataForThisPair = null;
			try {
				dataForThisPair = parser.getProbabilitiesForAncestralBranchComparisonsDefinedByTaxonSetMRCAs(bats, focalTaxa);
				for(float val:dataForThisPair){
					System.out.print(val+"\t");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("\t(");
			for(String taxon:focalTaxa){
				System.out.print(taxon+",");
			}
			System.out.println(")");
		}
		
	}

	/**
	 * Tests that the behaviour of Branch in Collections.contains() method is correct.
	 */
	public final void testBranchContains() {
		HashSet<Branch> branches = new HashSet<Branch>();
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(1,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(8,4));
		branches.add(new CodemlAncestralSiteOutputParser().new Branch(38,8));
		Branch test = new CodemlAncestralSiteOutputParser().new Branch(8,38);
		int[] hashCodes = new int[4];
		hashCodes[0] = test.hashCode();
		int i=1;
		for(Branch abranch:branches){
			hashCodes[i] = abranch.hashCode();
			i++;
		}
		if(!branches.contains(test)){
			fail("failed to find branch");
		}
	}

}
