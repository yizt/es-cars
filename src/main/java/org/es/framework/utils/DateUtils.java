package org.es.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
  private static Logger log = LoggerFactory.getLogger(DateUtils.class);

  public static String yyyyMMdd = "yyyyMMdd";

  public static String yyyyMMddHHmmss = "yyyyMMddHHmmss";

  public static String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

  public static String yyyyMMddHHmmssForLoseTime = "yyyy-MM-dd HH:mm:ss";

  public static String yyyyMMddHHmmssSplit = "yyyy-MM-dd-HH-mm-ss";

  public static String yyyyNMMYddHHHmm = "yyyy年MM月dd号 HH时mm分";// 格式2011年4月20号4时23分

  public static String yyyyMMddSplit = "yyyy-MM-dd";

  public static String HHmm = "HHmm";

  public static String kkmm = "kkmm";

  public static String HHmmSplit = "HH:mm";

  public static String HHmmss = "HHmmss";

  public static String HHmmssSplit = "HH:mm:ss";

  public static String MMYddR = "MM月dd日";

  private static final String dateRegx = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

  /**
   * 1、验证是否是日期格式，日期字符串适用格式如下：<br>
   * 2009-01-01 12:13:12<br>
   * 2009-1-1 12:13:12<br>
   * 2009/01/01 12:13:12<br>
   * 2009/1/1 12:13:12<br>
   * 2009-01-01<br>
   * 2009/01/01<br>
   * 20091004/01<br>
   * ......<br>
   * <br>
   * 
   * 2、日期字符串错误格式如下：<br>
   * 2009/13/01<br>
   * 2009/01/01 24:59:59<br>
   * 20091234<br>
   * ......<br>
   * <br>
   * 
   * 3、例代码：<br>
   * DateUtils.isDate("20091004") &nbsp;&nbsp;输出true<br>
   * DateUtils.isDate("20091304") &nbsp;&nbsp;输出false<br>
   * 
   * @param dateStr
   *          日期字符串<br>
   * @return true：是日期格式<br>
   *         false：不是日期格式<br>
   */
  public static boolean isDate(String dateStr) {
    Pattern pattern = Pattern.compile(dateRegx);
    Matcher matcher = pattern.matcher(dateStr);
    return matcher.matches();
  }

  /**
   * 1、验证是否指定格式的日期，日期字符串适用格式如下：<br>
   * 2009-01-01 12:13:12<br>
   * 2009-1-1 12:13:12<br>
   * 2009/01/01 12:13:12<br>
   * 2009/1/1 12:13:12<br>
   * 2009-01-01<br>
   * 2009/01/01<br>
   * 20091004/01<br>
   * ......<br>
   * <br>
   * 
   * 2、日期字符串错误格式如下：<br>
   * 2009/13/01<br>
   * 2009/01/01 24:59:59<br>
   * 20091234<br>
   * ......<br>
   * <br>
   * 
   * 3、示例代码：<br>
   * DateUtils.isDate("20091004",DateUtils.yyyyMMdd) &nbsp;&nbsp;输出true<br>
   * DateUtils.isDate("20091004",DateUtils.yyyyMMddHHmmss) &nbsp;&nbsp;输出false<br>
   * DateUtils.isDate("2009-1-04","yyyy-MM-dd") &nbsp;&nbsp;输出true<br>
   * 
   * @param dateStr
   *          日期字符串<br>
   * @param dateFormat
   *          指定格式，如：DateUtils.yyyyMMdd、DateUtils.yyyyMMddHHmmss ...
   * @return true：是指定格式的日期<br>
   *         false：不是指定格式的日期<br>
   */
  public static boolean isDate(String dateStr, String dateFormat) {
    boolean isDate = true;
    Pattern pattern = Pattern.compile(dateRegx);
    Matcher matcher = pattern.matcher(dateStr);
    if (matcher.matches()) {
      try {
        stringToDate(dateStr, dateFormat);
      } catch (Exception e) {
        isDate = false;
      }
    } else {
      isDate = false;
    }

    return isDate;
  }

  /**
   * 日期格式转换：从一种字符串格式转成成另外一种字符串格式
   * 
   * @param dateStr
   * @param srcFormat
   * @param descFormat
   * @return
   * 
   *         Sample: <br>
   *         String dateStr =
   *         covertDateStrFormat("20100428","yyyyMMdd","yyyy-MM-dd"); <br>
   *         String timeStr = covertDateStrFormat("1012","HHmm","HH:mm"); String
   *         timeStr = covertDateStrFormat("101222","HHmmss","HH:mm:ss");
   */
  public static String covertDateStrFormat(String dateStr, String srcFormat,
      String descFormat) {
    SimpleDateFormat format_src = new SimpleDateFormat(srcFormat);
    SimpleDateFormat format_desc = new SimpleDateFormat(descFormat);
    try {
      Date date = format_src.parse(dateStr);
      dateStr = format_desc.format(date);
    } catch (ParseException e) {
      log.error("could not convert the dateStr [" + dateStr
          + "] from formatter [" + srcFormat + "] to the given formatter["
          + descFormat + "]", e);
    }
    return dateStr;
  }

  /**
   * 日期格式转换：从一种日期格式转成成另外一种日期格式
   * 
   * @param date
   * @param srcFormat
   * @param descFormat
   * @return
   */
  public static Date covertDateFormat(Date date, String srcFormat,
      String descFormat) {
    SimpleDateFormat format_src = new SimpleDateFormat(srcFormat);
    SimpleDateFormat format_desc = new SimpleDateFormat(descFormat);
    try {
      String str = format_src.format(date);
      date = format_desc.parse(str);
    } catch (ParseException e) {
      log.error("could not convert the date [" + date + "] from formatter ["
          + srcFormat + "] to the given formatter[" + descFormat + "]", e);
    }
    return date;
  }

  /**
   * 根据给定的格式把字符串格式化为日期：格式yyyy-MM-dd
   * 
   * @param dateStr
   * @return
   */
  public static Date stringToDate(String dateStr) {
    SimpleDateFormat formater = new SimpleDateFormat(yyyyMMddSplit);
    Date date = null;
    try {
      date = formater.parse(dateStr);
    } catch (ParseException e) {
      log.error("format String to Date fail:", e);
    }
    return date;
  }

  /**
   * 根据给定的格式把字符串格式化为日期：格式yyyyMMdd
   * 
   * @param dateStr
   * @return
   */
  public static Date strToDate(String dateStr) {
    SimpleDateFormat formater = new SimpleDateFormat(yyyyMMdd);
    Date date = null;
    try {
      date = formater.parse(dateStr);
    } catch (ParseException e) {
      log.error("format String to Date fail:", e);
    }
    return date;
  }

  /**
   * 字符串转化为时间类型
   * 
   * @author alei
   * @param dateStr
   * @param format
   * @return
   */
  public static Date strToDate(String dateStr, String format) {
    SimpleDateFormat formater = new SimpleDateFormat(format);
    Date date = null;
    try {
      date = formater.parse(dateStr);
    } catch (ParseException e) {
      log.error("format String to Date fail:", e);
    }
    return date;
  }

  /**
   * 获取系统当前日期时间（字符串类型）：格式yyyyMMddHHmmss
   * 
   * @return
   */
  public static String getSysTimeStr() {
    // 按指定格式格式化日期
    SimpleDateFormat formater = new SimpleDateFormat(yyyyMMddHHmmss);
    // 返回格式化后的结果
    return formater.format(getSysTime());
  }

  /**
   * 获取系统当前日期时间（毫秒,字符串类型）：格式yyyyMMddHHmmssSSS
   * 
   * 
   * @return
   */
  public static String getSysTimeStr_yyyyMMddHHmmssSSS() {
    // 按指定格式格式化日期
    SimpleDateFormat formater = new SimpleDateFormat(yyyyMMddHHmmssSSS);
    // 返回格式化后的结果
    return formater.format(getSysTime());
  }

  /**
   * 获取系统当前日期时间（Date类型）：格式yyyyMMddHHmmss
   * 
   * @return
   */
  public static Date getSysTime() {
    // 获得日历类实例
    Calendar c = Calendar.getInstance();
    // 返回格式化后的结果
    return c.getTime();
  }

  /**
   * 根据给定的格式把字符串格式化为日期
   * 
   * @param dateStr
   * @param format
   * @return
   */
  public static Date stringToDate(String dateStr, String format) {
    SimpleDateFormat formater = new SimpleDateFormat(format);
    Date date = null;
    try {
      date = formater.parse(dateStr);
    } catch (ParseException e) {
      log.error("format String to Date fail:", e);
    }
    return date;
  }

  public static String str2Date(String dateStr) {
    Date date = new Date(Long.valueOf(dateStr) * 1000);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String strDate = sdf.format(date);
    return strDate;
  }

  /**
   * 根据给定的格式把日期格式化为字符串
   * 
   * @param date
   * @param format
   * @return
   */
  public static String dateToString(Date date, String format) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.format(date);
  }

  //
  public static String getSysTimeStr_yymmdd() {
    // 按指定格式格式化日期
    SimpleDateFormat formater = new SimpleDateFormat(yyyyMMdd);
    // 返回格式化后的结果
    return formater.format(getSysTime());
  }

  public static int todayMinute() {
    Calendar c = Calendar.getInstance();
    int hh = c.get(Calendar.HOUR_OF_DAY);
    int mm = c.get(Calendar.MINUTE);
    return hh * 60 + mm;
  }

  // 根据字符才HHmm计算当天已过的分钟数
  public static int HHmmToMintue(String strHHmm) {
    String strHour = strHHmm.substring(0, 2);
    String strMinute = strHHmm.substring(2);
    int iMinute = Integer.parseInt(strHour) * 60 + Integer.parseInt(strMinute);
    return iMinute;
  }

  /**
   * inDateStr Jun 5 2011 1:47:19:073PM outStr 20110605014719073
   * 
   * @param inDateStr
   * @return
   */
  public static String ConverToString(String inDateStr) {
    // "Jun  5 2011  1:47:19:073PM" ——
    Locale locale = new Locale("en", "US");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss:SSS",
        locale);

    String retStr = null;
    try {
      Date d = sdf.parse(inDateStr);
      retStr = dateToString(d, yyyyMMddHHmmssSSS);
    } catch (ParseException e) {
      log.error("Convert " + inDateStr
          + " to string failed! format:  MMM dd yyyy HH:mm:ss:SSS,locale", e);
      e.printStackTrace();
    }

    return retStr;
  }

  public static Date getLastYearDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.YEAR, -1);
    return c.getTime();
  }
  public static Date getNextYearDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.YEAR, +1);
    return c.getTime();
  }

  public static Date getLastMonthDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MONTH, -1);
    return c.getTime();
  }
  public static Date getNextMonthDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MONTH, +1);
    return c.getTime();
  }

  public static Date getLastDayDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.DAY_OF_MONTH, -1);
    return c.getTime();
  }
  public static Date getNextDayDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DAY_OF_MONTH, +1);
    return c.getTime();
  }

  public static Date getLastMinDate(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.MINUTE, -1);
    return c.getTime();
  }

}
