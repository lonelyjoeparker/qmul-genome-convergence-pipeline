_Dataset requirements for the convergence pipeline / API_

# Trees # 
There should be two or more treefiles, which must have unambiguous taxon names which map exactly to the maximal set of sequences present in the alignments.

These should be in strict Newick format, e.g. one tree per line from the first line on, with no additional markup (tree names, internal node names or branch rate parameters etc).

One file will contain one single tree - the species tree, or null (_H0_) hypothesis. The second file will contain the alternative hypotheses (gene tree, convergent hypotheses etc) and may have more than one tree in - each tree present will be tested separately. These are referred to using _Hn_ notation, e.g {H1, H2, H3... Hn}.

A further file may contain additional, simulated trees. 

# Sequence data #

One or more multiple sequence alignments will be fitted to the null (_H0_) and alternative (_H1... Hn_) hypotheses. Optionally simulated datasets can be analysed as well.

 - Data are assumed to be nucleotides, and in parts of the pipeline these will be translated on the assumption that they are in-frame, forward-sense coding loci.
 - A gapping parameter _g_ can be specified, which will exclude any sites in the alignment which have either:

    - _g_ or more gaps (counts threshold, assumed if _g_ > 1), or
    - greater than _g_ proportion of gaps, assumed if 0 =< _g_ =< 1.
 
 - Stop codons will be removed, but not flagged (e.g. a locus with stop codons present will still be analysed, even though a stop codon in the middle of the alignment should probably be taken as a worrying sign that the remainder of the sequence is intronic..)
 - Taxon names will be truncated to 10 characters and a UID appended. This UID can accept up to 999 taxa before overflow; so the maximum alignment size for this pipeline is 999 taxa.
 - File formats should be phylip/PAML, or fasta. Nexus support is partial so weird variants (MacClade; BEAST) may cause errors, including silent errors. Other formats (XML etc) not implemented at present.
 - Line endings should be linux or mac, not windows.
 - Whitespace characters especially should be checked..
