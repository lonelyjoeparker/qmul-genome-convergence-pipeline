package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.AamlDocument;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.DataSeries;

public class AamlAnalysisSGE extends PamlAnalysis {
	private TreeMap<AamlParameters, String> typedAamlParameters;
	private TreeMap<String, Float>[] patternSSLS;
	private File aamlOutput;
	private AamlDocument ctlFile;
	private boolean hasRun = false;
	private float[] SSLS = null;
	private float[][] SSLSmatrix = null;
	private DataSeries[] SSLSsummary = null;
	
	public AamlAnalysisSGE(AlignedSequenceRepresentation[] datasets, File[] treefiles, TreeMap<AamlParameters,String> parameters){
		this.datasets = datasets;
		this.treefiles = treefiles;
		this.typedAamlParameters = parameters;
		if(binaryDir == null){
			JFileChooser chooser = new JFileChooser();
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				executionBinary = chooser.getSelectedFile();
				binaryDir = chooser.getSelectedFile().getAbsoluteFile();
			}
		}
		if(workingDir == null){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				workingDir = chooser.getSelectedFile();
			}
		}
		AamlDocument ctlFile = new AamlDocument();
		while(!parameters.isEmpty()){
			AamlParameters param = typedAamlParameters.lastKey();
			String value = typedAamlParameters.get(param);
			ctlFile.setParameter(param, value);
			parameters.remove(param);
		}
		ctlFile.write(workingDir.getAbsolutePath(), name);
		activeCtlFile = ctlFile;
		// TODO this method should only take File args
		// TODO fielChooser to pick output wd
		// FIXME having workingDir as a String makes *no* sense - but what level to abstract the fileselection out to? depends on OO structure, e.g., does PamlAnalysis decide the WD, or caller object?
	}

	public AamlAnalysisSGE(AlignedSequenceRepresentation[] datasets, File[] treefiles, TreeMap<AamlParameters,String> parameters, String name){
		this.datasets = datasets;
		this.treefiles = treefiles;
		this.typedAamlParameters = parameters;
		this.name = name;
	}

	public void RunAnalysis(){
		if(binaryDir == null){
			JFileChooser chooser = new JFileChooser();
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				executionBinary = chooser.getSelectedFile();
				binaryDir = chooser.getSelectedFile().getAbsoluteFile(); //FIXME they reference the same thing... 
				System.out.println("Selected exe "+this.executionBinary.getAbsolutePath());
			}
		}
		if(workingDir == null){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setName("Pick a working directory");
			int retval = chooser.showDialog(chooser, "select a working directory");
			if(retval == JFileChooser.APPROVE_OPTION){
				workingDir = chooser.getSelectedFile();
				System.out.println("Selected wd "+this.workingDir.getAbsolutePath());
			}
		}
		ctlFile = new AamlDocument();
		aamlOutput = new File(workingDir,name);
		while(!typedAamlParameters.isEmpty()){
			AamlParameters param = typedAamlParameters.lastKey();
			String value = typedAamlParameters.get(param);
			ctlFile.setParameter(param, value);
			typedAamlParameters.remove(param);
		}
		
		ctlFile.write(aamlOutput);
		activeCtlFile = ctlFile;
		// TODO this method should only take File args
		// TODO fileChooser to pick output wd
		// FIXME having workingDir as a String makes *no* sense - but what level to abstract the fileselection out to? depends on OO structure, e.g., does PamlAnalysis decide the WD, or caller object?
			if(!activeCtlFile.hasBeenWritten){
	//			activeCtlFile.write(workingDir.getAbsolutePath(), name);
			}
			// TODO how to run a shell script?
			String exeString = ("/usr/bin/perl -w runCmd.pl "+this.workingDir.getAbsolutePath()+" "+this.executionBinary.getAbsolutePath() + " " + aamlOutput.getAbsolutePath());
			System.out.println(exeString);
			new VerboseSystemCommand(exeString);
			this.hasRun = true;
//			try {
//				Process p = Runtime.getRuntime().exec(exeString);
//				BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//				String line = iReader.readLine();
//				int i = 0;
//				while(line != null){
//					System.out.println(i+" "+line);
//					line = iReader.readLine();
//					i++;
//				}
//				System.out.println("done");
//				this.hasRun = true;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

	/**
	 * @return the run state of the analysis. NOTE that this only indicates if process has been started and does NOT guarantee execution or output validity.
	 */
	public boolean isHasRun() {
		return hasRun;
	}

	/**
	 * @param hasRun the hasRun to set
	 */
	public void setHasRun(boolean hasRun) {
		this.hasRun = hasRun;
	}
	
	public TreeMap<String, Float> getPatternSSLS(int whichTree){
		this.getAllPatternSSLS();
		return patternSSLS[whichTree];
	}

	public TreeMap<String, Float> getPatternSSLS(){
		this.getAllPatternSSLS();
		return patternSSLS[0];
	}
	
	public TreeMap<String, Float>[] getAllPatternSSLS(){
		if((patternSSLS != null)&& hasRun){
												// Use the one in memory
			return patternSSLS;									
		}else{
												// Instantiate a new one. NB if !hasRun the object returned will be null...
												// Straight away, though, we've got the problem of how many trees... 
			patternSSLS = (TreeMap<String, Float>[]) new TreeMap<?,?>[numberOfTreesets]; //Quick fix for now
			patternSSLS[0] = new TreeMap<String, Float>();	// really this would need to be instantiated each time we're in a new tree, below
			if(hasRun){
//				File lnfFile = new File(System.getProperty("user.dir")+"/lnf");
				File lnfFile = new File(workingDir.getAbsolutePath()+"/lnf");
				System.out.println("trying to read site patterns' lnL from "+lnfFile.getAbsolutePath());
				assert(lnfFile.canRead());
				System.out.println("trying to read site patterns' lnL from "+lnfFile.getAbsolutePath());
				ArrayList<String> SSLSdata = new CapitalisedFileReader().loadSequences(lnfFile, false);
				boolean firstlineRead = false;
				String firstline = "";
				int numPatterns;
				int whichTree = 0;
				for(String line:SSLSdata){
					if(!firstlineRead){
						firstline = line;
						firstlineRead = true;
					}else{
						// assume we're not reading the first line
						// can perform a conditional to work out if we're in a new tree's SSLS
						if(line.length()>0 && line.length()<=5){
							assert (line) != null;
							//Pattern digits = Pattern.compile("[0-9]{1,}");
							//Matcher match = digits.matcher(line);
							//TODO this is a problem
							// atrrrrrgh
							// maybe try a system involving splitting and testing each token
							// .. .slow but will work..
							//whichTree = Integer.parseInt(match.group())-1; 			//this is which tree we're dealing with - REMEMBER that lnf indexes to 1, not 0!
							String[] lineData = line.split(" {1,}");
							if(lineData.length>1){
								whichTree = Integer.parseInt(lineData[1])-1; 			//this is which tree we're dealing with - REMEMBER that lnf indexes to 1, not 0!
							}else{
								assert(true);
								whichTree = Integer.parseInt(line)-1; 			//this is which tree we're dealing with - REMEMBER that lnf indexes to 1, not 0!
							}
							patternSSLS[whichTree] = new TreeMap<String, Float>(); 	//instantiate the patternSSLS
						}
						if(line.length()>5){
							String[] lineData = line.split(" {1,}");
							assert(lineData.length>0);
							patternSSLS[whichTree].put(lineData[6], Float.parseFloat(lineData[3]));	
							/**
							 * TODO
							 * If there is more than one tree present there will be multiple lnL for each site pattern
							 * At the moment patternSSLS.put() will simply overwrite previous patterns' SSLS with the most recent one.
							 * Really patternSSLS should be a TreeMap<String,Float[]> e.g. an array of floats
							 * This is a fairly major refactoring. For instance, how the HELL do you make sure that the correct tree's lnL is loaded to the correct position in the float[]?
							 * Don't forget - patternSSLS is inherited from PamlAnalysis .... 
							 * 
							 * I guess another option would be to have an array of patternSSLS[]  e.g TreeMap<String,Float>[]
							 * That really *is* weird. It would have separate patterns.. but they should always be the same, no?	
							 */
							System.out.println(lineData[6]+","+ Float.parseFloat(lineData[3]));
						}
					}
				}
				String[] firstlineData = firstline.split(" {1,}");
				assert(firstlineData.length>0);
				numPatterns = Integer.parseInt(firstlineData[3]);
				for(String dat:firstlineData){
					System.out.println(dat);
				}
				System.out.println(patternSSLS[0].size()+" "+firstlineData[3]);
				assert(numPatterns == patternSSLS[0].size());
			}
			return patternSSLS;
		}
	}
	
	public void printSitewiseSSLS(AlignedSequenceRepresentation PSR){
		this.getPatternSSLS();
		String[] transposedSites = PSR.getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS[0].get(site);
			System.out.println(site+"\t"+SSLS[position]);
			position++;
		}
	}

	public float[] getSitewiseSSLS(AlignedSequenceRepresentation PSR){
		if(this.SSLS == null){
			this.determineSitewiseSSLS(PSR);
		}
		return this.SSLS;
	}

	public float[] getSitewiseSSLS(){
		if(this.SSLS == null){
			this.determineSitewiseSSLS(this.datasets[0]);
		}
		return this.SSLS;
	}
	
	/**
	 * Simplest determineSitewiseSSLS() method; assumes first / single tree, and default site patterns
	 * Void return type; if you actually <i>want</i> the sitewise SSLS call
	 */
	public void determineSitewiseSSLS(){
		this.getPatternSSLS();
		String[] transposedSites = this.datasets[0].getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS[0].get(site);
			position++;
		}
	}

	/**
	 * @param whichTree - which of the trees in the treefile to use lnL from
	 * 
	 * Simplest determineSitewiseSSLS() method; assumes first / single tree, and default site patterns

	 * <b>NOTE:</b>
	 * This method will set SSLS to the specific tree as passed by whichTree.
	 */
	public void determineSitewiseSSLS(int whichTree){
		this.getPatternSSLS();
		String[] transposedSites = this.datasets[0].getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS[whichTree].get(site);
			position++;
		}
	}

	/**
	 * @param PSR - reference sequence data site patterns are drawn on where different
	 */
	public void determineSitewiseSSLS(AlignedSequenceRepresentation PSR){
		this.getPatternSSLS();
		String[] transposedSites = PSR.getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS[0].get(site);
			position++;
		}
	}
	
	public float[][] getFloatSitewiseLnL(){
		if(SSLSmatrix == null){
			SSLSmatrix = new float[this.datasets[0].getNumberOfSites()][this.numberOfTreesets];
			if(this.SSLS == null){
				this.getAllPatternSSLS();
			}
			String[] transposedSites = this.datasets[0].getTransposedSites();
			int position = 0;
			for(String site:transposedSites){
				for(int i=0;i<this.numberOfTreesets;i++){
					SSLSmatrix[position][i] = patternSSLS[i].get(site);
				}
				position++;
			}
		}
		return SSLSmatrix;
	}
	
	public float[] getFloatSitewiseMeanLnL(){
		if(this.SSLSmatrix == null){
			this.getFloatSitewiseLnL();
		}
		if(this.SSLSsummary == null){
			SSLSsummary = new DataSeries[SSLSmatrix.length];
			for(int i=0;i<SSLSmatrix.length;i++){
				SSLSsummary[i] = new DataSeries(SSLSmatrix[i],"sitew "+i+" lnL");
			}
		}
		float[] meanSSLS = new float[SSLSmatrix.length];
		for(int i=0;i<SSLSmatrix.length;i++){
			meanSSLS[i] = SSLSsummary[i].getMean();
		}
		return meanSSLS;
	}
	
	public float[] getFloatSitewiseLnLVariance(){
		if(this.SSLSmatrix == null){
			this.getFloatSitewiseLnL();
		}
		if(this.SSLSsummary == null){
			SSLSsummary = new DataSeries[SSLSmatrix.length];
			for(int i=0;i<SSLSmatrix.length;i++){
				SSLSsummary[i] = new DataSeries(SSLSmatrix[i],"sitew "+i+" lnL");
			}
		}
		float[] varSSLS = new float[SSLSmatrix.length];
		for(int i=0;i<SSLSmatrix.length;i++){
			varSSLS[i] = SSLSsummary[i].getVariance();
		}
		return varSSLS;
	}

	public float[] getFloatSitewiseLnLSSE(){
		if(this.SSLSmatrix == null){
			this.getFloatSitewiseLnL();
		}
		if(this.SSLSsummary == null){
			SSLSsummary = new DataSeries[SSLSmatrix.length];
			for(int i=0;i<SSLSmatrix.length;i++){
				SSLSsummary[i] = new DataSeries(SSLSmatrix[i],"sitew "+i+" lnL");
			}
		}
		float[] sseSSLS = new float[SSLSmatrix.length];
		for(int i=0;i<SSLSmatrix.length;i++){
			sseSSLS[i] = SSLSsummary[i].getSSE();
		}
		return sseSSLS;
	}

	/**
	 *	@return SSLS - float[] of <i>sitewise</i> SSLS values (first tree) 
	 */
	public float[] getSSLS() {
		if(SSLS == null){
			this.determineSitewiseSSLS();
		}
		return SSLS;
	}
	
	/**
	 * This is a method to return the <i>sitewise</i> SSLS for a particular tree.
	 * <b>NOTE</b> this throws an ArrayIndexOutOfBoundsException.
	 * @param whichTree - the tree to return.
	 * @return float[] of sitewise SSLS
	 * @throws ArrayIndexOutOfBoundsException - if whichTree > nTrees
	 */
	public float[] getSSLS(int whichTree) throws ArrayIndexOutOfBoundsException{
		if(SSLSmatrix == null){
			this.getFloatSitewiseLnL();
		}
		return SSLSmatrix[whichTree];
	}
}
