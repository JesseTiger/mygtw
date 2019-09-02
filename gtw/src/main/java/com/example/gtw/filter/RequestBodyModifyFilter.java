package com.example.gtw.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBufAllocator;
import java.nio.charset.Charset;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-29
 */
//@Component
public class RequestBodyModifyFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    ServerHttpRequestDecorator serverHttpRequestDecorator = new ServerHttpRequestDecorator(request) {
      @Override
      public Flux<DataBuffer> getBody() {
        Flux<DataBuffer> body = super.getBody();
        return body.map(dataBuffer -> {
          byte[] content = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(content);
          DataBufferUtils.release(dataBuffer);
          String bodyJson = new String(content, Charset.forName("UTF-8"));
          //转化成json对象
          JSONObject jsonObject = JSON.parseObject(bodyJson);
          jsonObject.put("name", "new_insert_data1");
          jsonObject.put("phone", "new_insert_data2");
          String result = jsonObject.toJSONString();
          //转成字节
          byte[] bytes = result.getBytes();
          NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
          DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
          buffer.write(bytes);
          return buffer;
        });
      }

      @Override
      public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(super.getHeaders());
        //由于修改了请求体的body，导致content-length长度不确定，因此使用分块编码
        httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        return httpHeaders;
      }
    };
    return chain.filter(exchange.mutate().request(serverHttpRequestDecorator).build());
  }

  @Override
  public int getOrder() {
    return -45;
  }
}
