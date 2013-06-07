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
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.ParsimonyReconstruction;
import uk.ac.qmul.sbcs.evolution.convergence.StateComparison;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.serFilter;

public class InformativeSubstitutionDetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InformativeSubstitutionDetector p = new InformativeSubstitutionDetector();
		p.go(args);
	}

	private void go(String[] args){
		System.out.println("input\tInputFileName\tmodel\tParallel H1\tParallel H1c\tParallel H1o\tFrom MRCAH1\tfullStrictH1\tParallel H2\tParallel H2c\tParallel H2o\tFrom MRCAH2\tFrom MRCAH2 (require T.truncatus)\tFrom MRCAH2 (require T.truncatus, no root ambiguities)\tfull_strict_H2\t(Ambiguous at root)");
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
						String[] patterns = candidate.getPatterns();
						int numInformative = 0;
						int numSingletons   = 0;
						int numInvariant   = 0;
						for(String pattern:patterns){
							HashMap<Character,Integer> observed = new HashMap<Character,Integer>();
							char[] obsSites = pattern.toCharArray();
							int numValid = 0;
							for(char aSite:obsSites){
								if((aSite != '-')&&(aSite != 'X')){
									numValid++;
									if(observed.containsKey(aSite)){
										// this site already seen
										int before = observed.get(aSite);
										observed.put(aSite, before+1);
									}else{
										// this site not seen
										observed.put(aSite, 1);
									}
								}
							}
							// should now have built the # taxa for each pattern
							// find invariants
							if(observed.size() == 1){
								numInvariant++;
							}else{
								boolean singleton = false;
								int informativeTaxaCount = 0;
								Character[] patternItr = observed.keySet().toArray(new Character[0]);
								for(char aChar:patternItr){
									if(observed.containsKey(aChar)){
										if(observed.get(aChar) == 1){
											observed.remove(aChar);
											singleton = true;
										}else{
											informativeTaxaCount ++;
										}
									}
								}
								if((observed.size()>1)&&(informativeTaxaCount>1)){numInformative++;}else{numSingletons++;}
							}
						}
						System.out.println(input.getName()+"\t"+candidate.getInputFileName()+"\t"+numInformative+"\t"+numSingletons+"\t"+numInvariant+"\t"+patterns.length);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
