package com.github.longkerdandy.qfii.hkex.model;

import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Stock Shareholding Data Model
 */
public class StockShareholding {

  private Date date;
  private String code;
  private String name;
  private long shareholding;
  private float percent;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getShareholding() {
    return shareholding;
  }

  public void setShareholding(long shareholding) {
    this.shareholding = shareholding;
  }

  public float getPercent() {
    return percent;
  }

  public void setPercent(float percent) {
    this.percent = percent;
  }

  @Override
  public String toString() {
    return "StockShareholding{" +
        "date=" + DateFormatUtils.format(date, "yyyy-MM-dd") +
        ", code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", shareholding=" + shareholding +
        ", percent=" + percent * 100 + "%" +
        '}';
  }
}
