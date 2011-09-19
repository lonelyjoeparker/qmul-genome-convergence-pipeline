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


$psr = PhymlSequenceRepresentation->new();


chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);
$psr->printShortSequences(10);

print "psr is $psr\n";

$psr->testDataHashPerturbation("pteropus","I AM A CUNT LARRR");
print "now to find out if we've correctly written the actual reference... \n";
$psr->printShortSequences(10);

print "\nDone\n\n";


1;