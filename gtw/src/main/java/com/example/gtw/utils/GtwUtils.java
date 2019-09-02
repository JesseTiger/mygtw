package com.example.gtw.utils;

import io.netty.buffer.ByteBufAllocator;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-09-02
 */
public class GtwUtils {
  /**
   * 获取请求体中的字符串内容
   *
   * @param serverHttpRequest
   * @return
   */
  public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
    //获取请求体
    Flux<DataBuffer> body = serverHttpRequest.getBody();
    StringBuilder sb = new StringBuilder();
    body.subscribe(buffer -> {
      byte[] bytes = new byte[buffer.readableByteCount()];
      buffer.read(bytes);
      DataBufferUtils.release(buffer);
      String bodyString = new String(bytes, StandardCharsets.UTF_8);
      sb.append(bodyString);
    });
    return sb.toString();
  }


  /**
   * 字符串转 DataBuffer
   * @param value
   * @return
   */
 public static DataBuffer stringBuffer(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
    buffer.write(bytes);
    return buffer;
  }


}
