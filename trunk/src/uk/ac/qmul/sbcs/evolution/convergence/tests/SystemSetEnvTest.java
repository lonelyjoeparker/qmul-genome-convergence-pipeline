package uk.ac.qmul.sbcs.evolution.convergence.tests;

import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;

public class SystemSetEnvTest {
	
	public static void main(String[] args){
		System.setProperty("user.dir", "/pamlTest/javaDir");
		new VerboseSystemCommand("pwd");
		new VerboseSystemCommand("perl runCmd.pl /pamlTest/javaDir pwd");
		new VerboseSystemCommand("/pamlTest/binaries/evolver 5 /Applications/Phylogenetics/PAML/paml44/MCbase.dat");
		new VerboseSystemCommand("perl runCmd.pl /pamlTest/javaDir /pamlTest/binaries/evolver 5 /Applications/Phylogenetics/PAML/paml44/MCbase.dat");
		new VerboseSystemCommand("/pamlTest/binaries/codeml /Applications/Phylogenetics/PAML/paml44_myVersion/aaml.ctl");
		new VerboseSystemCommand("perl runCmd.pl /pamlTest/javaDir /pamlTest/binaries/codeml /Applications/Phylogenetics/PAML/paml44_myVersion/aaml.ctl");
	}
}
