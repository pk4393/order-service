package com.example.order_service.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.order_service.model.exception.MissingHeaderException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestHeaderValidationInterceptor implements HandlerInterceptor {

  private static final String REQUIRED_HEADER = "X-Order-Service-Auth";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String headerValue = request.getHeader(REQUIRED_HEADER);
    if (!StringUtils.hasText(headerValue)) {
      throw new MissingHeaderException("Missing required header: " + REQUIRED_HEADER);
    }
    return true;
  }
}
