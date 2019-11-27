package com.tukeping.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }

    @Bean
    public ServletRegistrationBean statViewServlet() {
        StatViewServlet statViewServlet = new StatViewServlet();
        ServletRegistrationBean bean = new ServletRegistrationBean(statViewServlet, "/druid/*");

        Map<String, String> params = new HashMap<>();
        params.put("loginUsername", "admin");
        params.put("loginPassword", "123456");
        params.put("allow", "");

        bean.setInitParameters(params);
        return bean;
    }

    @Bean
    public FilterRegistrationBean webStatFilter() {
        Map<String, String> params = new HashMap<>();
        params.put("exclusions", "*.js,*.css,/druid/*");

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        bean.setInitParameters(params);
        bean.setUrlPatterns(Arrays.asList("/*"));

        return bean;
    }
}
