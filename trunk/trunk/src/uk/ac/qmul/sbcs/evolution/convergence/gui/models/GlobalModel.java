package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.util.HashSet;
import java.util.Iterator;

public class GlobalModel {
	private boolean DEBUG;	// should we print debug info to console?
	private HashSet<String> taxonNamesSet;	//the set of all taxon names seen in this data.

	/**
	 * Default no-arg constructor.
	 */
	public GlobalModel(){
		DEBUG = false;
		taxonNamesSet = new HashSet<String>();	
	}
	
	public HashSet<String> getTaxonNamesSet() {
		return taxonNamesSet;
	}

	public void setTaxonNamesSet(HashSet<String> taxonNamesSet) {
		this.taxonNamesSet = taxonNamesSet;
	}

	public boolean isDEBUG() {
		return DEBUG;
	}

	public void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	/**
	 * returns the taxonList as a readable list (newlines separate taxa)
	 * @return
	 */
	public String getTaxonNamesSetAsMultilineString() {
		StringBuffer buf = new StringBuffer();
		Iterator<String> taxonItr = taxonNamesSet.iterator();
		while(taxonItr.hasNext()){
			buf.append(taxonItr.next()+"\n");
		}
		return buf.toString();
	}
}
