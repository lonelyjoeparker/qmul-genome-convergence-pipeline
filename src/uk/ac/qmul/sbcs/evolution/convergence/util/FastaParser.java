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
public class FastaParser extends AlignmentParser {

	/**
	 * Parses a fasta file
	 * 
	 * Notes from AlignedSequenceRepresentation.readPhylipFile(), from which this method was forked on 11 Feb / r370:
	 * 
	 * 	
	 * @since 31/10/2011
	 * RESOLVED: 31/10/2011: TODO testing revealing some very strange behaviour in some fasta reads. work out why.
	 * A number of errors - 
	 * 		lastname and lastdata initialised wrongly to null not ""
	 * 		taxon names added as substr(1) not substr(1,length-1) so not now truncated
	 * 		(incidental) loadSequences() now takes boolean arg for line reporting
	 * 		MAJOR error: 	lastname was always being reinitialised after sequenceHash.add() to "", so half of the time
	 * 						e.g. some taxa names are ignored, and their data entered in the other taxa, doubling line length
	 * 		MAJOR error:	the final taxon was never being added to sequenceHash as its match condition was not being filled.
	 */
	public FastaParser() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.AlignmentParser#parseInput(java.util.ArrayList)
	 */
	@Override
	public boolean parseInput(ArrayList<String> rawInput) {
//		assert(true);
//		System.out.println("Processing a fasta file");
		String lastname = "";
		String lastdata = "";
		int maxNoOfSites = 0;
		Iterator lines = rawInput.iterator();
		int count = 1;
		while(lines.hasNext()){
			String aline = (String)lines.next();
//			System.out.println(count+"\tname:["+lastname+"]\tdata ["+lastdata+"]\tline ["+aline+"]");
			count ++;
			Pattern fasta = Pattern.compile(">");
			Matcher isFasta = fasta.matcher(aline);
			if(isFasta.find()){
				if(lastname != ""){
					// a taxon name has previously been read in
					// add sequence to hash and reset lastname
//					System.out.println("about to add a taxon, data length is: "+lastdata.length());
					if(lastdata.length()>maxNoOfSites){
						maxNoOfSites = lastdata.length();
//						System.out.println("max site count increased to "+maxNoOfSites);
					}
					sequenceHash.put(lastname, lastdata.toCharArray());
					taxaList.add(lastname);
					numberOfTaxa++;
//					System.out.println("putting "+lastname+" "+lastdata);
					lastname = aline.substring(1);
					lastdata = "";
				}else{
					lastname = aline.substring(1);
				}
			}else{
//				System.out.println("data line, length "+aline.length());
				if(lastdata == ""){
					lastdata = aline;
//					System.out.println("adding to null lastdata:\t"+aline);
				}else{
					lastdata += aline;
//					System.out.println("adding to existing lastdata:\t"+aline);
				}
			}
		}
//		System.out.println("about to add LAST taxon, data length is: "+lastdata.length());
		if(lastdata.length()>maxNoOfSites){
			maxNoOfSites = lastdata.length();
//			System.out.println("max site count increased to "+maxNoOfSites);
		}
		sequenceHash.put(lastname, lastdata.toCharArray());
		taxaList.add(lastname);
		numberOfTaxa++;
//		System.out.println("putting "+lastname+" "+lastdata);
		numberOfSites = maxNoOfSites;
		Iterator<String> itr = taxaList.iterator();
		taxaListArray = new String[taxaList.size()];
		int index = 0;
		while(itr.hasNext()){
			taxaListArray[index] = itr.next();
			index++;
		}
		return true;
	}

}
