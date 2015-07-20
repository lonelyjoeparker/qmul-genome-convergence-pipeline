##Description##
The [SimpleMRCASubstitutionDetector.jar](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/SimpleMRCASubstitutionDetector.jar) jarfile will run with [these input files](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/examples/) as follows:

`java -jar SimpleMRCASubstitutionDetector.jar input.phy input.tre CANIS:FELIS PTERONOTUS:MYOTIS`

From that you should get [this output](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/examples/output_SimpleMRCASubstitutionDetector.txt).



The format is:

`java -jar SimpleMRCASubstitutionDetector.jar <alignment> <tree> <taxon:taxon..> <taxon:taxon..> <..>`

Where:
 * Alignment is one of phylip / fasta  / nexus
 * Phylogeny is (ideally) newick 
 * Each 'clade' is described by a list of taxon names with correct spelling and capitalisation, separated by a colon ':' with no whitespace.
 * There are n>1 or more clades, so there should be at least 3 arguments in total.

It will do three things:

Firstly read in the phylogeny and alignment, and do an ancestral sequence reconstruction by parsimony. Any remaining ambiguous nodes resolved to a single state randomly. All sites assumed iid.

Secondly find the MRCA for each clade listed, and print out the MRCA for that clade, all the branches with substitutions underneath it.

Finally for each MRCA, print out substitituons from that node to the clades' tips in order (e.g. both the tips directly below that clade, and also tips below other nodes, e.g. 'parallel')

There are also some pairwise tip comparisons as checks, and summary counts. 

##Detailed help##
Detailed help/manuals aren't available at the moment (July 2015) but please see the [general help pages](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/edit/master/trunk/bin/README.md) for this API.
