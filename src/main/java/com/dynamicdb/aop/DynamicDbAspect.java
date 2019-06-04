package com.dynamicdb.aop;

import com.dynamicdb.db.DynamicDbContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author whh
 */
@Aspect
@Component
public class DynamicDbAspect {

    private static final Logger log = LoggerFactory.getLogger(DynamicDbAspect.class);


    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, MyDataSource ds) {
        log.info("--------------------------------------------------");
        String name = ds.name();
        log.info("datasource name:{}", name);
        if (!DynamicDbContextHolder.containsDb(name)) {
            log.info("数据源 :{} 不存在,使用默认数据源:{}", name, point.getSignature());
        } else {
            log.info("Use DataSource : {} ----- >", name, point.getSignature());
            DynamicDbContextHolder.setDataSourceType(name);
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, MyDataSource ds) {
        log.info("--------------------------------------------------");
        log.info("restore DB name:{},signature:{}", ds.name(), point.getSignature());
        DynamicDbContextHolder.clearDbType();
    }

}
