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
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File("/Users/gsjones/Downloads/ENSG00000072364/convergentSites.out"));
		fail("Not yet implemented"); // TODO
	}

	public final void testGetAllBranchPairProbabilitiesSitewiseSummed() {
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File("/Users/gsjones/Downloads/ENSG00000072364/convergentSites.out"));
		float[][] data = parser.getAllBranchPairProbabilitiesSitewiseSummed();
		data.toString();
		fail("Not yet implemented"); // TODO
	}
}
