/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.File;

import junit.framework.TestCase;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;

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
	public final void testTranslate() {
		fail("Not yet implemented"); // TODO
	}

}
