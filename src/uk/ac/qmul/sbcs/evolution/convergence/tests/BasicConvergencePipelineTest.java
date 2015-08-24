package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;

import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.BasemlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.runners.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;

public class BasicConvergencePipelineTest {
	/*
	 * A basic flow for pipeline analysis:
	 * 		
	 * 		Read in data
	 * 		Optimise tree in RAxML
	 * 
	 * 		Paml (baseml) on data.nt
	 * 			- get params.nt
	 * 			- write input.nt.SSLS
	 * 		Simulate using params.nt
	 * 		Paml (baseml) on simulated.nt
	 * 			- write simulated.nt.SSLS
	 * 
	 * 		Paml (aaml) on data.aa
	 * 			- get params.aa
	 * 			- write input.aa.SSLS
	 * 		Simulate using params.aa
	 * 		Paml (aaml) on data.aa
	 * 			- write simulated.aa.SSLS
	 * 
	 * 		Goto pub; exit();
	 */
	private String runID = "secondCongruenceTest";
	private File workDir;
	private File sourceData;
	private AlignedSequenceRepresentation sourceDataASR;
	private SequenceCodingType inputSequenceCodingType;
	private File sourceTree;
	private String sourceTreeString;
	private BasemlResultReader ntAnalysisOutput;
	private File ntAnalysisOutputFile;
	private File optimisedTreeNT;
	private File pamlDataFileNT;
	private File simulatedNTData;
	private AlignedSequenceRepresentation simulatedNTDataASR;
	private File pamlDataFileNTSimulated;
	private File pamlCTlFileNT;
	private File pamlCTLFileNTSimulated;
	private File SSLSnt;
	private File SSLSntSimulated;
	private File sourceDataTranslatedAA;
	private File optimisedTreeAA;
	private File pamlDataFileAA;
	private File pamlDataFileAASimulated;
	private File pamlCTlFileAA;
	private File pamlCTLFileAASimulated;
	private File SSLSaa;
	private File SSLSaaTranslated;
	private DataSeries empiricalNTDataLnL;
	private DataSeries simulatedNTDataLnL;

	public static void main(String[] args){
		BasicConvergencePipelineTest pipeline = new BasicConvergencePipelineTest();
		pipeline.readData(args[0]);
		pipeline.sourceTree = new File(args[1]);
		pipeline.optimiseTreeNT();
		pipeline.doBasemlOnData();
		pipeline.simulateNT();
		pipeline.readSimulatedNTdata();
		pipeline.doBasemlOnSimulatedData();
		pipeline.compareSSLS(pipeline.empiricalNTDataLnL, pipeline.simulatedNTDataLnL);
		pipeline.optimiseTreeAA();
		pipeline.doAamlOnData();
		pipeline.simulateAA();
		pipeline.doAamlOnSimulatedData();
		pipeline.finalHousekeeping();
	}

	public void readData(String inputFile){
		sourceData = new File(inputFile);
		assert(this.sourceData.canRead());
		System.out.println(sourceData.getAbsolutePath().toString());
		workDir = new File(System.getProperty("user.dir"));				// Use this to set work dir as the ECLIPSE (or calling shell) run dir
		workDir = new File("/pamlTest/trialDataFromGeorgia");
		System.out.println(workDir.getAbsolutePath().toString());
		this.sourceDataASR = new AlignedSequenceRepresentation();
		try {
			sourceDataASR.loadSequences(sourceData,true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.printShortSequences(30);
		assert(sourceDataASR.renameTaxon("Monodelphis","Monodelphi"));
		assert(sourceDataASR.renameTaxon("Rhinolophus","Rhinolophu"));
		sourceDataASR.printNumberOfSites();
		sourceDataASR.printNumberOfTaxa();
		sourceDataASR.removeUnambiguousGaps();
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		pamlDataFileNT = new File(sourceData.getAbsoluteFile()+runID+"_pamlNT.phy");
		sourceDataASR.writePhylipFile(pamlDataFileNT, true);
	}

	public void optimiseTreeNT(){
		// TODO RAxML enums need to be properly defined.
		// TODO RAxMLAnalysis has not been tested.
		RAxMLAnalysis ra = new RAxMLAnalysis(pamlDataFileNT, workDir, sourceTree, runID, RAxMLAnalysis.NTmodelOptions.GTRCAT, RAxMLAnalysis.algorithmOptions.e);
		ra.RunAnalysis();
		System.out.println(ra.getOutputFile().getAbsolutePath());
	}

	public void doBasemlOnData(){
		this.ntAnalysisOutputFile = new File(pamlDataFileNT.getPath()+".baseml.out");
		TreeMap<BasemlParameters, String> parameters = new TreeMap<BasemlParameters, String>();
		parameters.put(BasemlParameters.SEQFILE, "seqfile = "+pamlDataFileNT.getAbsolutePath());
		parameters.put(BasemlParameters.TREEFILE, "treefile = "+sourceTree.getAbsolutePath());
		parameters.put(BasemlParameters.OUTFILE, "outfile = "+ntAnalysisOutputFile.getAbsolutePath());
		File[] treefiles = {sourceTree};
		AlignedSequenceRepresentation[] datasets = {sourceDataASR};
		BasemlAnalysis a = new BasemlAnalysis(datasets, treefiles, parameters,"basemlOnActualDataWhoop.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/src/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"baseml"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.setWorkingDir(workDir);
		a.RunAnalysis();

		/**
		 * FIXME we need to sort out where in the chain the location of the paml CTL FILE is set
		 * 			- at the moment it is sort-of set here according to the a.setWorkingDir() call
		 * 			- but it is still trying to exe in the eclipse dir
		 * 			- this needs to be clarified *right* now
		 * RESOLVED 31/10/2011
		 * 
		 * TODO the treefile is still screwy, btw..
		 */
		TreeMap<String, Float> ntDataSSLS = a.getPatternSSLS();
		float[] ntDataSSLSlnL = new float[ntDataSSLS.size()];
		Iterator dataSSLSItr = ntDataSSLS.keySet().iterator();
		int sIndex = 0;
		while(dataSSLSItr.hasNext()){
			ntDataSSLSlnL[sIndex] = ntDataSSLS.get(dataSSLSItr.next());
			sIndex++;
		}
		empiricalNTDataLnL = new DataSeries(ntDataSSLSlnL,"nt lnL data");
		this.ntAnalysisOutput = new BasemlResultReader(this.ntAnalysisOutputFile);
	}
	
	public void simulateNT(){
		// TODO get parametisation from treefile and pamlDataFileNT.getPath()+"baseml.out"
//		String simTree = "(((((((Bos:0.05785052,Tursiops:0.02951931):0.02616886,(Canis:0.04529243,Felis:0.03771493):0.02116793):0.00168035,Equus:0.04477773):0.00116782,(((Eidolon:0.01985485,Pteropus:0.01366940):0.03294410,(Megaderma:0.05900343,Rhinolophu:0.04393850):0.00690785):0.00372053,(Myotis:0.05432516,Pteronotus:0.05521158):0.01233150):0.01037568):0.00301172,Erinaceus:0.13013506):0.00750098,((Homo:0.00253268,Pan:0.00259210):0.04837197,Mus:0.16652704):0.00781731):0.00829466,(Dasypus:0.07494703,Loxodonta:0.06742841):0.00320961,Monodelphi:0.33543707);";
		File f = new File(workDir+"/testEvolverDocWrite_NT");
		EvolverSimulation es = new EvolverSimulation(workDir,f,this.ntAnalysisOutput.getOptimisedTree(),this.sourceDataASR.getNumberOfTaxa(),1000,1,this.inputSequenceCodingType);
//		String exeString = "touch "+f.getAbsolutePath();
//		System.out.println("\n\nattempting command "+exeString);
//		try {
//			Process p = Runtime.getRuntime().exec(exeString);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assert(f.canRead());
//		assert(f.canWrite());
		es.initialiseSimulation();
		es.addParameterReadyToSet("PAMLFLAG", "0");
		es.setParameter("RATES", ntAnalysisOutput.getBaseRates());
		es.setParameter("ALPHA", ntAnalysisOutput.getAlpha());
		es.setParameter("BASEFREQS", ntAnalysisOutput.getBaseFreqs());
//		es.setParameter("BASEFREQS", "0.25  0.25  0.25  0.25");
		es.simulateNoArg();
//		this.doSystem("/Applications/Phylogenetics/PAML/paml44_myVersion/src/evolver 5 "+f.getAbsolutePath());
		new VerboseSystemCommand("cp -v "+System.getProperty("user.dir")+"/mc.paml /pamlTest/trialDataFromGeorgia/evolver.output.phy");
		this.simulatedNTData = new File("/pamlTest/trialDataFromGeorgia/evolver.output.phy");
	}

	public void readSimulatedNTdata(){
		this.simulatedNTDataASR = new AlignedSequenceRepresentation();
		try {
			this.simulatedNTDataASR.loadSequences(simulatedNTData, true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doBasemlOnSimulatedData(){
		// TODO this should be pretty easy to implement by copying the other method, right?
		TreeMap<BasemlParameters, String> parameters = new TreeMap<BasemlParameters, String>();
		parameters.put(BasemlParameters.SEQFILE, "seqfile = "+simulatedNTData.getAbsolutePath());
		parameters.put(BasemlParameters.TREEFILE, "treefile = "+sourceTree.getAbsolutePath());
		parameters.put(BasemlParameters.OUTFILE, "outfile = "+simulatedNTData.getPath()+"baseml.out");
		File[] treefiles = {sourceTree};
		AlignedSequenceRepresentation[] datasets = {this.simulatedNTDataASR};
		BasemlAnalysis a = new BasemlAnalysis(datasets, treefiles, parameters,"basemlOnActualSimulatedDataWhoop.ctl");
		a.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/src/").getAbsoluteFile());
		a.setExecutionBinary(new File(a.getBinaryDir(),"baseml"));
		System.out.println(a.getBinaryDir().getAbsolutePath());
		System.out.println(a.getExecutionBinary().getAbsolutePath());
		a.setWorkingDir(workDir);
		a.RunAnalysis();

		/**
		 * FIXME we need to sort out where in the chain the location of the paml CTL FILE is set
		 * 			- at the moment it is sort-of set here according to the a.setWorkingDir() call
		 * 			- but it is still trying to exe in the eclipse dir
		 * 			- this needs to be clarified *right* now
		 * RESOLVED 31/10/2011
		 * 
		 * TODO the treefile is still screwy, btw..
		 */
		TreeMap<String,Float> simulatedSSLS = a.getPatternSSLS();
		assert(simulatedSSLS.size()>1);
		float[] simulatedSSLSlnL = new float[simulatedSSLS.size()];
		int sIndex = 0;
		Iterator simulatedSSLSItr = simulatedSSLS.keySet().iterator();
		while(simulatedSSLSItr.hasNext()){
			String key = (String)simulatedSSLSItr.next();
			assert(key != null);
			simulatedSSLSlnL[sIndex] = simulatedSSLS.get(key);
			sIndex++;
		}
		for(float lnL:simulatedSSLSlnL){
			System.out.println("\tlnL: "+lnL);
		}
		simulatedNTDataLnL = new DataSeries(simulatedSSLSlnL, "Simulated NT lnL");
	}

	public void compareSSLS(DataSeries empirical, DataSeries simulated){
		System.out.println("comparing data series '"+empirical.getName()+"' and '"+simulated.getName()+"'");
		System.out.println(empirical.getName()+" mean: "+empirical.getMean()+" (SE: "+empirical.getSE()+")");
		System.out.println(simulated.getName()+" mean: "+simulated.getMean()+" (SE: "+simulated.getSE()+")");
		int[] intervals = {0,5,25,50,75,95,100};
		float[] empiricalPercentiles = new float[7];
		try {
			for(int i=0;i<7;i++){
				empiricalPercentiles[i] = empirical.getValueAtPercentile(intervals[i]);
			}
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float[] simulatedPercentiles = new float[7];
		try {
			for(int i=0;i<7;i++){
				simulatedPercentiles[i] = simulated.getValueAtPercentile(intervals[i]);
			}
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Interval\tEmpirical\t\tSimulated\n===================================================");
		for(int i=0;i<7;i++){
			System.out.println(intervals[i]+"\t\t"+empiricalPercentiles[i]+"\t\t"+simulatedPercentiles[i]);
		}
		try {
			System.out.println("\nSimulated data lower 5% (value: "+simulated.getValueAtPercentile(5)+") overlaps empirical data at percentile: "+empirical.getPercentileCorrespondingToValue(simulated.getValueAtPercentile(5))+" (value: "+empirical.getValueAtPercentile(empirical.getPercentileCorrespondingToValue(simulated.getValueAtPercentile(5)))+")");
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void translateData(){}
	public void optimiseTreeAA(){}
	public void doAamlOnData(){}
	public void simulateAA(){}
	public void doAamlOnSimulatedData(){}
	public void finalHousekeeping(){}

	public void doSystem(String exeString){
		System.out.println("\n\nattempting command "+exeString);
		try {
			Process p = Runtime.getRuntime().exec(exeString);
			BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = iReader.readLine();
			int i = 0;
			while(line != null){
				System.out.println(i+" "+line);
				line = iReader.readLine();
				i++;
			}
			System.out.println("done with output\nerror:");
			BufferedReader eReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = eReader.readLine();
			int e = 0;
			while(line != null){
				System.out.println(e+" "+line);
				line = eReader.readLine();
				e++;
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void simulateNTOldMethod(){
		// TODO get parametisation from treefile and pamlDataFileNT.getPath()+"baseml.out"
		String simTree = "(((((((Bos:0.05785052,Tursiops:0.02951931):0.02616886,(Canis:0.04529243,Felis:0.03771493):0.02116793):0.00168035,Equus:0.04477773):0.00116782,(((Eidolon:0.01985485,Pteropus:0.01366940):0.03294410,(Megaderma:0.05900343,Rhinolophu:0.04393850):0.00690785):0.00372053,(Myotis:0.05432516,Pteronotus:0.05521158):0.01233150):0.01037568):0.00301172,Erinaceus:0.13013506):0.00750098,((Homo:0.00253268,Pan:0.00259210):0.04837197,Mus:0.16652704):0.00781731):0.00829466,(Dasypus:0.07494703,Loxodonta:0.06742841):0.00320961,Monodelphi:0.33543707);";
		File f = new File(workDir+"/testEvolverDocWrite_NT");
		String exeString = "touch "+f.getAbsolutePath();
		System.out.println("\n\nattempting command "+exeString);
		try {
			Process p = Runtime.getRuntime().exec(exeString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(f.canRead());
		assert(f.canWrite());
		EvolverDocument ed = new EvolverDocument(SequenceCodingType.DNA, f);
		assert(ed.initialisedWithSequenceType);
		ed.initialiseParameters();
		ed.setParameter("PAMLFLAG", "0");
		ed.setParameter("TREE_STRING", simTree);
		ed.setParameter("NUM_SEQS", "18");
		ed.setParameter("NREPS", "1");
		ed.finalizeParameters();
		ed.write();
		this.doSystem("/Applications/Phylogenetics/PAML/paml44_myVersion/src/evolver 5 "+f.getAbsolutePath());
		this.doSystem("cp -v /Users/gsjones/Documents/all_work/programming/java/QMUL\\ genome\\ convergence\\ project/mc.paml /pamlTest/trialDataFromGeorgia/evolver.output.phy");
		// FIXME this copy operation does NOT work as implemented here, because the exec parser literally presents the slashes...
		this.doSystem("echo that\\ was\\ nice"); // outputs "that\ was\ nice" but Runtime.getRuntime().exec() parses weirdly..
		this.simulatedNTData = new File("/pamlTest/trialDataFromGeorgia/evolver.output.phy");
	}
}
