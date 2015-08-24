package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;

public class BasemlDocumentTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPrintTemplate() {
		BasemlDocument b = new BasemlDocument();
		b.finalizeParameters();
		b.printTemplate();
	}

}
