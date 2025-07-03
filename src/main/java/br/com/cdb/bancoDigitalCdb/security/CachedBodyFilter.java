package br.com.cdb.bancoDigitalCdb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CachedBodyFilter implements Filter, jakarta.servlet.Filter {


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest currentRequest = (HttpServletRequest) servletRequest;
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(currentRequest);

        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilter() {
        FilterRegistrationBean<CachedBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachedBodyFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}