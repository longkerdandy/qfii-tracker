package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Spider for HKEX's Shenzhen StockShareholding Connect Disclosure
 */
public class ShenzhenConnectSpider extends ConnectSpider {

  private static final String URL = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sz ";
  private final int timeout;
  private final InfluxDBStorage storage;

  public ShenzhenConnectSpider(int timeout, InfluxDBStorage storage) {
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
    if (code.startsWith("7")) {
      code = code.replaceFirst("7", "00");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    return name;
  }
}
