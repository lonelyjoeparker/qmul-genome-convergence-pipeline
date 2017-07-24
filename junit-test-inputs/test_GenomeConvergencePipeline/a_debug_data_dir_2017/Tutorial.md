# Tutorial.md

This tutorial describes how to run a basic convergence pipeline analysis, and interpret the results.

## Prerequisites

See the ../README.md

## Convergence analysis

Run:

```
java -jar ../GeneralCongruencePipelineModelWag.jar XLOC_000538_Locus_1.cds.fasta phylogenies_only/H0.tre phylogenies_only/H1.tre two_phylogenies.tre   . /usr/local/apps/convergence_pipeline_dependencies/ 100 0 config.mao.txt 
```

## Analysing the output

The main output is a compressed serialised object (.ser). Analyse with:

```
java -jar /usr/local/apps/convergence_pipeline_alphas/ResultsPrinterSimple.jar . 0 3
```