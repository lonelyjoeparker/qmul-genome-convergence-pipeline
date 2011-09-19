#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;


$psr = PhymlSequenceRepresentation->new();

chomp($filename = $ARGV[0]);

$psr->loadSequences($filename);

$psr->translate();

$psr->printShortSequences(10);

$psr->translate();

$psr->printShortSequences(10);

print "\nDone\n\n";