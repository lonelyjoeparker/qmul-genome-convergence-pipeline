package uk.ac.qmul.sbcs.evolution.sandbox;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import cern.jet.random.engine.RandomEngine;

import jsc.goodnessfit.KolmogorovTest;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.ProbabilityDensityFunction;

public class EmpiricalDistributionsSandbox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		double[] somePDF = {0.0d,0.1d,0.2d,0.3d,0.4d,0.5d,0.6d,0.7d,0.8d,0.9d,1.0d};
	//	for(int i=0;i<1000;i++){
			double[] someData = new double[5];
			double[] someData2 = new double[5];
			for(int j=0;j<5;j++){
				someData[j] = Math.random();
				someData2[j] = j/5.0f;
			}
			double[] functionRange = {0.0d,1.0d};
			ProbabilityDensityFunction pdf = new ProbabilityDensityFunction(someData,functionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
			ProbabilityDensityFunction pdf2 = new ProbabilityDensityFunction(someData2,functionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
			EmpiricalDistribution ed = new EmpiricalDistribution(pdf.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
			EmpiricalDistribution ed2 = new EmpiricalDistribution(pdf2.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
			KolmogorovTest ks = new KolmogorovTest(ed2.getWholeCDF(), ed);
			double kD = ks.getTestStatistic();	
			double pD = ks.getSP();
			System.out.println(kD + "\t" + pD);
	//	}
	}

}
