package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.util.*;

/**
 * 
 * @author <mailto:joe@kitson-consulting.co.uk>Joe Parker</a>
 * @version 0.0.1
 * @since 03/11/2011
 *
 *<H1>04/11/2011 - This class now tested in a limited way (see below)</H1>
 *
 *<H2>03/11/201 - Important note - Deprecated class</H2>
 *CodemlResultReader is marked as deprecated at the moment until I can figure out a way to read omega and kappa parameters reliably from Codeml.
 *Use an AA (aaml) or NT (baseml) analysis, and the relevant reader, instead.
 *
 *<p>Also note that this class is just a copy-and-paste from BasemlResultReader, has not been codeml-ized or tested.
 */


public class CodemlResultReader {
	private final File file;
	private BasicFileReader reader;
	private ArrayList<String> rawData;
	private String optimisedTree;
	private String obsAvgFreqs;
	private String baseRates;
	private String baseFreqs;
	private String TiTv;
	private String rateMatrix;
	private String alpha;
	private String kappaRates;
	private String kappaFreqs;
	private String treeLength;
	private String omegaVal;
	private String kappaVal;
	private boolean initialised;
	
	@Deprecated
	public CodemlResultReader(){
		this.file = null;
		this.reader = null;
		this.rawData = null;
	}
	
	/**
	 * 
	 * @param afile - 	the baseml output file (named 'mlb' by default).
	 * 					NB this class assumes that my custom version of Paml4.4 (with additonal output writing) was used.
	 */
	public CodemlResultReader(File afile){
		this.file = afile;
		this.reader = new BasicFileReader();
		this.rawData = reader.loadSequences(file, false, false);
		Pattern ratesK = Pattern.compile("JOE_PARAM_rKAPPA");
		Pattern freqsK = Pattern.compile("JOE_PARAM_fKAPPA");
		Pattern tree = Pattern.compile("JOE_PARAM_TREE");
		Pattern rates = Pattern.compile("JOE_PARAM_RATES");
		Pattern freqs = Pattern.compile("JOE_PARAM_BFREQS");
		Pattern TiTv = Pattern.compile("JOE_PARAM_TITV");
		Pattern alpha = Pattern.compile("JOE_PARAM_ALPHA");
		Pattern inQmat= Pattern.compile("JOE_PARAM_RMAT");
		Pattern matrixLine = Pattern.compile("JOE_MAT");
		Pattern endQmat = Pattern.compile("JOE_PARAM_EQRB_end");
		Pattern treeLen = Pattern.compile("JOE_PARAM_TREELENGTH");
		Pattern kappa = Pattern.compile("JOE_PARAM_KAPPA");
		Pattern omega = Pattern.compile("JOE_PARAM_OMEGA");
		Pattern obsAvg = Pattern.compile("JOE_PARAM_AVG");	// FIXME currently no 'zxcv' token on this line
		boolean inQmatrix = false;
		String Qmatrix = "";
		for(String someline:rawData){
			Matcher isKRates = ratesK.matcher(someline);
			Matcher isKFreqs = freqsK.matcher(someline);
			Matcher isTree = tree.matcher(someline);
			Matcher isRates = rates.matcher(someline);
			Matcher isFreqs = freqs.matcher(someline);
			Matcher isTiTv = TiTv.matcher(someline);
			Matcher isAlpha = alpha.matcher(someline);
			Matcher isInQmat = inQmat.matcher(someline);
			Matcher isMatrixLine = matrixLine.matcher(someline);
			Matcher isOutQmat = endQmat.matcher(someline);
			Matcher isObsAvg = obsAvg.matcher(someline);
			Matcher isTreeLength = treeLen.matcher(someline);
			Matcher isKappa = kappa.matcher(someline);
			Matcher isOmega = omega.matcher(someline);
			if(inQmatrix){
				if(isMatrixLine.find()){
					String[] tokens = someline.split("zxcv");
					if(tokens.length>1){
						Qmatrix += tokens[tokens.length-1]+"\n";
					}
				}
				if(isOutQmat.find()){
					inQmatrix = false;
					this.rateMatrix = Qmatrix;
				}
			}
			if(isInQmat.find()){
				inQmatrix = true;
			}
			if(isTree.find()){
				this.optimisedTree = someline.split("zxcv")[1];
			}
			if(isRates.find()){
				this.baseRates = someline.split("zxcv")[1];
			}
			if(isFreqs.find()){
				/**
				 * @since - 02/11/2011
				 * @author - Joe Parker
				 * IMPORTANT
				 * 	Baseml in Paml outputs base freqs that can sometimes sum>1 (due to rounding?)
				 *  
				 */
				this.baseFreqs = someline.split("zxcv")[1];
			}
			if(isTiTv.find()){
				this.TiTv = someline.split("zxcv")[2];
			}
			if(isAlpha.find()){
				this.alpha = someline.split("zxcv")[1];
			}
			if(isObsAvg.find()){
				this.obsAvgFreqs = someline.split("AVG Average")[1];
			}
			if(isKRates.find()){
				System.out.println(someline);
				String[] data = someline.split("zxcv");
				System.out.println("["+data[1]+"]");
				this.kappaRates = data[1];
			}
			if(isKFreqs.find()){
				this.kappaFreqs = someline.split("zxcv")[1];
			}
			if(isKappa.find()){
				this.kappaVal = someline.split("zxcv")[1];
			}
			if(isOmega.find()){
				this.omegaVal = someline.split("zxcv")[1];
			}
			if(isTreeLength.find()){
				this.treeLength = someline.split("zxcv")[1];
			}
		}
		this.initialised = true;
	}

	public void printParams(){
		System.out.println("Alpha\t\t"+this.alpha);
		System.out.println("Base freqs\t\t"+this.baseFreqs);
		System.out.println("Base rates\t\t"+this.baseRates);
		System.out.println("Kappa freqs\t\t"+this.kappaFreqs);
		System.out.println("Kappa rates\t\t"+this.kappaRates);
		System.out.println("Obs freqs\t\t"+this.obsAvgFreqs);
		System.out.println("ML tree\t\t"+this.optimisedTree);
		System.out.println("Ti/Tv\t\t"+this.TiTv);
		System.out.println("Q Rate matrix:\n"+this.rateMatrix);
	}
	
	public String getObsAvgFreqs() {
		return obsAvgFreqs;
	}

	public String getBaseRates() {
		return baseRates;
	}

	public String getTiTv() {
		return TiTv;
	}

	public boolean isInitialised() {
		return initialised;
	}

	public File getFile() {
		return file;
	}

	public BasicFileReader getReader() {
		return reader;
	}

	public ArrayList<String> getRawData() {
		return rawData;
	}

	public String getOptimisedTree() {
		return optimisedTree;
	}

	public String getRates() {
		return baseRates;
	}

	public String getBaseFreqs() {
		return baseFreqs;
	}

	public String getRateMatrix() {
		return rateMatrix;
	}

	public String getAlpha() {
		return alpha;
	}

	public String getKappaRates() {
		return kappaRates;
	}

	public String getKappaFreqs() {
		return kappaFreqs;
	}

	public String getTreeLength() {
		return treeLength;
	}

	public String getOmegaVal() {
		return omegaVal;
	}

	public String getKappaVal() {
		return kappaVal;
	}
}
