package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceFileFormat;

/**
 * An abstract supertype representing a sequence alignment reader.
 * The reader will be constructed with no arguments, and populated 
 * concrete implementations of the parseFile() method. 
 * 
 * e.g.:
 * <pre>
AlignmentFileReader reader;
switch(guessAlignmentFormat){
	case(fasta): reader = new FastaFileReader();
	case(nexus): reader - new NexusFileReader();
	
	...
	
	if(reader.parseFile(inputAlignmentFile)){
		this.invariantSitesIndices = reader.getInvariantSitesIndices()
		this.numberOfTaxa = reader.getNumberOfTaxa()
		... 
		etc
	}
}
 * </pre>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public abstract class AlignmentParser {

	protected int numberOfSites;
	protected int numberOfTaxa;
	protected TreeMap<String, char[]> sequenceHash = new TreeMap<String, char[]>();
	protected TreeSet<String> taxaList = new TreeSet<String>();
	protected String[] taxaListArray;
	
	public abstract boolean parseInput(ArrayList<String> rawInput);


	public int getNumberOfSites() {
		return numberOfSites;
	}

	public int getNumberOfTaxa() {
		return numberOfTaxa;
	}

	public TreeMap<String, char[]> getSequenceHash() {
		return sequenceHash;
	}

	public TreeSet<String> getTaxaList() {
		return taxaList;
	}

	public String[] getTaxaListArray() {
		return taxaListArray;
	}

}
