package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;


import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.analyses.UnitPowerSimulation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;

/**
 * 
 * @author joeparker
 * @since 2012/01/04
 * 
 * Handler class for the UnitSimulation - hardcoded.
 * 
 * <p><Arguments>
 * <p>java -jar <b>UnitPowerSimulationHardcoded.jar</b> <i>workDir</i> <i>binariesDir</i> <i>dataSetSize</i> <i>nullSetSize</i> <i>numberOfConvergentSitesToSimulate</i>
 * <p><ol>
 * <li><i>workDir</i> - Fully qualified path to work directory.</li>
 * <li><i>binariesDir</i> - Fully qualified path to directory where PAML binaries are located.</li>
 * <li><i>dataSetSize</i> - Number of sites in simulated 'data' alignment.</li>
 * <li><i>nullSetSize</i> - Number of sites in simulated 'null' alignment.</li>
 * <li><i>numberOfConvergentSitesToSimulate</i> - Number of sites that should be forced to converge.</li>
 * </ol>
 */
public class UnitPowerSimulationRunnerArgs {
	
	private File treeFile;
	private File workDir;
	private File binariesLocation;
	private File evolverBinary;
	private File evolverCtl;
	private int datasetSize;
	private int nullDataSize;
	private int numberOfTaxa;
	private int numberOfSitesToConverge;
	private String[] inputArgs;
	private String[] taxaToRemove;
	private String[] taxaToConverge;
	private String masterTaxon;
	private String runID;
	private String tree;
	private SequenceCodingType sequenceCodingType;
	private String model;
	private HashMap<String,String> simulationParameters;
	private TreeSet<String> taxaList;
	
	public UnitPowerSimulationRunnerArgs(String[] args){
		this.inputArgs = args;
	}
	
	public static void main(String[] args){
		UnitPowerSimulationRunnerArgs aUnit = new UnitPowerSimulationRunnerArgs(args);
		aUnit.go();
	}
	
	public void go(){
		
		/*
		 * Execution:
		 * 
		 * 		- instantiate, initialise and parametize the dataSimulation and nullSimulation
		 * 		- instantiate a UnitPowerSimulation
		 * 		- simulatePower
		 * 		- print results to file.
		 */
		
		workDir = new File(inputArgs[0]);
		binariesLocation = new File(inputArgs[1]);
		treeFile = new File(workDir.getAbsolutePath()+"/22t_WAG.tre");
		evolverBinary = new File(binariesLocation.getAbsolutePath()+"/evolver");
		evolverCtl = new File(workDir.getAbsolutePath()+"/evolver.ctl");
		runID = "UnitPowerSimHC";
		model = "WAG";
		tree = "(Monodelphi:0.24760393,(((((((Bos:0.048904167,Tursiops:0.031684545):0.0090338518,Vicugna:0.042972451):0.012554305,Equus:0.039082616):0.0018692541,((Canis:0.035796925,Felis:0.031967411):0.018314869,(((Eidolon:0.019770896,Pteropus:0.012449182):0.028960857,(Megaderma:0.055404706,Rhinolophu:0.039700734):0.006976852):0.0032567174,(Myotis:0.05253924,Pteronotus:0.053246857):0.011490039):0.008641665):0.00091422006):0.0025944456,(Erinaceus:0.089282695,Sorex:0.10153418):0.01661825):0.0063002763,((Homo:0.0028293894,Pan:0.0028236869):0.04126969,(Mus:0.12335404,(Ochotona:0.072903436,Oryctolagu:0.042011259):0.029730032):0.0093948645):0.0074159284):0.0092128208,Loxodonta:0.062597437):0.0046032988,Dasypus:0.066898101);";
		datasetSize = new Integer(inputArgs[2]);
		nullDataSize = new Integer(inputArgs[3]);
		numberOfSitesToConverge = new Integer(inputArgs[4]);
		numberOfTaxa = new Integer(inputArgs[5]);

		//TODO NOTE - IMPORTANT
		/*
		 * This method is to test the BIG convergence hypothesis, e.g. dolphins into bats.
		 * I also need to test the WEAKER H2, e.g. just echolocating bats united
		 */
		
		taxaToConverge = new String[2];
		taxaToConverge[0] = "TURSIOPS";
		taxaToConverge[1] = "PTERONOTUS";
		taxaToRemove = new String[0];
		masterTaxon = "RHINOLOPHU";
		sequenceCodingType = SequenceCodingType.AA;
		this.initialiseTaxaList();
		
		/*
		 * For each 10²taxnumber<23, remove the cumulatively unneeded taxa from the taxaList, set the appropriate treefile
		 * Note that these are the outgroups to the bat spp. - the chiroptera are not downsampled in any way. 
		 */
		switch(numberOfTaxa){
			case(21):{
				//lose monodelphis
				//TEST: PASSED
				tree = "((((((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,(ERINACEUS:0.08928269,SOREX:0.10153418):0.01661825):0.00630028,((HOMO:0.00282939,PAN:0.00282369):0.04126969,(MUS:0.12335404,(OCHOTONA:0.07290344,ORYCTOLAGU:0.04201126):0.02973003):0.00939486):0.00741593):0.00921282,LOXODONTA:0.06259744):0.00460330,DASYPUS:0.06689810);";
				treeFile = new File(workDir.getAbsolutePath()+"/21t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				break;
			} 
			case(20):{ 
				//lose dasypus
				//TEST: PASSED
				tree = "((MUS:0.12335404,(OCHOTONA:0.07290344,ORYCTOLAGU:0.04201126):0.02973003):0.00939486,(LOXODONTA:0.07181026,(((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,(ERINACEUS:0.08928269,SOREX:0.10153418):0.01661825):0.00630028):0.00741593,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/20t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				break;
			}
			
			case(19):{
				//lose loxodonta
				//TEST: PASSED
				tree = "((MUS:0.12335404,(OCHOTONA:0.07290344,ORYCTOLAGU:0.04201126):0.02973003):0.00939486,(((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,(ERINACEUS:0.08928269,SOREX:0.10153418):0.01661825):0.01371620,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/19t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				break;
			}
			case(18):{
				//lose oryctolagus
				//TEST: PASSED
				tree = "((MUS:0.12335404,OCHOTONA:0.10263347):0.00939486,(((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,(ERINACEUS:0.08928269,SOREX:0.10153418):0.01661825):0.01371620,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/18t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				break;
			}
			case(17):{
				//lose ochotona
				//TEST: PASSED
				tree = "(MUS:0.13274890,(((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,(ERINACEUS:0.08928269,SOREX:0.10153418):0.01661825):0.01371620,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/17t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				break;
			}
			case(16):{
				//lose sorex
				//TEST: <not tested>
				tree = "(MUS:0.13274890,(((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.00259445,ERINACEUS:0.10590094):0.01371620,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/16t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				break;
			}
			case(15):{
				//lose erinaceus
				//TEST: FAIL
				tree = "(MUS:0.13274890,((((BOS:0.04890417,TURSIOPS:0.03168455):0.00903385,VICUGNA:0.04297245):0.01255430,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.01631065,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/15t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				break;
			}
			case(14):{
				//lose vicugna
				//TEST: <not tested>
				tree = "(MUS:0.13274890,(((BOS:0.04890417,TURSIOPS:0.03168455):0.02158816,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.01631065,(HOMO:0.00282939,PAN:0.00282369):0.04126969);";
				treeFile = new File(workDir.getAbsolutePath()+"/14t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				taxaList.remove("Vicugna");
				break;
			}
			case(13):{
				//lose pan
				//TEST: <not tested>
				tree = "(MUS:0.13274890,(((BOS:0.04890417,TURSIOPS:0.03168455):0.02158816,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.01631065,HOMO:0.04409908);";
				treeFile = new File(workDir.getAbsolutePath()+"/13t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				taxaList.remove("Vicugna");
				taxaList.remove("Pan");
				break;
			}
			case(12):{
				//lose bos
				//TEST: <not tested>
				tree = "(MUS:0.13274890,((TURSIOPS:0.05327270,EQUUS:0.03908262):0.00186925,((CANIS:0.03579693,FELIS:0.03196741):0.01831487,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.01631065,HOMO:0.04409908);";
				treeFile = new File(workDir.getAbsolutePath()+"/12t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				taxaList.remove("Vicugna");
				taxaList.remove("Pan");
				taxaList.remove("Bos");
				break;
			}
			case(11):{
				//lose canis
				//TEST: <not tested>
				tree = "(MUS:0.13274890,((TURSIOPS:0.05327270,EQUUS:0.03908262):0.00186925,(FELIS:0.05028228,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00864166):0.00091422):0.01631065,HOMO:0.04409908);";
				treeFile = new File(workDir.getAbsolutePath()+"/11t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				taxaList.remove("Vicugna");
				taxaList.remove("Pan");
				taxaList.remove("Bos");
				taxaList.remove("Canis");
				break;
			}
			case(10):{
				//lose felis
				//TEST: <not tested>
				tree = "(MUS:0.13274890,((TURSIOPS:0.05327270,EQUUS:0.03908262):0.00186925,(((EIDOLON:0.01977090,PTEROPUS:0.01244918):0.02896086,(MEGADERMA:0.05540471,RHINOLOPHU:0.03970073):0.00697685):0.00325672,(MYOTIS:0.05253924,PTERONOTUS:0.05324686):0.01149004):0.00955589):0.01631065,HOMO:0.04409908);";
				treeFile = new File(workDir.getAbsolutePath()+"/10t_WAGedit.tre");
				taxaList.remove("Monodelphi");
				taxaList.remove("Dasypus");
				taxaList.remove("Loxodonta");
				taxaList.remove("Oryctolagu");
				taxaList.remove("Ochotona");
				taxaList.remove("Sorex");
				taxaList.remove("Erinaceus");
				taxaList.remove("Vicugna");
				taxaList.remove("Pan");
				taxaList.remove("Bos");
				taxaList.remove("Canis");
				taxaList.remove("Felis");
				break;
			}
		}
		this.initialseSimulationParameters();
		
		EvolverSimulationSGE dataSimulation = new EvolverSimulationSGE(evolverBinary, workDir, evolverCtl, tree, numberOfTaxa, datasetSize, 1, sequenceCodingType);
		EvolverSimulationSGE nullSimulation = new EvolverSimulationSGE(evolverBinary, workDir, evolverCtl, tree, numberOfTaxa, nullDataSize, 1, sequenceCodingType);

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
		File finalOutput = new File(workDir.getAbsolutePath()+"/sim.out.txt");
		System.out.println(retVal);
		CustomFileWriter outputWriter = new CustomFileWriter(finalOutput,retVal);
	}

	private void initialseSimulationParameters() {
		simulationParameters = new HashMap<String,String>();
		simulationParameters.put("AARATEFILE", binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		simulationParameters.put("TREE_LENGTH", "-1");
		simulationParameters.put("PAMLFLAG", "0");
		simulationParameters.put("ALPHA", "0.2545");
		simulationParameters.put("AAFREQS", 
				"0.1055  0.0202  0.0592  0.0474  0.0061  0.0309  0.0390  0.0763  0.0251  0.0754 \n" +
				"0.1046  0.0468  0.0121  0.0410  0.0523  0.0668  0.0355  0.0009  0.0526  0.1020 ");
		//TODO Add the other simulation parameters (hardcoded for now)
	}

	/*
	 * This method initialises the 22-taxon taxalist. 
	 * In order to successfully run the t<22 simulations it would probably be sensible to instead parse the tree string and only add taxa to the list that appear in the tree. 
	 */
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
