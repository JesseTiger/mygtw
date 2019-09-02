//package com.example.gtw.web;
//
//import com.example.common.enums.RespEnums;
//import com.example.common.vo.ResultVo;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @Description:
// * @Author: ZHANGXIAOHU
// * @Date: 2019-08-02
// */
//@RestController
//@RequestMapping("index")
//public class TestController {
//
//  @PostMapping("test")
//  public ResultVo testGtwFilter(@RequestBody String name) {
//
//    System.err.println("name++" + name);
//    return new ResultVo(RespEnums.SUCCESS.getCode(), RespEnums.SUCCESS.getMsg(), name);
//  }
//
//
//}
