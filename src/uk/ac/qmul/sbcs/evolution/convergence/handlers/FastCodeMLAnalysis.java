package uk.ac.qmul.sbcs.evolution.convergence.handlers;

/**
 * A handler (non-JNI) for the FastCodeML program (Valle et al, 2014, Bioinformatics <a href="http://dx.doi.org/doi:10.1093/bioinformatics/btt760">doi:10.1093/bioinformatics/btt760</a>.
 * <p>Functions include:
 * <ul><li>Detection of positive selection
 * <li>Branch models
 * <li>Site models
 * <li>Branch-site models
 * </ul>
 * Parameters are stored in memory at runtime, and passed to the fastCodeML binary using the command-line. They are stored internally as enumerated types (enums)
 * <p>The class will set up an analysis when instantiated, and attempt to execute and parse it when run() is called. reparse() method attempts to repeat the parsing if the fastCodeML process hasn't finished. The output from the fastCodeML process is all to stout by default, so if stout is missed, there's no chance of getting it back.
 * <p>Alternatively, a runViaFile() method runs the same analysis, but directs the output from fastCodeML to a text file. This is safer in that reparseFromFile() can be called, including an arbitrarily long time after runViaFile(), but messier and increases the disk footprint of course...
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class FastCodeMLAnalysis {
	
	public void FastCodeMLAnalysis(){}
	
	public void run(){}
	
	public void runViaFile(){}
	
	public void reparse(){}
	
	public void reparseFromFile(){}
}
