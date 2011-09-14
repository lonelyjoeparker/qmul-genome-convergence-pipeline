#! /usr/bin/perl

use Carp;

##### CLASS DEF ######
#
# 	Test CongruenceTest on a simple dataset.
#
#	Still need to determine which parts of output to parse and return.
#
#
#	EXECUTION:
#
#		1  Read in data
#		2  Read in species tree.
#		3  Pass data and species tree to a CongruenceTest
#		4  CongruenceTest.analyze() and print out results.
#
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####


use Carp;
use PhymlSequenceRepresentation;
use NewickTreeRepresentation;
use ExternalFunctions;
use CodemlAnalysis;
use BasemlAnalysis;
use AamlAnalysis;


$psr = PhymlSequenceRepresentation->new();

$psr->loadSequences("wholeAlignmentPLEAASE.phy");


#$AA1 = CodemlAnalysis->runCodemlAnalysis("wholeAlignmentPLEAASE.phy", "./RAxML_modelComparison/RAxML_bestTree.wholeAlignment_LG.tre","speciesAATreeFileModel",2,'mtmam.dat'); #could also do top matrix (dayhoff) for one and blah for the other?
#system(`cp lnf lnf_AAspecies`);
#$AA1 = CodemlAnalysis->runCodemlAnalysis("wholeAlignmentPLEAASE.phy", "./RAxML_modelComparison/RAxML_bestTree.wholeAlignment_BLOSUM62.tre","speciesAATreeFileModel",2,'mtmam.dat');
#system(`cp lnf lnf_AAprestin`);

@transposed_sites = $psr->getTransposedSites();
@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_AAspecies","lnf_AAprestin",\@transposed_sites);
#print join("\n\t",@SSLS);

$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_AAspecies","lnf_AAprestin");
print "$conselReadyFile\n";

print "\nDone\n\n";


1;