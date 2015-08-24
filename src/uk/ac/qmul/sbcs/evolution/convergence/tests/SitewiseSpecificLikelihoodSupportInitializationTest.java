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
		String[] results = new String[27];
		results[3] = "g_0_ENSG00000104237_ng.fasinput.phy.nc.faconv1363157723669dayhoff.ser";
		results[4] = "g_0_ENSG00000104237_ng.fasinput.phy.nc.faconv1363157723669jones.ser";
		results[5] = "g_0_ENSG00000104237_ng.fasinput.phy.nc.faconv1363157723669wag.ser";
		results[6] = "g_0_ENSG00000109103_ng.fasinput.phy.nc.faconv1363157738330dayhoff.ser";
		results[7] = "g_0_ENSG00000109103_ng.fasinput.phy.nc.faconv1363157738330jones.ser";
		results[8] = "g_0_ENSG00000109103_ng.fasinput.phy.nc.faconv1363157738330wag.ser";
		results[9] = "g_0_ENSG00000129353_ng.fasinput.phy.nc.faconv1363157738888dayhoff.ser";
		results[10] = "g_0_ENSG00000129353_ng.fasinput.phy.nc.faconv1363157738888jones.ser";
		results[11] = "g_0_ENSG00000129353_ng.fasinput.phy.nc.faconv1363157738888wag.ser";
		results[12] = "g_0_ENSG00000132911_ng.fasinput.phy.nc.faconv1363157725396dayhoff.ser";
		results[13] = "g_0_ENSG00000132911_ng.fasinput.phy.nc.faconv1363157725396jones.ser";
		results[14] = "g_0_ENSG00000132911_ng.fasinput.phy.nc.faconv1363157725396wag.ser";
		results[15] = "g_0_ENSG00000164175_ng.fasinput.phy.nc.faconv1363157725396dayhoff.ser";
		results[16] = "g_0_ENSG00000164175_ng.fasinput.phy.nc.faconv1363157725396jones.ser";
		results[17] = "g_0_ENSG00000164175_ng.fasinput.phy.nc.faconv1363157725396wag.ser";
		results[18] = "g_0_ENSG00000166881_ng.fasinput.phy.nc.faconv1363157723469dayhoff.ser";
		results[19] = "g_0_ENSG00000166881_ng.fasinput.phy.nc.faconv1363157723469jones.ser";
		results[20] = "g_0_ENSG00000166881_ng.fasinput.phy.nc.faconv1363157723469wag.ser";
		results[21] = "g_0_ENSG00000197296_ng.fasinput.phy.nc.faconv1363157724125dayhoff.ser";
		results[22] = "g_0_ENSG00000197296_ng.fasinput.phy.nc.faconv1363157724125jones.ser";
		results[23] = "g_0_ENSG00000197296_ng.fasinput.phy.nc.faconv1363157724125wag.ser";
		results[24] = "g_0_prestinInputDec2011.fainput.phy.nc.faconv1363157738604dayhoff.ser";
		results[25] = "g_0_prestinInputDec2011.fainput.phy.nc.faconv1363157738604jones.ser";
		results[26] = "g_0_prestinInputDec2011.fainput.phy.nc.faconv1363157738604wag.ser";
		for(String someSer:results){
			if(someSer != null){
				try {
					FileInputStream fileInOne = new FileInputStream("/Users/gsjones/Documents/all_work/QMUL/FSD/results_revision_mar2013/serialised/"+someSer);
					ObjectInputStream inOne = new ObjectInputStream(fileInOne);
					SSLS2 = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(SSLS2.getInputFileName()+SSLS2.getFinished()+SSLS2.getSSLSseriesSitewise()[0][0]);
			}
		}
		this.hashCode();
	}
}
