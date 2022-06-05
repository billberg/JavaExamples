package com.github.billberg.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

	// SimpleDateFormat是线程不安全的，采用这种方式避免
	private static ThreadLocal<DateFormat> yyyyMMdd = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);
			return sdf;
		}
	};

	private static ThreadLocal<DateFormat> yyyyMMdd10 = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);
			return sdf;
		}
	};
	
	private static ThreadLocal<DateFormat> yyyyMMddSlash = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			//return new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			sdf.setLenient(false);
			return sdf;
		}
	};

	
	private static ThreadLocal<DateFormat> yyMMdd = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			return new SimpleDateFormat("yyMMdd");
		}
	};

	/*
	private static ThreadLocal<DateFormat> HHmmss = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			return new SimpleDateFormat("HHmmss");
		}
	};

	private static ThreadLocal<DateFormat> dateFormat14 = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}
	};
*/
	
	public static ThreadLocal<DateFormat> dateFormat18 = new ThreadLocal<DateFormat>() {
		@Override
		protected synchronized DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	
	public static Date yyyyMMdd(String strDate)  {
		try {
			return yyyyMMdd.get().parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("非法的日期格式", e);
		}
	}
	
	public static String yyyyMMdd(Date date)  {
		return yyyyMMdd.get().format(date);
	}
	
	public static Date yyyyMMdd10(String strDate)  {
		try {
			return yyyyMMdd10.get().parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("非法的日期格式", e);
		}
	}
	
	public static Date[] yyyyMMdd10(String[] date) {
		Date[] dateStr = new Date[date.length];
		for (int i = 0; i < date.length; i++)
			dateStr[i] = yyyyMMdd10(date[i]);
		
		return dateStr;
	}
	
	public static String yyyyMMdd10(Date date)  {
		return yyyyMMdd10.get().format(date);
	}
	
	public static Date yyyyMMddSlash(String strDate)  {
		try {
			return yyyyMMddSlash.get().parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("非法的日期格式", e);
		}
	}
	
	
	
	public static String yyyyMMddSlash(Date date)  {
		return yyyyMMddSlash.get().format(date);
	}
	
	
	public static Date getLastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	public static int getMonthsBetween(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);

		return (cal2.get(Calendar.YEAR)*12 - cal.get(Calendar.YEAR)*12 + cal2.get(Calendar.MONTH) - cal.get(Calendar.MONTH));
	}
	
	/**
	 * 如21-25之间间隔为4天，不是中间的3天
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysBetween(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);

		return (int)((cal2.getTimeInMillis() - cal.getTimeInMillis())/(1000*3600*24));
	}
	
	public static int getHoursBetween(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);

		return (int)((cal2.getTimeInMillis() - cal.getTimeInMillis())/(1000*3600));
	}
	
	//根据日期找到上一个最近的财报日
		public static Date getLastFinanceReportDate(Date date) {
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			
			int financeReportYear, financeReportMonth;
			if (month <= 3) {
				financeReportYear = year - 1;
				financeReportMonth = 12;
			} else {
				financeReportYear = year;
				financeReportMonth = 3*((month-1)/3);
			}
		
			cal.set(financeReportYear, financeReportMonth - 1, 1);
		    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			
			return cal.getTime();
		}
		
		
		public static Date getLastDayOfMonth(Date date) {
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(date);
			//int year = cal.get(Calendar.YEAR);
			//int month = cal.get(Calendar.MONTH);
			//cal.set(year, month, 1);
		    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			
			return cal.getTime();
		}
		
		public static Date getLastDayOfMonth(int year, int month) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month-1, 1);
		    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			
			return cal.getTime();
		}
		
		/**
		 * 
		 * @param date
		 * @param year
		 * @return
		 */
		public static Date addYear(Date date, int year) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.YEAR, year);
			Date fromDate = cal.getTime();
			
			return fromDate;
		}
		
		/**
		 * 
		 * @param date
		 * @param days 可以是负数
		 * @return
		 */
		public static Date addDays(Date date, int days) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, days);
			Date fromDate = cal.getTime();
			
			return fromDate;
		}
		
		/**
		 * 判断日期是否属于财报披露时间段
		 * 
		 * 		已披露的报表日期	下个报表日期		下个报表披露日期
				03-31			06-30			07-01～08-30(2Q)
				06-30			09-30			10-01～10-31(3Q)
				09-30			12-31			01-01～04-30(4Q)
				12-31			03-31			04-01～04-30(1Q)
		 * 
		 * @param date
		 * @return 财报披露时间段序号
		 */
		public static int rangeOfFinancePilu(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			//yyyy-01-01
			cal.set(year, 0, 1);
			Date yyyy0101 = cal.getTime();
			//yyyy-04-01
			cal.set(year, 3, 1);
			Date yyyy0401 = cal.getTime();
			//yyyy-04-30
			cal.set(year, 3, 30);
			Date yyyy0430 = cal.getTime();
			//yyyy-07-01
			cal.set(year, 6, 1);
			Date yyyy0701 = cal.getTime();
			//yyyy-08-30
			cal.set(year, 7, 30);
			Date yyyy0830 = cal.getTime();
			//yyyy-10-01
			cal.set(year, 9, 1);
			Date yyyy1001 = cal.getTime();
			//yyyy-10-31
			cal.set(year, 9, 31);
			Date yyyy1031 = cal.getTime();
			
			//-1表示日期不在财报披露时间段内
			int rangeIndex = -1;
		    if (date.compareTo(yyyy0101) >= 0 && date.compareTo(yyyy0430) <= 0) {
		    	rangeIndex = 4;
		    }
		    if (date.compareTo(yyyy0401) >= 0 && date.compareTo(yyyy0430) <= 0) {
		    	rangeIndex = 1;
		    } else if (date.compareTo(yyyy0701) >= 0 && date.compareTo(yyyy0830) <= 0) {
		    	rangeIndex = 2;
		    } else if (date.compareTo(yyyy1001) >= 0 && date.compareTo(yyyy1031) <= 0) {
		    	rangeIndex = 3;
		    }
		    
			return rangeIndex;
		}
		
		
		public static int getYear(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			return year;
		}
		
		/**
		 * 1~12
		 * @param date
		 * @return
		 */
		public static int getMonth(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			return month;
		}
		
		/**
		 * 1~7
		 * @param date
		 * @return
		 */
		public static int getDayOfWeek(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
			if (dayOfWeek == 0)
				dayOfWeek = 7;
			
			return dayOfWeek;
		}
		
		
		/**
		 * 	报表日期			规定披露日期			假设披露日期（实际披露日期缺失时使用）
			03-31			04-01～04-30			04-30
			06-30			07-01～08-30			08-30
			09-30			10-01～10-31			10-31
			12-31			01-01～04-30			03-31
		 * @param financeReportDate
		 * @return
		 */
		public static Date getFinanceHypoAnnounceDate(Date financeReportDate) {
			boolean isValid = validateFinanceReportDate(financeReportDate);
			if (!isValid)
				throw new RuntimeException("无效的财报日: " + yyyyMMdd(financeReportDate));
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(financeReportDate);
			int month = cal.get(Calendar.MONTH);
			if ((month+1) == 3) {
				cal.set(Calendar.MONTH, month+1);
				cal.set(Calendar.DAY_OF_MONTH,30);
				return cal.getTime();
			} else if ((month+1) == 6) {
				cal.set(Calendar.MONTH, month+2);
				cal.set(Calendar.DAY_OF_MONTH,30);
				return cal.getTime();
			} else if ((month+1) == 9) {
				cal.set(Calendar.MONTH, month+1);
				cal.set(Calendar.DAY_OF_MONTH,31);
				return cal.getTime();
			} else if ((month+1) == 12) {
				cal.set(Calendar.MONTH, month+3);
				cal.set(Calendar.DAY_OF_MONTH,31);
				return cal.getTime();
			}
			
			return null;
		}
		
		public static boolean validateFinanceReportDate(Date financeReportDate) {
			if (yyyyMMdd(financeReportDate).endsWith("0331") 
					|| yyyyMMdd(financeReportDate).endsWith("0630")
					|| yyyyMMdd(financeReportDate).endsWith("0930")
					|| yyyyMMdd(financeReportDate).endsWith("1231"))
				return true;
			
			return false;
		}
		
		/**
		 * 
		 * @param dates
		 * @return
		 */
		public static int max(List<Date> dates) {
			Date d = dates.get(0);
			int index = 0;
			for (int i = 1; i < dates.size(); i++) {
				if (dates.get(i).after(d)) {
					d = dates.get(i);
					index = i;
				}
			}
			
			return index;
		}
		
		public static String[] yyyyMMdd(Date[] date) {
			String[] dateStr = new String[date.length];
			for (int i = 0; i < date.length; i++)
				dateStr[i] = yyyyMMdd(date[i]);
			
			return dateStr;
		}
		
		public static String[] yyyyMMdd10(Date[] date) {
			String[] dateStr = new String[date.length];
			for (int i = 0; i < date.length; i++)
				dateStr[i] = yyyyMMdd10(date[i]);
			
			return dateStr;
		}
		
		/**
		 * 默认日期输出格式
		 * 
		 * @param date
		 * @return
		 */
		public static String toString(Date date) {
			return yyyyMMdd.get().format(date);
		}
		
		
		public static String toString(Date[] dateArray) {
			if (dateArray == null)
				return "null";

		    int iMax = dateArray.length - 1;
		    if (iMax == -1)
		        return "[]";

		    StringBuilder b = new StringBuilder();
		    b.append('[');
		    //这样处理比使用for (int i = 0; i < iMax; i++)
		    //后面再添加最后一个元素性能可能更高？i < iMax 每次也要做比较计算
		    for (int i = 0; ; i++) {
		        b.append(toString(dateArray[i]));
		        if (i == iMax)
		            return b.append(']').toString();
		        b.append(", ");
		    }
		}
		
		public static String toString(List<Date> dateList) {
			Date[] dateArray = dateList.toArray(new Date[0]);
			return toString(dateArray);
		}
}
