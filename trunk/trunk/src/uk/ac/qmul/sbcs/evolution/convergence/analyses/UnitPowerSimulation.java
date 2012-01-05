package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

public class UnitPowerSimulation {

	private File treeFile;
	private int datasetSize;
	private int nullDataSize;
	private int numberOfTaxa;
	private int sitesToConverge;
	private String[] taxaToConverge;
	private String[] taxaToRemove;
	private String masterTaxon;
	private SequenceCodingType sequenceCodingType;
	private String model;
	private TreeMap<AamlParameters, String> AaMLparameters = new TreeMap<AamlParameters, String>();
	private EvolverSimulationSGE dataSimulation;
	private EvolverSimulationSGE nullSimulation;
	
	private File workDir;
	private File binariesLocation;
	private File dataPhylipFile;
	private File nullPhylipFile;
	private File aaDataAnalysisOutputFile;
	private File aaNullAnalysisOutputFile;
	private String runID;
	private AlignedSequenceRepresentation simulatedData;
	private AlignedSequenceRepresentation simulatedNull;

	private File aaTreeOneAnalysisOutputFile;
	private File aaTreeTwoAnalysisOutputFile;
	private File pamlDataFileAA;
	private DataSeries datalnL;
	private DataSeries nulllnL;
	private NewickTreeRepresentation treeOne;
	private TreeSet<String> taxaList;

	@Deprecated
	public UnitPowerSimulation(){}
	
	/**
	 * 
	 * @param dataSimulation
	 * @param nullSimulation
	 * @param workDir
	 * @param binariesLocation
	 * @param runID
	 * @param model
	 * @param datasetSize
	 * @param nullDataSize
	 * @param sitesToConverge
	 * @param taxaToConverge
	 * @param taxaToRemove
	 * @param masterTaxon
	 * @param sequenceCodingType
	 * @param taxaList
	 */
	public UnitPowerSimulation(EvolverSimulationSGE dataSimulation, EvolverSimulationSGE nullSimulation, File treeFile, File workDir, File binariesLocation, String runID, String model, int datasetSize, int nullDataSize, int sitesToConverge, String[] taxaToConverge, String[] taxaToRemove, String masterTaxon, SequenceCodingType sequenceCodingType,TreeSet<String> taxaList){
		this.dataSimulation = dataSimulation;
		this.nullSimulation = nullSimulation;
		this.treeFile = treeFile;
		this.workDir = workDir;
		this.binariesLocation = binariesLocation;
		this.runID = runID;
		this.model = model;
		this.datasetSize = datasetSize;
		this.nullDataSize = nullDataSize;
		this.sitesToConverge = sitesToConverge;
		this.taxaToConverge = taxaToConverge;
		this.taxaToRemove = taxaToRemove;
		this.masterTaxon = masterTaxon;
		this.sequenceCodingType = sequenceCodingType;this.taxaList = taxaList;
	}
	
	public String simulatePower(){
		/*
		 * 	Execution:
		 * 		- simulate a large dataset (the null) and a smaller one (the data) under the model parameters
		 * 		- do paml on both
		 * 		- get SSLS dataseries on both paml outputs
		 * 		- return formatted output string
		 */
		
		dataSimulation.simulateNoArg();
		simulatedData = new AlignedSequenceRepresentation();
		try {
			simulatedData.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
			simulatedData.determineInputFileDatatype();
			simulatedData.determineInputSequenceType();
//			simulatedData.removeStopCodons();
//			simulatedData.removeUnambiguousGaps();
//			simulatedData.printNumberOfInvariantSites();
//			simulatedData.printShortSequences(30);
//			simulatedData.determineInvariantSites();
//			simulatedData.printNumberOfInvariantSites();
		} catch (TaxaLimitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// CONVERGE!!
		simulatedData.simulateConvergence(masterTaxon, taxaToConverge , sitesToConverge);
		dataPhylipFile = new File(this.workDir.getAbsolutePath()+"/data.converged.phy");
		simulatedData.writePhylipFile(dataPhylipFile, true);
		
		/* Do Aaml on data simulated on spp tree */
		aaDataAnalysisOutputFile = new File(dataPhylipFile.getPath()+".aamlData.out");
		AaMLparameters.put(AamlParameters.SEQFILE, "seqfile = "+this.dataPhylipFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.OUTFILE, "outfile = "+aaDataAnalysisOutputFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		File[] treefiles = {this.treeFile};
		AlignedSequenceRepresentation[] datasets = {simulatedData};
		AamlAnalysisSGE dataAaml = new AamlAnalysisSGE(datasets, treefiles, AaMLparameters,"aamlOnData.ctl");
		dataAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		dataAaml.setExecutionBinary(new File(dataAaml.getBinaryDir(),"codeml"));
		dataAaml.setWorkingDir(workDir);
		dataAaml.RunAnalysis();
		
		TreeMap<String, Float> dataSSLS = dataAaml.getPatternSSLS();
		float[] dataSSLSlnL = new float[dataSSLS.size()];
		Iterator dataSimSSLSItr1 = dataSSLS.keySet().iterator();
		int sIndex = 0;
		while(dataSimSSLSItr1.hasNext()){
			dataSSLSlnL[sIndex] = dataSSLS.get(dataSimSSLSItr1.next());
			sIndex++;
		}
		datalnL = simulatedData.getFullSitesLnL(dataSSLS);

		
		// simulation only
		
		nullSimulation.pokeSeed();
		nullSimulation.simulateNoArg();
		simulatedNull = new AlignedSequenceRepresentation();
		try {
			simulatedNull.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
			simulatedNull.determineInputFileDatatype();
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nullPhylipFile = new File(this.workDir.getAbsolutePath()+"/null.phy");
		simulatedNull.writePhylipFile(nullPhylipFile, true);
		
		aaNullAnalysisOutputFile = new File(this.workDir.getAbsolutePath()+"/aamlNull.out");
		
		AaMLparameters = new TreeMap<AamlParameters, String>();
		AaMLparameters.put(AamlParameters.SEQFILE, "seqfile = "+this.nullPhylipFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.OUTFILE, "outfile = "+aaNullAnalysisOutputFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		datasets[0] = simulatedNull;

		AamlAnalysisSGE nullAaml = new AamlAnalysisSGE(datasets, treefiles, AaMLparameters,"aamlOnNull.ctl");
		nullAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		nullAaml.setExecutionBinary(new File(nullAaml.getBinaryDir(),"codeml"));
		nullAaml.setWorkingDir(workDir);
		nullAaml.RunAnalysis();

		TreeMap<String, Float> nullSSLS = nullAaml.getPatternSSLS();
		float[] nullSSLSlnL = new float[nullSSLS.size()];
		Iterator nullSimSSLSItr1 = nullSSLS.keySet().iterator();
		sIndex = 0;
		while(nullSimSSLSItr1.hasNext()){
			nullSSLSlnL[sIndex] = nullSSLS.get(nullSimSSLSItr1.next());
			sIndex++;
		}
		nulllnL = simulatedNull.getFullSitesLnL(nullSSLS);
		
		return "return string value.\ndata mean\t"+datalnL.getMean()+"\nnull mean\t"+nulllnL.getMean();
	}
}
