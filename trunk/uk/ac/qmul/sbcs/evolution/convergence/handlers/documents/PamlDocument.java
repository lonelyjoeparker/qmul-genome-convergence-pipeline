package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.util.*;

public class PamlDocument {
	protected HashMap<String, String> parameterSet = new HashMap<String, String>();
	public boolean hasBeenWritten;
	public String absolutePath;
	protected String aamlTemplate = 
    "	PARAM_SEQFILE 		* sequence data filename\n" +
    "	PARAM_TREEFILE    	* tree structure file name\n" +
    "\n" +
    "	PARAM_OUTFILE       * main result file name\n" +
    "	PARAM_NOISY  		* 0,1,2,3,9: how much rubbish on the screen\n" +
    "	PARAM_VERBOSE  		* 0: concise; 1: detailed, 2: too much\n" +
    "	PARAM_RUNMODE  		* 0: user tree;  1: semi-automatic;  2: automatic\n" +
    "                   	* 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise\n" +
    "\n" +
    "	PARAM_SEQTYPE  		* 1:codons; 2:AAs; 3:codons-->AAs\n" +
    "	PARAM_AARATEFILE 	* only used for aa seqs with model=empirical(_F)\n" +
    "                   	* dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own\n" +
    "\n" +
    "	PARAM_MODEL  		* 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F\n" +
    "                   	* 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)\n" +
    "	PARAM_MGENE  		* aaml: 0:rates, 1:separate; \n" +
    "\n" +
    "	PARAM_FIX_ALPHA  	* 0: estimate gamma shape parameter; 1: fix it at alpha\n" +
    "	PARAM_ALPHA 		* initial or fixed alpha, 0:infinity (constant rate)\n" +
    "	PARAM_ALPHA  		* different alphas for genes\n" +
    "	PARAM_NCATG  		* # of categories in dG of NSsites models\n" +
    "\n" +
    "	PARAM_CLOCK   		* 0:no clock, 1:global clock; 2:local clock; 3:TipDate\n" +
    "	PARAM_GETSE  		* 0: don't want them, 1: want S.E.s of estimates\n" +
    "	PARAM_RATEANCESTOR 	* (0,1,2): rates (alpha>0) or ancestral states (1 or 2)\n" +
    "\n" +
    "						* Genetic codes: 0:universal, 1:mammalian mt., 2:yeast mt., 3:mold mt.,\n" +
    "						* 4: invertebrate mt., 5: ciliate nuclear, 6: echinoderm mt., \n" +
    "						* 7: euplotid mt., 8: alternative yeast nu. 9: ascidian mt., \n" +
    "						* 10: blepharisma nu.\n" +
    "						* These codes correspond to transl_table 1 to 11 of GENEBANK.\n" +
    "\n" +
    "	PARAM_SMALL_DIFF\n" +
    "	PARAM_CLEANDATA  	* remove sites with ambiguity data (1:yes, 0:no)?\n" +
    "						*        ndata = 2\n" +
    "	PARAM_METHOD   		* 0: simultaneous; 1: one branch at a time";
	
	protected String basemlTemplate =
		"	PARAM_SEQFILE \n" +
		"	PARAM_TREEFILE\n" +
		"	\n" +
		"	PARAM_OUTFILE       * main result file\n" +
		"	PARAM_NOISY   		* 0,1,2,3: how much rubbish on the screen\n" +
		"	PARAM_VERBOSE   	* 1: detailed output, 0: concise output\n" +
		"	PARAM_RUNMODE   	* 0: user tree;  1: semi-automatic;  2: automatic\n" +
		"						* 3: StepwiseAddition; (4,5):PerturbationNNI \n" +
		"	\n" +
		"	PARAM_MODEL   		* 0:JC69, 1:K80, 2:F81, 3:F84, 4:HKY85\n" +
		"						* 5:T92, 6:TN93, 7:REV, 8:UNREST, 9:REVu; 10:UNRESTu\n" +
		"	\n" +
		"	PARAM_MGENE  		* 0:rates, 1:separate; 2:diff pi, 3:diff kapa, 4:all diff\n" +
		"	\n" +
		"*	PARAM_NDATA\n" +
		"	PARAM_CLOCK  		* 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis\n" +
		"	PARAM_FIX_KAPPA    	* 0: estimate kappa; 1: fix kappa at value below\n" +
		"	PARAM_KAPPA  		* initial or fixed kappa\n" +
		"	\n" +
		"	PARAM_FIX_ALPHA   	* 0: estimate alpha; 1: fix alpha at value below\n" +
		"	PARAM_ALPHA   		* initial or fixed alpha, 0:infinity (constant rate)\n" +
		"	PARAM_MALPHA   		* 1: different alpha's for genes, 0: one alpha\n" +
		"	PARAM_NCATG  		* # of categories in the dG, AdG, or nparK models of rates\n" +
		"	PARAMNPARK  		* rate-class models. 1:rK, 2:rK&fK, 3:rK&MK(1/K), 4:rK&MK \n" +
		"	\n" +
		"	PARAM_NHOMO   		* 0 & 1: homogeneous, 2: kappa for branches, 3: N1, 4: N2\n" +
		"	PARAM_GETSE   		* 0: don't want them, 1: want S.E.s of estimates\n" +
		"	PARAM_RATEANCESTOR	* (0,1,2): rates (alpha>0) or ancestral states\n" +
		"	\n" +
		"	PARAM_SMALLDIFF\n" +
		"	PARAM_CLEANDATA  	* remove sites with ambiguity data (1:yes, 0:no)?\n" +
		"*	PARAM_ICODE			* (with RateAncestor=1. try \"GC\" in data,model=4,Mgene=4)\n" +
		"*	PARAM_FIX_BLENGTH	* 0: ignore, -1: random, 1: initial, 2: fixed\n" +
		"	PARAM_METHOD  		* Optimization method 0: simultaneous; 1: one branch a time\n";
	
	protected String codemlTemplate = 
		"	PARAM_SEQFILE 		* sequence data filename\n" +
		"	PARAM_TREEFILE    	* tree structure file name\n" +
		"\n" +
		"	PARAM_OUTFILE       * main result file name\n" +
		"	PARAM_NOISY  		* 0,1,2,3,9: how much rubbish on the screen\n" +
		"	PARAM_VERBOSE  		* 0: concise; 1: detailed, 2: too much\n" +
		"	PARAM_RUNMODE  		* 0: user tree;  1: semi-automatic;  2: automatic\n" +
		"                   	* 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise\n" +
		"\n" +
		"	PARAM_SEQTYPE  		* 1:codons; 2:AAs; 3:codons-->AAs\n" +
		"	PARAM_CODONFREQ 	* 0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table\n" +
		"\n" +
		"* 	PARAM_NDATA		\n" +
		"	PARAM_CLOCK  		* 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis\n" +
		"	PARAM_AADIST		* 0:equal, +:geometric; -:linear, 1-6:G1974,Miyata,c,p,v,a\n" +
	    "	PARAM_AARATEFILE 	* only used for aa seqs with model=empirical(_F)\n" +
		"						* dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own\n" +
		"\n" +
	    "	PARAM_MODEL  		* 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F\n" +
		"						* models for codons:\n" +
		"						* 0:one, 1:b, 2:2 or more dN/dS ratios for branches\n" +
		"						* models for AAs or codon-translated AAs:\n" +
		"						* 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F\n" +
		"						* 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)\n" +
		"\n" +
		"	PARAM_NSSITES		* 0:one w;1:neutral;2:selection; 3:discrete;4:freqs;\n" +
		"						* 5:gamma;6:2gamma;7:beta;8:beta&w;9:beta&gamma;\n" +
		"						* 10:beta&gamma+1; 11:beta&normal>1; 12:0&2normal>1;\n" +
		"						* 13:3normal>0\n" +
		"\n" +
		"	PARAM_ICODE			* 0:universal code; 1:mammalian mt; 2-10:see below\n" +
		"	PARAM_MGENE\n" +
		"						* codon: 0:rates, 1:separate; 2:diff pi, 3:diff kapa, 4:all diff\n" +
		"						* AA: 0:rates, 1:separate\n" +
		"\n" +
		"	PARAM_FIX_KAPPA    	* 0: estimate kappa; 1: fix kappa at value below\n" +
		"	PARAM_KAPPA  		* initial or fixed kappa\n" +
		"	PARAM_FIX_OMEGA  	* 1: omega or omega_1 fixed, 0: estimate \n" +
		"	PARAM_OMEGA 		* initial or fixed omega, for codons or codon-based AAs\n" +
		"\n" +
		"	PARAM_FIX_ALPHA   	* 0: estimate alpha; 1: fix alpha at value below\n" +
		"	PARAM_ALPHA   		* initial or fixed alpha, 0:infinity (constant rate)\n" +
		"	PARAM_MALPHA   		* 1: different alpha's for genes, 0: one alpha\n" +
		"	PARAM_NCATG  		* # of categories in the dG, AdG, or nparK models of rates\n" +
		"\n" +
		"	PARAM_GETSE   		* 0: don't want them, 1: want S.E.s of estimates\n" +
		"	PARAM_RATEANCESTOR	* (0,1,2): rates (alpha>0) or ancestral states\n" +
		"\n" +
		"	PARAM_SMALLDIFF\n" +
		"	PARAM_CLEANDATA  	* remove sites with ambiguity data (1:yes, 0:no)?\n" +
		"*	PARAM_FIX_BLENGTH  	* 0: ignore, -1: random, 1: initial, 2: fixed\n" +
		"	PARAM_METHOD  		* Optimization method 0: simultaneous; 1: one branch a time\n" +
		"\n" +
		"						* Genetic codes: 0:universal, 1:mammalian mt., 2:yeast mt., 3:mold mt.,\n" +
		"						* 4: invertebrate mt., 5: ciliate nuclear, 6: echinoderm mt., \n" +
		"						* 7: euplotid mt., 8: alternative yeast nu. 9: ascidian mt., \n" +
		"						* 10: blepharisma nu.\n" +
		"						* These codes correspond to transl_table 1 to 11 of GENEBANK.\n";
	
	public void write(String workingdir){
		finalizeParameters();
		this.hasBeenWritten = true;
	}
	
	public void setParameter(String param, String value){
		if(!parameterSet.containsKey(param)){
			parameterSet.put(param, value);
		}else{
			parameterSet.remove(param);
			parameterSet.put(param, value);
		}
	}

	public void finalizeParameters(){
	}
	
	public void printTemplate(){
		System.out.println(aamlTemplate);
		System.out.println(basemlTemplate);
		System.out.println(codemlTemplate);
	}
}
