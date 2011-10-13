package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Random;

import uk.ac.qmul.sbcs.evolution.convergence.util.*;

/**
 * @author Joe Parker, <a href="http://code.google.com/a/eclipselabs.org/u/joeparkerandthemegahairymen/">Kitson Consulting Ltd / Queen Mary, University of London.</a>
 * @version 0.1
 * @mailto: joe@kitson-consulting.co.uk
 * @since 09/19/2011
 */
		
public class AlignedSequenceRepresentation {

	private File file;
	private SequenceCodingType alignmentSequenceCodingType = SequenceCodingType.UNDEFINED;
	private ArrayList<String> rawInput;
	private SequenceFileFormat inputSequenceFileFormat;
	private int numberOfSites;
	private int numberOfInvariantSites;
	private int numberOfTaxa;
	private TreeSet<String> taxaList = new TreeSet<String>();
	private String[] taxaListArray;
	private TreeMap<String, char[]> sequenceHash = new TreeMap<String, char[]>();
	private HashMap<String, String> truncatedNamesHash = new HashMap<String, String>();
	private boolean sequenceFileTypeSet = false;
	private TreeMap<String,Character> translationLookup;
	protected boolean[] invariantSitesIndices;
	
	public void PhymlSequenceRespresentation(){}
	
	public void loadSequences(File inputFile) throws TaxaLimitException{
		file = inputFile;
		try{
			rawInput = new BasicFileReader().loadSequences(file,false);
			this.determineInputFileDatatype();
			assert(sequenceFileTypeSet);
			switch(inputSequenceFileFormat){
				case NEXUS: this.readNexusFile(); break;
				case FASTA: this.readFastaFile(); break;
				case PHYLIP: this.readPhylipFile(); break;
				case PHYDEX: this.readXMLFile(); break;
				case NATIVE: break;
			}
			if(numberOfTaxa > 999){
				System.out.println("Too many taxa in alignment - limit is 999");
				throw new TaxaLimitException(numberOfTaxa);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		alignmentSequenceCodingType = this.determineInputSequenceType();
		// TODO write truncatedNamesHash. Remember to check for duplicates, and use UID where required
		// TODO how are we going to handle on-the-fly UID generation w.r.t. compatibility with treefiles..?
		int UIDroot = 1;
		for(String longTaxon:taxaListArray){
			if(UIDroot > 1000){
				System.out.println("Too many taxa in alignment - limit is 999");
				throw new TaxaLimitException(UIDroot);
			}else{
				StringBuilder shortTaxon = new StringBuilder();
				if(longTaxon.length()>7){
					shortTaxon.append(longTaxon.substring(0, 7));
					shortTaxon.append(String.format("%03d", UIDroot));
				}else{
					shortTaxon.append(longTaxon);
					while(shortTaxon.length() < 7){
						shortTaxon.append("_");
					}
					shortTaxon.append(String.format("%03d", UIDroot));
				}
				assert(longTaxon.length()>1);
				assert(shortTaxon.length()>1);
				truncatedNamesHash.put(longTaxon, shortTaxon.toString());
			}
			UIDroot++;
		}
		this.determineInvariantSites();
	}
	
	public SequenceFileFormat determineInputFileDatatype(){
		if (sequenceFileTypeSet != true){
			System.out.println("determining input sequence format");
			this.inputSequenceFileFormat = SequenceFileFormat.NATIVE;
			//TODO: implement Perl wireframe code.
			Pattern fasta = Pattern.compile(">");
			Pattern nexus = Pattern.compile("NEXUS");
			Pattern phylip = Pattern.compile("^([ |\t]*[0-9]+){2}");
			Pattern xml = Pattern.compile("xml");
			Matcher isFasta = fasta.matcher(rawInput.get(0));
			Matcher isNexus = nexus.matcher(rawInput.get(0));
			Matcher isPhylip = phylip.matcher(rawInput.get(0));
			Matcher isXML = xml.matcher(rawInput.get(0));
			if(isFasta.find()){
				this.inputSequenceFileFormat = SequenceFileFormat.FASTA;
				System.out.println("Assuming filetype is fasta.");
			}
			if(isNexus.find()){
				this.inputSequenceFileFormat = SequenceFileFormat.NEXUS;
				System.out.println("Assuming filetype is NEXUS.");
			}
			if(isPhylip.find()){
				this.inputSequenceFileFormat = SequenceFileFormat.PHYLIP;
				System.out.println("Assuming filetype is Phylip.");
			}
			if(isXML.matches()){
				this.inputSequenceFileFormat = SequenceFileFormat.PHYDEX;
				System.out.println("Assuming filetype is PhyDEX-XML.");
			}
		}
		this.sequenceFileTypeSet = true;
		return this.inputSequenceFileFormat;
	}
	
	public SequenceCodingType determineInputSequenceType(){
		char[] candidates = "abcdefghijklmnopqrstuvwxyz-?*".toUpperCase().toCharArray();
		int[] counts = new int[29];
		int denom = numberOfSites * numberOfTaxa;
		float[] frequencies = new float[29];
		float totalObservations = 0;
		for(String taxon:taxaListArray){
			for(char aChar:sequenceHash.get(taxon)){
				switch(aChar){
					case 'A': counts[0]++; break;
					case 'B': counts[1]++; break;
					case 'C': counts[2]++; break;
					case 'D': counts[3]++; break;
					case 'E': counts[4]++; break;
					case 'F': counts[5]++; break;
					case 'G': counts[6]++; break;
					case 'H': counts[7]++; break;
					case 'I': counts[8]++; break;
					case 'J': counts[9]++; break;
					case 'K': counts[10]++; break;
					case 'L': counts[11]++; break;
					case 'M': counts[12]++; break;
					case 'N': counts[13]++; break;
					case 'O': counts[14]++; break;
					case 'P': counts[15]++; break;
					case 'Q': counts[16]++; break;
					case 'R': counts[17]++; break;
					case 'S': counts[18]++; break;
					case 'T': counts[19]++; break;
					case 'U': counts[20]++; break;
					case 'V': counts[21]++; break;
					case 'W': counts[22]++; break;
					case 'X': counts[23]++; break;
					case 'Y': counts[24]++; break;
					case 'Z': counts[25]++; break;
					case '-': counts[26]++; break;
					case '?': counts[27]++; break;
					case '*': counts[28]++; break;
					default: break;
				}
			}
		}
//		System.out.println("Determing sequence coding type.\nresidue\tCounts\tGlobal freq. (by fi,"+denom+" = Ni/("+numberOfSites+" * "+numberOfTaxa+"))");
		for(int i=0;i<29;i++){
			frequencies[i] = (float)counts[i] / denom;
			totalObservations += frequencies[i];
//			System.out.println(candidates[i]+"\t"+counts[i]+"\t"+frequencies[i]);
		}
		float obsDNA = 	frequencies[0]+
						frequencies[2]+						
						frequencies[6]+						
						frequencies[19]+						
						frequencies[26];						
		float obsRNA = 	frequencies[0]+
						frequencies[2]+						
						frequencies[6]+						
						frequencies[20]+						
						frequencies[26];						
		float obsAA = 	frequencies[0]+
						frequencies[2]+						
						frequencies[3]+						
						frequencies[4]+						
						frequencies[5]+						
						frequencies[6]+						
						frequencies[7]+						
						frequencies[8]+						
						frequencies[10]+						
						frequencies[11]+						
						frequencies[12]+						
						frequencies[13]+						
						frequencies[15]+						
						frequencies[16]+						
						frequencies[17]+						
						frequencies[18]+						
						frequencies[19]+						
						frequencies[21]+						
						frequencies[22]+						
						frequencies[24]+						
						frequencies[26];	
//		System.out.println("Predictions:\tDNA - "+obsDNA+"\tRNA - "+obsRNA+"\tAA - "+obsAA);

		if(obsDNA > 0.8){
			if(obsDNA > obsRNA){
				return SequenceCodingType.DNA;
			}else{
				return SequenceCodingType.RNA;
			}
		}else{
			if(obsRNA > obsAA){
				return SequenceCodingType.RNA;
			}else{
				return SequenceCodingType.AA;
			}
		}
	}
	
	public void readPhylipFile(){
		int maxNoOfSites = 0;
		System.out.println("Processing a phylip file");
		String firstline = rawInput.remove(0);
		String[] firstlineData = firstline.split(" {1,}");
		assert(firstlineData.length>1);
		System.out.println(firstlineData[0]+" taxa, "+firstlineData[1]+" characters");
		for(String aline:rawInput){
			String[] lineData = aline.split(" {1,}");
			if((aline.length()>1)){
				String name = lineData[0];
				char[] charSequence = lineData[1].toCharArray(); 
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
				System.out.println(name+" (name), "+charSequence[0]+" (characters; truncated to 20)");
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
	}
	
	public void readFastaFile(){
		System.out.println("Processing a fasta file");
		String lastname = null;
		String lastdata = null;
		int maxNoOfSites = 0;
		for(String aline:rawInput){
			Pattern fasta = Pattern.compile(">");
			Matcher isFasta = fasta.matcher(aline);
			if(isFasta.find()){
				if(lastname != null){
					// a taxon name has previously been read in
					// add sequence to hash and reset lastname
					if(lastdata.length()>maxNoOfSites){
						maxNoOfSites = lastdata.length();
					}
					sequenceHash.put(lastname, lastdata.toCharArray());
					taxaList.add(lastname);
					numberOfTaxa++;
//					System.out.println("putting "+lastname+" "+lastdata);
					lastname = null;
					lastdata = "";
				}else{
					lastname = aline.substring(1, aline.length()-1);
				}
			}else{
				lastdata += aline;
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
	}
	
	public void readNexusFile(){
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
	}
	
	public void readXMLFile(){
		System.out.println("Processing an XML PhyDEX file");
		System.out.println("Not implemented");
		// TODO Implement this
		// TODO Phydex data structure specifications TBC
	}
	
	public void writePhylipFile(String fullyPathQualifiedFileName){
		StringBuffer buffer = new StringBuffer();
		buffer.append(numberOfTaxa+"   "+numberOfSites+"\n");
		for(String taxon:taxaListArray){
			// TODO get shortname from truncatedNamesHash
			StringBuilder paddedTaxon = new StringBuilder(truncatedNamesHash.get(taxon));
			while(paddedTaxon.length() < 15){
				paddedTaxon.append(" ");
			}
			buffer.append(paddedTaxon);
			buffer.append(sequenceHash.get(taxon));
			buffer.append("\n");
		}
		new BasicFileWriter(fullyPathQualifiedFileName,buffer.toString());
	}
	
	public void writeFastaFile(){
		System.out.println("Not implemented");
		// TODO Implement this
	}
	
	public void writeNexusFile(){
		System.out.println("Not implemented");
		// TODO Implement this
	}

	public void writeXMLFile(){
		System.out.println("Not implemented");
		// TODO Implement this
	}

	/**
	 *	
	 * 	purpose:	constrain a subset of taxa to have the same AA
	 *	execution:	for i in num sites
	 *					random indices
	 *					get target master taxon as char array /// NB: codon triplets
	 *					read array[indices]
	 *					replace 
	 * 
	 * @param masterTaxon - The taxon that is being treated as the master. Sites will be converged to this taxon's sequence randomly
	 * @param taxaToForce - The taxa that in which convergence is to be simulated.
	 * @param numberOfSitesToConverge - The number of sites to be forced to converge.
	 */
	public void simulateConvergence(String masterTaxon, String[] taxaToForce, int numberOfSitesToConverge){
		System.out.println("Not implemented");
		char[] masterSequence = sequenceHash.get(masterTaxon);
		int[] targetSitesIndices;
		// TODO determine which sites we will converge at.
		// NOTE WELL - the Perl implementation of this uses a different index generation method for AA vs. codon data. Need to remember why this is.
		this.determineInvariantSites();
		int numberOfVariantSites = numberOfSites - numberOfInvariantSites;
		System.out.println("Attempting to simulate convergence in "+numberOfSitesToConverge+" sites.\nThere are "+numberOfSites+" in total; "+numberOfInvariantSites+" are invariant. That leaves "+numberOfVariantSites+" variant sites on which to simulate convergence.");
		HashSet<Integer> targetSet = new HashSet<Integer>();
		if(numberOfVariantSites < numberOfSitesToConverge){
			System.out.println("WARNING: Convergence simulation is parametized for more convergent sites than are available!\nI will instead simulate as many sites as possible (e.g. all sites - master and slave taxa will be identical. This seems like a very bad idea...");
			numberOfSitesToConverge = numberOfVariantSites;
			targetSitesIndices = new int[numberOfSitesToConverge];
			// We don't need to piss about with randomizations to fill the array, we can just write the variant sites' indices across.
			for(int i=0;i<numberOfSites;i++){
				if(!invariantSitesIndices[i]){
					targetSet.add(i);
				}
			}
		}else{
			targetSitesIndices = new int[numberOfSitesToConverge];
			// TODO time to randomize and get those indices.
			Random generator = new Random(System.currentTimeMillis());
			while(targetSet.size() < numberOfSitesToConverge){
				int possibleIndex = generator.nextInt(numberOfSites);
				if(!(invariantSitesIndices[possibleIndex] && targetSet.contains(possibleIndex))){
					targetSet.add(possibleIndex);
				}
			}
		}

		Iterator definedTargetSites = targetSet.iterator();
		for(int definedIndices=0; definedIndices<targetSitesIndices.length;definedIndices++){
			targetSitesIndices[definedIndices] = Integer.parseInt(definedTargetSites.next().toString());
			System.out.print(targetSitesIndices[definedIndices]+", ");
		}			
		System.out.println("\nStarting convergence");
		switch(alignmentSequenceCodingType){
			case AA:
				// TODO force AA convergence
				// TODO pay close attention to perl implementation
				for(String taxon:taxaListArray){
					char[] currentSequence = sequenceHash.remove(taxon);
					// Do stuff to it
					for(int index:targetSitesIndices){
						currentSequence[index] = masterSequence[index];
					}
					char[] newSequence = currentSequence;
					sequenceHash.put(taxon, newSequence);
				}
				break;
			case RNA:
				// TODO force RNA/DNA (e.g., codon) convergence
				// TODO pay close attention to perl implementation
				break;
			case DNA:
				// TODO force RNA/DNA (e.g., codon) convergence
				// TODO pay close attention to perl implementation
				break;
			default:
				break;
		}
	}
	
	public void printShortSequences(int printLimit){
		System.out.println("\nPrinting short sequence set:");
		for(String taxon:taxaList){
			String shortTaxon = truncatedNamesHash.get(taxon);
			char[] sequenceData = sequenceHash.get(taxon);
			if(sequenceData.length < printLimit){
				printLimit = sequenceData.length;
			}
			System.out.print(shortTaxon+"\t"+taxon+"\t");
			for(int i=0;i<printLimit;i++){
				System.out.print(sequenceData[i]);
			}
			System.out.println(" (truncated to "+printLimit+" sites)");
		}
	}
	
	/**
	 * 
	 * @param suppressErrors - controls verbose stdout commentary (TRUE = less verbose)
	 * @throws Exception
	 * @since 05/10/2011
	 */
	public void translate(boolean suppressErrors) throws Exception{
		/*
		 * 	
		 * U
		 * UUU 	(Phe/F) Phenylalanine 	
		 * UCU 	(Ser/S) Serine 	
		 * UAU 	(Tyr/Y) Tyrosine 	
		 * UGU 	(Cys/C) Cysteine
		 * UUC 	(Phe/F) Phenylalanine 	
		 * UCC 	(Ser/S) Serine 	
		 * UAC 	(Tyr/Y) Tyrosine 	
		 * UGC 	(Cys/C) Cysteine
		 * UUA 	(Leu/L) Leucine 	
		 * UCA 	(Ser/S) Serine 
		 * UAA 	Stop (Ochre) 
		 * UGA 	Stop (Opal)
		 * UUG 	(Leu/L) Leucine 
		 * UCG 	(Ser/S) Serine 
		 * UAG 	Stop (Amber) 
		 * UGG 	(Trp/W) Tryptophan    
		 * C 
		 * CUU 	(Leu/L) Leucine 
		 * CCU 	(Pro/P) Proline 
		 * CAU 	(His/H) Histidine 
		 * CGU 	(Arg/R) Arginine
		 * CUC 	(Leu/L) Leucine 
		 * CCC 	(Pro/P) Proline 
		 * CAC 	(His/H) Histidine 
		 * CGC 	(Arg/R) Arginine
		 * CUA 	(Leu/L) Leucine 
		 * CCA 	(Pro/P) Proline 
		 * CAA 	(Gln/Q) Glutamine 
		 * CGA 	(Arg/R) Arginine
		 * CUG 	(Leu/L) Leucine 
		 * CCG 	(Pro/P) Proline 
		 * CAG 	(Gln/Q) Glutamine 
		 * CGG 	(Arg/R) Arginine
		 * A
		 * AUU 	(Ile/I) Isoleucine 
		 * ACU 	(Thr/T) Threonine         
		 * AAU 	(Asn/N) Asparagine 
		 * AGU 	(Ser/S) Serine
		 * AUC 	(Ile/I) Isoleucine 
		 * ACC 	(Thr/T) Threonine 
		 * AAC 	(Asn/N) Asparagine 
		 * AGC 	(Ser/S) Serine
		 * AUA 	(Ile/I) Isoleucine 
		 * ACA 	(Thr/T) Threonine 
		 * AAA 	(Lys/K) Lysine 
		 * AGA 	(Arg/R) Arginine
		 * AUG[A] 	(Met/M) Methionine 
		 * ACG 	(Thr/T) Threonine 
		 * AAG 	(Lys/K) Lysine 
		 * AGG 	(Arg/R) Arginine
		 * G 
		 * GUU 	(Val/V) Valine 
		 * GCU 	(Ala/A) Alanine 
		 * GAU 	(Asp/D) Aspartic acid 
		 * GGU 	(Gly/G) Glycine
		 * GUC 	(Val/V) Valine 
		 * GCC 	(Ala/A) Alanine 
		 * GAC 	(Asp/D) Aspartic acid 
		 * GGC 	(Gly/G) Glycine
		 * GUA 	(Val/V) Valine 
		 * GCA 	(Ala/A) Alanine 
		 * GAA 	(Glu/E) Glutamic acid 
		 * GGA 	(Gly/G) Glycine
		 * GUG 	(Val/V) Valine 
		 * GCG 	(Ala/A) Alanine 
		 * GAG 	(Glu/E) Glutamic acid 
		 * GGG 	(Gly/G) Glycine
		 */
		translationLookup = new TreeMap<String,Character>();
		translationLookup.put("UUU",'F');
		translationLookup.put("UCU",'S');
		translationLookup.put("UAU",'Y');
		translationLookup.put("UGU",'C');
		translationLookup.put("UUC",'F');
		translationLookup.put("UCC",'S');
		translationLookup.put("UAC",'Y');
		translationLookup.put("UGC",'C');
		translationLookup.put("UUA",'L');
		translationLookup.put("UCA",'S');
		translationLookup.put("UAA",'*');
		translationLookup.put("UGA",'*');
		translationLookup.put("UUG",'L');
		translationLookup.put("UCG",'S');
		translationLookup.put("UAG",'*');
		translationLookup.put("UGG",'W');
		translationLookup.put("CUU",'L');
		translationLookup.put("CCU",'P');
		translationLookup.put("CAU",'H');
		translationLookup.put("CGU",'R');
		translationLookup.put("CUC",'L');
		translationLookup.put("CCC",'P');
		translationLookup.put("CAC",'H');
		translationLookup.put("CGC",'R');
		translationLookup.put("CUA",'L');
		translationLookup.put("CCA",'P');
		translationLookup.put("CAA",'Q');
		translationLookup.put("CGA",'R');
		translationLookup.put("CUG",'L');
		translationLookup.put("CCG",'P');
		translationLookup.put("CAG",'Q');
		translationLookup.put("CGG",'R');
		translationLookup.put("AUU",'I');
		translationLookup.put("ACU",'T');
		translationLookup.put("AAU",'N');
		translationLookup.put("AGU",'S');
		translationLookup.put("AUC",'I');
		translationLookup.put("ACC",'T');
		translationLookup.put("AAC",'N');
		translationLookup.put("AGC",'S');
		translationLookup.put("AUA",'I');
		translationLookup.put("ACA",'T');
		translationLookup.put("AAA",'K');
		translationLookup.put("AGA",'R');
		translationLookup.put("AUG",'M');
		translationLookup.put("ACG",'T');
		translationLookup.put("AAG",'K');
		translationLookup.put("AGG",'R');
		translationLookup.put("GUU",'V');
		translationLookup.put("GCU",'A');
		translationLookup.put("GAU",'D');
		translationLookup.put("GGU",'G');
		translationLookup.put("GUC",'V');
		translationLookup.put("GCC",'A');
		translationLookup.put("GAC",'D');
		translationLookup.put("GGC",'G');
		translationLookup.put("GUA",'V');
		translationLookup.put("GCA",'A');
		translationLookup.put("GAA",'E');
		translationLookup.put("GGA",'G');
		translationLookup.put("GUG",'V');
		translationLookup.put("GCG",'A');
		translationLookup.put("GAG",'E');
		translationLookup.put("GGG",'G');
		
//		System.out.println(numberOfSites+" sites constructed translation hash (size "+translationLookup.size()+")");
//		String testChar = "ACC";
//		System.out.println(translationLookup.get(testChar));
		
		if(alignmentSequenceCodingType.equals(SequenceCodingType.AA)){
			throw new Exception("This is already an AA sequence - cannot translate");
		}else{
			int newMaxNoSites = 0;
			for(String taxon:taxaListArray){
				char[] sequence = sequenceHash.remove(taxon);
				StringBuffer newAAseq = new StringBuffer();
				int numStopCodons = 0;
				int numAmbiguousCodons = 0;
				String codon = new String();
				char[] stringSequence = new StringWalker(sequence,'T','U').finishedSequence.toCharArray();
				char[] codonHolder = new char[3];
				char AA;
				for(int pos=0;pos < numberOfSites; pos += 3){
					AA = '-';
					codonHolder[0] = stringSequence[pos];
					try {
						codonHolder[1] = stringSequence[pos+1];
					} catch (ArrayIndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						System.out.println("Run out of sequence for 2nd codon position. Gap character (-) entered. Sequence position: "+pos);
						codonHolder[1] = '-';
					}
					try {
						codonHolder[2] = stringSequence[pos+2];
					} catch (ArrayIndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						System.out.println("Run out of sequence for 3rd codon positon. Gap character (-) entered. Sequence position: "+pos);
						codonHolder[2] = '-';
					}
					codon = new String(codonHolder);
//					System.out.println(codon.length()+" "+pos+" candidate codon: "+codon);
//					System.out.println("printing the AA, "+codon);
					if(!((codonHolder[0] == '-') && (codonHolder[1] == '-') && (codonHolder[2] == '-'))){
						try {
							AA = translationLookup.get(codon);
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							AA = '-';
							numAmbiguousCodons++;
							if(!suppressErrors){
								System.out.println(taxon+": unable to translate this codon. Missing data / gap character entered instead. (sequence position "+pos);
							}
						}
					}else{
						AA = '-';
						numAmbiguousCodons++;
						if(!suppressErrors){
							System.out.println(taxon+" ambiguous codon "+codon+"; position "+pos);
						}
					}
					if(AA == '*'){
						numStopCodons ++;
					}
					newAAseq.append(AA);
				}
				if(newAAseq.length() > newMaxNoSites){
					newMaxNoSites = newAAseq.length();
				}
				System.out.println(taxon+"\t\ttranslated.\tambig: "+numAmbiguousCodons+"\tstops: "+numStopCodons+"\tseq: "+newAAseq.substring(0, 4));
				sequenceHash.put(taxon, newAAseq.toString().toCharArray());
			}
			numberOfSites = newMaxNoSites;
			alignmentSequenceCodingType = SequenceCodingType.AA;
		}
	}
	
	/**
	 * @since 06/10/2011
	 */
	public void removeTaxa(String[] taxaToRemove){
		// TODO It strikes me that this is a FUCKING risky move with HashSets etc, since it probably means irreversibly deleting keys etc
		// TODO ... may be better to have an additional hideTaxa() method adding taxa to a String[] listToHide - then poll that when writing / processing etc..
		/*
		 * IMPLEMENTATION NOTE: 	w.r.t above comment - currently implemented permanent removeTaxa.
		 * 							partly this is because we expect PSR to be atomically created / destroyed from another caller.
		 */

		/*
		 * Implementation:
		 *  initialise tmpArrayList<String>
		 *  for(taxon: taxalist)
		 *  	if args.contains(taxon)
		 *  		sequenceHash.remove(taxon)
		 *  		truncatedNamesHash.remove(truncatedNamesHash)
		 *  	else
		 *  		tmpArrayList.add(taxon)
		 *  
		 *  int newNoTaxa = sequenceHash.size()
		 *  initialise new taxaNamesArray[newNoTaxa]
		 *  for (newTaxon:tmpArrayList)
		 *  	taxaNamesArray[i] = newTaxon
		 */
		ArrayList<String> tempTaxaList = new ArrayList<String>();
		HashSet<String> deleteList = new HashSet<String>();
		for(String dead: taxaToRemove){
			deleteList.add(dead);
		}
		for(String taxon:taxaListArray){
			if(deleteList.contains(taxon)){
				taxaList.remove(taxon);
				sequenceHash.remove(taxon);
				truncatedNamesHash.remove(taxon);
				System.out.println("removed taxon "+taxon);
				numberOfTaxa--;
			}else{
				tempTaxaList.add(taxon);
			}
		}
		assert(numberOfTaxa == sequenceHash.size());
		assert(numberOfTaxa == tempTaxaList.size());
		taxaListArray = new String[numberOfTaxa];
		// IMPLEMENTATION NOTE:		There is now NO GUARANTEE that taxaListArray and any of the Collections are in same ordering!
		Iterator nameItr = taxaList.iterator();
		for(int i=0;i<numberOfTaxa;i++){
			taxaListArray[i] = (String) nameItr.next();
		}
	}
	
	/**
	 * @since 06/10/2011
	 */
	public void removeUnambiguousGaps(){
		
		/*
		 * Implementation:
		 * 	initialise new maxNoSites = 0
		 * 	initialise new tmpSequenceHash
		 * 	initialise new boolean[] unambiguousGaps by polling (firstSeq(if char i eq  '-') && invariantSitesIndices)
		 *	foreach taxon: sequenceHash
		 *		char[] seq = sequenceHash.get(taxon)
		 *		initialise new StringBuilder
		 *		for(i in seq)
		 *			if NOT(unambiguousGap[i])
		 *				StringBuilder.append(seq[i)
		 *		tmpSequenceHash.put(taxon,StringBuilder.toCharArray)
		 *		update newMaxNoSites if needed
		 *	update noSites
		 *  this.sequenceHash = tmpSequenceHash
		 *  determineInvariantSites()
		 */
		int newNoSites = 0;
		boolean[] unambiguousGaps = new boolean[numberOfSites];
		char[] firstSeq = sequenceHash.get(sequenceHash.firstKey());
		for(int i=0;i<numberOfSites;i++){
			unambiguousGaps[i] = (invariantSitesIndices[i] && (firstSeq[i] == '-'));
		}
		for(String taxon:taxaListArray){
			char[] seqHolder = sequenceHash.get(taxon);
			StringBuilder newSeq = new StringBuilder();
			for(int j=0;j<numberOfSites;j++){
				if(!unambiguousGaps[j]){
					newSeq.append(seqHolder[j]);
				}
			}
			sequenceHash.remove(taxon);
			sequenceHash.put(taxon, newSeq.toString().toCharArray());
			if(newSeq.length()>newNoSites){
				newNoSites = newSeq.length();
			}
		}
		numberOfSites = newNoSites;
		this.determineInvariantSites();
	}
	
	public void removeStopCodons(){
		if(alignmentSequenceCodingType.equals(SequenceCodingType.AA)){
			for(String taxon:taxaListArray){
				char[] nonStopSequence = new StringWalker(sequenceHash.remove(taxon),'*','-').finishedSequence.toCharArray();
				sequenceHash.put(taxon, nonStopSequence);
			}
		}else{
			System.out.println("WARNING! This method is not implemented for DNA/RNA data. These codons have NOT been stripped.");
			// TODO Consider parsing DNA / RNA for stop codons. Bear in mind that introducing gaps to e.g. 3rd position codons may bias downstream dN/dS etc
		}
	}

	public void determineInvariantSites(){
		invariantSitesIndices = new boolean[numberOfSites];
		for(int i = 0; i<invariantSitesIndices.length;i++){
			invariantSitesIndices[i] = true;
		}
		
		assert(invariantSitesIndices[0]);
		assert(invariantSitesIndices[numberOfSites-1]);
		
		/*
		 * Define the first sequence
		 * Iterate through remaining sequences (well, may as well do all):
		 * 	if testTaxon[i] ne firstTaxon[i]; invariantSitesIndices[i] = false
		 */

		char[] firstSequence = sequenceHash.get(sequenceHash.firstKey());
		
		for(String taxon:taxaListArray){
			int index = 0;
			for(char testChar:sequenceHash.get(taxon)){
				if(!(testChar == firstSequence[index])){
					invariantSitesIndices[index] = false;
				}
				index++;
			}
		}
		
		for(boolean site:invariantSitesIndices){
			if(site){
				numberOfInvariantSites ++;
			}
		}
	}
	
	public ArrayList<String> getRawInput() {
		return rawInput;
	}

	/**
	 * @return the maximum numberOfSites in the data alignment
	 */
	public int getNumberOfSites() {
		return numberOfSites;
	}

	/**
	 * @return the numberOfTaxa
	 */
	public int getNumberOfTaxa() {
		return numberOfTaxa;
	}

	/**
	 * @return the taxaList (String ArrayList of untruncated taxa names.)
	 */
	public TreeSet<String> getTaxaList() {
		return taxaList;
	}

	/**
	 * @return the truncatedNamesHash
	 */
	public HashMap<String, String> getTruncatedNamesHash() {
		return truncatedNamesHash;
	}
}