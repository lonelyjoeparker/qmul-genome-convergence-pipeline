package uk.ac.qmul.sbcs.evolution.sandbox;

import java.util.TreeSet;

import uk.ac.qmul.sbcs.evolution.convergence.EnnumeratedTranslatorOrthodoxCodons;

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
				'U',
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
		StringBuffer uniqueMappingsBuffer = new StringBuffer();
		uniqueMappingsBuffer.append("\nUnique mappings:\n");
		for(int first=0;first<nucleotides.length;first++){
			for(int second=0;second<nucleotides.length;second++){
				for(int third=0;third<nucleotides.length;third++){
					// Enumerate the combinations for all allowed characters
					System.out.println("indices\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]);
					// For each combination get the expanded set of corresponding DNA bases (A, C, G, T)
					char[] expanded_first  = SequenceCodonEnumeration.expandAmbiguities(nucleotides[first]);
					char[] expanded_second = SequenceCodonEnumeration.expandAmbiguities(nucleotides[second]);
					char[] expanded_third  = SequenceCodonEnumeration.expandAmbiguities(nucleotides[third]);
//					System.out.println("\tcorresponding real triplets: "+expanded_first.toString()+" "+expanded_second.toString()+" "+expanded_third.toString());
					
					/* For each expanded character permutation, 
					 * translate it and add to a set of unique 
					 * members.
					 * If the set only has a single member by the end, 
					 * this combination can be added to the overall 
					 * unique mappings buffer
					 */
					TreeSet<Character> possibleAAs = new TreeSet<Character>();
					for(int e_1=0;e_1<expanded_first.length;e_1++){
						for(int e_2=0;e_2<expanded_second.length;e_2++){
							for(int e_3=0;e_3<expanded_third.length;e_3++){
								// Now we have a potential code of actual nucleotides which might be translatable
								// Convert to string
								String codon = new String(""+expanded_first[e_1]+expanded_second[e_2]+expanded_third[e_3]);
								Character translated_AA = new EnnumeratedTranslatorOrthodoxCodons().translate(codon);
								possibleAAs.add(translated_AA);
								System.out.println("\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]+"\texpansion "+expanded_first[e_1]+expanded_second[e_2]+expanded_third[e_3]+" => "+translated_AA);
							}
						}
					}
					/* Check to see if possibleAAs set has size � 1 */
					if(possibleAAs.size() == 1){
						/* Only a single mapping was found over all 
						 * nucleotide-expansion permutations. 
						 * 
						 * Add this mapping to the uniqueMappings 
						 * buffer.
						 */
						uniqueMappingsBuffer.append("indices\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]+"=>"+possibleAAs.first()+"\n");
					}else{
						// more than one possible translation could be made from the expanded ambiguity chars;
						// we'll have to call this one undetermined (x)
						uniqueMappingsBuffer.append("indices\t"+nucleotides[first]+nucleotides[second]+nucleotides[third]+"=>X\n");
					}
				}
			}
		}
		System.out.println(uniqueMappingsBuffer);
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
			case('U'):return "U".toCharArray();
			case('-'):return "-".toCharArray();
			case('R'):return "AG".toCharArray();
			case('Y'):return "CU".toCharArray();
			case('M'):return "AC".toCharArray();
			case('K'):return "GU".toCharArray();
			case('S'):return "CG".toCharArray();
			case('W'):return "AU".toCharArray();
			case('H'):return "ACU".toCharArray();
			case('B'):return "CGU".toCharArray();
			case('V'):return "ACG".toCharArray();
			case('D'):return "AGU".toCharArray();
			case('N'):return "ACGU".toCharArray();
			default:return "N".toCharArray();
		}
	}
}
