package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Spider for HKEX's Shanghai StockShareholding Connect Disclosure
 */
public class ShanghaiConnectSpider extends ConnectSpider {

  private String url = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sh";
  private int timeout = 3000;

  public ShanghaiConnectSpider(int timeout) {
    this.timeout = timeout;
  }

  public List<StockShareholding> fetch(Date queryDate) throws IOException, ParseException {
    return fetch(queryDate, this.url, this.timeout);
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("90")) {
      code = code.replaceFirst("90", "600");
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
