package com.github.zzxt0019.httplog.example.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.List;

public class MybatisPlusUtil {
    /**
     * 拼接QueryWrapper, 等于查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       Object[] 值
     * @param <T>          Bean
     */
    public static <T> void eq(QueryWrapper<T> queryWrapper, String[] columns, Object[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null) {
                /**
                 * 不是String, 或者不为空
                 */
                if (!(values[i] instanceof String) || StringUtils.isNotBlank((String) values[i])) {
                    queryWrapper.eq(columns[i], values[i]);
                }
            }
        }
    }

    /**
     * 拼接QueryWrapper, 等于查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        Object 值
     * @param <T>          Bean
     */
    public static <T> void eq(QueryWrapper<T> queryWrapper, String column, Object value) {
        if (value != null) {
            /**
             * 不是String, 或者不为空
             */
            if (!(value instanceof String) || StringUtils.isNotBlank((String) value)) {
                queryWrapper.eq(column, value);
            }
        }
    }

    /**
     * 拼接QueryWrapper, IN查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       List<?>[] 值
     * @param <T>          Bean
     */
    public static <T> void in(QueryWrapper<T> queryWrapper, String[] columns, List<?>[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null && !values[i].isEmpty()) {
                queryWrapper.in(columns[i], values[i]);
            }
        }
    }
    public static <T> void notin(QueryWrapper<T> queryWrapper, String[] columns, List<?>[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null && !values[i].isEmpty()) {
                queryWrapper.notIn(columns[i], values[i]);
            }
        }
    }

    /**
     * 拼接QueryWrapper, IN查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       Object[][] 值
     * @param <T>          Bean
     */
    public static <T> void in(QueryWrapper<T> queryWrapper, String[] columns, Object[][] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null && values[i].length > 0) {
                queryWrapper.in(columns[i], values[i]);
            }
        }
    }
    public static <T> void notin(QueryWrapper<T> queryWrapper, String[] columns, Object[][] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null && values[i].length > 0) {
                queryWrapper.notIn(columns[i], values[i]);
            }
        }
    }

    /**
     * 拼接QueryWrapper, IN查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        List<?> 值
     * @param <T>          Bean
     */
    public static <T> void in(QueryWrapper<T> queryWrapper, String column, List<?> value) {
        if (value != null && !value.isEmpty()) {
            queryWrapper.in(column, value);
        }
    }
    public static <T> void notin(QueryWrapper<T> queryWrapper, String column, List<?> value) {
        if (value != null && !value.isEmpty()) {
            queryWrapper.notIn(column, value);
        }
    }

    /**
     * 拼接QueryWrapper, IN查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        Object[] 值
     * @param <T>          Bean
     */
    public static <T> void in(QueryWrapper<T> queryWrapper, String column, Object[] value) {
        if (value != null && value.length > 0) {
            queryWrapper.in(column, value);
        }
    }
    public static <T> void notin(QueryWrapper<T> queryWrapper, String column, Object[] value) {
        if (value != null && value.length > 0) {
            queryWrapper.notIn(column, value);
        }
    }

    /**
     * 拼接QueryWrapper, 模糊查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       Object[] 值
     * @param <T>          Bean
     */
    public static <T> void like(QueryWrapper<T> queryWrapper, String[] columns, Object[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null) {
                /**
                 * 不是String, 或者不为空
                 */
                if (!(values[i] instanceof String) || StringUtils.isNotBlank((String) values[i])) {
                    queryWrapper.like(columns[i], escapeLike(values[i]));
                }
            }
        }
    }

    /**
     * 拼接QueryWrapper, 模糊查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        Object 值
     * @param <T>          Bean
     */
    public static <T> void like(QueryWrapper<T> queryWrapper, String column, Object value) {
        if (value != null) {
            /**
             * 不是String, 或者不为空
             */
            if (!(value instanceof String) || StringUtils.isNotBlank((String) value)) {
                queryWrapper.like(column, escapeLike(value));
            }
        }
    }

    /**
     * 拼接QueryWrapper, 大于等于查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       Object[] 值
     * @param <T>          Bean
     */
    public static <T> void ge(QueryWrapper<T> queryWrapper, String[] columns, Object[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null) {
                /**
                 * 不是String, 或者不为空
                 */
                if (!(values[i] instanceof String) || StringUtils.isNotBlank((String) values[i])) {
                    queryWrapper.ge(columns[i], values[i]);
                }
            }
        }
    }

    /**
     * 拼接QueryWrapper, 大于等于查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        Object 值
     * @param <T>          Bean
     */
    public static <T> void ge(QueryWrapper<T> queryWrapper, String column, Object value) {
        if (value != null) {
            /**
             * 不是String, 或者不为空
             */
            if (!(value instanceof String) || StringUtils.isNotBlank((String) value)) {
                queryWrapper.ge(column, value);
            }
        }
    }

    /**
     * 拼接QueryWrapper, 小于等于查询
     *
     * @param queryWrapper queryWrapper
     * @param columns      String[] 列名
     * @param values       Object[] 值
     * @param <T>          Bean
     */
    public static <T> void le(QueryWrapper<T> queryWrapper, String[] columns, Object[] values) {
        for (int i = 0; i < values.length && i < columns.length; i++) {
            if (values[i] != null) {
                /**
                 * 不是String, 或者不为空
                 */
                if (!(values[i] instanceof String) || StringUtils.isNotBlank((String) values[i])) {
                    queryWrapper.le(columns[i], values[i]);
                }
            }
        }
    }

    /**
     * 拼接QueryWrapper, 小于等于查询
     *
     * @param queryWrapper queryWrapper
     * @param column       String 列名
     * @param value        Object 值
     * @param <T>          Bean
     */
    public static <T> void le(QueryWrapper<T> queryWrapper, String column, Object value) {
        if (value != null) {
            /**
             * 不是String, 或者不为空
             */
            if (!(value instanceof String) || StringUtils.isNotBlank((String) value)) {
                queryWrapper.le(column, value);
            }
        }
    }

    private static String escapeLike(Object value) {
        return String.valueOf(value).replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("_", "\\\\_")
                .replaceAll("%", "\\\\%");
    }
}
