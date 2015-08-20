# CONTEXT / Phylogenomic Dataset Browser

_COmparative NucleoTide (and amino-acid) EXplorer Tool_

---

## What does it do?

Simultaneously displays large numbers (~thousands) of multiple sequence alignments and/or phylogenies, along with summary statistics. 

## Why does it do that?

Phylogenomic analyses rely on good quality input data. Visualising and _quantitatively_ sorting/filtering input data is essential. There isn't a simple standalone tool for this at the moment.

## What _doesn't_ it do?

You can't manually edit, align, or infer phylogenies with CONTEXT. There's plenty of other tools to do this. See RAxML, Muscle, Bali-Phy, Se-Al or GUIs like HYPHY or Geneious, for a start.

## How do I get it?

This repository. See [the trunk/bin directory](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/tree/master/trunk/bin) for the most recent builds - [current version here](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/CONTEXT-PhylogenomicDatasetBrowser-v0.8.jar?raw=true) (v0.8 prerelease).

## How do I use it?

Double-click the jarfile; or in a command-line run the jarfile with `java -jar CONTEXT-PhylogenomicDatasetBrowser-v0.8.jar`. Once it's up and running, select "File > Open Alignments..." or "File > Open Phylogenies" to get started.

## How do I get help?

At the moment we're still writing the manual and help pages. General information can be found in the [README](README.md) file.

To report bugs or request features please use [this issue tracker](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues) - that way we can keep a better grip on what needs doing, and others may be able to benefit from your solutions. Please include as much detail as you can, including:
* Which OS you're using
* Which version of Java you're using (try typing `java -version` on the command line)
* What input files you were trying to load
* A copy of any error message
* A screenshot if applicable

Thanks!

## Version

Currently at public 0.8 version. See [CONTEXT_versions.md](CONTEXT_versions.md) for more change log and version informations.

## Credits

CONTEXT is written in Java by Joe Parker with input from Steve Rossiter and other members of his lab. 

CONTEXT was largely developed from the Genome Convergence API supported by the BBSRC (UK) and European Research Council (2011-2015), with additional work by Joe at Kitson Consulting Ltd (2015). Continuing development / mainenance is by Joe at Royal Botanic Gardens, Kew.

## Depdendencies and packaged libraries

CONTEXT requires Java 1.6+. 
The CONTEXT codebase references the following open-source libraries:                 
* [Colt](https://dst.lbl.gov/ACSSoftware/colt/) - Open Source Libraries for High Performance Scientific and Technical Computing (colt.jar)
* [JAMA](http://math.nist.gov/javanumerics/jama/) - Java Matrix Algebra (1.0.3, 2012.11.09; Jama-1.0.3.jar)
* [JSC](http://www.jsc.nildram.co.uk/) - Java Statistical Classes v1.0 (jsc-1.jar)
* [XStream](http://x-stream.github.io/) - Java XML bindings (xstream-1.4.4.jar and dependencies)
* [JFreeChart](http://www.jfree.org/jfreechart/) (jfreechart-1.0.14.jar)
* [JEBL](http://sourceforge.net/projects/jebl/) - Java Evolutionary Biology Library v0.4 (jebl-0.4.jar)

The executable jarfiles (see trunk/bin directory) come with these build-path dependencies already packaged. They should run as standalone Java apps with no additional dependencies


