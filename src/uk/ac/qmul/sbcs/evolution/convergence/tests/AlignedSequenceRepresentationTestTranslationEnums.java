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
			datasetEmpty,
			datasetDebugGap,
			datasetDeAllGap,
			datasetIllegal
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
	public final void testTranslateOrthodox() {
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

}
