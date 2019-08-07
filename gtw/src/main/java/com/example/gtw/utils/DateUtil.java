package com.example.gtw.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-25
 */
@Component
public class DateUtil {

  public  String getDateStr(){
    DateTimeFormatter formatter0 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").withZone(ZoneId.of("Asia/Shanghai"));
    return formatter0.format(ZonedDateTime.now());
  }

}
