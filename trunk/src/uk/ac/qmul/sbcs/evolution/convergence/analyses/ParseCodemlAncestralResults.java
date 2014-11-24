package uk.ac.qmul.sbcs.evolution.convergence.analyses;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralGeneralOutputParser;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser;

public class ParseCodemlAncestralResults {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// parse codeml-anc.out file
		File generalResultsFile = new File(args[0]);
		CodemlAncestralGeneralOutputParser generalParser = new CodemlAncestralGeneralOutputParser(generalResultsFile);
		HashMap<String,Integer> nameIDmapping = generalParser.getTaxonNamesIDMap();
		TreeNode phylogeny = generalParser.getPhylogeny();
		// parse convergentSites.out file
		File convergentSitesFile = new File(args[1]);
		CodemlAncestralSiteOutputParser sitesParser = new CodemlAncestralSiteOutputParser(convergentSitesFile);
		sitesParser.setPhylogenyWithSpecifiedTipLabelNumberMapping(phylogeny, nameIDmapping);
		
		// should now have parsed the key input data
		// print main data
		float[][] allData = sitesParser.getAllBranchPairProbabilitiesSitewiseSummed();
		int row = 1;
		for(float[] site:allData){
			System.out.print(row+"\t");
			for(float prob:site){
				System.out.print("\t"+prob);
			}
			row++;
			System.out.println();
		}
		
		// just need to get focal and collective outputs
		String[] focal_1 = args[2].split("-");
		String[] focal_2 = args[3].split("-");
		float[] dataForThisPair = null;
		try {
			dataForThisPair = sitesParser.getProbabilitiesForAncestralBranchComparisonsDefinedByTaxonSetMRCAs(focal_1, focal_2);
			for(float val:dataForThisPair){
				System.out.print(val+"\t");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("\t(");
		for(String taxon:focal_1){
			System.out.print(taxon+",");
		}
		System.out.print(") (");
		for(String taxon:focal_2){
			System.out.print(taxon+",");
		}
		System.out.println(")");
	}

}
