$topo1Filename = "lnf";
$topo2Filename = "lnf_concat";

@topo1_lines;#the data

$firstline = 0;

open(IN,$topo1Filename) or die("Can't open lnf file $topo1Filename");
while(<IN>){
	chomp($line = $_);
	if($firstline < 1){
		@fields = split(/\ +/,$line);
		print join("|--|",@fields)."\n";
		$numSites1 = $fields[2];
		$numPatterns1 = $fields[3];
		$firstline = 1;
	}
	if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
		push(@topo1_lines,$line);
	}
}
close(IN);

@topo2_lines;#the data

open(IN,$topo2Filename) or die("Can't open lnf file $topo2Filename");
while(<IN>){
	chomp($line = $_);
	if(length($line) > 40){		#we are ASSUMING no lnf file line other than data is > 40 chars
		push(@topo2_lines,$line);
	}
}
close(IN);

open(OUT,'>','myBuilt_lnf.lnf');
print OUT "     2     $numSites1    $numPatterns1\n\n";
print OUT "\n 1\n\n";
print OUT join("\n",@topo1_lines);
print OUT "\n\n 2\n\n";
print OUT join("\n",@topo2_lines);

close(OUT);