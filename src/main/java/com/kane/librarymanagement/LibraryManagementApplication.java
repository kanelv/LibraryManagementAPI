package com.kane.librarymanagement;

import com.kane.librarymanagement.config.JwtConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class LibraryManagementApplication {

  private static final Logger log = LoggerFactory.getLogger(LibraryManagementApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(LibraryManagementApplication.class, args);
  }
}
