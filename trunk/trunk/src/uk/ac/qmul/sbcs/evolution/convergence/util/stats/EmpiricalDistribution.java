package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import java.math.BigDecimal;

import jsc.distributions.Distribution;
import cern.jet.random.Empirical;
import cern.jet.random.engine.RandomEngine;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @version 0.1
 * @since r145 2013/04/07
 * 
 * A class extending cern.jet.random.Emprirical and implementing {@link jsc.distributions.Distribution}; to allow for a {@link jsc.goodnessfit.KolmogorovTest}
 * This still requires input data to be represented as a probability density function; use {@link ProbabilityDensityFunction} for this.
 * 
 * @see ProbabilityDensityFunction
 * @see jsc.distributions.Distribution
 * @see jsc.goodnessfit.KolmogorovTest
 */
public class EmpiricalDistribution extends Empirical implements Distribution {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4527249891376212514L;
	private ProbabilityDensityFunction untransformedData;
	private double[] untransformedCDF;
	
	/**
	 * Overloaded constructor, <i>specifically</i> intended to allow input probability density functions that <b>do not</b> satisfy 0 ² F(x) ² 1 to be transformed to 0 ² F(x) ² 1, <i>so that</i> they can give meaningful K-S test D^ values when used with jsc.goodnessfit.KolmolgorovTest.
	 * <p>The inherited constructor uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution.EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator) should be used <b>only</b> where it is known in advance that the input pdf comes from a unit distribution.
	 * @param interpolationType
	 * @param randomGenerator
	 * @param originalPDF - the input data, untransformed. Should contain a BigDecimal[] binLimits and double[] function (the pdf), among other useful things.
	 * @param transformRange - explicitly sets the range the data should be transformed to.
	 * 
	 * @see cern.jet.random.Empirical
	 * @see jsc.goodnessfit.KolmolgorovTest
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.ProbabilityDensityFunction
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution#EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator)
	 */
	public EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator, ProbabilityDensityFunction originalPDF, double[] transformRange) {
		super(pdf, interpolationType, randomGenerator);
		this.untransformedData = originalPDF;
		// TODO cannot invoke super(), will have to nick the constructor from Empirical. 
		// TODO this is a massive fix, need a call to transformToUniform - not 
		double[] transformedPDF = this.transformToUnitWithRange(untransformedData, transformRange);
		super.setState(transformedPDF, interpolationType);
	}

	/**
	 * This is the inherited constructor from cern.jet.random.Empirical. It <b>will not</b> give meaningful K-S test D^ values (let alone <i>p</i>-values unless the input probability density function data happens to satisfy 0 ² F(x) ² 1.
	 * <p>To use other types of empirical data the overloaded constructor uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution.EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator, ProbabilityDensityFunction originalPDF) should be used.
	 * @param pdf - real-valued probability density function (assumed to be binned uniformly from 0:1)
	 * @param interpolationType
	 * @param randomGenerator
	 * 
	 * @see cern.jet.random.Empirical
	 * @see jsc.goodnessfit.KolmolgorovTest
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.ProbabilityDensityFunction
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution#EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator, ProbabilityDensityFunction originalPDF)
	 */
	public EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator) {
		super(pdf, interpolationType, randomGenerator);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overloaded constructor, <i>specifically</i> intended to allow input probability density functions that <b>do not</b> satisfy 0 ² F(x) ² 1 to be transformed to 0 ² F(x) ² 1, <i>so that</i> they can give meaningful K-S test D^ values when used with jsc.goodnessfitKolmolgorovTest.
	 * <p>The inherited constructor uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution.EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator) should be used <b>only</b> where it is known in advance that the input pdf comes from a unit distribution.
	 * @param interpolationType
	 * @param randomGenerator
	 * @param originalPDF - the input data, untransformed. Should contain a BigDecimal[] binLimits and double[] function (the pdf), among other useful things.
	 * 
	 * @see cern.jet.random.Empirical
	 * @see jsc.goodnessfit.KolmolgorovTest
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.ProbabilityDensityFunction
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution#EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator)
	 */
	public EmpiricalDistribution(double[] pdf, int interpolationType, RandomEngine randomGenerator, ProbabilityDensityFunction originalPDF) {
		super(pdf, interpolationType, randomGenerator);
		this.untransformedData = originalPDF;
		// TODO cannot invoke super(), will have to nick the constructor from Empirical. 
		// TODO this is a massive fix, need a call to transformToUniform - not 
		double[] transformedPDF = this.transformToUnit(untransformedData);
		super.setState(transformedPDF, interpolationType);
	}

	/**
	 * 
	 * @param pdf - a probability density function
	 * @return a pdf on [0:1]
	 */
	private double[] transformToUnit(ProbabilityDensityFunction untransformed) {
		// TODO Auto-generated method stub
		/*
		 * Approximate method:
		 * 	-find limits
		 *  -establish location and scale
		 *  -subtract location
		 *  -divide by scale
		 *  -need to check what assumptions etc are needed for this....
		 */
		return null;
	}

	/**
	 * 
	 * @param pdf - a probability density function
	 * @param transformRange - a double[2] containing the upper and lower limits of the transformed range.
	 * @return a pdf on [0:1]
	 */
	private double[] transformToUnitWithRange(ProbabilityDensityFunction untransformed, double[] transformRange) {
		// TODO Auto-generated method stub
		/*
		 * Approximate method:
		 * 	-find limits
		 *  -establish location and scale
		 *  -subtract location
		 *  -divide by scale
		 *  -need to check what assumptions etc are needed for this....
		 */
		return null;
	}

	/**
	 * <p>This method implemented according to jsc.distributions.Distribution, although a <i>similar</i> method occurs in cern.jet.random.Empirical
	 * <br>In that case <pre>Empirical.cdf(int i)</pre> simply returns the ith bin from the CDF. 
	 * <p>This is clearly not compatible with jsc.distributions.Distribution.cdf(double val) which would seem to expect a value of the CDF on (0:1] for a given value.
	 * <br>This implies that the PDF may have been similarly transformed to a unit distribution. If so, a similar transformation should apply here, and the call to cdf(double val) would need to be mapped to the correct CDF bin.
	 * <br>On the other hand, if the untransformed distribution is used then some way of retained the bins is needed. 
	 * <p>It might be worth overloading the constructor to accept a ProbabilityDensityFunction so that the ProbabilityDensityFunction.binIntervals can be retained.
	 * 
	 * @see jsc.distributions.Distribution#cdf(double)
	 * @param val - The proportion of observations on [0:1], <i>not</i> a real-valued variate from the observed data.
	 * 
	 */
	@Override
	public double cdf(double val) {
		// TODO Auto-generated method stub
		int binLocation = 0;
		if((val<0)||(val>1)){
			System.err.println("Bin location must be in range [0,1], not "+val);
			throw new IllegalArgumentException();
		}else{
			binLocation = (int)Math.round(val * (double)cdf.length);
		}
		return this.cdf(binLocation);
	}

	/**
	 * Implementation of abstract method inherited from {@link cern.jet.random.Empirical}.
	 * 
	 * <p>Finds the bin position where f(x) nearest percentile
	 * 
	 */
	@Override
	public double inverseCdf(double percentile) {
		double inverse = Double.NaN;
		double difference = 1;
		int bin = 0;
		/*
		 * Iterate through the bins
		 * 	comparing f(x) to percentile
		 *  if difference this[i] < difference
		 *  	bin=i
		 *  	difference = this[i]
		 *  return i/nBins
		 */
		for(int i=0;i<this.cdf.length;i++){
			double functionDiff = Math.abs(this.cdf[i]-percentile);
			if(functionDiff < difference){
				bin = i;
				difference = functionDiff;
			}
		}
		inverse = ((double) bin) / ((double) this.cdf.length);
		return inverse;
	}

	@Override
	public boolean isDiscrete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double mean() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double random() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSeed(long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public double variance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double[] getWholeCDF() {
		// TODO Auto-generated method stub
		return this.cdf;
	}
	
	public double estimateD(EmpiricalDistribution compared){
		double estimatedD = 0.0;
		for(int i=0;i<100;i++){
			double val = ((double)i / 100.0);
			double someD = Math.abs(this.cdf(val) - compared.cdf(val));
			if(someD > estimatedD){
				estimatedD = someD;
			}
		}
		return estimatedD;
	}

}
