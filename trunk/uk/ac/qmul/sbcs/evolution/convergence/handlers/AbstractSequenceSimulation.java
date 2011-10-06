package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.util.HashMap;
import uk.ac.qmul.sbcs.evolution.convergence.PhymlSequenceRepresentation;

public abstract class AbstractSequenceSimulation {
	private PhymlSequenceRepresentation simulatedDataSet;
	private HashMap<String, String> truncatedNamesHash = new HashMap<String, String>();
	private String phylogeny;
	private int sitesToSimulate;
	private int replicates;
	
	public AbstractSequenceSimulation(){}
	
	public AbstractSequenceSimulation(HashMap<String, String> truncatedNamesHash, String phylogeny, int sitesToSimulate, int replicates){
		this.truncatedNamesHash = truncatedNamesHash;
		this.phylogeny = phylogeny;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
	}
	
	public abstract void initialiseSimulation();
	
	public abstract PhymlSequenceRepresentation simulate();
}
