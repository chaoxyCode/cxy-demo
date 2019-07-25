package common.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 使用gson进行json处理
 *
 * @author chaoxy
 * @date 2019/7/5
 */
public class GsonUtil {

    /** Gson（不含NULL） */
    private static final Gson GSON = new Gson();
    /** Gson（含NULL） */
    private static final Gson GSON_NULL = new GsonBuilder().serializeNulls().create();
    /** Gson（不含NULL，Expose注解-忽略字段（不加注解或加注解属性为false）） */
    private static final Gson GSON_EXPOSE =
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    /** Gson（含NULL，Expose注解-忽略字段（不加注解或加注解属性为false）） */
    private static final Gson GSON_EXPOSE_NULL =
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

    /**
     * json字符串 转 实体Bean
     *
     * @param json json字符串
     * @param clazz 实体Bean
     * @return <T> T
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * bean转换成JSON字符串
     *
     * @param obj bean
     * @return String
     */
    public static String toJsonStr(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 转换成JSON字符串（含NULL）
     *
     * @param obj bean
     * @return String
     */
    public static String toJsonStrHavingNull(Object obj) {
        return GSON_NULL.toJson(obj);
    }
    /**
     * 转换成JSON字符串（不含NULL，Expose注解）
     *
     * @param obj bean
     * @return String
     */
    public static String toJsonStrExpose(Object obj) {
        return GSON_EXPOSE.toJson(obj);
    }
    /**
     * 转换成JSON字符串（含NULL，Expose注解）
     *
     * @param obj bean
     * @return String
     */
    public static String toJsonStrExposeHavingNull(Object obj) {
        return GSON_EXPOSE_NULL.toJson(obj);
    }
}
