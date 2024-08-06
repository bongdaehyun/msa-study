package com.sparta.msa_exam.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class ServerPortFilter implements Filter {

    private final HttpServletResponse httpServletResponse;

    @Value("${server.port}")
    private String server_port;

    public ServerPortFilter(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletResponse respones = (HttpServletResponse)servletResponse;
            respones.setHeader("Server-Port", server_port);
            filterChain.doFilter(servletRequest, servletResponse);
    }
}
