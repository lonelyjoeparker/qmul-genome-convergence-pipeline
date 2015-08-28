/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

/**
 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
 * @since Aug 25, 2015
 * @version 0.1
 */
public class AlignedSequenceRepresentationTestTranslationEnums extends TestCase{

	private AlignedSequenceRepresentation sourceDataASR;
	private File datasetOrthodox = new File("junit/debug/example_alignment_no_ambiguities_or_gaps.fa");			// standard plus a few ambiguities
	private File datasetExpanded = new File("junit/debug/example_alignment_ambiguities_expanded_no_gaps.fa"); 	// expanded translation hash, most unique ambiguity expansions
	private File datasetEmpty	 = new File("junit/debug/example_alignment_file_empty.fa"); 					// empty file, should fail
	private File datasetDebugGap = new File("junit/debug/example_alignment_some_ambiguities_and_gaps.fa");		// contains debug e.g. gaps in some codons
	private File datasetDeAllGap = new File("junit/debug/example_alignment_some_ambiguities_all_gaps.fa");		// contains debug e.g. at least one gaps in all codons
	private File datasetIllegal	 = new File("junit/debug/example_alignment_illegal_characters.fa");				// contains illegal characters, should translate as gaps
	private File[] inputFiles = {
			datasetOrthodox,
			datasetExpanded,
			datasetDebugGap,
			datasetDeAllGap,
			datasetIllegal,
			datasetEmpty
	};	// holder for all input files needed for test
	
	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.tests.AlignedSequenceRepresentationTest#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		for(File anInputFile:inputFiles){
			if(!anInputFile.exists()){fail("Could not find required input file "+anInputFile.getAbsolutePath());}
		}
		sourceDataASR = new AlignedSequenceRepresentation();
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.tests.AlignedSequenceRepresentationTest#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation#translate(boolean)}.
	 */
	public final void testTranslateOrthodoxModeOrthodoxSequences() {
		/* 
		 * this should load all sequences except empty file correctly
		 * but in normal translation mode all alignments will contain
		 * gaps except for normal File datasetOrthodox 
		 */
		try {
			sourceDataASR.loadSequences(datasetOrthodox, false);
		} catch (TaxaLimitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* translate it */
		try {
			sourceDataASR.translate(false);
		} catch (SequenceTypeNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* check there are no gaps */
		for(int seq=0;seq<sourceDataASR.getNumberOfTaxa();seq++){
			char[] sequence = sourceDataASR.getSequenceChars(seq);
			for(char translatedAA:sequence){
				if(translatedAA == '-'){
					fail("Translated sequence contains gaps"); // TODO
				}
			}
		}
	}

	public final void testLoadEmptyFileShouldFail(){
		try {
			sourceDataASR.loadSequences(this.datasetEmpty, false);
		} catch (TaxaLimitException e) {
			// will only get here if an taxaLimitException thrown
			fail("sjould not read an empty file");
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			// will only get here if an ArrayIndexOutOfBoundsException thrown but not a taxalimitexeption
			return;
		} catch (NullPointerException e){
			// will only get here if an ArrayIndexOutOfBoundsException thrown but not a taxalimitexeption
			return;
		}
		fail("Should never get here");
	}
	
	public final void testTranslateOrthodoxModeUnorthodoxSequences() {
		/* 
		 * this should load all sequences except empty file correctly
		 * but in normal translation mode all alignments will contain
		 * gaps except for normal File datasetOrthodox 
		 */
		for(int inputFile=1;inputFile<inputFiles.length-1;inputFile++){
			File alignment = inputFiles[inputFile];
			sourceDataASR = new AlignedSequenceRepresentation();
			try {
				sourceDataASR.loadSequences(alignment, false);
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* translate it by force*/
			try {
				sourceDataASR.forceTranslationIgnoringSequenceType();
			} catch (SequenceTypeNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* check there are no gaps */
			for(int seq=0;seq<sourceDataASR.getNumberOfTaxa();seq++){
				char[] sequence = sourceDataASR.getSequenceChars(seq);
				boolean gapSeen = false;
				for(char translatedAA:sequence){
					if(translatedAA == '-'){gapSeen = true;}
				}
				if(!gapSeen){fail("Translated sequence should  contain gaps (pass "+inputFile);}
			}
		}
	}
	
	public void testTranslateUnorthodoxModeOrthodoxSequences(){
		// use unorthodox translation hash and translate orthodox sequences
		// should pass when implemented
		
		// load an orthodox alignment
		// set mode=unorthodox : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// translate
		// should have no gaps or fail
		fail("not implemented");
	}

	public void testTranslateUnorthodoxModeUnorthodoxSequences(){
		// use unorthodox translation hash and translate unorthodox sequences
		// should pass when implemented for ungapped unorthodox sequences, fail for gapped unorthodox sequences
		
		// load an unorthodox alignment (no gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// force-translate
		// should have no gaps or fail
		fail("not implemented");
	}

	public void testTranslateUnorthodoxModeUnorthodoxGapSequences(){
		// use unorthodox translation hash and translate unorthodox and gapped sequences
		// should pass when implemented for ungapped unorthodox sequences, pass for gapped unorthodox sequences
		
		// load an unorthodox alignment (WITH some gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// force-translate
		// should have ????? some gaps or fail
		fail("not implemented");

		// load an unorthodox alignment (WITH all gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// force-translate
		// should have all gaps or fail
		fail("not implemented");
	}

	public void testTranslateUnorthodoxGapModeOrthodoxSequences(){
		// use unorthodox gap translation hash and translate orthodox sequences (normal plus unorthodox w/o gaps)
		// should pass when implemented for ungapped unorthodox sequences, fail for gapped unorthodox sequences
		
		// load an orthodox alignment
		// set mode=unorthodox+gap : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// translate
		// should have no gaps or fail
		fail("not implemented");
	}
	
	public void testTranslateUnorthodoxGapModeUnorthodoxSequences(){
		// use unorthodox gap translation hash and translate unorthodox sequences (unorthodox w/ gaps)
		// should pass when implemented for ungapped unorthodox sequences, pass for gapped unorthodox sequences

		// load an unorthodox alignment (no gaps)
		// set mode=unorthodox+gap : initialiseExpandedTranslationHashWithNoGapsInCodons()
		// force-translate
		// should have no gaps or fail
		fail("not implemented");
	}
	
	public void testTranslateUnorthodoxGapModeUnorthodoxGapSequences(){
		// use unorthodox gap translation hash and translate unorthodox and gapped sequences
		// should pass when implemented for orthodox sequences, ungapped unorthodox sequences, and gapped unorthodox sequences

		// load an unorthodox alignment (WITH some gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHash()
		// force-translate
		// should have ????? some gaps or fail
		fail("not implemented");

		// load an unorthodox alignment (WITH all gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHash()
		// force-translate
		// should have all gaps or fail
		fail("not implemented");

		// load an unorthodox alignment (WITH some gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHashWithOnlyGapsInCodons()
		// force-translate
		// should have ????? some gaps or fail
		fail("not implemented");

		// load an unorthodox alignment (WITH all gaps)
		// set mode=unorthodox : initialiseExpandedTranslationHashWithOnlyGapsInCodons()
		// force-translate
		// should have all gaps or fail
		fail("not implemented");
	}
}
