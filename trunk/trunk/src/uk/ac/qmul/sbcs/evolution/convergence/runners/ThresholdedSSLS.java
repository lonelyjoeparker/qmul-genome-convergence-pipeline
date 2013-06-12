package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.ParsimonyReconstruction;
import uk.ac.qmul.sbcs.evolution.convergence.StateComparison;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.serFilter;

public class ThresholdedSSLS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThresholdedSSLS p = new ThresholdedSSLS();
		p.go(args);
	}

	private void go(String[] args){
		System.out.println("input\tInputFileName\tmodel\tnT_H1\tnT_H2");
		File dir = new File(args[0]);
		FilenameFilter serFileFilter = new serFilter();
		StringBuffer bufMain = new StringBuffer();				// no treefile buffer for this one.
//		bufMain.append("locus\tensembl_URL\tshortcode\tdescription\thomog\tmodel\tmissingData\tlnl (species tree)\tlength (species tree)\tmodel alpha\tnumberOfTaxa\tnumberOfSites\tpreferredTopology");
//		bufMain.append("\n");

		Pattern specificPattern = Pattern.compile(args[1]);

		if(dir.isDirectory()){
			String[] serFilesList = dir.list(serFileFilter);
			for(String someFile:serFilesList){
				Matcher simMatch = specificPattern.matcher(someFile);
				if(simMatch.find()){
					InputStream serfile;
					ObjectInputStream inOne;
					SitewiseSpecificLikelihoodSupportAaml candidate;
					try {
						File input = new File(dir.getAbsolutePath()+"/"+someFile);
						serfile = new FileInputStream(input);
						inOne = new ObjectInputStream(serfile);
						candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
						int nT_H1 = 0;
						int nT_H2 = 0;
						float[][] SSLS = candidate.getSSLSseriesSitewise();
						int nSites = SSLS.length;
						for(int i=0; i<nSites;i++){
							float SSLS_H1 = SSLS[i][0] - SSLS[i][1];
							float SSLS_H2 = SSLS[i][0] - SSLS[i][SSLS[i].length-1];					
							if(SSLS_H1 < -0.0182){nT_H1++;};
							if(SSLS_H2 < -0.1263){nT_H2++;};
						}
						System.out.println(input.getName()+"\t"+candidate.getInputFileName()+"\t"+candidate.getModel()+"\t"+nT_H1+"\t"+nT_H2);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
