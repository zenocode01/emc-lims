package com.emclims.common.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 通用转换工具类
 * 减少各 ServiceImpl 中重复的 convertToVO/convertToExportVO 方法
 * 
 * 使用示例:
 * <pre>
 * // 转换列表
 * List&lt;CustomerVO&gt; voList = ConvertUtils.toList(customerList, this::convertToVO);
 * 
 * // 转换分页
 * Page&lt;CustomerVO&gt; voPage = ConvertUtils.toPage(page, this::convertToVO);
 * </pre>
 */
public class ConvertUtils {

    private ConvertUtils() {
        // 工具类不需要实例化
    }

    /**
     * 将实体列表转换为 VO 列表
     * @param source 实体列表
     * @param converter 转换函数 (entity -> vo)
     * @return VO 列表
     */
    public static <T, V> List<V> toList(List<T> source, Function<T, V> converter) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(converter)
                .toList();
    }

    /**
     * 将分页对象中的列表转换为 VO 列表
     * @param source 原始分页
     * @param converter 转换函数 (entity -> vo)
     * @return VO 分页
     */
    public static <T, V> Page<V> toPage(Page<T> source, Function<T, V> converter) {
        Page<V> target = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        target.setRecords(toList(source.getRecords(), converter));
        return target;
    }

    /**
     * 将单个实体转换为 VO
     * @param source 实体
     * @param converter 转换函数
     * @return VO 对象
     */
    public static <T, V> V toVO(T source, Function<T, V> converter) {
        if (source == null) {
            return null;
        }
        return converter.apply(source);
    }
}
