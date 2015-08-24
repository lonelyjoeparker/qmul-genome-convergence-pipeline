package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.BasemlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.EvolverDocument;

/**
 * 
 * @author Joe Parker
 * @since 23/10/2011
 * @version 0.0.1
 */
public class EvolverSimulation extends AbstractSequenceSimulation {
	private AlignedSequenceRepresentation simulatedDataSet;
	private HashMap<String, String> truncatedNamesHash = new HashMap<String, String>();
	private HashMap<String, String> parameterSet = new HashMap<String, String>();
	private SequenceCodingType simulationSequenceCodingType;
	private String phylogeny;
	private String activeTemplate;
	private int sitesToSimulate;
	private int replicates;
	private int numTaxa;
	boolean hasBeenWritten = false;
	boolean hasRun = false;
	boolean initialised = false;
	private EvolverDocument activeSimDoc;
	private File workingDir;
	private File evolverDocumentFile;
	private File evolverOutputFile;
	private File binaryLocation;

	public EvolverSimulation(){}

	public EvolverSimulation(HashMap<String, String> truncatedNamesHash, String phylogeny, int sitesToSimulate, int replicates, SequenceCodingType sqt){
		this.truncatedNamesHash = truncatedNamesHash;
		this.phylogeny = phylogeny;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
		this.simulationSequenceCodingType = sqt;
		// TODO consider explicit seed?
	}

	public EvolverSimulation(File binary, HashMap<String, String> truncatedNamesHash, String phylogeny, int sitesToSimulate, int replicates, SequenceCodingType sqt){
		this.binaryLocation = binary;
		this.truncatedNamesHash = truncatedNamesHash;
		this.phylogeny = phylogeny;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
		this.simulationSequenceCodingType = sqt;
		// TODO consider explicit seed?
	}

	/**
	 * @since 31/10/2011
	 * @param phylogeny
	 * @param sitesToSimulate
	 * @param replicates
	 * @param sqt - coding type of simulation - enum (DNA,RNA,codon,AA)
	 * 
	 * This should be the preferred constructor.
	 */
	public EvolverSimulation(File binary, File workingDir, File ctlFile, String phylogeny, int numTaxa, int sitesToSimulate, int replicates, SequenceCodingType sqt){
		this.binaryLocation = binary;
		this.workingDir = workingDir;
		this.phylogeny = phylogeny;
		this.numTaxa = numTaxa;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
		this.simulationSequenceCodingType = sqt;
		this.evolverDocumentFile = ctlFile;
		this.activeSimDoc = new EvolverDocument(this.simulationSequenceCodingType, this.evolverDocumentFile);
	}

	public EvolverSimulation(File workingDir, File ctlFile, String phylogeny, int numTaxa, int sitesToSimulate, int replicates, SequenceCodingType sqt){
		this.workingDir = workingDir;
		this.phylogeny = phylogeny;
		this.numTaxa = numTaxa;
		this.sitesToSimulate = sitesToSimulate;
		this.replicates = replicates;
		this.simulationSequenceCodingType = sqt;
		this.evolverDocumentFile = ctlFile;
		this.activeSimDoc = new EvolverDocument(this.simulationSequenceCodingType, this.evolverDocumentFile);
	}

	public void initialiseSimulation() {
		assert(activeSimDoc.initialisedWithSequenceType);
		activeSimDoc.initialiseParameters();
		parameterSet = new HashMap<String, String>();
		initialised = true;
		activeSimDoc.setParameter("NREPS", ""+this.replicates);
		activeSimDoc.setParameter("NUM_SEQS", ""+this.numTaxa);
		activeSimDoc.setParameter("NUM_SITES", ""+this.sitesToSimulate);
		activeSimDoc.setParameter("TREE_STRING", phylogeny);
	}

	@Override
	@Deprecated
	public AlignedSequenceRepresentation simulate() {
		assert(false);
		return null;
	}
	
	public void simulateNoArg() {
		Iterator pItr = parameterSet.keySet().iterator();
		while(pItr.hasNext()){
			String param = (String)pItr.next();
			activeSimDoc.setParameter(param, parameterSet.get(param));
		}
		activeSimDoc.finalizeParameters();
		activeSimDoc.write();
		// TODO provision to re-sun this in order to resimulate
		// TODO evolver binary location is currently HARD CODED
		switch(this.simulationSequenceCodingType){
		case DNA: 
			new VerboseSystemCommand(this.binaryLocation+" 5 "+evolverDocumentFile.getAbsolutePath());
			break;
		case RNA:
			// FIXME need to check the theory/flow design here.. if the input actual data was RNA will the correct nt freqs be available???
			new VerboseSystemCommand(this.binaryLocation+" 5 "+evolverDocumentFile.getAbsolutePath());
			break;
		case CODON: 
			new VerboseSystemCommand(this.binaryLocation+" 6 "+evolverDocumentFile.getAbsolutePath());
			break;
		case AA: 
			new VerboseSystemCommand(this.binaryLocation+" 7 "+evolverDocumentFile.getAbsolutePath());
			break;
		default: assert(false); break;
		}
		// FIXME 	IMPORTANT
		//			
		//			This method is meant to return a functional ASR object representing the simulated data.
		//			At the moment it doesn't. You have to find the output file, and read it in as a new ASR.
		//			DO NOT GET CAUGHT OUT BY THIS
		//
		//			Seriously....
		//
		//				...........don't!
		//
		//			31/10/2011
		//
		// NOTE		(later, 31/10/2011)
		//			Temporarily fixed this by changing return type. But this breaks the spirit of the class inheritance design.
		//			So still need to think about this.
	}

	/**
	 * 
	 * @param param - parameter to set
	 * @param value - value of the parameter
	 * @return returns FALSE if not initialised. Will throw assertion error if assertions are used.
	 */
	public boolean setParameter(String param, String value){
		if(initialised){
			activeSimDoc.setParameter(param, value);
			return true;
		}else{
			assert(false);
			return false;
		}
	}

	/**
	 * 
	 * @param param - parameter to addToThePendingSetList
	 * @param value - value of the parameter
	 * @return returns FALSE if not initialised. Will throw assertion error if assertions are used.
	 */
	public boolean addParameterReadyToSet(String param, String value){
		if(initialised){
			activeSimDoc.setParameter(param, value);
			return true;
		}else{
			assert(false);
			return false;
		}
	}

	public File getBinaryLocation() {
		return binaryLocation;
	}

	public void setBinaryLocation(File binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
}
