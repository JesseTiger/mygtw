package com.example.common.vo;

import com.example.common.enums.RespEnums;
import java.io.Serializable;
import java.util.HashMap;
import lombok.Data;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
@Data
public class ResultVo<T> implements Serializable {

  private Integer code;

  private String message;

  private T data;

  public ResultVo() {
  }

  public ResultVo(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public ResultVo(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * 默认的成功响应.
   */
  public static ResultVo success() {
    return new ResultVo(RespEnums.SUCCESS.getCode(), RespEnums.SUCCESS.getMsg(), new HashMap<>(0));
  }

  /**
   * 带参数的成功响应.
   */
  public static <T> ResultVo success(T data) {
    return new ResultVo(1000, "success", data);
  }

  /**
   * 失败响应.
   */
  public static ResultVo fail(int errorCode, String errorMessage) {
    return new ResultVo(errorCode, errorMessage, new HashMap<>(0));
  }
}
