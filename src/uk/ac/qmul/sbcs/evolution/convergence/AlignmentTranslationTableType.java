/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * Enumerated type to hold allowable options defining the translation table (codons-to-amino acids) 
 * initialised in {@link AlignedSequenceRepresentation} alignment objects. This is the lookup table 
 * used in {@link AlignedSequenceRepresentation#translate()} operations, and which behaviour is 
 * selected will determine two things:
 * 
 * <ul><li>Which triplet codons map to which amino acids <i>("AAU" => 'N')</i>; and</li>
 * <li>Which codons are <i>mappable at all</i>, e.g. any codon not listed in the translation hash will
 * cause an {@link java.lang.ArrayIndexOutOfBoundsException} if encountered.</li></ul>
 * 
 * <h2>SIMPLE_CODONS_CONTAIN_PRIMARY_CHARACTERS_ONLY_AND_NO_GAPS_OR_AMBIGUITIES (default)</h2>
 * <p>The default behaviour (e.g. initialised in constructor) is for the simple translation table 
 * to be used. This includes a couple of the most common 3- or 4-fold degenerate codons only:
 * <pre>
	translationLookup.put("NNN",'X');
	translationLookup.put("UCN",'S');
	translationLookup.put("CCN",'P');
	translationLookup.put("CGN",'R');
	translationLookup.put("CUN",'L');
	translationLookup.put("ACN",'T');
	translationLookup.put("GUN",'V');
	translationLookup.put("GGN",'G');
	translationLookup.put("GCN",'A');
 * </pre>
 * <br/>This means that only codons comprised of the characters {A,C,G,U} with no gaps or 
 * ambiguities are used. Any codon containing a gap {-} or ambiguity {R,Y,B,D,H...} characters will 
 * cause a ArrayIndexOutOfBoundsException to be thrown when the AlignedSequenceRepresentation tries 
 * to translate() it. This will be hopefully caught, and usually translated as a gap ('-') in the 
 * resulting amino-acid sequence alignment as a result. 
 * <br/><i>Translation behaviour: {@link uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation#initialiseTranslationHash()}</i>
 * 
 * <h2>EXPANDED_CODONS_CONTAIN_AMBIGUITIES_NOT_GAPS (optional)</h2>
 * <p>This optional mode allows for codons to include a range of ambiguity characters but not gaps.
 * 
 * <br/>Codons' corresponding translations have been expanded (to include all possible AAs when
 * interpreting ambiguity characters) and all unique mappings included in the translation hash.
 * <br/><i>Translation behaviour: {@link uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation#initialiseExpandedTranslationHashWithNoGapsInCodons()}</i>
 * 
 * <h2>DEBUG_CODONS_CONTAIN_AMBIGUITIES_OR_GAPS (debug/testing only)</h2>
 * <p>This optional mode allows for codons to include either gaps or a range of ambiguity characters.
 * 
 * <br/>Codons' corresponding translations have been expanded (to include all possible AAs when
 * interpreting ambiguity characters) and all unique mappings included in the translation hash. The
 * inclusion of gaps is open to interpretation (since gaps usually represent indels e.g. often 
 * resulting in a frameshift mutation when present as singletons) but since they might be used to 
 * encode missing data instead of indel processes this option is included for debug/testing purposes.
 * <br/><i>Translation behaviour: {@link uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation#initialiseExpandedTranslationHash()}</i>
 * 
 * <h2>DEBUG_CODONS_CONTAIN_AT_LEAST_ONE_GAP_CAN_CONTAIN_AMBIGUITIES (debug/testing only)</h2>
 * <p>This optional mode allows for codons to include at least one gap and optionally a range of ambiguity characters.
 * 
 * <br/>Codons' corresponding translations have been expanded (to include all possible AAs when
 * interpreting ambiguity characters) and all unique mappings included in the translation hash. The
 * inclusion of gaps is open to interpretation (since gaps usually represent indels e.g. often 
 * resulting in a frameshift mutation when present as singletons) but since they might be used to 
 * encode missing data instead of indel processes this option is included for debug/testing purposes.
 * <br/><i>Translation behaviour: {@link uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation#initialiseExpandedTranslationHashWithOnlyGapsInCodons()}</i>
 * 
 * <p>For triplet specifications, see:
 * <ul><li>{@link https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/doc/translationTablesEnumerated.txt}</li>
 * <li>{@link https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/edit/master/trunk/doc/translationTablesEnumeratedTriplets.md}</li></ul>
 * 
 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
 * @since Aug 25, 2015
 * @version 0.1
 * @see AlignedSequenceRepresentation
 */
public enum AlignmentTranslationTableType {
	SIMPLE_CODONS_CONTAIN_PRIMARY_CHARACTERS_ONLY_AND_NO_GAPS_OR_AMBIGUITIES,		// Simple translation hash: only the letters {A,C,G,U} with no gaps
	EXPANDED_CODONS_CONTAIN_AMBIGUITIES_NOT_GAPS,									// Expanded translation hash: normal {A,C,G,U} and ambiguity {R,Y,B,D,H...} letters, with no gaps
	DEBUG_CODONS_CONTAIN_AMBIGUITIES_OR_GAPS,										// Debug only: as expanded hash, but can include gap characters
	DEBUG_CODONS_CONTAIN_AT_LEAST_ONE_GAP_CAN_CONTAIN_AMBIGUITIES					// Debug only: as expanded hash, and at least one gap character
}
