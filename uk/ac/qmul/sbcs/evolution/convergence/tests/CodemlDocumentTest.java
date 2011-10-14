package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.*;
public class CodemlDocumentTest extends TestCase {

	public void testPrintTemplate() {
		CodemlDocument c = new CodemlDocument();
		c.setParameter("SEQFILE", "seqfile = /pamlTest/stewart.aa");
		c.setParameter("TREEFILE", "treefile = /pamlTest/stewart.trees");
		c.setParameter("AARATEFILE", "aaRatefile = /Applications/Phylogenetics/paml44/dat/wag.dat");
		File d = new File("/pamlTest/");
		File f = new File(d, "codemlTest.ctl");
		c.write(f);
		c.writeSerialized(new File(d,"codemlTest.ser"));
		c.writeBuffered(new File(d,"codemlTest.buf.ctl"));
	}

}
