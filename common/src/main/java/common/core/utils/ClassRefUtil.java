package common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 类反射公共类
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
public class ClassRefUtil {

  private static final Logger log = LoggerFactory.getLogger(ClassRefUtil.class);

  /**
   * 获取类的字段属性
   *
   * @param clazz 类
   * @return Field[]
   */
  public static Field[] getFields(Class clazz) {
    List<Field> listFields = new ArrayList<>();
    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
      Field[] fields = clazz.getDeclaredFields();
      Collections.addAll(listFields, fields);
    }
    Field[] fields = new Field[listFields.size()];
    return listFields.toArray(fields);
  }

  /**
   * 获取字段名称与类型
   *
   * @param clazz 类
   * @param isFilter 是否过滤
   * @return map
   */
  public static Map<String, Class> getFieldTypeClass(Class clazz, boolean isFilter) {
    Field[] fields = getFields(clazz);
    Map<String, Class> fieldMap = new HashMap<>(fields.length);
    for (Field field : fields) {
      if (isUseFilter(isFilter, field.getName())) {
        continue;
      }
      fieldMap.put(field.getName(), field.getType());
    }
    return fieldMap;
  }

  /**
   * 判断是否满足过滤条件
   *
   * @param isFilter 是否过滤
   * @param fieldName 字段名称
   * @return 是否满足过滤条件
   */
  private static boolean isUseFilter(boolean isFilter, String fieldName) {
    String serialVersionUID = "serialversionuid";
    return isFilter
        && (serialVersionUID.equals(fieldName.toLowerCase()) || "__".startsWith(fieldName));
  }

  /**
   * 将平级数据转换成map形式。对list等对象无处理。
   *
   * @param object 类
   * @return map
   * @throws Exception
   */
  public static Map<String, Object> getValue(Object object) throws Exception {
    Field[] fields = getFields(object.getClass());
    Map<String, Object> fieldsMap = new HashMap<>(fields.length);
    for (Field field : fields) {
      if (isUseFilter(true, field.getName())) {
        continue;
      }
      String methodName =
          "get" + ("" + field.getName().charAt(0)).toUpperCase() + field.getName().substring(1);
      Object obj = invokeMethod(object, methodName, null, null);
      if (null == obj) {
        fieldsMap.put(field.getName(), formatValue(field.getType().getName(), null));
      } else {
        fieldsMap.put(field.getName(), obj);
      }
    }
    return fieldsMap;
  }

  /**
   * 设置字段值
   *
   * @param clazz 类
   * @param mapValue 字段名称与类型的map
   * @return object
   * @throws Exception
   */
  public static Object setValue(Class clazz, Map<String, Object> mapValue) throws Exception {
    Field[] fields = getFields(clazz);
    Constructor con = clazz.getConstructor();
    Object object = con.newInstance();
    for (Field field : fields) {
      String fieldName = field.getName();
      if (isUseFilter(true, fieldName)) {
        continue;
      }
      String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      if (mapValue.containsKey(fieldName)) {
        Class filedClass = field.getType();
        invokeMethod(
            object,
            methodName,
            new Class[] {filedClass},
            new Object[] {formatValue(filedClass.getName(), mapValue.get(fieldName))});
      }
    }
    return object;
  }

  /**
   * 根据类型格式化值
   *
   * @param type 类型
   * @param value 值
   * @return 转换后的值
   */
  public static Object formatValue(String type, Object value) {
    if (isNullOrEmpty(type)) {
      return value;
    }
    String string = "java.lang.String";
    String bigDecimal = "java.math.BigDecimal";
    String bigInteger = "java.math.BigInteger";
    String boolean1 = "boolean";
    String boolean2 = "java.lang.Boolean";
    String int1 = "int";
    String int2 = "java.lang.Integer";
    String float1 = "float";
    String float2 = "java.lang.Float";
    String double1 = "double";
    String double2 = "java.lang.Double";
    String long1 = "long";
    String long2 = "java.lang.Long";
    String short1 = "short";
    String short2 = "java.lang.Short";
    String byte1 = "byte";
    String byte2 = "java.lang.Byte";
    if (string.equals(type)) {
      return isNullOrEmpty(value) ? null : (String) value;
    } else if (bigDecimal.equals(type)) {
      return isNullOrEmpty(value) ? null : (BigDecimal) value;
    } else if (bigInteger.equals(type)) {
      return isNullOrEmpty(value) ? null : (BigInteger) value;
    } else if (boolean1.equals(type) || boolean2.equals(type)) {
      return !isNullOrEmpty(value) && Boolean.parseBoolean(value.toString());
    } else if (int1.equals(type) || int2.equals(type)) {
      return isNullOrEmpty(value) ? 0 : Integer.parseInt(value.toString());
    } else if (float1.equals(type) || float2.equals(type)) {
      return isNullOrEmpty(value) ? 0f : Float.parseFloat(value.toString());
    } else if (double1.equals(type) || double2.equals(type)) {
      return isNullOrEmpty(value) ? 0d : Double.parseDouble(value.toString());
    } else if (long1.equals(type) || long2.equals(type)) {
      return isNullOrEmpty(value) ? 0L : Long.parseLong(value.toString());
    } else if (short1.equals(type) || short2.equals(type)) {
      return isNullOrEmpty(value) ? 0 : Short.parseShort(value.toString());
    } else if (byte1.equals(type) || byte2.equals(type)) {
      return isNullOrEmpty(value) ? 0 : Byte.parseByte(value.toString());
    } else {
      return value;
    }
  }

  /**
   * 判断是否为空
   *
   * @param value 待判断数据
   * @return true-为空
   */
  private static boolean isNullOrEmpty(Object value) {
    return null == value || "".equals(value.toString());
  }

  /**
   * 循环向上转型, 获取对象的 DeclaredMethod
   *
   * @param object 子类对象
   * @param methodName 父类中的方法名
   * @param parameterTypes 父类中的方法参数类型
   * @return 父类中的方法对象
   */
  public static Method getDeclaredMethod(
      Object object, String methodName, Class<?>... parameterTypes) {
    for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
      try {
        return clazz.getDeclaredMethod(methodName, parameterTypes);
      } catch (Exception e) {
        // 如果这里的异常打印或者往外抛，则就不会执行clazz=clazz.getSuperclass(),最后就不会进入到父类中了
      }
    }
    return null;
  }

  /**
   * 直接调用对象方法, 而忽略修饰符(private, protected, default)
   *
   * @param object 子类对象
   * @param methodName 父类中的方法名
   * @param parameterTypes 父类中的方法参数类型
   * @param parameters 父类中的方法参数
   * @return 父类中方法的执行结果
   * @throws Exception
   */
  public static Object invokeMethod(
      Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters)
      throws Exception {
    // 根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
    Method method = getDeclaredMethod(object, methodName, parameterTypes);
    if (null == method) {
      return null;
    }
    // 抑制Java对方法进行检查,主要是针对私有方法而言
    method.setAccessible(true);
    try {
      // 调用object 的 method 所代表的方法，其方法的参数是 parameters
      return method.invoke(object, parameters);
    } catch (Exception e) {
      log.error("反射直接调用方法[{}]异常", methodName, e);
      throw e;
    }
  }

  /**
   * 循环向上转型, 获取对象的 DeclaredField
   *
   * @param object 子类对象
   * @param fieldName 父类中的属性名
   * @return 父类中的属性对象
   */
  public static Field getDeclaredField(Object object, String fieldName) {
    Class<?> clazz = object.getClass();
    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (Exception e) {
        // 如果这里的异常打印或者往外抛，则就不会执行clazz=clazz.getSuperclass(),最后就不会进入到父类中了
      }
    }
    return null;
  }

  /**
   * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
   *
   * @param object 子类对象
   * @param fieldName 父类中的属性名
   * @param value 将要设置的值
   * @throws Exception
   */
  public static void setFieldValue(Object object, String fieldName, Object value) throws Exception {
    // 根据 对象和属性名通过反射 调用上面的方法获取 Field对象
    Field field = getDeclaredField(object, fieldName);
    if (null == field) {
      return;
    }
    // 抑制Java对其的检查
    field.setAccessible(true);
    try {
      // 将 object 中 field 所代表的值 设置为 value
      field.set(object, value);
    } catch (IllegalArgumentException e) {
      log.error("反射直接设置对象[{}]属性值异常", fieldName, e);
      throw e;
    }
  }

  /**
   * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
   *
   * @param object 子类对象
   * @param fieldName 父类中的属性名
   * @return 父类中的属性值
   * @throws Exception
   */
  public static Object getFieldValue(Object object, String fieldName) throws Exception {
    // 根据 对象和属性名通过反射 调用上面的方法获取 Field对象
    Field field = getDeclaredField(object, fieldName);
    if (null == field) {
      return null;
    }
    // 抑制Java对其的检查
    field.setAccessible(true);
    try {
      // 获取 object 中 field 所代表的属性值
      return field.get(object);
    } catch (Exception e) {
      log.error("反射直接读取对象[{}]属性值异常", fieldName, e);
      throw e;
    }
  }
}
