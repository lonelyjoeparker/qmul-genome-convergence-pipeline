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

	@Override
	public double cdf(double arg0) {
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
