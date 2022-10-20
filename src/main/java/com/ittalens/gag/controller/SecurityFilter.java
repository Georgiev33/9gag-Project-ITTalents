package com.ittalens.gag.controller;

import com.ittalens.gag.services.UserSessionServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class SecurityFilter implements Filter {

    private final UserSessionServiceImpl userSessionService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/users/auth")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (requestURI.contains("/users/register")){
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        userSessionService.isLogged();
        chain.doFilter(servletRequest, servletResponse);
    }
}
