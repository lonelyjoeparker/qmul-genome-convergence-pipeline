package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;

import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
/**
 * 
 * @author Joe Parker
 * @since 09/11/2011
 * @version 0.0.1
 * 
 * An analysis to determine the power of the SSLS/Paml-based congruence test.
 * 
 * <b>Basic execution:</b>
 * <ul>
 * 	<li>Input 22 taxon alignment, get parameters from a single PAML (perhaps this should happen at runner level)</li>
 * 	<li>Simulate sites</li>
 * 	<li>Paml on simulated sites</li>
 * 	<li>Converge sites</li>
 * 	<li>Paml on converged simulated sites</li>
 * 	<li>Summarise and get percentiles</li>
 * </ul>
 */

public class CongruenceTestPowerSimulation {
	AlignedSequenceRepresentation simulatedData;
	AlignedSequenceRepresentation simulatedNull;
	int numberOfDataSites;
	int numberOfDataTaxa;
	int numberOfNullSites;
	int numberOfConvergedSitesToModel;
	File evolverBinary;
	File workDir;
	String tree;
	String binariesLocation;
	
	public CongruenceTestPowerSimulation(AlignedSequenceRepresentation data, int simSites, int converge){
		this.simulatedData = data;
		this.simulatedNull = new AlignedSequenceRepresentation();
		this.numberOfDataSites = data.getNumberOfSites();
		this.numberOfDataTaxa = data.getNumberOfTaxa();
		this.numberOfNullSites = simSites;
		this.numberOfConvergedSitesToModel = converge;
		
	}
	
	public void go(String alpha, String treeLen){
		File f = new File(workDir+"/testSimulationsFromParamsNucleotides");
		EvolverSimulationSGE es = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfDataTaxa,numberOfDataSites,1,SequenceCodingType.AA);
		es.initialiseSimulation();
		es.addParameterReadyToSet("PAMLFLAG", "0");
		es.setParameter("ALPHA", alpha);
		es.setParameter("TREE_LENGTH", treeLen);
		es.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
		es.printCurrentParams();
		es.simulateNoArg();
		try {
			simulatedNull.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
		} catch (TaxaLimitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		 * OK, this is a little OTT. What do we actually *need* to do?
		 * 
		 * 	0 - Don't fuck around getting parameters from empirical data at runtime
		 * 	1 - Separate constructors for parametized and unparametized (hard-coded) sims
		 * 	2 - Separate classes for codeml / baseml / aaml analyses
		 * 	3 - Instantiate appropriate (large) simulation and run correct paml for null
		 * 	4 - Get a subset of sites, say 1000, and converge n
		 * 	5 - report %position of each of the converged sites, number converged, number of taxa, etc 
		 */

//
//		AlignedSequenceRepresentation simulatedSpp = new AlignedSequenceRepresentation();
//		try {
//			simulatedSpp.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
//		} catch (TaxaLimitException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		/* Do Aaml on data simulated on spp tree */
//		File aaSimTreeOneAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlSimTreeOne.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileOnePruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSimTreeOneAnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		treefiles[0] = this.treeFileOnePruned;
//		datasets[0] = simulatedSpp;
//		AamlAnalysisSGE treeOneAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeOneSim.ctl");
//		treeOneAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeOneAamlSim.setExecutionBinary(new File(treeOneAamlSim.getBinaryDir(),"codeml"));
//		treeOneAamlSim.setWorkingDir(workDir);
//		treeOneAamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeOneSimSSLS = treeOneAamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnL1 = new float[aaDataTreeOneSimSSLS.size()];
//		Iterator dataSimSSLSItr1 = aaDataTreeOneSimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItr1.hasNext()){
//			aaDataSimSSLSlnL1[sIndex] = aaDataTreeOneSimSSLS.get(dataSimSSLSItr1.next());
//			sIndex++;
//		}
////		treeOneSimlnL = new DataSeries(aaDataSimSSLSlnL1,"aa lnL data - tree 1 (sim)");
//		treeOneSimlnL = simulatedSpp.getFullSitesLnL(aaDataTreeOneSimSSLS);
//		
//		/* Do simulation on Prestin tree */
//		
//		AamlResultReader prestinTreeAaml = new AamlResultReader(aaTreeTwoAnalysisOutputFile);
//		tree = prestinTreeAaml.getOptimisedTree();
//		treeLen = prestinTreeAaml.getTreeLength();
//		alpha = "0";
//		optimisedTree = prestinTreeAaml.getOptimisedTree();
//		numberOfTaxa = this.sourceDataASR.getNumberOfTaxa();
//		numberOfSites = this.sitesInSimulations;
//		numberOfReplicates = 1;
//		EvolverSimulationSGE ep = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
//		ep.initialiseSimulation();
//		ep.addParameterReadyToSet("PAMLFLAG", "0");
//		ep.setParameter("ALPHA", alpha);
//		ep.setParameter("TREE_LENGTH", treeLen);
//		ep.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
//		ep.printCurrentParams();
//		ep.simulateNoArg();
//		AlignedSequenceRepresentation simulatedPre = new AlignedSequenceRepresentation();
//		try {
//			simulatedPre.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
//		} catch (TaxaLimitException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		/* Do Aaml on data simulated on prestin tree */
//
//		File aaSimTreeTwoAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlSimTreeTwo.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileOnePruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSimTreeTwoAnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		treefiles[0] = this.treeFileTwoPruned;
//		datasets[0] = simulatedPre;
//		AamlAnalysisSGE treeTwoAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreePreSim.ctl");
//		treeTwoAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeTwoAamlSim.setExecutionBinary(new File(treeTwoAamlSim.getBinaryDir(),"codeml"));
//		treeTwoAamlSim.setWorkingDir(workDir);
//		treeTwoAamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeTwoSimSSLS = treeTwoAamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnL2 = new float[aaDataTreeTwoSimSSLS.size()];
//		Iterator dataSimSSLSItr2 = aaDataTreeTwoSimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItr2.hasNext()){
//			aaDataSimSSLSlnL2[sIndex] = aaDataTreeTwoSimSSLS.get(dataSimSSLSItr2.next());
//			sIndex++;
//		}
////		treeTwoSimlnL = new DataSeries(aaDataSimSSLSlnL2,"aa lnL data - tree 2 (sim)");
//		treeTwoSimlnL = simulatedPre.getFullSitesLnL(aaDataTreeTwoSimSSLS);
//
//		/* Do simulation on de-novo (RAxML tree */
//
//		AamlResultReader raxTreeAaml = new AamlResultReader(this.aaTreeDeNovoAnalysisOutputFile);
//		tree = raxTreeAaml.getOptimisedTree();
//		treeLen = raxTreeAaml.getTreeLength();
//		alpha = "0";
//		optimisedTree = raxTreeAaml.getOptimisedTree();
//		numberOfTaxa = this.sourceDataASR.getNumberOfTaxa();
//		numberOfSites = this.sitesInSimulations;
//		numberOfReplicates = 1;
//		EvolverSimulationSGE er = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
//		er.initialiseSimulation();
//		er.addParameterReadyToSet("PAMLFLAG", "0");
//		er.setParameter("ALPHA", alpha);
//		er.setParameter("TREE_LENGTH", treeLen);
//		er.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
//		er.printCurrentParams();
//		er.simulateNoArg();
//		AlignedSequenceRepresentation simulatedRax = new AlignedSequenceRepresentation();
//		try {
//			simulatedRax.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
//		} catch (TaxaLimitException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		/* Do Aaml on data simulated on RAxML (de novo) tree */
//
//		File aaSimTreeDeNovoAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlSimTreeDeNovo.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSimTreeDeNovoAnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		treefiles[0] = this.treeFileTwoPruned;
//		datasets[0] = simulatedRax;
//		AamlAnalysisSGE treeDeNovoAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeRaxSim.ctl");
//		treeDeNovoAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeDeNovoAamlSim.setExecutionBinary(new File(treeDeNovoAamlSim.getBinaryDir(),"codeml"));
//		treeDeNovoAamlSim.setWorkingDir(workDir);
//		treeDeNovoAamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeDeNovoSimSSLS = treeDeNovoAamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnLrax = new float[aaDataTreeDeNovoSimSSLS.size()];
//		Iterator dataSimSSLSItrrax = aaDataTreeDeNovoSimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItrrax.hasNext()){
//			aaDataSimSSLSlnLrax[sIndex] = aaDataTreeDeNovoSimSSLS.get(dataSimSSLSItrrax.next());
//			sIndex++;
//		}
////		treeDeNovoSimlnL = new DataSeries(aaDataSimSSLSlnLrax,"aa lnL data - tree de novo (sim)");
//		treeDeNovoSimlnL = simulatedRax.getFullSitesLnL(aaDataTreeDeNovoSimSSLS);
		
	}
	
}
