/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * <br/>TODO should consider refactoring to the PamlAnalysis > CodemlAnalysis inheritance tree eventually
 * @see uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand
 * @see java.io.File
 * @see uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml
 * @see uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation
 * 
 */
public class CodemlAncestralPrepareAndRun {

	private AlignedSequenceRepresentation inputAlignmentData = null;	// the alignment (ASR)
	private SitewiseSpecificLikelihoodSupportAaml inputSSLSserfileData = null;	// the SSLS serfile, should include fitted trees and model parameters as well as the ASR
	private File codemlAncestralBinary = null;	// full path to the codeml-ancestral binary

	/** 
	 * Intended constructor for this class. java.io.File.canRead() should already have been called on these args to minimally guarantee they exist...
	 * @param serfile - full path to a serialised uk.ac.qmul.sbcs.evolution.convergence.util.SiteSpecificLikelihoodSupportAaml
	 * @param codeml_ancestral - full path to a codeml-ancestral binary
	 * @see java.io.File
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml
	 * @see uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation
	 */
	public CodemlAncestralPrepareAndRun(File serfile, File codeml_ancestral) {
		try {
			this.inputSSLSserfileData = (SitewiseSpecificLikelihoodSupportAaml) new ObjectInputStream(new FileInputStream(serfile)).readObject();
			this.inputAlignmentData = inputSSLSserfileData.getDataset();
			this.codemlAncestralBinary = codeml_ancestral;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Usage: CodemlAncestralPreparation &lt;input serfile&gt; &lt;codeml-ancestral binary path&gt;
	 * @param args - &lt;input serfile&gt; &lt;codeml-ancestral binary path&gt;
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length >1){
			File serfile = new File(args[0]);
			File codeml_ancestral = new File(args[1]);
			if(serfile.canRead() && codeml_ancestral.canRead()){
				CodemlAncestralPrepareAndRun anc = new CodemlAncestralPrepareAndRun(serfile, codeml_ancestral);
				anc.run();
			}
		}
	}

	/**
	 * Run an instantiated CodemlAncestralPrepareAndRun
	 * @see uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand
	 */
	private void run(){
		if(this.inputAlignmentData != null){
			/* get the parameters (fitted tree, alpha) from SSLS *serfile */
			NewickTreeRepresentation newickInputTree = new NewickTreeRepresentation(this.inputSSLSserfileData.getFittedTrees()[0], this.inputSSLSserfileData.getTaxaList());
			float modelAlpha = this.inputSSLSserfileData.getAlpha()[0];
			int noisy = 0; // noise of codeml-anc screen output
			int verbosity = 2; // verbosity of codeml-anc file output
			AlignedSequenceRepresentation fromScratch = null;
			
			/* if the input file has already been translated, will need to write a new one, gaah.. */
			if(this.inputAlignmentData.isAA()){
				fromScratch = new AlignedSequenceRepresentation();
				try {
					fromScratch.loadSequences(this.inputAlignmentData.getRawInput(), false);
				} catch (TaxaLimitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				fromScratch = this.inputAlignmentData;
			}
			
			/* write the inputs for codeml-anc */
			String pathToTreefile = "codeml_anc.tre";
			String pathToCodonfile = "codeml_anc.phy";
			String pathToCtlfile  = "codeml-anc.ctl";
			String ctlfileContent = generateSerfile(pathToCodonfile, pathToTreefile, noisy, verbosity, modelAlpha);
			fromScratch.writePhylipFile(pathToCodonfile, true);
			newickInputTree.write(new File(pathToTreefile));
			new BasicFileWriter(new File(pathToCtlfile),ctlfileContent);
			
			/* attempt to run codeml-anc... */
			String exeString = (this.codemlAncestralBinary.getAbsolutePath() + " " + pathToCtlfile);
			System.out.println(exeString);
			new VerboseSystemCommand(exeString);
		}
	}

	private String generateSerfile(String pathToCodonfile,	String pathToTreefile, int noisy, int verbosity, float modelAlpha) {
		// TODO Auto-generated method stub
		final String ctl_1 = "      seqfile = ";
		final String ctl_2 = " * sequence data filename\n		     treefile = ";
		final String ctl_3 = "    * tree structure file name\n      outfile = posteriorResults.out           * main result file name\n\n        noisy = ";
		final String ctl_4 = "  * 0,1,2,3,9: how much rubbish on the screen\n      verbose = ";
		final String ctl_5 = " * 0: concise; 1: detailed, 2: too much\n      runmode = 0  * 0: user tree;  1: semi-automatic;  2: automatic\n                   * 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise\n\n      seqtype = 3  * 1:codons; 2:AAs; 3:codons-->AAs\n    CodonFreq = 2  * 0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table\n\n*        ndata = 10\n        clock = 0  * 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis\n       aaDist = 0  * 0:equal, +:geometric; -:linear, 1-6:G1974,Miyata,c,p,v,a\n   aaRatefile = ./dat/wag.dat  * only used for aa seqs with model=empirical(_F)\n                   * dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own\n\n        model = 3 \n                   * models for codons:\n                       * 0:one, 1:b, 2:2 or more dN/dS ratios for branches\n                   * models for AAs or codon-translated AAs:\n                       * 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F\n                       * 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)\n\n      NSsites = 0  * 0:one w;1:neutral;2:selection; 3:discrete;4:freqs;\n                   * 5:gamma;6:2gamma;7:beta;8:beta&w;9:beta&gamma;\n                   * 10:beta&gamma+1; 11:beta&normal>1; 12:0&2normal>1;\n                   * 13:3normal>0\n\n        icode = 0  * 0:universal code; 1:mammalian mt; 2-10:see below\n        Mgene = 0\n                   * codon: 0:rates, 1:separate; 2:diff pi, 3:diff kapa, 4:all diff\n                   * AA: 0:rates, 1:separate\n\n    fix_kappa = 0  * 1: kappa fixed, 0: kappa to be estimated\n        kappa = 3  * initial or fixed kappa\n    fix_omega = 0  * 1: omega or omega_1 fixed, 0: estimate \n        omega = .4 * initial or fixed omega, for codons or codon-based AAs\n\n    fix_alpha = 1  * 0: estimate gamma shape parameter; 1: fix it at alpha\n        alpha = ";
		final String ctl_6 = "  * initial or fixed alpha, 0:infinity (constant rate)\n       Malpha = 0  * different alphas for genes\n        ncatG = 5  * # of categories in dG of NSsites models\n\n        getSE = 0  * 0: don't want them, 1: want S.E.s of estimates\n RateAncestor = 2  * (0,1,2): rates (alpha>0) or ancestral states (1 or 2)\n\n   Small_Diff = .5e-6\n    cleandata = 0 \n  fix_blength = 2 * 0: ignore, -1: random, 1: initial, 2: fixed\n        method = 0   * 0: simultaneous; 1: one branch at a time";
		return ctl_1 + pathToCodonfile + ctl_2 + pathToTreefile + ctl_3 + noisy + ctl_4 + verbosity + ctl_5 + modelAlpha + ctl_6;
	}
}
