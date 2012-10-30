package org.virion;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

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
	private void go() {
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
