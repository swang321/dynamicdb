package com.dynamicdb.config;

import com.dynamicdb.db.DynamicDb;
import com.dynamicdb.db.DynamicDbContextHolder;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.DataBinder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author whh
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
public class DynamicDbReg implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    private static final Logger log = LoggerFactory.getLogger(DynamicDbReg.class);

    private ConversionService conversionService = new DefaultConversionService();

    private PropertyValues dataSourcePropertyValues;

    private DataSource defaultDataSource;

    private Map<String, DataSource> customDataSource = new HashMap<>(16);

    @Override
    public void setEnvironment(Environment env) {
        initDefaultDataSource(env);
        initCustomDataSource(env);
    }

    private void initCustomDataSource(Environment env) {
        Map<String, Object> dsMap = new HashMap<>(16);

        dsMap.put("type", "com.alibaba.druid.pool.DruidDataSource");
        dsMap.put("driver-class-name", "com.mysql.jdbc.Driver");
        dsMap.put("url", "jdbc:mysql://192.168.135.129:3306/test?serverTimezone=UTC");
        dsMap.put("username", "root");
        dsMap.put("password", "root");

        DataSource dataSource = buildDataSource(dsMap);

        try {
            // 连接数据源
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        customDataSource.put("slave", dataSource);
        dataBinder(env);

    }

    /**
     * 为DataSource绑定更多数据
     */
    private void dataBinder(Environment env) {
        Iterable<ConfigurationPropertySource> cps = ConfigurationPropertySources.get(env);
        DataBinder binder = new DataBinder(cps);
        binder.setConversionService(conversionService);
        binder.setAutoGrowNestedPaths(false);
        binder.setIgnoreInvalidFields(false);
        binder.setIgnoreUnknownFields(false);

        if (dataSourcePropertyValues == null) {
            Map<String, Object> map = getMap(env);
            map.remove("type");
            map.remove("driver-class-name");
            map.remove("url");
            map.remove("username");
            map.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(map);
        }

        binder.bind(dataSourcePropertyValues);
    }


    private Map<String, Object> getMap(Environment env) {
        //  环境读取绑定
        Iterable<ConfigurationPropertySource> cps = ConfigurationPropertySources.get(env);
        Binder binder = new Binder(cps);
        BindResult<Properties> bind = binder.bind("spring.datasource", Properties.class);
        Properties properties = bind.get();

        Map<String, Object> dsMap = Maps.newHashMap();
        dsMap.put("type", properties.get("type"));
        dsMap.put("driver-class-name", properties.get("druid.driver-class-name"));
        dsMap.put("url", properties.getProperty("druid.url"));
        dsMap.put("username", properties.getProperty("druid.username"));
        dsMap.put("password", properties.getProperty("druid.password"));
        return dsMap;
    }


    private void initDefaultDataSource(Environment env) {
        Map<String, Object> dsMap = getMap(env);
        System.out.println(dsMap);
        defaultDataSource = buildDataSource(dsMap);
    }


    /**
     * 创建数据源
     */
    private DataSource buildDataSource(Map<String, Object> dsMap) {

        String driverClassName = dsMap.get("driver-class-name").toString();
        String url = dsMap.get("url").toString();
        String username = dsMap.get("username").toString();
        String password = dsMap.get("password").toString();

        DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName)
                .url(url).username(username).password(password);

        return factory.build();
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        HashMap<Object, Object> targetDb = new HashMap<>(16);
        targetDb.put("dataSource", defaultDataSource);
        // 将主数据源添加到更多数据源中
        DynamicDbContextHolder.DATASOURCEIDS.add("dataSource");
        targetDb.putAll(customDataSource);
        //添加更多数据源
        DynamicDbContextHolder.DATASOURCEIDS.addAll(customDataSource.keySet());

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDb.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDb);
        registry.registerBeanDefinition("dataSource", beanDefinition);

        log.info("动态数据源注册成功,从数据源个数:{}", customDataSource.size());
    }

}
