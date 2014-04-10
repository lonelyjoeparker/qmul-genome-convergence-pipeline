package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParsingIOException;
import junit.framework.TestCase;

public class CodemlParsingIOExceptionTest extends TestCase {

	public CodemlParsingIOExceptionTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testException() throws Exception{
		throw new CodemlParsingIOException();
	}

	public void testExceptionString() throws Exception{
		throw new CodemlParsingIOException("Additonal error info call");
	}
}
