package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.File;
import java.util.HashMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.BasemlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;

public class EvolverSimulation extends AbstractSequenceSimulation {
	private AlignedSequenceRepresentation simulatedDataSet;
	private HashMap<String, String> truncatedNamesHash = new HashMap<String, String>();
	private HashMap<String, String> parameterSet = new HashMap<String, String>();
	private String phylogeny;
	private String activeTemplate;
	private int sitesToSimulate;
	private int replicates;
	boolean hasBeenWritten = false;
	boolean hasRun = false;

	public EvolverSimulation(){}

	public EvolverSimulation(HashMap<String, String> truncatedNamesHash, String phylogeny, int sitesToSimulate, int replicates){
		this.truncatedNamesHash = truncatedNamesHash;
		this.phylogeny = phylogeny;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
		// TODO consider explicit seed?
	}

	@Override
	public void initialiseSimulation() {
		// TODO Auto-generated method stub

	}

	@Override
	public AlignedSequenceRepresentation simulate() {
		// TODO Auto-generated method stub
		// TODO provision to re-sun this in order to resimulate
		return null;
	}
}
