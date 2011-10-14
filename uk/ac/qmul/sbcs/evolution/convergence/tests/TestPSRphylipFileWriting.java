package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;
import java.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

import javax.swing.*;

public class TestPSRphylipFileWriting {

	private JFileChooser chooser = new JFileChooser();
	private File inputFile;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			new TestPSRphylipFileWriting().go();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void go() throws TaxaLimitException{
		int fileChosenStatus = chooser.showOpenDialog(null);
		if(fileChosenStatus == JFileChooser.APPROVE_OPTION){
			try {
				inputFile = chooser.getSelectedFile();
				System.out.println("trying to read "+inputFile.getAbsolutePath()+" file\n");
				AlignedSequenceRepresentation PSR = new AlignedSequenceRepresentation();
				PSR.loadSequences(inputFile);
				PSR.printShortSequences(20);
				System.out.println("read "+PSR.getNumberOfSites()+" sites and "+PSR.getNumberOfTaxa()+" taxa.");
				PSR.writePhylipFile("/Users/gsjones/Documents/all_work/programming/java/testingOutputAndOtherCrap/testPSRwrite.txt");
				try{
					PSR.translate(true);
					PSR.printShortSequences(20);
					PSR.writePhylipFile("/Users/gsjones/Documents/all_work/programming/java/testingOutputAndOtherCrap/testPSRwriteTRANSLATED.txt");
				}catch(Exception e){}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
