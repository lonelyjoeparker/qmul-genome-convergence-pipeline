package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.CodemlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulation;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;

import junit.framework.TestCase;

public class PamlParametersIntoSimulationTest extends TestCase {
	private AamlResultReader aaml = new AamlResultReader(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/mlaaml"));
	private BasemlResultReader baseml = new BasemlResultReader(new File("/pamlTest/trialDataFromGeorgia/evolver.output.phybaseml.out"));
	private CodemlResultReader codeml = new CodemlResultReader(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/mlc"));
	private File workDir = new File("/pamlTest");
	private File evolverBinaryDir = new File("/Applications/Phylogenetics/PAML/paml44_myVersion/bin/evolver");

	public PamlParametersIntoSimulationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvolverSimulationFileFileStringIntIntIntSequenceCodingType() {
		fail("Not yet implemented"); // TODO
	}

	public void testSimulateNoArg() {
		fail("Not yet implemented"); // TODO
	}
	
	public void testSimulateNucleotides(){
		File f = new File(workDir+"/testSimulationsFromParamsNucleotides");
		int numberOfTaxa = 18;
		int numberOfSites = 1000;
		int numberOfReplicates = 1;
		EvolverSimulation es = new EvolverSimulation(evolverBinaryDir,workDir,f,baseml.getOptimisedTree(),numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.DNA);
		es.initialiseSimulation();
		es.addParameterReadyToSet("PAMLFLAG", "0");
		es.setParameter("RATES", baseml.getBaseRates());
		es.setParameter("ALPHA", baseml.getAlpha());
		es.setParameter("BASEFREQS", baseml.getBaseFreqs());
		es.simulateNoArg();
		new VerboseSystemCommand("cp -v "+System.getProperty("user.dir")+"/mc.paml /pamlTest/testSimulationsFromParamsNucleotides.out");
	}
	
	public void testSimulateAminoAcids(){
		File f = new File(workDir+"/testSimulationsFromParamsAminoAcids");
		int numberOfTaxa = 6;
		int numberOfSites = 1000;
		int numberOfReplicates = 1;
		String tree = aaml.getOptimisedTree();
		String treeLen = aaml.getTreeLength();
		String alpha = aaml.getAlpha();
		
		EvolverSimulation es = new EvolverSimulation(workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
		es.initialiseSimulation();
		es.setParameter("ALPHA", alpha);
		es.setParameter("TREE_LENGTH", treeLen);
		es.setParameter("AARATEFILE", "/Applications/Phylogenetics/PAML/paml44_myVersion/dat/mtmam.dat");
		es.setBinaryLocation(evolverBinaryDir);
		es.simulateNoArg();
		new VerboseSystemCommand("cp -v "+System.getProperty("user.dir")+"/mc.paml /pamlTest/testSimulationsFromParamsAminoAcids.out");
	}

	public void testSimulateCodons(){
		File f = new File(workDir+"/testSimulationsFromParamsCodons");
		int numberOfTaxa = 18;
		int numberOfSites = 1000;
		int numberOfReplicates = 1;
		String tree = codeml.getOptimisedTree();
		String treeLen = codeml.getTreeLength();
		String omega = codeml.getOmegaVal();
		String kappa = codeml.getKappaVal();
		
		EvolverSimulation es = new EvolverSimulation(evolverBinaryDir, workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.CODON);
		es.initialiseSimulation();
		es.setParameter("OMEGA", omega);
		es.setParameter("KAPPA", kappa);
		es.setParameter("TREE_LENGTH", treeLen);
		es.simulateNoArg();
		new VerboseSystemCommand("cp -v "+System.getProperty("user.dir")+"/mc.paml /pamlTest/testSimulationsFromParamsCodons.out");
	}
}
