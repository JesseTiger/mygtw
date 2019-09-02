package com.example.account.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.account.web.BodyReaderHttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-06
 */
@WebFilter(filterName = "firstFilter", urlPatterns = "/*")
public class FirstFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    System.out.println("----------------------->first filter 过滤器被创建");
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;

    Enumeration<String> headerNames = req.getHeaderNames();

    JSONObject headers=new JSONObject();
    while (headerNames.hasMoreElements()) {
      String s = headerNames.nextElement();
      headers.put(s,req.getHeader(s));
    }

    ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(req);
    BufferedReader reader = new BufferedReader(new InputStreamReader(requestWrapper.getInputStream(), "UTF-8"));
    String temp = null;
    StringBuilder sb = new StringBuilder();
    while ((temp = reader.readLine()) != null) {
      sb.append(temp);
    }
    JSONObject json = JSONObject.parseObject(sb.toString().trim());
    System.err.println("header is :\t" + headers);
    System.err.println("requestBody is :\t" + json);

    filterChain.doFilter(requestWrapper, servletResponse);
  }

  @Override
  public void destroy() {

  }

}
