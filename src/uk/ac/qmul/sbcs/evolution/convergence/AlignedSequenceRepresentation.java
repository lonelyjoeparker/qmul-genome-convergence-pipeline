package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Random;

import uk.ac.qmul.sbcs.evolution.convergence.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

/**
 * @author Joe Parker, <a href="http://code.google.com/a/eclipselabs.org/u/joeparkerandthemegahairymen/">Kitson Consulting Ltd / Queen Mary, University of London.</a>
 * @version 0.1
 * @mailto joe@kitson-consulting.co.uk
 * @since 09/19/2011
 * @param sequenceHash				:	The main data structure; String taxon name, char[] sequence data
 * @param numberOfSites				:	The MAXIMUM number of sites in the alignment; NOTE not implicitly safe for IndexOutOfBoundsExceptions
 * @param numberOfInvariantSites	:	The number of INVARIANT sites in the alignment; Same array indices warning as for numberOfSites
 * @param numberOfTaxa				:	The number of taxa in the alignment. *most* alignment operations *should* preserve this (but worth checking where practicable.)	
 * @param taxaList					:	An TreeSet<String> holding the taxon names. NB it would usually be better to use an Iterator to iterate through sequenceHash.
 * @param taxaListArray				:	A String[] of taxon names.
 * @param truncatedNamesHash		:	A HashMap<String fullTaxonName, String truncatedTaxonName> of the truncated taxon names. These are max 10 chars, with unique taxon IDs �999. Padded with underscores.
 * @param meanSitewiseEntropy		:	The average (mean) Shannon entropy over all sites in the alignment, using base-4 or base-21 logs for NT/codon or AA datatypes, respectively.
 * @param meanTaxonwiseLongestUngappedSequence	:	The average (mean) longest contiguous data sequence without gaps, over all taxa in the alignment.
 */
		
public class AlignedSequenceRepresentation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9134007401329133617L;
	public File file;
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
	private TreeMap<String,Character> translationLookup = new TreeMap<String, Character>();
	protected boolean[] invariantSitesIndices;
	private String[] transposedSites;
	private float meanSitewiseEntropy = Float.NaN;
	private float meanTaxonwiseLongestUngappedSequence = Float.NaN;
	private char[] basisCharsNucleotide 	= {'A','C','G','T','R','Y','S','W','K','M','B','D','H','V','N'};
	private char[] basisCharsNucleotideGap 	= {'A','C','G','T','R','Y','S','W','K','M','B','D','H','V','N','-'};
	private char[] basisCharsRNA		 	= {'A','C','G','U','R','Y','S','W','K','M','B','D','H','V','N'};
	private char[] basisCharsRNAGap		 	= {'A','C','G','U','R','Y','S','W','K','M','B','D','H','V','N','-'};
	private char[] basisCharsAminoAcid	 	= {'A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','Y','*'};
	private char[] basisCharsAminoAcidGap 	= {'A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','Y','*','-'};

	
	/**
	 * This is the default, and currently preferred constructor for an AlignedSequenceRepresentation from a file.
	 * <p>TODO In future a stable AlignedSequenceRepresentation(File file) constructor should be built and tested. 
	 * @author joeparker
	 * @since v0.0.1
	 */
	public AlignedSequenceRepresentation(){}
	
	/**
	 * This is the preferred AlignmentSequenceRepresentation constructor when <i>cloning</i> (i.e., copying) an alignment a donor AlignmentSequenceRepresentation via the <code>clone()</code> method.
	 * <p><b>This constructor is not tested for use by any other method or class</b>
	 * <p>numberOfTaxa, numberOfInvariantSites, numberOfSites, truncatedNamesHash and invariantSitesIndices are all calculated again using the buildBlah() family of methods.
	 * <p>rawInput, inputSequenceFileFormat, translationLookup, transposedSites will all be null or not instantiated
	 * <p>File file location is set as "parentFilename_clone"
	 * @author joeparker
	 * @since 0.0.1 r63
	 * @param data - TreeMap<String, char[]> sequenceHash from donor AlignmentSequenceRepresentation.
	 * @param taxa - TreeSet<String> taxaList from donor AlignmentSequenceRepresentation.
	 * @param taxaArray - String[] taxaListArray from donor AlignmentSequenceRepresentation.
	 * @param type - SequenceCodingType alignmentSequenceCodingType from donor AlignmentSequenceRepresentation.
	 * @param parent - File file from donor AlignmentSequenceRepresentation.
	 * @throws TaxaLimitException
	 */
	protected AlignedSequenceRepresentation(TreeMap<String, char[]> data, TreeSet<String> taxa, String[] taxaArray, SequenceCodingType type, File parent) throws TaxaLimitException{
		file = new File(parent.getAbsoluteFile()+"_clone");
		alignmentSequenceCodingType = type;
		taxaList = taxa;
		taxaListArray = taxaArray;
		sequenceHash = data;
		numberOfSites = this.buildNumberOfSites();
		numberOfTaxa = sequenceHash.size();
		truncatedNamesHash = this.buildTruncatedNamesHash();
		this.determineInvariantSites();
		rawInput = null;
		inputSequenceFileFormat = null;
		sequenceFileTypeSet = false;
		
	}
	
	@Deprecated
	/**
	 * This constructor is currently deprecated until such time as I get round to writing and testing a robust AlignedSequenceRepresentation(File input) constructor.
	 * @author joeparker
	 * @since 0.0.1 r63
	 */
	public AlignedSequenceRepresentation(File inputFile) {
		// TODO Auto-generated constructor stub
	}

	@Deprecated
	/**
	 * Method to perform a deep copy of an AlignedSequenceRepresentation. Not implemented.
	 */
	public AlignedSequenceRepresentation cloneDeep(){
		return null;
	}
	/**
	 * A private method to build the numberOfSites by iteration through data sequenceHash
	 * @return the numberOfSites
	 * @author joeparker
	 */
	private int buildNumberOfSites() {
		int maxSites = 0;
		// TODO Auto-generated method stub
		Iterator itr = sequenceHash.keySet().iterator();
		while(itr.hasNext()){
			String thisKey = itr.next().toString();
			char[] thisSeq = sequenceHash.get(thisKey);
			if(thisSeq.length>maxSites){
				maxSites = thisSeq.length;
			}
		}
		return maxSites;
	}

	/**
	 * A private method to build the truncatedNamesHash, intended for the AlignedSequenceRepresentation(TreeMap<String, char[]> data, TreeSet<String> taxa, String[] taxaArray, SequenceCodingType type, File parent) constructor <b><ONLY/b>
	 * <p>Pasted from the identical operation in the loadSequences() method.
	 * TODO Should probably test to see if this method can be used in loadSequences() in fact, for robustness. This is not a priority now however (18/01/2012)
	 * @author joeparker
	 * @return the newly-built truncatedNamesHash
	 * @throws TaxaLimitException
	 */
	private HashMap<String, String> buildTruncatedNamesHash() throws TaxaLimitException {
		HashMap<String, String> newTnameHash = new HashMap<String,String>(); 	// A local hash to hold the truncated names hash for now..
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
				newTnameHash.put(longTaxon, shortTaxon.toString());
			}
			UIDroot++;
		}
		return newTnameHash;
	}

	public void loadSequences(File inputFile, boolean reportInputRead) throws TaxaLimitException{
		file = inputFile;
		if(!file.canRead()){System.out.println("SERIOUS: cannot find input file "+file.getAbsolutePath());}
		try{
			rawInput = new CapitalisedFileReader().loadSequences(file,reportInputRead);
			for(String line:rawInput){
				if(line.length() == 0){System.out.println("read: ["+line+"]");}
			}
			assert(rawInput.size()>0);
			this.determineInputFileDatatype();
			assert(sequenceFileTypeSet);
			AlignmentParser parser;
			switch(inputSequenceFileFormat){
				case NEXUS: 
					//this.readNexusFile(); // old function call, now using AlignmentParser concrete subclasses
					parser = new NexusParser();
					if(parser.parseInput(rawInput)){
						// do nothing
						System.out.println();
						this.extractParsedInformation(parser);
					} 
					break;
				case FASTA: 
					//this.readFastaFile(); // old function call, now using AlignmentParser concrete subclasses
					parser = new FastaParser();
					if(parser.parseInput(rawInput)){
						// do nothing
						System.out.println();
						this.extractParsedInformation(parser);
					} 
					break;
				case PHYLIP: 
					//this.readPhylipFile();// old function call, now using AlignmentParser concrete subclasses
					parser = new PhylipParser();
					if(parser.parseInput(rawInput)){
						// do nothing
						System.out.println();
						this.extractParsedInformation(parser);
					} 
					break;
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
		this.padSequences();
		this.determineInvariantSites();
	}
	
	/**
	 * This is a method to pad any sequences that have fewer chars than required to full length.
	 * <br/>The aim is to try and avoid ArrayIndexOutOfBoundsException()s...
	 */
	private void padSequences() {
		Iterator seqItr = sequenceHash.keySet().iterator();
		while(seqItr.hasNext()){
			String seq = (String)seqItr.next();
			int deficit;
			if(sequenceHash.get(seq).length<this.numberOfSites){
				char[] deficitSeq = sequenceHash.get(seq);		//Remove the short sequence
				deficit = deficitSeq.length - this.numberOfSites;	//How far short is it?
				int replacements = 0;
				char[] paddedSeq = new char[this.numberOfSites];	//A new sequence the right length
				for(int i=0;i<this.numberOfSites;i++){
					if(i<deficitSeq.length){
						//The deficit seq has a char here so populate the padded seq with that
						paddedSeq[i] = deficitSeq[i];
					}else{
						//The deficit seq doesn't have a char here (e.g. it is short) so pad with a gap.
						paddedSeq[i] = '-';
						replacements++;
					}
				}
				assert(deficit+replacements == 0);						//We should have replaced as many chars as we are short by
				sequenceHash.put(seq,paddedSeq);					//Replace the padded sequence
			}
		}
	}

	/**
	 * Takes a parser of AlignmentParser supertype and get aligment information from it to populate instance variables
	 * @param parser
	 */
	private void extractParsedInformation(AlignmentParser parser){
		this.numberOfSites = parser.getNumberOfSites();
		this.numberOfTaxa = parser.getNumberOfTaxa();
		this.sequenceHash = parser.getSequenceHash();
		this.taxaList = parser.getTaxaList();
		this.taxaListArray = parser.getTaxaListArray();
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
	
	/**
	 * @version 0.0.2
	 * @since 31/10/2011
	 * 
	 * Added methods to parse lines that include spaces within sequence lines.
	 * NOTE that this assumes rawInput has NO blank lines (before data, at least).
	 */
	public void readPhylipFile(){
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
	}

	/**
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
	public void readFastaFile(){
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

	/**
	 * 
	 * @param fullyPathQualifiedFileName - name of the whole
	 * @param useOriginalTaxonNames - or the truncated sequence names hash (first 6 chars of original name + underscore + 3-digit padded UID)
	 */
	public void writePhylipFile(File fullyPathQualifiedFileName, boolean useOriginalTaxonNames){
		StringBuffer buffer = new StringBuffer();
		buffer.append(taxaListArray.length+"   "+numberOfSites+"\n");
		/*
		 * We have to make sure the names are padded with enough space that (a) the sequences all line up, and (b) whitespace delimits names and sequences
		 * First we find the maximum taxon name length - we will use this (+2) as the pad limit
		 */
		int max_name_length = 0; // Longest sequence name
		/* 31/10/2015: different length check depending on whether we're writing the original names or truncated names hash */
		// first decide which array of taxon names to examine
		String[] taxonNamesForLengthCheck;
		if(useOriginalTaxonNames){
			// using original (imported) names
			taxonNamesForLengthCheck = taxaListArray;
		}else{
			// using truncated names from truncatedNamesHash.values
			taxonNamesForLengthCheck = truncatedNamesHash.values().toArray(new String[truncatedNamesHash.size()]);
		}
		// now examine each name in the chosen array to get max length
		for(String taxon:taxonNamesForLengthCheck){
			if(taxon.length()>max_name_length){max_name_length = taxon.length();}
		}
		for(String taxon:taxaListArray){
			StringBuilder paddedTaxon;
			if(useOriginalTaxonNames){
				paddedTaxon = new StringBuilder(taxon);
			}else{
				paddedTaxon = new StringBuilder(truncatedNamesHash.get(taxon));
			}
			while(paddedTaxon.length() < (max_name_length+5)){
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
	 * 	Purpose:	constrain a subset of taxa to have the same AA
	 *	<p>execution:	
	 * <pre>for i in num sites{
	 *	random indices
	 *	get target master taxon as char array /// NB: codon triplets
	 *	read array[indices]
	 *	replace
	 *}</pre> 
	 * 
	 * @param masterTaxon - The taxon that is being treated as the master. Sites will be converged to this taxon's sequence randomly
	 * @param taxaToForce - The taxa that in which convergence is to be simulated.
	 * @param numberOfSitesToConverge - The number of sites to be forced to converge.
	 * @throws TaxaAbsentFromAlignmentException  - In cases where the taxaToForce are not present in the alignment (check case of taxa?) 
	 * @throws VariantSitesUnavailableException 
	 */
	public void simulateConvergenceInVariantSites(String masterTaxon, String[] taxaToForce, int numberOfSitesToConverge) throws TaxaAbsentFromAlignmentException, VariantSitesUnavailableException{
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
			throw new VariantSitesUnavailableException();
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
				for(String taxon:taxaToForce){
					if(taxaList.contains(taxon)){
						char[] currentSequence = sequenceHash.remove(taxon);
						// Do stuff to it
						for(int index:targetSitesIndices){
							currentSequence[index] = masterSequence[index];
						}
						char[] newSequence = currentSequence;
						sequenceHash.put(taxon, newSequence);
					}else{
						throw new TaxaAbsentFromAlignmentException();
					}
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
			case CODON:
				// TODO this is not implemented... 
				// TODO urrrrrrrk
			default:
				break;
		}
	}
	
	/**
	 * Initialise the translation hash (if it isn't instantiated already). Any codons not listed here will be translated as a gap '-' character.
	 * @since 21/08/2012
	 * @see translate(), removeStopCodonsInDNA()
	 */
	public void initialiseTranslationHash(){
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
		if(translationLookup.size()<2){
			translationLookup = new TreeMap<String,Character>();
			/*
			 * Ambiguity characters (ONLY SOME OF (4-fold 3rd position): TODO include all)
			 */
			translationLookup.put("NNN",'X');
			translationLookup.put("UCN",'S');
			translationLookup.put("CCN",'P');
			translationLookup.put("CGN",'R');
			translationLookup.put("CUN",'L');
			translationLookup.put("ACN",'T');
			translationLookup.put("GUN",'V');
			translationLookup.put("GGN",'G');
			translationLookup.put("GCN",'A');
			/*
			 * Orthodox / fully-specified characters
			 */
			translationLookup.put("UUU",'F');
			translationLookup.put("UUC",'F');
			translationLookup.put("UUA",'L');
			translationLookup.put("UUG",'L');
			translationLookup.put("UGU",'C');
			translationLookup.put("UGC",'C');
			translationLookup.put("UAU",'Y');
			translationLookup.put("UAC",'Y');
			translationLookup.put("UCU",'S');
			translationLookup.put("UCC",'S');
			translationLookup.put("UCA",'S');
			translationLookup.put("UCG",'S');
			translationLookup.put("UAA",'*');
			translationLookup.put("UGA",'*');
			translationLookup.put("UAG",'*');
			translationLookup.put("UGG",'W');

			translationLookup.put("CUU",'L');
			translationLookup.put("CUC",'L');
			translationLookup.put("CUA",'L');
			translationLookup.put("CUG",'L');
			translationLookup.put("CCU",'P');
			translationLookup.put("CCC",'P');
			translationLookup.put("CCA",'P');
			translationLookup.put("CCG",'P');
			translationLookup.put("CGU",'R');
			translationLookup.put("CGC",'R');
			translationLookup.put("CGA",'R');
			translationLookup.put("CGG",'R');
			translationLookup.put("CAA",'Q');
			translationLookup.put("CAG",'Q');
			translationLookup.put("CAU",'H');
			translationLookup.put("CAC",'H');

			translationLookup.put("AUU",'I');
			translationLookup.put("AUC",'I');
			translationLookup.put("AUA",'I');
			translationLookup.put("AUG",'M');
			translationLookup.put("ACU",'T');
			translationLookup.put("ACC",'T');
			translationLookup.put("ACA",'T');
			translationLookup.put("ACG",'T');
			translationLookup.put("AAU",'N');
			translationLookup.put("AAC",'N');
			translationLookup.put("AAA",'K');
			translationLookup.put("AAG",'K');
			translationLookup.put("AGA",'R');
			translationLookup.put("AGG",'R');
			translationLookup.put("AGC",'S');
			translationLookup.put("AGU",'S');

			translationLookup.put("GUU",'V');
			translationLookup.put("GUC",'V');
			translationLookup.put("GUA",'V');
			translationLookup.put("GUG",'V');
			translationLookup.put("GGU",'G');
			translationLookup.put("GGC",'G');
			translationLookup.put("GGA",'G');
			translationLookup.put("GGG",'G');
			translationLookup.put("GCU",'A');
			translationLookup.put("GCC",'A');
			translationLookup.put("GCA",'A');
			translationLookup.put("GCG",'A');
			translationLookup.put("GAC",'D');
			translationLookup.put("GAU",'D');
			translationLookup.put("GAA",'E');
			translationLookup.put("GAG",'E');
		}
	}

	/**
	 * Initialises the 'MEGA expanded' translation hash (includes all ambiguity characters with unique expansions). Some of these codons include gaps - this is unorthodox behaviour.
	 * Any codons not listed here will be translated as a gap '-' character.
	 */
	public void initialiseExpandedTranslationHash(){
		if(translationLookup.size()<2){
			translationLookup = new TreeMap<String,Character>();

			translationLookup.put("AAA",'K');
			translationLookup.put("AAC",'N');
			translationLookup.put("AAG",'K');
			translationLookup.put("AAU",'N');
			translationLookup.put("AAR",'K');
			translationLookup.put("AAY",'N');
			translationLookup.put("AAR",'K');
			translationLookup.put("AA-",'X');
			translationLookup.put("ACA",'T');
			translationLookup.put("ACC",'T');
			translationLookup.put("ACG",'T');
			translationLookup.put("ACU",'T');
			translationLookup.put("ACR",'T');
			translationLookup.put("ACY",'T');
			translationLookup.put("ACR",'T');
			translationLookup.put("ACM",'T');
			translationLookup.put("ACK",'T');
			translationLookup.put("ACS",'T');
			translationLookup.put("ACW",'T');
			translationLookup.put("ACH",'T');
			translationLookup.put("ACB",'T');
			translationLookup.put("ACV",'T');
			translationLookup.put("ACD",'T');
			translationLookup.put("ACN",'T');
			translationLookup.put("AC-",'X');
			translationLookup.put("AGA",'R');
			translationLookup.put("AGC",'S');
			translationLookup.put("AGG",'R');
			translationLookup.put("AGU",'S');
			translationLookup.put("AGR",'R');
			translationLookup.put("AGY",'S');
			translationLookup.put("AGR",'R');
			translationLookup.put("AG-",'X');
			translationLookup.put("AUA",'I');
			translationLookup.put("AUC",'I');
			translationLookup.put("AUG",'M');
			translationLookup.put("AUU",'I');
			translationLookup.put("AUY",'I');
			translationLookup.put("AUM",'I');
			translationLookup.put("AUW",'I');
			translationLookup.put("AUH",'I');
			translationLookup.put("AU-",'X');
			translationLookup.put("AR-",'X');
			translationLookup.put("AY-",'X');
			translationLookup.put("AR-",'X');
			translationLookup.put("AM-",'X');
			translationLookup.put("AK-",'X');
			translationLookup.put("AS-",'X');
			translationLookup.put("AW-",'X');
			translationLookup.put("AH-",'X');
			translationLookup.put("AB-",'X');
			translationLookup.put("AV-",'X');
			translationLookup.put("AD-",'X');
			translationLookup.put("AN-",'X');
			translationLookup.put("A-A",'X');
			translationLookup.put("A-C",'X');
			translationLookup.put("A-G",'X');
			translationLookup.put("A-U",'X');
			translationLookup.put("A-R",'X');
			translationLookup.put("A-Y",'X');
			translationLookup.put("A-R",'X');
			translationLookup.put("A-M",'X');
			translationLookup.put("A-K",'X');
			translationLookup.put("A-S",'X');
			translationLookup.put("A-W",'X');
			translationLookup.put("A-H",'X');
			translationLookup.put("A-B",'X');
			translationLookup.put("A-V",'X');
			translationLookup.put("A-D",'X');
			translationLookup.put("A-N",'X');
			translationLookup.put("A--",'X');
			translationLookup.put("CAA",'Q');
			translationLookup.put("CAC",'H');
			translationLookup.put("CAG",'Q');
			translationLookup.put("CAU",'H');
			translationLookup.put("CAR",'Q');
			translationLookup.put("CAY",'H');
			translationLookup.put("CAR",'Q');
			translationLookup.put("CA-",'X');
			translationLookup.put("CCA",'P');
			translationLookup.put("CCC",'P');
			translationLookup.put("CCG",'P');
			translationLookup.put("CCU",'P');
			translationLookup.put("CCR",'P');
			translationLookup.put("CCY",'P');
			translationLookup.put("CCR",'P');
			translationLookup.put("CCM",'P');
			translationLookup.put("CCK",'P');
			translationLookup.put("CCS",'P');
			translationLookup.put("CCW",'P');
			translationLookup.put("CCH",'P');
			translationLookup.put("CCB",'P');
			translationLookup.put("CCV",'P');
			translationLookup.put("CCD",'P');
			translationLookup.put("CCN",'P');
			translationLookup.put("CC-",'X');
			translationLookup.put("CGA",'R');
			translationLookup.put("CGC",'R');
			translationLookup.put("CGG",'R');
			translationLookup.put("CGU",'R');
			translationLookup.put("CGR",'R');
			translationLookup.put("CGY",'R');
			translationLookup.put("CGR",'R');
			translationLookup.put("CGM",'R');
			translationLookup.put("CGK",'R');
			translationLookup.put("CGS",'R');
			translationLookup.put("CGW",'R');
			translationLookup.put("CGH",'R');
			translationLookup.put("CGB",'R');
			translationLookup.put("CGV",'R');
			translationLookup.put("CGD",'R');
			translationLookup.put("CGN",'R');
			translationLookup.put("CG-",'X');
			translationLookup.put("CUA",'L');
			translationLookup.put("CUC",'L');
			translationLookup.put("CUG",'L');
			translationLookup.put("CUU",'L');
			translationLookup.put("CUR",'L');
			translationLookup.put("CUY",'L');
			translationLookup.put("CUR",'L');
			translationLookup.put("CUM",'L');
			translationLookup.put("CUK",'L');
			translationLookup.put("CUS",'L');
			translationLookup.put("CUW",'L');
			translationLookup.put("CUH",'L');
			translationLookup.put("CUB",'L');
			translationLookup.put("CUV",'L');
			translationLookup.put("CUD",'L');
			translationLookup.put("CUN",'L');
			translationLookup.put("CU-",'X');
			translationLookup.put("CR-",'X');
			translationLookup.put("CY-",'X');
			translationLookup.put("CR-",'X');
			translationLookup.put("CM-",'X');
			translationLookup.put("CK-",'X');
			translationLookup.put("CS-",'X');
			translationLookup.put("CW-",'X');
			translationLookup.put("CH-",'X');
			translationLookup.put("CB-",'X');
			translationLookup.put("CV-",'X');
			translationLookup.put("CD-",'X');
			translationLookup.put("CN-",'X');
			translationLookup.put("C-A",'X');
			translationLookup.put("C-C",'X');
			translationLookup.put("C-G",'X');
			translationLookup.put("C-U",'X');
			translationLookup.put("C-R",'X');
			translationLookup.put("C-Y",'X');
			translationLookup.put("C-R",'X');
			translationLookup.put("C-M",'X');
			translationLookup.put("C-K",'X');
			translationLookup.put("C-S",'X');
			translationLookup.put("C-W",'X');
			translationLookup.put("C-H",'X');
			translationLookup.put("C-B",'X');
			translationLookup.put("C-V",'X');
			translationLookup.put("C-D",'X');
			translationLookup.put("C-N",'X');
			translationLookup.put("C--",'X');
			translationLookup.put("GAA",'E');
			translationLookup.put("GAC",'D');
			translationLookup.put("GAG",'E');
			translationLookup.put("GAU",'D');
			translationLookup.put("GAR",'E');
			translationLookup.put("GAY",'D');
			translationLookup.put("GAR",'E');
			translationLookup.put("GA-",'X');
			translationLookup.put("GCA",'A');
			translationLookup.put("GCC",'A');
			translationLookup.put("GCG",'A');
			translationLookup.put("GCU",'A');
			translationLookup.put("GCR",'A');
			translationLookup.put("GCY",'A');
			translationLookup.put("GCR",'A');
			translationLookup.put("GCM",'A');
			translationLookup.put("GCK",'A');
			translationLookup.put("GCS",'A');
			translationLookup.put("GCW",'A');
			translationLookup.put("GCH",'A');
			translationLookup.put("GCB",'A');
			translationLookup.put("GCV",'A');
			translationLookup.put("GCD",'A');
			translationLookup.put("GCN",'A');
			translationLookup.put("GC-",'X');
			translationLookup.put("GGA",'G');
			translationLookup.put("GGC",'G');
			translationLookup.put("GGG",'G');
			translationLookup.put("GGU",'G');
			translationLookup.put("GGR",'G');
			translationLookup.put("GGY",'G');
			translationLookup.put("GGR",'G');
			translationLookup.put("GGM",'G');
			translationLookup.put("GGK",'G');
			translationLookup.put("GGS",'G');
			translationLookup.put("GGW",'G');
			translationLookup.put("GGH",'G');
			translationLookup.put("GGB",'G');
			translationLookup.put("GGV",'G');
			translationLookup.put("GGD",'G');
			translationLookup.put("GGN",'G');
			translationLookup.put("GG-",'X');
			translationLookup.put("GUA",'V');
			translationLookup.put("GUC",'V');
			translationLookup.put("GUG",'V');
			translationLookup.put("GUU",'V');
			translationLookup.put("GUR",'V');
			translationLookup.put("GUY",'V');
			translationLookup.put("GUR",'V');
			translationLookup.put("GUM",'V');
			translationLookup.put("GUK",'V');
			translationLookup.put("GUS",'V');
			translationLookup.put("GUW",'V');
			translationLookup.put("GUH",'V');
			translationLookup.put("GUB",'V');
			translationLookup.put("GUV",'V');
			translationLookup.put("GUD",'V');
			translationLookup.put("GUN",'V');
			translationLookup.put("GU-",'X');
			translationLookup.put("GR-",'X');
			translationLookup.put("GY-",'X');
			translationLookup.put("GR-",'X');
			translationLookup.put("GM-",'X');
			translationLookup.put("GK-",'X');
			translationLookup.put("GS-",'X');
			translationLookup.put("GW-",'X');
			translationLookup.put("GH-",'X');
			translationLookup.put("GB-",'X');
			translationLookup.put("GV-",'X');
			translationLookup.put("GD-",'X');
			translationLookup.put("GN-",'X');
			translationLookup.put("G-A",'X');
			translationLookup.put("G-C",'X');
			translationLookup.put("G-G",'X');
			translationLookup.put("G-U",'X');
			translationLookup.put("G-R",'X');
			translationLookup.put("G-Y",'X');
			translationLookup.put("G-R",'X');
			translationLookup.put("G-M",'X');
			translationLookup.put("G-K",'X');
			translationLookup.put("G-S",'X');
			translationLookup.put("G-W",'X');
			translationLookup.put("G-H",'X');
			translationLookup.put("G-B",'X');
			translationLookup.put("G-V",'X');
			translationLookup.put("G-D",'X');
			translationLookup.put("G-N",'X');
			translationLookup.put("G--",'X');
			translationLookup.put("UAA",'*');
			translationLookup.put("UAC",'Y');
			translationLookup.put("UAG",'*');
			translationLookup.put("UAU",'Y');
			translationLookup.put("UAR",'*');
			translationLookup.put("UAY",'Y');
			translationLookup.put("UAR",'*');
			translationLookup.put("UA-",'X');
			translationLookup.put("UCA",'S');
			translationLookup.put("UCC",'S');
			translationLookup.put("UCG",'S');
			translationLookup.put("UCU",'S');
			translationLookup.put("UCR",'S');
			translationLookup.put("UCY",'S');
			translationLookup.put("UCR",'S');
			translationLookup.put("UCM",'S');
			translationLookup.put("UCK",'S');
			translationLookup.put("UCS",'S');
			translationLookup.put("UCW",'S');
			translationLookup.put("UCH",'S');
			translationLookup.put("UCB",'S');
			translationLookup.put("UCV",'S');
			translationLookup.put("UCD",'S');
			translationLookup.put("UCN",'S');
			translationLookup.put("UC-",'X');
			translationLookup.put("UGA",'*');
			translationLookup.put("UGC",'C');
			translationLookup.put("UGG",'W');
			translationLookup.put("UGU",'C');
			translationLookup.put("UGY",'C');
			translationLookup.put("UG-",'X');
			translationLookup.put("UUA",'L');
			translationLookup.put("UUC",'F');
			translationLookup.put("UUG",'L');
			translationLookup.put("UUU",'F');
			translationLookup.put("UUR",'L');
			translationLookup.put("UUY",'F');
			translationLookup.put("UUR",'L');
			translationLookup.put("UU-",'X');
			translationLookup.put("URA",'*');
			translationLookup.put("UR-",'X');
			translationLookup.put("UY-",'X');
			translationLookup.put("URA",'*');
			translationLookup.put("UR-",'X');
			translationLookup.put("UM-",'X');
			translationLookup.put("UK-",'X');
			translationLookup.put("US-",'X');
			translationLookup.put("UW-",'X');
			translationLookup.put("UH-",'X');
			translationLookup.put("UB-",'X');
			translationLookup.put("UV-",'X');
			translationLookup.put("UD-",'X');
			translationLookup.put("UN-",'X');
			translationLookup.put("U-A",'X');
			translationLookup.put("U-C",'X');
			translationLookup.put("U-G",'X');
			translationLookup.put("U-U",'X');
			translationLookup.put("U-R",'X');
			translationLookup.put("U-Y",'X');
			translationLookup.put("U-R",'X');
			translationLookup.put("U-M",'X');
			translationLookup.put("U-K",'X');
			translationLookup.put("U-S",'X');
			translationLookup.put("U-W",'X');
			translationLookup.put("U-H",'X');
			translationLookup.put("U-B",'X');
			translationLookup.put("U-V",'X');
			translationLookup.put("U-D",'X');
			translationLookup.put("U-N",'X');
			translationLookup.put("U--",'X');
			translationLookup.put("RA-",'X');
			translationLookup.put("RC-",'X');
			translationLookup.put("RG-",'X');
			translationLookup.put("RU-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RY-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RM-",'X');
			translationLookup.put("RK-",'X');
			translationLookup.put("RS-",'X');
			translationLookup.put("RW-",'X');
			translationLookup.put("RH-",'X');
			translationLookup.put("RB-",'X');
			translationLookup.put("RV-",'X');
			translationLookup.put("RD-",'X');
			translationLookup.put("RN-",'X');
			translationLookup.put("R-A",'X');
			translationLookup.put("R-C",'X');
			translationLookup.put("R-G",'X');
			translationLookup.put("R-U",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-Y",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-M",'X');
			translationLookup.put("R-K",'X');
			translationLookup.put("R-S",'X');
			translationLookup.put("R-W",'X');
			translationLookup.put("R-H",'X');
			translationLookup.put("R-B",'X');
			translationLookup.put("R-V",'X');
			translationLookup.put("R-D",'X');
			translationLookup.put("R-N",'X');
			translationLookup.put("R--",'X');
			translationLookup.put("YA-",'X');
			translationLookup.put("YC-",'X');
			translationLookup.put("YG-",'X');
			translationLookup.put("YUA",'L');
			translationLookup.put("YUG",'L');
			translationLookup.put("YUR",'L');
			translationLookup.put("YUR",'L');
			translationLookup.put("YU-",'X');
			translationLookup.put("YR-",'X');
			translationLookup.put("YY-",'X');
			translationLookup.put("YR-",'X');
			translationLookup.put("YM-",'X');
			translationLookup.put("YK-",'X');
			translationLookup.put("YS-",'X');
			translationLookup.put("YW-",'X');
			translationLookup.put("YH-",'X');
			translationLookup.put("YB-",'X');
			translationLookup.put("YV-",'X');
			translationLookup.put("YD-",'X');
			translationLookup.put("YN-",'X');
			translationLookup.put("Y-A",'X');
			translationLookup.put("Y-C",'X');
			translationLookup.put("Y-G",'X');
			translationLookup.put("Y-U",'X');
			translationLookup.put("Y-R",'X');
			translationLookup.put("Y-Y",'X');
			translationLookup.put("Y-R",'X');
			translationLookup.put("Y-M",'X');
			translationLookup.put("Y-K",'X');
			translationLookup.put("Y-S",'X');
			translationLookup.put("Y-W",'X');
			translationLookup.put("Y-H",'X');
			translationLookup.put("Y-B",'X');
			translationLookup.put("Y-V",'X');
			translationLookup.put("Y-D",'X');
			translationLookup.put("Y-N",'X');
			translationLookup.put("Y--",'X');
			translationLookup.put("RA-",'X');
			translationLookup.put("RC-",'X');
			translationLookup.put("RG-",'X');
			translationLookup.put("RU-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RY-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RM-",'X');
			translationLookup.put("RK-",'X');
			translationLookup.put("RS-",'X');
			translationLookup.put("RW-",'X');
			translationLookup.put("RH-",'X');
			translationLookup.put("RB-",'X');
			translationLookup.put("RV-",'X');
			translationLookup.put("RD-",'X');
			translationLookup.put("RN-",'X');
			translationLookup.put("R-A",'X');
			translationLookup.put("R-C",'X');
			translationLookup.put("R-G",'X');
			translationLookup.put("R-U",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-Y",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-M",'X');
			translationLookup.put("R-K",'X');
			translationLookup.put("R-S",'X');
			translationLookup.put("R-W",'X');
			translationLookup.put("R-H",'X');
			translationLookup.put("R-B",'X');
			translationLookup.put("R-V",'X');
			translationLookup.put("R-D",'X');
			translationLookup.put("R-N",'X');
			translationLookup.put("R--",'X');
			translationLookup.put("MA-",'X');
			translationLookup.put("MC-",'X');
			translationLookup.put("MGA",'R');
			translationLookup.put("MGG",'R');
			translationLookup.put("MGR",'R');
			translationLookup.put("MGR",'R');
			translationLookup.put("MG-",'X');
			translationLookup.put("MU-",'X');
			translationLookup.put("MR-",'X');
			translationLookup.put("MY-",'X');
			translationLookup.put("MR-",'X');
			translationLookup.put("MM-",'X');
			translationLookup.put("MK-",'X');
			translationLookup.put("MS-",'X');
			translationLookup.put("MW-",'X');
			translationLookup.put("MH-",'X');
			translationLookup.put("MB-",'X');
			translationLookup.put("MV-",'X');
			translationLookup.put("MD-",'X');
			translationLookup.put("MN-",'X');
			translationLookup.put("M-A",'X');
			translationLookup.put("M-C",'X');
			translationLookup.put("M-G",'X');
			translationLookup.put("M-U",'X');
			translationLookup.put("M-R",'X');
			translationLookup.put("M-Y",'X');
			translationLookup.put("M-R",'X');
			translationLookup.put("M-M",'X');
			translationLookup.put("M-K",'X');
			translationLookup.put("M-S",'X');
			translationLookup.put("M-W",'X');
			translationLookup.put("M-H",'X');
			translationLookup.put("M-B",'X');
			translationLookup.put("M-V",'X');
			translationLookup.put("M-D",'X');
			translationLookup.put("M-N",'X');
			translationLookup.put("M--",'X');
			translationLookup.put("KA-",'X');
			translationLookup.put("KC-",'X');
			translationLookup.put("KG-",'X');
			translationLookup.put("KU-",'X');
			translationLookup.put("KR-",'X');
			translationLookup.put("KY-",'X');
			translationLookup.put("KR-",'X');
			translationLookup.put("KM-",'X');
			translationLookup.put("KK-",'X');
			translationLookup.put("KS-",'X');
			translationLookup.put("KW-",'X');
			translationLookup.put("KH-",'X');
			translationLookup.put("KB-",'X');
			translationLookup.put("KV-",'X');
			translationLookup.put("KD-",'X');
			translationLookup.put("KN-",'X');
			translationLookup.put("K-A",'X');
			translationLookup.put("K-C",'X');
			translationLookup.put("K-G",'X');
			translationLookup.put("K-U",'X');
			translationLookup.put("K-R",'X');
			translationLookup.put("K-Y",'X');
			translationLookup.put("K-R",'X');
			translationLookup.put("K-M",'X');
			translationLookup.put("K-K",'X');
			translationLookup.put("K-S",'X');
			translationLookup.put("K-W",'X');
			translationLookup.put("K-H",'X');
			translationLookup.put("K-B",'X');
			translationLookup.put("K-V",'X');
			translationLookup.put("K-D",'X');
			translationLookup.put("K-N",'X');
			translationLookup.put("K--",'X');
			translationLookup.put("SA-",'X');
			translationLookup.put("SC-",'X');
			translationLookup.put("SG-",'X');
			translationLookup.put("SU-",'X');
			translationLookup.put("SR-",'X');
			translationLookup.put("SY-",'X');
			translationLookup.put("SR-",'X');
			translationLookup.put("SM-",'X');
			translationLookup.put("SK-",'X');
			translationLookup.put("SS-",'X');
			translationLookup.put("SW-",'X');
			translationLookup.put("SH-",'X');
			translationLookup.put("SB-",'X');
			translationLookup.put("SV-",'X');
			translationLookup.put("SD-",'X');
			translationLookup.put("SN-",'X');
			translationLookup.put("S-A",'X');
			translationLookup.put("S-C",'X');
			translationLookup.put("S-G",'X');
			translationLookup.put("S-U",'X');
			translationLookup.put("S-R",'X');
			translationLookup.put("S-Y",'X');
			translationLookup.put("S-R",'X');
			translationLookup.put("S-M",'X');
			translationLookup.put("S-K",'X');
			translationLookup.put("S-S",'X');
			translationLookup.put("S-W",'X');
			translationLookup.put("S-H",'X');
			translationLookup.put("S-B",'X');
			translationLookup.put("S-V",'X');
			translationLookup.put("S-D",'X');
			translationLookup.put("S-N",'X');
			translationLookup.put("S--",'X');
			translationLookup.put("WA-",'X');
			translationLookup.put("WC-",'X');
			translationLookup.put("WG-",'X');
			translationLookup.put("WU-",'X');
			translationLookup.put("WR-",'X');
			translationLookup.put("WY-",'X');
			translationLookup.put("WR-",'X');
			translationLookup.put("WM-",'X');
			translationLookup.put("WK-",'X');
			translationLookup.put("WS-",'X');
			translationLookup.put("WW-",'X');
			translationLookup.put("WH-",'X');
			translationLookup.put("WB-",'X');
			translationLookup.put("WV-",'X');
			translationLookup.put("WD-",'X');
			translationLookup.put("WN-",'X');
			translationLookup.put("W-A",'X');
			translationLookup.put("W-C",'X');
			translationLookup.put("W-G",'X');
			translationLookup.put("W-U",'X');
			translationLookup.put("W-R",'X');
			translationLookup.put("W-Y",'X');
			translationLookup.put("W-R",'X');
			translationLookup.put("W-M",'X');
			translationLookup.put("W-K",'X');
			translationLookup.put("W-S",'X');
			translationLookup.put("W-W",'X');
			translationLookup.put("W-H",'X');
			translationLookup.put("W-B",'X');
			translationLookup.put("W-V",'X');
			translationLookup.put("W-D",'X');
			translationLookup.put("W-N",'X');
			translationLookup.put("W--",'X');
			translationLookup.put("HA-",'X');
			translationLookup.put("HC-",'X');
			translationLookup.put("HG-",'X');
			translationLookup.put("HU-",'X');
			translationLookup.put("HR-",'X');
			translationLookup.put("HY-",'X');
			translationLookup.put("HR-",'X');
			translationLookup.put("HM-",'X');
			translationLookup.put("HK-",'X');
			translationLookup.put("HS-",'X');
			translationLookup.put("HW-",'X');
			translationLookup.put("HH-",'X');
			translationLookup.put("HB-",'X');
			translationLookup.put("HV-",'X');
			translationLookup.put("HD-",'X');
			translationLookup.put("HN-",'X');
			translationLookup.put("H-A",'X');
			translationLookup.put("H-C",'X');
			translationLookup.put("H-G",'X');
			translationLookup.put("H-U",'X');
			translationLookup.put("H-R",'X');
			translationLookup.put("H-Y",'X');
			translationLookup.put("H-R",'X');
			translationLookup.put("H-M",'X');
			translationLookup.put("H-K",'X');
			translationLookup.put("H-S",'X');
			translationLookup.put("H-W",'X');
			translationLookup.put("H-H",'X');
			translationLookup.put("H-B",'X');
			translationLookup.put("H-V",'X');
			translationLookup.put("H-D",'X');
			translationLookup.put("H-N",'X');
			translationLookup.put("H--",'X');
			translationLookup.put("BA-",'X');
			translationLookup.put("BC-",'X');
			translationLookup.put("BG-",'X');
			translationLookup.put("BU-",'X');
			translationLookup.put("BR-",'X');
			translationLookup.put("BY-",'X');
			translationLookup.put("BR-",'X');
			translationLookup.put("BM-",'X');
			translationLookup.put("BK-",'X');
			translationLookup.put("BS-",'X');
			translationLookup.put("BW-",'X');
			translationLookup.put("BH-",'X');
			translationLookup.put("BB-",'X');
			translationLookup.put("BV-",'X');
			translationLookup.put("BD-",'X');
			translationLookup.put("BN-",'X');
			translationLookup.put("B-A",'X');
			translationLookup.put("B-C",'X');
			translationLookup.put("B-G",'X');
			translationLookup.put("B-U",'X');
			translationLookup.put("B-R",'X');
			translationLookup.put("B-Y",'X');
			translationLookup.put("B-R",'X');
			translationLookup.put("B-M",'X');
			translationLookup.put("B-K",'X');
			translationLookup.put("B-S",'X');
			translationLookup.put("B-W",'X');
			translationLookup.put("B-H",'X');
			translationLookup.put("B-B",'X');
			translationLookup.put("B-V",'X');
			translationLookup.put("B-D",'X');
			translationLookup.put("B-N",'X');
			translationLookup.put("B--",'X');
			translationLookup.put("VA-",'X');
			translationLookup.put("VC-",'X');
			translationLookup.put("VG-",'X');
			translationLookup.put("VU-",'X');
			translationLookup.put("VR-",'X');
			translationLookup.put("VY-",'X');
			translationLookup.put("VR-",'X');
			translationLookup.put("VM-",'X');
			translationLookup.put("VK-",'X');
			translationLookup.put("VS-",'X');
			translationLookup.put("VW-",'X');
			translationLookup.put("VH-",'X');
			translationLookup.put("VB-",'X');
			translationLookup.put("VV-",'X');
			translationLookup.put("VD-",'X');
			translationLookup.put("VN-",'X');
			translationLookup.put("V-A",'X');
			translationLookup.put("V-C",'X');
			translationLookup.put("V-G",'X');
			translationLookup.put("V-U",'X');
			translationLookup.put("V-R",'X');
			translationLookup.put("V-Y",'X');
			translationLookup.put("V-R",'X');
			translationLookup.put("V-M",'X');
			translationLookup.put("V-K",'X');
			translationLookup.put("V-S",'X');
			translationLookup.put("V-W",'X');
			translationLookup.put("V-H",'X');
			translationLookup.put("V-B",'X');
			translationLookup.put("V-V",'X');
			translationLookup.put("V-D",'X');
			translationLookup.put("V-N",'X');
			translationLookup.put("V--",'X');
			translationLookup.put("DA-",'X');
			translationLookup.put("DC-",'X');
			translationLookup.put("DG-",'X');
			translationLookup.put("DU-",'X');
			translationLookup.put("DR-",'X');
			translationLookup.put("DY-",'X');
			translationLookup.put("DR-",'X');
			translationLookup.put("DM-",'X');
			translationLookup.put("DK-",'X');
			translationLookup.put("DS-",'X');
			translationLookup.put("DW-",'X');
			translationLookup.put("DH-",'X');
			translationLookup.put("DB-",'X');
			translationLookup.put("DV-",'X');
			translationLookup.put("DD-",'X');
			translationLookup.put("DN-",'X');
			translationLookup.put("D-A",'X');
			translationLookup.put("D-C",'X');
			translationLookup.put("D-G",'X');
			translationLookup.put("D-U",'X');
			translationLookup.put("D-R",'X');
			translationLookup.put("D-Y",'X');
			translationLookup.put("D-R",'X');
			translationLookup.put("D-M",'X');
			translationLookup.put("D-K",'X');
			translationLookup.put("D-S",'X');
			translationLookup.put("D-W",'X');
			translationLookup.put("D-H",'X');
			translationLookup.put("D-B",'X');
			translationLookup.put("D-V",'X');
			translationLookup.put("D-D",'X');
			translationLookup.put("D-N",'X');
			translationLookup.put("D--",'X');
			translationLookup.put("NA-",'X');
			translationLookup.put("NC-",'X');
			translationLookup.put("NG-",'X');
			translationLookup.put("NU-",'X');
			translationLookup.put("NR-",'X');
			translationLookup.put("NY-",'X');
			translationLookup.put("NR-",'X');
			translationLookup.put("NM-",'X');
			translationLookup.put("NK-",'X');
			translationLookup.put("NS-",'X');
			translationLookup.put("NW-",'X');
			translationLookup.put("NH-",'X');
			translationLookup.put("NB-",'X');
			translationLookup.put("NV-",'X');
			translationLookup.put("ND-",'X');
			translationLookup.put("NN-",'X');
			translationLookup.put("NNN",'X');
			translationLookup.put("N-A",'X');
			translationLookup.put("N-C",'X');
			translationLookup.put("N-G",'X');
			translationLookup.put("N-U",'X');
			translationLookup.put("N-R",'X');
			translationLookup.put("N-Y",'X');
			translationLookup.put("N-R",'X');
			translationLookup.put("N-M",'X');
			translationLookup.put("N-K",'X');
			translationLookup.put("N-S",'X');
			translationLookup.put("N-W",'X');
			translationLookup.put("N-H",'X');
			translationLookup.put("N-B",'X');
			translationLookup.put("N-V",'X');
			translationLookup.put("N-D",'X');
			translationLookup.put("N-N",'X');
			translationLookup.put("N--",'X');
			translationLookup.put("-AA",'X');
			translationLookup.put("-AC",'X');
			translationLookup.put("-AG",'X');
			translationLookup.put("-AU",'X');
			translationLookup.put("-AR",'X');
			translationLookup.put("-AY",'X');
			translationLookup.put("-AR",'X');
			translationLookup.put("-AM",'X');
			translationLookup.put("-AK",'X');
			translationLookup.put("-AS",'X');
			translationLookup.put("-AW",'X');
			translationLookup.put("-AH",'X');
			translationLookup.put("-AB",'X');
			translationLookup.put("-AV",'X');
			translationLookup.put("-AD",'X');
			translationLookup.put("-AN",'X');
			translationLookup.put("-A-",'X');
			translationLookup.put("-CA",'X');
			translationLookup.put("-CC",'X');
			translationLookup.put("-CG",'X');
			translationLookup.put("-CU",'X');
			translationLookup.put("-CR",'X');
			translationLookup.put("-CY",'X');
			translationLookup.put("-CR",'X');
			translationLookup.put("-CM",'X');
			translationLookup.put("-CK",'X');
			translationLookup.put("-CS",'X');
			translationLookup.put("-CW",'X');
			translationLookup.put("-CH",'X');
			translationLookup.put("-CB",'X');
			translationLookup.put("-CV",'X');
			translationLookup.put("-CD",'X');
			translationLookup.put("-CN",'X');
			translationLookup.put("-C-",'X');
			translationLookup.put("-GA",'X');
			translationLookup.put("-GC",'X');
			translationLookup.put("-GG",'X');
			translationLookup.put("-GU",'X');
			translationLookup.put("-GR",'X');
			translationLookup.put("-GY",'X');
			translationLookup.put("-GR",'X');
			translationLookup.put("-GM",'X');
			translationLookup.put("-GK",'X');
			translationLookup.put("-GS",'X');
			translationLookup.put("-GW",'X');
			translationLookup.put("-GH",'X');
			translationLookup.put("-GB",'X');
			translationLookup.put("-GV",'X');
			translationLookup.put("-GD",'X');
			translationLookup.put("-GN",'X');
			translationLookup.put("-G-",'X');
			translationLookup.put("-UA",'X');
			translationLookup.put("-UC",'X');
			translationLookup.put("-UG",'X');
			translationLookup.put("-UU",'X');
			translationLookup.put("-UR",'X');
			translationLookup.put("-UY",'X');
			translationLookup.put("-UR",'X');
			translationLookup.put("-UM",'X');
			translationLookup.put("-UK",'X');
			translationLookup.put("-US",'X');
			translationLookup.put("-UW",'X');
			translationLookup.put("-UH",'X');
			translationLookup.put("-UB",'X');
			translationLookup.put("-UV",'X');
			translationLookup.put("-UD",'X');
			translationLookup.put("-UN",'X');
			translationLookup.put("-U-",'X');
			translationLookup.put("-RA",'X');
			translationLookup.put("-RC",'X');
			translationLookup.put("-RG",'X');
			translationLookup.put("-RU",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RY",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RM",'X');
			translationLookup.put("-RK",'X');
			translationLookup.put("-RS",'X');
			translationLookup.put("-RW",'X');
			translationLookup.put("-RH",'X');
			translationLookup.put("-RB",'X');
			translationLookup.put("-RV",'X');
			translationLookup.put("-RD",'X');
			translationLookup.put("-RN",'X');
			translationLookup.put("-R-",'X');
			translationLookup.put("-YA",'X');
			translationLookup.put("-YC",'X');
			translationLookup.put("-YG",'X');
			translationLookup.put("-YU",'X');
			translationLookup.put("-YR",'X');
			translationLookup.put("-YY",'X');
			translationLookup.put("-YR",'X');
			translationLookup.put("-YM",'X');
			translationLookup.put("-YK",'X');
			translationLookup.put("-YS",'X');
			translationLookup.put("-YW",'X');
			translationLookup.put("-YH",'X');
			translationLookup.put("-YB",'X');
			translationLookup.put("-YV",'X');
			translationLookup.put("-YD",'X');
			translationLookup.put("-YN",'X');
			translationLookup.put("-Y-",'X');
			translationLookup.put("-RA",'X');
			translationLookup.put("-RC",'X');
			translationLookup.put("-RG",'X');
			translationLookup.put("-RU",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RY",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RM",'X');
			translationLookup.put("-RK",'X');
			translationLookup.put("-RS",'X');
			translationLookup.put("-RW",'X');
			translationLookup.put("-RH",'X');
			translationLookup.put("-RB",'X');
			translationLookup.put("-RV",'X');
			translationLookup.put("-RD",'X');
			translationLookup.put("-RN",'X');
			translationLookup.put("-R-",'X');
			translationLookup.put("-MA",'X');
			translationLookup.put("-MC",'X');
			translationLookup.put("-MG",'X');
			translationLookup.put("-MU",'X');
			translationLookup.put("-MR",'X');
			translationLookup.put("-MY",'X');
			translationLookup.put("-MR",'X');
			translationLookup.put("-MM",'X');
			translationLookup.put("-MK",'X');
			translationLookup.put("-MS",'X');
			translationLookup.put("-MW",'X');
			translationLookup.put("-MH",'X');
			translationLookup.put("-MB",'X');
			translationLookup.put("-MV",'X');
			translationLookup.put("-MD",'X');
			translationLookup.put("-MN",'X');
			translationLookup.put("-M-",'X');
			translationLookup.put("-KA",'X');
			translationLookup.put("-KC",'X');
			translationLookup.put("-KG",'X');
			translationLookup.put("-KU",'X');
			translationLookup.put("-KR",'X');
			translationLookup.put("-KY",'X');
			translationLookup.put("-KR",'X');
			translationLookup.put("-KM",'X');
			translationLookup.put("-KK",'X');
			translationLookup.put("-KS",'X');
			translationLookup.put("-KW",'X');
			translationLookup.put("-KH",'X');
			translationLookup.put("-KB",'X');
			translationLookup.put("-KV",'X');
			translationLookup.put("-KD",'X');
			translationLookup.put("-KN",'X');
			translationLookup.put("-K-",'X');
			translationLookup.put("-SA",'X');
			translationLookup.put("-SC",'X');
			translationLookup.put("-SG",'X');
			translationLookup.put("-SU",'X');
			translationLookup.put("-SR",'X');
			translationLookup.put("-SY",'X');
			translationLookup.put("-SR",'X');
			translationLookup.put("-SM",'X');
			translationLookup.put("-SK",'X');
			translationLookup.put("-SS",'X');
			translationLookup.put("-SW",'X');
			translationLookup.put("-SH",'X');
			translationLookup.put("-SB",'X');
			translationLookup.put("-SV",'X');
			translationLookup.put("-SD",'X');
			translationLookup.put("-SN",'X');
			translationLookup.put("-S-",'X');
			translationLookup.put("-WA",'X');
			translationLookup.put("-WC",'X');
			translationLookup.put("-WG",'X');
			translationLookup.put("-WU",'X');
			translationLookup.put("-WR",'X');
			translationLookup.put("-WY",'X');
			translationLookup.put("-WR",'X');
			translationLookup.put("-WM",'X');
			translationLookup.put("-WK",'X');
			translationLookup.put("-WS",'X');
			translationLookup.put("-WW",'X');
			translationLookup.put("-WH",'X');
			translationLookup.put("-WB",'X');
			translationLookup.put("-WV",'X');
			translationLookup.put("-WD",'X');
			translationLookup.put("-WN",'X');
			translationLookup.put("-W-",'X');
			translationLookup.put("-HA",'X');
			translationLookup.put("-HC",'X');
			translationLookup.put("-HG",'X');
			translationLookup.put("-HU",'X');
			translationLookup.put("-HR",'X');
			translationLookup.put("-HY",'X');
			translationLookup.put("-HR",'X');
			translationLookup.put("-HM",'X');
			translationLookup.put("-HK",'X');
			translationLookup.put("-HS",'X');
			translationLookup.put("-HW",'X');
			translationLookup.put("-HH",'X');
			translationLookup.put("-HB",'X');
			translationLookup.put("-HV",'X');
			translationLookup.put("-HD",'X');
			translationLookup.put("-HN",'X');
			translationLookup.put("-H-",'X');
			translationLookup.put("-BA",'X');
			translationLookup.put("-BC",'X');
			translationLookup.put("-BG",'X');
			translationLookup.put("-BU",'X');
			translationLookup.put("-BR",'X');
			translationLookup.put("-BY",'X');
			translationLookup.put("-BR",'X');
			translationLookup.put("-BM",'X');
			translationLookup.put("-BK",'X');
			translationLookup.put("-BS",'X');
			translationLookup.put("-BW",'X');
			translationLookup.put("-BH",'X');
			translationLookup.put("-BB",'X');
			translationLookup.put("-BV",'X');
			translationLookup.put("-BD",'X');
			translationLookup.put("-BN",'X');
			translationLookup.put("-B-",'X');
			translationLookup.put("-VA",'X');
			translationLookup.put("-VC",'X');
			translationLookup.put("-VG",'X');
			translationLookup.put("-VU",'X');
			translationLookup.put("-VR",'X');
			translationLookup.put("-VY",'X');
			translationLookup.put("-VR",'X');
			translationLookup.put("-VM",'X');
			translationLookup.put("-VK",'X');
			translationLookup.put("-VS",'X');
			translationLookup.put("-VW",'X');
			translationLookup.put("-VH",'X');
			translationLookup.put("-VB",'X');
			translationLookup.put("-VV",'X');
			translationLookup.put("-VD",'X');
			translationLookup.put("-VN",'X');
			translationLookup.put("-V-",'X');
			translationLookup.put("-DA",'X');
			translationLookup.put("-DC",'X');
			translationLookup.put("-DG",'X');
			translationLookup.put("-DU",'X');
			translationLookup.put("-DR",'X');
			translationLookup.put("-DY",'X');
			translationLookup.put("-DR",'X');
			translationLookup.put("-DM",'X');
			translationLookup.put("-DK",'X');
			translationLookup.put("-DS",'X');
			translationLookup.put("-DW",'X');
			translationLookup.put("-DH",'X');
			translationLookup.put("-DB",'X');
			translationLookup.put("-DV",'X');
			translationLookup.put("-DD",'X');
			translationLookup.put("-DN",'X');
			translationLookup.put("-D-",'X');
			translationLookup.put("-NA",'X');
			translationLookup.put("-NC",'X');
			translationLookup.put("-NG",'X');
			translationLookup.put("-NU",'X');
			translationLookup.put("-NR",'X');
			translationLookup.put("-NY",'X');
			translationLookup.put("-NR",'X');
			translationLookup.put("-NM",'X');
			translationLookup.put("-NK",'X');
			translationLookup.put("-NS",'X');
			translationLookup.put("-NW",'X');
			translationLookup.put("-NH",'X');
			translationLookup.put("-NB",'X');
			translationLookup.put("-NV",'X');
			translationLookup.put("-ND",'X');
			translationLookup.put("-NN",'X');
			translationLookup.put("-N-",'X');
			translationLookup.put("--A",'X');
			translationLookup.put("--C",'X');
			translationLookup.put("--G",'X');
			translationLookup.put("--U",'X');
			translationLookup.put("--R",'X');
			translationLookup.put("--Y",'X');
			translationLookup.put("--R",'X');
			translationLookup.put("--M",'X');
			translationLookup.put("--K",'X');
			translationLookup.put("--S",'X');
			translationLookup.put("--W",'X');
			translationLookup.put("--H",'X');
			translationLookup.put("--B",'X');
			translationLookup.put("--V",'X');
			translationLookup.put("--D",'X');
			translationLookup.put("--N",'X');
			translationLookup.put("---",'-');
		}
	}

	/**
	 * Initialises the 'expanded' translation hash (includes all ambiguity characters with unique expansions, <b>except</b> any with gap character). None of these codons include gaps. Any codons containing gaps will be trainslated as a gap.
	 */
	public void initialiseExpandedTranslationHashWithNoGapsInCodons(){
		if(translationLookup.size()<2){
			translationLookup = new TreeMap<String,Character>();
			translationLookup.put("NNN",'X');
			translationLookup.put("AAA",'K');
			translationLookup.put("AAC",'N');
			translationLookup.put("AAG",'K');
			translationLookup.put("AAU",'N');
			translationLookup.put("AAR",'K');
			translationLookup.put("AAY",'N');
			translationLookup.put("AAR",'K');
			translationLookup.put("ACA",'T');
			translationLookup.put("ACC",'T');
			translationLookup.put("ACG",'T');
			translationLookup.put("ACU",'T');
			translationLookup.put("ACR",'T');
			translationLookup.put("ACY",'T');
			translationLookup.put("ACR",'T');
			translationLookup.put("ACM",'T');
			translationLookup.put("ACK",'T');
			translationLookup.put("ACS",'T');
			translationLookup.put("ACW",'T');
			translationLookup.put("ACH",'T');
			translationLookup.put("ACB",'T');
			translationLookup.put("ACV",'T');
			translationLookup.put("ACD",'T');
			translationLookup.put("ACN",'T');
			translationLookup.put("AGA",'R');
			translationLookup.put("AGC",'S');
			translationLookup.put("AGG",'R');
			translationLookup.put("AGU",'S');
			translationLookup.put("AGR",'R');
			translationLookup.put("AGY",'S');
			translationLookup.put("AGR",'R');
			translationLookup.put("AUA",'I');
			translationLookup.put("AUC",'I');
			translationLookup.put("AUG",'M');
			translationLookup.put("AUU",'I');
			translationLookup.put("AUY",'I');
			translationLookup.put("AUM",'I');
			translationLookup.put("AUW",'I');
			translationLookup.put("AUH",'I');
			translationLookup.put("CAA",'Q');
			translationLookup.put("CAC",'H');
			translationLookup.put("CAG",'Q');
			translationLookup.put("CAU",'H');
			translationLookup.put("CAR",'Q');
			translationLookup.put("CAY",'H');
			translationLookup.put("CAR",'Q');
			translationLookup.put("CCA",'P');
			translationLookup.put("CCC",'P');
			translationLookup.put("CCG",'P');
			translationLookup.put("CCU",'P');
			translationLookup.put("CCR",'P');
			translationLookup.put("CCY",'P');
			translationLookup.put("CCR",'P');
			translationLookup.put("CCM",'P');
			translationLookup.put("CCK",'P');
			translationLookup.put("CCS",'P');
			translationLookup.put("CCW",'P');
			translationLookup.put("CCH",'P');
			translationLookup.put("CCB",'P');
			translationLookup.put("CCV",'P');
			translationLookup.put("CCD",'P');
			translationLookup.put("CCN",'P');
			translationLookup.put("CGA",'R');
			translationLookup.put("CGC",'R');
			translationLookup.put("CGG",'R');
			translationLookup.put("CGU",'R');
			translationLookup.put("CGR",'R');
			translationLookup.put("CGY",'R');
			translationLookup.put("CGR",'R');
			translationLookup.put("CGM",'R');
			translationLookup.put("CGK",'R');
			translationLookup.put("CGS",'R');
			translationLookup.put("CGW",'R');
			translationLookup.put("CGH",'R');
			translationLookup.put("CGB",'R');
			translationLookup.put("CGV",'R');
			translationLookup.put("CGD",'R');
			translationLookup.put("CGN",'R');
			translationLookup.put("CUA",'L');
			translationLookup.put("CUC",'L');
			translationLookup.put("CUG",'L');
			translationLookup.put("CUU",'L');
			translationLookup.put("CUR",'L');
			translationLookup.put("CUY",'L');
			translationLookup.put("CUR",'L');
			translationLookup.put("CUM",'L');
			translationLookup.put("CUK",'L');
			translationLookup.put("CUS",'L');
			translationLookup.put("CUW",'L');
			translationLookup.put("CUH",'L');
			translationLookup.put("CUB",'L');
			translationLookup.put("CUV",'L');
			translationLookup.put("CUD",'L');
			translationLookup.put("CUN",'L');
			translationLookup.put("GAA",'E');
			translationLookup.put("GAC",'D');
			translationLookup.put("GAG",'E');
			translationLookup.put("GAU",'D');
			translationLookup.put("GAR",'E');
			translationLookup.put("GAY",'D');
			translationLookup.put("GAR",'E');
			translationLookup.put("GCA",'A');
			translationLookup.put("GCC",'A');
			translationLookup.put("GCG",'A');
			translationLookup.put("GCU",'A');
			translationLookup.put("GCR",'A');
			translationLookup.put("GCY",'A');
			translationLookup.put("GCR",'A');
			translationLookup.put("GCM",'A');
			translationLookup.put("GCK",'A');
			translationLookup.put("GCS",'A');
			translationLookup.put("GCW",'A');
			translationLookup.put("GCH",'A');
			translationLookup.put("GCB",'A');
			translationLookup.put("GCV",'A');
			translationLookup.put("GCD",'A');
			translationLookup.put("GCN",'A');
			translationLookup.put("GGA",'G');
			translationLookup.put("GGC",'G');
			translationLookup.put("GGG",'G');
			translationLookup.put("GGU",'G');
			translationLookup.put("GGR",'G');
			translationLookup.put("GGY",'G');
			translationLookup.put("GGR",'G');
			translationLookup.put("GGM",'G');
			translationLookup.put("GGK",'G');
			translationLookup.put("GGS",'G');
			translationLookup.put("GGW",'G');
			translationLookup.put("GGH",'G');
			translationLookup.put("GGB",'G');
			translationLookup.put("GGV",'G');
			translationLookup.put("GGD",'G');
			translationLookup.put("GGN",'G');
			translationLookup.put("GUA",'V');
			translationLookup.put("GUC",'V');
			translationLookup.put("GUG",'V');
			translationLookup.put("GUU",'V');
			translationLookup.put("GUR",'V');
			translationLookup.put("GUY",'V');
			translationLookup.put("GUR",'V');
			translationLookup.put("GUM",'V');
			translationLookup.put("GUK",'V');
			translationLookup.put("GUS",'V');
			translationLookup.put("GUW",'V');
			translationLookup.put("GUH",'V');
			translationLookup.put("GUB",'V');
			translationLookup.put("GUV",'V');
			translationLookup.put("GUD",'V');
			translationLookup.put("GUN",'V');
			translationLookup.put("UAA",'*');
			translationLookup.put("UAC",'Y');
			translationLookup.put("UAG",'*');
			translationLookup.put("UAU",'Y');
			translationLookup.put("UAR",'*');
			translationLookup.put("UAY",'Y');
			translationLookup.put("UAR",'*');
			translationLookup.put("UCA",'S');
			translationLookup.put("UCC",'S');
			translationLookup.put("UCG",'S');
			translationLookup.put("UCU",'S');
			translationLookup.put("UCR",'S');
			translationLookup.put("UCY",'S');
			translationLookup.put("UCR",'S');
			translationLookup.put("UCM",'S');
			translationLookup.put("UCK",'S');
			translationLookup.put("UCS",'S');
			translationLookup.put("UCW",'S');
			translationLookup.put("UCH",'S');
			translationLookup.put("UCB",'S');
			translationLookup.put("UCV",'S');
			translationLookup.put("UCD",'S');
			translationLookup.put("UCN",'S');
			translationLookup.put("UGA",'*');
			translationLookup.put("UGC",'C');
			translationLookup.put("UGG",'W');
			translationLookup.put("UGU",'C');
			translationLookup.put("UGY",'C');
			translationLookup.put("UUA",'L');
			translationLookup.put("UUC",'F');
			translationLookup.put("UUG",'L');
			translationLookup.put("UUU",'F');
			translationLookup.put("UUR",'L');
			translationLookup.put("UUY",'F');
			translationLookup.put("UUR",'L');
			translationLookup.put("URA",'*');
			translationLookup.put("URA",'*');
			translationLookup.put("YUA",'L');
			translationLookup.put("YUG",'L');
			translationLookup.put("YUR",'L');
			translationLookup.put("YUR",'L');
			translationLookup.put("MGA",'R');
			translationLookup.put("MGG",'R');
			translationLookup.put("MGR",'R');
			translationLookup.put("MGR",'R');
		}
	}

	/**
	 * Initialises the 'expanded' translation hash (includes <b>only</b> ambiguity characters with unique expansions <b>and</b> at least one gap character). ALL of these codons include gaps - this is unorthodox behaviour.
	 */
	public void initialiseExpandedTranslationHashWithOnlyGapsInCodons(){
		if(translationLookup.size()<2){
			translationLookup = new TreeMap<String,Character>();
			translationLookup.put("AA-",'X');
			translationLookup.put("AC-",'X');
			translationLookup.put("AG-",'X');
			translationLookup.put("AU-",'X');
			translationLookup.put("AR-",'X');
			translationLookup.put("AY-",'X');
			translationLookup.put("AR-",'X');
			translationLookup.put("AM-",'X');
			translationLookup.put("AK-",'X');
			translationLookup.put("AS-",'X');
			translationLookup.put("AW-",'X');
			translationLookup.put("AH-",'X');
			translationLookup.put("AB-",'X');
			translationLookup.put("AV-",'X');
			translationLookup.put("AD-",'X');
			translationLookup.put("AN-",'X');
			translationLookup.put("A-A",'X');
			translationLookup.put("A-C",'X');
			translationLookup.put("A-G",'X');
			translationLookup.put("A-U",'X');
			translationLookup.put("A-R",'X');
			translationLookup.put("A-Y",'X');
			translationLookup.put("A-R",'X');
			translationLookup.put("A-M",'X');
			translationLookup.put("A-K",'X');
			translationLookup.put("A-S",'X');
			translationLookup.put("A-W",'X');
			translationLookup.put("A-H",'X');
			translationLookup.put("A-B",'X');
			translationLookup.put("A-V",'X');
			translationLookup.put("A-D",'X');
			translationLookup.put("A-N",'X');
			translationLookup.put("A--",'X');
			translationLookup.put("CA-",'X');
			translationLookup.put("CC-",'X');
			translationLookup.put("CG-",'X');
			translationLookup.put("CU-",'X');
			translationLookup.put("CR-",'X');
			translationLookup.put("CY-",'X');
			translationLookup.put("CR-",'X');
			translationLookup.put("CM-",'X');
			translationLookup.put("CK-",'X');
			translationLookup.put("CS-",'X');
			translationLookup.put("CW-",'X');
			translationLookup.put("CH-",'X');
			translationLookup.put("CB-",'X');
			translationLookup.put("CV-",'X');
			translationLookup.put("CD-",'X');
			translationLookup.put("CN-",'X');
			translationLookup.put("C-A",'X');
			translationLookup.put("C-C",'X');
			translationLookup.put("C-G",'X');
			translationLookup.put("C-U",'X');
			translationLookup.put("C-R",'X');
			translationLookup.put("C-Y",'X');
			translationLookup.put("C-R",'X');
			translationLookup.put("C-M",'X');
			translationLookup.put("C-K",'X');
			translationLookup.put("C-S",'X');
			translationLookup.put("C-W",'X');
			translationLookup.put("C-H",'X');
			translationLookup.put("C-B",'X');
			translationLookup.put("C-V",'X');
			translationLookup.put("C-D",'X');
			translationLookup.put("C-N",'X');
			translationLookup.put("C--",'X');
			translationLookup.put("GA-",'X');
			translationLookup.put("GC-",'X');
			translationLookup.put("GG-",'X');
			translationLookup.put("GU-",'X');
			translationLookup.put("GR-",'X');
			translationLookup.put("GY-",'X');
			translationLookup.put("GR-",'X');
			translationLookup.put("GM-",'X');
			translationLookup.put("GK-",'X');
			translationLookup.put("GS-",'X');
			translationLookup.put("GW-",'X');
			translationLookup.put("GH-",'X');
			translationLookup.put("GB-",'X');
			translationLookup.put("GV-",'X');
			translationLookup.put("GD-",'X');
			translationLookup.put("GN-",'X');
			translationLookup.put("G-A",'X');
			translationLookup.put("G-C",'X');
			translationLookup.put("G-G",'X');
			translationLookup.put("G-U",'X');
			translationLookup.put("G-R",'X');
			translationLookup.put("G-Y",'X');
			translationLookup.put("G-R",'X');
			translationLookup.put("G-M",'X');
			translationLookup.put("G-K",'X');
			translationLookup.put("G-S",'X');
			translationLookup.put("G-W",'X');
			translationLookup.put("G-H",'X');
			translationLookup.put("G-B",'X');
			translationLookup.put("G-V",'X');
			translationLookup.put("G-D",'X');
			translationLookup.put("G-N",'X');
			translationLookup.put("G--",'X');
			translationLookup.put("UA-",'X');
			translationLookup.put("UC-",'X');
			translationLookup.put("UG-",'X');
			translationLookup.put("UU-",'X');
			translationLookup.put("UR-",'X');
			translationLookup.put("UY-",'X');
			translationLookup.put("UR-",'X');
			translationLookup.put("UM-",'X');
			translationLookup.put("UK-",'X');
			translationLookup.put("US-",'X');
			translationLookup.put("UW-",'X');
			translationLookup.put("UH-",'X');
			translationLookup.put("UB-",'X');
			translationLookup.put("UV-",'X');
			translationLookup.put("UD-",'X');
			translationLookup.put("UN-",'X');
			translationLookup.put("U-A",'X');
			translationLookup.put("U-C",'X');
			translationLookup.put("U-G",'X');
			translationLookup.put("U-U",'X');
			translationLookup.put("U-R",'X');
			translationLookup.put("U-Y",'X');
			translationLookup.put("U-R",'X');
			translationLookup.put("U-M",'X');
			translationLookup.put("U-K",'X');
			translationLookup.put("U-S",'X');
			translationLookup.put("U-W",'X');
			translationLookup.put("U-H",'X');
			translationLookup.put("U-B",'X');
			translationLookup.put("U-V",'X');
			translationLookup.put("U-D",'X');
			translationLookup.put("U-N",'X');
			translationLookup.put("U--",'X');
			translationLookup.put("RA-",'X');
			translationLookup.put("RC-",'X');
			translationLookup.put("RG-",'X');
			translationLookup.put("RU-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RY-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RM-",'X');
			translationLookup.put("RK-",'X');
			translationLookup.put("RS-",'X');
			translationLookup.put("RW-",'X');
			translationLookup.put("RH-",'X');
			translationLookup.put("RB-",'X');
			translationLookup.put("RV-",'X');
			translationLookup.put("RD-",'X');
			translationLookup.put("RN-",'X');
			translationLookup.put("R-A",'X');
			translationLookup.put("R-C",'X');
			translationLookup.put("R-G",'X');
			translationLookup.put("R-U",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-Y",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-M",'X');
			translationLookup.put("R-K",'X');
			translationLookup.put("R-S",'X');
			translationLookup.put("R-W",'X');
			translationLookup.put("R-H",'X');
			translationLookup.put("R-B",'X');
			translationLookup.put("R-V",'X');
			translationLookup.put("R-D",'X');
			translationLookup.put("R-N",'X');
			translationLookup.put("R--",'X');
			translationLookup.put("YA-",'X');
			translationLookup.put("YC-",'X');
			translationLookup.put("YG-",'X');
			translationLookup.put("YU-",'X');
			translationLookup.put("YR-",'X');
			translationLookup.put("YY-",'X');
			translationLookup.put("YR-",'X');
			translationLookup.put("YM-",'X');
			translationLookup.put("YK-",'X');
			translationLookup.put("YS-",'X');
			translationLookup.put("YW-",'X');
			translationLookup.put("YH-",'X');
			translationLookup.put("YB-",'X');
			translationLookup.put("YV-",'X');
			translationLookup.put("YD-",'X');
			translationLookup.put("YN-",'X');
			translationLookup.put("Y-A",'X');
			translationLookup.put("Y-C",'X');
			translationLookup.put("Y-G",'X');
			translationLookup.put("Y-U",'X');
			translationLookup.put("Y-R",'X');
			translationLookup.put("Y-Y",'X');
			translationLookup.put("Y-R",'X');
			translationLookup.put("Y-M",'X');
			translationLookup.put("Y-K",'X');
			translationLookup.put("Y-S",'X');
			translationLookup.put("Y-W",'X');
			translationLookup.put("Y-H",'X');
			translationLookup.put("Y-B",'X');
			translationLookup.put("Y-V",'X');
			translationLookup.put("Y-D",'X');
			translationLookup.put("Y-N",'X');
			translationLookup.put("Y--",'X');
			translationLookup.put("RA-",'X');
			translationLookup.put("RC-",'X');
			translationLookup.put("RG-",'X');
			translationLookup.put("RU-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RY-",'X');
			translationLookup.put("RR-",'X');
			translationLookup.put("RM-",'X');
			translationLookup.put("RK-",'X');
			translationLookup.put("RS-",'X');
			translationLookup.put("RW-",'X');
			translationLookup.put("RH-",'X');
			translationLookup.put("RB-",'X');
			translationLookup.put("RV-",'X');
			translationLookup.put("RD-",'X');
			translationLookup.put("RN-",'X');
			translationLookup.put("R-A",'X');
			translationLookup.put("R-C",'X');
			translationLookup.put("R-G",'X');
			translationLookup.put("R-U",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-Y",'X');
			translationLookup.put("R-R",'X');
			translationLookup.put("R-M",'X');
			translationLookup.put("R-K",'X');
			translationLookup.put("R-S",'X');
			translationLookup.put("R-W",'X');
			translationLookup.put("R-H",'X');
			translationLookup.put("R-B",'X');
			translationLookup.put("R-V",'X');
			translationLookup.put("R-D",'X');
			translationLookup.put("R-N",'X');
			translationLookup.put("R--",'X');
			translationLookup.put("MA-",'X');
			translationLookup.put("MC-",'X');
			translationLookup.put("MG-",'X');
			translationLookup.put("MU-",'X');
			translationLookup.put("MR-",'X');
			translationLookup.put("MY-",'X');
			translationLookup.put("MR-",'X');
			translationLookup.put("MM-",'X');
			translationLookup.put("MK-",'X');
			translationLookup.put("MS-",'X');
			translationLookup.put("MW-",'X');
			translationLookup.put("MH-",'X');
			translationLookup.put("MB-",'X');
			translationLookup.put("MV-",'X');
			translationLookup.put("MD-",'X');
			translationLookup.put("MN-",'X');
			translationLookup.put("M-A",'X');
			translationLookup.put("M-C",'X');
			translationLookup.put("M-G",'X');
			translationLookup.put("M-U",'X');
			translationLookup.put("M-R",'X');
			translationLookup.put("M-Y",'X');
			translationLookup.put("M-R",'X');
			translationLookup.put("M-M",'X');
			translationLookup.put("M-K",'X');
			translationLookup.put("M-S",'X');
			translationLookup.put("M-W",'X');
			translationLookup.put("M-H",'X');
			translationLookup.put("M-B",'X');
			translationLookup.put("M-V",'X');
			translationLookup.put("M-D",'X');
			translationLookup.put("M-N",'X');
			translationLookup.put("M--",'X');
			translationLookup.put("KA-",'X');
			translationLookup.put("KC-",'X');
			translationLookup.put("KG-",'X');
			translationLookup.put("KU-",'X');
			translationLookup.put("KR-",'X');
			translationLookup.put("KY-",'X');
			translationLookup.put("KR-",'X');
			translationLookup.put("KM-",'X');
			translationLookup.put("KK-",'X');
			translationLookup.put("KS-",'X');
			translationLookup.put("KW-",'X');
			translationLookup.put("KH-",'X');
			translationLookup.put("KB-",'X');
			translationLookup.put("KV-",'X');
			translationLookup.put("KD-",'X');
			translationLookup.put("KN-",'X');
			translationLookup.put("K-A",'X');
			translationLookup.put("K-C",'X');
			translationLookup.put("K-G",'X');
			translationLookup.put("K-U",'X');
			translationLookup.put("K-R",'X');
			translationLookup.put("K-Y",'X');
			translationLookup.put("K-R",'X');
			translationLookup.put("K-M",'X');
			translationLookup.put("K-K",'X');
			translationLookup.put("K-S",'X');
			translationLookup.put("K-W",'X');
			translationLookup.put("K-H",'X');
			translationLookup.put("K-B",'X');
			translationLookup.put("K-V",'X');
			translationLookup.put("K-D",'X');
			translationLookup.put("K-N",'X');
			translationLookup.put("K--",'X');
			translationLookup.put("SA-",'X');
			translationLookup.put("SC-",'X');
			translationLookup.put("SG-",'X');
			translationLookup.put("SU-",'X');
			translationLookup.put("SR-",'X');
			translationLookup.put("SY-",'X');
			translationLookup.put("SR-",'X');
			translationLookup.put("SM-",'X');
			translationLookup.put("SK-",'X');
			translationLookup.put("SS-",'X');
			translationLookup.put("SW-",'X');
			translationLookup.put("SH-",'X');
			translationLookup.put("SB-",'X');
			translationLookup.put("SV-",'X');
			translationLookup.put("SD-",'X');
			translationLookup.put("SN-",'X');
			translationLookup.put("S-A",'X');
			translationLookup.put("S-C",'X');
			translationLookup.put("S-G",'X');
			translationLookup.put("S-U",'X');
			translationLookup.put("S-R",'X');
			translationLookup.put("S-Y",'X');
			translationLookup.put("S-R",'X');
			translationLookup.put("S-M",'X');
			translationLookup.put("S-K",'X');
			translationLookup.put("S-S",'X');
			translationLookup.put("S-W",'X');
			translationLookup.put("S-H",'X');
			translationLookup.put("S-B",'X');
			translationLookup.put("S-V",'X');
			translationLookup.put("S-D",'X');
			translationLookup.put("S-N",'X');
			translationLookup.put("S--",'X');
			translationLookup.put("WA-",'X');
			translationLookup.put("WC-",'X');
			translationLookup.put("WG-",'X');
			translationLookup.put("WU-",'X');
			translationLookup.put("WR-",'X');
			translationLookup.put("WY-",'X');
			translationLookup.put("WR-",'X');
			translationLookup.put("WM-",'X');
			translationLookup.put("WK-",'X');
			translationLookup.put("WS-",'X');
			translationLookup.put("WW-",'X');
			translationLookup.put("WH-",'X');
			translationLookup.put("WB-",'X');
			translationLookup.put("WV-",'X');
			translationLookup.put("WD-",'X');
			translationLookup.put("WN-",'X');
			translationLookup.put("W-A",'X');
			translationLookup.put("W-C",'X');
			translationLookup.put("W-G",'X');
			translationLookup.put("W-U",'X');
			translationLookup.put("W-R",'X');
			translationLookup.put("W-Y",'X');
			translationLookup.put("W-R",'X');
			translationLookup.put("W-M",'X');
			translationLookup.put("W-K",'X');
			translationLookup.put("W-S",'X');
			translationLookup.put("W-W",'X');
			translationLookup.put("W-H",'X');
			translationLookup.put("W-B",'X');
			translationLookup.put("W-V",'X');
			translationLookup.put("W-D",'X');
			translationLookup.put("W-N",'X');
			translationLookup.put("W--",'X');
			translationLookup.put("HA-",'X');
			translationLookup.put("HC-",'X');
			translationLookup.put("HG-",'X');
			translationLookup.put("HU-",'X');
			translationLookup.put("HR-",'X');
			translationLookup.put("HY-",'X');
			translationLookup.put("HR-",'X');
			translationLookup.put("HM-",'X');
			translationLookup.put("HK-",'X');
			translationLookup.put("HS-",'X');
			translationLookup.put("HW-",'X');
			translationLookup.put("HH-",'X');
			translationLookup.put("HB-",'X');
			translationLookup.put("HV-",'X');
			translationLookup.put("HD-",'X');
			translationLookup.put("HN-",'X');
			translationLookup.put("H-A",'X');
			translationLookup.put("H-C",'X');
			translationLookup.put("H-G",'X');
			translationLookup.put("H-U",'X');
			translationLookup.put("H-R",'X');
			translationLookup.put("H-Y",'X');
			translationLookup.put("H-R",'X');
			translationLookup.put("H-M",'X');
			translationLookup.put("H-K",'X');
			translationLookup.put("H-S",'X');
			translationLookup.put("H-W",'X');
			translationLookup.put("H-H",'X');
			translationLookup.put("H-B",'X');
			translationLookup.put("H-V",'X');
			translationLookup.put("H-D",'X');
			translationLookup.put("H-N",'X');
			translationLookup.put("H--",'X');
			translationLookup.put("BA-",'X');
			translationLookup.put("BC-",'X');
			translationLookup.put("BG-",'X');
			translationLookup.put("BU-",'X');
			translationLookup.put("BR-",'X');
			translationLookup.put("BY-",'X');
			translationLookup.put("BR-",'X');
			translationLookup.put("BM-",'X');
			translationLookup.put("BK-",'X');
			translationLookup.put("BS-",'X');
			translationLookup.put("BW-",'X');
			translationLookup.put("BH-",'X');
			translationLookup.put("BB-",'X');
			translationLookup.put("BV-",'X');
			translationLookup.put("BD-",'X');
			translationLookup.put("BN-",'X');
			translationLookup.put("B-A",'X');
			translationLookup.put("B-C",'X');
			translationLookup.put("B-G",'X');
			translationLookup.put("B-U",'X');
			translationLookup.put("B-R",'X');
			translationLookup.put("B-Y",'X');
			translationLookup.put("B-R",'X');
			translationLookup.put("B-M",'X');
			translationLookup.put("B-K",'X');
			translationLookup.put("B-S",'X');
			translationLookup.put("B-W",'X');
			translationLookup.put("B-H",'X');
			translationLookup.put("B-B",'X');
			translationLookup.put("B-V",'X');
			translationLookup.put("B-D",'X');
			translationLookup.put("B-N",'X');
			translationLookup.put("B--",'X');
			translationLookup.put("VA-",'X');
			translationLookup.put("VC-",'X');
			translationLookup.put("VG-",'X');
			translationLookup.put("VU-",'X');
			translationLookup.put("VR-",'X');
			translationLookup.put("VY-",'X');
			translationLookup.put("VR-",'X');
			translationLookup.put("VM-",'X');
			translationLookup.put("VK-",'X');
			translationLookup.put("VS-",'X');
			translationLookup.put("VW-",'X');
			translationLookup.put("VH-",'X');
			translationLookup.put("VB-",'X');
			translationLookup.put("VV-",'X');
			translationLookup.put("VD-",'X');
			translationLookup.put("VN-",'X');
			translationLookup.put("V-A",'X');
			translationLookup.put("V-C",'X');
			translationLookup.put("V-G",'X');
			translationLookup.put("V-U",'X');
			translationLookup.put("V-R",'X');
			translationLookup.put("V-Y",'X');
			translationLookup.put("V-R",'X');
			translationLookup.put("V-M",'X');
			translationLookup.put("V-K",'X');
			translationLookup.put("V-S",'X');
			translationLookup.put("V-W",'X');
			translationLookup.put("V-H",'X');
			translationLookup.put("V-B",'X');
			translationLookup.put("V-V",'X');
			translationLookup.put("V-D",'X');
			translationLookup.put("V-N",'X');
			translationLookup.put("V--",'X');
			translationLookup.put("DA-",'X');
			translationLookup.put("DC-",'X');
			translationLookup.put("DG-",'X');
			translationLookup.put("DU-",'X');
			translationLookup.put("DR-",'X');
			translationLookup.put("DY-",'X');
			translationLookup.put("DR-",'X');
			translationLookup.put("DM-",'X');
			translationLookup.put("DK-",'X');
			translationLookup.put("DS-",'X');
			translationLookup.put("DW-",'X');
			translationLookup.put("DH-",'X');
			translationLookup.put("DB-",'X');
			translationLookup.put("DV-",'X');
			translationLookup.put("DD-",'X');
			translationLookup.put("DN-",'X');
			translationLookup.put("D-A",'X');
			translationLookup.put("D-C",'X');
			translationLookup.put("D-G",'X');
			translationLookup.put("D-U",'X');
			translationLookup.put("D-R",'X');
			translationLookup.put("D-Y",'X');
			translationLookup.put("D-R",'X');
			translationLookup.put("D-M",'X');
			translationLookup.put("D-K",'X');
			translationLookup.put("D-S",'X');
			translationLookup.put("D-W",'X');
			translationLookup.put("D-H",'X');
			translationLookup.put("D-B",'X');
			translationLookup.put("D-V",'X');
			translationLookup.put("D-D",'X');
			translationLookup.put("D-N",'X');
			translationLookup.put("D--",'X');
			translationLookup.put("NA-",'X');
			translationLookup.put("NC-",'X');
			translationLookup.put("NG-",'X');
			translationLookup.put("NU-",'X');
			translationLookup.put("NR-",'X');
			translationLookup.put("NY-",'X');
			translationLookup.put("NR-",'X');
			translationLookup.put("NM-",'X');
			translationLookup.put("NK-",'X');
			translationLookup.put("NS-",'X');
			translationLookup.put("NW-",'X');
			translationLookup.put("NH-",'X');
			translationLookup.put("NB-",'X');
			translationLookup.put("NV-",'X');
			translationLookup.put("ND-",'X');
			translationLookup.put("NN-",'X');
			translationLookup.put("N-A",'X');
			translationLookup.put("N-C",'X');
			translationLookup.put("N-G",'X');
			translationLookup.put("N-U",'X');
			translationLookup.put("N-R",'X');
			translationLookup.put("N-Y",'X');
			translationLookup.put("N-R",'X');
			translationLookup.put("N-M",'X');
			translationLookup.put("N-K",'X');
			translationLookup.put("N-S",'X');
			translationLookup.put("N-W",'X');
			translationLookup.put("N-H",'X');
			translationLookup.put("N-B",'X');
			translationLookup.put("N-V",'X');
			translationLookup.put("N-D",'X');
			translationLookup.put("N-N",'X');
			translationLookup.put("N--",'X');
			translationLookup.put("-AA",'X');
			translationLookup.put("-AC",'X');
			translationLookup.put("-AG",'X');
			translationLookup.put("-AU",'X');
			translationLookup.put("-AR",'X');
			translationLookup.put("-AY",'X');
			translationLookup.put("-AR",'X');
			translationLookup.put("-AM",'X');
			translationLookup.put("-AK",'X');
			translationLookup.put("-AS",'X');
			translationLookup.put("-AW",'X');
			translationLookup.put("-AH",'X');
			translationLookup.put("-AB",'X');
			translationLookup.put("-AV",'X');
			translationLookup.put("-AD",'X');
			translationLookup.put("-AN",'X');
			translationLookup.put("-A-",'X');
			translationLookup.put("-CA",'X');
			translationLookup.put("-CC",'X');
			translationLookup.put("-CG",'X');
			translationLookup.put("-CU",'X');
			translationLookup.put("-CR",'X');
			translationLookup.put("-CY",'X');
			translationLookup.put("-CR",'X');
			translationLookup.put("-CM",'X');
			translationLookup.put("-CK",'X');
			translationLookup.put("-CS",'X');
			translationLookup.put("-CW",'X');
			translationLookup.put("-CH",'X');
			translationLookup.put("-CB",'X');
			translationLookup.put("-CV",'X');
			translationLookup.put("-CD",'X');
			translationLookup.put("-CN",'X');
			translationLookup.put("-C-",'X');
			translationLookup.put("-GA",'X');
			translationLookup.put("-GC",'X');
			translationLookup.put("-GG",'X');
			translationLookup.put("-GU",'X');
			translationLookup.put("-GR",'X');
			translationLookup.put("-GY",'X');
			translationLookup.put("-GR",'X');
			translationLookup.put("-GM",'X');
			translationLookup.put("-GK",'X');
			translationLookup.put("-GS",'X');
			translationLookup.put("-GW",'X');
			translationLookup.put("-GH",'X');
			translationLookup.put("-GB",'X');
			translationLookup.put("-GV",'X');
			translationLookup.put("-GD",'X');
			translationLookup.put("-GN",'X');
			translationLookup.put("-G-",'X');
			translationLookup.put("-UA",'X');
			translationLookup.put("-UC",'X');
			translationLookup.put("-UG",'X');
			translationLookup.put("-UU",'X');
			translationLookup.put("-UR",'X');
			translationLookup.put("-UY",'X');
			translationLookup.put("-UR",'X');
			translationLookup.put("-UM",'X');
			translationLookup.put("-UK",'X');
			translationLookup.put("-US",'X');
			translationLookup.put("-UW",'X');
			translationLookup.put("-UH",'X');
			translationLookup.put("-UB",'X');
			translationLookup.put("-UV",'X');
			translationLookup.put("-UD",'X');
			translationLookup.put("-UN",'X');
			translationLookup.put("-U-",'X');
			translationLookup.put("-RA",'X');
			translationLookup.put("-RC",'X');
			translationLookup.put("-RG",'X');
			translationLookup.put("-RU",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RY",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RM",'X');
			translationLookup.put("-RK",'X');
			translationLookup.put("-RS",'X');
			translationLookup.put("-RW",'X');
			translationLookup.put("-RH",'X');
			translationLookup.put("-RB",'X');
			translationLookup.put("-RV",'X');
			translationLookup.put("-RD",'X');
			translationLookup.put("-RN",'X');
			translationLookup.put("-R-",'X');
			translationLookup.put("-YA",'X');
			translationLookup.put("-YC",'X');
			translationLookup.put("-YG",'X');
			translationLookup.put("-YU",'X');
			translationLookup.put("-YR",'X');
			translationLookup.put("-YY",'X');
			translationLookup.put("-YR",'X');
			translationLookup.put("-YM",'X');
			translationLookup.put("-YK",'X');
			translationLookup.put("-YS",'X');
			translationLookup.put("-YW",'X');
			translationLookup.put("-YH",'X');
			translationLookup.put("-YB",'X');
			translationLookup.put("-YV",'X');
			translationLookup.put("-YD",'X');
			translationLookup.put("-YN",'X');
			translationLookup.put("-Y-",'X');
			translationLookup.put("-RA",'X');
			translationLookup.put("-RC",'X');
			translationLookup.put("-RG",'X');
			translationLookup.put("-RU",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RY",'X');
			translationLookup.put("-RR",'X');
			translationLookup.put("-RM",'X');
			translationLookup.put("-RK",'X');
			translationLookup.put("-RS",'X');
			translationLookup.put("-RW",'X');
			translationLookup.put("-RH",'X');
			translationLookup.put("-RB",'X');
			translationLookup.put("-RV",'X');
			translationLookup.put("-RD",'X');
			translationLookup.put("-RN",'X');
			translationLookup.put("-R-",'X');
			translationLookup.put("-MA",'X');
			translationLookup.put("-MC",'X');
			translationLookup.put("-MG",'X');
			translationLookup.put("-MU",'X');
			translationLookup.put("-MR",'X');
			translationLookup.put("-MY",'X');
			translationLookup.put("-MR",'X');
			translationLookup.put("-MM",'X');
			translationLookup.put("-MK",'X');
			translationLookup.put("-MS",'X');
			translationLookup.put("-MW",'X');
			translationLookup.put("-MH",'X');
			translationLookup.put("-MB",'X');
			translationLookup.put("-MV",'X');
			translationLookup.put("-MD",'X');
			translationLookup.put("-MN",'X');
			translationLookup.put("-M-",'X');
			translationLookup.put("-KA",'X');
			translationLookup.put("-KC",'X');
			translationLookup.put("-KG",'X');
			translationLookup.put("-KU",'X');
			translationLookup.put("-KR",'X');
			translationLookup.put("-KY",'X');
			translationLookup.put("-KR",'X');
			translationLookup.put("-KM",'X');
			translationLookup.put("-KK",'X');
			translationLookup.put("-KS",'X');
			translationLookup.put("-KW",'X');
			translationLookup.put("-KH",'X');
			translationLookup.put("-KB",'X');
			translationLookup.put("-KV",'X');
			translationLookup.put("-KD",'X');
			translationLookup.put("-KN",'X');
			translationLookup.put("-K-",'X');
			translationLookup.put("-SA",'X');
			translationLookup.put("-SC",'X');
			translationLookup.put("-SG",'X');
			translationLookup.put("-SU",'X');
			translationLookup.put("-SR",'X');
			translationLookup.put("-SY",'X');
			translationLookup.put("-SR",'X');
			translationLookup.put("-SM",'X');
			translationLookup.put("-SK",'X');
			translationLookup.put("-SS",'X');
			translationLookup.put("-SW",'X');
			translationLookup.put("-SH",'X');
			translationLookup.put("-SB",'X');
			translationLookup.put("-SV",'X');
			translationLookup.put("-SD",'X');
			translationLookup.put("-SN",'X');
			translationLookup.put("-S-",'X');
			translationLookup.put("-WA",'X');
			translationLookup.put("-WC",'X');
			translationLookup.put("-WG",'X');
			translationLookup.put("-WU",'X');
			translationLookup.put("-WR",'X');
			translationLookup.put("-WY",'X');
			translationLookup.put("-WR",'X');
			translationLookup.put("-WM",'X');
			translationLookup.put("-WK",'X');
			translationLookup.put("-WS",'X');
			translationLookup.put("-WW",'X');
			translationLookup.put("-WH",'X');
			translationLookup.put("-WB",'X');
			translationLookup.put("-WV",'X');
			translationLookup.put("-WD",'X');
			translationLookup.put("-WN",'X');
			translationLookup.put("-W-",'X');
			translationLookup.put("-HA",'X');
			translationLookup.put("-HC",'X');
			translationLookup.put("-HG",'X');
			translationLookup.put("-HU",'X');
			translationLookup.put("-HR",'X');
			translationLookup.put("-HY",'X');
			translationLookup.put("-HR",'X');
			translationLookup.put("-HM",'X');
			translationLookup.put("-HK",'X');
			translationLookup.put("-HS",'X');
			translationLookup.put("-HW",'X');
			translationLookup.put("-HH",'X');
			translationLookup.put("-HB",'X');
			translationLookup.put("-HV",'X');
			translationLookup.put("-HD",'X');
			translationLookup.put("-HN",'X');
			translationLookup.put("-H-",'X');
			translationLookup.put("-BA",'X');
			translationLookup.put("-BC",'X');
			translationLookup.put("-BG",'X');
			translationLookup.put("-BU",'X');
			translationLookup.put("-BR",'X');
			translationLookup.put("-BY",'X');
			translationLookup.put("-BR",'X');
			translationLookup.put("-BM",'X');
			translationLookup.put("-BK",'X');
			translationLookup.put("-BS",'X');
			translationLookup.put("-BW",'X');
			translationLookup.put("-BH",'X');
			translationLookup.put("-BB",'X');
			translationLookup.put("-BV",'X');
			translationLookup.put("-BD",'X');
			translationLookup.put("-BN",'X');
			translationLookup.put("-B-",'X');
			translationLookup.put("-VA",'X');
			translationLookup.put("-VC",'X');
			translationLookup.put("-VG",'X');
			translationLookup.put("-VU",'X');
			translationLookup.put("-VR",'X');
			translationLookup.put("-VY",'X');
			translationLookup.put("-VR",'X');
			translationLookup.put("-VM",'X');
			translationLookup.put("-VK",'X');
			translationLookup.put("-VS",'X');
			translationLookup.put("-VW",'X');
			translationLookup.put("-VH",'X');
			translationLookup.put("-VB",'X');
			translationLookup.put("-VV",'X');
			translationLookup.put("-VD",'X');
			translationLookup.put("-VN",'X');
			translationLookup.put("-V-",'X');
			translationLookup.put("-DA",'X');
			translationLookup.put("-DC",'X');
			translationLookup.put("-DG",'X');
			translationLookup.put("-DU",'X');
			translationLookup.put("-DR",'X');
			translationLookup.put("-DY",'X');
			translationLookup.put("-DR",'X');
			translationLookup.put("-DM",'X');
			translationLookup.put("-DK",'X');
			translationLookup.put("-DS",'X');
			translationLookup.put("-DW",'X');
			translationLookup.put("-DH",'X');
			translationLookup.put("-DB",'X');
			translationLookup.put("-DV",'X');
			translationLookup.put("-DD",'X');
			translationLookup.put("-DN",'X');
			translationLookup.put("-D-",'X');
			translationLookup.put("-NA",'X');
			translationLookup.put("-NC",'X');
			translationLookup.put("-NG",'X');
			translationLookup.put("-NU",'X');
			translationLookup.put("-NR",'X');
			translationLookup.put("-NY",'X');
			translationLookup.put("-NR",'X');
			translationLookup.put("-NM",'X');
			translationLookup.put("-NK",'X');
			translationLookup.put("-NS",'X');
			translationLookup.put("-NW",'X');
			translationLookup.put("-NH",'X');
			translationLookup.put("-NB",'X');
			translationLookup.put("-NV",'X');
			translationLookup.put("-ND",'X');
			translationLookup.put("-NN",'X');
			translationLookup.put("-N-",'X');
			translationLookup.put("--A",'X');
			translationLookup.put("--C",'X');
			translationLookup.put("--G",'X');
			translationLookup.put("--U",'X');
			translationLookup.put("--R",'X');
			translationLookup.put("--Y",'X');
			translationLookup.put("--R",'X');
			translationLookup.put("--M",'X');
			translationLookup.put("--K",'X');
			translationLookup.put("--S",'X');
			translationLookup.put("--W",'X');
			translationLookup.put("--H",'X');
			translationLookup.put("--B",'X');
			translationLookup.put("--V",'X');
			translationLookup.put("--D",'X');
			translationLookup.put("--N",'X');
			translationLookup.put("---",'-');
		}
	}
/**
	 * 
	 * @param suppressErrors - controls verbose stdout commentary (TRUE = less verbose)
	 * @throws Exception
	 * @since 21/05/2012
	 * @TODO The translate() method basically relies on char[] arrays mapping to a static hash. This is actually quite a lot of functionality to shove onto a relatively minor method - would be better instead to create a Codon class..
	 * 
	 */
	public void translate(boolean suppressErrors) throws SequenceTypeNotSupportedException{

		//this.initialiseTranslationHash(); // make sure the translation hash is available
		this.initialiseExpandedTranslationHash(); // expanded translation hash
//		System.out.println(numberOfSites+" sites constructed translation hash (size "+translationLookup.size()+")");
//		String testChar = "ACC";
//		System.out.println(translationLookup.get(testChar));
		
		if(alignmentSequenceCodingType.equals(SequenceCodingType.AA)){
			throw new SequenceTypeNotSupportedException(this.alignmentSequenceCodingType, SequenceCodingType.CODON);
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
					if (((pos+2)-stringSequence.length)<1) {
						AA = '-';
						assert (pos + 2 < stringSequence.length);
						assert (pos + 1 < stringSequence.length);
						codonHolder[0] = stringSequence[pos];
						try {
							codonHolder[1] = stringSequence[pos + 1];
						} catch (ArrayIndexOutOfBoundsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//						System.out.println("Run out of sequence for 2nd codon position. Gap character (-) entered. Sequence position: "+pos);
							codonHolder[1] = '-';
						}
						try {
							codonHolder[2] = stringSequence[pos + 2];
						} catch (ArrayIndexOutOfBoundsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//						System.out.println("Run out of sequence for 3rd codon positon. Gap character (-) entered. Sequence position: "+pos);
							codonHolder[2] = '-';
						}
						codon = new String(codonHolder);
						/*
						 * 
						 */
						
						
						//					System.out.println(codon.length()+" "+pos+" candidate codon: "+codon);
						//					System.out.println("printing the AA, "+codon);
						if (!
								((codonHolder[0] == '-') && (codonHolder[1] == '-') && (codonHolder[2] == '-'))
						) { //Whole codon is empty
							if ((codonHolder[0] == '-')
									|| (codonHolder[1] == '-')
									|| (codonHolder[2] == '-')) { //Part of codon is empty
								AA = '-';
								numAmbiguousCodons++;
							} else {
								try {
									//AA = translationLookup.get(codon);
									AA = EnnumeratedTranslatorFullCodons.translate(codon);
								} catch (NullPointerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									AA = '-';
									numAmbiguousCodons++;
									if (!suppressErrors) {
										System.out
												.println(taxon
														+ ": unable to translate this codon. Missing data / gap character entered instead. (sequence position "
														+ pos);
									}
								}
							}
						} else {
							AA = '-';
							numAmbiguousCodons++;
							if (!suppressErrors) {
								System.out.println(taxon + " ambiguous codon "
										+ codon + "; position " + pos);
							}
						}
						if (AA == '*') {
							numStopCodons++;
						}
						newAAseq.append(AA);
					}
				}
				if(newAAseq.length() > newMaxNoSites){
					newMaxNoSites = newAAseq.length();
				}
				System.out.println(taxon+"\t\ttranslated.\tambig: "+numAmbiguousCodons+"\tstops: "+numStopCodons+"\tseq: "+newAAseq.substring(0, 4));
				sequenceHash.put(taxon, newAAseq.toString().toCharArray());
			}
			// Fire updates to alignment stats
			numberOfSites = newMaxNoSites;
			this.calculateAlignmentStats(false);
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
	
	/**
	 * @since 22/08/2012
	 * @author joeparker
	 * @return no return type. sequences will have stop codons replaced with gaps (if AA) or replaced with gaps and removed where invariant (if Codon)
	 */
	public void removeStopCodons(){
		if(alignmentSequenceCodingType.equals(SequenceCodingType.AA)){
			for(String taxon:taxaListArray){
				char[] nonStopSequence = new StringWalker(sequenceHash.remove(taxon),'*','-').finishedSequence.toCharArray();
				sequenceHash.put(taxon, nonStopSequence);
			}
		}else{
			try {
				// TODO Consider parsing DNA / RNA for stop codons. Bear in mind that introducing gaps to e.g. 3rd position codons may bias downstream dN/dS etc
				this.engapStopCodonsInDNA();
				this.stripGapsPreserveCodons();
			} catch (SequenceTypeNotSupportedException e) {
				// TODO Auto-generated catch block
				System.out.println("WARNING! This method is not implemented for DNA/RNA data. These codons have NOT been stripped.");
				e.printStackTrace();
			}
		}
	}

	public void determineInvariantSites(){
		numberOfInvariantSites = 0;
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
		
		Iterator seqItr = sequenceHash.keySet().iterator();
		while(seqItr.hasNext()){
			String taxon = seqItr.next().toString();
			int index = 0;
//			System.out.println(taxon);
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
	
	protected TreeMap<String, char[]> getSequenceHash() {
		return sequenceHash;
	}

	protected void setSequenceHash(TreeMap<String, char[]> sequenceHash) {
		this.sequenceHash = sequenceHash;
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
	
	public String[] getTransposedSites(){
		if(transposedSites != null){
			return transposedSites;
		}else{
			assert(numberOfSites>0);
			transposedSites = new String[numberOfSites];
			StringBuilder[] sb = new StringBuilder[numberOfSites];
			for(int p=0;p<numberOfSites;p++){
				sb[p] = new StringBuilder();
			}
			int i=0;
			for(String taxon:taxaListArray){
				char[] chars = this.sequenceHash.get(taxon);
				assert(chars.length>0);
				assert(chars.length == this.numberOfSites);
				for(int j=0;j<chars.length;j++){
					sb[j].append(chars[j]);
				}
				i++;
			}
			for(int k=0;k<numberOfSites;k++){
			//	System.out.println(sb[k].toString());
				transposedSites[k] = sb[k].toString();
			}
			return transposedSites;
		}
	}

	/**
	 * @return the numberOfInvariantSites
	 */
	public int getNumberOfInvariantSites() {
		return numberOfInvariantSites;
	}

	/**
	 * @return the meanSitewiseEntropy
	 */
	public float getMeanSitewiseEntropy() {
		return meanSitewiseEntropy;
	}

	/**
	 * Getter method (acts as accessor to private calculateSitewiseEntropies() method)
	 * @param evaluateGaps - should gaps be treated as informative?
	 * @return float[] of sitewise entropties
	 */
	public float[] getSitewiseEntropies(boolean evaluateGaps) {
		return this.calculateSitewiseEntropies(evaluateGaps);
	}
	             
	/**
	 * @return the meanTaxonwiseLongestUngappedSequence
	 */
	public float getMeanTaxonwiseLongestUngappedSequence() {
		return meanTaxonwiseLongestUngappedSequence;
	}

	public void printNumberOfSites() {
		// TODO Auto-generated method stub
		System.out.println(this.numberOfSites+" sites.");
	}

	public void printNumberOfTaxa() {
		// TODO Auto-generated method stub
		System.out.println(this.numberOfTaxa+" taxa.");
	}
	
	public void printNumberOfInvariantSites() {
		System.out.println(this.numberOfInvariantSites+" invariant sites.");
	}

	/**
	 * A method to print a truncated version of the alignment, including the truncatedNamesHash (with UIDs)
	 * @param printLimit
	 */
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
	 * A method to print the entire alignment.
	 */
	public void printCompleteSequences() {
		Iterator itr = sequenceHash.keySet().iterator();
		while(itr.hasNext()){
			String thisTaxon = itr.next().toString();
			char[] paddedTaxon = new char[30];
			char[] taxonChar = thisTaxon.toCharArray();
			for(int i=0;i<30;i++){
				if(i<taxonChar.length){
					paddedTaxon[i] = taxonChar[i];
				}else{
					paddedTaxon[i] = ' ';
				}
			}
			char[] thisSeq = sequenceHash.get(thisTaxon);
			System.out.println(new String(paddedTaxon)+"\t"+new String(thisSeq));
		}
		
	}
	
	/**
	 * 
	 * @param fullyPathQualifiedFileName - name of the whole
	 * @param useOriginalTaxonNames - or the truncated sequence names hash (first 6 chars of original name + underscore + 3-digit padded UID)
	 */
	public void writePhylipFile(String fullyPathQualifiedFileName, boolean useOriginalTaxonNames){
		StringBuffer buffer = new StringBuffer();
		buffer.append(numberOfTaxa+"   "+numberOfSites+"\n");
		/*
		 * We have to make sure the names are padded with enough space that (a) the sequences all line up, and (b) whitespace delimits names and sequences
		 * First we find the maximum taxon name length - we will use this (+2) as the pad limit
		 */
		int max_name_length = 0; // Longest sequence name
		for(String taxon:taxaListArray){
			if(taxon.length()>max_name_length){max_name_length = taxon.length();}
		}
		for(String taxon:taxaListArray){
			// TODO get shortname from truncatedNamesHash
			StringBuilder paddedTaxon;
			if(useOriginalTaxonNames){
				paddedTaxon = new StringBuilder(taxon);
			}else{
				paddedTaxon = new StringBuilder(truncatedNamesHash.get(taxon));
			}
			while(paddedTaxon.length() < (max_name_length+5)){
				paddedTaxon.append(" ");
			}
			buffer.append(paddedTaxon);
			buffer.append(sequenceHash.get(taxon));
			buffer.append("\n");
		}
		new BasicFileWriter(fullyPathQualifiedFileName,buffer.toString());
	}
	
	/**
	 * 
	 * @since 31/10/2011
	 * @param oldName - the old name of the taxon (existing name)
	 * @param newName - the new name to change it to
	 * @return true if successful.
	 * 
	 * This is a VERY quickly written and tested name-changing routine.
	 * TODO could do with a LOT more testing... 31/10/2011
	 */
	public boolean renameTaxon(String oldName, String newName){
		boolean exitState = false;
		if(taxaList.contains(oldName)){
			int oldNumTaxa = sequenceHash.size();
			char[] dataHolder = sequenceHash.get(oldName);
			sequenceHash.remove(oldName);
			int tmpNumTaxa = sequenceHash.size();
			assert(oldNumTaxa != tmpNumTaxa);
			sequenceHash.put(newName, dataHolder);
			int newNumTaxa = sequenceHash.size();
			assert(oldNumTaxa == newNumTaxa);
			// TODO update taxaList
			taxaList.remove(oldName);
			taxaList.add(newName);
			// TODO update taxaListArray
			this.taxaListArray = new String[taxaList.size()];
			int nameIndex = 0;
			Iterator nameItr = taxaList.iterator();
			while(nameItr.hasNext()){
				taxaListArray[nameIndex] = (String)nameItr.next();
				nameIndex++;
			}
			// TODO update truncatedNamesHash
			String tempTruncatedName = truncatedNamesHash.get(oldName);
			truncatedNamesHash.remove(oldName);
			truncatedNamesHash.put(newName, tempTruncatedName);
			exitState = true;
		}else{
			exitState = false;
		}
		return exitState;
	}

	/**
	 * 
	 * @param SitePatternsSSLS - a TreeMap<String, Float> of site patterns and their lnL values
	 * @return fullSSLS - the sitewise lnL for each site in the alignment.
	 * @see DataSeries
	 * @since 2011/11/30
	 * @author Joe Parker
	 * 
	 * This method takes a set of site patterns (as in PAML) with associated lnL values and the *actual* sites in this ASR, and matches each site with the correct lnL.  
	 * <h2>IMPORTANT!</h2>
	 * Site patterns that are present in the data but <b>not</b> in the PAML output (e.g., gaps that aren't processed by codeml) are coded with lnL = 0.0f.
	 * This is done to avoid fails but all lnL of 0.0 should therefore be treated properly.
	 * A different option would be to code them 99.9...
	 */
	public DataSeries getFullSitesLnL(TreeMap<String, Float> SitePatternsSSLS) {
		// TODO Auto-generated method stub
		transposedSites = this.getTransposedSites();
		float[] sitePatterns = new float[transposedSites.length];
		/*
		 * Iterate through the sites, querying the site patterns to get their lnL
		 */
		int i = 0;
		for(String pattern:transposedSites){
			try {
				sitePatterns[i] = SitePatternsSSLS.get(pattern); // 09/08/2012 - Still not successfully debugged all instances of this..
				//TODO debug the above properly
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sitePatterns[i] = 0.0f; // this site pattern has not been seen (contains a gap?)
			}
			i++;
		}
		DataSeries fullSSLS= new DataSeries(sitePatterns,"full site lnL");
		return fullSSLS;
	}
	
	public AlignedSequenceRepresentation clone(){
		AlignedSequenceRepresentation aClone;
		try {
			aClone = new AlignedSequenceRepresentation(this.sequenceHash,this.taxaList,this.taxaListArray,this.alignmentSequenceCodingType,this.file);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			aClone = null;
		}
		return aClone;
	}
	
	/**
	 * @author joeparker
	 * @since 0.0.1 r63/4
	 * @param filter - either the number of missing taxa at any given site, or the proportion (0-100 out of 100) of taxa that are allowed to have missing data at any given site [e.g. if the ratio (gapped taxa/total taxa) > (filter/100), for any given site]
	 * @param filterByFactor - false to just use number of taxa missing as the threshold, true to use proportions.
	 * @throws FilterOutOfAllowableRangeException 
	 * The allowable filter bounds are:
	 * 	0<filter�100 	- when using filterFactor = true;
	 *  0<numberOfTaxa 	- when using filterFactor = false (e.g., threshold no. of gaps per site.
	 */
	public void filterForMissingData(int filter, boolean filterByFactor) throws FilterOutOfAllowableRangeException{
		int[] gapCounts = new int[numberOfSites];
		int[] datCounts = new int[numberOfSites];
		int[] nCounts = new int[numberOfSites];
		boolean[] sitesToFilter = new boolean[numberOfSites];
		Iterator itr = sequenceHash.keySet().iterator();
		while(itr.hasNext()){
			String aTaxon = itr.next().toString();
			char[] theseChars =	sequenceHash.get(aTaxon);
			for(int e=0;e<theseChars.length;e++){
				if(theseChars[e] == '-'){
					gapCounts[e]++;
				}else{
					datCounts[e]++;
				}
				nCounts[e]++;
			}
		}
		
		for(int i=0;i<numberOfSites;i++){
			assert((gapCounts[i]+datCounts[i]) == nCounts[i]); // Important: check that every character has been counted...
		}
		
		itr = null;
		//TODO
		/*
		 * Build up the gapCounts array by iteration throug sequenceHash
		 * then act appropriately to swap out sites with gaps, e.g.: 
		 * 		iterate again to replace data with gaps
		 * 		call removeUnambiguousGaps()
		 * 		housekeeping (invariant sites, numSites etc)
		 */
		if(filterByFactor){
			// TODO filter the alignment by factor of sites - implies factor 0�n<100
			if((filter<0) || (filter>100)){
				throw new FilterOutOfAllowableRangeException();
			}
			for(int g=0;g<numberOfSites;g++){
				if(nCounts[g]< numberOfTaxa){
					// for some reason this site does not have data at all sites. lose it.
					sitesToFilter[g] = true;
				}else if(((float)gapCounts[g]/(float)numberOfTaxa) > ((float)filter/(float)100)){		// This is the ratio: (gapped taxa/total taxa) > (filter/100)
					// remove this site
					sitesToFilter[g] = true;
				}else{
					// keep this site
					sitesToFilter[g] = false;
				}
			}
		}else{
			// TODO filter sites that have more than filter gaps.
			if((filter>numberOfTaxa) || (filter<0)){
				throw new FilterOutOfAllowableRangeException();
			}
			for(int g=0;g<numberOfSites;g++){
				if(nCounts[g]< numberOfTaxa){
					// for some reason this site does not have data at all sites. lose it.
					sitesToFilter[g] = true;
				}else if(gapCounts[g] > filter){
					// remove this site
					sitesToFilter[g] = true;
				}else{
					// keep this site
					sitesToFilter[g] = false;
				}
			}
		}
		// Actually insert the required gaps and remove them.
		// TODO consider the case of missing data...
		Iterator etr = sequenceHash.keySet().iterator();
		TreeMap<String,char[]> newHash = new TreeMap<String, char[]>();
		while(etr.hasNext()){
			String oldTaxon = etr.next().toString();
			char[] oldChars = sequenceHash.get(oldTaxon);
			char[] newChars = new char[numberOfSites];
			for(int sub=0;sub<numberOfSites;sub++){
				if(sitesToFilter[sub]){
					newChars[sub] = '-';
				}else{
					newChars[sub] = oldChars[sub];
				}
			}
			newHash.put(oldTaxon, newChars);
		}
		assert(sequenceHash.size() == newHash.size());
		sequenceHash = newHash;
		numberOfSites = this.buildNumberOfSites();
		this.determineInvariantSites();
		this.removeUnambiguousGaps();
		numberOfSites = this.buildNumberOfSites();
		this.determineInvariantSites();
		etr = null;
	}

	/**
	 * @since  - 09/08/2012
	 * @param  - sequenceIndex - which sequence to get (not guaranteed sort order)
	 * @return - a char array of sequence
	 * @TODO   - should probably overload this for taxon names and/or input sort order..
	 */
	public char[] getSequenceChars(int sequenceIndex) {
		// TODO Auto-generated method stub
		char[] retSeq = (char[]) sequenceHash.get(this.taxaListArray[sequenceIndex]);
		return retSeq;
	}

	/**
	 * @since  	- 09/08/2012
	 * @param 	- sequenceIndex - which taxon to get (not guaranteed sort order)
	 * @return 	- the taxon name (String)
	 * @TODO  	- should probably overload this for taxon names and/or input sort order..
	 */
	public String getTaxon(int taxonIndex) {
		return taxaListArray[taxonIndex];
	}
	
	/**
	 * @since 	- 21/08/2012
	 * @param 	- null
	 * @return 	- void
	 * This method strips gaps from an ASR where sequences are type 'RNA','DNA' or 'Codon'
	 * @fails 	- where sequence length <2
	 * @fails 	- if sequences are all gaps
	 * @throws 	- SequenceTypeNotSupportedException
	 */
	public void stripGapsPreserveCodons() throws SequenceTypeNotSupportedException{
		if((this.alignmentSequenceCodingType == SequenceCodingType.AA)||(this.alignmentSequenceCodingType == SequenceCodingType.UNDEFINED)){
			throw new SequenceTypeNotSupportedException(SequenceCodingType.CODON,this.alignmentSequenceCodingType);
		}else{
			/** get on with it.
			 * execution:
			 *	initialise boolean[] gapsToStrip 
			 *	first seq: fill boolean with true where gaps
			 *	codonwise iterate (can be over first seq as well, shouldn't fail):
			 *			set gapsToStrip[codon] = (gapsToStrip[codon]==true && seq.hasGap(codon))
			 *			e.g., truth table:
			 *
			 *					gapsToStrip[T]	gapsToStrip[F]
			 *		seq.gap(T)		T				F
			 *		seq.gap(F)		F				F
			 *	
			 *	end iteration;
			 *	
			 *	where gapsToStrip; remove sites
			 *	recount nChars
			 */		
			this.trimToWholeNumberOfCodons();
			boolean[] gapsToStrip = new boolean[this.numberOfSites];
			for(int i=0;i<gapsToStrip.length;i++){
				gapsToStrip[i] = false;
			}
			Iterator etr = sequenceHash.keySet().iterator();
			char[] firstSeq = sequenceHash.get(etr.next().toString());
			for(int i=0;i<this.numberOfSites;i+=3){				
				if((firstSeq[i+0]=='-')&&(firstSeq[i+1]=='-')&&(firstSeq[i+2]=='-')){
					gapsToStrip[i+0] = true;
					gapsToStrip[i+1] = true;
					gapsToStrip[i+2] = true;
				}
			}
			while(etr.hasNext()){
				String taxon = etr.next().toString();
				char[] seqChars = new StringWalker(sequenceHash.get(taxon),'T','U').finishedSequence.toCharArray();
				for(int i=0;i<this.numberOfSites;i+=3){				
					if(
							(seqChars[i+0]=='-')&&
							(seqChars[i+1]=='-')&&
							(seqChars[i+2]=='-')&&
							(gapsToStrip[i+0])&&
							(gapsToStrip[i+1])&&
							(gapsToStrip[i+2])
					){
						gapsToStrip[i+0] = true;
						gapsToStrip[i+1] = true;
						gapsToStrip[i+2] = true;
					}else{
						gapsToStrip[i+0] = false;
						gapsToStrip[i+1] = false;
						gapsToStrip[i+2] = false;
					}
				}
			}
			assert(gapsToStrip.length>0);
			// TODO OK, the gapsToStrip array should now be initialised correctly. Now strip sites where indicated.
			int newSitesLength = 0;
			etr = sequenceHash.keySet().iterator();
			while(etr.hasNext()){
				String taxon = etr.next().toString();
				char[] seqChars = sequenceHash.get(taxon);
				StringBuilder newChars = new StringBuilder();
				for(int i=0;i<seqChars.length;i++){
					if(!gapsToStrip[i]){
						newChars.append(seqChars[i]);
					}
				}
				if(newChars.length()>newSitesLength){
					newSitesLength = newChars.length();
				}
				sequenceHash.put(taxon, newChars.toString().toCharArray());
			}
			// TODO remember to call buildNumberOfSites after.
			this.numberOfSites = newSitesLength;
			assert(true);
		}
	}
	
	/**
	 * @since 	21/08/2012
	 * @return 	Boolean hasWholeNumberOfCodons - indicates whether no.codons/3 is an integer (whole codons) or not (partial codons)
	 * @throws 	SequenceTypeNotSupportedException
	 * A method that checks whether a whole number of codons are present, e.g. if (this.numberOfSites/3) has a remainder (% > 0)
	 * This method should throw a SequenceTypeNotSupportedException if AA or UNDEFINED sequence types are used
	 * @fails	Where numberOfChars == 0
	 * @fails	Where numberOfChars < 3
	 */
	public boolean hasWholeNumberOfCodons() throws SequenceTypeNotSupportedException{
		if((this.alignmentSequenceCodingType == SequenceCodingType.AA)||(this.alignmentSequenceCodingType == SequenceCodingType.UNDEFINED)){
			throw new SequenceTypeNotSupportedException(SequenceCodingType.CODON,this.alignmentSequenceCodingType);
		}else{
			if((this.numberOfSites%3)==0){
				// remainder of (no.sites / 3) should be 0 if whole codons present.
				return true;
			}else{
				// some remainder is present; must be a non-integer codon count. 
				return false;
			}
		}
	}
	
	/**
	 * @since 	21/08/2012
	 * @return  void. The AlignedSequenceRepresentation should have the same, or fewer, chars.
	 * @throws 	SequenceTypeNotSupportedException
	 * @fails	Where numberOfChars == 0
	 * @fails	Where numberOfChars < 3
	 * A class to iteratively shrink (3' trim) an alignment until an integer number of codons is present only.
	 * @see		hasWholeNumberOfCodons()
	 */
	public void trimToWholeNumberOfCodons() throws SequenceTypeNotSupportedException{
		if((this.alignmentSequenceCodingType == SequenceCodingType.AA)||(this.alignmentSequenceCodingType == SequenceCodingType.UNDEFINED)){
			throw new SequenceTypeNotSupportedException(SequenceCodingType.CODON,this.alignmentSequenceCodingType);
		}else{
			while(!this.hasWholeNumberOfCodons()){
				/**
				 * Trim a trailing site from all seqs
				 * Recount numberOfSites
				 */
				Iterator etr = sequenceHash.keySet().iterator();
				while(etr.hasNext()){
					String taxon = etr.next().toString();
					char[] oldChars = sequenceHash.get(taxon);
					char[] newChars = new char[oldChars.length-1];
					for(int i=0;i<newChars.length;i++){
						newChars[i] = oldChars[i];
					}
					sequenceHash.put(taxon, newChars);
				}
				this.numberOfSites = this.buildNumberOfSites();
			}
		}
	}
	
	/**
	 * @since 	21/08/2012
	 * @return  void. The AlignedSequenceRepresentation should have the same, or fewer, chars.
	 * @throws 	SequenceTypeNotSupportedException
	 * @fails	Where numberOfChars == 0
	 * @fails	Where numberOfChars < 3
	 * A class to REPLACE stop (TGA/UGA) codons from DNA,RNA or Codon data with gap '-' characters.
	 * @see		hasWholeNumberOfCodons(), trimToWholeNumberOfCodons, stripGapsPreserveCodons.
	 */
	public void engapStopCodonsInDNA() throws SequenceTypeNotSupportedException{
		this.initialiseTranslationHash(); // make sure the translation hash is available
		if((this.alignmentSequenceCodingType == SequenceCodingType.AA)||(this.alignmentSequenceCodingType == SequenceCodingType.UNDEFINED)){
			throw new SequenceTypeNotSupportedException(SequenceCodingType.CODON,this.alignmentSequenceCodingType);
		}else{
			this.trimToWholeNumberOfCodons();
			Iterator etr = sequenceHash.keySet().iterator();
			while(etr.hasNext()){
				String taxon = etr.next().toString();
				char[] oldChars = new StringWalker(sequenceHash.get(taxon),'T','U').finishedSequence.toCharArray();
				char[] newChars = new char[oldChars.length];
				for(int i=0;i<this.numberOfSites;i+=3){
					if(!
							(
									(oldChars[i+0]=='-')||
									(oldChars[i+1]=='-')||
									(oldChars[i+2]=='-')
							)
					){
						String codon = (""+oldChars[i]+oldChars[i+1]+oldChars[i+2]);
						char translated;
						try {
							translated = translationLookup.get(codon);
						} catch (NullPointerException e) {
							// There is a problem with this codon. It is probably an ambiguity combination not in the table, e.g. 'UNC' etc
							// Instead, pass a 'any' ('X') character for now. In translation this really needs sorting out though - they should be fully ennumerated.
							translated = 'X';
							e.printStackTrace();
						}
						if(translated =='*'){
							newChars[i+0] = '-';	
							newChars[i+1] = '-';	
							newChars[i+2] = '-';
						}else{
							newChars[i+0] = oldChars[i+0];	
							newChars[i+1] = oldChars[i+1];	
							newChars[i+2] = oldChars[i+2];	
						}
					}else{
						newChars[i+0] = oldChars[i+0];	
						newChars[i+1] = oldChars[i+1];	
						newChars[i+2] = oldChars[i+2];	
					}
				}
				sequenceHash.put(taxon, newChars);
			}
			this.stripGapsPreserveCodons();
		}
	}
	
	public HashMap<Integer, Integer> getInputIndices(){
		return null;
	}

	/**
	 * A method to return a HashMap<String,HashSet<String>[]> of taxon names and sequences as states for a Fitch parsimony reconstruction by uk.ac.qmul.sbcs.evolution.convergence.TreeNode
	 * @return Taxon names and sequences as HashSet<String>[] 
	 * @since 2013/05/23
	 * @see TreeNode
	 */
	public HashMap<String, HashSet<String>[]> getAminoAcidsAsFitchStates() {
		HashMap<String, HashSet<String>[]> states = new HashMap<String,HashSet<String>[]>();
		Iterator<String> taxonIter = sequenceHash.keySet().iterator();
		while(taxonIter.hasNext()){
			String taxon = taxonIter.next();
			char[] charsArray = sequenceHash.get(taxon);
			HashSet<String>[] sequenceStates = (HashSet<String>[]) Array.newInstance(HashSet.class, charsArray.length);
			for(int i=0;i<charsArray.length;i++){
				sequenceStates[i] = new HashSet<String>();
				sequenceStates[i].add(Character.toString(charsArray[i]));
			}
			states.put(taxon, sequenceStates);
		}
 		return states;
	}

	/**
	 * A utility method to populate basic stats (mean sitewise entropy; mean taxonwise longest ungapped sequence) for reporting.
	 * @author Joe Parker
	 * @since r188, 2013/07/29
	 * @param evaluateGaps	: are gaps to be evaluated in the entropy calculations?
	 */
	public void calculateAlignmentStats(boolean evaluateGaps) {
		// do longest contiguous sequence
		this.meanTaxonwiseLongestUngappedSequence = 0;
		for(char[] inputSequence:this.sequenceHash.values()){
			String seq = new String(inputSequence);
			String[] nonGapData = seq.split("-");
			int longest = 0;
			for(String nonGap:nonGapData){
				if(nonGap.length()>longest){
					longest = nonGap.length();
				}
			}
			this.meanTaxonwiseLongestUngappedSequence += longest;
		}
		this.meanTaxonwiseLongestUngappedSequence /= (float)this.numberOfTaxa;
		
		// do entropies
		this.meanSitewiseEntropy = 0;
		float[] entropies = this.calculateSitewiseEntropies(evaluateGaps);
		for(float siteEntropy:entropies){
			this.meanSitewiseEntropy += siteEntropy;
		}
		this.meanSitewiseEntropy /= (float)this.numberOfSites;
		
		//do invariant sites
		this.determineInvariantSites();
	}

	/**
	 * Private internal method to calculate sitewise entropies and return them as a float[]
	 * 
	 * <br>Log-base for alphabet will be assumed to be nucleotide/RNA/codon unless this ASR object has datatype=='AA'
	 * 
	 * 
	 * <p><table>
	 * <tr><td><b>IUPAC nucleotide code</b></td><td><b>Base</b></td></tr>
	 * <tr><td>A</td><td>Adenine</td></tr>
	 * <tr><td>C</td><td>Cytosine</td></tr>
	 * <tr><td>G</td><td>Guanine</td></tr>
	 * <tr><td>T (or U)</td><td>Thymine (or Uracil)</td></tr>
	 * <tr><td>R</td><td>A or G</td></tr>
	 * <tr><td>Y</td><td>C or T</td></tr>
	 * <tr><td>S</td><td>G or C</td></tr>
	 * <tr><td>W</td><td>A or T</td></tr>
	 * <tr><td>K</td><td>G or T</td></tr>
	 * <tr><td>M</td><td>A or C</td></tr>
	 * <tr><td>B</td><td>C or G or T</td></tr>
	 * <tr><td>D</td><td>A or G or T</td></tr>
	 * <tr><td>H</td><td>A or C or T</td></tr>
	 * <tr><td>V</td><td>A or C or G</td></tr>
	 * <tr><td>N</td><td>any base</td></tr>
	 * <tr><td>. or -</td><td>gap</td></tr></table>
	 * 
	 * @param evaluateGaps	: are gaps to be evaluated in the calculation?
	 */
	private float[] calculateSitewiseEntropies(boolean evaluateGaps) {
		if(this.transposedSites == null){
			this.getTransposedSites();
		}
		float[] siteEntropies = new float[this.transposedSites.length];
		char[] basisChars = null;
		if((this.alignmentSequenceCodingType == SequenceCodingType.AA) && evaluateGaps){
			basisChars = this.basisCharsAminoAcidGap;			// the full AA alphabet, including stop ('*') character AND gaps
		}else{
			if(this.alignmentSequenceCodingType == SequenceCodingType.AA){
				basisChars = this.basisCharsAminoAcid;		// the full AA alphabet, including stop ('*') character
			}else{
				if((this.alignmentSequenceCodingType == SequenceCodingType.RNA) && evaluateGaps){
					basisChars = this.basisCharsRNAGap;	// RNA alphabet, count gaps as a character, e.g. larger alphabet
				}else{
					if(this.alignmentSequenceCodingType == SequenceCodingType.RNA){
						basisChars = this.basisCharsRNA;	// RNA alphabet, no gaps
					}else{
						if(evaluateGaps){
							basisChars = this.basisCharsNucleotideGap;	// nuclotide alphabet, count gaps as a character, e.g. larger alphabet
						}else{
							basisChars = this.basisCharsNucleotide;	// nuclotide alphabet, DON'T count gaps as a character
						}
					}
				}
			}
		}
		int logBase = basisChars.length;			// size of the alphabet
		float[] frequencies = new float[logBase];	// this will hold the counts, then be divided to get frequencies
		for(int i=0;i<this.numberOfSites;i++){
			// get the counts and convert to frequencies
			char[] seqChars = this.transposedSites[i].toCharArray();
			for(int j=0;j<logBase;j++){
				char binTarget = basisChars[j];
				frequencies[j] = 0;
				for(char observed:seqChars){
					if(observed == binTarget){
						frequencies[j]++;
					}
				}
				frequencies[j] /= (float)this.numberOfTaxa;
			}
			// increment the site entropies by each pi log pi
			for(int e=0;e<logBase;e++){
				Double eventEntropy = -(frequencies[e] * (Math.log(frequencies[e]) / Math.log(logBase)));
				if (!eventEntropy.isNaN()) {
					siteEntropies[i] += eventEntropy;
				}					
				
			}
		}
		return siteEntropies;
	}

	/**
	 * Whether this alignment has been translated or not
	 * @return - TRUE if alignment's SequenceCodingType is 'AA'
	 * @see SequenceCodingType
	 */
	public boolean isAA() {
		if(this.alignmentSequenceCodingType == SequenceCodingType.AA){
			return true;
		}else{
			return false;
		}
	}

	public void loadSequences(ArrayList<String> inputData, boolean reportInputRead) throws TaxaLimitException{
		try{
			rawInput = inputData;
			for(String line:rawInput){
				if(line.length() == 0){System.out.println("read: ["+line+"]");}
			}
			assert(rawInput.size()>0);
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
		this.padSequences();
		this.determineInvariantSites();
	}

	public void printSharedSubs() {
		// TODO Auto-generated method stub
		int diag = 0;
		for(String taxon_2:this.taxaListArray){
			for(int i=0;(i<this.numberOfTaxa)&&(i!=diag);i++){
				String taxon_1 = this.taxaListArray[i];
				String shared = this.sharedSubsBetween(taxon_1,taxon_2);
				if(shared != null){
					System.out.println("shared in:\t"+taxon_1+"\t"+taxon_2+"("+diag+","+i+")\t"+this.numberOfSites+"\t"+shared);
				}
			}
			diag++;
		}
		System.out.println();
	}

	public void printSharedPrivateSubs() {
		// TODO Auto-generated method stub
		int diag = 0;
		for(String taxon_2:this.taxaListArray){
			for(int i=0;(i<this.numberOfTaxa)&&(i!=diag);i++){
				String taxon_1 = this.taxaListArray[i];
				String shared = this.privateSharedSubsBetween(taxon_1,taxon_2);
				if(shared != null){
					System.out.println("shared in:\t"+taxon_1+"\t"+taxon_2+"("+diag+","+i+")\t"+this.numberOfSites+"\t"+shared);
				}
			}
			diag++;
		}
		System.out.println();
	}

	/**
	 * Prints substitutions shared between two taxa exclusively (no other taxa) to stdout
	 * @param taxon_1
	 * @param taxon_2
	 */
	public void printSharedPrivateSubs(String taxon_1, String taxon_2) {
		// TODO Auto-generated method stub
		String shared = this.privateSharedSubsBetween(taxon_1,taxon_2);
		if(shared != null){
			System.out.println("shared in:\t"+taxon_1+"\t"+taxon_2+")\t"+this.numberOfSites+"\t"+shared);
		}
	}

	private String privateSharedSubsBetween(String taxon_1, String taxon_2) {
		// first build array of chars seen (other taxa)
		HashSet<Character>[] charsSeen = new HashSet[this.numberOfSites];
		Iterator itr = this.sequenceHash.keySet().iterator();
		while(itr.hasNext()){
			String background_taxon = (String)itr.next();
			if(!(background_taxon.equals(taxon_1))&&!(background_taxon.equals(taxon_2))){
				char[] background_sequence = sequenceHash.get(background_taxon);
				for(int i=0;i<this.numberOfSites;i++){
					if(charsSeen[i]==null){
						// first sequence, charset not initalised
						charsSeen[i] = new HashSet<Character>();
					}
					// add char seen (duplicates discarded anyway...
					charsSeen[i].add(background_sequence[i]);
				}
			}
		}
		// if taxon_1 char appears in seenlist, no point continuing
		StringBuffer buf = new StringBuffer();
		char[] seq_1 = this.sequenceHash.get(taxon_1);
		char[] seq_2 = this.sequenceHash.get(taxon_2);
		int matches = 0;
		for(int i=0;i<seq_1.length;i++){
			if((seq_1[i] == seq_2[i])&&(!charsSeen[i].contains(seq_1[i]))&&(seq_1[i] !='-')&&(seq_1[i] != 'X')){
				buf.append(seq_1[i]);
				matches++;
			}else{
				buf.append("^");
			}
		}
		if(matches > 0){
			return matches+"\t"+buf.toString();
		}else{
			return null;
		}
	}

	private String sharedSubsBetween(String taxon_1, String taxon_2) {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
		char[] seq_1 = this.sequenceHash.get(taxon_1);
		char[] seq_2 = this.sequenceHash.get(taxon_2);
		int matches = 0;
		for(int i=0;i<seq_1.length;i++){
			if(seq_1[i] == seq_2[i]){
				buf.append(seq_1[i]);
				matches++;
			}else{
				buf.append("^");
			}
		}
		if(matches > 0){
			return matches+"\t"+buf.toString();
		}else{
			return null;
		}
	}

	/**
	 *	
	 * 	Purpose:	constrain a subset of taxa to have the same AA
	 *	<p>execution:	
	 * <pre>for i in num sites{
	 *	random indices
	 *	get target master taxon as char array /// NB: codon triplets
	 *	read array[indices]
	 *	replace
	 *}</pre> 
	 * 
	 * @param masterTaxon - The taxon that is being treated as the master. Sites will be converged to this taxon's sequence randomly
	 * @param taxaToForce - The taxa that in which convergence is to be simulated.
	 * @param numberOfSitesToConverge - The number of sites to be forced to converge.
	 * @throws TaxaAbsentFromAlignmentException  - In cases where the taxaToForce are not present in the alignment (check case of taxa?) 
	 * @throws VariantSitesUnavailableException 
	 */
	public void simulateConvergenceInInvariantSites(String masterTaxon, String[] taxaToForce, int numberOfSitesToConverge) throws TaxaAbsentFromAlignmentException, VariantSitesUnavailableException{
		char[] masterSequence = sequenceHash.get(masterTaxon);
		int[] targetSitesIndices;
		// TODO determine which sites we will converge at.
		// NOTE WELL - the Perl implementation of this uses a different index generation method for AA vs. codon data. Need to remember why this is.
		
		/*
		 * first determine invariant sites. 
		 * this may have been done already but these indices are crucial, 
		 * so repeat in case there's been a translation, sites removed, taxa removed etc since initialisation
		 * 
		 */
		this.determineInvariantSites();
		System.out.println("Attempting to simulate convergence in "+numberOfSitesToConverge+" sites.\nThere are "+numberOfSites+" in total; "+numberOfInvariantSites+" invariant sites on which to simulate convergence.");
		HashSet<Integer> targetSet = new HashSet<Integer>();
		if(numberOfInvariantSites < numberOfSitesToConverge){
			System.out.println("WARNING: Convergence simulation is parametized for more convergent sites than are available!\nI will instead simulate as many sites as possible (e.g. all invariant sites - master and slave taxa will be identical. This seems like a very bad idea...");
			numberOfSitesToConverge = numberOfInvariantSites;
			targetSitesIndices = new int[numberOfSitesToConverge];
			// We don't need to piss about with randomizations to fill the array, we can just write the variant sites' indices across.
			for(int i=0;i<numberOfSites;i++){
				if(invariantSitesIndices[i]){
					targetSet.add(i);
				}
			}
			/*
			 * Don't throw the exception as it's not handled at the moment
			 * 
			 * throw new VariantSitesUnavailableException();
			 */
		}else{
			targetSitesIndices = new int[numberOfSitesToConverge];
			// randomisations will be unfairly slow if across large alignemnt with few invariant sites
			// so create specific array with indices of only invariants
			int[] invariantSitesIndicesOnly = new int[this.numberOfInvariantSites];
			int j=0;
			for(int i=0;i<numberOfSites;i++){
				if(invariantSitesIndices[i]){
					invariantSitesIndicesOnly[j] = i;
					j++;
				}
			}
			// TODO time to randomize and get those indices.
			Random generator = new Random(System.currentTimeMillis());
			while(targetSet.size() < numberOfSitesToConverge){
				int possibleIndex = generator.nextInt(numberOfInvariantSites);
				targetSet.add(invariantSitesIndicesOnly[possibleIndex]);
			}
		}
	
		// copy the target site indices set to the target sites indices array
		Iterator definedTargetSites = targetSet.iterator();
		for(int definedIndices=0; definedIndices<targetSitesIndices.length;definedIndices++){
			targetSitesIndices[definedIndices] = Integer.parseInt(definedTargetSites.next().toString());
			System.out.print(targetSitesIndices[definedIndices]+", ");
		}			
		
		// do the actual convergence
		System.out.println("\nStarting convergence");
		switch(alignmentSequenceCodingType){
			case AA:
				// TODO force AA convergence
				// TODO pay close attention to perl implementation
				Random generator = new Random(System.currentTimeMillis());
				for(int index:targetSitesIndices){
					/*
					 * 	This site should be invariant
					 * 	So we need to pick a new AA at random.
					 *	It should not be the existing one..
					 */
					char currentAA = masterSequence[index];	// what's currently here
					char convergeToAA = currentAA;				// init converve char
					int AAbasisCharsLimitNoStop = this.basisCharsAminoAcid.length - 1; // the last basis char is a stop '*' char; we don't want to pick from this
					while(convergeToAA == currentAA){			// loopthrough until new AA picked
						char proposeAA = this.basisCharsAminoAcid[generator.nextInt(AAbasisCharsLimitNoStop)];
						if((proposeAA != currentAA) && (proposeAA != '*')){
							convergeToAA = proposeAA;
						}
					}
					// actually force the convergence
					masterSequence[index]	= convergeToAA;
					// iterate through target taxa
					// lots of hash remove/put ops so this will be slow...
					for(String taxon:taxaToForce){
						if(taxaList.contains(taxon)){
							char[] currentSequence = sequenceHash.remove(taxon);
							// Do stuff to it
							currentSequence[index] = convergeToAA;
							sequenceHash.put(taxon, currentSequence);
						}else{
							throw new TaxaAbsentFromAlignmentException();
						}
					}
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
			case CODON:
				// TODO this is not implemented... 
				// TODO urrrrrrrk
			default:
				break;
		}
		this.determineInvariantSites();
	}

	/**
	 * Forces the alignment to be translated ignoring which sequence type has been assumed. Debug only. 
	 * @throws SequenceTypeNotSupportedException 
	 * @deprecated Debug only.
	 */
	@Deprecated
	public void forceTranslationIgnoringSequenceType() throws SequenceTypeNotSupportedException {
		// force the coding type to DNA
		this.alignmentSequenceCodingType = SequenceCodingType.DNA;
		// translate as if it was a DNA sequence
		this.translate(true);
	}
}