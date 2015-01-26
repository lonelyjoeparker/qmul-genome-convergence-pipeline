package uk.ac.qmul.sbcs.evolution.convergence.gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class GlobalModel {
	private boolean DEBUG;	// should we print debug info to console?
	private HashSet<String> taxonNamesSet;	//the set of all taxon names seen in this data.
	private File userBinariesLocation, userWorkdirLocation;	//the location of the required binaries; the location of the working directory.

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
		ArrayList<String> sortedTaxonList = new ArrayList(taxonNamesSet);
		Collections.sort(sortedTaxonList);
		for(String taxon:sortedTaxonList){
			buf.append(taxon+"\n");
		}
		return buf.toString();
	}

	/**
	 * Set the user-specified location of the required binaries
	 * @param newLocation
	 */
	public void setUserBinariesLocation(File newLocation) {
		userBinariesLocation = newLocation;
	}
	
	/**
	 * Get the user-specified location of the required binaries
	 * @return java.io.File - directory where required binaries are expected to be found.
	 */
	public File getUserBinariesLocation(){
		return userBinariesLocation;
	}

	/**
	 * Set the user-specified location of the working directory
	 * @param userWorkdirLocation the userWorkdirLocation to set
	 */
	public void setUserWorkdirLocation(File userWorkdirLocation) {
		this.userWorkdirLocation = userWorkdirLocation;
	}

	/**
	 * Get the user-specified location of the working directory
	 * @return the userWorkdirLocation
	 */
	public File getUserWorkdirLocation() {
		return userWorkdirLocation;
	}
}
