package com.kane.librarymanagement.interfaces.rest.controllers;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to provide CSRF token to FrontEnd
 * It should call this endpoint to get the CSRF token before making state-changing requests
 */
@RestController
@RequestMapping("/auth")
public class CsrfController {

  /**
   * Endpoint to retrieve CSRF token
   * Spring Security automatically provides the CsrfToken when this endpoint is called
   * Frontend should call this after login to get the CSRF token
   *
   * @param token The CSRF token automatically injected by Spring Security
   * @return The CSRF token details
   */
  @GetMapping("/csrf")
  public CsrfToken csrf(CsrfToken token) {
    return token;
  }
}
