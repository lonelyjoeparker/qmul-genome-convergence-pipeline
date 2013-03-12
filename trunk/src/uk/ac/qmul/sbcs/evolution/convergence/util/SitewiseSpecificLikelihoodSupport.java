package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.File;
import java.io.Serializable;
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
 * Designed to be initialised with ASR; 
 * SSLS patterns loaded; 
 * other aaml run parameters (model; overall lnL, alpha, fitted tree, li etc) loaded from parsed aaml.out file;
 * ÆSSLS calculated from patterns; 
 * serialised and reinflated as object afterwards (UNTESTED) (e.g. downloaded...)
 * <b>IMPORTANT</b>: <i>One</i> model (Aaml run) per object.......
 * 
 */

public class SitewiseSpecificLikelihoodSupport implements Serializable{
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
	private float[] treeLengths;
	private float[] li;
	private float[] pKH;
	private float[] pRELL;
	private float[] pSH;
	private boolean[] preferred;
	private float[] meanlnL;
	private float[] alpha;
	private float[][] SSLSseriesSitewise;
	private float[][] SSLSseriesPatternwise;
	private float[][] dSSLSseriesSitewise;
	private float[][] dSSLSseriesPatternwise;
	private String[] patterns;
	private float[] patternEntropies;
	private String[] sites;
	private float[] siteEntropies;
	private char[][] datasetAsCharMatrix; 
	private AlignedSequenceRepresentation dataset;
	
	public SitewiseSpecificLikelihoodSupport(){}
	public SitewiseSpecificLikelihoodSupport(AlignedSequenceRepresentation asr){
		this.dataset = asr;
		this.started = new Date(System.currentTimeMillis());
	}
	public SitewiseSpecificLikelihoodSupport(AlignedSequenceRepresentation asr, int taxa, int trees, int sites, int patterns, TreeMap<String,Float>[] lnLpatterns){}
	public SitewiseSpecificLikelihoodSupport(AlignedSequenceRepresentation asr, int taxa, int trees, int sites, int patterns, TreeMap<String,Float>[] lnLpatterns, String datasetID, String[] models){}

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
	public void parseAamlOutput(File aaH0AnalysisOutputFile) {
		// TODO Auto-generated method stub
		
	}
	public void fillOutAndVerify() {
		// TODO Auto-generated method stub
		this.finished = new Date(System.currentTimeMillis());
	}
	
	public long elapsed(){
		return started.compareTo(finished);
	}
}
