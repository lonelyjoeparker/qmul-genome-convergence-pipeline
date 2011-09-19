#! /usr/bin/perl

package BasemlAnalysis;

use Carp;

##### CLASS DEF ######
#
# 	Run baseml program of PAML.
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

$basemlTemplate = 
"     seqfile = DATA_FILE 
     treefile = TREE_FILE
      outfile = OUTPUT_FILE           * main result file name

        noisy = 2   * 0,1,2,3: how much rubbish on the screen
      verbose = 0   * 1: detailed output, 0: concise output
      runmode = 0   * 0: user tree;  1: semi-automatic;  2: automatic
                    * 3: StepwiseAddition; (4,5):PerturbationNNI 

        model = 7   * 0:JC69, 1:K80, 2:F81, 3:F84, 4:HKY85
                    * 5:T92, 6:TN93, 7:REV, 8:UNREST, 9:REVu; 10:UNRESTu

        Mgene = 0   * 0:rates, 1:separate; 2:diff pi, 3:diff kapa, 4:all diff

*       ndata = 100
        clock = 0   * 0:no clock, 1:clock; 2:local clock; 3:CombinedAnalysis
    fix_kappa = 0   * 0: estimate kappa; 1: fix kappa at value below
        kappa = 5  * initial or fixed kappa

    fix_alpha = 0   * 0: estimate alpha; 1: fix alpha at value below
        alpha = 0.5   * initial or fixed alpha, 0:infinity (constant rate)
       Malpha = 0   * 1: different alpha's for genes, 0: one alpha
        ncatG = 5   * # of categories in the dG, AdG, or nparK models of rates
        nparK = 0   * rate-class models. 1:rK, 2:rK&fK, 3:rK&MK(1/K), 4:rK&MK 

        nhomo = 0   * 0 & 1: homogeneous, 2: kappa for branches, 3: N1, 4: N2
        getSE = 0   * 0: don't want them, 1: want S.E.s of estimates
 RateAncestor = 0   * (0,1,2): rates (alpha>0) or ancestral states

   Small_Diff = 7e-6
    cleandata = 1  * remove sites with ambiguity data (1:yes, 0:no)?
*       icode = 0  * (with RateAncestor=1. try \"GC\" in data,model=4,Mgene=4)
* fix_blength = -1  * 0: ignore, -1: random, 1: initial, 2: fixed
       method = 0  * Optimization method 0: simultaneous; 1: one branch a time
";


sub runBasemlAnalysis{
	my $self = shift;
	my $dataFile = shift;
	my $treeFile = shift;
	my $outputFile = shift;
	my $wdir = shift;
	my $ctlFile = "$outputFile.baseml.ctl";
	my $executeStatus = 0;
	($baseml_ctl_file = $basemlTemplate) =~ s/DATA_FILE/$dataFile/;
	$baseml_ctl_file =~ s/TREE_FILE/$treeFile/;
	$baseml_ctl_file =~ s/OUTPUT_FILE/$outputFile/;
	open(OUT,'>',"$ctlFile") or die ("unable to open $ctlFile for writing\n");
	print OUT $baseml_ctl_file;
	close(OUT);
	$command = "./baseml $ctlFile >>stdout.txt 2>>stderr.txt";
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
		print "exited baseml OK ($elapsedTime s).\n";
	}
}

1;