#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;


$psr = PhymlSequenceRepresentation->new();

$fasta = "wholeAlignment.fa";
$nexus = "wholeAlignment.nex";
$phylip = "wholeAlignment.phy";

chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);

$psr->writePhylipFile($filename);


print "\nDone\n\n";