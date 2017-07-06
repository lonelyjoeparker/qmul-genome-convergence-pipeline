# CONTEXT Versions
_Versioning information for the [CONTEXT](CONTEXT.md) tool_
## 0.8 Public
*Mainly issues covered in [#43](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues/43)*

_2017-07-06_ **0.8.3**:
* Available at [trunk/bin directory as executable jarfile](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/CONTEXT-PhylogenomicDatasetBrowser-v0.8.3.jar?raw=true)
    - Closes [#42](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues/42)
    - Sequence translation now done via static access to EnnumeratedTranslator classes' translate() method. EnnumeratedTranslatorOrthodoxCodons.translate() will get most of the most common, but EnnumeratedTranslatorFullCodons.translate() will usually be needed.
    - File I/O tightened up; most alignment imports should work.
    - Fairly stable implementation of alignment statistics plotting.
    - Summary alignment statistics calculated and tabulated.

_2017-06-30_ **0.8.2**:
* Available at [trunk/bin directory as executable jarfile](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/CONTEXT-PhylogenomicDatasetBrowser-v0.8.2.jar?raw=true)
    - add text dump of alignment statistics
    - first implementation of statistics plotting via XChart
 
 - New dependencies:
    - XChart 3.3.1
 
_2015-08-25_ **0.8.1**:
* Available at [trunk/bin directory as executable jarfile](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/trunk/bin/CONTEXT-PhylogenomicDatasetBrowser-v0.8.1.jar?raw=true)
   * Fixes issues [#45](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues/45) [#44](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues/44) [#39](https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/issues/39)
   * Updates to GUI layout and rendering performance for phylogenies.

_2015-08-19_ **0.8 | First public version**:
* Renamed for public release to **CONTEXT** which is backronyms fairly weakly to 'COmparative NucleoTide (and amino-acid) EXplorer Tool'
   * Adds support for dump of alignment stats to tab-delimited textfile
   * Improvements to GUI rendering in DisplayAlignmentPanel to speed it up (a lot). Now handles ~000s alignments, even on a Raspi 2.

## Previous versions

_2011-2015_ Internal version 0.1 - 0.9:
* Known as 'BasicAlignmentStats', 'BasicAlignmentStatsGUI', and 'PhylogenomicDatasetBrowser'
