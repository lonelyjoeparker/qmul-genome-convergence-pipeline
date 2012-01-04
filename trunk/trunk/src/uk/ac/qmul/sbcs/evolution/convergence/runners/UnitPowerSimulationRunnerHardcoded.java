package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;


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
	
	private File treefileLocation;
	private int datasetSize;
	private int nullDataSize;
	private int numberOfTaxa;
	private int numberOfNullSites;
	private String[] taxaToRemove;
	private SequenceCodingType sequenceCodingType;
	private String model;
	private HashMap<String,String> simulationParameters;
	
	public static void main(String[] args){
	}
	
	public void go(){
		simulationParameters = new HashMap<String,String>();
		simulationParameters.put("", "");
		
		/*
		 * Execution:
		 * 
		 * 		- instantiate, initialise and parametize the dataSimulation and nullSimulation
		 * 		- instantiate a UnitPowerSimulation
		 * 		- simulatePower
		 * 		- print results to file.
		 */
		
		EvolverSimulationSGE dataSimulation = new EvolverSimulationSGE();
		EvolverSimulationSGE nullSimulation;

		Iterator itr = simulationParameters.keySet().iterator();
		while(itr.hasNext()){
			String param = (String)itr.next();
			dataSimulation.addParameterReadyToSet(param, simulationParameters.get(param)); //repeat for all parameters, most of which are hardcoded of course. NB we're doing this twice because the non-hardcoded UnitPowerSimulationRunner will need to.
		}
		
		nullSimulation = dataSimulation;
		nullSimulation.setSitesToSimulate(numberOfNullSites);
		dataSimulation.initialiseSimulation();
		nullSimulation.initialiseSimulation();
		
		UnitPowerSimulation simulation = new UnitPowerSimulation(nullSimulation, nullSimulation, treefileLocation, treefileLocation, model, model, datasetSize, datasetSize, datasetSize, taxaToRemove, taxaToRemove, model, sequenceCodingType, null);
		
	}
}
