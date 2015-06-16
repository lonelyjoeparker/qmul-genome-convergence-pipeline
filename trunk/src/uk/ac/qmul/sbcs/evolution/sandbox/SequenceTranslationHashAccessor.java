package uk.ac.qmul.sbcs.evolution.sandbox;

import java.util.TreeMap;

public class SequenceTranslationHashAccessor {

	private static TreeMap<String, Character>translationLookup = new TreeMap<String,Character>();
	
	public SequenceTranslationHashAccessor(){
	/*
	 * Ambiguity characters (ONLY SOME OF (4-fold 3rd position): TODO include all)
	 */
	translationLookup.put("NNN",'X');
	translationLookup.put("UCN",'S');
	translationLookup.put("CCN",'P');
	translationLookup.put("CGN",'R');
	translationLookup.put("CUN",'L');
	translationLookup.put("ACN",'T');
	translationLookup.put("GUN",'V');
	translationLookup.put("GGN",'G');
	translationLookup.put("GCN",'A');
	/*
	 * Orthodox / fully-specified characters
	 */
	// U
	translationLookup.put("UUU",'F');
	translationLookup.put("UUC",'F');
	translationLookup.put("UUA",'L');
	translationLookup.put("UUG",'L');
	translationLookup.put("UGU",'C');
	translationLookup.put("UGC",'C');
	translationLookup.put("UAU",'Y');
	translationLookup.put("UAC",'Y');
	translationLookup.put("UCU",'S');
	translationLookup.put("UCC",'S');
	translationLookup.put("UCA",'S');
	translationLookup.put("UCG",'S');
	translationLookup.put("UAA",'*');
	translationLookup.put("UGA",'*');
	translationLookup.put("UAG",'*');
	translationLookup.put("UGG",'W');
	// C
	translationLookup.put("CUU",'L');
	translationLookup.put("CUC",'L');
	translationLookup.put("CUA",'L');
	translationLookup.put("CUG",'L');
	translationLookup.put("CCU",'P');
	translationLookup.put("CCC",'P');
	translationLookup.put("CCA",'P');
	translationLookup.put("CCG",'P');
	translationLookup.put("CGU",'R');
	translationLookup.put("CGC",'R');
	translationLookup.put("CGA",'R');
	translationLookup.put("CGG",'R');
	translationLookup.put("CAA",'Q');
	translationLookup.put("CAG",'Q');
	translationLookup.put("CAU",'H');
	translationLookup.put("CAC",'H');
	// A
	translationLookup.put("AUU",'I');
	translationLookup.put("AUC",'I');
	translationLookup.put("AUA",'I');
	translationLookup.put("AUG",'M');
	translationLookup.put("ACU",'T');
	translationLookup.put("ACC",'T');
	translationLookup.put("ACA",'T');
	translationLookup.put("ACG",'T');
	translationLookup.put("AAU",'N');
	translationLookup.put("AAC",'N');
	translationLookup.put("AAA",'K');
	translationLookup.put("AAG",'K');
	translationLookup.put("AGA",'R');
	translationLookup.put("AGG",'R');
	translationLookup.put("AGC",'S');
	translationLookup.put("AGU",'S');
	// G
	translationLookup.put("GUU",'V');
	translationLookup.put("GUC",'V');
	translationLookup.put("GUA",'V');
	translationLookup.put("GUG",'V');
	translationLookup.put("GGU",'G');
	translationLookup.put("GGC",'G');
	translationLookup.put("GGA",'G');
	translationLookup.put("GGG",'G');
	translationLookup.put("GCU",'A');
	translationLookup.put("GCC",'A');
	translationLookup.put("GCA",'A');
	translationLookup.put("GCG",'A');
	translationLookup.put("GAC",'D');
	translationLookup.put("GAU",'D');
	translationLookup.put("GAA",'E');
	translationLookup.put("GAG",'E');
	}
	
	public static Character translate(String codon){
		return translationLookup.get(codon);
	}
}
