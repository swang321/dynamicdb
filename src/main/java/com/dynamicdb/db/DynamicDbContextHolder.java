package com.dynamicdb.db;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author whh
 */
public class DynamicDbContextHolder {

    /**
     * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXTLOCAL = new ThreadLocal<>();

    public static List<String> DATASOURCEIDS = Lists.newArrayList();

    /**
     * 设置数据源名
     */
    public static void setDataSourceType(String dbType) {
        CONTEXTLOCAL.set(dbType);
    }

    /**
     * 获取数据源名
     */
    public static String getDbSourceType() {
        return CONTEXTLOCAL.get();
    }

    /**
     * 清除数据源名
     */
    public static void clearDbType() {
        CONTEXTLOCAL.remove();
    }

    /**
     * 判断指定 DataSource 当前是否存在
     */
    public static boolean containsDb(String dbId) {
        return DATASOURCEIDS.contains(dbId);
    }

}
