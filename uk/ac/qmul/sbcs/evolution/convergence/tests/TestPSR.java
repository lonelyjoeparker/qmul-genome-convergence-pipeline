package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;
import java.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import javax.swing.*;

public class TestPSR {

	private JFileChooser chooser = new JFileChooser();
	private File inputFile;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TestPSR().go();
	}
	
	public void go(){
		int fileChosenStatus = chooser.showOpenDialog(null);
		if(fileChosenStatus == JFileChooser.APPROVE_OPTION){
			try {
				inputFile = chooser.getSelectedFile();
				System.out.println("trying to read "+inputFile.getAbsolutePath()+" file\n");
				PhymlSequenceRepresentation PSR = new PhymlSequenceRepresentation();
				PSR.loadSequences(inputFile);
				ArrayList<String> rawFileContents = PSR.getRawInput();
				System.out.println(rawFileContents.get(0));
				System.out.println(rawFileContents.get(rawFileContents.size()-1));
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
