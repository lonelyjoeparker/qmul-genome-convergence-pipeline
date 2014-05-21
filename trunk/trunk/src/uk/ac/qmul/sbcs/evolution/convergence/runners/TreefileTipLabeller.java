package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.ArrayList;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.CapitalisedFileReader;

public class TreefileTipLabeller {

	private static String usage = "TreefileTipLabeller\nUsage:\n\tjava -jar TreefileTipLabeller.jar <treefile> (labels all tips #1)\n\tjava -jar TreefileTipLabeller.jar <treefile> <tip_1> <tip_2>... (labels tips given in tip_1 tip2 etc)\n";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		switch(args.length){
			case 0:{
				System.err.println(usage);
				break;
			}
			case 1:{
				File tree = new File(args[0]);
				ArrayList<String> contents = new CapitalisedFileReader().loadSequences(tree);
				// Set up buffer
				StringBuffer b = new StringBuffer();
				TreeNode t = new TreeNode(contents.get(0),1); //should read the first tree in the file, if there's more than one.. oo-err
				String[] tips = t.getTipsBelow();
				for(int i=0;i<tips.length;i++){
					String[] taxon = {tips[i].toUpperCase()};
					String labelled = t.printRecursivelyLabelling(taxon)+";\n";
					b.append(labelled);
					System.out.print(labelled);
				}
				File output = new File(tree.getAbsolutePath()+"labelled.tre");
				new BasicFileWriter(output, b.toString());
				break;
			}
			default:{
				File tree = new File(args[0]);
				ArrayList<String> contents = new CapitalisedFileReader().loadSequences(tree);
				// Set up buffer
				StringBuffer b = new StringBuffer();
				TreeNode t = new TreeNode(contents.get(0),1); //should read the first tree in the file, if there's more than one.. oo-err
				for(int i=1;i<args.length;i++){
					String[] taxon = {args[i].toUpperCase()};
					String labelled = t.printRecursivelyLabelling(taxon)+";\n";
					b.append(labelled);
					System.out.print(labelled);
				}
				File output = new File(tree.getAbsolutePath()+"labelled.tre");
				new BasicFileWriter(output, b.toString());
				break;
			}
		}
	}

}
