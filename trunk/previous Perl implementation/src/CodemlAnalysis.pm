#! /usr/bin/perl

package CodemlAnalysis;

use Carp;

##### CLASS DEF ######
#
# 	Run codeml program of PAML.
#
#	Still need to determine which parts of output to parse and return.
#
#
#	METHODS:
#
#	new()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	analyze()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####

sub new{
	my $self = bless {}, shift;
	return $self;
}


##### Codeml .ctl template #####

$codemlTemplate = 
"     seqfile = PARAM_DATA_FILE 
    treefile = PARAM_TREE_FILE
      outfile = PARAM_OUTPUT_FILE           * main result file name

        noisy = 0  * 0,1,2,3,9: how much rubbish on the screen
      verbose = 1  * 0: concise; 1: detailed, 2: too much
      runmode = 0  * 0: user tree;  1: semi-automatic;  2: automatic
                   * 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise

      seqtype = PARAM_SEQTYPE  * 1:codons; 2:AAs; 3:codons-->AAs
    CodonFreq = 2  * 0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table
       aaDist = 0  * 0:equal, +:geometric; -:linear, 1-6:G1974,Miyata,c,p,v,a
   aaRatefile = PARAM_AARATEMAT * only used for aa seqs with model=empirical(_F)
                   * dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own

        model = 0
                   * models for codons:
                       * 0:one, 1:b, 2:2 or more dN/dS ratios for branches
                   * models for AAs or codon-translated AAs:
                       * 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F
                       * 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)

      NSsites = 0  * 0:one w;1:neutral;2:selection; 3:discrete;4:freqs;
                   * 5:gamma;6:2gamma;7:beta;8:beta&w;9:beta&gamma;
                   * 10:beta&gamma+1; 11:beta&normal>1; 12:0&2normal>1;
                   * 13:3normal>0

        icode = 0  * 0:universal code; 1:mammalian mt; 2-10:see below
        Mgene = 0  * 0:rates, 1:separate; 

    fix_kappa = 0  * 1: kappa fixed, 0: kappa to be estimated
        kappa = 2  * initial or fixed kappa
    fix_omega = 1  * 1: omega or omega_1 fixed, 0: estimate 
        omega = .4 * initial or fixed omega, for codons or codon-based AAs

    fix_alpha = 0  * 0: estimate gamma shape parameter; 1: fix it at alpha
        alpha = 0.1 * initial or fixed alpha, 0:infinity (constant rate)
       Malpha = 0  * different alphas for genes
        ncatG = 8  * # of categories in dG of NSsites models

        clock = 0   * 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis
        getSE = 1  * 0: don't want them, 1: want S.E.s of estimates
 RateAncestor = 1  * (0,1,2): rates (alpha>0) or ancestral states (1 or 2)

   Small_Diff = .5e-6
*    cleandata = 0  * remove sites with ambiguity data (1:yes, 0:no)?
*        ndata = 10
*  fix_blength = -1  * 0: ignore, -1: random, 1: initial, 2: fixed
        method = 0   * 0: simultaneous; 1: one branch at a time


* Genetic codes: 0:universal, 1:mammalian mt., 2:yeast mt., 3:mold mt.,
* 4: invertebrate mt., 5: ciliate nuclear, 6: echinoderm mt., 
* 7: euplotid mt., 8: alternative yeast nu. 9: ascidian mt., 
* 10: blepharisma nu.
* These codes correspond to transl_table 1 to 11 of GENEBANK.
";


sub runCodemlAnalysis{
	my $self = shift;
	my $dataFile = shift;
	my $treeFile = shift;
	my $nameStem = shift;
	my $param_seqtype = shift;
	my $param_rateMatrix = shift;
	my $outputFile = $dataFile.".codeml.out";
	my $ctlFile;
	if(length($nameStem)>0){
		$ctlFile = $nameStem.".codeml.ctl";
	}else{
		$ctlFile = $dataFile.".codeml.ctl";
	}
	print length($nameStem)." ARG ".$ctlFile."\n";
	my $executeStatus = 0;
	($codeml_ctl_file = $codemlTemplate) =~ s/PARAM_DATA_FILE/$dataFile/;
	$codeml_ctl_file =~ s/PARAM_TREE_FILE/$treeFile/;
	$codeml_ctl_file =~ s/PARAM_OUTPUT_FILE/$outputFile/;
	if(length($param_seqtype)>0){
		$codeml_ctl_file =~ s/PARAM_SEQTYPE/$param_seqtype/;
	}else{
		$codeml_ctl_file =~ s/PARAM_SEQTYPE/1/;
	}
	if(length($param_rateMatrix)>0){
		$codeml_ctl_file =~ s/PARAM_AARATEMAT/$param_rateMatrix/;
	}else{
		$codeml_ctl_file =~ s/PARAM_AARATEMAT/jones.dat/;
	}
	open(OUT,'>',"$ctlFile") or die ("unable to open ".$ctlFile ."for writing\n");
	print OUT $codeml_ctl_file;
	close(OUT);
	$command = "./codeml $ctlFile >>stdout.txt 2>>stderr.txt";
	print "attempting command $command\n";
	$startTime = time();
	system(`$command`);
	$elapsedTime = time()-$startTime;
	if ($? == -1) {
		print "failed to execute: $!\n";
	}
	elsif ($? & 127) {
		printf "child died with signal %d, %s coredump\n",
			($? & 127),  ($? & 128) ? 'with' : 'without';
	}
	else {
		printf "child exited with value %d\n", $? >> 8;
		$executeStatus = 1;
	}		
	if($executeStatus>0){
		print "exited codeml OK ($elapsedTime s).\n";
	}
}


1;