#! /usr/bin/perl

package PhymlSequenceRepresentation;

use Carp;
use 5.010;
use POSIX;
use ExternalFunctions;
use NewickTreeRepresentation;
use Switch;



##### CLASS DEF ######
#
# 	A utility class to hold a representation of aligned sequence data
#
#	Will take phylip / fasta / nexus (NON interleaved)
#	Will export to phylip
#
#	Phylip files exported should:
#		- be readable by consel / hyphy / phyml / paml
#		- not contain duplicate taxon names
#		- retain taxon names but truncate (use UIDs to identify) for phyml
#
#	Sequence / taxon alignment should be retrieveable
#
#	Should implement datatype = nt, codon, aa
#
#	Should verify gaps, stop codons and ambiguity data
#
#	Should verify (numsites / 3 = integer) if codon
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
#	loadSequences()
#		purpose:	load data. 
#					If datatype or filetype args are missing, determine them.
#					If remove arg is present, act on it.
#					If any taxon strings > 10 then parse them and make lookup hash.
#		args:		filename [remove=ambiguities|stops|both] [datatype=nt|codon|aa] [filetype=nexus|fasta|phylip].
#		returns:	void.
#
#
#	determineFiletype()
#		#DEPRECATED - determineDatatype() implemented - 07/07/2011
#		purpose:	guess the file format.
#		args:		self.
#		returns:	string [fasta|nexus|phylip].
#
#
#	determineDatatype()
#		purpose:	determine data format.
#		args:		REF TO: raw data array.
#		returns:	string [fasta|nexus|phylip].
#
#
#	removeAmbiguities()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	writePhylipFile()
#		purpose:	write instance data to a .phy file, given filename.
#		args:		self; filename.
#		returns:	void.
#
#
#	writePhylipFileForCodeml()
#		purpose:	write instance data to a .phy file, given filename.
#		args:		self; filename.
#		returns:	filename of new file.
#		execution:	include check for ((num chars/3) is integer)
#
#
#	writeFastaFile()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	writeNexusFile()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	readPhylipFile()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#		:::IMPORTANT IMPLEMENTATION NOTE:::								<-----------------IMPORTANT, VERY IMPORTANT.... 
#		:::     19 / Aug / 2011         :::
#		As currently implemented, uses whitespace to divide input between name / data
#		Therefore:
#			REQUIRES WHITESPACE TO BE PRESENT!!!!
#		
#		Also: Names are not truncated (assumed to be truncated already)
#		Also: Names are assumed to be unique
#		Also: Sequences are not checked for uniqueness
#		Also: The name / fullname lookup tables (fullNameTranslationHash and fullNameTranslationHashInvert) are NOT INITIALIZED
#
#
#	readFastaFile()
#		purpose:	read sequence data to a name/data hash, assuming fasta format.
#		args:		self.
#		returns:	number of sequences; add REFTO dataHash to instance vars.
#
#
#	readNexusFile()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getDataHash()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getNamesHash()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getNumberOfTaxa()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getNumberOfSites()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#	getPhymlPhylogeny()
#		purpose:	Generate a phylogeny for this data using Phyml.
#		args:		none.
#		returns:	NewickTreeRepresentation.
#		execution:	Call ExternalFunctions.runPhymlTree(self)
#					Pass returned NewickTreeRepresentation.
#
#
#	getPhymlPhylogenyFromFixedTopology()
#		purpose:	Generate a phylogeny for this data using Phyml.
#		args:		filename for fixed topology.
#		returns:	NewickTreeRepresentation.
#		execution:	Call ExternalFunctions.runPhymlTree(self,treefile)
#					Pass returned NewickTreeRepresentation.
#
#
#	getTransposedSites()
#		purpose:	Transpose the sequence alignment into site patterns (non-unique)
#		args:		none.
#		returns:	Array of site patterns (non-unique) in site order
#		execution:	Get num sites
#					Initialise array (length=num sites)
#					Add chars from sequences to sites array
#
#
#	forceConvergence()
#		purpose:	constrain a subset of taxa to have the same AA
#		args:		int: sites to constrain; string: master taxon; arrayReference: taxa to slave
#		returns:	void
#		execution:	for i in num sites
#						random indices
#						get target master taxon as char array /// NB: codon triplets
#						read array[indices]
#						replace 
#						
#
#   testDataHashPerturbation()
#        purpose:    test that the data hash is being referenced correctly when perturbed
#        args:        string taxon to edit; string new sequence
#        returns:    void
#        execution:    get hash
#                    replace target taxon's sequence with arg
#                    re-reference hash
#
#
#	printShortSequences()
#        purpose:    print an integer number of characters from the active sequences to STOUT
#        args:        int number of characters to print
#        returns:    void
#        execution:    get hash
#                    foreach sequence (print substr(sequence))
#
#
#	determineSequenceType()
#		purpose:	determine if sequences are DNA, RNA, AA
#		args:		none.
#		returns:	string [DNA|RNA|AA]
#		execution:	initialise empty alphabet vector
#					get letter frequencies in 1st sequence
#					DNA definition: A+C+G+T > 0.8
#					RNA definition: A+C+G+U > 0.8
#					AA	definition: [Alphabet] > 0.8
#
#
#	translate()
#		purpose:	translate DNA/RNA to AA
#		args:		none
#		returns:	void		// NB: IMPORTANT! doesn't clone self - destructively writes AA over DNA/RNA
#		execution:	die if seqtype = AA
#					grep Uracil > Thymine
#					divide into triplets
#					lookup codon table
#					replace data, keep track of AA length and num stop codons for each seq
#					get new num_sites
#
#
#	copy()
#		purpose:	copy a PhymlSequenceRepresentation object
#		args:		none
#		returns:	REF TO new PSR object
#		execution:	<to implement>
#
#
#	removeTaxa()
#		purpose: 	removes taxa from alignment. NB this is destructive (permanent)
#		args:		ref to array containing keys (names) of taxa
#		returns:	void.
#
#
#	removeStopCodons()
#		purpose:	no-arg constructor.
#		args:		none.
#		returns:	reference to self.
#
#
#
##### Date: 5/7/2011 || Joe Parker || QMUL.ac.uk #####

sub new{
	my $self = bless {}, shift;
	return $self;
}


sub loadSequences{
	my $self = shift;
	my $filename = shift;
	my $datatype = shift;
	print "attempting to open $filename\n";
	$self->{BASE_FILENAME} = $filename;
	open(IN,$filename) or die "can't open $filename!\n\n";
	my @lines;
	while(<IN>){
		chomp($line = $_);
		push(@lines, $line);
	}
	$lines_ref = \@lines;
	$self->{RAW_INPUT_DATA_ARRAY} = $lines_ref;
	print "read in ".scalar(@lines)." lines. Stored in array $lines_ref\n";
	if(scalar(@lines)<2){
		print "\nWARNING!\n\tRead fewer than 2 lines in input file (could this file have mac- or DOS- formatted linebreaks?)\n\tYou may want to abort this run.\n\n";
	}
	
	if(defined($datatype)){
		print "assuming file datatype is [$datatype]\n";
	}else{
		print "attempting to determine file datatype\n";
		$datatype = $self->determineDatatype();
		print "assuming file datatype is [$datatype]\n";
	}
	
	$self->{INPUT_DATATYPE} = $datatype;
	
	switch($datatype){
		case('fasta'){
			#readFastaFile()
			$taxaRead = $self->readFastaFile();
			print "read $taxaRead taxa from .fasta file.\n";
		}
		case('nexus'){
			#readNexusFile()
			$self->readNexusFile();
		}
		else{
			#assume phylip
			#readPhylipFile()
			$self->readPhylipFile;
		}
	};
	close(IN);
	
	my $determinedSequenceType = $self->determineSequenceType();
	print "Determined sequence type as '$determinedSequenceType'\n";
	$self->{SEQUENCE_TYPE} = $determinedSequenceType;	
}


sub determineDatatype{
	my $self = shift;
	$inputLinesRef = $self->{RAW_INPUT_DATA_ARRAY};
	@rawData = @$inputLinesRef;
	$datatype = 'phylip';
	if($rawData[0] =~ '>'){
		$datatype = 'fasta';
	}
	if(uc($rawData[0]) =~ '#NEXUS'){
		$datatype = 'nexus';
	}
	return $datatype;
}


sub readPhylipFile{				# could process interleaved files using hash (e.g. add to each entry as you go)
	my $self = shift;
	my %dataHash = {};
	my %fullNameTranslationHash = {};
	my %fullNameTranslationHashInvert = {};
	my $numberOfTaxa = 0;
	my $maxNumberOfSites = 0;
	my $currentTaxaName;
	my $currentTaxaData;

	$rawDataRef = $self->{RAW_INPUT_DATA_ARRAY};
	@rawData = @$rawDataRef;

	$firstline = shift(@rawData);
	@masterData = split(/\ +|\t/,$firstline);
	$self->{NUMBER_OF_TAXA} = $masterData[0];
	$self->{NUMBER_OF_SITES} = $masterData[1];
	
	print "$masterData[0] taxa, $masterData[1] sites\n\n";
	
	foreach $line(@rawData){
		if(length($line)> 13){
			unless($line =~ /\ +|\t+/){
				$piece = substr($line,0,20);
				die "ERROR.\nUnable to parse input, data: [$piece]\n\nPhylip files *must* contain whitespace between taxa names and sequence data. Please check and reformat the input file.\n\n";
			}
			@lineData = split(/\ +|\t+/,$line);
			$currentTaxaName = $lineData[0];
			$currentTaxaData = $lineData[1];
			print "name: $currentTaxaName\t".substr($currentTaxaData,0,10)."\n";
			if(exists($dataHash{$currentTaxaName})){
				#the file is probably interleaved
				$fulldata = $dataHash{$currentTaxaName};
				$fulldata .= $currentTaxaData;
				$dataHash{$currentTaxaName} = $fulldata;
			}else{
				#the file is probably not interleaved (i.e. sequential)
				#just add the current data to dataHash
				$dataHash{$currentTaxaName} = $currentTaxaData;
			}
		}
	}
	my $dataHashRef = \%dataHash;
	$self->{DATA_HASH_REF} = $dataHashRef;
}


sub readNexusFile{				# could process interleaved files using hash (e.g. add to each entry as you go)
	my $self = shift;
	my %dataHash = {};
	my %fullNameTranslationHash = {};
	my %fullNameTranslationHashInvert = {};
	my $numberOfTaxa = 0;
	my $maxNumberOfSites = 0;
	my $currentTaxaName;
	my $currentTaxaData;

	$rawDataRef = $self->{RAW_INPUT_DATA_ARRAY};
	@rawData = @$rawDataRef;

	$inMatrix = 0;
	$taxlabels = 0;
	$taxlabelsRead = 0;
	@taxlabelsBucket;

	foreach $line(@rawData){
		if($inMatrix > 0 && !($line =~ /\[/) && length($line)>10 && $taxlabels == 0){
			@lineData = split(/\ +|\t+/,$line);
			$thisName = uc($lineData[1]);
			$lineSequence = $lineData[2];
			
			$thisName =~ s/\'//g;
			$lineSequence =~ s/;//g;
			
			$truncatedName = substr($thisName,0,10);
			
			if(length($thisName)>0){
#				print "read $thisName :::".substr($lineSequence,0,30)."\n";
				$currentTaxaData = $lineSequence;
				
				#ok, we have a taxon that is in the data block,
				#and uppercase
				#and has no quotes
				#and is short enough
				#add names / values to hashes

				$fullNameTranslationHashInvert{$currentTaxaName}=$truncatedName;
				$fullNameTranslationHash{$truncatedName}=$currentTaxaName;
				$dataHash{$truncatedName}=$currentTaxaData;
				#check to see if we can update the maximum number of sites seen
				$currentTaxonLength = length($currentTaxaData);
				if($currentTaxonLength > $maxNumberOfSites){
					$maxNumberOfSites = $currentTaxonLength;
				}
				$numberOfTaxa ++;

			}
			
		}
		if($inMatrix > 0 && !($line =~ /\[/) && length($line)>10 && $taxlabels == 1){
			@lineData = split(/\ +|\t+/,$line);
			$thisName = uc($taxlabelsBucket[$numberOfTaxa]);
			$lineSequence = $lineData[1];
			
			$thisName =~ s/\'//g;
			$lineSequence =~ s/;//g;
			
			$truncatedName = substr($thisName,0,10);
			
			if(length($thisName)>0){
				print "read $thisName :::".substr($lineSequence,0,30)."\n";
				$currentTaxaData = $lineSequence;
				
				#ok, we have a taxon that is in the data block,
				#and uppercase
				#and has no quotes
				#and is short enough
				#add names / values to hashes

				$fullNameTranslationHashInvert{$currentTaxaName}=$truncatedName;
				$fullNameTranslationHash{$truncatedName}=$currentTaxaName;
				$dataHash{$truncatedName}=$currentTaxaData;
				#check to see if we can update the maximum number of sites seen
				$currentTaxonLength = length($currentTaxaData);
				if($currentTaxonLength > $maxNumberOfSites){
					$maxNumberOfSites = $currentTaxonLength;
				}
				$numberOfTaxa ++;

			}
			
		}
		if($line =~ /\[/){
		}
		if(uc($line) =~ /END/){
			$inMatrix = 0;
		}
		if(uc($line) =~ /MATRIX/){
			$inMatrix = 1;
		}		
		if($taxlabels == 1 && $taxlabelsRead == 0){
			print "FILE WITH TAXA LABELS\n";
			@fields = split(/\t|\ +/,$line);
			foreach $name(@fields){
				if(length($name)>3){
					$name =~ s/\'//g;
					print "[$name]\n";
					push(@taxlabelsBucket,$name);
				}
			}
			$taxlabelsRead = 1;
		}
		if(uc($line) =~ /TAXLABELS/){
			$taxlabels = 1;
		}
	}
	
	my $fullNameTranslationInvertRef = \%fullNameTranslationHashInvert;
	$self->{NAME_HASH_INVERT_REF} = $fullNameTranslationInvertRef;
	my $fullNameTranslationRef = \%fullNameTranslationHash;
	$self->{NAME_HASH_REF} = $fullNameTranslationRef;
	my $dataHashRef = \%dataHash;
	$self->{DATA_HASH_REF} = $dataHashRef;
	$self->{NUMBER_OF_TAXA} = $numberOfTaxa;
	$self->{NUMBER_OF_SITES} = $maxNumberOfSites;
	
	print scalar(%dataHash)."\n";
	print scalar(%fullNameTranslationHash)."\n";
	print scalar(%fullNameTranslationHashInvert)."\n";
	return $numberOfTaxa;
}


sub readFastaFile{				# can easily process multiline sequences using buffer

	####
	#
	#	// 	IMPORTANT	//
	#
	#	//	Under present flow, the taxon names are converted into truncated (10 char) names.
	#		These are checked for uniqueness against existing hash
	#		If non-unique a UID (number of taxa in this file 0<n<1000) is appended
	#		The newly-unique and truncated taxon ID is then added to the truncated_name/data and truncated_name/name hashes.
	#		BUT:::
	#		Be careful! When converting back to full names from truncated names at some point in the future, there is 
	#		NO LOGICAL GUARANTEE or runtime checking that the original, non-truncated taxa are unique. 
	#		In fact, they may very likely not be (and cause problems for trees etc).
	#
	#
	#		Joe, 7/07/2011
	#
	#
	####

	my $self = shift;
	my %dataHash = {};
	my %fullNameTranslationHash = {};
	my %fullNameTranslationHashInvert = {};
	my $numberOfTaxa = 0;
	my $maxNumberOfSites = 0;
	my $currentTaxaName;
	my $currentTaxaData;

	$rawDataRef = $self->{RAW_INPUT_DATA_ARRAY};
	@rawData = @$rawDataRef;
	foreach $line(@rawData){
		if($line =~ '>'){
			if(defined($currentTaxaName)){
				#there is a previous taxon waiting to be added to the hash
				print "\t\tReading taxon (seq. length: ".length($currentTaxaData)."): $currentTaxaName\n";
				#truncate name to 10 chars to comply with phylip standard
				$truncatedName = substr($currentTaxaName,0,10);
				#check existing full name hash for duplicate, warn if so
				if(exists($fullNameTranslationHashInvert{$currentTaxaName})){
					print "\n\n\t\tWARNING: DUPLICATE INPUT TAXON DETECTED.\n\n\t\tTaxon '$currentTaxaName' (untruncated) had a duplicate entry already in the input file\n\t\tWhen truncated, I will append a unique ID to this taxa, but if printing out full names, I will print the original, DUPLICATE, name.\n\t\tYou have been warned!\n";
				}			
				#check existing name hash for duplicate, append UID to incoming name if so WITH WARNING
				if(exists($dataHash{$truncatedName})){
					$newUIDtaxaName = substr($truncatedName,0,(length($truncatedName)-4))."_".sprintf("%03d",$numberOfTaxa);
					print "\n\n\t\tWARNING: DUPLICATE TRUNCATED TAXON DETECTED.\n\n\t\tTaxon '$truncatedName' (truncated) has a duplicate entry already in the data\n\t\tWill be renamed as '$newUIDtaxaName'\n\n";
					$truncatedName = $newUIDtaxaName;
				}
				#add names / values to hashes
				$fullNameTranslationHashInvert{$currentTaxaName}=$truncatedName;
				$fullNameTranslationHash{$truncatedName}=$currentTaxaName;
				$dataHash{$truncatedName}=$currentTaxaData;
				#check to see if we can update the maximum number of sites seen
				$currentTaxonLength = length($currentTaxaData);
				if($currentTaxonLength > $maxNumberOfSites){
					$maxNumberOfSites = $currentTaxonLength;
				}
				#reset data and name
				$currentTaxaData = '';
				$currentTaxaName = substr($line,1);			
			}else{
				$currentTaxaName = substr($line,1);			
			}
			$numberOfTaxa ++;
		}else{
			$currentTaxaData .= $line;	
		}
	}
	#repeat again to add last taxon
				#there is a previous taxon waiting to be added to the hash
				print "\t\tReading taxon (seq. length: ".length($currentTaxaData)."): $currentTaxaName\n";
				#truncate name to 10 chars to comply with phylip standard
				$truncatedName = substr($currentTaxaName,0,10);
				#check existing full name hash for duplicate, warn if so
				if(exists($fullNameTranslationHashInvert{$currentTaxaName})){
					print "\n\n\t\tWARNING: DUPLICATE INPUT TAXON DETECTED.\n\n\t\tTaxon '$currentTaxaName' (untruncated) had a duplicate entry already in the input file\n\t\tWhen truncated, I will append a unique ID to this taxa, but if printing out full names, I will print the original, DUPLICATE, name.\n\t\tYou have been warned!\n";
				}			
				#check existing name hash for duplicate, append UID to incoming name if so WITH WARNING
				if(exists($dataHash{$truncatedName})){
					$newUIDtaxaName = substr($truncatedName,0,(length($truncatedName)-4))."_".sprintf("%03d",$numberOfTaxa);
					print "\n\n\t\tWARNING: DUPLICATE TRUNCATED TAXON DETECTED.\n\n\t\tTaxon '$truncatedName' (truncated) has a duplicate entry already in the data\n\t\tWill be renamed as '$newUIDtaxaName'\n\n";
					$truncatedName = $newUIDtaxaName;
				}
				#add names / values to hashes
				$fullNameTranslationHashInvert{$currentTaxaName}=$truncatedName;
				$fullNameTranslationHash{$truncatedName}=$currentTaxaName;
				$dataHash{$truncatedName}=$currentTaxaData;
				#check to see if we can update the maximum number of sites seen
				$currentTaxonLength = length($currentTaxaData);
				if($currentTaxonLength > $maxNumberOfSites){
					$maxNumberOfSites = $currentTaxonLength;
				}

	my $fullNameTranslationInvertRef = \%fullNameTranslationHashInvert;
	$self->{NAME_HASH_INVERT_REF} = $fullNameTranslationInvertRef;
	my $fullNameTranslationRef = \%fullNameTranslationHash;
	$self->{NAME_HASH_REF} = $fullNameTranslationRef;
	my $dataHashRef = \%dataHash;
	$self->{DATA_HASH_REF} = $dataHashRef;
	$self->{NUMBER_OF_TAXA} = $numberOfTaxa;
	$self->{NUMBER_OF_SITES} = $maxNumberOfSites;
	
	print scalar(%dataHash)."\n";
	print scalar(%fullNameTranslationHash)."\n";
	print scalar(%fullNameTranslationHashInvert)."\n";
	return $numberOfTaxa;
}


sub writePhylipFile{
	my $self = shift;
	my $filename = shift;
	$filename .= '.phy';
	print "Attempting to write data to $filename..\n";
	open(OUT,'>',$filename) or die "unable to open $filename for writing.\n\n";
	$dataHashRef = $self->{DATA_HASH_REF};
	%dataHash = %$dataHashRef;
	$nameHashRef = $self->{NAME_HASH_REF};
	%nameHash = %$nameHashRef;
	print "reading data back from hash $dataHashRef (".scalar(%dataHash).")\n";
	my $output = '';
	my $numTaxa = 0;
	my $numChars = 0;
	foreach my $UID(sort keys %dataHash){
		if($UID =~ /HASH\(/){}else{
			%somehash = %$UID;
			$sequence = $dataHash{$UID};
			$fullname = $nameHash{$UID};
			$seqLength = length($sequence);
			if($numChars<$seqLength){$numChars = $seqLength};
			$output .= "$UID    ".' ' x (10-length($UID))."$sequence\n";
			$numTaxa ++;
		}
	}
	print OUT "$numTaxa   $numChars\n".$output;
	close(OUT);
}


sub writePhylipFileForCodeml{
	my $self = shift;
	my $filename = shift;
	$filename .= '.phy';
	print "Attempting to write data to $filename..\n";
	open(OUT,'>',$filename) or die "unable to open $filename for writing.\n\n";
	$dataHashRef = $self->{DATA_HASH_REF};
	%dataHash = %$dataHashRef;
	$nameHashRef = $self->{NAME_HASH_REF};
	%nameHash = %$nameHashRef;
	print "reading data back from hash $dataHashRef (".scalar(%dataHash).")\n";
	my $output = '';
	my $numTaxa = 0;
	my $numChars = 0;
	foreach my $UID(sort keys %dataHash){
		if($UID =~ /HASH\(/){}else{
			%somehash = %$UID;
			$sequence = $dataHash{$UID};
			$fullname = $nameHash{$UID};
			$seqLength = length($sequence);
			if($numChars<$seqLength){$numChars = $seqLength};
			$output .= "$UID    ".' ' x (10-length($UID))."$sequence\n";
			$numTaxa ++;
		}
	}
	switch($numChars/3){
		case(/\.3/){
			print "number of chars ($numChars) is one too many\n";
			$numChars --;
		}
		case(/\.6/){
			print "number of chars ($numChars) is two too many\n";
			$numChars -= 2;
		}
	}
	print OUT "$numTaxa   $numChars\n".$output;
	close(OUT);
	return $filename;
}


sub getPhymlPhylogeny{
	my $self = shift;
	my $filename = "./".time().".tmp";
	$self->writePhylipFile($filename);
	$NTR = ExternalFunctions->runPhymlTree($filename);
	return $NTR;
}


sub getPhymlPhylogenyFromFixedTopology{
	my $self = shift;
	my $filename = "./".time().".tmp";
	my $fixedTreeFile = shift;
	$self->writePhylipFile($filename);
	$NTR = ExternalFunctions->runPhymlTree($filename,$fixedTreeFile);
	return $NTR;
}


sub getTransposedSites{
	my $self = shift;
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my @retArr = ('') x $numSites;
	local %dataHash = %$seqHashRef;
	print "Attempting a transposition on object $self;\ndata $seqHashRef;\nsites $numSites.\n\n";
	foreach my $UID(sort keys %dataHash){
		print "scanning [$UID] to transpose...\n";
		@sequence = split(//,$dataHash{$UID});
		print "found ".scalar(@sequence)." sites\n";
		for($i=0;$i<scalar(@sequence);$i++){
#			print "\t".$retArr[0]."\n\t".$retArr[1]."\n\t".$retArr[2]."\n\t".$retArr[3]."\n\t".$retArr[4]."\n";
			$retArr[$i] .= $sequence[$i];
		}
	}
	return @retArr;
}


sub forceConvergence{
	$self = shift;
	my $numSitesToForce = shift;
	my $masterTaxon = shift;
	my $slaveTaxonArrayRef = shift;
	my @slaveTaxonArray = @$slaveTaxonArrayRef;
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my $sequenceType = $self->{SEQUENCE_TYPE};
	
#	forceConvergence()
#		purpose:	constrain a subset of taxa to have the same AA
#		args:		int: sites to constrain; string: master taxon; arrayReference: taxa to slave
#		returns:	void
#		execution:	for i in num sites
#						random indices
#						get target master taxon as char array /// NB: codon triplets
#						read array[indices]
#						replace 
#						
	%dataHash = %$seqHashRef;
	local @convergedSiteIndices = (1);
	local @convergedCodons = ();
	local $masterSequenceChars = $dataHash{$masterTaxon};
	local @masterSequenceCharArray = split(//,$masterSequenceChars);
	
	print "Attempting to force $numSitesToForce sites in alignment to converge with $masterTaxon (taxa ".join('/ ',@slaveTaxonArray)." will converge)\n";
	
	
#	print "Master sequence ($masterTaxon): $masterSequenceChars\n";

#	foreach my $ID(sort keys %dataHash){
#		print "\t$ID ".$dataHash{$ID}."\n";		##### GAAAAAH!!!! why can't we get the data hash.....?!?!?!?
#	}

	switch($sequenceType){
		case('DNA'){
			$numCodons = floor(length($masterSequenceChars)/3);
			print "$numCodons codons in sequence $masterTaxon (".length($masterSequenceChars)." nt).\nPicking sites to force.\n";
			while(scalar(@convergedSiteIndices)<$numSitesToForce){
				$candidateIndex = floor(rand($numCodons));
#				print "candidate index $candidateIndex, CSI size ".scalar(@convergedSiteIndices)."\n";
				my %indicesPresent = map{$_ => 1} @convergedSiteIndices;
				unless(exists($indicesPresent{$candidateIndex})){
					push(@convergedSiteIndices,$candidateIndex);
				}
			}
			
			print "we will force sites: [".join("], [",@convergedSiteIndices)."]\nCodons:\n";
			
			# get the codons at these positions
			
			foreach $index(@convergedSiteIndices){
				$thisCodon = $masterSequenceCharArray[($index-1)*3];
				$thisCodon .= $masterSequenceCharArray[(($index-1)*3)+1];
				$thisCodon .= $masterSequenceCharArray[(($index-1)*3)+2];
				print "\t($index)\t$thisCodon\n";
				push(@convergedCodons,$thisCodon);
			}
			
			#apply these codons to the slave taxa
			
			foreach $slave(@slaveTaxonArray){
				$slaveSequence = $dataHash{$slave};
				@slaveSequenceCharArray = split(//,$slaveSequence);
				print "converging sequence $slave: ".scalar(@slaveSequenceCharArray)."\n";

				for($i = 0; $i< $numSitesToForce; $i++){
					$codonToConvergeIndex = $convergedSiteIndices[$i];
					$codonToConverge = $convergedCodons[$i];
					@codonToConvergeChars = split(//,$codonToConverge);

					$baseIndex = $codonToConvergeIndex-1;
					$codon_1 = (($baseIndex)*3)+0;
					$codon_2 = (($baseIndex)*3)+1;
					$codon_3 = (($baseIndex)*3)+2;

					print "\tconverging position $codonToConvergeIndex\tto $codonToConvergeChars[0] $codonToConvergeChars[1] $codonToConvergeChars[2]\t";
					print "\texisting: $slaveSequenceCharArray[$codon_1] $slaveSequenceCharArray[$codon_2] $slaveSequenceCharArray[$codon_3]";

					$slaveSequenceCharArray[$codon_1] = $codonToConvergeChars[0];
					$slaveSequenceCharArray[$codon_2] = $codonToConvergeChars[1];
					$slaveSequenceCharArray[$codon_3] = $codonToConvergeChars[2];
					
					$newSequence = join('',@slaveSequenceCharArray);

					print "\t\tnew one\t: $slaveSequenceCharArray[$codon_1] $slaveSequenceCharArray[$codon_2] $slaveSequenceCharArray[$codon_3]\n";
					
		#			print "\t\t\t(in full: $newSequence)\n";

					$dataHash{$slave} = $newSequence;
					
					#NOTE ON THIS / TODOS
					
					#can just use str_replace() to replace codons, given start indices (remember to index to 0 / decrement)
					#make sure to iterate through all indices
					#may therefore not need to use / initialize slave sequence array
					#MAKE SURE TO WRITE FINISHED, CONVERGED SEQUENCE TO TOP-LEVEL DATA HASH

					# ........ blah coffee time now...
					
					
					# PPS remember to implement:
					#	§ phylip input processing
					#	# DNA / RNA / AA sequence type detection.
				}
			}
		}
		case('RNA'){
			$numCodons = floor(length($masterSequenceChars)/3);
			print "$numCodons codons in sequence $masterTaxon (".length($masterSequenceChars)." nt).\nPicking sites to force.\n";
			while(scalar(@convergedSiteIndices)<$numSitesToForce){
				$candidateIndex = floor(rand($numCodons));
#				print "candidate index $candidateIndex, CSI size ".scalar(@convergedSiteIndices)."\n";
				my %indicesPresent = map{$_ => 1} @convergedSiteIndices;
				unless(exists($indicesPresent{$candidateIndex})){
					push(@convergedSiteIndices,$candidateIndex);
				}
			}
			
			print "we will force sites: [".join("], [",@convergedSiteIndices)."]\nCodons:\n";
			
			# get the codons at these positions
			
			foreach $index(@convergedSiteIndices){
				$thisCodon = $masterSequenceCharArray[($index-1)*3];
				$thisCodon .= $masterSequenceCharArray[(($index-1)*3)+1];
				$thisCodon .= $masterSequenceCharArray[(($index-1)*3)+2];
				print "\t($index)\t$thisCodon\n";
				push(@convergedCodons,$thisCodon);
			}
			
			#apply these codons to the slave taxa
			
			foreach $slave(@slaveTaxonArray){
				$slaveSequence = $dataHash{$slave};
				@slaveSequenceCharArray = split(//,$slaveSequence);
				print "converging sequence $slave: ".scalar(@slaveSequenceCharArray)."\n";

				for($i = 0; $i< $numSitesToForce; $i++){
					$codonToConvergeIndex = $convergedSiteIndices[$i];
					$codonToConverge = $convergedCodons[$i];
					@codonToConvergeChars = split(//,$codonToConverge);

					$baseIndex = $codonToConvergeIndex-1;
					$codon_1 = (($baseIndex)*3)+0;
					$codon_2 = (($baseIndex)*3)+1;
					$codon_3 = (($baseIndex)*3)+2;

					print "\tconverging position $codonToConvergeIndex\tto $codonToConvergeChars[0] $codonToConvergeChars[1] $codonToConvergeChars[2]\t";
					print "\texisting: $slaveSequenceCharArray[$codon_1] $slaveSequenceCharArray[$codon_2] $slaveSequenceCharArray[$codon_3]";

					$slaveSequenceCharArray[$codon_1] = $codonToConvergeChars[0];
					$slaveSequenceCharArray[$codon_2] = $codonToConvergeChars[1];
					$slaveSequenceCharArray[$codon_3] = $codonToConvergeChars[2];
					
					$newSequence = join('',@slaveSequenceCharArray);

					print "\t\tnew one\t: $slaveSequenceCharArray[$codon_1] $slaveSequenceCharArray[$codon_2] $slaveSequenceCharArray[$codon_3]\n";
					
		#			print "\t\t\t(in full: $newSequence)\n";

					$dataHash{$slave} = $newSequence;
					
					#NOTE ON THIS / TODOS
					
					# UPDATE NOTE: 30/08/2011
					#	
					# This RNA implementation just a copy-and-paste of the DNA implementation: purely 
					# to satisfy flow control.
					# This RNA implementation has NOT BEEN TESTED AT ALL.

					#can just use str_replace() to replace codons, given start indices (remember to index to 0 / decrement)
					#make sure to iterate through all indices
					#may therefore not need to use / initialize slave sequence array
					#MAKE SURE TO WRITE FINISHED, CONVERGED SEQUENCE TO TOP-LEVEL DATA HASH

					
					
				}
			}
		}
		case('AA'){
			$numCodons = length($masterSequenceChars);
			print "Converging amino-acid sequence.\n$numCodons residues in sequence $masterTaxon (".length($masterSequenceChars)." nt).\nPicking sites to force.\n";
			
			
			# Build a set of indices for the (simulated) convergent sites
			
			while(scalar(@convergedSiteIndices)<$numSitesToForce){
				$candidateIndex = floor(rand($numCodons));
#				print "candidate index $candidateIndex, CSI size ".scalar(@convergedSiteIndices)."\n";
				my %indicesPresent = map{$_ => 1} @convergedSiteIndices;
				unless(exists($indicesPresent{$candidateIndex})){
					push(@convergedSiteIndices,$candidateIndex);
				}
			}
			
			print "we will force sites: [".join("], [",@convergedSiteIndices)."]\nCodons:\n";
			

			# get the codons at these positions
			
			foreach $index(@convergedSiteIndices){
				$thisCodon = $masterSequenceCharArray[($index-1)];
				print "\t($index)\t$thisCodon\n";
				push(@convergedCodons,$thisCodon);
			}
			

			#apply these codons to the slave taxa
			
			foreach $slave(@slaveTaxonArray){
				$slaveSequence = $dataHash{$slave};
				@slaveSequenceCharArray = split(//,$slaveSequence);
				print "converging sequence $slave: ".scalar(@slaveSequenceCharArray)."\n";

				for($i = 0; $i< $numSitesToForce; $i++){
					$codonToConvergeIndex = $convergedSiteIndices[$i]-1;
					$codonToConverge = $convergedCodons[$i];
#					@codonToConvergeChars = split(//,$codonToConverge);

#					$baseIndex = $codonToConvergeIndex-1;
#					$codon_1 = (($baseIndex)*3)+0;
#					$codon_2 = (($baseIndex)*3)+1;
#					$codon_3 = (($baseIndex)*3)+2;

					print "\tconverging position $codonToConvergeIndex\tto $codonToConverge\t\texisting: $slaveSequenceCharArray[$codonToConvergeIndex]";

					$slaveSequenceCharArray[$codonToConvergeIndex] = $codonToConverge;
#					$slaveSequenceCharArray[$codon_1] = $codonToConvergeChars[0];
#					$slaveSequenceCharArray[$codon_2] = $codonToConvergeChars[1];
#					$slaveSequenceCharArray[$codon_3] = $codonToConvergeChars[2];
					
					$newSequence = join('',@slaveSequenceCharArray);

					print "\t\tnew one\t: $slaveSequenceCharArray[$codonToConvergeIndex]\n";
					
		#			print "\t\t\t(in full: $newSequence)\n";

					$dataHash{$slave} = $newSequence;
				}
			}
		}
	};
	
	$dataHashRef = \%dataHash;
	print "hash ref for writing is $dataHashRef\n";
	$self->{DATA_HASH_REF} = $dataHashRef;
}


sub testDataHashPerturbation{
    $self = shift;
    my $masterTaxon = shift;
    my $newTaxonSequence = shift;
    my $seqHashRef = $self->{DATA_HASH_REF};
    my $numSites = $self->{NUMBER_OF_SITES};
    my $sequenceType = $self->{SEQUENCE_TYPE};
   
    local %dataHash = %$seqHashRef;
    local $masterSequenceChars = $dataHash{$masterTaxon};
    local @masterSequenceCharArray = split(//,$masterSequenceChars);
   
    print "Attempting to change $masterTaxon\n".substr($dataHash{$masterTaxon},0,10)."\n";
   
    switch($sequenceType){
        case('DNA'){
            $dataHash{$masterTaxon} = $newTaxonSequence;
        }
    };
   
    print "...find out if we changed it within the routine:\n".substr($dataHash{$masterTaxon},0,10)."\n";
   
    my $newDataHashRef = \%dataHash;
    print "old hash reference was $seqHashRef; new hash reference is $newDataHashRef\n";
    $self->{DATA_HASH_REF} = $newDataHashRef;
    print "we were working with $self\n";
    print "has the hash reference changed?\n";
    print $self{DATA_HASH_REF}."\n";
   
}


sub printShortSequences{
    my $self = shift;
    my $numberOfCharactersToPrint = shift;
    my $seqHashRef = $self->{DATA_HASH_REF};
    my $numSites = $self->{NUMBER_OF_SITES};
   
    print "Retrieving hash $seqHashRef... $numSites max characters (printing $numberOfCharactersToPrint; hash size ".%dataHash.")\n";
   
    local %dataHash = %$seqHashRef;
   
    if($numberOfCharactersToPrint > $numSites){$numberOfCharactersToPrint = $numSites;print "too many sites; printing whole sequence\n"}
   
    foreach my $taxon(sort keys %dataHash){
        $sequence = $dataHash{$taxon};
        $subSequence = substr($sequence,0,$numberOfCharactersToPrint);
        print "\t$taxon:\t$subSequence\n";
    }
}


sub determineSequenceType{
	my $self = shift;
	my $sequenceType = 'undefined';
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my $numTaxa = $self->{NUMBER_OF_TAXA};
	my @counts = (0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	my @frequencies = (0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	my @candidates = split(//,uc('abcdefghijklmnopqrstuvwxyz-?*'));

	local %dataHash = %$seqHashRef;

	foreach $taxon(sort keys %dataHash){
		$sequence = uc($dataHash{$taxon});
		@charArray = split(//,$sequence);
		foreach $char(@charArray){
			switch($char){
				case 'A' { $counts[0]++; }
				case 'B' { $counts[1]++; }
				case 'C' { $counts[2]++; }
				case 'D' { $counts[3]++; }
				case 'E' { $counts[4]++; }
				case 'F' { $counts[5]++; }
				case 'G' { $counts[6]++; }
				case 'H' { $counts[7]++; }
				case 'I' { $counts[8]++; }
				case 'J' { $counts[9]++; }
				case 'K' { $counts[10]++; }
				case 'L' { $counts[11]++; }
				case 'M' { $counts[12]++; }
				case 'N' { $counts[13]++; }
				case 'O' { $counts[14]++; }
				case 'P' { $counts[15]++; }
				case 'Q' { $counts[16]++; }
				case 'R' { $counts[17]++; }
				case 'S' { $counts[18]++; }
				case 'T' { $counts[19]++; }
				case 'U' { $counts[20]++; }
				case 'V' { $counts[21]++; }
				case 'W' { $counts[22]++; }
				case 'X' { $counts[23]++; }
				case 'Y' { $counts[24]++; }
				case 'Z' { $counts[25]++; }
				case '-' { $counts[26]++; }
				case '?' { $counts[27]++; }
				case '*' { $counts[28]++; }
			}
		}
	}

	print "residue\tCounts\tGlobal freq. (by fi = Ni/($numSites * $numTaxa))\n";
	$denom = $numSites * $numTaxa;
	$total = 0;
	
	for($i=0;$i<29;$i++){
		$frequencies[$i] = $counts[$i] / $denom;
		$total += $frequencies[$i];
		print "$candidates[$i]:\t$counts[$i]\t$frequencies[$i]\n";
	}
	
	print "Total observations: $total\n";

	$obsDNA = $frequencies[0]+$frequencies[2]+$frequencies[6]+$frequencies[19]+$frequencies[26];
	$obsRNA = $frequencies[0]+$frequencies[2]+$frequencies[6]+$frequencies[20]+$frequencies[26];
	$obsAA =	$frequencies[0]+
				$frequencies[2]+
				$frequencies[3]+
				$frequencies[4]+
				$frequencies[5]+
				$frequencies[6]+
				$frequencies[7]+
				$frequencies[8]+
				$frequencies[10]+
				$frequencies[11]+
				$frequencies[12]+
				$frequencies[13]+
				$frequencies[15]+
				$frequencies[16]+
				$frequencies[17]+
				$frequencies[18]+
				$frequencies[19]+
				$frequencies[21]+
				$frequencies[22]+
				$frequencies[24]+
				$frequencies[26]
				;
	
	print "Predictions:\tDNA:$obsDNA\tRNA:$obsRNA\tProtein:$obsAA\n";
	
	if($obsDNA > 0.8){
		if($obsDNA > $obsRNA){
			$sequenceType = 'DNA';
		}else{
			$sequenceType = 'RNA';
		}
	}else{
		if($obsRNA > $obsAA){
			$sequenceType = 'RNA';
		}else{
			$sequenceType = 'AA';
		}
	}
	
	return $sequenceType;
}


sub translate{

##### Translation table #####

	%lookupDNAtoAA = ("UUU" => "F",	"UCU" => "S",	"UAU" => "Y",	"UGU" => "C",	"UUC" => "F",	"UCC" => "S",	"UAC" => "Y",	"UGC" => "C",	"UUA" => "L",	"UCA" => "S",	"UAA" => "*",	"UGA" => "*",	"UUG" => "L",	"UCG" => "S",	"UAG" => "*",	"UGG" => "W",	"CUU" => "L",	"CCU" => "P",	"CAU" => "H",	"CGU" => "R",	"CUC" => "L",	"CCC" => "P",	"CAC" => "H",	"CGC" => "R",	"CUA" => "L",	"CCA" => "P",	"CAA" => "Q",	"CGA" => "R",	"CUG" => "L",	"CCG" => "P",	"CAG" => "Q",	"CGG" => "R",	"AUU" => "I",	"ACU" => "T",	"AAU" => "N",	"AGU" => "S",	"AUC" => "I",	"ACC" => "T",	"AAC" => "N",	"AGC" => "S",	"AUA" => "I",	"ACA" => "T",	"AAA" => "K",	"AGA" => "R",	"AUG" => "M",	"ACG" => "T",	"AAG" => "K",	"AGG" => "R",	"GUU" => "V",	"GCU" => "A",	"GAU" => "D",	"GGU" => "G",	"GUC" => "V",	"GCC" => "A",	"GAC" => "D",	"GGC" => "G",	"GUA" => "V",	"GCA" => "A",	"GAA" => "E",	"GGA" => "G",	"GUG" => "V",	"GCG" => "A",	"GAG" => "E",	"GGG" => "G");	
	%lookupAAtoDNA = ("F" => "UUU",	"S" => "UCU",	"Y" => "UAU",	"C" => "UGU",	"F" => "UUC",	"S" => "UCC",	"Y" => "UAC",	"C" => "UGC",	"L" => "UUA",	"S" => "UCA",	"*" => "UAA",	"*" => "UGA",	"L" => "UUG",	"S" => "UCG",	"*" => "UAG",	"W" => "UGG",	"L" => "CUU",	"P" => "CCU",	"H" => "CAU",	"R" => "CGU",	"L" => "CUC",	"P" => "CCC",	"H" => "CAC",	"R" => "CGC",	"L" => "CUA",	"P" => "CCA",	"Q" => "CAA",	"R" => "CGA",	"L" => "CUG",	"P" => "CCG",	"Q" => "CAG",	"R" => "CGG",	"I" => "AUU",	"T" => "ACU",	"N" => "AAU",	"S" => "AGU",	"I" => "AUC",	"T" => "ACC",	"N" => "AAC",	"S" => "AGC",	"I" => "AUA",	"T" => "ACA",	"K" => "AAA",	"R" => "AGA",	"M" => "AUG",	"T" => "ACG",	"K" => "AAG",	"R" => "AGG",	"V" => "GUU",	"A" => "GCU",	"D" => "GAU",	"G" => "GGU",	"V" => "GUC",	"A" => "GCC",	"D" => "GAC",	"G" => "GGC",	"V" => "GUA",	"A" => "GCA",	"E" => "GAA",	"G" => "GGA",	"V" => "GUG",	"A" => "GCG",	"E" => "GAG",	"G" => "GGG");
	
	
	
	# U
	# UUU 	(Phe/F) Phenylalanine 	
	# UCU 	(Ser/S) Serine 	
	# UAU 	(Tyr/Y) Tyrosine 	
	# UGU 	(Cys/C) Cysteine
	# UUC 	(Phe/F) Phenylalanine 	
	# UCC 	(Ser/S) Serine 	
	# UAC 	(Tyr/Y) Tyrosine 	
	# UGC 	(Cys/C) Cysteine
	# UUA 	(Leu/L) Leucine 	
	# UCA 	(Ser/S) Serine 
	# UAA 	Stop (Ochre) 
	# UGA 	Stop (Opal)
	# UUG 	(Leu/L) Leucine 
	# UCG 	(Ser/S) Serine 
	# UAG 	Stop (Amber) 
	# UGG 	(Trp/W) Tryptophan    
	# C 
	# CUU 	(Leu/L) Leucine 
	# CCU 	(Pro/P) Proline 
	# CAU 	(His/H) Histidine 
	# CGU 	(Arg/R) Arginine
	# CUC 	(Leu/L) Leucine 
	# CCC 	(Pro/P) Proline 
	# CAC 	(His/H) Histidine 
	# CGC 	(Arg/R) Arginine
	# CUA 	(Leu/L) Leucine 
	# CCA 	(Pro/P) Proline 
	# CAA 	(Gln/Q) Glutamine 
	# CGA 	(Arg/R) Arginine
	# CUG 	(Leu/L) Leucine 
	# CCG 	(Pro/P) Proline 
	# CAG 	(Gln/Q) Glutamine 
	# CGG 	(Arg/R) Arginine
	# A 
	# AUU 	(Ile/I) Isoleucine 
	# ACU 	(Thr/T) Threonine         
	# AAU 	(Asn/N) Asparagine 
	# AGU 	(Ser/S) Serine
	# AUC 	(Ile/I) Isoleucine 
	# ACC 	(Thr/T) Threonine 
	# AAC 	(Asn/N) Asparagine 
	# AGC 	(Ser/S) Serine
	# AUA 	(Ile/I) Isoleucine 
	# ACA 	(Thr/T) Threonine 
	# AAA 	(Lys/K) Lysine 
	# AGA 	(Arg/R) Arginine
	# AUG[A] 	(Met/M) Methionine 
	# ACG 	(Thr/T) Threonine 
	# AAG 	(Lys/K) Lysine 
	# AGG 	(Arg/R) Arginine
	# G 
	# GUU 	(Val/V) Valine 
	# GCU 	(Ala/A) Alanine 
	# GAU 	(Asp/D) Aspartic acid 
	# GGU 	(Gly/G) Glycine
	# GUC 	(Val/V) Valine 
	# GCC 	(Ala/A) Alanine 
	# GAC 	(Asp/D) Aspartic acid 
	# GGC 	(Gly/G) Glycine
	# GUA 	(Val/V) Valine 
	# GCA 	(Ala/A) Alanine 
	# GAA 	(Glu/E) Glutamic acid 
	# GGA 	(Gly/G) Glycine
	# GUG 	(Val/V) Valine 
	# GCG 	(Ala/A) Alanine 
	# GAG 	(Glu/E) Glutamic acid 
	# GGG 	(Gly/G) Glycine

	my $self = shift;
	my $sequenceType = $self->{SEQUENCE_TYPE};
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my $numTaxa = $self->{NUMBER_OF_TAXA};
	my %seqHash = %$seqHashRef;
	
	if($sequenceType eq 'AA'){
		die "Can't translate this! Already an amino-acid sequence...\n";
	}else{
		print "translating $sequenceType. $numSites bases, $numTaxa taxa.\n";
		$newNumSites;
		foreach $UID (sort keys %seqHash){
			unless($UID =~ /HASH/){
#				print "translating $UID\n";
				$newAAseq = '';
				$numStopCodons = 0;
				$numAmbiguousCodons = 0;
				$sequence = $seqHash{$UID};
	#			print "translating\t".substr($sequence,0,20)."\n";
				$sequence =~ s/T/U/g;
	#			print "(RNA version)\t".substr($sequence,0,20)."\n";
				for($pos = 0;$pos<$numSites;$pos = $pos+3){
					$codon = substr($sequence,$pos,3);
					$AA = $lookupDNAtoAA{$codon};
					if(length($codon)<3){
						$AA = '-';
						$numAmbiguousCodons++;
					}
					if($codon =~ /-/){
						$AA = '-';			##### NB THIS LOSES INFORMATION AS MANY 3 position sites are synonymous etc..
						print "\t$codon\t[$AA]\n";
						$numAmbiguousCodons++;
					}
	#				print "\t\t$codon\t[$AA]\n";
					if($AA eq '*'){
						$numStopCodons ++;
					}
					$newAAseq = $newAAseq . $AA;
				}
				if(length($newAAseq)>$newNumSites){
					$newNumSites = length($newAAseq);
				}
				$seqHash{$UID} = $newAAseq;
				print "$numStopCodons stop codons found.\t$numAmbiguousCodons ambiguous found in $UID\n";
			}
		}
		$self -> {NUMBER_OF_SITES} = $newNumSites;
	}
	$self -> {DATA_HASH_REF} = \%seqHash;
	$self -> {SEQUENCE_TYPE} = 'AA';
}


sub removeTaxa{
	my $self = shift;
	my $keysToRemoveRef = shift;
	my $sequenceType = $self->{SEQUENCE_TYPE};
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my $numTaxa = $self->{NUMBER_OF_TAXA};
	my %seqHash = %$seqHashRef;
	my @keysToRemove = @$keysToRemoveRef;
	
	foreach $destroy(@keysToRemove){
		if(exists $seqHash{$destroy}){
			delete $seqHash{$destroy};
			$numTaxa --;
		}
	}
	
	$self -> {NUMBER_OF_TAXA} = $numTaxa;
	$self -> {DATA_HASH_REF} = \%seqHash;
}


sub removeStopCodons{
	my $self = shift;
	my $keysToRemoveRef = shift;
	my $sequenceType = $self->{SEQUENCE_TYPE};
	my $seqHashRef = $self->{DATA_HASH_REF};
	my $numSites = $self->{NUMBER_OF_SITES};
	my $numTaxa = $self->{NUMBER_OF_TAXA};
	my %seqHash = %$seqHashRef;
	
	foreach $UID(keys %seqHash){
		$seq = $seqHash{$UID};
		$seq =~ s/\*/\-/g;
		$seqHash{$UID} = $seq;
	}
	
	$self -> {NUMBER_OF_TAXA} = $numTaxa;
	$self -> {DATA_HASH_REF} = \%seqHash;
}



1;