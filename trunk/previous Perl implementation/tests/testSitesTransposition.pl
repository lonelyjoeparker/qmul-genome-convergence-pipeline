#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;


$psr = PhymlSequenceRepresentation->new();

$fasta = "wholeAlignment.fa";
$nexus = "wholeAlignment.nex";
$phylip = "wholeAlignment.phy";

chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);

$psr->printShortSequences(10);

@transposed_sites = $psr->getTransposedSites();

foreach $site(@transposed_sites){
	#print "$site\n";
}

print "read ".scalar(@transposed_sites)." transposed sites\n";

print "site 1:\t$transposed_sites[0]\n";
print "site 2:\t$transposed_sites[1]\n";
print "site 3:\t$transposed_sites[2]\n";
print "site 4:\t$transposed_sites[3]\n";
print "site 5:\t$transposed_sites[4]\n";

print "\nDone\n\n";