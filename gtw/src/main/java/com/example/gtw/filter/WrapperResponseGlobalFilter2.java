package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.gtw.constants.MyConstants;
import com.example.gtw.utils.DateUtil;
import com.example.gtw.utils.JWTUtil;
import io.fusionauth.jwt.domain.JWT;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

//import com.example.common.enums.RespEnums;
//import com.example.common.vo.ResultVo;

/**
 * @Description: responseBody增加修改信息返回 主要用于登录时,返回 token
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-08
 */
@Component
@Log4j2
public class WrapperResponseGlobalFilter2 implements GlobalFilter, Ordered {


  @Autowired
  DateUtil dateUtil;
  @Autowired
  JWTUtil jwtUtil;

  @Value("${jwt.token.exp}")
  private long tokenExp;

  @Value("${jwt.reToken.exp}")
  private long reTokenExp;


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String requestUri = exchange.getRequest().getPath().value();
    log.info("[uri=\t{}]______进入 filter ③ : 响应回写 __________", requestUri);
// FIXME: 2019-09-01 测试是否删除修改了 header 中 的 beanId
    Collection<List<String>> values = exchange.getRequest().getHeaders().values();
    for (List<String> value : values) {
      System.err.println(value);
    }

    List<String> headerReqTs = new ArrayList<>();
    List<String> headerTs = new ArrayList<>();
    ServerHttpResponse originalResponse = exchange.getResponse();

    String reqTs = exchange.getRequest().getHeaders().getFirst("reqTs");
    //responseHeader 加入请求时间戳
    headerReqTs.add(reqTs);
    originalResponse.getHeaders().put("reqTs", headerReqTs);
    ServerWebExchange build;
    build = exchange.mutate().response(responseDecorator(exchange.mutate().response(originalResponse).build())).build();
    //responseHeader 加入响应时间戳
    headerTs.add(dateUtil.getDateStr());
    ServerHttpResponse response = build.getResponse();
    response.getHeaders().put("ts", headerTs);
    build = build.mutate().response(build.getResponse()).build();
    log.info(" [uri=\t{}]______离开 filter ③ : 响应回写 __________", requestUri);
    return chain.filter(build);
  }

  private ServerHttpResponseDecorator responseDecorator(ServerWebExchange exchange) {
    return new ServerHttpResponseDecorator(exchange.getResponse()) {
      ServerHttpResponse serverHttpResponse = exchange.getResponse();
      DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();

      @Override
      public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(DataBufferUtils.join(Flux.from(body))
            .map(dataBuffer -> {
              byte[] content = new byte[dataBuffer.readableByteCount()];
              dataBuffer.read(content);
              DataBufferUtils.release(dataBuffer);
              return content;
            }).flatMap(bytes -> {

              String responseBody = "";
              String requestUri = exchange.getRequest().getPath().value();

              responseBody = new String(bytes, StandardCharsets.UTF_8);
              log.info("[uri=\t{}]\t原响应返回值 bodyString:\t{}", requestUri, responseBody);
              JSONObject jsonTotal = JSONObject.parseObject(responseBody);
              if (!StringUtils.isEmpty(jsonTotal.get("status"))
                  && !jsonTotal.get("status").equals(HttpResponseStatus.OK.code())) {
                //error
              /*  ResultVo vo = new ResultVo(RespEnums.UNKNOW_ERROR.getCode(),
                    RespEnums.UNKNOW_ERROR.getMsg());
                jsonTotal.clear();
                jsonTotal = JSONObject.parseObject(vo.toString());*/
              } else {
                //normal
                Object code = jsonTotal.get("code");
//                if (!StringUtils.isEmpty(code) && code.equals(ErrorEnum.SUCCESS.getCode())) {
//                  JSONObject data = jsonTotal.getJSONObject("data");
//                  if (!StringUtils.isEmpty(data)) {
//                    //登录
//                    if (checkisLogin(requestUri)) {
//                      LoginAccountVO accVo = JSONObject.parseObject(data.toJSONString(), LoginAccountVO.class);
//                      HashMap<String, String> tokenMap = genericToken(accVo.getBeanId(), accVo.getRoleCode());
//                      accVo.setAccessToken(tokenMap.get("accessToken"));
//                      accVo.setRefreshToken(tokenMap.get("refreshToken"));
//                      jsonTotal.put("data", accVo);
//                      //刷新 token
//                    } else if (requestUri.equals(MyConstants.REFRESHTOKEN_URL)) {
//
//                      String beanId = exchange.getRequest().getHeaders().getFirst("beanId");
//                      String role = exchange.getRequest().getHeaders().getFirst("role");
//                      RefreshTokenVO refVo = JSONObject.parseObject(data.toJSONString(), RefreshTokenVO.class);
//                      HashMap<String, String> tokenMap = genericToken(beanId, role);
//                      refVo.setAccessToken(tokenMap.get("accessToken"));
//                      refVo.setRefreshToken(tokenMap.get("refreshToken"));
//                      jsonTotal.put("data", refVo);
//                    }
//                  }
//                }
              }
              log.info(" [uri=\t{}]\t 响应回写返回 :\t{}", requestUri, jsonTotal.toJSONString());
              bytes = jsonTotal.toString().getBytes();

              return Mono.just(bufferFactory.wrap(bytes));
            }));
      }

      @Override
      public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
      }
    };
  }

  /**
   * 生成 accessToken & refreshToken
   */
  private HashMap<String, String> genericToken(String beanId, String role) {
    if (StringUtils.isEmpty(role)) {
      role = MyConstants.LOGIN_ACCOUNT_ROLECODE;
    }
    HashMap<String, String> retMap = new HashMap<>();
    ZonedDateTime tokenZDT = ZonedDateTime.now(ZoneOffset.UTC).plusHours(tokenExp);
    ZonedDateTime reTokenZDT = ZonedDateTime.now(ZoneOffset.UTC).plusHours(reTokenExp);
    JWT jwtAccessToken = new JWT()
        .setIssuer("gwt Server")
        .setExpiration(tokenZDT)
        .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
        .addClaim(MyConstants.LOGIN_ACCOUNT_BEANID, beanId)
        .addClaim(MyConstants.LOGIN_ACCOUNT_ROLECODE, role);
    String accessToken = jwtUtil.encryptToken(jwtAccessToken);
    retMap.put("accessToken", accessToken);
    JWT jwtRefreshtoken = new JWT()
        .setExpiration(reTokenZDT)
        .addClaim(MyConstants.LOGIN_ACCOUNT_BEANID, beanId)
        .addClaim(MyConstants.LOGIN_ACCOUNT_ROLECODE, role);
    String refreshToken = jwtUtil.encryptToken(jwtRefreshtoken);
    retMap.put("refreshToken", refreshToken);
    return retMap;
  }

  /**
   * 校验uri 是否是登录 uri
   */
  private Boolean checkisLogin(String requestUri) {
    return MyConstants.LOGIN_URI_LIST.contains(requestUri);
  }

  @Override
  public int getOrder() {
    return -10;
  }
}



