package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import uk.ac.qmul.sbcs.evolution.convergence.*;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;

public abstract class PamlAnalysis {
	private float[] sitewiseLikelihoods;
	private int numberOfDatasets;
	private int numberOfTreesets;
	private int numberOfSitePatterns;
	private File executionBinary;
	private String binaryLocation;
	private String workingDir;
	private PamlDocument activeCtlFile;
	
	public PamlAnalysis(){}
	
	public void Initialize(PhymlSequenceRepresentation[] datasets, String[] treefilePathnames, TreeMap<String,String> parameters, String workingDir){
		this.workingDir = workingDir;
		if(binaryLocation == null){
			JFileChooser chooser = new JFileChooser();
			int retval = chooser.showDialog(chooser, "where is the execution binary for this analysis?");
			if(retval == JFileChooser.APPROVE_OPTION){
				executionBinary = chooser.getSelectedFile();
				binaryLocation = chooser.getSelectedFile().getAbsolutePath();
			}
		}
		PamlDocument ctlFile = new PamlDocument();
		while(!parameters.isEmpty()){
			String param = parameters.lastKey();
			String value = parameters.get(param);
			ctlFile.setParameter(param, value);
			parameters.remove(param);
		}
		ctlFile.write(workingDir);
		activeCtlFile = ctlFile;
	}
	
	public void RunAnalysis(){
		if(!activeCtlFile.hasBeenWritten){
			activeCtlFile.write(workingDir);
		}
	}
	
	
}
