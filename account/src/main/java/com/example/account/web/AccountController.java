package com.example.account.web;

import com.example.account.dto.TestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-26
 */
@RestController
@RequestMapping("/index")
public class AccountController {

  @PostMapping("test")
  public TestDto test(@RequestBody TestDto testDto) {
    return new TestDto("ok");
  }


}
