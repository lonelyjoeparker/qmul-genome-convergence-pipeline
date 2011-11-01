package uk.ac.qmul.sbcs.evolution.convergence.handlers;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JFileChooser;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlDocument;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.CodemlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;

/**
 * 
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker</a>
 * @since 29/10/2011
 */
public class RAxMLAnalysis {
	private String identifier;
	private File dataFile;
	private File treeConstraintFile;
	private boolean hasRun;
	private File executionBinary;
	private File binaryDir;
	private File workingDir;
	private TreeMap<String, String> parameters;
	private AAmodelOptions AAmodel;
	private NTmodelOptions NTmodel;
	private algorithmOptions algorithm;

	public enum AAmodelOptions{
		DAYHOFF, DCMUT, JTT, MTREV, WAG, RTREV, CPREV, VT, BLOSUM62, MTMAM, GTR,
		DAYHOFFF, DCMUTF, JTTF, MTREVF, WAGF, RTREVF, CPREVF, VTF, BLOSUM62F, MTMAMF, GTRF,

		PROTCATDAYHOFF, PROTCATDCMUT, PROTCATJTT, PROTCATMTREV, PROTCATWAG, PROTCATRTREV, PROTCATCPREV, PROTCATVT, PROTCATBLOSUM62, PROTCATMTMAM, PROTCATGTR,
		PROTCATDAYHOFFF, PROTCATDCMUTF, PROTCATJTTF, PROTCATMTREVF, PROTCATWAGF, PROTCATRTREVF, PROTCATCPREVF, PROTCATVTF, PROTCATBLOSUM62F, PROTCATMTMAMF, PROTCATGTRF,

		PROTMIXDAYHOFF, PROTMIXDCMUT, PROTMIXJTT, PROTMIXMTREV, PROTMIXWAG, PROTMIXRTREV, PROTMIXCPREV, PROTMIXVT, PROTMIXBLOSUM62, PROTMIXMTMAM, PROTMIXGTR,
		PROTMIXDAYHOFFF, PROTMIXDCMUTF, PROTMIXJTTF, PROTMIXMTREVF, PROTMIXWAGF, PROTMIXRTREVF, PROTMIXCPREVF, PROTMIXVTF, PROTMIXBLOSUM62F, PROTMIXMTMAMF, PROTMIXGTRF,

		PROTGAMMADAYHOFF, PROTGAMMADCMUT, PROTGAMMAJTT, PROTGAMMAMTREV, PROTGAMMAWAG, PROTGAMMARTREV, PROTGAMMACPREV, PROTGAMMAVT, PROTGAMMABLOSUM62, PROTGAMMAMTMAM, PROTGAMMAGTR,
		PROTGAMMADAYHOFFF, PROTGAMMADCMUTF, PROTGAMMAJTTF, PROTGAMMAMTREVF, PROTGAMMAWAGF, PROTGAMMARTREVF, PROTGAMMACPREVF, PROTGAMMAVTF, PROTGAMMABLOSUM62F, PROTGAMMAMTMAMF, PROTGAMMAGTRF
	}    
	public enum NTmodelOptions{GTRMIX, GTRCAT, GTRGAMMA}		
	public enum algorithmOptions {o, e, b, c, s}

	public RAxMLAnalysis(File input, File constraint, String id){
		this.dataFile = input;
		this.treeConstraintFile = constraint;
		this.identifier = id;
		this.hasRun = false;
		this.executionBinary = null;
		this.binaryDir = null;
		this.workingDir = null;
		this.parameters = new TreeMap<String,String>();
		// TODO set up other default parameters at this point?
		// TODO set up the -s, -n and -g parameters at this point? and/or maybe overload the constructor for 
		//	    instances where we want to infer phylogeny?
	}

	/**
	 * 
	 * @param input - the dataFile for the sequence data itself
	 * @param workingDir - the directory to write output files to
	 * @param constraint - the tree to constrain the topology to
	 * @param id  - a run ID for this job
	 * @param AAmodel
	 * @param algorithm
	 * 
	 * This constructor for AA analyses
	 */
	public RAxMLAnalysis(File input, File workingDir, File constraint, String id, AAmodelOptions AAmodel, algorithmOptions algorithm){
		this.dataFile = input;
		this.treeConstraintFile = constraint;
		this.identifier = id;
		this.hasRun = false;
		this.executionBinary = null;
		this.binaryDir = null;
		this.workingDir = workingDir;
		this.AAmodel = AAmodel;
		this.algorithm = algorithm;
		this.parameters = new TreeMap<String,String>();
		// TODO set up other default parameters at this point?
		// TODO set up the -s, -n and -g parameters at this point? and/or maybe overload the constructor for 
		//	    instances where we want to infer phylogeny?
	}

	/**
	 * 
	 * @param input - the dataFile for the sequence data itself
	 * @param workingDir - the directory to write output files to
	 * @param constraint - the tree to constrain the topology to
	 * @param id  - a run ID for this job
	 * @param NTmodel
	 * @param algorithm
	 * 
	 * This constructor for NUCLEOTIDE analyses
	 */
	public RAxMLAnalysis(File input, File workingDir, File constraint, String id, NTmodelOptions NTmodel, algorithmOptions algorithm){
		this.dataFile = input;
		this.treeConstraintFile = constraint;
		this.identifier = id;
		this.hasRun = false;
		this.executionBinary = null;
		this.binaryDir = new File("/Applications/Phylogenetics/RAxML/RAxML-7.2.8-ALPHA/raxmlHPC");
		this.workingDir = workingDir;
		this.NTmodel = NTmodel;
		this.algorithm = algorithm;
		this.parameters = new TreeMap<String,String>();
		// TODO set up other default parameters at this point?
		// TODO set up the -s, -n and -g parameters at this point? and/or maybe overload the constructor for 
		//	    instances where we want to infer phylogeny?
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
		// TODO how to run a shell script?
		new VerboseSystemCommand(this.getExeString());
	}


	// FIXME this is a bit of a mess
	public void setParamModel(AAmodelOptions model){
		if(parameters.containsKey("m")){
			parameters.remove("m");
		}
		parameters.put("m", model.toString());
	}

	// FIXME this is also a bit of a mess
	private String getExeString(){
//		StringBuilder sb = new StringBuilder();
//		sb.append(this.executionBinary.getAbsolutePath()+" ");
//		Iterator itr = parameters.values().iterator();
//		while(itr.hasNext()){
//			String key = (String)itr.next();
//			sb.append("-"+key+" "+parameters.get(key)+" ");
//		}
		//return sb.toString();
		//return "/Applications/Phylogenetics/RAxML/RAxML-7.2.8-ALPHA/raxmlHPC -m PROTCATJTT -n hpc-constrained-test -s /pamlTest/stewart.aa.alternative.phy -r /pamlTest/stewart.constraint.tre";
		return binaryDir.getAbsolutePath() + " -m " + NTmodel + " -n "+ identifier + " -s "+ dataFile.getAbsolutePath() + " -r " + treeConstraintFile.getAbsolutePath() + " -w " + workingDir.getAbsolutePath();
		// TODO build execution string from parameters and binary location using stringbuilder
		// TODO test
	}
}
