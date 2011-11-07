package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.TaxonNotFoundError;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.BasemlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.RAxMLAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.BasemlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;

/**
 * 
 * @author Joe Parker
 *
 * A very simple congruence analysis to compare the SSLS values of an AA dataset on two input trees.
 */
public class VerySimpleCongruenceAnalysisWithTreePruning {
	// Initialise with data and two trees
	// Aaml on tree 1
	// Aaml on tree 2
	// Compare SSLS of both.
	private File dataset;
	private File treeFileOne;
	private File treeFileTwo;
	private File treeFileRAxMLdeNovo;
	private File treeFileOnePruned;
	private File workDir;
	private String runID;
	private AlignedSequenceRepresentation sourceDataASR;
	private SequenceCodingType inputSequenceCodingType;
	private File ntTreeOneAnalysisOutputFile;
	private File ntTreeTwoAnalysisOutputFile;
	private File ntTreeDeNovoAnalysisOutputFile;
	private File pamlDataFileNT;
	private DataSeries treeOnelnL;
	private DataSeries treeTwolnL;
	private DataSeries treeDeNovolnL;
	private NewickTreeRepresentation treeOne;
	private NewickTreeRepresentation treeOnePruned;
	private NewickTreeRepresentation treeTwo;
	private NewickTreeRepresentation treeRAxML;
	private TreeSet<String> taxaList;
	
	public VerySimpleCongruenceAnalysisWithTreePruning(File data, File one, File two, File work, String ID, TreeSet<String> taxaList){
		this.dataset = data;
		this.treeFileOne = one;
		this.treeFileTwo = two;
		this.workDir = work;
		this.runID = ID;
		this.taxaList = taxaList;
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
		pamlDataFileNT = new File(dataset.getAbsoluteFile()+runID+"_pamlNT.phy");
		sourceDataASR.writePhylipFile(pamlDataFileNT, true);
		
		treeOne = new NewickTreeRepresentation(treeFileOne, taxaList);
		treeTwo = new NewickTreeRepresentation(treeFileTwo, sourceDataASR.getTaxaList());
		
		treeOne.printSimply();
		treeTwo.printSimply();
		

		/* Tree 1 needs pruning as it has the full taxon list */
		
		treeOnePruned = this.pruneTaxa(treeOne, this.excludedTaxaList(taxaList, sourceDataASR));
		treeFileOnePruned = new File(treeFileOne.getAbsoluteFile()+".pruned.tre");
		treeOnePruned.setTreeFile(treeFileOnePruned);
		treeOnePruned.write(treeFileOnePruned);
		
		/* Get a de-novo RAxML tree */
		
		RAxMLAnalysis ra = new RAxMLAnalysis(pamlDataFileNT, workDir, treeOne.getTreeFile(), runID, RAxMLAnalysis.NTmodelOptions.GTRCAT, RAxMLAnalysis.algorithmOptions.e);
		ra.setTreeConstraint(false);
		ra.setNoStartTree(true);
		ra.RunAnalysis();
		treeFileRAxMLdeNovo = new File(ra.getOutputFile()+"pruned");
		treeRAxML = new NewickTreeRepresentation(ra.getOutputFile(),taxaList); //Also prune RAxML tree
		treeRAxML.write(treeFileRAxMLdeNovo);

		
		/* Baseml runs */
		
		// Tree 1
		this.ntTreeOneAnalysisOutputFile = new File(pamlDataFileNT.getPath()+".basemlTreeOne.out");
		TreeMap<BasemlParameters, String> parameters = new TreeMap<BasemlParameters, String>();
		parameters.put(BasemlParameters.SEQFILE, "seqfile = "+pamlDataFileNT.getAbsolutePath());
		parameters.put(BasemlParameters.TREEFILE, "treefile = "+this.treeFileOnePruned.getAbsolutePath());
		parameters.put(BasemlParameters.OUTFILE, "outfile = "+ntTreeOneAnalysisOutputFile.getAbsolutePath());
		File[] treefiles = {this.treeFileOne};
		AlignedSequenceRepresentation[] datasets = {sourceDataASR};
		BasemlAnalysis treeOneBaseml = new BasemlAnalysis(datasets, treefiles, parameters,"basemlOnTreeOne.ctl");
		treeOneBaseml.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/src/").getAbsoluteFile());
		treeOneBaseml.setExecutionBinary(new File(treeOneBaseml.getBinaryDir(),"baseml"));
		treeOneBaseml.setWorkingDir(workDir);
		treeOneBaseml.RunAnalysis();
		TreeMap<String, Float> ntDataTreeOneSSLS = treeOneBaseml.getPatternSSLS();
		float[] ntDataSSLSlnL1 = new float[ntDataTreeOneSSLS.size()];
		Iterator dataSSLSItr1 = ntDataTreeOneSSLS.keySet().iterator();
		int sIndex = 0;
		while(dataSSLSItr1.hasNext()){
			ntDataSSLSlnL1[sIndex] = ntDataTreeOneSSLS.get(dataSSLSItr1.next());
			sIndex++;
		}
		treeOnelnL = new DataSeries(ntDataSSLSlnL1,"nt lnL data - tree 1 (pruned)");

		// Tree 2
		this.ntTreeTwoAnalysisOutputFile = new File(pamlDataFileNT.getPath()+".basemlTreeTwo.out");
		parameters.put(BasemlParameters.SEQFILE, "seqfile = "+pamlDataFileNT.getAbsolutePath());
		parameters.put(BasemlParameters.TREEFILE, "treefile = "+this.treeFileTwo.getAbsolutePath());
		parameters.put(BasemlParameters.OUTFILE, "outfile = "+ntTreeTwoAnalysisOutputFile.getAbsolutePath());
		treefiles[0] = this.treeFileTwo;
		BasemlAnalysis treeTwoBaseml = new BasemlAnalysis(datasets, treefiles, parameters,"basemlOnTreeTwo.ctl");
		treeTwoBaseml.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/src/").getAbsoluteFile());
		treeTwoBaseml.setExecutionBinary(new File(treeTwoBaseml.getBinaryDir(),"baseml"));
		treeTwoBaseml.setWorkingDir(workDir);
		treeTwoBaseml.RunAnalysis();
		TreeMap<String, Float> ntDataTreeTwoSSLS = treeTwoBaseml.getPatternSSLS();
		float[] ntDataSSLSlnL2 = new float[ntDataTreeTwoSSLS.size()];
		Iterator dataSSLSItr2 = ntDataTreeTwoSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItr2.hasNext()){
			ntDataSSLSlnL2[sIndex] = ntDataTreeTwoSSLS.get(dataSSLSItr2.next());
			sIndex++;
		}
		treeTwolnL = new DataSeries(ntDataSSLSlnL2,"nt lnL data - tree 2");
		

		// RAxML Tree
		this.ntTreeDeNovoAnalysisOutputFile = new File(pamlDataFileNT.getPath()+".basemlTreeDeNovo.out");
		parameters.put(BasemlParameters.SEQFILE, "seqfile = "+pamlDataFileNT.getAbsolutePath());
		parameters.put(BasemlParameters.TREEFILE, "treefile = "+this.treeFileRAxMLdeNovo.getAbsolutePath());
		parameters.put(BasemlParameters.OUTFILE, "outfile = "+ntTreeDeNovoAnalysisOutputFile.getAbsolutePath());
		treefiles[0] = this.treeFileRAxMLdeNovo;
		BasemlAnalysis treeDeNovoBaseml = new BasemlAnalysis(datasets, treefiles, parameters,"basemlOnTreeDeNovo.ctl");
		treeDeNovoBaseml.setBinaryDir(new File("/Applications/Phylogenetics/PAML/paml44_myVersion/src/").getAbsoluteFile());
		treeDeNovoBaseml.setExecutionBinary(new File(treeDeNovoBaseml.getBinaryDir(),"baseml"));
		treeDeNovoBaseml.setWorkingDir(workDir);
		treeDeNovoBaseml.RunAnalysis();
		TreeMap<String, Float> ntDataTreeDeNovoSSLS = treeDeNovoBaseml.getPatternSSLS();
		float[] ntDataSSLSlnLdeNovo = new float[ntDataTreeDeNovoSSLS.size()];
		Iterator dataSSLSItrdeNovo = ntDataTreeDeNovoSSLS.keySet().iterator();
		sIndex = 0;
		while(dataSSLSItrdeNovo.hasNext()){
			ntDataSSLSlnLdeNovo[sIndex] = ntDataTreeDeNovoSSLS.get(dataSSLSItrdeNovo.next());
			sIndex++;
		}
		treeDeNovolnL = new DataSeries(ntDataSSLSlnLdeNovo,"nt lnL data - RAxML tree");

		
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

	private NewickTreeRepresentation pruneTaxa(NewickTreeRepresentation unprunedTree, TreeSet<String> taxaToPrune){
		Iterator itrTaxon = taxaToPrune.iterator();
		while(itrTaxon.hasNext()){
			String taxonToPrune = (String)itrTaxon.next();
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
			String aTaxon = (String) itrComp.next();
			if(!includedList.contains(aTaxon)){
				excludedList.add(aTaxon);
			}
		}
		return excludedList;
	}
}
