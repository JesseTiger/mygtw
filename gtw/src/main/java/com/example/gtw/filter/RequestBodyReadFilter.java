package com.example.gtw.filter;

import com.example.common.enums.RespEnums;
import com.example.gtw.gtwexception.GtwException;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @Description: 获取 request body 参数值
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Log4j2
@Component
public class RequestBodyReadFilter implements GatewayFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("______into----Request body filter________");
    Object cachedRequestBodyObject = exchange.getAttribute("cachedRequestBodyObject");
    log.info("___requestBody is\t{}", cachedRequestBodyObject);
    //request
    ServerHttpRequest request = exchange.getRequest();
    //response
    ServerHttpResponse response = exchange.getResponse();
    //request uri path
    String requestUriPath = request.getURI().getPath();
    log.info("request uri is\t{}", requestUriPath);
    //request header

    // TODO: 2019-07-30 对请求体进行相关的操作(增加参数)或者对请求头进行相关操作(加请求头)

    if (StringUtils.isEmpty(cachedRequestBodyObject)) {
      return GtwException.gtwFail(response, RespEnums.UNKNOW_ERROR.getCode(), RespEnums.UNKNOW_ERROR.getMsg());
    }

//    //  把 reqTs 塞入 request 中
//    // TODO: 2019-07-26 very important--------
    ServerHttpRequest reqTs = exchange.getRequest().mutate().header("reqTs", "21321").build();
    //将现在的request 变成 change对象
    ServerWebExchange build = exchange.mutate().request(reqTs).build();
    return chain.filter(build);
//    // TODO: 2019-07-26 very important---------

//    return chain.filter(exchange);
  }


  @Override
  public int getOrder() {
    return -30;
  }
}
