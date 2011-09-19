package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.*;
import java.util.*;

public class PhymlSequenceRepresentation {
	private File file;
	private String inputFileDatatype;
	private String inputSequenceDatatype;
	private ArrayList<String> rawInput;
	private String sequenceType;
	private int numberOfSites;
	private int numberOfTaxa;
	private HashSet<char[]> sequenceHash;
	private HashSet<String> truncatedNamesHash;
	
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
	
	public void determineInputFileDatatype(){}
	
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
