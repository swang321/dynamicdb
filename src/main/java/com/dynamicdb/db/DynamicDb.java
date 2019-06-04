package com.dynamicdb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Author whh
 */
public class DynamicDb extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(DynamicDb.class);

    @Override
    protected Object determineCurrentLookupKey() {
        if (DynamicDbContextHolder.getDbSourceType() == null) {
            log.info("数据源为:====master");
        } else {
            log.info("数据源为:====:{}", DynamicDbContextHolder.getDbSourceType());
        }
        return DynamicDbContextHolder.getDbSourceType();
    }
}
