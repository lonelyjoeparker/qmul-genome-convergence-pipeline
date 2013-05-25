package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;

/**
 * @author joeparker
 * @since r130 12/3/2013
 * Class to contain basically all the SSLS features required.
 * 
 * <p>Designed to be initialised with ASR; 
 * <br/>SSLS patterns loaded; 
 * <br/>other aaml run parameters (model; overall lnL, alpha, fitted tree, li etc) loaded from parsed aaml.out file;
 * <br/>ÆSSLS calculated from patterns; 
 * <br/>serialised and reinflated as object afterwards (UNTESTED) (e.g. downloaded...)
 * 
 * <p><b>IMPORTANT</b>: <i>One</i> model (Aaml run) per object.......
 * 
 */

public class SitewiseSpecificLikelihoodSupportAaml implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1827398426573836001L;
	private String inputFileName;
	private File inputFile;
	private int numberOfTaxa;
	private int numberOfSites;
	private int numberOfSitePatterns;
	private int numberOfTopologies;
	private int numberOfModels;
	private int numberOfSeries;
	private Date started;
	private Date finished;
	private TreeMap<AamlParameters, String> parameters;
	private String model;
	private HashMap<String,Integer> patternMapping;	
	private TreeSet<String> taxaList;
	private TreeMap<String,Float>[] patternLikelihoods;
	private float homogeneityChiSq;
	private float[][] frequenciesMatrix;
	private float[] treeLengths;
	private String[] fittedTrees;
	private float[] meanlnL;
	private float[] alpha;
	private float[] li;
	private float[] pKH;
	private float[] pRELL;
	private float[] pSH;
	private boolean[] preferred;
	private float[][] SSLSseriesSitewise;
	private float[][] SSLSseriesPatternwise;
	private float[][] dSSLSseriesSitewise;
	private float[][] dSSLSseriesPatternwise;
	private String[] patterns;						// patterns, in order of lnf file, as String[]
	private float[] patternEntropies;				// entropy of each pattern
	private String[] sites;							// TRANSPOSED sites (eqivalent to patterns), in alignment order, as String[]
	private float[] siteEntropies;					// entropy of each transposed site (equivalent to patterns)
	private char[][] datasetAsCharMatrix; 			// dataset (alignment) as char[][]
	private char[][] transposedDatasetAsCharMatrix; // TRANSPOSED dataset (alignment) as char[][]
	private AlignedSequenceRepresentation dataset;
	private boolean doFilter;
	private int filterFactor;
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the homogeneityChiSq
	 */
	public float getHomogeneityChiSq() {
		return homogeneityChiSq;
	}
	/**
	 * @param homogeneityChiSq the homogeneityChiSq to set
	 */
	public void setHomogeneityChiSq(float homogeneityChiSq) {
		this.homogeneityChiSq = homogeneityChiSq;
	}

	/**
	 * @return the frequenciesMatrix
	 */
	public float[][] getFrequenciesMatrix() {
		return frequenciesMatrix;
	}
	/**
	 * @return the fittedTrees
	 */
	public String[] getFittedTrees() {
		return fittedTrees;
	}
	/**
	 * @return the doFilter
	 */
	public boolean isDoFilter() {
		return doFilter;
	}
	/**
	 * @return the filterFactor
	 */
	public int getFilterFactor() {
		return filterFactor;
	}
	/**
	 * @param frequenciesMatrix the frequenciesMatrix to set
	 */
	public void setFrequenciesMatrix(float[][] frequenciesMatrix) {
		this.frequenciesMatrix = frequenciesMatrix;
	}
	/**
	 * @param fittedTrees the fittedTrees to set
	 */
	public void setFittedTrees(String[] fittedTrees) {
		this.fittedTrees = fittedTrees;
	}
	/**
	 * @param doFilter the doFilter to set
	 */
	public void setDoFilter(boolean doFilter) {
		this.doFilter = doFilter;
	}
	/**
	 * @param filterFactor the filterFactor to set
	 */
	public void setFilterFactor(int filterFactor) {
		this.filterFactor = filterFactor;
	}
	public SitewiseSpecificLikelihoodSupportAaml(){}
	public SitewiseSpecificLikelihoodSupportAaml(AlignedSequenceRepresentation asr){
		this.dataset = asr;
		this.started = new Date(System.currentTimeMillis());
	}
	public SitewiseSpecificLikelihoodSupportAaml(AlignedSequenceRepresentation asr, int taxa, int trees, int sites, int patterns, TreeMap<String,Float>[] lnLpatterns){}
	public SitewiseSpecificLikelihoodSupportAaml(AlignedSequenceRepresentation asr, int taxa, int trees, int sites, int patterns, TreeMap<String,Float>[] lnLpatterns, String datasetID, String[] models){}

	/**
	 * @return the inputFileName
	 */
	public String getInputFileName() {
		return inputFileName;
	}
	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @return the patternEntropies
	 */
	public float[] getPatternEntropies() {
		return patternEntropies;
	}
	/**
	 * @return the siteEntropies
	 */
	public float[] getSiteEntropies() {
		return siteEntropies;
	}
	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @param patternEntropies the patternEntropies to set
	 */
	public void setPatternEntropies(float[] patternEntropies) {
		this.patternEntropies = patternEntropies;
	}
	/**
	 * @param siteEntropies the siteEntropies to set
	 */
	public void setSiteEntropies(float[] siteEntropies) {
		this.siteEntropies = siteEntropies;
	}
	/**
	 * @return the numberOfTaxa
	 */
	public int getNumberOfTaxa() {
		this.numberOfTaxa = this.dataset.getNumberOfTaxa();
		return numberOfTaxa;
	}
	/**
	 * @return the numberOfSites
	 */
	public int getNumberOfSites() {
		return numberOfSites;
	}
	/**
	 * @return the numberOfSitePatterns
	 */
	public int getNumberOfSitePatterns() {
		return numberOfSitePatterns;
	}
	/**
	 * @return the numberOfTopologies
	 */
	public int getNumberOfTopologies() {
		return numberOfTopologies;
	}
	/**
	 * @return the numberOfModels
	 */
	public int getNumberOfModels() {
		return numberOfModels;
	}
	/**
	 * @return the numberOfSeries
	 */
	public int getNumberOfSeries() {
		return numberOfSeries;
	}
	/**
	 * @return the started
	 */
	public Date getStarted() {
		return started;
	}
	/**
	 * @return the finished
	 */
	public Date getFinished() {
		return finished;
	}
	/**
	 * @return the patternMapping
	 */
	public HashMap<String, Integer> getPatternMapping() {
		return patternMapping;
	}
	/**
	 * @return the taxaList
	 */
	public TreeSet<String> getTaxaList() {
		return taxaList;
	}
	/**
	 * @return the patternLikelihoods
	 */
	public TreeMap<String, Float>[] getPatternLikelihoods() {
		return patternLikelihoods;
	}
	/**
	 * @return the treeLengths
	 */
	public float[] getTreeLengths() {
		return treeLengths;
	}
	/**
	 * @return the li
	 */
	public float[] getLi() {
		return li;
	}
	/**
	 * @return the pKH
	 */
	public float[] getpKH() {
		return pKH;
	}
	/**
	 * @return the pRELL
	 */
	public float[] getpRELL() {
		return pRELL;
	}
	/**
	 * @return the pSH
	 */
	public float[] getpSH() {
		return pSH;
	}
	/**
	 * @return the preferred
	 */
	public boolean[] getPreferred() {
		return preferred;
	}
	/**
	 * @return the meanlnL
	 */
	public float[] getMeanlnL() {
		return meanlnL;
	}
	/**
	 * @return the alpha
	 */
	public float[] getAlpha() {
		return alpha;
	}
	/**
	 * @return the sSLSseriesSitewise
	 */
	public float[][] getSSLSseriesSitewise() {
		return SSLSseriesSitewise;
	}
	/**
	 * @return the sSLSseriesPatternwise
	 */
	public float[][] getSSLSseriesPatternwise() {
		return SSLSseriesPatternwise;
	}
	/**
	 * @return the dSSLSseriesSitewise
	 */
	public float[][] getdSSLSseriesSitewise() {
		return dSSLSseriesSitewise;
	}
	/**
	 * @return the dSSLSseriesPatternwise
	 */
	public float[][] getdSSLSseriesPatternwise() {
		return dSSLSseriesPatternwise;
	}
	/**
	 * @return the patterns
	 */
	public String[] getPatterns() {
		return patterns;
	}
	/**
	 * @return the sites
	 */
	public String[] getSites() {
		return sites;
	}
	/**
	 * @return the datasetAsCharMatrix
	 */
	public char[][] getDatasetAsCharMatrix() {
		return datasetAsCharMatrix;
	}
	/**
	 * @return the dataset
	 */
	public AlignedSequenceRepresentation getDataset() {
		return dataset;
	}
	/**
	 * @return the parameters
	 */
	public TreeMap<AamlParameters, String> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(TreeMap<AamlParameters, String> parameters) {
		this.parameters = parameters;
	}
	/**
	 * @param inputFileName the inputFileName to set
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	/**
	 * @param numberOfTaxa the numberOfTaxa to set
	 */
	public void setNumberOfTaxa(int numberOfTaxa) {
		this.numberOfTaxa = numberOfTaxa;
	}
	/**
	 * @param numberOfSites the numberOfSites to set
	 */
	public void setNumberOfSites(int numberOfSites) {
		this.numberOfSites = numberOfSites;
	}
	/**
	 * @param numberOfSitePatterns the numberOfSitePatterns to set
	 */
	public void setNumberOfSitePatterns(int numberOfSitePatterns) {
		this.numberOfSitePatterns = numberOfSitePatterns;
	}
	/**
	 * @param numberOfTopologies the numberOfTopologies to set
	 */
	public void setNumberOfTopologies(int numberOfTopologies) {
		this.numberOfTopologies = numberOfTopologies;
	}
	/**
	 * @param numberOfModels the numberOfModels to set
	 */
	public void setNumberOfModels(int numberOfModels) {
		this.numberOfModels = numberOfModels;
	}
	/**
	 * @param numberOfSeries the numberOfSeries to set
	 */
	public void setNumberOfSeries(int numberOfSeries) {
		this.numberOfSeries = numberOfSeries;
	}
	/**
	 * @param started the started to set
	 */
	public void setStarted(Date started) {
		this.started = started;
	}
	/**
	 * @param finished the finished to set
	 */
	public void setFinished(Date finished) {
		this.finished = finished;
	}
	/**
	 * @param patternMapping the patternMapping to set
	 */
	public void setPatternMapping(HashMap<String, Integer> patternMapping) {
		this.patternMapping = patternMapping;
	}
	/**
	 * @param taxaList the taxaList to set
	 */
	public void setTaxaList(TreeSet<String> taxaList) {
		this.taxaList = taxaList;
	}
	/**
	 * @param patternLikelihoods the patternLikelihoods to set
	 */
	public void setPatternLikelihoods(TreeMap<String, Float>[] patternLikelihoods) {
		this.patternLikelihoods = patternLikelihoods;
	}
	/**
	 * @param treeLengths the treeLengths to set
	 */
	public void setTreeLengths(float[] treeLengths) {
		this.treeLengths = treeLengths;
	}
	/**
	 * @param li the li to set
	 */
	public void setLi(float[] li) {
		this.li = li;
	}
	/**
	 * @param pKH the pKH to set
	 */
	public void setpKH(float[] pKH) {
		this.pKH = pKH;
	}
	/**
	 * @param pRELL the pRELL to set
	 */
	public void setpRELL(float[] pRELL) {
		this.pRELL = pRELL;
	}
	/**
	 * @param pSH the pSH to set
	 */
	public void setpSH(float[] pSH) {
		this.pSH = pSH;
	}
	/**
	 * @param preferred the preferred to set
	 */
	public void setPreferred(boolean[] preferred) {
		this.preferred = preferred;
	}
	/**
	 * @param meanlnL the meanlnL to set
	 */
	public void setMeanlnL(float[] meanlnL) {
		this.meanlnL = meanlnL;
	}
	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(float[] alpha) {
		this.alpha = alpha;
	}
	/**
	 * @param sSLSseriesSitewise the sSLSseriesSitewise to set
	 */
	public void setSSLSseriesSitewise(float[][] sSLSseriesSitewise) {
		SSLSseriesSitewise = sSLSseriesSitewise;
	}
	/**
	 * @param sSLSseriesPatternwise the sSLSseriesPatternwise to set
	 */
	public void setSSLSseriesPatternwise(float[][] sSLSseriesPatternwise) {
		SSLSseriesPatternwise = sSLSseriesPatternwise;
	}
	/**
	 * @param dSSLSseriesSitewise the dSSLSseriesSitewise to set
	 */
	public void setdSSLSseriesSitewise(float[][] dSSLSseriesSitewise) {
		this.dSSLSseriesSitewise = dSSLSseriesSitewise;
	}
	/**
	 * @param dSSLSseriesPatternwise the dSSLSseriesPatternwise to set
	 */
	public void setdSSLSseriesPatternwise(float[][] dSSLSseriesPatternwise) {
		this.dSSLSseriesPatternwise = dSSLSseriesPatternwise;
	}
	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}
	/**
	 * @param sites the sites to set
	 */
	public void setSites(String[] sites) {
		this.sites = sites;
	}
	/**
	 * @param datasetAsCharMatrix the datasetAsCharMatrix to set
	 */
	public void setDatasetAsCharMatrix(char[][] datasetAsCharMatrix) {
		this.datasetAsCharMatrix = datasetAsCharMatrix;
	}
	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(AlignedSequenceRepresentation dataset) {
		this.dataset = dataset;
	}
	
	/**
	 * 
	 * @param aaH0AnalysisOutputFile - an Aaml output file
	 * Should parse the Aaml output to get parameters like pSH, alpha, fitted trees etc
	 */
	public void parseAamlOutput(File aamlAnalysisOutputFile) {
		ArrayList<String> aamlData = new CapitalisedFileReader().loadSequences(aamlAnalysisOutputFile, false);
		
		// SSLS object variables we're hoping to parse
		this.alpha 			= new float[this.numberOfTopologies];
		this.treeLengths 	= new float[this.numberOfTopologies];
		this.meanlnL 		= new float[this.numberOfTopologies];
		this.fittedTrees	= new String[this.numberOfTopologies];
		this.li				= new float[this.numberOfTopologies];
		this.pKH			= new float[this.numberOfTopologies];
		this.pRELL			= new float[this.numberOfTopologies];
		this.pSH			= new float[this.numberOfTopologies];
		this.frequenciesMatrix = new float[this.numberOfTaxa][20];
		this.preferred		= new boolean[this.numberOfTopologies];		// this to be calculated at the end

		// Variables for parsing flags etc
		int whichTreeTreeComparison; 	// an int var that describes which topology in the aaml file we're parsing - within the TREE COMPARISON; *not* initialised here
		int whichTree = -1; 			// an int var that describes which topology in the aaml file we're parsing
		int freqMatRow = 0;					// an int var for row of freq. matrix
		boolean inFreqMatrix = false;
		boolean inTreeComparison = false;
		
		for(String line:aamlData){
			if(line != null){
				String[] tokens = line.split(" ");
				String[] nonEmptyTokens0 = this.getSubsetOfEntriesLongerThan(0, tokens);
				String[] nonEmptyTokens1 = this.getSubsetOfEntriesLongerThan(1, tokens);

				// flag for freq mat OFF
				if(line.startsWith("HOMOGENEITY STATISTIC: X2")){
					inFreqMatrix = false;
					try {
						this.homogeneityChiSq = Float.parseFloat(nonEmptyTokens1[3]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						this.homogeneityChiSq = Float.NaN;
						e.printStackTrace();
					}
					freqMatRow = 0;
				}
				
				// check for frequencies matrix (set flag if so etc)
				if(inFreqMatrix){
					// freq mat stuff	
					if(nonEmptyTokens1.length>0){
						for(int i=1;i<nonEmptyTokens0.length;i++){
							try {
								this.frequenciesMatrix[freqMatRow][i-1] = Float.parseFloat(nonEmptyTokens0[i]);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								this.frequenciesMatrix[freqMatRow][i-1] = Float.NaN;
								e.printStackTrace();
							}
						}
						freqMatRow++;
					}else{
						freqMatRow = 0;
					}
				}

				// flag for freq mat ON
				if(line.startsWith("FREQUENCIES..")){
					inFreqMatrix = true;
				}
				
				// check for lnL
				if(line.startsWith("LNL(NTIME:")){
					float lnl;
					try {
						lnl = Float.parseFloat(nonEmptyTokens1[4]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						lnl = Float.NaN;
						e.printStackTrace();
					}
					this.meanlnL[whichTree] = lnl;
				}

				// check for alpha
				if(tokens[0].equals("JOE_PARAM_ALPHA")){
					float alpha;
					try {
						alpha = Float.parseFloat(nonEmptyTokens1[nonEmptyTokens1.length-1]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						alpha = Float.NaN;
						e.printStackTrace();
					}
					this.alpha[whichTree] = alpha;
				}

				// check for tree length
				if(tokens[0].equals("JOE_PARAM_TREELENGTHTREE")){
					float length;
					try {
						length = Float.parseFloat(nonEmptyTokens1[3]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						length = Float.NaN;
						e.printStackTrace();
					}
					this.treeLengths[whichTree] = length;
				}

				// check for fitted tree
				if(tokens[0].startsWith("JOE_PARAM_TREEZXCV")){
					String fittedTree = line.substring(18);
					this.fittedTrees[whichTree] = fittedTree;
				}

				// tree comparison flag OFF
				if(line.startsWith("PKH: P VALUE FOR KH")){
					inTreeComparison = false;
				}

				// check for {li,pKH,pRELL,pSH} etc (tree comparison table; set flag if so etc)
				if(inTreeComparison){
					try{
						String potentialTree = nonEmptyTokens0[0];
						if(potentialTree.endsWith("*")){
							potentialTree = potentialTree.substring(0, potentialTree.length()-1);
							this.preferred[Integer.parseInt(potentialTree) - 1] = true;
						}
						whichTreeTreeComparison = Integer.parseInt(potentialTree) - 1;
						try{
							this.li[whichTreeTreeComparison] = Float.parseFloat(nonEmptyTokens0[1]);
						}catch(Exception ex){
							this.li[whichTreeTreeComparison] = Float.NaN;
							ex.printStackTrace();
						}
						try{
							this.pKH[whichTreeTreeComparison] = Float.parseFloat(nonEmptyTokens0[4]);
						}catch(Exception ex){
							this.pKH[whichTreeTreeComparison] = Float.NaN;
							ex.printStackTrace();
						}
						try{
							this.pRELL[whichTreeTreeComparison] = Float.parseFloat(nonEmptyTokens0[5]);
						}catch(Exception ex){
							this.pRELL[whichTreeTreeComparison] = Float.NaN;
							ex.printStackTrace();
						}
						try{
							this.pSH[whichTreeTreeComparison] = Float.parseFloat(nonEmptyTokens0[6]);
						}catch(Exception ex){
							this.pSH[whichTreeTreeComparison] = Float.NaN;
							ex.printStackTrace();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}


				// tree comparison flag ON
				if(line.startsWith("  TREE           LI       DLI")){
					inTreeComparison = true;
				}

				// check for flag to increment whichTree
				if(tokens[0].equals("TREE") && tokens[1].equals("#")){
					whichTree++;
				}
			}
		}
		
		//TODO calculate preferred 
	}

	public void parseBasemlOutput(File basemlAnalysisOutputFile) {}
	
	public void parseCodemlOutput(File codemlAnalysisOutputFile) {}
	
	public void parsePamlOutput(File pamlAnalysisOutputFile) {}
	
	/**
	 * Method to expand out site patterns, transposed sites into correct arrays.
	 * 
	 * patternLikelihoods --> 
	 * 		float[#patterns][#trees]  SSLSseriesPatternwise <b>and</b> String[] patterns
	 * 
	 * sites --> 
	 * 		char [#taxa][#sites] dataSetAsCharMatrix
	 * 
	 * sitesMatrix --> 
	 * 		char[][] transposedDatasetAsCharMatrix <b>and</b> sitePatterns
	 * 
	 * transposedSites <b>and</b> patternLikelihoods --> 
	 * 		float[#sites][#trees] SSLS seriesSitewise
	 *
	 */
	public void fillOutAndVerify() {
		// try and do some rudimentary assertions
		assert(this.patternLikelihoods != null);
		assert(this.dataset != null);
		
		// initialise 
		this.SSLSseriesPatternwise = new float[this.numberOfSitePatterns][this.numberOfTopologies];
		this.patterns = new String[this.numberOfSitePatterns];
		this.datasetAsCharMatrix = new char[this.numberOfTaxa][this.numberOfSites];
		this.transposedDatasetAsCharMatrix = new char[this.numberOfSites][this.numberOfTaxa];
		this.sites = new String[this.numberOfSites];
		this.SSLSseriesSitewise = new float[this.numberOfSites][this.numberOfTopologies];
		
		/*
		 * patternLikelihoods --> 
		 * 		float[#patterns][#trees]  SSLSseriesPatternwise <b>and</b> String[] patterns
		 */
		patterns = (String[]) this.patternLikelihoods[0].keySet().toArray(new String[0]);
		for(int whichTree = 0;whichTree<this.numberOfTopologies;whichTree++){
			for(int i=0;i<this.numberOfSitePatterns;i++){
				try {
					this.SSLSseriesPatternwise[i][whichTree] = this.patternLikelihoods[whichTree].get(patterns[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Failed to transpose patterns matrix on pattern ["+i+"] and tree ["+whichTree+"]");
					e.printStackTrace();
				}
			}
		}

		/*
		 * sitesMatrix --> 
		 * 		char[][] transposedDatasetAsCharMatrix <b>and</b> sitePatterns
		 */
		this.sites = this.dataset.getTransposedSites();
		for(int i=0; i<this.numberOfSites;i++){
			this.transposedDatasetAsCharMatrix[i] = this.sites[i].toCharArray();
		}		
		
		/*
		 * sites --> 
		 * 		char [#taxa][#sites] dataSetAsCharMatrix
		 */
		String[] taxaListArray = (String[]) this.dataset.getTaxaList().toArray(new String[0]);
		for(int i=0;i<taxaListArray.length;i++){
			for(int j=0;j<this.numberOfSites;j++){
				this.datasetAsCharMatrix[i][j] = this.transposedDatasetAsCharMatrix[j][i];
			}
		}
		

		/*
		 * transposedSites <b>and</b> patternLikelihoods --> 
		 * 		float[#sites][#trees] SSLS seriesSitewise
		 */
		for(int whichTree = 0;whichTree<this.numberOfTopologies;whichTree++){
			for(int i=0; i<this.numberOfSites;i++){
				String thisObservedPattern = this.sites[i];
				this.SSLSseriesSitewise[i][whichTree] = this.patternLikelihoods[whichTree].get(thisObservedPattern);
			}		
		}
		
		this.finished = new Date(System.currentTimeMillis());
	}
	
	public long elapsed(){
		return finished.compareTo(started);
	}
	
	private String[] getSubsetOfEntriesLongerThan(int limit,String[] someParsedStringArray){
		int matchingEntries = 0;
		for(String entry:someParsedStringArray){
			if(entry.length()>limit){
				matchingEntries++;
			}
		}
		if(matchingEntries>0){
			String[] nonEmptyRetArray = new String[matchingEntries];
			int entriesAdded = 0;
			for(String entry:someParsedStringArray){
				if(entry.length()>limit){
					nonEmptyRetArray[entriesAdded] = entry;
					entriesAdded++;
				}
			}
			
			return nonEmptyRetArray;
		}else{
			return new String[0];
		}
	}
}
