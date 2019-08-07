package com.example.gtw.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.gtw.utils.DateUtil;
import com.example.gtw.utils.JWTUtil;
import io.fusionauth.jwt.domain.JWT;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

/**
 * @Description: responseBody增加修改信息返回 主要用于登录时,返回 token
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-24
 */
@Component
@Log4j2
public class WrapperResponseGlobalFilter implements GlobalFilter, Ordered {

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
    log.info("______into -----wrapper response  filter________");
    ServerHttpResponse originalResponse = exchange.getResponse();

    String reqTs = exchange.getRequest().getHeaders().getFirst("reqTs");
    //responseHeader 加入请求时间戳
    List<String> headerReqTs = new ArrayList<>();
    headerReqTs.add(reqTs);
    originalResponse.getHeaders().

        put("reqTs", headerReqTs);

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

            String requestUri = exchange.getRequest().getPath().value();
            //判断是否是登录,登录就重写 body
            log.info("WrapperResponseGlobalFilter 参数返回值:\t{}", responseBody);
            JSONObject jsonTotal = JSONObject.parseObject(responseBody);
            // 根据返回信息判断是否是 spring framework 的报错信息
//            if (!StringUtils.isEmpty(jsonTotal.get("status"))
//                && !jsonTotal.get("status").equals(HttpResponseStatus.OK.code())) {
//              //error
//              ResultVo vo = new ResultVo(RespEnums.UNKNOW_ERROR.getCode(),
//                  RespEnums.UNKNOW_ERROR.getMsg());
//              jsonTotal.clear();
//              jsonTotal = JSONObject.parseObject(vo.toString());
//            } else {

              //normal
//              Object code = jsonTotal.get("code");
//              if (!StringUtils.isEmpty(code) && code.equals(RespEnums.SUCCESS.getCode())) {
//                JSONObject data = jsonTotal.getJSONObject("data");
//                if (!StringUtils.isEmpty(data)) {
                  //登录
//                  if (checkisLogin(requestUri)) {
//                    LoginAccountVO accVo = JSONObject.parseObject(data.toJSONString(), LoginAccountVO.class);
//                    HashMap<String, String> tokenMap = genericToken(accVo.getBeanId(), accVo.getRoleCode());
//                    accVo.setAccessToken(tokenMap.get("accessToken"));
//                    accVo.setRefreshToken(tokenMap.get("refreshToken"));
//                    jsonTotal.put("data", "data");
                    //刷新 token
//                  }
//                  else if (requestUri.equals(MyConstants.REFRESHTOKEN_URL)) {
//
//                    String beanId = exchange.getRequest().getHeaders().getFirst("beanId");
//                    String role = exchange.getRequest().getHeaders().getFirst("role");
//                    RefreshTokenVO refVo = JSONObject.parseObject(data.toJSONString(), RefreshTokenVO.class);
//                    HashMap<String, String> tokenMap = genericToken(beanId, role);
//                    refVo.setAccessToken(tokenMap.get("accessToken"));
//                    refVo.setRefreshToken(tokenMap.get("refreshToken"));
//                    jsonTotal.put("data", refVo);
//                  }
//                }
//              }
//            }
            content = jsonTotal.toString().getBytes();

            //添加tsp 响应时间戳
//            headerTs.add(dateUtil.getDateStr());
//            originalResponse.getHeaders().put("ts", headerTs);

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

  /**
   * 生成 accessToken & refreshToken
   */
  private HashMap<String, String> genericToken(String beanId, String role) {
    if (StringUtils.isEmpty(role)) {
      role = "role";
    }
    HashMap<String, String> retMap = new HashMap<>();
    ZonedDateTime tokenZDT = ZonedDateTime.now(ZoneOffset.UTC).plusHours(tokenExp);
    ZonedDateTime reTokenZDT = ZonedDateTime.now(ZoneOffset.UTC).plusHours(reTokenExp);
    JWT jwtAccessToken = new JWT()
        .setIssuer("gwt Server")
        .setExpiration(tokenZDT)
        .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
        .addClaim("beanId", beanId)
        .addClaim("role", role);
    String accessToken = jwtUtil.encryptToken(jwtAccessToken);
    retMap.put("accessToken", accessToken);
    JWT jwtRefreshtoken = new JWT()
        .setExpiration(reTokenZDT)
        .addClaim("beanId", beanId)
        .addClaim("role", role);
    String refreshToken = jwtUtil.encryptToken(jwtRefreshtoken);
    retMap.put("refreshToken", refreshToken);
    return retMap;
  }

  /**
   * 校验uri 是否是登录 uri
   */
  private Boolean checkisLogin(String requestUri) {
    return false;

//        MyConstants.LOGIN_URI_LIST.contains(requestUri);
  }


  @Override
  public int getOrder() {
    // -1 is response write filter, must be called before that
    return -10;
  }
}