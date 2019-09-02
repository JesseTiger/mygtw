package com.example.gtw.feign;

import com.example.common.vo.ResultVo;
import com.example.gtw.web.dto.TestDto;
import com.example.gtw.web.vo.RewriteVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-09-01
 */
@FeignClient(name = "${service.name}", url = "${service.url}")
public interface TestFeign {


  @PostMapping("/account/index/rewrite")
  ResultVo<RewriteVo> rewrite(@RequestBody TestDto testDto);

}
