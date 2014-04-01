package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.ArrayList;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;

public class CodemlRstParser {

	private File rstFile;
	private ArrayList<String> rawData;
	private CodemlModel[] models;
	
	public CodemlRstParser(String string) {
		this.rstFile = new File(string);
	}

	/**
	 * Parses output from the codeml 'rst' files, using a uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel as the data representation
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CodemlRstParser(args[0]).parse();
	}

	/**
	 * Parse the output.
	 * As each new model is encountered, create a CodemlModel for it.
	 */
	private void parse() {
		// TODO Auto-generated method stub
		
	}

	private void writeOutput(){}
	
	private void callRegressions(){};
}
