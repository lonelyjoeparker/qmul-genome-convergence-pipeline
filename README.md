# qmul-genome-convergence-pipeline
Automatically exported from code.google.com/p/qmul-genome-convergence-pipeline

A collection of Java libraries and executables for conducting phylogenomic analyses, mainly for convergence detection.
Includes wrappers to a variety of tools including PAML, RAxML, and PhyloBayes.

## Projects:

Several projects including:
* [CONTEXT, a phylogenomic dataset browser](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/CONTEXT.md) (_current major version: v0.8.prerelease_)([all versions](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/CONTEXT_versions.md))
* A [simple MRCA substitution detector](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/README_SimpleMRCASubstitutionDetector.md) for finding parallel substitutions given an alignment and phylogeny (_current: public alpha, no version number_)
* A genome convergence detection pipeline (_current: in progress - no public release_)

### Depdendencies and packaged libraries

Requires Java 1.6+. 
The codebase references the following open-source libraries:                 
* [Colt](https://dst.lbl.gov/ACSSoftware/colt/) - Open Source Libraries for High Performance Scientific and Technical Computing (colt.jar)
* [JAMA](http://math.nist.gov/javanumerics/jama/) - Java Matrix Algebra (1.0.3, 2012.11.09; Jama-1.0.3.jar)
* [JSC](http://www.jsc.nildram.co.uk/) - Java Statistical Classes v1.0 (jsc-1.jar)
* [XStream](http://x-stream.github.io/) - Java XML bindings (xstream-1.4.4.jar and dependencies)
* [JFreeChart](http://www.jfree.org/jfreechart/) (jfreechart-1.0.14.jar)
* [JEBL](http://sourceforge.net/projects/jebl/) - Java Evolutionary Biology Library v0.4 (jebl-0.4.jar)

The executable jarfiles (see trunk/bin directory) come with these build-path dependencies already packaged. They should run as standalone Java apps with no additional dependencies


