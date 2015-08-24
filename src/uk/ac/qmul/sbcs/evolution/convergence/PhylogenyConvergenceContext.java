package uk.ac.qmul.sbcs.evolution.convergence;

/**
 * 
 * Enumerated type defining the allowable phylogeny convergence contexts. 
 * <br/>These set the role a phylogeny plays in the convergence pipeline:
 * <p><ul>
 * <li>NULL_CONVERGENCE_CONTEXT_NOT_SET, no convergence type set</li>
 * <li>REFERENCE_SPECIES_NULL_PHYLOGENY, the species / reference / null phylogeny</li>
 * <li>TEST_ALTERNATIVE_PHYLOGENY, one or more alternative / test phylogenies</li>
 * <li>RANDOM_CONTROL_PHYLOGENY, a phylogeny generated from a random distribution, used in calculation of the Uc statistic</li>
 * <li>RAXML_RESOLVED_PARTIALLY_CONSTRAINED_PHYLOGENY, partially resolved (polytomy) topologies used in RAxML search</li>
 * <li>RAXML_FULLY_RESOLVED_UNCONSTRAINED_PHYLOGENY, phylogeny fully resolved by unconstrainted / de novo RAxML search</li>
 * <li>NO_CONVERGENCE_CONTEXT_CODEML_NODE_LABELLING, phylogeny with no role in the convergence support calculation, but with internal nodes labelled for codeml to calculate dN/dS on subtrees/branches</li>
 * </ul>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 */
public enum PhylogenyConvergenceContext {
	NULL_CONVERGENCE_CONTEXT_NOT_SET,
	REFERENCE_SPECIES_NULL_PHYLOGENY,
	TEST_ALTERNATIVE_PHYLOGENY,
	RANDOM_CONTROL_PHYLOGENY,
	RAXML_RESOLVED_PARTIALLY_CONSTRAINED_PHYLOGENY,
	RAXML_FULLY_RESOLVED_UNCONSTRAINED_PHYLOGENY, 
	NO_CONVERGENCE_CONTEXT_CODEML_NODE_LABELLING
}
