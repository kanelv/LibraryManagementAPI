package com.kane.librarymanagement.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to sanitize request parameters for XSS prevention
 * This wraps the request to sanitize parameters before they reach controllers
 */
@Component
public class XssFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    XssRequestWrapper wrappedRequest = new XssRequestWrapper((HttpServletRequest) request);
    chain.doFilter(wrappedRequest, response);
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // Initialization logic if needed
  }

  @Override
  public void destroy() {
    // Cleanup logic if needed
  }
}
