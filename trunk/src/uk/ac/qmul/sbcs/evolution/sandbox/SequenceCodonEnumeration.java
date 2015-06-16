package uk.ac.qmul.sbcs.evolution.sandbox;

/**
 * Ennumerates all possible codon combinations and attempts to translate them.
 * @author joeparker
 *
 */
public class SequenceCodonEnumeration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SequenceCodonEnumeration().ennumerateCombinations();
	}

	private void ennumerateCombinations(){
		char[] nucleotides = {
				'A',
				'C',
				'G',
				'T',
				'R',	// A|G
				'Y',	// C|T
				'R',	// A|G
				'M',	// A|C
				'K',	// G|T
				'S',	// C|G
				'W',	// A|T
				'H',	// A|C|T
				'B',	// C|G|T
				'V',	// A|C|G
				'D',	// A|G|T
				'N',	// A|C|G|T
				'-'	// gap
		};
		for(int first=0;first<nucleotides.length;first++){
			for(int second=0;second<nucleotides.length;second++){
				for(int third=0;third<nucleotides.length;third++){
					// Enumerate the combinations for all allowed characters
					System.out.println("indices\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]);
					// For each combination get the expanded set of corresponding DNA bases (A, C, G, T)
					char[] expanded_first = SequenceCodonEnumeration.expandAmbiguities(nucleotides[first]);
					char[] expanded_second = SequenceCodonEnumeration.expandAmbiguities(nucleotides[second]);
					char[] expanded_third = SequenceCodonEnumeration.expandAmbiguities(nucleotides[third]);
//					System.out.println("\tcorresponding real triplets: "+expanded_first.toString()+" "+expanded_second.toString()+" "+expanded_third.toString());
					for(int e_1=0;e_1<expanded_first.length;e_1++){
						for(int e_2=0;e_2<expanded_second.length;e_2++){
							for(int e_3=0;e_3<expanded_third.length;e_3++){
								// Now we have a potential code of actual nucleotides which might be translatable
								// Convert to string
								String codon = new String(""+expanded_first[e_1]+expanded_second[e_2]+expanded_third[e_3]);
								Character translated_AA = new SequenceTranslationHashAccessor().translate(codon);
								System.out.println("\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]+"\texpansion "+expanded_first[e_1]+expanded_second[e_2]+expanded_third[e_3]+" => "+translated_AA);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Take a single nucleotide (which may be an IUPAC ambiguity code) and return all possible nucleotides
	 * <p>e.g.:
	 * <ul><li>A --> {A}</li>
	 * <li>V --> {A,C,G}</li>
	 * <li>N --> {A,C,G,T}</li>
 	 * <li>- --> {-}</li>
	 * @param nucleotideChar
	 * @return char[] of allowable ambiguities
	 */
	private static char[] expandAmbiguities(char nucleotideChar){
		switch(nucleotideChar){
			case('A'):return "A".toCharArray();
			case('C'):return "C".toCharArray();
			case('G'):return "G".toCharArray();
			case('T'):return "T".toCharArray();
			case('-'):return "-".toCharArray();
			case('R'):return "AG".toCharArray();
			case('Y'):return "CT".toCharArray();
			case('M'):return "AC".toCharArray();
			case('K'):return "GT".toCharArray();
			case('S'):return "CG".toCharArray();
			case('W'):return "AT".toCharArray();
			case('H'):return "ACT".toCharArray();
			case('B'):return "CGT".toCharArray();
			case('V'):return "ACG".toCharArray();
			case('D'):return "AGT".toCharArray();
			case('N'):return "ACGT".toCharArray();
			default:return "N".toCharArray();
		}
	}
}
