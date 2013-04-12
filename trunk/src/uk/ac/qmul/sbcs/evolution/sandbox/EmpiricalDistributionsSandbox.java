package uk.ac.qmul.sbcs.evolution.sandbox;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import cern.jet.random.Empirical;
import cern.jet.random.Uniform;
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

			/////// specific tests of cdf() method, common to all implementations
			
			// init
			double[] wideFunctionRange = {-10.0d,10.0d};
			double[] uniff = {-6.35918084997684f,-3.93759851809591f,-2.21174590289593f,-1.24442126601934f, 3.04381834808737f,-8.2444783160463f,-5.95814447849989f,0.0671290513128042f,1.97554216720164f,0.875178603455424f,4.69742903951555f,-6.42660288605839f,9.176234616898f,-9.40432901959866f,-0.732000516727567f,-5.7827518042177f,-4.51565932482481f,6.52691628783941f,7.89572019129992f,1.31803135387599f,-1.22617395594716f};
			double[] unif10 = {1.02738475659862f,1.50811678031459f,3.19657302228734f,4.21244767261669f,8.40045061893761f,5.29490644112229f,5.59071297058836f,2.08167154341936f,9.11403979640454f,9.07950394088402f,1.92111485404894f,1.08860559063032f,2.02587054576725f,8.21685578208417f,7.37943846033886f,6.59235533326864f,4.41377065842971f,4.75730574922636f,5.40445396210998f,3.02306494442746f,4.83131889021024f};
			double[] unif1 = {0.526772355660796,0.398782416479662,0.901285741012543,0.455820410279557,0.642212885431945,0.861228563589975,0.215375886065885,0.184008366661146,0.778669717721641,0.534172367071733,0.882508781040087,0.356307946611196,0.936016893479973,0.245627876371145,0.538643065374345,0.638791358564049,0.158813018817455,0.155748213874176,0.0135723217390478,0.529211234068498,0.24575675977394,0.971221415093169,0.384230916155502,0.874618443660438,0.549820814514533,0.781037411885336,0.618207290768623,0.699134208029136,0.487841309746727,0.0582582557108253};
			ProbabilityDensityFunction pdf_uniff = new ProbabilityDensityFunction(uniff,wideFunctionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
			ProbabilityDensityFunction pdf_unif10 = new ProbabilityDensityFunction(unif10,wideFunctionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
			ProbabilityDensityFunction pdf_unif1 = new ProbabilityDensityFunction(unif1,functionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
			EmpiricalDistribution ed_uniff = new EmpiricalDistribution(pdf_uniff.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
			EmpiricalDistribution ed_unif10 = new EmpiricalDistribution(pdf_unif10.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
			EmpiricalDistribution ed_unif1 = new EmpiricalDistribution(pdf_unif1.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());			
			// other implementations
			Uniform uniformCERN = new Uniform(RandomEngine.makeDefault());
			jsc.distributions.Uniform uniformJSC = new jsc.distributions.Uniform();
			Empirical empiricalCERN = new Empirical(pdf_unif1.getFunction(),Empirical.LINEAR_INTERPOLATION,RandomEngine.makeDefault());
			
			// compare
			for(int i=0;i<10;i++){
				double val = i/10.0f;
				System.out.println("val: "+val+"\t"+i+"\t"+uniformCERN.cdf(val)+"\t"+uniformJSC.cdf(val)+"\t"+ed_uniff.cdf(val)+"\t"+ed_unif10.cdf(val)+"\t"+ed_unif1.cdf(val)+"\t"+empiricalCERN.cdf(i));
			}
			
	//	}
	}

}
