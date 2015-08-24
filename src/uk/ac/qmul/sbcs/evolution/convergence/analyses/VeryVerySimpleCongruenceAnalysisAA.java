package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.RAxMLAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;

/**
 * 
 * @author Joe Parker
 *
 * A very simple congruence analysis to compare the SSLS values of an AA dataset on two input trees.
 */
public class VeryVerySimpleCongruenceAnalysisAA {
	// Initialise with data and two trees
	// Aaml on tree 1
	// Aaml on tree 2
	// Compare SSLS of both.
	private File dataset;
	private File treeFileOne;
	private File treeFileTwo;
	private File treeFileRAxMLdeNovo;
	private File workDir;
	private File binariesLocation;
	private String runID;
	private AlignedSequenceRepresentation sourceDataASR;
	private SequenceCodingType inputSequenceCodingType;
	private File aaTreeOneAnalysisOutputFile;
	private File aaTreeTwoAnalysisOutputFile;
	private File aaTreeDeNovoAnalysisOutputFile;
	private File pamlDataFileAA;
	private DataSeries treeOnelnL;
	private DataSeries treeTwolnL;
	private DataSeries treeDeNovolnL;
	private NewickTreeRepresentation treeOne;
	private NewickTreeRepresentation treeTwo;
	private NewickTreeRepresentation treeRAxML;
	private TreeSet<String> taxaList;
	
	public VeryVerySimpleCongruenceAnalysisAA(File data, File one, File two, File work, File binariesLocation, String ID, TreeSet<String> taxaList){
		this.dataset = data;
		this.treeFileOne = one;
		this.treeFileTwo = two;
		this.workDir = work;
		this.runID = ID;
		this.taxaList = taxaList;
		this.binariesLocation = binariesLocation;
	}
	
	public void go(){
		// Read in the data and treefiles
		assert(this.dataset.canRead());
		System.out.println(dataset.getAbsolutePath().toString());
		System.out.println(workDir.getAbsolutePath().toString());
		this.sourceDataASR = new AlignedSequenceRepresentation();
		try {
			sourceDataASR.loadSequences(dataset,true);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.printShortSequences(30);
		sourceDataASR.printNumberOfSites();
		sourceDataASR.printNumberOfTaxa();
		sourceDataASR.removeUnambiguousGaps();
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		try {
			sourceDataASR.translate(false);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		pamlDataFileAA = new File(dataset.getAbsoluteFile()+runID+"_pamlAA.phy");
		sourceDataASR.writePhylipFile(pamlDataFileAA, true);
		
		treeOne = new NewickTreeRepresentation(treeFileOne, taxaList);
		treeTwo = new NewickTreeRepresentation(treeFileTwo, taxaList);
		
		treeOne.printSimply();
		treeTwo.printSimply();
		
		
		/* Get a de-novo RAxML tree */
		
		RAxMLAnalysis ra = new RAxMLAnalysis(pamlDataFileAA, workDir, treeOne.getTreeFile(), runID, RAxMLAnalysis.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysis.algorithmOptions.e);
		ra.setTreeConstraint(false);
		ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
		ra.RunAnalysis();
		treeFileRAxMLdeNovo = ra.getOutputFile();
		treeRAxML = new NewickTreeRepresentation(treeFileRAxMLdeNovo,taxaList);

		
		/* Aaml runs */
		
		// Tree 1
		this.aaTreeOneAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlTreeOne.out");
		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileOne.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaTreeOneAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		File[] treefiles = {this.treeFileOne};
		AlignedSequenceRepresentation[] datasets = {sourceDataASR};
		AamlAnalysis treeOneAaml = new AamlAnalysis(datasets, treefiles, parameters,"aamlOnTreeOne.ctl");
		treeOneAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeOneAaml.setExecutionBinary(new File(treeOneAaml.getBinaryDir(),"codeml"));
		treeOneAaml.setWorkingDir(workDir);
		treeOneAaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeOneSSLS = treeOneAaml.getPatternSSLS();
		float[] aaDataSSLSlnL1 = new float[aaDataTreeOneSSLS.size()];
		Iterator dataSSLSItr1 = aaDataTreeOneSSLS.keySet().iterator();
		int sIndex = 0;
		while(dataSSLSItr1.hasNext()){
			aaDataSSLSlnL1[sIndex] = aaDataTreeOneSSLS.get(dataSSLSItr1.next());
			sIndex++;
		}
		treeOnelnL = new DataSeries(aaDataSSLSlnL1,"aa lnL data - tree 1");

		// Tree 2
		this.aaTreeTwoAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlTreeTwo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileTwo.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaTreeOneAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileTwo;
		AamlAnalysis treeTwoAaml = new AamlAnalysis(datasets, treefiles, parameters,"aamlOnTreeTwo.ctl");
		treeTwoAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeTwoAaml.setExecutionBinary(new File(treeTwoAaml.getBinaryDir(),"codeml"));
		treeTwoAaml.setWorkingDir(workDir);
		treeTwoAaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeTwoSSLS = treeTwoAaml.getPatternSSLS();
		float[] aaDataSSLSlnL2 = new float[aaDataTreeTwoSSLS.size()];
		Iterator dataSSLSItr2 = aaDataTreeTwoSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItr2.hasNext()){
			aaDataSSLSlnL2[sIndex] = aaDataTreeTwoSSLS.get(dataSSLSItr2.next());
			sIndex++;
		}
		treeTwolnL = new DataSeries(aaDataSSLSlnL2,"aa lnL data - tree 2");
		

		// RAxML tree 
		this.aaTreeDeNovoAnalysisOutputFile = new File(pamlDataFileAA.getPath()+".aamlTreeDeNovo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaTreeDeNovoAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileRAxMLdeNovo;
		AamlAnalysis treeDeNovoAaml = new AamlAnalysis(datasets, treefiles, parameters,"aamlOnTreeDeNovo.ctl");
		treeDeNovoAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeDeNovoAaml.setExecutionBinary(new File(treeDeNovoAaml.getBinaryDir(),"codeml"));
		treeDeNovoAaml.setWorkingDir(workDir);
		treeDeNovoAaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeDeNovoSSLS = treeDeNovoAaml.getPatternSSLS();
		float[] aaDataSSLSlnLdeNovo = new float[aaDataTreeDeNovoSSLS.size()];
		Iterator dataSSLSItrdeNovo = aaDataTreeDeNovoSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItrdeNovo.hasNext()){
			aaDataSSLSlnLdeNovo[sIndex] = aaDataTreeDeNovoSSLS.get(dataSSLSItrdeNovo.next());
			sIndex++;
		}
		treeDeNovolnL = new DataSeries(aaDataSSLSlnLdeNovo,"aa lnL data - RAxML tree");

		
		/* Compare SSLS */

		int[] intervals = {0,5,25,50,75,95,100};
		float[] treeOnePercentiles = new float[7];
		try {
			for(int i=0;i<7;i++){
				treeOnePercentiles[i] = treeOnelnL.getValueAtPercentile(intervals[i]);
			}
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float[] treeTwoPercentiles = new float[7];
		try {
			for(int i=0;i<7;i++){
				treeTwoPercentiles[i] = treeTwolnL.getValueAtPercentile(intervals[i]);
			}
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float[] treeDeNovoPercentiles = new float[7];
		try {
			for(int i=0;i<7;i++){
				treeDeNovoPercentiles[i] = treeDeNovolnL.getValueAtPercentile(intervals[i]);
			}
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Interval\tTree 1\t\tTree 2\t\tRAxML\n=============================================================");
		for(int i=0;i<7;i++){
			System.out.println(intervals[i]+"\t\t"+treeOnePercentiles[i]+"\t"+treeTwoPercentiles[i]+"\t"+treeDeNovoPercentiles[i]);
		}
		try {
			System.out.println("\nTree 2 data lower 5% (value: "+treeTwolnL.getValueAtPercentile(5)+") overlaps empirical data at percentile: "+treeOnelnL.getPercentileCorrespondingToValue(treeTwolnL.getValueAtPercentile(5))+" (value: "+treeOnelnL.getValueAtPercentile(treeOnelnL.getPercentileCorrespondingToValue(treeTwolnL.getValueAtPercentile(5)))+")");
			System.out.println("\nRAxML data lower 5% (value: "+treeDeNovolnL.getValueAtPercentile(5)+") overlaps empirical data at percentile: "+treeOnelnL.getPercentileCorrespondingToValue(treeDeNovolnL.getValueAtPercentile(5))+" (value: "+treeOnelnL.getValueAtPercentile(treeOnelnL.getPercentileCorrespondingToValue(treeDeNovolnL.getValueAtPercentile(5)))+")");
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
