package com.example.gtw.web.dto;

import lombok.Data;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@Data
public class TestDto {

  private String name;
  private String phone;

  public TestDto(String name, String phone) {
    this.name = name;
    this.phone = phone;
  }

}
