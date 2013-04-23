/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import jsc.goodnessfit.KolmogorovTest;
import cern.jet.random.engine.RandomEngine;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class PairedEmpirical {

	int N_A;
	int N_B;
	double[] A;
	double[] B;
	double[] A_unitTransformed;
	double[] B_unitTransformed;
	double[] range 	= {Double.NaN,Double.NaN};
	double[] unitRange = {-0d,1.0d};
	double scale 	= Double.NaN;
	double location = Double.NaN;
	BigDecimal interval;
	BigDecimal[] distributionRange = {new BigDecimal(0),new BigDecimal(1)};
	ProbabilityDensityFunction pdf_A;
	ProbabilityDensityFunction pdf_B;
	EmpiricalDistribution ed_A;
	EmpiricalDistribution ed_B;
	KolmogorovTest ks;
	
	public PairedEmpirical(double[] data_a, double[] data_b){
		Arrays.sort(data_a);
		Arrays.sort(data_b);
		this.N_A = data_a.length;
		this.N_B = data_b.length;
		this.A = data_a.clone();
		this.B = data_b.clone();
		this.range[0] = Math.min(A[0], B[0]);
		this.range[1] = Math.max(A[N_A-1], B[N_B-1]);
		this.scale = range[1] - range[0];
		this.location = range[0] / scale;
		
		// transform the data by (X' = X/scale - location)
		// create interval checking it can terminate etc..
		// create pdfs, EDs
		// create test stat.

		// transform the data by (X' = X/scale - location)
		this.A_unitTransformed = new double [this.N_A];
		this.B_unitTransformed = new double [this.N_B];
		for (int i = 0; i < N_A; i++) {
			this.A_unitTransformed[i] = (this.A[i] / scale) - location;
		}
		for (int i = 0; i < N_B; i++) {
			this.B_unitTransformed[i] = (this.B[i] / scale) - location;
		}
		
		// create interval - not actually testing it will terminate now, as the {@link ProbabilityDensityFunction} constructor should be handling this with explicit MathContext
		this.interval = new BigDecimal(1.0d / (double) (Math.max(N_A, N_B)*2.0d),MathContext.DECIMAL128);
		
		// create pdfs, EDs
		this.pdf_A = new ProbabilityDensityFunction(A_unitTransformed,unitRange,interval);
		this.pdf_B = new ProbabilityDensityFunction(B_unitTransformed,unitRange,interval);
		this.ed_A = new EmpiricalDistribution(pdf_A.getFunction(),EmpiricalDistribution.LINEAR_INTERPOLATION,RandomEngine.makeDefault());
		this.ed_B = new EmpiricalDistribution(pdf_B.getFunction(),EmpiricalDistribution.LINEAR_INTERPOLATION,RandomEngine.makeDefault());

		// create test stat.
		this.ks = new KolmogorovTest(A_unitTransformed,ed_B);
	}
	
	/**
	 * The behaviour this class needs to replicate
	 */
	@Deprecated
	private void prototype(){
		// a bunch of distributions on (-10:10)
		double[] ordered = {-10.0f,-9.0f,-8.0f,-7.0f,-6.0f,-5.0f,-4.0f,-3.0f,-2.0f,-1.0f,0.0f,1.0f,2.0f,3.0f,4.0f,5.0f,6.0f,7.0f,8.0f,9.0f,10f};
		double[] uniff = {-6.35918084997684f,-3.93759851809591f,-2.21174590289593f,-1.24442126601934f, 3.04381834808737f,-8.2444783160463f,-5.95814447849989f,0.0671290513128042f,1.97554216720164f,0.875178603455424f,4.69742903951555f,-6.42660288605839f,9.176234616898f,-9.40432901959866f,-0.732000516727567f,-5.7827518042177f,-4.51565932482481f,6.52691628783941f,7.89572019129992f,1.31803135387599f,-1.22617395594716f};
		double[] unif10 = {1.02738475659862f,1.50811678031459f,3.19657302228734f,4.21244767261669f,8.40045061893761f,5.29490644112229f,5.59071297058836f,2.08167154341936f,9.11403979640454f,9.07950394088402f,1.92111485404894f,1.08860559063032f,2.02587054576725f,8.21685578208417f,7.37943846033886f,6.59235533326864f,4.41377065842971f,4.75730574922636f,5.40445396210998f,3.02306494442746f,4.83131889021024f};
		double[] norm = {0.660572839333479f,0.0797906165244468f,1.63857542586835f,1.09376269979563f,0.176119081865946f,-0.97670653977667f,-0.471337425296921f,0.9635692753208f,0.83533323030851f,-0.287172779927618f,-0.332954605200311f,-0.389648493988506f,0.613728976414984f,1.44623291777936f,-0.0221722440108239f,0.416531670268897f,0.479983397711684f,-0.656481509449174f,-0.540704527888314f,-0.407246525275763f,-0.705449592323635f};
		double[] norm_10 = {5.7633638667616f,6.39802453645112f,4.75428644078549f,5.68351492957594f,6.1875414048615f,5.54739491703022f,5.78275240047244f,4.81502628423033f,4.93636421207665f,3.96353158612114f,5.21219941981893f,5.4310849397964f,2.91225993150518f,4.01424944417459f,4.18085534127271f,5.49409098352079f,4.86387738765962f,4.92171254794222f,6.54323395876331f,6.00583230420153f,4.69602008483443f};
		double[] norm5 = {4.12044236252056f,3.50979062375186f,6.62513059005838f,4.31996468214842f,6.41398749422907f,4.68360388453782f,5.49941207522795f,4.50533450337568f,6.52952016087318f,4.47154240056864f,5.07314166432082f,6.52378491714482f,4.66536650455161f,6.70806582828734f,7.07819172092462f,4.44320164272203f,3.61840779373539f,4.77155875391304f,6.03317841423138f,4.43413813122948f,5.26195237787355f};
		double[] norm_5 = {-3.80748223856822f,-5.75077164896431f,-3.3267984143375f,-4.07695338688592f,-4.92837429453196f,-5.83751845517887f,-4.98852233485545f,-4.78730748167994f,-6.5122338538245f,-4.77525803582946f,-3.85880205303318f,-4.75541150502231f,-4.84580506880202f,-7.32034278508732f,-3.05172271444051f,-5.48393140018985f,-5.05252061174948f,-4.81640785318711f,-4.02223193637001f,-4.12751611259949f,-3.94840819380712};
		// ks stuff for distributions on (0:1]
		double[] order1 = {0,0.0344827586206897,0.0689655172413793,0.103448275862069,0.137931034482759,0.172413793103448,0.206896551724138,0.241379310344828,0.275862068965517,0.310344827586207,0.344827586206897,0.379310344827586,0.413793103448276,0.448275862068966,0.482758620689655,0.517241379310345,0.551724137931034,0.586206896551724,0.620689655172414,0.655172413793103,0.689655172413793,0.724137931034483,0.758620689655172,0.793103448275862,0.827586206896552,0.862068965517241,0.896551724137931,0.931034482758621,0.96551724137931,1};
		double[] unif1 = {0.526772355660796,0.398782416479662,0.901285741012543,0.455820410279557,0.642212885431945,0.861228563589975,0.215375886065885,0.184008366661146,0.778669717721641,0.534172367071733,0.882508781040087,0.356307946611196,0.936016893479973,0.245627876371145,0.538643065374345,0.638791358564049,0.158813018817455,0.155748213874176,0.0135723217390478,0.529211234068498,0.24575675977394,0.971221415093169,0.384230916155502,0.874618443660438,0.549820814514533,0.781037411885336,0.618207290768623,0.699134208029136,0.487841309746727,0.0582582557108253};
		BigDecimal interval = new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(32));
		double[] functionRange = {-20.0d,20.0d};
		double[] functionRangeUnit = {-0d,1.0d};

		// actual execution
		double[] sortedOrdered = (ordered.clone());
		double[] sortedNorm    = (norm.clone());
		Arrays.sort(sortedOrdered);
		Arrays.sort(sortedNorm);
		double theMin = Math.min(sortedOrdered[0], sortedNorm[0]);
		double theMax = Math.max(sortedOrdered[sortedOrdered.length-1], sortedNorm[sortedNorm.length-1]);
		double scale = theMax - theMin;
		double location = theMin;
		// transform
		for(int i=0;i<sortedOrdered.length;i++){
			sortedOrdered[i] = (sortedOrdered[i] / scale) - (location/scale);
		}
		for(int i=0;i<sortedNorm.length;i++){
			sortedNorm[i] = (sortedNorm[i] / scale) - (location/scale);
		}
		// set up distributions
		ProbabilityDensityFunction pdf_ordered = new ProbabilityDensityFunction(sortedOrdered,functionRangeUnit,interval);
		ProbabilityDensityFunction pdf_norm = new ProbabilityDensityFunction(sortedNorm,  functionRangeUnit,interval);
		EmpiricalDistribution ed_ordered = new EmpiricalDistribution(pdf_ordered.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
		EmpiricalDistribution ed_norm = new EmpiricalDistribution(pdf_norm.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
		System.out.println("estimated D^\t" + ed_ordered.estimateD(ed_norm));
		System.out.println("estimated D^\t" + ed_ordered.estimateD(ed_ordered));
		System.out.println("estimated D^\t" + ed_norm.estimateD(ed_ordered));
		System.out.println("estimated D^\t" + ed_norm.estimateD(ed_norm));
		
		KolmogorovTest ks;
		// use CDF of ED (PROBBLY NOT THIS< TEST MORE)
		ks = new KolmogorovTest(ed_norm.getWholeCDF(), ed_ordered);
		System.out.println("unit: using CDF\t"+ks.getD() + "\t" + ks.getSP());

		// use PDF (of PDF class) (PROBBABLY NOT THIS< TEST MORE)
		ks = new KolmogorovTest(pdf_norm.getFunction(), ed_ordered);
		System.out.println("unit: using PDF\t"+ks.getD() + "\t" + ks.getSP());
		
		// use raw vals
		// PROBABLY THIS - - -test more!!
		ks = new KolmogorovTest(sortedNorm, ed_ordered);
		System.out.println("unit: using val\t"+ks.getD() + "\t" + ks.getSP());
		
	}
	

	
	
	/*
	 * THE FOLLOWING METHODS ARE NOT IMPLEMENTED
	 * *MUCH* less tested...
	 */
	
	
	/**
	 * Returns the value of the KS test stat Dmax. 
	 * <br/>DOES NOT check to see if an KS instance actually exists.
	 */
	public double getKS(){
		return this.ks.getD();
	}
	
	/**
	 * Returns the significance of the KS test stat Dmax. 
	 * <br/>DOES NOT check to see if an KS instance actually exists.
	 */
	public double getKS_sp(){
		return this.ks.getSP();
	}
	
	/**
	 * Returns the CDF density of the first variable series.
	 * @param value - untransformed variate
	 * @return - cumulative density.
	 */
	public double getDensity_A(double value){
		// TODO not tested
		double transformedValue = (value/scale) - location;
		return this.ed_A.cdf(transformedValue);
	}

	/**
	 * Returns the CDF density of the second variable series.
	 * @param value - untransformed variate
	 * @return - cumulative density.
	 */
	public double getDensity_B(double value){
		// TODO not tested
		double transformedValue = (value/scale) - location;
		return this.ed_B.cdf(transformedValue);
	}
	
	/**
	 * Returns the (CDF density of the <b>second</b> variable series) - (CDF density of the <b>first</b> variable series)
	 * @param value - untransformed variate
	 * @return overlap of f(B) - f(A) at value
	 */
	public double getDensityOverlapAt(double value){
		double transformedValue = (value/scale) - location;
		return this.ed_B.cdf(transformedValue) - this.ed_A.cdf(transformedValue);
	}
	
	/**
	 * Finds the cumulative density of the first variable series at variate-value corresponding to a given percentile density of the second variable series over their shared range.
	 * <p>First find value in ed_B corresponding to f(B) = percentile
	 * <br/>Return f(A) = value
	 * 
	 * @param percentile - cumulative % of second variate
	 * @return density of first variate
	 */
	public double getDensity_A_at_percentile_B(double percentile){
		/*
		 * First find value in ed_B corresponding to f(B) = percentile
		 * Return f(A) = value
		 */
		double value = this.ed_B.inverseCdf(percentile);
		double a_pc  = this.ed_A.cdf(value);
		return a_pc;
	}
	
	/**
	 * Returns the approximate Dmax, computed directly from the CDFs (not from a {@link jsc.goodnessfit.KolmolgorovTest}, although they <i>should</i> be exactly equal in theory; approximate in practice).
	 * @return  Dmax, the maximum difference between two computed cumulative density functions.
	 */
	public double getApproxD(){
		return this.ed_A.estimateD(ed_B);
	}
	
	/**
	 * Returns the inverse CDF (value) of both distributions at a given density
	 * @param percentile - percentile of 
	 * @return valuesAB - transformed values of distribution at percentile (invCDF)
	 */
	public double[] getValuesAtPercentile(double percentile){
		double value_A = (this.ed_A.inverseCdf(percentile) + location) * scale;
		double value_B = (this.ed_B.inverseCdf(percentile) + location) * scale;
		double[] valuesAB = {value_A,value_B};
		return valuesAB;
	}
}
