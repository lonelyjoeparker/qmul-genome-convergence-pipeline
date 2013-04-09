/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.math.BigDecimal;
import java.math.MathContext;

import uk.ac.qmul.sbcs.evolution.convergence.util.stats.EmpiricalDistribution;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.ProbabilityDensityFunction;
import cern.jet.random.engine.RandomEngine;

import jsc.goodnessfit.KolmogorovTest;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class EmpiricalDistributionTest extends TestCase {

	double[] ordered = {-10.0f,-9.0f,-8.0f,-7.0f,-6.0f,-5.0f,-4.0f,-3.0f,-2.0f,-1.0f,0.0f,1.0f,2.0f,3.0f,4.0f,5.0f,6.0f,7.0f,8.0f,9.0f,10f};
	double[] uniff = {-6.35918084997684f,-3.93759851809591f,-2.21174590289593f,-1.24442126601934f, 3.04381834808737f,-8.2444783160463f,-5.95814447849989f,0.0671290513128042f,1.97554216720164f,0.875178603455424f,4.69742903951555f,-6.42660288605839f,9.176234616898f,-9.40432901959866f,-0.732000516727567f,-5.7827518042177f,-4.51565932482481f,6.52691628783941f,7.89572019129992f,1.31803135387599f,-1.22617395594716f};
	double[] unif10 = {1.02738475659862f,1.50811678031459f,3.19657302228734f,4.21244767261669f,8.40045061893761f,5.29490644112229f,5.59071297058836f,2.08167154341936f,9.11403979640454f,9.07950394088402f,1.92111485404894f,1.08860559063032f,2.02587054576725f,8.21685578208417f,7.37943846033886f,6.59235533326864f,4.41377065842971f,4.75730574922636f,5.40445396210998f,3.02306494442746f,4.83131889021024f};
	double[] norm = {0.660572839333479f,0.0797906165244468f,1.63857542586835f,1.09376269979563f,0.176119081865946f,-0.97670653977667f,-0.471337425296921f,0.9635692753208f,0.83533323030851f,-0.287172779927618f,-0.332954605200311f,-0.389648493988506f,0.613728976414984f,1.44623291777936f,-0.0221722440108239f,0.416531670268897f,0.479983397711684f,-0.656481509449174f,-0.540704527888314f,-0.407246525275763f,-0.705449592323635f};
	double[] norm_10 = {5.7633638667616f,6.39802453645112f,4.75428644078549f,5.68351492957594f,6.1875414048615f,5.54739491703022f,5.78275240047244f,4.81502628423033f,4.93636421207665f,3.96353158612114f,5.21219941981893f,5.4310849397964f,2.91225993150518f,4.01424944417459f,4.18085534127271f,5.49409098352079f,4.86387738765962f,4.92171254794222f,6.54323395876331f,6.00583230420153f,4.69602008483443f};
	double[] norm5 = {4.12044236252056f,3.50979062375186f,6.62513059005838f,4.31996468214842f,6.41398749422907f,4.68360388453782f,5.49941207522795f,4.50533450337568f,6.52952016087318f,4.47154240056864f,5.07314166432082f,6.52378491714482f,4.66536650455161f,6.70806582828734f,7.07819172092462f,4.44320164272203f,3.61840779373539f,4.77155875391304f,6.03317841423138f,4.43413813122948f,5.26195237787355f};
	double[] norm_5 = {-3.80748223856822f,-5.75077164896431f,-3.3267984143375f,-4.07695338688592f,-4.92837429453196f,-5.83751845517887f,-4.98852233485545f,-4.78730748167994f,-6.5122338538245f,-4.77525803582946f,-3.85880205303318f,-4.75541150502231f,-4.84580506880202f,-7.32034278508732f,-3.05172271444051f,-5.48393140018985f,-5.05252061174948f,-4.81640785318711f,-4.02223193637001f,-4.12751611259949f,-3.94840819380712};
	BigDecimal interval = new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(100));
	double[] functionRange = {-20.0d,20.0d};
	
	
	/*
	 * ordered and unif 
	 * D = 0.1905, p-value = 0.8531
	 * 
	 * ordered and unif10 
	 * D = 0.5714, p-value = 0.001657
	 * 
	 * ordered and norm 
	 * D = 0.4762, p-value = 0.01591
	 * 
	 * ordered and norm5 
	 * D = 0.6667, p-value = 0.0001002
	 * 
	 * ordered and norm_5 
	 * D = 0.6667, p-value = 0.0001002
	 * 
	 * unif and unif10 
	 * D = 0.6667, p-value = 0.0001002
	 * 
	 * unif and norm 
	 * D = 0.5238, p-value = 0.005467
	 * 
	 * unif and norm5 
	 * D = 0.8095, p-value = 4.159e-07
	 * 
	 * unif and norm_5 
	 * D = 0.619, p-value = 0.0004386
	 * 
	 * norm and norm5 
	 * D = 1, p-value = 3.716e-12
	 * 
	 * norm and norm_5 
	 * D = 1, p-value = 3.716e-12
	 * 
	 * */
	
	
	/**
	 * @param name
	 */
	public EmpiricalDistributionTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testKSBasic(){
		ProbabilityDensityFunction pdf = new ProbabilityDensityFunction(ordered,functionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
		ProbabilityDensityFunction pdf2 = new ProbabilityDensityFunction(uniff,functionRange,new BigDecimal(1,MathContext.DECIMAL128).divide(new BigDecimal(10)));
		EmpiricalDistribution ed = new EmpiricalDistribution(pdf.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
		EmpiricalDistribution ed2 = new EmpiricalDistribution(pdf2.getFunction(), EmpiricalDistribution.LINEAR_INTERPOLATION, RandomEngine.makeDefault());
		KolmogorovTest ks = new KolmogorovTest(ed2.getWholeCDF(), ed);
		double kD = ks.getTestStatistic();	
		double pD = ks.getSP();
		System.out.println(kD + "\t" + pD);
		ks = new KolmogorovTest(pdf2.getFunction(), ed);
		kD = ks.getTestStatistic();	
		pD = ks.getSP();
		System.out.println(kD + "\t" + pD);
		ks = new KolmogorovTest(uniff, ed);
		kD = ks.getTestStatistic();	
		pD = ks.getSP();
		System.out.println(kD + "\t" + pD);
	}
}
