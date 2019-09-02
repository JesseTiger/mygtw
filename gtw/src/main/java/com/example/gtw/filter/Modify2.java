package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBufAllocator;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


//@Component
@Deprecated
@Log4j2
public class Modify2 implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest serverHttpRequest = exchange.getRequest();
    String method = serverHttpRequest.getMethodValue();
    if ("POST".equals(method)) {
      //从请求里获取Post请求体
      String bodyStr = resolveBodyFromRequest(serverHttpRequest);
      //TODO 得到Post请求的请求参数后，做你想做的事
      if (StringUtils.isEmpty(bodyStr)) {
        JSONObject json = new JSONObject();
        json.put("name", "111222");
        json.put("phone", "121212");
        bodyStr = json.toJSONString();
      } else if ("{\"post\":\"empty\"}".equals(bodyStr)) {
        bodyStr = "{\"11\":\"11\"}";
      }
//-------------
      log.info("全局参数处理: {} url：{} 参数：{}", method.toString(), serverHttpRequest.getURI().getRawPath(), bodyStr);
      URI uri = serverHttpRequest.getURI();
      URI newUri = UriComponentsBuilder.fromUri(uri).build(true).toUri();
      ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
      DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
      Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
      // 定义新的消息头
      HttpHeaders headers = new HttpHeaders();
      headers.putAll(exchange.getRequest().getHeaders());
      // 添加消息头
      headers.set("POST_NO_REQUEST_BODY", "SO");

      int length = bodyStr.getBytes().length;
      headers.remove(HttpHeaders.CONTENT_LENGTH);
      headers.setContentLength(length);

      ServerHttpRequestDecorator serverHttpRequestDecorator = new ServerHttpRequestDecorator(request) {
        @Override
        public HttpHeaders getHeaders() {
          long contentLength = headers.getContentLength();
          HttpHeaders httpHeaders = new HttpHeaders();
          httpHeaders.putAll(super.getHeaders());
          if (contentLength > 0) {
            httpHeaders.setContentLength(contentLength);
          } else {
            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
          }
          return httpHeaders;
        }

        @Override
        public Flux<DataBuffer> getBody() {
          return bodyFlux;
        }

      };

      //封装request，传给下一级
//     request.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(bodyStr.length()));

      request.mutate().header("reqTs", "21321");
//      //将现在的request 变成 change对象
//      ServerWebExchange build = exchange.mutate().request(reqTs).build();
//      return chain.filter(build);
      return chain.filter(exchange.mutate().request(request).build());

//-------------------

//      //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
//      URI uri = serverHttpRequest.getURI();
//      ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
//      DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
//      Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
//
//      request = new ServerHttpRequestDecorator(request) {
//        @Override
//        public Flux<DataBuffer> getBody() {
//          return bodyFlux;
//        }
//      };
//      //封装request，传给下一级
//      ServerWebExchange build = exchange.mutate().request(request).build();
//      return chain.filter(build);
    }

    return chain.filter(exchange);
  }


  /**
   * 从Flux<DataBuffer>中获取字符串的方法
   *
   * @return 请求体
   */
  private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
    //获取请求体
    Flux<DataBuffer> body = serverHttpRequest.getBody();

    AtomicReference<String> bodyRef = new AtomicReference<>();
    body.subscribe(buffer -> {
      CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
//      DataBufferUtils.release(buffer);
      bodyRef.set(charBuffer.toString());
    });
    //获取request body
    return bodyRef.get();
  }

  private DataBuffer stringBuffer(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

    NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
    buffer.write(bytes);
    return buffer;
  }

  @Override
  public int getOrder() {
    return -11;
  }
}