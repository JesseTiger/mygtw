//package com.example.gtw.filter;
//
//
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
//import org.reactivestreams.Publisher;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
///**
// * @Description: 返回值后追加信息,会有乱码,
// * @Author: ZHANGXIAOHU
// * @Date: 2019-07-29
// */
////@Component
//public class ResponseBodyModifyFilter implements GlobalFilter, Ordered {
//
//  @Override
//  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//    ServerHttpResponse response = exchange.getResponse();
//    DataBufferFactory dataBufferFactory = response.bufferFactory();
//
//    ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(response) {
//      @Override
//      public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//        if (body instanceof Flux) {
//          Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;
//
//          return super.writeWith(flux.buffer().map(dataBuffers -> {
//            ByteOutputStream outputStream = new ByteOutputStream();
//            dataBuffers.forEach(buffer -> {
//            byte[] array = new byte[buffer.readableByteCount()];
//              buffer.read(array);
//              outputStream.write(array);
//            });
//            String ss="{0123456789abcdefg}";
//            outputStream.write(ss.getBytes());
//            return dataBufferFactory.wrap(outputStream.getBytes());
//          }));
//        }
//        return super.writeWith(body);
//      }
//    };
//
//    ServerWebExchange serverWebExchange = exchange.mutate().response(responseDecorator).build();
//    return chain.filter(serverWebExchange);
//  }
//
//  @Override
//  public int getOrder() {
//    return -5;
//  }
//}
