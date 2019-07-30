package com.example.gtw.gtwexception;

import com.example.common.vo.ResultVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description: gtw 异常返回类
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
@Log4j2
public class GtwException {


  public static Mono<Void> gtwFail(ServerHttpResponse resp, Integer code, String msg) {
    resp.setStatusCode(HttpStatus.EXPECTATION_FAILED);
    resp.getHeaders().add("Content-Type", "application/json;charset=UTF-");
    ResultVo vo = new ResultVo(code, msg);
    String resultStr = "";
    try {
      resultStr = new ObjectMapper().writeValueAsString(vo);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    DataBuffer dataBuffer = resp.bufferFactory().wrap(resultStr.getBytes(StandardCharsets.UTF_8));
    return resp.writeWith(Flux.just(dataBuffer));
  }
}
