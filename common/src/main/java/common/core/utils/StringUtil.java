package common.core.utils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 字符串公共类
 *
 * @author chaoxy
 * @date 2019/05/13
 * @version 1.0
 */
public class StringUtil {

    /**
     * 生成UUID唯一序号
     *
     * @return String
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 判断对象是否为空
     *
     * @param str 对象
     * @return boolean
     */
    public static boolean isNullOrBlock(Object str) {
        return (null == str || "".equals(str.toString()));
    }

    public static boolean isNotNullOrBlock(Object string) {
        return !isNullOrBlock(string);
    }

    /**
     * 字符串去空后判断是否为空
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean afterTrimIsNullOrBlock(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        str = str.trim();
        return "".equals(str);
    }

    public static boolean afterTrimIsNotNullOrBlock(String str) {
        return !afterTrimIsNullOrBlock(str);
    }

    /**
     * 字符串首字母大写
     *
     * @param str 字符串
     * @return String
     */
    public static String fristCharToUpperCase(String str) {
        return (str.charAt(0) + "").toUpperCase() + str.substring(1);
    }

    /**
     * 字符串首字母小写
     *
     * @param str 字符串
     * @return String
     */
    public static String fristCharToLowerCase(String str) {
        return (str.charAt(0) + "").toLowerCase() + str.substring(1);
    }

    /**
     * list转String
     *
     * @param list
     * @return String
     */
    public static String list2String(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            sb.append(object.toString());
            sb.append(",");
        }
        String rs = sb.toString();
        return rs.substring(0, rs.length() - 1);
    }

    /**
     * 获取数字型随机数字
     *
     * @param numLen 随机码位数
     * @return String
     */
    public static String getRandomNum(int numLen) {
        if (numLen < 1 || numLen > 99) {
            numLen = 6;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < numLen; i++) {
            Random random = new Random();
            int randomInt = random.nextInt() % 10;
            rs.append(Math.abs(randomInt));
        }
        return rs.toString();
    }

    /**
     * unicode 转换成 utf-8
     *
     * @param str 字符串
     * @return String
     */
    public static String unicodeToUtf8(String str) {
        char aChar;
        int len = str.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = str.charAt(x++);
            if (aChar == '\\') {
                aChar = str.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = str.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
}
