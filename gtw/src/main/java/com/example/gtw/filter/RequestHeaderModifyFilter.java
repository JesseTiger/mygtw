package com.example.gtw.filter;

import com.example.common.enums.RespEnums;
import com.example.gtw.gtwexception.GtwException;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description: 获取 requestheader,可以根据 header的参数进行鉴权验签之类的处理
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
@Component
@Log4j2
public class RequestHeaderModifyFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("______into----Request header filter________");
    Object cachedRequestBodyObject = exchange.getAttribute("cachedRequestBodyObject");

    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    HttpHeaders headers = request.getHeaders();
    String version = headers.getFirst("version");
    String timestamp = headers.getFirst("timestamp");
    String apiId = headers.getFirst("apiId");
    log.info("request origin headers is \t{}, verison is\t{},timestamp is \t{},apiId is \t{} ",
        headers.getOrigin(), version, timestamp, apiId);
    if (StringUtils.isEmpty(version)) {
      return GtwException.gtwFail(response, RespEnums.UNKNOW_ERROR.getCode(), RespEnums.UNKNOW_ERROR.getMsg());
    }
    // TODO: 2019-07-30 header验签处理


  /*  String token = exchange.getRequest().getQueryParams().getFirst("token");
    //向headers中放文件，记得build
    ServerHttpRequest host = exchange.getRequest().mutate().header("ts", "201907300102931").build();
    //将现在的request 变成 change对象
    ServerWebExchange build = exchange.mutate().request(host).build();
    return chain.filter(build);*/
    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return -40;
  }
}
