package uk.ac.qmul.sbcs.evolution.convergence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jebl.math.Random;

/**
 * 
 * A utility class for operations on phylogenetic trees.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * 
 */
// TODO Expand this javadoc
public class TreeNode {
	ArrayList<TreeNode> daughters;
	HashMap<String,Integer> tipNumberingMap = null;
	TreeNode parent;
	boolean isTerminal;
	String content;
	int startPos;
	int endPos;
	int nodeNumber; // numbering of nodes; tips in order unless tip content is entirely numeric, in which case tips == numbers assumed. Internal nodes numbered L-R and root-tip
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
	 * Numbering of nodes; tips in order unless tip content is entirely numeric, in which case tips == numbers assumed. Internal nodes numbered L-R and root-tip
	 * @param maxTipNumbering tip numbering from 1 on
	 * @param maxInternalNumbering number of last assigned tip
	 * @return updated last numbering; [tip,internal] (int[])
	 */
	public int[] setNodeNumbers(int maxTipNumbering, int maxInternalNumbering){
		if(isTerminal){
			/*
			 * There are several possible contingencies for tip naming schemes that this method has to address:
			 * 
			 * 	1. Tips are named simply as [A-Za-z] strings, and numbering is not important, e.g.: (((LOXODONTA:0.1,DASYPUS:0.1):0.1,((((CANIS:0.1,(EQUUS:0.1,((TURSIOPS:0.1,BOS:0.1):0.1,VICUGNA:0.1):0.1):0.1):0.1,((PTERONOTUS:0.1,MYOTIS:0.1):0.1,((RHINOLOPHUS:0.1,MEGADERMA:0.1):0.1,(PTEROPUS:0.1,EIDOLON:0.1):0.1):0.1):0.1):0.1,(SOREX:0.1,ERINACEUS:0.1):0.1):0.1,((MUS:0.1,(ORYCTOLAGUS:0.1,OCHOTONA:0.1):0.1):0.1,(PAN:0.1,HOMO:0.1):0.1):0.1):0.1):0.1,MONODELPHIS:0.1)
			 * 		(in this case numbering would proceed from (1,2..n) in the order tips are encountered.
			 * 	2. Tips are named simply as in (1), but a specific numbering order is required by another class. For instance, PAML may have numbered sequences/taxa alphabetically.
			 * 		(in this case the numbering for the same topology above may look like (((8, 3), ((((2, (5, ((20, 1), 21))), ((16, 12), ((18, 9), (17, 4)))), (19, 6)), ((11, (14, 13)), (15, 7)))), 10) in numbered form.
			 * 	3. Tips are already numeric only (as in the exmaple in (2)
			 * 	4. Tips are a horrible hybrid composite label, e.g. (((8_LOXODONTA, 3_DASYPUS) 24 , ((((2_CANIS, (5_EQUUS, ((20_TURSIOPS, 1_BOS) 31 , 21_VICUGNA) 30 ) 29 ) 28 , ((16_PTERONOTUS, 12_MYOTIS) 33 , ((18_RHINOLOPHUS, 9_MEGADERMA) 35 , (17_PTEROPUS, 4_EIDOLON) 36 ) 34 ) 32 ) 27 , (19_SOREX, 6_ERINACEUS) 37 ) 26 , ((11_MUS, (14_ORYCTOLAGUS, 13_OCHOTONA) 40 ) 39 , (15_PAN, 7_HOMO) 41 ) 38 ) 25 ) 23 , 10_MONODELPHIS) 22 ;
			 * 
			 * This class has ^attempted^ to cover all these but focussed mainly on (1) and (2). Note that support for tip labelling with specific name-ID mappings is supported through the setTipNameNumberMapping() method, in which a HashMap<String,Integer> is used to specify them.
			 * Note also that this is not very well tested for odd mixtures of labels (some numeric, some alphabetical, some both), or for whitespace / special chars. 
			 */
			//enable parsing of Rod Paige / TreeView format strings, containing IDs and names. e.g. 10_MONODELPHIS should receive '10'.
			// TODO grep on /^[0-9]+/ e.g. greedy on opening contiguous numbers?
			Pattern digitStart = Pattern.compile("^[0-9]+");
			Matcher digitMatch = digitStart.matcher(content);
			if(digitMatch.find()){
				// a number is present at the start of the content string
				try {
					// try and parse the tip content as a number is present
					int parsedNumber = Integer.parseInt(digitMatch.group());
					this.nodeNumber = parsedNumber;
					if(nodeNumber > maxTipNumbering){maxTipNumbering = nodeNumber;}
				} catch (NumberFormatException e) {
					// either no number is present, or we can't parse it
					//e.printStackTrace(); we don't really need the stack trace
					// assign numnber for this tip de novo
					maxTipNumbering++;
					this.nodeNumber = maxTipNumbering;
				}
			} else {
				// this content string appears to be alphanumeric, at least, numerals are not present at the start of the string
				// increment and label tip numbers as normal (so that maxtipNumbering == number of tips in the tree), but we'll check to see if a specific name-ID mapping exists too.
				maxTipNumbering++;
				this.nodeNumber = maxTipNumbering;
				// see if the tipNumberingMap is set
				if(this.tipNumberingMap != null){
					// note that nodeNumber and tempNumbering are ^not^ initialised. this is because the ID returned by tipNumberingMap.get(content) could have any value and is not predictable. 
					// this means that iniialising to 0 and testing for inequality (or any other test) might give odd results.
					try {
						// there is a map present, see if this tip content has a numbering ID specified
						int tempNumbering = tipNumberingMap.get(content);
						this.nodeNumber = tempNumbering;
					} catch (Exception e) {
						// no tip found with a numbering specified
					}
				}
			}
		}else{
			maxInternalNumbering++;
			this.nodeNumber = maxInternalNumbering;
			for(TreeNode daughter:daughters){
				int[] lastNodeNumberings = daughter.setNodeNumbers(maxTipNumbering, maxInternalNumbering);
				maxTipNumbering = lastNodeNumberings[0];
				maxInternalNumbering = lastNodeNumberings[1];
			}
		}
		int[] retVals = {maxTipNumbering,maxInternalNumbering};
		return retVals;
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

	/**
	 * Prints the recursive (Newick) representation of this node (tree)
	 * @return String containing nodes below this one
	 */
	public String printRecursivelyAsNumberedNodes(){
		if(this.isTerminal){
			return this.nodeNumber+"_"+this.content;
		}else{
			String retString = "(";
			for(TreeNode daughter:daughters){
				retString = retString + daughter.printRecursivelyAsNumberedNodes() + ",";
			}
			retString = retString.substring(0, retString.length()-1) + ") "+this.nodeNumber;
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
	 * @return {@link HashMap&lt;String,HashSet&lt;String&gt;[]&gt;} - the states
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
	
	/**
	 * Returns the size of the largest <i>monophyletic</i> clade for the given taxa list.
	 * <br/>Hard polytomies (nodes with n>2 daughters) are counted as monophyletic <b>only</b> if all daughters are monophyletic.
	 * @author Joe Parker
	 * @since r194 2013/08/02
	 * @param someTaxa - a {@link HashSet&lt;String&gt;} with target taxa to look for monophly of.
	 * @return #of terminal taxa below that node which are in a reciprocally monophyletic clade.
	 */
	public int howManyFromMonophyleticSet(HashSet<String> someTaxa){
		if(isTerminal){
			if(someTaxa.contains(this.content)){
				// return true if this taxon is in the desired list
				return 1;
			}else{
				return 0;
			}
		}else{
			int howManyMax = 0;
			int howManyTotal = 0;
			int howManyMin = Integer.MAX_VALUE;
			// We will aggregate the max clade sizes up the tree
			for(TreeNode daughter:daughters){
				int daughterHowManyMRCA = daughter.howManyFromMonophyleticSet(someTaxa);
				howManyTotal += daughterHowManyMRCA;
				if(daughterHowManyMRCA > howManyMax){
					// Daughter has a max clade worth aggregating
					howManyMax = daughterHowManyMRCA;
				}
				if(daughterHowManyMRCA < howManyMin){
					// Daughter decrement howManyMin (this should == 0 in the case that the daughter has no taxa from the list)
					howManyMin = daughterHowManyMRCA;
				}
			}
			if(howManyMin == 0){
				// There is at least one daughter with a taxon outside of the taxaList; stop aggregating
				if(howManyMax == 1){
					// The only match(es) in daughters are singletons - since we already know this node is <i>not</i> monophyletic (howManyMin==0) we should not increment.
					return 0;
				}else{
					// This node isn't monophyletic, but at least one of the daughters is; so pass the largest monophyletic value below.
					return howManyMax;
				}
			}else{
				// This node is itself directly monophyletic (no daughters with max clade==0) so pass up the aggregated score
				return howManyTotal;
			}
		}
	}
	
	/**
	 * This method attempts to determine whether there are any monophyletic clades present containing the {@link HashSet&lt;String&gt;} of target taxa.
	 * <p>@Deprecated - this method does <b>not</b> work well if hard polytomies are present (daughters.size()>2); instead use {@link uk.ac.qmul.sbcs.evolution.convergence.TreeNode#howManyFromMonophyleticSet()}
	 * @author Joe Parker
	 * @since r194 2013/08/02
	 * @param someTaxa - a {@link HashSet&lt;String&gt;} with target taxa to look for monophly of.
	 * @return boolean ifMonophyletic
	 */
	@Deprecated
	public boolean containsMonophyleticClade(HashSet<String> someTaxa){
		boolean hasMonophyly = false;
		if(isTerminal){
			// return true if this taxon is in the desired list
			if(someTaxa.contains(this.content)){
				hasMonophyly = true;
			}
		}else{
			int tipsBelow = this.howManyTips();
			int sizeOfMRCA = someTaxa.size();
			/*
			 * If this clade is ² desired monophyletic clade, return true if all daughters have monophyly
			 * Else return true if any daughters have monophyly
			 */
			if(tipsBelow <= sizeOfMRCA){
				hasMonophyly = true;
				for(TreeNode daughter:daughters){
					if(!daughter.containsMonophyleticClade(someTaxa)){
						hasMonophyly = false;
					}
				}
			}else{
				if(daughters.size() > 2){
					// TODO correctly evaluate polytomies in the case that a polytomy contains internal, not external, nodes
					// FIXME just checking each (daughters' size == monophyletic set size) WON'T work, as daughter could contain clade without being monophyletic itself
					// for the moment, allow polytomy to dictate a false retval; sort this out later
					
					// FIXME OK, this is just for polytomies entirely of terminal nodes - VERY quick, NOT tested.. 2013 08 01

					// first see if we have a polytomy of only terminal taxa
					if(this.howManyTips() == someTaxa.size()){
						int allTerminalAndMonophyletic = 0;
						for(TreeNode daughter:daughters){
							if(daughter.containsMonophyleticClade(someTaxa) && daughter.isTerminal){
								allTerminalAndMonophyletic++;
							}
						}
						if(allTerminalAndMonophyletic == someTaxa.size()){
							hasMonophyly = true;
						}
					}

					// now, in case that's not applicable (e..g this has > n tips, or tips aren't all terminal, try seeing if the daughters have anything
					for(TreeNode daughter:daughters){
						if(daughter.containsMonophyleticClade(someTaxa) && (!daughter.isTerminal) && (daughter.howManyTips() == someTaxa.size())){
							hasMonophyly = true;
						}
					}
					// end FIXME
				}else{
					for(TreeNode daughter:daughters){
						if(daughter.containsMonophyleticClade(someTaxa)){
							hasMonophyly = true;
						}
					}
				}
			}
		}
		return hasMonophyly;
	}
	
	/**
	 * Simple utility method to count tips attached below this node (post-order traversal).
	 * @return int - number of tips below this node.
	 */
	public int howManyTips(){
		if(isTerminal){
			return 1;
		}else{
			int tips = 0;
			for(TreeNode daughter:daughters){
				tips += daughter.howManyTips();
			}
			return tips;
		}
	}
	
	public ArrayList<String> getTipsInOrder(){
		ArrayList<String> tips = new ArrayList<String>();
		if(isTerminal){
			tips.add(content);
		}else{
			for(TreeNode daughter:daughters){
				for(String daughterTip:daughter.getTipsInOrder()){
					tips.add(daughterTip);
				}
			}
		}
		return tips;
	}

	/**
	 * Get a list of the taxa (terminal tips) below this node.
	 * @return
	 */
	public String[] getTipsBelow(){
		if(this.isTerminal){
			// Simple, return the taxon name
			String[] below = {this.content};
			return below;
		}else{
			// Internal node. Poll daughters.
			int howManyTips = this.howManyTips();
			String[] below = new String[howManyTips];
			int startIndex = 0;
			for(TreeNode daughter:this.daughters){
				// Get daughter tips, add vals to below array, starting at last index
				String[] daughterTips = daughter.getTipsBelow();
				for(int i=0;i<daughterTips.length;i++){
					below[startIndex+i] = daughterTips[i];
				}
				startIndex += daughterTips.length;
			}
			return below;
		}
	}
	
	/**
	 * A method to retrieve the number (node ID, as set in {@link TreeNode#setNodeNumbers()} method) of the lowest node containing all taxa present in taxaContained, a {@link HashSet} of {@link String}s representing taxa. 
	 * <p>Note that although all taxa <b>must</b> be present (so, this method does not have predictable behaviour in trees where branches have been pruned etc), the most recent clade containing them which will be reported <b>may not actually be <i>strictly</i> monophyletic</b>. That is, other taxa not listed might be present as well: this method <i>only</i> guarantees that the node returned is the lowest containing all members in the target list.</p> 
	 * @param taxaContained - a {@link HashSet}&lt;String&gt; of taxon names.
	 * @return int; ID of the lowest node containing all members of taxaContained; or -1 otherwise.
	 * @see HashSet
	 * @since r293 2014/10/10
	 */
	public int getNodeNumberingIDContainingTaxa(HashSet<String> taxaContained){
		int retval = -1;
		if(isTerminal && (taxaContained.size()==1) && (taxaContained.contains(content))){
			// there is only one taxon sought, and this is it. return this node (tip's) number (ID)
			retval = nodeNumber;
		}else if(!isTerminal){
			// loop through daughters. if any have nonegative retval, we'll pass that up- othereise check this node.
			for(TreeNode daughter:daughters){
				int daughterRetval = daughter.getNodeNumberingIDContainingTaxa(taxaContained);
				retval = Math.max(retval, daughterRetval);
			}
			// if any of the daughters have the taxa required as a monophyletic clade, we should have passed up their IDs. If not, we need to see if this node does (i.e. daughters together comprise target clade)
			if((retval == -1) && (this.areTipsPresent(taxaContained).size() == taxaContained.size())){
				retval = nodeNumber;
			}
		}
		return retval;
	}

	/**
	 * A method to retrieve the number (node ID, as set in {@link TreeNode#setNodeNumbers()} method) of the lowest node containing all taxa present in taxaContained, a {@link HashSet} of {@link String}s representing taxa. 
	 * <p>Note that although all taxa <b>must</b> be present (so, this method does not have predictable behaviour in trees where branches have been pruned etc), the most recent clade containing them which will be reported <b>may not actually be <i>strictly</i> monophyletic</b>. That is, other taxa not listed might be present as well: this method <i>only</i> guarantees that the node returned is the lowest containing all members in the target list.</p> 
	 * @param taxaContained - a {@link HashSet}&lt;String&gt; of taxon names.
	 * @return int[]; {ID of the lowest node containing all members of taxaContained,ID of the node immediately above that}; or -1 otherwise.
	 * @see HashSet
	 */
	public int[] getBranchNumberingIDContainingTaxa(HashSet<String> taxaContained){
		int[] retval = {-1,-1};
		if(isTerminal && (taxaContained.size()==1) && (taxaContained.contains(content))){
			// there is only one taxon sought, and this is it. return this node (tip's) number (ID)
			retval[0] = nodeNumber;
		}else if(!isTerminal){
			// loop through daughters. if any have nonegative retval, we'll pass that up- othereise check this node.
			for(TreeNode daughter:daughters){
				int[] daughterRetval = daughter.getBranchNumberingIDContainingTaxa(taxaContained);
				retval[0] = Math.max(retval[0], daughterRetval[0]);
				if(retval[0] != -1){
					retval[1] = Math.max(retval[1], daughterRetval[1]);
				}
			}
			// if any of the daughters have the taxa required as a monophyletic clade, we should have passed up their IDs. If not, we need to see if this node does (i.e. daughters together comprise target clade)
			if((retval[0] == -1) && (this.areTipsPresent(taxaContained).size() == taxaContained.size())){
				retval[0] = nodeNumber;
			}
			// if the daughter retval is nonnegative then retval[1] needs to be this node ID
			if((retval[0] > -1)&&(retval[0] != nodeNumber)&&(retval[1] == -1)){
				retval[1] = nodeNumber;
			}
		}
		return retval;
	}

	/**
	 * Sets the HashMap<String,Integer> that will contain the taxon names / numeric IDs (both unique). This information is required to ensure that classes using specific tip-ID mappings can specify them (e.g. {@link:uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlAncestralSiteOutputParser} using PAML's tip-numberings, not native sequential numberings.
	 * @param tipNumberMap a HashMap of String,Integer containing the taxon names / numeric IDs (both unique)
	 */
	public void setTipNameNumberMapping(HashMap<String, Integer> tipNumberMap) {
		// set mapping for this node
		if(this.tipNumberingMap == null){
			this.tipNumberingMap = tipNumberMap;
		}
		if(!isTerminal){
			// traverse the tree recursively
			for(TreeNode daughter:daughters){
				daughter.setTipNameNumberMapping(tipNumberingMap);
			}
		}
	}

	/**
	 * A method that extracts the ID (numbering) for tip taxonName within the tree. NB names are matched using String.equals(String) call, so are caSe SeNsITive.
	 * <p><b>IMPORTANT</b> returns 0 with no error/exception if no matching tip can be found. Be warned!
	 * @param taxonName
	 * @return integer unique ID defining the tip which has this taxonName. NB returns 0 if no tip found with no error.
	 */
	public int getTipNumber(String taxonName) {
		int retval = 0;
		// is this a terminal?
		if(isTerminal){
			//it this tip the right one?
			if(content.equals(taxonName)){
				retval = nodeNumber;
			}
			return retval;
		}else{
			//recurse the tree
			for(TreeNode daughter:daughters){
				int searchDaughters = daughter.getTipNumber(taxonName);
				if(searchDaughters != 0){
					retval = searchDaughters;
				}
			}
			return retval;
		}
	}
	
	/**
	 * Returns a list of relative X,Y positions for line segments representing branches, to be rendered with a Graphics2D.drawLine(x1,y1,x2,y2) call or similar.
	 * <br/>Note this method assumes a strictly bifurcating tree, e.g. n=2 daughters for each node exactly.
	 * <br/>Note also that 'left' and 'right' refer to these two daughters, not left/right orientation on the screen.
	 * @param startX - Xposition to start from
	 * @param startY - Y pos to start from
	 * @param branchIncrementX - how much to increment each branch by (width)
	 * @param branchIncrementY - how much to increment each branch by (height)
	 * @return - An ArrayList<Integer[]> of branches. Each Integer[4] of the form {x1, y1, x2, y2}. All x will be positive. Those branches 'left' of the root will have -ve x, those right will have +ve.
	 */
	public ArrayList<Integer[]> getBranchesAsCoOrdinates(int startX, int startY, int branchIncrementX, int branchIncrementY){
		// Instantiate the return array
		ArrayList<Integer[]> returnLineCoordinates = new ArrayList<Integer[]>();
		
		// Calculate co-ordinates for this branch, only extend in X-direction
		int endX = startX + branchIncrementX;
		Integer[] thisBranch = new Integer[4];
		thisBranch[0] = startX;
		thisBranch[1] = startY;
		thisBranch[2] = endX;
		thisBranch[3] = startY;
		returnLineCoordinates.add(thisBranch);
		
		// Calculate co-ordinates for the vertical line which will connect the daughters, extend Y direction only
		int endYleft = startY - branchIncrementY;
		int endYright= startY + branchIncrementY;
		Integer[] nodeConnector = new Integer[4];
		nodeConnector[0] = endX;
		nodeConnector[1] = endYleft;
		nodeConnector[2] = endX;
		nodeConnector[3] = endYright;			
		returnLineCoordinates.add(nodeConnector);
		
		// repeat calculation for daughters. assume n=2 daughters exactly. left daughter will have -Y, right daughter will have +Y
		if(!this.isTerminal){
			// can't just iterate - each daughter needs a different Y offset.
			// daughters.get(0)
			TreeNode leftDaughter = daughters.get(0);
			ArrayList<Integer[]> daughterLeftCoords = leftDaughter.getBranchesAsCoOrdinates(endX, endYleft, branchIncrementX, branchIncrementY);
			returnLineCoordinates.addAll(daughterLeftCoords);
			// daughters.get(1)
			TreeNode rightDaughter = daughters.get(1);
			ArrayList<Integer[]> daughterRightCoords = rightDaughter.getBranchesAsCoOrdinates(endX, endYright, branchIncrementX, branchIncrementY);
			returnLineCoordinates.addAll(daughterRightCoords);
		}
		
		// return finished list of co-ordinates
		return returnLineCoordinates;
	}
}
