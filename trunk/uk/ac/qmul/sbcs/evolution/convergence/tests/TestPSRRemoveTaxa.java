package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;
import java.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;
import javax.swing.*;

public class TestPSRRemoveTaxa {

	private JFileChooser chooser = new JFileChooser();
	private File inputFile;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TestPSRRemoveTaxa().go();
	}
	
	public void go(){
		int fileChosenStatus = chooser.showOpenDialog(null);
		if(fileChosenStatus == JFileChooser.APPROVE_OPTION){
			try {
				inputFile = chooser.getSelectedFile();
				System.out.println("trying to read "+inputFile.getAbsolutePath()+" file\n");
				PhymlSequenceRepresentation PSR = new PhymlSequenceRepresentation();
				try{
					PSR.loadSequences(inputFile);
				}catch(TaxaLimitException ex){
					ex.printStackTrace();
				}
				PSR.printShortSequences(20);
				System.out.println("read "+PSR.getNumberOfSites()+" sites and "+PSR.getNumberOfTaxa()+" taxa.");
				String first = PSR.getTaxaList().first();
				String last = PSR.getTaxaList().last();
				System.out.println("removing "+last+" and "+first);
				String[] taxaToRemove = {last,first, last};
				PSR.removeTaxa(taxaToRemove);
				PSR.printShortSequences(20);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
