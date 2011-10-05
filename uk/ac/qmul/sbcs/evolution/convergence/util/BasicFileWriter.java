package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.*;

/**
 * 
 * @author Joe Parker, 2011
 * @version 0.1
 *  @
 */
public class BasicFileWriter {
	private final String fullyPathQualifiedFileName;
	private final String data;
	
/**
 * 
 * @param fullyPathQualifiedFileNameArg - a fully qualified (e.g. absolute) filename (String)
 * @param dataArg - the data (String) to write
 */	
	public BasicFileWriter(String fullyPathQualifiedFileNameArg, String dataArg){
		fullyPathQualifiedFileName = fullyPathQualifiedFileNameArg;
		data = dataArg;
		try{
			FileWriter writer = new FileWriter(fullyPathQualifiedFileName);
			writer.write(data);
			writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
