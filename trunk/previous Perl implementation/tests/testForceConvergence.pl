#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;

chomp($filename = $ARGV[0]);

$psr = PhymlSequenceRepresentation->new();

$psr->loadSequences($filename);

chomp($numSitesToForce = $ARGV[1]);
$masterTaxon = 'R_ferrum';
@slaveTaxonArray = ("M_ricketti","Mega_spas","dolphin_De");
$slaveTaxonArrayRef = \@slaveTaxonArray;


$psr->forceConvergence($numSitesToForce,$masterTaxon,$slaveTaxonArrayRef);

$psr->writePhylipFile("converged");

#@ret = $psr -> getTransposedSites();
#print join("\n\t",@ret);

print "\nDone\n\n";