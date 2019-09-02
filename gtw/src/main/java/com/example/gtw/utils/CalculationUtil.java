package com.example.gtw.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: ZHANGXIAOHU
 * @Date: 2019-08-14
 */
public class CalculationUtil {


  public static BigDecimal getMaxDiff(List<BigDecimal> list, Integer length) {

    if (list == null || list.isEmpty() || length < 2) {
      return new BigDecimal("0");
    }
    BigDecimal b1 = list.get(0);
    BigDecimal b2 = list.get(0);
    BigDecimal bb = new BigDecimal("0");
    for (int i = 0; i < length - 1; i++) {

      if (b1.compareTo(list.get(i)) > 0) {
        b1 = list.get(i);
      }
      if (b2.compareTo(list.get(i)) < 0) {
        b2 = list.get(i);
      }
    }
    bb = b2.subtract(b1);
    return bb;
  }

  public static void main(String[] args) {
    List<BigDecimal> list = new ArrayList<>();
    list.add(new BigDecimal("1"));
    list.add(new BigDecimal("9"));
    list.add(new BigDecimal("2"));
    list.add(new BigDecimal("8"));
    list.add(new BigDecimal("3"));
    list.add(new BigDecimal("14"));
    BigDecimal maxDiff = getMaxDiff(list, list.size());
    System.err.println(maxDiff);
  }
}
