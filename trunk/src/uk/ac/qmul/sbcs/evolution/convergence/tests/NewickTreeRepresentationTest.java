package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TaxonNotFoundError;

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
		File file = new File("/pamlTest/simpleNewick.tre");
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
		File alternativeFile = new File("/pamlTest/newickTestWrite.tre");
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
		File file = new File("/pamlTest/simpleNewick.tre");
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
		File file = new File("/pamlTest/simpleNewick.tre");
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
}
