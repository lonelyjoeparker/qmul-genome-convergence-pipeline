#! /usr/bin/perl

use Carp;
use PhymlSequenceRepresentation;
use NewickTreeRepresentation;
use ExternalFunctions;
use CodemlAnalysis;
use BasemlAnalysis;
use AamlAnalysis;

# Execution:

# Get simulation number and number of sites to force from ARGV

# System make exe dir

# System copy resources to exe dir

# Perl read in file

# Perl converge

# Perl Baseml on both NT trees, using unique ID and copying lnf each time

# Perl consel

# Perl SSLS

# Parse SSLS and consel data, write to outfile

# System copy results files (paml out and consel / SSLS out) to results
 
# Perl translate, remove stop

# Perl converge

# Perl Codeml on both AA trees, using unique ID and copying lnf each time

# Perl consel

# Perl SSLS

# Parse SSLS and consel data, write to outfile

# System copy results files (paml out and consel / SSLS out) to results

# System remove working files from wdir (this enabled when I know it's working



##########

# Execution:

# Get simulation number and number of sites to force from ARGV

$simNumber = $ARGV[0];
$numConvergence = $ARGV[1];


# Perl read in file

$psr = PhymlSequenceRepresentation->new();
$psr->loadSequences("prestin_LARGEhierarchicalFit_sim.$simNumber");


# Destroy some sequences (housekeeping)

$psr->printShortSequences(10);
@removeThem = ('DOLPHIN_DE','RISSOS_DOL','ASELLISCUS','BALAENOPTE','BARBASELLA','BOAR','EONYCTERIS','FELIS_CATU','GERBIL','GREATER_FA','GREATER_TU','HARBOUR_PO','HYPEROODON','H_ARM','H_LARVATUS','H_PRATTI','KOGIA_BREV','LESSER_MOU','MESOPLODON','MUS_MUS','M_FULIGIN','RABBIT_ORY','ROUSETTUS','R_LUCTUS','R_PUSILLUS','ZIPHIUS');
$psr->removeTaxa(\@removeThem);
$psr->printShortSequences(10);
$psr->writePhylipFile("./prestin_LARGEhierarchicalFit_sim.$simNumber");

# Perl Baseml on both NT trees, using unique ID and copying lnf each time

#$DNA1 = BasemlAnalysis->runBasemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.phy", "./desperateSpecies.tre","./speciesNT.s=$simNumber.c=$numConvergence"); #could also do top matrix (dayhoff) for one and blah for the other?
#$command = "cp lnf lnf_NTspecies";
#system(`$command`);

#$DNA2 = BasemlAnalysis->runBasemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.phy", "./desperatePrestin.tre","./prestinNT.s=$simNumber.c=$numConvergence"); #could also do top matrix (dayhoff) for one and blah for the other?
#$command = "cp lnf lnf_NTprestin";
#system(`$command`);

#system(`rm lnf rst rst1 rub 2base.t`);


# Perl consel

#$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_NTspecies","lnf_NTprestin");
#print "$conselReadyFile\n";


# Perl SSLS

#@transposed_sites = $psr->getTransposedSites();
#@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_NTspecies","lnf_NTprestin",\@transposed_sites,'DNA',0,'../results/dSSLS.out');


# Perl converge

$masterTaxon = 'T_TRUNCAT';
@slaveTaxonArray = ('R_FERRUM');
   

$slaveTaxonArrayRef = \@slaveTaxonArray;
$psr->forceConvergence($numConvergence,$masterTaxon,$slaveTaxonArrayRef);

$psr->writePhylipFile("./prestin_LARGEhierarchicalFit_sim.converged.$simNumber");
$psr->writePhylipFile("./prestin_LARGEhierarchicalFit_sim.$simNumber.COD.converged");


# Perl Baseml on both NT trees, using unique ID and copying lnf each time

$DNA1 = BasemlAnalysis->runBasemlAnalysis("./prestin_LARGEhierarchicalFit_sim.converged.$simNumber.phy", "./desperateSpecies.tre","./speciesNT.s=$simNumber.c=$numConvergence.converged"); #could also do top matrix (dayhoff) for one and blah for the other?
$command = "cp lnf lnf_NTspecies_converged";
system(`$command`);

$DNA2 = BasemlAnalysis->runBasemlAnalysis("./prestin_LARGEhierarchicalFit_sim.converged.$simNumber.phy", "./desperatePrestin.tre","./prestinNT.s=$simNumber.c=$numConvergence.converged"); #could also do top matrix (dayhoff) for one and blah for the other?
$command = "cp lnf lnf_NTprestin_converged";
system(`$command`);

#system(`rm lnf rst rst1 rub 2base.t`);


# Perl consel

$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_NTspecies_converged","lnf_NTprestin_converged");
print "$conselReadyFile\n";


# Perl SSLS

@transposed_sites = $psr->getTransposedSites();
@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_NTspecies_converged","lnf_NTprestin_converged",\@transposed_sites,'DNA',$numConvergence,'../results/dSSLS.out');
#print join("\n\t",@SSLS);


# Parse SSLS and consel data, write to outfile

# (TODO)


# System copy results files (paml out and consel / SSLS out) to results
 
# (TODO)


# Perl read in file

$psr = PhymlSequenceRepresentation->new();
$psr->loadSequences("prestin_LARGEhierarchicalFit_sim.$simNumber");


# Destroy some sequences (housekeeping)

#$psr->printShortSequences(10);
@removeThem = ('DOLPHIN_DE','RISSOS_DOL','ASELLISCUS','BALAENOPTE','BARBASELLA','BOAR','EONYCTERIS','FELIS_CATU','GERBIL','GREATER_FA','GREATER_TU','HARBOUR_PO','HYPEROODON','H_ARM','H_LARVATUS','H_PRATTI','KOGIA_BREV','LESSER_MOU','MESOPLODON','MUS_MUS','M_FULIGIN','RABBIT_ORY','ROUSETTUS','R_LUCTUS','R_PUSILLUS','ZIPHIUS');
$psr->removeTaxa(\@removeThem);
#$psr->printShortSequences(10);
$psr->writePhylipFile("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA");

#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.phy", "./desperateSpecies.tre","./speciesAA.s=$simNumber.c=$numConvergence",2,'jones.dat');
#system(`cp lnf lnf_AAspecies`);
#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.phy", "./desperatePrestin.tre","./prestinAA.s=$simNumber.c=$numConvergence",2,'jones.dat');
#system(`cp lnf lnf_AAprestin`);


# Perl consel

#$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_AAspecies","lnf_AAprestin");
#print "$conselReadyFile\n";


# Perl SSLS

#@transposed_sites = $psr->getTransposedSites();
#foreach $hmm(@transposed_sites){
#	print "\ttrans:\t$hmm\n";
#}
#@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_AAspecies","lnf_AAprestin",\@transposed_sites,'AA',0,'../results/dSSLS.out');
#print join("\n\t",@SSLS);


# Perl converge

$masterTaxon = 'T_TRUNCAT';
@slaveTaxonArray = ('R_FERRUM');
$slaveTaxonArrayRef = \@slaveTaxonArray;
#$psr->printShortSequences(100);
$psr->translate();
#$psr->printShortSequences(100);
$psr->removeStopCodons();
#$psr->printShortSequences(100);
$psr->forceConvergence($numConvergence,$masterTaxon,$slaveTaxonArrayRef);
#$psr->printShortSequences(100);
$psr->writePhylipFile("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged");

# Perl Aaml on both trees

 $AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./desperateSpecies.tre","./speciesAA.s=$simNumber.c=$numConvergence",3,'jones.dat');
 system(`cp lnf lnf_AAspecies_converged`);
 $AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./desperatePrestin.tre","./prestinAA.s=$simNumber.c=$numConvergence",3,'jones.dat');
 system(`cp lnf lnf_AAprestin_converged`);

#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./RAxML_bestTree.2011_prestin_large.WAG.tre","./speciesAA.s=$simNumber.c=$numConvergence.converged",2,'jones.dat');
#system(`cp lnf lnf_AAspecies_converged`);
#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./RAxML_bestTree.2011_prestin_large.BLOSUM62.tre","./prestinAA.s=$simNumber.c=$numConvergence.converged",2,'jones.dat');
#system(`cp lnf lnf_AAprestin_converged`);


# Perl consel

$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_AAspecies_converged","lnf_AAprestin_converged");
print "$conselReadyFile\n";


# Perl SSLS

@transposed_sites = $psr->getTransposedSites();
#foreach $hmm(@transposed_sites){
#	print "\ttrans:\t$hmm\n";
#}
@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_AAspecies_converged","lnf_AAprestin_converged",\@transposed_sites,'AA',$numConvergence,'../results/dSSLS.out');
#print join("\n\t",@SSLS);


# Perl Codeml on both AA trees, using unique ID and copying lnf each time

 $AA1 = CodemlAnalysis->runCodemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./desperateSpecies.tre","./speciesAA2.s=$simNumber.c=$numConvergence",2,'jones.dat');
 system(`cp lnf lnf_AA2species_converged`);
 $AA1 = CodemlAnalysis->runCodemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./desperatePrestin.tre","./prestinAA2.s=$simNumber.c=$numConvergence",2,'jones.dat');
 system(`cp lnf lnf_AA2prestin_converged`);

#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./RAxML_bestTree.2011_prestin_large.WAG.tre","./speciesAA.s=$simNumber.c=$numConvergence.converged",2,'jones.dat');
#system(`cp lnf lnf_AAspecies_converged`);
#$AA1 = AamlAnalysis->runAamlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.AA.converged.phy", "./RAxML_bestTree.2011_prestin_large.BLOSUM62.tre","./prestinAA.s=$simNumber.c=$numConvergence.converged",2,'jones.dat');
#system(`cp lnf lnf_AAprestin_converged`);


# Perl consel

$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_AA2species_converged","lnf_AA2prestin_converged");
print "$conselReadyFile\n";


# Perl SSLS

@transposed_sites = $psr->getTransposedSites();
#foreach $hmm(@transposed_sites){
#	print "\ttrans:\t$hmm\n";
#}
@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_AA2species_converged","lnf_AA2prestin_converged",\@transposed_sites,'AA2',$numConvergence,'../results/dSSLS.out');
#print join("\n\t",@SSLS);


#CODDON MODEL

 $AA1 = CodemlAnalysis->runCodemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.COD.converged.phy", "./desperateSpecies.tre","./speciesCOD.s=$simNumber.c=$numConvergence",3,'jones.dat');
 system(`cp lnf lnf_CODspecies_converged`);
 $AA1 = CodemlAnalysis->runCodemlAnalysis("./prestin_LARGEhierarchicalFit_sim.$simNumber.COD.converged.phy", "./desperatePrestin.tre","./prestinCOD.s=$simNumber.c=$numConvergence",3,'jones.dat');
 system(`cp lnf lnf_CODprestin_converged`);

# Perl consel

$conselReadyFile = ExternalFunctions->runConselAnalysis("lnf_CODspecies_converged","lnf_CODprestin_converged");
print "$conselReadyFile\n";

# Perl SSLS

@transposed_sites = $psr->getTransposedSites();
#foreach $hmm(@transposed_sites){
#	print "\ttrans:\t$hmm\n";
#}
@SSLS = ExternalFunctions->getPamlSitewiseSSLS("lnf_CODspecies_converged","lnf_CODprestin_converged",\@transposed_sites,'COD',$numConvergence,'../results/dSSLS.out');



# Parse SSLS and consel data, write to outfile

# (TODO)


# System copy results files (paml out and consel / SSLS out) to results

# (TODO)


# System remove working files from wdir (this enabled when I know it's working)

# (TODO)
# (now probably the responsibility of the handler script)