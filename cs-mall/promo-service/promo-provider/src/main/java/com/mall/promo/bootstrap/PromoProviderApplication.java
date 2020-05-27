package com.mall.promo.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author cy
 * @date 2020/5/25 12:41
 */
@MapperScan(basePackages = "com.mall.promo.dal")
@ComponentScan(basePackages = "com.mall.promo")
@SpringBootApplication
public class PromoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromoProviderApplication.class, args);
    }

}
