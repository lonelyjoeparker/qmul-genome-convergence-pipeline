package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;
import junit.framework.TestCase;

public class DataSeriesTest extends TestCase {

	public void testDataSeries() {
		fail("Not yet implemented"); // TODO
	}

	public void testDataSeriesFloatArrayString() {
		fail("Not yet implemented"); // TODO
	}

	public void testDataSeriesArrayListOfFloatString() {
		fail("Not yet implemented"); // TODO
	}

	public void testFormBins() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetMean() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetMode() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetMedian() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetSE() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetSum() {
		fail("Not yet implemented"); // TODO
	}

	public void testCalculatePDF() {
		fail("Not yet implemented"); // TODO
	}

	public void testCalculateCDF() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetValueAtPercentile() {
		float[] dummy = new float[10000];
		for(int i = 0; i<10000; i++){
			dummy[i] = (float)Math.random();
		}
		DataSeries test = new DataSeries(dummy,"test");
		float valueAtMinus1 = 0.0f;
		float valueAt5;
		float valueAt25;
		float valueAt50;
		float valueAt75;
		float valueAt95;
		float valueAt99;
		float valueAt100;
		float valueAt101 = 0.0f;
		try {
//			valueAtMinus1 = test.getValueAtPercentile(-1);
			valueAt5 = test.getValueAtPercentile(5);
			valueAt25 = test.getValueAtPercentile(25);
			valueAt50 = test.getValueAtPercentile(50);
			valueAt75 = test.getValueAtPercentile(75);
			valueAt95 = test.getValueAtPercentile(95);
			valueAt99 = test.getValueAtPercentile(99);
			valueAt100 = test.getValueAtPercentile(100);
//			valueAt101 = test.getValueAtPercentile(101);
			System.out.println("Percentiles:\n-1%\t5%\t25%\t50%\t75%\t95%\t99%\t100%\t101");
			System.out.println(valueAtMinus1+"\t"+valueAt5+"\t"+valueAt25+"\t"+valueAt50+"\t"+valueAt75+"\t"+valueAt95+"\t"+valueAt99+"\t"+valueAt100+"\t"+valueAt101);
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testGetPercentileCorrespondingToValue(){
		float[] dummy = new float[10000];
		for(int i = 0; i<10000; i++){
			dummy[i] = (float)Math.random();
		}
		DataSeries test = new DataSeries(dummy,"test");
		int valueMinus1 = test.getPercentileCorrespondingToValue(-0.1f);
		int value1 = test.getPercentileCorrespondingToValue(0.1f);
		int value2 = test.getPercentileCorrespondingToValue(0.2f);
		int value3 = test.getPercentileCorrespondingToValue(0.3f);
		int value4 = test.getPercentileCorrespondingToValue(0.4f);
		int value5 = test.getPercentileCorrespondingToValue(0.5f);
		int value6 = test.getPercentileCorrespondingToValue(0.6f);
		int value7 = test.getPercentileCorrespondingToValue(0.7f);
		int value8 = test.getPercentileCorrespondingToValue(0.8f);
		int value9 = test.getPercentileCorrespondingToValue(0.9f);
		int value10 = test.getPercentileCorrespondingToValue(1.0f);
		int value11 = test.getPercentileCorrespondingToValue(1.1f);
		System.out.println("Intervals:\n0.1\t0.2\t0.3\t0.4\t0.5\t0.6...1.1");
		System.out.println(valueMinus1+"\t"+value1+"\t"+value2+"\t"+value3+"\t"+value4+"\t"+value5+"\t"+value6+"\t"+value7+"\t"+value8+"\t"+value9+"\t"+value10+"\t"+value11);
	}
	
	public void testReturnUpperPercentileOverlap() {
		fail("Not yet implemented"); // TODO
	}

	public void testReturnLowerPercentileOverlap() {
		fail("Not yet implemented"); // TODO
	}

	public void testDoKStest() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetName() {
		fail("Not yet implemented"); // TODO
	}

}
