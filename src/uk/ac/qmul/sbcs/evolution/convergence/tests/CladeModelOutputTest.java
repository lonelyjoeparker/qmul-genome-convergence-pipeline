package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.CodemlResultReader;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CladeModelOutput;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression;
import junit.framework.TestCase;

public class CladeModelOutputTest extends TestCase {

	public CladeModelOutputTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testCladeModelOutputFileFile() {
		File rst = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/g_100_ENSG00000167671_ng.fas/rst");
		File MC  = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/g_100_ENSG00000167671_ng.fas/codemlMC.out");
		CladeModelOutput cmo = new CladeModelOutput(rst);
		CodemlResultReader crr = new CodemlResultReader(MC);
		crr.printParams();
	}

	public final void testGetRegressionData() {
		SitewiseSpecificLikelihoodSupportAaml candidate = null;
		try{
			InputStream serfile = new FileInputStream("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/g_100_ENSG00000167671_ng.fas/g_100_ENSG00000167671_ng.fasinput100.faconv1367266284364wag.ser");
			ObjectInputStream inOne = new ObjectInputStream(serfile);
			candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
		}catch (Exception ex){
			ex.printStackTrace();
		}
		File rst = new File("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/g_100_ENSG00000167671_ng.fas/rst");
		CladeModelOutput cmo = new CladeModelOutput(rst);
		HashMap<Integer,Double> siteOmegas = cmo.getAllSiteOmegas();
		int numSites = candidate.getNumberOfSites();
		int numTrees = candidate.getNumberOfTopologies();
		float[][] SSLS = candidate.getSSLSseriesSitewise();
		float dSSLS_sum = 0;
		double[] x = new double[numSites];
		double[] y = new double[numSites];
		for(int i=0;i<numSites;i++){
			float dSSLS = SSLS[i][0] - SSLS[i][numTrees-1];
			dSSLS_sum += dSSLS;
			double w = siteOmegas.get(i);
			System.out.println(i+"\t"+dSSLS+"\t"+w);
			x[i] = dSSLS;
			y[i] = w;
		}
		double mean_dSSLS = (dSSLS_sum / (float)numSites);
		LinearRegression linear = new LinearRegression(x,y);
		System.out.print(candidate.getInputFileName()+"\t");
		System.out.print(rst.getParent()+"\t");
		System.out.print(mean_dSSLS+"\t");
		System.out.print(linear.getRsq()+"\t");
		System.out.print(linear.getBeta0_lo()+"\t");
		System.out.print(linear.getBeta0()+"\t");
		System.out.print(linear.getBeta0_hi()+"\t");
		System.out.print(linear.getBeta1_lo()+"\t");
		System.out.print(linear.getBeta1()+"\t");
		System.out.print(linear.getBeta1_hi()+"\t");
	}

}
