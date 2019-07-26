package com.example.account.dto;

import lombok.Data;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Data
public class TestDto {

  private String name;

  public TestDto(String name) {
    this.name = name;
  }

  public TestDto() {
  }
}
