package com.example.account.web;

import com.example.account.dto.TestDto;
import com.example.account.vo.RewriteVo;
import com.example.common.enums.RespEnums;
import com.example.common.vo.ResultVo;
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
@RequestMapping("index")
public class AccountController {

  @PostMapping("test")
  public TestDto test(@RequestBody TestDto testDto) {

    return new TestDto("ok","12345");
  }

  @PostMapping("rewrite")
  public ResultVo<RewriteVo> rewrite(@RequestBody TestDto testDto) {
    String youyou = testDto.getYouyou();
    System.err.println("youyou参数值是 : "+youyou);
    RewriteVo vo = new RewriteVo("001", "特特001");
    return new ResultVo<>(RespEnums.SUCCESS.getCode(), RespEnums.SUCCESS.getMsg(), vo);
  }
  @PostMapping("nodata")
  public ResultVo nodata() {
    RewriteVo vo = new RewriteVo("00111", "malegejiji");
    return new ResultVo<>(RespEnums.SUCCESS.getCode(), RespEnums.SUCCESS.getMsg(), vo);
  }
}
