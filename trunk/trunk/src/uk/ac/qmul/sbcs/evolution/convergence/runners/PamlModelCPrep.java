package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;

public class PamlModelCPrep {

	String a = "a";
	String codeml = "\n\t  seqfile = paml.input.nt.phy \t\t* sequence data filename\n\t treefile = codemlMC-H111.tre\n      outfile = codemlMC.out           * main result file name\n\n        noisy = 9  * 0,1,2,3,9: how much rubbish on the screen\n      verbose = 0  * 0: concise; 1: detailed, 2: too much\n      runmode = 0  * 0: user tree\n\n      seqtype = 1  * 1:codons; 2:AAs; 3:codons-->AAs\n    CodonFreq = 2  * 0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table\n        clock = 0  * 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis\n\n  \t\t\t * Model C: model = 3  NSsites = 2 \n   \t\t\t * Model D: model = 3  NSsites = 3\n\n        model = 3  * 3 = clade models\n      NSsites = 2  * choose \"2\" or \"3\"\n\t\t\t \n        icode = 0  * 0:universal code; 1:mammalian mt; 2-10:see below\n\n    fix_kappa = 0  * 1: kappa fixed, 0: kappa to be estimated\n        kappa = 2  * initial or fixed kappa\n\n    fix_omega = 0  * 1: omega or omega_1 fixed, 0: estimate \n        omega = 0.1 * initial or fixed omega, for codons or codon-based AAs\n\n    fix_alpha = 1  * 0: estimate gamma shape parameter; 1: fix it at alpha\n        alpha = 0 * initial or fixed alpha, 0:infinity (constant rate)\n\n        ncatG = 2  * # of categories in dG of NSsites models\n\n        getSE = 0  * 0: don't want them, 1: want S.E.s of estimates\n RateAncestor = 0  * (0,1,2): rates (alpha>0) or ancestral states (1 or 2)\n\n   Small_Diff = .5e-6\n    cleandata = 0  * remove sites with ambiguity data (1:yes, 0:no)?\n\n";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PamlModelCPrep c = new PamlModelCPrep();
		try {
			c.go(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void go(String[] args) throws IOException, ClassNotFoundException{
		File input = new File(args[0]);
		File tree  = new File(input.getParent()+"/codemlMC-H111.tre");
		File ctl   = new File(input.getParent()+"/codemlMC-H111.ctl");
		InputStream serfile = new FileInputStream(input);
		String[] echolocatorsH2 = {"TURSIOPS","MEGADERMA","RHINOLOPHUS","MYOTIS","PTERONOTUS"};
		ObjectInputStream inOne = new ObjectInputStream(serfile);
		SitewiseSpecificLikelihoodSupportAaml candidate = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
		String[] trees = candidate.getFittedTrees();
		String H111 = trees[trees.length-1].replaceAll("\\s", "");
		TreeNode t = new TreeNode(H111,1);
		String labelled = t.printRecursivelyLabelling(echolocatorsH2);
		new CustomFileWriter(ctl, codeml  + "\n");
		new CustomFileWriter(tree,labelled+";\n");
		
	}
}
