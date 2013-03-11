package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;

public class SitewiseSpecificLikelihoodSupport implements Serializable{
	public String inputFileName;
	public int numberOfTaxa;
	public int numberOfSites;
	public int numberOfSitePatterns;
	public int numberOfTopologies;
	public int numberOfModels;
	public int numberOfSeries;
	public Date started;
	public Date finished;
	public HashMap<String,Integer> patternMapping;
	public TreeSet<String> taxaList;
	public float[] treeLengths;
	public float[] li;
	public float[] pKH;
	public float[] pRELL;
	public float[] pSH;
	public boolean[] preferred;
	public float[] meanlnL;
	public float[] alpha;
	public float[][] SSLSseriesSitewise;
	public float[][] SSLSseriesPatternwise;
	public String[] patterns;
	public String[] sites;
	public char[][] datasetAsCharMatrix; 
	public AlignedSequenceRepresentation dataset;
	
	/**
	 * Class to contain basically all the SSLS features required.
	 * 
	 * Designed to be initialised with ASR; 
	 * SSLS patterns loaded; 
	 * other aaml run parameters (model; overall lnL, alpha, fitted tree, li etc) loaded from parsed aaml.out file;
	 * ÆSSLS calculated from patterns; 
	 * serialised and reinflated as object afterwards (UNTESTED) (e.g. downloaded...)
	 * 
	 */
}
