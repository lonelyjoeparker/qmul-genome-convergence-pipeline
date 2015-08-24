package uk.ac.qmul.sbcs.evolution.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import uk.ac.qmul.sbcs.evolution.convergence.util.SerfileFilter;
import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.util.SitewiseSpecificLikelihoodSupportAaml;

public class ResultsBrowserSimple extends JComponent{

	static String arg1;
	File dir;
	public ResultsBrowserSimple(String arg12) {
		// TODO Auto-generated constructor stub
		this.arg1 = arg12;
	}

	public ResultsBrowserSimple() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ResultsBrowserSimple().go();
	}
	
	private void go(){
		FilenameFilter serFileFilter = new SerfileFilter();
		if(dir != null){
			dir = new File(arg1);
		}
		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			dir = fc.getSelectedFile();
		}
		
		if(dir.isDirectory()){
			String[] serFilesList = dir.list(serFileFilter);
			SitewiseSpecificLikelihoodSupportAaml[] results = new SitewiseSpecificLikelihoodSupportAaml[serFilesList.length];
			for(int i=0; i<results.length;i++){
				try {
					FileInputStream fileInOne = new FileInputStream(dir.getAbsolutePath()+"/"+serFilesList[i]);
					ObjectInputStream inOne = new ObjectInputStream(fileInOne);
					results[i] = (SitewiseSpecificLikelihoodSupportAaml) inOne.readObject();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			for(SitewiseSpecificLikelihoodSupportAaml someRun:results){
				try {
					System.out.println("Fetching results for file "+someRun.getInputFile().getPath()+" (chi-sq homo.: "+someRun.getHomogeneityChiSq()+")");
					System.out.println("Fitted topologies: ");
					float[] alphas = someRun.getAlpha();
					float[] pSH = someRun.getpSH();
					float[] sli = someRun.getLi();
					float[] lengths = someRun.getTreeLengths();
					String[] fittedTrees = someRun.getFittedTrees();
					System.out.println("\ttree\talpha\tpSH\tlnL\tlengths\ttopology");
					for(int k=0;k<someRun.getNumberOfTopologies();k++){
						System.out.println("\t"+k+"\t"+alphas[k]+"\t"+pSH[k]+"\t"+sli[k]+"\t"+lengths[k]+"\t"+fittedTrees[k]);
					}
					AlignedSequenceRepresentation asr = someRun.getDataset();
					asr.printShortSequences(30);
					float[][] SSLS = someRun.getSSLSseriesSitewise();
					for(int j=0;j<someRun.getNumberOfSites();j++){
						System.out.print(someRun.getSites()[j]);
						for(int k=1;k<someRun.getNumberOfTopologies();k++){
							float dSSLS = SSLS[j][0] - SSLS[j][k];
							System.out.print("\t"+dSSLS);
						}
						System.out.println();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("arg must be a directory");
		}
	}
}
