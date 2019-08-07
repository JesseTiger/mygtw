package com.example.gtw.utils;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.json.Mapper;
import io.fusionauth.jwt.rsa.RSASigner;
import io.fusionauth.jwt.rsa.RSAVerifier;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;


/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-16
 */
@Component
public class JWTUtil {

  @Value("${rsa.privateKey.path}")
  private String privateKey;

  @Value("${rsa.publicKey.path}")
  private String pubicKey;

  /**
   * rsa 加密生成 token
   */
  public String encryptToken(JWT jwt) {
    try {
      Signer signer = RSASigner
          .newSHA256Signer(
              StreamUtils.copyToString(new ClassPathResource(privateKey).getInputStream(), Charset.defaultCharset())
          );
      return JWT.getEncoder().encode(jwt, signer);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * rsa token 解密
   */
  public JWT decryptToken(String token) throws Exception {

    Verifier verifier = RSAVerifier.newVerifier(new ClassPathResource(pubicKey).getFile().toPath());
    JWT decode = JWT.getDecoder().decode(token, verifier);
    return decode;
  }

  /**
   * 校验 token 是否过期
   */
  public Boolean verifyExp(String token) throws Exception {
    Verifier verifier = RSAVerifier.newVerifier(new ClassPathResource(pubicKey).getFile().toPath());
    JWT jwt = JWT.getDecoder().decode(token, verifier);
    return checkEXP(jwt);
  }


  /**
   * 根据当前时间和 jwt 的 exp 时间相比,没过期返回 true
   */
  public Boolean checkEXP(JWT jwt) {
    Map<String, Object> allClaims = jwt.getAllClaims();
    ZonedDateTime exp = (ZonedDateTime) allClaims.get("exp");
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ZoneId.SHORT_IDS.get("CTT")));
    return exp.isAfter(now);
  }

  /**
   * 根据当前时间和 jwt 的 exp 时间相比, 当前时间小说明为过期
   */

  public JWT parseJWT(String token) {
    String[] split = token.split("\\.");
    byte[] decode = Base64.getUrlDecoder().decode(split[1]);
    JWT jwt = Mapper.deserialize(decode, JWT.class);
    return jwt;
  }


}
