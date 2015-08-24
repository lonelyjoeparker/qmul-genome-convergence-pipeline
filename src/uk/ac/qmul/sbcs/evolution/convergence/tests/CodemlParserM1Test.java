/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.util.ArrayList;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class CodemlParserM1Test extends TestCase {

	CodemlParserM1 parser;
	/**
	 * @param name
	 */
	public CodemlParserM1Test(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		parser = new CodemlParserM1(this.setUpData());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1#toString()}.
	 */
	public final void testToString() {
		String retStr = parser.toString();
		System.out.println(retStr);
		if(retStr.charAt(0) != 'M'){fail();}
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1#guessWhichModel()}.
	 */
	public final void testGuessWhichModel() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1#getModelData()}.
	 */
	public final void testGetModelData() {
		CodemlModel model = parser.getModelData();
		if(model == null){
			fail("null CodemlModel");
		}
		System.out.println(model.getLnL());
		if(Float.isNaN(model.getLnL())){
			fail("no valid lnL");
		}
	}

	/**
	 * Test method for {@link uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParserM1#CodemlParserM1(java.util.ArrayList)}.
	 */
	public final void testCodemlParserM1() {
		fail("Not yet implemented"); // TODO
	}

	public final void testRegression(){
		CodemlModel model = parser.getModelData();
		try{
			model.getIntervalsRegression().getRsq();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	private ArrayList<String> setUpData(){
		ArrayList<String> data = new ArrayList<String>();
		data.add("Model 1: NearlyNeutral");
		data.add("dN/dS (w) for site classes (K=2)");
		data.add("");
		data.add("p:   0.73116  0.26884");
		data.add("w:   0.00000  1.00000");
		data.add("");
		data.add("Naive Empirical Bayes (NEB) probabilities for 2 classes & postmean_w");
		data.add("(amino acids refer to 1st sequence: HIPPOSIDEROS)");
		data.add("");
		data.add("   1 -   0.75743 0.24257 ( 1)  0.243");
		data.add("   2 -   0.00000 1.00000 ( 2)  1.000");
		data.add("   3 -   0.76103 0.23897 ( 1)  0.239");
		data.add("   4 -   0.00000 1.00000 ( 2)  1.000");
		data.add("   5 -   0.80779 0.19221 ( 1)  0.192");
		data.add("   6 -   0.81146 0.18854 ( 1)  0.189");
		data.add("   7 -   0.81146 0.18854 ( 1)  0.189");
		data.add("   8 -   0.76845 0.23155 ( 1)  0.232");
		data.add("   9 -   0.81146 0.18854 ( 1)  0.189");
		data.add("  10 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  11 -   0.75490 0.24510 ( 1)  0.245");
		data.add("  12 -   0.81836 0.18164 ( 1)  0.182");
		data.add("  13 -   0.81265 0.18735 ( 1)  0.187");
		data.add("  14 -   0.83971 0.16029 ( 1)  0.160");
		data.add("  15 -   0.81146 0.18854 ( 1)  0.189");
		data.add("  16 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  17 -   0.76262 0.23738 ( 1)  0.237");
		data.add("  18 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  19 -   0.83622 0.16378 ( 1)  0.164");
		data.add("  20 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  21 -   0.83296 0.16704 ( 1)  0.167");
		data.add("  22 -   0.76183 0.23817 ( 1)  0.238");
		data.add("  23 -   0.80470 0.19530 ( 1)  0.195");
		data.add("  24 -   0.80724 0.19276 ( 1)  0.193");
		data.add("  25 -   0.80262 0.19738 ( 1)  0.197");
		data.add("  26 -   0.83622 0.16378 ( 1)  0.164");
		data.add("  27 -   0.83296 0.16704 ( 1)  0.167");
		data.add("  28 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  29 -   0.81265 0.18735 ( 1)  0.187");
		data.add("  30 -   0.76320 0.23680 ( 1)  0.237");
		data.add("  31 -   0.75892 0.24108 ( 1)  0.241");
		data.add("  32 -   0.76320 0.23680 ( 1)  0.237");
		data.add("  33 -   0.76262 0.23738 ( 1)  0.237");
		data.add("  34 -   0.81015 0.18985 ( 1)  0.190");
		data.add("  35 -   0.75669 0.24331 ( 1)  0.243");
		data.add("  36 -   0.81836 0.18164 ( 1)  0.182");
		data.add("  37 -   0.79685 0.20315 ( 1)  0.203");
		data.add("  38 -   0.81146 0.18854 ( 1)  0.189");
		data.add("  39 -   0.83622 0.16378 ( 1)  0.164");
		data.add("  40 -   0.00001 0.99999 ( 2)  1.000");
		data.add("  41 -   0.74409 0.25591 ( 1)  0.256");
		data.add("  42 -   0.77571 0.22429 ( 1)  0.224");
		data.add("  43 -   0.81809 0.18191 ( 1)  0.182");
		data.add("  44 -   0.79685 0.20315 ( 1)  0.203");
		data.add("  45 -   0.80262 0.19738 ( 1)  0.197");
		data.add("  46 -   0.75490 0.24510 ( 1)  0.245");
		data.add("  47 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  48 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  49 -   0.83296 0.16704 ( 1)  0.167");
		data.add("  50 -   0.80470 0.19530 ( 1)  0.195");
		data.add("  51 -   0.83692 0.16308 ( 1)  0.163");
		data.add("  52 -   0.80262 0.19738 ( 1)  0.197");
		data.add("  53 -   0.75431 0.24569 ( 1)  0.246");
		data.add("  54 -   0.00000 1.00000 ( 2)  1.000");
		data.add("  55 -   0.76183 0.23817 ( 1)  0.238");
		data.add("  56 -   0.75596 0.24404 ( 1)  0.244");
		data.add("  57 -   0.75832 0.24168 ( 1)  0.242");
		data.add("  58 -   0.81265 0.18735 ( 1)  0.187");
		data.add("  59 -   0.75892 0.24108 ( 1)  0.241");
		data.add("  60 -   0.75216 0.24784 ( 1)  0.248");
		data.add("  61 -   0.80779 0.19221 ( 1)  0.192");
		data.add("  62 -   0.80226 0.19774 ( 1)  0.198");
		data.add("  63 -   0.80262 0.19738 ( 1)  0.197");
		data.add("  64 -   0.83296 0.16704 ( 1)  0.167");
		data.add("  65 -   0.75596 0.24404 ( 1)  0.244");
		data.add("  66 -   0.80047 0.19953 ( 1)  0.200");
		data.add("  67 -   0.75892 0.24108 ( 1)  0.241");
		data.add("  68 -   0.00001 0.99999 ( 2)  1.000");
		data.add("  69 -   0.83622 0.16378 ( 1)  0.164");
		data.add("  70 -   0.76320 0.23680 ( 1)  0.237");
		data.add("  71 -   0.76183 0.23817 ( 1)  0.238");
		data.add("  72 -   0.76442 0.23558 ( 1)  0.236");
		data.add("  73 -   0.79685 0.20315 ( 1)  0.203");
		data.add("  74 -   0.75892 0.24108 ( 1)  0.241");
		data.add("  75 -   0.81146 0.18854 ( 1)  0.189");
		data.add("  76 -   0.81146 0.18854 ( 1)  0.189");
		data.add("  77 -   0.79413 0.20587 ( 1)  0.206");
		data.add("  78 -   0.83296 0.16704 ( 1)  0.167");
		data.add("  79 L   0.79200 0.20800 ( 1)  0.208");
		data.add("  80 L   0.84534 0.15466 ( 1)  0.155");
		data.add("  81 R   0.77698 0.22302 ( 1)  0.223");
		data.add("  82 G   0.00001 0.99999 ( 2)  1.000");
		data.add("  83 S   0.82622 0.17378 ( 1)  0.174");
		data.add("  84 A   0.76402 0.23598 ( 1)  0.236");
		data.add("  85 L   0.84534 0.15466 ( 1)  0.155");
		data.add("  86 W   0.83765 0.16235 ( 1)  0.162");
		data.add("  87 S   0.83903 0.16097 ( 1)  0.161");
		data.add("  88 W   0.83765 0.16235 ( 1)  0.162");
		data.add("  89 A   0.82852 0.17148 ( 1)  0.171");
		data.add("  90 P   0.00000 1.00000 ( 2)  1.000");
		data.add("  91 G   0.76527 0.23473 ( 1)  0.235");
		data.add("  92 P   0.81871 0.18129 ( 1)  0.181");
		data.add("  93 A   0.77342 0.22658 ( 1)  0.227");
		data.add("  94 G   0.86608 0.13392 ( 1)  0.134");
		data.add("  95 R   0.00001 0.99999 ( 2)  1.000");
		data.add("  96 S   0.83903 0.16097 ( 1)  0.161");
		data.add("  97 W   0.83765 0.16235 ( 1)  0.162");
		data.add("  98 H   0.77493 0.22507 ( 1)  0.225");
		data.add("  99 P   0.76028 0.23972 ( 1)  0.240");
		data.add(" 100 G   0.76527 0.23473 ( 1)  0.235");
		data.add(" 101 P   0.76867 0.23133 ( 1)  0.231");
		data.add(" 102 A   0.82757 0.17243 ( 1)  0.172");
		data.add(" 103 R   0.86179 0.13821 ( 1)  0.138");
		data.add(" 104 S   0.83801 0.16199 ( 1)  0.162");
		data.add(" 105 I   0.82431 0.17569 ( 1)  0.176");
		data.add(" 106 A   0.87925 0.12075 ( 1)  0.121");
		data.add(" 107 V   0.88547 0.11453 ( 1)  0.115");
		data.add(" 108 T   0.88600 0.11400 ( 1)  0.114");
		data.add(" 109 R   0.88126 0.11874 ( 1)  0.119");
		data.add(" 110 T   0.77181 0.22819 ( 1)  0.228");
		data.add(" 111 L   0.78844 0.21156 ( 1)  0.212");
		data.add(" 112 V   0.88547 0.11453 ( 1)  0.115");
		data.add(" 113 P   0.00000 1.00000 ( 2)  1.000");
		data.add(" 114 E   0.79162 0.20838 ( 1)  0.208");
		data.add(" 115 V   0.87116 0.12884 ( 1)  0.129");
		data.add(" 116 Q   0.86252 0.13748 ( 1)  0.137");
		data.add(" 117 L   0.84239 0.15761 ( 1)  0.158");
		data.add(" 118 A   0.84394 0.15606 ( 1)  0.156");
		data.add(" 119 Q   0.86252 0.13748 ( 1)  0.137");
		data.add(" 120 P   0.00001 0.99999 ( 2)  1.000");
		data.add(" 121 L   0.84239 0.15761 ( 1)  0.158");
		data.add(" 122 S   0.78241 0.21759 ( 1)  0.218");
		data.add(" 123 A   0.84394 0.15606 ( 1)  0.156");
		data.add(" 124 E   0.88931 0.11069 ( 1)  0.111");
		data.add(" 125 P   0.83299 0.16701 ( 1)  0.167");
		data.add(" 126 S   0.77654 0.22346 ( 1)  0.223");
		data.add(" 127 V   0.88547 0.11453 ( 1)  0.115");
		data.add(" 128 W   0.85391 0.14609 ( 1)  0.146");
		data.add(" 129 T   0.77468 0.22532 ( 1)  0.225");
		data.add(" 130 A   0.78106 0.21894 ( 1)  0.219");
		data.add(" 131 L   0.86236 0.13764 ( 1)  0.138");
		data.add(" 132 A   0.84394 0.15606 ( 1)  0.156");
		data.add(" 133 L   0.00000 1.00000 ( 2)  1.000");
		data.add(" 134 G   0.78139 0.21861 ( 1)  0.219");
		data.add(" 135 A   0.84394 0.15606 ( 1)  0.156");
		data.add(" 136 P   0.83299 0.16701 ( 1)  0.167");
		data.add(" 137 T   0.00000 1.00000 ( 2)  1.000");
		data.add(" 138 G   0.84764 0.15236 ( 1)  0.152");
		data.add(" 139 S   0.76868 0.23132 ( 1)  0.231");
		data.add(" 140 T   0.84863 0.15137 ( 1)  0.151");
		data.add(" 141 T   0.00001 0.99999 ( 2)  1.000");
		data.add(" 142 C   0.84189 0.15811 ( 1)  0.158");
		data.add(" 143 -   0.81442 0.18558 ( 1)  0.186");
		data.add(" 144 -   0.81500 0.18500 ( 1)  0.185");
		data.add(" 145 -   0.82318 0.17682 ( 1)  0.177");
		data.add(" 146 -   0.78024 0.21976 ( 1)  0.220");
		data.add(" 147 -   0.84895 0.15105 ( 1)  0.151");
		data.add(" 148 -   0.84089 0.15911 ( 1)  0.159");
		data.add(" 149 -   0.81507 0.18493 ( 1)  0.185");
		data.add(" 150 -   0.83121 0.16879 ( 1)  0.169");
		data.add(" 151 -   0.82318 0.17682 ( 1)  0.177");
		data.add(" 152 -   0.82603 0.17397 ( 1)  0.174");
		data.add(" 153 -   0.82603 0.17397 ( 1)  0.174");
		data.add(" 154 -   0.84123 0.15877 ( 1)  0.159");
		data.add(" 155 -   0.84534 0.15466 ( 1)  0.155");
		data.add(" 156 -   0.84895 0.15105 ( 1)  0.151");
		data.add(" 157 -   0.76319 0.23681 ( 1)  0.237");
		data.add(" 158 -   0.84895 0.15105 ( 1)  0.151");
		data.add(" 159 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 160 -   0.82603 0.17397 ( 1)  0.174");
		data.add(" 161 -   0.84184 0.15816 ( 1)  0.158");
		data.add(" 162 -   0.82467 0.17533 ( 1)  0.175");
		data.add(" 163 -   0.84895 0.15105 ( 1)  0.151");
		data.add(" 164 -   0.84123 0.15877 ( 1)  0.159");
		data.add(" 165 -   0.83121 0.16879 ( 1)  0.169");
		data.add(" 166 -   0.80708 0.19292 ( 1)  0.193");
		data.add(" 167 -   0.75939 0.24061 ( 1)  0.241");
		data.add(" 168 -   0.84895 0.15105 ( 1)  0.151");
		data.add(" 169 -   0.75786 0.24214 ( 1)  0.242");
		data.add(" 170 -   0.85061 0.14939 ( 1)  0.149");
		data.add(" 171 -   0.82394 0.17606 ( 1)  0.176");
		data.add(" 172 -   0.82772 0.17228 ( 1)  0.172");
		data.add(" 173 -   0.85061 0.14939 ( 1)  0.149");
		data.add(" 174 -   0.82745 0.17255 ( 1)  0.173");
		data.add(" 175 -   0.83269 0.16731 ( 1)  0.167");
		data.add(" 176 -   0.76560 0.23440 ( 1)  0.234");
		data.add(" 177 -   0.84247 0.15753 ( 1)  0.158");
		data.add(" 178 -   0.82689 0.17311 ( 1)  0.173");
		data.add(" 179 -   0.84259 0.15741 ( 1)  0.157");
		data.add(" 180 -   0.81287 0.18713 ( 1)  0.187");
		data.add(" 181 -   0.80430 0.19570 ( 1)  0.196");
		data.add(" 182 -   0.81070 0.18930 ( 1)  0.189");
		data.add(" 183 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 184 -   0.81571 0.18429 ( 1)  0.184");
		data.add(" 185 -   0.80118 0.19882 ( 1)  0.199");
		data.add(" 186 -   0.84529 0.15471 ( 1)  0.155");
		data.add(" 187 -   0.81887 0.18113 ( 1)  0.181");
		data.add(" 188 -   0.82160 0.17840 ( 1)  0.178");
		data.add(" 189 -   0.82772 0.17228 ( 1)  0.172");
		data.add(" 190 -   0.82910 0.17090 ( 1)  0.171");
		data.add(" 191 -   0.00001 0.99999 ( 2)  1.000");
		data.add(" 192 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 193 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 194 -   0.79872 0.20128 ( 1)  0.201");
		data.add(" 195 -   0.81247 0.18753 ( 1)  0.188");
		data.add(" 196 -   0.75870 0.24130 ( 1)  0.241");
		data.add(" 197 -   0.82500 0.17500 ( 1)  0.175");
		data.add(" 198 -   0.81936 0.18064 ( 1)  0.181");
		data.add(" 199 -   0.76003 0.23997 ( 1)  0.240");
		data.add(" 200 -   0.74888 0.25112 ( 1)  0.251");
		data.add(" 201 -   0.79872 0.20128 ( 1)  0.201");
		data.add(" 202 -   0.81245 0.18755 ( 1)  0.188");
		data.add(" 203 -   0.82689 0.17311 ( 1)  0.173");
		data.add(" 204 -   0.82274 0.17726 ( 1)  0.177");
		data.add(" 205 -   0.82263 0.17737 ( 1)  0.177");
		data.add(" 206 -   0.84619 0.15381 ( 1)  0.154");
		data.add(" 207 -   0.82977 0.17023 ( 1)  0.170");
		data.add(" 208 -   0.85222 0.14778 ( 1)  0.148");
		data.add(" 209 -   0.81641 0.18359 ( 1)  0.184");
		data.add(" 210 -   0.76030 0.23970 ( 1)  0.240");
		data.add(" 211 -   0.76171 0.23829 ( 1)  0.238");
		data.add(" 212 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 213 -   0.77881 0.22119 ( 1)  0.221");
		data.add(" 214 -   0.76191 0.23809 ( 1)  0.238");
		data.add(" 215 -   0.82114 0.17886 ( 1)  0.179");
		data.add(" 216 -   0.82882 0.17118 ( 1)  0.171");
		data.add(" 217 -   0.76994 0.23006 ( 1)  0.230");
		data.add(" 218 -   0.82462 0.17538 ( 1)  0.175");
		data.add(" 219 -   0.81603 0.18397 ( 1)  0.184");
		data.add(" 220 -   0.84619 0.15381 ( 1)  0.154");
		data.add(" 221 -   0.79794 0.20206 ( 1)  0.202");
		data.add(" 222 -   0.82112 0.17888 ( 1)  0.179");
		data.add(" 223 -   0.82387 0.17613 ( 1)  0.176");
		data.add(" 224 -   0.81252 0.18748 ( 1)  0.187");
		data.add(" 225 -   0.79819 0.20181 ( 1)  0.202");
		data.add(" 226 -   0.82943 0.17057 ( 1)  0.171");
		data.add(" 227 -   0.82710 0.17290 ( 1)  0.173");
		data.add(" 228 -   0.81505 0.18495 ( 1)  0.185");
		data.add(" 229 -   0.75570 0.24430 ( 1)  0.244");
		data.add(" 230 -   0.84619 0.15381 ( 1)  0.154");
		data.add(" 231 -   0.82154 0.17846 ( 1)  0.178");
		data.add(" 232 -   0.79103 0.20897 ( 1)  0.209");
		data.add(" 233 -   0.80945 0.19055 ( 1)  0.191");
		data.add(" 234 -   0.78079 0.21921 ( 1)  0.219");
		data.add(" 235 -   0.83792 0.16208 ( 1)  0.162");
		data.add(" 236 -   0.82019 0.17981 ( 1)  0.180");
		data.add(" 237 -   0.80804 0.19196 ( 1)  0.192");
		data.add(" 238 -   0.82883 0.17117 ( 1)  0.171");
		data.add(" 239 -   0.81409 0.18591 ( 1)  0.186");
		data.add(" 240 -   0.85222 0.14778 ( 1)  0.148");
		data.add(" 241 -   0.00000 1.00000 ( 2)  1.000");
		data.add(" 242 -   0.83956 0.16044 ( 1)  0.160");
		data.add(" 243 -   0.82910 0.17090 ( 1)  0.171");
		data.add(" 244 -   0.77376 0.22624 ( 1)  0.226");
		data.add(" 245 -   0.85222 0.14778 ( 1)  0.148");
		data.add(" 246 -   0.85222 0.14778 ( 1)  0.148");
		data.add(" 247 -   0.83360 0.16640 ( 1)  0.166");
		data.add(" 248 -   0.81245 0.18755 ( 1)  0.188");
		data.add(" 249 -   0.82291 0.17709 ( 1)  0.177");
		data.add(" 250 -   0.83348 0.16652 ( 1)  0.167");
		data.add(" 251 -   0.80944 0.19056 ( 1)  0.191");
		data.add(" 252 -   0.82883 0.17117 ( 1)  0.171");
		data.add(" 253 -   0.75660 0.24340 ( 1)  0.243");
		data.add(" 254 -   0.82447 0.17553 ( 1)  0.176");
		data.add(" 255 -   0.82291 0.17709 ( 1)  0.177");
		data.add(" 256 -   0.81878 0.18122 ( 1)  0.181");
		data.add(" 257 -   0.85222 0.14778 ( 1)  0.148");
		data.add(" 258 -   0.81754 0.18246 ( 1)  0.182");
		data.add(" 259 -   0.81051 0.18949 ( 1)  0.189");
		data.add(" 260 -   0.81409 0.18591 ( 1)  0.186");
		data.add(" 261 -   0.82291 0.17709 ( 1)  0.177");
		data.add(" 262 -   0.82883 0.17117 ( 1)  0.171");
		data.add(" 263 -   0.83414 0.16586 ( 1)  0.166");
		data.add(" 264 -   0.81409 0.18591 ( 1)  0.186");
		data.add(" 265 -   0.82910 0.17090 ( 1)  0.171");
		data.add(" 266 -   0.83914 0.16086 ( 1)  0.161");
		data.add(" 267 -   0.82481 0.17519 ( 1)  0.175");
		data.add(" 268 -   0.75517 0.24483 ( 1)  0.245");
		data.add(" 269 -   0.81593 0.18407 ( 1)  0.184");
		data.add("");
		data.add("");
		data.add("lnL = -1544.169025");
		data.add("");
		data.add("");
		return data;
	}
}
