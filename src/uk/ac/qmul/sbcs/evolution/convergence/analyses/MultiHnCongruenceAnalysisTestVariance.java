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
import uk.ac.qmul.sbcs.evolution.convergence.handlers.NewickUtilitiesHandler;
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
public class MultiHnCongruenceAnalysisTestVariance {
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
	private ExperimentalDataSeries treeOneObservedlnL;
	private ExperimentalDataSeries treeOneSimlnLOnTreeOne;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH1;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH2;
	private ExperimentalDataSeries treeOneSimlnLOnTreeH3;
	private ExperimentalDataSeries treeOneSimlnLOnTreeDeNovo;
	private ExperimentalDataSeries treeH1ObservedlnL;
	private ExperimentalDataSeries treeH2ObservedlnL;
	private ExperimentalDataSeries treeH3ObservedlnL;
	//private ExperimentalDataSeries treeTwoSimlnL;
	private ExperimentalDataSeries treeDeNovoObservedlnL;
	//private ExperimentalDataSeries treeDeNovoSimlnL;
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
//	private XYSeriesCollection XY;
	private ExperimentalDataSeries H0H1DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H2DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H3DifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0RaxDifferencesObs = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H1DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H2DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0H3DifferencesExp = new ExperimentalDataSeries();
	private ExperimentalDataSeries H0RaxDifferencesExp = new ExperimentalDataSeries();
	
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
	public MultiHnCongruenceAnalysisTestVariance(File data, File treefileH0, File treefileH1, File treefileH2, File treefileH3, File work, File binariesLocation, String ID, TreeSet<String> taxaList, int sitesToSimulate, int thisFilter, boolean filterThisByFactor){
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
		int nTrees = 30;
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
			sourceDataASR.removeStopCodons();
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
		
		TreeSet<String> excludedTaxaList = this.excludedTaxaList(taxaList, sourceDataASR);
		if(excludedTaxaList.size()>0){
			// There are some taxa present in the taxa list that are absent from the data. 
			// The trees must be pruned.
			
			/* Tree H0 needs pruning as it has the full taxon list */
//			treeH0Pruned = this.pruneTaxa(treeH0, this.excludedTaxaList(taxaList, sourceDataASR));
			treeH0Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH0, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
			treeH0Pruned.setTreeFile(treeFileH0Pruned);
			treeH0Pruned.writeMultipleReplicates(treeFileH0Pruned,nTrees);

			/* Ditto, Tree H1 needs pruning as it has the full taxon list */
			
//			treeH1Pruned = this.pruneTaxa(treeH1, this.excludedTaxaList(taxaList, sourceDataASR));
			treeH1Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH1, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH1Pruned = new File(treeFileH1.getAbsoluteFile()+".pruned.tre");
			treeH1Pruned.setTreeFile(treeFileH1Pruned);
			treeH1Pruned.writeMultipleReplicates(treeFileH1Pruned,nTrees);
			
			/* Ditto, Tree H2 needs pruning as it has the full taxon list */
			
//			treeH2Pruned = this.pruneTaxa(treeH2, this.excludedTaxaList(taxaList, sourceDataASR));
			treeH2Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH2, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH2Pruned = new File(treeFileH2.getAbsoluteFile()+".pruned.tre");
			treeH2Pruned.setTreeFile(treeFileH2Pruned);
			treeH2Pruned.writeMultipleReplicates(treeFileH2Pruned,nTrees);

			/* Ditto, Tree H3 needs pruning as it has the full taxon list */
			
//			treeH3Pruned = this.pruneTaxa(treeH3, this.excludedTaxaList(taxaList, sourceDataASR));
			treeH3Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH3, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH3Pruned = new File(treeFileH3.getAbsoluteFile()+".pruned.tre");
			treeH3Pruned.setTreeFile(treeFileH3Pruned);
			treeH3Pruned.writeMultipleReplicates(treeFileH3Pruned,nTrees);
		}else{
			// The data contains all the taxa seen in the treefile.
			// No need to prune, just write multiple.
			
//			treeH0Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH0, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
			treeH0.setTreeFile(treeFileH0Pruned);
			treeH0.writeMultipleReplicates(treeFileH0Pruned,nTrees);

//			treeH1Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH1, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH1Pruned = new File(treeFileH1.getAbsoluteFile()+".pruned.tre");
			treeH1.setTreeFile(treeFileH1Pruned);
			treeH1.writeMultipleReplicates(treeFileH1Pruned,nTrees);
			
//			treeH2Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH2, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH2Pruned = new File(treeFileH2.getAbsoluteFile()+".pruned.tre");
			treeH2.setTreeFile(treeFileH2Pruned);
			treeH2.writeMultipleReplicates(treeFileH2Pruned,nTrees);

//			treeH3Pruned = new NewickUtilitiesHandler(this.binariesLocation,this.treeFileH3, this.taxaList).pruneTaxa(excludedTaxaList);
			treeFileH3Pruned = new File(treeFileH3.getAbsoluteFile()+".pruned.tre");
			treeH3.setTreeFile(treeFileH3Pruned);
			treeH3.writeMultipleReplicates(treeFileH3Pruned,nTrees);
		}


		/*
		 * Skip this
		treeH0.printSimply();
		treeH1.printSimply();
		 */
		
		
		/* Get a de-novo RAxML tree */
		
		RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, treeH0.getTreeFile(), runID, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
		ra.setTreeConstraint(false);
		ra.setNoStartTree(true);
		ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
	//	ra.setWorkingDir(this.workDir);
		ra.RunAnalysis();
		treeFileRAxMLdeNovo = ra.getOutputFile();
		treeRAxML = new NewickTreeRepresentation(treeFileRAxMLdeNovo,taxaList); //NOTE RAxML has a single tree

		
		/* Aaml runs */
		
		// Tree 1 (H0; null hypothesis)
		this.aaH0AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH0.out");
		TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH0Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH0AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		File[] treefiles = {this.treeFileH0Pruned};
		AlignedSequenceRepresentation[] datasets = {this.sourceDataASR};
		AamlAnalysisSGE treeH0Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH0.ctl");
		treeH0Aaml.setNumberOfTreesets(nTrees);
		treeH0Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH0Aaml.setExecutionBinary(new File(treeH0Aaml.getBinaryDir(),"codeml"));
		treeH0Aaml.setWorkingDir(workDir);
		/**
		 * Run the analysis. This is currently dropping out to shell via runCommand.pl
		 */
		treeH0Aaml.RunAnalysis();
		/**
		 * Now need to map the output; 
		 * 		take the AamlAnalysis, 
		 * 		getPatternSSLS() to get TreeMap<String,Float> of <i>sitewise</i> lnL,
		 * 		use an Iterator to safely put all contents into a float[]
		 * 
		 * 		TODO: It would be better / more intuitive to just have AamlAnalysis return the lnL as a float[]
		 */
//		TreeMap<String, Float> aaDataTreeOneSSLS = treeOneAaml.getPatternSSLS();
//		float[] aaDataSSLSlnL0 = new float[aaDataTreeOneSSLS.size()];
//		Iterator dataSSLSItr0 = aaDataTreeOneSSLS.keySet().iterator();
		int sIndex = 0;
//		while(dataSSLSItr0.hasNext()){
//			aaDataSSLSlnL0[sIndex] = aaDataTreeOneSSLS.get(dataSSLSItr0.next());
//			sIndex++;
//		}
//		treeOnelnL = new DataSeries(aaDataSSLSlnL1,"aa lnL data - tree 1");
//		treeOneObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeOneSSLS));
		treeOneObservedlnL = new ExperimentalDataSeries(treeH0Aaml.getFloatSitewiseMeanLnL(),"aa lnL data - tree H0");
		float[] treeH0Variances = treeH0Aaml.getFloatSitewiseLnLVariance();
		float[] treeH0SSE = treeH0Aaml.getFloatSitewiseLnLSSE();
		
		// Tree 2 (H1)
 		this.aaH1AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH1.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH1Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH1AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH1Pruned;
		AamlAnalysisSGE treeH1Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH1.ctl");
		treeH1Aaml.setNumberOfTreesets(nTrees);
		treeH1Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH1Aaml.setExecutionBinary(new File(treeH1Aaml.getBinaryDir(),"codeml"));
		treeH1Aaml.setWorkingDir(workDir);
		treeH1Aaml.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeTwoSSLS = treeTwoAaml.getPatternSSLS();
//		float[] aaDataSSLSlnL1 = new float[aaDataTreeTwoSSLS.size()];
//		Iterator dataSSLSItr1 = aaDataTreeTwoSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSSLSItr1.hasNext()){
//			aaDataSSLSlnL1[sIndex] = aaDataTreeTwoSSLS.get(dataSSLSItr1.next());
//			sIndex++;
//		}
//		treeH1ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeTwoSSLS));
		treeH1ObservedlnL = new ExperimentalDataSeries(treeH1Aaml.getFloatSitewiseMeanLnL(),"aa lnL data - tree H1");
		float[] treeH1Variances = treeH1Aaml.getFloatSitewiseLnLVariance();
		float[] treeH1SSE = treeH1Aaml.getFloatSitewiseLnLSSE();
		
		// Tree 3 (H2)
		this.aaH2AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH2.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH2Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH2AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH2Pruned;
		AamlAnalysisSGE treeH2Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH2.ctl");
		treeH2Aaml.setNumberOfTreesets(nTrees);
		treeH2Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH2Aaml.setExecutionBinary(new File(treeH2Aaml.getBinaryDir(),"codeml"));
		treeH2Aaml.setWorkingDir(workDir);
		treeH2Aaml.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeH2SSLS = treeH2Aaml.getPatternSSLS();
//		float[] aaDataSSLSlnL2 = new float[aaDataTreeH2SSLS.size()];
//		Iterator dataSSLSItr2 = aaDataTreeH2SSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSSLSItr2.hasNext()){
//			aaDataSSLSlnL2[sIndex] = aaDataTreeH2SSLS.get(dataSSLSItr2.next());
//			sIndex++;
//		}
//		treeH2ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeH2SSLS));
		treeH2ObservedlnL = new ExperimentalDataSeries(treeH2Aaml.getFloatSitewiseMeanLnL(),"aa lnL data - tree H2");
		float[] treeH2Variances = treeH2Aaml.getFloatSitewiseLnLVariance();
		float[] treeH2SSE = treeH2Aaml.getFloatSitewiseLnLSSE();

		// Tree 4 (H3)
		this.aaH3AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeH3.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH3Pruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH3AnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileH3Pruned;
		AamlAnalysisSGE treeH3Aaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeH3.ctl");
		treeH3Aaml.setNumberOfTreesets(nTrees);
		treeH3Aaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeH3Aaml.setExecutionBinary(new File(treeH3Aaml.getBinaryDir(),"codeml"));
		treeH3Aaml.setWorkingDir(workDir);
		treeH3Aaml.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeH3SSLS = treeH3Aaml.getPatternSSLS();
//		float[] aaDataSSLSlnL3 = new float[aaDataTreeH3SSLS.size()];
//		Iterator dataSSLSItr3 = aaDataTreeH3SSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSSLSItr3.hasNext()){
//			aaDataSSLSlnL3[sIndex] = aaDataTreeH3SSLS.get(dataSSLSItr3.next());
//			sIndex++;
//		}
//		treeH3ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeH3SSLS));
		treeH3ObservedlnL = new ExperimentalDataSeries(treeH3Aaml.getFloatSitewiseMeanLnL(),"aa lnL data - tree H3");
		float[] treeH3Variances = treeH3Aaml.getFloatSitewiseLnLVariance();
		float[] treeH3SSE = treeH3Aaml.getFloatSitewiseLnLSSE();

		// RAxML tree 
		this.aaTreeDeNovoAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlTreeDeNovo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaTreeDeNovoAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		treefiles[0] = this.treeFileRAxMLdeNovo;
		AamlAnalysisSGE treeDeNovoAaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeDeNovo.ctl");
		treeDeNovoAaml.setNumberOfTreesets(1);
		treeDeNovoAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeDeNovoAaml.setExecutionBinary(new File(treeDeNovoAaml.getBinaryDir(),"codeml"));
		treeDeNovoAaml.setWorkingDir(workDir);
		treeDeNovoAaml.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeDeNovoSSLS = treeDeNovoAaml.getPatternSSLS();
//		float[] aaDataSSLSlnLdeNovo = new float[aaDataTreeDeNovoSSLS.size()];
//		Iterator dataSSLSItrdeNovo = aaDataTreeDeNovoSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSSLSItrdeNovo.hasNext()){
//			aaDataSSLSlnLdeNovo[sIndex] = aaDataTreeDeNovoSSLS.get(dataSSLSItrdeNovo.next());
//			sIndex++;
//		}
//		treeDeNovolnL = new DataSeries(aaDataSSLSlnLdeNovo,"aa lnL data - RAxML tree");
//		treeDeNovoObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeDeNovoSSLS));
		treeDeNovoObservedlnL = new ExperimentalDataSeries(treeDeNovoAaml.getFloatSitewiseMeanLnL(),"aa lnL data - tree RAxML");

		/* Compute ÆSSLS (obs) */
		try {
			H0H1DifferencesObs = treeOneObservedlnL.compareData(treeH1ObservedlnL);
			H0H1DifferencesObs.printBasic();
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0H2DifferencesObs = treeOneObservedlnL.compareData(treeH2ObservedlnL);
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0H3DifferencesObs = treeOneObservedlnL.compareData(treeH3ObservedlnL);
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			H0RaxDifferencesObs = treeOneObservedlnL.compareData(treeDeNovoObservedlnL);
			H0RaxDifferencesObs.printBasic();
		} catch (UnequalDataSeriesLengthException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		/* Do simulation on Species tree */
//
//		File f = new File(workDir+"/testSimulationsFromParamsNucleotides");
//		AamlResultReader speciesTreeAaml = new AamlResultReader(aaH0AnalysisOutputFile);
//		String tree = speciesTreeAaml.getOptimisedTree();
//		String treeLen = speciesTreeAaml.getTreeLength();
//		String alphaSpp = speciesTreeAaml.getAlpha();
//		String obsAAAvgFreqs = speciesTreeAaml.getObsAvgFreqs();
//		String optimisedTree = speciesTreeAaml.getOptimisedTree();
//		int numberOfTaxa = this.sourceDataASR.getNumberOfTaxa();
//		int numberOfSites = this.sitesInSimulations;
//		int numberOfReplicates = 1;
//		EvolverSimulationSGE es = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
//		es.initialiseSimulation();
//		es.addParameterReadyToSet("PAMLFLAG", "0");
//		es.setParameter("ALPHA", alphaSpp);
//		es.setParameter("TREE_LENGTH", treeLen);
//		es.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
//		es.setParameter("AAFREQS", obsAAAvgFreqs);
//		es.printCurrentParams();
//		es.simulateNoArg();
//		AlignedSequenceRepresentation simulatedSpp = new AlignedSequenceRepresentation();
//		try {
//			simulatedSpp.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
//		} catch (TaxaLimitException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		/* Do Aaml on data simulated on spp tree */
//		File aaSppSimTreeOneAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSppSimFitTreeOne.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH0Pruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSppSimTreeOneAnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
//		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaSpp);
//		treefiles[0] = this.treeFileH0Pruned;
//		datasets[0] = simulatedSpp;
//		AamlAnalysisSGE treeOneAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlSppSimOnTreeOne.ctl");
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
//		treeOneSimlnLOnTreeOne = new ExperimentalDataSeries(simulatedSpp.getFullSitesLnL(aaDataTreeOneSimSSLS));
//
//		/* Aaml on data simulated under H0 (species tree) - fit to H1 (tree 2 / prestin)	*/ 
//		File aaSppSimTreeH1AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSppSimFitTreeH1.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH1Pruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSppSimTreeH1AnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
//		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaSpp);
//		treefiles[0] = this.treeFileH1Pruned;
//		AamlAnalysisSGE treeH1AamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlSppSimOnTreeH1.ctl");
//		treeH1AamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeH1AamlSim.setExecutionBinary(new File(treeH1AamlSim.getBinaryDir(),"codeml"));
//		treeH1AamlSim.setWorkingDir(workDir);
//		treeH1AamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeH1SimSSLS = treeH1AamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnLH1 = new float[aaDataTreeH1SimSSLS.size()];
//		Iterator dataSimSSLSItrH1 = aaDataTreeH1SimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItrH1.hasNext()){
//			aaDataSimSSLSlnLH1[sIndex] = aaDataTreeH1SimSSLS.get(dataSimSSLSItrH1.next());
//			sIndex++;
//		}
//		treeOneSimlnLOnTreeH1 = new ExperimentalDataSeries(simulatedSpp.getFullSitesLnL(aaDataTreeH1SimSSLS));
//
//		/* Aaml on data simulated under H0 (species tree) - fit to H2		*/ 
//		File aaSppSimTreeH2AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSppSimFitTreeH2.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH2Pruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSppSimTreeH2AnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
//		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaSpp);
//		treefiles[0] = this.treeFileH2Pruned;
//		AamlAnalysisSGE treeH2AamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlSppSimOnTreeH2.ctl");
//		treeH2AamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeH2AamlSim.setExecutionBinary(new File(treeH2AamlSim.getBinaryDir(),"codeml"));
//		treeH2AamlSim.setWorkingDir(workDir);
//		treeH2AamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeH2SimSSLS = treeH2AamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnLH2 = new float[aaDataTreeH2SimSSLS.size()];
//		Iterator dataSimSSLSItrH2 = aaDataTreeH2SimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItrH2.hasNext()){
//			aaDataSimSSLSlnLH2[sIndex] = aaDataTreeH2SimSSLS.get(dataSimSSLSItrH2.next());
//			sIndex++;
//		}
//		treeOneSimlnLOnTreeH2 = new ExperimentalDataSeries(simulatedSpp.getFullSitesLnL(aaDataTreeH2SimSSLS));
//
//		/* Aaml on data simulated under H0 (species tree) - fit to H3		*/ 
//		File aaSppSimTreeH3AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSppSimFitTreeH3.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileH3Pruned.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSppSimTreeH3AnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
//		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaSpp);
//		treefiles[0] = this.treeFileH3Pruned;
//		AamlAnalysisSGE treeH3AamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlSppSimOnTreeH3.ctl");
//		treeH3AamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeH3AamlSim.setExecutionBinary(new File(treeH3AamlSim.getBinaryDir(),"codeml"));
//		treeH3AamlSim.setWorkingDir(workDir);
//		treeH3AamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeH3SimSSLS = treeH3AamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnLH3 = new float[aaDataTreeH3SimSSLS.size()];
//		Iterator dataSimSSLSItrH3 = aaDataTreeH3SimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItrH3.hasNext()){
//			aaDataSimSSLSlnLH3[sIndex] = aaDataTreeH3SimSSLS.get(dataSimSSLSItrH3.next());
//			sIndex++;
//		}
//		treeOneSimlnLOnTreeH3 = new ExperimentalDataSeries(simulatedSpp.getFullSitesLnL(aaDataTreeH3SimSSLS));
//
//		File aaSppSimTreeDeNovoAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSppSimFitTreeDeNovo.out");
//		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
//		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
//		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSppSimTreeDeNovoAnalysisOutputFile.getAbsolutePath());
//		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
//		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
//		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaSpp);
//		treefiles[0] = this.treeFileRAxMLdeNovo;
//		AamlAnalysisSGE treeDeNovoAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlSppSimOnTreeDeNovo.ctl");
//		treeDeNovoAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
//		treeDeNovoAamlSim.setExecutionBinary(new File(treeH1AamlSim.getBinaryDir(),"codeml"));
//		treeDeNovoAamlSim.setWorkingDir(workDir);
//		treeDeNovoAamlSim.RunAnalysis();
//		TreeMap<String, Float> aaDataTreeDeNovoSimSSLS = treeDeNovoAamlSim.getPatternSSLS();
//		float[] aaDataSimSSLSlnL3 = new float[aaDataTreeDeNovoSimSSLS.size()];
//		Iterator dataSimSSLSItr3 = aaDataTreeDeNovoSimSSLS.keySet().iterator();
//		sIndex = 0;
//		while(dataSimSSLSItr3.hasNext()){
//			aaDataSimSSLSlnL3[sIndex] = aaDataTreeDeNovoSimSSLS.get(dataSimSSLSItr3.next());
//			sIndex++;
//		}
//		treeOneSimlnLOnTreeDeNovo = new ExperimentalDataSeries(simulatedSpp.getFullSitesLnL(aaDataTreeDeNovoSimSSLS));
//
//		/* Do simulation on Prestin tree */
//		AamlResultReader prestinTreeAaml = new AamlResultReader(aaH1AnalysisOutputFile);
//		String alphaPre = prestinTreeAaml.getAlpha();

		/**
		 * Removed simulation on prestin tree
		 * @since 2012/03/28
		AamlResultReader prestinTreeAaml = new AamlResultReader(aaTreeTwoAnalysisOutputFile);
		tree = prestinTreeAaml.getOptimisedTree();
		treeLen = prestinTreeAaml.getTreeLength();
		String alphaPre = prestinTreeAaml.getAlpha();
		optimisedTree = prestinTreeAaml.getOptimisedTree();
		numberOfTaxa = this.sourceDataASR.getNumberOfTaxa();
		numberOfSites = this.sitesInSimulations;
		numberOfReplicates = 1;
		EvolverSimulationSGE ep = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
		ep.initialiseSimulation();
		ep.addParameterReadyToSet("PAMLFLAG", "0");
		ep.setParameter("ALPHA", alphaPre);
		ep.setParameter("TREE_LENGTH", treeLen);
		ep.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
		ep.setParameter("AAFREQS", obsAAAvgFreqs);
		ep.printCurrentParams();
		ep.simulateNoArg();
		AlignedSequenceRepresentation simulatedPre = new AlignedSequenceRepresentation();
		try {
			simulatedPre.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
		} catch (TaxaLimitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

		/* Do Aaml on data simulated on prestin tree */

		/**
		 * Removed likelihood fit for prestin-simulated data on prestin tree
		 * @since 2012/03/28
		File aaSimTreeTwoAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSimTreeTwo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileOnePruned.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSimTreeTwoAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
		parameters.put(AamlParameters.ALPHA, "alpha = "+ alphaPre);
		treefiles[0] = this.treeFileTwoPruned;
		datasets[0] = simulatedPre;
		AamlAnalysisSGE treeTwoAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreePreSim.ctl");
		treeTwoAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeTwoAamlSim.setExecutionBinary(new File(treeTwoAamlSim.getBinaryDir(),"codeml"));
		treeTwoAamlSim.setWorkingDir(workDir);
		treeTwoAamlSim.RunAnalysis();
		TreeMap<String, Float> aaDataTreeTwoSimSSLS = treeTwoAamlSim.getPatternSSLS();
		float[] aaDataSimSSLSlnL2 = new float[aaDataTreeTwoSimSSLS.size()];
		Iterator dataSimSSLSItr2 = aaDataTreeTwoSimSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSimSSLSItr2.hasNext()){
			aaDataSimSSLSlnL2[sIndex] = aaDataTreeTwoSimSSLS.get(dataSimSSLSItr2.next());
			sIndex++;
		}
//		treeTwoSimlnL = new DataSeries(aaDataSimSSLSlnL2,"aa lnL data - tree 2 (sim)");
		treeTwoSimlnL = new ExperimentalDataSeries(simulatedPre.getFullSitesLnL(aaDataTreeTwoSimSSLS));
		*/
		
		/* Do simulation on de-novo (RAxML tree */
		AamlResultReader raxTreeAaml = new AamlResultReader(this.aaTreeDeNovoAnalysisOutputFile);
		String alphaRax = raxTreeAaml.getAlpha();

		/**
		 * Removed simulation on prestin tree
		 * @since 2012/03/28
		AamlResultReader raxTreeAaml = new AamlResultReader(this.aaTreeDeNovoAnalysisOutputFile);
		tree = raxTreeAaml.getOptimisedTree();
		treeLen = raxTreeAaml.getTreeLength();
		String alphaRax = raxTreeAaml.getAlpha();
		optimisedTree = raxTreeAaml.getOptimisedTree();
		numberOfTaxa = this.sourceDataASR.getNumberOfTaxa();
		numberOfSites = this.sitesInSimulations;
		numberOfReplicates = 1;
		EvolverSimulationSGE er = new EvolverSimulationSGE(this.evolverBinary,workDir,f,tree,numberOfTaxa,numberOfSites,numberOfReplicates,SequenceCodingType.AA);
		er.initialiseSimulation();
		er.addParameterReadyToSet("PAMLFLAG", "0");
		er.setParameter("ALPHA", alphaRax);
		er.setParameter("TREE_LENGTH", treeLen);
		er.setParameter("AARATEFILE", this.binariesLocation+"/dat/mtmam.dat");
		er.setParameter("AAFREQS", obsAAAvgFreqs);
		er.printCurrentParams();
		er.simulateNoArg();
		AlignedSequenceRepresentation simulatedRax = new AlignedSequenceRepresentation();
		try {
			simulatedRax.loadSequences(new File(this.workDir.getAbsoluteFile()+"/mc.paml"),false);
		} catch (TaxaLimitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		/* Do Aaml on data simulated on RAxML (de novo) tree */

		/**
		 * Removed likelihood fit for RAxML-simulated data on RAxML tree
		 * @since 2012/03/28
		File aaSimTreeDeNovoAnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aamlSimTreeDeNovo.out");
		parameters.put(AamlParameters.SEQFILE, "seqfile = "+this.workDir.getAbsolutePath()+"/mc.paml");
		parameters.put(AamlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
		parameters.put(AamlParameters.OUTFILE, "outfile = "+aaSimTreeDeNovoAnalysisOutputFile.getAbsolutePath());
		parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/wag.dat");
		parameters.put(AamlParameters.FIX_ALPHA, "fix_alpha = 1");
		parameters.put(AamlParameters.ALPHA, "alpha = "+alphaRax);
		treefiles[0] = this.treeFileTwoPruned;
		datasets[0] = simulatedRax;
		AamlAnalysisSGE treeDeNovoAamlSim = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeRaxSim.ctl");
		treeDeNovoAamlSim.setBinaryDir(this.binariesLocation.getAbsoluteFile());
		treeDeNovoAamlSim.setExecutionBinary(new File(treeDeNovoAamlSim.getBinaryDir(),"codeml"));
		treeDeNovoAamlSim.setWorkingDir(workDir);
		treeDeNovoAamlSim.RunAnalysis();
		TreeMap<String, Float> aaDataTreeDeNovoSimSSLS = treeDeNovoAamlSim.getPatternSSLS();
		float[] aaDataSimSSLSlnLrax = new float[aaDataTreeDeNovoSimSSLS.size()];
		Iterator dataSimSSLSItrrax = aaDataTreeDeNovoSimSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSimSSLSItrrax.hasNext()){
			aaDataSimSSLSlnLrax[sIndex] = aaDataTreeDeNovoSimSSLS.get(dataSimSSLSItrrax.next());
			sIndex++;
		}
//		treeDeNovoSimlnL = new DataSeries(aaDataSimSSLSlnLrax,"aa lnL data - tree de novo (sim)");
		try {
			treeDeNovoSimlnL = new ExperimentalDataSeries(simulatedRax.getFullSitesLnL(aaDataTreeDeNovoSimSSLS));
		} catch (NullPointerException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		*/

		/* Compute ÆSSLS (expected) */
//		try {
//			H0H1DifferencesExp = treeOneSimlnLOnTreeOne.compareData(treeOneSimlnLOnTreeH1);
//			H0H1DifferencesExp.printBasic();
//		} catch (UnequalDataSeriesLengthException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			H0H2DifferencesExp = treeOneSimlnLOnTreeOne.compareData(treeOneSimlnLOnTreeH2);
//		} catch (UnequalDataSeriesLengthException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			H0H3DifferencesExp = treeOneSimlnLOnTreeOne.compareData(treeOneSimlnLOnTreeH3);
//		} catch (UnequalDataSeriesLengthException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			H0RaxDifferencesExp = treeOneSimlnLOnTreeOne.compareData(treeOneSimlnLOnTreeDeNovo);
//			H0RaxDifferencesExp.printBasic();
//		} catch (UnequalDataSeriesLengthException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		/* Compare lnL distributions */

//		int[] intervals = new int[101];
//		for(int aBin=0;aBin<101;aBin++){
//			intervals[aBin] = aBin;
//		}
//		float[] treeOnePercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeOnePercentiles[i] = treeOneObservedlnL.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] treeTwoPercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeTwoPercentiles[i] = treeH1ObservedlnL.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] treeDeNovoPercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeDeNovoPercentiles[i] = treeDeNovoObservedlnL.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] treeOneSimPercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeOneSimPercentiles[i] = treeOneSimlnLOnTreeOne.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] treeTwoSimPercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeTwoSimPercentiles[i] = treeOneSimlnLOnTreeH1.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] treeRaxSimPercentiles = new float[101];
//		try {
//			for(int i=0;i<101;i++){
//				treeRaxSimPercentiles[i] = treeOneSimlnLOnTreeDeNovo.getValueAtPercentile(intervals[i]);
//			}
//		} catch (PercentileOutOfRangeError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		float[] speciesVsPrestinDSSLSobs = new float[101];
//		for(int i=0;i<101;i++){
//			try {
//				speciesVsPrestinDSSLSobs[i] = H0H1DifferencesObs.getValueAtPercentile(intervals[i]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		float[] speciesVsDeNovoDSSLSobs = new float[101];
//		for(int i=0;i<101;i++){
//			try {
//				speciesVsDeNovoDSSLSobs[i] = H0RaxDifferencesObs.getValueAtPercentile(intervals[i]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		float[] speciesVsPrestinDSSLSexp = new float[101];
//		for(int i=0;i<101;i++){
//			try {
//				speciesVsPrestinDSSLSexp[i] = H0H1DifferencesExp.getValueAtPercentile(intervals[i]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		float[] speciesVsDeNovoDSSLSexp = new float[101];
//		for(int i=0;i<101;i++){
//			try {
//				speciesVsDeNovoDSSLSexp[i] = H0RaxDifferencesExp.getValueAtPercentile(intervals[i]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	//	System.out.println("Interval\tTree 1\t\tTree1 sim\t\tTree 2\t\tTree2 sim\t\tRAxML\t\tRaxML sim\n=============================================================");
		logfileData.append(runID+"\n");
		logfileData.append("Input: "+this.dataset.getAbsolutePath().toString()+"\n");
		if(this.filterByFactor){
			logfileData.append("Data number of sites AFTER filtering "+this.sourceDataASR.getNumberOfSites()+" (filtering by factor "+this.filter+"/100)\n");
		}else{
			logfileData.append("Data number of sites AFTER filtering "+this.sourceDataASR.getNumberOfSites()+" (filtering where number of gaps >"+this.filter+")\n");
		}
		logfileData.append("Null sites  "+sitesInSimulations+"\n");
// TODO site patterns are obtained indirectly here
// TODO it would make more sense to explicitly read the site patterns in AamlAnalysisSGE, declare instance var, and then get that.
		
//		logfileData.append("Site patterns in data: "+aaDataTreeOneSSLS.size()+"\n");
//		logfileData.append("Species tree estimated alpha\t"+alphaSpp+"\n");
//		logfileData.append("Prestin tree estimated alpha\t"+alphaPre+"\n");
//		logfileData.append("de novo tree estimated alpha\t"+alphaRax+"\n");
		logfileData.append("Sitewise SSLS (obs) for:\th0\t(var h0)\tSSE h0\th1\t(var h1)\tSSE h1\th2\t(var h2)\tSSE h2\th3\t(var h3)\tSSE h3\n");
		float[] h0bar = treeOneObservedlnL.getData();
		float[] h1bar = treeH1ObservedlnL.getData();
		float[] h2bar = treeH2ObservedlnL.getData();
		float[] h3bar = treeH3ObservedlnL.getData();
		for(int i=0;i<h0bar.length;i++){
			logfileData.append(i+"\t"+h0bar[i]+"\t"+treeH0Variances[i]+"\t"+treeH0SSE[i]+"\t"+h1bar[i]+"\t"+treeH1Variances[i]+"\t"+treeH1SSE[i]+"\t"+h2bar[i]+"\t"+treeH2Variances[i]+"\t"+treeH2SSE[i]+"\t"+h3bar[i]+"\t"+treeH3Variances[i]+"\t"+treeH3SSE[i]+"\n");
		}
		float[] h0h1Df = H0H1DifferencesObs.getData();
		float[] h0h2Df = H0H2DifferencesObs.getData();
		float[] h0h3Df = H0H3DifferencesObs.getData();
		float[] srDf = H0RaxDifferencesObs.getData();
		logfileData.append("Sitewise dSSLS (obs) for:\tH0-H1\tH0-H2\tH0-H3\tH0-Rax\n");
		for(int i=0;i<h0h1Df.length;i++){
			logfileData.append(i+"\t"+h0h1Df[i]+"\t"+h0h2Df[i]+"\t"+h0h3Df[i]+"\t"+srDf[i]+"\n");
		}
		/*
		 * Skip this for now.. 
		
		logfileData.append("Sitewise dSSLS (exp) for:\tSpp-Pre\tSpp-Rax\n");
		h0h1Df = H0H1DifferencesExp.getData();
		h0h2Df = H0H2DifferencesExp.getData();
		h0h2Df = H0H3DifferencesExp.getData();
		srDf = H0RaxDifferencesExp.getData();
	//	for(int i=0;i<spDf.length;i++){
	//		logfileData.append(i+"\t"+spDf[i]+"\t\t"+srDf[i]+"\n");
	//	}
	 * 
	 */
		/* Yeah, fuck this lot off too...
		 * 
		logfileData.append("Input: "+this.dataset.getAbsolutePath().toString()+"\n\nInterval\tTree 1\t\tTree 2\t\tRAxML\n=============================================================\nPercentiles\n");
		for(int i=0;i<101;i++){
			System.out.println(intervals[i]+"\t\t"+treeOnePercentiles[i]+"\t"+treeOneSimPercentiles[i]+"\t"+treeTwoPercentiles[i]+"\t"+treeTwoSimPercentiles[i]+"\t"+treeDeNovoPercentiles[i]+"\t"+treeRaxSimPercentiles[i]+"\t"+speciesVsPrestinDSSLSobs[i]+"\t"+speciesVsDeNovoDSSLSobs[i]);
			logfileData.append(intervals[i]+"\t\t"+treeOnePercentiles[i]+"\t"+treeOneSimPercentiles[i]+"\t"+treeTwoPercentiles[i]+"\t"+treeTwoSimPercentiles[i]+"\t"+treeDeNovoPercentiles[i]+"\t"+treeRaxSimPercentiles[i]+"\t"+speciesVsPrestinDSSLSobs[i]+"\t"+speciesVsDeNovoDSSLSobs[i]+"\n");
		}

		try {
			System.out.println("\nTree 2 data lower 5% (value: "+treeH1ObservedlnL.getValueAtPercentile(5)+") overlaps empirical data at percentile: "+treeOneObservedlnL.getPercentileCorrespondingToValue(treeH1ObservedlnL.getValueAtPercentile(5))+" (value: "+treeOneObservedlnL.getValueAtPercentile(treeOneObservedlnL.getPercentileCorrespondingToValue(treeH1ObservedlnL.getValueAtPercentile(5)))+")");
			System.out.println("\nRAxML data lower 5% (value: "+treeDeNovoObservedlnL.getValueAtPercentile(5)+") overlaps empirical data at percentile: "+treeOneObservedlnL.getPercentileCorrespondingToValue(treeDeNovoObservedlnL.getValueAtPercentile(5))+" (value: "+treeOneObservedlnL.getValueAtPercentile(treeOneObservedlnL.getPercentileCorrespondingToValue(treeDeNovoObservedlnL.getValueAtPercentile(5)))+")");
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		*
		*/	
		

		
		
		/*
		 * DANGER
		 * HIGH VOLTAGE
		 * 
		 * TRYING TO PLOT SOME MOFO CHART ACTION...
		 * 
		 * NB requires JFreeChart
		 * 
		 */
		/**
		 * @since - this version, n.b. TODO prune most of this out in any deployment..
		 */

//		XY = new XYSeriesCollection();
//		XYSeries XYdata = new XYSeries("observed SSLS");
//		float[] sppPreDSSLSarray = this.sppPreDifferencesObs.getData();
//		for(int i=0;i<sppPreDSSLSarray.length;i++){
//			XYdata.add(i,sppPreDSSLSarray[i]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("observed SSLS");
//
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("EXPECTED SSLS");
//		sppPreDSSLSarray = this.sppPreDifferencesExp.getData();
//		for(int i=0;i<sppPreDSSLSarray.length;i++){
//			XYdata.add(i,sppPreDSSLSarray[i]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("EXPECTED SSLS");
//
//		float explicitMin = Math.min(sppPreDifferencesObs.getMin(), sppPreDifferencesExp.getMin());
//		float explicitMax = Math.max(sppPreDifferencesObs.getMax(), sppPreDifferencesExp.getMax());
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data norm");


//		float[][] dCDF = sppPreDifferencesObs.getCountsPDFCDFDataDeprecateMeEfficient(explicitMin, explicitMax);
//		float[][] dCDF = sppPreDifferencesObs.getCountsPDFCDFDataDeprecateMeEfficient(); //without explicitly coding the range
//
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][1]);
//		}
//		
//		XY.addSeries(XYdata);
//		showGraph("norm");
		
		// normal data - freqs hist
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data freq");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][2]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm-freq");

		// normal data - cumulative
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data cumul. freq");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][3]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm- cumul. freq");
		
		// normal data - point vals
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data point");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][4]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm-point");
		
		/*
		 * SHIT THE BED!!! THIS WORKS!!!!
		 * 
		 * Now try it for the differences in empirical sets
		 * If that works, try it on the differences in null sets
		 * 
		 * NB eventually will need to set explicit bin ranges etc.
		 */
				
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data norm - EXP");
		
//		dCDF = sppPreDifferencesExp.getCountsPDFCDFDataDeprecateMeEfficient(explicitMin, explicitMax);
//		dCDF = sppPreDifferencesExp.getCountsPDFCDFDataDeprecateMeEfficient(); // without explicitly coding the range
//
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][1]);
//		}
//		
//		XY.addSeries(XYdata);
//		showGraph("norm exp");
		
		// normal data - freqs hist
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data ÆSSLS freq exp");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][2]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm-freq exp");

		// normal data - cumulative
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data cumul. freq ÆSSLS exp");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][3]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm- cumul. freq ÆSSLS exp");
		
		// normal data - point vals
//		XY = new XYSeriesCollection();
//		XYdata = new XYSeries("some joe data point ÆSSLS exp");
//		for(int i=0;i<dCDF.length;i++){
//			XYdata.add(dCDF[i][0],dCDF[i][4]);
//		}
//		XY.addSeries(XYdata);
//		showGraph("norm-point ÆSSLS exp");
		
		/*
		 * SHIT THE BED!!! THIS WORKS!!!!
		 * 
		 * Now try it on the differences in null sets' ÆSSLS
		 * 
		 * NB eventually will need to set explicit bin ranges etc.
		 */
	
		/**
		 * Complicated shit huh? OK now time to get the number of SITES whose lnL difference (H0-Hn)
		 * is < than critical value...
		 */

//		logfileData.append("Significant sites:\np\texp|H0\tobs(n<E) - H1\texp|H0\tobs(n<E) - H2\texp|H0\tobs(n<E) - H3\texp|H0\tobs(n<E) - RAxML\n");

//		float[] densities = {0.0f,0.001f,0.002f,0.003f,0.004f,0.005f,0.01f,0.05f,0.1f,0.5f};
//		float[] criticalValsH0H1 = new float[10];			
//		float[] criticalValsH0H2 = new float[10];			
//		float[] criticalValsH0H3 = new float[10];			
//		float[] criticalValsH0Rax = new float[10];
//		int[] extremeCountsH0H1 = new int[10];
//		int[] extremeCountsH0H2 = new int[10];
//		int[] extremeCountsH0H3 = new int[10];
//		int[] extremeCountsH0Rax = new int[10];
//		
//		for(int which=0;which<10;which++){
//			// First spp vs prestin (H0 - H1)
//			try {
//				criticalValsH0H1[which] = this.H0H1DifferencesExp.getThresholdValueAtCumulativeDensity(densities[which],1000);
//				extremeCountsH0H1[which] = this.H0H1DifferencesObs.getNumberLessThan(criticalValsH0H1[which]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				criticalValsH0H1[which] = Float.NaN;
//				extremeCountsH0H1[which] = 0;
//				e.printStackTrace();
//			}
//			// H0 - H2
//			try {
//				criticalValsH0H2[which] = this.H0H2DifferencesExp.getThresholdValueAtCumulativeDensity(densities[which],1000);
//				extremeCountsH0H2[which] = this.H0H2DifferencesObs.getNumberLessThan(criticalValsH0H2[which]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				criticalValsH0H2[which] = Float.NaN;
//				extremeCountsH0H2[which] = 0;
//				e.printStackTrace();
//			}
//			// H0 - H3
//			try {
//				criticalValsH0H3[which] = this.H0H3DifferencesExp.getThresholdValueAtCumulativeDensity(densities[which],1000);
//				extremeCountsH0H3[which] = this.H0H3DifferencesObs.getNumberLessThan(criticalValsH0H3[which]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				criticalValsH0H3[which] = Float.NaN;
//				extremeCountsH0H3[which] = 0;
//				e.printStackTrace();
//			}
//			// Next spp vs raxml
//			try {
//				criticalValsH0Rax[which] = this.H0RaxDifferencesExp.getThresholdValueAtCumulativeDensity(densities[which],1000);
//				extremeCountsH0Rax[which] = this.H0RaxDifferencesObs.getNumberLessThan(criticalValsH0Rax[which]);
//			} catch (PercentileOutOfRangeError e) {
//				// TODO Auto-generated catch block
//				criticalValsH0Rax[which] = Float.NaN;
//				extremeCountsH0Rax[which] = 0;
//				e.printStackTrace();
//			}
//			logfileData.append(densities[which]+"\t"+criticalValsH0H1[which]+"\t"+extremeCountsH0H1[which]+"\t"+criticalValsH0H2[which]+"\t"+extremeCountsH0H2[which]+"\t"+criticalValsH0H3[which]+"\t"+extremeCountsH0H3[which]+"\t"+criticalValsH0Rax[which]+"\t"+extremeCountsH0Rax[which]+"\n");
//		}
		long elapsed = (System.currentTimeMillis() - time)/1000;
		logfileData.append("\nTotal time "+elapsed+"s.\n");
		File logfile = new File(this.workDir.getAbsolutePath()+"/"+runID+".SSLS.out");
		BasicFileWriter writer = new BasicFileWriter(logfile, logfileData.toString());
	}

//	private void showGraph(String title) {
//		final JFreeChart chart = createChart(XY,title);
//		final ChartPanel chartPanel = new ChartPanel(chart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//	//	final ApplicationFrame frame = new ApplicationFrame("Joe Chart Title");
//		JFrame frame = new JFrame(title);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setContentPane(chartPanel);
//		frame.pack();
//		frame.setVisible(true);
//	}
//
//	private JFreeChart createChart(final XYDataset dataset,String title) {
//		final JFreeChart chart = ChartFactory.createScatterPlot(
//				title,                  // chart title
//				"X",                      // x axis label
//				"Y",                      // y axis label
//				dataset,                  // data
//				PlotOrientation.VERTICAL,
//				true,                     // include legend
//				true,                     // tooltips
//				false                     // urls
//		);
//		XYPlot plot = (XYPlot) chart.getPlot();
//		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//		renderer.setSeriesLinesVisible(0, true);
//		plot.setRenderer(renderer);
//		return chart;
//	}
	
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
