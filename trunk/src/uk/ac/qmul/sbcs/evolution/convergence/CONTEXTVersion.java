/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence;

import beast.app.util.Version;

/**
 * Concrete subclass implementing {@link Version} functionality, broadly as implemented in {@link BEASTVersion} (both forked under GNU LGPL 2.1 from <a href="https://github.com/CompEvol/beast2/blob/master/src/beast/app/util/Version.java ">BEAST2 on GitHub</a>.
 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
 * @since Aug 19, 2015
 * @version 0.1
 * @see beast.app.BEASTVersion
 * @see beast.app.util.Version
 */
public class CONTEXTVersion extends Version {

    /**
     * Version string: assumed to be in format x.x.x
     */
    private static final String VERSION = "0.8";

    private static final String DATE_STRING = "2011-2015";

    private static final boolean IS_PRERELEASE = true;

    private static final String CONTEXT_WEBPAGE = "https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/CONTEXT.md";
    
    private static final String CONTEXT_SOURCE = "https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline";
    
    private static final String CITATION = "Parker and Rossiter, 2015. CONTEXT: A phylogenomic dataset browser. In prep.";
    
    private static final String CITATION_URL = "http://www.lonelyjoeparker.com/?p=1205";

    /**
	 * 
	 */
	public CONTEXTVersion() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see beast.app.util.Version#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return VERSION;
	}

	/* (non-Javadoc)
	 * @see beast.app.util.Version#getVersionString()
	 */
	@Override
	public String getVersionString() {
        return "v" + VERSION + (IS_PRERELEASE ? " Prerelease" : "");
	}

	/* (non-Javadoc)
	 * @see beast.app.util.Version#getDateString()
	 */
	@Override
	public String getDateString() {
		return DATE_STRING;
	}

	/* (non-Javadoc)
	 * @see beast.app.util.Version#getCredits()
	 */
	@Override
	public String[] getCredits() {
        return new String[]{
        		this.getVersionString(),
        		"",
                "Designed and developed by",
                "Joe Parker & Steve Rossiter",
                "",
                "Department of Biodiversity Informatics &amp; Spatial Analysis",
                "Royal Botanic Gardens, Kew",
                "joe.parker@kew.org",
                "",
                "School of Biological And Chemical Sciences",
                "Queen Mary University of London",
                "s.j.rossiter@qmul.ac.uk",
                "",
                "Downloads, Help & Resources:",
                CONTEXT_WEBPAGE,
                "",
                "Source code distributed under the GNU Lesser General Public License:",
                CONTEXT_SOURCE,
                "",
                "Please cite this software if used in any academic work:",
                CITATION,
                CITATION_URL,
                "",
                "CONTEXT developers:",
                "Joe Parker, Steve Rossiter",
                "",
                "Thanks to:",
                "Kalina T. J. Davies and James A. Cotton",
                "",
                "",
                "Incorporating third-party code under various licences:",
                "Colt - Open Source Libraries for High Performance Scientific and Technical Computing (colt.jar), ",
                "JAMA - Java Matrix Algebra (1.0.3, 2012.11.09; Jama-1.0.3.jar), ",
                "JSC - Java Statistical Classes v1.0 (jsc-1.jar), ",
                "XStream - Java XML bindings (xstream-1.4.4.jar and dependencies), ",
                "JFreeChart (jfreechart-1.0.14.jar), ",
                "JEBL - Java Evolutionary Biology Library v0.4 (jebl-0.4.jar)"};
	}

    public String getHTMLCredits() {
        String sStr = "<H2>CONTEXT:</H2><i>A Phylogenomic dataset browser</i><hr/>";
        for (String s : getCredits()) {
            if (s.contains("@")) {
                s = "<a href=\"mailto:" + s + "\">" + s + "</a><br>";
            }
            if (s.contains("jar")) {
                s = "<i>" + s + "</i>";
            }
            if (s.contains("http")) {
                sStr += "<a href=\"" + s + "\">" + s + "</a><br>";
            } else {
                sStr += "<p>" + s + "</p>";
            }
        }
        return sStr;
    }

    public String getMajorVersion() {
        return VERSION.substring(0, VERSION.lastIndexOf("."));
    }
}
