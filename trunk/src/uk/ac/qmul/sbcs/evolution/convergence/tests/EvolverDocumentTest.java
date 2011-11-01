package uk.ac.qmul.sbcs.evolution.convergence.tests;

import junit.framework.TestCase;
import java.io.*;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.*;
import uk.ac.qmul.sbcs.evolution.convergence.*;

public class EvolverDocumentTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInitAA(){
		File f = new File("/pamlTest/testEvolverDocWrite_AA");
		assert(f.canRead());
		assert(f.canWrite());
		EvolverDocument ed = new EvolverDocument(SequenceCodingType.AA, f);
		assert(ed.initialisedWithSequenceType);
		ed.initialiseParameters();
		ed.setParameter("AARATEFILE", "./dat/mtmam.dat");
		assert(ed.getParameter("PAMLFLAG").equals("0"));
		ed.finalizeParameters();
		ed.write();
	}

	public void testInitDNA(){
		File f = new File("/pamlTest/testEvolverDocWrite_DNA");
		assert(f.canRead());
		assert(f.canWrite());
		EvolverDocument ed = new EvolverDocument(SequenceCodingType.DNA, f);
		assert(ed.initialisedWithSequenceType);
		ed.initialiseParameters();
		assert(ed.getParameter("PAMLFLAG").equals("0"));
		ed.finalizeParameters();
		ed.write();
	}

	public void testInitRNA(){
		File f = new File("/pamlTest/testEvolverDocWrite_RNA");
		assert(f.canRead());
		assert(f.canWrite());
		EvolverDocument ed = new EvolverDocument(SequenceCodingType.RNA, f);
		assert(ed.initialisedWithSequenceType);
		ed.initialiseParameters();
		assert(ed.getParameter("PAMLFLAG").equals("0"));
		ed.finalizeParameters();
		ed.write();
	}

	public void testInitCodon(){
		File f = new File("/pamlTest/testEvolverDocWrite_Codon");
		assert(f.canRead());
		assert(f.canWrite());
		EvolverDocument ed = new EvolverDocument(SequenceCodingType.CODON, f);
		assert(ed.initialisedWithSequenceType);
		ed.initialiseParameters();
		assert(ed.getParameter("PAMLFLAG").equals("0"));
		ed.finalizeParameters();
		ed.write();
	}
	
	public void testRandomOddMethod(){
		for(int i = 0;i<50;i++){
			System.out.println(new EvolverDocument().getRandomOddInteger());
		}
	}
}
