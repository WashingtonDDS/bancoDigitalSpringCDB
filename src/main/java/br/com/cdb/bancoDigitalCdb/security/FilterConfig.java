package br.com.cdb.bancoDigitalCdb.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilter() {
        FilterRegistrationBean<CachedBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachedBodyFilter());
        registrationBean.addUrlPatterns("/seguros/viagem");
        return registrationBean;
    }
}
