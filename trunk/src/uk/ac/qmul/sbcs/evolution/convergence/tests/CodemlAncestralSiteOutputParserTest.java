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
		CodemlAncestralSiteOutputParser parser2 = new CodemlAncestralSiteOutputParser(new File("~/Documents/all_work/manuscripts_presentations/nature-rebuttal/zhang_empirical/codeml_anc_all_loci/ENSG00000072364/convergentSites.out"));
		fail("Not yet implemented"); // TODO
	}

	public final void testGetAllBranchPairProbabilitiesSitewiseSummed() {
		CodemlAncestralSiteOutputParser parser = new CodemlAncestralSiteOutputParser(new File("/Users/gsjones/Downloads/ENSG00000072364/convergentSites.out"));
		float[][] data = parser.getAllBranchPairProbabilitiesSitewiseSummed();
		data.toString();
		CodemlAncestralSiteOutputParser parser2 = new CodemlAncestralSiteOutputParser(new File("/Users/joeparker/Documents/all_work/manuscripts_presentations/nature-rebuttal/zhang_empirical/codeml_anc_all_loci/ENSG00000072364/convergentSites.out"));
		data = parser2.getAllBranchPairProbabilitiesSitewiseSummed();
		data.toString();
		fail("Not yet implemented"); // TODO
	}
}
