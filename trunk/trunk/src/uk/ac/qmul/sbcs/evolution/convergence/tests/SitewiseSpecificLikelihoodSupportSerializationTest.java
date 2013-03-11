package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupport;
import junit.framework.TestCase;
import java.io.*;

public class SitewiseSpecificLikelihoodSupportSerializationTest extends TestCase {

	SitewiseSpecificLikelihoodSupport SSLS1;
	SitewiseSpecificLikelihoodSupport SSLS2;

	
	public SitewiseSpecificLikelihoodSupportSerializationTest() {
	}

	public SitewiseSpecificLikelihoodSupportSerializationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSerialization(){
		SSLS1 = new SitewiseSpecificLikelihoodSupport();
		SSLS1.alpha = new float[2];
		SSLS1.alpha[0] = 0.04f;
		SSLS1.alpha[1] = 0.02f;
		SSLS1.inputFileName = "ssls one";
		SSLS2 = new SitewiseSpecificLikelihoodSupport();
		SSLS2.alpha = new float[2];
		SSLS2.alpha[0] = 10.04f;
		SSLS2.alpha[1] = 30.02f;
		SSLS2.inputFileName = "ssls two";
		SSLS2.numberOfModels = 1;
		try {
			FileOutputStream fileOutOne = new FileOutputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.1.ser");
			ObjectOutputStream outOne;
			outOne = new ObjectOutputStream(fileOutOne);
			outOne.writeObject(this.SSLS1);
			outOne.close();
			fileOutOne.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileOutputStream fileOutTwo = new FileOutputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.2.ser");
			ObjectOutputStream outTwo = new ObjectOutputStream(fileOutTwo);
			outTwo.writeObject(this.SSLS2);
			outTwo.close();
			fileOutTwo.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert(true);
		SSLS1 = null;
		SSLS2 = null;
		assert(true);
	}
	
	public void testInflation(){
		try {
			FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.1.ser");
			ObjectInputStream inOne = new ObjectInputStream(fileInOne);
			SSLS1 = (SitewiseSpecificLikelihoodSupport) inOne.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			FileInputStream fileInTwo = new FileInputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.2.ser");
			ObjectInputStream inTwo = new ObjectInputStream(fileInTwo);
			SSLS2 = (SitewiseSpecificLikelihoodSupport) inTwo.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(SSLS1.inputFileName);
		System.out.println(SSLS2.inputFileName);
		assert(true);
	}
}
