#! /usr/bin/perl

package AamlAnalysis;

use Carp;

##### CLASS DEF ######
#
# 	Run aaml program of PAML.
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

$aamlTemplate = 
"     seqfile = PARAM_DATA_FILE 
     treefile = PARAM_TREE_FILE
      outfile = PARAM_OUTPUT_FILE           * main result file name

        noisy = 9  * 0,1,2,3,9: how much rubbish on the screen
      verbose = 1  * 0: concise; 1: detailed, 2: too much
      runmode = 0  * 0: user tree;  1: semi-automatic;  2: automatic
                   * 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise

      seqtype = PARAM_SEQTYPE  * 1:codons; 2:AAs; 3:codons-->AAs
   aaRatefile = PARAM_AARATEMAT * only used for aa seqs with model=empirical(_F)
                   * dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own

        model = 0  * 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F
                   * 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)
        Mgene = 0  * aaml: 0:rates, 1:separate; 

    fix_alpha = 0  * 0: estimate gamma shape parameter; 1: fix it at alpha
        alpha = 0. * initial or fixed alpha, 0:infinity (constant rate)
       Malpha = 0  * different alphas for genes
        ncatG = 2  * # of categories in dG of NSsites models

        clock = 0   * 0:no clock, 1:global clock; 2:local clock; 3:TipDate
        getSE = 0  * 0: don't want them, 1: want S.E.s of estimates
 RateAncestor = 1 * (0,1,2): rates (alpha>0) or ancestral states (1 or 2)

* Genetic codes: 0:universal, 1:mammalian mt., 2:yeast mt., 3:mold mt.,
* 4: invertebrate mt., 5: ciliate nuclear, 6: echinoderm mt., 
* 7: euplotid mt., 8: alternative yeast nu. 9: ascidian mt., 
* 10: blepharisma nu.
* These codes correspond to transl_table 1 to 11 of GENEBANK.

   Small_Diff = .5e-6
     cleandata = 1  * remove sites with ambiguity data (1:yes, 0:no)?
*        ndata = 2
        method = 0   * 0: simultaneous; 1: one branch at a time
";

$codonAamlTemplate = 
"     seqfile = DATA_FILE 
     treefile = TREE_FILE
      outfile = OUTPUT_FILE           * main result file name

        noisy = 9  * 0,1,2,3,9: how much rubbish on the screen
      verbose = 0  * 0: concise; 1: detailed, 2: too much
      runmode = 0  * 0: user tree;  1: semi-automatic;  2: automatic
                   * 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise

      seqtype = 3  * 1:codons; 2:AAs; 3:codons-->AAs
   aaRatefile = /Applications/Phylogenetics/paml44/dat/jones.dat * only used for aa seqs with model=empirical(_F)
                   * dayhoff.dat, jones.dat, wag.dat, mtmam.dat, or your own

        model = 3  * 0:poisson, 1:proportional, 2:Empirical, 3:Empirical+F
                   * 6:FromCodon, 7:AAClasses, 8:REVaa_0, 9:REVaa(nr=189)
        Mgene = 0  * aaml: 0:rates, 1:separate; 

    fix_alpha = 1  * 0: estimate gamma shape parameter; 1: fix it at alpha
        alpha = 0. * initial or fixed alpha, 0:infinity (constant rate)
       Malpha = 0  * different alphas for genes
        ncatG = 2  * # of categories in dG of NSsites models

        clock = 0   * 0:no clock, 1:global clock; 2:local clock; 3:TipDate
        getSE = 0  * 0: don't want them, 1: want S.E.s of estimates
 RateAncestor = 1 * (0,1,2): rates (alpha>0) or ancestral states (1 or 2)

* Genetic codes: 0:universal, 1:mammalian mt., 2:yeast mt., 3:mold mt.,
* 4: invertebrate mt., 5: ciliate nuclear, 6: echinoderm mt., 
* 7: euplotid mt., 8: alternative yeast nu. 9: ascidian mt., 
* 10: blepharisma nu.
* These codes correspond to transl_table 1 to 11 of GENEBANK.

   Small_Diff = .5e-6
     cleandata = 1  * remove sites with ambiguity data (1:yes, 0:no)?
*        ndata = 2
        method = 0   * 0: simultaneous; 1: one branch at a time
";


sub runAamlAnalysis{
	my $self = shift;
	my $dataFile = shift;
	my $treeFile = shift;
	my $nameStem = shift;
	my $param_seqtype = shift;
	my $param_rateMatrix = shift;
	my $outputFile = $dataFile.".aaml.out";
	my $ctlFile;
	if(length($nameStem)>0){
		$ctlFile = $nameStem.".aaml.ctl";
	}else{
		$ctlFile = $dataFile.".aaml.ctl";
	}
	my $executeStatus = 0;
	($aaml_ctl_file = $aamlTemplate) =~ s/PARAM_DATA_FILE/$dataFile/;
	$aaml_ctl_file =~ s/PARAM_TREE_FILE/$treeFile/;
	$aaml_ctl_file =~ s/PARAM_OUTPUT_FILE/$outputFile/;
	if(length($param_seqtype)>0){
		$aaml_ctl_file =~ s/PARAM_SEQTYPE/$param_seqtype/;
	}else{
		$aaml_ctl_file =~ s/PARAM_SEQTYPE/1/;
	}
	if(length($param_rateMatrix)>0){
		$aaml_ctl_file =~ s/PARAM_AARATEMAT/$param_rateMatrix/;
	}else{
		$aaml_ctl_file =~ s/PARAM_AARATEMAT/jones.dat/;
	}
	open(OUT,'>',"$ctlFile") or die ("unable to open $ctlFile.ctl for writing\n");
	print OUT $aaml_ctl_file;
	close(OUT);
	$command = "./codeml $ctlFile >stdout.txt 2>stderr.txt";
	print "attempting command $command\n";
	$startTime = time();
	system(`$command`);
	$elapsedTime = time()-$startTime;
	if ($? == -1) {
		print "failed to execute: $! ($elapsedTime s)\n";
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
		print "exited aaml OK ($elapsedTime s).\n";
	}
}

sub runCodonAamlAnalysis{
	my $self = shift;
	my $dataFile = shift;
	my $treeFile = shift;
	my $outputFile = shift;
	my $ctlFile = "$outputFile.aaml.ctl";
	my $executeStatus = 0;
	($aaml_ctl_file = $aamlTemplate) =~ s/DATA_FILE/$dataFile/;
	$aaml_ctl_file =~ s/TREE_FILE/$treeFile/;
	$aaml_ctl_file =~ s/OUTPUT_FILE/$outputFile/;
	open(OUT,'>',"./$ctlFile") or die ("unable to open ./$ctlFile.ctl for writing\n");
	print OUT $aaml_ctl_file;
	close(OUT);
	$command = "/Applications/Phylogenetics/paml44/bin/aaml ./$ctlFile >stdout.txt 2>stderr.txt";
	print "attempting command $command\n";
	$startTime = time();
	system(`$command`);
	$elapsedTime = time()-$startTime;
	if ($? == -1) {
		print "failed to execute: $! ($elapsedTime s)\n";
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
		print "exited aaml OK ($elapsedTime s).\n";
	}
}

1;