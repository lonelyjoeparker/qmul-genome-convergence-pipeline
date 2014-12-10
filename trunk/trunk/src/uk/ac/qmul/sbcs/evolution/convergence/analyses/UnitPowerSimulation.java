package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaAbsentFromAlignmentException;
import uk.ac.qmul.sbcs.evolution.convergence.VariantSitesUnavailableException;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;

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
		this.numberOfTaxa = taxaList.size();
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
		this.sequenceCodingType = sequenceCodingType;
		this.taxaList = taxaList;
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
		try {
			simulatedData.simulateConvergenceInVariantSites(masterTaxon, taxaToConverge , sitesToConverge);
		} catch (TaxaAbsentFromAlignmentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (VariantSitesUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dataPhylipFile = new File(this.workDir.getAbsolutePath()+"/data.converged.phy");
		simulatedData.writePhylipFile(dataPhylipFile, true);
		
		/* Do Aaml on data simulated on spp tree */
		aaDataAnalysisOutputFile = new File(dataPhylipFile.getPath()+".aamlData.out");
		AaMLparameters.put(AamlParameters.SEQFILE, "seqfile = "+this.dataPhylipFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.OUTFILE, "outfile = "+aaDataAnalysisOutputFile.getAbsolutePath());
		AaMLparameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		AaMLparameters.put(AamlParameters.ALPHA, "alpha = 0.2545");
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
		
//		nullSimulation.pokeSeed();
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
		AaMLparameters.put(AamlParameters.ALPHA, "alpha = 0.2545");
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
		
		float dataMean = datalnL.getMean();
		float dataMedi = datalnL.getMedian();
		float data95ci;
		float data05ci;
		float nullMean = nulllnL.getMean();
		float nullMedi = nulllnL.getMedian();
		float null95ci;
		float null10ci;
		float null09ci;
		float null08ci;
		float null07ci;
		float null06ci;
		float null05ci;
		float null04ci;
		float null03ci;
		float null02ci;
		float null01ci;
		try {
			data95ci = datalnL.getValueAtPercentile(95);
			data05ci = datalnL.getValueAtPercentile(5);
			null95ci = nulllnL.getValueAtPercentile(95);
			null10ci = nulllnL.getValueAtPercentile(10);
			null09ci = nulllnL.getValueAtPercentile(9);
			null08ci = nulllnL.getValueAtPercentile(8);
			null07ci = nulllnL.getValueAtPercentile(7);
			null06ci = nulllnL.getValueAtPercentile(6);
			null05ci = nulllnL.getValueAtPercentile(5);
			null04ci = nulllnL.getValueAtPercentile(4);
			null03ci = nulllnL.getValueAtPercentile(3);
			null02ci = nulllnL.getValueAtPercentile(2);
			null01ci = nulllnL.getValueAtPercentile(1);
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			data95ci = 999.9f;
			data05ci = 999.9f;
			null95ci = 999.9f;
			null10ci = 999.9f;
			null09ci = 999.9f;
			null08ci = 999.9f;
			null07ci = 999.9f;
			null06ci = 999.9f;
			null05ci = 999.9f;
			null04ci = 999.9f;
			null03ci = 999.9f;
			null02ci = 999.9f;
			null01ci = 999.9f;
		}
		int dataObsLessThan10 = datalnL.numObservationsLessThan(null10ci);
		int dataObsLessThan9 = datalnL.numObservationsLessThan(null09ci);
		int dataObsLessThan8 = datalnL.numObservationsLessThan(null08ci);
		int dataObsLessThan7 = datalnL.numObservationsLessThan(null07ci);
		int dataObsLessThan6 = datalnL.numObservationsLessThan(null06ci);
		int dataObsLessThan5 = datalnL.numObservationsLessThan(null05ci);
		int dataObsLessThan4 = datalnL.numObservationsLessThan(null04ci);
		int dataObsLessThan3 = datalnL.numObservationsLessThan(null03ci);
		int dataObsLessThan2 = datalnL.numObservationsLessThan(null02ci);
		int dataObsLessThan1 = datalnL.numObservationsLessThan(null01ci);
		
		StringBuilder output = new StringBuilder();
		
		output.append(this.numberOfTaxa+"\t"+this.datasetSize+"\t"+this.nullDataSize+"\t"+this.sitesToConverge+"\t"+this.taxaToConverge.length+"\t");
		output.append("Dataset\t95%\tMean\tMedian\t5%\tData\t"+data95ci+"\t"+dataMean+"\t"+dataMedi+"\t"+data05ci+"\tNull\t"+null95ci+"\t"+nullMean+"\t"+nullMedi+"\t"+null05ci+"\t");
		output.append("Num sites < % (Actual convergent sites = "+this.sitesToConverge+")\t");
		output.append("10\t"+dataObsLessThan10+"\t");
		output.append("09\t"+dataObsLessThan9+"\t");
		output.append("08\t"+dataObsLessThan8+"\t");
		output.append("07\t"+dataObsLessThan7+"\t");
		output.append("06\t"+dataObsLessThan6+"\t");
		output.append("05\t"+dataObsLessThan5+"\t");
		output.append("04\t"+dataObsLessThan4+"\t");
		output.append("03\t"+dataObsLessThan3+"\t");
		output.append("02\t"+dataObsLessThan2+"\t");
		output.append("01\t"+dataObsLessThan1+"\t");
		output.append("\n");
		return output.toString();
	}
}
