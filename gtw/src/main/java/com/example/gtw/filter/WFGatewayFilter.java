package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.gtw.utils.GtwUtils;
import java.net.URI;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-09-02
 */
//@Component
public class WFGatewayFilter implements GatewayFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // FIXME: 2019-09-02 尝试修改 requestbody
    ServerHttpRequest oldRequest = exchange.getRequest();
    String requestBody = GtwUtils.resolveBodyFromRequest(oldRequest);
    JSONObject jsonObject = JSONObject.parseObject(requestBody);
    jsonObject.put("youyou", "meimei");
    requestBody=jsonObject.toString();
    URI uri = oldRequest.getURI();
    URI newUri = UriComponentsBuilder.fromUri(uri).build(true).toUri();
    ServerHttpRequest newRequest = oldRequest.mutate().uri(newUri).build();
    DataBuffer bodyDataBuffer = GtwUtils.stringBuffer(jsonObject.toString());
    Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

    // 定义新的消息头
    HttpHeaders newHeaders = new HttpHeaders();
    newHeaders.putAll(exchange.getRequest().getHeaders());

    // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
    int length = requestBody.getBytes().length;
    System.err.println("header的 content_length: "+length);
    newHeaders.remove(HttpHeaders.CONTENT_LENGTH);
    newHeaders.setContentLength(length);
//     设置CONTENT_TYPE
    if (!StringUtils.isEmpty(HttpHeaders.CONTENT_TYPE)) {
      newHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
    newRequest = new ServerHttpRequestDecorator(newRequest) {
      @Override
      public Flux<DataBuffer> getBody() {
        return bodyFlux;
      }
      @Override
      public HttpHeaders getHeaders() {
        long contentLength = newHeaders.getContentLength();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(super.getHeaders());
        if (contentLength > 0) {
          httpHeaders.setContentLength(contentLength);
        } else {
          // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
          httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        }
        return httpHeaders;
      }
    };
    //封装request，传给下一级
    newRequest.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(requestBody.length()));

    return chain.filter(exchange.mutate().request(newRequest).build());

  }

  @Override
  public int getOrder() {
    return -28;
  }
}
