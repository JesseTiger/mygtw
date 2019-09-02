package com.example.gtw.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-29
 */
public class MyConstants {

  public static final String APP_SECRET = "YXBwU2VjcmV0";

  public static final Set<String> LOGIN_URI_LIST = new HashSet<>();
  public static final Set<String> I18N_LIST = new HashSet<>();
  public static final Set<Integer> RS_LIST = new HashSet<>();
  public static final String ACCOUNT_SERVICE = "/account";
  public static final String VERSION = "/v1.0";

  public static final String LOGIN_ACCOUNT_ROLECODE = "roleCode";
  public static final String LOGIN_ACCOUNT_BEANID = "beanId";

  public static final String REFRESHTOKEN_URL = VERSION + ACCOUNT_SERVICE + "/refreshToken";
  public static final String VERFIY_SWITCH_ON = "on";

  static {
    //    短信验证码登录
    LOGIN_URI_LIST.add(VERSION + ACCOUNT_SERVICE + "/loginWithSMS");
    //    账号密码登录
    LOGIN_URI_LIST.add(VERSION + ACCOUNT_SERVICE + "/loginAccount");
    //    微信UnionID登录
    LOGIN_URI_LIST.add(VERSION + ACCOUNT_SERVICE + "/loginWithUnionId");
    //    获取扫码登录结果
//    LOGIN_URI_LIST.add(VERSION+ACCOUNT_SERVICE +   "/getQrCodeLoginResult");
    //    授权码登录
    LOGIN_URI_LIST.add(VERSION + ACCOUNT_SERVICE + "/loginWithAuthCode");

    I18N_LIST.add("zh-cn");
    I18N_LIST.add("en-us");
//    1，webportal;2，手机app;3，HU;4，Tbox;5， CIS;6，TBOSS。7，第三方服务端。8，智能后视镜
    for (int i = 1; i < 9; i++) {
      RS_LIST.add(i);
    }

  }
}
