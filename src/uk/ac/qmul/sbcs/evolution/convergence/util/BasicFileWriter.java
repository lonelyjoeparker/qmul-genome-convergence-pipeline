package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.*;

/**
 * 
 * @author Joe Parker, 2011
 * @version 0.1
 * @since 31/10/2011
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
		this.fullyPathQualifiedFileName = fullyPathQualifiedFileNameArg;
		data = dataArg;
		try{
			FileWriter writer = new FileWriter(fullyPathQualifiedFileName);
			writer.write(data);
			writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param outputFile to write to
	 * @param data to write
	 * NB this is the preferred BasicFileWriter as it provides for safer I/O.
	 */
	public BasicFileWriter(File outputFile, String data) {
		// TODO Auto-generated constructor stub
		this.fullyPathQualifiedFileName = outputFile.getAbsolutePath();
		this.data = data;
		try{
			FileWriter writer = new FileWriter(fullyPathQualifiedFileName);
			writer.write(data);
			writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
