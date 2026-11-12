package org.example.sor_mrp.shared.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000); // Max characters of the body to log
    filter.setIncludeHeaders(false); // Set to true if you want headers too
    filter.setAfterMessagePrefix("HTTP REQUEST: ");
    return filter;
  }
}
