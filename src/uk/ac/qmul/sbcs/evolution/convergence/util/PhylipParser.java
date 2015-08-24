/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class PhylipParser extends AlignmentParser {

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.AlignmentParser#parseInput(java.util.ArrayList)
	 */
	@Override
	public boolean parseInput(ArrayList<String> rawInput) {
		int maxNoOfSites = 0;
		System.out.println("Processing a phylip file");
		String firstline = rawInput.remove(0);
		String[] firstlineData = firstline.split(" {1,}|\t");
		assert(firstlineData.length>1);
//		System.out.println(firstlineData[0]+" taxa, "+firstlineData[1]+" characters");
		for(String aline:rawInput){
			String[] lineData = aline.split(" {1,}");
			if((aline.length()>1)){
				String name = lineData[0];
				char[] charSequence;
				if(lineData.length<3){
					charSequence = lineData[1].toCharArray(); 
				}else{
					// There are probably spaces in the sequence, parse accordingly
					StringBuilder sb = new StringBuilder();
					for(int s = 1;s<lineData.length;s++){
						sb.append(lineData[s]);
					}		
					charSequence = sb.toString().toCharArray();
				}
				if(charSequence.length>maxNoOfSites){
					maxNoOfSites = charSequence.length;
				}
				try {
					sequenceHash.put(name,charSequence);
					taxaList.add(name);
					numberOfTaxa ++;
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println(name+" (name), "+charSequence[0]+" (characters; truncated to 20)");
			}
		}
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
