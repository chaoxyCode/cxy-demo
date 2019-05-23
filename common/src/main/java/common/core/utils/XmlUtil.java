package common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Xml工具类-JAXB
 *
 * @version 1.0
 * @auther chaoxy
 * @date 2019-05-23
 */
public class XmlUtil {

  private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

  /**
   * xml转换成JavaBean
   *
   * @param xml xml字符串
   * @param clazz class类
   * @param <T> 泛型
   * @return T
   */
  public static <T> T formatXml(String xml, Class<?> clazz) {
    try {
      // jdk自带的转换类JAXB
      JAXBContext context = JAXBContext.newInstance(clazz);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      StringReader sr = new StringReader(xml);
      @SuppressWarnings("unchecked")
      T t = (T) unmarshaller.unmarshal(sr);
      sr.close();
      return t;
    } catch (JAXBException je) {
      log.error("xml转换JavaBean[{}]时发生异常: \n", clazz.getSimpleName(), je);
      return null;
    }
  }

  /**
   * JavaBean转换成xml字符串
   *
   * @param obj 待转换JavaBean
   * @param encoding 编码
   * @return String
   */
  public static String toXml(Object obj, String encoding) {
    String xmlStr = null;
    String className = obj.getClass().getSimpleName();
    StringWriter sw = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(obj.getClass());
      // 创建marshaller（指挥）通过它可以将xml与对象互相转换 。对于一些不规范的xml格式它也可以进行规范调试
      Marshaller marshaller = context.createMarshaller();
      // 乱码转换
      marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
      // 格式化生成的xml串
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      // 是否省略XML头申明信息
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
      // 将对象输出成XML形式
      marshaller.marshal(obj, sw);
      xmlStr = sw.toString();
    } catch (JAXBException je) {
      log.error("JavaBean[{}]转换xml时发生异常: \n", className, je);
    } finally {
      try {
        sw.close();
      } catch (IOException ioe) {
        log.error("JavaBean[{}]转换xml关闭文件流时发生异常: \n", className, ioe);
      }
    }
    return xmlStr;
  }
}
