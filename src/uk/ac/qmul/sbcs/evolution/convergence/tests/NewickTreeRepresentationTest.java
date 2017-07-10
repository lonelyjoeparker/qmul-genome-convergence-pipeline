package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaListsMismatchException;
import uk.ac.qmul.sbcs.evolution.convergence.TaxonNotFoundError;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.NewickUtilitiesHandler;

import junit.framework.TestCase;

public class NewickTreeRepresentationTest extends TestCase {

	public NewickTreeRepresentationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNewickTreeRepresentation() {
		fail("Not yet implemented"); // TODO
	}

	public void testNewickTreeRepresentationFileTreeSetOfString() {
		File file = new File("junit-test-inputs/simpleNewick.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(file,names);
		System.out.println(r.getNumberOfTaxa()+" taxa read.");
		r.printSimply();
	}

	public void testNewickTreeRepresentationFile() {
		fail("Not yet implemented"); // TODO
	}
	
	public void testNewickTreeRepresentationSimpleNexusFileWithNamesSet(){
		File simpleNexus = new File("junit-test-inputs/exampleTree.tre");
		File simplePhylip = new File("junit-test-inputs/exampleTree2.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("a");
		names.add("b");
		names.add("c");
		names.add("d");
		names.add("e");
		NewickTreeRepresentation nexusTree = new NewickTreeRepresentation(simpleNexus,names);
		NewickTreeRepresentation phylipTree = new NewickTreeRepresentation(simplePhylip,names);
		System.out.println(nexusTree.getNumberOfTaxa()+" taxa read.");
		nexusTree.printSimply();
		if(!((nexusTree.getNumberOfTrees()==phylipTree.getNumberOfTrees())&&(phylipTree.getNumberOfTrees()>2))){
			fail("parsing has failed");
		}else{
			System.out.println("limited treeParsing NEXUS seems to have worked...");
		}
	}

	public void testNewickTreeRepresentationSimpleNexusFileNoNamesSet(){
		File simpleNexus = new File("junit-test-inputs/exampleTree.tre");
		File simplePhylip = new File("junit-test-inputs/exampleTree2.tre");
		NewickTreeRepresentation nexusTree = new NewickTreeRepresentation(simpleNexus);
		NewickTreeRepresentation phylipTree = new NewickTreeRepresentation(simplePhylip);
		System.out.println(nexusTree.getNumberOfTaxa()+" taxa read.");
		nexusTree.printSimply();
		if(!((nexusTree.getNumberOfTrees()==phylipTree.getNumberOfTrees())&&(phylipTree.getNumberOfTrees()>2))){
			fail("parsing has failed");
		}else{
			System.out.println("limited treeParsing NEXUS seems to have worked...");
		}
	}

	public void testNewickTreeRepresentationStringTreeSetOfString() {
		String input = ("(((Human:0.1,Chimpanzee:0.2):0.8,Gorilla:0.3):0.7,Orangutan:0.4,Gibbon:0.5);");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(input,names);
		System.out.println(r.getNumberOfTaxa()+" taxa read.");
		r.printSimply();
	}

	public void testConcatenateTrees(){
		String input = ("(((Human:0.1,Chimpanzee:0.2):0.8,Gorilla:0.3):0.7,Orangutan:0.4,Gibbon:0.5);");
		String input2 = ("(((Gorilla:0.1,Chimpanzee:0.2):0.8,Human:0.3):0.7,Orangutan:0.4,Gibbon:0.5);");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(input,names);
		NewickTreeRepresentation r2 = new NewickTreeRepresentation(input2,names);
		System.out.println(r.getNumberOfTaxa()+" taxa read.");
		NewickTreeRepresentation concatenated = null;
		try {
			concatenated = r.concatenate(r2);
		} catch (TaxaListsMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		concatenated.printSimply();
	}
	
	public void testNewickTreeRepresentationString() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetTreeString() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetTreeFile() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetNumberOfTaxa() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetTaxaNames() {
		fail("Not yet implemented"); // TODO
	}

	@Deprecated
	public void testWrite() {
		String input = ("(((Human:0.1,Chimpanzee:0.2):0.8,Gorilla:0.3):0.7,Orangutan:0.4,Gibbon:0.5);");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(input,names);
//		System.out.println(r.getNumberOfTaxa()+" taxa read.");
//		r.printSimply();
		/**
		 * r.write();
		 * 
		 * This method commented out because it tests fine and we don't really need another zillion treefiles!
		 */
	}

	@Deprecated
	public void testWriteFile() {
		File alternativeFile = new File("junit-test-inputs/newickTestWrite.tre");
		String input = ("(((Human:0.1,Chimpanzee:0.2):0.8,Gorilla:0.3):0.7,Orangutan:0.4,Gibbon:0.5);");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(input,names);
//		System.out.println(r.getNumberOfTaxa()+" taxa read.");
//		r.printSimply();
		/**
		 * r.write(alternativeFile);
		 * 
		 * This method commented out because it tests fine and we don't really need another zillion treefiles!
		 */
	}

	public void testSetTreeFile() {
		fail("Not yet implemented"); // TODO
	}

	public void testPruneTaxon() {
		File file = new File("junit-test-inputs/simpleNewick.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(file,names);
		System.out.println(r.getNumberOfTaxa()+" taxa read.");
		r.printSimply();
		try {
			r.pruneTaxon("Human");
			r.printSimply();
			r.pruneTaxon("Chimpanzee");
		} catch (TaxonNotFoundError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testPruneTaxonTwo() {
		File file = new File("junit-test-inputs/simpleNewick.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(file,names);
		System.out.println(r.getNumberOfTaxa()+" taxa read.");
		r.printSimply();
		try {
			r.pruneTaxon("Orangutan");
			r.printSimply();
			r.pruneTaxon("Gibbon");
		} catch (TaxonNotFoundError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testPrunePipeline(){
		File treeFileH0 = new File("junit-test-inputs/Spp.tre");
		TreeSet<String> taxaList = new TreeSet<String>();
		TreeSet<String> pruneList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		pruneList.add("VICUGNA");
		pruneList.add("OCHOTONA");
		pruneList.add("ORYCTOLAGUS");
		pruneList.add("SOREX");
		NewickTreeRepresentation treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
		NewickTreeRepresentation treeH0Pruned = treeH0.pruneTaxa(pruneList);
		File treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
		treeH0Pruned.setTreeFile(treeFileH0Pruned);
		treeH0Pruned.writeMultipleReplicates(treeFileH0Pruned,30);
		treeH0Pruned.printSimply();
	}

	public void testPrunePipelineNWUtilsPrune(){
		File treeFileH0 = new File("junit-test-inputs/Spp.UC.tre");
		File binaries = new File("./bin-dependencies");
		TreeSet<String> taxaList = new TreeSet<String>();
		TreeSet<String> pruneList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		pruneList.add("VICUGNA");
		pruneList.add("OCHOTONA");
		pruneList.add("ORYCTOLAGUS");
		pruneList.add("SOREX");
		NewickTreeRepresentation treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
//		NewickTreeRepresentation treeH0Pruned = treeH0.pruneTaxa(pruneList);
		NewickTreeRepresentation treeH0Pruned = new NewickUtilitiesHandler(binaries,treeFileH0,taxaList).pruneTaxa(pruneList);
		File treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
		treeH0Pruned.setTreeFile(treeFileH0Pruned);
		treeH0Pruned.writeMultipleReplicates(treeFileH0Pruned,30);
		treeH0Pruned.printSimply();
	}

	public void testPrunePipelineNWUtilsPruneAndDeRoot(){
		File treeFileH0 = new File("junit-test-inputs/Spp.UC.tre");
		File binaries = new File("./bin-dependencies");
		TreeSet<String> taxaList = new TreeSet<String>();
		TreeSet<String> pruneList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		pruneList.add("BOS");
		pruneList.add("VICUGNA");
		pruneList.add("OCHOTONA");
		pruneList.add("ORYCTOLAGUS");
		pruneList.add("SOREX");
		NewickTreeRepresentation treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
//		NewickTreeRepresentation treeH0Pruned = treeH0.pruneTaxa(pruneList);
		NewickTreeRepresentation treeH0Pruned = new NewickUtilitiesHandler(binaries,treeFileH0,taxaList).pruneAndDeRootTaxa(pruneList);
		File treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".prunedDEROOTED.tre");
		treeH0Pruned.setTreeFile(treeFileH0Pruned);
		treeH0Pruned.writeMultipleReplicates(treeFileH0Pruned,30);
		treeH0Pruned.printSimply();
	}

	public void testPrunePipelineNWUtilsPruneAndDeRootEmptyPruneList(){
			File treeFileH0 = new File("junit-test-inputs/Spp.UC.tre");
			File binaries = new File("./bin-dependencies");
			TreeSet<String> taxaList = new TreeSet<String>();
			TreeSet<String> pruneList = new TreeSet<String>();
			taxaList.add("TURSIOPS");
			taxaList.add("CANIS");
			taxaList.add("FELIS");
			taxaList.add("LOXODONTA");
			taxaList.add("ERINACEUS");
			taxaList.add("MUS");
			taxaList.add("MONODELPHIS");
			taxaList.add("PAN");
			taxaList.add("HOMO");
			taxaList.add("PTERONOTUS");
			taxaList.add("RHINOLOPHUS");
			taxaList.add("PTEROPUS");
			taxaList.add("EIDOLON");
			taxaList.add("DASYPUS");
			taxaList.add("EQUUS");
			taxaList.add("MEGADERMA");
			taxaList.add("MYOTIS");
			taxaList.add("BOS");
			/* The extra 4 taxa in the 22 taxon tree */
			taxaList.add("VICUGNA");
			taxaList.add("OCHOTONA");
			taxaList.add("ORYCTOLAGUS");
			taxaList.add("SOREX");
			pruneList.add("BOS");
			pruneList.add("VICUGNA");
			pruneList.add("OCHOTONA");
			pruneList.add("ORYCTOLAGUS");
			pruneList.add("SOREX");
			pruneList = new TreeSet<String>();
			NewickTreeRepresentation treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
	//		NewickTreeRepresentation treeH0Pruned = treeH0.pruneTaxa(pruneList);
			NewickTreeRepresentation treeH0Pruned = new NewickUtilitiesHandler(binaries, treeFileH0,taxaList).pruneAndDeRootTaxa(pruneList);
			File treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".prunedDEROOTED.tre");
			treeH0Pruned.setTreeFile(treeFileH0Pruned);
			treeH0Pruned.writeMultipleReplicates(treeFileH0Pruned,30);
			treeH0Pruned.printSimply();
		}

	/**
	 * Test harness to check the NewickTreeRepresentation can iteratively label tips, using TreeNode#printRecursivelyLabelling()
	 */
	public void testPrintIterativelyLabellingTips(){
		// set up the tree object itself as an initialised NewickTreeRepresentation
		File file = new File("junit-test-inputs/simpleNewick.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(file,names);
		
		// print the tree with iterative branch labelling
		String labelledString = r.printIterativelyLabellingTips();
		
		System.out.println(labelledString);
	}

	/**
	 * Test harness to check the NewickTreeRepresentation write iteratively labelled tip trees, using TreeNode#printRecursivelyLabelling()
	 */
	public void testWriteIterativelyLabellingTips(){
		// set up the tree object itself as an initialised NewickTreeRepresentation
		File file = new File("junit-test-inputs/simpleNewick.tre");
		TreeSet<String> names = new TreeSet<String>();
		names.add("Human");
		names.add("Chimpanzee");
		names.add("Gorilla");
		names.add("Orangutan");
		names.add("Gibbon");
		NewickTreeRepresentation r = new NewickTreeRepresentation(file,names);
		
		// write the tree with iterative branch labelling
		r.writeLabellingTipsRecursively(new File("junit-test-inputs/labelledIterativelyNewick.tre"));
	}
	
	public void testReadMultipleTrees(){
			File treeFile = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/debug.tre");
			TreeSet<String> taxaList = new TreeSet<String>();
			taxaList.add("TURSIOPS");
			taxaList.add("CANIS");
			taxaList.add("FELIS");
			taxaList.add("LOXODONTA");
			taxaList.add("ERINACEUS");
			taxaList.add("MUS");
			taxaList.add("MONODELPHIS");
			taxaList.add("PAN");
			taxaList.add("HOMO");
			taxaList.add("PTERONOTUS");
			taxaList.add("RHINOLOPHUS");
			taxaList.add("PTEROPUS");
			taxaList.add("EIDOLON");
			taxaList.add("DASYPUS");
			taxaList.add("EQUUS");
			taxaList.add("MEGADERMA");
			taxaList.add("MYOTIS");
			taxaList.add("BOS");
			/* The extra 4 taxa in the 22 taxon tree */
			taxaList.add("VICUGNA");
			taxaList.add("OCHOTONA");
			taxaList.add("ORYCTOLAGUS");
			taxaList.add("SOREX");
			NewickTreeRepresentation tree = new NewickTreeRepresentation(treeFile,taxaList);
			if(tree.getNumberOfTrees() == 11){
				assert(true);
			}else{
				fail();
			}
		}

	public void testPrunePipelineNWUtilsPruneMultipleTrees(){
		File treeFileH0 = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/debug.tre");
		File binaries = new File("./bin-dependencies");
		TreeSet<String> taxaList = new TreeSet<String>();
		TreeSet<String> pruneList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		pruneList.add("VICUGNA");
		pruneList.add("OCHOTONA");
		pruneList.add("ORYCTOLAGUS");
		pruneList.add("SOREX");
		pruneList.add("BALLS");
		NewickTreeRepresentation treeH0Pruned = new NewickUtilitiesHandler(binaries,treeFileH0,taxaList).pruneTaxa(pruneList);
		File treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
		treeH0Pruned.setTreeFile(treeFileH0Pruned);
		treeH0Pruned.printSimply();
	}

	public void testReadMultipleTreesGetArray(){
		File treeFile = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/debug.tre");
		TreeSet<String> taxaList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		NewickTreeRepresentation tree = new NewickTreeRepresentation(treeFile,taxaList);
		if(tree.getNumberOfTrees() == 11){
			assert(true);
		}else{
			fail();
		}
		String[] separateTopologies = tree.getIndividualTrees();
		if(separateTopologies.length == 11){
			assert(true);
		}else{
			fail();
		}
		
	}

	public void testReadMultipleTreesGetArrayReadSpecific(){
		File treeFile = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/debug.tre");
		TreeSet<String> taxaList = new TreeSet<String>();
		taxaList.add("TURSIOPS");
		taxaList.add("CANIS");
		taxaList.add("FELIS");
		taxaList.add("LOXODONTA");
		taxaList.add("ERINACEUS");
		taxaList.add("MUS");
		taxaList.add("MONODELPHIS");
		taxaList.add("PAN");
		taxaList.add("HOMO");
		taxaList.add("PTERONOTUS");
		taxaList.add("RHINOLOPHUS");
		taxaList.add("PTEROPUS");
		taxaList.add("EIDOLON");
		taxaList.add("DASYPUS");
		taxaList.add("EQUUS");
		taxaList.add("MEGADERMA");
		taxaList.add("MYOTIS");
		taxaList.add("BOS");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("VICUGNA");
		taxaList.add("OCHOTONA");
		taxaList.add("ORYCTOLAGUS");
		taxaList.add("SOREX");
		NewickTreeRepresentation tree = new NewickTreeRepresentation(treeFile,taxaList);
		if(tree.getNumberOfTrees() == 11){
			assert(true);
		}else{
			fail();
		}
		String tree_0 = tree.getSpecificTree(0);
		if(tree_0.equals("(((LOXODONTA,DASYPUS),(((((CANIS,FELIS),(EQUUS,((TURSIOPS,BOS),VICUGNA))),((PTERONOTUS,MYOTIS),((RHINOLOPHUS,MEGADERMA),(PTEROPUS,EIDOLON)))),(SOREX,ERINACEUS)),((MUS,(ORYCTOLAGUS,OCHOTONA)),(PAN,HOMO)))),MONODELPHIS);")){
			assert(true);
		}else{
			fail();
		}
		String tree_10 = tree.getSpecificTree(10);
		if(tree_10.equals("(((((((PTERONOTUS,MYOTIS),((RHINOLOPHUS,MEGADERMA),TURSIOPS)),(PTEROPUS,EIDOLON)),((SOREX,ERINACEUS),(((MUS,(ORYCTOLAGUS,OCHOTONA)),(PAN,HOMO)),(MONODELPHIS,(LOXODONTA,DASYPUS))))),(CANIS,FELIS)),EQUUS),BOS,VICUGNA);")){
			assert(true);
		}else{
			fail();
		}
		if(!tree_10.equals("FAILMESUCKER(((((((PTERONOTUS,MYOTIS),((RHINOLOPHUS,MEGADERMA),TURSIOPS)),(PTEROPUS,EIDOLON)),((SOREX,ERINACEUS),(((MUS,(ORYCTOLAGUS,OCHOTONA)),(PAN,HOMO)),(MONODELPHIS,(LOXODONTA,DASYPUS))))),(CANIS,FELIS)),EQUUS),BOS,VICUGNA);")){
			assert(true);
		}else{
			fail();
		}
		
	}
}
