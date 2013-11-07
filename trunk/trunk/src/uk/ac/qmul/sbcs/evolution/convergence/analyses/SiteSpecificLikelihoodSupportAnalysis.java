package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaListsMismatchException;
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
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;
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
 * 
 * NEW for revision r119:
 * 		Makes use of the new codon-trimming and stop-codon-removal routines in AlignedSequenceRepresentation (r116)
 * 		Prints out a DNA phylip file with no invariant codons or stop codons. This should be the same as the AA one that is translated, and is indended for parallel/subsequent CODEML analyses.
 */
public class SiteSpecificLikelihoodSupportAnalysis {
	// Initialise with data and two trees
	// Aaml on tree 1
	// Aaml on tree 2
	// Compare SSLS of both.
	private File dataset;
	private File treeFileH0;
	private File treeFileH1;
	private File treeFileH2;
	private File treeFileH3;
	private File treeFileH1CladeLabelled;
	private File treeFileH2CladeLabelled;
	private File treeFileH3CladeLabelled;
	private File treeFileRAxMLdeNovo;
	private File treeFileH0Pruned;
	private File treeFileH1Pruned;
	private File treeFileH2Pruned;
	private File treeFileH3Pruned;
	private File treeFileH1CladeLabelledPruned;
	private File treeFileH2CladeLabelledPruned;
	private File treeFileH3CladeLabelledPruned;
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
	private File pamlDataFileNT;
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
	private NewickTreeRepresentation treeCladeLabelledH1;
	private NewickTreeRepresentation treeCladeLabelledH1Pruned;
	private NewickTreeRepresentation treeCladeLabelledH2;
	private NewickTreeRepresentation treeCladeLabelledH2Pruned;
	private NewickTreeRepresentation treeCladeLabelledH3;
	private NewickTreeRepresentation treeCladeLabelledH3Pruned;
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
	
	/*
	 * 	Global variables added for this refactoring (06/03/2013)
	 */
	private String[] modelsList;
	private SitewiseSpecificLikelihoodSupportAaml[] results;
	private File mainTreesFile;
	private File constraintTreeFile;
	private File labelledTreesFile;
	private File randomTreesFile;
	private File mainTreesFilePruned;
	private File labelledTreesFilePruned;
	private File randomTreesFilePruned;
	private File constraintTreeFilePruned;
	private NewickTreeRepresentation mainTrees;
	private NewickTreeRepresentation randomTrees;
	private NewickTreeRepresentation constraintTree;
	private NewickTreeRepresentation labelledTrees;
	private NewickTreeRepresentation resolvedTree;
	private NewickTreeRepresentation treeRAxML;
	private boolean doFullyUnconstrainedRAxML = false;
	private TreeSet<String> excludedTaxa;
	private String[] initialisationTasks;
	private String[] exitTasks;

	
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
	public SiteSpecificLikelihoodSupportAnalysis(File data, File treefileH0, File treefileH1, File treefileH2, File treefileH3, File treefileH1labelled, File treefileH2labelled, File treefileH3labelled, File work, File binariesLocation, String ID, TreeSet<String> taxaList, int sitesToSimulate, int thisFilter, boolean filterThisByFactor){
		this.dataset = data;
		this.treeFileH0 = treefileH0;
		this.treeFileH1 = treefileH1;
		this.treeFileH2 = treefileH2;
		this.treeFileH3 = treefileH3;
		this.treeFileH1CladeLabelled = treefileH1labelled;
		this.treeFileH2CladeLabelled = treefileH2labelled;
		this.treeFileH3CladeLabelled = treefileH3labelled;
		this.workDir = work;
		this.runID = ID;
		this.taxaList = taxaList;
		this.binariesLocation = binariesLocation;
		this.sitesInSimulations = sitesToSimulate;
		this.evolverBinary = new File(binariesLocation+"/evolver");
		this.filter = thisFilter;
		this.filterByFactor = filterThisByFactor;
	}
	
	public SiteSpecificLikelihoodSupportAnalysis(File dataSet2, File mainTreesFile, File constraintTreeFile, File labelledTreesFile, File randomTrees, File workDir2, File binaries, String runID2, TreeSet<String> taxaList2, String[] modelsList2, int thisFilter, boolean doFactor, String[] begins, String[] ends) {
		// TODO Auto-generated constructor stub
		this.dataset = dataSet2;
		this.mainTreesFile = mainTreesFile;
		this.constraintTreeFile = constraintTreeFile;
		this.labelledTreesFile = labelledTreesFile;
		this.randomTreesFile = randomTrees;
		this.workDir = workDir2;
		this.runID = runID2;
		this.taxaList = taxaList2;
		this.modelsList = modelsList2;
		this.binariesLocation = binaries;
		this.evolverBinary = new File(binariesLocation+"/evolver");
		this.filter = thisFilter;
		this.filterByFactor = doFactor;
		this.mainTreesFilePruned = new File(this.mainTreesFile.getAbsoluteFile()+".pruned.tre");
		this.labelledTreesFilePruned = new File(this.labelledTreesFile.getAbsoluteFile()+".pruned.tre");
		this.constraintTreeFilePruned = new File(this.constraintTreeFile.getAbsoluteFile()+".pruned.tre");
		this.initialisationTasks = begins;
		this.exitTasks = ends;
	}

	/**
	 * This is a utility method to initialise the NewickTreeRepresentations and AlignedSequenceRepresentation so they will be stored in the XML. 
	 * It isn't strictly necessary.
	 * It <b>may</b> conflict with the same calls made later...
	 */
	public void preValidate(){
		this.basicInputAlignmentOperations();
		this.basicInputTreefileInitialisation();
	}
	
	/**
	 * 	-    prune input.trees
	 *	-    prune labelled.trees
	 *  -	 prune random trees
	 *	-    prune constraint.tre
	 *  -    cat random trees with input
	 *	-    RAxML -g on constraint.tre
	 *	-    cat RAxML.tre with input.trees
	 *  -    cat RAxML tree with input (if it exists)
	 *	-    for each (model:models) {get sitewise lnL for all trees}
	 */
	public void run(){
		long time = System.currentTimeMillis();
		System.out.println(dataset.getAbsolutePath().toString());
		System.out.println(workDir.getAbsolutePath().toString());
		
		this.runInitTasks();
		
		/* Basic input seq file operations */
		
		this.basicInputAlignmentOperations();
		this.basicInputTreefileInitialisation();
		this.excludedTaxa = this.excludedTaxaList(taxaList, sourceDataASR);		

		if(this.excludedTaxa.size()>0){
			/* Not all the taxa given in the taxa list are there. Pruning of input trees required */
		
			/* Prune input trees */
			
			this.pruneInputTrees();
			
			
			/* Prune labelled trees */
			
			this.pruneLabelledTrees();
			
			/* Prune random trees */
			
			this.pruneRandomTrees();
			
			/* Prune constraint.tre */
			
			this.pruneConstraintTree();
		}
		
		if(this.doFullyUnconstrainedRAxML){
			/* Get a de-novo RAxML tree */
			
			RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, this.mainTreesFile, runID, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
			ra.setTreeConstraint(false);
			ra.setNoStartTree(true);
			ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
		//	ra.setWorkingDir(this.workDir);
			ra.RunAnalysis();
			treeFileRAxMLdeNovo = ra.getOutputFile();
			treeRAxML = new NewickTreeRepresentation(treeFileRAxMLdeNovo,taxaList);
		}
		
		/* Resolve old H2(3) topology by RAxML -g */
		/* NB THIS ASSUMES A SINGLE TREE */
		this.constraintTree.write(this.constraintTreeFilePruned);
		this.resolvedTree = this.resolveTopologyWithSubtreeConstraint(this.constraintTree);
		
		
		/* Concatenate pruned, resolved RAxML topology with pruned a priori ones */
		try {
			if(randomTrees.getNumberOfTrees() > 0){
				this.mainTrees = this.mainTrees.concatenate(randomTrees);
			}
			if(resolvedTree.getNumberOfTrees() > 0){
				this.mainTrees = this.mainTrees.concatenate(resolvedTree);
			}
			if(this.doFullyUnconstrainedRAxML){
				this.mainTrees = this.mainTrees.concatenate(treeRAxML);
			}
		} catch (TaxaListsMismatchException e) {
			e.printStackTrace();
		}
		
		/* Write the pruned trees (labelled and main trees files; File vars are global) to disk for PAML */
		mainTrees.write(this.mainTreesFilePruned);
		labelledTrees.write(this.labelledTreesFilePruned);
		
		/* For each model, get lnL site patterns, for all trees */
		
		SitewiseSpecificLikelihoodSupportAaml SSLS;
		
		for(int i=0;i<modelsList.length;i++){
			String thisModel = modelsList[i];
			/* Get the lnL for this */
			/* Pasting in the Aaml H0 from old go() method.. lots of this could be abstracted */
			/* we want to populate a SSLS object eventually... */
			SSLS = new SitewiseSpecificLikelihoodSupportAaml(sourceDataASR);
			this.aaH0AnalysisOutputFile = new File(workDir.getAbsolutePath()+"/aaml.out");
			TreeMap<AamlParameters, String> parameters = new TreeMap<AamlParameters, String>();
			parameters.put(AamlParameters.SEQFILE, "seqfile = "+pamlDataFileAA.getAbsolutePath());
			parameters.put(AamlParameters.TREEFILE, "treefile = "+this.mainTreesFilePruned.getAbsolutePath());
			parameters.put(AamlParameters.OUTFILE, "outfile = "+aaH0AnalysisOutputFile.getAbsolutePath());
			parameters.put(AamlParameters.AARATEFILE, "aaRatefile = "+this.binariesLocation.getAbsolutePath()+"/dat/"+thisModel+".dat");
			File[] treefiles = {this.mainTreesFilePruned};
			AlignedSequenceRepresentation[] datasets = {new AlignedSequenceRepresentation()};
			AamlAnalysisSGE treeOneAaml = new AamlAnalysisSGE(datasets, treefiles, parameters,"aamlOnTreeOne.ctl");
			treeOneAaml.setBinaryDir(this.binariesLocation.getAbsoluteFile());
			treeOneAaml.setExecutionBinary(new File(treeOneAaml.getBinaryDir(),"codeml"));
			treeOneAaml.setWorkingDir(new File("./")); // BIG - will this work on the cluster?
			treeOneAaml.setNumberOfTreesets(this.mainTrees.getNumberOfTrees());
			SSLS.setParameters((TreeMap<AamlParameters, String>) parameters.clone());
			treeOneAaml.RunAnalysis();

			/*
			TreeMap<String, Float> aaDataTreeOneSSLS = treeOneAaml.getPatternSSLS();
			float[] aaDataSSLSlnL0 = new float[aaDataTreeOneSSLS.size()];
			Iterator dataSSLSItr0 = aaDataTreeOneSSLS.keySet().iterator();
			int sIndex = 0;
			while(dataSSLSItr0.hasNext()){
				aaDataSSLSlnL0[sIndex] = aaDataTreeOneSSLS.get(dataSSLSItr0.next());
				sIndex++;
			}
			*/
//			treeOnelnL = new DataSeries(aaDataSSLSlnL1,"aa lnL data - tree 1");
//			treeH0ObservedlnL = new ExperimentalDataSeries(sourceDataASR.getFullSitesLnL(aaDataTreeOneSSLS));
			
			/**
			 * At this point everything would be loaded into a SitewiseSpecificLikelihoodSupport object
			 * then SSLS.stop() called (to get runtime)
			 * and SSLS would be serialised (to be later inflated in client)
			 */
			
			SSLS.setDoFilter(filterByFactor);
			SSLS.setFilterFactor(filter);
			SSLS.setInputFileName(this.dataset.getName());
			SSLS.setInputFile(this.dataset);
			SSLS.setNumberOfModels(1);
			SSLS.setNumberOfSites(this.sourceDataASR.getNumberOfSites());
			SSLS.setNumberOfSitePatterns(treeOneAaml.getPatternSSLS().size());
			SSLS.setNumberOfTopologies(mainTrees.getNumberOfTrees());
			SSLS.setNumberOfTaxa(mainTrees.getNumberOfTaxa());
			SSLS.setModel(thisModel);
			SSLS.setTaxaList(taxaList);
			SSLS.setNumberOfSeries(SSLS.getNumberOfModels() * SSLS.getNumberOfTopologies());
			SSLS.setPatternLikelihoods(treeOneAaml.getAllPatternSSLS());
			SSLS.parseAamlOutput(aaH0AnalysisOutputFile);
			SSLS.fillOutAndVerify();
			
			try {
				FileOutputStream fileOutOne = new FileOutputStream(this.workDir.getAbsolutePath()+"/"+this.workDir.getName()+this.runID+thisModel+".ser");
				ObjectOutputStream outOne;
				outOne = new ObjectOutputStream(fileOutOne);
				outOne.writeObject(SSLS);
				outOne.close();
				fileOutOne.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		/* Collate info and print to buffer etc */
		
		long elapsed = (System.currentTimeMillis() - time)/1000;
		logfileData.append("\nTotal time "+elapsed+"s.\n");
		File logfile = new File(this.workDir.getAbsolutePath()+"/"+runID+".SSLS.out");
		new BasicFileWriter(logfile, logfileData.toString());
		
		this.runExitTasks();
	}
	
	private void runInitTasks() {
		for(String task:this.initialisationTasks){
			new VerboseSystemCommand(task);
		}
	}

	private void runExitTasks() {
		for(String task:this.exitTasks){
			new VerboseSystemCommand(task);
		}
	}

	private void basicInputTreefileInitialisation() {
		this.mainTrees 		= new NewickTreeRepresentation(this.mainTreesFile, 		this.taxaList);
		this.labelledTrees 	= new NewickTreeRepresentation(this.labelledTreesFile, 	this.taxaList);
		this.randomTrees	= new NewickTreeRepresentation(this.randomTreesFile,	this.taxaList);
		this.constraintTree = new NewickTreeRepresentation(this.constraintTreeFile, this.taxaList);
	}

	/**
	 * Routine to resolve a topology with soft polytomies by RAxML
	 * <br/><b>nb.</b> RAxML will only allow one tree at a time, so have to concat them.
	 * @since r135; updated r191 to allow for multiple constraint trees.
	 * @param constraintTree2
	 * @return
	 */
	private NewickTreeRepresentation resolveTopologyWithSubtreeConstraint(NewickTreeRepresentation constraintTree2) {
		if(this.constraintTree.getNumberOfTrees() == 1){
			/*
			 * There is only one constraint tree in the constraint tree file; proceed as per r135
			 */
			RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, this.constraintTreeFilePruned, runID, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
			ra.setTreeConstraint(true);
			ra.setMultifuricatingConstraint(true);
			ra.setNoStartTree(true);
			ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
		//	ra.setWorkingDir(this.workDir);
			ra.RunAnalysis();
			resolvedTree = new NewickTreeRepresentation(ra.getOutputFile(),taxaList);
			return resolvedTree;
		}else{
			/*
			 * More than one constraint tree is present. Resolve them separately by RAxML
			 */
			String[] prunedUnresolvedTrees = constraintTree.getIndividualTrees();
			String prunedResolvedTrees = ""; 
			for(int i=0;i<constraintTree.getNumberOfTrees();i++){
				// Output single tree to a file
				File temporarySingleConstraintTree = new File(this.constraintTreeFile.getAbsolutePath()+"_tmp_"+i);
				new BasicFileWriter(temporarySingleConstraintTree,prunedUnresolvedTrees[i]+";");
				// Do raxml and harvest output
				RAxMLAnalysisSGE ra = new RAxMLAnalysisSGE(pamlDataFileAA, workDir, temporarySingleConstraintTree, runID+"_"+i, RAxMLAnalysisSGE.AAmodelOptions.PROTCATDAYHOFF, RAxMLAnalysisSGE.algorithmOptions.e);
				ra.setTreeConstraint(true);
				ra.setMultifuricatingConstraint(true);
				ra.setNoStartTree(true);
				ra.setBinaryDir(new File(this.binariesLocation.getAbsoluteFile()+"/raxmlHPC"));
			//	ra.setWorkingDir(this.workDir);
				ra.RunAnalysis();
				prunedResolvedTrees += ra.getBestTree()+"\n";
			}
			
			resolvedTree = new NewickTreeRepresentation(prunedResolvedTrees,taxaList);
			return resolvedTree;
			
		}
	}

	private void pruneConstraintTree() {
		NewickTreeRepresentation unprunedConstraintTree = new NewickTreeRepresentation(this.constraintTreeFile, this.taxaList);
		this.constraintTree = this.pruneTaxa(unprunedConstraintTree, excludedTaxa);
	}

	private void pruneRandomTrees() {
		NewickTreeRepresentation unprunedRandomTrees = new NewickTreeRepresentation(this.randomTreesFile, this.taxaList);
		this.randomTrees = this.pruneTaxa(unprunedRandomTrees, excludedTaxa);
		
	}

	private void pruneLabelledTrees() {
		NewickTreeRepresentation unprunedLabelledTrees = new NewickTreeRepresentation(this.labelledTreesFile, this.taxaList);
		this.labelledTrees = this.pruneTaxa(unprunedLabelledTrees, excludedTaxa);
	}

	private void pruneInputTrees() {
		NewickTreeRepresentation unprunedMainTrees = new NewickTreeRepresentation(this.mainTreesFile, this.taxaList);
		this.mainTrees = this.pruneTaxa(unprunedMainTrees, excludedTaxa);
	}

	private void basicInputAlignmentOperations() {
		this.sourceDataASR = new AlignedSequenceRepresentation();
		try {
			sourceDataASR.loadSequences(dataset,false);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * @since r119, 22/08/2012
		 * The removeUnambiguousGaps call has now been placed by integer-codon trimming and stop-codon removal at the codon level
		 * since it is anticipated that sequence data will be exons or CDS, therefore potential exists that single-nt removals, even where 
		 * invariant (unambiguous) gaps, could cause frameshifts.
		 * 
		 * sourceDataASR.removeUnambiguousGaps(); //removed
		 */
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		try {
			sourceDataASR.trimToWholeNumberOfCodons();
			sourceDataASR.removeStopCodons();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		pamlDataFileNT = new File(dataset.getAbsoluteFile()+runID+"_pamlNT.phy");
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
			// TODO IMPORTANT
			// At the moment the filterFor..() method DOES NOT respect codon boundaries
			// As a result frameshift can occur.
			// Using filterFactor 100 for now (22/08/2012) until such time as it is made codon-safe.
			// Ultimately should throw a SequenceTypeNotSupportedException for Codon datatypes and direct a (codon-safe) method.
		} catch (FilterOutOfAllowableRangeException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		sourceDataASR.writePhylipFile(pamlDataFileNT, true);
		try {
			sourceDataASR.translate(true);
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.writePhylipFile(pamlDataFileAA, true);
		
	}

	/**
	 * @deprecated - this method should not normally be used by CongruenceRunner, but run() instead. 
	 */
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
		/**
		 * @since r119, 22/08/2012
		 * The removeUnambiguousGaps call has now been placed by integer-codon trimming and stop-codon removal at the codon level
		 * since it is anticipated that sequence data will be exons or CDS, therefore potential exists that single-nt removals, even where 
		 * invariant (unambiguous) gaps, could cause frameshifts.
		 * 
		 * sourceDataASR.removeUnambiguousGaps(); //removed
		 */
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		try {
			sourceDataASR.trimToWholeNumberOfCodons();
			sourceDataASR.removeStopCodons();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inputSequenceCodingType = sourceDataASR.determineInputSequenceType();
		pamlDataFileNT = new File(dataset.getAbsoluteFile()+runID+"_pamlNT.phy");
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
			// TODO IMPORTANT
			// At the moment the filterFor..() method DOES NOT respect codon boundaries
			// As a result frameshift can occur.
			// Using filterFactor 100 for now (22/08/2012) until such time as it is made codon-safe.
			// Ultimately should throw a SequenceTypeNotSupportedException for Codon datatypes and direct a (codon-safe) method.
		} catch (FilterOutOfAllowableRangeException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		sourceDataASR.writePhylipFile(pamlDataFileNT, true);
		try {
			sourceDataASR.translate(true);
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceDataASR.writePhylipFile(pamlDataFileAA, true);
		
		treeH0 = new NewickTreeRepresentation(treeFileH0, taxaList);
		treeH1 = new NewickTreeRepresentation(treeFileH1, taxaList);
		treeH2 = new NewickTreeRepresentation(treeFileH2, taxaList);
		treeH3 = new NewickTreeRepresentation(treeFileH3, taxaList);
		treeCladeLabelledH1 = new NewickTreeRepresentation(treeFileH1CladeLabelled, taxaList);
		treeCladeLabelledH2 = new NewickTreeRepresentation(treeFileH2CladeLabelled, taxaList);
		treeCladeLabelledH3 = new NewickTreeRepresentation(treeFileH3CladeLabelled, taxaList);
		
		TreeSet<String> excludedTaxa = this.excludedTaxaList(taxaList, sourceDataASR);
		if(excludedTaxa.size()<1){
			// pruning actions are redundant..
			/* Tree H0 needs pruning as it has the full taxon list */
			
			treeH0Pruned = treeH0;
			treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
			treeH0Pruned.setTreeFile(treeFileH0Pruned);
			treeH0Pruned.write(treeFileH0Pruned);

			/* Ditto, Tree H1 needs pruning as it has the full taxon list */
			
			treeH1Pruned = treeH1;
			treeFileH1Pruned = new File(treeFileH1.getAbsoluteFile()+".pruned.tre");
			treeH1Pruned.setTreeFile(treeFileH1Pruned);
			treeH1Pruned.write(treeFileH1Pruned);
			
			/* Ditto, Tree H2 needs pruning as it has the full taxon list */
			
			treeH2Pruned = treeH2;
			treeFileH2Pruned = new File(treeFileH2.getAbsoluteFile()+".pruned.tre");
			treeH2Pruned.setTreeFile(treeFileH2Pruned);
			treeH2Pruned.write(treeFileH2Pruned);

			/* Ditto, Tree H3 needs pruning as it has the full taxon list */
			
			treeH3Pruned = treeH3;
			treeFileH3Pruned = new File(treeFileH3.getAbsoluteFile()+".pruned.tre");
			treeH3Pruned.setTreeFile(treeFileH3Pruned);
			treeH3Pruned.write(treeFileH3Pruned);

			/* Ditto, LABELLED Tree H1 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH1Pruned = treeCladeLabelledH1;
			treeFileH1CladeLabelledPruned = new File(treeFileH1CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH1Pruned.setTreeFile(treeFileH1CladeLabelledPruned);
			treeCladeLabelledH1Pruned.write(treeFileH1CladeLabelledPruned);
			
			/* Ditto, LABELLED Tree H2 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH2Pruned = treeCladeLabelledH2;
			treeFileH2CladeLabelledPruned = new File(treeFileH2CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH2Pruned.setTreeFile(treeFileH2CladeLabelledPruned);
			treeCladeLabelledH2Pruned.write(treeFileH2CladeLabelledPruned);

			/* Ditto, LABELLED Tree H3 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH3Pruned = treeCladeLabelledH3;
			treeFileH3CladeLabelledPruned = new File(treeFileH3CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH3Pruned.setTreeFile(treeFileH3CladeLabelledPruned);
			treeCladeLabelledH3Pruned.write(treeFileH3CladeLabelledPruned);
		}else{
			// there are ≥1 taxa to prune...
			/* Tree H0 needs pruning as it has the full taxon list */
			
			treeH0Pruned = this.pruneTaxa(treeH0, excludedTaxa);
			treeFileH0Pruned = new File(treeFileH0.getAbsoluteFile()+".pruned.tre");
			treeH0Pruned.setTreeFile(treeFileH0Pruned);
			treeH0Pruned.write(treeFileH0Pruned);

			/* Ditto, Tree H1 needs pruning as it has the full taxon list */
			
			treeH1Pruned = this.pruneTaxa(treeH1, excludedTaxa);
			treeFileH1Pruned = new File(treeFileH1.getAbsoluteFile()+".pruned.tre");
			treeH1Pruned.setTreeFile(treeFileH1Pruned);
			treeH1Pruned.write(treeFileH1Pruned);
			
			/* Ditto, Tree H2 needs pruning as it has the full taxon list */
			
			treeH2Pruned = this.pruneTaxa(treeH2, excludedTaxa);
			treeFileH2Pruned = new File(treeFileH2.getAbsoluteFile()+".pruned.tre");
			treeH2Pruned.setTreeFile(treeFileH2Pruned);
			treeH2Pruned.write(treeFileH2Pruned);

			/* Ditto, Tree H3 needs pruning as it has the full taxon list */
			
			treeH3Pruned = this.pruneTaxa(treeH3, excludedTaxa);
			treeFileH3Pruned = new File(treeFileH3.getAbsoluteFile()+".pruned.tre");
			treeH3Pruned.setTreeFile(treeFileH3Pruned);
			treeH3Pruned.write(treeFileH3Pruned);

			/* Ditto, LABELLED Tree H1 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH1Pruned = this.pruneTaxa(treeCladeLabelledH1, excludedTaxa);
			treeFileH1CladeLabelledPruned = new File(treeFileH1CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH1Pruned.setTreeFile(treeFileH1CladeLabelledPruned);
			treeCladeLabelledH1Pruned.write(treeFileH1CladeLabelledPruned);
			
			/* Ditto, LABELLED Tree H2 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH2Pruned = this.pruneTaxa(treeCladeLabelledH2, excludedTaxa);
			treeFileH2CladeLabelledPruned = new File(treeFileH2CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH2Pruned.setTreeFile(treeFileH2CladeLabelledPruned);
			treeCladeLabelledH2Pruned.write(treeFileH2CladeLabelledPruned);

			/* Ditto, LABELLED Tree H3 needs pruning as it has the full taxon list */
			
			treeCladeLabelledH3Pruned = this.pruneTaxa(treeCladeLabelledH3, excludedTaxa);
			treeFileH3CladeLabelledPruned = new File(treeFileH3CladeLabelled.getAbsoluteFile()+".pruned.tre");
			treeCladeLabelledH3Pruned.setTreeFile(treeFileH3CladeLabelledPruned);
			treeCladeLabelledH3Pruned.write(treeFileH3CladeLabelledPruned);
		}

		
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
		File nw_prune_exe = new File(this.binariesLocation.getAbsolutePath() + "/nw_prune");
		if(nw_prune_exe.exists()){
			//TODO implement NW_prune for pruning, not my lamo method
			NewickTreeRepresentation prunedTree = new NewickUtilitiesHandler(this.binariesLocation, unprunedTree.getTreeFile(), this.taxaList).pruneTaxa(taxaToPrune);
			unprunedTree = prunedTree;
		}else{
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

	/**
	 * @return the doFullyUnconstrainedRAxML
	 */
	public boolean isDoFullyUnconstrainedRAxML() {
		return doFullyUnconstrainedRAxML;
	}

	/**
	 * @param doFullyUnconstrainedRAxML the doFullyUnconstrainedRAxML to set
	 */
	public void setDoFullyUnconstrainedRAxML(boolean doFullyUnconstrainedRAxML) {
		this.doFullyUnconstrainedRAxML = doFullyUnconstrainedRAxML;
	}
}
