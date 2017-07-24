# Dependencies

This file contains third-party dependencies required by this package (This also folder contains the 'runCmd.pl' perl wrapper script for these)

**See below for citation information**

| Package | Version (see Note 1) | Source | Notes | Required? |
| ------- | ----------- | ------ | ----- | --------- |
| Newick Utils | 1.6 (probably) | http://cegg.unige.ch/newick_utils | `nw_prune`; for pruning tips with missing data | :heavy_check_mark: |
| Newick Utils | 1.6 (probably) | http://cegg.unige.ch/newick_utils | `nw_display`; for some debugging |  |
| RAxML | 7.2.6 | [Specific 7.2.6 version](https://sco.h-its.org/exelixis/php/countSource726.php); [main site](https://sco.h-its.org/exelixis/web/software/raxml/index.html) | `raxmlHPC`, `raxmlHPC-PTHREADS`; for fitting branch lengths to data for specified topologies and de novo inference in some analyses  | :heavy_check_mark: |
| (Perl) | (5.8+) | Almost certainly installed on your machine, but see http://www.perl.org | (For running the `runCmd.pl` wrapper script in this directory | :heavy_check_mark: |
| PAML | (see Note 2, below) | http://abacus.gene.ucl.ac.uk/software/pamlOld.html | `codeml`, `baseml`, `./dat <folder of substitution model matrices)`; for inferring substitution model parameters; fitting  | :heavy_check_mark: |
| PAML | (see Note 2) | http://abacus.gene.ucl.ac.uk/software/pamlOld.html | `evolver`; for some simulations |  |
| PhyloBayes | 3.3f | (see Note 3, below) | `pb`, `ppred`  for simulation of amino acids under neutrality  | :heavy_check_mark: |


## Notes ##

### 0. Input data assumptions and formats ###

Input data assumptions and formats are not covered here but in brief, the input data should consist of multiple (hundreds or thousands) of loci, presumably orthologous coding sequences, aligned (we used TransAlign, MAFFT, `muscle` and GBlocks by but see also [Blackburne & Whelan (2013)](http://dx.doi.org/10.1093/mss256) for a really important paper comparing aligners and their effects on downstream phylogenomics (e.g., this stuff).

Data will be converted to the `.phy` (PAML) format by the pipeline but should usually start out in either .fasta or .phy if possible to avoid weird errors. Becuase the PAML format only supports 10-character sequence names (PAMLv4.4), seqeuence names longer than this will be truncated in the pipeline, with a three-character numeric unique identifier (UID) assigned incrementally to each sequence (indexed from '001', e.g. `Bat_Sp_001`, `Duck_Sp002` etc). A maximum of 999 UIDs are possible with this system as a result, so the pipeline will throw an error for any alignment with >999 taxa.

The input requirements for phylogenies are more complex so will be dealt with elsewhere.

### 1. Versions ###
This pipeline was developed from 2011 onwards. Versions given are the versions used in testing/analysis/publication. We have not tested these dependencies with any other versions, so if you use earlier or later releases at your discretion, one of three things might happen:

 a) Everything works perfectly
 b) Everything fails completely
 c) Things _appear_ to have worked, but in fact contain silent or hard-to-spot errors.
 
It's up to you...

### 2. PAML ###
The published pipeline uses a modified version of PAML4.4b which has some tweaks to the output formatting (precision and certain markup additions to aid parsing). We aren't able to distribute this at the moment as Ziheng holds the copyright to the source (rightly!!!) but for limited academic use please [email to discuss](mailto:joe.parker@kew.org)

### 3. PhyloBayes ###
Current PhyloBayes version is here http://megasun.bch.umontreal.ca/People/lartillot/www/download.html, you may need to contact the package authors for the 3.3f version required by this package as the current version is at 4.1.

### 4. Citations ###
 1. Stamatakis, A. (2006) RAxML-VI-HPC: maximum likelihood-based phylogenetic analyses with thousands of taxa and mixed models. _Bioinformatics_ **22**:2688-2690 https://www.ncbi.nlm.nih.gov/pubmed/16928733
 2. Yang, Z. (2007) PAML 4: phylogenetic analysis by maximum likelihood. _Mol. Biol. Evol._ **24**:1586-1591 https://www.ncbi.nlm.nih.gov/pubmed/17483113
 3. Junier, T. & Zdobnov, E. M. (2010) The Newick utilities: high-throughput phylogenetic tree processing in the UNIX shell. _Bioinformatics_ **26**:1669-1670 https://www.ncbi.nlm.nih.gov/pubmed/20472542
 4. Lartillot, N., Lepage, T. & Blanquart, S. (2009) PhyloBayes 3: a Bayesian software package for phylogenetic reconstruction and molecular dating. _Bioinformatics_ **25**(17):2286-8 https://www.ncbi.nlm.nih.gov/pubmed/19535536
 5. Yang, Z., Nielsen, R., Goldman, N. & Pedersen, A. M. (2000) Codon-substitution models for heterogeneous selection pressure at amino acid sites. _Genetics_ **155**:431-449 https://www.ncbi.nlm.nih.gov/pubmed/10790415
