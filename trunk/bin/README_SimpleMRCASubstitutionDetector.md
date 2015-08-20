_Reconstructs ancestral sequences given a tree and alignment. Identifies parallel substitutions._

##Description
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

##Detailed help
Detailed help/manuals aren't available at the moment (July 2015) but please see the [general help pages](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/README.md#help) for this API.

## Depdendencies and packaged libraries

 Requires Java 1.6+. 
The codebase references the following open-source libraries:                 
* [Colt](https://dst.lbl.gov/ACSSoftware/colt/) - Open Source Libraries for High Performance Scientific and Technical Computing (colt.jar)
* [JAMA](http://math.nist.gov/javanumerics/jama/) - Java Matrix Algebra (1.0.3, 2012.11.09; Jama-1.0.3.jar)
* [JSC](http://www.jsc.nildram.co.uk/) - Java Statistical Classes v1.0 (jsc-1.jar)
* [XStream](http://x-stream.github.io/) - Java XML bindings (xstream-1.4.4.jar and dependencies)
* [JFreeChart](http://www.jfree.org/jfreechart/) (jfreechart-1.0.14.jar)
* [JEBL](http://sourceforge.net/projects/jebl/) - Java Evolutionary Biology Library v0.4 (jebl-0.4.jar)

The executable jarfile (this trunk/bin directory) comes with these build-path dependencies already packaged. It should run as a standalone Java app with no additional dependencies.


