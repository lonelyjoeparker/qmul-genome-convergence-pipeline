package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument;
import junit.framework.TestCase;

public class PamlDocumentTest extends TestCase {

	public void testPrintTemplates() {
		new PamlDocument().printTemplate();
	}

}
