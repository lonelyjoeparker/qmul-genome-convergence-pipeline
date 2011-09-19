#! /usr/bin/perl

use Carp;

##### CLASS DEF ######
#
# 	Test congruence of various sized prestin datasets with and without megaderma
#
#	Still need to determine which parts of output to parse and return.
#
#
#	EXECUTION:
#
#		1  Read in data (full 39 taxa)
#		2  Read in species trees (full 39 and core 7 taxa).
#		3  Congruence test with and without megaderma (full 39 taxa case; back-check essentially)
#		4  Congruence test with and without megaderma on pruned 7-taxa case (think it's 7)
#		5  For n = 1..30:
#				for rep = 1..30:
#					add n random taxa from complete set to core set
#					test congruence with and without megaderma
#				(end)
#		   (end)
#		6  Collate results. Big trip to pub to celebrate first (and very useful) implementation
#
#
#		TODO: 
#			The CongruenceTest does not currently implement modeltest. not a problem for this 
#			application because I can run modeltest externally and fix params ..but it should be implemented.
#			
#			The CongruenceTest does not simulate down branches to generate null distribution.
#			Again, this should be implemented.
#
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####



1;