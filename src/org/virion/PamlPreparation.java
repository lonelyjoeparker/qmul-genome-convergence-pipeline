package org.virion;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

/**
 * Class to prepare a range of input file formats by stripping stop codons and writing PAML/codeml/RAxML compatible *.phy files out.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * 
 */
public class PamlPreparation {

	/**
	 * @param args
	 */
	private File inputFile;
	private AlignedSequenceRepresentation data;
	public PamlPreparation(String string) {
		// TODO Auto-generated constructor stub
		this.inputFile = new File(string);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PamlPreparation quick = new PamlPreparation(args[0]);
		quick.go();
	}
	public void go() {
		// TODO Auto-generated method stub
		data = new AlignedSequenceRepresentation();
		try {
			data.loadSequences(inputFile, true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.removeStopCodons();
		data.writePhylipFile(inputFile.getAbsoluteFile()+".stops.removed.phy",true);
	}

}
