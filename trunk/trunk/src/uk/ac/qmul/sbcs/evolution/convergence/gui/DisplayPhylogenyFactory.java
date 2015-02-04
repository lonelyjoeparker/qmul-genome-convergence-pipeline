/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.io.File;
import java.util.ArrayList;

import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.TreeNode;

/**
 * This is a factory object: the the factory methods are called in static context to create an array of {@link DisplayPhylogeny}
 *  objects from {@link DisplayPhylogenyFactory#fromFile()}, {@link DisplayPhylogenyFactory#fromTreeNode()}, 
 *  {@link DisplayPhylogenyFactory#fromNewickTreeRepresentation()}, and {@link DisplayPhylogenyFactory#fromString()} inputs.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class DisplayPhylogenyFactory {

	/**
	 * Static factory method: Create and return an array of {@link DisplayPhylogeny} objects from input {@link File}.
	 * @param input {@link File} - object referencing file containing Newick-formatted tree strings.
	 * @return {@link DisplayPhylogeny}[]
	 * @see DisplayPhylogeny
	 */
	public static DisplayPhylogeny[] fromFile(File input){
		/*
		 * The File may have a multi-phylogeny content (trees on separate lines)
		 * So create a new NewickTreeRepresentation first.
		 * Then get #trees and build / populate on that basis.
		 * Then set the File variable in each DisplayPhylogeny so created.
		 * 
		 * NB using ArrayList and try/catch to try and ensure no null members of return array.
		 */
		ArrayList<DisplayPhylogeny> productsTempList = new ArrayList<DisplayPhylogeny>();
		NewickTreeRepresentation tempTree = new NewickTreeRepresentation(input);
		for(int tree = 0; tree<tempTree.getNumberOfTrees();tree++){
			try {
				String specificTree = tempTree.getSpecificTree(tree);
				DisplayPhylogeny tempPhylogeny = new DisplayPhylogeny(specificTree, input);
				productsTempList.add(tempPhylogeny);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/* Cast the ArrayList to a DisplayPhylogeny[] and return, unless it is empty. */
		if(!productsTempList.isEmpty()){
			DisplayPhylogeny[] products = productsTempList.toArray(new DisplayPhylogeny[productsTempList.size()]);
			return products;
		}else{
			// no DisplayPhylogenies were created
			return null;
		}
	}
	
	/**
	 * Static factory method: Create and return an array of {@link DisplayPhylogeny} objects from input {@link TreeNode}.
	 * @param input {@link TreeNode} - object containing phylogeny data representation as recursively nested nodes.
	 * @return {@link DisplayPhylogeny}[]
	 * @see DisplayPhylogeny
	 * @see TreeNode
	 */
	public static DisplayPhylogeny[] fromTreeNode(TreeNode input){
		/*
		 *  We expect exactly one tree in each TreeNode object; 
		 *  so create a new DisplayPhylogeny straight from TreeNode on that basis.
		 */
		DisplayPhylogeny[] products = {new DisplayPhylogeny(input)};
		return products;
	}
	
	/**
	 * Static factory method: Create and return an array of {@link DisplayPhylogeny} objects from input {@link NewickTreeRepresentation}.
	 * @param input {@link NewickTreeRepresentation} - object containing data representation of Newick tree file, and/or tree string, and/or TreeNode.
	 * @return {@link DisplayPhylogeny}[]
	 * @see DisplayPhylogeny
	 * @see NewickTreeRepresentation
	 */
	public static DisplayPhylogeny[] fromNewickTreeRepresentation(NewickTreeRepresentation input){
		/*
		 * The NewickTreeRepresentation format may have a multi-phylogeny content (trees on separate lines)
		 * So get #trees and build / populate on that basis.
		 * 
		 * NB using ArrayList and try/catch to try and ensure no null members of return array.
		 */
		ArrayList<DisplayPhylogeny> productsTempList = new ArrayList<DisplayPhylogeny>();
		for(int tree = 0; tree<input.getNumberOfTrees();tree++){
			try {
				String specificTree = input.getSpecificTree(tree);
				DisplayPhylogeny tempPhylogeny = new DisplayPhylogeny(specificTree);
				productsTempList.add(tempPhylogeny);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/* Cast the ArrayList to a DisplayPhylogeny[] and return, unless it is empty. */
		if(!productsTempList.isEmpty()){
			DisplayPhylogeny[] products = productsTempList.toArray(new DisplayPhylogeny[productsTempList.size()]);
			return products;
		}else{
			// no DisplayPhylogenies were created
			return null;
		}
	}
	
	/**
	 * Static factory method: Create and return an array of {@link DisplayPhylogeny} objects from input {@link String}.
	 * @param input {@link String} - string containing Newick-format phylogeny.
	 * @return {@link DisplayPhylogeny}[]
	 * @see DisplayPhylogeny
	 */
	public static DisplayPhylogeny[] fromString(String input){
		/*
		 * The String format may have a multi-phylogeny content (trees on separate lines)
		 * So get #trees and build / populate on that basis.
		 * 
		 * Attempt to separate the string based on ";" chars.
		 * 
		 * NB using ArrayList and try/catch to try and ensure no null members of return array.
		 */
		ArrayList<DisplayPhylogeny> productsTempList = new ArrayList<DisplayPhylogeny>();
		String[] inputSplit = input.split(";");
		if(inputSplit.length>1){
			// more than one tree in string, seemingly. attempt to add them all
			for(String splitTreeString: inputSplit){
				try {
					DisplayPhylogeny tempPhylogeny = new DisplayPhylogeny(splitTreeString);
					productsTempList.add(tempPhylogeny);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			// only one tree in string, seemingly. attempt to add it.
			try {
				DisplayPhylogeny tempPhylogeny = new DisplayPhylogeny(input);
				productsTempList.add(tempPhylogeny);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/* Cast the ArrayList to a DisplayPhylogeny[] and return, unless it is empty. */
		if(!productsTempList.isEmpty()){
			DisplayPhylogeny[] products = productsTempList.toArray(new DisplayPhylogeny[productsTempList.size()]);
			return products;
		}else{
			// no DisplayPhylogenies were created: return null.
			return null;
		}
	}
	
	/**
	 * No-arg constructor. 
	 * <p>Nothing to do; no instance variables and all factory from&lt;T extends Object&gt;() methods are static.
	 */
	public DisplayPhylogenyFactory(){
		// Nothing to do; no instance variables and all factory from<T extends Object>() methods are static.
	}
}
