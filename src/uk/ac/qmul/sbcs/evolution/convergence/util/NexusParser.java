/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class NexusParser extends AlignmentParser {

	/**
	 * Parses a sequence alignment file. parseInput() method forked from AlignedSequenceRepresentaion.readNexusFile(), 11/02/2015 r371
	 */
	public NexusParser() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.AlignmentParser#parseInput(java.util.ArrayList)
	 */
	@Override
	public boolean parseInput(ArrayList<String> rawInput) {
		System.out.println("Processing a nexus file");
		boolean inMatrix = false;
		boolean taxlabelsPresent = false;
		boolean taxlabelsRead = false;
		boolean inTaxlabels = false;
		int maxNoOfSites = 0;
		
		/*
		 * Set up the regex variables for the parsing 
		 */
		Pattern blockEnd = Pattern.compile("END");
		Pattern blockMatrix = Pattern.compile("MATRIX");
		Pattern blockComment = Pattern.compile("\\[");
		Pattern blockTaxlabels = Pattern.compile("TAXLABELS");

		for(String aline:rawInput){
			String ucLine = aline.toUpperCase();
			int lineLength = aline.length();

			/*
			 * Assign Matchers to the Patterns and also length.
			 * Should now be ready to evalute block conditions.
			 */
			Matcher isEnd = blockEnd.matcher(ucLine);
			Matcher isMatrix = blockMatrix.matcher(ucLine);
			Matcher isComment = blockComment.matcher(ucLine);
			Matcher isTaxlabels = blockTaxlabels.matcher(ucLine);
			
			boolean foundEnd = isEnd.find();
			boolean foundMatrix = isMatrix.find();
			boolean foundComment = isComment.find();
			boolean foundTaxlabels = isTaxlabels.find();
			
//			System.out.println("Nexus parsing line: "+aline+"\t");

			if(inMatrix && lineLength>10 && !(taxlabelsPresent || foundComment)){
				/*
				 * This is a data line - NO taxlabels block specified
				 */
				// TODO parse data line - without expecting taxlabels information.
//				System.out.println("\t\tDATALINE (no taxlabels)"+aline.substring(0,15));
				
				Pattern quotesIn = Pattern.compile("'");
				Matcher areQuotesIn = quotesIn.matcher(aline);
				if(areQuotesIn.find()){
					aline = new StringWalker(aline,' ','_','\'').finishedSequence;
				}
				ucLine = aline.toUpperCase();
				String[] lineData = aline.split("[\t| ]+");
//				System.out.println("split line data into "+lineData.length+" tokens");
				String tokenName = new StringRemover(lineData[1].toUpperCase(),'\'').finishedSequence;
				String tokenData = new StringRemover(lineData[2].toUpperCase(),';').finishedSequence;
				// TODO replace ' and ; chars, and try/catch?
				sequenceHash.put(tokenName, tokenData.toCharArray());
				taxaList.add(tokenName);
				numberOfTaxa++;
				if(tokenData.length()>maxNoOfSites){
					maxNoOfSites = tokenData.length();
				}
			}
			if(inMatrix && (lineLength > 10) && taxlabelsPresent && !foundComment){
				/*
				 * This is a data line - WITH taxlabels block
				 */
				// TODO parse data line - with taxlabels expected inline.
				String cleanedLine = new StringRemover(aline.trim(),';').finishedSequence;
//				System.out.println("\t\tDATALINE (with taxlabels "+cleanedLine.substring(0,30));
				sequenceHash.put(taxaListArray[numberOfTaxa],cleanedLine.toCharArray());
				numberOfTaxa++;
				if(cleanedLine.length()>maxNoOfSites){
					maxNoOfSites = cleanedLine.length();
				}
			}
			if(foundComment){
//				System.out.println("\tfound a 'COMMENT ([)' statement");
				/*
				 * Do nothing
				 */
			}
			if(foundEnd){
//				System.out.println("\tfound a 'END' statement");
				inMatrix = false;
				inTaxlabels = false;
				if(taxaList.size()>2){
					taxlabelsRead = true;
					// Write the sorted taxaList (TreeSet) to a String[]
					Iterator<String> itr = taxaList.iterator();
					taxaListArray = new String[taxaList.size()];
					int index = 0;
					while(itr.hasNext()){
						taxaListArray[index] = itr.next();
						index++;
					}
				}
			}
			if(foundMatrix){
//				System.out.println("\tfound a 'MATRIX' statement");
				inMatrix = true;
			}
			if(taxlabelsPresent && !taxlabelsRead){
				String cleanedLine = new StringWalker(aline,' ','_','\'').finishedSequence;
//				System.out.println("cleanedline "+cleanedLine);
				if(inTaxlabels && lineLength>2){
					// TODO implement taxlabels collection (parse name data, add to taxlabels array)
					/*
					 * IMPORTANT - 	taxlabels may be on a single line (space or comma separated?) 
					 * 				OR they may be in sequential lines.
					 * 				TODO Make sure to parse BOTH!
					 * 				TODO ****VERY IMPORTANT*** Expecting that input ordering will be preserved; e.g. use a SET
					 */
//					System.out.println("constructing taxa labels HashSet");
					String[] taxlabels = cleanedLine.split("[ |\t|,]{1,}");
					for(String label:taxlabels){
						if(label.length()>1){
							String cleanedLabel = new StringRemover(label,'\'').finishedSequence.toUpperCase();
							taxaList.add(cleanedLabel);
//							System.out.println("\tN:"+cleanedLabel);
						}
					}
				}
			}
			if(foundTaxlabels){
//				System.out.println("\tfound a 'TAXLABELS' statement");
				taxlabelsPresent = true;
				inTaxlabels = true;
			}

//			System.out.println("end of line parsing");
		}
		numberOfSites = maxNoOfSites;
		return true;
	}

}
