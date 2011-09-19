package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.*;
import java.util.*;

/*
 * @author Joe Parker, Kitson Consulting Ltd/Queen Mary, University of London: http://code.google.com/a/eclipselabs.org/u/joeparkerandthemegahairymen/
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
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public SequenceFileFormat determineInputFileDatatype(){
		if(sequenceFileTypeSet){
			return this.sequenceFormat;
		}else{
			this.sequenceFormat = SequenceFileFormat.NATIVE;
			//TODO: implement Perl wireframe code.
			return this.sequenceFormat;
		}
	}
	
	public void determineInputSequenceType(){}
	
	public void readPhylipFile(){}
	
	public void readFastaFile(){}
	
	public void readNexusFile(){}
	
	public void readXMLFile(){}
	
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
