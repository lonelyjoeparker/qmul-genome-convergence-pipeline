/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 * <p>The following model types are recognised by codeml:
 * <pre>
 * 	MODEL_TYPE_0 // default, site-wise models
 *	MODEL_TYPE_1 // free-ratios (one rate per branch, rarely used)
 *	MODEL_TYPE_2 // branch-site models, fg branch speficied.
 *	MODEL_TYPE_3 // Clade (lineage) models, e.g. C & D
 * </pre>
 */
public enum CodemlModelType {
	MODEL_TYPE_0_DEFAULT,
	MODEL_TYPE_1_FREERATIOS,
	MODEL_TYPE_2_BRANCHSITE,
	MODEL_TYPE_3_CLADE
}
