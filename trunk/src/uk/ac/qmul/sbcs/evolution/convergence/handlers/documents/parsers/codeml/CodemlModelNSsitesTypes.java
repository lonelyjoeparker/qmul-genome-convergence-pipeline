package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 * <p>The following NSsites types are recognised by codeml:
 * <pre>
 *	NSsites_TYPE_0_W			// 0<=w<999
 *	NSsites_TYPE_1_NEUTRAL		// w<1, w==1
 *	NSsites_TYPE_2_SELECTION	// w<1, w==1, w>1
 *	NSsites_TYPE_3_DISCRETE		// three w ratios, each as as NSsites=0
 *	NSsites_TYPE_4_FREQS		//
 *	NSsites_TYPE_5_GAMMA		//
 *	NSsites_TYPE_6_2GAMMA		//
 *	NSsites_TYPE_7_BETA			// beta-distributed w with B(p,q)
 *	NSsites_TYPE_8_BETAW		// beta-distributed w plus separate w > 1
 * </pre>
 */
public enum CodemlModelNSsitesTypes {
	NSsites_TYPE_0_W,
	NSsites_TYPE_1_NEUTRAL,
	NSsites_TYPE_2_SELECTION,
	NSsites_TYPE_3_DISCRETE,
	NSsites_TYPE_4_FREQS,
	NSsites_TYPE_5_GAMMA,
	NSsites_TYPE_6_2GAMMA,
	NSsites_TYPE_7_BETA,
	NSsites_TYPE_8_BETAW
}
