package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Joe Parker, <a href="http://code.google.com/a/eclipselabs.org/u/joeparkerandthemegahairymen/">Kitson Consulting Ltd / Queen Mary, University of London.</a>
 * @version 0.1
 */
		
public class PhymlSequenceRepresentation {

	private File file;
	private String inputFileDatatype;
	private String inputSequenceDatatype;
	private ArrayList<String> rawInput;
	private SequenceFileFormat sequenceFormat;
	private int numberOfSites;
	private int numberOfTaxa;
	private HashSet<char[]> sequenceHash;
	private HashSet<String> truncatedNamesHash;
	private boolean sequenceFileTypeSet = false;
	
	public void PhymlSequenceRespresentation(){}
	
	public void loadSequences(File inputFile){
		file = inputFile;
		try{
			rawInput = new BasicFileReader().loadSequences(file);
			this.determineInputFileDatatype();
			assert(sequenceFileTypeSet);
			switch(sequenceFormat){
				case NEXUS: this.readNexusFile(); break;
				case FASTA: this.readFastaFile(); break;
				case PHYLIP: this.readPhylipFile(); break;
				case PHYDEX: this.readXMLFile(); break;
				case NATIVE: break;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public SequenceFileFormat determineInputFileDatatype(){
		if (sequenceFileTypeSet != true){
			System.out.println("determining input sequence format");
			this.sequenceFormat = SequenceFileFormat.NATIVE;
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
				this.sequenceFormat = SequenceFileFormat.FASTA;
				System.out.println("Assuming filetype is fasta.");
			}
			if(isNexus.find()){
				this.sequenceFormat = SequenceFileFormat.NEXUS;
				System.out.println("Assuming filetype is NEXUS.");
			}
			if(isPhylip.find()){
				this.sequenceFormat = SequenceFileFormat.PHYLIP;
				System.out.println("Assuming filetype is Phylip.");
			}
			if(isXML.matches()){
				this.sequenceFormat = SequenceFileFormat.PHYDEX;
				System.out.println("Assuming filetype is PhyDEX-XML.");
			}
		}
		this.sequenceFileTypeSet = true;
		return this.sequenceFormat;
	}
	
	public void determineInputSequenceType(){}
	
	public void readPhylipFile(){
		System.out.println("Processing a phylip file");
		String[] firstlineData = rawInput.get(0).split(" {1,}");
		assert(firstlineData.length>1);
		System.out.println(firstlineData[0]+" taxa, "+firstlineData[1]+" characters");
	}
	
	public void readFastaFile(){
		System.out.println("Processing a fasta file");
	}
	
	public void readNexusFile(){
		System.out.println("Processing a nexus file");
	}
	
	public void readXMLFile(){
		System.out.println("Processing an XML PhyDEX file");
	}
	
	public void writePhylipFile(){}
	
	public void writeFastaFile(){}
	
	public void writeNexusFile(){}
	
	public void writeXMLFile(){}
	
	public void forceConvergence(){}
	
	public void printShortSequences(){}
	
	public void translate(){}
	
	public void removeTaxa(){}
	
	public void removeStopCodons(){}

	public ArrayList<String> getRawInput() {
		return rawInput;
	}

	public void setRawInput(ArrayList<String> rawInput) {
		this.rawInput = rawInput;
	}
}
