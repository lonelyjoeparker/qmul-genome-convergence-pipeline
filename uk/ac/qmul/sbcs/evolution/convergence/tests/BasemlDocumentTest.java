package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;

public class BasemlDocumentTest extends TestCase {

	public void testPrintTemplate() {
		BasemlDocument b = new BasemlDocument();
		b.finalizeParameters();
		b.printTemplate();
	}

}
