package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Spider for HKEX's Shanghai StockShareholding Connect Disclosure
 */
public class ShanghaiConnectSpider extends ConnectSpider {

  private static final String URL = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sh";
  private final int timeout;
  private final InfluxDBStorage storage;

  public ShanghaiConnectSpider(int timeout, InfluxDBStorage storage) {
    this.timeout = timeout;
    this.storage = storage;
  }

  public void fetchRangeAndUpdate(Date startDate, Date endDate) throws IOException, ParseException {
    fetchRangeAndUpdate(startDate, endDate, URL, this.timeout, this.storage);
  }

  public void fetchAndUpdate(Date queryDate) throws IOException, ParseException {
    fetchAndUpdate(queryDate, URL, this.timeout, this.storage);
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("9")) {
      code = code.replaceFirst("9", "60");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    if (name.equals(" 片仔？")) {
      return "片仔癀";
    }
    return name;
  }
}
