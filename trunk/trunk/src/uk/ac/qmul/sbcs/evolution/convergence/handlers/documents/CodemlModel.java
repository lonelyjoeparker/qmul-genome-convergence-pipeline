package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.util.ArrayList;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlModelNSsitesTypes;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlModelType;

public class CodemlModel {
	private String modelString;
	private int numberOfRates;
	private float lnL;
	private float [] globalOmegaRates;
	private float [] globalProportions;
	private float [] estimatedOmegas;
	private String[] rawData;
	private CodemlModelType modelType;
	private CodemlModelNSsitesTypes NSsitesType;
	
	/**
	 * no-arg constructor
	 */
	public CodemlModel(){}
	
	/**
	 * 
	 * @param data - String representing a line from a data file (rst)
	 * @return true if data added successfully
	 */
	public boolean addData(String data){
		return false;
	}
	
	/**
	 * 
	 * @param data - ArrayList<String> representing a line from a data file (rst)
	 * @return true if data added successfully
	 */
	public boolean addData(ArrayList<String> data){
		return false;
	}

	/**
	 * Do a regression of intervals (log-transformed)
	 */
	public void doIntervalRegression(){
		int[] intervals = this.calculateSelectionIntervals();
		// use uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression
	}
	
	/**
	 * Calculates the intervals between selected sites
	 * @return
	 */
	private int[] calculateSelectionIntervals() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getModelString() {
		return modelString;
	}
	public int getNumberOfRates() {
		return numberOfRates;
	}
	public float getLnL() {
		return lnL;
	}
	public float[] getGlobalOmegaRates() {
		return globalOmegaRates;
	}
	public float[] getGlobalProportions() {
		return globalProportions;
	}
	public float[] getEstimatedOmegas() {
		return estimatedOmegas;
	}
	public String[] getRawData() {
		return rawData;
	}
	public void setModelString(String modelString) {
		this.modelString = modelString;
	}
	public void setNumberOfRates(int numberOfRates) {
		this.numberOfRates = numberOfRates;
	}
	public void setLnL(float lnL) {
		this.lnL = lnL;
	}
	public void setGlobalOmegaRates(float[] globalOmegaRates) {
		this.globalOmegaRates = globalOmegaRates;
	}
	public void setGlobalProportions(float[] globalProportions) {
		this.globalProportions = globalProportions;
	}
	public void setEstimatedOmegas(float[] floats) {
		this.estimatedOmegas = floats;
	}
	public void setRawData(String[] rawData) {
		this.rawData = rawData;
	}

	public void setCodemlModelType(CodemlModelType modelType) {
		this.modelType = modelType;
	}

	public void setCodemlModelNSsitesType(CodemlModelNSsitesTypes nSsitesType) {
		this.NSsitesType = NSsitesType;
	}
	
	
	
}
