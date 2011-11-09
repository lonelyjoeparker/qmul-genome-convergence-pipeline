package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.AamlDocument;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;

public class AamlAnalysisSGE extends PamlAnalysis {
	private TreeMap<AamlParameters, String> typedAamlParameters;
	private TreeMap<String, Float> patternSSLS;
	private File aamlOutput;
	private AamlDocument ctlFile;
	private boolean hasRun = false;
	private float[] SSLS;
	
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
	
	public TreeMap<String, Float> getPatternSSLS(){
		if((patternSSLS != null)&& hasRun){
			return patternSSLS;									// Use the one in memory
		}else{
			patternSSLS = new TreeMap<String, Float>();		// Instantiate a new one. NB if !hasRun the object returned will be null...
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
	
	public void printSitewiseSSLS(AlignedSequenceRepresentation PSR){
		this.getPatternSSLS();
		String[] transposedSites = PSR.getTransposedSites();
		assert(transposedSites.length>0);
		SSLS = new float[transposedSites.length];
		int position = 0;
		for(String site:transposedSites){
			SSLS[position] = patternSSLS.get(site);
			System.out.println(site+"\t"+SSLS[position]);
		}
	}

	public float[] getSitewiseSSLS(AlignedSequenceRepresentation PSR){
		if(this.SSLS == null){
			this.determineSitewiseSSLS(PSR);
		}
		return this.SSLS;
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
}
