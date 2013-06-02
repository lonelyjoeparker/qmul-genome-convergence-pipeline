package uk.ac.qmul.sbcs.evolution.convergence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jebl.math.Random;

public class TreeNode {
	ArrayList<TreeNode> daughters;
	TreeNode parent;
	boolean isTerminal;
	String content;
	int startPos;
	int endPos;
	double branchLength;
	public HashSet<String>[] states;
	int fitchStateChanges;
	
	/**
	 * Constructor for node
	 * @param tree
	 * @param startAt
	 */
	public TreeNode(String tree, int startAt){
		this.content = tree;
		this.startPos = startAt;
		this.endPos = startPos;
		this.isTerminal = false;
		this.daughters  = new ArrayList<TreeNode>();
		TreeNode somenode = null;
		String someName = null;
		String someDist = null;
		while(endPos < content.length()){
			char someChar = content.charAt(endPos);
			switch(someChar){
			case '(': //condition new node;
				endPos++;
				somenode = new TreeNode(tree,endPos);
				endPos = somenode.getEndPos();
				break;
			case ')': //condition end node;
				if(someName != null){
					daughters.add(new TreeNode(someName, someDist));
					someName = null;
					someDist = null;
				}else{
					somenode.setDistance(someDist);
					daughters.add(somenode);
					somenode = null;
					someName = null;
					someDist = null;
					
				}
				this.content = "(";
				for(TreeNode daughter:daughters){
					content = content + daughter.getContent() + ",";
				}
				content = content.substring(0, content.length()-1)+")";
				endPos++;
				return;
			case ':': //condition distance for previous node;
				someDist = "";
				endPos++;
				break;
			case ',': //condition second node;
				if(someName != null){
					daughters.add(new TreeNode(someName,someDist));
					someName = null;
					someDist = null;
				}else{
					somenode.setDistance(someDist);
					daughters.add(somenode);
					somenode = null;
					someDist = null;
				}
				endPos++;
				break;
			default:  //condition default;
				if(someName != null){
					if(someDist != null){
						// we have a distance initialised, and a name. The name should be OK, increment dist
						someDist = someDist + someChar;
					}else{
						// we only have a name initialised (probably haven't reached the ':' char yet
						someName = someName + someChar;
					}
				}else{
					if(someDist != null){
						// we have a dist, increment dist
						someDist = someDist + someChar;
					}else{
						// init name
						someName = Character.toString(someChar);
					}
				}
				endPos++;
				break;
			}
		}
	}
	
	/**
	 * Constructor for terminal taxa only
	 * @param name
	 */
	private TreeNode(String name, String brLength){
		this.content = name;
		if(brLength != null){
			this.branchLength = Double.parseDouble(brLength);
		}
		this.isTerminal = true;
		this.daughters = null;
	}
	
	/**
	 * Returns the position the last node constructor left at
	 * @return
	 */
	public int getEndPos(){
		return endPos;
	}
	
	/**
	 * Override the toString method for nodes.
	 */
	@Override
	public String toString(){
		return this.content;
	}
	
	/**
	 * Set the distance - normally for after an internal node has been initialised.
	 * @param brLength
	 */
	private void setDistance(String brLength){
		if(brLength != null){
			this.branchLength = Double.parseDouble(brLength);
		}
	}

	/**
	 * This calls a post-order (leaves-to-root) traversal of the tree, terminal taxa will have their states determined by the input list.
	 * <p>Execution:
	 * <pre>
	 * if(isTerminal){
	 * 	states = inputStates.get(name)
	 *  return states
	 * }else{
	 * 	 leftDaughterStates = daughters[0].getFitchStates(inputStates)
	 * 	rightDaughterStates = daughters[1].getFitchStates(inputStates)
	 *  for(i, stateslength){
	 *  	resolve fitch states; e.g. if no intersection between left & right states, union; else intersection
	 *  }
	 *  return states
	 * }
	 * </pre>
	 * @param states; a HashMap of all of the states for terminal taxa
	 * @return post-order traversal will give sets of possible states for this node, pass up.
	 */
	@Deprecated
	/**
	 * WARNING WARNING WARNING WARNING WARNING
	 * Recently (r177; 25/05/2013) refactored daughters to ArrayList<TreeNode> from TreeNode[]
	 * THE CURRENT IMPLEMENTATION OF THIS METHOD DOES NOT ACCOUNT FOR daughters>2
	 * @TODO
	 */
	public HashSet<String>[] getFitchStates(HashMap<String, HashSet<String>[]> inputStates) {
		if(isTerminal){
			this.states = inputStates.get(content);
			return states;
		}else{
			HashSet<String>[]  leftStates = this.daughters.get(0).getFitchStates(inputStates);
			HashSet<String>[] rightStates = this.daughters.get(1).getFitchStates(inputStates);
			this.fitchStateChanges += this.daughters.get(0).getFitchStateChanges();
			this.fitchStateChanges += this.daughters.get(1).getFitchStateChanges();
			if(leftStates.length != rightStates.length){
				throw new IllegalArgumentException("Fitch reconstruction: lengths of daughters' state arrays don't match!");
			}else{
				this.states = (HashSet<String>[]) Array.newInstance(HashSet.class, leftStates.length);
				for(int i=0;i<leftStates.length;i++){
					HashSet<String> leftState =  leftStates[i];
					HashSet<String>rightState = rightStates[i];
					HashSet<String> unionSet = new HashSet<String>();
					HashSet<String> intersectionSet = new HashSet<String>();
					for(String someState:leftState){
						if(rightState.contains(someState)){
							intersectionSet.add(someState);
						}else{
							unionSet.add(someState);
						}
					}
					if(intersectionSet.isEmpty()){
						// there are no states from left present in right.
						// create the union set (already done for left, add all right)
					//	this.fitchStateChanges++;
						this.fitchStateChanges += unionSet.size()-1;
						unionSet.addAll(rightState);
						this.fitchStateChanges += rightState.size()-1;
						states[i] = unionSet;
					}else{
						states[i] = intersectionSet;
					}
				}
				return states;
			}
		}
	}
	
	/**
	 * Performs the pre-order (root-leaves) traversal to set the fitch states, ASSUMING a pre-order traversal has occured.
	 */
	public void resolveFitchStatesTopnode(){
		for(int i=0;i<states.length;i++){
			if(states[i].size() > 1){
				// we must be the top node, argh. pick a parent state
				int whichParentState = Random.nextInt(states[i].size());
				Object[] someState = states[i].toArray();
				states[i] =  new HashSet<String>();
				states[i].add((String)someState[whichParentState]);
			}
		}
	}

	/**
	 * Performs the pre-order (root-leaves) traversal to set the fitch states, ASSUMING a pre-order traversal has occured.
	 */
	public void resolveFitchStates(HashSet<String>[] parentStates){
		if(!this.isTerminal){
			if(this.states == null){
				throw new NullPointerException("get fitch state changes error: states not assigned yet!");
			}else{
				for(int i=0;i<parentStates.length;i++){
					Object[] parentState = parentStates[i].toArray();
					if(states[i].contains((String)parentState[0])){
						// we must be the top node, argh. pick a parent state
						states[i] = parentStates[i];
					}else{
						int whichState = Random.nextInt(states[i].size());
						Object[] someState = states[i].toArray();
						states[i] =  new HashSet<String>();
						states[i].add((String)someState[whichState]);
					}
				}
				for(TreeNode daughter:daughters){
					daughter.resolveFitchStates(states);
				}
			}
		}
	}

	private int getFitchStateChanges(){
		if(this.isTerminal){
			return 0;
		}else{
			if(this.states == null){
				throw new NullPointerException("get fitch state changes error: states not assigned yet!");
			}
			return this.fitchStateChanges;
		}
		
	}
	
	public void printStates(){
		if(isTerminal){
			for(HashSet<String> aState:states){
				Object[] stateArray = aState.toArray();
				System.out.print("\t"+stateArray[0].toString());
			}
			System.out.println("\t"+content);
		}else{
			for(HashSet<String> aState:states){
				Object[] stateArray = aState.toArray();
				for(Object o:stateArray){
					System.out.print("\t"+o.toString());
				}
				System.out.print("|");
			}
			System.out.println("\t"+content);
			for(TreeNode daughter:daughters){
				daughter.printStates();
			}
		}
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the states
	 */
	public HashSet<String>[] getStates() {
		return states;
	}

	public void printTree() {
		// TODO Auto-generated method stub
		System.out.println(this.content);
	}
	
	public String printRecursively(){
		if(this.isTerminal){
			return content;
		}else{
			String retString = "(";
			for(TreeNode daughter:daughters){
				retString = retString + daughter.printRecursively() + ",";
			}
			retString = retString.substring(0, retString.length()-1) + ")";
			return retString;
		}
	}

	public String printRecursivelyLabelling(String[] someTaxa){
		if(this.isTerminal){
			if(this.subtreeContains(someTaxa)){
				return content+"#1";
			}else{
				return content;
			}
		}else{
			if(this.subtreeContains(someTaxa)){
				String retString = "(";
				for(TreeNode daughter:daughters){
					retString = retString + daughter.printRecursivelyLabelling(someTaxa) + ",";
				}
				retString = retString.substring(0, retString.length()-1) + ")#1";
				return retString;
			}else{
				String retString = "(";
				for(TreeNode daughter:daughters){
					retString = retString + daughter.printRecursivelyLabelling(someTaxa) + ",";
				}
				retString = retString.substring(0, retString.length()-1) + ")";
				return retString;
			}
		}
	}
	
	public boolean subtreeContains(String[] someTaxa){
		boolean thisContains = false;
		if(this.isTerminal){
			for(String taxon:someTaxa){
				if(taxon.equals(content)){
					thisContains = true;
				}
			}
		}else{
			boolean allContain = true;
			ArrayList<Boolean> daughtersContain = new ArrayList<Boolean>();
			for(TreeNode daughter:daughters){
				daughtersContain.add(daughter.subtreeContains(someTaxa));
			}
			if(daughtersContain.contains(false)){
				allContain = false;
			}
			thisContains = allContain;
		}
		return thisContains;	
	}

	/**
	 * Intended to return the tip states and MRCA of all said tips.
	 * <p>Currently relies on simply stopping and passing ret hashmap when it has size of tipsTotrace.length (+1 for the MRCA)
	 * <p>Therefore the desired tip list <b>MUST</b> have been pruned by {@link TreeNode}.areTipsPresent(HashSet<String> echoMap)} first..
	 * <p>NB also - 'MRCA' is used as a key for the MRCA states, so there <b>Must Not Be Any Tips Labelled 'MRCA'. At. All.</b> (ideally catch or failsafe this)
	 * @param tipsToTrace - the terminal taxon tips that we want the states + MRCA of.
	 * @return {@link HashMap<String,HashSet<String>[]>}
	 */
	public HashMap<String, HashSet<String>[]> getTipAndMRCAStatesOf(HashSet<String> tipsToTrace) {
		HashMap<String,HashSet<String>[]> retMap = new HashMap<String,HashSet<String>[]>();
		if(isTerminal){
			// Just check if this is one of the desired tips
			if(tipsToTrace.contains(content)){
				retMap.put(this.content, this.states);
			}
		}else{
			// An internal node - iterate through daughters
			for(TreeNode daughter:daughters){
				HashMap<String,HashSet<String>[]> daughterContents = daughter.getTipAndMRCAStatesOf(tipsToTrace);
				if(daughterContents != null){
					retMap.putAll(daughterContents);
				}
			}
			// Check to see if we have all the daughters
			if(retMap.size() == tipsToTrace.size()){
				retMap.put("MRCA", this.states);
			}
		}
		return retMap;
	}

	/**
	 * Which of the tips in the supplied list are below this node?
	 * @param echoMap
	 * @return
	 */
	public HashSet<String> areTipsPresent(HashSet<String> echoMap) {
		HashSet<String> retMap = new HashSet<String>();
		if(echoMap.contains(content)){
			retMap.add(content);
		}
		if(!isTerminal){
			for(TreeNode daughter:daughters){
				HashSet<String> daughterContents = daughter.areTipsPresent(echoMap);
				if(daughterContents != null){
					retMap.addAll(daughterContents);
				}
			}
		}
		return retMap; 
	}
}
