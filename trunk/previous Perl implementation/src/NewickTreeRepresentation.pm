#! /usr/bin/perl

package NewickTreeRepresentation;

use Carp;

##### CLASS DEF ######
#
# 	A utility class of phylogeny representation functions
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
#	newFromString()
#		purpose:	input string arg constructor.
#		args:		a newick tree string.
#		returns:	reference to self.
#		execution:	input tree string
#					countTaxa()
#					save tree string params
#
#
#	defineSubtree()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	countTaxa()
#		purpose:	count how many taxa are in this tree.
#		args:		none.
#		returns:	int number of taxa.
#
#
#	printTree()
#		purpose:	print tree string to STDOUT.
#		args:		none.
#		returns:	void.
#
#
#	getTree()
#		purpose:	get tree string.
#		args:		none.
#		returns:	void.
#
#
#	printSeparateTaxa()
#		purpose:	print a list of taxa in this tree.
#		args:		none.
#		returns:	void.
#
#
#	printTreeFile()
#		purpose:	print tree string to a file.
#		args:		filename.
#		returns:	treefile name.
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####

sub new{
	my $self = bless {}, shift;
	$self -> {TREE_STRING} = '';
	$self -> {NUMBER_OF_TAXA} = 0;
	return $self;
}


sub newFromString{
	my $self = bless {}, shift;
	my $inputString = shift;
	$self -> {TREE_STRING} = $inputString;
	local $numberOfTaxa = $self->countTaxa();
	$self -> {NUMBER_OF_TAXA} = $numberOfTaxa;
	return $self;
}


sub defineSubtree{
	my $self = shift;
}


sub countTaxa{
	my $self = shift;
	local $treeString = $self -> {TREE_STRING};
	local $numberOfTaxa = 0;
	@delimits = split(/[\,\(]{1,}/,$treeString);
	$numberOfTaxa = scalar(@delimits)-1;
	return $numberOfTaxa;
}


sub printTree{
	my $self = shift;
	local $treeString = $self -> {TREE_STRING};
	print "The tree representation for $self is:\n\t$treeString\n";	
}


sub getTree{
	my $self = shift;
	local $treeString = $self -> {TREE_STRING};
	return $treeString;
}


sub printSeparateTaxa{
	my $self = shift;
	local $treeString = $self -> {TREE_STRING};
	local $numberOfTaxa = 0;
	@delimits = split(/[\,\(]{1,}/,$treeString);
	foreach $delim(@delimits){
		print "\t$delim\n";
	}
}


sub printTreeFile{
	my $self = shift;
	my $filename = shift;
	local $treeString = $self -> {TREE_STRING};
	local $treeFileName = "$filename.tre";
	open(OUT,'>',$treeFileName) or die ("unable to open $treeFileName for writing\n");
	print OUT $treeString;
	close(OUT);
	return $treeFileName;
}


1;