package uk.ac.qmul.sbcs.evolution.convergence.analyses;

/**
 * 
 * @author Joe Parker
 * @since 09/11/2011
 * @version 0.0.1
 * 
 * An analysis to determine the power of the SSLS/Paml-based congruence test.
 * 
 * <b>Basic execution:</b>
 * <ul>
 * 	<li>Input 22 taxon alignment, get parameters from a single PAML (perhaps this should happen at runner level)</li>
 * 	<li>Simulate sites</li>
 * 	<li>Paml on simulated sites</li>
 * 	<li>Converge sites</li>
 * 	<li>Paml on converged simulated sites</li>
 * 	<li>Summarise and get percentiles</li>
 * </ul>
 */
public class CongruenceTestPowerSimulation {

}
