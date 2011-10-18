package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.TreeMap;
import javax.swing.JFileChooser;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlDocument;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.CodemlParameters;

public class CodemlAnalysis extends PamlAnalysis {
	private CodemlDocument ctlFile;
	private File codemlOutput;
	private TreeMap<CodemlParameters, String> typedCodemlParameters;
	private boolean hasRun = false;
	

	public CodemlAnalysis(AlignedSequenceRepresentation[] datasets, File[] treefiles, TreeMap<CodemlParameters,String> parameters, String name){
		this.datasets = datasets;
		this.treefiles = treefiles;
		this.typedCodemlParameters = parameters;
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
		ctlFile = new CodemlDocument();
		codemlOutput = new File(workingDir,name);
		while(!typedCodemlParameters.isEmpty()){
			CodemlParameters param = typedCodemlParameters.lastKey();
			String value = typedCodemlParameters.get(param);
			ctlFile.setParameter(param, value);
			typedCodemlParameters.remove(param);
		}
		
		ctlFile.write(codemlOutput);
		activeCtlFile = ctlFile;
		// TODO this method should only take File args
		// TODO fileChooser to pick output wd
		// FIXME having workingDir as a String makes *no* sense - but what level to abstract the fileselection out to? depends on OO structure, e.g., does PamlAnalysis decide the WD, or caller object?
			if(!activeCtlFile.hasBeenWritten){
	//			activeCtlFile.write(workingDir.getAbsolutePath(), name);
			}
			// TODO how to run a shell script?
			try {
				String exeString = (this.executionBinary.getAbsolutePath() + " " + codemlOutput.getAbsolutePath());
				System.out.println(exeString);
				Process p = Runtime.getRuntime().exec(exeString);
				BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = iReader.readLine();
				int i = 0;
				while(line != null){
					System.out.println(i+" "+line);
					line = iReader.readLine();
					i++;
				}
				System.out.println("done");
				this.hasRun = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
