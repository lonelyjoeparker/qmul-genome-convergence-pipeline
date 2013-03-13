package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TreeMap;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import junit.framework.TestCase;

public class SitewiseSpecificLikelihoodSupportInitializationTest extends TestCase {

	SitewiseSpecificLikelihoodSupportAaml SSLS1, SSLS2, SSLS3;
	
	public SitewiseSpecificLikelihoodSupportInitializationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseAamlOutputFile(){
		try {
			FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.jones.ser");
			ObjectInputStream inOne = new ObjectInputStream(fileInOne);
			SSLS1 = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
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
		AlignedSequenceRepresentation asr = SSLS1.getDataset();
		TreeMap<String, Float>[] patterns = SSLS1.getPatternLikelihoods();
		SSLS1.setNumberOfSitePatterns(patterns[0].size());
		SSLS1.setNumberOfSites(asr.getNumberOfSites());
		SSLS1.setNumberOfSeries(SSLS1.getNumberOfModels() * SSLS1.getNumberOfTopologies());
		SSLS1.parseAamlOutput(new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/f_100_ENSG00000070214_ng.fas/aaml.out"));
		SSLS1.fillOutAndVerify();
		SSLS1.getFinished();
	}
	
	public void examineReinflation(){
		try {
			FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/serialised/f_100_ENSG00000070214_ng.fasinput.phy.nc.fa.debugconv1363145629173wag.ser");
			ObjectInputStream inOne = new ObjectInputStream(fileInOne);
			SSLS1 = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
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
			FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.wag.ser");
			ObjectInputStream inOne = new ObjectInputStream(fileInOne);
			SSLS2 = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
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
			FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/programming/java/QMUL_GCP/SSLS.dayhoff.ser");
			ObjectInputStream inOne = new ObjectInputStream(fileInOne);
			SSLS3 = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
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
		this.hashCode();
	}
}
