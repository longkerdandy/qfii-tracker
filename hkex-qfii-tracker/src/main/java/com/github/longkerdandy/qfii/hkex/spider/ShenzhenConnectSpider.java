package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Spider for HKEX's Shenzhen StockShareholding Connect Disclosure
 */
public class ShenzhenConnectSpider extends ConnectSpider {

  private String url = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sz ";
  private int timeout = 3000;

  public ShenzhenConnectSpider(int timeout) {
    this.timeout = timeout;
  }

  public List<StockShareholding> fetch(Date queryDate) throws IOException, ParseException {
    return fetch(queryDate, this.url, this.timeout);
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("70")) {
      code = code.replaceFirst("70", "000");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    return name;
  }
}
