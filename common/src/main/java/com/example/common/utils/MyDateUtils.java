package com.example.common.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-07-30
 */
public class MyDateUtils {

  /**
   * 根据传入步长数,步长单位,指定的具体时间,算出指定的天数在具体时间
   *
   * @param step 步长数,如果为空,返回当前时间
   * @param stepUnit 步长单位,如果为空,返回当前时间
   * @param zonedDateTime 指定的具体时间时间,如果为空,已当前时间处理
   */
  public static ZonedDateTime checkDiffByAppointDate(Integer step, Integer stepUnit, ZonedDateTime zonedDateTime) {
    ZonedDateTime pZTD;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS");
    if (zonedDateTime == null && (step == null || step == 0) && (stepUnit == null || stepUnit == 0)) {
      return ZonedDateTime.now();
    }

    System.err.println("指定时间\t" + zonedDateTime);
    if (stepUnit > 0) {
      pZTD = zonedDateTime.plusDays(step.longValue());
    } else {
      pZTD = zonedDateTime.minusDays(step.longValue());
    }
    System.err.println("结果时间\t" + pZTD.toLocalDateTime().format(formatter));

    return pZTD;
  }

  public static void main(String[] args) {
    ZonedDateTime zdt =
        ZonedDateTime.of(2019, 5, 2, 0, 30, 0, 0,
            ZoneId.of(ZoneId.SHORT_IDS.get("CTT"))); // switch to summer time
    ZonedDateTime zdt1 = zdt.truncatedTo(ChronoUnit.DAYS);
//    ZonedDateTime zdt2 = zdt.toLocalDate().atStartOfDay(zdt.getZone());

    checkDiffByAppointDate(98, 1, zdt1);
  }


}
