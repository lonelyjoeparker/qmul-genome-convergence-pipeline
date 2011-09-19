#! /usr/bin/perl

package CongruenceFunctions;

use Carp;

##### CLASS DEF ######
#
# 	A utility class of mainly unrelated helper functions
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
#	defineSubsample()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	defineSubsampleExcludingTaxa()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	defineSubtree()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getSitewiseLnL()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####

sub new{
	my $self = bless {}, shift;
	return $self;
}


1;