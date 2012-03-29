package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.util.ArrayList;

import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.ExperimentalDataSeries;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PercentileOutOfRangeError;
import junit.framework.TestCase;

public class ExperimentalDataSeriesTest extends TestCase {

	private ExperimentalDataSeries eds;
	
	public ExperimentalDataSeriesTest(String name) {
		super(name);
		BasicFileReader reader = new BasicFileReader();
		ArrayList<String> data = reader.loadSequences(new File("/pamlTest/trialDataFromGeorgia/expLnLvals"));
		float [] floatData = new float[data.size()];
		for(int i=0; i<floatData.length; i++){
			floatData[i] = (float) Float.parseFloat(data.get(i));
		}
		eds = new ExperimentalDataSeries(floatData, "test data");
	}

	protected void setUp() throws Exception {
		super.setUp();
 
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExperimentalDataSeries() {
		fail("Not yet implemented"); // TODO
	}

	public void testExperimentalDataSeriesFloatArrayString() {
		fail("Not yet implemented"); // TODO
	}

	public void testExperimentalDataSeriesArrayListOfFloatString() {
		fail("Not yet implemented"); // TODO
	}

	public void testExperimentalDataSeriesDataSeries() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetMin() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetMax() {
		float max = eds.getMax();
		boolean a = true;
		boolean b = false;
		
		boolean c = a && b;
		boolean d = a && a;
		boolean e = b && b;
		
		boolean f = true;
	}

	public void testSetMin() {
		fail("Not yet implemented"); // TODO
	}

	public void testSetMax() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetData() {
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
		fail("Not yet implemented"); // TODO
	}

	public void testGetPercentileCorrespondingToValue() {
		fail("Not yet implemented"); // TODO
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

	public void testGetCount() {
		fail("Not yet implemented"); // TODO
	}

	public void testCompareData() {
		fail("Not yet implemented"); // TODO
	}

	public void testPrintBasic() {
		fail("Not yet implemented"); // TODO
	}

	public void testNumObservationsGreaterThan() {
		fail("Not yet implemented"); // TODO
	}

	public void testNumObservationsLessThan() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetCountsDataDeprecateMe() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetCountsPDFCDFDataDeprecateMe() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetCountsPDFCDFDataDeprecateMeEfficientFloatFloat() {
		fail("Not yet implemented"); // TODO
	}

	public void testGetCountsPDFCDFDataDeprecateMeEfficient() {
		fail("Not yet implemented"); // TODO
	}
	
	public void testGetThresholdValueAtCumulativeDensityRangeOutDown(){
		float test = -0.1f;
		float critical = Float.NaN;
		try {
			critical = eds.getThresholdValueAtCumulativeDensity(test);
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		critical ++;
	}

	public void testGetThresholdValueAtCumulativeDensityRangeOutUp(){
		float test = 1.1f;
		float critical = Float.NaN;
		try {
			critical = eds.getThresholdValueAtCumulativeDensity(test);
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		critical ++;
	}

	public void testGetThresholdValueAtCumulativeDensity(){
		float test = 0.05f;
		float critical = Float.NaN;
		try {
			critical = eds.getThresholdValueAtCumulativeDensity(test);
		} catch (PercentileOutOfRangeError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		critical ++;
	}
	
	public void testGetThresholdValueAtCumulativeDensityRanges(){
		float test = -0.1f;
		float[] critical = new float[100];
		for(int i=0;i<100;i++){
			test = 0f + ((float)i/100f);
			try {
				critical[i] = eds.getThresholdValueAtCumulativeDensity(test);
			} catch (PercentileOutOfRangeError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		critical[0]++;
	}
	
	public void testGetNumberGreaterThan(){
		int[] num = new int[5];
		num[0] = eds.getNumberGreaterThan(0.0f);
		num[1] = eds.getNumberGreaterThan(0.1f);
		num[2] = eds.getNumberGreaterThan(-0.1f);
		num[3] = eds.getNumberGreaterThan(-1f);
		num[4] = eds.getNumberGreaterThan(1f);
		num[0]++;
	}

	public void testGetNumberLessThan(){
		int num = 0;
		num = eds.getNumberLessThan(0.0f);
		num = eds.getNumberLessThan(0.1f);
		num = eds.getNumberLessThan(-0.1f);
		num = eds.getNumberLessThan(-1f);
		num = eds.getNumberLessThan(1f);
		num++;
	}
}
