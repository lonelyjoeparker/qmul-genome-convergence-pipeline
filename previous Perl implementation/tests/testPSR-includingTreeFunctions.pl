#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;
use NewickTreeRepresentation;


$psr = PhymlSequenceRepresentation->new();

$fasta = "wholeAlignment.fa";
$nexus = "wholeAlignment.nex";
$phylip = "wholeAlignment.phy";

chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);

#$psr->writePhylipFile($filename);
$tree = $psr->getPhymlPhylogeny();

print $tree->countTaxa()." taxa in the inferred tree\n";
#print $tree->getTree()." (representation)\n";
#$tree->printTree();

print "\nDone\n\n";