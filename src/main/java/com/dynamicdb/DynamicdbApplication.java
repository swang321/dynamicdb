package com.dynamicdb;

import com.dynamicdb.config.DynamicDbReg;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author admin
 */
@Import(DynamicDbReg.class)
@SpringBootApplication
@MapperScan("com.dynamicdb.mapper")
public class DynamicdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicdbApplication.class, args);
    }

}
