package com.github.billberg.utils;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * TODO 弃用使用了包装类的方法，包装类转换成基础类型再计算
 * @author DiffWind
 *
 */
public class Functions {

	/**
	 * 简单收益率simR: 适用于对不同时间序列的收益求和计算（此时simR使用加法）
	 * 公式: 1+simR = d2/d1
	 * 		d2 = d1*(1+simR)
	 * 
	 * 举例: 
	 * 		StockA: 1.05 = 1*(1+0.05)
	 * 		StockB: 0.95 = 1*(1-0.05)
	 * 		 收益和: 0
	 * @param d2 时间序列上后发生的数
	 * @param d1 时间序列上先发生的数
	 * @return
	 */
	public static double simR(double d2, double d1) {
		return (d2-d1)/d1;		
	}
	
	
	/**
	 * 对数收益率logR: 适用于对同一个时间序列的累计收益计算（此时logR使用加法）
	 * 公式: logR = log(1+simR) = log(ts2) - log(ts1)
	 * 		ts2 = ts1*exp(logR)
	 * 关系: simR = exp(logR) - 1
	 * 
	 * 举例: 
	 * 		StockA T1: 2 = 1*exp(0.6931)
	 * 		StockA T2: 1 = 1*exp(0.6931)*exp(-0.6931) = 1*exp(0)
	 * 		 收益和: 0
	 * 
	 * @param d2 时间序列上后发生的数
	 * @param d1 时间序列上先发生的数
	 * @return
	 */
	public static double logR(double d2, double d1) {
		return Math.log(d2/d1);		
	}
	
	public static double simR2logR(double simR) {
		return Math.log(simR+1);
	}
	
	public static double logR2simR(double logR) {
		return Math.pow(Math.E, logR) - 1;
	}
	
	
	/**
	 * 有意义?
	 * 根据长窗口收益计算微分窗口收益
	 * @param longW
	 * @param longWChg
	 * @param shortW
	 * @return
	 */
	public static double calcDiffWindReturn(int longW, double longWChg, int shortW) {
		//先计算日对数收益，再计算shortW窗口收益
		double day_r = Functions.simR2logR(longWChg)/longW;
		double shortW_r = shortW*day_r;
		double shortW_R = Functions.logR2simR(shortW_r);
		return 100*shortW_R;
	}
		
	/**
	 * 对数收益率, 时间序列倒序
	 * 添加最后一个收益率为0，使收益率序列长度与原序列长度一致，不影响未来窗口累计收益计算
	 * 
	 * @param ts
	 * @return
	 */
	public static double[] logR(double[] ts, int order) {
		double[] logR = new double[ts.length];
		if (order == -1) {
			logR[ts.length-1] = 0;
			for (int i = 0; i < ts.length-1; i++)
				logR[i] = Math.log(ts[i]/ts[i+1]);
		} else {
			throw new RuntimeException("未实现");
		}
			
		return logR;		
	}
	
	/**
	 * 
	 * @param ts
	 * @param order
	 * @return
	 */
	public static float[] logR(float[] ts, int order) {
		float[] logR = new float[ts.length];
		if (order == -1) {
			logR[ts.length-1] = 0;
			for (int i = 0; i < ts.length-1; i++)
				logR[i] = (float) Math.log(ts[i]/ts[i+1]);
		} else {
			throw new RuntimeException("未实现");
		}
			
		return logR;		
	}
	
	
	/**
	 * 
	 * @param ts
	 * @param order
	 * @return 长度为ts.length-1
	 */
	public static float[] simR(float[] ts, int order) {
		float[] simR = new float[ts.length-1];
		if (order == -1) {
			for (int i = 0; i < ts.length-1; i++)
				simR[i] = ts[i]/ts[i+1] - 1;
		} else {
			throw new RuntimeException("未实现");
		}
			
		return simR;		
	}
	
	
	/**
	 * 
	 * @param ts
	 * @param order
	 * @return 长度为ts.length-1
	 */
	public static double[] simR(double[] ts, int order) {
		double[] simR = new double[ts.length-1];
		if (order == -1) {
			for (int i = 0; i < ts.length-1; i++)
				simR[i] = ts[i]/ts[i+1] - 1;
		} else {
			throw new RuntimeException("未实现");
		}
			
		return simR;		
	}
	
	/**
	 * 
	 * @param ts
	 * @param order
	 * @return 长度为ts.length-1
	 */
	public static Double[] simR(Double[] ts, int order) {
		Double[] simR = new Double[ts.length-1];
		if (order == -1) {
			for (int i = 0; i < ts.length-1; i++) {
				if (ts[i] == null || ts[i+1] == null)
					break;
				
				simR[i] = ts[i]/ts[i+1] - 1;
			}
		} else {
			throw new RuntimeException("未实现");
		}
			
		return simR;		
	}
	
	/**
	 * TODO 重构，移到DoubleUtil
	 * @param ts
	 * @param d
	 * @return
	 */
	public static double[] multiply(double[] ts, double d) {
		double[] result = new double[ts.length];
		for (int i = 0; i < ts.length; i++)
			result[i] = ts[i]*d;
			
		return result;		
	}
	
	/**
	 * TODO 重构，移到DoubleUtil
	 * @param ts
	 * @param d
	 * @return
	 */
	public static float[] multiply(float[] ts, float d) {
		float[] result = new float[ts.length];
		for (int i = 0; i < ts.length; i++)
			result[i] = ts[i]*d;
			
		return result;		
	}
	
	public static double[] cumsum(double[] ts, int order) {
		double[] result = new double[ts.length];
		double cumsum = 0;
		if (order == -1) {
			for (int i = ts.length-1; i >= 0; i--) {
				cumsum = cumsum + ts[i];
				result[i] = cumsum;
			}
		} else {
			throw new RuntimeException("未实现");
		}
			
		return result;		
	}
	
	//必须保证不丢失信息
	public static double[] cumsub(double[] ts, int order) {
		double[] result = new double[ts.length];
		double cumsub = 0;
		if (order == 1) {
			for (int i = 0; i < ts.length; i++) {
				cumsub = cumsub - ts[i];
				result[i] = cumsub;
			}
		} else {
			throw new RuntimeException("未实现");
		}
			
		return result;		
	}
	
	/**
	 * 从0开始求累积和
	 * @param ts
	 * @param order
	 * @return 比原序列长度加1
	 */
	public static double[] cumsum0(double[] ts, int order) {
		double[] result = new double[ts.length+1];
		double cumsum = 0;
		if (order == -1) {
			result[ts.length] = 0;
			for (int i = ts.length-1; i >= 0; i--) {
				cumsum = cumsum + ts[i];
				result[i] = cumsum;
			}
		} else {
			throw new RuntimeException("未实现");
		}
			
		return result;		
	}
	
	/**
	 * 从0开始求累积差
	 * @param ts
	 * @param order
	 * @return
	 */
	public static double[] cumsub0(double[] ts, int order) {
		double[] result = new double[ts.length+1];
		double cumsub = 0;
		if (order == 1) {
			result[0] = 0;
			for (int i = 0; i < ts.length; i++) {
				cumsub = cumsub - ts[i];
				result[i+1] = cumsub;
			}
		} else {
			throw new RuntimeException("未实现");
		}
			
		return result;		
	}
	
	
	/*
	public static double[] logR(double[] ts) {
		double[] r = new double[ts.length-1];
		for (int i = 0; i < ts.length-1; i++)
			r[i] = Math.log(ts[i]/ts[i+1]);
			
		return r;		
	}
	*/
	
	/**
	 * 查找最大值所在序号
	 * 
	 * @param arr
	 * @param startIndex 数组开始下标，包含
	 * @param endIndex 数组结束下标，不包含
	 * @return
	 */
	public static int whichMax(double[] arr, int startIndex, int endIndex) {
		//assert (startIndex >= 0 && startIndex < endIndex && endIndex <= arr.length);
		if (!(startIndex >= 0 && startIndex < endIndex && endIndex <= arr.length)) {
			return -1;
		}
		
		int pos = startIndex;
		for (int i = startIndex+1; i < endIndex; i++) {
			if (arr[i] > arr[pos]) {
				pos = i;
			} 
		}
		
		return pos;
	}
	
	/**
	 * 查找最小值所在序号
	 * 
	 * @param arr
	 * @param startIndex 数组开始下标，包含
	 * @param endIndex 数组结束下标，不包含
	 * @return
	 */
	public static int whichMin(double[] arr, int startIndex, int endIndex) {
		//assert (startIndex >= 0 && startIndex < endIndex && endIndex <= arr.length);
		if (!(startIndex >= 0 && startIndex < endIndex && endIndex <= arr.length)) {
			return -1;
		}
		
		int pos = startIndex;
		for (int i = startIndex+1; i < endIndex; i++) {
			if (arr[i] < arr[pos]) {
				pos = i;
			} 
		}
		
		return pos;
	}
	
	public static double min(double[] arr) {
		if (DoubleUtil.checkHasNa(arr)) {
			return Double.NaN;
		}
		
		double minValue = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < minValue) {
				minValue = arr[i];
			} 
		}
		
		return minValue;
	}
	

	//TODO round代码移到DoubleUtil
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
	
	public static double[] round(double[] d) {
		double[] rd = new double[d.length];
		for (int i = 0; i < d.length; i++) {
			rd[i] = round(d[i]);
		}
		
		return rd;
	}
	
	public static boolean in(int x, int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (x == arr[i])
				return true;
		}
		
		return false;
	}
	
	
	public static double sum(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			if (!Double.isNaN(arr[i]))
			sum += arr[i];
		}
		return sum;
	}
	
	/**
	 * 
	 * @param arr
	 * @param from
	 * @param w
	 * @return
	 */
	public static double sum(double[] arr, int from, int w) {
		double sum = 0;
		for (int i = from; i < from+w && i < arr.length; i++) {
			if (!Double.isNaN(arr[i]))
				sum += arr[i];
		}
		return sum;
	}
	
	
	public static int sum(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	/**
	 * 百分位
	 * 
	 * @param ts
	 * @param x
	 * @return
	 */
	public static double percent(double[] ts, double x) {
		double percentile = Double.NaN;
		int pos = 0;
		for (int i = 0; i < ts.length; i++) {
			//double也有相等的时候
			if (ts[i] <= x) {
				pos++;
			}
		}
		
		//百分位
		percentile = pos/(double)ts.length;
		return percentile;
				
	}
	
	
	/**
	 * TODO 优化
	 * 百分位
	 * 
	 * @param ts
	 * @param from
	 * @param length 应满足from+length <= ts.length
	 * @param x
	 * @return
	 */
	public static double percent(double[] ts, int from, int length, double x) {
		double percent = Double.NaN;
		int pos = 0;
		int to = (ts.length < (from+length))? ts.length : (from+length);
		for (int i = from; i < to; i++) {
			//double也有相等的时候
			if (ts[i] <= x) {
				pos++;
			}
		}
		
		//百分位
		percent = pos/(double)(to - from);
		return percent;
				
	}
	
	
	
	
	
	/**
	 * 余弦相似度
	 * 
	 * @param v1
	 * @param v2
	 * @return 相似度[-1,1]
	 */
	public static double cosSim(double[] v1, double[] v2) {
		double inner = 0d, norm1 = 0d, norm2 = 0d;
		for (int i = 0; i < v1.length; i++) {
			inner += v1[i]*v2[i];
			norm1 += v1[i]*v1[i];
			norm2 += v2[i]*v2[i];
		}
		
		return inner/Math.sqrt(norm1*norm2);
	}
	
	/**
	 * 欧几里德相似度
	 * 
	 * @param v1
	 * @param v2
	 * @return 相似度(0,1]
	 */
	public static double eucSim(double[] v1, double[] v2) {
		double eucDist = 0d;
		for (int i = 0; i < v1.length; i++) {
			eucDist += (v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		
		return 1/(1+Math.sqrt(eucDist));
	}
	
	/**
	 * 标准化欧几里德相似度
	 * 两个向量统一标准化而不独立标准化，以不丢失量纲的差异
	 * 不处理Double.NAN，传入参数时应先补充缺失值
	 * 
	 * (2,3),(3,3)的相似度与(0.2,0.3),(0.3,0.3)的相似度相同，且相似度接近于1
	 * (1,1),(5,5)的相似度小于1
	 * 
	 * @param v1
	 * @param v2
	 * @return 相似度(0,1]
	 */
	public static double normEucSim(double[] v1, double[] v2) {
		double eucDist = 0d;
		//两个向量统一标准化而不独立标准化，以不丢失量纲的差异
		double norm1 = 0d;
		for (int i = 0; i < v1.length; i++) {
			norm1 += v1[i]*v1[i] + v2[i]*v2[i];
			eucDist += (v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		
		return 1/(1+Math.sqrt(eucDist/(norm1/2)));
	}
	
	
	/**
	 * 余弦-欧几里德相似度
	 * 不处理Double.NAN，传入参数时应先补充缺失值
	 * 
	 * @param v1
	 * @param v2
	 * @return 相似度(-1,1]
	 */
	public static double cosEucSim(double[] v1, double[] v2) {
		double cosSim = cosSim(v1,v2);
		double eucSim = normEucSim(v1,v2);
		
		double sim = 0d;
		if (cosSim >= 0)
			sim = cosSim*eucSim;
		else 
			sim = cosSim/Math.exp(eucSim);
		
		return sim;
	}
	
	
	/**
	 * TODO 未实现
	 * |x1/y1|*|x2/y2|
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double xxxSim(double[] v1, double[] v2) {
		double eucDist = 0d;
		double[] nv1 = new double[v1.length];
		double[] nv2 = new double[v1.length];
		double norm1 = 0d, norm2 = 0d;
		for (int i = 0; i < v1.length; i++) {
			norm1 += v1[i]*v1[i];
			norm2 += v2[i]*v2[i];
			eucDist += (v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		/*
		norm1 = Math.sqrt(norm1);
		norm2 = Math.sqrt(norm2);
		
		for (int i = 0; i < v1.length; i++) {
			nv1[i] = v1[i]/norm1;
			nv2[i] = v2[i]/norm2;
			eucDist += (nv1[i]-nv2[i])*(nv1[i]-nv2[i]);
		}
		*/
		
		return 1/(1+Math.sqrt(eucDist));
	}
	
	
	public static double[] abs(double[] d) {
		double[] ret = new double[d.length];
		for (int i = 0; i < d.length; i++) {
			ret[i] = Math.abs(d[i]);
		}
		
		return ret;
	}
	
	public static double[] log(double[] ts) {
		double[] log = new double[ts.length];
		for (int i = 0; i < ts.length; i++)
			log[i] = Math.log(ts[i]);
			
		return log;		
	}
	
}
