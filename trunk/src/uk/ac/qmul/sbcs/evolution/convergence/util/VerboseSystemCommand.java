package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VerboseSystemCommand {
	public final String exeString;
	
	public VerboseSystemCommand(String exeString){
		this.exeString = exeString;
		System.out.println("\n\nattempting command "+exeString);
		try {
			Process p = Runtime.getRuntime().exec(exeString);
			BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = iReader.readLine();
			int i = 0;
			while(line != null){
				System.out.println(i+" "+line);
				line = iReader.readLine();
				i++;
			}
			System.out.println("done with output\nerror:");
			BufferedReader eReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = eReader.readLine();
			int e = 0;
			while(line != null){
				System.out.println(e+" "+line);
				line = eReader.readLine();
				e++;
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
