package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.common.enums.RespEnums;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description: responseBody 和 responseHeader 添加修改数据返回. 注意,order 一定要在-1 之前,因为 -1 的过滤器走的事 netty 的响应回写
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
@Data
@Log4j2
@Component
public class ResponseBodyModifylFilter3 implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("______into----response-----wrapper response  filter________");
    ServerHttpResponse originalResponse = exchange.getResponse();
    Object requestBody = exchange.getAttribute("cachedRequestBodyObject");
log.info("!!!!!!!!11111"+requestBody);
    String reqTs = exchange.getRequest().getHeaders().getFirst("reqTs");
    //responseHeader 加入请求时间戳
    List<String> headerReqTs = new ArrayList<>();
//    headerReqTs.add(reqTs);
    originalResponse.getHeaders().put("reqTs", headerReqTs);
    List<String> headerTs = new ArrayList<>();

    DataBufferFactory bufferFactory = originalResponse.bufferFactory();
    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

      @Override
      public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Flux) {
          Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
          return super.writeWith(fluxBody.map(dataBuffer -> {
            // probably should reuse buffers
            byte[] content = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(content);
            //释放掉内存
            DataBufferUtils.release(dataBuffer);
            String responseBody = new String(content, Charset.forName("UTF-8"));
            //TODO，s就是response的值，想修改、查看就随意而为了
            String requestUri = exchange.getRequest().getPath().value();
            log.info("WrapperResponseGlobalFilter 参数返回值:\t{}", responseBody);
            JSONObject jsonTotal = JSONObject.parseObject(responseBody);

            Object code = jsonTotal.get("code");
            if (!StringUtils.isEmpty(code) && code.equals(RespEnums.SUCCESS.getCode())) {
//              JSONObject data = jsonTotal.getJSONObject("data");
              JSONObject data = new JSONObject();
              data.put("id", "002");
              data.put("name", "spy-002");
              jsonTotal.put("data", data);
            }
            content = jsonTotal.toString().getBytes();

            //添加tsp 响应时间戳
            headerTs.add("111111111");
            originalResponse.getHeaders().put("ts", headerTs);

            byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
            return bufferFactory.wrap(uppedContent);
          }));
        }
        // if body is not a flux. never got there.
        return super.writeWith(body);
      }
    };
    // replace response with decorator
    ServerWebExchange build = exchange.mutate().response(decoratedResponse).build();

    return chain.filter(build);
  }


  @Override
  public int getOrder() {
    return -10;
  }
}
