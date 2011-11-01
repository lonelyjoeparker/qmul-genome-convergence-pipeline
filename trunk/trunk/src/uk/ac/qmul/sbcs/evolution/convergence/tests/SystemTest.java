package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.io.*;

public class SystemTest {
	public static void main(String[] args){
		System.out.println("I reckon Working Directory = " + System.getProperty("user.dir"));
		try {
			Process p = Runtime.getRuntime().exec("java -version");
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = iReader.readLine();
			int i = 0;
			while(line != null){
				System.out.println(i+" "+line);
				line = iReader.readLine();
				i++;
			}
			BufferedReader eReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = eReader.readLine();
			i = 0;
			while(line != null){
				System.out.println("ERROR: "+i+" "+line);
				line = eReader.readLine();
				i++;
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
					/*
					 * TODO correct AAmlAnalysis and BasemlAnalysis
					 * TODO set up ConselAnalysis
					 * TODO finish simulateConvergence method for PSR.SequenceType.DNA and RNA
					 * TODO run treefiles as strings currently
					 * TODO set up Simulate using evolver or seq-gen
					 * TODO write some kind of stats class etc
					 * 
					 * in other words...
					 * 
					 * GET ! ON ! WITH ! THE ! MAIN ! PROJECT !!!!!!
					 * 
					 */
}

