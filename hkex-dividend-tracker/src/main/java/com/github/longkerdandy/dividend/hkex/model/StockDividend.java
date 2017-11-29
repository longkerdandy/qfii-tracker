package com.github.longkerdandy.dividend.hkex.model;

import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Stock Dividend Data Model
 */
public class StockDividend {

  private String code;
  private String name;
  private Date publicDate;
  private String year;
  private String reason;
  private String description;
  private String method;
  private Date rightsOffDate;
  private Date transferBegin;
  private Date transferEnd;
  private Date divideDate;

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

  public Date getPublicDate() {
    return publicDate;
  }

  public void setPublicDate(Date publicDate) {
    this.publicDate = publicDate;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Date getRightsOffDate() {
    return rightsOffDate;
  }

  public void setRightsOffDate(Date rightsOffDate) {
    this.rightsOffDate = rightsOffDate;
  }

  public Date getTransferBegin() {
    return transferBegin;
  }

  public void setTransferBegin(Date transferBegin) {
    this.transferBegin = transferBegin;
  }

  public Date getTransferEnd() {
    return transferEnd;
  }

  public void setTransferEnd(Date transferEnd) {
    this.transferEnd = transferEnd;
  }

  public Date getDivideDate() {
    return divideDate;
  }

  public void setDivideDate(Date divideDate) {
    this.divideDate = divideDate;
  }

  @Override
  public String toString() {
    return "StockDividend{" +
        "code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", publicDate=" + DateFormatUtils.format(publicDate, "yyyy-MM-dd") +
        ", year=" + year +
        ", reason='" + reason + '\'' +
        ", description='" + description + '\'' +
        ", method='" + method + '\'' +
        ", rightsOffDate=" + DateFormatUtils.format(rightsOffDate, "yyyy-MM-dd") +
        ", transferBegin=" + DateFormatUtils.format(transferBegin, "yyyy-MM-dd") +
        ", transferEnd=" + DateFormatUtils.format(transferEnd, "yyyy-MM-dd") +
        ", divideDate=" + DateFormatUtils.format(divideDate, "yyyy-MM-dd") +
        '}';
  }
}
