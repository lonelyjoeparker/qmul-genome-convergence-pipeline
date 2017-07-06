# READ ME... #

*Jump to: [Usage/contents](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/tree/master/trunk/bin#usage) | [Help](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/tree/master/trunk/bin#help)*

Collection of executable jarfiles for a variety of tasks. 

Generally compatible with Java 1.6+.

_Hint: you'll need to right-click on the compressed archives and choose 'Save link as...' in most browsers..._

### Warranty ###
While some elements of this code have test harnesses and been written to test specifications, the vast majority of methods have only undergone limited testing. 

You should **use this code at your own risk** and in particular, compare to your expectations and other applications' output. 

*If you think you have found a bug, please report it here: https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues*

### Help
If you have a problem or need help using a binary, please first read [this general release information](http://www.lonelyjoeparker.com/?p=1255).
If you think you have found a bug or error please check the [existing issues on GitHub](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues).
If you still haven't got any further and want to send a question, email joe+bioinformaticshelp@kitserve.org.uk. I may or may not be able to answer your question quickly. **Please include these details**: 
 * Which Java version, and which jarfile you are using (and where/when you downloaded it);
 * Which OS and version you're using;
 * Error trace from the console (this will usually have the word 'Exception:' and some other lines, probably numbered);
 * The input command and input files as well as any output. **If your input/output files are larger than 1MB, please either send only the first few lines, or else use an online filestore like http://wetransfer.com to send me the whole input.**

### Usage
For usage details and examples, see the specific READMEs:
 * CONTEXT (the COmparativeNucleotide(and)TreesExplorationTool; previously 'PhylogenomicDatasetBrowser'):  [CONTEXT.md](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/CONTEXT.md)
  * *GUI application to browse and rapidly sort/compare phylogenomic-scale datasets (thousands of alignments/phylogenies).*
 * BasicAlignmentStats: [no readme available yet]()
  * *Reads an alignment or directory of alignments and prints descriptive statistics (length, gappiness, entropy etc) for each one.* 
 * GeneralConvergenceApplicationGUI:  [no readme available yet]()
  * *GUI application to create and run convergence detection analyses on phylogenomic data. Not released.*
 * SimpleMRCASubstitutionDetector: [README_SimpleMRCASubstitutionDetector.md](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/README_SimpleMRCASubstitutionDetector.md)
  * *Reconstructs ancestral sequences using parsimony, finds and prints parallel substitutions*
  

## Depdendencies and packaged libraries

Requires Java 1.6+. 
The codebase references the following open-source libraries:                 
* [Colt](https://dst.lbl.gov/ACSSoftware/colt/) - Open Source Libraries for High Performance Scientific and Technical Computing (colt.jar)
* [JAMA](http://math.nist.gov/javanumerics/jama/) - Java Matrix Algebra (1.0.3, 2012.11.09; Jama-1.0.3.jar)
* [JSC](http://www.jsc.nildram.co.uk/) - Java Statistical Classes v1.0 (jsc-1.jar)
* [XStream](http://x-stream.github.io/) - Java XML bindings (xstream-1.4.4.jar and dependencies)
* [JFreeChart](http://www.jfree.org/jfreechart/) (jfreechart-1.0.14.jar)
* [JEBL](http://sourceforge.net/projects/jebl/) - Java Evolutionary Biology Library v0.4 (jebl-0.4.jar)
* [XChart](https://github.com/timmolter/XChart) - XChart chart/plotting library by Tim Molter v3.3.1 (XChart-3.3.1.jar)

These executable jarfiles (this trunk/bin directory) come with these build-path dependencies already packaged. They should run as standalone Java apps with no additional dependencies.


