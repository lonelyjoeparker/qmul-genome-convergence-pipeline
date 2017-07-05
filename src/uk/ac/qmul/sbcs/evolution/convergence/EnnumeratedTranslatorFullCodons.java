package uk.ac.qmul.sbcs.evolution.convergence;

import java.util.TreeMap;

/**
 * This class should translate any codon that doesn't evaluate to 'X' (the rest *will* return X)
 * A codon evaluates to !=X if and only if *every* possible combination of its ambiguity characters agrees.
 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
 * @since 5 Jul 2017
 * @version 0.1
 */
public final class EnnumeratedTranslatorFullCodons {

	private static TreeMap<String, Character>translationLookup = new TreeMap<String,Character>();
	static{
		translationLookup.put("AAA",'K');
		translationLookup.put("AAC",'N');
		translationLookup.put("AAG",'K');
		translationLookup.put("AAU",'N');
		translationLookup.put("AAR",'K');
		translationLookup.put("AAY",'N');
		translationLookup.put("AAR",'K');
		translationLookup.put("ACA",'T');
		translationLookup.put("ACC",'T');
		translationLookup.put("ACG",'T');
		translationLookup.put("ACU",'T');
		translationLookup.put("ACR",'T');
		translationLookup.put("ACY",'T');
		translationLookup.put("ACR",'T');
		translationLookup.put("ACM",'T');
		translationLookup.put("ACK",'T');
		translationLookup.put("ACS",'T');
		translationLookup.put("ACW",'T');
		translationLookup.put("ACH",'T');
		translationLookup.put("ACB",'T');
		translationLookup.put("ACV",'T');
		translationLookup.put("ACD",'T');
		translationLookup.put("ACN",'T');
		translationLookup.put("AGA",'R');
		translationLookup.put("AGC",'S');
		translationLookup.put("AGG",'R');
		translationLookup.put("AGU",'S');
		translationLookup.put("AGR",'R');
		translationLookup.put("AGY",'S');
		translationLookup.put("AGR",'R');
		translationLookup.put("AUA",'I');
		translationLookup.put("AUC",'I');
		translationLookup.put("AUG",'M');
		translationLookup.put("AUU",'I');
		translationLookup.put("AUY",'I');
		translationLookup.put("AUM",'I');
		translationLookup.put("AUW",'I');
		translationLookup.put("AUH",'I');
		translationLookup.put("CAA",'Q');
		translationLookup.put("CAC",'H');
		translationLookup.put("CAG",'Q');
		translationLookup.put("CAU",'H');
		translationLookup.put("CAR",'Q');
		translationLookup.put("CAY",'H');
		translationLookup.put("CAR",'Q');
		translationLookup.put("CCA",'P');
		translationLookup.put("CCC",'P');
		translationLookup.put("CCG",'P');
		translationLookup.put("CCU",'P');
		translationLookup.put("CCR",'P');
		translationLookup.put("CCY",'P');
		translationLookup.put("CCR",'P');
		translationLookup.put("CCM",'P');
		translationLookup.put("CCK",'P');
		translationLookup.put("CCS",'P');
		translationLookup.put("CCW",'P');
		translationLookup.put("CCH",'P');
		translationLookup.put("CCB",'P');
		translationLookup.put("CCV",'P');
		translationLookup.put("CCD",'P');
		translationLookup.put("CCN",'P');
		translationLookup.put("CGA",'R');
		translationLookup.put("CGC",'R');
		translationLookup.put("CGG",'R');
		translationLookup.put("CGU",'R');
		translationLookup.put("CGR",'R');
		translationLookup.put("CGY",'R');
		translationLookup.put("CGR",'R');
		translationLookup.put("CGM",'R');
		translationLookup.put("CGK",'R');
		translationLookup.put("CGS",'R');
		translationLookup.put("CGW",'R');
		translationLookup.put("CGH",'R');
		translationLookup.put("CGB",'R');
		translationLookup.put("CGV",'R');
		translationLookup.put("CGD",'R');
		translationLookup.put("CGN",'R');
		translationLookup.put("CUA",'L');
		translationLookup.put("CUC",'L');
		translationLookup.put("CUG",'L');
		translationLookup.put("CUU",'L');
		translationLookup.put("CUR",'L');
		translationLookup.put("CUY",'L');
		translationLookup.put("CUR",'L');
		translationLookup.put("CUM",'L');
		translationLookup.put("CUK",'L');
		translationLookup.put("CUS",'L');
		translationLookup.put("CUW",'L');
		translationLookup.put("CUH",'L');
		translationLookup.put("CUB",'L');
		translationLookup.put("CUV",'L');
		translationLookup.put("CUD",'L');
		translationLookup.put("CUN",'L');
		translationLookup.put("GAA",'E');
		translationLookup.put("GAC",'D');
		translationLookup.put("GAG",'E');
		translationLookup.put("GAU",'D');
		translationLookup.put("GAR",'E');
		translationLookup.put("GAY",'D');
		translationLookup.put("GAR",'E');
		translationLookup.put("GCA",'A');
		translationLookup.put("GCC",'A');
		translationLookup.put("GCG",'A');
		translationLookup.put("GCU",'A');
		translationLookup.put("GCR",'A');
		translationLookup.put("GCY",'A');
		translationLookup.put("GCR",'A');
		translationLookup.put("GCM",'A');
		translationLookup.put("GCK",'A');
		translationLookup.put("GCS",'A');
		translationLookup.put("GCW",'A');
		translationLookup.put("GCH",'A');
		translationLookup.put("GCB",'A');
		translationLookup.put("GCV",'A');
		translationLookup.put("GCD",'A');
		translationLookup.put("GCN",'A');
		translationLookup.put("GGA",'G');
		translationLookup.put("GGC",'G');
		translationLookup.put("GGG",'G');
		translationLookup.put("GGU",'G');
		translationLookup.put("GGR",'G');
		translationLookup.put("GGY",'G');
		translationLookup.put("GGR",'G');
		translationLookup.put("GGM",'G');
		translationLookup.put("GGK",'G');
		translationLookup.put("GGS",'G');
		translationLookup.put("GGW",'G');
		translationLookup.put("GGH",'G');
		translationLookup.put("GGB",'G');
		translationLookup.put("GGV",'G');
		translationLookup.put("GGD",'G');
		translationLookup.put("GGN",'G');
		translationLookup.put("GUA",'V');
		translationLookup.put("GUC",'V');
		translationLookup.put("GUG",'V');
		translationLookup.put("GUU",'V');
		translationLookup.put("GUR",'V');
		translationLookup.put("GUY",'V');
		translationLookup.put("GUR",'V');
		translationLookup.put("GUM",'V');
		translationLookup.put("GUK",'V');
		translationLookup.put("GUS",'V');
		translationLookup.put("GUW",'V');
		translationLookup.put("GUH",'V');
		translationLookup.put("GUB",'V');
		translationLookup.put("GUV",'V');
		translationLookup.put("GUD",'V');
		translationLookup.put("GUN",'V');
		translationLookup.put("UAA",'*');
		translationLookup.put("UAC",'Y');
		translationLookup.put("UAG",'*');
		translationLookup.put("UAU",'Y');
		translationLookup.put("UAR",'*');
		translationLookup.put("UAY",'Y');
		translationLookup.put("UAR",'*');
		translationLookup.put("UCA",'S');
		translationLookup.put("UCC",'S');
		translationLookup.put("UCG",'S');
		translationLookup.put("UCU",'S');
		translationLookup.put("UCR",'S');
		translationLookup.put("UCY",'S');
		translationLookup.put("UCR",'S');
		translationLookup.put("UCM",'S');
		translationLookup.put("UCK",'S');
		translationLookup.put("UCS",'S');
		translationLookup.put("UCW",'S');
		translationLookup.put("UCH",'S');
		translationLookup.put("UCB",'S');
		translationLookup.put("UCV",'S');
		translationLookup.put("UCD",'S');
		translationLookup.put("UCN",'S');
		translationLookup.put("UGA",'*');
		translationLookup.put("UGC",'C');
		translationLookup.put("UGG",'W');
		translationLookup.put("UGU",'C');
		translationLookup.put("UGY",'C');
		translationLookup.put("UUA",'L');
		translationLookup.put("UUC",'F');
		translationLookup.put("UUG",'L');
		translationLookup.put("UUU",'F');
		translationLookup.put("UUR",'L');
		translationLookup.put("UUY",'F');
		translationLookup.put("UUR",'L');
		translationLookup.put("URA",'*');
		translationLookup.put("URA",'*');
		translationLookup.put("YUA",'L');
		translationLookup.put("YUG",'L');
		translationLookup.put("YUR",'L');
		translationLookup.put("YUR",'L');
		translationLookup.put("MGA",'R');
		translationLookup.put("MGG",'R');
		translationLookup.put("MGR",'R');
		translationLookup.put("MGR",'R');
		translationLookup.put("---",'-');
	}
	
	public EnnumeratedTranslatorFullCodons() {
	}

	/**
	 * Translate a three-letter nucleotide triplet (codon) to an amino acid.
	 * Translation hash contains only triplets which unambiguously expand to non-X ('undetermined')
	 * Any other triplet will be returned as 'X'.
	 * @param codon
	 * @return Character representing an amino acid.
	 */
	public static final Character translate(String codon){
		Character translation;
		if(translationLookup.keySet().contains(codon)){
			translation = translationLookup.get(codon);
		}else{
			translation = 'X';
		}
		if(translation == null){
			translation = 'X';
		}
		return translation;
	}

}
