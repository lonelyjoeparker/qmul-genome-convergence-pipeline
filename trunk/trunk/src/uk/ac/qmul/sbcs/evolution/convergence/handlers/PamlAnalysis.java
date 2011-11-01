package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;

public abstract class PamlAnalysis {
	private float[] sitewiseLikelihoods;
	private int numberOfDatasets;
	private int numberOfTreesets;
	private int numberOfSitePatterns;
	protected File executionBinary;
	protected File binaryDir;
	protected File workingDir;
	protected PamlDocument activeCtlFile;
	protected String name = "default.ctl";
	protected TreeMap<String,String> parameters;
	protected AlignedSequenceRepresentation[] datasets;
	protected File[] treefiles;
	private TreeMap<String, Float> patternSSLS;
	private float[] SSLS;
	private boolean hasRun = false;
	
	public PamlAnalysis(){}
	
	public PamlAnalysis(AlignedSequenceRepresentation[] datasets, File[] treefiles, TreeMap<String,String> parameters, File workingDir, String name){
		this.datasets = datasets;
		this.treefiles = treefiles;
		this.workingDir = workingDir;
		this.name = name;
		if(binaryDir == null){
			JFileChooser chooser = new JFileChooser();
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				executionBinary = chooser.getSelectedFile();
				binaryDir = chooser.getSelectedFile().getAbsoluteFile();
			}
		}
	}
	
	public PamlAnalysis(AlignedSequenceRepresentation[] datasets, File[] treefiles, TreeMap<String,String> parameters){
		this.datasets = datasets;
		this.treefiles = treefiles;
		this.parameters = parameters;
		if(binaryDir == null){
			JFileChooser chooser = new JFileChooser();
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				executionBinary = chooser.getSelectedFile();
				binaryDir = chooser.getSelectedFile();
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
	}

	public void Initialize(){
		PamlDocument ctlFile = new PamlDocument();
		while(!parameters.isEmpty()){
			String param = parameters.lastKey();
			String value = parameters.get(param);
			ctlFile.setParameter(param, value);
			parameters.remove(param);
		}
		ctlFile.write(new File(workingDir.getAbsoluteFile(), name));
		activeCtlFile = ctlFile;
		// TODO this method should only take File args
		// TODO fielChooser to pick output wd
		// FIXME having workingDir as a String makes *no* sense - but what level to abstract the fileselection out to? depends on OO structure, e.g., does PamlAnalysis decide the WD, or caller object?
	}

	public void RunAnalysis(){
		if(!activeCtlFile.hasBeenWritten){
//			activeCtlFile.write(workingDir.getAbsolutePath(), name);
		}
		// TODO how to run a shell script?
		try {
			String exeString = (this.executionBinary.getAbsolutePath() + " ");
			System.out.println(exeString);
			Process p = Runtime.getRuntime().exec(exeString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the executionBinary
	 */
	public File getExecutionBinary() {
		return executionBinary;
	}

	/**
	 * @param executionBinary the executionBinary to set
	 */
	public void setExecutionBinary(File executionBinary) {
		this.executionBinary = executionBinary;
	}

	/**
	 * @return the binaryDir
	 */
	public File getBinaryDir() {
		return binaryDir;
	}

	/**
	 * @param binaryDir the binaryDir to set
	 */
	public void setBinaryDir(File binaryDir) {
		this.binaryDir = binaryDir;
	}
	
	public void determineSitewiseSSLS(AlignedSequenceRepresentation PSR){
		this.getPatternSSLS();
		String[] transposedSites = PSR.getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS.get(site);
		}
	}

	public TreeMap<String, Float> getPatternSSLS(){
		if((patternSSLS != null)&& hasRun){
			return patternSSLS;									// Use the one in memory
		}else{
			patternSSLS = new TreeMap<String, Float>();		// Instantiate a new one. NB if !hasRun the object returned will be null...
			if(hasRun){
				File lnfFile = new File(System.getProperty("user.dir")+"/lnf");
				assert(lnfFile.canRead());
				ArrayList<String> SSLSdata = new BasicFileReader().loadSequences(lnfFile, false);
				boolean firstlineRead = false;
				String firstline = "";
				int numPatterns;
				for(String line:SSLSdata){
					if(!firstlineRead){
						firstline = line;
						firstlineRead = true;
					}else{
						if(line.length()>5){
							String[] lineData = line.split(" {1,}");
							assert(lineData.length>0);
							patternSSLS.put(lineData[6], Float.parseFloat(lineData[3]));
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
				System.out.println(patternSSLS.size()+" "+firstlineData[3]);
				assert(numPatterns == patternSSLS.size());
			}
			return patternSSLS;
		}
	}

	public float[] getSitewiseSSLS(AlignedSequenceRepresentation PSR){
		if(this.SSLS == null){
			this.determineSitewiseSSLS(PSR);
		}
		return this.SSLS;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
		System.out.println("working dir set to "+this.workingDir.getAbsolutePath());
	}
}
