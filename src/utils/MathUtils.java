package utils;

import java.math.BigInteger;
import java.util.Arrays;

public class MathUtils {
	public static double Log(double x,long base){
		return Math.log(x) / Math.log(base);
	}
	
	 public static double Median(double[] l)
	  {
	    Arrays.sort(l);
	    int middle = l.length / 2;
	    if (l.length % 2 == 0)
	    {
	      double left = l[middle - 1];
	      double right = l[middle];
	      return (left + right) / 2;
	    }
	    else
	    {
	      return l[middle];
	    }
	  }
	 
	 public static BigInteger factorial(int n)
	    {
	        BigInteger ret = BigInteger.ONE;
	        for (int i = 1; i <= n; ++i) ret = ret.multiply(BigInteger.valueOf(i));
	        return ret;
	    }
}
