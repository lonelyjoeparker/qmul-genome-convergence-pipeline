package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.io.File;
import java.util.HashMap;


import uk.ac.qmul.sbcs.evolution.convergence.SequenceCodingType;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;

public class EvolverDocument {

	private String activeTemplate;
	public String absolutePath;
	private File outputFile;
	public boolean hasBeenWritten;
	public final boolean initialisedWithSequenceType;
	protected HashMap<String, String> parameterSet = new HashMap<String, String>();
	public final SequenceCodingType sequenceType;

	String MCbaseTemplate = 
		"PARAM_PAMLFLAG          * 0: paml format (mc.paml); 1:paup format (mc.nex)\n" +
		"PARAM_SEED   * random number seed (odd number)\n" +
		"\n" +
		"PARAM_NUM_SEQS PARAM_NUM_SITES PARAM_NREPS  * <# seqs>  <# nucleotide sites>  <# replicates>\n" +
		"PARAM_TREE_LENGTH         * <tree length, use -1 if tree below has absolute branch lengths>\n" +
		"\n" +
		"PARAM_TREE_STRING\n" +
		"\n" +
		"PARAM_MODEL          * model: 0:JC69, 1:K80, 2:F81, 3:F84, 4:HKY85, 5:T92, 6:TN93, 7:REV\n" +
		"PARAM_RATES * kappa or rate parameters in model\n" +
		"PARAM_ALPHA  PARAM_NCATG     * <alpha>  <#categories for discrete gamma>\n" +
		"\n" +
		"PARAM_BASEFREQS    * base frequencies\n" +
		"  T        C        A        G\n" +
		"\n" +
		"\n" +
		"==================================================\n" +
		"The rest of this data file are notes, ignored by the program evolver.\n" +
		"Change the values of the parameters, but do not delete them.\n" +
		"evolver simulates nucleotide sequences under the REV+Gamma model\n" +
		"and its simpler forms.\n" +
		"\n" +
		"More notes:\n" +
		"  Parameter kappa or rate parameters in the substituton model: \n" +
		"    For TN93, two kappa values are required, while for REV, 5 values\n" +
		"    (a,b,c,d,e) are required (see Yang 1994 for the definition of these\n" +
		"    parameters).  \n" +
		"    The kappa parameter is defined differently under HKY85 (when k=1 means\n" +
		"    no transition bias) and under F84 (when k=0 means no bias).\n" +
		"    JC69 and F81 are considered species cases of HKY85, so use 1 for kappa\n" +
		"    for those two models.  Notation is from my two papers in JME in 1994.\n" +
		"  \n" +
		"  Use equal base frequencies (0.25) for JC69 and K80.\n" +
		"  Use 0 for alpha to have the same rate for all sites.\n" +
		"  Use 0 for <#categories for discrete gamma> to use the continuous gamma\n" +
		"\n" +
		"=========!! Check screen output carefully !! =====\n";

	String MCcodonTemplate = 
		"PARAM_PAMLFLAG          * 0,1:seqs or patters in paml format (mc.paml); 2:paup format (mc.nex)\n" +
		"PARAM_SEED       * random number seed (odd number)\n" +
		"PARAM_NUM_SEQS PARAM_NUM_SITES PARAM_NREPS   * <# seqs>  <# codons>  <# replicates>\n" +
		"\n" +
		"PARAM_TREE_LENGTH         * <tree length; see note below; use -1 if tree below has absolute branch lengths>\n" +
		"PARAM_TREE_STRING\n" +
		"\n" +
		"PARAM_OMEGA         * omega\n" +
		"PARAM_KAPPA           * kappa\n" +
		"\n" +
		"PARAM_CODONFREQS\n" +
		"\n" +
		"// end of file.\n" +
		"\n" +
		"============================================================================\n" +
		"Change values of parameters, but do not delete them.  You can add\n" +
		"empty lines, but do not break one line into several lines.\n" +
		"\n" +
		"Note that tree length and branch lengths under the codon model are\n" +
		"measured by the expected number of nucleotide substitutions per codon\n" +
		"(see Goldman & Yang 1994).  For amino acid models, they are defined as\n" +
		"the expected number of amino acid changes per amino acid site.\n" +
		"\n" +
		"64 codon freqs are in fixed order TTT, TTC, TTA, TTG, TCT, TCC, ..., GGG, \n" +
		"from abglobin.nuc under the F3x4 table\n" +
		"\n" +
		"=================!! Check screen output carefully!! ========================\n" +
		"\n" +
		"\n" +
		"(1)\n" +
		"mtCDNAape.nuc (7 apes):\n" +
		"position  1:    T:0.20517    C:0.28293    A:0.30784    G:0.20406\n" +
		"position  2:    T:0.40979    C:0.27911    A:0.18995    G:0.12116\n" +
		"position  3:    T:0.15105    C:0.43290    A:0.37123    G:0.04482\n" +
		"\n" +
		"    0.013116    0.037590    0.032235    0.003892\n" +
		"    0.008933    0.025603    0.021955    0.002650\n" +
		"    0.006080    0.017424    0           0\n" +
		"    0.003878    0.011114    0.009530    0.001151\n" +
		"    0.018086    0.051835    0.044451    0.005366\n" +
		"    0.012319    0.035306    0.030276    0.003655\n" +
		"    0.008384    0.024027    0.020604    0.002487\n" +
		"    0.005347    0.015325    0.013142    0.001587\n" +
		"    0.019679    0.056401    0.048366    0.005838\n" +
		"    0.013404    0.038415    0.032942    0.003977\n" +
		"    0.009122    0.026143    0.022419    0.002707\n" +
		"    0.005818    0.016675    0           0\n" +
		"    0.013045    0.037386    0.032060    0.003870\n" +
		"    0.008885    0.025464    0.021836    0.002636\n" +
		"    0.006047    0.017329    0.014861    0.001794\n" +
		"    0.003857    0.011053    0.009479    0.001144\n" +
		"\n" +
		"\n" +
		"(2)\n" +
		"bglobin.nuc (17 vertebrates):\n" +
		"position  1:    T:0.13930    C:0.23080    A:0.23652    G:0.39338\n" +
		"position  2:    T:0.31005    C:0.20997    A:0.32802    G:0.15196\n" +
		"position  3:    T:0.26675    C:0.34191    A:0.05923    G:0.33211\n" +
		"\n" +
		"    0.011746    0.015055    0.002608    0.014623\n" +
		"    0.007954    0.010195    0.001766    0.009903\n" +
		"    0.012426    0.015929    0           0\n" +
		"    0.005758    0.007379    0           0.007167\n" +
		"    0.019461    0.024944    0.004321    0.024229\n" +
		"    0.013179    0.016892    0.002926    0.016408\n" +
		"    0.020589    0.026390    0.004572    0.025634\n" +
		"    0.009538    0.012226    0.002118    0.011875\n" +
		"    0.019943    0.025562    0.004428    0.024829\n" +
		"    0.013506    0.017311    0.002999    0.016815\n" +
		"    0.021099    0.027044    0.004685    0.026269\n" +
		"    0.009774    0.012529    0.002170    0.012169\n" +
		"    0.033169    0.042516    0.007365    0.041297\n" +
		"    0.022463    0.028792    0.004988    0.027966\n" +
		"    0.035092    0.044980    0.007792    0.043691\n" +
		"    0.016257    0.020838    0.003611    0.020240";
	
	String MCaaTemplate = 
		"PARAM_PAMLFLAG          * 0,1:seqs or patters in paml format (mc.paml); 2:paup format (mc.nex)\n" +
		"PARAM_SEED       * random number seed (odd number)\n" +
		"PARAM_NUM_SEQS PARAM_NUM_SITES PARAM_NREPS   * <# seqs>  <# codons>  <# replicates>\n" +
		"\n" +
		"PARAM_TREE_LENGTH         * <tree length; see note below; use -1 if tree below has absolute branch lengths>\n" +
		"\n" +
		"PARAM_TREE_STRING\n" +
		"\n" +
		"PARAM_ALPHA PARAM_NCATG        * <alpha; see notes below>  <#categories for discrete gamma>\n" +
		"PARAM_MODEL PARAM_AARATEFILE * <model> [aa substitution rate file, need only if model=2 or 3]\n" +
		"\n" +
		"PARAM_AAFREQS\n" +
		"\n" +
		" A R N D C Q E G H I\n" +
		" L K M F P S T W Y V\n" +
		"\n" +
		"// end of file\n" +
		"\n" +
		"=============================================================================\n" +
		"Notes for using the option in evolver to simulate amino acid sequences. \n" +
		"Change values of parameters, but do not delete them.  It is o.k. to add \n" +
		"empty lines, but do not break down the same line into two or more lines.\n" +
		"\n" +
		"  model = 0 (poisson), 1 (proportional), 2 (empirical), 3 (empirical_F)\n" +
		"  Use 0 for alpha to have the same rate for all sites.\n" +
		"  Use 0 for <#categories for discrete gamma> to use the continuous gamma\n" +
		"  <aa substitution rate file> can be dayhoff.dat, jones.dat, and so on.\n" +
		"  <aa frequencies> have to be in the right order, as indicated.\n" +
		"=================!! Check screen output carefully!! =====================";	
	
	/**
	 * @param No-arg constructor; default type set to AA
	 * 
	 * This constructor should be avoided if possible, as there is no telling which template is used.
	 */
	public EvolverDocument(){
		this.activeTemplate = this.MCaaTemplate;
		this.sequenceType = SequenceCodingType.UNDEFINED;
		this.initialisedWithSequenceType = false;
	}

	/**
	 * 
	 * @param sequenceType - overloaded constructor; switch activeTemplate on enum.
	 * 
	 * This should be the preferred EvolverDocument constructor, since the template selection is safer.
	 */
	public EvolverDocument(SequenceCodingType sequenceType){
		this.sequenceType = sequenceType;
		boolean sequenceTypeSet;		// We are going to set the final variable initialisedWithSequenceType later, this will be a holder for now
		switch(sequenceType){
			case AA: 
				this.activeTemplate = this.MCaaTemplate; 
				sequenceTypeSet = true;
			case DNA: 
				this.activeTemplate = this.MCbaseTemplate;
				sequenceTypeSet = true;
			case RNA: 
				this.activeTemplate = this.MCbaseTemplate;
				sequenceTypeSet = true;
			case CODON: 
				this.activeTemplate = this.MCcodonTemplate;
				sequenceTypeSet = true;
			case UNDEFINED: 
				this.activeTemplate = this.MCaaTemplate;
				sequenceTypeSet = false;
			default: 
				this.activeTemplate = this.MCaaTemplate;
				sequenceTypeSet = false;
		}
		this.initialisedWithSequenceType = sequenceTypeSet;
	}
	
	/**
	 * 
	 * @param sequenceType - overloaded constructor; switch activeTemplate on enum.
	 * 
	 * This should be the preferred EvolverDocument constructor, since the template selection is safer.
	 */
	public EvolverDocument(SequenceCodingType sequenceType, File output){
		this.outputFile = output;
		this.absolutePath = this.outputFile.getAbsolutePath();
		this.sequenceType = sequenceType;
		boolean sequenceTypeSet;		// We are going to set the final variable initialisedWithSequenceType later, this will be a holder for now
		switch(sequenceType){
			case AA: 
				this.activeTemplate = this.MCaaTemplate; 
				sequenceTypeSet = true;
				System.out.println("this"+sequenceType+sequenceTypeSet);
				break;
			case DNA: 
				this.activeTemplate = this.MCbaseTemplate;
				sequenceTypeSet = true;
				break;
			case RNA: 
				this.activeTemplate = this.MCbaseTemplate;
				sequenceTypeSet = true;
				break;
			case CODON: 
				this.activeTemplate = this.MCcodonTemplate;
				sequenceTypeSet = true;
				break;
			case UNDEFINED: 
				this.activeTemplate = this.MCaaTemplate;
				sequenceTypeSet = false;
				break;
			default: 
				this.activeTemplate = this.MCaaTemplate;
				sequenceTypeSet = false;
		}
		this.initialisedWithSequenceType = sequenceTypeSet;
	}

	public void initialiseParameters(){
		if(this.initialisedWithSequenceType){
			// Switch on sequenceType and initialise template-specific parameters
			switch(sequenceType){
			case AA:
				// TODO;
				// Codon-specific (MCaa) parameters;
				this.setParameter("ALPHA",".5");
				this.setParameter("NCATG","8"); 
				this.setParameter("MODEL","2"); 
				this.setParameter("AARATEFILE","mtmam.dat"); 
				this.setParameter("AAFREQS",
						"0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 \n" +
						"0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 0.05 ");
				break;
			case CODON:
				// TODO;
				// Codon-specific (MCcodon) parameters;
				this.setParameter("OMEGA","0.3");
				this.setParameter("KAPPA","5");
				this.setParameter("CODONFREQS",
						"  0.00983798  0.01745548  0.00222048  0.01443315"+
						"  0.00844604  0.01498576  0.00190632  0.01239105"+
						"  0.01064012  0.01887870  0           0"+
						"  0.00469486  0.00833007  0           0.00688776"+
						"  0.01592816  0.02826125  0.00359507  0.02336796"+
						"  0.01367453  0.02426265  0.00308642  0.02006170"+
						"  0.01722686  0.03056552  0.00388819  0.02527326"+
						"  0.00760121  0.01348678  0.00171563  0.01115161"+
						"  0.01574077  0.02792876  0.00355278  0.02309304"+
						"  0.01351366  0.02397721  0.00305010  0.01982568"+
						"  0.01702419  0.03020593  0.00384245  0.02497593"+
						"  0.00751178  0.01332811  0.00169545  0.01102042"+
						"  0.02525082  0.04480239  0.00569924  0.03704508"+
						"  0.02167816  0.03846344  0.00489288  0.03180369"+
						"  0.02730964  0.04845534  0.00616393  0.04006555"+
						"  0.01205015  0.02138052  0.00271978  0.01767859");
				break;
			case DNA:
				// TODO;
				// Codon-specific (MCbase) parameters;
				this.setParameter("MODEL","7");
				this.setParameter("RATES","0.88892  0.03190  0.00001  0.07102  0.02418");
				this.setParameter("ALPHA","0.2500");
				this.setParameter("NCATG","4");
				this.setParameter("BASEFREQS","0.25318  0.32894  0.31196  0.10592");
				break;
			case RNA:
				// TODO;
				// Codon-specific (MCbase) parameters;
				this.setParameter("MODEL","7");
				this.setParameter("RATES","0.88892  0.03190  0.00001  0.07102  0.02418");
				this.setParameter("ALPHA","0.2500");
				this.setParameter("NCATG","4");
				this.setParameter("BASEFREQS","0.25318  0.32894  0.31196  0.10592");
				break;
			default:
			}
			// TODO;
			// Initialise common parameters
			int seed = this.getRandomOddInteger();
			this.setParameter("PAMLFLAG","0");
			this.setParameter("SEED",new Integer(seed).toString());
			this.setParameter("NUM_SEQS","5"); 
			this.setParameter("NUM_SITES","1000"); 
			this.setParameter("NREPS","10");
			this.setParameter("TREE_LENGTH","-1");
			this.setParameter("TREE_STRING","(((Human:0.06135, Chimpanzee:0.07636):0.03287, Gorilla:0.08197):0.11219, Orangutan:0.28339, Gibbon:0.42389);");
		}
	}
	
	public void finalizeParameters(){
		for(String param: parameterSet.keySet()){
			String value = parameterSet.get(param);
			activeTemplate = activeTemplate.replaceFirst("PARAM_"+param, value);
			System.out.println(param+" set to "+value+"... hopefully?");
			// FIXME why aren't we writing this activeTemplate correctly?
		}
	}

	public String getParameter(String key){
		return parameterSet.get(key);
	}
	
	public void printTemplate(){
		System.out.println(activeTemplate);
	}

	/**
	 * @since 11/10/2011
	 * @param param - Enum AamlParameters
	 * @param value - String value of this parameter in 'param = value' syntax
	 */
	public void setParameter(String param, String value){
		if(!parameterSet.containsKey(param.toString())){
			parameterSet.put(param.toString(), value);
		}else{
			parameterSet.remove(param.toString());
			parameterSet.put(param.toString(), value);
		}
	}

	public void write(File outputFile){
		finalizeParameters();
		try {
			assert(activeTemplate.length()>0);
			CustomFileWriter w = new CustomFileWriter(outputFile, activeTemplate);
			this.hasBeenWritten = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(String workingdir, String filename){
		finalizeParameters();
		try {
			BasicFileWriter w = new BasicFileWriter(workingdir+filename, activeTemplate);
			this.hasBeenWritten = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(){
		assert(this.outputFile.canWrite());
		finalizeParameters();
		try {
			assert(activeTemplate.length()>0);
			CustomFileWriter w = new CustomFileWriter(outputFile, activeTemplate);
			this.hasBeenWritten = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getRandomOddInteger(){
		int retval = 0;
		java.util.Random r = new java.util.Random();
		while(retval == 0){
			int seed = r.nextInt(999999);
//			double product = ((double)seed) / ((double)2);
//			double floor = Math.floor(product);
			if(seed % 2 != 0){
				retval = seed;
			}
		}
		return retval;
	}
}
