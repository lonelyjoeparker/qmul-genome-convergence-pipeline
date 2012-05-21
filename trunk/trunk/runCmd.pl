#! /usr/bin/perl -w

print "QMUL_GCP\n";

foreach $arg(@ARGV){
#	$exe .= $arg." ";
	print "\t$arg\n";
}

$wd = shift(@ARGV);
print "dir change to $wd\n";
chdir($wd);
$exe = '';

foreach $arg(@ARGV){
	$exe .= $arg." ";
#	print "\t$arg\n";
}

#open(OUT,'>',"systemScript.sh");
#close(OUT);
print "runCmd exe:$exe\t";
system($exe);
#system(`./systemScript.sh > systemScript.out 2> systemScript.err`);
#system(`qsub ./systemScript.sh > systemScript.out 2> systemScript.err`);