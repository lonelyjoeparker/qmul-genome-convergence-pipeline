/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralGeneralOutputParser;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser.BranchLineage;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class CodemlAncestralGeneralOutputParserTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralGeneralOutputParser#CodemlAncestralGeneralOutputParser()}.
	 */
	public final void testCodemlAncestralGeneralOutputParser() {
		fail("The no-arg constructor is deprecated."); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralGeneralOutputParser#CodemlAncestralGeneralOutputParser(java.io.File)}.
	 */
	public final void testCodemlAncestralGeneralOutputParserFile() {
		String outputFilePath = System.getProperty("debugOutputFilePath");
		CodemlAncestralGeneralOutputParser parser = new CodemlAncestralGeneralOutputParser(new File(outputFilePath));
		TreeSet<String> taxonSet = parser.getInferredTaxonSet();
		HashMap<String,Integer> nameIDmapping = parser.getTaxonNamesIDMap();
		// set up now test first taxon BOS
		if(!taxonSet.contains("BOS")){
			fail("Taxon 'BOS' not seen (taxon set)!");
		}
		if(!nameIDmapping.keySet().contains("BOS")){
			fail("Taxon 'BOS' not seen (ID mapping key set)!");
		}else{
			// check the ID is correct
			int ID_bos = nameIDmapping.get("BOS");
			if(ID_bos != 1){
				fail("BOS ID not 1!");
			}
		}
		// test last taxon VICUGNA
		if(!taxonSet.contains("VICUGNA")){
			fail("Taxon 'VICUGNA' not seen (taxon set)!");
		}
		if(!nameIDmapping.keySet().contains("VICUGNA")){
			fail("Taxon 'VICUGNA' not seen (ID mapping key set)!");
		}else{
			// check the ID is correct
			int ID_vicugna = nameIDmapping.get("VICUGNA");
			if(ID_vicugna != 21){
				fail("VICUGNA ID not 21!");
			}
		}
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralGeneralOutputParser#getPhylogeny()}.
	 */
	public final void testGetPhylogeny() {
		String outputFilePath = System.getProperty("debugOutputFilePath");
		CodemlAncestralGeneralOutputParser parser = new CodemlAncestralGeneralOutputParser(new File(outputFilePath));
		TreeNode phylogeny = parser.getPhylogeny();
		String testExpectedTree = "(((LOXODONTA,DASYPUS),((((CANIS,(EQUUS,((TURSIOPS,BOS),VICUGNA))),((PTERONOTUS,MYOTIS),((RHINOLOPHUS,MEGADERMA),(PTEROPUS,EIDOLON)))),(SOREX,ERINACEUS)),((MUS,(ORYCTOLAGUS,OCHOTONA)),(PAN,HOMO)))),MONODELPHIS)";
		if(!phylogeny.getContent().equals(testExpectedTree)){
			fail("tree string not parsed correctly ("+phylogeny.getContent()+")"); 
		}
	}
}
