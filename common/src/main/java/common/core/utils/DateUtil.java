package common.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Java1.7 以前的日期处理类
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
public class DateUtil {

  /** 日期格式：yyyy-MM-dd */
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  /** 24小时制日期格式：yyyy-MM-dd HH:mm:ss */
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * 获取当前时间(日期格式：yyyy-MM-dd HH:mm:ss)
   *
   * @return Date
   */
  public static Date getNowDate() {
    return new Date();
  }

  /**
   * 获取当前时间(默认日期格式：yyyy-MM-dd HH:mm:ss)
   *
   * @return String
   */
  public static String getNowStrDate(String format) {
    if (null == format || "".equals(format)) {
      format = DATE_TIME_FORMAT;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.format(new Date());
  }

  /**
   * 判断是否润年
   *
   * @param dateStr 字符串日期
   * @return boolean
   */
  public static boolean isLeapYear(String dateStr) {
    Date date = str2Date(dateStr, null);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int year = calendar.get(Calendar.YEAR);
    // 判断闰年的方法：闰年满足两个条件（满足一个即为闰年） 1、能被4整除但不能被100整除 2、能被400整除
    return 0 == year % 400 || (0 == year % 4 && 0 != year % 100);
  }

  /**
   * 提取一个月中的最后一天
   *
   * @param day
   * @return Date
   */
  public static Date getLastDate(long day) {
    Date date = new Date();
    long date3Hm = date.getTime() - 3600000 * 34 * day;
    return new Date(date3Hm);
  }

  /**
   * 英文月(三位) 转换 数字月(两位)
   *
   * @param month 英文月(三位)
   * @return String
   */
  public static String getMonthNumByMonthEng(String month) {
    if (null == month || 3 != month.length()) {
      return null;
    }
    // Java1.7之前大家都清楚switch的比较范围只能局限于（int 、short 、byte 、char）之间，
    // Java1.7后实现字符串比较的功能的实现机制是：将字符串之间的比较转换为其哈希值的比较。
    switch (month) {
      case "Jan":
        return month = "01";
      case "Feb":
        return month = "02";
      case "Mar":
        return month = "03";
      case "Apr":
        return month = "04";
      case "May":
        return month = "05";
      case "Jun":
        return month = "06";
      case "Jul":
        return month = "07";
      case "Aug":
        return month = "08";
      case "Sep":
        return month = "09";
      case "Oct":
        return month = "10";
      case "Nov":
        return month = "11";
      case "Dec":
        return month = "12";
      default:
        month = "";
        break;
    }
    return month;
  }

  /**
   * 转换格林尼治标准时间成yyyy-MM-dd
   *
   * @param value 时间字符串
   * @return String
   */
  private static String converGMTStrDate(String value) {
    /*
     * GMT(Greenwich Mean Time)代表格林尼治标准时间，全球都以格林威治的时间作为标准来设定时间 UTC(Coordinated
     * Universal Time)代表世界协调时间（又称世界标准时间、世界统一时间）
     * 是经过平均太阳时(以格林威治时间GMT为准)、地轴运动修正后的新时标以及以「秒」为单位的国际原子时所综合精算而成的时间，计算过程相当严谨精密
     * CST却同时可以代表如下 4 个不同的时区： Central Standard Time (USA) UT-6:00 美国标准时间 Central
     * Standard Time (Australia) UT+9:30 澳大利亚标准时间 China Standard Time UT+8:00 中国标准时间
     * Cuba Standard Time UT-4:00 古巴标准时间
     */
    if (isGMT(value)) {
      String year = value.substring(24, 28);
      String date = value.substring(8, 10);
      String temp = value.substring(4, 7);
      return year + "-" + getMonthNumByMonthEng(temp) + "-" + date;
    }
    return null;
  }

  /**
   * 判断是否符合GMT时间规则
   *
   * @param value 待判断字符串
   * @return boolean
   */
  private static boolean isGMT(String value) {
    if (null == value || value.isEmpty()) {
      return false;
    }
    int gmtLength = 28;
    int cstLength = 20;
    String cst = "CST";
    return gmtLength == value.length() && cstLength == value.indexOf(cst);
  }

  /**
   * 字符串 日期 转 Date 日期
   *
   * @param inValue 字符串日期
   * @return Date
   */
  public static Date converString2Date(String inValue) {
    if (null == inValue || "".equals(inValue)) {
      return null;
    }
    String value = inValue.replaceAll("：", ":").trim();
    if (value.contains("年") && value.contains("月") && value.contains("日")) {
      if (value.contains("时") && value.contains("分") && value.contains("秒")) {
        if (value.contains("毫秒")) {
          return str2Date(value, "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
        }
        return str2Date(value, "yyyy年MM月dd日 HH时mm分ss秒");
      }
      return str2Date(value, "yyyy年MM月dd日");
    }
    if (isGMT(value)) {
      value = converGMTStrDate(value);
    }
    if (value.contains("-")) {
      if (value.length() == 23) {
        return str2Date(value, "yyyy-MM-dd HH:mm:ss.SSS");
      }
      if (value.length() == 19) {
        return str2Date(value, DATE_TIME_FORMAT);
      }
      if (value.length() == 10) {
        return str2Date(value, DATE_FORMAT);
      }
    }
    if (value.contains("/")) {
      if (value.length() == 23) {
        return str2Date(value, "yyyy/MM/dd HH:mm:ss.SSS");
      }
      if (value.length() == 19) {
        return str2Date(value, "yyyy/MM/dd HH:mm:ss");
      }
      if (value.length() == 10) {
        return str2Date(value, "yyyy/MM/dd");
      }
    }
    if (value.contains(":")) {
      if (value.length() == 12) {
        return str2Date(value, "HH:mm:ss.SSS");
      }
      if (value.length() == 10) {
        return str2Date(value, "HH:mm:ss");
      }
    }
    if (value.contains(" ")) {
      if (value.length() == 18) {
        return str2Date(value, "yyyyMMdd HHmmssSSS");
      }
      if (value.length() == 15) {
        return str2Date(value, "yyyyMMdd HHmmss");
      }
    }
    if (value.length() == 17) {
      return str2Date(value, "yyyyMMddHHmmssSSS");
    }
    if (value.length() == 14) {
      return str2Date(value, "yyyyMMddHHmmss");
    }
    if (value.length() == 9) {
      return str2Date(value, "HHmmssSSS");
    }
    if (value.length() == 8) {
      return str2Date(value, "yyyyMMdd");
    }
    if (value.length() == 6) {
      return str2Date(value, "HHmmss");
    }
    return null;
  }

  /**
   * 日期 转 字符串
   *
   * @param date 日期
   * @param format 日期格式
   * @return String
   */
  public static String date2Str(Date date, String format) {
    if (null == format || "".equals(format)) {
      format = DATE_FORMAT;
    }
    if (null == date) {
      date = getNowDate();
    }
    DateFormat dateformat = new SimpleDateFormat(format);
    return dateformat.format(date);
  }

  /**
   * 字符串 转 日期
   *
   * @param dateStr 字符串日期
   * @param formatStr 日期格式
   * @return Date
   */
  public static Date str2Date(String dateStr, String formatStr) {
    if (null == formatStr || "".equals(formatStr)) {
      formatStr = DATE_FORMAT;
    }
    DateFormat dateformat = new SimpleDateFormat(formatStr);
    Date date = null;
    try {
      date = dateformat.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 计算两个日期之间相差的天数
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 相差的天数
   */
  public static double daysBetween(Date startDate, Date endDate) {
    long time2 = endDate.getTime();
    long time1 = startDate.getTime();
    long diff = time2 - time1;
    return Double.parseDouble(
        (diff / (1000 * 60 * 60 * 24))
            + "."
            + ((diff % (1000 * 60 * 60 * 24)) / (24 * 60 * 60 * 10)));
  }

  /**
   * 获取两日期的天数差值
   *
   * @param startDate 起始日期
   * @param endDate 结束日期
   * @param dateFormat 格式
   * @return double
   * @throws ParseException
   */
  public static double daysBetween(String startDate, String endDate, String dateFormat)
      throws ParseException {
    return daysBetween(str2Date(startDate, dateFormat), str2Date(endDate, dateFormat));
  }

  /**
   * 日期增加天数
   *
   * @param loopDate 基准日期
   * @param addOsubNum 增加的天数
   * @return 增加后的日期
   */
  public static String nextDayDate(String loopDate, int addOsubNum) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DateUtil.str2Date(loopDate, null));
    calendar.add(Calendar.DAY_OF_YEAR, addOsubNum);
    return DateUtil.date2Str(calendar.getTime(), null);
  }

  /**
   * 比较两个时间的分数差
   *
   * @param startDate 起始日期
   * @param endDate 结束日期
   * @param dateFormat 格式
   * @return 分钟差值
   * @throws ParseException
   */
  public static long compareTime(String startDate, String endDate, String dateFormat)
      throws ParseException {
    if (null == dateFormat || "".equals(dateFormat)) {
      dateFormat = DATE_FORMAT;
    }
    Date dateStart = str2Date(startDate, dateFormat);
    Date dateEnd = str2Date(endDate, dateFormat);
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    dateStart = sdf.parse(sdf.format(dateStart));
    dateEnd = sdf.parse(sdf.format(dateEnd));
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateStart);
    long time1 = cal.getTimeInMillis();
    cal.setTime(dateEnd);
    long time2 = cal.getTimeInMillis();
    return (time2 - time1) / (1000);
  }
}
