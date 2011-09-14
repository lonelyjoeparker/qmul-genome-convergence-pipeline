#! /usr/bin/perl

package ExternalFunctions;

use Carp;
use PhymlSequenceRepresentation;
use NewickTreeRepresentation;

##### CLASS DEF ######
#
# 	A utility class of external applications calls
#
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
#	runConselAnalysis()
#		purpose:	analyse the output from two PAML likelihood optimizations.
#		args:		filenames of two PAML lnf files.
#		returns:	support for first topology.
#		execution:	concatenate files' lnL matrices
#					run consel -paml
#					parse output
#
#
#	runPhymlTree()
#		purpose:	no-arg constructor.
#		args:		String filename (address to phylip file) [String treefilename (address to starting tree file)].
#		returns:	NewickTreeRepresentation NTR.
#		execution:	Take a sequence alignment and optional starting tree
#					Print out their respective formats as required by phyml
#					Run phyml and collect tree
#					Create a new NewickTreeRepresentation with the new tree
#
#
#	runCodemlAnalysis()
#		purpose:	perform codeml analysis of an input dataset and treefile.
#		args:		String filename (address to phylip file) String treefilename (address to starting tree file).
#		returns:	String filename (address to lnF file)
#		execution:	grep file paths to pre-existing .ctl string
#					print out .ctl string as file
#					run codeml
#
#
#	parsePamlSitewiseSSLS()
#		purpose:	parse 2 lnf files from Paml and alignment to get site patterns' lnL values and return sitewise SSLS
#		args:		lnf1; lnf2; (REF TO) alignment sites array (transposed)
#		returns:	array of SSLS values ordered input file 1 pref = positive
#		execution:	reference site array
#					read in lnF files site patterns' lnL values to arrays
#					for each site in alignment transposition:
#						: iterate lnF 1 to get matching lnL
#						: iterate lnF 2 to get matching lnL
#						: calculate SSLS as 1-2
#					return array
#
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####

##### Codeml .ctl template #####

$codemlTemplate = 
"     seqfile = DATA_FILE 
    treefile = TREE_FILE
      outfile = OUTPUT_FILE           * main result file name

        noisy = 0  * 0,1,2,3,9: how much rubbish on the screen
      verbose = 1  * 0: concise; 1: detailed, 2: too much
      runmode = 0  * 0: user tree;  1: semi-automatic;  2: automatic
                   * 3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise

      seqtype = 1  * 1:codons; 2:AAs; 3:codons-->AAs
    CodonFreq = 2  * 0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table
       aaDist = 0  * 0:equal, +:geometric; -:linear, 1-6:G1974,Miyata,c,p,v,a
   aaRatefile = wag.dat * only used for aa seqs with model=empirical(_F)
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

    fix_kappa = 1  * 1: kappa fixed, 0: kappa to be estimated
        kappa = 2  * initial or fixed kappa
    fix_omega = 1  * 1: omega or omega_1 fixed, 0: estimate 
        omega = .4 * initial or fixed omega, for codons or codon-based AAs

    fix_alpha = 1  * 0: estimate gamma shape parameter; 1: fix it at alpha
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


##### Phyml args #####
#
#		Type 13 parameters for DNA sequences : 
#		./phyml 
#			<sequences file> 
#			<data type> 0
#			<sequence format> [i|s] 
#			<nb data sets> 
#			<nb bootstrapped data sets> 
#			<substitution model> [HKY | JC69 | K80 | F81 | F84 | TN93 | GTR]
#			<ts/tv ratio> [value | e = estimate]
#			<prop. invariable sites> [value | e = estimate]
#			<nb categories> 
#			<gamma parameter> [value | e = estimate]
#			<starting tree> [BIONJ | filename]
#			<optimise topology> [y|n]
#			<optimise branch lengths and rate parameters> [y|n]
#		Example :
#		./phyml seqs1 0 i 2 0 HKY 4.0 e 1 1.0 BIONJ y y
#			<sequences file> 
#			<data type> 1
#			<sequence format> [i|s] 
#			<nb data sets> 
#			<nb bootstrapped data sets> 
#			<substitution model> [JTT | Dayhoff | WAG | DCMut | RtREV | CpREV | VT | Blosum62 | MtMam]
#			<prop. invariable sites> [value | e = estimate]
#			<nb categories> 
#			<gamma parameter> [value | e = estimate]
#			<starting tree> [BIONJ | filename]
#			<optimise topology> [y|n]
#			<optimise branch lengths and rate parameters> [y|n]
#		
#		Type 12 parameters for amino-acids sequences : 
#		./phyml sequences file data type sequence format nb data sets nb bootstrapped data sets substitution model prop. invariable sites nb categories gamma parameter starting tree optimise topology optimise branch lengths and rate parameters 
#		Example :
#		./phyml seqs2 1 i 1 0 JTT 0.0 4 1.0 BIONJ n n
#
#
##### Date: 13/7/2011 || Joe Parker || QMUL.ac.uk #####


sub new{
	my $self = bless {}, shift;
	return $self;
}


sub runConselAnalysis{
	my $self = shift;
	my $filename1 = shift;
	my $filename2 = shift;
	my $wdir = shift;
	my $filestem = $filename1.$filename2;
	my $outputFile = $filename1.$filename2.".lnf";

	@topo1_lines = ('');#the data
	
	$firstline = 0;
	
	open(IN,$filename1) or die("Can't open lnf file ./$filename1\n");
	while(<IN>){
		chomp($line = $_);
		if($firstline < 1){
			@fields = split(/\ +/,$line);
			$numSites1 = $fields[2];
			$numPatterns1 = $fields[3];
			$firstline = 1;
		}
		if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
			push(@topo1_lines,$line);
		}
	}
	close(IN);
	
	@topo2_lines = ('');#the data
	
	open(IN,$filename2) or die("Can't open lnf file ./$filename2\n");
	while(<IN>){
		chomp($line = $_);
		if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
			push(@topo2_lines,$line);
		}
	}
	close(IN);
	
	open(OUT,'>',$outputFile);
	print OUT "     2     $numSites1    $numPatterns1\n\n";
	print OUT "\n 1\n\n";
	print OUT join("\n",@topo1_lines);
	print OUT "\n\n 2\n\n";
	print OUT join("\n",@topo2_lines);
	close(OUT);

	system(`./seqmt --paml $filestem`);
	system(`./makermt --paml $filestem >> $filestem.out`);
	system(`./consel $filestem >> $filestem.out`);
	system(`./catpv $filestem.pv >> $filestem.out`);
	system(`./catci $filestem.ci >> $filestem.out`);
	
	return $outputFile;
}


sub runPhymlTree{
	my $self = shift;
	my $dataFilename = shift;
	my $executeStatus = 0;
	my $inferredTreeString = '';
	if(defined($start = shift)){
		my $startingTree = shift;
		# do phyml run with initial topology
	}else{
		# do phyml run without initial topology
		$command = "/Applications/Phylogenetics/phyml/phyml_v2.4.4/exe/phyml_macOSX $dataFilename.phy 0 s 2 0 HKY 4.0 0.0 1 1.0 BIONJ y n >stdout.txt 2>stderr.txt";
		print "attempting command $command\n";
		$startTime = time();
		system(`$command`);
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
	}
	if($executeStatus>0){
		$elapsedTime = time()-$startTime;
		print "phyml tree successfully generated ($elapsedTime seconds)\n";
		$treefile = $dataFilename.".phy_phyml_tree.txt";
		open(IN, $treefile) or die ("unable to open the file $treefile\n");
		my @lines;
		while(<IN>){
			chomp($line = $_);
			push(@lines,$line);
		}
		$inferredTreeString = $lines[0];
		close(IN);
	}
	my $inferredTree = NewickTreeRepresentation->newFromString($inferredTreeString);
	return $inferredTree;
}


sub runPhymlTree{
	my $self = shift;
	my $dataFilename = shift;
	my $start = shift;
	my $executeStatus = 0;
	my $inferredTreeString = '';
	if(defined($start)){
		my $startingTree = $start;
		# do phyml run with initial topology fixed
		$command = "/Applications/Phylogenetics/phyml/phyml_v2.4.4/exe/phyml_macOSX $dataFilename.phy 0 s 2 0 HKY 4.0 e 1 1.0 $startingTree n y >stdout.txt 2>stderr.txt";
	}else{
		# do phyml run without initial topology
		$command = "/Applications/Phylogenetics/phyml/phyml_v2.4.4/exe/phyml_macOSX $dataFilename.phy 0 s 2 0 HKY 4.0 e 1 1.0 BIONJ y y >stdout.txt 2>stderr.txt";
	}

	print "attempting command $command\n";
	$startTime = time();
	system(`$command`);
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
		$elapsedTime = time()-$startTime;
		print "phyml tree successfully generated ($elapsedTime seconds)\n";
		$treefile = $dataFilename.".phy_phyml_tree.txt";
		open(IN, $treefile) or die ("unable to open the file $treefile\n");
		my @lines;
		while(<IN>){
			chomp($line = $_);
			push(@lines,$line);
		}
		$inferredTreeString = $lines[0];
		close(IN);
	}
	my $inferredTree = NewickTreeRepresentation->newFromString($inferredTreeString);
	return $inferredTree;
}

sub runCodemlAnalysis{
	my $self = shift;
	my $dataFile = shift;
	my $treeFile = shift;
	my $outputFile = $dataFile.".codeml.out";
	my $ctlFile = "$dataFile.ctl";
	my $executeStatus = 0;
	($codeml_ctl_file = $codemlTemplate) =~ s/DATA_FILE/$dataFile/;
	$codeml_ctl_file =~ s/TREE_FILE/$treeFile/;
	$codeml_ctl_file =~ s/OUTPUT_FILE/$outputFile/;
	open(OUT,'>',"./$ctlFile") or die ("unable to open ./$ctlFile.ctl for writing\n");
	print OUT $codeml_ctl_file;
	close(OUT);
	$command = "/Applications/Phylogenetics/paml44/bin/codeml ./$ctlFile >stdout.txt 2>stderr.txt";
	print "attempting command $command\n";
	$startTime = time();
	system(`$command`);
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
		print "exited codeml OK.\n";
	}
}


sub getPamlSitewiseSSLS{
	my $self = shift;
	my $topo1Filename = shift;
	my $topo2Filename = shift;
	my $alignmentArrayReference = shift;
	my $mode = shift;
	my $convergenceReps = shift;
	my $outfile = shift;
	my @alignmentArray = @$alignmentArrayReference;
	my @returnSSLS = (0) x scalar(@alignmentArray);
	print "Evaluating SSLS for ".scalar(@returnSSLS)." sites\n";
	
	open(OUT, '>>',$outfile) or die "unable to open $oufile for dSSLS data\n";
	
	#topo1 and topo2 are the SITE PATTERN dSSLS values from paml for topologies 1 & 2
	#topo1 is the correct one
	#but it doesn't really matter

	#open lnf file 1 and parse contents

	@topo1_patterns = ('');#the site patterns (aminio acids)
	@topo1_lnl = ('');#the lnL values for this site pattern
	@topo1_indices = ('');#the index in the file. NB indexed to 1, NOT ZERO 0
	
	open(IN,$topo1Filename) or die("Can't open lnf file $topo1Filename");
	while(<IN>){
		chomp($line = $_);
		if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
#			print "\t$line\n";
			@fields = split(/\ +/,$line);
			print "$fields[1]\t$fields[3]\t$fields[5]\t$fields[6]\n";
			push(@topo1_patterns,$fields[6]);
			push(@topo1_lnl,$fields[3]);
			push(@topo1_indices,$fields[1]);
		}
	}
	close(IN);
	
	#open lnf file 1 and parse contents
	
	@topo2_patterns = ('');#the site patterns (aminio acids)
	@topo2_lnl = ('');#the lnL values for this site pattern
	@topo2_indices = ('');#the index in the file. NB indexed to 1, NOT ZERO 0
	
	
	open(IN,$topo2Filename) or die("Can't open lnf file $topo2Filename");
	while(<IN>){
		chomp($line = $_);
		if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
#			print "\t$line\n";
			@fields = split(/\ +/,$line);
			print "$fields[1]\t$fields[3]\t$fields[5]\t$fields[6]\n";
			push(@topo2_patterns,$fields[6]);
			push(@topo2_lnl,$fields[3]);
			push(@topo2_indices,$fields[1]);
		}
	}
	close(IN);
	
	print "lnf file 1: ".scalar(@topo1_lnl)." site patterns' lnL read (last: ".$topo_1_lnl[scalar(@topo1_lnl)-2].")\n";
	print "lnf file 2: ".scalar(@topo2_lnl)." site patterns' lnL read (last: ".$topo_2_lnl[scalar(@topo2_lnl)-2].")\n";
	
	print "\nSitewise SSLS values:\nsite\tcodon\ttopo1\ttopo2\tdifference\n";
	
	$site = 1;
	$codon = 'null';
	$topo1_support = 'null';
	$topo2_support = 'null';
	$difference = 'null';
	
    foreach $sites(@alignmentArray){
#        print "\t$sites\n";                 ################IMPORTANT, README################################
        $sites = "\Q$sites";                #Added the \Q escapre character to deal with '*' characters.
        $codon = substr($sites,0,1);        #But it might make more sense to escape these when assembling the transposition matrix..

        #iterate through site patterns to find matching one and get lnl values
        for($i = 0; $i<scalar(@topo1_patterns);$i++){
            if($topo1_patterns[$i] =~ /$sites/){
#                print "\nMATCH ***T1***:\n\t\t$topo1_patterns[$i]\n\t\t$sites\n";
                $topo1_support = $topo1_lnl[$i];
            }
        }
       
        for($i = 0; $i<scalar(@topo2_patterns);$i++){
            if($topo2_patterns[$i] =~ /$sites/){
 #               print "\nMATCH ***T2***:\n\t\t$topo2_patterns[$i]\n\t\t$sites\n";
                $topo2_support = $topo2_lnl[$i];
            }
        }
		
		
		#calculate dSSLS as topo1 - topo2
		$difference = $topo1_support - $topo2_support;
		
		#print output 
		
		print "$site\t$codon\t$topo1_support\t$topo2_support\t$difference\n";
		print OUT "$mode\t$convergenceReps\t$site\t$codon\t$topo1_support\t$topo2_support\t$difference\n";
		
		#push output to return array
		
		push(@returnSSLS,$difference);
		
		#increment / dereference vars
	
		$site++;
		$codon = 'null';
		$topo1_support = 'null';
		$topo2_support = 'null';
		$difference = 'null';
	}	
	return(@returnSSLS);
	close(OUT);
}



1;