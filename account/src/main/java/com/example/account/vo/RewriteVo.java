package com.example.account.vo;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-29
 */
@Log4j2
@Data
public class RewriteVo {

  private String id;
  private String name;

  public RewriteVo() {
  }

  public RewriteVo(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
