package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;
import java.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.*;

public class TestPSR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("trying to read "+args[0]+" file\n");
		File inputFile = new File(args[0]);
		PhymlSequenceRepresentation PSR = new PhymlSequenceRepresentation();
		PSR.loadSequences(inputFile);
		ArrayList<String> rawFileContents = PSR.getRawInput();
		System.out.println(rawFileContents.get(0));
		System.out.println(rawFileContents.get(rawFileContents.size()-1));
	}

}
