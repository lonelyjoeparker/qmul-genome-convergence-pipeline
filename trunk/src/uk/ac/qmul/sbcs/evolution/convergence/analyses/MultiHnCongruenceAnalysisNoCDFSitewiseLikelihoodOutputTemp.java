package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
//
//import javax.swing.JFrame;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.FilterOutOfAllowableRangeException;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.TaxonNotFoundError;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.AamlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.EvolverSimulationSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.RAxMLAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.RAxMLAnalysisSGE;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

import uk.ac.qmul.sbcs.evolution.convergence.util.stats.ExperimentalDataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.UnequalDataSeriesLengthException;

/**
 * 
 * @author Joe Parker
 * @since r96: 30/3/2012
 * A multiple congruence analysis to compare the SSLS values of an AA dataset on FOUR input trees and a de novo RAxML tree.
 * 
 * 
 * Assumes the working directory has no existing RAxML runs with the same name (runID and alignment file name would be enough to screw it).
 * 
 * NEW for this revision (0.0.1 r80):
 * 		Data is now EXPLICITLY filtered for missing taxa (gaps) using the AlignedSequenceRepresentation.filterForMissingData(int filter, boolean useFactor) method.
 * 		This method either removes:
 * 			[useFactor = true]: any sites with a proportion of gaps > (filter/100); or
 * 			[useFactor - false]: any sites with COUNT of gaps > filter
 * 
 * 		useFactor = true is probably safer as there's no chance of setting filter < numTaxa (which would cause it to throw an exception). The only downside is that is harder to interpret (slightly)
 * 
 * 		Also note that this means the PamlParameter cleandata should probably be set to cleandata=0 for most purposes.
 */
public class MultiHnCongruenceAnalysisNoCDFSitewiseLikelihoodOutputTemp {
	// Initialise with data and two trees
	// Aaml on tree 1
	// Aaml on tree 2
	// Compare SSLS of both.
	private File dataset;
	private File treeFileH0;
	private File treeFileH1;
	private File treeFileH2;
	private File treeFileH3;
	private File treeFileRAxMLdeNovo;
	private File treeFileH0Pruned;
	private File treeFileH1Pruned;
	private File treeFileH2Pruned;
	private File treeFileH3Pruned;
	private File workDir;
	private File binariesLocation;
	private File evolverBinary;
	private String runID;
	private AlignedSequenceRepresentation sourceDataASR;
	private SequenceCodingType inputSequenceCodingType;
	private File aaH0AnalysisOutputFile;
	private File aaH1AnalysisOutputFile;
	private File aaH2AnalysisOutputFile;
	private File aaH3AnalysisOutputFile;
	private File aaTreeDeNovoAnalysisOutputFile;
	private File pamlDataFileAA;
	private ExperimentalDataSeries treeH0ObservedlnL;
	private ExperimentalDataSeries treeH1ObservedlnL;
	private ExperimentalDataSeries treeH2ObservedlnL;
	private ExperimentalDataSeries treeH3ObservedlnL;
	private ExperimentalDataSeries treeDeNovoObservedlnL;
	private NewickTreeRepresentation treeH0;
	private NewickTreeRepresentation treeH0Pruned;
	private NewickTreeRepresentation treeH1;
	private NewickTreeRepresentation treeH1Pruned;
	private NewickTreeRepresentation treeH2;
	private NewickTreeRepresentation treeH2Pruned;
	private NewickTreeRepresentation treeH3;
	private NewickTreeRepresentation treeH3Pruned;
	private NewickTreeRepresentation treeRAxML;
	private TreeSet<String> taxaList;
	private int sitesInSimulations;
	StringBuilder logfileData = new StringBuilder();
	private int filter;
	private boolean filterByFactor;
//	private XYSeriesCollection XY; // Chart plotting variable
	private ExperimentalDataSeries H0H1DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H2DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H3DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0RaxDifferencesObs = new ExperimentalDataSeries();
/*
 * Simulation variables - commented out 09/08/2012
	private ExperimentalDataSeries treeTwoSimlnL;
	private ExperimentalDataSeries treeDeNovoSimlnL;
	private ExperimentalDataSeries H0H1DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H2DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H3DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0RaxDifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries treeOneSimlnLOnTreeOne;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH1;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH2;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH3;
	private ExperimentalDataSeries treeOneSimlnLOnTreeDeNovo;
*/
	
	/**
	 * 
	 * @param data - absolute file location of sequence file in fasta / phylip / nexus format.
	 * @param treefileH0 - absolute file location of H0 (null hypothesis) treefile, in Newick format.
	 * @param treefileH1 - absolute file location of H1 (alternative hypothesis 1) treefile, in Newick format.
	 * @param treefileH2 - absolute file location of H2 (alternative hypothesis 2) treefile, in Newick format.
	 * @param treefileH3 - absolute file location of H3 (alternative hypothesis 3) treefile, in Newick format.
	 * @param work - absolute location of working directory.
	 * @param binariesLocation - absolute location of binaries directory.
	 * @param ID - ID string (mainly for RAxML runs).
	 * @param taxaList - an ArrayList<String> of taxa present.
	 * @param sitesToSimulate - integer number of sites to simulate the null distribution.
	 * @param thisFilter - filter out sites with this many (or greater) taxa having gaps (missing data)
	 * @param filterThisByFactor - whether to filter by % or absolute number.
	 */
	public MultiHnCongruenceAnalysisNoCDFSitewiseLikelihoodOutputTemp(File data, File treefileH0, File treefileH1, File treefileH2, File treefileH3, File work, File binariesLocation, String ID, TreeSet<String> taxaList, int sitesToSimulate, int thisFilter, boolean filterThisByFactor){
		this.dataset = data;
		this.treeFileH0 = treefileH0;
		this.treeFileH1 = treefileH1;
		this.treeFileH2 = treefileH2;
		this.treeFileH3 = treefileH3;
		this.workDir = work;
		this.runID = ID;
		this.taxaList = taxaList;
		this.binariesLocation = binariesLocation;
		this.sitesInSimulations = sitesToSimulate;
		this.evolverBinary = new File(binariesLocation+"/evolver");
		this.filter = thisFilter;
		this.filterByFactor = filterThisByFactor;
	}
	
	public void go(){
		long time = System.currentTimeMillis();
		// Read in the data and treefiles
		float min = Math.min(0, -1);
		assert(this.dataset.canRead());
		System.out.println(dataset.getAbsolutePath().toString());
		System.out.println(workDir.getAbsolutePath().toString());
		this.sourceDataASR = new AlignedSequenceRepresentation();
		try {
			sourceDataASR.loadSequences(dataset,false);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sourceDataASR.printShortSequences(30);
//		sourceDataASR.printNumberOfSites();
//		sourceDataASR.printNumberOfTaxa();
		sourceDataASR.removeUnambiguousGaps();
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		try {
			sourceDataASR.translate(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		pamlDataFileAA = new File(dataset.getAbsoluteFile()+runID+"_pamlAA.phy");
		/*
		 * NEW for this revision (0.0.1 r80):
		 * 		Data is now EXPLICITLY filtered for missing taxa (gaps) using the AlignedSequenceRepresentation.filterForMissingData(int filter, boolean useFactor) method.
		 * 		This method either removes:
		 * 			[useFactor = true]: any sites with a proportion of gaps > (filter/100); or
		 * 			[useFactor - false]: any sites with COUNT of gaps > filter
		 * 
		 * 		useFactor = true is probably safer as there's no chance of setting filter < numTaxa (which would cause it to throw an exception). The only downside is that is harder to interpret (slightly)
		 * 
		 * 		Also note that this means the PamlParameter cleandata should probably be set to cleandata=0 for most purposes.
		 */
		try {
			sourceDataASR.filterForMissingData(filter, filterByFactor);
		} catch (FilterOutOfAllowableRangeException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		sourceDataASR.writePhylipFile(pamlDataFileAA, true);
		
		treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
		treeH1 = new NewickTreeRepresentation(treeFileH1, taxaList);
		treeH2 = new NewickTreeRepresentation(treeFileH2, taxaList);
		treeH3 = new NewickTreeRepresentation(treeFileH3, taxaList);
		
		/* Tree H0 needs pruning as it has the full taxon list */
		
		treeH0Pruned = this.pruneTaxa(treeH0, this.excludedTaxaList(taxaList, sourceDataASR));
		treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
		treeH0Pruned.setTreeFile(treeFileH0Pruned);
		treeH0Pruned.write(treeFileH0Pruned);

		/* Ditto, Tree H1 needs pruning as it has the full taxon list */
		
		treeH1Pruned = this.pruneTaxa(treeH1, this.excludedTaxaList(taxaList, sourceDataASR));
		treeFileH1Pruned = new File(treeFileH1.getAbsoluteFile()+".pruned.tre");
		treeH1Pruned.setTreeFile(treeFileH1Pruned);
		treeH1Pruned.write(treeFileH1Pruned);
		
		/* Ditto, Tree H2 needs pruning as it has the full taxon list */
		
		treeH2Pruned = this.pruneTaxa(treeH2, this.excludedTaxaList(taxaList, sourceDataASR));
		treeFileH2Pruned = new File(treeFileH2.getAbsoluteFile()+".pruned.tre");
		treeH2Pruned.setTreeFile(treeFileH2Pruned);
		treeH2Pruned.write(treeFileH2Pruned);

		/* Ditto, Tree H3 needs pruning as it has the full taxon list */
		
		treeH3Pruned = this.pruneTaxa(treeH3, this.excludedTaxaList(taxaList, sourceDataASR));
		treeFileH3Pruned = new File(treeFileH3.getAbsoluteFile()+".pruned.tre");
		treeH3Pruned.setTreeFile(treeFileH3Pruned);
		treeH3Pruned.write(treeFileH3Pruned);

		
		/* Get a de-novo RAxML tree */
		
		RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, treeH0.getTreeFile(), runID, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
		ra.setTreeConstraint(false);
		ra.setNoStartTree(true);
		ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
	//	ra.setWorkingDir(this.workDir);
		ra.RunAnalysis();
		treeFileRAxMLdeNovo = ra.getOutputFile();
		treeRAxML = new NewickTreeRepresentation(treeFileRAxMLdeNovo,taxaList);

		
		/* Aaml runs */
		
		// Tree 1 (H0; null hypothesis)
		this.aaH0AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeOne.out");
		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH0Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH0AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		File[] treefiles = {this.treeFileH0Pruned};
		AlignedSequenceRepresentation[] datasets = {new AlignedSequenceRepresentation()};
		AamlAnalysisSGE treeOneAaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeOne.ctl");
		treeOneAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeOneAaml.setExecutionBinary(new File(treeOneAaml.getBinaryDir(),"codeml"));
		treeOneAaml.setWorkingDir(workDir);
		treeOneAaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeOneSSLS = treeOneAaml.getPatternSSLS();
		float[] aaDataSSLSlnL0 = new float[aaDataTreeOneSSLS.size()];
		Iterator dataSSLSItr0 = aaDataTreeOneSSLS.keySet().iterator();
		int sIndex = 0;
		while(dataSSLSItr0.hasNext()){
			aaDataSSLSlnL0[sIndex] = aaDataTreeOneSSLS.get(dataSSLSItr0.next());
			sIndex++;
		}
//		treeOnelnL = new DataSeries(aaDataSSLSlnL1,"aa lnL data - tree 1");
		treeH0ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeOneSSLS));

		// Tree 2 (H1)
		this.aaH1AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeTwo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH1Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH1AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH1Pruned;
		AamlAnalysisSGE treeTwoAaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeTwo.ctl");
		treeTwoAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeTwoAaml.setExecutionBinary(new File(treeTwoAaml.getBinaryDir(),"codeml"));
		treeTwoAaml.setWorkingDir(workDir);
		treeTwoAaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeTwoSSLS = treeTwoAaml.getPatternSSLS();
		float[] aaDataSSLSlnL1 = new float[aaDataTreeTwoSSLS.size()];
		Iterator dataSSLSItr1 = aaDataTreeTwoSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItr1.hasNext()){
			aaDataSSLSlnL1[sIndex] = aaDataTreeTwoSSLS.get(dataSSLSItr1.next());
			sIndex++;
		}
		treeH1ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeTwoSSLS));
		
		// Tree 3 (H2)
		this.aaH2AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH2.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH2Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH2AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH2Pruned;
		AamlAnalysisSGE treeH2Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH2.ctl");
		treeH2Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH2Aaml.setExecutionBinary(new File(treeH2Aaml.getBinaryDir(),"codeml"));
		treeH2Aaml.setWorkingDir(workDir);
		treeH2Aaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeH2SSLS = treeH2Aaml.getPatternSSLS();
		float[] aaDataSSLSlnL2 = new float[aaDataTreeH2SSLS.size()];
		Iterator dataSSLSItr2 = aaDataTreeH2SSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItr2.hasNext()){
			aaDataSSLSlnL2[sIndex] = aaDataTreeH2SSLS.get(dataSSLSItr2.next());
			sIndex++;
		}
		treeH2ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeH2SSLS));

		// Tree 4 (H3)
		this.aaH3AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH3.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH3Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH3AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH3Pruned;
		AamlAnalysisSGE treeH3Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH3.ctl");
		treeH3Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH3Aaml.setExecutionBinary(new File(treeH3Aaml.getBinaryDir(),"codeml"));
		treeH3Aaml.setWorkingDir(workDir);
		treeH3Aaml.RunAnalysis();
		TreeMap<String, Float> aaDataTreeH3SSLS = treeH3Aaml.getPatternSSLS();
		float[] aaDataSSLSlnL3 = new float[aaDataTreeH3SSLS.size()];
		Iterator dataSSLSItr3 = aaDataTreeH3SSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItr3.hasNext()){
			aaDataSSLSlnL3[sIndex] = aaDataTreeH3SSLS.get(dataSSLSItr3.next());
			sIndex++;
		}
		treeH3ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeH3SSLS));

		// RAxML tree 
		this.aaTreeDeNovoAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeDeNovo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaTreeDeNovoAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileRAxMLdeNovo;
		AamlAnalysisSGE treeDeNovoAaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeDeNovo.ctl");
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
//		treeDeNovolnL = new DataSeries(aaDataSSLSlnLdeNovo,"aa lnL data - RAxML tree");
		treeDeNovoObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeDeNovoSSLS));

		/* Compute ∆SSLS (obs) */
		try {
			H0H1DifferencesObs = treeH0ObservedlnL.compareData(treeH1ObservedlnL);
			H0H1DifferencesObs.printBasic();
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0H2DifferencesObs = treeH0ObservedlnL.compareData(treeH2ObservedlnL);
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0H3DifferencesObs = treeH0ObservedlnL.compareData(treeH3ObservedlnL);
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0RaxDifferencesObs = treeH0ObservedlnL.compareData(treeDeNovoObservedlnL);
			H0RaxDifferencesObs.printBasic();
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		
		logfileData.append(runID+"\n");
		logfileData.append("Input: "+this.dataset.getAbsolutePath().toString()+"\n");
		if(this.filterByFactor){
			logfileData.append("Data number of sites AFTER filtering "+this.sourceDataASR.getNumberOfSites()+" (filtering by factor "+this.filter+"/100)\n");
		}else{
			logfileData.append("Data number of sites AFTER filtering "+this.sourceDataASR.getNumberOfSites()+" (filtering where number of gaps >"+this.filter+")\n");
		}
		logfileData.append("Null sites  "+sitesInSimulations+"\n");
		logfileData.append("Site patterns in data: "+aaDataTreeOneSSLS.size()+"\n");
		logfileData.append("Site\tfirstSeq\tlnLH0\tlnLH1\tlnLH2\tlnLH3\t∆H0-H1\t∆H0-H2\t∆H0-H3\t∆H0-RAxML\n");
		char[] firstSeqAA = this.sourceDataASR.getSequenceChars(0);
		logfileData.append(
				"mean\t"+
				"-\t"+
				treeH0ObservedlnL.getMean()+"\t"+
				treeH1ObservedlnL.getMean()+"\t"+
				treeH2ObservedlnL.getMean()+"\t"+
				treeH3ObservedlnL.getMean()+"\t"+
				H0H1DifferencesObs.getMean()+"\t"+
				H0H2DifferencesObs.getMean()+"\t"+
				H0H3DifferencesObs.getMean()+"\t"+
				H0RaxDifferencesObs.getMean()+"\n"
		);
		logfileData.append(
				"SE\t"+
				"-\t"+
				treeH0ObservedlnL.getSE()+"\t"+
				treeH1ObservedlnL.getSE()+"\t"+
				treeH2ObservedlnL.getSE()+"\t"+
				treeH3ObservedlnL.getSE()+"\t"+
				H0H1DifferencesObs.getSE()+"\t"+
				H0H2DifferencesObs.getSE()+"\t"+
				H0H3DifferencesObs.getSE()+"\t"+
				H0RaxDifferencesObs.getSE()+"\n"
		);
		float[] h0lnL = treeH0ObservedlnL.getData();
		float[] h1lnL = treeH1ObservedlnL.getData();
		float[] h2lnL = treeH2ObservedlnL.getData();
		float[] h3lnL = treeH3ObservedlnL.getData();
		float[] h0h1Df = H0H1DifferencesObs.getData();
		float[] h0h2Df = H0H2DifferencesObs.getData();
		float[] h0h3Df = H0H3DifferencesObs.getData();
		float[] srDf = H0RaxDifferencesObs.getData();
		for(int i=0;i<h0h1Df.length;i++){
			logfileData.append(
					i+"\t"+
					firstSeqAA[i]+"\t"+
					h0lnL[i]+"\t"+
					h1lnL[i]+"\t"+
					h2lnL[i]+"\t"+
					h3lnL[i]+"\t"+
					h0h1Df[i]+"\t"+
					h0h2Df[i]+"\t"+
					h0h3Df[i]+"\t"+
					srDf[i]+"\n"
					);
		}
		

		System.out.println("Means\tlnLH0\tlnLH1\tlnLH2\tlnLH3\t∆H0-H1\t∆H0-H2\t∆H0-H3\t∆H0-RAxML");
		System.out.println(
				"<>\t"+
				treeH0ObservedlnL.getMean()+"\t"+
				treeH1ObservedlnL.getMean()+"\t"+
				treeH2ObservedlnL.getMean()+"\t"+
				treeH3ObservedlnL.getMean()+"\t"+
				H0H1DifferencesObs.getMean()+"\t"+
				H0H2DifferencesObs.getMean()+"\t"+
				H0H3DifferencesObs.getMean()+"\t"+
				H0RaxDifferencesObs.getMean()
		);
		
	
		long elapsed = (System.currentTimeMillis() - time)/1000;
		logfileData.append("\nTotal time "+elapsed+"s.\n");
		File logfile = new File(this.workDir.getAbsolutePath()+"/"+runID+".SSLS.out");
		new BasicFileWriter(logfile, logfileData.toString());
	}

	
	private NewickTreeRepresentation pruneTaxa(NewickTreeRepresentation unprunedTree, TreeSet<String> taxaToPrune){
		Iterator itrTaxon = taxaToPrune.iterator();
		while(itrTaxon.hasNext()){
			String taxonToPrune = (String)itrTaxon.next().toString().toUpperCase();
			try {
				unprunedTree.pruneTaxon(taxonToPrune);
				System.out.println("Pruned taxon "+taxonToPrune+" from tree.");
			} catch (TaxonNotFoundError e) {
				// TODO Auto-generated catch block
				System.out.println("Couldn't prune taxon "+taxonToPrune+" from tree.");
				e.printStackTrace();
			}
		}
		return unprunedTree;
	}
	
	/**
	 * 
	 * @param fullList - the full list of taxa that might be seen in this pipeline
	 * @param alignment - the actual alignment under test.
	 * @return excludedList - a TreeSet<String> of the taxa that are NOT in the alignment and need to be pruned from any future tree
	 */
	private TreeSet<String> excludedTaxaList(TreeSet<String> fullList, AlignedSequenceRepresentation alignment){
		TreeSet<String> excludedList = new TreeSet<String>();
		TreeSet<String> includedList = alignment.getTaxaList();
		Iterator itrComp = fullList.iterator();
		while(itrComp.hasNext()){
			String aTaxon = (String) itrComp.next().toString().toUpperCase();
			if(!includedList.contains(aTaxon)){
				excludedList.add(aTaxon);
			}
		}
		return excludedList;
	}
}
