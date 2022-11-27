package com.ittalens.gag.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class SecurityFilter implements Filter {

    private static final List<String> AVAILABLE_URI = Arrays.asList(
            "/users/register",
            "/users/verify/",
            "/users/auth",
            "/posts/all");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        if (AVAILABLE_URI.contains(requestURI)|| requestURI.startsWith("/users/verify/")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (request.getSession().getAttribute("LOGGED") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }
}
