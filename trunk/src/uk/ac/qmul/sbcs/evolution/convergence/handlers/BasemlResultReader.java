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
 * @since 01/11/2011
 *
 *JOE_PARAM_EQRB_begin
JOE_PARAM_RATES Rate parameters:    0.92586  0.05582  0.00001  0.07883  0.01573
JOE_PARAM_BFREQS Base frequencies:   0.27106  0.32622  0.28411  0.11861
JOE_PARAM_RMAT Rate matrix Q, Average Ts/Tv =   9.4618
JOE_MAT    -1.243912    1.181848    0.062060    0.000005
JOE_MAT     0.981989   -1.076924    0.087636    0.007299
JOE_MAT     0.059208    0.100625   -0.623950    0.464117
JOE_MAT     0.000011    0.020076    1.111706   -1.131793
JOE_MAT 
JOE_PARAM_EQRB_end
JOE_PARAM_ALPHA alpha (gamma, K=5) =  0.25466
JOE_PARAM_rKAPPA rate:   0.00097  0.02882  0.19742  0.81376  3.95903
JOE_PARAM_fKAPPA f
 */
public class BasemlResultReader {
	private final File file;
	private BasicFileReader reader;
	private ArrayList<String> rawData;
	private String optimisedTree;
	private String rates;
	private String baseFreqs;
	private String rateMatrix;
	private String alpha;
	private String kappaRates;
	private String kappaFreqs;
	
	@Deprecated
	public BasemlResultReader(){
		this.file = null;
		this.reader = null;
		this.rawData = null;
	}
	
	/**
	 * 
	 * @param afile - 	the baseml output file (named 'mlb' by default).
	 * 					NB this class assumes that my custom version of Paml4.4 (with additonal output writing) was used.
	 */
	public BasemlResultReader(File afile){
		this.file = afile;
		this.reader = new BasicFileReader();
		this.rawData = reader.loadSequences(file, true);
		Pattern ratesK = Pattern.compile("JOE_PARAM_rKAPPA");
		Pattern freqsK = Pattern.compile("JOE_PARAM_fKAPPA");
		for(String someline:rawData){
			Matcher isKRates = ratesK.matcher(someline);
			Matcher isKFreqs = freqsK.matcher(someline);
			if(isKRates.find()){
				System.out.println(someline);
				String[] data = someline.split("zxcv");
				System.out.println("["+data[1]+"]");
				this.kappaRates = data[1];
			}
			if(isKFreqs.find()){
				String[] data = someline.split("zxcv");
				this.kappaFreqs = data[1];
			}
		}
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
		return rates;
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
}
