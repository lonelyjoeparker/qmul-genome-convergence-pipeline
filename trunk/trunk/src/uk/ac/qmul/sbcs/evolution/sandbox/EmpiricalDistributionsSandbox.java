package uk.ac.qmul.sbcs.evolution.sandbox;

import java.util.Arrays;

import jsc.goodnessfit.KolmogorovTest;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution;

public class EmpiricalDistributionsSandbox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		double[] somePDF = {0.0d,0.1d,0.2d,0.3d,0.4d,0.5d,0.6d,0.7d,0.8d,0.9d,1.0d};
		double[] somePDF = new double[10];
		for(int i=0;i<10;i++){
			somePDF[i] = 0.1;
		}
		Arrays.sort(somePDF);
		EmpiricalDistribution ed = new EmpiricalDistribution(somePDF, 1, null);
		KolmogorovTest ks = new KolmogorovTest(somePDF, ed);
		double kD = ks.getTestStatistic();	
		double pD = ks.getSP();
		System.out.println();
	}

}
