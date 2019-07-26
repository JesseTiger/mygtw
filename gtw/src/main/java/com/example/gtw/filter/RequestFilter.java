package com.example.gtw.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Component
@Log4j2
public class RequestFilter implements GatewayFilter, Ordered {


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    Object cachedRequestBodyObject = exchange.getAttribute("cachedRequestBodyObject");
    log.info("requestBody is\t{}",cachedRequestBodyObject);
    return chain.filter(exchange);
  }


  @Override
  public int getOrder() {
    return 0;
  }
}
