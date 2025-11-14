package com.kane.librarymanagement.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.util.HtmlUtils;

/**
 * Request wrapper that sanitizes request parameters to prevent XSS attacks
 * This escapes HTML entities in all request parameters
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

  public XssRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String[] getParameterValues(String parameter) {
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
      return null;
    }

    String[] encodedValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      encodedValues[i] = sanitize(values[i]);
    }
    return encodedValues;
  }

  @Override
  public String getParameter(String parameter) {
    String value = super.getParameter(parameter);
    return sanitize(value);
  }

  @Override
  public String getHeader(String name) {
    String value = super.getHeader(name);
    return sanitize(value);
  }

  /**
   * Sanitizes input by escaping HTML entities
   * @param value The input string to sanitize
   * @return Sanitized string with HTML entities escaped
   */
  private String sanitize(String value) {
    if (value == null) {
      return null;
    }
    // Escape HTML entities to prevent XSS
    return HtmlUtils.htmlEscape(value);
  }
}
