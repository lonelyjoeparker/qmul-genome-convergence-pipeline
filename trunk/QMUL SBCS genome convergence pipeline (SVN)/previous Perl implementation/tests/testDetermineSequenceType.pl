#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;


$psr = PhymlSequenceRepresentation->new();

chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);



print "\nDone\n\n";