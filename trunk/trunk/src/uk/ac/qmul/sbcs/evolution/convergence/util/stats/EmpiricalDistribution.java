package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

import jsc.distributions.Distribution;
import cern.jet.random.Empirical;
import cern.jet.random.engine.RandomEngine;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @version 0.1
 * @since r145 2013/04/07
 * 
 * A class extending cern.jet.random.Emprirical and implementing jsc.distributions.Distribution; to allow for a jsc.goodnessfit.KolmolgorovTest
 * This still requires input data to be represented as a probability density function; use ProbabilityDensityFunction for this.
 * 
 * @see ProbabilityDensityFunction
 */
public class EmpiricalDistribution extends Empirical implements Distribution {


	public EmpiricalDistribution(double[] pdf, int interpolationType,
			RandomEngine randomGenerator) {
		super(pdf, interpolationType, randomGenerator);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see jsc.distributions.Distribution#cdf(double)
	 */
	/**
	 * <p>This method implemented according to jsc.distributions.Distribution, although a <i>similar</i> method occurs in cern.jet.random.Empirical
	 * <br>In that case <pre>Empirical.cdf(int i)</pre> simply returns the ith bin from the CDF. 
	 * <p>This is clearly not compatible with jsc.distributions.Distribution.cdf(double val) which would seem to expect a value of the CDF on (0:1] for a given value.
	 * <br>This implies that the PDF may have been similarly transformed to a unit distribution. If so, a similar transformation should apply here, and the call to cdf(double val) would need to be mapped to the correct CDF bin.
	 * <br>On the other hand, if the untransformed distribution is used then some way of retained the bins is needed. 
	 * <p>It might be worth overloading the constructor to accept a ProbabilityDensityFunction so that the ProbabilityDensityFunction.binIntervals can be retained.
	 * 
	 * @see jsc.distributions.Distribution#cdf(double)
	 * @param val - TODO: Not sure yet. <i>Either</i> the proportion of observations on (0:1], <i>or</i> a real-valued variate from the observed data.
	 * 
	 */
	@Override
	public double cdf(double val) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double inverseCdf(double arg0) {
		// TODO Auto-generated method stub
		return 0;
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

}
