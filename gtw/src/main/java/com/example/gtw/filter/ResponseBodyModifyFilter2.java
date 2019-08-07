//package com.example.gtw.filter;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @Description: 直接改 responsebody 输出
// * @Author: ZHANGXIAOHU
// * @Date: 2019-07-29
// */
////@Component
//public class ResponseBodyModifyFilter2 implements GlobalFilter, Ordered {
//
//  @Override
//  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//    DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
//    ObjectMapper objMapper = new ObjectMapper();
//    byte[] obj;
//    try {
//      obj = objMapper.writeValueAsBytes("zhajzhah");
//      return exchange.getResponse().writeWith(Mono.just(obj).map(r -> dataBufferFactory.wrap(r)));
//    } catch (JsonProcessingException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    return chain.filter(exchange);
//  }
//
//  @Override
//  public int getOrder() {
//    return -5;
//  }
//}
