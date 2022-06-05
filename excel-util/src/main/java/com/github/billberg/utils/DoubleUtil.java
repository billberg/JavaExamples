package com.github.billberg.utils;

import java.math.BigDecimal;

/**
 * change to DoubleUtil?
 * @author zhangjz
 *
 */
public class DoubleUtil {
	public static boolean checkHasNa(Double... args) {
		for (Double d : args) {
			if (d == null || d.isNaN())
				return true;
		}
		
		return false;
	}
	
	public static boolean checkHasNa(double[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (Double.isNaN(arr[i]))
				return true;
		}
		
		return false;
	}
	
	
	public static boolean checkPositive(Double... args) {
		for (Double d : args) {
			if (d == null || d < 0d)
				return false;
		}
		
		return true;
	}
	
	
	public static Double ifNull2NaN(Double d) {
		if (d == null) 
			return Double.NaN;
		
		return d;
	}
	
	public static Double ifNull2Zero(Double d) {
		if (d == null || Double.isNaN(d)) 
			return 0d;
		
		return d;
	}
	
	
	public static float[] double2Float(double[] seq) {
		float[] result = new float[seq.length];
		for (int i = 0; i < seq.length; i++)
			result[i] = (float) seq[i];
		
		return result;
	}
	
	/**
	 * 创建数组并使用固定值初始化，相当于np.repeat
	 * @param d
	 * @param length
	 * @return
	 */
	public static double[] repeat(double d, int length) {
		double[] darray = new double[length];
		for (int i = 0; i < length; i++) {
			darray[i] = d;
		}
		return darray;
	}
	

	public static double round(double d) {
		if (Double.isNaN(d) || Double.isInfinite(d))
			return d;
		
		return new  BigDecimal(Double.toString(d)).setScale(2,java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double round(double d, int scale) {
		if (Double.isNaN(d) || Double.isInfinite(d))
			return d;
		
		return new  BigDecimal(Double.toString(d)).setScale(scale,java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
