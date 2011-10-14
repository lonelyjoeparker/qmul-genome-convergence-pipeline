package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.util.HashMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;

public class SeqGenSimulation extends AbstractSequenceSimulation {
	private AlignedSequenceRepresentation simulatedDataSet;
	private HashMap<String, String> truncatedNamesHash = new HashMap<String, String>();
	private String phylogeny;
	private int sitesToSimulate;
	private int replicates;

	public SeqGenSimulation(){}

	public SeqGenSimulation(HashMap<String, String> truncatedNamesHash, String phylogeny, int sitesToSimulate, int replicates){
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
