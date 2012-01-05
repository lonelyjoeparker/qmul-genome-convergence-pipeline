package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;


import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.analyses.UnitPowerSimulation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;

/**
 * 
 * @author joeparker
 * @since 2012/01/04
 * 
 * Handler class for the UnitSimulation - hardcoded.
 */
public class UnitPowerSimulationRunnerHardcoded {
	
	private File treeFile;
	private File workDir;
	private File binariesLocation;
	private File evolverBinary;
	private File evolverCtl;
	private int datasetSize;
	private int nullDataSize;
	private int numberOfTaxa;
	private int numberOfSitesToConverge;
	private String[] taxaToRemove;
	private String[] taxaToConverge;
	private String masterTaxon;
	private String runID;
	private String tree;
	private SequenceCodingType sequenceCodingType;
	private String model;
	private HashMap<String,String> simulationParameters;
	private TreeSet<String> taxaList;
	
	public static void main(String[] args){
		UnitPowerSimulationRunnerHardcoded aUnit = new UnitPowerSimulationRunnerHardcoded();
		aUnit.go();
	}
	
	public void go(){
		this.initialiseTaxaList();
		this.initialseSimulationParameters();
		
		
		/*
		 * Execution:
		 * 
		 * 		- instantiate, initialise and parametize the dataSimulation and nullSimulation
		 * 		- instantiate a UnitPowerSimulation
		 * 		- simulatePower
		 * 		- print results to file.
		 */
		
		treeFile = new File("/pamlTest/trialPowerSim/22t_WAG.tre");
		workDir = new File("/pamlTest/trialPowerSim/");
		binariesLocation = new File("/pamlTest/binaries/");
		evolverBinary = new File("/pamlTest/binaries/evolver");
		evolverCtl = new File("/pamlTest/trialPowerSim/evolver.ctl");
		runID = "UnitPowerSimHC";
		model = "WAG";
		tree = "(Monodelphi:0.24760393,(((((((Bos:0.048904167,Tursiops:0.031684545):0.0090338518,Vicugna:0.042972451):0.012554305,Equus:0.039082616):0.0018692541,((Canis:0.035796925,Felis:0.031967411):0.018314869,(((Eidolon:0.019770896,Pteropus:0.012449182):0.028960857,(Megaderma:0.055404706,Rhinolophu:0.039700734):0.006976852):0.0032567174,(Myotis:0.05253924,Pteronotus:0.053246857):0.011490039):0.008641665):0.00091422006):0.0025944456,(Erinaceus:0.089282695,Sorex:0.10153418):0.01661825):0.0063002763,((Homo:0.0028293894,Pan:0.0028236869):0.04126969,(Mus:0.12335404,(Ochotona:0.072903436,Oryctolagu:0.042011259):0.029730032):0.0093948645):0.0074159284):0.0092128208,Loxodonta:0.062597437):0.0046032988,Dasypus:0.066898101);";
		datasetSize = 200;
		nullDataSize = 500;
		numberOfSitesToConverge = 2;
		taxaToConverge = new String[2];
		taxaToConverge[0] = "Tursiops";
		taxaToConverge[1] = "Pteronotus";
		taxaToRemove = new String[0];
		masterTaxon = "RHINOLOPHU";
		sequenceCodingType = SequenceCodingType.AA;
		
		EvolverSimulationSGE dataSimulation = new EvolverSimulationSGE(evolverBinary, workDir, evolverCtl, tree, 22, datasetSize, 1, sequenceCodingType);
		EvolverSimulationSGE nullSimulation = new EvolverSimulationSGE(evolverBinary, workDir, evolverCtl, tree, 22, nullDataSize, 1, sequenceCodingType);

		dataSimulation.initialiseSimulation();
		nullSimulation.initialiseSimulation();

		Iterator itr = simulationParameters.keySet().iterator();
		while(itr.hasNext()){
			String param = (String)itr.next();
			dataSimulation.addParameterReadyToSet(param, simulationParameters.get(param)); //repeat for all parameters, most of which are hardcoded of course. NB we're doing this twice because the non-hardcoded UnitPowerSimulationRunner will need to.
			nullSimulation.addParameterReadyToSet(param, simulationParameters.get(param));
		}
		
		
		UnitPowerSimulation simulation = new UnitPowerSimulation(dataSimulation, nullSimulation, treeFile, workDir, binariesLocation, runID, model, datasetSize, nullDataSize, numberOfSitesToConverge, taxaToConverge, taxaToRemove, masterTaxon, sequenceCodingType, taxaList);
		
		String retVal = simulation.simulatePower();
		System.out.println(retVal);
	}

	private void initialseSimulationParameters() {
		simulationParameters = new HashMap<String,String>();
		simulationParameters.put("AARATEFILE", "/pamlTest/binaries/dat/wag.dat");
		simulationParameters.put("TREE_LENGTH", "-1");
		simulationParameters.put("PAMLFLAG", "0");
		//TODO Add the other simulation parameters (hardcoded for now)
	}

	public void initialiseTaxaList(){
		taxaList = new TreeSet<String>();
		taxaList.add("Tursiops");
		taxaList.add("Canis");
		taxaList.add("Felis");
		taxaList.add("Loxodonta");
		taxaList.add("Erinaceus");
		taxaList.add("Mus");
		taxaList.add("Monodelphi");
		taxaList.add("Pan");
		taxaList.add("Homo");
		taxaList.add("Pteronotus");
		taxaList.add("Rhinolophu");
		taxaList.add("Pteropus");
		taxaList.add("Eidolon");
		taxaList.add("Dasypus");
		taxaList.add("Equus");
		taxaList.add("Megaderma");
		taxaList.add("Myotis");
		taxaList.add("Bos");
		/* The extra 4 taxa in the 22 taxon tree */
		taxaList.add("Vicugna");
		taxaList.add("Ochotona");
		taxaList.add("Oryctolagu");
		taxaList.add("Sorex");
	}
}
