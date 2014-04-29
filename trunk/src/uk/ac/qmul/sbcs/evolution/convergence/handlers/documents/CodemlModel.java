package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlModelNSsitesTypes;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlModelType;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression;

/**
 * Representation of sitewise selection (dN/dS rates ratio) for codeml M0, M1a, M2a, M3, M4, M5, M6, M7, M8, branch-site and Clade models C & D.
 * <br/>Includes methods to extract selected sites and do regressions on their indices to determine aggregation of selected sites for qulity-control analyses.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParser
 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1
 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM2
 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression
 */
public class CodemlModel {
	private String modelString;
	private int numberOfRates;
	private float lnL;
	private float [] globalOmegaRates;
	private float [] globalProportions;
	private float [] estimatedOmegas;
	private float [][] estimatedProportions;
	private String[] rawData;
	private int[] selectionIntervals = null;
	private CodemlModelType modelType;
	private CodemlModelNSsitesTypes NSsitesType;
	private LinearRegression intervalsRegression = null;
	
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
	 * Iterates through the sites in the alignment to work out which may be under positive selection.
	 * <br/>Returns a list of indices of sites (indexed to 1, <b>not</b> 0!) which have greatest BEB support for last site class. 
	 * <p><b>Note</b> that:
	 * <ul>
	 * <li>Sites with 0.333 &lt; Pr(BEB(last cat)) &lt; 0.5 will be counted, but may not have BEB <i>products</i> &gt; 1;
	 * <li>The last site class is treated as the informative one, but some models (e.g. M1) this may not actually be under selection at all. It does not make sense calling this method on those models...</li>
	 * <li>The subsets of sites picked up as 'selected' by this method may (for these reasons) <b>differ substantially</b> from those picked up by summing the (BEB * omega) products, as in {@link CodemlModel#getSelectedSitesByBEBProbabilityProducts()}.</li>
	 * <li>It therefore makes sense to call one or the other depending on what you intend to do with the data (and also verify the global omegas are sensible).</li>
	 * </p>
	 * @return selectedSiteIndices - int[] of site indices (indexed to 1, <b>not</b> 0!) for sites with highest BEB posterior for last site class)
	 * @see CodemlModel#calculateSelectionIntervalsByBEBProbabilityProducts()
	 * @see CodemlModel#getSelectedSitesByBEBProbabilityProducts()
	 */
	public int[] getSelectedSitesByBEBProbabilities() throws NullPointerException{
		int[] selectedSiteIndices;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int i=0;i<this.estimatedProportions.length;i++){
			// look at each site's proportions in turn
			float maxProportionNotLast = 0.0f; // cumulative proportions of all site classes at this site *apart* from the last (possibly selected) site class
			for(int j = 0; j<(this.estimatedProportions[i].length-1);j++){
				maxProportionNotLast = Math.max(estimatedProportions[i][j],maxProportionNotLast);
			}
			if(maxProportionNotLast < this.estimatedProportions[i][this.estimatedProportions[i].length-1]){
				// the biggest propportion so far isn't bigger than the last site class proportion, so add this site
				indices.add((i+1)); // index to 1 not 0...
			}
		}
		Integer[] selectedSiteIndicesInt = indices.toArray(new Integer[indices.size()]);
		selectedSiteIndices = new int[selectedSiteIndicesInt.length];
		for(int i=0;i<selectedSiteIndices.length;i++){
			selectedSiteIndices[i] = selectedSiteIndicesInt[i];
		}
		if(selectedSiteIndices.length == 0){throw new NullPointerException("Array length == 0; No selected sites present.");}
		return selectedSiteIndices;
	}
	
	/**
	 * Gets the intervals between selected sites - determined here as 'sites with last site class prob as majority.'
	 * <br/>Calls {@link CodemlModel#getSelectedSitesByBEBProbabilities()} to get this info.
	 * @return int[] of intervals (distances in AA/codons between selected sites.
	 * @see CodemlModel#getSelectedSitesByBEBProbabilities()
	 */
	public int[] getSelectionIntervalsByBEBProbabilities() {
		int[] selectedSitesIndices = this.getSelectedSitesByBEBProbabilities();
		int[] selectionSitesIntervals = new int[selectedSitesIndices.length-1];
		for(int i=0; i<selectionSitesIntervals.length;i++){
			selectionSitesIntervals[i] = selectedSitesIndices[i+1] - selectedSitesIndices[i];
		}
		Arrays.sort(selectionSitesIntervals);
		return selectionSitesIntervals;
	}
	
	/**
	 * Getter method for intervals of selected sites.
	 * @return - int[] of intervals of selected sites.
	 * @see CodemlModel#calculateSelectionIntervalsByBEBProbabilityProducts()
	 * @see CodemlModel#doIntervalRegression()
	 */
	public int[] getSelectionIntervalsByBEBProbabilityProducts(){
		if(this.selectionIntervals == null){
			this.calculateSelectionIntervalsByBEBProbabilityProducts();
		}
		return this.selectionIntervals;
	}
	
	/**
	 * Do a regression of intervals (log-transformed, base e)
	 * Intervals are calculated between each codon with estimated w > 1
	 * DOES NOT include first or last interval (from 5' to first w>1, or from last w>1 to 3')
	 * 
	 * e.g. if omegas are (sites 1:4, 7:9, 11:19 and 21:24 omitted):
	 * <pre>
	 * i	w
	 * 5	1.5
	 * 6	1.0
	 * 7	1.1
	 * 10	1.01
	 * 20	1.2
	 * 25	1.0
	 * </pre>
	 * Then the sites passing BEB would be:
	 * <pre>
	 * i	w
	 * 5	1.5
	 * 7	1.1
	 * 10	1.01
	 * 20	1.2
	 * </pre>
	 * And the corresponding intervals assuming start/5' at 1 and end/3' at 25:
	 * <pre>
	 * i	w	interval
	 * 1	(5')	n/a
	 * 5	1.5	n/a
	 * 7	1.1	2
	 * 10	1.01	3
	 * 20	1.2	10
	 * 25	(3')	n/a
	 * </pre>
	 * These are log-transformed (base e), sorted and regressed:
	 * <pre>
	 * i	w	intrvl	log(i)	index
	 * 1	(5')	n/a	n/a	n/a
	 * 25	(3')	n/a	n/a	n/a
	 * 5	1.5	n/a	n/a	n/a
	 * 20	1.2	10	2.30	1
	 * 10	1.01	3	1.10	2
	 * 7	1.1	2	0.693	3
	 * 
	 * > summary(lm_r)
	 * 
	 * Call:
	 * lm(formula = y_vals ~ x_vals)
	 * 
	 * Residuals:
	 *       1       2       3 
	 *  0.1322 -0.2643  0.1322 
	 * 
	 * Coefficients:
	 *             Estimate Std. Error t value Pr(>|t|)
	 * (Intercept)  -0.2427     0.4945  -0.491    0.710
	 * x_vals        0.8035     0.2289   3.510    0.177
	 * 
	 * Residual standard error: 0.3237 on 1 degrees of freedom
	 * Multiple R-squared: 0.9249,	Adjusted R-squared: 0.8498 
	 * F-statistic: 12.32 on 1 and 1 DF,  p-value: 0.1767  
	 * </pre>
	 * In this implementation this is done using {@link uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression}, adapted from  class of the same name, Copyright © 2000Ð2011, Robert Sedgewick and Kevin Wayne, Last updated: Wed Feb 9 09:20:16 EST 2011.</p>
	 * @throws Exception - if there aren't enough intervals (<3) for a regression it won't do one...
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression
	 */
	public void doIntervalRegression() throws Exception{
		// check the intervals have been calculated
		if(this.selectionIntervals == null){
			this.calculateSelectionIntervalsByBEBProbabilityProducts();
		}
		// the intervals should be here now, but are there enough for a regression?
		if(selectionIntervals.length<3){
			throw new Exception("Two or fewer intervals present - not enough for a regression!");
		}
		// use uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression

		/*
		 * Doing the intervals & regression - from ~/Documents/all_work/QMUL/FSD/results_final_nov2012/complete_summary_includingLRT.pl:1564
		 * 
		 * IMPORTANT! don't forget to sort the interval values prior to regression...
		 * 
		 * 	$last = 0;
			my @BEB_intervals;
			my $numBEB=scalar(@data_BEB); // this is just an array of sites with BEB > 0.5 grabbed from the codeml.out file (e.g. ~/Documents/all_work/QMUL/FSD/results_final_nov2012/M8_outfiles/f_100_ENSG00000Otofaa_ng.fas_codemlM8.out:6594)
			// nb the @data_BEB sites are all ³0.5 BEB though (since they're from outfile not rst file) - here we'll have to filter.
			my $numBEBintervals=scalar(@data_BEB)+1;
			foreach $data(sort(@data_BEB)){
				@fields = split(/\ {1,}/,$data);
				my $site = $fields[1];
				my $distance = $site-$last;
				push(@BEB_intervals,$distance);
				$last = $site;
			}
			if(($nSitesAA-$last)>0){push(@BEB_intervals,($nSitesAA-$last))};		#adding the last distance in
			my $numBEBintervals=scalar(@BEB_intervals);

			if($numBEBintervals>1){
				print R "vals=c(".join(',',@BEB_intervals).")\n";
				print R "sortlogvals=sort(log(vals))\n";
				print R "indices=(1:length(sortlogvals))\n";
				print R "lm1=lm(sortlogvals~indices)\n";
		 */
		double[] indices 		= new double[this.selectionIntervals.length];
		double[] log_intervals 	= new double[this.selectionIntervals.length];
		for(int i=0;i<indices.length;i++){
			indices[i]	= i;
			log_intervals[i] = Math.log(this.selectionIntervals[i]);
		}
		intervalsRegression = new LinearRegression(indices,log_intervals);
	}
	
	/**
	 * Calculates the intervals between selected sites
	 * <br/>Uses the PRODUCT of Pr(BEB(i))*(dN/dS(i)) for each i in available site categories
	 * for details of intervals calculation see above ({@link CodemlModel#doIntervalRegression()}).
	 * 
	 * IMPORTANT! don't forget to sort the values prior to regression...
	 * @see CodemlModel#doIntervalRegression()
	 * @see CodemlModel#getSelectedSitesByBEBProbabilities()
	 */
	private void calculateSelectionIntervalsByBEBProbabilityProducts() {
		int siteIndex = 1;
		int last = -1;
		ArrayList<Integer> intervalsList = new ArrayList<Integer>();
		// loop through omegas calculating intervals. DON'T include first or last interval (from 5' to first w>1, or from last w>1 to 3')
		for(float someOmega:this.estimatedOmegas){
			if(someOmega > 1){
				if(last>-1){	// don't include the interval from 5' to first dNdS>1 site
					int distance = siteIndex - last;
					// add to array
					intervalsList.add(distance);
				}
				last = siteIndex;
			}
			siteIndex++;
		}
		Collections.sort(intervalsList); // sort the intervals
		this.selectionIntervals = new int[intervalsList.size()];
		for(int i=0;i<intervalsList.size();i++){
			selectionIntervals[i] = intervalsList.get(i);
		}
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
	public void setEstimatedOmegas(float[] estimatedOmegas) {
		this.estimatedOmegas = estimatedOmegas;
	}
	public void setEstimatedProportions(float[][] estimatedSitewiseProportions){
		this.estimatedProportions = estimatedSitewiseProportions;
	}
	public void setRawData(String[] rawData) {
		this.rawData = rawData;
	}

	public void setCodemlModelType(CodemlModelType modelType) {
		this.modelType = modelType;
	}

	public void setCodemlModelNSsitesType(CodemlModelNSsitesTypes nSsitesType) {
		this.NSsitesType = nSsitesType;
	}
	
	/**
	 * @return the modelType
	 */
	public CodemlModelType getModelType() {
		return modelType;
	}

	/**
	 * @return the nSsitesType
	 */
	public CodemlModelNSsitesTypes getNSsitesType() {
		return NSsitesType;
	}

	/**
	 * Getter method for the regression of log-transformed selected sites intervals
	 * @return LinearRegression of indices against log-transformed selection intervals
	 * @throws Exception - if there are too few intervals for a regression it won't do one
	 */
	public LinearRegression getIntervalsRegression() throws Exception{
		if(this.intervalsRegression == null){
			this.doIntervalRegression();
		}
		return this.intervalsRegression;
	}
	
	/**
	 * Utility method to check whether this model contains valid data.
	 * @return boolean isValid - TRUE if data is all valid (not-null); makes no guarantee results are meaningful
	 */
	public boolean selfValidate(){
		boolean isValid = true;

		// check estimated omegas
		for(float omega:this.estimatedOmegas){
			if(!(omega >= 0.0f)){isValid=false;}
		}

		// check global proportions
		for(float prop:this.globalProportions){
			if(!(prop >= 0.0f)){isValid=false;}
		}

		// check global omegas
		for(float omega:this.globalOmegaRates){
			if(!(omega >= 0.0f)){isValid=false;}
		}

		// check lnL
		if(Float.isNaN(lnL)){isValid=false;}
		
		// check number of rates and omegas list match size
		if(this.numberOfRates != this.globalOmegaRates.length ){isValid=false;}
		if(this.numberOfRates != this.globalProportions.length){isValid=false;}

		// results
		return isValid;
	}

}
