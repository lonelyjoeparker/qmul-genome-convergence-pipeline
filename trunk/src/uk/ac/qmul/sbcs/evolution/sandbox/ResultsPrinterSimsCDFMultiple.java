package uk.ac.qmul.sbcs.evolution.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.serFilter;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.PairedEmpirical;

public class ResultsPrinterSimsCDFMultiple{

	boolean printSites = false;
	int referenceTree; // Explicit to changes Mar 2014: the reference hypothesis ('H0') is formally defined.
	int maxTrees;
	int[] testTrees;
	int[] nullTrees;
	File dir;
	String[] models = {"wag","jones","dayhoff"};
	
	public ResultsPrinterSimsCDFMultiple(String whichDir, String doSites, String refTreeInt, String maxtreesInt, String testRange, String nullRange, String modelsRange) {
		// TODO Auto-generated constructor stub
		this.dir = new File(whichDir);
		if(doSites != null){
			int intval = Integer.parseInt(doSites);
			if(intval==1){
				this.printSites = true;
			}
		}
		if(refTreeInt != null){
			this.referenceTree = Integer.parseInt(refTreeInt);
		}
		if(maxtreesInt != null){
			this.maxTrees = Integer.parseInt(maxtreesInt);
		}
		// Parse the testRange and nullRange args - these tell us where the test and null trees are located
		String[] testIndices = testRange.split(":");
		String[] nullIndices = nullRange.split(":");
		int testFrom = 0;
		int testTo = 0;
		if(testIndices[0] != null){
			testFrom = Integer.parseInt(testIndices[0]);
		}
		if(testIndices[1] != null){
			testTo = Integer.parseInt(testIndices[1]);
		}
		int nullFrom = 0;
		int nullTo = 0;
		if(nullIndices[0] != null){
			nullFrom = Integer.parseInt(nullIndices[0]);
		}
		if(nullIndices[1] != null){
			nullTo = Integer.parseInt(nullIndices[1]);
		}
		nullTrees = new int[(nullTo-nullFrom)+1];
		testTrees = new int[(testTo-testFrom)+1];

		// OK now we have the ranges hopefully. Set up the arrays which we'll use to index with..
		int test_i = testFrom;
		for(int i=0;i<testTrees.length;i++){
			testTrees[i] = test_i;
			test_i++;
		}
		int null_i = nullFrom;
		for(int i=0;i<nullTrees.length;i++){
			nullTrees[i] = null_i;
			null_i++;
		}
		
		// get the models
		models = modelsRange.split(":");
		assert(true);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new ResultsPrinterSimsCDFMultiple(args[0], args[1], args[2], args[3], args[4], args[5], args[6]).go2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void go2(){
		/*
		 * Same stuff as go() in terms of method, except treating things a little differently..
		 * 
		 * get 
		 * 			SitewiseSpecificLikelihoodSupportAaml[] results 	= new SitewiseSpecificLikelihoodSupportAaml[runfiles];
		 *			SitewiseSpecificLikelihoodSupportAaml[] simulations = new SitewiseSpecificLikelihoodSupportAaml[simfiles];
		 * 
		 * then buid 
		 * 
		 * double [][] simulatedDeltas = new double[this.maxTrees-1][0]; (will be built up by array concatenation)
		 * double[][] dSSLS = new double[this.maxTrees-1][nSites]; 
		 * 
		 * this time we're interested in..
		 * 
		 * 			dSSLS	[0:10,maxTrees-1] vs dSSLS[11: maxTrees-2]  <--- this is the weirdness of the observed ÆSSLS for our Ha of interest as opposed to the random Ha (check maths etc)
		 * simulatedDeltas	[0:10,maxTrees-1] vs dSSLS[11: maxTrees-2]  <--- this is the weirdness of the observed ÆSSLS for our Ha of interest as opposed to the random Ha.. IF. THE. NULL. HYPOTHESIS (H0). WAS. TRUE.
		 * 
		 * New approach 2013/05/01
		 * 
		 * 	Method 1:
		 * 		a) emprirical data:
		 * 				aggregate all ÆSSLS(H0-Ha; a=1,2,3 and 112) and ÆSSLS(H0-Hrandoms; r1:r100)
		 * 					(simplest way to do this is build the double[][] dSSLS array as before then populate dSSLS_random[] after)
		 * 				KS test Ha vs. Hrandom.
		 * 				Ha #n will be nSites. Hrandom #n will be nSites*100 e.g. random trees count
		 * 				Sitewise information is pooled across all sites.
		 * 		b) simulated data:
		 * 				aggregate all ÆSSLS as above
		 * 				KS test Ha vs. Hrandom, as above, but this time we can assume H0 is correct, cos we've simulated on it
		 * 				Ha #n will be nSites*nSims. Hrandom #n will be nSites*nSims*100 e.g. random trees count
		 * 				Sitewise information will be pooled across sites again. 
		 * 
		 * 	Method 1 output will then be:
		 * 		- observed ÆSSLS in empirical
		 * 		- % in random trees of that ÆSSLS value, KS comparison
		 * 		- simulated distribution, same stats.
		 * 
		 * 
		 * Method 1.5:
		 * 		a) for each site, i,  in a given double[][] dSSLS:
		 * 			- construct CDF from ÆSSLS (site i) for H0-Hrandom (for all random)
		 * 			- obtain observed values for that site (i) in H0-Ha (a­random)
		 * 			- get CDF % in random data of Ha
		 * 			- output will be a double[] of CDF percentiles (for each Ha, so a double[][] overall), representing the extremeness-of-ÆSSLS of each Ha for each site i
		 * 		b) repeat (a) above but for simulated data, this time aggregating the sitewise 'extremeness' double[][]s into one.
		 * 		c) then KS test the extremeness double[][]s together, e.g. asking the question:
		 * 
		 *			"Does empirical data have more sites with more extreme support for Ha over H0 *when* compared
		 *			 to random Ha than simulated data do [e.g. we;d expect due to chance]"
		 *
		 *
		 * Methods 1 and 1.5 should be easyish to implement. 
		 * Method 1 main tasks:
		 * 	- determine which trees (cols in double[][]dSSLS) are Ha or Hr, method to concatenate Hr data (both empirical and simulated)
		 *  - KS test of random vs. alternative trees in empirical and simulated data.
		 *  Method 1.5 main tasks:
		 *  - initialise parallel double[][]extreme_percentiles
		 *  - sitewise CDF construction, populate extreme_percentiles[tree][site] (for tree­random)
		 *  - KS test etc on extreme_percentiles for empirical and simulated data.
		 */
		FilenameFilter serFileFilter = new serFilter();
		StringBuffer bufMain = new StringBuffer();				// no treefile buffer for this one.

		Pattern simPattern = Pattern.compile("s_");

		HashMap<String, String> lociData = this.initMetadata();
		if(dir.isDirectory()){
			String[] serFilesList = dir.list(serFileFilter);
			int simfiles = 0;
			int runfiles = 0;
			for(String someFile:serFilesList){
				Matcher simMatch = simPattern.matcher(someFile);
				if(simMatch.find()){
					simfiles++;
				}else{
					runfiles++;
				}
			}
			SitewiseSpecificLikelihoodSupportAaml[] results 	= new SitewiseSpecificLikelihoodSupportAaml[runfiles];
			SitewiseSpecificLikelihoodSupportAaml[] simulations = new SitewiseSpecificLikelihoodSupportAaml[simfiles];
			int simfilesIndex = 0;
			int runfilesIndex = 0;
			System.err.println("reading .ser files:");
			for(int i=0; i<serFilesList.length;i++){
				System.err.print(".");
				if(Math.round((double)i/10.0d) == (double)i/10.0d){
					System.err.print(i);
					System.err.println();
				}
				try {
					FileInputStream fileInOne = new FileInputStream(dir.getAbsolutePath()+"/"+serFilesList[i]);
					ObjectInputStream inOne = new ObjectInputStream(fileInOne);
					SitewiseSpecificLikelihoodSupportAaml candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
					Matcher simMatch = simPattern.matcher(serFilesList[i]);
					if(simMatch.find()){
						simulations[simfilesIndex] = candidate;
						simfilesIndex++;
					}else{
						results[runfilesIndex] = candidate;
						runfilesIndex++;
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			for(String model:models){
				/*
				 * Analyse SIMULATIONS
				 */
				System.err.println("Analyse simulated ÆSSLS");
				double [][] simulatedDeltas = new double[this.maxTrees][0];
				// METHOD 1.5 initialise double[][] extremePercentilesExpected
				for(int i=0;i<simulations.length;i++){
					System.err.print('o');
					if(Math.round((double)i/10.0d) == (double)i/10.0d){
						System.err.print(i);
						System.err.println();
					}
					SitewiseSpecificLikelihoodSupportAaml someRun = simulations[i];
					try {
						if((someRun.getModel().equals(model))&&(someRun.getNumberOfTopologies()==this.maxTrees)){
							int nSites = someRun.getNumberOfSites();
							float[][] SSLS = someRun.getSSLSseriesSitewise();
							double[][] dSSLS = new double[this.maxTrees][nSites];
							double[][] dSSLS_e = new double[this.testTrees.length][nSites]; // dSSLS percentiles, this run, concatenated later
							double[][] dSSLS_ec = new double[this.testTrees.length][nSites]; // dSSLS (corrected by random trees), this un, corrected later
							for(int j=0;j<nSites;j++){
								double [] randomTreesDSSLS = new double[nullTrees.length];
								int random_t_i = 0;
								for(int k=0;k<this.maxTrees;k++){
									try {
										/*
										 * IMPORTANT - March 2014 - the ÆSSLS now calculated with respect to an explicit REFERENCE tree, not necessarily H0
										 */
										dSSLS[k][j] = SSLS[j][referenceTree] - SSLS[j][k];
									} catch (Exception e) {
										// TODO Auto-generated catch block
										dSSLS[k][j] = Float.NaN;
										e.printStackTrace();
									}
									if(k==nullTrees[random_t_i]){
										randomTreesDSSLS[random_t_i] = dSSLS[k][j];
										if(random_t_i<nullTrees.length-1){random_t_i++;} // otherwise it will fall off the end of nullTrees in cases where the last tree in the file is a non-null one (common)
									}
								}
								// METHOD 1.5 construct CDF from dSSLS for *this* *site* *ONLY*
								// METHOD 1.5 first init random trees double[] for this site (dSSLS,{random_0:random_nRandom})
								// METHOD 1.5 then get invCDF of observed vals from Ha trees
								// METHOD 1.5 then put those p vals to extremePercentilesExpected
								Arrays.sort(randomTreesDSSLS);
								double[] nullThing = {randomTreesDSSLS[0],randomTreesDSSLS[randomTreesDSSLS.length-1]};
								if(!((nullThing[0]==0.0)&&(nullThing[1]==0.0))){
									PairedEmpirical randoms = new PairedEmpirical(randomTreesDSSLS,nullThing,true);
									// METHOD 1.5 then get invCDF of observed vals from Ha trees
									// METHOD 1.5 then put those p vals to extremePercentilesObserved
									for(int c=0;c<(this.testTrees.length);c++){
										double someDelta = dSSLS[testTrees[c]][j];
										double dSSLS_expect;
										if(someDelta<randomTreesDSSLS[0]){
											// observed val < all expected vals, no point in convening a dist.
											dSSLS_expect = 0.0d;
										}else{
											if(someDelta>randomTreesDSSLS[randomTreesDSSLS.length-1]){
												// observed val > all expected vals, no point in convening a dist.
												dSSLS_expect = 1.0d;
											}else{
												// observed is in expected, find range.
												try {
													dSSLS_expect = randoms.getDensity_A(someDelta);
												} catch (Exception e) {
													// TODO Auto-generated catch block
													dSSLS_expect = Double.NaN;
													e.printStackTrace();
												}
											}
										}
										dSSLS_e[c][j]  = dSSLS_expect;
										dSSLS_ec[c][j] = dSSLS[testTrees[c]][j] + randoms.getMean_A();
									}
									randoms = null;
								}
							}
							for (int k = 0; k < this.maxTrees; k++) {
								simulatedDeltas[k] = this.concat(simulatedDeltas[k], dSSLS[k]);
								// METHOD 1.5 concat for extremePercentilesExpected as well
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				

				/*
				 * Analysed OBSERVED sites
				 */
				for(SitewiseSpecificLikelihoodSupportAaml someRun:results){
					try {
						if((someRun.getModel().equals(model))&&(someRun.getNumberOfTopologies()==this.maxTrees)){
							StringBuffer buf = new StringBuffer();
							try {
								float[] alphas = someRun.getAlpha();
								float[] sli = someRun.getLi();
								float[] lengths = someRun.getTreeLengths();

								/*
								 * Hacky quick thing Sat night to check this works 
								 * *really* ought to do in a proper testbed for production code..
								 */
								double[] testDouble = new double[alphas.length];
								for(int counter=0;counter<testDouble.length; counter++){
									testDouble[counter] = alphas[counter];
								}
								boolean[] preferred = someRun.getPreferred();
								int nTax = someRun.getNumberOfTaxa();
								int nSites = someRun.getNumberOfSites();

								String[] nameSplit = someRun.getInputFile().getPath().split("_");
								if(lociData.containsKey(nameSplit[5].toUpperCase())){
									System.out.print(lociData.get(nameSplit[5].toUpperCase())+"\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
									buf.append(lociData.get(nameSplit[5].toUpperCase())+"\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
								}else{
									System.out.print(nameSplit[5].toUpperCase()+"\tNA\tNA\tNA\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
									buf.append(nameSplit[5].toUpperCase()+"\tNA\tNA\tNA\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
								}

								Integer prefTree = null;
								for(int k=0;k<preferred.length;k++){
									if(preferred[k]){
										prefTree = k;
									}
								}
								if(prefTree == null){
									System.out.println("\tNA");
									buf.append("\tNA");
								}else{
									System.out.println("\t"+prefTree);
									buf.append("\t"+prefTree);
								}

								float[][] SSLS = someRun.getSSLSseriesSitewise();
								float[] lnLsums = new float[someRun.getNumberOfTopologies()];
								BigDecimal[] diffsSummed = new BigDecimal[this.maxTrees];
								for(int i=0;i<this.maxTrees;i++){
									diffsSummed[i] = new BigDecimal(0);
								}
								for(int j=0;j<someRun.getNumberOfSites();j++){
									lnLsums[0] = lnLsums[0] + SSLS[j][0];
									lnLsums[(this.maxTrees-1)] = lnLsums[(this.maxTrees-1)] + SSLS[j][(this.maxTrees-1)];
								}
								double[][] dSSLS = new double[this.maxTrees][nSites];
								// METHOD 1.5 initialise double[][] extremePercentilesObserved
								double[][] extremePercentilesObserved = new double[this.testTrees.length][nSites];
								double[][] dSSLScorrectedForRandomTrees = new double[this.testTrees.length][nSites];
								for(int j=0;j<nSites;j++){
									double [] randomTreesDSSLS = new double[nullTrees.length];
									int random_t_i = 0;
									for(int k=0;k<this.maxTrees;k++){
										try {
											lnLsums[k] = lnLsums[k] + SSLS[j][k];
											/*
											 * IMPORTANT - March 2014 - the ÆSSLS now calculated with respect to an explicit REFERENCE tree, not necessarily H0
											 */
											dSSLS[k][j] = SSLS[j][referenceTree] - SSLS[j][k];							
											if((k)==nullTrees[random_t_i]){
												randomTreesDSSLS[random_t_i] = dSSLS[k][j];
												if(random_t_i<nullTrees.length-1){random_t_i++;} // otherwise it will fall off the end of nullTrees in cases where the last tree in the file is a non-null one (common)
											}
											/*
											 * IMPORTANT - March 2014 - the ÆSSLS now calculated with respect to an explicit REFERENCE tree, not necessarily H0
											 */
											BigDecimal thisDiffBD = new BigDecimal(SSLS[j][referenceTree] - SSLS[j][k]);
											diffsSummed[k] = diffsSummed[k].add(thisDiffBD);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											dSSLS[k][j] = Float.NaN;
											e.printStackTrace();
										}
									}
									// METHOD 1.5 construct CDF from dSSLS for *this* *site* *ONLY*
									// METHOD 1.5 first init random trees double[] for this site (dSSLS,{random_0:random_nRandom})
									Arrays.sort(randomTreesDSSLS);
									double[] nullThing = {randomTreesDSSLS[0],randomTreesDSSLS[randomTreesDSSLS.length-1]};
									if(!((nullThing[0]==0.0)&&(nullThing[1]==0.0))){												// This is a test for the start AND the end of the randomTreesDSSLS being == 0.0d
										PairedEmpirical randoms = new PairedEmpirical(randomTreesDSSLS,nullThing,true);
										// METHOD 1.5 then get invCDF of observed vals from Ha trees
										// METHOD 1.5 then put those p vals to extremePercentilesObserved
										
										// METHOD 1.6 ***** 
										// Could compare the ÆSSLS in the observed sites for H0Ha with the expected (simulated) sites in H0Hrandom, or indeed Ha
										// This would tell us unexpectedness of each site for Hr or Ha
										// So do unexpectedness for H0Ha, and H0Hr; where 
										// "Unexpectedness" == percentile of observed site ÆSSLS vs. simulated distribution for *that* topology.
										// This means:
										// Unexpectedness for H0Ha is the statistic for convergence, but
										// Unexpectedness for H0Hr is the correction for that stat. First time out report uncorrected vals, corrected ones, and mean correction factor (plus SD) to check.
										
										for(int c=0;c<(this.testTrees.length);c++){
											double someDelta = dSSLS[testTrees[c]][j];
											double dSSLS_expect;
											if(someDelta<randomTreesDSSLS[0]){
												// observed val < all expected vals, no point in convening a dist.
												dSSLS_expect = 0.0d;
											}else{
												if(someDelta>randomTreesDSSLS[randomTreesDSSLS.length-1]){
													// observed val > all expected vals, no point in convening a dist.
													dSSLS_expect = 1.0d;
												}else{
													// observed is in expected, find range.
													try {
														dSSLS_expect = randoms.getDensity_A(someDelta);
													} catch (Exception e) {
														// TODO Auto-generated catch block
														dSSLS_expect = Double.NaN;
														e.printStackTrace();
													}
												}
											}
											extremePercentilesObserved[c][j] = dSSLS_expect;									
											dSSLScorrectedForRandomTrees[c][j] = dSSLS[testTrees[c]][j] + randoms.getMean_A();
										}
										randoms = null;
									}
								}
								
								// METHOD 1.6 Unexpectedness matrix initialise
								double[][] unexpectedness = new double[this.maxTrees][dSSLS[0].length];
								double[] sumUnexpectedness = new double[this.maxTrees];
								double[] sumUnexpectednessCorrected = new double[this.maxTrees];
								// METHOD 1.6 populate unexpectedness matrix
								for(int a=0;a<this.maxTrees;a++){
									// extract the expected values for H(a) from the ÆSSLS matrix for simulations
									int numSimulatedSites = simulatedDeltas[a].length;
									double[] expected_a = new double[numSimulatedSites];
									for(int j=0;j<numSimulatedSites;j++){
										expected_a[j] = simulatedDeltas[a][j];
									}

									// Set up the eCDF for expected sites (simulated ÆSSLS for H0-Ha)
									Arrays.sort(expected_a);
									double[] nullThing = {expected_a[0],expected_a[numSimulatedSites-1]};
									PairedEmpirical randoms = null;
									if(!((nullThing[0]==0.0)&&(nullThing[1]==0.0))){												// This is a test for the start AND the end of the randomTreesDSSLS being == 0.0d
										randoms = new PairedEmpirical(expected_a,nullThing,true);
									}
									
									// Get sitewise U(a) as CDF(dSSLS[a][j]) where CDF from expected H(a)
									// NB null values will be given Double.NaN - TAKE CARE when processing this...
									for(int j=0;j<unexpectedness[0].length;j++){
										double observedSiteDSSLS = dSSLS[a][j];
										if(randoms==null){
											// Failed to set up the eCDF for expected ÆSSLS
											unexpectedness[a][j] = Double.NaN;
										}else{
											if(observedSiteDSSLS < expected_a[0]){
												unexpectedness[a][j] = 1.0d; 	// Maximal unexpectedness (observed ÆSSLS more extreme, left tail, than all expected values)
											}else if(observedSiteDSSLS > expected_a[numSimulatedSites-1]){
												unexpectedness[a][j] = 0.0d; 	// Minimal unexpectedness (observed ÆSSLS less extreme, right tail, than all expected values)
											}else{
												unexpectedness[a][j] = (1.0d - randoms.getDensity_A(observedSiteDSSLS));
											}
										}
									}
									randoms=null;
								}
								// METHOD 1.6 calculate sitewise Ua correction (by -mean(Ur))
								double[] unexpectednessRandomTreesCorrection = new double[unexpectedness[0].length];
								for(int j=0;j<unexpectedness[0].length;j++){
									// really irritatingly, it looks like we have to iterate through again, since the unexpectedness values weren't known in advance last time..
									double[] nullUnexpectedness = new double[nullTrees.length];
									for(int r=0;r<nullUnexpectedness.length;r++){
										nullUnexpectedness[r] = unexpectedness[nullTrees[r]-1][j];
									}
									unexpectednessRandomTreesCorrection[j] = this.meanSafeForNaN(nullUnexpectedness);
								}
								
								// METHOD 1.6: now sum the unexpectednesses over sites
								for(int j=0;j<unexpectedness[0].length;j++){
									for(int a=0;a<this.maxTrees;a++){
										sumUnexpectedness[a] += unexpectedness[a][j];
										sumUnexpectednessCorrected[a] += (unexpectedness[a][j] - unexpectednessRandomTreesCorrection[j]);
									}
								}
								
								// METHOD 1.5: KS test (for i in 0:testHa) extremePercentilesObserved vs extremePercentilesExpected
								

								// Final results printing shindig
								for(int c=0;c<(this.testTrees.length);c++){
									// IMPORTANT 
									// IMPORTANT
									// IMPORTANT
									// NO REALLY
									// IMPORTANT
									// ---------------------> the testTrees and nullTrees are indexed to INPUT TREE ORDER but the dSSLS indices will be i-1 since they're 1 smaller (no H0-H0, see?)
									//						  TODO ABSOLUTELY MUST TODO find a clear scheme for this, or else MEGA fencepost issues...
									// Do the KS etc
									
									int t = testTrees[c];		
									/* This is the index of *which* test we're doing, *not* the topology under test. 
									 * So (e.g.)
									 * Topology H0-H1 is the second [1] tree in the lnL sets, but the first [0] comparison.
									 * 
									 * Therefore:
									 * 		c refers to which TREE is being compared against H0 (assuming [0]th tree is the H0 one)
									 * 		t refers to which TEST it is..
									 */
									/**
									 * UPDATE MARCH 2014
									 * IGNORE the bit above - indices for all trees, dSSLS matrices, and tests are now the same;
									 * So the tree indices are from 0..(n-1)
									 * and so are the tests
									 * and the reference tree is *also* specified that way...
									 */

									BigDecimal avgOfSummedDiffs = diffsSummed[t].divide(new BigDecimal(nSites),RoundingMode.HALF_EVEN);
									String AOSD = String.format("%.16f",avgOfSummedDiffs);
									System.out.print(
											"\ttH"+referenceTree+"-H"+testTrees[c]+"\t"
											+AOSD+"\t"
											+(sumUnexpectednessCorrected[t]/((double)unexpectedness[0].length))+"\t"
											+"\n");
									buf.append(
											"\ttH"+referenceTree+"-H"+testTrees[c]+"\t"
											+AOSD+"\t"
											+(sumUnexpectednessCorrected[t]/((double)unexpectedness[0].length))+"\t"
									);
								}
								System.out.println("");
								buf.append("\n");
								bufMain.append(buf);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			new CustomFileWriter(new File(dir+"/output.txt"),bufMain.toString());
		}else{
			System.out.println("arg must be a directory");
		}
	
	}

	/**
	 * A method to collapse a subset of matrix columns into one double[]
	 * @param collapseMatrix	- input matrix
	 * @param collapseColumns	- indices of matrix columns to collapse
	 * @return aggregated		- double[] of aggregated data
	 */
	private double[] collapseMatrixColumns(double[][] collapseMatrix, int[] collapseColumns) {
		// TODO Auto-generated method stub
		if(collapseMatrix.length < collapseColumns.length){
			throw new IllegalArgumentException();
		}
		double[] aggregated = new double[collapseColumns.length * collapseMatrix[0].length];
		int aggregated_i = 0;
		for(int i=0;i<collapseColumns.length;i++){
			for(int j=0;j<collapseMatrix[collapseColumns[i]-1].length;j++){
				aggregated[aggregated_i] = collapseMatrix[collapseColumns[i]-1][j];
				aggregated_i++;
			}
		}
		return aggregated;
	}

	private void go(){
		FilenameFilter serFileFilter = new serFilter();
		StringBuffer bufMain = new StringBuffer();
		StringBuffer bufTree = new StringBuffer();

//		bufMain.append("locus\tensembl_URL\tshortcode\tdescription\thomog\tmodel\tmissingData\tlnl (species tree)\tlength (species tree)\tmodel alpha\tnumberOfTaxa\tnumberOfSites\tpreferredTopology\td_lnL_H1\td_lnL_H2 (old H2, not in paper, HARD bat-bat polytomy)\td_lnL_H3 (old H3, 'H2' in the paper, HARD polytomy)\td_lnL_H4 (H2a)\td_lnL_H5 (H2b)\td_lnL_H6 (H2c)\td_lnL_H7 (H2d)\td_lnL_H8 (H2e)\td_lnL_H9 (H2f)\td_lnL_H10 (H2g)\td_lnL_H12_random\td_lnL_H12_random\td_lnL_H13_random\td_lnL_H14_random\td_lnL_H15_random\td_lnL_H16_random\td_lnL_H17_random\td_lnL_H18_random\td_lnL_H19_random\td_lnL_H20_random\td_lnL_H21 (H2 resolved by ML)\tdSSLS_H1\tdSSLS_H2 (old H2, not in paper, HARD bat-bat polytomy)\tdSSLS_H3 (old H3, 'H2' in the paper, HARD polytomy)\tdSSLS_H4 (H2a)\tdSSLS_H5 (H2b)\tdSSLS_H6 (H2c)\tdSSLS_H7 (H2d)\tdSSLS_H8 (H2e)\tdSSLS_H9 (H2f)\tdSSLS_H10 (H2g)\tdSSLS_H12_random\tdSSLS_H12_random\tdSSLS_H13_random\tdSSLS_H14_random\tdSSLS_H15_random\tdSSLS_H16_random\tdSSLS_H17_random\tdSSLS_H18_random\tdSSLS_H19_random\tdSSLS_H20_random\tdSSLS_H21 (H2 resolved by ML)\ttÆSSLS_H1\ttÆSSLS_H2\ttÆSSLS_H3\ttÆSSLS_H4\ttÆSSLS_H5\ttÆSSLS_H6\ttÆSSLS_H7\ttÆSSLS_H8\ttÆSSLS_H9\ttÆSSLS_H10\ttÆSSLS_H12_random\ttÆSSLS_H12_random\ttÆSSLS_H13_random\ttÆSSLS_H14_random\ttÆSSLS_H15_random\ttÆSSLS_H16_random\ttÆSSLS_H17_random\ttÆSSLS_H18_random\ttÆSSLS_H19_random\ttÆSSLS_H20_random\ttÆSSLS_H21 (H2 resolved by ML)\tnumSitesBigDiff_H1\tnumSitesBigDiff_H2 (old H2, not in paper, HARD bat-bat polytomy)\tnumSitesBigDiff_H3 (old H3, 'H2' in the paper, HARD polytomy)\tnumSitesBigDiff_H4 (H2a)\tnumSitesBigDiff_H5 (H2b)\tnumSitesBigDiff_H6 (H2c)\tnumSitesBigDiff_H7 (H2d)\tnumSitesBigDiff_H8 (H2e)\tnumSitesBigDiff_H9 (H2f)\tnumSitesBigDiff_H10 (H2g)\tnumSitesBigDiff_H11_random\tnumSitesBigDiff_H12_random\tnumSitesBigDiff_H13_random\tnumSitesBigDiff_H14_random\tnumSitesBigDiff_H15_random\tnumSitesBigDiff_H16_random\tnumSitesBigDiff_H17_random\tnumSitesBigDiff_H18_random\tnumSitesBigDiff_H19_random\tnumSitesBigDiff_H20_random\tnumSitesBigDiff_H21 (H2 resolved by ML)\n");
		bufMain.append("locus\tensembl_URL\tshortcode\tdescription\thomog\tmodel\tmissingData\tlnl (species tree)\tlength (species tree)\tmodel alpha\tnumberOfTaxa\tnumberOfSites\tpreferredTopology");
//		"d_lnL_H1\td_lnL_H2 (old H2, not in paper, HARD bat-bat polytomy)\td_lnL_H3 (old H3, 'H2' in the paper, HARD polytomy)\td_lnL_H4 (H2a)\td_lnL_H5 (H2b)\td_lnL_H6 (H2c)\td_lnL_H7 (H2d)\td_lnL_H8 (H2e)\td_lnL_H9 (H2f)\td_lnL_H10 (H2g)\td_lnL_H12_random\td_lnL_H12_random\td_lnL_H13_random\td_lnL_H14_random\td_lnL_H15_random\td_lnL_H16_random\td_lnL_H17_random\td_lnL_H18_random\td_lnL_H19_random\td_lnL_H20_random\td_lnL_H21 (H2 resolved by ML)
//		"\tdSSLS_H1\tdSSLS_H2 (old H2, not in paper, HARD bat-bat polytomy)\tdSSLS_H3 (old H3, 'H2' in the paper, HARD polytomy)\tdSSLS_H4 (H2a)\tdSSLS_H5 (H2b)\tdSSLS_H6 (H2c)\tdSSLS_H7 (H2d)\tdSSLS_H8 (H2e)\tdSSLS_H9 (H2f)\tdSSLS_H10 (H2g)\tdSSLS_H12_random\tdSSLS_H12_random\tdSSLS_H13_random\tdSSLS_H14_random\tdSSLS_H15_random\tdSSLS_H16_random\tdSSLS_H17_random\tdSSLS_H18_random\tdSSLS_H19_random\tdSSLS_H20_random\tdSSLS_H21 (H2 resolved by ML)
//		\ttÆSSLS_H1\ttÆSSLS_H2\ttÆSSLS_H3\ttÆSSLS_H4\ttÆSSLS_H5\ttÆSSLS_H6\ttÆSSLS_H7\ttÆSSLS_H8\ttÆSSLS_H9\ttÆSSLS_H10\ttÆSSLS_H12_random\ttÆSSLS_H12_random\ttÆSSLS_H13_random\ttÆSSLS_H14_random\ttÆSSLS_H15_random\ttÆSSLS_H16_random\ttÆSSLS_H17_random\ttÆSSLS_H18_random\ttÆSSLS_H19_random\ttÆSSLS_H20_random\ttÆSSLS_H21 (H2 resolved by ML)
//		\tnumSitesBigDiff_H1\tnumSitesBigDiff_H2 (old H2, not in paper, HARD bat-bat polytomy)\tnumSitesBigDiff_H3 (old H3, 'H2' in the paper, HARD polytomy)\tnumSitesBigDiff_H4 (H2a)\tnumSitesBigDiff_H5 (H2b)\tnumSitesBigDiff_H6 (H2c)\tnumSitesBigDiff_H7 (H2d)\tnumSitesBigDiff_H8 (H2e)\tnumSitesBigDiff_H9 (H2f)\tnumSitesBigDiff_H10 (H2g)\tnumSitesBigDiff_H11_random\tnumSitesBigDiff_H12_random\tnumSitesBigDiff_H13_random\tnumSitesBigDiff_H14_random\tnumSitesBigDiff_H15_random\tnumSitesBigDiff_H16_random\tnumSitesBigDiff_H17_random\tnumSitesBigDiff_H18_random\tnumSitesBigDiff_H19_random\tnumSitesBigDiff_H20_random\tnumSitesBigDiff_H21 (H2 resolved by ML)\n");
		
		for(int i=1;i<this.maxTrees;i++){
			bufMain.append("\td_lnL_H"+i);
		}
		for(int i=1;i<this.maxTrees;i++){
			bufMain.append("\tdSSLS_H"+i);
		}
		for(int i=1;i<this.maxTrees;i++){
			bufMain.append("\ttSSLS_H"+i);
		}
		for(int i=1;i<this.maxTrees;i++){
			bufMain.append("\tnumThresh_H"+i);
		}
		for(int i=1;i<this.maxTrees;i++){
			bufMain.append("\tKS_tests_"+i+"\tks_D\tks_P_est\t(expected_D;nReps_in_expect)\tCDF_empirical_at_5pc_simulated\tCDF_overlap_0\tCDF_overlap_-1\tCDF_overlap_-2\tCDF_empirical_pc01\tCDF_simulations_pc01\tCDF_empirical_pc05\tCDF_empirical_05");
		}
		bufMain.append("\n");
		bufTree.append("#NEXUS\nbegin trees;\n");

		Pattern simPattern = Pattern.compile("s_");
		Pattern dayPattern = Pattern.compile("dayhoff");
		Pattern jonPattern = Pattern.compile("jones");
		Pattern wagPattern = Pattern.compile("wag");

		HashMap<String, String> lociData = this.initMetadata();
		if(dir.isDirectory()){
			String[] serFilesList = dir.list(serFileFilter);
			int simfiles = 0;
			int runfiles = 0;
			for(String someFile:serFilesList){
				Matcher simMatch = simPattern.matcher(someFile);
				if(simMatch.find()){
					simfiles++;
				}else{
					runfiles++;
				}
			}
			SitewiseSpecificLikelihoodSupportAaml[] results 	= new SitewiseSpecificLikelihoodSupportAaml[runfiles];
			SitewiseSpecificLikelihoodSupportAaml[] simulations = new SitewiseSpecificLikelihoodSupportAaml[simfiles];
			int simfilesIndex = 0;
			int runfilesIndex = 0;
			System.err.println("reading .ser files:");
			for(int i=0; i<serFilesList.length;i++){
				System.err.print(".");
				if(Math.round((double)i/10.0d) == (double)i/10.0d){
					System.err.print(i);
					System.err.println();
				}
				try {
					FileInputStream fileInOne = new FileInputStream(dir.getAbsolutePath()+"/"+serFilesList[i]);
					ObjectInputStream inOne = new ObjectInputStream(fileInOne);
					SitewiseSpecificLikelihoodSupportAaml candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
					Matcher simMatch = simPattern.matcher(serFilesList[i]);
					if(simMatch.find()){
						simulations[simfilesIndex] = candidate;
						simfilesIndex++;
					}else{
						results[runfilesIndex] = candidate;
						runfilesIndex++;
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			String[] models = {"wag","jones","dayhoff"};
			for(String model:models){
				/*
				 * Analyse SIMULATIONS
				 */
				System.err.println("Analyse simulated ÆSSLS");
				double [][] simulatedDeltas = new double[this.maxTrees-1][0];
				for(int i=0;i<simulations.length;i++){
					System.err.print('o');
					if(Math.round((double)i/10.0d) == (double)i/10.0d){
						System.err.print(i);
						System.err.println();
					}
					SitewiseSpecificLikelihoodSupportAaml someRun = simulations[i];
					try {
						if(someRun.getModel().equals(model)){
							int nTax = someRun.getNumberOfTaxa();
							int nSites = someRun.getNumberOfSites();
							float[][] SSLS = someRun.getSSLSseriesSitewise();
							double[][] dSSLS = new double[this.maxTrees-1][nSites];
							for(int j=0;j<nSites;j++){
								for(int k=1;k<this.maxTrees;k++){
									try {
										dSSLS[k-1][j] = SSLS[j][0] - SSLS[j][k];
									} catch (Exception e) {
										// TODO Auto-generated catch block
										dSSLS[k-1][j] = Float.NaN;
										e.printStackTrace();
									}
								}
							}
							for (int k = 0; k < this.maxTrees-1; k++) {
								simulatedDeltas[k] = this.concat(simulatedDeltas[k], dSSLS[k]);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				/*
				 * Analyse SUBSAMLE of SIMULATIONS
				 * Just H1 ÆSSLS for now
				 */
				int totalReps = 0;
				double[] expectedD = new double[this.maxTrees-1];
				System.err.println("Analyse expected KS");
				for(int i=0;i<simulations.length;i++){
					System.err.print('x');
					if(Math.round((double)i/10.0d) == (double)i/10.0d){
						System.err.print(i);
						System.err.println();
					}
					SitewiseSpecificLikelihoodSupportAaml someRun = simulations[i];
					try {
						if(someRun.getModel().equals(model)&&(Math.random()>0.75d)&&(totalReps<31)){
							int nSites = someRun.getNumberOfSites();
							float[][] SSLS = someRun.getSSLSseriesSitewise();
							double[][] dSSLS = new double[this.maxTrees-1][nSites];
							for(int j=0;j<nSites;j++){
								for(int k=1;k<this.maxTrees;k++){
									try {
										dSSLS[k-1][j] = SSLS[j][0] - SSLS[j][k];
									} catch (Exception e) {
										// TODO Auto-generated catch block
										dSSLS[k-1][j] = Float.NaN;
										e.printStackTrace();
									}
								}
							}
							for (int k = 0; k < this.maxTrees-1; k++) {
								expectedD[k] += new PairedEmpirical(dSSLS[k],simulatedDeltas[k]).getKS();
							}
							totalReps++;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				for (int k = 0; k < this.maxTrees-1; k++) {
					expectedD[k] = expectedD[k]/(double)totalReps;
				}

				/*
				 * Analysed OBSERVED sites
				 */
				for(SitewiseSpecificLikelihoodSupportAaml someRun:results){
					try {
						if(someRun.getModel().equals(model)){
							StringBuffer buf = new StringBuffer();
							try {
								float[] alphas = someRun.getAlpha();
								float[] sli = someRun.getLi();
								float[] lengths = someRun.getTreeLengths();

								/*
								 * Hacky quick thing Sat night to check this works 
								 * *really* ought to do in a proper testbed for production code..
								 */
								double[] testDouble = new double[alphas.length];
								for(int counter=0;counter<testDouble.length; counter++){
									testDouble[counter] = alphas[counter];
								}
//								KolmogorovTest ks = new KolmogorovTest(testDouble, new jsc.distributions.Normal(1.0f, 0.25f));
//								double kD = ks.getTestStatistic();	
//								double pD = ks.getSP();
								boolean[] preferred = someRun.getPreferred();
								String[] fittedTrees = someRun.getFittedTrees();
								int nTax = someRun.getNumberOfTaxa();
								int nSites = someRun.getNumberOfSites();

								String[] nameSplit = someRun.getInputFile().getPath().split("_");
								if(lociData.containsKey(nameSplit[5].toUpperCase())){
									System.out.print(lociData.get(nameSplit[5].toUpperCase())+"\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
									buf.append(lociData.get(nameSplit[5].toUpperCase())+"\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
								}else{
									System.out.print(nameSplit[5].toUpperCase()+"\tNA\tNA\tNA\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
									buf.append(nameSplit[5].toUpperCase()+"\tNA\tNA\tNA\t"+someRun.getHomogeneityChiSq()+"\t"+model+"\t"+someRun.getFilterFactor()+"\t"+sli[0]+"\t"+lengths[0]+"\t"+alphas[0]+"\t"+nTax+"\t"+nSites);
								}

								if((fittedTrees.length == this.maxTrees)&&(someRun.getNumberOfTaxa()==22)){
							//		bufTree.append("\ttree "+model+"_"+someRun.getFilterFactor()+"_"+nameSplit[5]+" = [&R] "+fittedTrees[(this.maxTrees-1)]+"\n");	// this is a bit of a fudge, we're looking for the random tree really, if one's not been specified it won't be there...
								}
								//							System.out.println("Fitted topologies: ");
								//							System.out.println("\ttree\talpha\tpSH\tlnL\tlengths\ttopology");
								//								for(int k=0;k<someRun.getNumberOfTopologies();k++){
								//							System.out.print("\t"+sli[k]);
								//								}
								Integer prefTree = null;
								for(int k=0;k<preferred.length;k++){
									if(preferred[k]){
										prefTree = k;
									}
								}
								if(prefTree == null){
									System.out.print("\tNA");
									buf.append("\tNA");
								}else{
									System.out.print("\t"+prefTree);
									buf.append("\t"+prefTree);
								}
								for(int k=1;k<this.maxTrees;k++){
									float dlnL;
									try {
										dlnL = (sli[0]-sli[k]);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										dlnL = Float.NaN;
										e.printStackTrace();
									}
									System.out.print("\t"+dlnL);
									buf.append("\t"+dlnL);
								}
								//							AlignedSequenceRepresentation asr = someRun.getDataset();
								//						asr.printShortSequences(30);
								int[] largeDeltas = new int[(this.maxTrees-1)];
								float[][] SSLS = someRun.getSSLSseriesSitewise();
								float[] lnLsums = new float[someRun.getNumberOfTopologies()];
								BigDecimal[] diffsSummed = new BigDecimal[(this.maxTrees-1)];
								BigDecimal[] diffsSummedThresh = new BigDecimal[(this.maxTrees-1)];
								for(int i=0;i<(this.maxTrees-1);i++){
									diffsSummed[i] = new BigDecimal(0);
									diffsSummedThresh[i] = new BigDecimal(0);
								}
								for(int j=0;j<someRun.getNumberOfSites();j++){
									lnLsums[0] = lnLsums[0] + SSLS[j][0];
									lnLsums[(this.maxTrees-1)] = lnLsums[(this.maxTrees-1)] + SSLS[j][(this.maxTrees-1)];
								}
								if(this.printSites){
									System.out.println();
								}
								double[][] dSSLS = new double[this.maxTrees-1][nSites];
								for(int j=0;j<nSites;j++){
									//								System.out.print(someRun.getSites()[j]);
									for(int k=1;k<this.maxTrees;k++){
										try {
											lnLsums[k] = lnLsums[k] + SSLS[j][k];
											dSSLS[k-1][j] = SSLS[j][0] - SSLS[j][k];
											BigDecimal thisDiffBD = new BigDecimal(SSLS[j][0] - SSLS[j][k]);
											diffsSummed[(k-1)] = diffsSummed[(k-1)].add(thisDiffBD);
											if(Math.abs(thisDiffBD.doubleValue())>0.1){
												diffsSummedThresh[(k-1)] = diffsSummedThresh[(k-1)].add(thisDiffBD);
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											dSSLS[k-1][j] = Float.NaN;
											e.printStackTrace();
										}
										if(dSSLS[k-1][j] < -0.1f){
											largeDeltas[k-1]++;
										}
										if(this.printSites){
											System.out.print("\t"+dSSLS[j][k-1]);
										}
									}
									if(this.printSites){
										System.out.println();
									}
								}
								float[] lnLsumDiffs 	= new float[(this.maxTrees-1)];
								float[] lnLsumAvg 		= new float[(this.maxTrees-1)];
								float[] lnLsumDiffAvgs 	= new float[(this.maxTrees-1)];
								for(int k=0;k<(this.maxTrees-1);k++){
									// Average the ÆSSLS
									BigDecimal avgOfSummedDiffs = diffsSummed[k].divide(new BigDecimal(nSites),RoundingMode.HALF_EVEN);
									String AOSD = String.format("%.16f",avgOfSummedDiffs);
									System.out.print("\t"+AOSD);
									buf.append("\t"+AOSD);
								}
								for(int k=0;k<(this.maxTrees-1);k++){
									// Average the ÆSSLS (threshold-corrected values)
									BigDecimal avgOfSummedDiffs = diffsSummedThresh[k].divide(new BigDecimal(nSites),RoundingMode.HALF_EVEN);
									String AOSD = String.format("%.16f",avgOfSummedDiffs);
									System.out.print("\t"+AOSD);
									buf.append("\t"+AOSD);
								}
								for(int k=0;k<largeDeltas.length;k++){
									lnLsumDiffs[k] = lnLsums[0] - lnLsums[k];
									lnLsumAvg[k] = (lnLsums[k] / nSites);
									lnLsumDiffAvgs[k] = (lnLsumDiffs[k]/nSites);
									System.out.print("\t"+largeDeltas[k]);
									buf.append("\t"+largeDeltas[k]);
								}
								for(int k=0;k<(this.maxTrees-1);k++){
									// Do the KS etc
									PairedEmpirical significance = new PairedEmpirical(dSSLS[k],simulatedDeltas[k]);
									double K  = significance.getKS();
									double p  = significance.getKS_sp();
									double d = significance.getDensity_A_at_percentile_B(0.05d);
									double overlap_0;
									try {
										overlap_0 = significance.getDensityOverlapAt(-0.0d);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										overlap_0 = Double.NaN;
										e.printStackTrace();
									}
									double overlap_1;
									try {
										overlap_1 = significance.getDensityOverlapAt(-1.0d);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										overlap_1 = Double.NaN;
										e.printStackTrace();
									}
									double overlap_2;
									try {
										overlap_2 = significance.getDensityOverlapAt(-2.0d);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										overlap_2 = Double.NaN;
										e.printStackTrace();
									}
									double[] pc_01 = significance.getValuesAtPercentile(0.01d);
									double[] pc_05 = significance.getValuesAtPercentile(0.05d);
									System.out.print("\tt"+k+"\t"+K+"\t"+p+"\t("+expectedD[k]+";"+totalReps+")\t"+d+"\t"+overlap_0+"\t"+overlap_1+"\t"+overlap_2+"\t"+pc_01[0]+"\t"+pc_01[01]+"\t"+pc_05[0]+"\t"+pc_05[1]);
									      buf.append("\tt"+k+"\t"+K+"\t"+p+"\t("+expectedD[k]+";"+totalReps+")\t"+d+"\t"+overlap_0+"\t"+overlap_1+"\t"+overlap_2+"\t"+pc_01[0]+"\t"+pc_01[01]+"\t"+pc_05[0]+"\t"+pc_05[1]);
								}
								System.out.println("");
								buf.append("\r");
								bufMain.append(buf);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			bufTree.append("\nEnd;\n");
			new CustomFileWriter(new File(dir+"/output.txt"),bufMain.toString());
			new CustomFileWriter(new File(dir+"/output.trees"),bufTree.toString());
		}else{
			System.out.println("arg must be a directory");
		}
	}

	public HashMap<String,String> initMetadata(){
		HashMap<String,String> metadata = new HashMap<String,String>();
		String[] unparsedData = {
				"ENSG00000111481	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111481	COPZ1	coatomer protein complex, subunit zeta 1 [Source:HGNC Symbol;Acc:2243]",
				"ENSG00000138002	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138002	IFT172	intraflagellar transport 172 homolog (Chlamydomonas) [Source:HGNC Symbol;Acc:30391]",
				"ENSG00000172845	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172845	SP3	Sp3 transcription factor [Source:HGNC Symbol;Acc:11208]",
				"ENSG00000102218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102218	RP2	retinitis pigmentosa 2 (X-linked recessive) [Source:HGNC Symbol;Acc:10274]",
				"ENSG00000112118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112118	MCM3	minichromosome maintenance complex component 3 [Source:HGNC Symbol;Acc:6945]",
				"ENSG00000079432	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000079432	CIC	capicua homolog (Drosophila) [Source:HGNC Symbol;Acc:14214]",
				"ENSG00000117360	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117360	PRPF3	PRP3 pre-mRNA processing factor 3 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:17348]",
				"ENSG00000081148	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081148	IMPG2	interphotoreceptor matrix proteoglycan 2 [Source:HGNC Symbol;Acc:18362]",
				"ENSG00000213398	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213398	LCAT	lecithin-cholesterol acyltransferase [Source:HGNC Symbol;Acc:6522]",
				"ENSG00000116703	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116703	PDC	phosducin [Source:HGNC Symbol;Acc:8759]",
				"ENSG00000080511	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080511	RDH8	retinol dehydrogenase 8 (all-trans) [Source:HGNC Symbol;Acc:14423]",
				"ENSG00000134982	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134982	APC	adenomatous polyposis coli [Source:HGNC Symbol;Acc:583]",
				"ENSG00000100949	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100949	RABGGTA	Rab geranylgeranyltransferase, alpha subunit [Source:HGNC Symbol;Acc:9795]",
				"ENSG00000180245	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180245	RRH	retinal pigment epithelium-derived rhodopsin homolog [Source:HGNC Symbol;Acc:10450]",
				"ENSG00000152684	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152684	PELO	pelota homolog (Drosophila) [Source:HGNC Symbol;Acc:8829]",
				"ENSG00000070495	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070495	JMJD6	jumonji domain containing 6 [Source:HGNC Symbol;Acc:19355]",
				"ENSG00000113161	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113161	HMGCR	3-hydroxy-3-methylglutaryl-CoA reductase [Source:HGNC Symbol;Acc:5006]",
				"ENSG00000164175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164175	SLC45A2	solute carrier family 45, member 2 [Source:HGNC Symbol;Acc:16472]",
				"ENSG00000163468	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163468	CCT3	chaperonin containing TCP1, subunit 3 (gamma) [Source:HGNC Symbol;Acc:1616]",
				"ENSG00000118402	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118402	ELOVL4	ELOVL fatty acid elongase 4 [Source:HGNC Symbol;Acc:14415]",
				"ENSG00000188158	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188158	NHS	Nance-Horan syndrome (congenital cataracts and dental anomalies) [Source:HGNC Symbol;Acc:7820]",
				"ENSG00000163918	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163918	RFC4	replication factor C (activator 1) 4, 37kDa [Source:HGNC Symbol;Acc:9972]",
				"ENSG00000116745	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116745	RPE65	retinal pigment epithelium-specific protein 65kDa [Source:HGNC Symbol;Acc:10294]",
				"ENSG00000125124	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125124	BBS2	Bardet-Biedl syndrome 2 [Source:HGNC Symbol;Acc:967]",
				"ENSG00000197579	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197579	TOPORS	topoisomerase I binding, arginine/serine-rich, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:21653]",
				"ENSG00000109103	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109103	UNC119	unc-119 homolog (C. elegans) [Source:HGNC Symbol;Acc:12565]",
				"ENSG00000137955	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137955	RABGGTB	Rab geranylgeranyltransferase, beta subunit [Source:HGNC Symbol;Acc:9796]",
				"ENSG00000111049	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111049	MYF5	myogenic factor 5 [Source:HGNC Symbol;Acc:7565]",
				"ENSG00000140463	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140463	BBS4	Bardet-Biedl syndrome 4 [Source:HGNC Symbol;Acc:969]",
				"ENSG00000104142	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104142	VPS18	vacuolar protein sorting 18 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:15972]",
				"ENSG00000120708	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120708	TGFBI	transforming growth factor, beta-induced, 68kDa [Source:HGNC Symbol;Acc:11771]",
				"ENSG00000153147	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153147	SMARCA5	SWI/SNF related, matrix associated, actin dependent regulator of chromatin, subfamily a, member 5 [Source:HGNC Symbol;Acc:11101]",
				"ENSG00000008838	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008838	MED24	mediator complex subunit 24 [Source:HGNC Symbol;Acc:22963]",
				"ENSG00000102805	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102805	CLN5	ceroid-lipofuscinosis, neuronal 5 [Source:HGNC Symbol;Acc:2076]",
				"ENSG00000112742	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112742	TTK	TTK protein kinase [Source:HGNC Symbol;Acc:12401]",
				"ENSG00000113971	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113971	NPHP3	nephronophthisis 3 (adolescent) [Source:HGNC Symbol;Acc:7907]",
				"ENSG00000128050	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128050	PAICS	phosphoribosylaminoimidazole carboxylase, phosphoribosylaminoimidazole succinocarboxamide synthetase [Source:HGNC Symbol;Acc:8587]",
				"ENSG00000135862	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135862	LAMC1	laminin, gamma 1 (formerly LAMB2) [Source:HGNC Symbol;Acc:6492]",
				"ENSG00000136940	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136940	PDCL	phosducin-like [Source:HGNC Symbol;Acc:8770]",
				"ENSG00000184845	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184845	DRD1	dopamine receptor D1 [Source:HGNC Symbol;Acc:3020]",
				"ENSG00000148516	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148516	ZEB1	zinc finger E-box binding homeobox 1 [Source:HGNC Symbol;Acc:11642]",
				"ENSG00000100099	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100099	HPS4	Hermansky-Pudlak syndrome 4 [Source:HGNC Symbol;Acc:15844]",
				"ENSG00000084774	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084774	CAD	carbamoyl-phosphate synthetase 2, aspartate transcarbamylase, and dihydroorotase [Source:HGNC Symbol;Acc:1424]",
				"ENSG00000170214	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170214	ADRA1B	adrenoceptor alpha 1B [Source:HGNC Symbol;Acc:278]",
				"ENSG00000136603	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136603	SKIL	SKI-like oncogene [Source:HGNC Symbol;Acc:10897]",
				"ENSG00000122375	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122375	OPN4	opsin 4 [Source:HGNC Symbol;Acc:14449]",
				"ENSG00000118707	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118707	TGIF2	TGFB-induced factor homeobox 2 [Source:HGNC Symbol;Acc:15764]",
				"ENSG00000170412	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170412	GPRC5C	G protein-coupled receptor, family C, group 5, member C [Source:HGNC Symbol;Acc:13309]",
				"ENSG00000166887	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166887	VPS39	vacuolar protein sorting 39 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:20593]",
				"ENSG00000168036	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168036	CTNNB1	catenin (cadherin-associated protein), beta 1, 88kDa [Source:HGNC Symbol;Acc:2514]",
				"ENSG00000106571	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106571	GLI3	GLI family zinc finger 3 [Source:HGNC Symbol;Acc:4319]",
				"ENSG00000122882	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122882	ECD	ecdysoneless homolog (Drosophila) [Source:HGNC Symbol;Acc:17029]",
				"ENSG00000146166	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146166	LGSN	lengsin, lens protein with glutamine synthetase domain [Source:HGNC Symbol;Acc:21016]",
				"ENSG00000163914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163914	RHO	rhodopsin [Source:HGNC Symbol;Acc:10012]",
				"ENSG00000143493	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143493	INTS7	integrator complex subunit 7 [Source:HGNC Symbol;Acc:24484]",
				"ENSG00000177707	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177707	PVRL3	poliovirus receptor-related 3 [Source:HGNC Symbol;Acc:17664]",
				"ENSG00000104237	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104237	RP1	retinitis pigmentosa 1 (autosomal dominant) [Source:HGNC Symbol;Acc:10263]",
				"ENSG00000109738	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109738	GLRB	glycine receptor, beta [Source:HGNC Symbol;Acc:4329]",
				"ENSG00000123307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123307	NEUROD4	neuronal differentiation 4 [Source:HGNC Symbol;Acc:13802]",
				"ENSG00000092200	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092200	RPGRIP1	retinitis pigmentosa GTPase regulator interacting protein 1 [Source:HGNC Symbol;Acc:13436]",
				"ENSG00000103494	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103494	RPGRIP1L	RPGRIP1-like [Source:HGNC Symbol;Acc:29168]",
				"ENSG00000073111	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000073111	MCM2	minichromosome maintenance complex component 2 [Source:HGNC Symbol;Acc:6944]",
				"ENSG00000119977	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119977	TCTN3	tectonic family member 3 [Source:HGNC Symbol;Acc:24519]",
				"ENSG00000183337	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183337	BCOR	BCL6 corepressor [Source:HGNC Symbol;Acc:20893]",
				"ENSG00000159248	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159248	GJD2	gap junction protein, delta 2, 36kDa [Source:HGNC Symbol;Acc:19154]",
				"ENSG00000158158	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158158	CNNM4	cyclin M4 [Source:HGNC Symbol;Acc:105]",
				"ENSG00000076716	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076716	GPC4	glypican 4 [Source:HGNC Symbol;Acc:4452]",
				"ENSG00000184302	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184302	SIX6	SIX homeobox 6 [Source:HGNC Symbol;Acc:10892]",
				"ENSG00000157404	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157404	KIT	v-kit Hardy-Zuckerman 4 feline sarcoma viral oncogene homolog [Source:HGNC Symbol;Acc:6342]",
				"ENSG00000071553	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071553	ATP6AP1	ATPase, H+ transporting, lysosomal accessory protein 1 [Source:HGNC Symbol;Acc:868]",
				"ENSG00000174231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174231	PRPF8	PRP8 pre-mRNA processing factor 8 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:17340]",
				"ENSG00000076604	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076604	TRAF4	TNF receptor-associated factor 4 [Source:HGNC Symbol;Acc:12034]",
				"ENSG00000007372	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000007372	PAX6	paired box 6 [Source:HGNC Symbol;Acc:8620]",
				"ENSG00000105325	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105325	FZR1	fizzy/cell division cycle 20 related 1 (Drosophila) [Source:HGNC Symbol;Acc:24824]",
				"ENSG00000168610	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168610	STAT3	signal transducer and activator of transcription 3 (acute-phase response factor) [Source:HGNC Symbol;Acc:11364]",
				"ENSG00000TMC1aa	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000TMC1aa	TMC1	TMC1 - Davies et al",
				"ENSG00000Pcdh15	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000Pcdh15	PCDH15	Protocadhedrin 15 - Shen et al",
				"ENSG00000Cdh23a	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000Cdh23a	CDH23	Cadhedrin 23 - Shen et al",
				"ENSG0000PRESTIN	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG0000PRESTIN	SLC26A5	Prestin - cochlear OHC motility protein",
				"ENSG00000KCNQ4a	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000KCNQ4a	KCNQ4	KCNQ4 - Shen et al",
				"ENSG00000Otofaa	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000Otofaa	OTOF	Otoferlin - Shen et al",
				"ENSG00000204311	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204311	DFNB59	deafness, autosomal recessive 59 [Source:HGNC Symbol;Acc:29502]",
				"ENSG00000136160	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136160	EDNRB	endothelin receptor type B [Source:HGNC Symbol;Acc:3180]",
				"ENSG00000100473	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100473	COCH	coagulation factor C homolog, cochlin (Limulus polyphemus) [Source:HGNC Symbol;Acc:2180]",
				"ENSG00000117707	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117707	PROX1	prospero homeobox 1 [Source:HGNC Symbol;Acc:9459]",
				"ENSG00000077498	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000077498	TYR	tyrosinase (oculocutaneous albinism IA) [Source:HGNC Symbol;Acc:12442]",
				"ENSG00000125863	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125863	MKKS	McKusick-Kaufman syndrome [Source:HGNC Symbol;Acc:7108]",
				"ENSG00000198836	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198836	OPA1	optic atrophy 1 (autosomal dominant) [Source:HGNC Symbol;Acc:8140]",
				"ENSG00000088836	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088836	SLC4A11	solute carrier family 4, sodium borate transporter, member 11 [Source:HGNC Symbol;Acc:16438]",
				"ENSG00000125447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125447	GGA3	golgi-associated, gamma adaptin ear containing, ARF binding protein 3 [Source:HGNC Symbol;Acc:17079]",
				"ENSG00000163161	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163161	ERCC3	excision repair cross-complementing rodent repair deficiency, complementation group 3 [Source:HGNC Symbol;Acc:3435]",
				"ENSG00000154309	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154309	DISP1	dispatched homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:19711]",
				"ENSG00000089818	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089818	NECAP1	NECAP endocytosis associated 1 [Source:HGNC Symbol;Acc:24539]",
				"ENSG00000138081	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138081	FBXO11	F-box protein 11 [Source:HGNC Symbol;Acc:13590]",
				"ENSG00000176697	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176697	BDNF	brain-derived neurotrophic factor [Source:HGNC Symbol;Acc:1033]",
				"ENSG00000164930	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164930	FZD6	frizzled family receptor 6 [Source:HGNC Symbol;Acc:4044]",
				"ENSG00000101384	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101384	JAG1	jagged 1 [Source:HGNC Symbol;Acc:6188]",
				"ENSG00000105991	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105991	HOXA1	homeobox A1 [Source:HGNC Symbol;Acc:5099]",
				"ENSG00000115207	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115207	GTF3C2	general transcription factor IIIC, polypeptide 2, beta 110kDa [Source:HGNC Symbol;Acc:4665]",
				"ENSG00000136156	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136156	ITM2B	integral membrane protein 2B [Source:HGNC Symbol;Acc:6174]",
				"ENSG00000163666	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163666	HESX1	HESX homeobox 1 [Source:HGNC Symbol;Acc:4877]",
				"ENSG00000143393	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143393	PI4KB	phosphatidylinositol 4-kinase, catalytic, beta [Source:HGNC Symbol;Acc:8984]",
				"ENSG00000186575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186575	NF2	neurofibromin 2 (merlin) [Source:HGNC Symbol;Acc:7773]",
				"ENSG00000167524	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167524	SGK494	uncharacterized serine/threonine-protein kinase SgK494  [Source:RefSeq peptide;Acc:NP_001167574]",
				"ENSG00000173210	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173210	ABLIM3	actin binding LIM protein family, member 3 [Source:HGNC Symbol;Acc:29132]",
				"ENSG00000108424	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108424	KPNB1	karyopherin (importin) beta 1 [Source:HGNC Symbol;Acc:6400]",
				"ENSG00000100201	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100201	DDX17	DEAD (Asp-Glu-Ala-Asp) box helicase 17 [Source:HGNC Symbol;Acc:2740]",
				"ENSG00000221914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221914	PPP2R2A	protein phosphatase 2, regulatory subunit B, alpha [Source:HGNC Symbol;Acc:9304]",
				"ENSG00000103194	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103194	USP10	ubiquitin specific peptidase 10 [Source:HGNC Symbol;Acc:12608]",
				"ENSG00000169764	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169764	UGP2	UDP-glucose pyrophosphorylase 2 [Source:HGNC Symbol;Acc:12527]",
				"ENSG00000100036	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100036	SLC35E4	solute carrier family 35, member E4 [Source:HGNC Symbol;Acc:17058]",
				"ENSG00000111596	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111596	CNOT2	CCR4-NOT transcription complex, subunit 2 [Source:HGNC Symbol;Acc:7878]",
				"ENSG00000135341	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135341	MAP3K7	mitogen-activated protein kinase kinase kinase 7 [Source:HGNC Symbol;Acc:6859]",
				"ENSG00000115524	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115524	SF3B1	splicing factor 3b, subunit 1, 155kDa [Source:HGNC Symbol;Acc:10768]",
				"ENSG00000168439	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168439	STIP1	stress-induced-phosphoprotein 1 [Source:HGNC Symbol;Acc:11387]",
				"ENSG00000138668	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138668	HNRNPD	heterogeneous nuclear ribonucleoprotein D (AU-rich element RNA binding protein 1, 37kDa) [Source:HGNC Symbol;Acc:5036]",
				"ENSG00000105866	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105866	SP4	Sp4 transcription factor [Source:HGNC Symbol;Acc:11209]",
				"ENSG00000162236	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162236	STX5	syntaxin 5 [Source:HGNC Symbol;Acc:11440]",
				"ENSG00000198728	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198728	LDB1	LIM domain binding 1 [Source:HGNC Symbol;Acc:6532]",
				"ENSG00000221838	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221838	AP4M1	adaptor-related protein complex 4, mu 1 subunit [Source:HGNC Symbol;Acc:574]",
				"ENSG00000141385	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141385	AFG3L2	AFG3 ATPase family gene 3-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:315]",
				"ENSG00000111652	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111652	COPS7A	COP9 constitutive photomorphogenic homolog subunit 7A (Arabidopsis) [Source:HGNC Symbol;Acc:16758]",
				"ENSG00000167193	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167193	CRK	v-crk sarcoma virus CT10 oncogene homolog (avian) [Source:HGNC Symbol;Acc:2362]",
				"ENSG00000035403	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000035403	VCL	vinculin [Source:HGNC Symbol;Acc:12665]",
				"ENSG00000108055	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108055	SMC3	structural maintenance of chromosomes 3 [Source:HGNC Symbol;Acc:2468]",
				"ENSG00000142025	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000142025	DMRTC2	DMRT-like family C2 [Source:HGNC Symbol;Acc:13911]",
				"ENSG00000122741	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122741	DCAF10	DDB1 and CUL4 associated factor 10 [Source:HGNC Symbol;Acc:23686]",
				"ENSG00000103035	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103035	PSMD7	proteasome (prosome, macropain) 26S subunit, non-ATPase, 7 [Source:HGNC Symbol;Acc:9565]",
				"ENSG00000134343	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134343	ANO3	anoctamin 3 [Source:HGNC Symbol;Acc:14004]",
				"ENSG00000113716	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113716	HMGXB3	HMG box domain containing 3 [Source:HGNC Symbol;Acc:28982]",
				"ENSG00000125347	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125347	IRF1	interferon regulatory factor 1 [Source:HGNC Symbol;Acc:6116]",
				"ENSG00000004897	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004897	CDC27	cell division cycle 27 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1728]",
				"ENSG00000244038	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000244038	DDOST	dolichyl-diphosphooligosaccharide--protein glycosyltransferase [Source:HGNC Symbol;Acc:2728]",
				"ENSG00000161057	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161057	PSMC2	proteasome (prosome, macropain) 26S subunit, ATPase, 2 [Source:HGNC Symbol;Acc:9548]",
				"ENSG00000123570	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123570	RAB9B	RAB9B, member RAS oncogene family [Source:HGNC Symbol;Acc:14090]",
				"ENSG00000158435	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158435	C2orf29	chromosome 2 open reading frame 29 [Source:HGNC Symbol;Acc:25217]",
				"ENSG00000119318	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119318	RAD23B	RAD23 homolog B (S. cerevisiae) [Source:HGNC Symbol;Acc:9813]",
				"ENSG00000155506	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155506	LARP1	La ribonucleoprotein domain family, member 1 [Source:HGNC Symbol;Acc:29531]",
				"ENSG00000066117	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000066117	SMARCD1	SWI/SNF related, matrix associated, actin dependent regulator of chromatin, subfamily d, member 1 [Source:HGNC Symbol;Acc:11106]",
				"ENSG00000111554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111554	MDM1	Mdm1 nuclear protein homolog (mouse) [Source:HGNC Symbol;Acc:29917]",
				"ENSG00000175084	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175084	DES	desmin [Source:HGNC Symbol;Acc:2770]",
				"ENSG00000107371	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107371	EXOSC3	exosome component 3 [Source:HGNC Symbol;Acc:17944]",
				"ENSG00000164270	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164270	HTR4	5-hydroxytryptamine (serotonin) receptor 4, G protein-coupled [Source:HGNC Symbol;Acc:5299]",
				"ENSG00000167216	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167216	KATNAL2	katanin p60 subunit A-like 2 [Source:HGNC Symbol;Acc:25387]",
				"ENSG00000129226	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129226	CD68	CD68 molecule [Source:HGNC Symbol;Acc:1693]",
				"ENSG00000158636	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158636	C11orf30	chromosome 11 open reading frame 30 [Source:HGNC Symbol;Acc:18071]",
				"ENSG00000157500	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157500	APPL1	adaptor protein, phosphotyrosine interaction, PH domain and leucine zipper containing 1 [Source:HGNC Symbol;Acc:24035]",
				"ENSG00000104812	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104812	GYS1	glycogen synthase 1 (muscle) [Source:HGNC Symbol;Acc:4706]",
				"ENSG00000139182	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139182	CLSTN3	calsyntenin 3 [Source:HGNC Symbol;Acc:18371]",
				"ENSG00000100220	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100220	C22orf28	chromosome 22 open reading frame 28 [Source:HGNC Symbol;Acc:26935]",
				"ENSG00000102901	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102901	CENPT	centromere protein T [Source:HGNC Symbol;Acc:25787]",
				"ENSG00000198105	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198105	ZNF248	zinc finger protein 248 [Source:HGNC Symbol;Acc:13041]",
				"ENSG00000167595	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167595	C19orf55	chromosome 19 open reading frame 55 [Source:HGNC Symbol;Acc:25204]",
				"ENSG00000162882	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162882	HAAO	3-hydroxyanthranilate 3,4-dioxygenase [Source:HGNC Symbol;Acc:4796]",
				"ENSG00000106780	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106780	MEGF9	multiple EGF-like-domains 9 [Source:HGNC Symbol;Acc:3234]",
				"ENSG00000170385	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170385	SLC30A1	solute carrier family 30 (zinc transporter), member 1 [Source:HGNC Symbol;Acc:11012]",
				"ENSG00000148154	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148154	UGCG	UDP-glucose ceramide glucosyltransferase [Source:HGNC Symbol;Acc:12524]",
				"ENSG00000066185	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000066185	ZMYND12	zinc finger, MYND-type containing 12 [Source:HGNC Symbol;Acc:21192]",
				"ENSG00000080822	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080822	CLDND1	claudin domain containing 1 [Source:HGNC Symbol;Acc:1322]",
				"ENSG00000006047	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006047	YBX2	Y box binding protein 2 [Source:HGNC Symbol;Acc:17948]",
				"ENSG00000146409	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146409	SLC18B1	solute carrier family 18, subfamily B, member 1 [Source:HGNC Symbol;Acc:21573]",
				"ENSG00000115020	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115020	PIKFYVE	phosphoinositide kinase, FYVE finger containing [Source:HGNC Symbol;Acc:23785]",
				"ENSG00000090539	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090539	CHRD	chordin [Source:HGNC Symbol;Acc:1949]",
				"ENSG00000023909	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000023909	GCLM	glutamate-cysteine ligase, modifier subunit [Source:HGNC Symbol;Acc:4312]",
				"ENSG00000182199	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182199	SHMT2	serine hydroxymethyltransferase 2 (mitochondrial) [Source:HGNC Symbol;Acc:10852]",
				"ENSG00000129315	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129315	CCNT1	cyclin T1 [Source:HGNC Symbol;Acc:1599]",
				"ENSG00000145194	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145194	ECE2	endothelin converting enzyme 2 [Source:HGNC Symbol;Acc:13275]",
				"ENSG00000180353	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180353	HCLS1	hematopoietic cell-specific Lyn substrate 1 [Source:HGNC Symbol;Acc:4844]",
				"ENSG00000064703	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064703	DDX20	DEAD (Asp-Glu-Ala-Asp) box polypeptide 20 [Source:HGNC Symbol;Acc:2743]",
				"ENSG00000143578	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143578	CREB3L4	cAMP responsive element binding protein 3-like 4 [Source:HGNC Symbol;Acc:18854]",
				"ENSG00000112242	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112242	E2F3	E2F transcription factor 3 [Source:HGNC Symbol;Acc:3115]",
				"ENSG00000167778	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167778	SPRYD3	SPRY domain containing 3 [Source:HGNC Symbol;Acc:25920]",
				"ENSG00000163636	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163636	PSMD6	proteasome (prosome, macropain) 26S subunit, non-ATPase, 6 [Source:HGNC Symbol;Acc:9564]",
				"ENSG00000154727	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154727	GABPA	GA binding protein transcription factor, alpha subunit 60kDa [Source:HGNC Symbol;Acc:4071]",
				"ENSG00000137824	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137824	FAM82A2	family with sequence similarity 82, member A2 [Source:HGNC Symbol;Acc:25550]",
				"ENSG00000232119	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000232119	MCTS1	malignant T cell amplified sequence 1 [Source:HGNC Symbol;Acc:23357]",
				"ENSG00000168291	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168291	PDHB	pyruvate dehydrogenase (lipoamide) beta [Source:HGNC Symbol;Acc:8808]",
				"ENSG00000184313	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184313	HEATR8	HEAT repeat containing 8 [Source:HGNC Symbol;Acc:24802]",
				"ENSG00000101624	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101624	CEP76	centrosomal protein 76kDa [Source:HGNC Symbol;Acc:25727]",
				"ENSG00000170085	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170085	C5orf25	chromosome 5 open reading frame 25 [Source:HGNC Symbol;Acc:24779]",
				"ENSG00000003056	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000003056	M6PR	mannose-6-phosphate receptor (cation dependent) [Source:HGNC Symbol;Acc:6752]",
				"ENSG00000164933	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164933	SLC25A32	solute carrier family 25 (mitochondrial folate carrier) , member 32 [Source:HGNC Symbol;Acc:29683]",
				"ENSG00000142867	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000142867	BCL10	B-cell CLL/lymphoma 10 [Source:HGNC Symbol;Acc:989]",
				"ENSG00000163527	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163527	STT3B	STT3, subunit of the oligosaccharyltransferase complex, homolog B (S. cerevisiae) [Source:HGNC Symbol;Acc:30611]",
				"ENSG00000174842	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174842	GLMN	glomulin, FKBP associated protein [Source:HGNC Symbol;Acc:14373]",
				"ENSG00000138802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138802	SEC24B	SEC24 family, member B (S. cerevisiae) [Source:HGNC Symbol;Acc:10704]",
				"ENSG00000166889	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166889	PATL1	protein associated with topoisomerase II homolog 1 (yeast) [Source:HGNC Symbol;Acc:26721]",
				"ENSG00000186889	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186889	TMEM17	transmembrane protein 17 [Source:HGNC Symbol;Acc:26623]",
				"ENSG00000094755	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000094755	GABRP	gamma-aminobutyric acid (GABA) A receptor, pi [Source:HGNC Symbol;Acc:4089]",
				"ENSG00000008516	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008516	MMP25	matrix metallopeptidase 25 [Source:HGNC Symbol;Acc:14246]",
				"ENSG00000178301	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178301	AQP11	aquaporin 11 [Source:HGNC Symbol;Acc:19940]",
				"ENSG00000162972	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162972	C2orf47	chromosome 2 open reading frame 47 [Source:HGNC Symbol;Acc:26198]",
				"ENSG00000058063	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000058063	ATP11B	ATPase, class VI, type 11B [Source:HGNC Symbol;Acc:13553]",
				"ENSG00000067596	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000067596	DHX8	DEAH (Asp-Glu-Ala-His) box polypeptide 8 [Source:HGNC Symbol;Acc:2749]",
				"ENSG00000134294	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134294	SLC38A2	solute carrier family 38, member 2 [Source:HGNC Symbol;Acc:13448]",
				"ENSG00000106692	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106692	FKTN	fukutin [Source:HGNC Symbol;Acc:3622]",
				"ENSG00000100580	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100580	TMED8	transmembrane emp24 protein transport domain containing 8 [Source:HGNC Symbol;Acc:18633]",
				"ENSG00000094914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000094914	AAAS	achalasia, adrenocortical insufficiency, alacrimia [Source:HGNC Symbol;Acc:13666]",
				"ENSG00000119862	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119862	LGALSL	lectin, galactoside-binding-like [Source:HGNC Symbol;Acc:25012]",
				"ENSG00000124523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124523	SIRT5	sirtuin 5 [Source:HGNC Symbol;Acc:14933]",
				"ENSG00000137817	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137817	PARP6	poly (ADP-ribose) polymerase family, member 6 [Source:HGNC Symbol;Acc:26921]",
				"ENSG00000221955	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221955	SLC12A8	solute carrier family 12 (potassium/chloride transporters), member 8 [Source:HGNC Symbol;Acc:15595]",
				"ENSG00000023445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000023445	BIRC3	baculoviral IAP repeat containing 3 [Source:HGNC Symbol;Acc:591]",
				"ENSG00000149262	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149262	INTS4	integrator complex subunit 4 [Source:HGNC Symbol;Acc:25048]",
				"ENSG00000136305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136305	CIDEB	cell death-inducing DFFA-like effector b [Source:HGNC Symbol;Acc:1977]",
				"ENSG00000083097	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000083097	DOPEY1	dopey family member 1 [Source:HGNC Symbol;Acc:21194]",
				"ENSG00000179044	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179044	EXOC3L1	exocyst complex component 3-like 1 [Source:HGNC Symbol;Acc:27540]",
				"ENSG00000010322	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010322	NISCH	nischarin [Source:HGNC Symbol;Acc:18006]",
				"ENSG00000196419	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196419	XRCC6	X-ray repair complementing defective repair in Chinese hamster cells 6 [Source:HGNC Symbol;Acc:4055]",
				"ENSG00000113643	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113643	RARS	arginyl-tRNA synthetase [Source:HGNC Symbol;Acc:9870]",
				"ENSG00000116005	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116005	PCYOX1	prenylcysteine oxidase 1 [Source:HGNC Symbol;Acc:20588]",
				"ENSG00000166436	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166436	TRIM66	tripartite motif containing 66 [Source:HGNC Symbol;Acc:29005]",
				"ENSG00000162928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162928	PEX13	peroxisomal biogenesis factor 13 [Source:HGNC Symbol;Acc:8855]",
				"ENSG00000163694	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163694	RBM47	RNA binding motif protein 47 [Source:HGNC Symbol;Acc:30358]",
				"ENSG00000173809	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173809	TDRD12	tudor domain containing 12 [Source:HGNC Symbol;Acc:25044]",
				"ENSG00000151881	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151881	C5orf28	chromosome 5 open reading frame 28 [Source:HGNC Symbol;Acc:26139]",
				"ENSG00000105612	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105612	DNASE2	deoxyribonuclease II, lysosomal [Source:HGNC Symbol;Acc:2960]",
				"ENSG00000115649	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115649	CNPPD1	cyclin Pas1/PHO80 domain containing 1 [Source:HGNC Symbol;Acc:25220]",
				"ENSG00000072952	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072952	MRVI1	murine retrovirus integration site 1 homolog [Source:HGNC Symbol;Acc:7237]",
				"ENSG00000128595	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128595	CALU	calumenin [Source:HGNC Symbol;Acc:1458]",
				"ENSG00000157060	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157060	SHCBP1L	SHC SH2-domain binding protein 1-like [Source:HGNC Symbol;Acc:16788]",
				"ENSG00000172197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172197	MBOAT1	membrane bound O-acyltransferase domain containing 1 [Source:HGNC Symbol;Acc:21579]",
				"ENSG00000084070	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084070	SMAP2	small ArfGAP2 [Source:HGNC Symbol;Acc:25082]",
				"ENSG00000108883	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108883	EFTUD2	elongation factor Tu GTP binding domain containing 2 [Source:HGNC Symbol;Acc:30858]",
				"ENSG00000119711	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119711	ALDH6A1	aldehyde dehydrogenase 6 family, member A1 [Source:HGNC Symbol;Acc:7179]",
				"ENSG00000014914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000014914	MTMR11	myotubularin related protein 11 [Source:HGNC Symbol;Acc:24307]",
				"ENSG00000186104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186104	CYP2R1	cytochrome P450, family 2, subfamily R, polypeptide 1 [Source:HGNC Symbol;Acc:20580]",
				"ENSG00000087191	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000087191	PSMC5	proteasome (prosome, macropain) 26S subunit, ATPase, 5 [Source:HGNC Symbol;Acc:9552]",
				"ENSG00000129170	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129170	CSRP3	cysteine and glycine-rich protein 3 (cardiac LIM protein) [Source:HGNC Symbol;Acc:2472]",
				"ENSG00000147596	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147596	PRDM14	PR domain containing 14 [Source:HGNC Symbol;Acc:14001]",
				"ENSG00000105771	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105771	SMG9	smg-9 homolog, nonsense mediated mRNA decay factor (C. elegans) [Source:HGNC Symbol;Acc:25763]",
				"ENSG00000123737	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123737	EXOSC9	exosome component 9 [Source:HGNC Symbol;Acc:9137]",
				"ENSG00000101158	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101158	TH1L	TH1-like (Drosophila) [Source:HGNC Symbol;Acc:15934]",
				"ENSG00000143390	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143390	RFX5	regulatory factor X, 5 (influences HLA class II expression) [Source:HGNC Symbol;Acc:9986]",
				"ENSG00000136021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136021	SCYL2	SCY1-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:19286]",
				"ENSG00000112146	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112146	FBXO9	F-box protein 9 [Source:HGNC Symbol;Acc:13588]",
				"ENSG00000151500	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151500	THYN1	thymocyte nuclear protein 1 [Source:HGNC Symbol;Acc:29560]",
				"ENSG00000240771	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000240771	ARHGEF25	Rho guanine nucleotide exchange factor (GEF) 25 [Source:HGNC Symbol;Acc:30275]",
				"ENSG00000159479	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159479	MED8	mediator complex subunit 8 [Source:HGNC Symbol;Acc:19971]",
				"ENSG00000159322	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159322	ADPGK	ADP-dependent glucokinase [Source:HGNC Symbol;Acc:25250]",
				"ENSG00000155755	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155755	TMEM237	transmembrane protein 237 [Source:HGNC Symbol;Acc:14432]",
				"ENSG00000161800	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161800	RACGAP1	Rac GTPase activating protein 1 [Source:HGNC Symbol;Acc:9804]",
				"ENSG00000135898	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135898	GPR55	G protein-coupled receptor 55 [Source:HGNC Symbol;Acc:4511]",
				"ENSG00000125875	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125875	TBC1D20	TBC1 domain family, member 20 [Source:HGNC Symbol;Acc:16133]",
				"ENSG00000162039	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162039	C16orf73	chromosome 16 open reading frame 73 [Source:HGNC Symbol;Acc:28569]",
				"ENSG00000105290	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105290	APLP1	amyloid beta (A4) precursor-like protein 1 [Source:HGNC Symbol;Acc:597]",
				"ENSG00000010671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010671	BTK	Bruton agammaglobulinemia tyrosine kinase [Source:HGNC Symbol;Acc:1133]",
				"ENSG00000079785	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000079785	DDX1	DEAD (Asp-Glu-Ala-Asp) box helicase 1 [Source:HGNC Symbol;Acc:2734]",
				"ENSG00000146828	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146828	SLC12A9	solute carrier family 12 (potassium/chloride transporters), member 9 [Source:HGNC Symbol;Acc:17435]",
				"ENSG00000213699	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213699	C2orf18	chromosome 2 open reading frame 18 [Source:HGNC Symbol;Acc:26055]",
				"ENSG00000163217	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163217	BMP10	bone morphogenetic protein 10 [Source:HGNC Symbol;Acc:20869]",
				"ENSG00000076650	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076650	GPATCH1	G patch domain containing 1 [Source:HGNC Symbol;Acc:24658]",
				"ENSG00000113013	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113013	HSPA9	heat shock 70kDa protein 9 (mortalin) [Source:HGNC Symbol;Acc:5244]",
				"ENSG00000159921	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159921	GNE	glucosamine (UDP-N-acetyl)-2-epimerase/N-acetylmannosamine kinase [Source:HGNC Symbol;Acc:23657]",
				"ENSG00000111684	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111684	LPCAT3	lysophosphatidylcholine acyltransferase 3 [Source:HGNC Symbol;Acc:30244]",
				"ENSG00000180336	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180336	C17orf104	chromosome 17 open reading frame 104 [Source:HGNC Symbol;Acc:26670]",
				"ENSG00000104760	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104760	FGL1	fibrinogen-like 1 [Source:HGNC Symbol;Acc:3695]",
				"ENSG00000070759	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070759	TESK2	testis-specific kinase 2 [Source:HGNC Symbol;Acc:11732]",
				"ENSG00000120802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120802	TMPO	thymopoietin [Source:HGNC Symbol;Acc:11875]",
				"ENSG00000115392	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115392	FANCL	Fanconi anemia, complementation group L [Source:HGNC Symbol;Acc:20748]",
				"ENSG00000183798	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183798	EMILIN3	elastin microfibril interfacer 3 [Source:HGNC Symbol;Acc:16123]",
				"ENSG00000152661	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152661	GJA1	gap junction protein, alpha 1, 43kDa [Source:HGNC Symbol;Acc:4274]",
				"ENSG00000078177	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000078177	N4BP2	NEDD4 binding protein 2 [Source:HGNC Symbol;Acc:29851]",
				"ENSG00000176974	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176974	SHMT1	serine hydroxymethyltransferase 1 (soluble) [Source:HGNC Symbol;Acc:10850]",
				"ENSG00000131375	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131375	CAPN7	calpain 7 [Source:HGNC Symbol;Acc:1484]",
				"ENSG00000181092	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181092	ADIPOQ	adiponectin, C1Q and collagen domain containing [Source:HGNC Symbol;Acc:13633]",
				"ENSG00000139291	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139291	TMEM19	transmembrane protein 19 [Source:HGNC Symbol;Acc:25605]",
				"ENSG00000124207	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124207	CSE1L	CSE1 chromosome segregation 1-like (yeast) [Source:HGNC Symbol;Acc:2431]",
				"ENSG00000134061	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134061	CD180	CD180 molecule [Source:HGNC Symbol;Acc:6726]",
				"ENSG00000010318	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010318	PHF7	PHD finger protein 7 [Source:HGNC Symbol;Acc:18458]",
				"ENSG00000138135	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138135	CH25H	cholesterol 25-hydroxylase [Source:HGNC Symbol;Acc:1907]",
				"ENSG00000177938	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177938	CAPZA3	capping protein (actin filament) muscle Z-line, alpha 3 [Source:HGNC Symbol;Acc:24205]",
				"ENSG00000132623	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132623	ANKRD5	ankyrin repeat domain 5 [Source:HGNC Symbol;Acc:15803]",
				"ENSG00000151151	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151151	IPMK	inositol polyphosphate multikinase [Source:HGNC Symbol;Acc:20739]",
				"ENSG00000165694	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165694	FRMD7	FERM domain containing 7 [Source:HGNC Symbol;Acc:8079]",
				"ENSG00000114107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114107	CEP70	centrosomal protein 70kDa [Source:HGNC Symbol;Acc:29972]",
				"ENSG00000141736	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141736	ERBB2	v-erb-b2 erythroblastic leukemia viral oncogene homolog 2, neuro/glioblastoma derived oncogene homolog (avian) [Source:HGNC Symbol;Acc:3430]",
				"ENSG00000183258	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183258	DDX41	DEAD (Asp-Glu-Ala-Asp) box polypeptide 41 [Source:HGNC Symbol;Acc:18674]",
				"ENSG00000143028	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143028	SYPL2	synaptophysin-like 2 [Source:HGNC Symbol;Acc:27638]",
				"ENSG00000176422	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176422	SPRYD4	SPRY domain containing 4 [Source:HGNC Symbol;Acc:27468]",
				"ENSG00000115325	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115325	DOK1	docking protein 1, 62kDa (downstream of tyrosine kinase 1) [Source:HGNC Symbol;Acc:2990]",
				"ENSG00000132768	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132768	DPH2	DPH2 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:3004]",
				"ENSG00000154222	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154222	CC2D1B	coiled-coil and C2 domain containing 1B [Source:HGNC Symbol;Acc:29386]",
				"ENSG00000116771	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116771	AGMAT	agmatine ureohydrolase (agmatinase) [Source:HGNC Symbol;Acc:18407]",
				"ENSG00000139266	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139266	38411	membrane-associated ring finger (C3HC4) 9 [Source:HGNC Symbol;Acc:25139]",
				"ENSG00000137868	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137868	STRA6	stimulated by retinoic acid gene 6 homolog (mouse) [Source:HGNC Symbol;Acc:30650]",
				"ENSG00000162923	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162923	WDR26	WD repeat domain 26 [Source:HGNC Symbol;Acc:21208]",
				"ENSG00000153827	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153827	TRIP12	thyroid hormone receptor interactor 12 [Source:HGNC Symbol;Acc:12306]",
				"ENSG00000114982	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114982	KANSL3	KAT8 regulatory NSL complex subunit 3 [Source:HGNC Symbol;Acc:25473]",
				"ENSG00000109171	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109171	SLAIN2	SLAIN motif family, member 2 [Source:HGNC Symbol;Acc:29282]",
				"ENSG00000103429	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103429	BFAR	bifunctional apoptosis regulator [Source:HGNC Symbol;Acc:17613]",
				"ENSG00000127995	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127995	CASD1	CAS1 domain containing 1 [Source:HGNC Symbol;Acc:16014]",
				"ENSG00000174080	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174080	CTSF	cathepsin F [Source:HGNC Symbol;Acc:2531]",
				"ENSG00000039600	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000039600	SOX30	SRY (sex determining region Y)-box 30 [Source:HGNC Symbol;Acc:30635]",
				"ENSG00000180251	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180251	SLC9A4	solute carrier family 9, subfamily A (NHE4, cation proton antiporter 4), member 4 [Source:HGNC Symbol;Acc:11077]",
				"ENSG00000101782	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101782	RIOK3	RIO kinase 3 (yeast) [Source:HGNC Symbol;Acc:11451]",
				"ENSG00000110871	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110871	COQ5	coenzyme Q5 homolog, methyltransferase (S. cerevisiae) [Source:HGNC Symbol;Acc:28722]",
				"ENSG00000197296	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197296	FITM2	fat storage-inducing transmembrane protein 2 [Source:HGNC Symbol;Acc:16135]",
				"ENSG00000169554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169554	ZEB2	zinc finger E-box binding homeobox 2 [Source:HGNC Symbol;Acc:14881]",
				"ENSG00000121690	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121690	DEPDC7	DEP domain containing 7 [Source:HGNC Symbol;Acc:29899]",
				"ENSG00000179476	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179476	C14orf28	chromosome 14 open reading frame 28 [Source:HGNC Symbol;Acc:19834]",
				"ENSG00000155636	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155636	RBM45	RNA binding motif protein 45 [Source:HGNC Symbol;Acc:24468]",
				"ENSG00000008086	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008086	CDKL5	cyclin-dependent kinase-like 5 [Source:HGNC Symbol;Acc:11411]",
				"ENSG00000165195	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165195	PIGA	phosphatidylinositol glycan anchor biosynthesis, class A [Source:HGNC Symbol;Acc:8957]",
				"ENSG00000117305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117305	HMGCL	3-hydroxymethyl-3-methylglutaryl-CoA lyase [Source:HGNC Symbol;Acc:5005]",
				"ENSG00000144021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144021	CIAO1	cytosolic iron-sulfur protein assembly 1 [Source:HGNC Symbol;Acc:14280]",
				"ENSG00000168288	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168288	MMADHC	methylmalonic aciduria (cobalamin deficiency) cblD type, with homocystinuria [Source:HGNC Symbol;Acc:25221]",
				"ENSG00000179912	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179912	R3HDM2	R3H domain containing 2 [Source:HGNC Symbol;Acc:29167]",
				"ENSG00000105401	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105401	CDC37	cell division cycle 37 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1735]",
				"ENSG00000166398	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166398	KIAA0355	KIAA0355 [Source:HGNC Symbol;Acc:29016]",
				"ENSG00000065060	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065060	UHRF1BP1	UHRF1 binding protein 1 [Source:HGNC Symbol;Acc:21216]",
				"ENSG00000177000	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177000	MTHFR	methylenetetrahydrofolate reductase (NAD(P)H) [Source:HGNC Symbol;Acc:7436]",
				"ENSG00000139890	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139890	REM2	RAS (RAD and GEM)-like GTP binding 2 [Source:HGNC Symbol;Acc:20248]",
				"ENSG00000136875	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136875	PRPF4	PRP4 pre-mRNA processing factor 4 homolog (yeast) [Source:HGNC Symbol;Acc:17349]",
				"ENSG00000172568	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172568	FNDC9	fibronectin type III domain containing 9 [Source:HGNC Symbol;Acc:33547]",
				"ENSG00000165379	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165379	LRFN5	leucine rich repeat and fibronectin type III domain containing 5 [Source:HGNC Symbol;Acc:20360]",
				"ENSG00000136122	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136122	BORA	bora, aurora kinase A activator [Source:HGNC Symbol;Acc:24724]",
				"ENSG00000105227	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105227	PRX	periaxin [Source:HGNC Symbol;Acc:13797]",
				"ENSG00000168811	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168811	IL12A	interleukin 12A (natural killer cell stimulatory factor 1, cytotoxic lymphocyte maturation factor 1, p35) [Source:HGNC Symbol;Acc:5969]",
				"ENSG00000083782	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000083782	EPYC	epiphycan [Source:HGNC Symbol;Acc:3053]",
				"ENSG00000144909	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144909	OSBPL11	oxysterol binding protein-like 11 [Source:HGNC Symbol;Acc:16397]",
				"ENSG00000182010	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182010	RTKN2	rhotekin 2 [Source:HGNC Symbol;Acc:19364]",
				"ENSG00000152147	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152147	GEMIN6	gem (nuclear organelle) associated protein 6 [Source:HGNC Symbol;Acc:20044]",
				"ENSG00000141084	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141084	RANBP10	RAN binding protein 10 [Source:HGNC Symbol;Acc:29285]",
				"ENSG00000131779	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131779	PEX11B	peroxisomal biogenesis factor 11 beta [Source:HGNC Symbol;Acc:8853]",
				"ENSG00000157181	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157181	C1orf27	chromosome 1 open reading frame 27 [Source:HGNC Symbol;Acc:24299]",
				"ENSG00000178922	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178922	HYI	hydroxypyruvate isomerase (putative) [Source:HGNC Symbol;Acc:26948]",
				"ENSG00000180332	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180332	KCTD4	potassium channel tetramerisation domain containing 4 [Source:HGNC Symbol;Acc:23227]",
				"ENSG00000163795	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163795	ZNF513	zinc finger protein 513 [Source:HGNC Symbol;Acc:26498]",
				"ENSG00000128342	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128342	LIF	leukemia inhibitory factor [Source:HGNC Symbol;Acc:6596]",
				"ENSG00000135677	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135677	GNS	glucosamine (N-acetyl)-6-sulfatase [Source:HGNC Symbol;Acc:4422]",
				"ENSG00000079462	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000079462	PAFAH1B3	platelet-activating factor acetylhydrolase 1b, catalytic subunit 3 (29kDa) [Source:HGNC Symbol;Acc:8576]",
				"ENSG00000136152	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136152	COG3	component of oligomeric golgi complex 3 [Source:HGNC Symbol;Acc:18619]",
				"ENSG00000170312	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170312	CDK1	cyclin-dependent kinase 1 [Source:HGNC Symbol;Acc:1722]",
				"ENSG00000103671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103671	TRIP4	thyroid hormone receptor interactor 4 [Source:HGNC Symbol;Acc:12310]",
				"ENSG00000116741	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116741	RGS2	regulator of G-protein signaling 2, 24kDa [Source:HGNC Symbol;Acc:9998]",
				"ENSG00000182923	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182923	CEP63	centrosomal protein 63kDa [Source:HGNC Symbol;Acc:25815]",
				"ENSG00000107581	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107581	EIF3A	eukaryotic translation initiation factor 3, subunit A [Source:HGNC Symbol;Acc:3271]",
				"ENSG00000107443	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107443	CCNJ	cyclin J [Source:HGNC Symbol;Acc:23434]",
				"ENSG00000151746	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151746	BICD1	bicaudal D homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:1049]",
				"ENSG00000186141	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186141	POLR3C	polymerase (RNA) III (DNA directed) polypeptide C (62kD) [Source:HGNC Symbol;Acc:30076]",
				"ENSG00000163702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163702	IL17RC	interleukin 17 receptor C [Source:HGNC Symbol;Acc:18358]",
				"ENSG00000139637	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139637	C12orf10	chromosome 12 open reading frame 10 [Source:HGNC Symbol;Acc:17590]",
				"ENSG00000204130	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204130	RUFY2	RUN and FYVE domain containing 2 [Source:HGNC Symbol;Acc:19761]",
				"ENSG00000167264	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167264	DUS2L	dihydrouridine synthase 2-like, SMM1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:26014]",
				"ENSG00000122735	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122735	DNAI1	dynein, axonemal, intermediate chain 1 [Source:HGNC Symbol;Acc:2954]",
				"ENSG00000104880	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104880	ARHGEF18	Rho/Rac guanine nucleotide exchange factor (GEF) 18 [Source:HGNC Symbol;Acc:17090]",
				"ENSG00000125772	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125772	GPCPD1	glycerophosphocholine phosphodiesterase GDE1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:26957]",
				"ENSG00000154781	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154781	C3orf19	chromosome 3 open reading frame 19 [Source:HGNC Symbol;Acc:28033]",
				"ENSG00000125895	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125895	TMEM74B	transmembrane protein 74B [Source:HGNC Symbol;Acc:15893]",
				"ENSG00000196305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196305	IARS	isoleucyl-tRNA synthetase [Source:HGNC Symbol;Acc:5330]",
				"ENSG00000119139	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119139	TJP2	tight junction protein 2 (zona occludens 2) [Source:HGNC Symbol;Acc:11828]",
				"ENSG00000092445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092445	TYRO3	TYRO3 protein tyrosine kinase [Source:HGNC Symbol;Acc:12446]",
				"ENSG00000107929	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107929	LARP4B	La ribonucleoprotein domain family, member 4B [Source:HGNC Symbol;Acc:28987]",
				"ENSG00000112029	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112029	FBXO5	F-box protein 5 [Source:HGNC Symbol;Acc:13584]",
				"ENSG00000115839	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115839	RAB3GAP1	RAB3 GTPase activating protein subunit 1 (catalytic) [Source:HGNC Symbol;Acc:17063]",
				"ENSG00000165076	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165076	PRSS37	protease, serine, 37 [Source:HGNC Symbol;Acc:29211]",
				"ENSG00000058804	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000058804	TMEM48	transmembrane protein 48 [Source:HGNC Symbol;Acc:25525]",
				"ENSG00000065970	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065970	FOXJ2	forkhead box J2 [Source:HGNC Symbol;Acc:24818]",
				"ENSG00000109762	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109762	SNX25	sorting nexin 25 [Source:HGNC Symbol;Acc:21883]",
				"ENSG00000131931	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131931	THAP1	THAP domain containing, apoptosis associated protein 1 [Source:HGNC Symbol;Acc:20856]",
				"ENSG00000139132	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139132	FGD4	FYVE, RhoGEF and PH domain containing 4 [Source:HGNC Symbol;Acc:19125]",
				"ENSG00000047410	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000047410	TPR	translocated promoter region, nuclear basket protein [Source:HGNC Symbol;Acc:12017]",
				"ENSG00000155363	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155363	MOV10	Mov10, Moloney leukemia virus 10, homolog (mouse) [Source:HGNC Symbol;Acc:7200]",
				"ENSG00000100802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100802	C14orf93	chromosome 14 open reading frame 93 [Source:HGNC Symbol;Acc:20162]",
				"ENSG00000048649	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000048649	RSF1	remodeling and spacing factor 1 [Source:HGNC Symbol;Acc:18118]",
				"ENSG00000129691	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129691	ASH2L	ash2 (absent, small, or homeotic)-like (Drosophila) [Source:HGNC Symbol;Acc:744]",
				"ENSG00000141452	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141452	C18orf8	chromosome 18 open reading frame 8 [Source:HGNC Symbol;Acc:24326]",
				"ENSG00000106609	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106609	TMEM248	transmembrane protein 248 [Source:HGNC Symbol;Acc:25476]",
				"ENSG00000152133	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152133	CCDC75	coiled-coil domain containing 75 [Source:HGNC Symbol;Acc:26768]",
				"ENSG00000145681	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145681	HAPLN1	hyaluronan and proteoglycan link protein 1 [Source:HGNC Symbol;Acc:2380]",
				"ENSG00000214655	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000214655	KIAA0913	KIAA0913 [Source:HGNC Symbol;Acc:23528]",
				"ENSG00000164951	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164951	PDP1	pyruvate dehyrogenase phosphatase catalytic subunit 1 [Source:HGNC Symbol;Acc:9279]",
				"ENSG00000198783	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198783	ZNF830	zinc finger protein 830 [Source:HGNC Symbol;Acc:28291]",
				"ENSG00000163029	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163029	SMC6	structural maintenance of chromosomes 6 [Source:HGNC Symbol;Acc:20466]",
				"ENSG00000166589	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166589	CDH16	cadherin 16, KSP-cadherin [Source:HGNC Symbol;Acc:1755]",
				"ENSG00000134709	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134709	HOOK1	hook homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:19884]",
				"ENSG00000023318	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000023318	ERP44	endoplasmic reticulum protein 44 [Source:HGNC Symbol;Acc:18311]",
				"ENSG00000185737	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185737	NRG3	neuregulin 3 [Source:HGNC Symbol;Acc:7999]",
				"ENSG00000106144	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106144	CASP2	caspase 2, apoptosis-related cysteine peptidase [Source:HGNC Symbol;Acc:1503]",
				"ENSG00000106086	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106086	PLEKHA8	pleckstrin homology domain containing, family A (phosphoinositide binding specific) member 8 [Source:HGNC Symbol;Acc:30037]",
				"ENSG00000110888	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110888	CAPRIN2	caprin family member 2 [Source:HGNC Symbol;Acc:21259]",
				"ENSG00000129159	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129159	KCNC1	potassium voltage-gated channel, Shaw-related subfamily, member 1 [Source:HGNC Symbol;Acc:6233]",
				"ENSG00000185880	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185880	TRIM69	tripartite motif containing 69 [Source:HGNC Symbol;Acc:17857]",
				"ENSG00000165185	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165185	KIAA1958	KIAA1958 [Source:HGNC Symbol;Acc:23427]",
				"ENSG00000120051	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120051	CCDC147	coiled-coil domain containing 147 [Source:HGNC Symbol;Acc:26676]",
				"ENSG00000119431	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119431	HDHD3	haloacid dehalogenase-like hydrolase domain containing 3 [Source:HGNC Symbol;Acc:28171]",
				"ENSG00000184378	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184378	ACTRT3	actin-related protein T3 [Source:HGNC Symbol;Acc:24022]",
				"ENSG00000171148	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171148	TADA3	transcriptional adaptor 3 [Source:HGNC Symbol;Acc:19422]",
				"ENSG00000124678	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124678	TCP11	t-complex 11 homolog (mouse) [Source:HGNC Symbol;Acc:11658]",
				"ENSG00000177427	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177427	SMCR7	Smith-Magenis syndrome chromosome region, candidate 7 [Source:HGNC Symbol;Acc:17920]",
				"ENSG00000186566	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186566	GPATCH8	G patch domain containing 8 [Source:HGNC Symbol;Acc:29066]",
				"ENSG00000154473	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154473	BUB3	budding uninhibited by benzimidazoles 3 homolog (yeast) [Source:HGNC Symbol;Acc:1151]",
				"ENSG00000117477	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117477	C1orf114	chromosome 1 open reading frame 114 [Source:HGNC Symbol;Acc:28051]",
				"ENSG00000119723	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119723	COQ6	coenzyme Q6 homolog, monooxygenase (S. cerevisiae) [Source:HGNC Symbol;Acc:20233]",
				"ENSG00000032219	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000032219	ARID4A	AT rich interactive domain 4A (RBP1-like) [Source:HGNC Symbol;Acc:9885]",
				"ENSG00000095303	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095303	PTGS1	prostaglandin-endoperoxide synthase 1 (prostaglandin G/H synthase and cyclooxygenase) [Source:HGNC Symbol;Acc:9604]",
				"ENSG00000116044	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116044	NFE2L2	nuclear factor (erythroid-derived 2)-like 2 [Source:HGNC Symbol;Acc:7782]",
				"ENSG00000132823	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132823	C20orf111	chromosome 20 open reading frame 111 [Source:HGNC Symbol;Acc:16105]",
				"ENSG00000166333	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166333	ILK	integrin-linked kinase [Source:HGNC Symbol;Acc:6040]",
				"ENSG00000185610	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185610	DBX2	developing brain homeobox 2 [Source:HGNC Symbol;Acc:33186]",
				"ENSG00000137807	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137807	KIF23	kinesin family member 23 [Source:HGNC Symbol;Acc:6392]",
				"ENSG00000154721	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154721	JAM2	junctional adhesion molecule 2 [Source:HGNC Symbol;Acc:14686]",
				"ENSG00000094804	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000094804	CDC6	cell division cycle 6 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1744]",
				"ENSG00000172244	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172244	C5orf34	chromosome 5 open reading frame 34 [Source:HGNC Symbol;Acc:24738]",
				"ENSG00000146476	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146476	C6orf211	chromosome 6 open reading frame 211 [Source:HGNC Symbol;Acc:17872]",
				"ENSG00000100147	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100147	CCDC134	coiled-coil domain containing 134 [Source:HGNC Symbol;Acc:26185]",
				"ENSG00000167395	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167395	ZNF646	zinc finger protein 646 [Source:HGNC Symbol;Acc:29004]",
				"ENSG00000151093	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151093	OXSM	3-oxoacyl-ACP synthase, mitochondrial [Source:HGNC Symbol;Acc:26063]",
				"ENSG00000165891	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165891	E2F7	E2F transcription factor 7 [Source:HGNC Symbol;Acc:23820]",
				"ENSG00000154447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154447	SH3RF1	SH3 domain containing ring finger 1 [Source:HGNC Symbol;Acc:17650]",
				"ENSG00000143450	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143450	OAZ3	ornithine decarboxylase antizyme 3 [Source:HGNC Symbol;Acc:8097]",
				"ENSG00000137976	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137976	DNASE2B	deoxyribonuclease II beta [Source:HGNC Symbol;Acc:28875]",
				"ENSG00000096384	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000096384	HSP90AB1	heat shock protein 90kDa alpha (cytosolic), class B member 1 [Source:HGNC Symbol;Acc:5258]",
				"ENSG00000004799	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004799	PDK4	pyruvate dehydrogenase kinase, isozyme 4 [Source:HGNC Symbol;Acc:8812]",
				"ENSG00000151962	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151962	RBM46	RNA binding motif protein 46 [Source:HGNC Symbol;Acc:28401]",
				"ENSG00000137462	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137462	TLR2	toll-like receptor 2 [Source:HGNC Symbol;Acc:11848]",
				"ENSG00000164603	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164603	C7orf60	chromosome 7 open reading frame 60 [Source:HGNC Symbol;Acc:26475]",
				"ENSG00000006453	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006453	BAIAP2L1	BAI1-associated protein 2-like 1 [Source:HGNC Symbol;Acc:21649]",
				"ENSG00000089154	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089154	GCN1L1	GCN1 general control of amino-acid synthesis 1-like 1 (yeast) [Source:HGNC Symbol;Acc:4199]",
				"ENSG00000011143	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011143	MKS1	Meckel syndrome, type 1 [Source:HGNC Symbol;Acc:7121]",
				"ENSG00000092871	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092871	RFFL	ring finger and FYVE-like domain containing E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:24821]",
				"ENSG00000112218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112218	GPR63	G protein-coupled receptor 63 [Source:HGNC Symbol;Acc:13302]",
				"ENSG00000166783	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166783	KIAA0430	KIAA0430 [Source:HGNC Symbol;Acc:29562]",
				"ENSG00000132275	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132275	RRP8	ribosomal RNA processing 8, methyltransferase, homolog (yeast) [Source:HGNC Symbol;Acc:29030]",
				"ENSG00000124602	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124602	UNC5CL	unc-5 homolog C (C. elegans)-like [Source:HGNC Symbol;Acc:21203]",
				"ENSG00000113456	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113456	RAD1	RAD1 homolog (S. pombe) [Source:HGNC Symbol;Acc:9806]",
				"ENSG00000114388	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114388	NPRL2	nitrogen permease regulator-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:24969]",
				"ENSG00000101337	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101337	TM9SF4	transmembrane 9 superfamily protein member 4 [Source:HGNC Symbol;Acc:30797]",
				"ENSG00000149634	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149634	SPATA25	spermatogenesis associated 25 [Source:HGNC Symbol;Acc:16158]",
				"ENSG00000023228	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000023228	NDUFS1	NADH dehydrogenase (ubiquinone) Fe-S protein 1, 75kDa (NADH-coenzyme Q reductase) [Source:HGNC Symbol;Acc:7707]",
				"ENSG00000256061	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000256061	DYX1C1	dyslexia susceptibility 1 candidate 1 [Source:HGNC Symbol;Acc:21493]",
				"ENSG00000135469	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135469	COQ10A	coenzyme Q10 homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:26515]",
				"ENSG00000109929	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109929	SC5DL	sterol-C5-desaturase (ERG3 delta-5-desaturase homolog, S. cerevisiae)-like [Source:HGNC Symbol;Acc:10547]",
				"ENSG00000169499	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169499	PLEKHA2	pleckstrin homology domain containing, family A (phosphoinositide binding specific) member 2 [Source:HGNC Symbol;Acc:14336]",
				"ENSG00000162775	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162775	RBM15	RNA binding motif protein 15 [Source:HGNC Symbol;Acc:14959]",
				"ENSG00000135900	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135900	MRPL44	mitochondrial ribosomal protein L44 [Source:HGNC Symbol;Acc:16650]",
				"ENSG00000155229	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155229	MMS19	MMS19 nucleotide excision repair homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:13824]",
				"ENSG00000168060	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168060	NAALADL1	N-acetylated alpha-linked acidic dipeptidase-like 1 [Source:HGNC Symbol;Acc:23536]",
				"ENSG00000170881	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170881	RNF139	ring finger protein 139 [Source:HGNC Symbol;Acc:17023]",
				"ENSG00000175097	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175097	RAG2	recombination activating gene 2 [Source:HGNC Symbol;Acc:9832]",
				"ENSG00000054356	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000054356	PTPRN	protein tyrosine phosphatase, receptor type, N [Source:HGNC Symbol;Acc:9676]",
				"ENSG00000145863	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145863	GABRA6	gamma-aminobutyric acid (GABA) A receptor, alpha 6 [Source:HGNC Symbol;Acc:4080]",
				"ENSG00000109787	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109787	KLF3	Kruppel-like factor 3 (basic) [Source:HGNC Symbol;Acc:16516]",
				"ENSG00000172167	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172167	MTBP	Mdm2, transformed 3T3 cell double minute 2, p53 binding protein (mouse) binding protein, 104kDa [Source:HGNC Symbol;Acc:7417]",
				"ENSG00000068097	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068097	HEATR6	HEAT repeat containing 6 [Source:HGNC Symbol;Acc:24076]",
				"ENSG00000176994	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176994	SMCR8	Smith-Magenis syndrome chromosome region, candidate 8 [Source:HGNC Symbol;Acc:17921]",
				"ENSG00000162994	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162994	C2orf63	chromosome 2 open reading frame 63 [Source:HGNC Symbol;Acc:26453]",
				"ENSG00000178149	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178149	DALRD3	DALR anticodon binding domain containing 3 [Source:HGNC Symbol;Acc:25536]",
				"ENSG00000163576	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163576	EFHB	EF-hand domain family, member B [Source:HGNC Symbol;Acc:26330]",
				"ENSG00000132906	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132906	CASP9	caspase 9, apoptosis-related cysteine peptidase [Source:HGNC Symbol;Acc:1511]",
				"ENSG00000239305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000239305	RNF103	ring finger protein 103 [Source:HGNC Symbol;Acc:12859]",
				"ENSG00000073756	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000073756	PTGS2	prostaglandin-endoperoxide synthase 2 (prostaglandin G/H synthase and cyclooxygenase) [Source:HGNC Symbol;Acc:9605]",
				"ENSG00000134070	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134070	IRAK2	interleukin-1 receptor-associated kinase 2 [Source:HGNC Symbol;Acc:6113]",
				"ENSG00000141052	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141052	MYOCD	myocardin [Source:HGNC Symbol;Acc:16067]",
				"ENSG00000170222	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170222	C17orf48	chromosome 17 open reading frame 48 [Source:HGNC Symbol;Acc:30925]",
				"ENSG00000103051	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103051	COG4	component of oligomeric golgi complex 4 [Source:HGNC Symbol;Acc:18620]",
				"ENSG00000135945	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135945	REV1	REV1, polymerase (DNA directed) [Source:HGNC Symbol;Acc:14060]",
				"ENSG00000132386	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132386	SERPINF1	serpin peptidase inhibitor, clade F (alpha-2 antiplasmin, pigment epithelium derived factor), member 1 [Source:HGNC Symbol;Acc:8824]",
				"ENSG00000163626	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163626	COX18	COX18 cytochrome c oxidase assembly homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:26801]",
				"ENSG00000161326	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161326	DUSP14	dual specificity phosphatase 14 [Source:HGNC Symbol;Acc:17007]",
				"ENSG00000187003	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187003	ACTL7A	actin-like 7A [Source:HGNC Symbol;Acc:161]",
				"ENSG00000114999	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114999	TTL	tubulin tyrosine ligase [Source:HGNC Symbol;Acc:21586]",
				"ENSG00000034693	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000034693	PEX3	peroxisomal biogenesis factor 3 [Source:HGNC Symbol;Acc:8858]",
				"ENSG00000122861	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122861	PLAU	plasminogen activator, urokinase [Source:HGNC Symbol;Acc:9052]",
				"ENSG00000157045	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157045	NTAN1	N-terminal asparagine amidase [Source:HGNC Symbol;Acc:29909]",
				"ENSG00000082516	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082516	GEMIN5	gem (nuclear organelle) associated protein 5 [Source:HGNC Symbol;Acc:20043]",
				"ENSG00000106105	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106105	GARS	glycyl-tRNA synthetase [Source:HGNC Symbol;Acc:4162]",
				"ENSG00000171448	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171448	ZBTB26	zinc finger and BTB domain containing 26 [Source:HGNC Symbol;Acc:23383]",
				"ENSG00000155324	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155324	GRAMD3	GRAM domain containing 3 [Source:HGNC Symbol;Acc:24911]",
				"ENSG00000134851	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134851	TMEM165	transmembrane protein 165 [Source:HGNC Symbol;Acc:30760]",
				"ENSG00000131482	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131482	G6PC	glucose-6-phosphatase, catalytic subunit [Source:HGNC Symbol;Acc:4056]",
				"ENSG00000141298	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141298	SSH2	slingshot homolog 2 (Drosophila) [Source:HGNC Symbol;Acc:30580]",
				"ENSG00000002919	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000002919	SNX11	sorting nexin 11 [Source:HGNC Symbol;Acc:14975]",
				"ENSG00000068831	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068831	RASGRP2	RAS guanyl releasing protein 2 (calcium and DAG-regulated) [Source:HGNC Symbol;Acc:9879]",
				"ENSG00000176928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176928	GCNT4	glucosaminyl (N-acetyl) transferase 4, core 2 [Source:HGNC Symbol;Acc:17973]",
				"ENSG00000158220	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158220	ESYT3	extended synaptotagmin-like protein 3 [Source:HGNC Symbol;Acc:24295]",
				"ENSG00000249115	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000249115	HAUS5	HAUS augmin-like complex, subunit 5 [Source:HGNC Symbol;Acc:29130]",
				"ENSG00000163508	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163508	EOMES	eomesodermin [Source:HGNC Symbol;Acc:3372]",
				"ENSG00000164404	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164404	GDF9	growth differentiation factor 9 [Source:HGNC Symbol;Acc:4224]",
				"ENSG00000184108	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184108	TRIML1	tripartite motif family-like 1 [Source:HGNC Symbol;Acc:26698]",
				"ENSG00000169375	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169375	SIN3A	SIN3 transcription regulator homolog A (yeast) [Source:HGNC Symbol;Acc:19353]",
				"ENSG00000169508	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169508	GPR183	G protein-coupled receptor 183 [Source:HGNC Symbol;Acc:3128]",
				"ENSG00000133858	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133858	ZFC3H1	zinc finger, C3H1-type containing [Source:HGNC Symbol;Acc:28328]",
				"ENSG00000106344	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106344	RBM28	RNA binding motif protein 28 [Source:HGNC Symbol;Acc:21863]",
				"ENSG00000179456	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179456	ZNF238	zinc finger protein 238 [Source:HGNC Symbol;Acc:13030]",
				"ENSG00000168389	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168389	MFSD2A	major facilitator superfamily domain containing 2A [Source:HGNC Symbol;Acc:25897]",
				"ENSG00000089195	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089195	TRMT6	tRNA methyltransferase 6 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:20900]",
				"ENSG00000164068	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164068	RNF123	ring finger protein 123 [Source:HGNC Symbol;Acc:21148]",
				"ENSG00000156990	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156990	RPUSD3	RNA pseudouridylate synthase domain containing 3 [Source:HGNC Symbol;Acc:28437]",
				"ENSG00000154328	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154328	NEIL2	nei endonuclease VIII-like 2 (E. coli) [Source:HGNC Symbol;Acc:18956]",
				"ENSG00000115548	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115548	KDM3A	lysine (K)-specific demethylase 3A [Source:HGNC Symbol;Acc:20815]",
				"ENSG00000163644	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163644	PPM1K	protein phosphatase, Mg2+/Mn2+ dependent, 1K [Source:HGNC Symbol;Acc:25415]",
				"ENSG00000166321	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166321	NUDT13	nudix (nucleoside diphosphate linked moiety X)-type motif 13 [Source:HGNC Symbol;Acc:18827]",
				"ENSG00000138459	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138459	SLC35A5	solute carrier family 35, member A5 [Source:HGNC Symbol;Acc:20792]",
				"ENSG00000068394	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068394	GPKOW	G patch domain and KOW motifs [Source:HGNC Symbol;Acc:30677]",
				"ENSG00000108753	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108753	HNF1B	HNF1 homeobox B [Source:HGNC Symbol;Acc:11630]",
				"ENSG00000101981	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101981	F9	coagulation factor IX [Source:HGNC Symbol;Acc:3551]",
				"ENSG00000157168	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157168	NRG1	neuregulin 1 [Source:HGNC Symbol;Acc:7997]",
				"ENSG00000181830	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181830	SLC35C1	solute carrier family 35, member C1 [Source:HGNC Symbol;Acc:20197]",
				"ENSG00000176986	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176986	SEC24C	SEC24 family, member C (S. cerevisiae) [Source:HGNC Symbol;Acc:10705]",
				"ENSG00000106789	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106789	CORO2A	coronin, actin binding protein, 2A [Source:HGNC Symbol;Acc:2255]",
				"ENSG00000184307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184307	ZDHHC23	zinc finger, DHHC-type containing 23 [Source:HGNC Symbol;Acc:28654]",
				"ENSG00000138271	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138271	GPR87	G protein-coupled receptor 87 [Source:HGNC Symbol;Acc:4538]",
				"ENSG00000080166	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080166	DCT	dopachrome tautomerase (dopachrome delta-isomerase, tyrosine-related protein 2) [Source:HGNC Symbol;Acc:2709]",
				"ENSG00000163507	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163507	KIAA1524	KIAA1524 [Source:HGNC Symbol;Acc:29302]",
				"ENSG00000118197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118197	DDX59	DEAD (Asp-Glu-Ala-Asp) box polypeptide 59 [Source:HGNC Symbol;Acc:25360]",
				"ENSG00000088682	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088682	COQ9	coenzyme Q9 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25302]",
				"ENSG00000139626	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139626	ITGB7	integrin, beta 7 [Source:HGNC Symbol;Acc:6162]",
				"ENSG00000146463	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146463	ZMYM4	zinc finger, MYM-type 4 [Source:HGNC Symbol;Acc:13055]",
				"ENSG00000171766	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171766	GATM	glycine amidinotransferase (L-arginine:glycine amidinotransferase) [Source:HGNC Symbol;Acc:4175]",
				"ENSG00000162878	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162878	PKDCC	protein kinase domain containing, cytoplasmic homolog (mouse) [Source:HGNC Symbol;Acc:25123]",
				"ENSG00000101109	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101109	STK4	serine/threonine kinase 4 [Source:HGNC Symbol;Acc:11408]",
				"ENSG00000178295	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178295	GEN1	Gen endonuclease homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:26881]",
				"ENSG00000100558	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100558	PLEK2	pleckstrin 2 [Source:HGNC Symbol;Acc:19238]",
				"ENSG00000092931	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092931	MFSD11	major facilitator superfamily domain containing 11 [Source:HGNC Symbol;Acc:25458]",
				"ENSG00000134057	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134057	CCNB1	cyclin B1 [Source:HGNC Symbol;Acc:1579]",
				"ENSG00000112297	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112297	AIM1	absent in melanoma 1 [Source:HGNC Symbol;Acc:356]",
				"ENSG00000178445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178445	GLDC	glycine dehydrogenase (decarboxylating) [Source:HGNC Symbol;Acc:4313]",
				"ENSG00000135655	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135655	USP15	ubiquitin specific peptidase 15 [Source:HGNC Symbol;Acc:12613]",
				"ENSG00000198087	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198087	CD2AP	CD2-associated protein [Source:HGNC Symbol;Acc:14258]",
				"ENSG00000156239	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156239	N6AMT1	N-6 adenine-specific DNA methyltransferase 1 (putative) [Source:HGNC Symbol;Acc:16021]",
				"ENSG00000164164	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164164	OTUD4	OTU domain containing 4 [Source:HGNC Symbol;Acc:24949]",
				"ENSG00000185246	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185246	PRPF39	PRP39 pre-mRNA processing factor 39 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:20314]",
				"ENSG00000132773	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132773	TOE1	target of EGR1, member 1 (nuclear) [Source:HGNC Symbol;Acc:15954]",
				"ENSG00000165449	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165449	SLC16A9	solute carrier family 16, member 9 (monocarboxylic acid transporter 9) [Source:HGNC Symbol;Acc:23520]",
				"ENSG00000149499	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149499	EML3	echinoderm microtubule associated protein like 3 [Source:HGNC Symbol;Acc:26666]",
				"ENSG00000091947	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000091947	TMEM101	transmembrane protein 101 [Source:HGNC Symbol;Acc:28653]",
				"ENSG00000014919	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000014919	COX15	COX15 homolog, cytochrome c oxidase assembly protein (yeast) [Source:HGNC Symbol;Acc:2263]",
				"ENSG00000085840	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085840	ORC1	origin recognition complex, subunit 1 [Source:HGNC Symbol;Acc:8487]",
				"ENSG00000173253	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173253	DMRT2	doublesex and mab-3 related transcription factor 2 [Source:HGNC Symbol;Acc:2935]",
				"ENSG00000144136	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144136	SLC20A1	solute carrier family 20 (phosphate transporter), member 1 [Source:HGNC Symbol;Acc:10946]",
				"ENSG00000138434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138434	SSFA2	sperm specific antigen 2 [Source:HGNC Symbol;Acc:11319]",
				"ENSG00000121753	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121753	BAI2	brain-specific angiogenesis inhibitor 2 [Source:HGNC Symbol;Acc:944]",
				"ENSG00000126216	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126216	TUBGCP3	tubulin, gamma complex associated protein 3 [Source:HGNC Symbol;Acc:18598]",
				"ENSG00000105707	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105707	HPN	hepsin [Source:HGNC Symbol;Acc:5155]",
				"ENSG00000013523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013523	ANGEL1	angel homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:19961]",
				"ENSG00000061656	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000061656	SPAG4	sperm associated antigen 4 [Source:HGNC Symbol;Acc:11214]",
				"ENSG00000180185	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180185	FAHD1	fumarylacetoacetate hydrolase domain containing 1 [Source:HGNC Symbol;Acc:14169]",
				"ENSG00000139131	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139131	YARS2	tyrosyl-tRNA synthetase 2, mitochondrial [Source:HGNC Symbol;Acc:24249]",
				"ENSG00000108349	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108349	CASC3	cancer susceptibility candidate 3 [Source:HGNC Symbol;Acc:17040]",
				"ENSG00000172215	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172215	CXCR6	chemokine (C-X-C motif) receptor 6 [Source:HGNC Symbol;Acc:16647]",
				"ENSG00000128829	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128829	EIF2AK4	eukaryotic translation initiation factor 2 alpha kinase 4 [Source:HGNC Symbol;Acc:19687]",
				"ENSG00000138363	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138363	ATIC	5-aminoimidazole-4-carboxamide ribonucleotide formyltransferase/IMP cyclohydrolase [Source:HGNC Symbol;Acc:794]",
				"ENSG00000166997	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166997	CNPY4	canopy 4 homolog (zebrafish) [Source:HGNC Symbol;Acc:28631]",
				"ENSG00000131711	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131711	MAP1B	microtubule-associated protein 1B [Source:HGNC Symbol;Acc:6836]",
				"ENSG00000123610	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123610	TNFAIP6	tumor necrosis factor, alpha-induced protein 6 [Source:HGNC Symbol;Acc:11898]",
				"ENSG00000105976	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105976	MET	met proto-oncogene (hepatocyte growth factor receptor) [Source:HGNC Symbol;Acc:7029]",
				"ENSG00000129173	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129173	E2F8	E2F transcription factor 8 [Source:HGNC Symbol;Acc:24727]",
				"ENSG00000119943	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119943	PYROXD2	pyridine nucleotide-disulphide oxidoreductase domain 2 [Source:HGNC Symbol;Acc:23517]",
				"ENSG00000128052	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128052	KDR	kinase insert domain receptor (a type III receptor tyrosine kinase) [Source:HGNC Symbol;Acc:6307]",
				"ENSG00000157800	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157800	SLC37A3	solute carrier family 37 (glycerol-3-phosphate transporter), member 3 [Source:HGNC Symbol;Acc:20651]",
				"ENSG00000146232	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146232	NFKBIE	nuclear factor of kappa light polypeptide gene enhancer in B-cells inhibitor, epsilon [Source:HGNC Symbol;Acc:7799]",
				"ENSG00000127951	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127951	FGL2	fibrinogen-like 2 [Source:HGNC Symbol;Acc:3696]",
				"ENSG00000102890	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102890	ELMO3	engulfment and cell motility 3 [Source:HGNC Symbol;Acc:17289]",
				"ENSG00000173627	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173627	APOBEC4	apolipoprotein B mRNA editing enzyme, catalytic polypeptide-like 4 (putative) [Source:HGNC Symbol;Acc:32152]",
				"ENSG00000113946	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113946	CLDN16	claudin 16 [Source:HGNC Symbol;Acc:2037]",
				"ENSG00000113522	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113522	RAD50	RAD50 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:9816]",
				"ENSG00000105137	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105137	SYDE1	synapse defective 1, Rho GTPase, homolog 1 (C. elegans) [Source:HGNC Symbol;Acc:25824]",
				"ENSG00000136197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136197	C7orf25	chromosome 7 open reading frame 25 [Source:HGNC Symbol;Acc:21703]",
				"ENSG00000110514	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110514	MADD	MAP-kinase activating death domain [Source:HGNC Symbol;Acc:6766]",
				"ENSG00000152242	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152242	C18orf25	chromosome 18 open reading frame 25 [Source:HGNC Symbol;Acc:28172]",
				"ENSG00000139209	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139209	SLC38A4	solute carrier family 38, member 4 [Source:HGNC Symbol;Acc:14679]",
				"ENSG00000115661	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115661	STK16	serine/threonine kinase 16 [Source:HGNC Symbol;Acc:11394]",
				"ENSG00000150086	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150086	GRIN2B	glutamate receptor, ionotropic, N-methyl D-aspartate 2B [Source:HGNC Symbol;Acc:4586]",
				"ENSG00000094975	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000094975	C1orf9	chromosome 1 open reading frame 9 [Source:HGNC Symbol;Acc:1240]",
				"ENSG00000175137	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175137	SH3BP5L	SH3-binding domain protein 5-like [Source:HGNC Symbol;Acc:29360]",
				"ENSG00000010626	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010626	LRRC23	leucine rich repeat containing 23 [Source:HGNC Symbol;Acc:19138]",
				"ENSG00000183111	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183111	ARHGEF37	Rho guanine nucleotide exchange factor (GEF) 37 [Source:HGNC Symbol;Acc:34430]",
				"ENSG00000123094	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123094	RASSF8	Ras association (RalGDS/AF-6) domain family (N-terminal) member 8 [Source:HGNC Symbol;Acc:13232]",
				"ENSG00000069956	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069956	MAPK6	mitogen-activated protein kinase 6 [Source:HGNC Symbol;Acc:6879]",
				"ENSG00000115414	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115414	FN1	fibronectin 1 [Source:HGNC Symbol;Acc:3778]",
				"ENSG00000164620	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164620	RELL2	RELT-like 2 [Source:HGNC Symbol;Acc:26902]",
				"ENSG00000163214	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163214	DHX57	DEAH (Asp-Glu-Ala-Asp/His) box polypeptide 57 [Source:HGNC Symbol;Acc:20086]",
				"ENSG00000162520	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162520	SYNC	syncoilin, intermediate filament protein [Source:HGNC Symbol;Acc:28897]",
				"ENSG00000107021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107021	TBC1D13	TBC1 domain family, member 13 [Source:HGNC Symbol;Acc:25571]",
				"ENSG00000125454	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125454	SLC25A19	solute carrier family 25 (mitochondrial thiamine pyrophosphate carrier), member 19 [Source:HGNC Symbol;Acc:14409]",
				"ENSG00000042445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000042445	RETSAT	retinol saturase (all-trans-retinol 13,14-reductase) [Source:HGNC Symbol;Acc:25991]",
				"ENSG00000188037	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188037	CLCN1	chloride channel, voltage-sensitive 1 [Source:HGNC Symbol;Acc:2019]",
				"ENSG00000174953	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174953	DHX36	DEAH (Asp-Glu-Ala-His) box polypeptide 36 [Source:HGNC Symbol;Acc:14410]",
				"ENSG00000172432	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172432	GTPBP2	GTP binding protein 2 [Source:HGNC Symbol;Acc:4670]",
				"ENSG00000172732	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172732	MUS81	MUS81 endonuclease homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:29814]",
				"ENSG00000145901	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145901	TNIP1	TNFAIP3 interacting protein 1 [Source:HGNC Symbol;Acc:16903]",
				"ENSG00000128059	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128059	PPAT	phosphoribosyl pyrophosphate amidotransferase [Source:HGNC Symbol;Acc:9238]",
				"ENSG00000097096	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000097096	SYDE2	synapse defective 1, Rho GTPase, homolog 2 (C. elegans) [Source:HGNC Symbol;Acc:25841]",
				"ENSG00000100591	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100591	AHSA1	AHA1, activator of heat shock 90kDa protein ATPase homolog 1 (yeast) [Source:HGNC Symbol;Acc:1189]",
				"ENSG00000066056	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000066056	TIE1	tyrosine kinase with immunoglobulin-like and EGF-like domains 1 [Source:HGNC Symbol;Acc:11809]",
				"ENSG00000149218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149218	ENDOD1	endonuclease domain containing 1 [Source:HGNC Symbol;Acc:29129]",
				"ENSG00000092529	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092529	CAPN3	calpain 3, (p94) [Source:HGNC Symbol;Acc:1480]",
				"ENSG00000157796	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157796	WDR19	WD repeat domain 19 [Source:HGNC Symbol;Acc:18340]",
				"ENSG00000108559	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108559	NUP88	nucleoporin 88kDa [Source:HGNC Symbol;Acc:8067]",
				"ENSG00000151806	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151806	GUF1	GUF1 GTPase homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25799]",
				"ENSG00000164776	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164776	PHKG1	phosphorylase kinase, gamma 1 (muscle) [Source:HGNC Symbol;Acc:8930]",
				"ENSG00000134852	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134852	CLOCK	clock homolog (mouse) [Source:HGNC Symbol;Acc:2082]",
				"ENSG00000163328	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163328	GPR155	G protein-coupled receptor 155 [Source:HGNC Symbol;Acc:22951]",
				"ENSG00000127980	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127980	PEX1	peroxisomal biogenesis factor 1 [Source:HGNC Symbol;Acc:8850]",
				"ENSG00000125744	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125744	RTN2	reticulon 2 [Source:HGNC Symbol;Acc:10468]",
				"ENSG00000163870	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163870	TPRA1	transmembrane protein, adipocyte asscociated 1 [Source:HGNC Symbol;Acc:30413]",
				"ENSG00000114026	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114026	OGG1	8-oxoguanine DNA glycosylase [Source:HGNC Symbol;Acc:8125]",
				"ENSG00000198270	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198270	TMEM116	transmembrane protein 116 [Source:HGNC Symbol;Acc:25084]",
				"ENSG00000140254	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140254	DUOXA1	dual oxidase maturation factor 1 [Source:HGNC Symbol;Acc:26507]",
				"ENSG00000174547	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174547	MRPL11	mitochondrial ribosomal protein L11 [Source:HGNC Symbol;Acc:14042]",
				"ENSG00000133706	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133706	LARS	leucyl-tRNA synthetase [Source:HGNC Symbol;Acc:6512]",
				"ENSG00000139354	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139354	GAS2L3	growth arrest-specific 2 like 3 [Source:HGNC Symbol;Acc:27475]",
				"ENSG00000125485	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125485	DDX31	DEAD (Asp-Glu-Ala-Asp) box polypeptide 31 [Source:HGNC Symbol;Acc:16715]",
				"ENSG00000100439	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100439	ABHD4	abhydrolase domain containing 4 [Source:HGNC Symbol;Acc:20154]",
				"ENSG00000111254	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111254	AKAP3	A kinase (PRKA) anchor protein 3 [Source:HGNC Symbol;Acc:373]",
				"ENSG00000001630	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000001630	CYP51A1	cytochrome P450, family 51, subfamily A, polypeptide 1 [Source:HGNC Symbol;Acc:2649]",
				"ENSG00000143107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143107	FNDC7	fibronectin type III domain containing 7 [Source:HGNC Symbol;Acc:26668]",
				"ENSG00000138166	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138166	DUSP5	dual specificity phosphatase 5 [Source:HGNC Symbol;Acc:3071]",
				"ENSG00000170876	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170876	TMEM43	transmembrane protein 43 [Source:HGNC Symbol;Acc:28472]",
				"ENSG00000145779	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145779	TNFAIP8	tumor necrosis factor, alpha-induced protein 8 [Source:HGNC Symbol;Acc:17260]",
				"ENSG00000014123	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000014123	UFL1	UFM1-specific ligase 1 [Source:HGNC Symbol;Acc:23039]",
				"ENSG00000177191	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177191	B3GNT8	UDP-GlcNAc:betaGal beta-1,3-N-acetylglucosaminyltransferase 8 [Source:HGNC Symbol;Acc:24139]",
				"ENSG00000173083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173083	HPSE	heparanase [Source:HGNC Symbol;Acc:5164]",
				"ENSG00000185875	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185875	THNSL1	threonine synthase-like 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:26160]",
				"ENSG00000054983	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000054983	GALC	galactosylceramidase [Source:HGNC Symbol;Acc:4115]",
				"ENSG00000064607	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064607	SUGP2	SURP and G patch domain containing 2 [Source:HGNC Symbol;Acc:18641]",
				"ENSG00000151553	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151553	FAM160B1	family with sequence similarity 160, member B1 [Source:HGNC Symbol;Acc:29320]",
				"ENSG00000109771	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109771	LRP2BP	LRP2 binding protein [Source:HGNC Symbol;Acc:25434]",
				"ENSG00000104613	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104613	INTS10	integrator complex subunit 10 [Source:HGNC Symbol;Acc:25548]",
				"ENSG00000183735	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183735	TBK1	TANK-binding kinase 1 [Source:HGNC Symbol;Acc:11584]",
				"ENSG00000125434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125434	SLC25A35	solute carrier family 25, member 35 [Source:HGNC Symbol;Acc:31921]",
				"ENSG00000156802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156802	ATAD2	ATPase family, AAA domain containing 2 [Source:HGNC Symbol;Acc:30123]",
				"ENSG00000196504	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196504	PRPF40A	PRP40 pre-mRNA processing factor 40 homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:16463]",
				"ENSG00000039560	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000039560	RAI14	retinoic acid induced 14 [Source:HGNC Symbol;Acc:14873]",
				"ENSG00000105668	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105668	UPK1A	uroplakin 1A [Source:HGNC Symbol;Acc:12577]",
				"ENSG00000166532	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166532	RIMKLB	ribosomal modification protein rimK-like family member B [Source:HGNC Symbol;Acc:29228]",
				"ENSG00000158552	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158552	ZFAND2B	zinc finger, AN1-type domain 2B [Source:HGNC Symbol;Acc:25206]",
				"ENSG00000006114	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006114	SYNRG	synergin, gamma [Source:HGNC Symbol;Acc:557]",
				"ENSG00000184867	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184867	ARMCX2	armadillo repeat containing, X-linked 2 [Source:HGNC Symbol;Acc:16869]",
				"ENSG00000198001	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198001	IRAK4	interleukin-1 receptor-associated kinase 4 [Source:HGNC Symbol;Acc:17967]",
				"ENSG00000124074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124074	C16orf48	chromosome 16 open reading frame 48 [Source:HGNC Symbol;Acc:25246]",
				"ENSG00000214447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000214447	FAM187A	family with sequence similarity 187, member A [Source:HGNC Symbol;Acc:35153]",
				"ENSG00000115464	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115464	USP34	ubiquitin specific peptidase 34 [Source:HGNC Symbol;Acc:20066]",
				"ENSG00000164291	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164291	ARSK	arylsulfatase family, member K [Source:HGNC Symbol;Acc:25239]",
				"ENSG00000138111	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138111	TMEM180	transmembrane protein 180 [Source:HGNC Symbol;Acc:26196]",
				"ENSG00000007944	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000007944	MYLIP	myosin regulatory light chain interacting protein [Source:HGNC Symbol;Acc:21155]",
				"ENSG00000128928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128928	IVD	isovaleryl-CoA dehydrogenase [Source:HGNC Symbol;Acc:6186]",
				"ENSG00000144852	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144852	NR1I2	nuclear receptor subfamily 1, group I, member 2 [Source:HGNC Symbol;Acc:7968]",
				"ENSG00000180530	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180530	NRIP1	nuclear receptor interacting protein 1 [Source:HGNC Symbol;Acc:8001]",
				"ENSG00000035928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000035928	RFC1	replication factor C (activator 1) 1, 145kDa [Source:HGNC Symbol;Acc:9969]",
				"ENSG00000121680	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121680	PEX16	peroxisomal biogenesis factor 16 [Source:HGNC Symbol;Acc:8857]",
				"ENSG00000151876	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151876	FBXO4	F-box protein 4 [Source:HGNC Symbol;Acc:13583]",
				"ENSG00000111231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111231	GPN3	GPN-loop GTPase 3 [Source:HGNC Symbol;Acc:30186]",
				"ENSG00000099942	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000099942	CRKL	v-crk sarcoma virus CT10 oncogene homolog (avian)-like [Source:HGNC Symbol;Acc:2363]",
				"ENSG00000135473	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135473	PAN2	PAN2 poly(A) specific ribonuclease subunit homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:20074]",
				"ENSG00000140398	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140398	NEIL1	nei endonuclease VIII-like 1 (E. coli) [Source:HGNC Symbol;Acc:18448]",
				"ENSG00000150556	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150556	LYPD6B	LY6/PLAUR domain containing 6B [Source:HGNC Symbol;Acc:27018]",
				"ENSG00000166881	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166881	TMEM194A	transmembrane protein 194A [Source:HGNC Symbol;Acc:29001]",
				"ENSG00000188549	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188549	C15orf52	chromosome 15 open reading frame 52 [Source:HGNC Symbol;Acc:33488]",
				"ENSG00000167604	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167604	NFKBID	nuclear factor of kappa light polypeptide gene enhancer in B-cells inhibitor, delta [Source:HGNC Symbol;Acc:15671]",
				"ENSG00000166224	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166224	SGPL1	sphingosine-1-phosphate lyase 1 [Source:HGNC Symbol;Acc:10817]",
				"ENSG00000106772	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106772	PRUNE2	prune homolog 2 (Drosophila) [Source:HGNC Symbol;Acc:25209]",
				"ENSG00000166311	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166311	SMPD1	sphingomyelin phosphodiesterase 1, acid lysosomal [Source:HGNC Symbol;Acc:11120]",
				"ENSG00000106330	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106330	MOSPD3	motile sperm domain containing 3 [Source:HGNC Symbol;Acc:25078]",
				"ENSG00000135968	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135968	GCC2	GRIP and coiled-coil domain containing 2 [Source:HGNC Symbol;Acc:23218]",
				"ENSG00000114491	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114491	UMPS	uridine monophosphate synthetase [Source:HGNC Symbol;Acc:12563]",
				"ENSG00000011021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011021	CLCN6	chloride channel, voltage-sensitive 6 [Source:HGNC Symbol;Acc:2024]",
				"ENSG00000163959	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163959	AC069257.9	Organic solute transporter subunit alpha  [Source:UniProtKB/Swiss-Prot;Acc:Q86UW1]",
				"ENSG00000050405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000050405	LIMA1	LIM domain and actin binding 1 [Source:HGNC Symbol;Acc:24636]",
				"ENSG00000169902	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169902	TPST1	tyrosylprotein sulfotransferase 1 [Source:HGNC Symbol;Acc:12020]",
				"ENSG00000108797	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108797	CNTNAP1	contactin associated protein 1 [Source:HGNC Symbol;Acc:8011]",
				"ENSG00000066777	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000066777	ARFGEF1	ADP-ribosylation factor guanine nucleotide-exchange factor 1 (brefeldin A-inhibited) [Source:HGNC Symbol;Acc:15772]",
				"ENSG00000112039	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112039	FANCE	Fanconi anemia, complementation group E [Source:HGNC Symbol;Acc:3586]",
				"ENSG00000188827	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188827	SLX4	SLX4 structure-specific endonuclease subunit homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23845]",
				"ENSG00000112874	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112874	NUDT12	nudix (nucleoside diphosphate linked moiety X)-type motif 12 [Source:HGNC Symbol;Acc:18826]",
				"ENSG00000115239	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115239	ASB3	ankyrin repeat and SOCS box containing 3 [Source:HGNC Symbol;Acc:16013]",
				"ENSG00000171557	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171557	FGG	fibrinogen gamma chain [Source:HGNC Symbol;Acc:3694]",
				"ENSG00000134258	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134258	VTCN1	V-set domain containing T cell activation inhibitor 1 [Source:HGNC Symbol;Acc:28873]",
				"ENSG00000128487	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128487	SPECC1	sperm antigen with calponin homology and coiled-coil domains 1 [Source:HGNC Symbol;Acc:30615]",
				"ENSG00000119638	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119638	NEK9	NIMA (never in mitosis gene a)- related kinase 9 [Source:HGNC Symbol;Acc:18591]",
				"ENSG00000184517	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184517	ZFP1	zinc finger protein 1 homolog (mouse) [Source:HGNC Symbol;Acc:23328]",
				"ENSG00000120280	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120280	CXorf21	chromosome X open reading frame 21 [Source:HGNC Symbol;Acc:25667]",
				"ENSG00000197563	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197563	PIGN	phosphatidylinositol glycan anchor biosynthesis, class N [Source:HGNC Symbol;Acc:8967]",
				"ENSG00000164089	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164089	AGXT2L1	alanine-glyoxylate aminotransferase 2-like 1 [Source:HGNC Symbol;Acc:14404]",
				"ENSG00000139508	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139508	SLC46A3	solute carrier family 46, member 3 [Source:HGNC Symbol;Acc:27501]",
				"ENSG00000120907	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120907	ADRA1A	adrenoceptor alpha 1A [Source:HGNC Symbol;Acc:277]",
				"ENSG00000168894	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168894	RNF181	ring finger protein 181 [Source:HGNC Symbol;Acc:28037]",
				"ENSG00000163125	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163125	RPRD2	regulation of nuclear pre-mRNA domain containing 2 [Source:HGNC Symbol;Acc:29039]",
				"ENSG00000162616	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162616	DNAJB4	DnaJ (Hsp40) homolog, subfamily B, member 4 [Source:HGNC Symbol;Acc:14886]",
				"ENSG00000168538	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168538	TRAPPC11	trafficking protein particle complex 11 [Source:HGNC Symbol;Acc:25751]",
				"ENSG00000140009	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140009	ESR2	estrogen receptor 2 (ER beta) [Source:HGNC Symbol;Acc:3468]",
				"ENSG00000124701	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124701	APOBEC2	apolipoprotein B mRNA editing enzyme, catalytic polypeptide-like 2 [Source:HGNC Symbol;Acc:605]",
				"ENSG00000125812	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125812	GZF1	GDNF-inducible zinc finger protein 1 [Source:HGNC Symbol;Acc:15808]",
				"ENSG00000121350	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121350	PYROXD1	pyridine nucleotide-disulphide oxidoreductase domain 1 [Source:HGNC Symbol;Acc:26162]",
				"ENSG00000097046	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000097046	CDC7	cell division cycle 7 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1745]",
				"ENSG00000117601	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117601	SERPINC1	serpin peptidase inhibitor, clade C (antithrombin), member 1 [Source:HGNC Symbol;Acc:775]",
				"ENSG00000108771	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108771	DHX58	DEXH (Asp-Glu-X-His) box polypeptide 58 [Source:HGNC Symbol;Acc:29517]",
				"ENSG00000174004	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174004	LRRC33	leucine rich repeat containing 33 [Source:HGNC Symbol;Acc:24613]",
				"ENSG00000188223	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188223	LIN37	lin-37 homolog (C. elegans) [Source:HGNC Symbol;Acc:33234]",
				"ENSG00000095380	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095380	NANS	N-acetylneuraminic acid synthase [Source:HGNC Symbol;Acc:19237]",
				"ENSG00000122299	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122299	ZC3H7A	zinc finger CCCH-type containing 7A [Source:HGNC Symbol;Acc:30959]",
				"ENSG00000167772	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167772	ANGPTL4	angiopoietin-like 4 [Source:HGNC Symbol;Acc:16039]",
				"ENSG00000184564	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184564	SLITRK6	SLIT and NTRK-like family, member 6 [Source:HGNC Symbol;Acc:23503]",
				"ENSG00000081177	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081177	EXD2	exonuclease 3prime-5prime domain containing 2 [Source:HGNC Symbol;Acc:20217]",
				"ENSG00000253710	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000253710	ALG11	asparagine-linked glycosylation 11, alpha-1,2-mannosyltransferase homolog (yeast) [Source:HGNC Symbol;Acc:32456]",
				"ENSG00000143324	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143324	XPR1	xenotropic and polytropic retrovirus receptor 1 [Source:HGNC Symbol;Acc:12827]",
				"ENSG00000176387	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176387	HSD11B2	hydroxysteroid (11-beta) dehydrogenase 2 [Source:HGNC Symbol;Acc:5209]",
				"ENSG00000123427	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123427	METTL21B	methyltransferase like 21B [Source:HGNC Symbol;Acc:24936]",
				"ENSG00000171453	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171453	POLR1C	polymerase (RNA) I polypeptide C, 30kDa [Source:HGNC Symbol;Acc:20194]",
				"ENSG00000145740	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145740	SLC30A5	solute carrier family 30 (zinc transporter), member 5 [Source:HGNC Symbol;Acc:19089]",
				"ENSG00000164125	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164125	FAM198B	family with sequence similarity 198, member B [Source:HGNC Symbol;Acc:25312]",
				"ENSG00000115365	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115365	LANCL1	LanC lantibiotic synthetase component C-like 1 (bacterial) [Source:HGNC Symbol;Acc:6508]",
				"ENSG00000188175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188175	HEPACAM2	HEPACAM family member 2 [Source:HGNC Symbol;Acc:27364]",
				"ENSG00000028528	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000028528	SNX1	sorting nexin 1 [Source:HGNC Symbol;Acc:11172]",
				"ENSG00000118407	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118407	FILIP1	filamin A interacting protein 1 [Source:HGNC Symbol;Acc:21015]",
				"ENSG00000184939	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184939	ZFP90	zinc finger protein 90 homolog (mouse) [Source:HGNC Symbol;Acc:23329]",
				"ENSG00000164220	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164220	F2RL2	coagulation factor II (thrombin) receptor-like 2 [Source:HGNC Symbol;Acc:3539]",
				"ENSG00000128881	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128881	TTBK2	tau tubulin kinase 2 [Source:HGNC Symbol;Acc:19141]",
				"ENSG00000189046	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000189046	ALKBH2	alkB, alkylation repair homolog 2 (E. coli) [Source:HGNC Symbol;Acc:32487]",
				"ENSG00000138615	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138615	CILP	cartilage intermediate layer protein, nucleotide pyrophosphohydrolase [Source:HGNC Symbol;Acc:1980]",
				"ENSG00000013293	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013293	SLC7A14	solute carrier family 7 (orphan transporter), member 14 [Source:HGNC Symbol;Acc:29326]",
				"ENSG00000145708	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145708	CRHBP	corticotropin releasing hormone binding protein [Source:HGNC Symbol;Acc:2356]",
				"ENSG00000165282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165282	PIGO	phosphatidylinositol glycan anchor biosynthesis, class O [Source:HGNC Symbol;Acc:23215]",
				"ENSG00000124574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124574	ABCC10	ATP-binding cassette, sub-family C (CFTR/MRP), member 10 [Source:HGNC Symbol;Acc:52]",
				"ENSG00000108406	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108406	DHX40	DEAH (Asp-Glu-Ala-His) box polypeptide 40 [Source:HGNC Symbol;Acc:18018]",
				"ENSG00000151364	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151364	KCTD14	potassium channel tetramerisation domain containing 14 [Source:HGNC Symbol;Acc:23295]",
				"ENSG00000122966	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122966	CIT	citron (rho-interacting, serine/threonine kinase 21) [Source:HGNC Symbol;Acc:1985]",
				"ENSG00000069702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069702	TGFBR3	transforming growth factor, beta receptor III [Source:HGNC Symbol;Acc:11774]",
				"ENSG00000135521	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135521	LTV1	LTV1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:21173]",
				"ENSG00000181856	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181856	SLC2A4	solute carrier family 2 (facilitated glucose transporter), member 4 [Source:HGNC Symbol;Acc:11009]",
				"ENSG00000055208	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000055208	TAB2	TGF-beta activated kinase 1/MAP3K7 binding protein 2 [Source:HGNC Symbol;Acc:17075]",
				"ENSG00000197594	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197594	ENPP1	ectonucleotide pyrophosphatase/phosphodiesterase 1 [Source:HGNC Symbol;Acc:3356]",
				"ENSG00000172728	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172728	FUT10	fucosyltransferase 10 (alpha (1,3) fucosyltransferase) [Source:HGNC Symbol;Acc:19234]",
				"ENSG00000167695	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167695	FAM57A	family with sequence similarity 57, member A [Source:HGNC Symbol;Acc:29646]",
				"ENSG00000090924	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090924	PLEKHG2	pleckstrin homology domain containing, family G (with RhoGef domain) member 2 [Source:HGNC Symbol;Acc:29515]",
				"ENSG00000130695	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130695	CEP85	centrosomal protein 85kDa [Source:HGNC Symbol;Acc:25309]",
				"ENSG00000112305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112305	SMAP1	small ArfGAP 1 [Source:HGNC Symbol;Acc:19651]",
				"ENSG00000117523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117523	PRRC2C	proline-rich coiled-coil 2C [Source:HGNC Symbol;Acc:24903]",
				"ENSG00000150753	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150753	CCT5	chaperonin containing TCP1, subunit 5 (epsilon) [Source:HGNC Symbol;Acc:1618]",
				"ENSG00000106336	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106336	FBXO24	F-box protein 24 [Source:HGNC Symbol;Acc:13595]",
				"ENSG00000149541	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149541	B3GAT3	beta-1,3-glucuronyltransferase 3 (glucuronosyltransferase I) [Source:HGNC Symbol;Acc:923]",
				"ENSG00000254986	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000254986	DPP3	dipeptidyl-peptidase 3 [Source:HGNC Symbol;Acc:3008]",
				"ENSG00000150938	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150938	CRIM1	cysteine rich transmembrane BMP regulator 1 (chordin-like) [Source:HGNC Symbol;Acc:2359]",
				"ENSG00000242372	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000242372	EIF6	eukaryotic translation initiation factor 6 [Source:HGNC Symbol;Acc:6159]",
				"ENSG00000163933	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163933	RFT1	RFT1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:30220]",
				"ENSG00000014138	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000014138	POLA2	polymerase (DNA directed), alpha 2, accessory subunit [Source:HGNC Symbol;Acc:30073]",
				"ENSG00000162607	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162607	USP1	ubiquitin specific peptidase 1 [Source:HGNC Symbol;Acc:12607]",
				"ENSG00000138134	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138134	STAMBPL1	STAM binding protein-like 1 [Source:HGNC Symbol;Acc:24105]",
				"ENSG00000147255	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147255	IGSF1	immunoglobulin superfamily, member 1 [Source:HGNC Symbol;Acc:5948]",
				"ENSG00000169299	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169299	PGM2	phosphoglucomutase 2 [Source:HGNC Symbol;Acc:8906]",
				"ENSG00000157184	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157184	CPT2	carnitine palmitoyltransferase 2 [Source:HGNC Symbol;Acc:2330]",
				"ENSG00000147138	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147138	GPR174	G protein-coupled receptor 174 [Source:HGNC Symbol;Acc:30245]",
				"ENSG00000167815	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167815	PRDX2	peroxiredoxin 2 [Source:HGNC Symbol;Acc:9353]",
				"ENSG00000163661	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163661	PTX3	pentraxin 3, long [Source:HGNC Symbol;Acc:9692]",
				"ENSG00000084090	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084090	STARD7	StAR-related lipid transfer (START) domain containing 7 [Source:HGNC Symbol;Acc:18063]",
				"ENSG00000138231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138231	DBR1	debranching enzyme homolog 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:15594]",
				"ENSG00000164935	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164935	DCSTAMP	dendrocyte expressed seven transmembrane protein [Source:HGNC Symbol;Acc:18549]",
				"ENSG00000167173	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167173	C15orf39	chromosome 15 open reading frame 39 [Source:HGNC Symbol;Acc:24497]",
				"ENSG00000037637	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000037637	FBXO42	F-box protein 42 [Source:HGNC Symbol;Acc:29249]",
				"ENSG00000064835	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064835	POU1F1	POU class 1 homeobox 1 [Source:HGNC Symbol;Acc:9210]",
				"ENSG00000114200	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114200	BCHE	butyrylcholinesterase [Source:HGNC Symbol;Acc:983]",
				"ENSG00000171169	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171169	NAIF1	nuclear apoptosis inducing factor 1 [Source:HGNC Symbol;Acc:25446]",
				"ENSG00000132467	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132467	UTP3	UTP3, small subunit (SSU) processome component, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:24477]",
				"ENSG00000104635	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104635	SLC39A14	solute carrier family 39 (zinc transporter), member 14 [Source:HGNC Symbol;Acc:20858]",
				"ENSG00000114796	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114796	KLHL24	kelch-like 24 (Drosophila) [Source:HGNC Symbol;Acc:25947]",
				"ENSG00000185739	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185739	SRL	sarcalumenin [Source:HGNC Symbol;Acc:11295]",
				"ENSG00000162702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162702	ZNF281	zinc finger protein 281 [Source:HGNC Symbol;Acc:13075]",
				"ENSG00000132383	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132383	RPA1	replication protein A1, 70kDa [Source:HGNC Symbol;Acc:10289]",
				"ENSG00000160401	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160401	C9orf117	chromosome 9 open reading frame 117 [Source:HGNC Symbol;Acc:27843]",
				"ENSG00000186666	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186666	BCDIN3D	BCDIN3 domain containing [Source:HGNC Symbol;Acc:27050]",
				"ENSG00000180917	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180917	FTSJD1	FtsJ methyltransferase domain containing 1 [Source:HGNC Symbol;Acc:25635]",
				"ENSG00000175063	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175063	UBE2C	ubiquitin-conjugating enzyme E2C [Source:HGNC Symbol;Acc:15937]",
				"ENSG00000102575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102575	ACP5	acid phosphatase 5, tartrate resistant [Source:HGNC Symbol;Acc:124]",
				"ENSG00000125870	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125870	SNRPB2	small nuclear ribonucleoprotein polypeptide B [Source:HGNC Symbol;Acc:11155]",
				"ENSG00000118307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118307	CASC1	cancer susceptibility candidate 1 [Source:HGNC Symbol;Acc:29599]",
				"ENSG00000120053	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120053	GOT1	glutamic-oxaloacetic transaminase 1, soluble (aspartate aminotransferase 1) [Source:HGNC Symbol;Acc:4432]",
				"ENSG00000124702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124702	KLHDC3	kelch domain containing 3 [Source:HGNC Symbol;Acc:20704]",
				"ENSG00000020129	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000020129	NCDN	neurochondrin [Source:HGNC Symbol;Acc:17597]",
				"ENSG00000065361	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065361	ERBB3	v-erb-b2 erythroblastic leukemia viral oncogene homolog 3 (avian) [Source:HGNC Symbol;Acc:3431]",
				"ENSG00000143799	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143799	PARP1	poly (ADP-ribose) polymerase 1 [Source:HGNC Symbol;Acc:270]",
				"ENSG00000118503	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118503	TNFAIP3	tumor necrosis factor, alpha-induced protein 3 [Source:HGNC Symbol;Acc:11896]",
				"ENSG00000127870	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127870	RNF6	ring finger protein (C3H2C3 type) 6 [Source:HGNC Symbol;Acc:10069]",
				"ENSG00000198331	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198331	HYLS1	hydrolethalus syndrome 1 [Source:HGNC Symbol;Acc:26558]",
				"ENSG00000151150	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151150	ANK3	ankyrin 3, node of Ranvier (ankyrin G) [Source:HGNC Symbol;Acc:494]",
				"ENSG00000031698	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000031698	SARS	seryl-tRNA synthetase [Source:HGNC Symbol;Acc:10537]",
				"ENSG00000172175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172175	MALT1	mucosa associated lymphoid tissue lymphoma translocation gene 1 [Source:HGNC Symbol;Acc:6819]",
				"ENSG00000127334	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127334	DYRK2	dual-specificity tyrosine-(Y)-phosphorylation regulated kinase 2 [Source:HGNC Symbol;Acc:3093]",
				"ENSG00000174405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174405	LIG4	ligase IV, DNA, ATP-dependent [Source:HGNC Symbol;Acc:6601]",
				"ENSG00000033030	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000033030	ZCCHC8	zinc finger, CCHC domain containing 8 [Source:HGNC Symbol;Acc:25265]",
				"ENSG00000129460	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129460	NGDN	neuroguidin, EIF4E binding protein [Source:HGNC Symbol;Acc:20271]",
				"ENSG00000119684	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119684	MLH3	mutL homolog 3 (E. coli) [Source:HGNC Symbol;Acc:7128]",
				"ENSG00000185133	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185133	INPP5J	inositol polyphosphate-5-phosphatase J [Source:HGNC Symbol;Acc:8956]",
				"ENSG00000165409	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165409	TSHR	thyroid stimulating hormone receptor [Source:HGNC Symbol;Acc:12373]",
				"ENSG00000176087	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176087	SLC35A4	solute carrier family 35, member A4 [Source:HGNC Symbol;Acc:20753]",
				"ENSG00000090432	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090432	MUL1	mitochondrial E3 ubiquitin protein ligase 1 [Source:HGNC Symbol;Acc:25762]",
				"ENSG00000151617	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151617	EDNRA	endothelin receptor type A [Source:HGNC Symbol;Acc:3179]",
				"ENSG00000134575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134575	ACP2	acid phosphatase 2, lysosomal [Source:HGNC Symbol;Acc:123]",
				"ENSG00000156574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156574	NODAL	nodal homolog (mouse) [Source:HGNC Symbol;Acc:7865]",
				"ENSG00000162231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162231	NXF1	nuclear RNA export factor 1 [Source:HGNC Symbol;Acc:8071]",
				"ENSG00000100445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100445	SDR39U1	short chain dehydrogenase/reductase family 39U, member 1 [Source:HGNC Symbol;Acc:20275]",
				"ENSG00000135297	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135297	MTO1	mitochondrial translation optimization 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:19261]",
				"ENSG00000130803	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130803	ZNF317	zinc finger protein 317 [Source:HGNC Symbol;Acc:13507]",
				"ENSG00000169180	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169180	XPO6	exportin 6 [Source:HGNC Symbol;Acc:19733]",
				"ENSG00000117000	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117000	RLF	rearranged L-myc fusion [Source:HGNC Symbol;Acc:10025]",
				"ENSG00000103932	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103932	RPAP1	RNA polymerase II associated protein 1 [Source:HGNC Symbol;Acc:24567]",
				"ENSG00000090376	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090376	IRAK3	interleukin-1 receptor-associated kinase 3 [Source:HGNC Symbol;Acc:17020]",
				"ENSG00000171988	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171988	JMJD1C	jumonji domain containing 1C [Source:HGNC Symbol;Acc:12313]",
				"ENSG00000155868	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155868	MED7	mediator complex subunit 7 [Source:HGNC Symbol;Acc:2378]",
				"ENSG00000121075	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121075	TBX4	T-box 4 [Source:HGNC Symbol;Acc:11603]",
				"ENSG00000161021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161021	MAML1	mastermind-like 1 (Drosophila) [Source:HGNC Symbol;Acc:13632]",
				"ENSG00000156469	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156469	MTERFD1	MTERF domain containing 1 [Source:HGNC Symbol;Acc:24258]",
				"ENSG00000119383	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119383	PPP2R4	protein phosphatase 2A activator, regulatory subunit 4 [Source:HGNC Symbol;Acc:9308]",
				"ENSG00000198554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198554	WDHD1	WD repeat and HMG-box DNA binding protein 1 [Source:HGNC Symbol;Acc:23170]",
				"ENSG00000166888	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166888	STAT6	signal transducer and activator of transcription 6, interleukin-4 induced [Source:HGNC Symbol;Acc:11368]",
				"ENSG00000129353	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129353	SLC44A2	solute carrier family 44, member 2 [Source:HGNC Symbol;Acc:17292]",
				"ENSG00000186416	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186416	NKRF	NFKB repressing factor [Source:HGNC Symbol;Acc:19374]",
				"ENSG00000132801	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132801	ZSWIM3	zinc finger, SWIM-type containing 3 [Source:HGNC Symbol;Acc:16157]",
				"ENSG00000196118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196118	C16orf93	chromosome 16 open reading frame 93 [Source:HGNC Symbol;Acc:28078]",
				"ENSG00000010818	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010818	HIVEP2	human immunodeficiency virus type I enhancer binding protein 2 [Source:HGNC Symbol;Acc:4921]",
				"ENSG00000159063	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159063	ALG8	asparagine-linked glycosylation 8, alpha-1,3-glucosyltransferase homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23161]",
				"ENSG00000111581	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111581	NUP107	nucleoporin 107kDa [Source:HGNC Symbol;Acc:29914]",
				"ENSG00000121931	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121931	LRIF1	ligand dependent nuclear receptor interacting factor 1 [Source:HGNC Symbol;Acc:30299]",
				"ENSG00000198624	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198624	CCDC69	coiled-coil domain containing 69 [Source:HGNC Symbol;Acc:24487]",
				"ENSG00000128510	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128510	CPA4	carboxypeptidase A4 [Source:HGNC Symbol;Acc:15740]",
				"ENSG00000156050	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156050	FAM161B	family with sequence similarity 161, member B [Source:HGNC Symbol;Acc:19854]",
				"ENSG00000005156	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005156	LIG3	ligase III, DNA, ATP-dependent [Source:HGNC Symbol;Acc:6600]",
				"ENSG00000152455	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152455	SUV39H2	suppressor of variegation 3-9 homolog 2 (Drosophila) [Source:HGNC Symbol;Acc:17287]",
				"ENSG00000124201	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124201	ZNFX1	zinc finger, NFX1-type containing 1 [Source:HGNC Symbol;Acc:29271]",
				"ENSG00000163833	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163833	FBXO40	F-box protein 40 [Source:HGNC Symbol;Acc:29816]",
				"ENSG00000163092	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163092	XIRP2	xin actin-binding repeat containing 2 [Source:HGNC Symbol;Acc:14303]",
				"ENSG00000114867	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114867	EIF4G1	eukaryotic translation initiation factor 4 gamma, 1 [Source:HGNC Symbol;Acc:3296]",
				"ENSG00000188493	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188493	C19orf54	chromosome 19 open reading frame 54 [Source:HGNC Symbol;Acc:24758]",
				"ENSG00000179262	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179262	RAD23A	RAD23 homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:9812]",
				"ENSG00000164002	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164002	DEM1	defects in morphology 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:26115]",
				"ENSG00000095739	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095739	BAMBI	BMP and activin membrane-bound inhibitor homolog (Xenopus laevis) [Source:HGNC Symbol;Acc:30251]",
				"ENSG00000163141	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163141	BNIPL	BCL2/adenovirus E1B 19kD interacting protein like [Source:HGNC Symbol;Acc:16976]",
				"ENSG00000116679	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116679	IVNS1ABP	influenza virus NS1A binding protein [Source:HGNC Symbol;Acc:16951]",
				"ENSG00000126107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126107	HECTD3	HECT domain containing E3 ubiquitin protein ligase 3 [Source:HGNC Symbol;Acc:26117]",
				"ENSG00000147872	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147872	PLIN2	perilipin 2 [Source:HGNC Symbol;Acc:248]",
				"ENSG00000102908	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102908	NFAT5	nuclear factor of activated T-cells 5, tonicity-responsive [Source:HGNC Symbol;Acc:7774]",
				"ENSG00000105663	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105663	MLL4	Histone-lysine N-methyltransferase MLL4  [Source:UniProtKB/Swiss-Prot;Acc:Q9UMN6]",
				"ENSG00000073282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000073282	TP63	tumor protein p63 [Source:HGNC Symbol;Acc:15979]",
				"ENSG00000171135	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171135	JAGN1	jagunal homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:26926]",
				"ENSG00000154783	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154783	FGD5	FYVE, RhoGEF and PH domain containing 5 [Source:HGNC Symbol;Acc:19117]",
				"ENSG00000172613	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172613	RAD9A	RAD9 homolog A (S. pombe) [Source:HGNC Symbol;Acc:9827]",
				"ENSG00000103994	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103994	ZFP106	zinc finger protein 106 homolog (mouse) [Source:HGNC Symbol;Acc:23240]",
				"ENSG00000169217	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169217	CD2BP2	CD2 (cytoplasmic tail) binding protein 2 [Source:HGNC Symbol;Acc:1656]",
				"ENSG00000123562	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123562	MORF4L2	mortality factor 4 like 2 [Source:HGNC Symbol;Acc:16849]",
				"ENSG00000165861	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165861	ZFYVE1	zinc finger, FYVE domain containing 1 [Source:HGNC Symbol;Acc:13180]",
				"ENSG00000180979	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180979	LRRC57	leucine rich repeat containing 57 [Source:HGNC Symbol;Acc:26719]",
				"ENSG00000115267	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115267	IFIH1	interferon induced with helicase C domain 1 [Source:HGNC Symbol;Acc:18873]",
				"ENSG00000038358	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000038358	EDC4	enhancer of mRNA decapping 4 [Source:HGNC Symbol;Acc:17157]",
				"ENSG00000147526	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147526	TACC1	transforming, acidic coiled-coil containing protein 1 [Source:HGNC Symbol;Acc:11522]",
				"ENSG00000175782	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175782	SLC35E3	solute carrier family 35, member E3 [Source:HGNC Symbol;Acc:20864]",
				"ENSG00000171819	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171819	ANGPTL7	angiopoietin-like 7 [Source:HGNC Symbol;Acc:24078]",
				"ENSG00000109689	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109689	STIM2	stromal interaction molecule 2 [Source:HGNC Symbol;Acc:19205]",
				"ENSG00000080819	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080819	CPOX	coproporphyrinogen oxidase [Source:HGNC Symbol;Acc:2321]",
				"ENSG00000101745	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101745	ANKRD12	ankyrin repeat domain 12 [Source:HGNC Symbol;Acc:29135]",
				"ENSG00000168883	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168883	USP39	ubiquitin specific peptidase 39 [Source:HGNC Symbol;Acc:20071]",
				"ENSG00000180263	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180263	FGD6	FYVE, RhoGEF and PH domain containing 6 [Source:HGNC Symbol;Acc:21740]",
				"ENSG00000001561	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000001561	ENPP4	ectonucleotide pyrophosphatase/phosphodiesterase 4 (putative) [Source:HGNC Symbol;Acc:3359]",
				"ENSG00000106952	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106952	TNFSF8	tumor necrosis factor (ligand) superfamily, member 8 [Source:HGNC Symbol;Acc:11938]",
				"ENSG00000157224	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157224	CLDN12	claudin 12 [Source:HGNC Symbol;Acc:2034]",
				"ENSG00000127311	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127311	HELB	helicase (DNA) B [Source:HGNC Symbol;Acc:17196]",
				"ENSG00000213523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213523	SRA1	steroid receptor RNA activator 1 [Source:HGNC Symbol;Acc:11281]",
				"ENSG00000184984	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184984	CHRM5	cholinergic receptor, muscarinic 5 [Source:HGNC Symbol;Acc:1954]",
				"ENSG00000162623	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162623	TYW3	tRNA-yW synthesizing protein 3 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:24757]",
				"ENSG00000013375	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013375	PGM3	phosphoglucomutase 3 [Source:HGNC Symbol;Acc:8907]",
				"ENSG00000175595	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175595	ERCC4	excision repair cross-complementing rodent repair deficiency, complementation group 4 [Source:HGNC Symbol;Acc:3436]",
				"ENSG00000114841	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114841	DNAH1	dynein, axonemal, heavy chain 1 [Source:HGNC Symbol;Acc:2940]",
				"ENSG00000105223	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105223	PLD3	phospholipase D family, member 3 [Source:HGNC Symbol;Acc:17158]",
				"ENSG00000177511	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177511	ST8SIA3	ST8 alpha-N-acetyl-neuraminide alpha-2,8-sialyltransferase 3 [Source:HGNC Symbol;Acc:14269]",
				"ENSG00000092853	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092853	CLSPN	claspin [Source:HGNC Symbol;Acc:19715]",
				"ENSG00000168907	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168907	PLA2G4F	phospholipase A2, group IVF [Source:HGNC Symbol;Acc:27396]",
				"ENSG00000060140	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000060140	STYK1	serine/threonine/tyrosine kinase 1 [Source:HGNC Symbol;Acc:18889]",
				"ENSG00000107862	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107862	GBF1	golgi brefeldin A resistant guanine nucleotide exchange factor 1 [Source:HGNC Symbol;Acc:4181]",
				"ENSG00000145723	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145723	GIN1	gypsy retrotransposon integrase 1 [Source:HGNC Symbol;Acc:25959]",
				"ENSG00000172426	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172426	RSPH9	radial spoke head 9 homolog (Chlamydomonas) [Source:HGNC Symbol;Acc:21057]",
				"ENSG00000135587	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135587	SMPD2	sphingomyelin phosphodiesterase 2, neutral membrane (neutral sphingomyelinase) [Source:HGNC Symbol;Acc:11121]",
				"ENSG00000105677	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105677	TMEM147	transmembrane protein 147 [Source:HGNC Symbol;Acc:30414]",
				"ENSG00000185002	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185002	RFX6	regulatory factor X, 6 [Source:HGNC Symbol;Acc:21478]",
				"ENSG00000132612	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132612	VPS4A	vacuolar protein sorting 4 homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:13488]",
				"ENSG00000077235	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000077235	GTF3C1	general transcription factor IIIC, polypeptide 1, alpha 220kDa [Source:HGNC Symbol;Acc:4664]",
				"ENSG00000115657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115657	ABCB6	ATP-binding cassette, sub-family B (MDR/TAP), member 6 [Source:HGNC Symbol;Acc:47]",
				"ENSG00000129151	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129151	BBOX1	butyrobetaine (gamma), 2-oxoglutarate dioxygenase (gamma-butyrobetaine hydroxylase) 1 [Source:HGNC Symbol;Acc:964]",
				"ENSG00000185652	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185652	NTF3	neurotrophin 3 [Source:HGNC Symbol;Acc:8023]",
				"ENSG00000159131	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159131	GART	phosphoribosylglycinamide formyltransferase, phosphoribosylglycinamide synthetase, phosphoribosylaminoimidazole synthetase [Source:HGNC Symbol;Acc:4163]",
				"ENSG00000130307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130307	USHBP1	Usher syndrome 1C binding protein 1 [Source:HGNC Symbol;Acc:24058]",
				"ENSG00000163655	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163655	GMPS	guanine monphosphate synthetase [Source:HGNC Symbol;Acc:4378]",
				"ENSG00000144659	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144659	SLC25A38	solute carrier family 25, member 38 [Source:HGNC Symbol;Acc:26054]",
				"ENSG00000138071	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138071	ACTR2	ARP2 actin-related protein 2 homolog (yeast) [Source:HGNC Symbol;Acc:169]",
				"ENSG00000197912	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197912	SPG7	spastic paraplegia 7 (pure and complicated autosomal recessive) [Source:HGNC Symbol;Acc:11237]",
				"ENSG00000213859	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213859	KCTD11	potassium channel tetramerisation domain containing 11 [Source:HGNC Symbol;Acc:21302]",
				"ENSG00000165972	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165972	CCDC38	coiled-coil domain containing 38 [Source:HGNC Symbol;Acc:26843]",
				"ENSG00000112984	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112984	KIF20A	kinesin family member 20A [Source:HGNC Symbol;Acc:9787]",
				"ENSG00000123607	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123607	TTC21B	tetratricopeptide repeat domain 21B [Source:HGNC Symbol;Acc:25660]",
				"ENSG00000112339	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112339	HBS1L	HBS1-like (S. cerevisiae) [Source:HGNC Symbol;Acc:4834]",
				"ENSG00000123405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123405	NFE2	nuclear factor (erythroid-derived 2), 45kDa [Source:HGNC Symbol;Acc:7780]",
				"ENSG00000100104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100104	SRRD	SRR1 domain containing [Source:HGNC Symbol;Acc:33910]",
				"ENSG00000110060	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110060	PUS3	pseudouridylate synthase 3 [Source:HGNC Symbol;Acc:25461]",
				"ENSG00000132259	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132259	CNGA4	cyclic nucleotide gated channel alpha 4 [Source:HGNC Symbol;Acc:2152]",
				"ENSG00000213380	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213380	COG8	component of oligomeric golgi complex 8 [Source:HGNC Symbol;Acc:18623]",
				"ENSG00000164366	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164366	CCDC127	coiled-coil domain containing 127 [Source:HGNC Symbol;Acc:30520]",
				"ENSG00000167074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167074	TEF	thyrotrophic embryonic factor [Source:HGNC Symbol;Acc:11722]",
				"ENSG00000187288	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187288	CIDEC	cell death-inducing DFFA-like effector c [Source:HGNC Symbol;Acc:24229]",
				"ENSG00000117593	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117593	DARS2	aspartyl-tRNA synthetase 2, mitochondrial [Source:HGNC Symbol;Acc:25538]",
				"ENSG00000169154	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169154	GOT1L1	glutamic-oxaloacetic transaminase 1-like 1 [Source:HGNC Symbol;Acc:28487]",
				"ENSG00000213928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213928	IRF9	interferon regulatory factor 9 [Source:HGNC Symbol;Acc:6131]",
				"ENSG00000172059	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172059	KLF11	Kruppel-like factor 11 [Source:HGNC Symbol;Acc:11811]",
				"ENSG00000130119	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130119	GNL3L	guanine nucleotide binding protein-like 3 (nucleolar)-like [Source:HGNC Symbol;Acc:25553]",
				"ENSG00000070601	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070601	FRMPD1	FERM and PDZ domain containing 1 [Source:HGNC Symbol;Acc:29159]",
				"ENSG00000125484	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125484	GTF3C4	general transcription factor IIIC, polypeptide 4, 90kDa [Source:HGNC Symbol;Acc:4667]",
				"ENSG00000124151	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124151	NCOA3	nuclear receptor coactivator 3 [Source:HGNC Symbol;Acc:7670]",
				"ENSG00000144120	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144120	TMEM177	transmembrane protein 177 [Source:HGNC Symbol;Acc:28143]",
				"ENSG00000174576	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174576	NPAS4	neuronal PAS domain protein 4 [Source:HGNC Symbol;Acc:18983]",
				"ENSG00000179409	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179409	GEMIN4	gem (nuclear organelle) associated protein 4 [Source:HGNC Symbol;Acc:15717]",
				"ENSG00000105221	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105221	AKT2	v-akt murine thymoma viral oncogene homolog 2 [Source:HGNC Symbol;Acc:392]",
				"ENSG00000164088	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164088	PPM1M	protein phosphatase, Mg2+/Mn2+ dependent, 1M [Source:HGNC Symbol;Acc:26506]",
				"ENSG00000144712	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144712	CAND2	cullin-associated and neddylation-dissociated 2 (putative) [Source:HGNC Symbol;Acc:30689]",
				"ENSG00000165118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165118	C9orf64	chromosome 9 open reading frame 64 [Source:HGNC Symbol;Acc:28144]",
				"ENSG00000166135	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166135	HIF1AN	hypoxia inducible factor 1, alpha subunit inhibitor [Source:HGNC Symbol;Acc:17113]",
				"ENSG00000163513	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163513	TGFBR2	transforming growth factor, beta receptor II (70/80kDa) [Source:HGNC Symbol;Acc:11773]",
				"ENSG00000108950	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108950	FAM20A	family with sequence similarity 20, member A [Source:HGNC Symbol;Acc:23015]",
				"ENSG00000110786	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110786	PTPN5	protein tyrosine phosphatase, non-receptor type 5 (striatum-enriched) [Source:HGNC Symbol;Acc:9657]",
				"ENSG00000113231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113231	PDE8B	phosphodiesterase 8B [Source:HGNC Symbol;Acc:8794]",
				"ENSG00000166450	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166450	PRTG	protogenin [Source:HGNC Symbol;Acc:26373]",
				"ENSG00000069122	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069122	GPR116	G protein-coupled receptor 116 [Source:HGNC Symbol;Acc:19030]",
				"ENSG00000095139	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095139	ARCN1	archain 1 [Source:HGNC Symbol;Acc:649]",
				"ENSG00000150093	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150093	ITGB1	integrin, beta 1 (fibronectin receptor, beta polypeptide, antigen CD29 includes MDF2, MSK12) [Source:HGNC Symbol;Acc:6153]",
				"ENSG00000172936	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172936	MYD88	myeloid differentiation primary response gene (88) [Source:HGNC Symbol;Acc:7562]",
				"ENSG00000151689	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151689	INPP1	inositol polyphosphate-1-phosphatase [Source:HGNC Symbol;Acc:6071]",
				"ENSG00000119777	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119777	TMEM214	transmembrane protein 214 [Source:HGNC Symbol;Acc:25983]",
				"ENSG00000168591	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168591	TMUB2	transmembrane and ubiquitin-like domain containing 2 [Source:HGNC Symbol;Acc:28459]",
				"ENSG00000114805	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114805	PLCH1	phospholipase C, eta 1 [Source:HGNC Symbol;Acc:29185]",
				"ENSG00000149474	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149474	CSRP2BP	CSRP2 binding protein [Source:HGNC Symbol;Acc:15904]",
				"ENSG00000105851	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105851	PIK3CG	phosphoinositide-3-kinase, catalytic, gamma polypeptide [Source:HGNC Symbol;Acc:8978]",
				"ENSG00000166439	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166439	RNF169	ring finger protein 169 [Source:HGNC Symbol;Acc:26961]",
				"ENSG00000189091	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000189091	SF3B3	splicing factor 3b, subunit 3, 130kDa [Source:HGNC Symbol;Acc:10770]",
				"ENSG00000108423	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108423	TUBD1	tubulin, delta 1 [Source:HGNC Symbol;Acc:16811]",
				"ENSG00000150347	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150347	ARID5B	AT rich interactive domain 5B (MRF1-like) [Source:HGNC Symbol;Acc:17362]",
				"ENSG00000173114	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173114	LRRN3	leucine rich repeat neuronal 3 [Source:HGNC Symbol;Acc:17200]",
				"ENSG00000173846	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173846	PLK3	polo-like kinase 3 [Source:HGNC Symbol;Acc:2154]",
				"ENSG00000145041	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145041	VPRBP	Vpr (HIV-1) binding protein [Source:HGNC Symbol;Acc:30911]",
				"ENSG00000139517	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139517	LNX2	ligand of numb-protein X 2 [Source:HGNC Symbol;Acc:20421]",
				"ENSG00000126653	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126653	NSRP1	nuclear speckle splicing regulatory protein 1 [Source:HGNC Symbol;Acc:25305]",
				"ENSG00000181904	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181904	C5orf24	chromosome 5 open reading frame 24 [Source:HGNC Symbol;Acc:26746]",
				"ENSG00000182173	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182173	TSEN54	tRNA splicing endonuclease 54 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:27561]",
				"ENSG00000136937	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136937	NCBP1	nuclear cap binding protein subunit 1, 80kDa [Source:HGNC Symbol;Acc:7658]",
				"ENSG00000185231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185231	MC2R	melanocortin 2 receptor (adrenocorticotropic hormone) [Source:HGNC Symbol;Acc:6930]",
				"ENSG00000113749	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113749	HRH2	histamine receptor H2 [Source:HGNC Symbol;Acc:5183]",
				"ENSG00000072121	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072121	ZFYVE26	zinc finger, FYVE domain containing 26 [Source:HGNC Symbol;Acc:20761]",
				"ENSG00000107263	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107263	RAPGEF1	Rap guanine nucleotide exchange factor (GEF) 1 [Source:HGNC Symbol;Acc:4568]",
				"ENSG00000132837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132837	DMGDH	dimethylglycine dehydrogenase [Source:HGNC Symbol;Acc:24475]",
				"ENSG00000154645	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154645	CHODL	chondrolectin [Source:HGNC Symbol;Acc:17807]",
				"ENSG00000197587	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197587	DMBX1	diencephalon/mesencephalon homeobox 1 [Source:HGNC Symbol;Acc:19026]",
				"ENSG00000135924	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135924	DNAJB2	DnaJ (Hsp40) homolog, subfamily B, member 2 [Source:HGNC Symbol;Acc:5228]",
				"ENSG00000133816	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133816	MICAL2	microtubule associated monoxygenase, calponin and LIM domain containing 2 [Source:HGNC Symbol;Acc:24693]",
				"ENSG00000134253	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134253	TRIM45	tripartite motif containing 45 [Source:HGNC Symbol;Acc:19018]",
				"ENSG00000111727	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111727	HCFC2	host cell factor C2 [Source:HGNC Symbol;Acc:24972]",
				"ENSG00000198018	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198018	ENTPD7	ectonucleoside triphosphate diphosphohydrolase 7 [Source:HGNC Symbol;Acc:19745]",
				"ENSG00000164823	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164823	OSGIN2	oxidative stress induced growth inhibitor family member 2 [Source:HGNC Symbol;Acc:1355]",
				"ENSG00000151445	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151445	C14orf133	chromosome 14 open reading frame 133 [Source:HGNC Symbol;Acc:20347]",
				"ENSG00000104221	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104221	BRF2	BRF2, subunit of RNA polymerase III transcription initiation factor, BRF1-like [Source:HGNC Symbol;Acc:17298]",
				"ENSG00000134900	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134900	TPP2	tripeptidyl peptidase II [Source:HGNC Symbol;Acc:12016]",
				"ENSG00000181035	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181035	SLC25A42	solute carrier family 25, member 42 [Source:HGNC Symbol;Acc:28380]",
				"ENSG00000167721	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167721	TSR1	TSR1, 20S rRNA accumulation, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25542]",
				"ENSG00000108828	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108828	VAT1	vesicle amine transport protein 1 homolog (T. californica) [Source:HGNC Symbol;Acc:16919]",
				"ENSG00000182934	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182934	SRPR	signal recognition particle receptor (docking protein) [Source:HGNC Symbol;Acc:11307]",
				"ENSG00000170915	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170915	PAQR8	progestin and adipoQ receptor family member VIII [Source:HGNC Symbol;Acc:15708]",
				"ENSG00000121057	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121057	AKAP1	A kinase (PRKA) anchor protein 1 [Source:HGNC Symbol;Acc:367]",
				"ENSG00000124155	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124155	PIGT	phosphatidylinositol glycan anchor biosynthesis, class T [Source:HGNC Symbol;Acc:14938]",
				"ENSG00000168297	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168297	PXK	PX domain containing serine/threonine kinase [Source:HGNC Symbol;Acc:23326]",
				"ENSG00000163659	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163659	TIPARP	TCDD-inducible poly(ADP-ribose) polymerase [Source:HGNC Symbol;Acc:23696]",
				"ENSG00000137802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137802	MAPKBP1	mitogen-activated protein kinase binding protein 1 [Source:HGNC Symbol;Acc:29536]",
				"ENSG00000178764	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178764	ZHX2	zinc fingers and homeoboxes 2 [Source:HGNC Symbol;Acc:18513]",
				"ENSG00000152779	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152779	SLC16A12	solute carrier family 16, member 12 (monocarboxylic acid transporter 12) [Source:HGNC Symbol;Acc:23094]",
				"ENSG00000164128	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164128	NPY1R	neuropeptide Y receptor Y1 [Source:HGNC Symbol;Acc:7956]",
				"ENSG00000102802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102802	C13orf33	chromosome 13 open reading frame 33 [Source:HGNC Symbol;Acc:25926]",
				"ENSG00000116191	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116191	RALGPS2	Ral GEF with PH domain and SH3 binding motif 2 [Source:HGNC Symbol;Acc:30279]",
				"ENSG00000081320	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081320	STK17B	serine/threonine kinase 17b [Source:HGNC Symbol;Acc:11396]",
				"ENSG00000135249	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135249	RINT1	RAD50 interactor 1 [Source:HGNC Symbol;Acc:21876]",
				"ENSG00000198917	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198917	C9orf114	chromosome 9 open reading frame 114 [Source:HGNC Symbol;Acc:26933]",
				"ENSG00000115266	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115266	APC2	adenomatosis polyposis coli 2 [Source:HGNC Symbol;Acc:24036]",
				"ENSG00000163811	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163811	WDR43	WD repeat domain 43 [Source:HGNC Symbol;Acc:28945]",
				"ENSG00000047936	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000047936	ROS1	c-ros oncogene 1 , receptor tyrosine kinase [Source:HGNC Symbol;Acc:10261]",
				"ENSG00000112855	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112855	HARS2	histidyl-tRNA synthetase 2, mitochondrial (putative) [Source:HGNC Symbol;Acc:4817]",
				"ENSG00000172061	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172061	LRRC15	leucine rich repeat containing 15 [Source:HGNC Symbol;Acc:20818]",
				"ENSG00000160062	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160062	ZBTB8A	zinc finger and BTB domain containing 8A [Source:HGNC Symbol;Acc:24172]",
				"ENSG00000084623	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084623	EIF3I	eukaryotic translation initiation factor 3, subunit I [Source:HGNC Symbol;Acc:3272]",
				"ENSG00000131043	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131043	C20orf4	chromosome 20 open reading frame 4 [Source:HGNC Symbol;Acc:15886]",
				"ENSG00000108387	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108387	36769	septin 4 [Source:HGNC Symbol;Acc:9165]",
				"ENSG00000104231	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104231	ZFAND1	zinc finger, AN1-type domain 1 [Source:HGNC Symbol;Acc:25858]",
				"ENSG00000198408	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198408	MGEA5	meningioma expressed antigen 5 (hyaluronidase) [Source:HGNC Symbol;Acc:7056]",
				"ENSG00000009950	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000009950	MLXIPL	MLX interacting protein-like [Source:HGNC Symbol;Acc:12744]",
				"ENSG00000138074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138074	SLC5A6	solute carrier family 5 (sodium-dependent vitamin transporter), member 6 [Source:HGNC Symbol;Acc:11041]",
				"ENSG00000153575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153575	TUBGCP5	tubulin, gamma complex associated protein 5 [Source:HGNC Symbol;Acc:18600]",
				"ENSG00000124214	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124214	STAU1	staufen, RNA binding protein, homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:11370]",
				"ENSG00000174282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174282	ZBTB4	zinc finger and BTB domain containing 4 [Source:HGNC Symbol;Acc:23847]",
				"ENSG00000135519	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135519	KCNH3	potassium voltage-gated channel, subfamily H (eag-related), member 3 [Source:HGNC Symbol;Acc:6252]",
				"ENSG00000175104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175104	TRAF6	TNF receptor-associated factor 6, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:12036]",
				"ENSG00000112701	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112701	SENP6	SUMO1/sentrin specific peptidase 6 [Source:HGNC Symbol;Acc:20944]",
				"ENSG00000105639	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105639	JAK3	Janus kinase 3 [Source:HGNC Symbol;Acc:6193]",
				"ENSG00000009830	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000009830	POMT2	protein-O-mannosyltransferase 2 [Source:HGNC Symbol;Acc:19743]",
				"ENSG00000169189	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169189	NSMCE1	non-SMC element 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:29897]",
				"ENSG00000104368	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104368	PLAT	plasminogen activator, tissue [Source:HGNC Symbol;Acc:9051]",
				"ENSG00000161011	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161011	SQSTM1	sequestosome 1 [Source:HGNC Symbol;Acc:11280]",
				"ENSG00000115486	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115486	GGCX	gamma-glutamyl carboxylase [Source:HGNC Symbol;Acc:4247]",
				"ENSG00000174640	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174640	SLCO2A1	solute carrier organic anion transporter family, member 2A1 [Source:HGNC Symbol;Acc:10955]",
				"ENSG00000135407	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135407	AVIL	advillin [Source:HGNC Symbol;Acc:14188]",
				"ENSG00000145495	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145495	37315	membrane-associated ring finger (C3HC4) 6, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:30550]",
				"ENSG00000164715	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164715	LMTK2	lemur tyrosine kinase 2 [Source:HGNC Symbol;Acc:17880]",
				"ENSG00000111602	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111602	TIMELESS	timeless homolog (Drosophila) [Source:HGNC Symbol;Acc:11813]",
				"ENSG00000146733	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146733	PSPH	phosphoserine phosphatase [Source:HGNC Symbol;Acc:9577]",
				"ENSG00000134574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134574	DDB2	damage-specific DNA binding protein 2, 48kDa [Source:HGNC Symbol;Acc:2718]",
				"ENSG00000178921	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178921	PFAS	phosphoribosylformylglycinamidine synthase [Source:HGNC Symbol;Acc:8863]",
				"ENSG00000163938	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163938	GNL3	guanine nucleotide binding protein-like 3 (nucleolar) [Source:HGNC Symbol;Acc:29931]",
				"ENSG00000143702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143702	CEP170	centrosomal protein 170kDa [Source:HGNC Symbol;Acc:28920]",
				"ENSG00000172594	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172594	SMPDL3A	sphingomyelin phosphodiesterase, acid-like 3A [Source:HGNC Symbol;Acc:17389]",
				"ENSG00000151014	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151014	CCRN4L	CCR4 carbon catabolite repression 4-like (S. cerevisiae) [Source:HGNC Symbol;Acc:14254]",
				"ENSG00000158480	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158480	SPATA2	spermatogenesis associated 2 [Source:HGNC Symbol;Acc:14681]",
				"ENSG00000144837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144837	PLA1A	phospholipase A1 member A [Source:HGNC Symbol;Acc:17661]",
				"ENSG00000178385	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178385	PLEKHM3	pleckstrin homology domain containing, family M, member 3 [Source:HGNC Symbol;Acc:34006]",
				"ENSG00000137962	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137962	ARHGAP29	Rho GTPase activating protein 29 [Source:HGNC Symbol;Acc:30207]",
				"ENSG00000139926	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139926	FRMD6	FERM domain containing 6 [Source:HGNC Symbol;Acc:19839]",
				"ENSG00000106546	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106546	AHR	aryl hydrocarbon receptor [Source:HGNC Symbol;Acc:348]",
				"ENSG00000157856	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157856	CCDC164	coiled-coil domain containing 164 [Source:HGNC Symbol;Acc:24245]",
				"ENSG00000071246	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071246	VASH1	vasohibin 1 [Source:HGNC Symbol;Acc:19964]",
				"ENSG00000173653	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173653	RCE1	RCE1 homolog, prenyl protein protease (S. cerevisiae) [Source:HGNC Symbol;Acc:13721]",
				"ENSG00000088305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088305	DNMT3B	DNA (cytosine-5-)-methyltransferase 3 beta [Source:HGNC Symbol;Acc:2979]",
				"ENSG00000147650	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147650	LRP12	low density lipoprotein receptor-related protein 12 [Source:HGNC Symbol;Acc:31708]",
				"ENSG00000127993	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127993	RBM48	RNA binding motif protein 48 [Source:HGNC Symbol;Acc:21785]",
				"ENSG00000105700	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105700	KXD1	KxDL motif containing 1 [Source:HGNC Symbol;Acc:28420]",
				"ENSG00000102606	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102606	ARHGEF7	Rho guanine nucleotide exchange factor (GEF) 7 [Source:HGNC Symbol;Acc:15607]",
				"ENSG00000168386	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168386	FILIP1L	filamin A interacting protein 1-like [Source:HGNC Symbol;Acc:24589]",
				"ENSG00000107614	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107614	TRDMT1	tRNA aspartic acid methyltransferase 1 [Source:HGNC Symbol;Acc:2977]",
				"ENSG00000107937	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107937	GTPBP4	GTP binding protein 4 [Source:HGNC Symbol;Acc:21535]",
				"ENSG00000143442	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143442	POGZ	pogo transposable element with ZNF domain [Source:HGNC Symbol;Acc:18801]",
				"ENSG00000020922	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000020922	MRE11A	MRE11 meiotic recombination 11 homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:7230]",
				"ENSG00000108848	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108848	LUC7L3	LUC7-like 3 (S. cerevisiae) [Source:HGNC Symbol;Acc:24309]",
				"ENSG00000172071	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172071	EIF2AK3	eukaryotic translation initiation factor 2-alpha kinase 3 [Source:HGNC Symbol;Acc:3255]",
				"ENSG00000137547	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137547	MRPL15	mitochondrial ribosomal protein L15 [Source:HGNC Symbol;Acc:14054]",
				"ENSG00000163536	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163536	SERPINI1	serpin peptidase inhibitor, clade I (neuroserpin), member 1 [Source:HGNC Symbol;Acc:8943]",
				"ENSG00000139567	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139567	ACVRL1	activin A receptor type II-like 1 [Source:HGNC Symbol;Acc:175]",
				"ENSG00000119523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119523	ALG2	asparagine-linked glycosylation 2, alpha-1,3-mannosyltransferase homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23159]",
				"ENSG00000006555	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006555	TTC22	tetratricopeptide repeat domain 22 [Source:HGNC Symbol;Acc:26067]",
				"ENSG00000177548	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177548	RABEP2	rabaptin, RAB GTPase binding effector protein 2 [Source:HGNC Symbol;Acc:24817]",
				"ENSG00000124279	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124279	FASTKD3	FAST kinase domains 3 [Source:HGNC Symbol;Acc:28758]",
				"ENSG00000085982	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085982	USP40	ubiquitin specific peptidase 40 [Source:HGNC Symbol;Acc:20069]",
				"ENSG00000140287	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140287	HDC	histidine decarboxylase [Source:HGNC Symbol;Acc:4855]",
				"ENSG00000105778	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105778	AVL9	AVL9 homolog (S. cerevisiase) [Source:HGNC Symbol;Acc:28994]",
				"ENSG00000163879	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163879	DNALI1	dynein, axonemal, light intermediate chain 1 [Source:HGNC Symbol;Acc:14353]",
				"ENSG00000204174	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204174	PPYR1	pancreatic polypeptide receptor 1 [Source:HGNC Symbol;Acc:9329]",
				"ENSG00000023041	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000023041	ZDHHC6	zinc finger, DHHC-type containing 6 [Source:HGNC Symbol;Acc:19160]",
				"ENSG00000138449	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138449	SLC40A1	solute carrier family 40 (iron-regulated transporter), member 1 [Source:HGNC Symbol;Acc:10909]",
				"ENSG00000113296	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113296	THBS4	thrombospondin 4 [Source:HGNC Symbol;Acc:11788]",
				"ENSG00000113282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113282	CLINT1	clathrin interactor 1 [Source:HGNC Symbol;Acc:23186]",
				"ENSG00000159579	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159579	RSPRY1	ring finger and SPRY domain containing 1 [Source:HGNC Symbol;Acc:29420]",
				"ENSG00000144115	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144115	THNSL2	threonine synthase-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:25602]",
				"ENSG00000115970	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115970	THADA	thyroid adenoma associated [Source:HGNC Symbol;Acc:19217]",
				"ENSG00000124159	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124159	MATN4	matrilin 4 [Source:HGNC Symbol;Acc:6910]",
				"ENSG00000119688	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119688	ABCD4	ATP-binding cassette, sub-family D (ALD), member 4 [Source:HGNC Symbol;Acc:68]",
				"ENSG00000185019	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185019	UBOX5	U-box domain containing 5 [Source:HGNC Symbol;Acc:17777]",
				"ENSG00000127837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127837	AAMP	angio-associated, migratory cell protein [Source:HGNC Symbol;Acc:18]",
				"ENSG00000103042	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103042	SLC38A7	solute carrier family 38, member 7 [Source:HGNC Symbol;Acc:25582]",
				"ENSG00000198853	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198853	RUSC2	RUN and SH3 domain containing 2 [Source:HGNC Symbol;Acc:23625]",
				"ENSG00000135423	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135423	GLS2	glutaminase 2 (liver, mitochondrial) [Source:HGNC Symbol;Acc:29570]",
				"ENSG00000155893	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155893	ACPL2	acid phosphatase-like 2 [Source:HGNC Symbol;Acc:26303]",
				"ENSG00000061918	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000061918	GUCY1B3	guanylate cyclase 1, soluble, beta 3 [Source:HGNC Symbol;Acc:4687]",
				"ENSG00000170989	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170989	S1PR1	sphingosine-1-phosphate receptor 1 [Source:HGNC Symbol;Acc:3165]",
				"ENSG00000119812	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119812	FAM98A	family with sequence similarity 98, member A [Source:HGNC Symbol;Acc:24520]",
				"ENSG00000164066	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164066	INTU	inturned planar cell polarity effector homolog (Drosophila) [Source:HGNC Symbol;Acc:29239]",
				"ENSG00000141738	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141738	GRB7	growth factor receptor-bound protein 7 [Source:HGNC Symbol;Acc:4567]",
				"ENSG00000168259	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168259	DNAJC7	DnaJ (Hsp40) homolog, subfamily C, member 7 [Source:HGNC Symbol;Acc:12392]",
				"ENSG00000187726	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187726	DNAJB13	DnaJ (Hsp40) homolog, subfamily B, member 13 [Source:HGNC Symbol;Acc:30718]",
				"ENSG00000139531	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139531	SUOX	sulfite oxidase [Source:HGNC Symbol;Acc:11460]",
				"ENSG00000004660	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004660	CAMKK1	calcium/calmodulin-dependent protein kinase kinase 1, alpha [Source:HGNC Symbol;Acc:1469]",
				"ENSG00000135100	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135100	HNF1A	HNF1 homeobox A [Source:HGNC Symbol;Acc:11621]",
				"ENSG00000179913	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179913	B3GNT3	UDP-GlcNAc:betaGal beta-1,3-N-acetylglucosaminyltransferase 3 [Source:HGNC Symbol;Acc:13528]",
				"ENSG00000171488	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171488	LRRC8C	leucine rich repeat containing 8 family, member C [Source:HGNC Symbol;Acc:25075]",
				"ENSG00000161956	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161956	SENP3	SUMO1/sentrin/SMT3 specific peptidase 3 [Source:HGNC Symbol;Acc:17862]",
				"ENSG00000176170	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176170	SPHK1	sphingosine kinase 1 [Source:HGNC Symbol;Acc:11240]",
				"ENSG00000138614	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138614	C15orf44	chromosome 15 open reading frame 44 [Source:HGNC Symbol;Acc:25372]",
				"ENSG00000175189	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175189	INHBC	inhibin, beta C [Source:HGNC Symbol;Acc:6068]",
				"ENSG00000119397	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119397	CNTRL	centriolin [Source:HGNC Symbol;Acc:1858]",
				"ENSG00000154832	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154832	CXXC1	CXXC finger protein 1 [Source:HGNC Symbol;Acc:24343]",
				"ENSG00000138073	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138073	PREB	prolactin regulatory element binding [Source:HGNC Symbol;Acc:9356]",
				"ENSG00000100644	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100644	HIF1A	hypoxia inducible factor 1, alpha subunit (basic helix-loop-helix transcription factor) [Source:HGNC Symbol;Acc:4910]",
				"ENSG00000128045	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128045	RASL11B	RAS-like, family 11, member B [Source:HGNC Symbol;Acc:23804]",
				"ENSG00000122378	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122378	FAM213A	family with sequence similarity 213, member A [Source:HGNC Symbol;Acc:28651]",
				"ENSG00000221829	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221829	FANCG	Fanconi anemia, complementation group G [Source:HGNC Symbol;Acc:3588]",
				"ENSG00000158270	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158270	COLEC12	collectin sub-family member 12 [Source:HGNC Symbol;Acc:16016]",
				"ENSG00000140682	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140682	TGFB1I1	transforming growth factor beta 1 induced transcript 1 [Source:HGNC Symbol;Acc:11767]",
				"ENSG00000123901	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123901	GPR83	G protein-coupled receptor 83 [Source:HGNC Symbol;Acc:4523]",
				"ENSG00000149582	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149582	TMEM25	transmembrane protein 25 [Source:HGNC Symbol;Acc:25890]",
				"ENSG00000118564	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118564	FBXL5	F-box and leucine-rich repeat protein 5 [Source:HGNC Symbol;Acc:13602]",
				"ENSG00000148175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148175	STOM	stomatin [Source:HGNC Symbol;Acc:3383]",
				"ENSG00000060642	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000060642	PIGV	phosphatidylinositol glycan anchor biosynthesis, class V [Source:HGNC Symbol;Acc:26031]",
				"ENSG00000122707	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122707	RECK	reversion-inducing-cysteine-rich protein with kazal motifs [Source:HGNC Symbol;Acc:11345]",
				"ENSG00000138400	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138400	MDH1B	malate dehydrogenase 1B, NAD (soluble) [Source:HGNC Symbol;Acc:17836]",
				"ENSG00000116031	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116031	CD207	CD207 molecule, langerin [Source:HGNC Symbol;Acc:17935]",
				"ENSG00000108784	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108784	NAGLU	N-acetylglucosaminidase, alpha [Source:HGNC Symbol;Acc:7632]",
				"ENSG00000013561	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013561	RNF14	ring finger protein 14 [Source:HGNC Symbol;Acc:10058]",
				"ENSG00000183862	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183862	CNGA2	cyclic nucleotide gated channel alpha 2 [Source:HGNC Symbol;Acc:2149]",
				"ENSG00000189184	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000189184	PCDH18	protocadherin 18 [Source:HGNC Symbol;Acc:14268]",
				"ENSG00000102882	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102882	MAPK3	mitogen-activated protein kinase 3 [Source:HGNC Symbol;Acc:6877]",
				"ENSG00000156453	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156453	PCDH1	protocadherin 1 [Source:HGNC Symbol;Acc:8655]",
				"ENSG00000175073	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175073	VCPIP1	valosin containing protein (p97)/p47 complex interacting protein 1 [Source:HGNC Symbol;Acc:30897]",
				"ENSG00000188554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188554	NBR1	neighbor of BRCA1 gene 1 [Source:HGNC Symbol;Acc:6746]",
				"ENSG00000159588	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159588	CCDC17	coiled-coil domain containing 17 [Source:HGNC Symbol;Acc:26574]",
				"ENSG00000162630	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162630	B3GALT2	UDP-Gal:betaGlcNAc beta 1,3-galactosyltransferase, polypeptide 2 [Source:HGNC Symbol;Acc:917]",
				"ENSG00000124145	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124145	SDC4	syndecan 4 [Source:HGNC Symbol;Acc:10661]",
				"ENSG00000089248	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089248	ERP29	endoplasmic reticulum protein 29 [Source:HGNC Symbol;Acc:13799]",
				"ENSG00000129347	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129347	KRI1	KRI1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25769]",
				"ENSG00000166317	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166317	SYNPO2L	synaptopodin 2-like [Source:HGNC Symbol;Acc:23532]",
				"ENSG00000154822	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154822	PLCL2	phospholipase C-like 2 [Source:HGNC Symbol;Acc:9064]",
				"ENSG00000196843	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196843	ARID5A	AT rich interactive domain 5A (MRF1-like) [Source:HGNC Symbol;Acc:17361]",
				"ENSG00000157103	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157103	SLC6A1	solute carrier family 6 (neurotransmitter transporter, GABA), member 1 [Source:HGNC Symbol;Acc:11042]",
				"ENSG00000130766	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130766	SESN2	sestrin 2 [Source:HGNC Symbol;Acc:20746]",
				"ENSG00000009307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000009307	CSDE1	cold shock domain containing E1, RNA-binding [Source:HGNC Symbol;Acc:29905]",
				"ENSG00000151687	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151687	ANKAR	ankyrin and armadillo repeat containing [Source:HGNC Symbol;Acc:26350]",
				"ENSG00000166526	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166526	ZNF3	zinc finger protein 3 [Source:HGNC Symbol;Acc:13089]",
				"ENSG00000090863	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090863	GLG1	golgi glycoprotein 1 [Source:HGNC Symbol;Acc:4316]",
				"ENSG00000103426	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103426	CORO7	coronin 7 [Source:HGNC Symbol;Acc:26161]",
				"ENSG00000089022	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089022	MAPKAPK5	mitogen-activated protein kinase-activated protein kinase 5 [Source:HGNC Symbol;Acc:6889]",
				"ENSG00000081014	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081014	AP4E1	adaptor-related protein complex 4, epsilon 1 subunit [Source:HGNC Symbol;Acc:573]",
				"ENSG00000140030	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140030	GPR65	G protein-coupled receptor 65 [Source:HGNC Symbol;Acc:4517]",
				"ENSG00000197417	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197417	SHPK	sedoheptulokinase [Source:HGNC Symbol;Acc:1492]",
				"ENSG00000143569	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143569	UBAP2L	ubiquitin associated protein 2-like [Source:HGNC Symbol;Acc:29877]",
				"ENSG00000118816	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118816	CCNI	cyclin I [Source:HGNC Symbol;Acc:1595]",
				"ENSG00000109065	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109065	NAT9	N-acetyltransferase 9 (GCN5-related, putative) [Source:HGNC Symbol;Acc:23133]",
				"ENSG00000089597	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089597	GANAB	glucosidase, alpha; neutral AB [Source:HGNC Symbol;Acc:4138]",
				"ENSG00000100505	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100505	TRIM9	tripartite motif containing 9 [Source:HGNC Symbol;Acc:16288]",
				"ENSG00000124608	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124608	AARS2	alanyl-tRNA synthetase 2, mitochondrial (putative) [Source:HGNC Symbol;Acc:21022]",
				"ENSG00000145358	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145358	DDIT4L	DNA-damage-inducible transcript 4-like [Source:HGNC Symbol;Acc:30555]",
				"ENSG00000163781	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163781	TOPBP1	topoisomerase (DNA) II binding protein 1 [Source:HGNC Symbol;Acc:17008]",
				"ENSG00000179256	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179256	C12orf69	chromosome 12 open reading frame 69 [Source:HGNC Symbol;Acc:34401]",
				"ENSG00000185085	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185085	INTS5	integrator complex subunit 5 [Source:HGNC Symbol;Acc:29352]",
				"ENSG00000165383	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165383	LRRC18	leucine rich repeat containing 18 [Source:HGNC Symbol;Acc:23199]",
				"ENSG00000095370	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095370	SH2D3C	SH2 domain containing 3C [Source:HGNC Symbol;Acc:16884]",
				"ENSG00000151835	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151835	SACS	spastic ataxia of Charlevoix-Saguenay (sacsin) [Source:HGNC Symbol;Acc:10519]",
				"ENSG00000182134	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182134	TDRKH	tudor and KH domain containing [Source:HGNC Symbol;Acc:11713]",
				"ENSG00000105135	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105135	ILVBL	ilvB (bacterial acetolactate synthase)-like [Source:HGNC Symbol;Acc:6041]",
				"ENSG00000180357	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180357	ZNF609	zinc finger protein 609 [Source:HGNC Symbol;Acc:29003]",
				"ENSG00000127083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127083	OMD	osteomodulin [Source:HGNC Symbol;Acc:8134]",
				"ENSG00000174944	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174944	P2RY14	purinergic receptor P2Y, G-protein coupled, 14 [Source:HGNC Symbol;Acc:16442]",
				"ENSG00000128802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128802	GDF2	growth differentiation factor 2 [Source:HGNC Symbol;Acc:4217]",
				"ENSG00000162227	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162227	TAF6L	TAF6-like RNA polymerase II, p300/CBP-associated factor (PCAF)-associated factor, 65kDa [Source:HGNC Symbol;Acc:17305]",
				"ENSG00000138670	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138670	RASGEF1B	RasGEF domain family, member 1B [Source:HGNC Symbol;Acc:24881]",
				"ENSG00000112983	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112983	BRD8	bromodomain containing 8 [Source:HGNC Symbol;Acc:19874]",
				"ENSG00000187605	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187605	TET3	tet methylcytosine dioxygenase 3 [Source:HGNC Symbol;Acc:28313]",
				"ENSG00000149428	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149428	HYOU1	hypoxia up-regulated 1 [Source:HGNC Symbol;Acc:16931]",
				"ENSG00000178035	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178035	IMPDH2	IMP (inosine 5prime-monophosphate) dehydrogenase 2 [Source:HGNC Symbol;Acc:6053]",
				"ENSG00000123415	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123415	SMUG1	single-strand-selective monofunctional uracil-DNA glycosylase 1 [Source:HGNC Symbol;Acc:17148]",
				"ENSG00000108342	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108342	CSF3	colony stimulating factor 3 (granulocyte) [Source:HGNC Symbol;Acc:2438]",
				"ENSG00000052749	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000052749	RRP12	ribosomal RNA processing 12 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:29100]",
				"ENSG00000112837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112837	TBX18	T-box 18 [Source:HGNC Symbol;Acc:11595]",
				"ENSG00000132017	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132017	DCAF15	DDB1 and CUL4 associated factor 15 [Source:HGNC Symbol;Acc:25095]",
				"ENSG00000169905	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169905	TOR1AIP2	torsin A interacting protein 2 [Source:HGNC Symbol;Acc:24055]",
				"ENSG00000108561	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108561	C1QBP	complement component 1, q subcomponent binding protein [Source:HGNC Symbol;Acc:1243]",
				"ENSG00000115307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115307	AUP1	ancient ubiquitous protein 1 [Source:HGNC Symbol;Acc:891]",
				"ENSG00000132361	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132361	KIAA0664	KIAA0664 [Source:HGNC Symbol;Acc:29094]",
				"ENSG00000124641	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124641	MED20	mediator complex subunit 20 [Source:HGNC Symbol;Acc:16840]",
				"ENSG00000187210	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187210	GCNT1	glucosaminyl (N-acetyl) transferase 1, core 2 [Source:HGNC Symbol;Acc:4203]",
				"ENSG00000016864	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000016864	GLT8D1	glycosyltransferase 8 domain containing 1 [Source:HGNC Symbol;Acc:24870]",
				"ENSG00000151332	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151332	MBIP	MAP3K12 binding inhibitory protein 1 [Source:HGNC Symbol;Acc:20427]",
				"ENSG00000110074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110074	FOXRED1	FAD-dependent oxidoreductase domain containing 1 [Source:HGNC Symbol;Acc:26927]",
				"ENSG00000135740	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135740	SLC9A5	solute carrier family 9, subfamily A (NHE5, cation proton antiporter 5), member 5 [Source:HGNC Symbol;Acc:11078]",
				"ENSG00000167131	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167131	CCDC103	coiled-coil domain containing 103 [Source:HGNC Symbol;Acc:32700]",
				"ENSG00000205835	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205835	GMNC	geminin coiled-coil domain containing [Source:HGNC Symbol;Acc:40049]",
				"ENSG00000205213	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205213	LGR4	leucine-rich repeat containing G protein-coupled receptor 4 [Source:HGNC Symbol;Acc:13299]",
				"ENSG00000161405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161405	IKZF3	IKAROS family zinc finger 3 (Aiolos) [Source:HGNC Symbol;Acc:13178]",
				"ENSG00000155846	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155846	PPARGC1B	peroxisome proliferator-activated receptor gamma, coactivator 1 beta [Source:HGNC Symbol;Acc:30022]",
				"ENSG00000132481	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132481	TRIM47	tripartite motif containing 47 [Source:HGNC Symbol;Acc:19020]",
				"ENSG00000132912	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132912	DCTN4	dynactin 4 (p62) [Source:HGNC Symbol;Acc:15518]",
				"ENSG00000167969	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167969	ECI1	enoyl-CoA delta isomerase 1 [Source:HGNC Symbol;Acc:2703]",
				"ENSG00000155304	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155304	HSPA13	heat shock protein 70kDa family, member 13 [Source:HGNC Symbol;Acc:11375]",
				"ENSG00000025434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000025434	NR1H3	nuclear receptor subfamily 1, group H, member 3 [Source:HGNC Symbol;Acc:7966]",
				"ENSG00000105650	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105650	PDE4C	phosphodiesterase 4C, cAMP-specific [Source:HGNC Symbol;Acc:8782]",
				"ENSG00000151208	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151208	DLG5	discs, large homolog 5 (Drosophila) [Source:HGNC Symbol;Acc:2904]",
				"ENSG00000127564	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127564	PKMYT1	protein kinase, membrane associated tyrosine/threonine 1 [Source:HGNC Symbol;Acc:29650]",
				"ENSG00000108344	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108344	PSMD3	proteasome (prosome, macropain) 26S subunit, non-ATPase, 3 [Source:HGNC Symbol;Acc:9560]",
				"ENSG00000104413	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104413	ESRP1	epithelial splicing regulatory protein 1 [Source:HGNC Symbol;Acc:25966]",
				"ENSG00000137996	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137996	RTCA	RNA 3prime-terminal phosphate cyclase [Source:HGNC Symbol;Acc:17981]",
				"ENSG00000131370	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131370	SH3BP5	SH3-domain binding protein 5 (BTK-associated) [Source:HGNC Symbol;Acc:10827]",
				"ENSG00000006576	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006576	PHTF2	putative homeodomain transcription factor 2 [Source:HGNC Symbol;Acc:13411]",
				"ENSG00000107742	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107742	SPOCK2	sparc/osteonectin, cwcv and kazal-like domains proteoglycan (testican) 2 [Source:HGNC Symbol;Acc:13564]",
				"ENSG00000183763	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183763	TRAIP	TRAF interacting protein [Source:HGNC Symbol;Acc:30764]",
				"ENSG00000203705	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000203705	TATDN3	TatD DNase domain containing 3 [Source:HGNC Symbol;Acc:27010]",
				"ENSG00000100726	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100726	TELO2	TEL2, telomere maintenance 2, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:29099]",
				"ENSG00000137074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137074	APTX	aprataxin [Source:HGNC Symbol;Acc:15984]",
				"ENSG00000148841	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148841	ITPRIP	inositol 1,4,5-trisphosphate receptor interacting protein [Source:HGNC Symbol;Acc:29370]",
				"ENSG00000181038	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181038	METTL23	methyltransferase like 23 [Source:HGNC Symbol;Acc:26988]",
				"ENSG00000160785	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160785	SLC25A44	solute carrier family 25, member 44 [Source:HGNC Symbol;Acc:29036]",
				"ENSG00000148335	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148335	NTMT1	N-terminal Xaa-Pro-Lys N-methyltransferase 1 [Source:HGNC Symbol;Acc:23373]",
				"ENSG00000166073	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166073	GPR176	G protein-coupled receptor 176 [Source:HGNC Symbol;Acc:32370]",
				"ENSG00000075856	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000075856	SART3	squamous cell carcinoma antigen recognized by T cells 3 [Source:HGNC Symbol;Acc:16860]",
				"ENSG00000179630	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179630	LACC1	laccase (multicopper oxidoreductase) domain containing 1 [Source:HGNC Symbol;Acc:26789]",
				"ENSG00000104218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104218	CSPP1	centrosome and spindle pole associated protein 1 [Source:HGNC Symbol;Acc:26193]",
				"ENSG00000001084	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000001084	GCLC	glutamate-cysteine ligase, catalytic subunit [Source:HGNC Symbol;Acc:4311]",
				"ENSG00000171451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171451	DSEL	dermatan sulfate epimerase-like [Source:HGNC Symbol;Acc:18144]",
				"ENSG00000125630	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125630	POLR1B	polymerase (RNA) I polypeptide B, 128kDa [Source:HGNC Symbol;Acc:20454]",
				"ENSG00000068912	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068912	ERLEC1	endoplasmic reticulum lectin 1 [Source:HGNC Symbol;Acc:25222]",
				"ENSG00000100075	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100075	SLC25A1	solute carrier family 25 (mitochondrial carrier; citrate transporter), member 1 [Source:HGNC Symbol;Acc:10979]",
				"ENSG00000171467	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171467	ZNF318	zinc finger protein 318 [Source:HGNC Symbol;Acc:13578]",
				"ENSG00000169641	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169641	LUZP1	leucine zipper protein 1 [Source:HGNC Symbol;Acc:14985]",
				"ENSG00000148606	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148606	POLR3A	polymerase (RNA) III (DNA directed) polypeptide A, 155kDa [Source:HGNC Symbol;Acc:30074]",
				"ENSG00000121104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121104	FAM117A	family with sequence similarity 117, member A [Source:HGNC Symbol;Acc:24179]",
				"ENSG00000133316	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133316	WDR74	WD repeat domain 74 [Source:HGNC Symbol;Acc:25529]",
				"ENSG00000072778	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072778	ACADVL	acyl-CoA dehydrogenase, very long chain [Source:HGNC Symbol;Acc:92]",
				"ENSG00000159140	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159140	SON	SON DNA binding protein [Source:HGNC Symbol;Acc:11183]",
				"ENSG00000138592	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138592	USP8	ubiquitin specific peptidase 8 [Source:HGNC Symbol;Acc:12631]",
				"ENSG00000100714	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100714	MTHFD1	methylenetetrahydrofolate dehydrogenase (NADP+ dependent) 1, methenyltetrahydrofolate cyclohydrolase, formyltetrahydrofolate synthetase [Source:HGNC Symbol;Acc:7432]",
				"ENSG00000137497	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137497	NUMA1	nuclear mitotic apparatus protein 1 [Source:HGNC Symbol;Acc:8059]",
				"ENSG00000171861	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171861	RNMTL1	RNA methyltransferase like 1 [Source:HGNC Symbol;Acc:18485]",
				"ENSG00000112996	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112996	MRPS30	mitochondrial ribosomal protein S30 [Source:HGNC Symbol;Acc:8769]",
				"ENSG00000100600	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100600	LGMN	legumain [Source:HGNC Symbol;Acc:9472]",
				"ENSG00000139990	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139990	DCAF5	DDB1 and CUL4 associated factor 5 [Source:HGNC Symbol;Acc:20224]",
				"ENSG00000117222	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117222	RBBP5	retinoblastoma binding protein 5 [Source:HGNC Symbol;Acc:9888]",
				"ENSG00000006282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006282	SPATA20	spermatogenesis associated 20 [Source:HGNC Symbol;Acc:26125]",
				"ENSG00000072415	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072415	MPP5	membrane protein, palmitoylated 5 (MAGUK p55 subfamily member 5) [Source:HGNC Symbol;Acc:18669]",
				"ENSG00000165406	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165406	38046	membrane-associated ring finger (C3HC4) 8, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:23356]",
				"ENSG00000127688	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127688	GAN	gigaxonin [Source:HGNC Symbol;Acc:4137]",
				"ENSG00000183354	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183354	KIAA2026	KIAA2026 [Source:HGNC Symbol;Acc:23378]",
				"ENSG00000138698	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138698	RAP1GDS1	RAP1, GTP-GDP dissociation stimulator 1 [Source:HGNC Symbol;Acc:9859]",
				"ENSG00000183762	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183762	KREMEN1	kringle containing transmembrane protein 1 [Source:HGNC Symbol;Acc:17550]",
				"ENSG00000171243	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171243	SOSTDC1	sclerostin domain containing 1 [Source:HGNC Symbol;Acc:21748]",
				"ENSG00000177575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177575	CD163	CD163 molecule [Source:HGNC Symbol;Acc:1631]",
				"ENSG00000155307	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155307	SAMSN1	SAM domain, SH3 domain and nuclear localization signals 1 [Source:HGNC Symbol;Acc:10528]",
				"ENSG00000214160	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000214160	ALG3	asparagine-linked glycosylation 3, alpha-1,3- mannosyltransferase homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23056]",
				"ENSG00000073584	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000073584	SMARCE1	SWI/SNF related, matrix associated, actin dependent regulator of chromatin, subfamily e, member 1 [Source:HGNC Symbol;Acc:11109]",
				"ENSG00000186281	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186281	GPAT2	glycerol-3-phosphate acyltransferase 2, mitochondrial [Source:HGNC Symbol;Acc:27168]",
				"ENSG00000144227	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144227	NXPH2	neurexophilin 2 [Source:HGNC Symbol;Acc:8076]",
				"ENSG00000196660	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196660	SLC30A10	solute carrier family 30, member 10 [Source:HGNC Symbol;Acc:25355]",
				"ENSG00000070367	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070367	EXOC5	exocyst complex component 5 [Source:HGNC Symbol;Acc:10696]",
				"ENSG00000128915	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128915	NARG2	NMDA receptor regulated 2 [Source:HGNC Symbol;Acc:29885]",
				"ENSG00000011083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011083	SLC6A7	solute carrier family 6 (neurotransmitter transporter, L-proline), member 7 [Source:HGNC Symbol;Acc:11054]",
				"ENSG00000071539	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071539	TRIP13	thyroid hormone receptor interactor 13 [Source:HGNC Symbol;Acc:12307]",
				"ENSG00000175390	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175390	EIF3F	eukaryotic translation initiation factor 3, subunit F [Source:HGNC Symbol;Acc:3275]",
				"ENSG00000168758	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168758	SEMA4C	sema domain, immunoglobulin domain (Ig), transmembrane domain (TM) and short cytoplasmic domain, (semaphorin) 4C [Source:HGNC Symbol;Acc:10731]",
				"ENSG00000148488	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148488	ST8SIA6	ST8 alpha-N-acetyl-neuraminide alpha-2,8-sialyltransferase 6 [Source:HGNC Symbol;Acc:23317]",
				"ENSG00000076864	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076864	RAP1GAP	RAP1 GTPase activating protein [Source:HGNC Symbol;Acc:9858]",
				"ENSG00000184677	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184677	ZBTB40	zinc finger and BTB domain containing 40 [Source:HGNC Symbol;Acc:29045]",
				"ENSG00000178074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178074	C2orf69	chromosome 2 open reading frame 69 [Source:HGNC Symbol;Acc:26799]",
				"ENSG00000151611	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151611	MMAA	methylmalonic aciduria (cobalamin deficiency) cblA type [Source:HGNC Symbol;Acc:18871]",
				"ENSG00000169247	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169247	SH3TC2	SH3 domain and tetratricopeptide repeats 2 [Source:HGNC Symbol;Acc:29427]",
				"ENSG00000147465	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147465	STAR	steroidogenic acute regulatory protein [Source:HGNC Symbol;Acc:11359]",
				"ENSG00000084693	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084693	AGBL5	ATP/GTP binding protein-like 5 [Source:HGNC Symbol;Acc:26147]",
				"ENSG00000104907	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104907	TRMT1	tRNA methyltransferase 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25980]",
				"ENSG00000128581	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128581	RABL5	RAB, member RAS oncogene family-like 5 [Source:HGNC Symbol;Acc:21895]",
				"ENSG00000184675	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184675	FAM123B	family with sequence similarity 123B [Source:HGNC Symbol;Acc:26837]",
				"ENSG00000084112	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084112	SSH1	slingshot homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:30579]",
				"ENSG00000100116	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100116	GCAT	glycine C-acetyltransferase [Source:HGNC Symbol;Acc:4188]",
				"ENSG00000139155	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139155	SLCO1C1	solute carrier organic anion transporter family, member 1C1 [Source:HGNC Symbol;Acc:13819]",
				"ENSG00000135148	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135148	TRAFD1	TRAF-type zinc finger domain containing 1 [Source:HGNC Symbol;Acc:24808]",
				"ENSG00000166598	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166598	HSP90B1	heat shock protein 90kDa beta (Grp94), member 1 [Source:HGNC Symbol;Acc:12028]",
				"ENSG00000165731	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165731	RET	ret proto-oncogene [Source:HGNC Symbol;Acc:9967]",
				"ENSG00000110811	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110811	LEPREL2	leprecan-like 2 [Source:HGNC Symbol;Acc:19318]",
				"ENSG00000105875	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105875	WDR91	WD repeat domain 91 [Source:HGNC Symbol;Acc:24997]",
				"ENSG00000150051	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150051	MKX	mohawk homeobox [Source:HGNC Symbol;Acc:23729]",
				"ENSG00000130032	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130032	PRRG3	proline rich Gla (G-carboxyglutamic acid) 3 (transmembrane) [Source:HGNC Symbol;Acc:30798]",
				"ENSG00000147434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147434	CHRNA6	cholinergic receptor, nicotinic, alpha 6 (neuronal) [Source:HGNC Symbol;Acc:15963]",
				"ENSG00000196396	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196396	PTPN1	protein tyrosine phosphatase, non-receptor type 1 [Source:HGNC Symbol;Acc:9642]",
				"ENSG00000100983	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100983	GSS	glutathione synthetase [Source:HGNC Symbol;Acc:4624]",
				"ENSG00000115946	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115946	PNO1	partner of NOB1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:32790]",
				"ENSG00000126861	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126861	OMG	oligodendrocyte myelin glycoprotein [Source:HGNC Symbol;Acc:8135]",
				"ENSG00000123154	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123154	WDR83	WD repeat domain 83 [Source:HGNC Symbol;Acc:32672]",
				"ENSG00000101230	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101230	ISM1	isthmin 1 homolog (zebrafish) [Source:HGNC Symbol;Acc:16213]",
				"ENSG00000088827	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088827	SIGLEC1	sialic acid binding Ig-like lectin 1, sialoadhesin [Source:HGNC Symbol;Acc:11127]",
				"ENSG00000088448	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088448	ANKRD10	ankyrin repeat domain 10 [Source:HGNC Symbol;Acc:20265]",
				"ENSG00000125450	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125450	NUP85	nucleoporin 85kDa [Source:HGNC Symbol;Acc:8734]",
				"ENSG00000109819	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109819	PPARGC1A	peroxisome proliferator-activated receptor gamma, coactivator 1 alpha [Source:HGNC Symbol;Acc:9237]",
				"ENSG00000115896	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115896	PLCL1	phospholipase C-like 1 [Source:HGNC Symbol;Acc:9063]",
				"ENSG00000121316	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121316	PLBD1	phospholipase B domain containing 1 [Source:HGNC Symbol;Acc:26215]",
				"ENSG00000116701	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116701	NCF2	neutrophil cytosolic factor 2 [Source:HGNC Symbol;Acc:7661]",
				"ENSG00000111145	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111145	ELK3	ELK3, ETS-domain protein (SRF accessory protein 2) [Source:HGNC Symbol;Acc:3325]",
				"ENSG00000176971	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176971	FIBIN	fin bud initiation factor homolog (zebrafish) [Source:HGNC Symbol;Acc:33747]",
				"ENSG00000092098	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092098	RNF31	ring finger protein 31 [Source:HGNC Symbol;Acc:16031]",
				"ENSG00000134569	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134569	LRP4	low density lipoprotein receptor-related protein 4 [Source:HGNC Symbol;Acc:6696]",
				"ENSG00000114988	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114988	LMAN2L	lectin, mannose-binding 2-like [Source:HGNC Symbol;Acc:19263]",
				"ENSG00000054118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000054118	THRAP3	thyroid hormone receptor associated protein 3 [Source:HGNC Symbol;Acc:22964]",
				"ENSG00000115364	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115364	MRPL19	mitochondrial ribosomal protein L19 [Source:HGNC Symbol;Acc:14052]",
				"ENSG00000116273	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116273	PHF13	PHD finger protein 13 [Source:HGNC Symbol;Acc:22983]",
				"ENSG00000161638	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161638	ITGA5	integrin, alpha 5 (fibronectin receptor, alpha polypeptide) [Source:HGNC Symbol;Acc:6141]",
				"ENSG00000188828	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188828	GLRA4	glycine receptor, alpha 4 [Source:HGNC Symbol;Acc:31715]",
				"ENSG00000111817	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111817	DSE	dermatan sulfate epimerase [Source:HGNC Symbol;Acc:21144]",
				"ENSG00000164118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164118	CEP44	centrosomal protein 44kDa [Source:HGNC Symbol;Acc:29356]",
				"ENSG00000166226	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166226	CCT2	chaperonin containing TCP1, subunit 2 (beta) [Source:HGNC Symbol;Acc:1615]",
				"ENSG00000145569	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145569	FAM105A	family with sequence similarity 105, member A [Source:HGNC Symbol;Acc:25629]",
				"ENSG00000135250	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135250	SRPK2	SRSF protein kinase 2 [Source:HGNC Symbol;Acc:11306]",
				"ENSG00000059573	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000059573	ALDH18A1	aldehyde dehydrogenase 18 family, member A1 [Source:HGNC Symbol;Acc:9722]",
				"ENSG00000123815	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123815	ADCK4	aarF domain containing kinase 4 [Source:HGNC Symbol;Acc:19041]",
				"ENSG00000119969	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119969	HELLS	helicase, lymphoid-specific [Source:HGNC Symbol;Acc:4861]",
				"ENSG00000095383	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095383	TBC1D2	TBC1 domain family, member 2 [Source:HGNC Symbol;Acc:18026]",
				"ENSG00000164134	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164134	NAA15	N(alpha)-acetyltransferase 15, NatA auxiliary subunit [Source:HGNC Symbol;Acc:30782]",
				"ENSG00000134222	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134222	PSRC1	proline/serine-rich coiled-coil 1 [Source:HGNC Symbol;Acc:24472]",
				"ENSG00000126368	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126368	NR1D1	nuclear receptor subfamily 1, group D, member 1 [Source:HGNC Symbol;Acc:7962]",
				"ENSG00000104953	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104953	TLE6	transducin-like enhancer of split 6 (E(sp1) homolog, Drosophila) [Source:HGNC Symbol;Acc:30788]",
				"ENSG00000213160	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213160	KLHL23	kelch-like 23 (Drosophila) [Source:HGNC Symbol;Acc:27506]",
				"ENSG00000180269	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180269	GPR139	G protein-coupled receptor 139 [Source:HGNC Symbol;Acc:19995]",
				"ENSG00000165105	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165105	RASEF	RAS and EF-hand domain containing [Source:HGNC Symbol;Acc:26464]",
				"ENSG00000178163	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178163	ZNF518B	zinc finger protein 518B [Source:HGNC Symbol;Acc:29365]",
				"ENSG00000165194	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165194	PCDH19	protocadherin 19 [Source:HGNC Symbol;Acc:14270]",
				"ENSG00000171017	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171017	LRRC8E	leucine rich repeat containing 8 family, member E [Source:HGNC Symbol;Acc:26272]",
				"ENSG00000100938	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100938	GMPR2	guanosine monophosphate reductase 2 [Source:HGNC Symbol;Acc:4377]",
				"ENSG00000126217	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126217	MCF2L	MCF.2 cell line derived transforming sequence-like [Source:HGNC Symbol;Acc:14576]",
				"ENSG00000090674	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090674	MCOLN1	mucolipin 1 [Source:HGNC Symbol;Acc:13356]",
				"ENSG00000126804	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126804	ZBTB1	zinc finger and BTB domain containing 1 [Source:HGNC Symbol;Acc:20259]",
				"ENSG00000177125	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177125	ZBTB34	zinc finger and BTB domain containing 34 [Source:HGNC Symbol;Acc:31446]",
				"ENSG00000100330	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100330	MTMR3	myotubularin related protein 3 [Source:HGNC Symbol;Acc:7451]",
				"ENSG00000196365	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196365	LONP1	lon peptidase 1, mitochondrial [Source:HGNC Symbol;Acc:9479]",
				"ENSG00000166348	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166348	USP54	ubiquitin specific peptidase 54 [Source:HGNC Symbol;Acc:23513]",
				"ENSG00000095574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095574	IKZF5	IKAROS family zinc finger 5 (Pegasus) [Source:HGNC Symbol;Acc:14283]",
				"ENSG00000132254	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132254	ARFIP2	ADP-ribosylation factor interacting protein 2 [Source:HGNC Symbol;Acc:17160]",
				"ENSG00000111245	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111245	MYL2	myosin, light chain 2, regulatory, cardiac, slow [Source:HGNC Symbol;Acc:7583]",
				"ENSG00000116514	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116514	RNF19B	ring finger protein 19B [Source:HGNC Symbol;Acc:26886]",
				"ENSG00000108465	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108465	CDK5RAP3	CDK5 regulatory subunit associated protein 3 [Source:HGNC Symbol;Acc:18673]",
				"ENSG00000121864	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121864	ZNF639	zinc finger protein 639 [Source:HGNC Symbol;Acc:30950]",
				"ENSG00000084652	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084652	TXLNA	taxilin alpha [Source:HGNC Symbol;Acc:30685]",
				"ENSG00000174306	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174306	ZHX3	zinc fingers and homeoboxes 3 [Source:HGNC Symbol;Acc:15935]",
				"ENSG00000105607	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105607	GCDH	glutaryl-CoA dehydrogenase [Source:HGNC Symbol;Acc:4189]",
				"ENSG00000151092	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151092	NGLY1	N-glycanase 1 [Source:HGNC Symbol;Acc:17646]",
				"ENSG00000163026	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163026	C2orf44	chromosome 2 open reading frame 44 [Source:HGNC Symbol;Acc:26157]",
				"ENSG00000101294	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101294	HM13	histocompatibility (minor) 13 [Source:HGNC Symbol;Acc:16435]",
				"ENSG00000007202	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000007202	KIAA0100	KIAA0100 [Source:HGNC Symbol;Acc:28960]",
				"ENSG00000163960	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163960	UBXN7	UBX domain protein 7 [Source:HGNC Symbol;Acc:29119]",
				"ENSG00000104154	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104154	SLC30A4	solute carrier family 30 (zinc transporter), member 4 [Source:HGNC Symbol;Acc:11015]",
				"ENSG00000101391	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101391	CDK5RAP1	CDK5 regulatory subunit associated protein 1 [Source:HGNC Symbol;Acc:15880]",
				"ENSG00000154734	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154734	ADAMTS1	ADAM metallopeptidase with thrombospondin type 1 motif, 1 [Source:HGNC Symbol;Acc:217]",
				"ENSG00000153187	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153187	HNRNPU	heterogeneous nuclear ribonucleoprotein U (scaffold attachment factor A) [Source:HGNC Symbol;Acc:5048]",
				"ENSG00000174840	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174840	PDE12	phosphodiesterase 12 [Source:HGNC Symbol;Acc:25386]",
				"ENSG00000127463	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127463	EMC1	ER membrane protein complex subunit 1 [Source:HGNC Symbol;Acc:28957]",
				"ENSG00000101282	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101282	RSPO4	R-spondin 4 [Source:HGNC Symbol;Acc:16175]",
				"ENSG00000155729	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155729	KCTD18	potassium channel tetramerisation domain containing 18 [Source:HGNC Symbol;Acc:26446]",
				"ENSG00000109458	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109458	GAB1	GRB2-associated binding protein 1 [Source:HGNC Symbol;Acc:4066]",
				"ENSG00000163820	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163820	FYCO1	FYVE and coiled-coil domain containing 1 [Source:HGNC Symbol;Acc:14673]",
				"ENSG00000166508	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166508	MCM7	minichromosome maintenance complex component 7 [Source:HGNC Symbol;Acc:6950]",
				"ENSG00000183801	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183801	OLFML1	olfactomedin-like 1 [Source:HGNC Symbol;Acc:24473]",
				"ENSG00000167671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167671	UBXN6	UBX domain protein 6 [Source:HGNC Symbol;Acc:14928]",
				"ENSG00000041802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000041802	LSG1	large subunit GTPase 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25652]",
				"ENSG00000065029	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065029	ZNF76	zinc finger protein 76 [Source:HGNC Symbol;Acc:13149]",
				"ENSG00000204852	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204852	TCTN1	tectonic family member 1 [Source:HGNC Symbol;Acc:26113]",
				"ENSG00000163884	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163884	KLF15	Kruppel-like factor 15 [Source:HGNC Symbol;Acc:14536]",
				"ENSG00000085274	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085274	MYNN	myoneurin [Source:HGNC Symbol;Acc:14955]",
				"ENSG00000050426	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000050426	LETMD1	LETM1 domain containing 1 [Source:HGNC Symbol;Acc:24241]",
				"ENSG00000164073	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164073	MFSD8	major facilitator superfamily domain containing 8 [Source:HGNC Symbol;Acc:28486]",
				"ENSG00000116120	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116120	FARSB	phenylalanyl-tRNA synthetase, beta subunit [Source:HGNC Symbol;Acc:17800]",
				"ENSG00000134765	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134765	DSC1	desmocollin 1 [Source:HGNC Symbol;Acc:3035]",
				"ENSG00000138639	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138639	ARHGAP24	Rho GTPase activating protein 24 [Source:HGNC Symbol;Acc:25361]",
				"ENSG00000053770	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000053770	AP5M1	adaptor-related protein complex 5, mu 1 subunit [Source:HGNC Symbol;Acc:20192]",
				"ENSG00000151552	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151552	QDPR	quinoid dihydropteridine reductase [Source:HGNC Symbol;Acc:9752]",
				"ENSG00000156671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156671	SAMD8	sterile alpha motif domain containing 8 [Source:HGNC Symbol;Acc:26320]",
				"ENSG00000133110	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133110	POSTN	periostin, osteoblast specific factor [Source:HGNC Symbol;Acc:16953]",
				"ENSG00000197746	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197746	PSAP	prosaposin [Source:HGNC Symbol;Acc:9498]",
				"ENSG00000170581	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170581	STAT2	signal transducer and activator of transcription 2, 113kDa [Source:HGNC Symbol;Acc:11363]",
				"ENSG00000103168	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103168	TAF1C	TATA box binding protein (TBP)-associated factor, RNA polymerase I, C, 110kDa [Source:HGNC Symbol;Acc:11534]",
				"ENSG00000107623	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107623	GDF10	growth differentiation factor 10 [Source:HGNC Symbol;Acc:4215]",
				"ENSG00000166971	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166971	AKTIP	AKT interacting protein [Source:HGNC Symbol;Acc:16710]",
				"ENSG00000205221	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205221	VIT	vitrin [Source:HGNC Symbol;Acc:12697]",
				"ENSG00000156030	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156030	C14orf43	chromosome 14 open reading frame 43 [Source:HGNC Symbol;Acc:19853]",
				"ENSG00000135074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135074	ADAM19	ADAM metallopeptidase domain 19 [Source:HGNC Symbol;Acc:197]",
				"ENSG00000100034	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100034	PPM1F	protein phosphatase, Mg2+/Mn2+ dependent, 1F [Source:HGNC Symbol;Acc:19388]",
				"ENSG00000144152	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144152	FBLN7	fibulin 7 [Source:HGNC Symbol;Acc:26740]",
				"ENSG00000138119	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138119	MYOF	myoferlin [Source:HGNC Symbol;Acc:3656]",
				"ENSG00000170448	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170448	NFXL1	nuclear transcription factor, X-box binding-like 1 [Source:HGNC Symbol;Acc:18726]",
				"ENSG00000115211	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115211	EIF2B4	eukaryotic translation initiation factor 2B, subunit 4 delta, 67kDa [Source:HGNC Symbol;Acc:3260]",
				"ENSG00000181195	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181195	PENK	proenkephalin [Source:HGNC Symbol;Acc:8831]",
				"ENSG00000155463	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155463	OXA1L	oxidase (cytochrome c) assembly 1-like [Source:HGNC Symbol;Acc:8526]",
				"ENSG00000164187	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164187	LMBRD2	LMBR1 domain containing 2 [Source:HGNC Symbol;Acc:25287]",
				"ENSG00000135472	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135472	FAIM2	Fas apoptotic inhibitory molecule 2 [Source:HGNC Symbol;Acc:17067]",
				"ENSG00000186648	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186648	LRRC16B	leucine rich repeat containing 16B [Source:HGNC Symbol;Acc:20272]",
				"ENSG00000005884	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005884	ITGA3	integrin, alpha 3 (antigen CD49C, alpha 3 subunit of VLA-3 receptor) [Source:HGNC Symbol;Acc:6139]",
				"ENSG00000175182	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175182	FAM131A	family with sequence similarity 131, member A [Source:HGNC Symbol;Acc:28308]",
				"ENSG00000163376	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163376	KBTBD8	kelch repeat and BTB (POZ) domain containing 8 [Source:HGNC Symbol;Acc:30691]",
				"ENSG00000118495	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118495	PLAGL1	pleiomorphic adenoma gene-like 1 [Source:HGNC Symbol;Acc:9046]",
				"ENSG00000081059	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081059	TCF7	transcription factor 7 (T-cell specific, HMG-box) [Source:HGNC Symbol;Acc:11639]",
				"ENSG00000078098	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000078098	FAP	fibroblast activation protein, alpha [Source:HGNC Symbol;Acc:3590]",
				"ENSG00000143319	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143319	ISG20L2	interferon stimulated exonuclease gene 20kDa-like 2 [Source:HGNC Symbol;Acc:25745]",
				"ENSG00000182827	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182827	ACBD3	acyl-CoA binding domain containing 3 [Source:HGNC Symbol;Acc:15453]",
				"ENSG00000125945	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125945	ZNF436	zinc finger protein 436 [Source:HGNC Symbol;Acc:20814]",
				"ENSG00000111790	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111790	FGFR1OP2	FGFR1 oncogene partner 2 [Source:HGNC Symbol;Acc:23098]",
				"ENSG00000105732	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105732	ZNF574	zinc finger protein 574 [Source:HGNC Symbol;Acc:26166]",
				"ENSG00000122971	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122971	ACADS	acyl-CoA dehydrogenase, C-2 to C-3 short chain [Source:HGNC Symbol;Acc:90]",
				"ENSG00000164398	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164398	ACSL6	acyl-CoA synthetase long-chain family member 6 [Source:HGNC Symbol;Acc:16496]",
				"ENSG00000154856	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154856	APCDD1	adenomatosis polyposis coli down-regulated 1 [Source:HGNC Symbol;Acc:15718]",
				"ENSG00000177479	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177479	ARIH2	ariadne homolog 2 (Drosophila) [Source:HGNC Symbol;Acc:690]",
				"ENSG00000140497	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140497	SCAMP2	secretory carrier membrane protein 2 [Source:HGNC Symbol;Acc:10564]",
				"ENSG00000188133	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188133	TMEM215	transmembrane protein 215 [Source:HGNC Symbol;Acc:33816]",
				"ENSG00000197093	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197093	GAL3ST4	galactose-3-O-sulfotransferase 4 [Source:HGNC Symbol;Acc:24145]",
				"ENSG00000105856	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105856	HBP1	HMG-box transcription factor 1 [Source:HGNC Symbol;Acc:23200]",
				"ENSG00000126581	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126581	BECN1	beclin 1, autophagy related [Source:HGNC Symbol;Acc:1034]",
				"ENSG00000090520	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090520	DNAJB11	DnaJ (Hsp40) homolog, subfamily B, member 11 [Source:HGNC Symbol;Acc:14889]",
				"ENSG00000149090	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149090	PAMR1	peptidase domain containing associated with muscle regeneration 1 [Source:HGNC Symbol;Acc:24554]",
				"ENSG00000114737	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114737	CISH	cytokine inducible SH2-containing protein [Source:HGNC Symbol;Acc:1984]",
				"ENSG00000144524	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144524	COPS7B	COP9 constitutive photomorphogenic homolog subunit 7B (Arabidopsis) [Source:HGNC Symbol;Acc:16760]",
				"ENSG00000244462	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000244462	RBM12	RNA binding motif protein 12 [Source:HGNC Symbol;Acc:9898]",
				"ENSG00000124466	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124466	LYPD3	LY6/PLAUR domain containing 3 [Source:HGNC Symbol;Acc:24880]",
				"ENSG00000168890	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168890	TMEM150A	transmembrane protein 150A [Source:HGNC Symbol;Acc:24677]",
				"ENSG00000163728	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163728	TTC14	tetratricopeptide repeat domain 14 [Source:HGNC Symbol;Acc:24697]",
				"ENSG00000175426	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175426	PCSK1	proprotein convertase subtilisin/kexin type 1 [Source:HGNC Symbol;Acc:8743]",
				"ENSG00000130985	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130985	UBA1	ubiquitin-like modifier activating enzyme 1 [Source:HGNC Symbol;Acc:12469]",
				"ENSG00000119681	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119681	LTBP2	latent transforming growth factor beta binding protein 2 [Source:HGNC Symbol;Acc:6715]",
				"ENSG00000178252	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178252	WDR6	WD repeat domain 6 [Source:HGNC Symbol;Acc:12758]",
				"ENSG00000101224	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101224	CDC25B	cell division cycle 25 homolog B (S. pombe) [Source:HGNC Symbol;Acc:1726]",
				"ENSG00000128917	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128917	DLL4	delta-like 4 (Drosophila) [Source:HGNC Symbol;Acc:2910]",
				"ENSG00000103197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103197	TSC2	tuberous sclerosis 2 [Source:HGNC Symbol;Acc:12363]",
				"ENSG00000163930	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163930	BAP1	BRCA1 associated protein-1 (ubiquitin carboxy-terminal hydrolase) [Source:HGNC Symbol;Acc:950]",
				"ENSG00000122390	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122390	NAA60	N(alpha)-acetyltransferase 60, NatF catalytic subunit [Source:HGNC Symbol;Acc:25875]",
				"ENSG00000151690	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151690	MFSD6	major facilitator superfamily domain containing 6 [Source:HGNC Symbol;Acc:24711]",
				"ENSG00000167447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167447	SMG8	smg-8 homolog, nonsense mediated mRNA decay factor (C. elegans) [Source:HGNC Symbol;Acc:25551]",
				"ENSG00000187860	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187860	CCDC157	coiled-coil domain containing 157 [Source:HGNC Symbol;Acc:33854]",
				"ENSG00000132382	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132382	MYBBP1A	MYB binding protein (P160) 1a [Source:HGNC Symbol;Acc:7546]",
				"ENSG00000164023	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164023	SGMS2	sphingomyelin synthase 2 [Source:HGNC Symbol;Acc:28395]",
				"ENSG00000111879	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111879	FAM184A	family with sequence similarity 184, member A [Source:HGNC Symbol;Acc:20991]",
				"ENSG00000112365	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112365	ZBTB24	zinc finger and BTB domain containing 24 [Source:HGNC Symbol;Acc:21143]",
				"ENSG00000120049	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120049	KCNIP2	Kv channel interacting protein 2 [Source:HGNC Symbol;Acc:15522]",
				"ENSG00000137845	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137845	ADAM10	ADAM metallopeptidase domain 10 [Source:HGNC Symbol;Acc:188]",
				"ENSG00000131067	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131067	GGT7	gamma-glutamyltransferase 7 [Source:HGNC Symbol;Acc:4259]",
				"ENSG00000042286	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000042286	AIFM2	apoptosis-inducing factor, mitochondrion-associated, 2 [Source:HGNC Symbol;Acc:21411]",
				"ENSG00000143127	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143127	ITGA10	integrin, alpha 10 [Source:HGNC Symbol;Acc:6135]",
				"ENSG00000179115	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179115	FARSA	phenylalanyl-tRNA synthetase, alpha subunit [Source:HGNC Symbol;Acc:3592]",
				"ENSG00000112144	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112144	ICK	intestinal cell (MAK-like) kinase [Source:HGNC Symbol;Acc:21219]",
				"ENSG00000125991	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125991	ERGIC3	ERGIC and golgi 3 [Source:HGNC Symbol;Acc:15927]",
				"ENSG00000124126	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124126	PREX1	phosphatidylinositol-3,4,5-trisphosphate-dependent Rac exchange factor 1 [Source:HGNC Symbol;Acc:32594]",
				"ENSG00000162601	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162601	MYSM1	Myb-like, SWIRM and MPN domains 1 [Source:HGNC Symbol;Acc:29401]",
				"ENSG00000100014	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100014	SPECC1L	sperm antigen with calponin homology and coiled-coil domains 1-like [Source:HGNC Symbol;Acc:29022]",
				"ENSG00000181827	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181827	RFX7	regulatory factor X, 7 [Source:HGNC Symbol;Acc:25777]",
				"ENSG00000139269	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139269	INHBE	inhibin, beta E [Source:HGNC Symbol;Acc:24029]",
				"ENSG00000161594	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161594	KLHL10	kelch-like 10 (Drosophila) [Source:HGNC Symbol;Acc:18829]",
				"ENSG00000255604	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000255604	VTN	vitronectin [Source:HGNC Symbol;Acc:12724]",
				"ENSG00000022840	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000022840	RNF10	ring finger protein 10 [Source:HGNC Symbol;Acc:10055]",
				"ENSG00000156873	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156873	PHKG2	phosphorylase kinase, gamma 2 (testis) [Source:HGNC Symbol;Acc:8931]",
				"ENSG00000198915	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198915	RASGEF1A	RasGEF domain family, member 1A [Source:HGNC Symbol;Acc:24246]",
				"ENSG00000180198	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180198	RCC1	regulator of chromosome condensation 1 [Source:HGNC Symbol;Acc:1913]",
				"ENSG00000128294	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128294	TPST2	tyrosylprotein sulfotransferase 2 [Source:HGNC Symbol;Acc:12021]",
				"ENSG00000100325	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100325	ASCC2	activating signal cointegrator 1 complex subunit 2 [Source:HGNC Symbol;Acc:24103]",
				"ENSG00000186009	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186009	ATP4B	ATPase, H+/K+ exchanging, beta polypeptide [Source:HGNC Symbol;Acc:820]",
				"ENSG00000172269	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172269	DPAGT1	dolichyl-phosphate (UDP-N-acetylglucosamine) N-acetylglucosaminephosphotransferase 1 (GlcNAc-1-P transferase) [Source:HGNC Symbol;Acc:2995]",
				"ENSG00000171612	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171612	SLC25A33	solute carrier family 25 (pyrimidine nucleotide carrier), member 33 [Source:HGNC Symbol;Acc:29681]",
				"ENSG00000103043	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103043	VAC14	Vac14 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25507]",
				"ENSG00000108666	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108666	C17orf75	chromosome 17 open reading frame 75 [Source:HGNC Symbol;Acc:30173]",
				"ENSG00000100167	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100167	36403	septin 3 [Source:HGNC Symbol;Acc:10750]",
				"ENSG00000176040	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176040	TMPRSS7	transmembrane protease, serine 7 [Source:HGNC Symbol;Acc:30846]",
				"ENSG00000125648	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125648	SLC25A23	solute carrier family 25 (mitochondrial carrier; phosphate carrier), member 23 [Source:HGNC Symbol;Acc:19375]",
				"ENSG00000141499	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141499	WRAP53	WD repeat containing, antisense to TP53 [Source:HGNC Symbol;Acc:25522]",
				"ENSG00000239382	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000239382	ALKBH6	alkB, alkylation repair homolog 6 (E. coli) [Source:HGNC Symbol;Acc:28243]",
				"ENSG00000100483	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100483	METTL21D	methyltransferase like 21D [Source:HGNC Symbol;Acc:20352]",
				"ENSG00000128285	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128285	MCHR1	melanin-concentrating hormone receptor 1 [Source:HGNC Symbol;Acc:4479]",
				"ENSG00000178467	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178467	P4HTM	prolyl 4-hydroxylase, transmembrane (endoplasmic reticulum) [Source:HGNC Symbol;Acc:28858]",
				"ENSG00000149257	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149257	SERPINH1	serpin peptidase inhibitor, clade H (heat shock protein 47), member 1, (collagen binding protein 1) [Source:HGNC Symbol;Acc:1546]",
				"ENSG00000196968	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196968	FUT11	fucosyltransferase 11 (alpha (1,3) fucosyltransferase) [Source:HGNC Symbol;Acc:19233]",
				"ENSG00000141294	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141294	LRRC46	leucine rich repeat containing 46 [Source:HGNC Symbol;Acc:25047]",
				"ENSG00000143498	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143498	TAF1A	TATA box binding protein (TBP)-associated factor, RNA polymerase I, A, 48kDa [Source:HGNC Symbol;Acc:11532]",
				"ENSG00000120837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120837	NFYB	nuclear transcription factor Y, beta [Source:HGNC Symbol;Acc:7805]",
				"ENSG00000146918	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146918	NCAPG2	non-SMC condensin II complex, subunit G2 [Source:HGNC Symbol;Acc:21904]",
				"ENSG00000123500	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123500	COL10A1	collagen, type X, alpha 1 [Source:HGNC Symbol;Acc:2185]",
				"ENSG00000166133	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166133	RPUSD2	RNA pseudouridylate synthase domain containing 2 [Source:HGNC Symbol;Acc:24180]",
				"ENSG00000117408	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117408	IPO13	importin 13 [Source:HGNC Symbol;Acc:16853]",
				"ENSG00000176274	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176274	SLC25A53	solute carrier family 25, member 53 [Source:HGNC Symbol;Acc:31894]",
				"ENSG00000206557	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000206557	TRIM71	tripartite motif containing 71, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:32669]",
				"ENSG00000008710	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008710	PKD1	polycystic kidney disease 1 (autosomal dominant) [Source:HGNC Symbol;Acc:9008]",
				"ENSG00000168237	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168237	GLYCTK	glycerate kinase [Source:HGNC Symbol;Acc:24247]",
				"ENSG00000182255	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182255	KCNA4	potassium voltage-gated channel, shaker-related subfamily, member 4 [Source:HGNC Symbol;Acc:6222]",
				"ENSG00000163145	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163145	C1QTNF7	C1q and tumor necrosis factor related protein 7 [Source:HGNC Symbol;Acc:14342]",
				"ENSG00000139083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139083	ETV6	ets variant 6 [Source:HGNC Symbol;Acc:3495]",
				"ENSG00000114019	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114019	AMOTL2	angiomotin like 2 [Source:HGNC Symbol;Acc:17812]",
				"ENSG00000130517	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130517	PGPEP1	pyroglutamyl-peptidase I [Source:HGNC Symbol;Acc:13568]",
				"ENSG00000166908	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166908	PIP4K2C	phosphatidylinositol-5-phosphate 4-kinase, type II, gamma [Source:HGNC Symbol;Acc:23786]",
				"ENSG00000204052	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204052	LRRC73	leucine rich repeat containing 73 [Source:HGNC Symbol;Acc:21375]",
				"ENSG00000072818	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072818	ACAP1	ArfGAP with coiled-coil, ankyrin repeat and PH domains 1 [Source:HGNC Symbol;Acc:16467]",
				"ENSG00000187678	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187678	SPRY4	sprouty homolog 4 (Drosophila) [Source:HGNC Symbol;Acc:15533]",
				"ENSG00000010361	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010361	FUZ	fuzzy homolog (Drosophila) [Source:HGNC Symbol;Acc:26219]",
				"ENSG00000168795	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168795	ZBTB5	zinc finger and BTB domain containing 5 [Source:HGNC Symbol;Acc:23836]",
				"ENSG00000117597	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117597	DIEXF	digestive organ expansion factor homolog (zebrafish) [Source:HGNC Symbol;Acc:28440]",
				"ENSG00000135124	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135124	P2RX4	purinergic receptor P2X, ligand-gated ion channel, 4 [Source:HGNC Symbol;Acc:8535]",
				"ENSG00000072364	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072364	AFF4	AF4/FMR2 family, member 4 [Source:HGNC Symbol;Acc:17869]",
				"ENSG00000198042	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198042	MAK16	MAK16 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:13703]",
				"ENSG00000109381	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109381	ELF2	E74-like factor 2 (ets domain transcription factor) [Source:HGNC Symbol;Acc:3317]",
				"ENSG00000139344	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139344	AMDHD1	amidohydrolase domain containing 1 [Source:HGNC Symbol;Acc:28577]",
				"ENSG00000167139	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167139	TBC1D21	TBC1 domain family, member 21 [Source:HGNC Symbol;Acc:28536]",
				"ENSG00000143387	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143387	CTSK	cathepsin K [Source:HGNC Symbol;Acc:2536]",
				"ENSG00000095539	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095539	SEMA4G	sema domain, immunoglobulin domain (Ig), transmembrane domain (TM) and short cytoplasmic domain, (semaphorin) 4G [Source:HGNC Symbol;Acc:10735]",
				"ENSG00000198440	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198440	ZNF583	zinc finger protein 583 [Source:HGNC Symbol;Acc:26427]",
				"ENSG00000082641	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082641	NFE2L1	nuclear factor (erythroid-derived 2)-like 1 [Source:HGNC Symbol;Acc:7781]",
				"ENSG00000164237	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164237	CMBL	carboxymethylenebutenolidase homolog (Pseudomonas) [Source:HGNC Symbol;Acc:25090]",
				"ENSG00000138411	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138411	HECW2	HECT, C2 and WW domain containing E3 ubiquitin protein ligase 2 [Source:HGNC Symbol;Acc:29853]",
				"ENSG00000135409	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135409	AMHR2	anti-Mullerian hormone receptor, type II [Source:HGNC Symbol;Acc:465]",
				"ENSG00000103005	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103005	C16orf57	chromosome 16 open reading frame 57 [Source:HGNC Symbol;Acc:25792]",
				"ENSG00000103507	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103507	BCKDK	branched chain ketoacid dehydrogenase kinase [Source:HGNC Symbol;Acc:16902]",
				"ENSG00000145020	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145020	AMT	aminomethyltransferase [Source:HGNC Symbol;Acc:473]",
				"ENSG00000008405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008405	CRY1	cryptochrome 1 (photolyase-like) [Source:HGNC Symbol;Acc:2384]",
				"ENSG00000136045	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136045	PWP1	PWP1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:17015]",
				"ENSG00000173039	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173039	RELA	v-rel reticuloendotheliosis viral oncogene homolog A (avian) [Source:HGNC Symbol;Acc:9955]",
				"ENSG00000173320	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173320	STOX2	storkhead box 2 [Source:HGNC Symbol;Acc:25450]",
				"ENSG00000178802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178802	MPI	mannose phosphate isomerase [Source:HGNC Symbol;Acc:7216]",
				"ENSG00000166200	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166200	COPS2	COP9 constitutive photomorphogenic homolog subunit 2 (Arabidopsis) [Source:HGNC Symbol;Acc:30747]",
				"ENSG00000126524	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126524	SBDS	Shwachman-Bodian-Diamond syndrome [Source:HGNC Symbol;Acc:19440]",
				"ENSG00000155256	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155256	ZFYVE27	zinc finger, FYVE domain containing 27 [Source:HGNC Symbol;Acc:26559]",
				"ENSG00000038274	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000038274	MAT2B	methionine adenosyltransferase II, beta [Source:HGNC Symbol;Acc:6905]",
				"ENSG00000085978	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085978	ATG16L1	autophagy related 16-like 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:21498]",
				"ENSG00000112651	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112651	MRPL2	mitochondrial ribosomal protein L2 [Source:HGNC Symbol;Acc:14056]",
				"ENSG00000077458	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000077458	FAM76B	family with sequence similarity 76, member B [Source:HGNC Symbol;Acc:28492]",
				"ENSG00000116237	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116237	ICMT	isoprenylcysteine carboxyl methyltransferase [Source:HGNC Symbol;Acc:5350]",
				"ENSG00000146006	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146006	LRRTM2	leucine rich repeat transmembrane neuronal 2 [Source:HGNC Symbol;Acc:19409]",
				"ENSG00000138382	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138382	METTL5	methyltransferase like 5 [Source:HGNC Symbol;Acc:25006]",
				"ENSG00000064225	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064225	ST3GAL6	ST3 beta-galactoside alpha-2,3-sialyltransferase 6 [Source:HGNC Symbol;Acc:18080]",
				"ENSG00000145029	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145029	NICN1	nicolin 1 [Source:HGNC Symbol;Acc:18317]",
				"ENSG00000198646	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198646	NCOA6	nuclear receptor coactivator 6 [Source:HGNC Symbol;Acc:15936]",
				"ENSG00000135617	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135617	PRADC1	protease-associated domain containing 1 [Source:HGNC Symbol;Acc:16047]",
				"ENSG00000110066	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110066	SUV420H1	suppressor of variegation 4-20 homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:24283]",
				"ENSG00000116062	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116062	MSH6	mutS homolog 6 (E. coli) [Source:HGNC Symbol;Acc:7329]",
				"ENSG00000147912	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147912	FBXO10	F-box protein 10 [Source:HGNC Symbol;Acc:13589]",
				"ENSG00000171219	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171219	CDC42BPG	CDC42 binding protein kinase gamma (DMPK-like) [Source:HGNC Symbol;Acc:29829]",
				"ENSG00000181751	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181751	C5orf30	chromosome 5 open reading frame 30 [Source:HGNC Symbol;Acc:25052]",
				"ENSG00000141349	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141349	G6PC3	glucose 6 phosphatase, catalytic, 3 [Source:HGNC Symbol;Acc:24861]",
				"ENSG00000070610	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070610	GBA2	glucosidase, beta (bile acid) 2 [Source:HGNC Symbol;Acc:18986]",
				"ENSG00000117308	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117308	GALE	UDP-galactose-4-epimerase [Source:HGNC Symbol;Acc:4116]",
				"ENSG00000072954	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072954	TMEM38A	transmembrane protein 38A [Source:HGNC Symbol;Acc:28462]",
				"ENSG00000163322	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163322	FAM175A	family with sequence similarity 175, member A [Source:HGNC Symbol;Acc:25829]",
				"ENSG00000111845	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111845	PAK1IP1	PAK1 interacting protein 1 [Source:HGNC Symbol;Acc:20882]",
				"ENSG00000134072	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134072	CAMK1	calcium/calmodulin-dependent protein kinase I [Source:HGNC Symbol;Acc:1459]",
				"ENSG00000115828	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115828	QPCT	glutaminyl-peptide cyclotransferase [Source:HGNC Symbol;Acc:9753]",
				"ENSG00000109775	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109775	UFSP2	UFM1-specific peptidase 2 [Source:HGNC Symbol;Acc:25640]",
				"ENSG00000164934	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164934	DCAF13	DDB1 and CUL4 associated factor 13 [Source:HGNC Symbol;Acc:24535]",
				"ENSG00000105088	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105088	OLFM2	olfactomedin 2 [Source:HGNC Symbol;Acc:17189]",
				"ENSG00000100302	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100302	RASD2	RASD family, member 2 [Source:HGNC Symbol;Acc:18229]",
				"ENSG00000132300	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132300	PTCD3	pentatricopeptide repeat domain 3 [Source:HGNC Symbol;Acc:24717]",
				"ENSG00000011478	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011478	QPCTL	glutaminyl-peptide cyclotransferase-like [Source:HGNC Symbol;Acc:25952]",
				"ENSG00000110881	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110881	ASIC1	acid-sensing (proton-gated) ion channel 1 [Source:HGNC Symbol;Acc:100]",
				"ENSG00000164080	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164080	RAD54L2	RAD54-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:29123]",
				"ENSG00000123411	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123411	IKZF4	IKAROS family zinc finger 4 (Eos) [Source:HGNC Symbol;Acc:13179]",
				"ENSG00000104756	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104756	KCTD9	potassium channel tetramerisation domain containing 9 [Source:HGNC Symbol;Acc:22401]",
				"ENSG00000011426	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011426	ANLN	anillin, actin binding protein [Source:HGNC Symbol;Acc:14082]",
				"ENSG00000182271	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182271	TMIGD1	transmembrane and immunoglobulin domain containing 1 [Source:HGNC Symbol;Acc:32431]",
				"ENSG00000100281	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100281	HMGXB4	HMG box domain containing 4 [Source:HGNC Symbol;Acc:5003]",
				"ENSG00000205517	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205517	RGL3	ral guanine nucleotide dissociation stimulator-like 3 [Source:HGNC Symbol;Acc:30282]",
				"ENSG00000151498	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151498	ACAD8	acyl-CoA dehydrogenase family, member 8 [Source:HGNC Symbol;Acc:87]",
				"ENSG00000124571	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124571	XPO5	exportin 5 [Source:HGNC Symbol;Acc:17675]",
				"ENSG00000099991	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000099991	CABIN1	calcineurin binding protein 1 [Source:HGNC Symbol;Acc:24187]",
				"ENSG00000169241	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169241	SLC50A1	solute carrier family 50 (sugar transporter), member 1 [Source:HGNC Symbol;Acc:30657]",
				"ENSG00000110013	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110013	SIAE	sialic acid acetylesterase [Source:HGNC Symbol;Acc:18187]",
				"ENSG00000166855	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166855	CLPX	ClpX caseinolytic peptidase X homolog (E. coli) [Source:HGNC Symbol;Acc:2088]",
				"ENSG00000198815	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198815	FOXJ3	forkhead box J3 [Source:HGNC Symbol;Acc:29178]",
				"ENSG00000181789	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181789	COPG1	coatomer protein complex, subunit gamma 1 [Source:HGNC Symbol;Acc:2236]",
				"ENSG00000103269	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103269	RHBDL1	rhomboid, veinlet-like 1 (Drosophila) [Source:HGNC Symbol;Acc:10007]",
				"ENSG00000116406	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116406	EDEM3	ER degradation enhancer, mannosidase alpha-like 3 [Source:HGNC Symbol;Acc:16787]",
				"ENSG00000166167	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166167	BTRC	beta-transducin repeat containing E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:1144]",
				"ENSG00000134243	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134243	SORT1	sortilin 1 [Source:HGNC Symbol;Acc:11186]",
				"ENSG00000129083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129083	COPB1	coatomer protein complex, subunit beta 1 [Source:HGNC Symbol;Acc:2231]",
				"ENSG00000148468	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148468	FAM171A1	family with sequence similarity 171, member A1 [Source:HGNC Symbol;Acc:23522]",
				"ENSG00000082397	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082397	EPB41L3	erythrocyte membrane protein band 4.1-like 3 [Source:HGNC Symbol;Acc:3380]",
				"ENSG00000213930	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213930	GALT	galactose-1-phosphate uridylyltransferase [Source:HGNC Symbol;Acc:4135]",
				"ENSG00000004766	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004766	CCDC132	coiled-coil domain containing 132 [Source:HGNC Symbol;Acc:25956]",
				"ENSG00000129167	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129167	TPH1	tryptophan hydroxylase 1 [Source:HGNC Symbol;Acc:12008]",
				"ENSG00000168246	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168246	UBTD2	ubiquitin domain containing 2 [Source:HGNC Symbol;Acc:24463]",
				"ENSG00000089693	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089693	MLF2	myeloid leukemia factor 2 [Source:HGNC Symbol;Acc:7126]",
				"ENSG00000179314	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179314	WSCD1	WSC domain containing 1 [Source:HGNC Symbol;Acc:29060]",
				"ENSG00000138604	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138604	GLCE	glucuronic acid epimerase [Source:HGNC Symbol;Acc:17855]",
				"ENSG00000163083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163083	INHBB	inhibin, beta B [Source:HGNC Symbol;Acc:6067]",
				"ENSG00000141644	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141644	MBD1	methyl-CpG binding domain protein 1 [Source:HGNC Symbol;Acc:6916]",
				"ENSG00000076242	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076242	MLH1	mutL homolog 1, colon cancer, nonpolyposis type 2 (E. coli) [Source:HGNC Symbol;Acc:7127]",
				"ENSG00000082258	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082258	CCNT2	cyclin T2 [Source:HGNC Symbol;Acc:1600]",
				"ENSG00000141642	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141642	ELAC1	elaC homolog 1 (E. coli) [Source:HGNC Symbol;Acc:14197]",
				"ENSG00000133657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133657	ATP13A3	ATPase type 13A3 [Source:HGNC Symbol;Acc:24113]",
				"ENSG00000105197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105197	TIMM50	translocase of inner mitochondrial membrane 50 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23656]",
				"ENSG00000136643	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136643	RPS6KC1	ribosomal protein S6 kinase, 52kDa, polypeptide 1 [Source:HGNC Symbol;Acc:10439]",
				"ENSG00000165280	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165280	VCP	valosin containing protein [Source:HGNC Symbol;Acc:12666]",
				"ENSG00000162066	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162066	AMDHD2	amidohydrolase domain containing 2 [Source:HGNC Symbol;Acc:24262]",
				"ENSG00000169914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169914	OTUD3	OTU domain containing 3 [Source:HGNC Symbol;Acc:29038]",
				"ENSG00000168906	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168906	MAT2A	methionine adenosyltransferase II, alpha [Source:HGNC Symbol;Acc:6904]",
				"ENSG00000135838	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135838	NPL	N-acetylneuraminate pyruvate lyase (dihydrodipicolinate synthase) [Source:HGNC Symbol;Acc:16781]",
				"ENSG00000165832	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165832	TRUB1	TruB pseudouridine (psi) synthase homolog 1 (E. coli) [Source:HGNC Symbol;Acc:16060]",
				"ENSG00000106397	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106397	PLOD3	procollagen-lysine, 2-oxoglutarate 5-dioxygenase 3 [Source:HGNC Symbol;Acc:9083]",
				"ENSG00000152766	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152766	ANKRD22	ankyrin repeat domain 22 [Source:HGNC Symbol;Acc:28321]",
				"ENSG00000069329	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069329	VPS35	vacuolar protein sorting 35 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:13487]",
				"ENSG00000163082	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163082	SGPP2	sphingosine-1-phosphate phosphatase 2 [Source:HGNC Symbol;Acc:19953]",
				"ENSG00000021776	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000021776	AQR	aquarius homolog (mouse) [Source:HGNC Symbol;Acc:29513]",
				"ENSG00000152520	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152520	PAN3	PAN3 poly(A) specific ribonuclease subunit homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:29991]",
				"ENSG00000134815	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134815	DHX34	DEAH (Asp-Glu-Ala-His) box polypeptide 34 [Source:HGNC Symbol;Acc:16719]",
				"ENSG00000106524	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106524	ANKMY2	ankyrin repeat and MYND domain containing 2 [Source:HGNC Symbol;Acc:25370]",
				"ENSG00000122870	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122870	BICC1	bicaudal C homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:19351]",
				"ENSG00000169783	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169783	LINGO1	leucine rich repeat and Ig domain containing 1 [Source:HGNC Symbol;Acc:21205]",
				"ENSG00000213339	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213339	QTRT1	queuine tRNA-ribosyltransferase 1 [Source:HGNC Symbol;Acc:23797]",
				"ENSG00000105696	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105696	TMEM59L	transmembrane protein 59-like [Source:HGNC Symbol;Acc:13237]",
				"ENSG00000164733	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164733	CTSB	cathepsin B [Source:HGNC Symbol;Acc:2527]",
				"ENSG00000148572	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148572	NRBF2	nuclear receptor binding factor 2 [Source:HGNC Symbol;Acc:19692]",
				"ENSG00000087087	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000087087	SRRT	serrate RNA effector molecule homolog (Arabidopsis) [Source:HGNC Symbol;Acc:24101]",
				"ENSG00000116095	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116095	PLEKHA3	pleckstrin homology domain containing, family A (phosphoinositide binding specific) member 3 [Source:HGNC Symbol;Acc:14338]",
				"ENSG00000100897	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100897	DCAF11	DDB1 and CUL4 associated factor 11 [Source:HGNC Symbol;Acc:20258]",
				"ENSG00000013392	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013392	RWDD2A	RWD domain containing 2A [Source:HGNC Symbol;Acc:21385]",
				"ENSG00000159086	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159086	GCFC1	GC-rich sequence DNA-binding factor 1 [Source:HGNC Symbol;Acc:13579]",
				"ENSG00000104537	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104537	ANXA13	annexin A13 [Source:HGNC Symbol;Acc:536]",
				"ENSG00000119689	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119689	DLST	dihydrolipoamide S-succinyltransferase (E2 component of 2-oxo-glutarate complex) [Source:HGNC Symbol;Acc:2911]",
				"ENSG00000148158	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148158	SNX30	sorting nexin family member 30 [Source:HGNC Symbol;Acc:23685]",
				"ENSG00000005238	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005238	FAM214B	family with sequence similarity 214, member B [Source:HGNC Symbol;Acc:25666]",
				"ENSG00000198265	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198265	HELZ	helicase with zinc finger [Source:HGNC Symbol;Acc:16878]",
				"ENSG00000221986	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221986	MYBPHL	myosin binding protein H-like [Source:HGNC Symbol;Acc:30434]",
				"ENSG00000078699	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000078699	CBFA2T2	core-binding factor, runt domain, alpha subunit 2; translocated to, 2 [Source:HGNC Symbol;Acc:1536]",
				"ENSG00000135372	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135372	NAT10	N-acetyltransferase 10 (GCN5-related) [Source:HGNC Symbol;Acc:29830]",
				"ENSG00000143337	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143337	TOR1AIP1	torsin A interacting protein 1 [Source:HGNC Symbol;Acc:29456]",
				"ENSG00000198774	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198774	RASSF9	Ras association (RalGDS/AF-6) domain family (N-terminal) member 9 [Source:HGNC Symbol;Acc:15739]",
				"ENSG00000170892	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170892	TSEN34	tRNA splicing endonuclease 34 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:15506]",
				"ENSG00000135720	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135720	DYNC1LI2	dynein, cytoplasmic 1, light intermediate chain 2 [Source:HGNC Symbol;Acc:2966]",
				"ENSG00000132394	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132394	EEFSEC	eukaryotic elongation factor, selenocysteine-tRNA-specific [Source:HGNC Symbol;Acc:24614]",
				"ENSG00000145868	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145868	FBXO38	F-box protein 38 [Source:HGNC Symbol;Acc:28844]",
				"ENSG00000011243	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011243	AKAP8L	A kinase (PRKA) anchor protein 8-like [Source:HGNC Symbol;Acc:29857]",
				"ENSG00000137207	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137207	YIPF3	Yip1 domain family, member 3 [Source:HGNC Symbol;Acc:21023]",
				"ENSG00000011260	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011260	UTP18	UTP18 small subunit (SSU) processome component homolog (yeast) [Source:HGNC Symbol;Acc:24274]",
				"ENSG00000170633	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170633	RNF34	ring finger protein 34, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:17297]",
				"ENSG00000057657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000057657	PRDM1	PR domain containing 1, with ZNF domain [Source:HGNC Symbol;Acc:9346]",
				"ENSG00000104447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104447	TRPS1	trichorhinophalangeal syndrome I [Source:HGNC Symbol;Acc:12340]",
				"ENSG00000111669	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111669	TPI1	triosephosphate isomerase 1 [Source:HGNC Symbol;Acc:12009]",
				"ENSG00000003756	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000003756	RBM5	RNA binding motif protein 5 [Source:HGNC Symbol;Acc:9902]",
				"ENSG00000151458	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151458	ANKRD50	ankyrin repeat domain 50 [Source:HGNC Symbol;Acc:29223]",
				"ENSG00000204930	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204930	FAM221B	family with sequence similarity 221, member B [Source:HGNC Symbol;Acc:30762]",
				"ENSG00000164749	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164749	HNF4G	hepatocyte nuclear factor 4, gamma [Source:HGNC Symbol;Acc:5026]",
				"ENSG00000131061	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131061	ZNF341	zinc finger protein 341 [Source:HGNC Symbol;Acc:15992]",
				"ENSG00000135374	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135374	ELF5	E74-like factor 5 (ets domain transcription factor) [Source:HGNC Symbol;Acc:3320]",
				"ENSG00000142186	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000142186	SCYL1	SCY1-like 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:14372]",
				"ENSG00000102981	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102981	PARD6A	par-6 partitioning defective 6 homolog alpha (C. elegans) [Source:HGNC Symbol;Acc:15943]",
				"ENSG00000135093	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135093	USP30	ubiquitin specific peptidase 30 [Source:HGNC Symbol;Acc:20065]",
				"ENSG00000168137	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168137	SETD5	SET domain containing 5 [Source:HGNC Symbol;Acc:25566]",
				"ENSG00000112038	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112038	OPRM1	opioid receptor, mu 1 [Source:HGNC Symbol;Acc:8156]",
				"ENSG00000116698	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116698	SMG7	smg-7 homolog, nonsense mediated mRNA decay factor (C. elegans) [Source:HGNC Symbol;Acc:16792]",
				"ENSG00000137574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137574	TGS1	trimethylguanosine synthase 1 [Source:HGNC Symbol;Acc:17843]",
				"ENSG00000000005	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000000005	TNMD	tenomodulin [Source:HGNC Symbol;Acc:17757]",
				"ENSG00000110107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110107	PRPF19	PRP19/PSO4 pre-mRNA processing factor 19 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:17896]",
				"ENSG00000147262	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147262	GPR119	G protein-coupled receptor 119 [Source:HGNC Symbol;Acc:19060]",
				"ENSG00000143363	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143363	PRUNE	prune homolog (Drosophila) [Source:HGNC Symbol;Acc:13420]",
				"ENSG00000112992	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112992	NNT	nicotinamide nucleotide transhydrogenase [Source:HGNC Symbol;Acc:7863]",
				"ENSG00000099949	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000099949	LZTR1	leucine-zipper-like transcription regulator 1 [Source:HGNC Symbol;Acc:6742]",
				"ENSG00000169018	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169018	FEM1B	fem-1 homolog b (C. elegans) [Source:HGNC Symbol;Acc:3649]",
				"ENSG00000157657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157657	ZNF618	zinc finger protein 618 [Source:HGNC Symbol;Acc:29416]",
				"ENSG00000115525	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115525	ST3GAL5	ST3 beta-galactoside alpha-2,3-sialyltransferase 5 [Source:HGNC Symbol;Acc:10872]",
				"ENSG00000127483	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127483	HP1BP3	heterochromatin protein 1, binding protein 3 [Source:HGNC Symbol;Acc:24973]",
				"ENSG00000082146	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082146	STRADB	STE20-related kinase adaptor beta [Source:HGNC Symbol;Acc:13205]",
				"ENSG00000108509	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108509	CAMTA2	calmodulin binding transcription activator 2 [Source:HGNC Symbol;Acc:18807]",
				"ENSG00000076248	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076248	UNG	uracil-DNA glycosylase [Source:HGNC Symbol;Acc:12572]",
				"ENSG00000100056	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100056	DGCR14	DiGeorge syndrome critical region gene 14 [Source:HGNC Symbol;Acc:16817]",
				"ENSG00000170242	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170242	USP47	ubiquitin specific peptidase 47 [Source:HGNC Symbol;Acc:20076]",
				"ENSG00000029363	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000029363	BCLAF1	BCL2-associated transcription factor 1 [Source:HGNC Symbol;Acc:16863]",
				"ENSG00000115289	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115289	PCGF1	polycomb group ring finger 1 [Source:HGNC Symbol;Acc:17615]",
				"ENSG00000100697	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100697	DICER1	dicer 1, ribonuclease type III [Source:HGNC Symbol;Acc:17098]",
				"ENSG00000165458	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165458	INPPL1	inositol polyphosphate phosphatase-like 1 [Source:HGNC Symbol;Acc:6080]",
				"ENSG00000013275	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013275	PSMC4	proteasome (prosome, macropain) 26S subunit, ATPase, 4 [Source:HGNC Symbol;Acc:9551]",
				"ENSG00000085382	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085382	HACE1	HECT domain and ankyrin repeat containing E3 ubiquitin protein ligase 1 [Source:HGNC Symbol;Acc:21033]",
				"ENSG00000004455	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004455	AK2	adenylate kinase 2 [Source:HGNC Symbol;Acc:362]",
				"ENSG00000118985	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118985	ELL2	elongation factor, RNA polymerase II, 2 [Source:HGNC Symbol;Acc:17064]",
				"ENSG00000054116	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000054116	TRAPPC3	trafficking protein particle complex 3 [Source:HGNC Symbol;Acc:19942]",
				"ENSG00000166946	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166946	CCNDBP1	cyclin D-type binding-protein 1 [Source:HGNC Symbol;Acc:1587]",
				"ENSG00000162510	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162510	MATN1	matrilin 1, cartilage matrix protein [Source:HGNC Symbol;Acc:6907]",
				"ENSG00000011523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011523	CEP68	centrosomal protein 68kDa [Source:HGNC Symbol;Acc:29076]",
				"ENSG00000011275	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000011275	RNF216	ring finger protein 216 [Source:HGNC Symbol;Acc:21698]",
				"ENSG00000185278	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185278	ZBTB37	zinc finger and BTB domain containing 37 [Source:HGNC Symbol;Acc:28365]",
				"ENSG00000102974	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102974	CTCF	CCCTC-binding factor (zinc finger protein) [Source:HGNC Symbol;Acc:13723]",
				"ENSG00000157613	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157613	CREB3L1	cAMP responsive element binding protein 3-like 1 [Source:HGNC Symbol;Acc:18856]",
				"ENSG00000128891	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128891	C15orf57	chromosome 15 open reading frame 57 [Source:HGNC Symbol;Acc:28295]",
				"ENSG00000089351	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089351	GRAMD1A	GRAM domain containing 1A [Source:HGNC Symbol;Acc:29305]",
				"ENSG00000164209	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164209	SLC25A46	solute carrier family 25, member 46 [Source:HGNC Symbol;Acc:25198]",
				"ENSG00000119979	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119979	FAM45A	family with sequence similarity 45, member A [Source:HGNC Symbol;Acc:31793]",
				"ENSG00000173264	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173264	GPR137	G protein-coupled receptor 137 [Source:HGNC Symbol;Acc:24300]",
				"ENSG00000166164	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166164	BRD7	bromodomain containing 7 [Source:HGNC Symbol;Acc:14310]",
				"ENSG00000164896	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164896	FASTK	Fas-activated serine/threonine kinase [Source:HGNC Symbol;Acc:24676]",
				"ENSG00000038002	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000038002	AGA	aspartylglucosaminidase [Source:HGNC Symbol;Acc:318]",
				"ENSG00000166886	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166886	NAB2	NGFI-A binding protein 2 (EGR1 binding protein 2) [Source:HGNC Symbol;Acc:7627]",
				"ENSG00000157551	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157551	KCNJ15	potassium inwardly-rectifying channel, subfamily J, member 15 [Source:HGNC Symbol;Acc:6261]",
				"ENSG00000164221	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164221	CCDC112	coiled-coil domain containing 112 [Source:HGNC Symbol;Acc:28599]",
				"ENSG00000143369	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143369	ECM1	extracellular matrix protein 1 [Source:HGNC Symbol;Acc:3153]",
				"ENSG00000156509	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156509	FBXO43	F-box protein 43 [Source:HGNC Symbol;Acc:28521]",
				"ENSG00000107185	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107185	RGP1	RGP1 retrograde golgi transport homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:21965]",
				"ENSG00000139697	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139697	SBNO1	strawberry notch homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:22973]",
				"ENSG00000167325	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167325	RRM1	ribonucleotide reductase M1 [Source:HGNC Symbol;Acc:10451]",
				"ENSG00000140829	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140829	DHX38	DEAH (Asp-Glu-Ala-His) box polypeptide 38 [Source:HGNC Symbol;Acc:17211]",
				"ENSG00000103510	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103510	KAT8	K(lysine) acetyltransferase 8 [Source:HGNC Symbol;Acc:17933]",
				"ENSG00000215305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000215305	VPS16	vacuolar protein sorting 16 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:14584]",
				"ENSG00000148840	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148840	PPRC1	peroxisome proliferator-activated receptor gamma, coactivator-related 1 [Source:HGNC Symbol;Acc:30025]",
				"ENSG00000091039	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000091039	OSBPL8	oxysterol binding protein-like 8 [Source:HGNC Symbol;Acc:16396]",
				"ENSG00000102554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102554	KLF5	Kruppel-like factor 5 (intestinal) [Source:HGNC Symbol;Acc:6349]",
				"ENSG00000136870	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136870	ZNF189	zinc finger protein 189 [Source:HGNC Symbol;Acc:12980]",
				"ENSG00000179134	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179134	SAMD4B	sterile alpha motif domain containing 4B [Source:HGNC Symbol;Acc:25492]",
				"ENSG00000126088	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126088	UROD	uroporphyrinogen decarboxylase [Source:HGNC Symbol;Acc:12591]",
				"ENSG00000138385	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138385	SSB	Sjogren syndrome antigen B (autoantigen La) [Source:HGNC Symbol;Acc:11316]",
				"ENSG00000163430	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163430	FSTL1	follistatin-like 1 [Source:HGNC Symbol;Acc:3972]",
				"ENSG00000162065	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162065	TBC1D24	TBC1 domain family, member 24 [Source:HGNC Symbol;Acc:29203]",
				"ENSG00000100823	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100823	APEX1	APEX nuclease (multifunctional DNA repair enzyme) 1 [Source:HGNC Symbol;Acc:587]",
				"ENSG00000099341	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000099341	PSMD8	proteasome (prosome, macropain) 26S subunit, non-ATPase, 8 [Source:HGNC Symbol;Acc:9566]",
				"ENSG00000148925	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148925	BTBD10	BTB (POZ) domain containing 10 [Source:HGNC Symbol;Acc:21445]",
				"ENSG00000126746	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126746	ZNF384	zinc finger protein 384 [Source:HGNC Symbol;Acc:11955]",
				"ENSG00000115216	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115216	NRBP1	nuclear receptor binding protein 1 [Source:HGNC Symbol;Acc:7993]",
				"ENSG00000100982	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100982	PCIF1	PDX1 C-terminal inhibiting factor 1 [Source:HGNC Symbol;Acc:16200]",
				"ENSG00000130726	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130726	TRIM28	tripartite motif containing 28 [Source:HGNC Symbol;Acc:16384]",
				"ENSG00000085276	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000085276	MECOM	MDS1 and EVI1 complex locus [Source:HGNC Symbol;Acc:3498]",
				"ENSG00000125107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125107	CNOT1	CCR4-NOT transcription complex, subunit 1 [Source:HGNC Symbol;Acc:7877]",
				"ENSG00000042429	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000042429	MED17	mediator complex subunit 17 [Source:HGNC Symbol;Acc:2375]",
				"ENSG00000106328	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106328	FSCN3	fascin homolog 3, actin-bundling protein, testicular (Strongylocentrotus purpuratus) [Source:HGNC Symbol;Acc:3961]",
				"ENSG00000157766	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157766	ACAN	aggrecan [Source:HGNC Symbol;Acc:319]",
				"ENSG00000102904	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102904	TSNAXIP1	translin-associated factor X interacting protein 1 [Source:HGNC Symbol;Acc:18586]",
				"ENSG00000164197	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164197	RNF180	ring finger protein 180 [Source:HGNC Symbol;Acc:27752]",
				"ENSG00000100359	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100359	SGSM3	small G protein signaling modulator 3 [Source:HGNC Symbol;Acc:25228]",
				"ENSG00000156970	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156970	BUB1B	budding uninhibited by benzimidazoles 1 homolog beta (yeast) [Source:HGNC Symbol;Acc:1149]",
				"ENSG00000113302	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113302	IL12B	interleukin 12B (natural killer cell stimulatory factor 2, cytotoxic lymphocyte maturation factor 2, p40) [Source:HGNC Symbol;Acc:5970]",
				"ENSG00000143398	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143398	PIP5K1A	phosphatidylinositol-4-phosphate 5-kinase, type I, alpha [Source:HGNC Symbol;Acc:8994]",
				"ENSG00000112576	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112576	CCND3	cyclin D3 [Source:HGNC Symbol;Acc:1585]",
				"ENSG00000204628	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204628	GNB2L1	guanine nucleotide binding protein (G protein), beta polypeptide 2-like 1 [Source:HGNC Symbol;Acc:4399]",
				"ENSG00000104738	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104738	MCM4	minichromosome maintenance complex component 4 [Source:HGNC Symbol;Acc:6947]",
				"ENSG00000136960	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136960	ENPP2	ectonucleotide pyrophosphatase/phosphodiesterase 2 [Source:HGNC Symbol;Acc:3357]",
				"ENSG00000156273	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156273	BACH1	BTB and CNC homology 1, basic leucine zipper transcription factor 1 [Source:HGNC Symbol;Acc:935]",
				"ENSG00000186532	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186532	SMYD4	SET and MYND domain containing 4 [Source:HGNC Symbol;Acc:21067]",
				"ENSG00000167528	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167528	ZNF641	zinc finger protein 641 [Source:HGNC Symbol;Acc:31834]",
				"ENSG00000120539	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120539	MASTL	microtubule associated serine/threonine kinase-like [Source:HGNC Symbol;Acc:19042]",
				"ENSG00000103653	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103653	CSK	c-src tyrosine kinase [Source:HGNC Symbol;Acc:2444]",
				"ENSG00000123395	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123395	C12orf44	chromosome 12 open reading frame 44 [Source:HGNC Symbol;Acc:25679]",
				"ENSG00000148602	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148602	LRIT1	leucine-rich repeat, immunoglobulin-like and transmembrane domains 1 [Source:HGNC Symbol;Acc:23404]",
				"ENSG00000171792	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171792	C12orf32	chromosome 12 open reading frame 32 [Source:HGNC Symbol;Acc:28206]",
				"ENSG00000139537	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139537	CCDC65	coiled-coil domain containing 65 [Source:HGNC Symbol;Acc:29937]",
				"ENSG00000149294	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149294	NCAM1	neural cell adhesion molecule 1 [Source:HGNC Symbol;Acc:7656]",
				"ENSG00000091436	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000091436	AC013461.1	Mitogen-activated protein kinase kinase kinase MLT  [Source:UniProtKB/Swiss-Prot;Acc:Q9NYL2]",
				"ENSG00000100979	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100979	PLTP	phospholipid transfer protein [Source:HGNC Symbol;Acc:9093]",
				"ENSG00000138380	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138380	ALS2CR8	amyotrophic lateral sclerosis 2 (juvenile) chromosome region, candidate 8 [Source:HGNC Symbol;Acc:14435]",
				"ENSG00000164076	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164076	CAMKV	CaM kinase-like vesicle-associated [Source:HGNC Symbol;Acc:28788]",
				"ENSG00000140396	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140396	NCOA2	nuclear receptor coactivator 2 [Source:HGNC Symbol;Acc:7669]",
				"ENSG00000177981	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177981	ASB8	ankyrin repeat and SOCS box containing 8 [Source:HGNC Symbol;Acc:17183]",
				"ENSG00000139055	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139055	ERP27	endoplasmic reticulum protein 27 [Source:HGNC Symbol;Acc:26495]",
				"ENSG00000153291	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153291	SLC25A27	solute carrier family 25, member 27 [Source:HGNC Symbol;Acc:21065]",
				"ENSG00000171885	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171885	AQP4	aquaporin 4 [Source:HGNC Symbol;Acc:637]",
				"ENSG00000164078	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164078	MST1R	macrophage stimulating 1 receptor (c-met-related tyrosine kinase) [Source:HGNC Symbol;Acc:7381]",
				"ENSG00000122417	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122417	ODF2L	outer dense fiber of sperm tails 2-like [Source:HGNC Symbol;Acc:29225]",
				"ENSG00000112773	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112773	FAM46A	family with sequence similarity 46, member A [Source:HGNC Symbol;Acc:18345]",
				"ENSG00000010292	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010292	NCAPD2	non-SMC condensin I complex, subunit D2 [Source:HGNC Symbol;Acc:24305]",
				"ENSG00000100462	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100462	PRMT5	protein arginine methyltransferase 5 [Source:HGNC Symbol;Acc:10894]",
				"ENSG00000162391	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162391	FAM151A	family with sequence similarity 151, member A [Source:HGNC Symbol;Acc:25032]",
				"ENSG00000114062	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114062	UBE3A	ubiquitin protein ligase E3A [Source:HGNC Symbol;Acc:12496]",
				"ENSG00000149308	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149308	NPAT	nuclear protein, ataxia-telangiectasia locus [Source:HGNC Symbol;Acc:7896]",
				"ENSG00000173230	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173230	GOLGB1	golgin B1 [Source:HGNC Symbol;Acc:4429]",
				"ENSG00000119321	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119321	FKBP15	FK506 binding protein 15, 133kDa [Source:HGNC Symbol;Acc:23397]",
				"ENSG00000047346	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000047346	FAM214A	family with sequence similarity 214, member A [Source:HGNC Symbol;Acc:25609]",
				"ENSG00000171044	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171044	XKR6	XK, Kell blood group complex subunit-related family, member 6 [Source:HGNC Symbol;Acc:27806]",
				"ENSG00000108588	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108588	CCDC47	coiled-coil domain containing 47 [Source:HGNC Symbol;Acc:24856]",
				"ENSG00000111011	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111011	RSRC2	arginine/serine-rich coiled-coil 2 [Source:HGNC Symbol;Acc:30559]",
				"ENSG00000072571	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000072571	HMMR	hyaluronan-mediated motility receptor (RHAMM) [Source:HGNC Symbol;Acc:5012]",
				"ENSG00000183671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183671	GPR1	G protein-coupled receptor 1 [Source:HGNC Symbol;Acc:4463]",
				"ENSG00000071243	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071243	ING3	inhibitor of growth family, member 3 [Source:HGNC Symbol;Acc:14587]",
				"ENSG00000107104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107104	KANK1	KN motif and ankyrin repeat domains 1 [Source:HGNC Symbol;Acc:19309]",
				"ENSG00000183621	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183621	ZNF438	zinc finger protein 438 [Source:HGNC Symbol;Acc:21029]",
				"ENSG00000111405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111405	ENDOU	endonuclease, polyU-specific [Source:HGNC Symbol;Acc:14369]",
				"ENSG00000155066	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155066	PROM2	prominin 2 [Source:HGNC Symbol;Acc:20685]",
				"ENSG00000146007	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146007	ZMAT2	zinc finger, matrin-type 2 [Source:HGNC Symbol;Acc:26433]",
				"ENSG00000157077	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157077	ZFYVE9	zinc finger, FYVE domain containing 9 [Source:HGNC Symbol;Acc:6775]",
				"ENSG00000146834	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146834	MEPCE	methylphosphate capping enzyme [Source:HGNC Symbol;Acc:20247]",
				"ENSG00000156253	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156253	RWDD2B	RWD domain containing 2B [Source:HGNC Symbol;Acc:1302]",
				"ENSG00000114204	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114204	SERPINI2	serpin peptidase inhibitor, clade I (pancpin), member 2 [Source:HGNC Symbol;Acc:8945]",
				"ENSG00000076382	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000076382	SPAG5	sperm associated antigen 5 [Source:HGNC Symbol;Acc:13452]",
				"ENSG00000168676	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168676	KCTD19	potassium channel tetramerisation domain containing 19 [Source:HGNC Symbol;Acc:24753]",
				"ENSG00000171236	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171236	LRG1	leucine-rich alpha-2-glycoprotein 1 [Source:HGNC Symbol;Acc:29480]",
				"ENSG00000167711	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167711	SERPINF2	serpin peptidase inhibitor, clade F (alpha-2 antiplasmin, pigment epithelium derived factor), member 2 [Source:HGNC Symbol;Acc:9075]",
				"ENSG00000188039	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188039	NWD1	NACHT and WD repeat domain containing 1 [Source:HGNC Symbol;Acc:27619]",
				"ENSG00000132205	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132205	EMILIN2	elastin microfibril interfacer 2 [Source:HGNC Symbol;Acc:19881]",
				"ENSG00000184575	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184575	XPOT	exportin, tRNA (nuclear export receptor for tRNAs) [Source:HGNC Symbol;Acc:12826]",
				"ENSG00000089775	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089775	ZBTB25	zinc finger and BTB domain containing 25 [Source:HGNC Symbol;Acc:13112]",
				"ENSG00000101940	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101940	WDR13	WD repeat domain 13 [Source:HGNC Symbol;Acc:14352]",
				"ENSG00000064490	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064490	RFXANK	regulatory factor X-associated ankyrin-containing protein [Source:HGNC Symbol;Acc:9987]",
				"ENSG00000009413	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000009413	REV3L	REV3-like, polymerase (DNA directed), zeta, catalytic subunit [Source:HGNC Symbol;Acc:9968]",
				"ENSG00000122733	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122733	KIAA1045	KIAA1045 [Source:HGNC Symbol;Acc:29180]",
				"ENSG00000103248	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103248	MTHFSD	methenyltetrahydrofolate synthetase domain containing [Source:HGNC Symbol;Acc:25778]",
				"ENSG00000175970	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175970	UNC119B	unc-119 homolog B (C. elegans) [Source:HGNC Symbol;Acc:16488]",
				"ENSG00000198863	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198863	RUNDC1	RUN domain containing 1 [Source:HGNC Symbol;Acc:25418]",
				"ENSG00000198561	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198561	CTNND1	catenin (cadherin-associated protein), delta 1 [Source:HGNC Symbol;Acc:2515]",
				"ENSG00000168010	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168010	ATG16L2	autophagy related 16-like 2 (S. cerevisiae) [Source:HGNC Symbol;Acc:25464]",
				"ENSG00000137100	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137100	DCTN3	dynactin 3 (p22) [Source:HGNC Symbol;Acc:2713]",
				"ENSG00000134759	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134759	ELP2	elongation protein 2 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:18248]",
				"ENSG00000152223	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152223	EPG5	ectopic P-granules autophagy protein 5 homolog (C. elegans) [Source:HGNC Symbol;Acc:29331]",
				"ENSG00000118017	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118017	A4GNT	alpha-1,4-N-acetylglucosaminyltransferase [Source:HGNC Symbol;Acc:17968]",
				"ENSG00000065328	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065328	MCM10	minichromosome maintenance complex component 10 [Source:HGNC Symbol;Acc:18043]",
				"ENSG00000144567	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144567	FAM134A	family with sequence similarity 134, member A [Source:HGNC Symbol;Acc:28450]",
				"ENSG00000130812	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130812	ANGPTL6	angiopoietin-like 6 [Source:HGNC Symbol;Acc:23140]",
				"ENSG00000124357	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124357	NAGK	N-acetylglucosamine kinase [Source:HGNC Symbol;Acc:17174]",
				"ENSG00000112964	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112964	GHR	growth hormone receptor [Source:HGNC Symbol;Acc:4263]",
				"ENSG00000135914	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135914	HTR2B	5-hydroxytryptamine (serotonin) receptor 2B, G protein-coupled [Source:HGNC Symbol;Acc:5294]",
				"ENSG00000164062	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164062	APEH	N-acylaminoacyl-peptide hydrolase [Source:HGNC Symbol;Acc:586]",
				"ENSG00000152464	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152464	RPP38	ribonuclease P/MRP 38kDa subunit [Source:HGNC Symbol;Acc:30329]",
				"ENSG00000120832	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120832	MTERFD3	MTERF domain containing 3 [Source:HGNC Symbol;Acc:30779]",
				"ENSG00000167986	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167986	DDB1	damage-specific DNA binding protein 1, 127kDa [Source:HGNC Symbol;Acc:2717]",
				"ENSG00000167962	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167962	ZNF598	zinc finger protein 598 [Source:HGNC Symbol;Acc:28079]",
				"ENSG00000206562	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000206562	METTL6	methyltransferase like 6 [Source:HGNC Symbol;Acc:28343]",
				"ENSG00000118298	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118298	CA14	carbonic anhydrase XIV [Source:HGNC Symbol;Acc:1372]",
				"ENSG00000092295	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092295	TGM1	transglutaminase 1 (K polypeptide epidermal type I, protein-glutamine-gamma-glutamyltransferase) [Source:HGNC Symbol;Acc:11777]",
				"ENSG00000079999	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000079999	KEAP1	kelch-like ECH-associated protein 1 [Source:HGNC Symbol;Acc:23177]",
				"ENSG00000182447	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182447	OTOL1	otolin 1 [Source:HGNC Symbol;Acc:34071]",
				"ENSG00000074695	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000074695	LMAN1	lectin, mannose-binding, 1 [Source:HGNC Symbol;Acc:6631]",
				"ENSG00000129048	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129048	CCRL1	chemokine (C-C motif) receptor-like 1 [Source:HGNC Symbol;Acc:1611]",
				"ENSG00000134028	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134028	ADAMDEC1	ADAM-like, decysin 1 [Source:HGNC Symbol;Acc:16299]",
				"ENSG00000115850	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115850	LCT	lactase [Source:HGNC Symbol;Acc:6530]",
				"ENSG00000109756	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109756	RAPGEF2	Rap guanine nucleotide exchange factor (GEF) 2 [Source:HGNC Symbol;Acc:16854]",
				"ENSG00000186106	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186106	ANKRD46	ankyrin repeat domain 46 [Source:HGNC Symbol;Acc:27229]",
				"ENSG00000163104	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163104	SMARCAD1	SWI/SNF-related, matrix-associated actin-dependent regulator of chromatin, subfamily a, containing DEAD/H box 1 [Source:HGNC Symbol;Acc:18398]",
				"ENSG00000107560	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107560	RAB11FIP2	RAB11 family interacting protein 2 (class I) [Source:HGNC Symbol;Acc:29152]",
				"ENSG00000167182	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167182	SP2	Sp2 transcription factor [Source:HGNC Symbol;Acc:11207]",
				"ENSG00000119636	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119636	C14orf45	chromosome 14 open reading frame 45 [Source:HGNC Symbol;Acc:19855]",
				"ENSG00000138593	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138593	SECISBP2L	SECIS binding protein 2-like [Source:HGNC Symbol;Acc:28997]",
				"ENSG00000092850	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092850	TEKT2	tektin 2 (testicular) [Source:HGNC Symbol;Acc:11725]",
				"ENSG00000205744	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205744	DENND1C	DENN/MADD domain containing 1C [Source:HGNC Symbol;Acc:26225]",
				"ENSG00000005007	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005007	UPF1	UPF1 regulator of nonsense transcripts homolog (yeast) [Source:HGNC Symbol;Acc:9962]",
				"ENSG00000121454	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121454	LHX4	LIM homeobox 4 [Source:HGNC Symbol;Acc:21734]",
				"ENSG00000152705	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152705	CATSPER3	cation channel, sperm associated 3 [Source:HGNC Symbol;Acc:20819]",
				"ENSG00000221947	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000221947	XKR9	XK, Kell blood group complex subunit-related family, member 9 [Source:HGNC Symbol;Acc:20937]",
				"ENSG00000119227	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119227	PIGZ	phosphatidylinositol glycan anchor biosynthesis, class Z [Source:HGNC Symbol;Acc:30596]",
				"ENSG00000133958	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133958	UNC79	unc-79 homolog (C. elegans) [Source:HGNC Symbol;Acc:19966]",
				"ENSG00000132911	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132911	NMUR2	neuromedin U receptor 2 [Source:HGNC Symbol;Acc:16454]",
				"ENSG00000122584	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122584	NXPH1	neurexophilin 1 [Source:HGNC Symbol;Acc:20693]",
				"ENSG00000166250	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166250	CLMP	CXADR-like membrane protein [Source:HGNC Symbol;Acc:24039]",
				"ENSG00000155890	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000155890	TRIM42	tripartite motif containing 42 [Source:HGNC Symbol;Acc:19014]",
				"ENSG00000139192	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139192	TAPBPL	TAP binding protein-like [Source:HGNC Symbol;Acc:30683]",
				"ENSG00000216490	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000216490	IFI30	interferon, gamma-inducible protein 30 [Source:HGNC Symbol;Acc:5398]",
				"ENSG00000198730	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198730	CTR9	Ctr9, Paf1/RNA polymerase II complex component, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:16850]",
				"ENSG00000124228	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124228	DDX27	DEAD (Asp-Glu-Ala-Asp) box polypeptide 27 [Source:HGNC Symbol;Acc:15837]",
				"ENSG00000162631	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162631	NTNG1	netrin G1 [Source:HGNC Symbol;Acc:23319]",
				"ENSG00000136451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136451	VEZF1	vascular endothelial zinc finger 1 [Source:HGNC Symbol;Acc:12949]",
				"ENSG00000144182	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144182	LIPT1	lipoyltransferase 1 [Source:HGNC Symbol;Acc:29569]",
				"ENSG00000096696	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000096696	DSP	desmoplakin [Source:HGNC Symbol;Acc:3052]",
				"ENSG00000163389	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163389	POGLUT1	protein O-glucosyltransferase 1 [Source:HGNC Symbol;Acc:22954]",
				"ENSG00000115459	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115459	ELMOD3	ELMO/CED-12 domain containing 3 [Source:HGNC Symbol;Acc:26158]",
				"ENSG00000152315	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152315	KCNK13	potassium channel, subfamily K, member 13 [Source:HGNC Symbol;Acc:6275]",
				"ENSG00000174946	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174946	GPR171	G protein-coupled receptor 171 [Source:HGNC Symbol;Acc:30057]",
				"ENSG00000198894	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198894	KIAA1737	KIAA1737 [Source:HGNC Symbol;Acc:20365]",
				"ENSG00000141646	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141646	SMAD4	SMAD family member 4 [Source:HGNC Symbol;Acc:6770]",
				"ENSG00000049883	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000049883	PTCD2	pentatricopeptide repeat domain 2 [Source:HGNC Symbol;Acc:25734]",
				"ENSG00000173848	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173848	NET1	neuroepithelial cell transforming 1 [Source:HGNC Symbol;Acc:14592]",
				"ENSG00000139908	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139908	TSSK4	testis-specific serine kinase 4 [Source:HGNC Symbol;Acc:19825]",
				"ENSG00000163686	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163686	ABHD6	abhydrolase domain containing 6 [Source:HGNC Symbol;Acc:21398]",
				"ENSG00000105202	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105202	FBL	fibrillarin [Source:HGNC Symbol;Acc:3599]",
				"ENSG00000071537	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071537	SEL1L	sel-1 suppressor of lin-12-like (C. elegans) [Source:HGNC Symbol;Acc:10717]",
				"ENSG00000165283	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165283	STOML2	stomatin (EPB72)-like 2 [Source:HGNC Symbol;Acc:14559]",
				"ENSG00000121900	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121900	TMEM54	transmembrane protein 54 [Source:HGNC Symbol;Acc:24143]",
				"ENSG00000172733	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172733	PURG	purine-rich element binding protein G [Source:HGNC Symbol;Acc:17930]",
				"ENSG00000168876	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168876	ANKRD49	ankyrin repeat domain 49 [Source:HGNC Symbol;Acc:25970]",
				"ENSG00000100441	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100441	KHNYN	KH and NYN domain containing [Source:HGNC Symbol;Acc:20166]",
				"ENSG00000106443	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106443	PHF14	PHD finger protein 14 [Source:HGNC Symbol;Acc:22203]",
				"ENSG00000121073	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121073	SLC35B1	solute carrier family 35, member B1 [Source:HGNC Symbol;Acc:20798]",
				"ENSG00000137776	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137776	SLTM	SAFB-like, transcription modulator [Source:HGNC Symbol;Acc:20709]",
				"ENSG00000111339	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111339	ART4	ADP-ribosyltransferase 4 (Dombrock blood group) [Source:HGNC Symbol;Acc:726]",
				"ENSG00000127946	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127946	HIP1	huntingtin interacting protein 1 [Source:HGNC Symbol;Acc:4913]",
				"ENSG00000131149	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131149	KIAA0182	KIAA0182 [Source:HGNC Symbol;Acc:28979]",
				"ENSG00000006625	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006625	GGCT	gamma-glutamylcyclotransferase [Source:HGNC Symbol;Acc:21705]",
				"ENSG00000170088	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170088	TMEM192	transmembrane protein 192 [Source:HGNC Symbol;Acc:26775]",
				"ENSG00000132464	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132464	ENAM	enamelin [Source:HGNC Symbol;Acc:3344]",
				"ENSG00000081051	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000081051	AFP	alpha-fetoprotein [Source:HGNC Symbol;Acc:317]",
				"ENSG00000176542	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176542	KIAA2018	KIAA2018 [Source:HGNC Symbol;Acc:30494]",
				"ENSG00000157353	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157353	FUK	fucokinase [Source:HGNC Symbol;Acc:29500]",
				"ENSG00000188895	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188895	MSL1	male-specific lethal 1 homolog (Drosophila) [Source:HGNC Symbol;Acc:27905]",
				"ENSG00000184434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184434	LRRC19	leucine rich repeat containing 19 [Source:HGNC Symbol;Acc:23379]",
				"ENSG00000107593	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107593	PKD2L1	polycystic kidney disease 2-like 1 [Source:HGNC Symbol;Acc:9011]",
				"ENSG00000106723	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106723	SPIN1	spindlin 1 [Source:HGNC Symbol;Acc:11243]",
				"ENSG00000153066	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153066	TXNDC11	thioredoxin domain containing 11 [Source:HGNC Symbol;Acc:28030]",
				"ENSG00000166341	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166341	DCHS1	dachsous 1 (Drosophila) [Source:HGNC Symbol;Acc:13681]",
				"ENSG00000120899	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120899	PTK2B	PTK2B protein tyrosine kinase 2 beta [Source:HGNC Symbol;Acc:9612]",
				"ENSG00000107201	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107201	DDX58	DEAD (Asp-Glu-Ala-Asp) box polypeptide 58 [Source:HGNC Symbol;Acc:19102]",
				"ENSG00000198799	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198799	LRIG2	leucine-rich repeats and immunoglobulin-like domains 2 [Source:HGNC Symbol;Acc:20889]",
				"ENSG00000182580	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182580	EPHB3	EPH receptor B3 [Source:HGNC Symbol;Acc:3394]",
				"ENSG00000112655	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112655	PTK7	PTK7 protein tyrosine kinase 7 [Source:HGNC Symbol;Acc:9618]",
				"ENSG00000080007	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080007	DDX43	DEAD (Asp-Glu-Ala-Asp) box polypeptide 43 [Source:HGNC Symbol;Acc:18677]",
				"ENSG00000160746	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160746	ANO10	anoctamin 10 [Source:HGNC Symbol;Acc:25519]",
				"ENSG00000138032	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138032	PPM1B	protein phosphatase, Mg2+/Mn2+ dependent, 1B [Source:HGNC Symbol;Acc:9276]",
				"ENSG00000127585	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127585	FBXL16	F-box and leucine-rich repeat protein 16 [Source:HGNC Symbol;Acc:14150]",
				"ENSG00000140543	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140543	DET1	de-etiolated homolog 1 (Arabidopsis) [Source:HGNC Symbol;Acc:25477]",
				"ENSG00000013619	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000013619	MAMLD1	mastermind-like domain containing 1 [Source:HGNC Symbol;Acc:2568]",
				"ENSG00000100890	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100890	RP11-173D9.3	Mitochondrial ribonuclease P protein 3  [Source:UniProtKB/Swiss-Prot;Acc:O15091]",
				"ENSG00000141378	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141378	PTRH2	peptidyl-tRNA hydrolase 2 [Source:HGNC Symbol;Acc:24265]",
				"ENSG00000122025	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122025	FLT3	fms-related tyrosine kinase 3 [Source:HGNC Symbol;Acc:3765]",
				"ENSG00000135299	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135299	ANKRD6	ankyrin repeat domain 6 [Source:HGNC Symbol;Acc:17280]",
				"ENSG00000176454	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176454	LPCAT4	lysophosphatidylcholine acyltransferase 4 [Source:HGNC Symbol;Acc:30059]",
				"ENSG00000111199	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111199	TRPV4	transient receptor potential cation channel, subfamily V, member 4 [Source:HGNC Symbol;Acc:18083]",
				"ENSG00000107036	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107036	KIAA1432	KIAA1432 [Source:HGNC Symbol;Acc:17686]",
				"ENSG00000125245	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125245	GPR18	G protein-coupled receptor 18 [Source:HGNC Symbol;Acc:4472]",
				"ENSG00000126562	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126562	WNK4	WNK lysine deficient protein kinase 4 [Source:HGNC Symbol;Acc:14544]",
				"ENSG00000136271	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136271	DDX56	DEAD (Asp-Glu-Ala-Asp) box helicase 56 [Source:HGNC Symbol;Acc:18193]",
				"ENSG00000174939	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174939	ASPHD1	aspartate beta-hydroxylase domain containing 1 [Source:HGNC Symbol;Acc:27380]",
				"ENSG00000103150	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103150	MLYCD	malonyl-CoA decarboxylase [Source:HGNC Symbol;Acc:7150]",
				"ENSG00000114395	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114395	CYB561D2	cytochrome b-561 domain containing 2 [Source:HGNC Symbol;Acc:30253]",
				"ENSG00000108523	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108523	RNF167	ring finger protein 167 [Source:HGNC Symbol;Acc:24544]",
				"ENSG00000108592	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108592	FTSJ3	FtsJ homolog 3 (E. coli) [Source:HGNC Symbol;Acc:17136]",
				"ENSG00000140451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140451	PIF1	PIF1 5prime-to-3prime DNA helicase homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:26220]",
				"ENSG00000048405	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000048405	ZNF800	zinc finger protein 800 [Source:HGNC Symbol;Acc:27267]",
				"ENSG00000126814	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126814	TRMT5	tRNA methyltransferase 5 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:23141]",
				"ENSG00000142731	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000142731	PLK4	polo-like kinase 4 [Source:HGNC Symbol;Acc:11397]",
				"ENSG00000179562	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179562	GCC1	GRIP and coiled-coil domain containing 1 [Source:HGNC Symbol;Acc:19095]",
				"ENSG00000074219	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000074219	TEAD2	TEA domain family member 2 [Source:HGNC Symbol;Acc:11715]",
				"ENSG00000144802	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144802	NFKBIZ	nuclear factor of kappa light polypeptide gene enhancer in B-cells inhibitor, zeta [Source:HGNC Symbol;Acc:29805]",
				"ENSG00000205669	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000205669	ACOT6	acyl-CoA thioesterase 6 [Source:HGNC Symbol;Acc:33159]",
				"ENSG00000163909	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163909	HEYL	hairy/enhancer-of-split related with YRPW motif-like [Source:HGNC Symbol;Acc:4882]",
				"ENSG00000019485	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000019485	PRDM11	PR domain containing 11 [Source:HGNC Symbol;Acc:13996]",
				"ENSG00000158014	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000158014	SLC30A2	solute carrier family 30 (zinc transporter), member 2 [Source:HGNC Symbol;Acc:11013]",
				"ENSG00000153292	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153292	GPR110	G protein-coupled receptor 110 [Source:HGNC Symbol;Acc:18990]",
				"ENSG00000028203	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000028203	VEZT	vezatin, adherens junctions transmembrane protein [Source:HGNC Symbol;Acc:18258]",
				"ENSG00000137507	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137507	LRRC32	leucine rich repeat containing 32 [Source:HGNC Symbol;Acc:4161]",
				"ENSG00000100842	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100842	EFS	embryonal Fyn-associated substrate [Source:HGNC Symbol;Acc:16898]",
				"ENSG00000198003	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198003	CCDC151	coiled-coil domain containing 151 [Source:HGNC Symbol;Acc:28303]",
				"ENSG00000134444	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134444	KIAA1468	KIAA1468 [Source:HGNC Symbol;Acc:29289]",
				"ENSG00000054938	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000054938	CHRDL2	chordin-like 2 [Source:HGNC Symbol;Acc:24168]",
				"ENSG00000152193	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152193	RNF219	ring finger protein 219 [Source:HGNC Symbol;Acc:20308]",
				"ENSG00000137500	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137500	CCDC90B	coiled-coil domain containing 90B [Source:HGNC Symbol;Acc:28108]",
				"ENSG00000167098	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167098	SUN5	Sad1 and UNC84 domain containing 5 [Source:HGNC Symbol;Acc:16252]",
				"ENSG00000156384	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156384	SFR1	SWI5-dependent recombination repair 1 [Source:HGNC Symbol;Acc:29574]",
				"ENSG00000037749	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000037749	MFAP3	microfibrillar-associated protein 3 [Source:HGNC Symbol;Acc:7034]",
				"ENSG00000138193	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138193	PLCE1	phospholipase C, epsilon 1 [Source:HGNC Symbol;Acc:17175]",
				"ENSG00000167984	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167984	NLRC3	NLR family, CARD domain containing 3 [Source:HGNC Symbol;Acc:29889]",
				"ENSG00000188822	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188822	CNR2	cannabinoid receptor 2 (macrophage) [Source:HGNC Symbol;Acc:2160]",
				"ENSG00000065526	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065526	SPEN	spen homolog, transcriptional regulator (Drosophila) [Source:HGNC Symbol;Acc:17575]",
				"ENSG00000089692	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089692	LAG3	lymphocyte-activation gene 3 [Source:HGNC Symbol;Acc:6476]",
				"ENSG00000108389	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108389	MTMR4	myotubularin related protein 4 [Source:HGNC Symbol;Acc:7452]",
				"ENSG00000120278	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120278	PLEKHG1	pleckstrin homology domain containing, family G (with RhoGef domain) member 1 [Source:HGNC Symbol;Acc:20884]",
				"ENSG00000121903	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121903	ZSCAN20	zinc finger and SCAN domain containing 20 [Source:HGNC Symbol;Acc:13093]",
				"ENSG00000115310	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115310	RTN4	reticulon 4 [Source:HGNC Symbol;Acc:14085]",
				"ENSG00000122641	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000122641	INHBA	inhibin, beta A [Source:HGNC Symbol;Acc:6066]",
				"ENSG00000103245	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103245	NARFL	nuclear prelamin A recognition factor-like [Source:HGNC Symbol;Acc:14179]",
				"ENSG00000112200	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112200	ZNF451	zinc finger protein 451 [Source:HGNC Symbol;Acc:21091]",
				"ENSG00000082269	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000082269	FAM135A	family with sequence similarity 135, member A [Source:HGNC Symbol;Acc:21084]",
				"ENSG00000185028	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185028	LRRC14B	leucine rich repeat containing 14B [Source:HGNC Symbol;Acc:37268]",
				"ENSG00000168701	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168701	TMEM208	transmembrane protein 208 [Source:HGNC Symbol;Acc:25015]",
				"ENSG00000005981	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005981	ASB4	ankyrin repeat and SOCS box containing 4 [Source:HGNC Symbol;Acc:16009]",
				"ENSG00000056558	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000056558	TRAF1	TNF receptor-associated factor 1 [Source:HGNC Symbol;Acc:12031]",
				"ENSG00000165621	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165621	OXGR1	oxoglutarate (alpha-ketoglutarate) receptor 1 [Source:HGNC Symbol;Acc:4531]",
				"ENSG00000169919	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169919	GUSB	glucuronidase, beta [Source:HGNC Symbol;Acc:4696]",
				"ENSG00000170476	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170476	RP11-1280I22.1	Plasma cell-induced resident endoplasmic reticulum protein  [Source:UniProtKB/Swiss-Prot;Acc:Q8WU39]",
				"ENSG00000213901	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213901	SLC23A3	solute carrier family 23 (nucleobase transporters), member 3 [Source:HGNC Symbol;Acc:20601]",
				"ENSG00000171016	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171016	PYGO1	pygopus homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:30256]",
				"ENSG00000154099	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154099	DNAAF1	dynein, axonemal, assembly factor 1 [Source:HGNC Symbol;Acc:30539]",
				"ENSG00000110429	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110429	FBXO3	F-box protein 3 [Source:HGNC Symbol;Acc:13582]",
				"ENSG00000198924	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198924	DCLRE1A	DNA cross-link repair 1A [Source:HGNC Symbol;Acc:17660]",
				"ENSG00000123342	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123342	MMP19	matrix metallopeptidase 19 [Source:HGNC Symbol;Acc:7165]",
				"ENSG00000129317	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129317	PUS7L	pseudouridylate synthase 7 homolog (S. cerevisiae)-like [Source:HGNC Symbol;Acc:25276]",
				"ENSG00000185722	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000185722	ANKFY1	ankyrin repeat and FYVE domain containing 1 [Source:HGNC Symbol;Acc:20763]",
				"ENSG00000132780	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132780	NASP	nuclear autoantigenic sperm protein (histone-binding) [Source:HGNC Symbol;Acc:7644]",
				"ENSG00000170477	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170477	KRT4	keratin 4 [Source:HGNC Symbol;Acc:6441]",
				"ENSG00000150403	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150403	TMCO3	transmembrane and coiled-coil domains 3 [Source:HGNC Symbol;Acc:20329]",
				"ENSG00000100968	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100968	NFATC4	nuclear factor of activated T-cells, cytoplasmic, calcineurin-dependent 4 [Source:HGNC Symbol;Acc:7778]",
				"ENSG00000040487	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000040487	PQLC2	PQ loop repeat containing 2 [Source:HGNC Symbol;Acc:26001]",
				"ENSG00000101349	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101349	PAK7	p21 protein (Cdc42/Rac)-activated kinase 7 [Source:HGNC Symbol;Acc:15916]",
				"ENSG00000115241	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115241	PPM1G	protein phosphatase, Mg2+/Mn2+ dependent, 1G [Source:HGNC Symbol;Acc:9278]",
				"ENSG00000173402	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173402	DAG1	dystroglycan 1 (dystrophin-associated glycoprotein 1) [Source:HGNC Symbol;Acc:2666]",
				"ENSG00000104783	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104783	KCNN4	potassium intermediate/small conductance calcium-activated channel, subfamily N, member 4 [Source:HGNC Symbol;Acc:6293]",
				"ENSG00000128536	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128536	CDHR3	cadherin-related family member 3 [Source:HGNC Symbol;Acc:26308]",
				"ENSG00000149182	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149182	ARFGAP2	ADP-ribosylation factor GTPase activating protein 2 [Source:HGNC Symbol;Acc:13504]",
				"ENSG00000165478	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165478	HEPACAM	hepatic and glial cell adhesion molecule [Source:HGNC Symbol;Acc:26361]",
				"ENSG00000065485	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000065485	PDIA5	protein disulfide isomerase family A, member 5 [Source:HGNC Symbol;Acc:24811]",
				"ENSG00000165887	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165887	ANKRD2	ankyrin repeat domain 2 (stretch responsive muscle) [Source:HGNC Symbol;Acc:495]",
				"ENSG00000104998	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104998	IL27RA	interleukin 27 receptor, alpha [Source:HGNC Symbol;Acc:17290]",
				"ENSG00000003137	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000003137	CYP26B1	cytochrome P450, family 26, subfamily B, polypeptide 1 [Source:HGNC Symbol;Acc:20581]",
				"ENSG00000101751	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101751	POLI	polymerase (DNA directed) iota [Source:HGNC Symbol;Acc:9182]",
				"ENSG00000109511	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109511	ANXA10	annexin A10 [Source:HGNC Symbol;Acc:534]",
				"ENSG00000144460	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144460	NYAP2	neuronal tyrosine-phosphorylated phosphoinositide-3-kinase adaptor 2 [Source:HGNC Symbol;Acc:29291]",
				"ENSG00000145388	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145388	METTL14	methyltransferase like 14 [Source:HGNC Symbol;Acc:29330]",
				"ENSG00000166592	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166592	RRAD	Ras-related associated with diabetes [Source:HGNC Symbol;Acc:10446]",
				"ENSG00000170871	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170871	KIAA0232	KIAA0232 [Source:HGNC Symbol;Acc:28992]",
				"ENSG00000132600	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132600	PRMT7	protein arginine methyltransferase 7 [Source:HGNC Symbol;Acc:25557]",
				"ENSG00000107140	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107140	TESK1	testis-specific kinase 1 [Source:HGNC Symbol;Acc:11731]",
				"ENSG00000198951	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198951	NAGA	N-acetylgalactosaminidase, alpha- [Source:HGNC Symbol;Acc:7631]",
				"ENSG00000162624	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162624	LHX8	LIM homeobox 8 [Source:HGNC Symbol;Acc:28838]",
				"ENSG00000176715	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000176715	ACSF3	acyl-CoA synthetase family member 3 [Source:HGNC Symbol;Acc:27288]",
				"ENSG00000099937	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000099937	SERPIND1	serpin peptidase inhibitor, clade D (heparin cofactor), member 1 [Source:HGNC Symbol;Acc:4838]",
				"ENSG00000108018	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108018	SORCS1	sortilin-related VPS10 domain containing receptor 1 [Source:HGNC Symbol;Acc:16697]",
				"ENSG00000170345	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170345	FOS	FBJ murine osteosarcoma viral oncogene homolog [Source:HGNC Symbol;Acc:3796]",
				"ENSG00000106392	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106392	C1GALT1	core 1 synthase, glycoprotein-N-acetylgalactosamine 3-beta-galactosyltransferase, 1 [Source:HGNC Symbol;Acc:24337]",
				"ENSG00000152785	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152785	BMP3	bone morphogenetic protein 3 [Source:HGNC Symbol;Acc:1070]",
				"ENSG00000139263	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139263	LRIG3	leucine-rich repeats and immunoglobulin-like domains 3 [Source:HGNC Symbol;Acc:30991]",
				"ENSG00000160007	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160007	ARHGAP35	Rho GTPase activating protein 35 [Source:HGNC Symbol;Acc:4591]",
				"ENSG00000165275	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165275	RG9MTD3	RNA (guanine-9-) methyltransferase domain containing 3 [Source:HGNC Symbol;Acc:26454]",
				"ENSG00000116539	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116539	ASH1L	ash1 (absent, small, or homeotic)-like (Drosophila) [Source:HGNC Symbol;Acc:19088]",
				"ENSG00000148702	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148702	HABP2	hyaluronan binding protein 2 [Source:HGNC Symbol;Acc:4798]",
				"ENSG00000107957	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107957	SH3PXD2A	SH3 and PX domains 2A [Source:HGNC Symbol;Acc:23664]",
				"ENSG00000117528	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117528	ABCD3	ATP-binding cassette, sub-family D (ALD), member 3 [Source:HGNC Symbol;Acc:67]",
				"ENSG00000175175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175175	PPM1E	protein phosphatase, Mg2+/Mn2+ dependent, 1E [Source:HGNC Symbol;Acc:19322]",
				"ENSG00000074356	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000074356	C17orf85	chromosome 17 open reading frame 85 [Source:HGNC Symbol;Acc:24612]",
				"ENSG00000086189	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000086189	DIMT1	DIM1 dimethyladenosine transferase 1 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:30217]",
				"ENSG00000124587	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124587	PEX6	peroxisomal biogenesis factor 6 [Source:HGNC Symbol;Acc:8859]",
				"ENSG00000166986	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166986	MARS	methionyl-tRNA synthetase [Source:HGNC Symbol;Acc:6898]",
				"ENSG00000128203	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128203	ASPHD2	aspartate beta-hydroxylase domain containing 2 [Source:HGNC Symbol;Acc:30437]",
				"ENSG00000128191	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128191	DGCR8	DiGeorge syndrome critical region gene 8 [Source:HGNC Symbol;Acc:2847]",
				"ENSG00000172053	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172053	QARS	glutaminyl-tRNA synthetase [Source:HGNC Symbol;Acc:9751]",
				"ENSG00000164124	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164124	TMEM144	transmembrane protein 144 [Source:HGNC Symbol;Acc:25633]",
				"ENSG00000103496	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103496	STX4	syntaxin 4 [Source:HGNC Symbol;Acc:11439]",
				"ENSG00000130713	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130713	EXOSC2	exosome component 2 [Source:HGNC Symbol;Acc:17097]",
				"ENSG00000196584	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196584	XRCC2	X-ray repair complementing defective repair in Chinese hamster cells 2 [Source:HGNC Symbol;Acc:12829]",
				"ENSG00000178522	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178522	AMBN	ameloblastin (enamel matrix protein) [Source:HGNC Symbol;Acc:452]",
				"ENSG00000180096	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180096	35673	septin 1 [Source:HGNC Symbol;Acc:2879]",
				"ENSG00000074657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000074657	ZNF532	zinc finger protein 532 [Source:HGNC Symbol;Acc:30940]",
				"ENSG00000113758	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113758	DBN1	drebrin 1 [Source:HGNC Symbol;Acc:2695]",
				"ENSG00000164070	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164070	HSPA4L	heat shock 70kDa protein 4-like [Source:HGNC Symbol;Acc:17041]",
				"ENSG00000163378	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163378	EOGT	EGF domain-specific O-linked N-acetylglucosamine (GlcNAc) transferase [Source:HGNC Symbol;Acc:28526]",
				"ENSG00000162517	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162517	PEF1	penta-EF-hand domain containing 1 [Source:HGNC Symbol;Acc:30009]",
				"ENSG00000116786	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116786	PLEKHM2	pleckstrin homology domain containing, family M (with RUN domain) member 2 [Source:HGNC Symbol;Acc:29131]",
				"ENSG00000134954	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134954	ETS1	v-ets erythroblastosis virus E26 oncogene homolog 1 (avian) [Source:HGNC Symbol;Acc:3488]",
				"ENSG00000074935	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000074935	TUBE1	tubulin, epsilon 1 [Source:HGNC Symbol;Acc:20775]",
				"ENSG00000100601	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100601	ALKBH1	alkB, alkylation repair homolog 1 (E. coli) [Source:HGNC Symbol;Acc:17911]",
				"ENSG00000103111	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103111	MON1B	MON1 homolog B (yeast) [Source:HGNC Symbol;Acc:25020]",
				"ENSG00000071462	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071462	WBSCR22	Williams Beuren syndrome chromosome region 22 [Source:HGNC Symbol;Acc:16405]",
				"ENSG00000147432	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147432	CHRNB3	cholinergic receptor, nicotinic, beta 3 (neuronal) [Source:HGNC Symbol;Acc:1963]",
				"ENSG00000147471	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000147471	PROSC	proline synthetase co-transcribed homolog (bacterial) [Source:HGNC Symbol;Acc:9457]",
				"ENSG00000113790	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113790	EHHADH	enoyl-CoA, hydratase/3-hydroxyacyl CoA dehydrogenase [Source:HGNC Symbol;Acc:3247]",
				"ENSG00000159167	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159167	STC1	stanniocalcin 1 [Source:HGNC Symbol;Acc:11373]",
				"ENSG00000103160	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103160	HSDL1	hydroxysteroid dehydrogenase like 1 [Source:HGNC Symbol;Acc:16475]",
				"ENSG00000006194	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006194	ZNF263	zinc finger protein 263 [Source:HGNC Symbol;Acc:13056]",
				"ENSG00000164342	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164342	TLR3	toll-like receptor 3 [Source:HGNC Symbol;Acc:11849]",
				"ENSG00000174684	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174684	B3GNT1	UDP-GlcNAc:betaGal beta-1,3-N-acetylglucosaminyltransferase 1 [Source:HGNC Symbol;Acc:15685]",
				"ENSG00000180537	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000180537	RNF182	ring finger protein 182 [Source:HGNC Symbol;Acc:28522]",
				"ENSG00000138375	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138375	SMARCAL1	SWI/SNF related, matrix associated, actin dependent regulator of chromatin, subfamily a-like 1 [Source:HGNC Symbol;Acc:11102]",
				"ENSG00000186767	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186767	SPIN4	spindlin family, member 4 [Source:HGNC Symbol;Acc:27040]",
				"ENSG00000164830	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164830	OXR1	oxidation resistance 1 [Source:HGNC Symbol;Acc:15822]",
				"ENSG00000088298	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000088298	EDEM2	ER degradation enhancer, mannosidase alpha-like 2 [Source:HGNC Symbol;Acc:15877]",
				"ENSG00000071655	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000071655	MBD3	methyl-CpG binding domain protein 3 [Source:HGNC Symbol;Acc:6918]",
				"ENSG00000167513	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167513	CDT1	chromatin licensing and DNA replication factor 1 [Source:HGNC Symbol;Acc:24576]",
				"ENSG00000124713	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124713	GNMT	glycine N-methyltransferase [Source:HGNC Symbol;Acc:4415]",
				"ENSG00000138442	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138442	WDR12	WD repeat domain 12 [Source:HGNC Symbol;Acc:14098]",
				"ENSG00000137343	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137343	ATAT1	alpha tubulin acetyltransferase 1 [Source:HGNC Symbol;Acc:21186]",
				"ENSG00000104213	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104213	PDGFRL	platelet-derived growth factor receptor-like [Source:HGNC Symbol;Acc:8805]",
				"ENSG00000171657	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171657	GPR82	G protein-coupled receptor 82 [Source:HGNC Symbol;Acc:4533]",
				"ENSG00000007545	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000007545	CRAMP1L	Crm, cramped-like (Drosophila) [Source:HGNC Symbol;Acc:14122]",
				"ENSG00000164331	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164331	ANKRA2	ankyrin repeat, family A (RFXANK-like), 2 [Source:HGNC Symbol;Acc:13208]",
				"ENSG00000130939	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130939	UBE4B	ubiquitination factor E4B [Source:HGNC Symbol;Acc:12500]",
				"ENSG00000127954	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127954	STEAP4	STEAP family member 4 [Source:HGNC Symbol;Acc:21923]",
				"ENSG00000197324	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197324	LRP10	low density lipoprotein receptor-related protein 10 [Source:HGNC Symbol;Acc:14553]",
				"ENSG00000166349	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166349	RAG1	recombination activating gene 1 [Source:HGNC Symbol;Acc:9831]",
				"ENSG00000167107	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167107	ACSF2	acyl-CoA synthetase family member 2 [Source:HGNC Symbol;Acc:26101]",
				"ENSG00000069206	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069206	ADAM7	ADAM metallopeptidase domain 7 [Source:HGNC Symbol;Acc:214]",
				"ENSG00000116525	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116525	TRIM62	tripartite motif containing 62 [Source:HGNC Symbol;Acc:25574]",
				"ENSG00000198060	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198060	36950	membrane-associated ring finger (C3HC4) 5 [Source:HGNC Symbol;Acc:26025]",
				"ENSG00000111252	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111252	SH2B3	SH2B adaptor protein 3 [Source:HGNC Symbol;Acc:29605]",
				"ENSG00000008118	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000008118	CAMK1G	calcium/calmodulin-dependent protein kinase IG [Source:HGNC Symbol;Acc:14585]",
				"ENSG00000143970	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143970	ASXL2	additional sex combs like 2 (Drosophila) [Source:HGNC Symbol;Acc:23805]",
				"ENSG00000249853	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000249853	HS3ST5	heparan sulfate (glucosamine) 3-O-sulfotransferase 5 [Source:HGNC Symbol;Acc:19419]",
				"ENSG00000165813	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165813	C10orf118	chromosome 10 open reading frame 118 [Source:HGNC Symbol;Acc:24349]",
				"ENSG00000182670	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182670	TTC3	tetratricopeptide repeat domain 3 [Source:HGNC Symbol;Acc:12393]",
				"ENSG00000070214	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000070214	SLC44A1	solute carrier family 44, member 1 [Source:HGNC Symbol;Acc:18798]",
				"ENSG00000103423	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103423	DNAJA3	DnaJ (Hsp40) homolog, subfamily A, member 3 [Source:HGNC Symbol;Acc:11808]",
				"ENSG00000005844	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005844	ITGAL	integrin, alpha L (antigen CD11A (p180), lymphocyte function-associated antigen 1; alpha polypeptide) [Source:HGNC Symbol;Acc:6148]",
				"ENSG00000102794	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102794	IRG1	immunoresponsive 1 homolog (mouse) [Source:HGNC Symbol;Acc:33904]",
				"ENSG00000112406	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112406	HECA	headcase homolog (Drosophila) [Source:HGNC Symbol;Acc:21041]",
				"ENSG00000151704	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151704	KCNJ1	potassium inwardly-rectifying channel, subfamily J, member 1 [Source:HGNC Symbol;Acc:6255]",
				"ENSG00000034533	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000034533	ASTE1	asteroid homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:25021]",
				"ENSG00000165934	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165934	CPSF2	cleavage and polyadenylation specific factor 2, 100kDa [Source:HGNC Symbol;Acc:2325]",
				"ENSG00000057593	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000057593	F7	coagulation factor VII (serum prothrombin conversion accelerator) [Source:HGNC Symbol;Acc:3544]",
				"ENSG00000178691	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178691	SUZ12	suppressor of zeste 12 homolog (Drosophila) [Source:HGNC Symbol;Acc:17101]",
				"ENSG00000111490	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111490	TBC1D30	TBC1 domain family, member 30 [Source:HGNC Symbol;Acc:29164]",
				"ENSG00000100350	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100350	FOXRED2	FAD-dependent oxidoreductase domain containing 2 [Source:HGNC Symbol;Acc:26264]",
				"ENSG00000152359	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000152359	POC5	POC5 centriolar protein homolog (Chlamydomonas) [Source:HGNC Symbol;Acc:26658]",
				"ENSG00000183508	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183508	FAM46C	family with sequence similarity 46, member C [Source:HGNC Symbol;Acc:24712]",
				"ENSG00000135387	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135387	CAPRIN1	cell cycle associated protein 1 [Source:HGNC Symbol;Acc:6743]",
				"ENSG00000136891	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136891	TEX10	testis expressed 10 [Source:HGNC Symbol;Acc:25988]",
				"ENSG00000100024	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100024	UPB1	ureidopropionase, beta [Source:HGNC Symbol;Acc:16297]",
				"ENSG00000037474	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000037474	NSUN2	NOP2/Sun RNA methyltransferase family, member 2 [Source:HGNC Symbol;Acc:25994]",
				"ENSG00000092010	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000092010	PSME1	proteasome (prosome, macropain) activator subunit 1 (PA28 alpha) [Source:HGNC Symbol;Acc:9568]",
				"ENSG00000175166	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175166	PSMD2	proteasome (prosome, macropain) 26S subunit, non-ATPase, 2 [Source:HGNC Symbol;Acc:9559]",
				"ENSG00000169155	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169155	ZBTB43	zinc finger and BTB domain containing 43 [Source:HGNC Symbol;Acc:17908]",
				"ENSG00000100084	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100084	HIRA	HIR histone cell cycle regulation defective homolog A (S. cerevisiae) [Source:HGNC Symbol;Acc:4916]",
				"ENSG00000143368	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143368	SF3B4	splicing factor 3b, subunit 4, 49kDa [Source:HGNC Symbol;Acc:10771]",
				"ENSG00000134697	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134697	GNL2	guanine nucleotide binding protein-like 2 (nucleolar) [Source:HGNC Symbol;Acc:29925]",
				"ENSG00000113595	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113595	TRIM23	tripartite motif containing 23 [Source:HGNC Symbol;Acc:660]",
				"ENSG00000118689	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118689	FOXO3	forkhead box O3 [Source:HGNC Symbol;Acc:3821]",
				"ENSG00000133961	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133961	NUMB	numb homolog (Drosophila) [Source:HGNC Symbol;Acc:8060]",
				"ENSG00000144476	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144476	CXCR7	chemokine (C-X-C motif) receptor 7 [Source:HGNC Symbol;Acc:23692]",
				"ENSG00000113812	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113812	ACTR8	ARP8 actin-related protein 8 homolog (yeast) [Source:HGNC Symbol;Acc:14672]",
				"ENSG00000162639	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162639	HENMT1	HEN1 methyltransferase homolog 1 (Arabidopsis) [Source:HGNC Symbol;Acc:26400]",
				"ENSG00000187555	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187555	USP7	ubiquitin specific peptidase 7 (herpes virus-associated) [Source:HGNC Symbol;Acc:12630]",
				"ENSG00000087263	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000087263	OGFOD1	2-oxoglutarate and iron-dependent oxygenase domain containing 1 [Source:HGNC Symbol;Acc:25585]",
				"ENSG00000183751	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183751	TBL3	transducin (beta)-like 3 [Source:HGNC Symbol;Acc:11587]",
				"ENSG00000126775	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126775	ATG14	autophagy related 14 [Source:HGNC Symbol;Acc:19962]",
				"ENSG00000025796	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000025796	SEC63	SEC63 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:21082]",
				"ENSG00000117751	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117751	PPP1R8	protein phosphatase 1, regulatory subunit 8 [Source:HGNC Symbol;Acc:9296]",
				"ENSG00000168264	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168264	IRF2BP2	interferon regulatory factor 2 binding protein 2 [Source:HGNC Symbol;Acc:21729]",
				"ENSG00000118432	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000118432	CNR1	cannabinoid receptor 1 (brain) [Source:HGNC Symbol;Acc:2159]",
				"ENSG00000166451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166451	CENPN	centromere protein N [Source:HGNC Symbol;Acc:30873]",
				"ENSG00000105695	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105695	MAG	myelin associated glycoprotein [Source:HGNC Symbol;Acc:6783]",
				"ENSG00000106483	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106483	SFRP4	secreted frizzled-related protein 4 [Source:HGNC Symbol;Acc:10778]",
				"ENSG00000198663	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198663	C6orf89	chromosome 6 open reading frame 89 [Source:HGNC Symbol;Acc:21114]",
				"ENSG00000184374	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184374	COLEC10	collectin sub-family member 10 (C-type lectin) [Source:HGNC Symbol;Acc:2220]",
				"ENSG00000160396	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160396	HIPK4	homeodomain interacting protein kinase 4 [Source:HGNC Symbol;Acc:19007]",
				"ENSG00000165966	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165966	PDZRN4	PDZ domain containing ring finger 4 [Source:HGNC Symbol;Acc:30552]",
				"ENSG00000141744	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141744	PNMT	phenylethanolamine N-methyltransferase [Source:HGNC Symbol;Acc:9160]",
				"ENSG00000105699	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105699	LSR	lipolysis stimulated lipoprotein receptor [Source:HGNC Symbol;Acc:29572]",
				"ENSG00000164463	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164463	CREBRF	CREB3 regulatory factor [Source:HGNC Symbol;Acc:24050]",
				"ENSG00000121486	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121486	TRMT1L	TRM1 tRNA methyltransferase 1-like [Source:HGNC Symbol;Acc:16782]",
				"ENSG00000140299	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140299	BNIP2	BCL2/adenovirus E1B 19kDa interacting protein 2 [Source:HGNC Symbol;Acc:1083]",
				"ENSG00000114573	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000114573	ATP6V1A	ATPase, H+ transporting, lysosomal 70kDa, V1 subunit A [Source:HGNC Symbol;Acc:851]",
				"ENSG00000169313	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169313	P2RY12	purinergic receptor P2Y, G-protein coupled, 12 [Source:HGNC Symbol;Acc:18124]",
				"ENSG00000138796	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138796	HADH	hydroxyacyl-CoA dehydrogenase [Source:HGNC Symbol;Acc:4799]",
				"ENSG00000138780	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138780	GSTCD	glutathione S-transferase, C-terminal domain containing [Source:HGNC Symbol;Acc:25806]",
				"ENSG00000093009	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000093009	CDC45	cell division cycle 45 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1739]",
				"ENSG00000108262	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108262	GIT1	G protein-coupled receptor kinase interacting ArfGAP 1 [Source:HGNC Symbol;Acc:4272]",
				"ENSG00000198356	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198356	ASNA1	arsA arsenite transporter, ATP-binding, homolog 1 (bacterial) [Source:HGNC Symbol;Acc:752]",
				"ENSG00000172500	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172500	FIBP	fibroblast growth factor (acidic) intracellular binding protein [Source:HGNC Symbol;Acc:3705]",
				"ENSG00000105270	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105270	CLIP3	CAP-GLY domain containing linker protein 3 [Source:HGNC Symbol;Acc:24314]",
				"ENSG00000161896	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000161896	IP6K3	inositol hexakisphosphate kinase 3 [Source:HGNC Symbol;Acc:17269]",
				"ENSG00000112818	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112818	MEP1A	meprin A, alpha (PABA peptide hydrolase) [Source:HGNC Symbol;Acc:7015]",
				"ENSG00000103249	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103249	CLCN7	chloride channel, voltage-sensitive 7 [Source:HGNC Symbol;Acc:2025]",
				"ENSG00000197879	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000197879	MYO1C	myosin IC [Source:HGNC Symbol;Acc:7597]",
				"ENSG00000141098	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141098	GFOD2	glucose-fructose oxidoreductase domain containing 2 [Source:HGNC Symbol;Acc:28159]",
				"ENSG00000111424	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111424	VDR	vitamin D (1,25- dihydroxyvitamin D3) receptor [Source:HGNC Symbol;Acc:12679]",
				"ENSG00000112033	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112033	PPARD	peroxisome proliferator-activated receptor delta [Source:HGNC Symbol;Acc:9235]",
				"ENSG00000089234	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000089234	BRAP	BRCA1 associated protein [Source:HGNC Symbol;Acc:1099]",
				"ENSG00000153786	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153786	ZDHHC7	zinc finger, DHHC-type containing 7 [Source:HGNC Symbol;Acc:18459]",
				"ENSG00000136811	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136811	ODF2	outer dense fiber of sperm tails 2 [Source:HGNC Symbol;Acc:8114]",
				"ENSG00000137801	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137801	THBS1	thrombospondin 1 [Source:HGNC Symbol;Acc:11785]",
				"ENSG00000100612	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100612	DHRS7	dehydrogenase/reductase (SDR family) member 7 [Source:HGNC Symbol;Acc:21524]",
				"ENSG00000131143	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131143	COX4I1	cytochrome c oxidase subunit IV isoform 1 [Source:HGNC Symbol;Acc:2265]",
				"ENSG00000116793	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116793	PHTF1	putative homeodomain transcription factor 1 [Source:HGNC Symbol;Acc:8939]",
				"ENSG00000135999	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135999	EPC2	enhancer of polycomb homolog 2 (Drosophila) [Source:HGNC Symbol;Acc:24543]",
				"ENSG00000164252	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164252	AGGF1	angiogenic factor with G patch and FHA domains 1 [Source:HGNC Symbol;Acc:24684]",
				"ENSG00000135373	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135373	EHF	ets homologous factor [Source:HGNC Symbol;Acc:3246]",
				"ENSG00000127526	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127526	SLC35E1	solute carrier family 35, member E1 [Source:HGNC Symbol;Acc:20803]",
				"ENSG00000213420	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000213420	GPC2	glypican 2 [Source:HGNC Symbol;Acc:4450]",
				"ENSG00000153822	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153822	KCNJ16	potassium inwardly-rectifying channel, subfamily J, member 16 [Source:HGNC Symbol;Acc:6262]",
				"ENSG00000137413	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137413	TAF8	TAF8 RNA polymerase II, TATA box binding protein (TBP)-associated factor, 43kDa [Source:HGNC Symbol;Acc:17300]",
				"ENSG00000116574	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116574	RHOU	ras homolog family member U [Source:HGNC Symbol;Acc:17794]",
				"ENSG00000162139	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162139	NEU3	sialidase 3 (membrane sialidase) [Source:HGNC Symbol;Acc:7760]",
				"ENSG00000138030	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138030	KHK	ketohexokinase (fructokinase) [Source:HGNC Symbol;Acc:6315]",
				"ENSG00000060749	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000060749	QSER1	glutamine and serine rich 1 [Source:HGNC Symbol;Acc:26154]",
				"ENSG00000068745	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068745	IP6K2	inositol hexakisphosphate kinase 2 [Source:HGNC Symbol;Acc:17313]",
				"ENSG00000128849	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128849	CGNL1	cingulin-like 1 [Source:HGNC Symbol;Acc:25931]",
				"ENSG00000095906	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095906	NUBP2	nucleotide binding protein 2 [Source:HGNC Symbol;Acc:8042]",
				"ENSG00000136877	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136877	FPGS	folylpolyglutamate synthase [Source:HGNC Symbol;Acc:3824]",
				"ENSG00000134030	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134030	CTIF	CBP80/20-dependent translation initiation factor [Source:HGNC Symbol;Acc:23925]",
				"ENSG00000179021	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000179021	C3orf38	chromosome 3 open reading frame 38 [Source:HGNC Symbol;Acc:28384]",
				"ENSG00000083896	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000083896	YTHDC1	YTH domain containing 1 [Source:HGNC Symbol;Acc:30626]",
				"ENSG00000188266	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188266	AGPHD1	aminoglycoside phosphotransferase domain containing 1 [Source:HGNC Symbol;Acc:34403]",
				"ENSG00000141434	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141434	MEP1B	meprin A, beta [Source:HGNC Symbol;Acc:7020]",
				"ENSG00000182541	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182541	LIMK2	LIM domain kinase 2 [Source:HGNC Symbol;Acc:6614]",
				"ENSG00000173227	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173227	SYT12	synaptotagmin XII [Source:HGNC Symbol;Acc:18381]",
				"ENSG00000181472	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181472	ZBTB2	zinc finger and BTB domain containing 2 [Source:HGNC Symbol;Acc:20868]",
				"ENSG00000123992	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123992	DNPEP	aspartyl aminopeptidase [Source:HGNC Symbol;Acc:2981]",
				"ENSG00000139218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139218	SCAF11	SR-related CTD-associated factor 11 [Source:HGNC Symbol;Acc:10784]",
				"ENSG00000103415	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103415	HMOX2	heme oxygenase (decycling) 2 [Source:HGNC Symbol;Acc:5014]",
				"ENSG00000137094	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137094	DNAJB5	DnaJ (Hsp40) homolog, subfamily B, member 5 [Source:HGNC Symbol;Acc:14887]",
				"ENSG00000165916	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165916	PSMC3	proteasome (prosome, macropain) 26S subunit, ATPase, 3 [Source:HGNC Symbol;Acc:9549]",
				"ENSG00000105671	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105671	DDX49	DEAD (Asp-Glu-Ala-Asp) box polypeptide 49 [Source:HGNC Symbol;Acc:18684]",
				"ENSG00000168685	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168685	IL7R	interleukin 7 receptor [Source:HGNC Symbol;Acc:6024]",
				"ENSG00000167130	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167130	DOLPP1	dolichyl pyrophosphate phosphatase 1 [Source:HGNC Symbol;Acc:29565]",
				"ENSG00000105647	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105647	PIK3R2	phosphoinositide-3-kinase, regulatory subunit 2 (beta) [Source:HGNC Symbol;Acc:8980]",
				"ENSG00000183579	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000183579	ZNRF3	zinc and ring finger 3 [Source:HGNC Symbol;Acc:18126]",
				"ENSG00000127578	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127578	WFIKKN1	WAP, follistatin/kazal, immunoglobulin, kunitz and netrin domain containing 1 [Source:HGNC Symbol;Acc:30912]",
				"ENSG00000172531	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000172531	PPP1CA	protein phosphatase 1, catalytic subunit, alpha isozyme [Source:HGNC Symbol;Acc:9281]",
				"ENSG00000188997	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188997	KCTD21	potassium channel tetramerisation domain containing 21 [Source:HGNC Symbol;Acc:27452]",
				"ENSG00000169989	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169989	TIGD4	tigger transposable element derived 4 [Source:HGNC Symbol;Acc:18335]",
				"ENSG00000162069	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162069	CCDC64B	coiled-coil domain containing 64B [Source:HGNC Symbol;Acc:33584]",
				"ENSG00000127580	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127580	WDR24	WD repeat domain 24 [Source:HGNC Symbol;Acc:20852]",
				"ENSG00000128886	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128886	ELL3	elongation factor RNA polymerase II-like 3 [Source:HGNC Symbol;Acc:23113]",
				"ENSG00000149639	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149639	SOGA1	suppressor of glucose, autophagy associated 1 [Source:HGNC Symbol;Acc:16111]",
				"ENSG00000170190	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170190	SLC16A5	solute carrier family 16, member 5 (monocarboxylic acid transporter 6) [Source:HGNC Symbol;Acc:10926]",
				"ENSG00000182263	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182263	FIGN	fidgetin [Source:HGNC Symbol;Acc:13285]",
				"ENSG00000104047	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104047	DTWD1	DTW domain containing 1 [Source:HGNC Symbol;Acc:30926]",
				"ENSG00000103266	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000103266	STUB1	STIP1 homology and U-box containing protein 1, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:11427]",
				"ENSG00000137843	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137843	PAK6	p21 protein (Cdc42/Rac)-activated kinase 6 [Source:HGNC Symbol;Acc:16061]",
				"ENSG00000107372	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000107372	ZFAND5	zinc finger, AN1-type domain 5 [Source:HGNC Symbol;Acc:13008]",
				"ENSG00000144554	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144554	FANCD2	Fanconi anemia, complementation group D2 [Source:HGNC Symbol;Acc:3585]",
				"ENSG00000171456	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000171456	ASXL1	additional sex combs like 1 (Drosophila) [Source:HGNC Symbol;Acc:18318]",
				"ENSG00000090534	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000090534	THPO	thrombopoietin [Source:HGNC Symbol;Acc:11795]",
				"ENSG00000135336	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135336	ORC3	origin recognition complex, subunit 3 [Source:HGNC Symbol;Acc:8489]",
				"ENSG00000069712	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000069712	KIAA1107	KIAA1107 [Source:HGNC Symbol;Acc:29192]",
				"ENSG00000178971	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000178971	CTC1	CTS telomere maintenance complex component 1 [Source:HGNC Symbol;Acc:26169]",
				"ENSG00000117400	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117400	MPL	myeloproliferative leukemia virus oncogene [Source:HGNC Symbol;Acc:7217]",
				"ENSG00000129351	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129351	ILF3	interleukin enhancer binding factor 3, 90kDa [Source:HGNC Symbol;Acc:6038]",
				"ENSG00000119844	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119844	AFTPH	aftiphilin [Source:HGNC Symbol;Acc:25951]",
				"ENSG00000136169	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136169	SETDB2	SET domain, bifurcated 2 [Source:HGNC Symbol;Acc:20263]",
				"ENSG00000040275	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000040275	CCDC99	coiled-coil domain containing 99 [Source:HGNC Symbol;Acc:26010]",
				"ENSG00000186638	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186638	KIF24	kinesin family member 24 [Source:HGNC Symbol;Acc:19916]",
				"ENSG00000127481	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127481	UBR4	ubiquitin protein ligase E3 component n-recognin 4 [Source:HGNC Symbol;Acc:30313]",
				"ENSG00000129810	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129810	SGOL1	shugoshin-like 1 (S. pombe) [Source:HGNC Symbol;Acc:25088]",
				"ENSG00000080345	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080345	RIF1	RAP1 interacting factor homolog (yeast) [Source:HGNC Symbol;Acc:23207]",
				"ENSG00000036549	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000036549	ZZZ3	zinc finger, ZZ-type containing 3 [Source:HGNC Symbol;Acc:24523]",
				"ENSG00000101773	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101773	RBBP8	retinoblastoma binding protein 8 [Source:HGNC Symbol;Acc:9891]",
				"ENSG00000166261	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166261	ZNF202	zinc finger protein 202 [Source:HGNC Symbol;Acc:12994]",
				"ENSG00000150712	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000150712	MTMR12	myotubularin related protein 12 [Source:HGNC Symbol;Acc:18191]",
				"ENSG00000091972	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000091972	CD200	CD200 molecule [Source:HGNC Symbol;Acc:7203]",
				"ENSG00000116212	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116212	LRRC42	leucine rich repeat containing 42 [Source:HGNC Symbol;Acc:28792]",
				"ENSG00000102393	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000102393	GLA	galactosidase, alpha [Source:HGNC Symbol;Acc:4296]",
				"ENSG00000010256	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000010256	UQCRC1	ubiquinol-cytochrome c reductase core protein I [Source:HGNC Symbol;Acc:12585]",
				"ENSG00000166444	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166444	ST5	suppression of tumorigenicity 5 [Source:HGNC Symbol;Acc:11350]",
				"ENSG00000145882	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000145882	PCYOX1L	prenylcysteine oxidase 1 like [Source:HGNC Symbol;Acc:28477]",
				"ENSG00000137106	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137106	GRHPR	glyoxylate reductase/hydroxypyruvate reductase [Source:HGNC Symbol;Acc:4570]",
				"ENSG00000164011	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164011	ZNF691	zinc finger protein 691 [Source:HGNC Symbol;Acc:28028]",
				"ENSG00000204138	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000204138	PHACTR4	phosphatase and actin regulator 4 [Source:HGNC Symbol;Acc:25793]",
				"ENSG00000188177	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188177	ZC3H6	zinc finger CCCH-type containing 6 [Source:HGNC Symbol;Acc:24762]",
				"ENSG00000095585	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000095585	BLNK	B-cell linker [Source:HGNC Symbol;Acc:14211]",
				"ENSG00000051009	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000051009	FAM160A2	family with sequence similarity 160, member A2 [Source:HGNC Symbol;Acc:25378]",
				"ENSG00000166343	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166343	MSS51	MSS51 mitochondrial translational activator homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:21000]",
				"ENSG00000167720	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167720	SRR	serine racemase [Source:HGNC Symbol;Acc:14398]",
				"ENSG00000079337	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000079337	RAPGEF3	Rap guanine nucleotide exchange factor (GEF) 3 [Source:HGNC Symbol;Acc:16629]",
				"ENSG00000064933	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000064933	PMS1	PMS1 postmeiotic segregation increased 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:9121]",
				"ENSG00000121064	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121064	SCPEP1	serine carboxypeptidase 1 [Source:HGNC Symbol;Acc:29507]",
				"ENSG00000117713	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117713	ARID1A	AT rich interactive domain 1A (SWI-like) [Source:HGNC Symbol;Acc:11110]",
				"ENSG00000137942	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000137942	FNBP1L	formin binding protein 1-like [Source:HGNC Symbol;Acc:20851]",
				"ENSG00000144468	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000144468	RHBDD1	rhomboid domain containing 1 [Source:HGNC Symbol;Acc:23081]",
				"ENSG00000170571	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170571	EMB	embigin [Source:HGNC Symbol;Acc:30465]",
				"ENSG00000130768	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130768	SMPDL3B	sphingomyelin phosphodiesterase, acid-like 3B [Source:HGNC Symbol;Acc:21416]",
				"ENSG00000025156	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000025156	HSF2	heat shock transcription factor 2 [Source:HGNC Symbol;Acc:5225]",
				"ENSG00000188786	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188786	MTF1	metal-regulatory transcription factor 1 [Source:HGNC Symbol;Acc:7428]",
				"ENSG00000138399	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138399	FASTKD1	FAST kinase domains 1 [Source:HGNC Symbol;Acc:26150]",
				"ENSG00000132872	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132872	SYT4	synaptotagmin IV [Source:HGNC Symbol;Acc:11512]",
				"ENSG00000116205	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000116205	TCEANC2	transcription elongation factor A (SII) N-terminal and central domain containing 2 [Source:HGNC Symbol;Acc:26494]",
				"ENSG00000165819	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165819	METTL3	methyltransferase like 3 [Source:HGNC Symbol;Acc:17563]",
				"ENSG00000146281	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000146281	PM20D2	peptidase M20 domain containing 2 [Source:HGNC Symbol;Acc:21408]",
				"ENSG00000132424	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132424	PNISR	PNN-interacting serine/arginine-rich protein [Source:HGNC Symbol;Acc:21222]",
				"ENSG00000132874	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000132874	SLC14A2	solute carrier family 14 (urea transporter), member 2 [Source:HGNC Symbol;Acc:10919]",
				"ENSG00000196323	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196323	ZBTB44	zinc finger and BTB domain containing 44 [Source:HGNC Symbol;Acc:25001]",
				"ENSG00000143756	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143756	FBXO28	F-box protein 28 [Source:HGNC Symbol;Acc:29046]",
				"ENSG00000135750	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135750	KCNK1	potassium channel, subfamily K, member 1 [Source:HGNC Symbol;Acc:6272]",
				"ENSG00000129255	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129255	MPDU1	mannose-P-dolichol utilization defect 1 [Source:HGNC Symbol;Acc:7207]",
				"ENSG00000006704	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006704	GTF2IRD1	GTF2I repeat domain containing 1 [Source:HGNC Symbol;Acc:4661]",
				"ENSG00000153823	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153823	PID1	phosphotyrosine interaction domain containing 1 [Source:HGNC Symbol;Acc:26084]",
				"ENSG00000109572	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109572	CLCN3	chloride channel, voltage-sensitive 3 [Source:HGNC Symbol;Acc:2021]",
				"ENSG00000160410	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000160410	SHKBP1	SH3KBP1 binding protein 1 [Source:HGNC Symbol;Acc:19214]",
				"ENSG00000141446	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000141446	ESCO1	establishment of cohesion 1 homolog 1 (S. cerevisiae) [Source:HGNC Symbol;Acc:24645]",
				"ENSG00000159708	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159708	LRRC36	leucine rich repeat containing 36 [Source:HGNC Symbol;Acc:25615]",
				"ENSG00000135451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135451	TROAP	trophinin associated protein (tastin) [Source:HGNC Symbol;Acc:12327]",
				"ENSG00000165805	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000165805	C12orf50	chromosome 12 open reading frame 50 [Source:HGNC Symbol;Acc:26665]",
				"ENSG00000112658	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000112658	SRF	serum response factor (c-fos serum response element-binding transcription factor) [Source:HGNC Symbol;Acc:11291]",
				"ENSG00000156535	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156535	CD109	CD109 molecule [Source:HGNC Symbol;Acc:21685]",
				"ENSG00000189079	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000189079	ARID2	AT rich interactive domain 2 (ARID, RFX-like) [Source:HGNC Symbol;Acc:18037]",
				"ENSG00000154920	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154920	EME1	essential meiotic endonuclease 1 homolog 1 (S. pombe) [Source:HGNC Symbol;Acc:24965]",
				"ENSG00000106351	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000106351	AGFG2	ArfGAP with FG repeats 2 [Source:HGNC Symbol;Acc:5177]",
				"ENSG00000140948	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140948	ZCCHC14	zinc finger, CCHC domain containing 14 [Source:HGNC Symbol;Acc:24134]",
				"ENSG00000174928	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174928	C3orf33	chromosome 3 open reading frame 33 [Source:HGNC Symbol;Acc:26434]",
				"ENSG00000126259	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126259	KIRREL2	kin of IRRE like 2 (Drosophila) [Source:HGNC Symbol;Acc:18816]",
				"ENSG00000117399	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000117399	CDC20	cell division cycle 20 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1723]",
				"ENSG00000138468	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138468	SENP7	SUMO1/sentrin specific peptidase 7 [Source:HGNC Symbol;Acc:30402]",
				"ENSG00000170234	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170234	PWWP2A	PWWP domain containing 2A [Source:HGNC Symbol;Acc:29406]",
				"ENSG00000130305	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000130305	NSUN5	NOP2/Sun domain family, member 5 [Source:HGNC Symbol;Acc:16385]",
				"ENSG00000168329	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168329	CX3CR1	chemokine (C-X3-C motif) receptor 1 [Source:HGNC Symbol;Acc:2558]",
				"ENSG00000174579	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000174579	MSL2	male-specific lethal 2 homolog (Drosophila) [Source:HGNC Symbol;Acc:25544]",
				"ENSG00000119906	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119906	FAM178A	family with sequence similarity 178, member A [Source:HGNC Symbol;Acc:17814]",
				"ENSG00000133597	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133597	ADCK2	aarF domain containing kinase 2 [Source:HGNC Symbol;Acc:19039]",
				"ENSG00000168488	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168488	ATXN2L	ataxin 2-like [Source:HGNC Symbol;Acc:31326]",
				"ENSG00000164074	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000164074	C4orf29	chromosome 4 open reading frame 29 [Source:HGNC Symbol;Acc:26111]",
				"ENSG00000153071	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000153071	DAB2	disabled homolog 2, mitogen-responsive phosphoprotein (Drosophila) [Source:HGNC Symbol;Acc:2662]",
				"ENSG00000124688	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000124688	MAD2L1BP	MAD2L1 binding protein [Source:HGNC Symbol;Acc:21059]",
				"ENSG00000156219	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000156219	ART3	ADP-ribosyltransferase 3 [Source:HGNC Symbol;Acc:725]",
				"ENSG00000143751	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143751	C1orf55	chromosome 1 open reading frame 55 [Source:HGNC Symbol;Acc:26643]",
				"ENSG00000148444	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148444	COMMD3	COMM domain containing 3 [Source:HGNC Symbol;Acc:23332]",
				"ENSG00000127125	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000127125	PPCS	phosphopantothenoylcysteine synthetase [Source:HGNC Symbol;Acc:25686]",
				"ENSG00000169251	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000169251	NMD3	NMD3 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:24250]",
				"ENSG00000188674	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000188674	C2orf80	chromosome 2 open reading frame 80 [Source:HGNC Symbol;Acc:34352]",
				"ENSG00000037897	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000037897	METTL1	methyltransferase like 1 [Source:HGNC Symbol;Acc:7030]",
				"ENSG00000173451	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173451	THAP2	THAP domain containing, apoptosis associated protein 2 [Source:HGNC Symbol;Acc:20854]",
				"ENSG00000119888	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119888	EPCAM	epithelial cell adhesion molecule [Source:HGNC Symbol;Acc:11529]",
				"ENSG00000096872	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000096872	IFT74	intraflagellar transport 74 homolog (Chlamydomonas) [Source:HGNC Symbol;Acc:21424]",
				"ENSG00000166924	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166924	NYAP1	neuronal tyrosine-phosphorylated phosphoinositide-3-kinase adaptor 1 [Source:HGNC Symbol;Acc:22009]",
				"ENSG00000119725	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119725	ZNF410	zinc finger protein 410 [Source:HGNC Symbol;Acc:20144]",
				"ENSG00000143499	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143499	SMYD2	SET and MYND domain containing 2 [Source:HGNC Symbol;Acc:20982]",
				"ENSG00000182050	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182050	MGAT4C	mannosyl (alpha-1,3-)-glycoprotein beta-1,4-N-acetylglucosaminyltransferase, isozyme C (putative) [Source:HGNC Symbol;Acc:30871]",
				"ENSG00000170004	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000170004	CHD3	chromodomain helicase DNA binding protein 3 [Source:HGNC Symbol;Acc:1918]",
				"ENSG00000005100	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000005100	DHX33	DEAH (Asp-Glu-Ala-His) box polypeptide 33 [Source:HGNC Symbol;Acc:16718]",
				"ENSG00000184828	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184828	ZBTB7C	zinc finger and BTB domain containing 7C [Source:HGNC Symbol;Acc:31700]",
				"ENSG00000149136	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000149136	SSRP1	structure specific recognition protein 1 [Source:HGNC Symbol;Acc:11327]",
				"ENSG00000140320	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000140320	BAHD1	bromo adjacent homology domain containing 1 [Source:HGNC Symbol;Acc:29153]",
				"ENSG00000068796	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068796	KIF2A	kinesin heavy chain member 2A [Source:HGNC Symbol;Acc:6318]",
				"ENSG00000040933	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000040933	INPP4A	inositol polyphosphate-4-phosphatase, type I, 107kDa [Source:HGNC Symbol;Acc:6074]",
				"ENSG00000105248	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000105248	CCDC94	coiled-coil domain containing 94 [Source:HGNC Symbol;Acc:25518]",
				"ENSG00000104067	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000104067	TJP1	tight junction protein 1 (zona occludens 1) [Source:HGNC Symbol;Acc:11827]",
				"ENSG00000129244	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129244	ATP1B2	ATPase, Na+/K+ transporting, beta 2 polypeptide [Source:HGNC Symbol;Acc:805]",
				"ENSG00000184226	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000184226	PCDH9	protocadherin 9 [Source:HGNC Symbol;Acc:8661]",
				"ENSG00000182973	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000182973	CNOT10	CCR4-NOT transcription complex, subunit 10 [Source:HGNC Symbol;Acc:23817]",
				"ENSG00000187068	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000187068	C3orf70	chromosome 3 open reading frame 70 [Source:HGNC Symbol;Acc:33731]",
				"ENSG00000120709	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000120709	FAM53C	family with sequence similarity 53, member C [Source:HGNC Symbol;Acc:1336]",
				"ENSG00000181852	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000181852	RNF41	ring finger protein 41 [Source:HGNC Symbol;Acc:18401]",
				"ENSG00000198794	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198794	SCAMP5	secretory carrier membrane protein 5 [Source:HGNC Symbol;Acc:30386]",
				"ENSG00000163848	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163848	ZNF148	zinc finger protein 148 [Source:HGNC Symbol;Acc:12933]",
				"ENSG00000043143	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000043143	PHF15	PHD finger protein 15 [Source:HGNC Symbol;Acc:22984]",
				"ENSG00000135111	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000135111	TBX3	T-box 3 [Source:HGNC Symbol;Acc:11602]",
				"ENSG00000101361	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101361	NOP56	NOP56 ribonucleoprotein homolog (yeast) [Source:HGNC Symbol;Acc:15911]",
				"ENSG00000115234	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000115234	SNX17	sorting nexin 17 [Source:HGNC Symbol;Acc:14979]",
				"ENSG00000108264	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108264	TADA2A	transcriptional adaptor 2A [Source:HGNC Symbol;Acc:11531]",
				"ENSG00000173334	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000173334	TRIB1	tribbles homolog 1 (Drosophila) [Source:HGNC Symbol;Acc:16891]",
				"ENSG00000162772	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000162772	ATF3	activating transcription factor 3 [Source:HGNC Symbol;Acc:785]",
				"ENSG00000196233	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000196233	LCOR	ligand dependent nuclear receptor corepressor [Source:HGNC Symbol;Acc:29503]",
				"ENSG00000111725	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000111725	PRKAB1	protein kinase, AMP-activated, beta 1 non-catalytic subunit [Source:HGNC Symbol;Acc:9378]",
				"ENSG00000006712	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000006712	PAF1	Paf1, RNA polymerase II associated factor, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:25459]",
				"ENSG00000033170	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000033170	FUT8	fucosyltransferase 8 (alpha (1,6) fucosyltransferase) [Source:HGNC Symbol;Acc:4019]",
				"ENSG00000109670	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000109670	FBXW7	F-box and WD repeat domain containing 7, E3 ubiquitin protein ligase [Source:HGNC Symbol;Acc:16712]",
				"ENSG00000151923	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000151923	TIAL1	TIA1 cytotoxic granule-associated RNA binding protein-like 1 [Source:HGNC Symbol;Acc:11804]",
				"ENSG00000078142	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000078142	PIK3C3	phosphoinositide-3-kinase, class 3 [Source:HGNC Symbol;Acc:8974]",
				"ENSG00000108312	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108312	UBTF	upstream binding transcription factor, RNA polymerase I [Source:HGNC Symbol;Acc:12511]",
				"ENSG00000134371	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000134371	CDC73	cell division cycle 73, Paf1/RNA polymerase II complex component, homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:16783]",
				"ENSG00000125656	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125656	CLPP	ClpP caseinolytic peptidase, ATP-dependent, proteolytic subunit homolog (E. coli) [Source:HGNC Symbol;Acc:2084]",
				"ENSG00000123560	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000123560	PLP1	proteolipid protein 1 [Source:HGNC Symbol;Acc:9086]",
				"ENSG00000068308	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000068308	OTUD5	OTU domain containing 5 [Source:HGNC Symbol;Acc:25402]",
				"ENSG00000167004	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167004	PDIA3	protein disulfide isomerase family A, member 3 [Source:HGNC Symbol;Acc:4606]",
				"ENSG00000133884	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133884	DPF2	D4, zinc and double PHD fingers family 2 [Source:HGNC Symbol;Acc:9964]",
				"ENSG00000138757	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138757	G3BP2	GTPase activating protein (SH3 domain) binding protein 2 [Source:HGNC Symbol;Acc:30291]",
				"ENSG00000186660	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000186660	ZFP91	zinc finger protein 91 homolog (mouse) [Source:HGNC Symbol;Acc:14983]",
				"ENSG00000113141	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113141	IK	IK cytokine, down-regulator of HLA II [Source:HGNC Symbol;Acc:5958]",
				"ENSG00000136485	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136485	DCAF7	DDB1 and CUL4 associated factor 7 [Source:HGNC Symbol;Acc:30915]",
				"ENSG00000136819	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000136819	C9orf78	chromosome 9 open reading frame 78 [Source:HGNC Symbol;Acc:24932]",
				"ENSG00000143106	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000143106	PSMA5	proteasome (prosome, macropain) subunit, alpha type, 5 [Source:HGNC Symbol;Acc:9534]",
				"ENSG00000125834	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000125834	STK35	serine/threonine kinase 35 [Source:HGNC Symbol;Acc:16254]",
				"ENSG00000080824	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000080824	HSP90AA1	heat shock protein 90kDa alpha (cytosolic), class A member 1 [Source:HGNC Symbol;Acc:5253]",
				"ENSG00000175115	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175115	PACS1	phosphofurin acidic cluster sorting protein 1 [Source:HGNC Symbol;Acc:30032]",
				"ENSG00000142178	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000142178	SIK1	salt-inducible kinase 1 [Source:HGNC Symbol;Acc:11142]",
				"ENSG00000198218	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000198218	QRICH1	glutamine-rich 1 [Source:HGNC Symbol;Acc:24713]",
				"ENSG00000139746	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139746	RBM26	RNA binding motif protein 26 [Source:HGNC Symbol;Acc:20327]",
				"ENSG00000167578	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167578	RAB4B	RAB4B, member RAS oncogene family [Source:HGNC Symbol;Acc:9782]",
				"ENSG00000154511	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000154511	FAM69A	family with sequence similarity 69, member A [Source:HGNC Symbol;Acc:32213]",
				"ENSG00000126091	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000126091	ST3GAL3	ST3 beta-galactoside alpha-2,3-sialyltransferase 3 [Source:HGNC Symbol;Acc:10866]",
				"ENSG00000159202	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000159202	UBE2Z	ubiquitin-conjugating enzyme E2Z [Source:HGNC Symbol;Acc:25847]",
				"ENSG00000121577	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000121577	POPDC2	popeye domain containing 2 [Source:HGNC Symbol;Acc:17648]",
				"ENSG00000119402	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000119402	FBXW2	F-box and WD repeat domain containing 2 [Source:HGNC Symbol;Acc:13608]",
				"ENSG00000128607	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000128607	KLHDC10	kelch domain containing 10 [Source:HGNC Symbol;Acc:22194]",
				"ENSG00000166446	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000166446	CDYL2	chromodomain protein, Y-like 2 [Source:HGNC Symbol;Acc:23030]",
				"ENSG00000168090	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168090	COPS6	COP9 constitutive photomorphogenic homolog subunit 6 (Arabidopsis) [Source:HGNC Symbol;Acc:21749]",
				"ENSG00000084731	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000084731	KIF3C	kinesin family member 3C [Source:HGNC Symbol;Acc:6321]",
				"ENSG00000108528	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108528	SLC25A11	solute carrier family 25 (mitochondrial carrier; oxoglutarate carrier), member 11 [Source:HGNC Symbol;Acc:10981]",
				"ENSG00000113083	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000113083	LOX	lysyl oxidase [Source:HGNC Symbol;Acc:6664]",
				"ENSG00000138795	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000138795	LEF1	lymphoid enhancer-binding factor 1 [Source:HGNC Symbol;Acc:6551]",
				"ENSG00000139546	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000139546	TARBP2	TAR (HIV-1) RNA binding protein 2 [Source:HGNC Symbol;Acc:11569]",
				"ENSG00000129933	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000129933	MAU2	MAU2 chromatid cohesion factor homolog (C. elegans) [Source:HGNC Symbol;Acc:29140]",
				"ENSG00000175215	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000175215	CTDSP2	CTD (carboxy-terminal domain, RNA polymerase II, polypeptide A) small phosphatase 2 [Source:HGNC Symbol;Acc:17077]",
				"ENSG00000131051	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000131051	RBM39	RNA binding motif protein 39 [Source:HGNC Symbol;Acc:15923]",
				"ENSG00000148943	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148943	LIN7C	lin-7 homolog C (C. elegans) [Source:HGNC Symbol;Acc:17789]",
				"ENSG00000168301	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000168301	KCTD6	potassium channel tetramerisation domain containing 6 [Source:HGNC Symbol;Acc:22235]",
				"ENSG00000110321	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000110321	EIF4G2	eukaryotic translation initiation factor 4 gamma, 2 [Source:HGNC Symbol;Acc:3297]",
				"ENSG00000015592	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000015592	STMN4	stathmin-like 4 [Source:HGNC Symbol;Acc:16078]",
				"ENSG00000148835	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000148835	TAF5	TAF5 RNA polymerase II, TATA box binding protein (TBP)-associated factor, 100kDa [Source:HGNC Symbol;Acc:11539]",
				"ENSG00000157837	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000157837	SPPL3	signal peptide peptidase like 3 [Source:HGNC Symbol;Acc:30424]",
				"ENSG00000163590	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000163590	PPM1L	protein phosphatase, Mg2+/Mn2+ dependent, 1L [Source:HGNC Symbol;Acc:16381]",
				"ENSG00000036257	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000036257	CUL3	cullin 3 [Source:HGNC Symbol;Acc:2553]",
				"ENSG00000094880	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000094880	CDC23	cell division cycle 23 homolog (S. cerevisiae) [Source:HGNC Symbol;Acc:1724]",
				"ENSG00000004487	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000004487	KDM1A	lysine (K)-specific demethylase 1A [Source:HGNC Symbol;Acc:29079]",
				"ENSG00000177885	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000177885	GRB2	growth factor receptor-bound protein 2 [Source:HGNC Symbol;Acc:4566]",
				"ENSG00000133895	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000133895	MEN1	multiple endocrine neoplasia I [Source:HGNC Symbol;Acc:7010]",
				"ENSG00000100105	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000100105	PATZ1	POZ (BTB) and AT hook containing zinc finger 1 [Source:HGNC Symbol;Acc:13071]",
				"ENSG00000167693	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000167693	NXN	nucleoredoxin [Source:HGNC Symbol;Acc:18008]",
				"ENSG00000108175	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000108175	ZMIZ1	zinc finger, MIZ-type containing 1 [Source:HGNC Symbol;Acc:16493]",
				"ENSG00000101266	http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=ENSG00000101266	CSNK2A1	casein kinase 2, alpha 1 polypeptide [Source:HGNC Symbol;Acc:2457]"
		};
		for(String locus:unparsedData){
			metadata.put(locus.split("\t")[0].toUpperCase(), locus);
		}
		return metadata;
	}
	
	/**
	 * Private utility class to calculate the mean of a variable safely (NaNs ignored/don't screw it up)
	 * <p>NB <i>N</i>, the count of items in the array, will <b>not</b> include NaNs.
	 * @param values - a double[] with some values including Double.NaNs
	 * @return arithmetic mean of values
	 */
	private double meanSafeForNaN(double[] values){
		double mean = 0.0d;
		int count = 0;
		for(int i=0;i<values.length;i++){
			if(!(new Double(values[i]).isNaN())){
				mean += values[i];
				count ++;
			}
		}
		mean = mean / (new Double(count));
		return mean;
	}
	
	protected static double [] concat(double[] first, double[] second){
		double[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	protected static <T> T[] concat(T[] first, T[] second){
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	protected static <T> T[] concatAll(T[] first, T[]... rest){
		int totalLength = first.length;
		for(T[] array : rest){
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for(T[] array : rest){
			System.arraycopy(array, 0, result, offset, array.length);
		}
		return result;
	}
}
