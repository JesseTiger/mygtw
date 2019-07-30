package com.example.common.enums;


/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
public enum RespEnums {

  // 成功：1
  SUCCESS(1000, "执行成功.有数据"),

  // 未知错误
  UNKNOW_ERROR(-1000, "执行错误"),
  ;

  private Integer code;
  private String msg;

  RespEnums(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
