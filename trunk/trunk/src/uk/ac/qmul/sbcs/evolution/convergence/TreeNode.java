package uk.ac.qmul.sbcs.evolution.convergence;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;

import jebl.math.Random;

public class TreeNode {
	TreeNode[] daughters;
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
					daughters[1] = new TreeNode(someName, someDist);
					someName = null;
					someDist = null;
				}else{
					somenode.setDistance(someDist);
					daughters[1] = somenode;
					somenode = null;
					someName = null;
					someDist = null;
					
				}
				this.content = "internal("+daughters[0].getContent()+","+daughters[1].getContent()+")";
				endPos++;
				return;
			case ':': //condition distance for previous node;
				someDist = "";
				endPos++;
				break;
			case ',': //condition second node;
				daughters  = new TreeNode[2];
				if(someName != null){
					daughters[0] = new TreeNode(someName,someDist);
					someName = null;
					someDist = null;
				}else{
					somenode.setDistance(someDist);
					daughters[0] = somenode;
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
	public HashSet<String>[] getFitchStates(HashMap<String, HashSet<String>[]> inputStates) {
		if(isTerminal){
			this.states = inputStates.get(content);
			return states;
		}else{
			HashSet<String>[]  leftStates = this.daughters[0].getFitchStates(inputStates);
			HashSet<String>[] rightStates = this.daughters[1].getFitchStates(inputStates);
			this.fitchStateChanges += this.daughters[0].getFitchStateChanges();
			this.fitchStateChanges += this.daughters[1].getFitchStateChanges();
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
}
