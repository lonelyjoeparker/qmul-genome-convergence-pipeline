package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * Exception class extending java.io.IOException
 * <br/>For codeml parsers, indicates some parsing error.
 */
public class CodemlParsingIOException extends IOException {

	public CodemlParsingIOException() {
		System.err.println("CodemlParsingIOException: Error parsing PAML codeml outputs");
		// TODO Auto-generated constructor stub
	}

	public CodemlParsingIOException(String arg0) {
		super(arg0);
		System.err.println("CodemlParsingIOException: Error parsing PAML codeml outputs.");
		System.err.println("CodemlParsingIOException: (additonal): "+arg0);
		// TODO Auto-generated constructor stub
	}

	public CodemlParsingIOException(Throwable arg0) {
		super(arg0);
		System.err.println("CodemlParsingIOException: Error parsing PAML codeml outputs");
		// TODO Auto-generated constructor stub
	}

	public CodemlParsingIOException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		System.err.println("CodemlParsingIOException: Error parsing PAML codeml outputs");
		// TODO Auto-generated constructor stub
	}

}
