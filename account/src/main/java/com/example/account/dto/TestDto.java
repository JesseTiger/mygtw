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
  private String phone;
  private String youyou;



  public TestDto(String name, String phone) {
    this.name = name;
    this.phone = phone;
  }

}
