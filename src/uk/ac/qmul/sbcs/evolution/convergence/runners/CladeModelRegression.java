package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CladeModelOutput;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression;

public class CladeModelRegression {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File serFile = new File(args[0]);
		File rst = new File(args[1]);
		boolean printSites = Boolean.parseBoolean(args[2]);
		SitewiseSpecificLikelihoodSupportAaml candidate = null;
		try{
			InputStream serfile = new FileInputStream(serFile);
			ObjectInputStream inOne = new ObjectInputStream(serfile);
			candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
		}catch (Exception ex){
			ex.printStackTrace();
		}
		CladeModelOutput cmo = new CladeModelOutput(rst);
		HashMap<Integer,Double> siteOmegas = cmo.getAllSiteOmegas();
		int numSites = candidate.getNumberOfSites();
		int numTrees = candidate.getNumberOfTopologies();
		float[][] SSLS = candidate.getSSLSseriesSitewise();
		float dSSLS_sum = 0;
		String parent = rst.getParent();
		double[] x = new double[numSites];
		double[] y = new double[numSites];
		if(printSites){
			for(int i=0;i<numSites;i++){
				float dSSLS = SSLS[i][0] - SSLS[i][numTrees-1];
				dSSLS_sum += dSSLS;
				double w = siteOmegas.get(i);
				System.out.println(parent+"\t"+i+"\t"+dSSLS+"\t"+w);
				x[i] = dSSLS;
				y[i] = w;
			}
		}else{
			for(int i=0;i<numSites;i++){
				float dSSLS = SSLS[i][0] - SSLS[i][numTrees-1];
				dSSLS_sum += dSSLS;
				double w = siteOmegas.get(i);
				x[i] = dSSLS;
				y[i] = w;
			}
		}
		if(!printSites){
			double mean_dSSLS = (dSSLS_sum / (float)numSites);
			LinearRegression linear = new LinearRegression(y,x);
			System.out.print(candidate.getInputFileName()+"\t");
			System.out.print(parent+"\t");
			System.out.print(mean_dSSLS+"\t");
			System.out.print(linear.getRsq()+"\t");
			System.out.print(linear.getBeta0_lo()+"\t");
			System.out.print(linear.getBeta0()+"\t");
			System.out.print(linear.getBeta0_hi()+"\t");
			System.out.print(linear.getBeta1_lo()+"\t");
			System.out.print(linear.getBeta1()+"\t");
			System.out.print(linear.getBeta1_hi()+"\n");
		}
	}
}
