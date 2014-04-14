package uk.ac.qmul.sbcs.evolution.convergence.util.stats;

/**
 * Class to carry out a linear regression
 * <br/>Copyright © 2000Ð2011, Robert Sedgewick and Kevin Wayne. 
 * <br/>Last updated: Wed Feb 9 09:20:16 EST 2011.
 *
 * <p><table>
 *  <tr><td><b>variable</b>		</td><td><b>regression parameter</b><td></tr>
 *  <tr><td>int N;		 		</td><td>#obs<td></tr>
 *  <tr><td>double xbar; 		</td><td>mean x<td></tr>
 *  <tr><td>double ybar; 		</td><td>mean y<td></tr>
 *  <tr><td>double Rsq;			</td><td>R-squared<td></tr>
 *  <tr><td>double beta0;		</td><td>intercept<td></tr>
 *  <tr><td>double beta0_lo;	</td><td>lower 95% interval of intercept estimate<td></tr>
 *  <tr><td>double beta0_hi;	</td><td>upper 95% interval of intercept estimate<td></tr>
 *  <tr><td>double beta1;		</td><td>slope<td></tr>
 *  <tr><td>double beta1_lo;	</td><td>lower 95% interval of slope estimate<td></tr>
 *  <tr><td>double beta1_hi;	</td><td>upper 95% interval of slope estimate<td></tr>
 *  <tr><td>double SStotal;<td></tr>
 *  <tr><td>double SSresiduals;<td></tr>
 *  <tr><td>double SSX;<td></tr>
 *  </table>    
 * @author Robert Sedgewick and Kevin Wayne, modified by <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 */
public class LinearRegression {
	double[] x;
	double[] y;
	int N;		 		// #obs
    double xbar; 		// mean x
    double ybar; 		// mean y
    double Rsq;			// R-squared
    double beta0;		// intercept
    double beta0_lo;	// lower 95% interval of intercept estimate
    double beta0_hi;	// upper 95% interval of intercept estimate
    double beta1;		// slope
    double beta1_lo;	// lower 95% interval of slope estimate
    double beta1_hi;	// upper 95% interval of slope estimate
    double SStotal;
    double SSresiduals;
    double SSX;
	
	/**
	 * No-arg constructor, deprecated
	 */
	@Deprecated
	public LinearRegression(){
		// @Deprecated
	}
	
	public LinearRegression(double[] a, double[] b){
		this.x = a;
		this.y = b;
		this.N = x.length;

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for(int n=0;n<N;n++) {
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
        }
        xbar = sumx / N;
        ybar = sumy / N;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < N; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        beta1 = xybar / xxbar;
        beta0 = ybar - beta1 * xbar;

        // print results
        //System.out.println("y   = " + beta1 + " * x + " + beta0);

        // analyze results
        int df = N - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < N; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        Rsq		   	 = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/N + xbar*xbar*svar1;
        //System.out.println("R^2                 = " + R2);
        //System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
        //System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (N * xxbar);
        //System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

        //System.out.println("SSTO = " + yybar);
        //System.out.println("SSE  = " + rss);
        //System.out.println("SSR  = " + ssr);
        
        SStotal     = yybar;
        SSresiduals = rss;
        SSX			= ssr;
        	
        double b_0_interval = Math.sqrt(svar0) * 1.96d;
        beta0_lo = beta0-b_0_interval;
        beta0_hi = beta0+b_0_interval;
        double b_1_interval = Math.sqrt(svar1) * 1.96d;
        beta1_lo = beta1-b_1_interval;
        beta1_hi = beta1+b_1_interval;
        
        //System.out.println("confint b0: "+beta0+" ("+(beta0-b_0_interval)+"; "+(beta0+b_0_interval)+")");
        //System.out.println("confint b1: "+beta1+" ("+(beta1-b_1_interval)+"; "+(beta1+b_1_interval)+")");
	}

	/**
	 * @return the x observations
	 */
	public double[] getX() {
		return x;
	}

	/**
	 * @return the y observations
	 */
	public double[] getY() {
		return y;
	}

	/**
	 * @return the number of observations
	 */
	public int getN() {
		return N;
	}

	/**
	 * @return the xbar (mean x)
	 */
	public double getXbar() {
		return xbar;
	}

	/**
	 * @return the ybar (mean y)
	 */
	public double getYbar() {
		return ybar;
	}

	/**
	 * @return the r-squared
	 */
	public double getRsq() {
		return Rsq;
	}

	/**
	 * @return the beta0 (y-intercept)
	 */
	public double getBeta0() {
		return beta0;
	}

	/**
	 * @return the beta0_lo (y-intercept lower 95% confidence interval)
	 */
	public double getBeta0_lo() {
		return beta0_lo;
	}

	/**
	 * @return the beta0_hi (y-intercept upper 95% confidence interval)
	 */
	public double getBeta0_hi() {
		return beta0_hi;
	}

	/**
	 * @return the beta1 (slope)
	 */
	public double getBeta1() {
		return beta1;
	}

	/**
	 * @return the beta1_lo (slope lower 95% confidence interval)
	 */
	public double getBeta1_lo() {
		return beta1_lo;
	}

	/**
	 * @return the beta1_hi (slope upper 95% confidence interval)
	 */
	public double getBeta1_hi() {
		return beta1_hi;
	}

	/**
	 * @return the SStotal (total SS)
	 */
	public double getSStotal() {
		return SStotal;
	}

	/**
	 * @return the SSresiduals (error SS)
	 */
	public double getSSresiduals() {
		return SSresiduals;
	}

	/**
	 * @return the SSX (observations SS)
	 */
	public double getSSX() {
		return SSX;
	}
}
