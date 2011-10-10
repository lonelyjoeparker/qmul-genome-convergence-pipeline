package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;

public class AamlDocumentTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPrintTemplate() {
		new AamlDocument().printTemplate();
	}

	public void testAamlDocument() {
		fail("Not yet implemented"); // TODO
	}

	public void testAddParam() {
		AamlDocument document = new AamlDocument();
		document.setParameter("SEQFILE", "seqfile = test.phy");
		document.setParameter("TREEFILE", "treefile = test.tre");
		document.setParameter("FOOFILE", "foo = test.foo");
		assert(!document.hasBeenWritten);
		if(!document.hasBeenWritten){
			document.write(".");
		}
		assert(document.hasBeenWritten);
		System.out.println("\n\n\nTEMPLATE\n\n");
		document.printTemplate();
	}
	
	public void testAssertions(){
		String foo = "foo";
		String bar = "bar";
		String barf = foo;
		String barn = "bar";
		assert(foo.equals(barf));
		assert(!foo.equals(bar));
		assert(foo.hashCode() != bar.hashCode());
		assert(foo.hashCode() == barf.hashCode());
		assert(bar.hashCode() == barn.hashCode());
		assert(barf.hashCode() != bar.hashCode());
		barf = "bar";
		assert(!foo.equals(barf));
		assert(!foo.equals(bar));
		assert(foo.hashCode() != bar.hashCode());
		assert(foo.hashCode() != barf.hashCode());
		assert(bar.hashCode() == barn.hashCode());
		assert(barf.hashCode() == bar.hashCode());
	}
}
