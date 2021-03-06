package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.common.enums.RespEnums;
import com.example.gtw.gtwexception.GtwException;
import com.example.gtw.utils.GtwUtils;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @Description: 获取 request body 参数值
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Log4j2
@Component
public class RequestBodyReadFilter implements GatewayFilter, Ordered {

//  @Autowired
//  GtwUtils gtwUtils;

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

    // FIXME: 2019-09-01  删除原 header中的 beanId
//    request.getHeaders().remove("beanId").remove(0);
//    HttpHeaders headers = request.getHeaders();

//    //  把 reqTs 塞入 request 中
//    // TODO: 2019-07-26 very important--------
    ServerHttpRequest reqTs = exchange.getRequest().mutate().header("reqTs", "21321").
        header("beanId", "111111111111111111").
        header("beanId", "333333333333333333")
        .build();
    //将现在的request 变成 change对象
    ServerWebExchange build = exchange.mutate().request(reqTs).build();


    // FIXME: 2019-09-02 尝试修改 requestbody
    ServerHttpRequest oldRequest = build.getRequest();
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
    HttpHeaders headers = new HttpHeaders();
    headers.putAll(exchange.getRequest().getHeaders());
//    headers.remove("beanId").remove(0);

    // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
    int length = requestBody.getBytes().length;
    System.err.println("header的 content_length: "+length);
    headers.remove(HttpHeaders.CONTENT_LENGTH);
    headers.setContentLength(length);
//     设置CONTENT_TYPE
    if (!StringUtils.isEmpty(HttpHeaders.CONTENT_TYPE)) {
      headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }


    newRequest = new ServerHttpRequestDecorator(newRequest) {
      @Override
      public Flux<DataBuffer> getBody() {
        return bodyFlux;
      }
      @Override
      public HttpHeaders getHeaders() {
        long contentLength = headers.getContentLength();
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

//    return chain.filter(build);
//    // TODO: 2019-07-26 very important---------

//    return chain.filter(exchange);
  }


  @Override
  public int getOrder() {
    return -30;
  }
}
